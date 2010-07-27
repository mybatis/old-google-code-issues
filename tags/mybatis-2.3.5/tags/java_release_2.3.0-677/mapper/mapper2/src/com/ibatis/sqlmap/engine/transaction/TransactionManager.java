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
package com.ibatis.sqlmap.engine.transaction;

import com.ibatis.common.util.Throttle;
import com.ibatis.sqlmap.engine.scope.SessionScope;

import javax.sql.DataSource;
import java.sql.SQLException;

public class TransactionManager {

  private TransactionConfig transactionConfig;

  private boolean forceCommit;

  private Throttle txThrottle;

  public TransactionManager(TransactionConfig transactionConfig) {
    this.transactionConfig = transactionConfig;
    this.txThrottle = new Throttle(transactionConfig.getMaximumConcurrentTransactions());
  }


  public void begin(SessionScope session) throws SQLException, TransactionException {
    begin(session, IsolationLevel.UNSET_ISOLATION_LEVEL);
  }

  public void begin(SessionScope session, int transactionIsolation) throws SQLException, TransactionException {
    Transaction trans = session.getTransaction();
    TransactionState state = session.getTransactionState();
    if (state == TransactionState.STATE_STARTED) {
      throw new TransactionException("TransactionManager could not start a new transaction.  " +
          "A transaction is already started.");
    } else if (state == TransactionState.STATE_USER_PROVIDED) {
      throw new TransactionException("TransactionManager could not start a new transaction.  " +
          "A user provided connection is currently being used by this session.  " +
          "The calling .setUserConnection (null) will clear the user provided transaction.");
    }

    txThrottle.increment();

    try {
      trans = transactionConfig.newTransaction(transactionIsolation);
      session.setCommitRequired(false);
    } catch (SQLException e) {
      txThrottle.decrement();
      throw e;
    } catch (TransactionException e) {
      txThrottle.decrement();
      throw e;
    }

    session.setTransaction(trans);
    session.setTransactionState(TransactionState.STATE_STARTED);
  }

  public void commit(SessionScope session) throws SQLException, TransactionException {
    Transaction trans = session.getTransaction();
    TransactionState state = session.getTransactionState();
    if (state == TransactionState.STATE_USER_PROVIDED) {
      throw new TransactionException("TransactionManager could not commit.  " +
          "A user provided connection is currently being used by this session.  " +
          "You must call the commit() method of the Connection directly.  " +
          "The calling .setUserConnection (null) will clear the user provided transaction.");
    } else if (state != TransactionState.STATE_STARTED && state != TransactionState.STATE_COMMITTED ) {
      throw new TransactionException("TransactionManager could not commit.  No transaction is started.");
    }
    if (session.isCommitRequired() || forceCommit) {
      trans.commit();
      session.setCommitRequired(false);
    }
    session.setTransactionState(TransactionState.STATE_COMMITTED);
  }

  public void end(SessionScope session) throws SQLException, TransactionException {
    Transaction trans = session.getTransaction();
    TransactionState state = session.getTransactionState();

    if (state == TransactionState.STATE_USER_PROVIDED) {
      throw new TransactionException("TransactionManager could not end this transaction.  " +
          "A user provided connection is currently being used by this session.  " +
          "You must call the rollback() method of the Connection directly.  " +
          "The calling .setUserConnection (null) will clear the user provided transaction.");
    }

    try {
      if (trans != null) {
        try {
          if (state != TransactionState.STATE_COMMITTED) {
            if (session.isCommitRequired() || forceCommit) {
              trans.rollback();
              session.setCommitRequired(false);
            }
          }
        } finally {
          session.closePreparedStatements();
          trans.close();
        }
      }
    } finally {

      if (state != TransactionState.STATE_ENDED) {
        txThrottle.decrement();
      }

      session.setTransaction(null);
      session.setTransactionState(TransactionState.STATE_ENDED);
    }
  }

  public DataSource getDataSource() {
    return transactionConfig.getDataSource();
  }

  public void setDataSource(DataSource ds) {
    transactionConfig.setDataSource(ds);
  }

  public boolean isForceCommit() {
    return forceCommit;
  }

  public void setForceCommit(boolean forceCommit) {
    this.forceCommit = forceCommit;
  }

}