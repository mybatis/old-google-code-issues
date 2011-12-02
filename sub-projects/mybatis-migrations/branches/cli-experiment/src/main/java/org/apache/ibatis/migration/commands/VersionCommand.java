package org.apache.ibatis.migration.commands;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.migration.Change;
import org.apache.ibatis.migration.MigrationException;
import org.apache.ibatis.migration.MigrationsOptions;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Migrates the database up or down to the specified version." )
public class VersionCommand extends BaseCommand {

  @Parameter( description = "<version>" )
  public BigDecimal version;

  public VersionCommand(MigrationsOptions options)
  {
    super(options);
  }

  public void execute() {
    ensureVersionExists(version);

    Change change = getLastAppliedChange();
    if (version.compareTo(change.getId()) > 0) {
      options.printStream.println("Upgrading to: " + version);
      Command up = new UpCommand(options, true);
      // Command up = new UpCommand(basePath, environment, force, true);
      while (!version.equals(change.getId())) {
        up.execute();
        change = getLastAppliedChange();
      }
    } else if (version.compareTo(change.getId()) < 0) {
      options.printStream.println("Downgrading to: " + version);
      Command down = new DownCommand(options);
      // Command down = new DownCommand(basePath, environment, force);
      while (!version.equals(change.getId())) {
        down.execute();
        change = getLastAppliedChange();
      }
    } else {
      options.printStream.println("Already at version: " + version);
    }
    options.printStream.println();
  }

  private void ensureVersionExists(BigDecimal version) {
    List<Change> migrations = getMigrations();
    if (!migrations.contains(new Change(version))) {
      throw new MigrationException("A migration for the specified version number does not exist.");
    }
  }

}
