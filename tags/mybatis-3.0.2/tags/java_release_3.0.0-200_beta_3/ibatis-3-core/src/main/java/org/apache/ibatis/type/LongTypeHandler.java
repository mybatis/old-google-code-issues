package org.apache.ibatis.type;

import java.sql.*;

public class LongTypeHandler extends BaseTypeHandler {

  public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setLong(i, (Long) parameter);
  }

  public Object getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    return rs.getLong(columnName);
  }

  public Object getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    return cs.getLong(columnIndex);
  }

}
