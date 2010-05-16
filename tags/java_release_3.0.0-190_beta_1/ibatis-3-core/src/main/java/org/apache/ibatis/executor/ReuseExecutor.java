package org.apache.ibatis.executor;

import org.apache.ibatis.executor.result.ResultHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.transaction.Transaction;

import java.sql.*;
import java.util.*;

public class ReuseExecutor extends BaseExecutor {

  private final Map<String, Statement> statementMap = new HashMap<String, Statement>();

  public ReuseExecutor(Transaction transaction) {
    super(transaction);
  }

  public int doUpdate(MappedStatement ms, Object parameter)
      throws SQLException {
    Configuration configuration = ms.getConfiguration();
    StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, null);
    Statement stmt = prepareStatement(handler);
    return handler.update(stmt);
  }

  public List doQuery(MappedStatement ms, Object parameter, int rowOffset, int rowLimit, ResultHandler resultHandler) throws SQLException {
    Configuration configuration = ms.getConfiguration();
    StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, rowOffset, rowLimit, resultHandler);
    Statement stmt = prepareStatement(handler);
    return handler.query(stmt, resultHandler);
  }

  public List doFlushStatements()
      throws SQLException {
    for (Statement stmt : statementMap.values()) {
      closeStatement(stmt);
    }
    return Collections.EMPTY_LIST;
  }

  private Statement prepareStatement(StatementHandler handler)
      throws SQLException {
    Statement stmt;
    BoundSql boundSql = handler.getBoundSql();
    String sql = boundSql.getSql();
    if (hasStatementFor(sql)) {
      stmt = getStatement(sql);
    } else {
      Connection connection = transaction.getConnection();
      stmt = handler.prepare(connection);
      putStatement(sql, stmt);
    }
    handler.parameterize(stmt);
    return stmt;
  }

  private boolean hasStatementFor(String sql) {
    return statementMap.keySet().contains(sql);
  }

  private Statement getStatement(String s) {
    return statementMap.get(s);
  }

  private void putStatement(String sql, Statement stmt) {
    statementMap.put(sql, stmt);
  }

}
