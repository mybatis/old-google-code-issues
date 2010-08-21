package org.apache.ibatis.executor.statement;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.result.ResultHandler;
import org.apache.ibatis.mapping.BoundSql;

import java.sql.*;
import java.util.List;

public interface StatementHandler {

  Statement prepare(Connection connection)
      throws SQLException;

  void parameterize(Statement statement)
      throws SQLException;

  void batch(Statement statement)
      throws SQLException;

  int update(Statement statement)
      throws SQLException;

  List query(Statement statement, ResultHandler resultHandler)
      throws SQLException;

  BoundSql getBoundSql();

  ParameterHandler getParameterHandler();

}
