package org.apache.ibatis.executor.loader;

import org.apache.ibatis.executor.*;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.Configuration;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.ExecutorType;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.logging.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.*;

public class ResultLoader {

  private static final Log log = LogFactory.getLog(Connection.class);

  protected static final Class[] LIST_INTERFACES = new Class[]{List.class};
  protected static final Class[] SET_INTERFACES = new Class[]{Set.class};

  protected final Configuration configuration;
  protected final Executor executor;
  protected final MappedStatement mappedStatement;
  protected final Object parameterObject;
  protected final Class targetType;

  protected boolean loaded;
  protected Object resultObject;

  public ResultLoader(Configuration config, Executor executor, MappedStatement mappedStatement, Object parameterObject, Class targetType) {
    this.configuration = config;
    this.executor = executor;
    this.mappedStatement = mappedStatement;
    this.parameterObject = parameterObject;
    this.targetType = targetType;
  }

  public Object loadResult() throws SQLException {
    List list = selectList();
    if (targetType != null && Set.class.isAssignableFrom(targetType)) {
      resultObject = new HashSet(list);
    } else if (targetType != null && Collection.class.isAssignableFrom(targetType)) {
      resultObject = list;
    } else if (targetType != null && targetType.isArray()) {
      resultObject = listToArray(list, targetType.getComponentType());
    } else {
      if (list.size() > 1) {
        throw new ExecutorException("Statement " + mappedStatement.getId() + " returned more than one row, where no more than one was expected.");
      } else if (list.size() == 1) {
        resultObject = list.get(0);
      }
    }
    return resultObject;
  }

  private List selectList() throws SQLException {
    Executor localExecutor = executor;
    if (localExecutor.isClosed()) {
      localExecutor = newExecutor();
    }
    try {
      return localExecutor.query(mappedStatement, parameterObject, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    } finally {
      if (executor.isClosed()) {
        localExecutor.close();
      }
    }
  }

  private Executor newExecutor() throws SQLException {
    Environment environment = configuration.getEnvironment();
    if (environment == null) throw new ExecutorException("ResultLoader could not load lazily.  Environment was not configured.");
    TransactionFactory txFactory = environment.getTransactionFactory();
    if (txFactory == null) throw new ExecutorException("ResultLoader could not load lazily.  Transaction Factory was not configured.");
    DataSource ds = environment.getDataSource();
    if (ds == null) throw new ExecutorException("ResultLoader could not load lazily.  DataSource was not configured.");
    Connection conn = ds.getConnection();
    conn = wrapConnection(conn);
    Transaction tx = txFactory.newTransaction(conn, false);
    return configuration.newExecutor(tx, ExecutorType.SIMPLE);
  }

  public boolean wasNull() {
    return resultObject == null;
  }

  private Connection wrapConnection(Connection connection) {
    if (log.isDebugEnabled()) {
      return ConnectionLogger.newInstance(connection);
    } else {
      return connection;
    }
  }

  private Object[] listToArray(List list, Class type) {
    Object array = java.lang.reflect.Array.newInstance(type, list.size());
    array = list.toArray((Object[]) array);
    return (Object[]) array;
  }

}
