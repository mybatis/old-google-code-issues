package org.apache.ibatis.builder.xml.dynamic;

import org.apache.ibatis.reflection.MetaObject;

import java.util.HashMap;
import java.util.Map;

public class DynamicContext {

  private Map<String, Object> bindings = new HashMap<String, Object>();
  private StringBuilder sqlBuilder = new StringBuilder();
  private int uniqueNumber = 0;

  public DynamicContext(Object parameterObject) {
    if (parameterObject instanceof Map) {
      bindings.putAll((Map<String, Object>) parameterObject);
    } else if (parameterObject != null) {
      MetaObject metaObject = MetaObject.forObject(parameterObject);
      String[] names = metaObject.getGetterNames();
      for (String name : names) {
        bindings.put(name, metaObject.getValue(name));
      }
    }
    bindings.put("_parameter", parameterObject);
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
}
