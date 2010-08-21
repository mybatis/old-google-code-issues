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
package com.ibatis.db.dao.jdbc;

import com.ibatis.common.exception.NestedRuntimeException;
import com.ibatis.common.resources.Resources;
import com.ibatis.db.dao.DaoException;
import com.ibatis.db.dao.DaoTransaction;
import com.ibatis.db.dao.DaoTransactionPool;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import java.io.Reader;
import java.sql.SQLException;
import java.util.Map;

public class SqlMap2DaoTransactionPool implements DaoTransactionPool {

  private SqlMapClient sqlMap;

  public void configure(Map properties)
      throws DaoException {

    try {
      String xmlConfig = (String) properties.get("sql-map-config-file");

      Reader reader = Resources.getResourceAsReader(xmlConfig);
      sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
    } catch (Exception e) {
      throw new NestedRuntimeException("Error configuring SqlMapClientDaoTransactionPool.  Cause: " + e, e);
    }

  }

  public DaoTransaction getTransaction()
      throws DaoException {
    try {
      sqlMap.startTransaction();
      return new SqlMap2DaoTransaction(sqlMap);
    } catch (SQLException e) {
      throw new DaoException("Error getting transaction. Cause: " + e, e);
    }
  }

  public void releaseTransaction(DaoTransaction trans)
      throws DaoException {
    // No implementation required.
  }

  public SqlMapClient getSqlMapClient() {
    return sqlMap;
  }


}
