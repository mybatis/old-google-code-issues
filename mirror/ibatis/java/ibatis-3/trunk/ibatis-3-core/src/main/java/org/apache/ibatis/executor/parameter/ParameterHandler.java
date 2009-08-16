package org.apache.ibatis.executor.parameter;

import java.sql.*;

public interface ParameterHandler {

  Object getParameterObject();

  void setParameters(PreparedStatement ps)
      throws SQLException;

}
