package org.apache.ibatis.session.defaults;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.*;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.*;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;

import javax.sql.DataSource;
import java.sql.*;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

  private static final Log log = LogFactory.getLog(Connection.class);

  private final Configuration configuration;
  private Environment environment;
  private DataSource dataSource;
  private TransactionFactory transactionFactory;

  public DefaultSqlSessionFactory(Configuration configuration) {
    this.configuration = configuration;
    this.environment = configuration.getEnvironment();
    if (environment == null) {
      this.dataSource = null;
      this.transactionFactory = new ManagedTransactionFactory();
    } else {
      this.dataSource = environment.getDataSource();
      this.transactionFactory = environment.getTransactionFactory();
    }
  }

  public SqlSession openSession() {
    return openSession(configuration.getDefaultExecutorType(), false);
  }

  public SqlSession openSession(boolean autoCommit) {
    return openSession(configuration.getDefaultExecutorType(), autoCommit);
  }

  public SqlSession openSession(ExecutorType execType) {
    return openSession(execType, false);
  }

  public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
    try {
      if (dataSource == null) {
        throw new SessionException("Configuration does not include an environment with a DataSource, so session cannot be created unless a connection is passed in.");
      }
      Connection connection = dataSource.getConnection();
      connection = wrapConnection(connection);
      Transaction tx = transactionFactory.newTransaction(connection, autoCommit);
      Executor executor = configuration.newExecutor(tx, execType);
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (SQLException e) {
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    }
  }

  public SqlSession openSession(Connection connection) {
    return openSession(configuration.getDefaultExecutorType(), connection);
  }

  public SqlSession openSession(ExecutorType execType, Connection connection) {
    try {
      boolean autoCommit;
      try {
        autoCommit = connection.getAutoCommit();
      } catch (SQLException e) {
        // Failover to true, as most poor drivers
        // or databases won't support transactions
        autoCommit = true;
      }
      connection = wrapConnection(connection);
      Transaction tx = transactionFactory.newTransaction(connection, autoCommit);
      Executor executor = configuration.newExecutor(tx, execType);
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    }
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  private Connection wrapConnection(Connection connection) {
    if (log.isDebugEnabled()) {
      return ConnectionLogger.newInstance(connection);
    } else {
      return connection;
    }
  }

}

