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

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.io.ExternalResources;
import org.apache.ibatis.migration.CommandLine;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Creates a new migration with the provided description." )
public class NewCommand extends BaseCommand {

  private static final String MIGRATIONS_HOME = "MIGRATIONS_HOME";
  private static final String MIGRATIONS_HOME_PROPERTY = "migrationHome";
  private static final String CUSTOM_NEW_COMMAND_TEMPATE_PROPERTY = "new_command.template";
  private static final String MIGRATIONS_PROPERTIES = "migration.properties";

  @Parameter(
    description = "<description>",
    validateWith = DescriptionValidator.class,
    required = true,
    arity = 1 )
  public List<String> descriptions;

  public NewCommand(CommandLine commandLine)
  {
    super(commandLine);
  }

  public void execute() {
    String description = descriptions.get(0);
    Properties variables = new Properties();
    variables.setProperty("description", description);
    existingEnvironmentFile();
    String filename = getNextIDAsString() + "_" + description.replace(' ', '_') + ".sql";
    String migrationsHome = "";
    migrationsHome = System.getenv(MIGRATIONS_HOME);

    // Check if there is a system property
    if (migrationsHome == null) {
      migrationsHome = System.getProperty(MIGRATIONS_HOME_PROPERTY);
    }

    if (commandLine.template != null) {
      copyExternalResourceTo(commandLine.template, scriptFile(filename), variables);
    } else if ((migrationsHome != null) && (!migrationsHome.equals(""))) {
      try {
        //get template name from properties file
        final String customConfiguredTemplate = ExternalResources.getConfiguredTemplate(migrationsHome + "/" + MIGRATIONS_PROPERTIES, CUSTOM_NEW_COMMAND_TEMPATE_PROPERTY);
        copyExternalResourceTo(migrationsHome + "/" + customConfiguredTemplate, scriptFile(filename), variables);
      } catch (FileNotFoundException e) {
        commandLine.getPrintStream().append("Your migrations configuration did not find your custom template.  Using the default template.");
        copyDefaultTemplate(variables, filename);
      }
    } else {
      copyDefaultTemplate(variables, filename);
    }

    commandLine.getPrintStream().println("Done!");
    commandLine.getPrintStream().println();
  }

  private void copyDefaultTemplate(Properties variables, String filename) {
    copyResourceTo("org/apache/ibatis/migration/template_migration.sql", scriptFile(filename), variables);
  }
}
