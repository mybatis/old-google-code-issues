package org.apache.ibatis.migration.commands;

import org.apache.ibatis.jdbc.*;
import org.apache.ibatis.migration.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class DownCommand extends BaseCommand {

  public DownCommand(File repository, String environment, boolean force) {
    super(repository, environment, force);
  }

  public void execute(String... params) {
    try {
      Change lastChange = getLastAppliedChange();
      List<Change> migrations = getMigrations();
      Collections.reverse(migrations);
      for (Change change : migrations) {
        if (change.getId().equals(lastChange.getId())) {
          out.println(horizontalLine("Undoing: " + change.getFilename(), 80));
          ScriptRunner runner = getScriptRunner();
          try {
            runner.runScript(new MigrationReader(new FileReader(scriptFile(change.getFilename())), true, environmentProperties()));
          } finally {
            runner.closeConnection();
          }
          if (changelogExists()) {
            deleteChange(change);
          } else {
            out.println("Changelog doesn't exist. No further migrations will be undone (normal for the last migration).");
          }
          out.println();
          break;
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


  protected void reverse(Comparable[] comparable) {
    Arrays.sort(comparable, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Comparable) o2).compareTo(o1);
      }
    });
  }

}
