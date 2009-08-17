package org.apache.ibatis.submitted.dynsql2;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class FirstNameTypeHandler implements TypeHandler {

    public Object getResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return cs.getString(columnIndex);
    }

    public Object getResult(ResultSet rs, String columnName)
            throws SQLException {
        return rs.getString(columnName);
    }

    public void setParameter(PreparedStatement ps, int i, Object parameter,
            JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.VARCHAR);
        } else {
            Name name = (Name) parameter;
            ps.setString(i, name.getFirstName());
        }
    }

}
