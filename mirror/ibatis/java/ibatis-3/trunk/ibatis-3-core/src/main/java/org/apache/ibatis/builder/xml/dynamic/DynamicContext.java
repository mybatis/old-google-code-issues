package org.apache.ibatis.builder.xml.dynamic;

import org.apache.ibatis.ognl.OgnlException;
import org.apache.ibatis.ognl.OgnlRuntime;
import org.apache.ibatis.ognl.PropertyAccessor;
import org.apache.ibatis.reflection.MetaObject;

import java.util.HashMap;
import java.util.Map;

public class DynamicContext {

  public static final String PARAMETER_OBJECT_KEY = "_parameter";

  static {
    OgnlRuntime.setPropertyAccessor(ContextMap.class, new ContextAccessor());
  }

  private final ContextMap bindings = new ContextMap();
  private final StringBuilder sqlBuilder = new StringBuilder();
  private int uniqueNumber = 0;

  public DynamicContext(Object parameterObject) {
    if (parameterObject != null && !(parameterObject instanceof Map)) {
      MetaObject metaObject = MetaObject.forObject(parameterObject);
      String[] names = metaObject.getGetterNames();
      for (String name : names) {
        bindings.put(name, metaObject.getValue(name));
      }
    }
    bindings.put(PARAMETER_OBJECT_KEY, parameterObject);
  }

  public Map<String, Object> getBindings() {
    return bindings;
  }

  public void bind(String name, Object value) {
    bindings.put(name, value);
  }

  public void appendSql(String sql) {
    sqlBuilder.append(sql);
    sqlBuilder.append(" ");
  }

  public String getSql() {
    return sqlBuilder.toString().trim();
  }

  public int getUniqueNumber() {
    return uniqueNumber++;
  }

  static class ContextMap extends HashMap<String, Object> {
  }

  static class ContextAccessor implements PropertyAccessor {

    public Object getProperty(Map context, Object target, Object name)
        throws OgnlException {
      Map map = (Map) target;

      Object result = map.get(name);
      if (result != null) {
        return result;
      }      

      Object parameterObject = map.get(PARAMETER_OBJECT_KEY);
      if (parameterObject instanceof Map) {
    	  return ((Map)parameterObject).get(name);
      }

      return null;
    }

    public void setProperty(Map context, Object target, Object name, Object value)
        throws OgnlException {
      Map map = (Map) target;
      map.put(name, value);
    }
  }

}
