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
package com.ibatis.dao.engine.transaction.jta;

import com.ibatis.common.jdbc.logging.ConnectionLogProxy;
import com.ibatis.common.logging.Log;
import com.ibatis.common.logging.LogFactory;
import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.engine.transaction.ConnectionDaoTransaction;

import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import java.sql.Connection;
import java.sql.SQLException;

public class JtaDaoTransaction implements ConnectionDaoTransaction {

  private static final Log connectionLog = LogFactory.getLog(Connection.class);

  private UserTransaction userTransaction;
  private DataSource dataSource;
  private Connection connection;

  private boolean commmitted = false;
  private boolean newTransaction = false;

  public JtaDaoTransaction(UserTransaction utx, DataSource ds) {
    // Check parameters
    userTransaction = utx;
    dataSource = ds;
    if (userTransaction == null) {
      throw new DaoException("JtaTransaction initialization failed.  UserTransaction was null.");
    }
    if (dataSource == null) {
      throw new DaoException("JtaTransaction initialization failed.  DataSource was null.");
    }

    // Start JTA Transaction
    try {
      newTransaction = userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION;
      if (newTransaction) {
        userTransaction.begin();
      }
    } catch (Exception e) {
      throw new DaoException("JtaTransaction could not start transaction.  Cause: ", e);
    }

    try {
      // Open JDBC Connection
      connection = dataSource.getConnection();
      if (connection == null) {
        throw new DaoException("Could not start transaction.  Cause: The DataSource returned a null connection.");
      }
      if (connection.getAutoCommit()) {
        connection.setAutoCommit(false);
      }
      if (connectionLog.isDebugEnabled()) {
        connection = ConnectionLogProxy.newInstance(connection);
      }
    } catch (SQLException e) {
      throw new DaoException("Error opening JDBC connection.  Cause: " + e, e);
    }
  }

  public void commit() {
    if (commmitted) {
      throw new DaoException("JtaTransaction could not commit because this transaction has already been committed.");
    }
    try {
      try {
        if (newTransaction) {
          userTransaction.commit();
        }
      } finally {
        close();
      }
    } catch (Exception e) {
      throw new DaoException("JtaTransaction could not commit.  Cause: ", e);
    }
    commmitted = true;
  }

  public void rollback() {
    if (!commmitted) {
      try {
        try {
          if (userTransaction != null) {
            if (newTransaction) {
              userTransaction.rollback();
            } else {
              userTransaction.setRollbackOnly();
            }
          }
        } finally {
          close();
        }
      } catch (Exception e) {
        throw new DaoException("JTA transaction could not rollback.  Cause: ", e);
      }
    }

  }


  public void close() throws SQLException {
    if (connection != null) {
      connection.close();
      connection = null;
    }
  }

  public Connection getConnection() {
    return connection;
  }


}
