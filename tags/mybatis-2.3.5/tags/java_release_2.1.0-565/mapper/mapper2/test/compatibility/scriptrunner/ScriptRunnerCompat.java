package compatibility.scriptrunner;

import com.ibatis.common.jdbc.ScriptRunner;
import com.ibatis.common.resources.Resources;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

public class ScriptRunnerCompat {

  public static void runInitializationScript(Connection conn, String script)
      throws SQLException, IOException {

    // -- Create a new ScriptRunner instance using default constructor.
    ScriptRunner runner = new ScriptRunner();

    // -- Another constructor can accept database configuration information as a property map.
    // ScriptRunner runner = new ScriptRunner (properties);

    // -- You can configure logwriters for progress and error reporting.
    // -- Default is System.out and System.err, we'll keep them quiet for the demo.
    runner.setLogWriter(null);
    runner.setErrorLogWriter(null);

    // -- Load the script as a Reader using the iBATIS common Resources class
    Reader reader = Resources.getResourceAsReader(script);

    // -- Run the script from the Reader
    runner.runScript(conn, reader);

    // -- If the ScriptRunner was configured with a properties file, you don't need to pass in a connection
    // runner.runInitializationScript(reader);

  }


}
