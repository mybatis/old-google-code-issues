package compatibility;

public class BaseCompat {

  // -- You'd probably use a sequence generator for a unique ID
  private static int nextId = 1000;
  private static Object SYNCH = new Object();

  private static boolean logEnabled = true;

  public static int getNextId() {
    synchronized (SYNCH) {
      return nextId++;
    }
  }

  public static boolean isLogEnabled() {
    return logEnabled;
  }

  public static void setLogEnabled(boolean logEnabled) {
    BaseCompat.logEnabled = logEnabled;
  }

  public static void println(Object o) {
    if (logEnabled) {
      System.out.println(o);
    }
  }

}
