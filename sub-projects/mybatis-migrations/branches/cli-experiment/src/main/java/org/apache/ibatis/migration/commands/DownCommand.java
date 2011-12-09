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
package org.apache.ibatis.migration.commands;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.apache.ibatis.migration.Change;
import org.apache.ibatis.migration.CommandLine;
import org.apache.ibatis.migration.MigrationException;
import org.apache.ibatis.migration.MigrationReader;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Undoes migrations applied to the database. ONE by default or 'n' specified." )
public class DownCommand extends BaseCommand {

  @Parameter( description = "[n]", arity = 1, required = false )
  public List<Integer> limits;

  public DownCommand(CommandLine commandLine)
  {
    super(commandLine);
  }

  public void execute() {
    int limit;
    if (limits != null && !limits.isEmpty()) {
      limit = limits.get(0);
    } else {
      limit = 1;
    }
    try {
      Change lastChange = getLastAppliedChange();
      List<Change> migrations = getMigrations();
      Collections.reverse(migrations);
      int steps = 0;
      for (Change change : migrations) {
        if (change.getId().equals(lastChange.getId())) {
          commandLine.getPrintStream().println(horizontalLine("Undoing: " + change.getFilename(), 80));
          ScriptRunner runner = getScriptRunner();
          try {
            runner.runScript(new MigrationReader(scriptFileReader(scriptFile(change.getFilename())), true, environmentProperties()));
          } finally {
            runner.closeConnection();
          }
          if (changelogExists()) {
            deleteChange(change);
          } else {
            commandLine.getPrintStream().println("Changelog doesn't exist. No further migrations will be undone (normal for the last migration).");
          }
          commandLine.getPrintStream().println();
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
