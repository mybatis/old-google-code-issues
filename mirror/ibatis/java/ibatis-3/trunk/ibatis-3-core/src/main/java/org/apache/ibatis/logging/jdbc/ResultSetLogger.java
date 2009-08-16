package org.apache.ibatis.logging.jdbc;

import org.apache.ibatis.logging.*;
import org.apache.ibatis.reflection.ExceptionUtil;

import java.lang.reflect.*;
import java.sql.ResultSet;

/**
 * ResultSet proxy to add logging
 */
public class ResultSetLogger extends BaseJdbcLogger implements InvocationHandler {

  private static final Log log = LogFactory.getLog(ResultSet.class);

  boolean first = true;
  private ResultSet rs;

  private ResultSetLogger(ResultSet rs) {
    super();
    this.rs = rs;
    if (log.isDebugEnabled()) {
      log.debug("<== ResultSet Returned");
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
      } else if ("next".equals(method.getName()) || "close".equals(method.getName())) {
        String s = getValueString();
        if (!"[]".equals(s)) {
          if (first) {
            first = false;
            if (log.isDebugEnabled()) {
              log.debug("<== Columns: " + getColumnString());
            }
          }
          if (log.isDebugEnabled()) {
            log.debug("<== Row: " + s);
          }
        }
        clearColumnInfo();
      }
      return o;
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
  }

  /**
   * Creates a logging version of a ResultSet
   *
   * @param rs - the ResultSet to proxy
   * @return - the ResultSet with logging
   */
  public static ResultSet newInstance(ResultSet rs) {
    InvocationHandler handler = new ResultSetLogger(rs);
    ClassLoader cl = ResultSet.class.getClassLoader();
    return (ResultSet) Proxy.newProxyInstance(cl, new Class[]{ResultSet.class}, handler);
  }

  /**
   * Get the wrapped result set
   *
   * @return the resultSet
   */
  public ResultSet getRs() {
    return rs;
  }

}
