package org.apache.ibatis.executor;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.transaction.Transaction;

import java.sql.*;
import java.util.*;

public class SimpleExecutor extends BaseExecutor {

  public SimpleExecutor(Transaction transaction) {
    super(transaction);
  }

  public int doUpdate(MappedStatement ms, Object parameter)
      throws SQLException {
    Statement stmt = null;
    try {
      Configuration configuration = ms.getConfiguration();
      StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null);
      stmt = prepareStatement(handler);
      return handler.update(stmt);
    } finally {
      closeStatement(stmt);
    }
  }

  public List doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
    Statement stmt = null;
    try {
      Configuration configuration = ms.getConfiguration();
      StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, rowBounds, resultHandler);
      stmt = prepareStatement(handler);
      return handler.query(stmt, resultHandler);
    } finally {
      closeStatement(stmt);
    }
  }

  public List doFlushStatements()
      throws SQLException {
    return Collections.EMPTY_LIST;
  }

  private Statement prepareStatement(StatementHandler handler) throws SQLException {
    Statement stmt;
    Connection connection = transaction.getConnection();
    stmt = handler.prepare(connection);
    handler.parameterize(stmt);
    return stmt;
  }

}
