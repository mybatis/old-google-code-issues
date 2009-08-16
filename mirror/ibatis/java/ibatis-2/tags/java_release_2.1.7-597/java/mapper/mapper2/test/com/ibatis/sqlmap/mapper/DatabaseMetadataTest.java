package com.ibatis.sqlmap.mapper;

import com.ibatis.sqlmap.engine.mapper.metadata.Database;
import com.ibatis.sqlmap.engine.mapper.metadata.DatabaseFactory;
import com.ibatis.sqlmap.engine.mapper.metadata.Table;
import com.ibatis.sqlmap.BaseSqlMapTest;

public class DatabaseMetadataTest extends BaseSqlMapTest {

  protected void setUp() throws Exception {
    initSqlMap("com/ibatis/sqlmap/maps/SqlMapConfig.xml", null);
    initScript("scripts/account-init.sql");
  }

  public void testDatabaseMetaData() throws Exception {
    Database db = DatabaseFactory.newDatabase(sqlMap.getDataSource(), null, null);

    Table table = db.getTable("ACCOUNT");

    assertNotNull(table);
    assertEquals("ACCOUNT", table.getName());

    String[] columnNames = table.getColumnNames();

    assertEquals(8, columnNames.length);

    assertNotNull(table.getColumn("ACC_BANNER_OPTION"));
    assertNotNull(table.getColumn("ACC_EMAIL"));
    assertNotNull(table.getColumn("ACC_FIRST_NAME"));
    assertNotNull(table.getColumn("ACC_LAST_NAME"));
    assertNotNull(table.getColumn("ACC_ID"));
    assertNotNull(table.getColumn("ACC_DATE_ADDED"));
    assertNotNull(table.getColumn("ACC_AGE"));
    assertNotNull(table.getColumn("ACC_CART_OPTION"));
  }

}
