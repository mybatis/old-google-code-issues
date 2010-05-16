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
package com.ibatis.sqlmap.engine.mapping.result;


import com.ibatis.sqlmap.engine.scope.RequestScope;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is a grouping of ResultMapping objects used to map results back to objects
 */
public interface ResultMap {

  public static final Object NO_VALUE = new Object();

  /**
   * A way to identify the ResultMap
   * 
   * @return - an ID
   */
  public String getId();

  /**
   * Perform the mapping, and return the results
   * 
   * @param request - the request scope
   * @param rs - the result set to map
   * 
   * @return - an object array with the data in it
   * 
   * @throws SQLException - if an exception is thrown processing the results
   */
  public Object[] getResults(RequestScope request, ResultSet rs)
      throws SQLException;

  /**
   * Callback method for RowHandler
   * 
   * @param request - the request scope
   * @param resultObject - the object being populated
   * @param values - the values from the database
   * 
   * @return - the populated object
   */
  public Object setResultObjectValues(RequestScope request, Object resultObject, Object[] values);

  /**
   * Getter for the ResultMapping objects
   * 
   * @return - an array of ResultMapping objects
   */
  public ResultMapping[] getResultMappings();

  /**
   * Getter for the class that data wil be mapped into
   * 
   * @return - the class
   */
  public Class getResultClass();

  /**
   * Gets a unique key based on the values provided.
   * @param values Result values representing a single row of results.
   * @return The unique key.
   */
  public Object getUniqueKey(Object[] values);

  public ResultMap resolveSubMap (RequestScope request, ResultSet rs) throws SQLException;

  public Discriminator getDiscriminator();

}
