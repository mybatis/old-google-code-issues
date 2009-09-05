package org.apache.ibatis.executor.resultset;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.Discriminator;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.executor.ExecutorException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscriminatorHandler {

  private final MappedStatement mappedStatement;

  public DiscriminatorHandler(MappedStatement mappedStatement) {
    this.mappedStatement = mappedStatement;
  }

  public ResultMap resolveSubMap(ResultSet rs, ResultMap rm) throws SQLException {
    ResultMap subMap = rm;
    Discriminator discriminator = rm.getDiscriminator();
    if (discriminator != null) {
      ResultMapping resultMapping = discriminator.getResultMapping();
      Object value = getPrimitiveResultMappingValue(rs, resultMapping);
      String subMapId = discriminator.getMapIdFor(String.valueOf(value));

      try {
        subMap = mappedStatement.getConfiguration().getResultMap(subMapId);
      } catch (Exception e) {
        subMap = rm;
      }

      if (subMap != rm) {
        subMap = resolveSubMap(rs, subMap);
      }
    }
    return subMap;
  }

  private Object getPrimitiveResultMappingValue(ResultSet rs, ResultMapping resultMapping) throws SQLException {
    Object value;
    TypeHandler typeHandler = resultMapping.getTypeHandler();
    if (typeHandler != null) {
      value = typeHandler.getResult(rs, resultMapping.getColumn());
    } else {
      throw new ExecutorException("No type handler could be found to map the property '" + resultMapping.getProperty() + "' to the column '" + resultMapping.getColumn() + "'.  One or both of the types, or the combination of types is not supported.");
    }
    return value;
  }

}
