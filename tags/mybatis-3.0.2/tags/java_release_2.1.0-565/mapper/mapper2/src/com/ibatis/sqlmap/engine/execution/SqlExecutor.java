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
package com.ibatis.sqlmap.engine.execution;

import com.ibatis.sqlmap.engine.mapping.parameter.BasicParameterMapping;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMapping;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.scope.ErrorContext;
import com.ibatis.sqlmap.engine.scope.RequestScope;
import com.ibatis.sqlmap.engine.scope.SessionScope;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for executing the SQL
 */
public class SqlExecutor {

  //
  // Constants
  //

  /**
   * Constant to let us know not to skip anything
   */
  public static final int NO_SKIPPED_RESULTS = 0;
  
  /**
   * Constant to let us know to include all records
   */
  public static final int NO_MAXIMUM_RESULTS = -999999;

  //
  // Public Methods
  //

  /**
   * Execute an update
   * 
   * @param request - the request scope
   * @param conn - the database connection
   * @param sql - the sql statement to execute
   * @param parameters - the parameters for the sql statement
   * 
   * @return - the number of records changed
   * 
   * @throws SQLException - if the update fails
   */
  public int executeUpdate(RequestScope request, Connection conn, String sql, Object[] parameters)
      throws SQLException {
    ErrorContext errorContext = request.getErrorContext();
    errorContext.setActivity("executing update");
    errorContext.setObjectId(sql);

    PreparedStatement ps = null;
    int rows = 0;

    try {
      errorContext.setMoreInfo("Check the SQL Statement (preparation failed).");
      ps = conn.prepareStatement(sql);

      errorContext.setMoreInfo("Check the parameters (set parameters failed).");
      request.getParameterMap().setParameters(request, ps, parameters);

      errorContext.setMoreInfo("Check the statement (update failed).");

      ps.execute();
      rows = ps.getUpdateCount();
    }
    finally {
      closeStatement(ps);
    }

    return rows;
  }

  /**
   * Adds a statement to a batch
   * 
   * @param request - the request scope
   * @param conn - the database connection
   * @param sql - the sql statement
   * @param parameters - the parameters for the statement
   * 
   * @throws SQLException - if the statement fails
   */
  public void addBatch(RequestScope request, Connection conn, String sql, Object[] parameters)
      throws SQLException {
    Batch batch = (Batch) request.getSession().getBatch();
    if (batch == null) {
      batch = new Batch();
      request.getSession().setBatch(batch);
    }
    batch.addBatch(request, conn, sql, parameters);
  }

  /**
   * Execute a batch of statements
   * 
   * @param session - the session scope
   * 
   * @return - the number of rows impacted by the batch
   * 
   * @throws SQLException - if a statement fails
   */
  public int executeBatch(SessionScope session)
      throws SQLException {
    int rows = 0;
    Batch batch = (Batch) session.getBatch();
    if (batch != null) {
      try {
        rows = batch.executeBatch();
      } finally {
        batch.cleanupBatch();
      }
    }
    return rows;
  }

  /**
   * Long form of the method to execute a query
   * 
   * @param request - the request scope
   * @param conn - the database connection
   * @param sql - the SQL statement to execute
   * @param parameters - the parameters for the statement
   * @param skipResults - the number of results to skip
   * @param maxResults - the maximum number of results to return
   * @param callback - the row handler for the query
   * 
   * @throws SQLException - if the query fails
   */
  public void executeQuery(RequestScope request, Connection conn, String sql, Object[] parameters,
                           int skipResults, int maxResults, RowHandlerCallback callback)
      throws SQLException {
    ErrorContext errorContext = request.getErrorContext();
    errorContext.setActivity("executing query");
    errorContext.setObjectId(sql);

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      errorContext.setMoreInfo("Check the SQL Statement (preparation failed).");

      Integer rsType = request.getStatement().getResultSetType();
      if (rsType != null) {
        ps = conn.prepareStatement(sql, rsType.intValue(), ResultSet.CONCUR_READ_ONLY);
      } else {
        ps = conn.prepareStatement(sql);
      }

      Integer fetchSize = request.getStatement().getFetchSize();
      if (fetchSize != null) {
        ps.setFetchSize(fetchSize.intValue());
      }

      errorContext.setMoreInfo("Check the parameters (set parameters failed).");
      request.getParameterMap().setParameters(request, ps, parameters);

      errorContext.setMoreInfo("Check the statement (query failed).");

      ps.execute();
      rs = ps.getResultSet();

      errorContext.setMoreInfo("Check the results (failed to retrieve results).");
      handleResults(request, rs, skipResults, maxResults, callback);

      // clear out remaining results
      while (ps.getMoreResults());

    } finally {
      try {
        closeResultSet(rs);
      } finally {
        closeStatement(ps);
      }
    }

  }

  /**
   * Execute a stored procedure that updates data
   * 
   * @param request - the request scope
   * @param conn - the database connection
   * @param sql - the SQL to call the procedure
   * @param parameters - the parameters for the procedure
   * 
   * @return - the rows impacted by the procedure
   * 
   * @throws SQLException - if the procedure fails
   */
  public int executeUpdateProcedure(RequestScope request, Connection conn, String sql, Object[] parameters)
      throws SQLException {
    ErrorContext errorContext = request.getErrorContext();
    errorContext.setActivity("executing update procedure");
    errorContext.setObjectId(sql);

    CallableStatement cs = null;
    int rows = 0;

    try {
      errorContext.setMoreInfo("Check the SQL Statement (preparation failed).");
      cs = conn.prepareCall(sql);

      ParameterMap parameterMap = request.getParameterMap();

      ParameterMapping[] mappings = parameterMap.getParameterMappings();

      errorContext.setMoreInfo("Check the output parameters (register output parameters failed).");
      registerOutputParameters(cs, mappings);

      errorContext.setMoreInfo("Check the parameters (set parameters failed).");
      parameterMap.setParameters(request, cs, parameters);

      errorContext.setMoreInfo("Check the statement (update procedure failed).");

      cs.execute();
      rows = cs.getUpdateCount();

      errorContext.setMoreInfo("Check the output parameters (retrieval of output parameters failed).");
      retrieveOutputParameters(cs, mappings, parameters);
    } finally {
      closeStatement(cs);
    }

    return rows;
  }

  /**
   * Execute a stored procedure
   * 
   * @param request - the request scope
   * @param conn - the database connection
   * @param sql - the sql to call the procedure
   * @param parameters - the parameters for the procedure
   * @param skipResults - the number of results to skip
   * @param maxResults - the maximum number of results to return
   * @param callback - a row handler for processing the results
   * 
   * @throws SQLException - if the procedure fails
   */
  public void executeQueryProcedure(RequestScope request, Connection conn, String sql, Object[] parameters,
                                    int skipResults, int maxResults, RowHandlerCallback callback)
      throws SQLException {
    ErrorContext errorContext = request.getErrorContext();
    errorContext.setActivity("executing query procedure");
    errorContext.setObjectId(sql);

    CallableStatement cs = null;
    ResultSet rs = null;

    try {
      errorContext.setMoreInfo("Check the SQL Statement (preparation failed).");
      cs = conn.prepareCall(sql);

      ParameterMap parameterMap = request.getParameterMap();

      ParameterMapping[] mappings = parameterMap.getParameterMappings();

      errorContext.setMoreInfo("Check the output parameters (register output parameters failed).");
      registerOutputParameters(cs, mappings);

      errorContext.setMoreInfo("Check the parameters (set parameters failed).");
      parameterMap.setParameters(request, cs, parameters);

      errorContext.setMoreInfo("Check the statement (update procedure failed).");

      cs.execute();
      rs = cs.getResultSet();

      errorContext.setMoreInfo("Check the results (failed to retrieve results).");
      handleResults(request, rs, skipResults, maxResults, callback);

      errorContext.setMoreInfo("Check the output parameters (retrieval of output parameters failed).");
      retrieveOutputParameters(cs, mappings, parameters);

    } finally {
      try {
        closeResultSet(rs);
      } finally {
        closeStatement(cs);
      }
    }

  }

  /**
   * Clean up any batches on the session
   * 
   * @param session - the session to clean up
   */
  public void cleanup(SessionScope session) {
    Batch batch = (Batch) session.getBatch();
    if (batch != null) {
      batch.cleanupBatch();
      session.setBatch(null);
    }
  }

  //
  // Private Methods
  //

  private void retrieveOutputParameters(CallableStatement cs, ParameterMapping[] mappings, Object[] parameters) throws SQLException {
    for (int i = 0; i < mappings.length; i++) {
      BasicParameterMapping mapping = ((BasicParameterMapping) mappings[i]);
      if (mapping.isOutputAllowed()) {
        Object o = mapping.getTypeHandler().getResult(cs, i + 1);
        parameters[i] = o;
      }
    }
  }

  private void registerOutputParameters(CallableStatement cs, ParameterMapping[] mappings) throws SQLException {
    for (int i = 0; i < mappings.length; i++) {
      BasicParameterMapping mapping = ((BasicParameterMapping) mappings[i]);
      if (mapping.isOutputAllowed()) {
        cs.registerOutParameter(i + 1, mapping.getJdbcType());
      }
    }
  }

  private void handleResults(RequestScope request, ResultSet rs, int skipResults, int maxResults, RowHandlerCallback callback) throws SQLException {
    try {
      request.setResultSet(rs);
      ResultMap resultMap = request.getResultMap();
      if (resultMap != null) {
        // Skip Results
        if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
          if (skipResults > 0) {
            rs.absolute(skipResults);
          }
        } else {
          for (int i = 0; i < skipResults; i++) {
            if (!rs.next()) {
              break;
            }
          }
        }

        // Get Results
        int resultsFetched = 0;
        while ((maxResults == SqlExecutor.NO_MAXIMUM_RESULTS || resultsFetched < maxResults) && rs.next()) {
          Object[] columnValues = resultMap.resolveSubMap(request, rs).getResults(request, rs);
          callback.handleResultObject(request, columnValues, rs);
          resultsFetched++;
        }
      }
    } finally {
      request.setResultSet(null);
    }
  }

  /**
   * @param ps
   */
  private static void closeStatement(PreparedStatement ps) {
    if (ps != null) {
      try {
        ps.close();
      } catch (SQLException e) {
        // ignore
      }
    }
  }

  /**
   * @param rs
   */
  private static void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        // ignore
      }
    }
  }

  //
  // Inner Classes
  //

  private static class Batch {
    private String currentSql;
    private List statementList = new ArrayList();
    private int size;
    private static final int SUCCESS_NO_INFO = -2;
    private static final int EXECUTE_FAILED = -3;

    /**
     * Create a new batch
     */
    public Batch() {
      this.size = 0;
    }

    /**
     * Getter for the batch size
     * 
     * @return - the batch size
     */
    public int getSize() {
      return size;
    }

    /**
     * Add a prepared statement to the batch
     * 
     * @param request - the request scope
     * @param conn - the database connection
     * @param sql - the SQL to add
     * @param parameters - the parameters for the SQL
     * 
     * @throws SQLException - if the prepare for the SQL fails
     */
    public void addBatch(RequestScope request, Connection conn, String sql, Object[] parameters) throws SQLException {
      PreparedStatement ps = null;
      if (currentSql != null
          && sql.hashCode() == currentSql.hashCode()
          && sql.length() == currentSql.length()) {
        int last = statementList.size() - 1;
        ps = (PreparedStatement) statementList.get(last);
      } else {
        ps = conn.prepareStatement(sql);
        currentSql = sql;
        statementList.add(ps);
      }
      request.getParameterMap().setParameters(request, ps, parameters);
      ps.addBatch();
      size++;
    }

    /**
     * Execute the current session's batch
     * 
     * @return - the number of rows updated
     * 
     * @throws SQLException - if the batch fails
     */
    public int executeBatch() throws SQLException {
      int totalRowCount = 0;
      for (int i = 0, n = statementList.size(); i < n; i++) {
        PreparedStatement ps = (PreparedStatement) statementList.get(i);
        int[] rowCounts = ps.executeBatch();
        for (int j = 0; j < rowCounts.length; j++) {
          if (rowCounts[j] == SUCCESS_NO_INFO) {
            // do nothing
          } else if (rowCounts[j] == EXECUTE_FAILED) {
            throw new SQLException("The batched statement at index " + j + " failed to execute.");
          } else {
            totalRowCount += rowCounts[j];
          }
        }
      }
      return totalRowCount;
    }

    /**
     * Close all the statements in the batch and clear all the statements
     */
    public void cleanupBatch() {
      for (int i = 0, n = statementList.size(); i < n; i++) {
        PreparedStatement ps = (PreparedStatement) statementList.get(i);
        closeStatement(ps);
      }
      currentSql = null;
      statementList.clear();
      size = 0;
    }
  }

}
