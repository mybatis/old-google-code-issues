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

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.mapping.result.ResultObjectFactory;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;

/**
 * A more advanced SQL map client interface
 */
public interface ExtendedSqlMapClient extends SqlMapClient {

  /**
   * Get the SQL delegate
   * 
   * @return - the SqlMapExecutorDelegate
   */
  public SqlMapExecutorDelegate getDelegate();

  /**
   * Get a mapped statement by ID
   * 
   * @param id - the ID
   * 
   * @return - the mapped statement
   */
  public MappedStatement getMappedStatement(String id);

  /**
   * Get the SQL executor
   * 
   * @return - the SQL executor
   */
  public SqlExecutor getSqlExecutor();

  /**
   * Get the status of lazy loading
   * 
   * @return - the status
   */
  public boolean isLazyLoadingEnabled();

  /**
   * Get the status of CGLib enhancements
   * 
   * @return - the status
   */
  public boolean isEnhancementEnabled();
  
  public ResultObjectFactory getResultObjectFactory();

}
