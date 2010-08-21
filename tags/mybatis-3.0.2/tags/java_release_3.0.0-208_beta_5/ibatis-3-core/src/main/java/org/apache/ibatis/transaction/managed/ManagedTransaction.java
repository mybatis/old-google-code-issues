package org.apache.ibatis.transaction.managed;

import org.apache.ibatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

public class ManagedTransaction implements Transaction {

  private Connection connection;

  public ManagedTransaction(Connection connection) {
    this.connection = connection;
  }

  public Connection getConnection() {
    return connection;
  }

  public void commit() throws SQLException {
    // Does nothing
  }

  public void rollback() throws SQLException {
    // Does nothing
  }

  public void close() throws SQLException {
    // Does nothing
  }

}
