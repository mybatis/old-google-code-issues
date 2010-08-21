package compatibility;

import com.ibatis.common.util.Stopwatch;
import compatibility.dao.CrudCompat;
import compatibility.scriptrunner.ScriptRunnerCompat;
import compatibility.sqlmap.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class RunAllCompat extends BaseCompat implements Runnable {

  private static final int THREADS = 250;
  private static final int STATEMENTS = 19;

  private static final Stopwatch stopwatch = new Stopwatch();

  public static void main(String[] args) throws Exception {

    // -- Set this to false to keep the compatibility quiet.
    setLogEnabled(false);

    // -- We'll borrow the already configured connection from an SQL Map
    Connection conn = SqlMapConfigCompat.getSqlMap().getDataSource().getConnection();
    ScriptRunnerCompat.runInitializationScript(conn, "compatibility/ddl/hsql-init.sql");
    conn.commit();
    conn.close();

    // Give everything a chance to initialize
    new RunAllCompat().run();

    stopwatch.reset();

    // Let's throw a bunch of threads against this to give it a good workout.
    ThreadGroup group = new ThreadGroup("Comatibility");
    long t = System.currentTimeMillis();
    List threadList = new ArrayList();
    for (int i = 0; i < THREADS; i++) {
      Thread thread = new Thread(group, new RunAllCompat());
      threadList.add(thread);
    }
    for (int i = 0; i < THREADS; i++) {
      ((Thread) threadList.get(i)).start();
    }
    while (group.activeCount() > 0) {
      Thread.sleep(10);
    }

    t = System.currentTimeMillis() - t;
    System.out.println("Ran " + (STATEMENTS * THREADS) + " statements using " + THREADS + " threads in " + t + " milliseconds.");

    System.out.flush();
    System.err.flush();

  }

  private static Object threadLock = new Object();
  private static int threadCount = 0;

  public void run() {
    synchronized (threadLock) {
      println((++threadCount) + " Threads Running (start)");
    }
    try {
      println("[**** QUERY FOR OBJECT ***]");
      QueryForObjectCompat.queryForObjectCompatibility();
      println("[**** QUERY FOR LIST ***]");
      QueryForListCompat.queryForListCompatibility();
      println("[**** SIMPLE TYPES ***]");
      SimpleTypesCompat.simpleTypesCompatibility();
      println("[**** HASH MAP ***]");
      HashMapCompat.hashMapCompatibility();
      println("[**** ROW HANDLER ***]");
      RowHandlerCompat.rowHandlerCompatibility();
      println("[**** DYNAMIC QUERY ***]");
      DynamicQueryCompat.dynamicQueryCompatibility();
      println("[**** INSERT ***]");
      int id = InsertCompat.insertCompatibility();
      println("[**** UPDATE ***]");
      UpdateCompat.updateCompatibility(id);
      println("[**** DELETE ***]");
      DeleteCompat.deleteCompatibility(id);

      println("[**** DAO INSERT ***]");
      id = CrudCompat.insertCompatibility();
      println("[**** DAO UPDATE ***]");
      CrudCompat.updateCompatibility(id);
      println("[**** DAO GET ***]");
      CrudCompat.getAccountCompatibility();
      println("[**** DAO DELETE ***]");
      CrudCompat.deleteCompatibility(id);

    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      synchronized (threadLock) {
        println((--threadCount) + " Threads Running (finish)");
      }
    }
  }

}
