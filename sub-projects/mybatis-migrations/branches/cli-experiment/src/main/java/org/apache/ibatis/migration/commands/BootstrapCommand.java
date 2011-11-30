package org.apache.ibatis.migration.commands;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.migration.MigrationException;
import org.apache.ibatis.migration.MigrationReader;
import org.apache.ibatis.migration.MigrationsOptions;

import com.beust.jcommander.Parameters;

import java.io.File;

@Parameters( commandDescription = "Runs the bootstrap SQL script (see scripts/bootstrap.sql for more)." )
public class BootstrapCommand extends BaseCommand {

  public BootstrapCommand( MigrationsOptions options )
  {
    super(options);
  }

  public void execute() {
    try {
      if (changelogExists() && !options.force) {
        options.printStream.println("For your safety, the bootstrap SQL script will only run before migrations are applied (i.e. before the changelog exists).  If you're certain, you can run it using the --force option.");
      } else {
        File bootstrap = scriptFile("bootstrap.sql");
        if (bootstrap.exists()) {
          options.printStream.println(horizontalLine("Applying: bootstrap.sql", 80));
          ScriptRunner runner = getScriptRunner();
          try {
            runner.runScript(new MigrationReader(scriptFileReader(bootstrap), false, environmentProperties()));
          } finally {
            runner.closeConnection();
          }
          options.printStream.println();
        } else {
            options.printStream.println("Error, could not run bootstrap.sql.  The file does not exist.");
        }
      }
    } catch (Exception e) {
      throw new MigrationException("Error running bootstrapper.  Cause: " + e, e);
    }
  }

}
