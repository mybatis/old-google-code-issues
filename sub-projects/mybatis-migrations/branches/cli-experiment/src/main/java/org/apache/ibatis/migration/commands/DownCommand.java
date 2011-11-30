package org.apache.ibatis.migration.commands;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.apache.ibatis.migration.Change;
import org.apache.ibatis.migration.MigrationException;
import org.apache.ibatis.migration.MigrationReader;
import org.apache.ibatis.migration.MigrationsOptions;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Undoes migrations applied to the database. ONE by default or 'n' specified." )
public class DownCommand extends BaseCommand {

  @Parameter( description = "[n]" )
  public int limit;

  public DownCommand(MigrationsOptions options)
  {
    super(options);
  }

  public void execute() {
    try {
      Change lastChange = getLastAppliedChange();
      List<Change> migrations = getMigrations();
      Collections.reverse(migrations);
      int steps = 0;
      for (Change change : migrations) {
        if (change.getId().equals(lastChange.getId())) {
          options.printStream.println(horizontalLine("Undoing: " + change.getFilename(), 80));
          ScriptRunner runner = getScriptRunner();
          try {
            runner.runScript(new MigrationReader(scriptFileReader(scriptFile(change.getFilename())), true, environmentProperties()));
          } finally {
            runner.closeConnection();
          }
          if (changelogExists()) {
            deleteChange(change);
          } else {
            options.printStream.println("Changelog doesn't exist. No further migrations will be undone (normal for the last migration).");
          }
          options.printStream.println();
          steps++;
          if (steps == limit) {
            break;
          }
          lastChange = getLastAppliedChange();
        }
      }
    } catch (Exception e) {
      throw new MigrationException("Error undoing last migration.  Cause: " + e, e);
    }
  }

  protected void deleteChange(Change change) {
    SqlRunner runner = getSqlRunner();
    try {
      runner.delete("delete from " + changelogTable() + " where id = ?", change.getId());
    } catch (SQLException e) {
      throw new MigrationException("Error querying last applied migration.  Cause: " + e, e);
    } finally {
      runner.closeConnection();
    }
  }

}
