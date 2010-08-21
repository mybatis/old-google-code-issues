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
package com.ibatis.common.jdbc.logging;

import com.ibatis.common.beans.ClassInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;

/**
 * ResultSet proxy to add logging
 */
public class ResultSetLogProxy extends BaseLogProxy implements InvocationHandler {

  private static final Log log = LogFactory.getLog(ResultSet.class);

  boolean first = true;
  private ResultSet rs;

  private ResultSetLogProxy(ResultSet rs) {
    super();
    this.rs = rs;
    if (log.isDebugEnabled()) {
      log.debug("{rset-" + id + "} ResultSet");
    }
  }

  public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
    try {
      Object o = method.invoke(rs, params);
      if (GET_METHODS.contains(method.getName())) {
        if (params[0] instanceof String) {
          setColumn(params[0], o);
          //        setColumn(params[0], rs.getObject((String) params[0]));
          //      } else {
          //        setColumn(params[0], rs.getObject(((Integer) params[0]).intValue()));
        }
      } else if ("next".equals(method.getName())) {
        String s = getValueString();
        if (!"[]".equals(s)) {
          if (first) {
            first = false;
            if (log.isDebugEnabled()) {
              log.debug("{rset-" + id + "} Header: " + getColumnString());
            }
          }
          if (log.isDebugEnabled()) {
            log.debug("{rset-" + id + "} Result: " + s);
          }
        }
        clearColumnInfo();
      }
      return o;
    } catch (Throwable t) {
      throw ClassInfo.unwrapThrowable(t);
    }
  }

  /**
   * Creates a logging version of a ResultSet 
   * 
   * @param rs - the ResultSet to proxy
   * @return - the ResultSet with logging
   */
  public static ResultSet newInstance(ResultSet rs) {
    InvocationHandler handler = new ResultSetLogProxy(rs);
    ClassLoader cl = ResultSet.class.getClassLoader();
    return (ResultSet) Proxy.newProxyInstance(cl, new Class[]{ResultSet.class}, handler);
  }


}
