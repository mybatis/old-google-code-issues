package org.apache.ibatis.migration;

import org.apache.ibatis.migration.commands.*;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CommandLine {

  protected static final PrintStream out = System.out;

  private static final String PATH_PREFIX = "--path=";
  private static final String ENV_PREFIX = "--env=";
  private static final String FORCE = "--force";
  private static final String TRACE = "--trace";
  private static final String HELP = "--help";

  private static final String INIT = "init";
  private static final String BOOTSTRAP = "bootstrap";
  private static final String NEW = "new";
  private static final String UP = "up";
  private static final String DOWN = "down";
  private static final String PENDING = "pending";
  private static final String SCRIPT = "script";
  private static final String VERSION = "version";
  private static final String STATUS = "status";

  private static final Set<String> KNOWN_COMMANDS = Collections.unmodifiableSet(
      new HashSet<String>(Arrays.asList(INIT, NEW, UP, VERSION, DOWN, PENDING, STATUS, BOOTSTRAP, SCRIPT)));

  private File repository;
  private String environment;
  private boolean force;
  private boolean trace;

  private String command;
  private String params;

  private String parseError;
  private boolean help;

  public CommandLine(String[] args) {
    parse(args);
    validate();
  }

  public void execute() {
    try {
      if (help) {
        printUsage();
      } else if (parseError != null) {
        printError();
        printUsage();
      } else {
        try {
          runCommand();
        } catch (MigrationException e) {
          out.println("\nERROR: " + e.getMessage());
          if (trace) {
            e.printStackTrace();
          }
        }
      }
    } finally {
      out.flush();
    }
  }

  private void runCommand() {
    if (INIT.equals(command)) {
      new InitializeCommand(repository, environment, force).execute(params);
    } else if (BOOTSTRAP.equals(command)) {
      new BootstrapCommand(repository, environment, force).execute(params);
    } else if (NEW.equals(command)) {
      new NewCommand(repository, environment, force).execute(params);
    } else if (STATUS.equals(command)) {
      new StatusCommand(repository, environment, force).execute(params);
    } else if (UP.equals(command)) {
      new UpCommand(repository, environment, force).execute(params);
    } else if (VERSION.equals(command)) {
      new VersionCommand(repository, environment, force).execute(params);
    } else if (PENDING.equals(command)) {
      new PendingCommand(repository, environment, force).execute(params);
    } else if (DOWN.equals(command)) {
      new DownCommand(repository, environment, force).execute(params);
    } else if (SCRIPT.equals(command)) {
      new ScriptCommand(repository, environment, force).execute(params);
    } else {
      throw new MigrationException("Attempt to execute unkown command.");
    }
  }

  private void parse(String[] args) {
    for (String arg : args) {
      if (arg.startsWith(PATH_PREFIX) && arg.length() > PATH_PREFIX.length()) {
        repository = new File(arg.split("=")[1]);
      } else if (arg.startsWith(ENV_PREFIX) && arg.length() > ENV_PREFIX.length()) {
        environment = arg.split("=")[1];
      } else if (arg.startsWith(TRACE)) {
        trace = true;
      } else if (arg.startsWith(FORCE)) {
        force = true;
      } else if (arg.startsWith(HELP)) {
        help = true;
      } else if (command == null) {
        command = arg;
      } else if (params == null) {
        params = arg;
      } else {
        params += " ";
        params += arg;
      }
    }
  }

  private void validate() {
    if (repository == null) {
      repository = new File("./");
    }
    if (environment == null) {
      environment = "development";
    }
    if (repository.exists() && !repository.isDirectory()) {
      parseError = ("Migrations path must be a directory: " + repository.getAbsolutePath());
    } else {
      repository = new File(repository.getAbsolutePath());
      if (command == null) {
        parseError = "No command specified.";
      } else {
        if (!KNOWN_COMMANDS.contains(command)) {
          parseError = "Unknown command: " + command;
        }
      }
    }
  }

  private void printError() {
    out.println(parseError);
    out.flush();
  }

  private void printUsage() {
    out.println();
    out.println("Usage: migrate command [parameter] [--path=<directory>] [--env=<environment>]");
    out.println();
    out.println("--path=<directory>   Path to repository.  Default current working directory.");
    out.println("--env=<environment>  Environment to configure. Default environment is 'development'.");
    out.println("--force              Forces script to continue even if SQL errors are encountered.");
    out.println("--help               Displays this usage message.");
    out.println("--trace              Shows additional error details (if any).");
    out.println();
    out.println("Commands:");
    out.println("  init               Creates (if necessary) and initializes a migration path.");
    out.println("  bootstrap          Runs the bootstrap SQL script (see scripts/bootstrap.sql for more).");
    out.println("  new <description>  Creates a new migration with the provided description.");
    out.println("  up                 Run all unapplied migrations.");
    out.println("  down               Undoes the last migration applied to the database.");
    out.println("  version <version>  Migrates the database up or down to the specified version.");
    out.println("  pending            Force executes pending migrations out of order (not recommended).");
    out.println("  status             Prints the changelog from the database if the changelog table exists.");
    out.println("  script <v1> <v2>   Generates a delta migration script from version v1 to v2 (undo if v1 > v2).");
    out.println();
    out.flush();
  }

}