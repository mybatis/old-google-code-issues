package org.apache.ibatis.executor.resultset;

import org.apache.ibatis.mapping.*;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.Executor;
import static org.apache.ibatis.executor.resultset.NoValue.NO_VALUE;
import org.apache.ibatis.executor.loader.ResultLoader;
import org.apache.ibatis.executor.loader.ResultLoaderRegistry;
import org.apache.ibatis.executor.loader.ResultObjectProxy;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.result.DefaultResultHandler;
import org.apache.ibatis.executor.result.ResultHandler;
import org.apache.ibatis.executor.result.DefaultResultContext;
import org.apache.ibatis.cache.CacheKey;

import java.util.*;
import java.sql.*;

public class NewResultSetHandler implements ResultSetHandler {

  private Executor executor;
  private final Configuration configuration;
  private final MappedStatement mappedStatement;
  private final RowLimit rowLimit;
  private final ParameterHandler parameterHandler;
  private final ResultHandler resultHandler;
  private final BoundSql boundSql;
  private final TypeHandlerRegistry typeHandlerRegistry;
  private final ObjectFactory objectFactory;

  public NewResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql, int offset, int limit) {
    this.executor = executor;
    this.configuration = mappedStatement.getConfiguration();
    this.mappedStatement = mappedStatement;
    this.rowLimit = new RowLimit(offset, limit);
    this.parameterHandler = parameterHandler;
    this.resultHandler = resultHandler;
    this.boundSql = boundSql;
    this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    this.objectFactory = configuration.getObjectFactory();
  }

  //
  // HANDLE OUTPUT PARAMETER
  //

  public void handleOutputParameters(CallableStatement cs) throws SQLException {
    final Object parameterObject = parameterHandler.getParameterObject();
    final MetaObject metaParam = MetaObject.forObject(parameterObject);
    final List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    for (int i = 0; i < parameterMappings.size(); i++) {
      final ParameterMapping parameterMapping = parameterMappings.get(i);
      if (parameterMapping.getMode() == ParameterMode.OUT || parameterMapping.getMode() == ParameterMode.INOUT) {
        if ("java.sql.ResultSet".equalsIgnoreCase(parameterMapping.getJavaType().getName())) {
          handleRefCursorOutputParameter(cs, parameterMapping, i, metaParam);
        } else {
          metaParam.setValue(parameterMapping.getProperty(), parameterMapping.getTypeHandler().getResult(cs, i + 1));
        }
      }
    }
  }

  private void handleRefCursorOutputParameter(CallableStatement cs, ParameterMapping parameterMapping, int parameterMappingIndex, MetaObject metaParam) throws SQLException {
    final ResultSet rs = (ResultSet) cs.getObject(parameterMappingIndex + 1);
    final String resultMapId = parameterMapping.getResultMapId();
    if (resultMapId != null) {
      final ResultMap resultMap = mappedStatement.getConfiguration().getResultMap(resultMapId);
      final DefaultResultHandler resultHandler = new DefaultResultHandler();
      handleResultSet(rs, resultMap, resultHandler, new RowLimit());
      metaParam.setValue(parameterMapping.getProperty(), resultHandler.getResultList());
    } else {
      throw new ExecutorException("Parameter requires ResultMap for output types of java.sql.ResultSet");
    }
    rs.close();
  }

  //
  // HANDLE RESULT SETS
  //

  public List handleResultSets(Statement stmt) throws SQLException {
    final List multipleResults = new ArrayList();
    final List<ResultMap> resultMaps = mappedStatement.getResultMaps();
    int count = 0;
    ResultSet rs = stmt.getResultSet();
    while (rs != null) {
      final ResultMap resultMap = resultMaps.get(count);
      if (resultHandler == null) {
        DefaultResultHandler defaultResultHandler = new DefaultResultHandler();
        handleResultSet(rs, resultMap, defaultResultHandler, rowLimit);
        multipleResults.add(defaultResultHandler.getResultList());
      } else {
        handleResultSet(rs, resultMap, resultHandler, rowLimit);
      }
      rs = getNextResultSet(stmt);
      count++;
    }
    return collapseSingleResultList(multipleResults);
  }

  private List collapseSingleResultList(List multipleResults) {
    if (multipleResults.size() == 1) {
      return (List) multipleResults.get(0);
    } else {
      return multipleResults;
    }
  }

  private void handleResultSet(ResultSet rs, ResultMap resultMap, ResultHandler resultHandler, RowLimit rowLimit) throws SQLException {
    final DefaultResultContext resultContext = new DefaultResultContext();
    final List<String> mappedColumnNames = new ArrayList<String>();
    final List<String> unmappedColumnNames = new ArrayList<String>();
    skipRows(rs, rowLimit);
    while (shouldProcessMoreRows(rs, resultContext.getResultCount(), rowLimit)) {
      final ResultMap discriminatedResultMap = resolveDiscriminatedResultMap(rs, resultMap);
      final ResultLoaderRegistry lazyLoader = instantiateResultLoaderRegistry();
      final Object resultObject = createResultObject(rs, discriminatedResultMap, lazyLoader);
      if (!typeHandlerRegistry.hasTypeHandler(resultMap.getType())) {
        final MetaObject metaObject = MetaObject.forObject(resultObject);
        getMappedAndUnmappedColumnNames(rs, discriminatedResultMap, mappedColumnNames, unmappedColumnNames);
        applyPropertyMappings(rs, discriminatedResultMap, mappedColumnNames, metaObject, lazyLoader);
        applyAutomaticMappings(rs, unmappedColumnNames, metaObject);
        processNestedJoinResults(rs, resultMap, resultObject);
      }
      resultContext.nextResultObject(resultObject);
      resultHandler.handleResult(resultContext);
    }
  }

  private ResultLoaderRegistry instantiateResultLoaderRegistry() {
    if (configuration.isLazyLoadingEnabled()) {
      return new ResultLoaderRegistry();
    } else {
      return null;
    }
  }

  private boolean shouldProcessMoreRows(ResultSet rs, int count, RowLimit rowLimit) throws SQLException {
    return rs.next() && count < rowLimit.getLimit();
  }

  private void skipRows(ResultSet rs, RowLimit rowLimit) throws SQLException {
    if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
      rs.absolute(rowLimit.getOffset());
    } else {
      for (int i = 0; i < rowLimit.getOffset(); i++) rs.next();
    }
  }

  private ResultSet getNextResultSet(Statement stmt) throws SQLException {
    // Making this method tolerant of bad JDBC drivers
    try {
      if (stmt.getConnection().getMetaData().supportsMultipleResultSets()) {
        // Crazy Standard JDBC way of determining if there are more results
        if (!((!stmt.getMoreResults()) && (stmt.getUpdateCount() == -1))) {
          return stmt.getResultSet();
        }
      }
    } catch (Exception e) {
      // Intentionally ignored.
    }
    return null;
  }

  //
  // PROPERTY MAPPINGS
  //

  private void applyPropertyMappings(ResultSet rs, ResultMap resultMap, List<String> mappedColumnNames, MetaObject metaObject, ResultLoaderRegistry lazyLoader) throws SQLException {
    final List<ResultMapping> propertyMappings = resultMap.getPropertyResultMappings();
    for (ResultMapping propertyMapping : propertyMappings) {
      final String column = propertyMapping.getColumn();
      if (propertyMapping.isCompositeResult() || (column != null && mappedColumnNames.contains(column.toUpperCase()))) {
        final TypeHandler typeHandler = propertyMapping.getTypeHandler();
        if (propertyMapping.getNestedQueryId() != null) {
          applyNestedQueryMapping(rs, metaObject, resultMap, propertyMapping, lazyLoader);
        } else if (typeHandler != null) {
          applySimplePropertyMapping(rs, metaObject, propertyMapping);
        }
      }
    }
  }

  private void applySimplePropertyMapping(ResultSet rs, MetaObject metaObject, ResultMapping propertyMapping) throws SQLException {
    final TypeHandler typeHandler = propertyMapping.getTypeHandler();
    final String column = propertyMapping.getColumn();
    final String property = propertyMapping.getProperty();
    final Object value = typeHandler.getResult(rs, column);
    metaObject.setValue(property, value);
  }

  private void applyNestedQueryMapping(ResultSet rs, MetaObject metaResultObject, ResultMap resultMap, ResultMapping propertyMapping, ResultLoaderRegistry lazyLoader) throws SQLException {
    final String nestedQueryId = propertyMapping.getNestedQueryId();
    final String property = propertyMapping.getProperty();
    final MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
    final Class nestedQueryParameterType = nestedQuery.getParameterMap().getType();
    final Object nestedQueryParameterObject = prepareParameterForNestedQuery(rs, propertyMapping, nestedQueryParameterType);

    Object value = null;
    if (nestedQueryParameterObject != null) {
      final CacheKey key = executor.createCacheKey(nestedQuery, nestedQueryParameterObject, RowLimit.NO_ROW_OFFSET, RowLimit.NO_ROW_LIMIT);
      if (executor.isCached(nestedQuery, key)) {
        executor.deferLoad(nestedQuery, metaResultObject, property, key);
      } else {
        final ResultLoader resultLoader = new ResultLoader(configuration, executor, nestedQuery, nestedQueryParameterObject, propertyMapping.getJavaType());
        if (configuration.isLazyLoadingEnabled()) {
          lazyLoader.registerLoader(property, metaResultObject, resultLoader);
        } else {
          value = resultLoader.loadResult();
        }
      }
    }
    if (typeHandlerRegistry.hasTypeHandler(resultMap.getType())) {
      //resultObject = value;
    } else if (value != null) {
      metaResultObject.setValue(property, value);
    }
  }

  private void applyAutomaticMappings(ResultSet rs, List<String> unmappedColumnNames, MetaObject metaObject) throws SQLException {
    for (String columnName : unmappedColumnNames) {
      final String property = metaObject.findProperty(columnName);
      if (property != null) {
        final Class propertyType = metaObject.getSetterType(property);
        if (typeHandlerRegistry.hasTypeHandler(propertyType)) {
          final TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(propertyType);
          final Object value = typeHandler.getResult(rs, columnName);
          metaObject.setValue(property, value);
        }
      }
    }
  }

  private void getMappedAndUnmappedColumnNames(ResultSet rs, ResultMap resultMap, List<String> mappedColumnNames, List<String> unmappedColumnNames) throws SQLException {
    mappedColumnNames.clear();
    unmappedColumnNames.clear();
    final ResultSetMetaData rsmd = rs.getMetaData();
    final int columnCount = rsmd.getColumnCount();
    final Set<String> mappedColumns = resultMap.getMappedColumns();
    for (int i = 1; i <= columnCount; i++) {
      final String columnName = configuration.isUseColumnLabel() ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i);
      final String upperColumnName = columnName.toUpperCase();
      if (mappedColumns.contains(upperColumnName)) {
        mappedColumnNames.add(columnName);
      } else {
        unmappedColumnNames.add(columnName);
      }
    }
  }

  //
  // INSTANTIATION & CONSTRUCTOR MAPPING
  //

  private Object createResultObject(ResultSet rs, ResultMap resultMap, ResultLoaderRegistry lazyLoader) throws SQLException {
    final Object resultObject = createResultObject(rs, resultMap);
    if (configuration.isLazyLoadingEnabled()) {
      return ResultObjectProxy.createProxy(resultMap.getType(), resultObject, lazyLoader);
    }
    return resultObject;
  }

  private Object createResultObject(ResultSet rs, ResultMap resultMap) throws SQLException {
    final Class resultType = resultMap.getType();
    final List<ResultMapping> constructorMappings = resultMap.getConstructorResultMappings();
    if (typeHandlerRegistry.hasTypeHandler(resultType)) {
      return createPrimitiveResultObject(rs, resultType);
    } else if (constructorMappings.size() > 0) {
      return createParameterizedResultObject(rs, resultType, constructorMappings);
    } else {
      return objectFactory.create(resultType);
    }
  }

  private Object createParameterizedResultObject(ResultSet rs, Class resultType, List<ResultMapping> constructorMappings) throws SQLException {
    final List<Class> parameterTypes = new ArrayList<Class>();
    final List<Object> parameterValues = new ArrayList<Object>();
    for (ResultMapping constructorMapping : constructorMappings) {
      final Class parameterType = constructorMapping.getJavaType();
      final TypeHandler typeHandler = constructorMapping.getTypeHandler();
      final String column = constructorMapping.getColumn();
      final Object value = typeHandler.getResult(rs, column);
      parameterTypes.add(parameterType);
      parameterValues.add(value);
    }
    return objectFactory.create(resultType, parameterTypes, parameterValues);
  }

  private Object createPrimitiveResultObject(ResultSet rs, Class resultType) throws SQLException {
    final ResultSetMetaData rsmd = rs.getMetaData();
    final String columnName = configuration.isUseColumnLabel() ? rsmd.getColumnLabel(1) : rsmd.getColumnName(1);
    final TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(resultType);
    return typeHandler.getResult(rs, columnName);
  }

  //
  // NESTED QUERY
  //

  private Object prepareParameterForNestedQuery(ResultSet rs, ResultMapping resultMapping, Class parameterType) throws SQLException {
    if (resultMapping.isCompositeResult()) {
      return prepareCompositeKeyParameter(rs, resultMapping, parameterType);
    } else {
      return prepareSimpleKeyParameter(rs, resultMapping, parameterType);
    }
  }

  private Object prepareSimpleKeyParameter(ResultSet rs, ResultMapping resultMapping, Class parameterType) throws SQLException {
    final TypeHandler typeHandler;
    if (typeHandlerRegistry.hasTypeHandler(parameterType)) {
      typeHandler = typeHandlerRegistry.getTypeHandler(parameterType);
    } else {
      typeHandler = typeHandlerRegistry.getUnkownTypeHandler();
    }
    return typeHandler.getResult(rs, resultMapping.getColumn());
  }

  private Object prepareCompositeKeyParameter(ResultSet rs, ResultMapping resultMapping, Class parameterType) throws SQLException {
    final Object parameterObject = instantiateParameterObject(parameterType);
    final MetaObject metaObject = MetaObject.forObject(parameterObject);
    for (ResultMapping innerResultMapping : resultMapping.getComposites()) {
      final Class propType = metaObject.getSetterType(innerResultMapping.getProperty());
      final TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(propType);
      final Object propValue = typeHandler.getResult(rs, innerResultMapping.getColumn());
      metaObject.setValue(innerResultMapping.getProperty(), propValue);
    }
    return parameterObject;
  }

  private Object instantiateParameterObject(Class parameterType) {
    if (parameterType == null) {
      return new HashMap();
    } else {
      return objectFactory.create(parameterType);
    }
  }

  //
  // DISCRIMINATOR
  //

  public ResultMap resolveDiscriminatedResultMap(ResultSet rs, ResultMap resultMap) throws SQLException {
    final Discriminator discriminator = resultMap.getDiscriminator();
    if (discriminator != null) {
      final Object value = getDiscriminatorValue(rs, discriminator);
      final String discriminatedMapId = discriminator.getMapIdFor(String.valueOf(value));
      if (configuration.hasResultMap(discriminatedMapId)) {
        return configuration.getResultMap(discriminatedMapId);
      }
    }
    return resultMap;
  }

  private Object getDiscriminatorValue(ResultSet rs, Discriminator discriminator) throws SQLException {
    final ResultMapping resultMapping = discriminator.getResultMapping();
    final TypeHandler typeHandler = resultMapping.getTypeHandler();
    if (typeHandler != null) {
      return typeHandler.getResult(rs, resultMapping.getColumn());
    } else {
      throw new ExecutorException("No type handler could be found to map the property '" + resultMapping.getProperty() + "' to the column '" + resultMapping.getColumn() + "'.  One or both of the types, or the combination of types is not supported.");
    }
  }

  //
  // NESTED RESULT MAP (JOIN MAPPING)
  //

  private Map nestedResultObjects = new HashMap();
  private CacheKey currentNestedKey;

  private Object processNestedJoinResults(ResultSet rs, ResultMap resultMap, Object resultObject) {
    CacheKey previousKey = currentNestedKey;
    try {
      currentNestedKey = createUniqueResultKey(resultMap, resultObject);
      if (nestedResultObjects.containsKey(currentNestedKey)) {
        // Unique key is already known, so get the existing result object and process additional results.
        resultObject = NO_VALUE;
      } else if (currentNestedKey != null) {
        // Unique key is NOT known, so create a new result object and then process additional results.
        nestedResultObjects.put(currentNestedKey, resultObject);
      }
      Object knownResultObject = nestedResultObjects.get(currentNestedKey);
      if (knownResultObject != null && resultObject != NO_VALUE) {
        applyNestedResultMappings(rs, resultMap, knownResultObject);
      }
      return resultObject;

    } finally {
      currentNestedKey = previousKey;
    }
  }

  private void applyNestedResultMappings(ResultSet rs, ResultMap resultMap, Object resultObject) {
    for (ResultMapping resultMapping : resultMap.getPropertyResultMappings()) {
      final String nestedResultMapId = resultMapping.getNestedResultMapId();
      if (nestedResultMapId != null) {
        try {
          final ResultMap nestedResultMap = getNestedResultMap(rs, nestedResultMapId);
          final MetaObject metaObject = MetaObject.forObject(resultObject);
          final Object propertyValue = getPropertyValue(resultMapping, metaObject);
          final Reference<Boolean> foundValues = new Reference(false);

          final DefaultResultHandler defaultResultHandler = new DefaultResultHandler();
          handleResultSet(rs, nestedResultMap, defaultResultHandler, new RowLimit());
          final List nestedResults = defaultResultHandler.getResultList();

          if (propertyValue != null && propertyValue instanceof Collection) {
            if (foundValues.get()) {
              ((Collection) propertyValue).addAll(nestedResults);
            }
          } else {
            if (nestedResults.size() == 1) {
              final Object nestedResultObject = nestedResults.get(0);
              metaObject.setValue(resultMapping.getProperty(), nestedResultObject);
            } else {
              throw new ExecutorException("Expected exactly 1 or 0 results for '" + resultMapping.getProperty() + "', but found "+nestedResults.size()+".");
            }
          }

        } catch (Exception e) {
          throw new ExecutorException("Error getting nested result map values for '" + resultMapping.getProperty() + "'.  Cause: " + e, e);
        }
      }
    }
  }

  private Object getPropertyValue(ResultMapping resultMapping, MetaObject metaObject) {
    final String propertyName = resultMapping.getProperty();
    Class type = resultMapping.getJavaType();
    Object propertyValue = metaObject.getValue(propertyName);
    if (propertyValue == null) {
      if (type == null) {
        type = metaObject.getSetterType(propertyName);
      }
      try {
        if (Collection.class.isAssignableFrom(type)) {
          propertyValue = objectFactory.create(type);
          metaObject.setValue(propertyName, propertyValue);
        }
      } catch (Exception e) {
        throw new ExecutorException("Error instantiating collection property for result '" + resultMapping.getProperty() + "'.  Cause: " + e, e);
      }
    }
    return propertyValue;
  }

  private ResultMap getNestedResultMap(ResultSet rs, String nestedResultMapId) throws SQLException {
    ResultMap nestedResultMap = configuration.getResultMap(nestedResultMapId);
    nestedResultMap = resolveDiscriminatedResultMap(rs, nestedResultMap);
    return nestedResultMap;
  }

  //
  // UNIQUE RESULT KEY
  //

  private CacheKey createUniqueResultKey(ResultMap resultMap, Object resultObject) {
    if (resultObject == null) {
      return null;
    } else {
      return createCacheKeyForResultObject(resultMap, resultObject);
    }
  }

  private CacheKey createCacheKeyForResultObject(ResultMap resultMap, Object resultObject) {
    final CacheKey cacheKey = new CacheKey();
    cacheKey.update(resultMap.getType().getName());
    if (typeHandlerRegistry.hasTypeHandler(resultObject.getClass())) {
      cacheKey.update(resultObject);
    } else {
      updateCacheKeyForComplexResultObject(resultMap, resultObject, cacheKey);
    }
    return cacheKey;
  }

  private void updateCacheKeyForComplexResultObject(ResultMap resultMap, Object resultObject, CacheKey cacheKey) {
    final MetaObject metaResultObject = MetaObject.forObject(resultObject);
    for (ResultMapping resultMapping : resultMap.getIdResultMappings()) {
      if (resultMapping.getNestedQueryId() == null && resultMapping.getNestedResultMapId() == null) {
        final String propName = resultMapping.getProperty();
        if (propName != null) {
          final Object value = metaResultObject.getValue(propName);
          if (value != null) {
            cacheKey.update(propName);
            cacheKey.update(value);
          }
        }
      }
    }
  }


}
