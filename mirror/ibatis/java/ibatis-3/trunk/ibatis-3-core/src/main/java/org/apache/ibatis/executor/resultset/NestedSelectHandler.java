package org.apache.ibatis.executor.resultset;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.Configuration;
import org.apache.ibatis.executor.loader.ResultLoaderRegistry;
import org.apache.ibatis.executor.loader.ResultLoader;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class NestedSelectHandler {

  private final Configuration configuration;
  private final Executor executor;
  private final ObjectFactory objectFactory;
  private final TypeHandlerRegistry typeHandlerRegistry;
  private final MappedStatement mappedStatement;

  public NestedSelectHandler(Executor executor, Configuration configuration, MappedStatement mappedStatement) {
    this.executor = executor;
    this.configuration = configuration;
    this.mappedStatement = mappedStatement;
    this.objectFactory = configuration.getObjectFactory();
    this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
  }

  public Object processNestedSelect(ResultSet rs, ResultMap rm, ResultMapping resultMapping, ResultLoaderRegistry lazyLoader, Object resultObject)
      throws SQLException {
    Configuration configuration = mappedStatement.getConfiguration();
    MappedStatement nestedQuery = configuration.getMappedStatement(resultMapping.getNestedQueryId());
    Class parameterType = nestedQuery.getParameterMap().getType();
    Object parameterObject = prepareNestedParameterObject(rs, resultMapping, parameterType);
    resultObject = processNestedSelectResult(nestedQuery, rm, resultMapping, lazyLoader, parameterObject, resultObject);
    return resultObject;
  }

  private Object processNestedSelectResult(MappedStatement nestedQuery, ResultMap rm, ResultMapping resultMapping, ResultLoaderRegistry lazyLoader, Object parameterObject, Object resultObject) {
    MetaObject metaResultObject = MetaObject.forObject(resultObject);
    Object value = null;
    try {
      if (parameterObject != null) {
        CacheKey key = executor.createCacheKey(nestedQuery, parameterObject, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT);
        if (executor.isCached(nestedQuery, key)) {
          executor.deferLoad(nestedQuery, metaResultObject, resultMapping.getProperty(), key);
        } else {
          ResultLoader resultLoader = new ResultLoader(configuration, executor, nestedQuery, parameterObject, resultMapping.getJavaType());
          if (lazyLoader == null) {
            value = resultLoader.loadResult();
          } else {
            lazyLoader.registerLoader(resultMapping.getProperty(), metaResultObject, resultLoader);
          }
        }
      }
    } catch (Exception e) {
      throw new ExecutorException("Error setting nested bean property.  Cause: " + e, e);
    }
    if (typeHandlerRegistry.hasTypeHandler(rm.getType())) {
      resultObject = value;
    } else if (value != null) {
      metaResultObject.setValue(resultMapping.getProperty(), value);
    }
    return resultObject;
  }


  private Object prepareNestedParameterObject(ResultSet rs, ResultMapping resultMapping, Class parameterType) throws SQLException {
    Object parameterObject;
    if (typeHandlerRegistry.hasTypeHandler(parameterType)) {
      TypeHandler th = typeHandlerRegistry.getTypeHandler(parameterType);
      parameterObject = th.getResult(rs, resultMapping.getColumn());
    } else {
      if (parameterType == null) {
        parameterObject = new HashMap();
      } else {
        parameterObject = objectFactory.create(parameterType);
      }
      if (resultMapping.isCompositeResult()) {
        MetaObject metaObject = MetaObject.forObject(parameterObject);
        for (ResultMapping innerResultMapping : resultMapping.getComposites()) {
          Class propType = metaObject.getSetterType(innerResultMapping.getProperty());
          TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(propType);
          Object propValue = typeHandler.getResult(rs, innerResultMapping.getColumn());
          metaObject.setValue(innerResultMapping.getProperty(), propValue);
        }
      } else {
        String columnName = resultMapping.getColumn();
        TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(parameterType);
        if (typeHandler == null) {
          typeHandler = typeHandlerRegistry.getUnkownTypeHandler();
        }
        parameterObject = typeHandler.getResult(rs, columnName);
      }
    }
    return parameterObject;
  }


}
