package org.apache.ibatis.type;

import java.sql.*;

public interface TypeHandler {

  public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
      throws SQLException;

  public Object getResult(ResultSet rs, String columnName)
      throws SQLException;

  public Object getResult(CallableStatement cs, int columnIndex)
      throws SQLException;

}
