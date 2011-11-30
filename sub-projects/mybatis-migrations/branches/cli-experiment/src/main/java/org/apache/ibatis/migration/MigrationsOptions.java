package org.apache.ibatis.migration;

import java.io.File;
import java.io.PrintStream;

import org.apache.ibatis.migration.commands.MigrationsPathConverterValidator;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( separators = "=" )
public final class MigrationsOptions
{

  @Parameter( names = "--trace", description = "Shows additional error details (if any)." )
  public boolean trace = false;

  @Parameter( names = "--help", description = "Displays this usage message." )
  public boolean help = false;

  @Parameter(
   names = { "--path" },
   converter = MigrationsPathConverterValidator.class,
   validateWith = MigrationsPathConverterValidator.class,
   description = "Path to repository.  Default current working directory."
  )
  public File basePath = new File( System.getProperty( "user.dir" ) );

  @Parameter( names = { "--env" }, description = "Environment to configure." )
  public String environment = "development";

  @Parameter( names = { "--template" }, description = "Path to custom template for creating new sql scripts." )
  public String template;

  @Parameter( names = { "--force" } )
  public boolean force = false;

  public PrintStream printStream;

  public File envPath;

  public File scriptPath;

  public File driverPath;

  public void init()
  {
    this.envPath = subdirectory(basePath, "environments");
    this.scriptPath = subdirectory(basePath, "scripts");
    this.driverPath = subdirectory(basePath, "drivers");
  }

  private File subdirectory(File base, String sub) {
    return new File( base, sub );
  }

}
