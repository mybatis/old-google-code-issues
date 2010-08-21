package com.ibatis.sqlmap.engine.mapper.metadata;

import javax.sql.DataSource;
import java.sql.*;

public class DatabaseFactory {
  private DatabaseFactory() {
  }

  public static Database newDatabase(DataSource dataSource, String catalog, String schema) throws SQLException {
    Database database = new Database(catalog, schema);
    Connection conn = dataSource.getConnection();
    ResultSet rs = null;
    try {
      DatabaseMetaData dbmd = conn.getMetaData();

      try {
        rs = dbmd.getColumns(catalog, schema, null, null);
        while (rs.next()) {
          String tableName = rs.getString("TABLE_NAME");
          String columnName = rs.getString("COLUMN_NAME");
          int dataType = Integer.parseInt(rs.getString("DATA_TYPE"));
          Table table = database.getTable(tableName);
          if (table == null) {
            table = new Table(tableName);
            database.addTable(table);
          }
          table.addColumn(new Column(columnName, dataType));
        }
      } finally {
        if (rs != null) rs.close();
      }

      try {
        String[] tableNames = database.getTableNames();
        for (int i=0; i < tableNames.length; i++) {
          Table table = database.getTable(tableNames[i]);
          rs = dbmd.getPrimaryKeys(catalog, schema, table.getName());
          if (rs.next()) {
            String columnName = rs.getString("COLUMN_NAME");
            table.setPrimaryKey (table.getColumn(columnName));
          }
        }
      } finally {
        if (rs != null) rs.close();
      }

    } finally {
      conn.close();
    }
    return database;
  }

}
