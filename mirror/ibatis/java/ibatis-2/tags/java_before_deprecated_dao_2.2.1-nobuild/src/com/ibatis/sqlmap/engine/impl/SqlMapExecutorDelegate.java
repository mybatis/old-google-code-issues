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

import com.ibatis.common.beans.Probe;
import com.ibatis.common.beans.ProbeFactory;
import com.ibatis.common.jdbc.exception.NestedSQLException;
import com.ibatis.common.util.PaginatedList;
import com.ibatis.common.util.ThrottledPool;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.cache.CacheKey;
import com.ibatis.sqlmap.engine.cache.CacheModel;
import com.ibatis.sqlmap.engine.exchange.DataExchangeFactory;
import com.ibatis.sqlmap.engine.execution.BatchException;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultObjectFactory;
import com.ibatis.sqlmap.engine.mapping.statement.InsertStatement;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.mapping.statement.PaginatedDataList;
import com.ibatis.sqlmap.engine.mapping.statement.SelectKeyStatement;
import com.ibatis.sqlmap.engine.scope.RequestScope;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.transaction.Transaction;
import com.ibatis.sqlmap.engine.transaction.TransactionException;
import com.ibatis.sqlmap.engine.transaction.TransactionManager;
import com.ibatis.sqlmap.engine.transaction.TransactionState;
import com.ibatis.sqlmap.engine.transaction.user.UserProvidedTransaction;
import com.ibatis.sqlmap.engine.type.TypeHandlerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The workhorse that really runs the SQL
 */
public class SqlMapExecutorDelegate {

  private static final Probe PROBE = ProbeFactory.getProbe();

  /**
   * The default maximum number of requests
   */
  public static final int DEFAULT_MAX_REQUESTS = 512;
  /**
   * The default maximum number of sessions
   */
  public static final int DEFAULT_MAX_SESSIONS = 128;
  /**
   * The default maximum number of transactions
   */
  public static final int DEFAULT_MAX_TRANSACTIONS = 32;

  private boolean lazyLoadingEnabled;
  private boolean cacheModelsEnabled;
  private boolean enhancementEnabled;

  private int maxRequests = DEFAULT_MAX_REQUESTS;
  private int maxSessions = DEFAULT_MAX_SESSIONS;
  private int maxTransactions = DEFAULT_MAX_TRANSACTIONS;

  private TransactionManager txManager;

  private HashMap mappedStatements;
  private HashMap cacheModels;
  private HashMap resultMaps;
  private HashMap parameterMaps;

  private ThrottledPool requestPool;
  private ThrottledPool sessionPool;

  protected SqlExecutor sqlExecutor;
  private TypeHandlerFactory typeHandlerFactory;
  private DataExchangeFactory dataExchangeFactory;
  
  private ResultObjectFactory resultObjectFactory;

  /**
   * Default constructor
   */
  public SqlMapExecutorDelegate() {
    mappedStatements = new HashMap();
    cacheModels = new HashMap();
    resultMaps = new HashMap();
    parameterMaps = new HashMap();

    requestPool = new ThrottledPool(RequestScope.class, DEFAULT_MAX_REQUESTS);
    sessionPool = new ThrottledPool(SessionScope.class, DEFAULT_MAX_SESSIONS);

    sqlExecutor = new SqlExecutor();
    typeHandlerFactory = new TypeHandlerFactory();
    dataExchangeFactory = new DataExchangeFactory(typeHandlerFactory);
  }

  /**
   * Getter for the DataExchangeFactory
   *
   * @return - the DataExchangeFactory
   */
  public DataExchangeFactory getDataExchangeFactory() {
    return dataExchangeFactory;
  }

  /**
   * Getter for the TypeHandlerFactory
   *
   * @return - the TypeHandlerFactory
   */
  public TypeHandlerFactory getTypeHandlerFactory() {
    return typeHandlerFactory;
  }

  /**
   * Getter for the status of lazy loading
   *
   * @return - the status
   */
  public boolean isLazyLoadingEnabled() {
    return lazyLoadingEnabled;
  }

  /**
   * Turn on or off lazy loading
   *
   * @param lazyLoadingEnabled - the new state of caching
   */
  public void setLazyLoadingEnabled(boolean lazyLoadingEnabled) {
    this.lazyLoadingEnabled = lazyLoadingEnabled;
  }

  /**
   * Getter for the status of caching
   *
   * @return - the status
   */
  public boolean isCacheModelsEnabled() {
    return cacheModelsEnabled;
  }

  /**
   * Turn on or off caching
   *
   * @param cacheModelsEnabled - the new state of caching
   */
  public void setCacheModelsEnabled(boolean cacheModelsEnabled) {
    this.cacheModelsEnabled = cacheModelsEnabled;
  }

  /**
   * Getter for the status of CGLib enhancements
   *
   * @return - the status
   */
  public boolean isEnhancementEnabled() {
    return enhancementEnabled;
  }

  /**
   * Turn on or off CGLib enhancements
   *
   * @param enhancementEnabled - the new state
   */
  public void setEnhancementEnabled(boolean enhancementEnabled) {
    this.enhancementEnabled = enhancementEnabled;
  }

  /**
   * Getter for the maximum number of requests
   *
   * @return - the maximum number of requests
   */
  public int getMaxRequests() {
    return maxRequests;
  }

  /**
   * Setter for the maximum number of requests
   *
   * @param maxRequests - the maximum number of requests
   */
  public void setMaxRequests(int maxRequests) {
    this.maxRequests = maxRequests;
    requestPool = new ThrottledPool(RequestScope.class, maxRequests);
  }

  /**
   * Getter for the maximum number of sessions
   *
   * @return - the maximum number of sessions
   */
  public int getMaxSessions() {
    return maxSessions;
  }

  /**
   * Setter for the maximum number of sessions
   *
   * @param maxSessions - the maximum number of sessions
   */
  public void setMaxSessions(int maxSessions) {
    this.maxSessions = maxSessions;
    this.sessionPool = new ThrottledPool(SessionScope.class, maxSessions);
  }

  /**
   * Getter for the the maximum number of transactions
   *
   * @return - the maximum number of transactions
   */
  public int getMaxTransactions() {
    return maxTransactions;
  }

  /**
   * Setter for the maximum number of transactions
   *
   * @param maxTransactions - the maximum number of transactions
   */
  public void setMaxTransactions(int maxTransactions) {
    this.maxTransactions = maxTransactions;
  }

  /**
   * Getter for the transaction manager
   *
   * @return - the transaction manager
   */
  public TransactionManager getTxManager() {
    return txManager;
  }

  /**
   * Setter for the transaction manager
   *
   * @param txManager - the transaction manager
   */
  public void setTxManager(TransactionManager txManager) {
    this.txManager = txManager;
  }

  /**
   * Add a mapped statement
   *
   * @param ms - the mapped statement to add
   */
  public void addMappedStatement(MappedStatement ms) {
    if (mappedStatements.containsKey(ms.getId())) {
      throw new SqlMapException("There is already a statement named " + ms.getId() + " in this SqlMap.");
    }
    ms.setBaseCacheKey(hashCode());
    mappedStatements.put(ms.getId(), ms);
  }

  /**
   * Get an iterator of the mapped statements
   *
   * @return - the iterator
   */
  public Iterator getMappedStatementNames() {
    return mappedStatements.keySet().iterator();
  }

  /**
   * Get a mappedstatement by its ID
   *
   * @param id - the statement ID
   * @return - the mapped statement
   */
  public MappedStatement getMappedStatement(String id) {
    MappedStatement ms = (MappedStatement) mappedStatements.get(id);
    if (ms == null) {
      throw new SqlMapException("There is no statement named " + id + " in this SqlMap.");
    }
    return ms;
  }

  /**
   * Add a cache model
   *
   * @param model - the model to add
   */
  public void addCacheModel(CacheModel model) {
    cacheModels.put(model.getId(), model);
  }

  /**
   * Get an iterator of the cache models
   *
   * @return - the cache models
   */
  public Iterator getCacheModelNames() {
    return cacheModels.keySet().iterator();
  }

  /**
   * Get a cache model by ID
   *
   * @param id - the ID
   * @return - the cache model
   */
  public CacheModel getCacheModel(String id) {
    CacheModel model = (CacheModel) cacheModels.get(id);
    if (model == null) {
      throw new SqlMapException("There is no cache model named " + id + " in this SqlMap.");
    }
    return model;
  }

  /**
   * Add a result map
   *
   * @param map - the result map to add
   */
  public void addResultMap(ResultMap map) {
    resultMaps.put(map.getId(), map);
  }

  /**
   * Get an iterator of the result maps
   *
   * @return - the result maps
   */
  public Iterator getResultMapNames() {
    return resultMaps.keySet().iterator();
  }

  /**
   * Get a result map by ID
   *
   * @param id - the ID
   * @return - the result map
   */
  public ResultMap getResultMap(String id) {
    ResultMap map = (ResultMap) resultMaps.get(id);
    if (map == null) {
      throw new SqlMapException("There is no result map named " + id + " in this SqlMap.");
    }
    return map;
  }

  /**
   * Add a parameter map
   *
   * @param map - the map to add
   */
  public void addParameterMap(ParameterMap map) {
    parameterMaps.put(map.getId(), map);
  }

  /**
   * Get an iterator of all of the parameter maps
   *
   * @return - the parameter maps
   */
  public Iterator getParameterMapNames() {
    return parameterMaps.keySet().iterator();
  }

  /**
   * Get a parameter map by ID
   *
   * @param id - the ID
   * @return - the parameter map
   */
  public ParameterMap getParameterMap(String id) {
    ParameterMap map = (ParameterMap) parameterMaps.get(id);
    if (map == null) {
      throw new SqlMapException("There is no parameter map named " + id + " in this SqlMap.");
    }
    return map;
  }

  /**
   * Flush all of the data caches
   */
  public void flushDataCache() {
    Iterator models = cacheModels.values().iterator();
    while (models.hasNext()) {
      ((CacheModel) models.next()).flush();
    }
  }

  /**
   * Flush a single cache by ID
   *
   * @param id - the ID
   */
  public void flushDataCache(String id) {
    CacheModel model = getCacheModel(id);
    if (model != null) {
      model.flush();
    }
  }

  //-- Basic Methods
  /**
   * Call an insert statement by ID
   *
   * @param session - the session
   * @param id      - the statement ID
   * @param param   - the parameter object
   * @return - the generated key (or null)
   * @throws SQLException - if the insert fails
   */
  public Object insert(SessionScope session, String id, Object param) throws SQLException {
    Object generatedKey = null;

    MappedStatement ms = getMappedStatement(id);
    Transaction trans = getTransaction(session);
    boolean autoStart = trans == null;

    try {
      trans = autoStartTransaction(session, autoStart, trans);

      SelectKeyStatement selectKeyStatement = null;
      if (ms instanceof InsertStatement) {
        selectKeyStatement = ((InsertStatement) ms).getSelectKeyStatement();
      }

      if (selectKeyStatement != null && !selectKeyStatement.isAfter()) {
        generatedKey = executeSelectKey(session, trans, ms, param);
      }

      RequestScope request = popRequest(session, ms);
      try {
        ms.executeUpdate(request, trans, param);
      } finally {
        pushRequest(request);
      }

      if (selectKeyStatement != null && selectKeyStatement.isAfter()) {
        generatedKey = executeSelectKey(session, trans, ms, param);
      }

      autoCommitTransaction(session, autoStart);
    } finally {
      autoEndTransaction(session, autoStart);
    }

    return generatedKey;
  }

  private Object executeSelectKey(SessionScope session, Transaction trans, MappedStatement ms, Object param) throws SQLException {
    Object generatedKey = null;
    RequestScope request;
    InsertStatement insert = (InsertStatement) ms;
    SelectKeyStatement selectKeyStatement = insert.getSelectKeyStatement();
    if (selectKeyStatement != null) {
      request = popRequest(session, selectKeyStatement);
      try {
        generatedKey = selectKeyStatement.executeQueryForObject(request, trans, param, null);
        String keyProp = selectKeyStatement.getKeyProperty();
        if (keyProp != null) {
          PROBE.setObject(param, keyProp, generatedKey);
        }
      } finally {
        pushRequest(request);
      }
    }
    return generatedKey;
  }

  /**
   * Execute an update statement
   *
   * @param session - the session scope
   * @param id      - the statement ID
   * @param param   - the parameter object
   * @return - the number of rows updated
   * @throws SQLException - if the update fails
   */
  public int update(SessionScope session, String id, Object param) throws SQLException {
    int rows = 0;

    MappedStatement ms = getMappedStatement(id);
    Transaction trans = getTransaction(session);
    boolean autoStart = trans == null;

    try {
      trans = autoStartTransaction(session, autoStart, trans);

      RequestScope request = popRequest(session, ms);
      try {
        rows = ms.executeUpdate(request, trans, param);
      } finally {
        pushRequest(request);
      }

      autoCommitTransaction(session, autoStart);
    } finally {
      autoEndTransaction(session, autoStart);
    }

    return rows;
  }

  /**
   * Execute a delete statement
   *
   * @param session - the session scope
   * @param id      - the statement ID
   * @param param   - the parameter object
   * @return - the number of rows deleted
   * @throws SQLException - if the delete fails
   */
  public int delete(SessionScope session, String id, Object param) throws SQLException {
    return update(session, id, param);
  }

  /**
   * Execute a select for a single object
   *
   * @param session     - the session scope
   * @param id          - the statement ID
   * @param paramObject - the parameter object
   * @return - the result of the query
   * @throws SQLException - if the query fails
   */
  public Object queryForObject(SessionScope session, String id, Object paramObject) throws SQLException {
    return queryForObject(session, id, paramObject, null);
  }

  /**
   * Execute a select for a single object
   *
   * @param session      - the session scope
   * @param id           - the statement ID
   * @param paramObject  - the parameter object
   * @param resultObject - the result object (if not supplied or null, a new object will be created)
   * @return - the result of the query
   * @throws SQLException - if the query fails
   */
  public Object queryForObject(SessionScope session, String id, Object paramObject, Object resultObject) throws SQLException {
    Object object = null;

    MappedStatement ms = getMappedStatement(id);
    Transaction trans = getTransaction(session);
    boolean autoStart = trans == null;

    try {
      trans = autoStartTransaction(session, autoStart, trans);

      RequestScope request = popRequest(session, ms);
      try {
        object = ms.executeQueryForObject(request, trans, paramObject, resultObject);
      } finally {
        pushRequest(request);
      }

      autoCommitTransaction(session, autoStart);
    } finally {
      autoEndTransaction(session, autoStart);
    }

    return object;
  }

  /**
   * Execute a query for a list
   *
   * @param session     - the session scope
   * @param id          - the statement ID
   * @param paramObject - the parameter object
   * @return - the data list
   * @throws SQLException - if the query fails
   */
  public List queryForList(SessionScope session, String id, Object paramObject) throws SQLException {
    return queryForList(session, id, paramObject, SqlExecutor.NO_SKIPPED_RESULTS, SqlExecutor.NO_MAXIMUM_RESULTS);
  }

  /**
   * Execute a query for a list
   *
   * @param session     - the session scope
   * @param id          - the statement ID
   * @param paramObject - the parameter object
   * @param skip        - the number of rows to skip
   * @param max         - the maximum number of rows to return
   * @return - the data list
   * @throws SQLException - if the query fails
   */
  public List queryForList(SessionScope session, String id, Object paramObject, int skip, int max) throws SQLException {
    List list = null;

    MappedStatement ms = getMappedStatement(id);
    Transaction trans = getTransaction(session);
    boolean autoStart = trans == null;

    try {
      trans = autoStartTransaction(session, autoStart, trans);

      RequestScope request = popRequest(session, ms);
      try {
        list = ms.executeQueryForList(request, trans, paramObject, skip, max);
      } finally {
        pushRequest(request);
      }

      autoCommitTransaction(session, autoStart);
    } finally {
      autoEndTransaction(session, autoStart);
    }

    return list;
  }

  /**
   * Execute a query with a row handler.
   * The row handler is called once per row in the query results.
   *
   * @param session     - the session scope
   * @param id          - the statement ID
   * @param paramObject - the parameter object
   * @param rowHandler  - the row handler
   * @throws SQLException - if the query fails
   */
  public void queryWithRowHandler(SessionScope session, String id, Object paramObject, RowHandler rowHandler) throws SQLException {

    MappedStatement ms = getMappedStatement(id);
    Transaction trans = getTransaction(session);
    boolean autoStart = trans == null;

    try {
      trans = autoStartTransaction(session, autoStart, trans);

      RequestScope request = popRequest(session, ms);
      try {
        ms.executeQueryWithRowHandler(request, trans, paramObject, rowHandler);
      } finally {
        pushRequest(request);
      }

      autoCommitTransaction(session, autoStart);
    } finally {
      autoEndTransaction(session, autoStart);
    }

  }

  /**
   * Execute a query and return a paginated list
   *
   * @param session     - the session scope
   * @param id          - the statement ID
   * @param paramObject - the parameter object
   * @param pageSize    - the page size
   * @return - the data list
   * @throws SQLException - if the query fails
   */
  public PaginatedList queryForPaginatedList(SessionScope session, String id, Object paramObject, int pageSize) throws SQLException {
    return new PaginatedDataList(session.getSqlMapExecutor(), id, paramObject, pageSize);
  }

  /**
   * Execute a query for a map.
   * The map has the table key as the key, and the results as the map data
   *
   * @param session     - the session scope
   * @param id          - the statement ID
   * @param paramObject - the parameter object
   * @param keyProp     - the key property (from the results for the map)
   * @return - the Map
   * @throws SQLException - if the query fails
   */
  public Map queryForMap(SessionScope session, String id, Object paramObject, String keyProp) throws SQLException {
    return queryForMap(session, id, paramObject, keyProp, null);
  }

  /**
   * Execute a query for a map.
   * The map has the table key as the key, and a property from the results as the map data
   *
   * @param session     - the session scope
   * @param id          - the statement ID
   * @param paramObject - the parameter object
   * @param keyProp     - the property for the map key
   * @param valueProp   - the property for the map data
   * @return - the Map
   * @throws SQLException - if the query fails
   */
  public Map queryForMap(SessionScope session, String id, Object paramObject, String keyProp, String valueProp) throws SQLException {
    Map map = new HashMap();

    List list = queryForList(session, id, paramObject);

    for (int i = 0, n = list.size(); i < n; i++) {
      Object object = list.get(i);
      Object key = PROBE.getObject(object, keyProp);
      Object value = null;
      if (valueProp == null) {
        value = object;
      } else {
        value = PROBE.getObject(object, valueProp);
      }
      map.put(key, value);
    }

    return map;
  }

  // -- Transaction Control Methods
  /**
   * Start a transaction on the session
   *
   * @param session - the session
   * @throws SQLException - if the transaction could not be started
   */
  public void startTransaction(SessionScope session) throws SQLException {
    try {
      txManager.begin(session);
    } catch (TransactionException e) {
      throw new NestedSQLException("Could not start transaction.  Cause: " + e, e);
    }
  }

  /**
   * Start a transaction on the session with the specified isolation level.
   *
   * @param session - the session
   * @throws SQLException - if the transaction could not be started
   */
  public void startTransaction(SessionScope session, int transactionIsolation) throws SQLException {
    try {
      txManager.begin(session, transactionIsolation);
    } catch (TransactionException e) {
      throw new NestedSQLException("Could not start transaction.  Cause: " + e, e);
    }
  }

  /**
   * Commit the transaction on a session
   *
   * @param session - the session
   * @throws SQLException - if the transaction could not be committed
   */
  public void commitTransaction(SessionScope session) throws SQLException {
    try {
      // Auto batch execution
      if (session.isInBatch()) {
        executeBatch(session);
      }
      sqlExecutor.cleanup(session);
      txManager.commit(session);
    } catch (TransactionException e) {
      throw new NestedSQLException("Could not commit transaction.  Cause: " + e, e);
    }
  }

  /**
   * End the transaction on a session
   *
   * @param session - the session
   * @throws SQLException - if the transaction could not be ended
   */
  public void endTransaction(SessionScope session) throws SQLException {
    try {
      try {
        sqlExecutor.cleanup(session);
      } finally {
        txManager.end(session);
      }
    } catch (TransactionException e) {
      throw new NestedSQLException("Error while ending transaction.  Cause: " + e, e);
    }
  }

  /**
   * Start a batch for a session
   *
   * @param session - the session
   */
  public void startBatch(SessionScope session) {
    session.setInBatch(true);
  }

  /**
   * Execute a batch for a session
   *
   * @param session - the session
   * @return - the number of rows impacted by the batch
   * @throws SQLException - if the batch fails
   */
  public int executeBatch(SessionScope session) throws SQLException {
    session.setInBatch(false);
    return sqlExecutor.executeBatch(session);
  }

  /**
   * Execute a batch for a session
   *
   * @param session - the session
   * @return - a List of BatchResult objects (may be null if no batch
   *  has been initiated).  There will be one BatchResult object in the
   *  list for each sub-batch executed
   * @throws SQLException if a database access error occurs, or the drive
   *   does not support batch statements
   * @throws BatchException if the driver throws BatchUpdateException
   */
  public List executeBatchDetailed(SessionScope session) throws SQLException, BatchException {
    session.setInBatch(false);
    return sqlExecutor.executeBatchDetailed(session);
  }
  
  /**
   * Use a user-provided transaction for a session
   *
   * @param session        - the session scope
   * @param userConnection - the user supplied connection
   */
  public void setUserProvidedTransaction(SessionScope session, Connection userConnection) {
    if (session.getTransactionState() == TransactionState.STATE_USER_PROVIDED) {
      session.recallTransactionState();
    }
    if (userConnection != null) {
      Connection conn = userConnection;
      session.saveTransactionState();
      session.setTransaction(new UserProvidedTransaction(conn));
      session.setTransactionState(TransactionState.STATE_USER_PROVIDED);
    } else {
      session.setTransaction(null);
      session.closePreparedStatements();
      session.reset(); // used to be pushSession, which is probably incorrect.
    }
  }
  /**
   * Get the DataSource for the session
   *
   * @return - the DataSource
   */
  public DataSource getDataSource() {
    DataSource ds = null;
    if (txManager != null) {
      ds = txManager.getDataSource();
    }
    return ds;
  }

  /**
   * Getter for the SqlExecutor
   *
   * @return the SqlExecutor
   */
  public SqlExecutor getSqlExecutor() {
    return sqlExecutor;
  }

  /**
   * Get a transaction for the session
   *
   * @param session - the session
   * @return - the transaction
   */
  public Transaction getTransaction(SessionScope session) {
    return session.getTransaction();
  }

  // -- Protected Methods

  protected void autoEndTransaction(SessionScope session, boolean autoStart) throws SQLException {
    if (autoStart) {
      session.getSqlMapTxMgr().endTransaction();
    }
  }

  protected void autoCommitTransaction(SessionScope session, boolean autoStart) throws SQLException {
    if (autoStart) {
      session.getSqlMapTxMgr().commitTransaction();
    }
  }

  protected Transaction autoStartTransaction(SessionScope session, boolean autoStart, Transaction trans) throws SQLException {
    Transaction transaction = trans;
    if (autoStart) {
      session.getSqlMapTxMgr().startTransaction();
      transaction = getTransaction(session);
    }
    return transaction;
  }

  public boolean equals(Object obj) {
    return this == obj;
  }

  public int hashCode() {
    CacheKey key = new CacheKey();
    if (txManager != null) {
      key.update(txManager);
      if (txManager.getDataSource() != null) {
        key.update(txManager.getDataSource());
      }
    }
    key.update(System.identityHashCode(this));
    return key.hashCode();
  }

  protected RequestScope popRequest(SessionScope session, MappedStatement mappedStatement) {
    RequestScope request = (RequestScope) requestPool.pop();
    session.incrementRequestStackDepth();
    request.setSession(session);
    mappedStatement.initRequest(request);
    return request;
  }

  protected void pushRequest(RequestScope request) {
    request.getSession().decrementRequestStackDepth();
    request.reset();
    requestPool.push(request);
  }

  protected SessionScope popSession() {
    return (SessionScope) sessionPool.pop();
  }

  protected void pushSession(SessionScope session) {
    session.reset();
    sessionPool.push(session);
  }

  public ResultObjectFactory getResultObjectFactory() {
    return resultObjectFactory;
  }

  public void setResultObjectFactory(ResultObjectFactory resultObjectFactory) {
    this.resultObjectFactory = resultObjectFactory;
  }

}

