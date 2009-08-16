/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ibatis.sqlmap.engine.impl;

import com.ibatis.common.jdbc.exception.NestedSQLException;
import com.ibatis.common.util.PaginatedList;
import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.transaction.Transaction;
import com.ibatis.sqlmap.engine.transaction.TransactionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Implementation of SqlMapSession
 */
public class SqlMapSessionImpl implements SqlMapSession {

  private static final Log log = LogFactory.getLog(SqlMapSessionImpl.class);

  protected SqlMapExecutorDelegate delegate;
  protected SessionScope session;
  protected boolean closed;

  /**
   * Constructor
   *
   * @param client - the client that will use the session
   */
  public SqlMapSessionImpl(ExtendedSqlMapClient client) {
    this.delegate = client.getDelegate();
    this.session = this.delegate.popSession();
    this.session.setSqlMapClient(client);
    this.session.setSqlMapExecutor(client);
    this.session.setSqlMapTxMgr(client);
    this.closed = false;
  }

  /**
   * Start the session
   */
  public void open() {
    session.setSqlMapTxMgr(this);
  }

  /**
   * Getter to tell if the session is still open
   *
   * @return - the status of the session
   */
  public boolean isClosed() {
    return closed;
  }

  public void close() {
    if (delegate != null && session != null) delegate.pushSession(session);
    if (session != null) session = null;
    if (delegate != null) delegate = null;
    if (!closed) closed = true;
  }

  public Object insert(String id, Object param) throws SQLException {
    return delegate.insert(session, id, param);
  }

  public int update(String id, Object param) throws SQLException {
    return delegate.update(session, id, param);
  }

  public int delete(String id, Object param) throws SQLException {
    return delegate.delete(session, id, param);
  }

  public Object queryForObject(String id, Object paramObject) throws SQLException {
    return delegate.queryForObject(session, id, paramObject);
  }

  public Object queryForObject(String id, Object paramObject, Object resultObject) throws SQLException {
    return delegate.queryForObject(session, id, paramObject, resultObject);
  }

  public List queryForList(String id, Object paramObject) throws SQLException {
    return delegate.queryForList(session, id, paramObject);
  }

  public List queryForList(String id, Object paramObject, int skip, int max) throws SQLException {
    return delegate.queryForList(session, id, paramObject, skip, max);
  }

  public PaginatedList queryForPaginatedList(String id, Object paramObject, int pageSize) throws SQLException {
    return delegate.queryForPaginatedList(session, id, paramObject, pageSize);
  }

  public Map queryForMap(String id, Object paramObject, String keyProp) throws SQLException {
    return delegate.queryForMap(session, id, paramObject, keyProp);
  }

  public Map queryForMap(String id, Object paramObject, String keyProp, String valueProp) throws SQLException {
    return delegate.queryForMap(session, id, paramObject, keyProp, valueProp);
  }

  public void queryWithRowHandler(String id, Object paramObject, RowHandler rowHandler) throws SQLException {
    delegate.queryWithRowHandler(session, id, paramObject, rowHandler);
  }

  public void startTransaction() throws SQLException {
    delegate.startTransaction(session);
  }

  public void startTransaction(int transactionIsolation) throws SQLException {
    delegate.startTransaction(session, transactionIsolation);
  }

  public void commitTransaction() throws SQLException {
    delegate.commitTransaction(session);
  }

  public void endTransaction() throws SQLException {
    delegate.endTransaction(session);
  }

  public void startBatch() throws SQLException {
    delegate.startBatch(session);
  }

  public int executeBatch() throws SQLException {
    return delegate.executeBatch(session);
  }

  public void setUserConnection(Connection connection) throws SQLException {
    delegate.setUserProvidedTransaction(session, connection);
  }

  /**
   * TODO Deprecated
   *
   * @return
   * @throws SQLException
   * @deprecated
   */
  public Connection getUserConnection() throws SQLException {
    return getCurrentConnection();
  }

  public Connection getCurrentConnection() throws SQLException {
    try {
      Connection conn = null;
      Transaction trans = delegate.getTransaction(session);
      if (trans != null) {
        conn = trans.getConnection();
      }
      return conn;
    } catch (TransactionException e) {
      throw new NestedSQLException("Error getting Connection from Transaction.  Cause: " + e, e);
    }
  }

  public DataSource getDataSource() {
    return delegate.getDataSource();
  }

  /**
   * Gets a mapped statement by ID
   *
   * @param id - the ID
   * @return - the mapped statement
   */
  public MappedStatement getMappedStatement(String id) {
    return delegate.getMappedStatement(id);
  }

  /**
   * Get the status of lazy loading
   *
   * @return - the status
   */
  public boolean isLazyLoadingEnabled() {
    return delegate.isLazyLoadingEnabled();
  }

  /**
   * Get the status of CGLib enhancements
   *
   * @return - the status
   */
  public boolean isEnhancementEnabled() {
    return delegate.isEnhancementEnabled();
  }

  /**
   * Get the SQL executor
   *
   * @return -  the executor
   */
  public SqlExecutor getSqlExecutor() {
    return delegate.getSqlExecutor();
  }

  /**
   * Get the delegate
   *
   * @return - the delegate
   */
  public SqlMapExecutorDelegate getDelegate() {
    return delegate;
  }

}
