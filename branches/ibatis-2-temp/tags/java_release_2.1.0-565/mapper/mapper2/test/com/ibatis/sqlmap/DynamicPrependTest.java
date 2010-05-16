package com.ibatis.sqlmap;

import testdomain.Account;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class DynamicPrependTest extends BaseSqlMapTest {

  protected void setUp() throws Exception {
    initSqlMap("com/ibatis/sqlmap/maps/SqlMapConfig.xml", null);
    initScript("scripts/account-init.sql");
  }

  protected void tearDown() throws Exception {
  }

  // Iterate with prepend

  public void testIterateWithPrepend1() throws SQLException {
    List params = Arrays.asList(new Integer[]{new Integer(1), new Integer(2), new Integer(3)});
    List list = sqlMap.queryForList("dynamicIterateWithPrepend1", params);
    assertAccount1((Account) list.get(0));
    assertEquals(3, list.size());
  }

  public void testIterateWithPrepend2() throws SQLException {
    List params = Arrays.asList(new Integer[]{new Integer(1), new Integer(2), new Integer(3)});
    List list = sqlMap.queryForList("dynamicIterateWithPrepend2", params);
    assertAccount1((Account) list.get(0));
    assertEquals(3, list.size());
  }

  public void testIterateWithPrepend3() throws SQLException {
    List params = Arrays.asList(new Integer[]{new Integer(1), new Integer(2), new Integer(3)});
    List list = sqlMap.queryForList("dynamicIterateWithPrepend3", params);
    assertAccount1((Account) list.get(0));
    assertEquals(3, list.size());
  }

  public void testDynamicWithPrepend1() throws SQLException {
    Account account = new Account();
    account.setId(1);
    account = (Account) sqlMap.queryForObject("dynamicWithPrepend", account);
    assertAccount1(account);
  }

  public void testDynamicWithPrepend2() throws SQLException {
    Account account = new Account();
    account.setId(1);
    account.setFirstName("Clinton");
    account = (Account) sqlMap.queryForObject("dynamicWithPrepend", account);
    assertAccount1(account);
  }

  public void testDynamicWithPrepend3() throws SQLException {
    Account account = new Account();
    account.setId(1);
    account.setFirstName("Clinton");
    account.setLastName("Begin");
    account = (Account) sqlMap.queryForObject("dynamicWithPrepend", account);
    assertAccount1(account);
  }

  public void testIterateWithPrepend4() throws SQLException {
    List list = sqlMap.queryForList("dynamicWithPrepend", null);
    assertAccount1((Account) list.get(0));
    assertEquals(5, list.size());
  }

  public void testIterateWithTwoPrepends() throws SQLException {
    Account account = new Account();
    account.setId(1);
    account.setFirstName("Clinton");
    account = (Account) sqlMap.queryForObject("dynamicWithPrepend", account);
    assertNotNull(account);
    assertAccount1(account);

    List list = sqlMap.queryForList("dynamicWithTwoDynamicElements", account);
    assertAccount1((Account) list.get(0));
  }

  public void testComplexDynamic() throws SQLException {
    Account account = new Account();
    account.setId(1);
    account.setFirstName("Clinton");
    account.setLastName("Begin");
    List list = sqlMap.queryForList("complexDynamicStatement", account);
    assertAccount1((Account) list.get(0));
  }
}
