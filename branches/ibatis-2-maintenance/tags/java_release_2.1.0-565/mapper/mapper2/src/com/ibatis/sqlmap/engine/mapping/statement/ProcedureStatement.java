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

import com.ibatis.sqlmap.engine.scope.RequestScope;

import java.sql.Connection;
import java.sql.SQLException;

public class ProcedureStatement extends GeneralStatement {

  protected void postProcessParameterObject(RequestScope request, Object parameterObject, Object[] parameters) {
    request.getParameterMap().refreshParameterObjectValues(request, parameterObject, parameters);
  }

  protected int sqlExecuteUpdate(RequestScope request, Connection conn, String sqlString, Object[] parameters) throws SQLException {
    return getSqlExecutor().executeUpdateProcedure(request, conn, sqlString.trim(), parameters);
  }

  protected void sqlExecuteQuery(RequestScope request, Connection conn, String sqlString, Object[] parameters, int skipResults, int maxResults, RowHandlerCallback callback) throws SQLException {
    getSqlExecutor().executeQueryProcedure(request, conn, sqlString.trim(), parameters, skipResults, maxResults, callback);
  }

}
