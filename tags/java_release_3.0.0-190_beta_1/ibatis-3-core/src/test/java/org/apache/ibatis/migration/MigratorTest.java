package org.apache.ibatis.migration;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.SqlRunner;
import org.junit.*;
import static org.junit.Assert.*;

import javax.sql.DataSource;
import java.io.*;
import java.net.*;
import java.sql.Connection;

public class MigratorTest extends BaseDataTest {

  private static PrintStream out;
  private static StringOutputStream buffer;

  @BeforeClass
  public static void setup() throws Exception {
    out = System.out;
    buffer = new StringOutputStream();
    System.setOut(new PrintStream(buffer));

    DataSource ds = createUnpooledDataSource(BLOG_PROPERTIES);
    Connection conn = ds.getConnection();
    SqlRunner executor = new SqlRunner(conn);
    safeRun(executor, "DROP TABLE bootstrap");
    safeRun(executor, "DROP TABLE comment");
    safeRun(executor, "DROP TABLE post_tag");
    safeRun(executor, "DROP TABLE tag");
    safeRun(executor, "DROP TABLE post");
    safeRun(executor, "DROP TABLE blog");
    safeRun(executor, "DROP TABLE author");
    safeRun(executor, "DROP PROCEDURE selectTwoSetsOfAuthors");
    safeRun(executor, "DROP PROCEDURE insertAuthor");
    safeRun(executor, "DROP PROCEDURE selectAuthorViaOutParams");
    safeRun(executor, "DROP TABLE changelog");
    conn.commit();
    conn.close();
  }

  @AfterClass
  public static void teardown() {
    System.setOut(out);
  }

  @Test
  public void shouldRunThroughFullMigrationUseCaseInOneTestToEnsureOrder() throws Exception {
    File f = getExampleDir();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "bootstrap", "--env=development"));
    assertTrue(buffer.toString().contains("--// Bootstrap.sql"));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "status"));
    assertTrue(buffer.toString().contains("...pending..."));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "up"));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "status"));
    assertFalse(buffer.toString().contains("...pending..."));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "down"));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "status"));
    assertTrue(buffer.toString().contains("...pending..."));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "version", "20080827200215"));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "status"));
    assertFalse(buffer.toString().contains("...pending..."));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "down"));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "status"));
    assertTrue(buffer.toString().contains("...pending..."));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "pending"));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "status"));
    assertFalse(buffer.toString().contains("...pending..."));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "--help"));
    assertTrue(buffer.toString().contains("--help"));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "script","20080827200212","20080827200214"));
    assertFalse(buffer.toString().contains("20080827200210"));
    assertFalse(buffer.toString().contains("20080827200211"));
    assertTrue(buffer.toString().contains("20080827200212"));
    assertTrue(buffer.toString().contains("20080827200213"));
    assertTrue(buffer.toString().contains("20080827200214"));
    assertFalse(buffer.toString().contains("20080827200215"));
    assertFalse(buffer.toString().contains("--//@UNDO"));
    buffer.clear();

    Migrator.main(args("--path=" + f.getAbsolutePath(), "script","20080827200215", "20080827200213"));
    assertFalse(buffer.toString().contains("20080827200210"));
    assertFalse(buffer.toString().contains("20080827200211"));
    assertFalse(buffer.toString().contains("20080827200212"));
    assertTrue(buffer.toString().contains("20080827200213"));
    assertTrue(buffer.toString().contains("20080827200214"));
    assertTrue(buffer.toString().contains("20080827200215"));
    assertTrue(buffer.toString().contains("--//@UNDO"));
    buffer.clear();
  }


  @Test
  public void shouldInitTempDirectory() throws Exception {
    File basePath = getTempDir();
    Migrator.main(args("--path=" + basePath.getAbsolutePath(), "init"));
    assertNotNull(basePath.list());
    assertEquals(4, basePath.list().length);
    File scriptPath = new File(basePath.getCanonicalPath() + File.separator + "scripts");
    assertEquals(3, scriptPath.list().length);
    Migrator.main(args("--path=" + basePath.getAbsolutePath(), "new", "test new migration"));
    assertEquals(4, scriptPath.list().length);
  }

  private String[] args(String... args) {
    return args;
  }

  private File getExampleDir() throws IOException, URISyntaxException {
    URL resourceURL = Resources.getResourceURL(getClass().getClassLoader(), "org/apache/ibatis/migration/example/");
    File f = new File(resourceURL.toURI());
    assertTrue(f.exists());
    assertTrue(f.isDirectory());
    return f;
  }

  private File getTempDir() throws IOException {
    File f = File.createTempFile("migration", "test");
    assertTrue(f.delete());
    assertTrue(f.mkdir());
    assertTrue(f.exists());
    assertTrue(f.isDirectory());
    f.deleteOnExit();
    return f;
  }

  private static class StringOutputStream extends OutputStream {
    private StringBuilder builder = new StringBuilder();

    public void write(int b) throws IOException {
      builder.append((char) b);
//      out.write(b);
    }

    public String toString() {
      return builder.toString();
    }

    public void clear() {
      builder.setLength(0);
    }
  }

  private static void safeRun(SqlRunner executor, String sql) {
    try {
      executor.run(sql);
    } catch (Exception e) {
      //ignore
    }
  }
}
