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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Properties;

import org.apache.ibatis.migration.commands.BootstrapCommand;
import org.apache.ibatis.migration.commands.Command;
import org.apache.ibatis.migration.commands.DownCommand;
import org.apache.ibatis.migration.commands.InitializeCommand;
import org.apache.ibatis.migration.commands.MigrationsPathConverterValidator;
import org.apache.ibatis.migration.commands.NewCommand;
import org.apache.ibatis.migration.commands.PendingCommand;
import org.apache.ibatis.migration.commands.ScriptCommand;
import org.apache.ibatis.migration.commands.StatusCommand;
import org.apache.ibatis.migration.commands.UpCommand;
import org.apache.ibatis.migration.commands.VersionCommand;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( separators = "=" )
public class CommandLine {

  @Parameter(
    names = { "-X", "--trace" },
    description = "Shows additional error details (if any)."
  )
  public boolean trace = false;

  @Parameter(
    names = { "-h", "--help" },
    description = "Displays this usage message."
  )
  public boolean help = false;

  @Parameter(
   names = { "-p", "--path" },
   converter = MigrationsPathConverterValidator.class,
   validateWith = MigrationsPathConverterValidator.class,
   description = "Path to repository.  Default current working directory."
  )
  public File basePath = new File( System.getProperty( "user.dir" ) );

  @Parameter(
    names = { "-e", "--env" },
    description = "Environment to configure."
  )
  public String environment = "development";

  @Parameter(
    names = { "-t", "--template" },
    description = "Path to custom template for creating new sql scripts."
  )
  public String template;

  @Parameter(
    names = { "-f", "--force" },
    description = "Forces script to continue even if SQL errors are encountered."
  )
  public boolean force = false;

  @Parameter(
    names = { "-v", "--version" },
    description = "Display version information"
  )
  public boolean showVersion = false;

  private PrintStream printStream = System.out;

  public File envPath;

  public File scriptPath;

  public File driverPath;

  public CommandLine() {
  }

  public void setPrintStream(PrintStream out) {
    this.printStream = out;
  }

  public PrintStream getPrintStream() {
    return this.printStream;
  }

  public void execute(String[] args) {
    final JCommander jCommander = new JCommander(this);
    jCommander.setProgramName(System.getProperty("app.name"));
    jCommander.addCommand("bootstrap", new BootstrapCommand(this), "bs");
    jCommander.addCommand("down", new DownCommand(this), "d");
    jCommander.addCommand("init", new InitializeCommand(this), "i");
    jCommander.addCommand("new", new NewCommand(this), "n");
    jCommander.addCommand("pending", new PendingCommand(this), "p");
    jCommander.addCommand("script", new ScriptCommand(this), "sc");
    jCommander.addCommand("status", new StatusCommand(this), "st");
    jCommander.addCommand("up", new UpCommand(this), "u");
    jCommander.addCommand("version", new VersionCommand(this), "v");
    jCommander.parse(args);

    if ( help )
    {
        jCommander.usage();
    }
    else
    {
      this.envPath = subdirectory(basePath, "environments");
      this.scriptPath = subdirectory(basePath, "scripts");
      this.driverPath = subdirectory(basePath, "drivers");

      if (showVersion) {
        printVersionInformation();
      }

      String parsedCommand = jCommander.getParsedAlias();

      boolean error = false;
      try {
        if (parsedCommand != null) {
          Command command = Command.class.cast( jCommander.getCommands().get( parsedCommand ).getObjects().get( 0 ) );
          runCommand(parsedCommand, command);
        }
      } catch ( Exception e ) {
        error = true;
        printStream.println("\nERROR: " + e.getMessage());
        if (trace) {
          e.printStackTrace(printStream);
        }
      } finally {
        printStream.flush();
        if (error) {
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

  private File subdirectory(File base, String sub) {
    return new File( base, sub );
  }

  private void printVersionInformation()
  {
    Properties properties = new Properties();
    InputStream input = getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.mybatis/mybatis-migrations/pom.properties");

    if (input != null) {
      try {
        properties.load(input);
      } catch (IOException e) {
          // ignore, just don't load the properties
      } finally {
        try {
          input.close();
        } catch (IOException e) {
          // close quietly
        }
      }
    }

    printStream.printf("%s %s (%s)%n",
                       properties.getProperty("name"),
                       properties.getProperty("version"),
                       properties.getProperty("build"));

    String migrationsHome = System.getenv("MIGRATIONS_HOME");
    printStream.printf("Migrations home: %s%n",
                       (migrationsHome != null && migrationsHome.length() > 0) ? migrationsHome : "UNKNOWN");

    printStream.printf("Java version: %s, vendor: %s%n",
                       System.getProperty("java.version"),
                       System.getProperty("java.vendor"));
    printStream.printf("Java home: %s%n", System.getProperty("java.home"));
    printStream.printf("Default locale: %s_%s, platform encoding: %s%n",
                       System.getProperty("user.language"),
                       System.getProperty("user.country"),
                       System.getProperty("sun.jnu.encoding"));
    printStream.printf("OS name: \"%s\", version: \"%s\", arch: \"%s\", family: \"%s\"%n",
                       System.getProperty("os.name").toLowerCase(),
                       System.getProperty("os.version"),
                       System.getProperty("os.arch"),
                       getOsFamily() );
  }

  private static final String getOsFamily()
  {
    String osName = System.getProperty("os.name").toLowerCase();
    String pathSep = System.getProperty("path.separator");

    if (osName.indexOf("windows") != -1) {
      return "windows";
    } else if (osName.indexOf("os/2") != -1) {
      return "os/2";
    } else if (osName.indexOf("z/os") != -1 || osName.indexOf("os/390") != -1) {
      return "z/os";
    } else if (osName.indexOf("os/400") != -1) {
      return "os/400";
    } else if (pathSep.equals(";")) {
      return "dos";
    } else if (osName.indexOf("mac") != -1) {
      if (osName.endsWith("x")) {
        return "mac"; // MACOSX
      }
      return "unix";
    } else if (osName.indexOf("nonstop_kernel") != -1) {
      return "tandem";
    } else if (osName.indexOf("openvms") != -1) {
      return "openvms";
    } else if (pathSep.equals(":")) {
      return "unix";
    }

    return "undefined";
  }

}
