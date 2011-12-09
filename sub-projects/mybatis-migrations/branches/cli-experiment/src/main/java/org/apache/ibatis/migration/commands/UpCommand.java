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

  @Parameter( description = "[n]", arity = 1, required = false )
  public List<Integer> limits;

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
      int limit;
      if (limits != null && !limits.isEmpty()) {
        limit = limits.get(0);
      } else {
        limit = 1;
      }
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
