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
package com.ibatis.sqlmap.engine.mapping.statement;

import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.cache.CacheKey;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.scope.RequestScope;
import com.ibatis.sqlmap.engine.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

public interface MappedStatement {

  public String getId();

  public Integer getResultSetType();

  public int executeUpdate(RequestScope request, Transaction trans, Object parameterObject)
      throws SQLException;

  public Object executeQueryForObject(RequestScope request, Transaction trans, Object parameterObject, Object resultObject)
      throws SQLException;

  public List executeQueryForList(RequestScope request, Transaction trans, Object parameterObject, int skipResults, int maxResults)
      throws SQLException;

  public void executeQueryWithRowHandler(RequestScope request, Transaction trans, Object parameterObject, RowHandler rowHandler)
      throws SQLException;

  public CacheKey getCacheKey(RequestScope request, Object parameterObject);

  public ParameterMap getParameterMap();

  public ResultMap getResultMap();

  public void setBaseCacheKey(int base);

  public void addExecuteListener(ExecuteListener listener);

  public void notifyListeners();

  public void initRequest(RequestScope request);

  public Sql getSql();

  public Class getParameterClass();

  public Integer getFetchSize();


}
