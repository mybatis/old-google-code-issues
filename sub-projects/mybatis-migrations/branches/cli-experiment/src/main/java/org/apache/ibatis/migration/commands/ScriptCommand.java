package org.apache.ibatis.migration.commands;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.migration.Change;
import org.apache.ibatis.migration.MigrationException;
import org.apache.ibatis.migration.MigrationReader;
import org.apache.ibatis.migration.MigrationsOptions;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Generates a delta migration script from version v1 to v2 (undo if v1 > v2)." )
public class ScriptCommand extends BaseCommand {

  @Parameter(
    arity = 2,
    description = "<v1> <v2>",
    converter = VersionConverterValidator.class,
    validateWith = VersionConverterValidator.class
  )
  public List<BigDecimal> versions;

  public ScriptCommand(MigrationsOptions options)
  {
    super(options);
  }

  public void execute() {
    try {
      BigDecimal v1 = versions.get( 0 );
      BigDecimal v2 = versions.get( 1 );
      boolean undo = v1.compareTo(v2) > 0;
      Properties variables = environmentProperties();
      List<Change> migrations = getMigrations();
      Collections.sort(migrations);
      if (undo) {
        Collections.reverse(migrations);
      }
      for (Change change : migrations) {
        if (shouldRun(change, v1, v2)) {
          options.printStream.println("-- " + change.getFilename());
          File file = scriptFile(change.getFilename());
          MigrationReader migrationReader = new MigrationReader(scriptFileReader(file), undo, variables);
          char[] cbuf = new char[1024];
          int l;
          while ((l = migrationReader.read(cbuf)) == cbuf.length) {
            options.printStream.print(new String(cbuf, 0, l));
          }
          options.printStream.print(new String(cbuf, 0, l - 1));
          options.printStream.println();
          options.printStream.println();
          options.printStream.println(undo ? generateVersionDelete(change) : generateVersionInsert(change));
          options.printStream.println();
        }
      }
    } catch (IOException e) {
      throw new MigrationException("Error generating script. Cause: " + e, e);
    }

  }

  private String generateVersionInsert(Change change) {
    return "INSERT INTO " + changelogTable() + " (ID, APPLIED_AT, DESCRIPTION) " +
        "VALUES (" + change.getId() + ", '" + generateAppliedTimeStampAsString() + "', '" + change.getDescription().replace('\'', ' ') + "');";
  }

  private String generateVersionDelete(Change change) {
    return "DELETE FROM " + changelogTable() + " WHERE ID = " + change.getId() + ";";
  }

  private boolean shouldRun(Change change, BigDecimal v1, BigDecimal v2) {
    BigDecimal id = change.getId();
    if (v1.compareTo(v2) > 0) {
      return (id.compareTo(v2) >= 0 && id.compareTo(v1) <= 0);
    } else {
      return (id.compareTo(v1) >= 0 && id.compareTo(v2) <= 0);
    }
  }

}
