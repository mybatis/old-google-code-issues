package org.apache.ibatis.executor.statement;

import org.apache.ibatis.executor.*;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.result.ResultHandler;
import org.apache.ibatis.mapping.*;

import java.sql.*;
import java.util.List;

public class RoutingStatementHandler implements StatementHandler {

  private final StatementHandler delegate;

  public RoutingStatementHandler(Executor executor, MappedStatement ms, Object parameter, int rowOffset, int rowLimit, ResultHandler resultHandler) {

    switch (ms.getStatementType()) {
      case STATEMENT:
        delegate = new SimpleStatementHandler(executor, ms, parameter, rowOffset, rowLimit, resultHandler);
        break;
      case PREPARED:
        delegate = new PreparedStatementHandler(executor, ms, parameter, rowOffset, rowLimit, resultHandler);
        break;
      case CALLABLE:
        delegate = new CallableStatementHandler(executor, ms, parameter, rowOffset, rowLimit, resultHandler);
        break;
      default:
        throw new ExecutorException("Unknown statement type: " + ms.getStatementType());
    }

  }

  public Statement prepare(Connection connection) throws SQLException {
    return delegate.prepare(connection);
  }

  public void parameterize(Statement statement) throws SQLException {
    delegate.parameterize(statement);
  }

  public void batch(Statement statement) throws SQLException {
    delegate.batch(statement);
  }

  public int update(Statement statement) throws SQLException {
    return delegate.update(statement);
  }

  public List query(Statement statement, ResultHandler resultHandler) throws SQLException {
    return delegate.query(statement, resultHandler);
  }

  public BoundSql getBoundSql() {
    return delegate.getBoundSql();
  }

  public ParameterHandler getParameterHandler() {
    return delegate.getParameterHandler();
  }
}
