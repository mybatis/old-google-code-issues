package org.apache.ibatis.migration.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.migration.Change;
import org.apache.ibatis.migration.MigrationException;
import org.apache.ibatis.migration.MigrationReader;
import org.apache.ibatis.migration.MigrationsOptions;

import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Force executes pending migrations out of order (not recommended)." )
public class PendingCommand extends BaseCommand {

  public PendingCommand(MigrationsOptions options)
  {
    super(options);
  }

  public void execute() {
    try {
      if (!changelogExists()) {
        throw new MigrationException("Change log doesn't exist, no migrations applied.  Try running 'up' instead.");
      }
      List<Change> pending = getPendingChanges();
      options.printStream.println("WARNING: Running pending migrations out of order can create unexpected results.");
      for (Change change : pending) {
        options.printStream.println(horizontalLine("Applying: " + change.getFilename(), 80));
        ScriptRunner runner = getScriptRunner();
        try {
          runner.runScript(new MigrationReader(scriptFileReader(scriptFile(change.getFilename())), false, environmentProperties()));
        } finally {
          runner.closeConnection();
        }
        insertChangelog(change);
        options.printStream.println();
      }
    } catch (Exception e) {
      throw new MigrationException("Error executing command.  Cause: " + e, e);
    }
  }

  private List<Change> getPendingChanges() {
    List<Change> pending = new ArrayList<Change>();
    List<Change> migrations = getMigrations();
    List<Change> changelog = getChangelog();
    for (Change change : migrations) {
      int index = changelog.indexOf(change);
      if (index < 0) {
        pending.add(change);
      }
    }
    Collections.sort(pending);
    return pending;
  }

}
