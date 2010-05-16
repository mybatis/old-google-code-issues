package org.apache.ibatis.logging.jdbc;

import java.util.*;

/**
 * Base class for proxies to do logging
 */
public class BaseJdbcLogger {

  protected static final Set<String> SET_METHODS = new HashSet<String>();
  protected static final Set<String> GET_METHODS = new HashSet<String>();
  protected static final Set<String> EXECUTE_METHODS = new HashSet<String>();

  private Map<Object, Object> columnMap = new HashMap<Object, Object>();

  private List<Object> columnNames = new ArrayList<Object>();
  private List<Object> columnValues = new ArrayList<Object>();

  /**
   * Default constructor
   */
  public BaseJdbcLogger() {
  }

  static {
    SET_METHODS.add("setString");
    SET_METHODS.add("setInt");
    SET_METHODS.add("setByte");
    SET_METHODS.add("setShort");
    SET_METHODS.add("setLong");
    SET_METHODS.add("setDouble");
    SET_METHODS.add("setFloat");
    SET_METHODS.add("setTimestamp");
    SET_METHODS.add("setDate");
    SET_METHODS.add("setTime");
    SET_METHODS.add("setArray");
    SET_METHODS.add("setBigDecimal");
    SET_METHODS.add("setAsciiStream");
    SET_METHODS.add("setBinaryStream");
    SET_METHODS.add("setBlob");
    SET_METHODS.add("setBoolean");
    SET_METHODS.add("setBytes");
    SET_METHODS.add("setCharacterStream");
    SET_METHODS.add("setClob");
    SET_METHODS.add("setObject");
    SET_METHODS.add("setNull");

    GET_METHODS.add("getString");
    GET_METHODS.add("getInt");
    GET_METHODS.add("getByte");
    GET_METHODS.add("getShort");
    GET_METHODS.add("getLong");
    GET_METHODS.add("getDouble");
    GET_METHODS.add("getFloat");
    GET_METHODS.add("getTimestamp");
    GET_METHODS.add("getDate");
    GET_METHODS.add("getTime");
    GET_METHODS.add("getArray");
    GET_METHODS.add("getBigDecimal");
    GET_METHODS.add("getAsciiStream");
    GET_METHODS.add("getBinaryStream");
    GET_METHODS.add("getBlob");
    GET_METHODS.add("getBoolean");
    GET_METHODS.add("getBytes");
    GET_METHODS.add("getCharacterStream");
    GET_METHODS.add("getClob");
    GET_METHODS.add("getObject");
    GET_METHODS.add("getNull");

    EXECUTE_METHODS.add("execute");
    EXECUTE_METHODS.add("executeUpdate");
    EXECUTE_METHODS.add("executeQuery");

  }

  protected void setColumn(Object key, Object value) {
    columnMap.put(key, value);
    columnNames.add(key);
    columnValues.add(value);
  }

  protected Object getColumn(Object key) {
    return columnMap.get(key);
  }

  protected String getValueString() {
    return columnValues.toString();
  }

  protected String getTypeString() {
    List<Object> typeList = new ArrayList<Object>(columnValues.size());
    for (Object value : columnValues) {
      if (value == null) {
        typeList.add("null");
      } else {
        typeList.add(value.getClass().getName());
      }
    }
    return typeList.toString();
  }

  protected String getColumnString() {
    return columnNames.toString();
  }

  protected void clearColumnInfo() {
    columnMap.clear();
    columnNames.clear();
    columnValues.clear();
  }

  protected String removeBreakingWhitespace(String original) {
    StringTokenizer whitespaceStripper = new StringTokenizer(original);
    StringBuilder builder = new StringBuilder();
    while (whitespaceStripper.hasMoreTokens()) {
      builder.append(whitespaceStripper.nextToken());
      builder.append(" ");
    }
    return builder.toString();
  }

}
