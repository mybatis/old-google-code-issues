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
package com.ibatis.db.sqlmap;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.sqlmap.client.SqlMapClient;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MappedStatement {

  private SqlMapClient sqlMapClient;
  private String statementName;

  public MappedStatement(SqlMapClient sqlMapClient, String statementName) {
    this.sqlMapClient = sqlMapClient;
    this.statementName = statementName;
  }

  public int executeUpdate(Connection conn, Object parameterObject)
      throws SQLException {
    int n;
    try {
      sqlMapClient.setUserConnection(conn);
      n = sqlMapClient.update(statementName, parameterObject);
    } finally {
      sqlMapClient.setUserConnection(null);
    }
    return n;
  }

  public void executeQueryWithRowHandler(Connection conn, Object parameterObject, RowHandler rowHandler)
      throws SQLException {
    try {
      sqlMapClient.setUserConnection(conn);
      sqlMapClient.queryWithRowHandler(statementName, parameterObject, new RowHandlerAdapter(rowHandler));
    } finally {
      sqlMapClient.setUserConnection(null);
    }
  }

  public Map executeQueryForMap(Connection conn, Object parameterObject, String keyProperty)
      throws SQLException {
    Map map;
    try {
      sqlMapClient.setUserConnection(conn);
      map = sqlMapClient.queryForMap(statementName, parameterObject, keyProperty);
    } finally {
      sqlMapClient.setUserConnection(null);
    }
    return map;
  }

  public Map executeQueryForMap(Connection conn, Object parameterObject, String keyProperty, String valueProperty)
      throws SQLException {
    Map map;
    try {
      sqlMapClient.setUserConnection(conn);
      map = sqlMapClient.queryForMap(statementName, parameterObject, keyProperty, valueProperty);
    } finally {
      sqlMapClient.setUserConnection(null);
    }
    return map;
  }

  public PaginatedList executeQueryForPaginatedList(Object parameterObject, int pageSize)
      throws SQLException {
    PaginatedList list = sqlMapClient.queryForPaginatedList(statementName, parameterObject, pageSize);
    return list;
  }

  public List executeQueryForList(Connection conn, Object parameterObject)
      throws SQLException {
    List list;
    try {
      sqlMapClient.setUserConnection(conn);
      list = sqlMapClient.queryForList(statementName, parameterObject);
    } finally {
      sqlMapClient.setUserConnection(null);
    }
    return list;
  }

  public List executeQueryForList(Connection conn, Object parameterObject, int skipResults, int maxResults)
      throws SQLException {
    List list;
    try {
      sqlMapClient.setUserConnection(conn);
      list = sqlMapClient.queryForList(statementName, parameterObject, skipResults, maxResults);
    } finally {
      sqlMapClient.setUserConnection(null);
    }
    return list;
  }

  public Object executeQueryForObject(Connection conn, Object parameterObject)
      throws SQLException {
    Object o;
    try {
      sqlMapClient.setUserConnection(conn);
      o = sqlMapClient.queryForObject(statementName, parameterObject);
    } finally {
      sqlMapClient.setUserConnection(null);
    }
    return o;
  }

  public Object executeQueryForObject(Connection conn, Object parameterObject, Object resultObject)
      throws SQLException {
    Object o;
    try {
      sqlMapClient.setUserConnection(conn);
      o = sqlMapClient.queryForObject(statementName, parameterObject, resultObject);
    } finally {
      sqlMapClient.setUserConnection(null);
    }
    return o;
  }


}
