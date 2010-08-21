package com.ibatis.sqlmap;

import com.ibatis.common.resources.Resources;
import testdomain.ProcBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class OracleProcTest extends BaseSqlMapTest {
  // SETUP & TEARDOWN

  protected void setUp() throws Exception {
    Properties props = Resources.getResourceAsProperties("com/ibatis/sqlmap/maps/OracleConfig.properties");
    initSqlMap("com/ibatis/sqlmap/maps/SqlMapConfig.xml", props);
  }

  protected void tearDown() throws Exception {
  }

  public void testProcWithOutputParams() throws Exception {
    String first = "jim.smith@somewhere.com";
    String second = "bob.jackson@somewhere.com";


    Map map = new HashMap();
    map.put("email1", first);
    map.put("email2", second);
    map.put("status", "failure");

    sqlMap.update("swapEmailAddresses", map);

    assertEquals(first, map.get("email2"));
    assertEquals(second, map.get("email1"));
    assertEquals("success", map.get("status"));

  }

  public void testProcWithOutputParamsUsingBean() throws Exception {
    String first = "jim.smith@somewhere.com";
    String second = "bob.jackson@somewhere.com";

    ProcBean bean = new ProcBean();
    bean.setEmail1(first);
    bean.setEmail2(second);
    bean.setStatus("failure");

    sqlMap.update("swapEmailAddressesUsingBean", bean);

    assertEquals(first, bean.getEmail2());
    assertEquals(second, bean.getEmail1());
    assertEquals("success", bean.getStatus());

  }

  public void testProcWithNoParams() throws Exception {

    sqlMap.update("noParamProc", null);

  }
}
