package com.ibatis.sqlmap;

import testdomain.*;

import java.util.List;

public class RepeatingGroupMappingTest extends BaseSqlMapTest {

  protected void setUp() throws Exception {
    initSqlMap("com/ibatis/sqlmap/maps/SqlMapConfig.xml", null);
    initScript("scripts/jpetstore-hsqldb-schema.sql");
    initScript("scripts/jpetstore-hsqldb-dataload.sql");
  }

  public void testGroupBy() throws Exception {
    List list = sqlMap.queryForList("getAllCategories",null);
    assertEquals (5, list.size());
  }

  public void testGroupByExtended() throws Exception {
    List list = sqlMap.queryForList("getAllCategoriesExtended",null);
    assertEquals (5, list.size());
  }

  public void testNestedProperties() throws Exception {
    List list = sqlMap.queryForList("getFish",null);
    assertEquals (1, list.size());

    Category cat = (Category)list.get(0);
    assertEquals ("FISH",cat.getCategoryId());
    assertEquals ("Fish",cat.getName());
    assertNotNull ("Expected product list.", cat.getProductList());
    assertEquals (4, cat.getProductList().size());

    Product product = (Product)cat.getProductList().get(0);
    assertEquals (2, product.getItemList().size());

  }
}
