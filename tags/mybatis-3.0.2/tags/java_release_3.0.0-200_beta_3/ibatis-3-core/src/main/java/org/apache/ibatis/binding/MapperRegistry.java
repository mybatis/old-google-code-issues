package org.apache.ibatis.binding;

import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.mapping.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.util.*;

public class MapperRegistry {

  private Configuration config;
  private Set<Class> knownMappers = new HashSet<Class>();

  public MapperRegistry(Configuration config) {
    this.config = config;
  }

  public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    if (!knownMappers.contains(type))
      throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
    try {
      return MapperProxy.newMapperProxy(type, sqlSession);
    } catch (Exception e) {
      throw new BindingException("Error getting mapper instance. Cause: " + e, e);
    }
  }

  public boolean hasMapper(Class type) {
    return knownMappers.contains(type);
  }

  public void addMapper(Class type) {
    if (!type.isInterface())
      throw new BindingException("Only interfaces can be configured by the MapperFactory.  Type " + type + " is not an interface.");
    if (knownMappers.contains(type))
      throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
    knownMappers.add(type);
    // It's important that the type is added before the parser is run
    // otherwise the binding may automatically be attempted by the
    // mapper parser.  If the type is already known, it won't try.
    MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
    parser.parse();
  }
}
