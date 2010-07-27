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
package com.ibatis.sqlmap.engine.scope;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.client.SqlMapTransactionManager;
import com.ibatis.sqlmap.engine.transaction.Transaction;
import com.ibatis.sqlmap.engine.transaction.TransactionState;

/**
 * A Session based implementation of the Scope interface
 */
public class SessionScope extends BaseScope {

  private static long nextId;

  private long id;

  // Used by Any
  private SqlMapClient sqlMapClient;
  private SqlMapExecutor sqlMapExecutor;
  private SqlMapTransactionManager sqlMapTxMgr;
  private int requestStackDepth;

  // Used by TransactionManager
  private Transaction transaction;
  private TransactionState transactionState;

  // Used by SqlMapExecutorDelegate.setUserProvidedTransaction()
  private TransactionState savedTransactionState;

  // Used by StandardSqlMapClient and GeneralStatement
  private boolean inBatch;

  // Used by SqlExecutor
  private Object batch;

  private boolean commitRequired;

  /**
   * Default constructor
   */
  public SessionScope() {
    reset();
  }

  /**
   * Get the SqlMapClient for the session
   * 
   * @return - the SqlMapClient
   */
  public SqlMapClient getSqlMapClient() {
    return sqlMapClient;
  }

  /**
   * Set the SqlMapClient for the session
   * 
   * @param sqlMapClient - the SqlMapClient
   */
  public void setSqlMapClient(SqlMapClient sqlMapClient) {
    this.sqlMapClient = sqlMapClient;
  }

  /**
   * Get the SQL executor for the session
   * 
   * @return - the SQL executor
   */
  public SqlMapExecutor getSqlMapExecutor() {
    return sqlMapExecutor;
  }

  /**
   * Get the SQL executor for the session
   * 
   * @param sqlMapExecutor - the SQL executor
   */
  public void setSqlMapExecutor(SqlMapExecutor sqlMapExecutor) {
    this.sqlMapExecutor = sqlMapExecutor;
  }

  /**
   * Get the transaction manager
   * 
   * @return - the transaction manager
   */
  public SqlMapTransactionManager getSqlMapTxMgr() {
    return sqlMapTxMgr;
  }

  /**
   * Set the transaction manager
   * 
   * @param sqlMapTxMgr - the transaction manager
   */
  public void setSqlMapTxMgr(SqlMapTransactionManager sqlMapTxMgr) {
    this.sqlMapTxMgr = sqlMapTxMgr;
  }

  /**
   * Tells us if we are in batch mode or not
   * 
   * @return - true if we are working with a batch
   */
  public boolean isInBatch() {
    return inBatch;
  }

  /**
   * Turn batch mode on or off
   * 
   * @param inBatch - the switch
   */
  public void setInBatch(boolean inBatch) {
    this.inBatch = inBatch;
  }

  /**
   * Getter for the session transaction
   * 
   * @return - the transaction
   */
  public Transaction getTransaction() {
    return transaction;
  }

  /**
   * Setter for the session transaction
   * 
   * @param transaction - the transaction
   */
  public void setTransaction(Transaction transaction) {
    this.transaction = transaction;
  }

  /**
   * Getter for the transaction state of the session
   * 
   * @return - the state
   */
  public TransactionState getTransactionState() {
    return transactionState;
  }

  /**
   * Setter for the transaction state of the session
   * 
   * @param transactionState - the new transaction state
   */
  public void setTransactionState(TransactionState transactionState) {
    this.transactionState = transactionState;
  }

  /**
   * Getter for the batch of the session
   * 
   * @return - the batch
   */
  public Object getBatch() {
    return batch;
  }

  /**
   * Stter for the batch of the session
   * 
   * @param batch the new batch
   */
  public void setBatch(Object batch) {
    this.batch = batch;
  }

  /**
   * Get the request stack depth
   * 
   * @return - the stack depth
   */
  public int getRequestStackDepth() {
    return requestStackDepth;
  }

  /**
   * Increment the stack depth by one.
   */
  public void incrementRequestStackDepth() {
    requestStackDepth++;
  }

  /**
   * Decrement the stack depth by one.
   */
  public void decrementRequestStackDepth() {
    requestStackDepth--;
  }

  /**
   * Getter to tell if a commit is required for the session
   * 
   * @return - true if a commit is required
   */
  public boolean isCommitRequired() {
    return commitRequired;
  }

  /**
   * Setter to tell the session that a commit is required for the session
   * 
   * @param commitRequired - the flag
   */
  public void setCommitRequired(boolean commitRequired) {
    this.commitRequired = commitRequired;
  }

  public void reset() {
    super.reset();
    this.batch = null;
    sqlMapExecutor = null;
    sqlMapTxMgr = null;
    inBatch = false;
    transaction = null;
    transactionState = null;
    batch = null;
    requestStackDepth = 0;
    id = getNextId();
  }

  public boolean equals(Object parameterObject) {
    if (this == parameterObject) return true;
    if (!(parameterObject instanceof SessionScope)) return false;

    final SessionScope sessionScope = (SessionScope) parameterObject;

    if (id != sessionScope.id) return false;

    return true;
  }

  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  /**
   * Method to get a unique ID
   * 
   * @return - the new ID
   */
  public synchronized static long getNextId() {
    return nextId++;
  }

  /**
   * Saves the current transaction state
   */
  public void saveTransactionState() {
    savedTransactionState = transactionState;
  }

  /**
   * Restores the previously saved transaction state
   */
  public void recallTransactionState() {
    transactionState = savedTransactionState;
  }
}
