package org.apache.ibatis.transaction;

import java.sql.*;

public interface Transaction {

  Connection getConnection();

  void commit() throws SQLException;

  void rollback() throws SQLException;

  void close() throws SQLException;

}
