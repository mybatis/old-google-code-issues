package org.apache.ibatis.migration.commands;

import java.util.List;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.migration.Change;
import org.apache.ibatis.migration.MigrationException;
import org.apache.ibatis.migration.MigrationReader;
import org.apache.ibatis.migration.MigrationsOptions;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Run unapplied migrations, ALL by default, or 'n' specified." )
public class UpCommand extends BaseCommand {

  private final boolean runOneStepOnly;

  @Parameter( description = "[n]" )
  private int limit;

  public UpCommand(MigrationsOptions options)
  {
    this(options, false);
  }

  public UpCommand(MigrationsOptions options, boolean runOneStepOnly)
  {
    super(options);
    this.runOneStepOnly = runOneStepOnly;
  }

  public void execute() {
    try {
      Change lastChange = null;
      if (changelogExists()) {
        lastChange = getLastAppliedChange();
      }
      List<Change> migrations = getMigrations();
      int steps = 0;
      for (Change change : migrations) {
        if (lastChange == null || change.getId().compareTo(lastChange.getId()) > 0) {
          options.printStream.println(horizontalLine("Applying: " + change.getFilename(), 80));
          ScriptRunner runner = getScriptRunner();
          try {
            runner.runScript(new MigrationReader(scriptFileReader(scriptFile(change.getFilename())), false, environmentProperties()));
          } finally {
            runner.closeConnection();
          }
          insertChangelog(change);
          options.printStream.println();
          steps++;
          if (steps == limit || runOneStepOnly) {
            break;
          }
        }
      }
    } catch (Exception e) {
      throw new MigrationException("Error executing command.  Cause: " + e, e);
    }
  }

}
