package org.apache.ibatis.submitted.primitive_result_type;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.sql.Connection;
import java.io.Reader;
import java.math.BigDecimal;

public class PrimitiveResultTypeTest {

  @BeforeClass
  public static void setup() throws Exception {
    SqlSession session = IbatisConfig.getSession();
    Connection conn = session.getConnection();
    ScriptRunner runner = new ScriptRunner(conn);
    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/primitive_result_type/create.sql");
    runner.runScript(reader);
  }

  @Test
  public void shouldReturnProperPrimitiveType() {
    List codes = ProductDAO.selectProductCodes();
    for (Object code : codes) {
      assertTrue(code instanceof Integer);
    }
    List lcodes = ProductDAO.selectProductCodesL();
    for (Object lcode : lcodes) {
      System.out.println(lcode instanceof Integer);
    }
    List bcodes = ProductDAO.selectProductCodesB();
    for (Object bcode : bcodes) {
      System.out.println(bcode instanceof BigDecimal);
    }
  }

}
