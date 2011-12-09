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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.migration.Change;
import org.apache.ibatis.migration.MigrationsOptions;

import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Prints the changelog from the database if the changelog table exists." )
public class StatusCommand extends BaseCommand {

  public StatusCommand(MigrationsOptions options)
  {
    super(options);
  }

  public void execute() {
    options.printStream.println("ID             Applied At          Description");
    options.printStream.println(horizontalLine("", 80));
    List<Change> merged = new ArrayList<Change>();
    List<Change> migrations = getMigrations();
    if (changelogExists()) {
      List<Change> changelog = getChangelog();
      for (Change change : migrations) {
        int index = changelog.indexOf(change);
        if (index > -1) {
          merged.add(changelog.get(index));
        } else {
          merged.add(change);
        }
      }
      Collections.sort(merged);
    } else {
      merged.addAll(migrations);
    }
    for (Change change : merged) {
      options.printStream.println(change);
    }
    options.printStream.println();
  }


}
