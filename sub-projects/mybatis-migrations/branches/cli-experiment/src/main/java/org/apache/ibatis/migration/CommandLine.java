/*
 *    Copyright 2009-2011 The MyBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.migration;

import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.migration.commands.BootstrapCommand;
import org.apache.ibatis.migration.commands.Command;
import org.apache.ibatis.migration.commands.DownCommand;
import org.apache.ibatis.migration.commands.InitializeCommand;
import org.apache.ibatis.migration.commands.NewCommand;
import org.apache.ibatis.migration.commands.PendingCommand;
import org.apache.ibatis.migration.commands.ScriptCommand;
import org.apache.ibatis.migration.commands.StatusCommand;
import org.apache.ibatis.migration.commands.UpCommand;
import org.apache.ibatis.migration.commands.VersionCommand;

import com.beust.jcommander.JCommander;

public class CommandLine {

  private PrintStream printStream = System.out;

  private final Map<String, Command> commands = new HashMap<String, Command>();

  private final MigrationsOptions options = new MigrationsOptions();

  private final JCommander jCommander = new JCommander( options );

  private final String[] args;

  public CommandLine(String[] args) {
    this.args = args;

    jCommander.setProgramName( "migrate" );
    registerCommand( "bootstrap", new BootstrapCommand(options) );
    registerCommand( "down", new DownCommand(options) );
    registerCommand( "init", new InitializeCommand(options) );
    registerCommand( "new", new NewCommand(options) );
    registerCommand( "pending", new PendingCommand(options) );
    registerCommand( "script", new ScriptCommand(options) );
    registerCommand( "status", new StatusCommand(options) );
    registerCommand( "up", new UpCommand(options) );
    registerCommand( "version", new VersionCommand(options) );
  }

  private void registerCommand( String name, Command command )
  {
    commands.put( name, command );
    jCommander.addCommand( name, command );
  }

  public void setPrintStream(PrintStream out) {
    this.printStream = out;
  }

  public PrintStream getPrintStream() {
    return this.printStream;
  }

  public void execute() {
    jCommander.parse( args );

    if ( options.help )
    {
        jCommander.usage();
    }
    else
    {
      options.printStream = printStream;
      options.init();

      String parsedCommand = jCommander.getParsedCommand();
      Command command = commands.get( parsedCommand );

      boolean error = false;
      try
      {
        runCommand( parsedCommand, command );
      }
      catch ( Exception e )
      {
        error = true;
        printStream.println( "\nERROR: " + e.getMessage() );
        if ( options.trace )
        {
          e.printStackTrace();
        }
      }
      finally
      {
        printStream.flush();
        if ( error )
        {
          System.exit( 1 );
        }
      }
    }
  }

  private void runCommand( String name, Command command ) {
    printStream.println("------------------------------------------------------------------------");
    printStream.printf("MyBatis Migrations - %s%n", name);
    printStream.println("------------------------------------------------------------------------");

    long start = System.currentTimeMillis();
    int exit = 0;

    try {
      command.execute();
    } catch (Throwable t) {
      exit = -1;
      t.printStackTrace(printStream);
    } finally {
      printStream.println("------------------------------------------------------------------------");
      printStream.printf("MyBatis Migrations %s%n", (exit < 0) ? "FAILURE" : "SUCCESS");
      printStream.printf("Total time: %ss%n", ((System.currentTimeMillis() - start) / 1000));
      printStream.printf("Finished at: %s%n", new Date());

      final Runtime runtime = Runtime.getRuntime();
      final int megaUnit = 1024 * 1024;
      printStream.printf("Final Memory: %sM/%sM%n",
                (runtime.totalMemory() - runtime.freeMemory()) / megaUnit,
                runtime.totalMemory() / megaUnit);

      printStream.println("------------------------------------------------------------------------");

      if (exit != 0) System.exit(exit);
    }
  }

}
