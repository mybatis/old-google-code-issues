package org.apache.ibatis.executor.keygen;

import org.apache.ibatis.executor.*;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.type.*;

import java.sql.*;

public class Jdbc3KeyGenerator implements KeyGenerator {

  public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
  }

  public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
    try {
      final Configuration configuration = ms.getConfiguration();
      final TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
      if (parameter != null) {
        String keyProperty = ms.getKeyProperty();
        final MetaObject metaParam = MetaObject.forObject(parameter);
        if (keyProperty != null && metaParam.hasSetter(keyProperty)) {
          Class keyPropertyType = metaParam.getSetterType(keyProperty);
          TypeHandler th =  typeHandlerRegistry.getTypeHandler(keyPropertyType);
          if (th != null) {
            ResultSet rs = stmt.getGeneratedKeys();
            try {
              ResultSetMetaData rsmd = rs.getMetaData();
              int colCount = rsmd.getColumnCount();
              if (colCount > 0) {
                String colName = rsmd.getColumnName(1);
                while (rs.next()) {
                  Object value = th.getResult(rs,colName);
                  metaParam.setValue(keyProperty,value);
                }
              }
            } finally {
              try {
                if (rs != null) rs.close();
              } catch (Exception e) {
                //ignore
              }
            }
          }
        }
      }
    } catch (Exception e) {
      throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
    }
  }


}
