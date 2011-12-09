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

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.migration.Change;
import org.apache.ibatis.migration.CommandLine;
import org.apache.ibatis.migration.MigrationException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Migrates the database up or down to the specified version." )
public class VersionCommand extends BaseCommand {

  @Parameter( description = "<version>", arity = 1, required = true )
  public List<BigDecimal> versions;

  public VersionCommand(CommandLine commandLine)
  {
    super(commandLine);
  }

  public void execute() {
    BigDecimal version = versions.get(0);

    ensureVersionExists(version);

    Change change = getLastAppliedChange();
    if (version.compareTo(change.getId()) > 0) {
      commandLine.getPrintStream().println("Upgrading to: " + version);
      Command up = new UpCommand(commandLine, true);
      // Command up = new UpCommand(basePath, environment, force, true);
      while (!version.equals(change.getId())) {
        up.execute();
        change = getLastAppliedChange();
      }
    } else if (version.compareTo(change.getId()) < 0) {
      commandLine.getPrintStream().println("Downgrading to: " + version);
      Command down = new DownCommand(commandLine);
      // Command down = new DownCommand(basePath, environment, force);
      while (!version.equals(change.getId())) {
        down.execute();
        change = getLastAppliedChange();
      }
    } else {
      commandLine.getPrintStream().println("Already at version: " + version);
    }
    commandLine.getPrintStream().println();
  }

  private void ensureVersionExists(BigDecimal version) {
    List<Change> migrations = getMigrations();
    if (!migrations.contains(new Change(version))) {
      throw new MigrationException("A migration for the specified version number does not exist.");
    }
  }

}
