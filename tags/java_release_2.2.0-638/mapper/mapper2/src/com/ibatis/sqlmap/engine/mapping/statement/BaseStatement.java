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

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.cache.CacheKey;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.scope.RequestScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public abstract class BaseStatement implements MappedStatement {

  private String id;
  private Integer resultSetType;
  private Integer fetchSize;
  private ResultMap resultMap;
  private ParameterMap parameterMap;
  private Class parameterClass;
  private Sql sql;
  private int baseCacheKey;
  private ExtendedSqlMapClient sqlMapClient;
  private Integer timeout;
  private ResultMap[] additionalResultMaps = new ResultMap[0];

  private List executeListeners = new ArrayList();

  private String resource;

  public String getId() {
    return id;
  }

  public Integer getResultSetType() {
    return resultSetType;
  }

  public void setResultSetType(Integer resultSetType) {
    this.resultSetType = resultSetType;
  }

  public Integer getFetchSize() {
    return fetchSize;
  }

  public void setFetchSize(Integer fetchSize) {
    this.fetchSize = fetchSize;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Sql getSql() {
    return sql;
  }

  public void setSql(Sql sql) {
    this.sql = sql;
  }

  public ResultMap getResultMap() {
    return resultMap;
  }

  public void setResultMap(ResultMap resultMap) {
    this.resultMap = resultMap;
  }

  public ParameterMap getParameterMap() {
    return parameterMap;
  }

  public void setParameterMap(ParameterMap parameterMap) {
    this.parameterMap = parameterMap;
  }

  public Class getParameterClass() {
    return parameterClass;
  }

  public void setParameterClass(Class parameterClass) {
    this.parameterClass = parameterClass;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public CacheKey getCacheKey(RequestScope request, Object parameterObject) {
    Sql sql = request.getSql();
    ParameterMap pmap = sql.getParameterMap(request, parameterObject);
    CacheKey cacheKey = pmap.getCacheKey(request, parameterObject);
    cacheKey.update(id);
    cacheKey.update(baseCacheKey);
    cacheKey.update(sql.getSql(request, parameterObject)); //Fixes bug 953001
    return cacheKey;
  }

  public void setBaseCacheKey(int base) {
    this.baseCacheKey = base;
  }

  public void addExecuteListener(ExecuteListener listener) {
    executeListeners.add(listener);
  }

  public void notifyListeners() {
    for (int i = 0, n = executeListeners.size(); i < n; i++) {
      ((ExecuteListener) executeListeners.get(i)).onExecuteStatement(this);
    }
  }

  public SqlExecutor getSqlExecutor() {
    return sqlMapClient.getSqlExecutor();
  }

  public SqlMapClient getSqlMapClient() {
    return sqlMapClient;
  }

  public void setSqlMapClient(SqlMapClient sqlMapClient) {
    this.sqlMapClient = (ExtendedSqlMapClient) sqlMapClient;
  }

  public void initRequest(RequestScope request) {
    request.setStatement(this);
    request.setParameterMap(parameterMap);
    request.setResultMap(resultMap);
    request.setSql(sql);
  }

  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public void addResultMap(ResultMap resultMap) {
    List resultMapList = Arrays.asList(additionalResultMaps);
    resultMapList = new ArrayList(resultMapList);
    resultMapList.add(resultMap);
    additionalResultMaps = (ResultMap[])resultMapList.toArray(new ResultMap[resultMapList.size()]);
  }

  public boolean hasMultipleResultMaps() {
    return additionalResultMaps.length > 0;
  }

  public ResultMap[] getAdditionalResultMaps() {
    return additionalResultMaps;
  }


}
