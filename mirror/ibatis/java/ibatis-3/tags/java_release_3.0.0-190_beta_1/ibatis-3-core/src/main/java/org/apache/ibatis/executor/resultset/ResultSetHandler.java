package org.apache.ibatis.executor.resultset;

import java.sql.*;
import java.util.List;

public interface ResultSetHandler {

  List handleResultSets(Statement stmt) throws SQLException;

  void handleOutputParameters(CallableStatement cs) throws SQLException;

}
