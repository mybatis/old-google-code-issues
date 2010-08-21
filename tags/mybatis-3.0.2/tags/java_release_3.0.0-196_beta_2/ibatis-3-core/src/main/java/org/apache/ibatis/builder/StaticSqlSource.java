package org.apache.ibatis.builder;

import org.apache.ibatis.mapping.*;

import java.util.List;

public class StaticSqlSource implements SqlSource {

  private String sql;
  private List<ParameterMapping> parameterMappings;

  public StaticSqlSource(String sql) {
    this(sql, null);
  }

  public StaticSqlSource(String sql, List<ParameterMapping> parameterMappings) {
    this.sql = sql;
    this.parameterMappings = parameterMappings;
  }

  public BoundSql getBoundSql(Object parameterObject) {
    return new BoundSql(sql, parameterMappings, parameterObject);
  }

}
