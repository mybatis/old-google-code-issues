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
package com.ibatis.sqlmap.engine.mapping.sql.dynamic;

import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.mapping.parameter.BasicParameterMap;
import com.ibatis.sqlmap.engine.mapping.parameter.InlineParameterMapParser;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMapping;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.mapping.sql.SqlChild;
import com.ibatis.sqlmap.engine.mapping.sql.SqlText;
import com.ibatis.sqlmap.engine.mapping.sql.dynamic.elements.*;
import com.ibatis.sqlmap.engine.mapping.sql.simple.SimpleDynamicSql;
import com.ibatis.sqlmap.engine.mapping.statement.GeneralStatement;
import com.ibatis.sqlmap.engine.scope.RequestScope;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DynamicSql implements Sql, DynamicParent {

  private static final InlineParameterMapParser PARAM_PARSER = new InlineParameterMapParser();

  private List children = new ArrayList();
  private SqlMapExecutorDelegate delegate;

  public DynamicSql(SqlMapExecutorDelegate delegate) {
    this.delegate = delegate;
  }

  public String getSql(RequestScope request, Object parameterObject) {
    String sql = request.getDynamicSql();
    if (sql == null) {
      process(request, parameterObject);
      sql = request.getDynamicSql();
    }
    return sql;
  }

  public ParameterMap getParameterMap(RequestScope request, Object parameterObject) {
    ParameterMap map = request.getDynamicParameterMap();
    if (map == null) {
      process(request, parameterObject);
      map = request.getDynamicParameterMap();
    }
    return map;
  }

  public ResultMap getResultMap(RequestScope request, Object parameterObject) {
    return request.getResultMap();
  }

  public void cleanup(RequestScope request) {
    request.setDynamicSql(null);
    request.setDynamicParameterMap(null);
  }

  private void process(RequestScope request, Object parameterObject) {
    SqlTagContext ctx = new SqlTagContext();
    List localChildren = children;
    processBodyChildren(request, ctx, parameterObject, localChildren.iterator());

    BasicParameterMap map = new BasicParameterMap(delegate);
    map.setId(request.getStatement().getId() + "-InlineParameterMap");
    map.setParameterClass(((GeneralStatement) request.getStatement()).getParameterClass());
    map.setParameterMappingList(ctx.getParameterMappings());

    String dynSql = ctx.getBodyText();

    // Processes $substitutions$ after DynamicSql
    if (SimpleDynamicSql.isSimpleDynamicSql(dynSql)) {
      dynSql = new SimpleDynamicSql(delegate, dynSql).getSql(request, parameterObject);
    }

    request.setDynamicSql(dynSql);
    request.setDynamicParameterMap(map);
  }

  private void processBodyChildren(RequestScope request, SqlTagContext ctx, Object parameterObject, Iterator localChildren) {
    PrintWriter out = ctx.getWriter();
    processBodyChildren(request, ctx, parameterObject, localChildren, out);
  }

  private void processBodyChildren(RequestScope request, SqlTagContext ctx, Object parameterObject, Iterator localChildren, PrintWriter out) {
    while (localChildren.hasNext()) {
      SqlChild child = (SqlChild) localChildren.next();
      if (child instanceof SqlText) {
        SqlText sqlText = (SqlText) child;
        String sqlStatement = sqlText.getText();
        if (sqlText.isWhiteSpace()) {
          out.print(sqlStatement);
        } else {
          // BODY OUT
          out.print(sqlStatement);

          ParameterMapping[] mappings = sqlText.getParameterMappings();
          if (mappings != null) {
            for (int i = 0, n = mappings.length; i < n; i++) {
              ctx.addParameterMapping(mappings[i]);
            }
          }
        }
      } else if (child instanceof SqlTag) {
        SqlTag tag = (SqlTag) child;
        SqlTagHandler handler = tag.getHandler();
        int response = SqlTagHandler.INCLUDE_BODY;
        do {
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
          
          response = handler.doStartFragment(ctx, tag, parameterObject);
          if (response != SqlTagHandler.SKIP_BODY) {

            processBodyChildren(request, ctx, parameterObject, tag.getChildren(), pw);
            pw.flush();
            pw.close();
            StringBuffer body = sw.getBuffer();
            response = handler.doEndFragment(ctx, tag, parameterObject, body);
            handler.doPrepend(ctx, tag, parameterObject, body);
            
            if (response != SqlTagHandler.SKIP_BODY) {
              if (body.length() > 0) {
                // BODY OUT

                if (tag.isPostParseRequired()) {
                  SqlText sqlText = PARAM_PARSER.parseInlineParameterMap(delegate.getTypeHandlerFactory(), body.toString());
                  out.print(sqlText.getText());
                  ParameterMapping[] mappings = sqlText.getParameterMappings();
                  if (mappings != null) {
                    for (int i = 0, n = mappings.length; i < n; i++) {
                      ctx.addParameterMapping(mappings[i]);
                    }
                  }
                } else {
                  out.print(body.toString());
                }
              }
            }
            
          }
        } while (response == SqlTagHandler.REPEAT_BODY);
        
        ctx.popRemoveFirstPrependMarker(tag);
        
        if(ctx.peekIterateContext()!= null && ctx.peekIterateContext().getTag() == tag) {
          ctx.popIterateContext();
        }
        
      }
    }
  }

  public void addChild(SqlChild child) {
    children.add(child);
  }

}
