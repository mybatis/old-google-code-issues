package org.apache.ibatis.executor.resultset;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.*;
import org.apache.ibatis.executor.loader.*;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.result.*;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.*;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.type.*;

import java.sql.*;
import java.util.*;

public class DefaultResultSetHandler implements ResultSetHandler {

  private static final Object NO_VALUE = new Object();

  private Configuration configuration;
  private final Executor executor;
  private final ObjectFactory objectFactory;
  private final TypeHandlerRegistry typeHandlerRegistry;
  private final MappedStatement mappedStatement;
  private final int rowOffset;
  private final int rowLimit;
  private final Object parameterObject;

  private final Map nestedResultObjects;

  private final ResultHandler resultHandler;
  private final BoundSql boundSql;

  private CacheKey currentNestedKey;

  public DefaultResultSetHandler(Configuration configuration, Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, int rowOffset, int rowLimit, ResultHandler resultHandler, BoundSql boundSql) {
    this.configuration = configuration;
    this.executor = executor;
    this.objectFactory = mappedStatement.getConfiguration().getObjectFactory();
    this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    this.mappedStatement = mappedStatement;
    this.rowOffset = rowOffset;
    this.rowLimit = rowLimit;
    this.parameterObject = parameterHandler.getParameterObject();
    this.nestedResultObjects = new HashMap();
    this.resultHandler = resultHandler;
    this.boundSql = boundSql;
  }

  public List handleResultSets(Statement statement) throws SQLException {
    List<List> resultsList = new ArrayList<List>();
    ResultSet rs = getFirstResultSet(statement);
    if (rs != null) {
      try {
        for (int i = 0, n = mappedStatement.getResultMaps().size(); i < n; i++) {
          ResultMap resultMap = mappedStatement.getResultMaps().get(i);
          ErrorContext.instance().activity("handling result set").object(resultMap.getId());
          if (resultHandler == null) {
            DefaultResultHandler defaultResultHandler = new DefaultResultHandler();
            handleResults(rs, resultMap, defaultResultHandler, rowOffset, rowLimit);
            resultsList.add(defaultResultHandler.getResultList());
          } else {
            handleResults(rs, resultMap, resultHandler, rowOffset, rowLimit);
          }
          if (moveToNextResultsSafely(statement)) {
            rs = statement.getResultSet();
            nestedResultObjects.clear();
          } else {
            break;
          }
        }
      } finally {
        closeResultSet(rs);
      }
    }
    if (resultsList.size() == 1) {
      return resultsList.get(0);
    } else {
      return resultsList;
    }
  }

  public void handleOutputParameters(CallableStatement callableStatement) throws SQLException {
    ErrorContext.instance().activity("handling output parameters");
    MetaObject metaParam = MetaObject.forObject(parameterObject);
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    for (int i = 0; i < parameterMappings.size(); i++) {
      ParameterMapping parameterMapping = parameterMappings.get(i);
      if (parameterMapping.getMode() == ParameterMode.OUT || parameterMapping.getMode() == ParameterMode.INOUT) {
        if ("java.sql.ResultSet".equalsIgnoreCase(parameterMapping.getJavaType().getName())) {
          // TODO: We need an easy way to unit test this without installing Oracle.
          // Mocks are obvious, but will they be effective enough?  DBunit?
          ResultSet rs = (ResultSet) callableStatement.getObject(i + 1);
          String resultMapId = parameterMapping.getResultMapId();
          if (resultMapId != null) {
            ResultMap resultMap = mappedStatement.getConfiguration().getResultMap(resultMapId);
            DefaultResultHandler resultHandler = new DefaultResultHandler();
            handleResults(rs, resultMap, resultHandler, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT);
            metaParam.setValue(parameterMapping.getProperty(), resultHandler.getResultList());
          } else {
            throw new ExecutorException("Parameter requires ResultMap for output types of java.sql.ResultSet");
          }
          rs.close();
        } else {
          metaParam.setValue(parameterMapping.getProperty(), parameterMapping.getTypeHandler().getResult(callableStatement, i + 1));
        }
      }
    }
  }

  private void handleResults(ResultSet rs, ResultMap resultMap, ResultHandler resultHandler, int skipResults, int maxResults) throws SQLException {
    if (resultMap != null) {
      skipResults(rs, skipResults);
      ResultContext context = new ResultContext();
      while ((maxResults == Executor.NO_ROW_LIMIT || context.getResultCount() < maxResults)
          && !context.isStopped() && rs.next()) {
        currentNestedKey = null;
        ResultMap rm = resolveSubMap(rs, resultMap);
        Object resultObject = loadResultObject(rs, rm, new Reference(false));
        if (resultObject != NO_VALUE) {
          if (resultObject instanceof PlatformTypeHolder) {
            resultObject = ((PlatformTypeHolder) resultObject).get(null);
          }
          context.nextResultObject(resultObject);
          resultHandler.handleResult(context);
        }
      }
    }
  }

  private Object loadResultObject(ResultSet rs, ResultMap rm, Reference<Boolean> foundValues) throws SQLException {
    if (rm.getType() == null) {
      throw new ExecutorException("The result class was null when trying to get results for ResultMap " + rm.getId());
    }

    Object resultObject = createResultObject(rs, rm);
    ResultLoaderRegistry lazyLoader = null;
    if (this.mappedStatement.getConfiguration().isEnhancementEnabled()) {
      lazyLoader = new ResultLoaderRegistry();
      resultObject = ResultObjectProxy.createProxy(rm.getType(), resultObject, lazyLoader);
    }

    List<ResultMapping> appliedResultMappings = new ArrayList<ResultMapping>();
    resultObject = mapResults(rs, rm, lazyLoader, resultObject, appliedResultMappings, foundValues);
    resultObject = processNestedJoinResults(rs, appliedResultMappings, resultObject);
    return resultObject;
  }

  private Object mapResults(ResultSet rs, ResultMap rm, ResultLoaderRegistry lazyLoader, Object resultObject, List<ResultMapping> appliedResultMappings, Reference<Boolean> foundValues) throws SQLException {
    MetaObject metaResultObject = MetaObject.forObject(resultObject);
    Set<String> propSet = new HashSet<String>();
    Set<String> colSet = new HashSet<String>();
    Map<String, ResultMapping> autoMappings = new HashMap<String, ResultMapping>();
    ResultSetMetaData rsmd = rs.getMetaData();
    for (int i = 1, n = rsmd.getColumnCount(); i <= n; i++) {
      boolean useLabel = mappedStatement.getConfiguration().isUseColumnLabel();
      String columnLabel = (useLabel ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i));
      columnLabel = columnLabel.toUpperCase();
      String propName = metaResultObject.findProperty(columnLabel);
      colSet.add(columnLabel);
      if (propName != null && metaResultObject.hasSetter(propName)) {
        propSet.add(propName);
        Class javaType = metaResultObject.getSetterType(propName);
        TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(javaType);
        ResultMapping resultMapping = new ResultMapping.Builder(configuration, propName, columnLabel, typeHandler)
            .javaType(javaType).build();
        autoMappings.put(propName, resultMapping);
      }
    }
    // Map results/ignore missing
    for (ResultMapping resultMapping : rm.getPropertyResultMappings()) {
      String propName = resultMapping.getProperty();
      String colName = resultMapping.getColumn();
      colName = colName == null ? null : colName.toUpperCase();
      autoMappings.remove(propName);
      if (colName == null || colSet.contains(colName)) {
        resultObject = processResult(rs, rm, resultMapping, lazyLoader, resultObject, foundValues);
        appliedResultMappings.add(resultMapping);
      }
    }
    // Automap remaining results
    for (String key : autoMappings.keySet()) {
      ResultMapping autoMapping = autoMappings.get(key);
      if (autoMapping.getTypeHandler() != null) {
        resultObject = processResult(rs, rm, autoMapping, lazyLoader, resultObject, foundValues);
        appliedResultMappings.add(autoMapping);
      }
    }
    return resultObject;
  }

  private Object createResultObject(ResultSet rs, ResultMap rm) throws SQLException {
    if (PlatformTypeHolder.isPlatformType(rm.getType())) {
      return new PlatformTypeHolder();
    }
    Object resultObject;
    if (rm.getConstructorResultMappings().size() > 0) {
      Map<String, Object> constructorArgs = new HashMap<String, Object>();
      List<Class> argTypes = new ArrayList<Class>();
      List<Object> argValues = new ArrayList<Object>();
      for (ResultMapping resultMapping : rm.getConstructorResultMappings()) {
        Object value = processResult(rs, rm, resultMapping, null, constructorArgs, new Reference(false));
        argTypes.add(resultMapping.getJavaType());
        argValues.add(value);
      }
      resultObject = objectFactory.create(rm.getType(), argTypes, argValues);
    } else {
      resultObject = objectFactory.create(rm.getType());
    }
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

  private Object processResult(ResultSet rs, ResultMap rm, ResultMapping resultMapping, ResultLoaderRegistry lazyLoader, Object resultObject, Reference<Boolean> foundValues) throws SQLException {
    if (resultMapping.getNestedQueryId() != null) {
      Configuration configuration = mappedStatement.getConfiguration();
      MappedStatement nestedQuery = configuration.getMappedStatement(resultMapping.getNestedQueryId());
      Class parameterType = nestedQuery.getParameterMap().getType();
      Object parameterObject = prepareNestedParameterObject(rs, resultMapping, parameterType);
      resultObject = processNestedSelectResult(nestedQuery, rm, resultMapping, lazyLoader, parameterObject, resultObject);
    } else if (resultMapping.getNestedResultMapId() == null) {
      resultObject = processSimpleResult(rs, rm, resultMapping, resultObject, foundValues);
    }
    return resultObject;
  }

  private Object processSimpleResult(ResultSet rs, ResultMap rm, ResultMapping resultMapping, Object resultObject, Reference<Boolean> foundValues) throws SQLException {
    MetaObject metaResultObject = MetaObject.forObject(resultObject);
    Object value = getPrimitiveResultMappingValue(rs, resultMapping);
    String property = resultMapping.getProperty();
    if (typeHandlerRegistry.hasTypeHandler(rm.getType()) || property == null) {
      resultObject = value;
    } else if (value != null) {
      metaResultObject.setValue(property, value);
    }
    foundValues.set(value != null || foundValues.get());
    return resultObject;
  }

  private Object processNestedJoinResults(ResultSet rs, List<ResultMapping> resultMappings, Object resultObject) {
    CacheKey previousKey = currentNestedKey;
    try {
      currentNestedKey = createUniqueResultKey(resultMappings, resultObject, currentNestedKey);
      if (nestedResultObjects.containsKey(currentNestedKey)) {
        // Unique key is already known, so get the existing result object and process additional results.
        resultObject = NO_VALUE;
      } else if (currentNestedKey != null) {
        // Unique key is NOT known, so create a new result object and then process additional results.
        nestedResultObjects.put(currentNestedKey, resultObject);
      }
      Object knownResultObject = nestedResultObjects.get(currentNestedKey);
      if (knownResultObject != null && knownResultObject != NO_VALUE) {
        for (ResultMapping resultMapping : resultMappings) {
          Configuration configuration = mappedStatement.getConfiguration();
          String nestedResultMapId = resultMapping.getNestedResultMapId();
          if (nestedResultMapId != null) {
            ResultMap nestedResultMap = configuration.getResultMap(nestedResultMapId);
            try {

              // get the discriminated submap if it exists
              nestedResultMap = resolveSubMap(rs, nestedResultMap);

              Class type = resultMapping.getJavaType();
              String propertyName = resultMapping.getProperty();

              MetaObject metaObject = MetaObject.forObject(knownResultObject);
              Object propertyValue = metaObject.getValue(propertyName);
              if (propertyValue == null) {
                if (type == null) {
                  type = metaObject.getSetterType(propertyName);
                }

                try {
                  // create the object if is it a Collection.  If not a Collection
                  // then we will just set the property to the object created
                  // in processing the nested result map
                  if (Collection.class.isAssignableFrom(type)) {
                    propertyValue = objectFactory.create(type);
                    metaObject.setValue(propertyName, propertyValue);
                  }
                } catch (Exception e) {
                  throw new ExecutorException("Error instantiating collection property for result '" + resultMapping.getProperty() + "'.  Cause: " + e, e);
                }
              }

              Reference<Boolean> foundValues = new Reference(false);
              Object nestedResultObject = loadResultObject(rs, nestedResultMap, foundValues);
              if (nestedResultObject != null && nestedResultObject != NO_VALUE) {
                if (propertyValue != null && propertyValue instanceof Collection) {
                  if (foundValues.get()) {
                    ((Collection) propertyValue).add(nestedResultObject);
                  }
                } else {
                  metaObject.setValue(propertyName, nestedResultObject);
                }
              }
            } catch (Exception e) {
              throw new ExecutorException("Error getting nested result map values for '" + resultMapping.getProperty() + "'.  Cause: " + e, e);
            }
          }
        }
      }
    } finally {
      currentNestedKey = previousKey;
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

  //////////////////////////////////////////
  // UTILITY METHODS
  //////////////////////////////////////////

  private ResultMap resolveSubMap(ResultSet rs, ResultMap rm) throws SQLException {
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

  private CacheKey createUniqueResultKey(List<ResultMapping> resultMappings, Object resultObject, CacheKey parentCacheKey) {
    if (resultObject == null) {
      return null;
    } else {
      MetaObject metaResultObject = MetaObject.forObject(resultObject);
      CacheKey cacheKey = new CacheKey();
      cacheKey.update(parentCacheKey);
      boolean updated = false;
      if (typeHandlerRegistry.hasTypeHandler(resultObject.getClass())) {
        cacheKey.update(resultObject);
      } else {
        for (ResultMapping resultMapping : resultMappings) {
          if (resultMapping.getNestedQueryId() == null) {
            String propName = resultMapping.getProperty();
            if (propName != null) {
              cacheKey.update(metaResultObject.getValue(propName));
              updated = true;
            }
          }
        }
      }
      return updated ? cacheKey : null;
    }
  }

  private ResultSet getFirstResultSet(Statement statement) throws SQLException {
    ResultSet rs = null;
    boolean hasMoreResults = true;
    while (hasMoreResults) {
      rs = statement.getResultSet();
      if (rs != null) {
        break;
      }
      // This is the messed up JDBC approach for determining if there are more results
      hasMoreResults = !((!moveToNextResultsSafely(statement)) && (statement.getUpdateCount() == -1));
    }
    return rs;
  }

  private boolean moveToNextResultsSafely(Statement statement) throws SQLException {
    if (mappedStatement.getConfiguration().isMultipleResultSetsEnabled()) {
      return statement.getMoreResults();
    }
    return false;
  }

  private void skipResults(ResultSet rs, int skipResults) throws SQLException {
    if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
      if (skipResults > 0) {
        rs.absolute(skipResults);
      }
    } else {
      for (int i = 0; i < skipResults; i++) {
        if (!rs.next()) break;
      }
    }
  }

  private void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        // ignore
      }
    }
  }

  private static class Reference<T> {
    private T value;

    private Reference(T value) {
      this.value = value;
    }

    public T get() {
      return value;
    }

    public void set(T value) {
      this.value = value;
    }
  }

}
