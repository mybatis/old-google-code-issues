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
package com.ibatis.sqlmap.engine.transaction.jta;

import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.engine.transaction.BaseTransactionConfig;
import com.ibatis.sqlmap.engine.transaction.Transaction;
import com.ibatis.sqlmap.engine.transaction.TransactionException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import java.sql.SQLException;
import java.util.Properties;

public class JtaTransactionConfig extends BaseTransactionConfig {

  private DataSource dataSource;
  private UserTransaction userTransaction;

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource ds) {
    this.dataSource = ds;
  }

  public void initialize(Properties props) throws SQLException, TransactionException {
    String utxName = null;
    try {
      utxName = (String) props.get("UserTransaction");
      InitialContext initCtx = new InitialContext();
      userTransaction = (UserTransaction) initCtx.lookup(utxName);
    } catch (NamingException e) {
      throw new SqlMapException("Error initializing JtaTransactionConfig while looking up UserTransaction (" + utxName + ").  Cause: " + e);
    }
  }

  public Transaction newTransaction(int transactionIsolation) throws SQLException, TransactionException {
    return new JtaTransaction(userTransaction, dataSource, transactionIsolation);
  }

}

