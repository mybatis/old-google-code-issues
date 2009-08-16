package com.ibatis.sqlmap.engine.mapper.metadata;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;

public class DatabaseFactory {

  private DatabaseFactory() {
  }

  public static Database newDatabase (DataSource dataSource, String catalog, String schema) throws SQLException {
    Database database = new Database (catalog, schema);
    Connection conn = dataSource.getConnection();
    ResultSet rs = null;
    try {
      DatabaseMetaData dbmd = conn.getMetaData();
      rs = dbmd.getColumns(catalog, schema, null, null);
      while (rs.next()) {
        String tableName = rs.getString ("TABLE_NAME");
        String columnName = rs.getString ("COLUMN_NAME");
        int dataType = Integer.parseInt(rs.getString ("DATA_TYPE"));
        Table table = database.getTable(tableName);
        if (table == null) {
          table = new Table(tableName);
          database.addTable(table);
        }
        table.addColumn(new Column(columnName, dataType));
      }
    } finally {
      try {
        if (rs != null) rs.close();
      } finally {
        conn.close();
      }
    }
    return database;
  }

}
