package com.ibatis.sqlmap.engine.builder;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

public class TypeHandlerCallbackAdapter implements TypeHandler {

  private TypeHandlerCallback callback;

  public TypeHandlerCallbackAdapter(TypeHandlerCallback callback) {
    this.callback = callback;
  }

  public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
    callback.setParameter(new ParameterSetterImpl(ps, i), parameter);
  }

  public Object getResult(ResultSet rs, String columnName) throws SQLException {
    return callback.getResult(new ResultGetterImpl(rs, columnName));
  }

  public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
    return callback.getResult(new ResultGetterImpl(new CallableStatementResultSet(cs), columnIndex));
  }

  public static class ParameterSetterImpl implements ParameterSetter {

    private PreparedStatement ps;
    private int index;

    /**
     * Creates an instance for a PreparedStatement and column index
     *
     * @param statement   - the PreparedStatement
     * @param columnIndex - the column index
     */
    public ParameterSetterImpl(PreparedStatement statement, int columnIndex) {
      this.ps = statement;
      this.index = columnIndex;
    }

    public void setArray(Array x) throws SQLException {
      ps.setArray(index, x);
    }

    public void setAsciiStream(InputStream x, int length) throws SQLException {
      ps.setAsciiStream(index, x, length);
    }

    public void setBigDecimal(BigDecimal x) throws SQLException {
      ps.setBigDecimal(index, x);
    }

    public void setBinaryStream(InputStream x, int length) throws SQLException {
      ps.setBinaryStream(index, x, length);
    }

    public void setBlob(Blob x) throws SQLException {
      ps.setBlob(index, x);
    }

    public void setBoolean(boolean x) throws SQLException {
      ps.setBoolean(index, x);
    }

    public void setByte(byte x) throws SQLException {
      ps.setByte(index, x);
    }

    public void setBytes(byte x[]) throws SQLException {
      ps.setBytes(index, x);
    }

    public void setCharacterStream(Reader reader, int length) throws SQLException {
      ps.setCharacterStream(index, reader, length);
    }

    public void setClob(Clob x) throws SQLException {
      ps.setClob(index, x);
    }

    public void setDate(Date x) throws SQLException {
      ps.setDate(index, x);
    }

    public void setDate(Date x, Calendar cal) throws SQLException {
      ps.setDate(index, x, cal);
    }

    public void setDouble(double x) throws SQLException {
      ps.setDouble(index, x);
    }

    public void setFloat(float x) throws SQLException {
      ps.setFloat(index, x);
    }

    public void setInt(int x) throws SQLException {
      ps.setInt(index, x);
    }

    public void setLong(long x) throws SQLException {
      ps.setLong(index, x);
    }

    public void setNull(int sqlType) throws SQLException {
      ps.setNull(index, sqlType);
    }

    public void setNull(int sqlType, String typeName) throws SQLException {
      ps.setNull(index, sqlType, typeName);
    }

    public void setObject(Object x) throws SQLException {
      ps.setObject(index, x);
    }

    public void setObject(Object x, int targetSqlType) throws SQLException {
      ps.setObject(index, x, targetSqlType);
    }

    public void setObject(Object x, int targetSqlType, int scale) throws SQLException {
      ps.setObject(index, x, scale);
    }

    public void setRef(Ref x) throws SQLException {
      ps.setRef(index, x);
    }

    public void setShort(short x) throws SQLException {
      ps.setShort(index, x);
    }

    public void setString(String x) throws SQLException {
      ps.setString(index, x);
    }

    public void setTime(Time x) throws SQLException {
      ps.setTime(index, x);
    }

    public void setTime(Time x, Calendar cal) throws SQLException {
      ps.setTime(index, x, cal);
    }

    public void setTimestamp(Timestamp x) throws SQLException {
      ps.setTimestamp(index, x);
    }

    public void setTimestamp(Timestamp x, Calendar cal) throws SQLException {
      ps.setTimestamp(index, x, cal);
    }

    public void setURL(URL x) throws SQLException {
      ps.setURL(index, x);
    }

    public PreparedStatement getPreparedStatement() {
      return ps;
    }

    public int getParameterIndex() {
      return index;
    }
  }

  public static class ResultGetterImpl implements ResultGetter {

    private ResultSet rs;
    private String name;
    private int index;

    /**
     * Creates an instance for a PreparedStatement and column index
     *
     * @param resultSet   - the result set
     * @param columnIndex - the column index
     */
    public ResultGetterImpl(ResultSet resultSet, int columnIndex) {
      this.rs = resultSet;
      this.index = columnIndex;
    }

    /**
     * Creates an instance for a PreparedStatement and column name
     *
     * @param resultSet  - the result set
     * @param columnName - the column index
     */
    public ResultGetterImpl(ResultSet resultSet, String columnName) {
      this.rs = resultSet;
      this.name = columnName;
    }


    public Array getArray() throws SQLException {
      if (name != null) {
        return rs.getArray(name);
      } else {
        return rs.getArray(index);
      }
    }

    public BigDecimal getBigDecimal() throws SQLException {
      if (name != null) {
        return rs.getBigDecimal(name);
      } else {
        return rs.getBigDecimal(index);
      }
    }

    public Blob getBlob() throws SQLException {
      if (name != null) {
        return rs.getBlob(name);
      } else {
        return rs.getBlob(index);
      }
    }

    public boolean getBoolean() throws SQLException {
      if (name != null) {
        return rs.getBoolean(name);
      } else {
        return rs.getBoolean(index);
      }
    }

    public byte getByte() throws SQLException {
      if (name != null) {
        return rs.getByte(name);
      } else {
        return rs.getByte(index);
      }
    }

    public byte[] getBytes() throws SQLException {
      if (name != null) {
        return rs.getBytes(name);
      } else {
        return rs.getBytes(index);
      }
    }

    public Clob getClob() throws SQLException {
      if (name != null) {
        return rs.getClob(name);
      } else {
        return rs.getClob(index);
      }
    }

    public Date getDate() throws SQLException {
      if (name != null) {
        return rs.getDate(name);
      } else {
        return rs.getDate(index);
      }
    }

    public Date getDate(Calendar cal) throws SQLException {
      if (name != null) {
        return rs.getDate(name, cal);
      } else {
        return rs.getDate(index, cal);
      }
    }

    public double getDouble() throws SQLException {
      if (name != null) {
        return rs.getDouble(name);
      } else {
        return rs.getDouble(index);
      }
    }

    public float getFloat() throws SQLException {
      if (name != null) {
        return rs.getFloat(name);
      } else {
        return rs.getFloat(index);
      }
    }

    public int getInt() throws SQLException {
      if (name != null) {
        return rs.getInt(name);
      } else {
        return rs.getInt(index);
      }
    }

    public long getLong() throws SQLException {
      if (name != null) {
        return rs.getLong(name);
      } else {
        return rs.getLong(index);
      }
    }

    public Object getObject() throws SQLException {
      if (name != null) {
        return rs.getObject(name);
      } else {
        return rs.getObject(index);
      }
    }

    public Object getObject(Map map) throws SQLException {
      if (name != null) {
        return rs.getObject(name, map);
      } else {
        return rs.getObject(index, map);
      }
    }

    public Ref getRef() throws SQLException {
      if (name != null) {
        return rs.getRef(name);
      } else {
        return rs.getRef(index);
      }
    }

    public short getShort() throws SQLException {
      if (name != null) {
        return rs.getShort(name);
      } else {
        return rs.getShort(index);
      }
    }

    public String getString() throws SQLException {
      if (name != null) {
        return rs.getString(name);
      } else {
        return rs.getString(index);
      }
    }

    public Time getTime() throws SQLException {
      if (name != null) {
        return rs.getTime(name);
      } else {
        return rs.getTime(index);
      }
    }

    public Time getTime(Calendar cal) throws SQLException {
      if (name != null) {
        return rs.getTime(name);
      } else {
        return rs.getTime(index);
      }
    }

    public Timestamp getTimestamp() throws SQLException {
      if (name != null) {
        return rs.getTimestamp(name);
      } else {
        return rs.getTimestamp(index);
      }
    }

    public Timestamp getTimestamp(Calendar cal) throws SQLException {
      if (name != null) {
        return rs.getTimestamp(name, cal);
      } else {
        return rs.getTimestamp(index, cal);
      }
    }

    public URL getURL() throws SQLException {
      if (name != null) {
        return rs.getURL(name);
      } else {
        return rs.getURL(index);
      }
    }

    public boolean wasNull() throws SQLException {
      return rs.wasNull();
    }

    public ResultSet getResultSet() {
      return rs;
    }

    public int getColumnIndex() {
      return index;
    }

    public String getColumnName() {
      return name;
    }
  }

  public static class CallableStatementResultSet implements ResultSet {

    private CallableStatement cs;

    /**
     * Constructor to stretch a ResultSet interface over a CallableStatement
     *
     * @param cs - the CallableStatement
     */
    public CallableStatementResultSet(CallableStatement cs) {
      this.cs = cs;
    }

    public boolean absolute(int row) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void afterLast() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void beforeFirst() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void cancelRowUpdates() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void clearWarnings() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void close() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void deleteRow() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public int findColumn(String columnName) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean first() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public Array getArray(String colName) throws SQLException {
      return cs.getArray(colName);
    }

    public Array getArray(int i) throws SQLException {
      return cs.getArray(i);
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public InputStream getAsciiStream(String columnName) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
      return cs.getBigDecimal(columnIndex);
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException {
      return cs.getBigDecimal(columnName);
    }

    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public InputStream getBinaryStream(String columnName) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public Blob getBlob(String colName) throws SQLException {
      return cs.getBlob(colName);
    }

    public Blob getBlob(int i) throws SQLException {
      return cs.getBlob(i);
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
      return cs.getBoolean(columnIndex);
    }

    public boolean getBoolean(String columnName) throws SQLException {
      return cs.getBoolean(columnName);
    }

    public byte getByte(int columnIndex) throws SQLException {
      return cs.getByte(columnIndex);
    }

    public byte getByte(String columnName) throws SQLException {
      return cs.getByte(columnName);
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
      return cs.getBytes(columnIndex);
    }

    public byte[] getBytes(String columnName) throws SQLException {
      return cs.getBytes(columnName);
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public Reader getCharacterStream(String columnName) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public Clob getClob(String colName) throws SQLException {
      return cs.getClob(colName);
    }

    public Clob getClob(int i) throws SQLException {
      return cs.getClob(i);
    }

    public int getConcurrency() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public String getCursorName() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public Date getDate(int columnIndex) throws SQLException {
      return cs.getDate(columnIndex);
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
      return cs.getDate(columnIndex, cal);
    }

    public Date getDate(String columnName) throws SQLException {
      return cs.getDate(columnName);
    }

    public Date getDate(String columnName, Calendar cal) throws SQLException {
      return cs.getDate(columnName, cal);
    }

    public double getDouble(int columnIndex) throws SQLException {
      return cs.getDouble(columnIndex);
    }

    public double getDouble(String columnName) throws SQLException {
      return cs.getDouble(columnName);
    }

    public int getFetchDirection() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public int getFetchSize() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public float getFloat(int columnIndex) throws SQLException {
      return cs.getFloat(columnIndex);
    }

    public float getFloat(String columnName) throws SQLException {
      return cs.getFloat(columnName);
    }

    public int getInt(int columnIndex) throws SQLException {
      return cs.getInt(columnIndex);
    }

    public int getInt(String columnName) throws SQLException {
      return cs.getInt(columnName);
    }

    public long getLong(int columnIndex) throws SQLException {
      return cs.getLong(columnIndex);
    }

    public long getLong(String columnName) throws SQLException {
      return cs.getLong(columnName);
    }

    public ResultSetMetaData getMetaData() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public Object getObject(String colName, Map map) throws SQLException {
      return cs.getObject(colName, map);
    }

    public Object getObject(int columnIndex) throws SQLException {
      return cs.getObject(columnIndex);
    }

    public Object getObject(String columnName) throws SQLException {
      return cs.getObject(columnName);
    }

    public Object getObject(int i, Map map) throws SQLException {
      return cs.getObject(i, map);
    }

    public Ref getRef(String colName) throws SQLException {
      return cs.getRef(colName);
    }

    public Ref getRef(int i) throws SQLException {
      return cs.getRef(i);
    }

    public int getRow() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public short getShort(int columnIndex) throws SQLException {
      return cs.getShort(columnIndex);
    }

    public short getShort(String columnName) throws SQLException {
      return cs.getShort(columnName);
    }

    public Statement getStatement() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public String getString(int columnIndex) throws SQLException {
      return cs.getString(columnIndex);
    }

    public String getString(String columnName) throws SQLException {
      return cs.getString(columnName);
    }

    public Time getTime(int columnIndex) throws SQLException {
      return cs.getTime(columnIndex);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
      return cs.getTime(columnIndex, cal);
    }

    public Time getTime(String columnName) throws SQLException {
      return cs.getTime(columnName);
    }

    public Time getTime(String columnName, Calendar cal) throws SQLException {
      return cs.getTime(columnName, cal);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
      return cs.getTimestamp(columnIndex);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
      return cs.getTimestamp(columnIndex, cal);
    }

    public Timestamp getTimestamp(String columnName) throws SQLException {
      return cs.getTimestamp(columnName);
    }

    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
      return cs.getTimestamp(columnName, cal);
    }

    public int getType() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public InputStream getUnicodeStream(String columnName) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public URL getURL(int columnIndex) throws SQLException {
      return cs.getURL(columnIndex);
    }

    public URL getURL(String columnName) throws SQLException {
      return cs.getURL(columnName);
    }

    public SQLWarning getWarnings() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void insertRow() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean isAfterLast() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean isBeforeFirst() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean isFirst() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean isLast() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean last() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void moveToCurrentRow() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void moveToInsertRow() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean next() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean previous() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void refreshRow() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean relative(int rows) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean rowDeleted() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean rowInserted() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean rowUpdated() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void setFetchDirection(int direction) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void setFetchSize(int rows) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateArray(String columnName, Array x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateBlob(String columnName, Blob x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateByte(String columnName, byte x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateBytes(String columnName, byte x[]) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateClob(String columnName, Clob x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateDate(String columnName, Date x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateDouble(String columnName, double x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateFloat(String columnName, float x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateInt(String columnName, int x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateLong(String columnName, long x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateNull(int columnIndex) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateNull(String columnName) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateObject(String columnName, Object x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateObject(String columnName, Object x, int scale) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateRef(String columnName, Ref x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateRow() throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateShort(String columnName, short x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateString(int columnIndex, String x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateString(String columnName, String x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateTime(String columnName, Time x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
      throw new UnsupportedOperationException("CallableStatement does not support this method.");
    }

    public boolean wasNull() throws SQLException {
      return cs.wasNull();
    }

  }


}
