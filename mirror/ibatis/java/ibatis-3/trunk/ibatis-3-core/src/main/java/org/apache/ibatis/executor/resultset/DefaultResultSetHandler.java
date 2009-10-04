package org.apache.ibatis.executor.resultset;

import org.apache.ibatis.mapping.*;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.MetaClass;
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

public class DefaultResultSetHandler implements ResultSetHandler {

  private final Executor executor;
  private final Configuration configuration;
  private final MappedStatement mappedStatement;
  private final RowLimit rowLimit;
  private final ParameterHandler parameterHandler;
  private final ResultHandler resultHandler;
  private final BoundSql boundSql;
  private final TypeHandlerRegistry typeHandlerRegistry;
  private final ObjectFactory objectFactory;

  private final Map localRowValueCaches = new HashMap();
  private final Map globalRowValueCache = new HashMap();
  private static final CacheKey NULL_ROW_KEY = new CacheKey();

  public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql, int offset, int limit) {
    this.executor = executor;
    this.configuration = mappedStatement.getConfiguration();
    this.mappedStatement = mappedStatement;
    this.rowLimit = new RowLimit(offset, limit);
    this.parameterHandler = parameterHandler;
    this.boundSql = boundSql;
    this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    this.objectFactory = configuration.getObjectFactory();
    this.resultHandler = resultHandler;
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
      final ResultMap resultMap = configuration.getResultMap(resultMapId);
      final DefaultResultHandler resultHandler = new DefaultResultHandler();
      handleRowValues(rs, resultMap, resultHandler, new RowLimit());
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
      handleResultSet(rs, resultMap, multipleResults);
      rs = getNextResultSet(stmt);
      count++;
      globalRowValueCache.clear();
    }
    return collapseSingleResultList(multipleResults);
  }

  private void handleResultSet(ResultSet rs, ResultMap resultMap, List multipleResults) throws SQLException {
    if (resultHandler == null) {
      DefaultResultHandler defaultResultHandler = new DefaultResultHandler();
      handleRowValues(rs, resultMap, defaultResultHandler, rowLimit);
      multipleResults.add(defaultResultHandler.getResultList());
    } else {
      handleRowValues(rs, resultMap, resultHandler, rowLimit);
    }
  }

  private List collapseSingleResultList(List multipleResults) {
    if (multipleResults.size() == 1) {
      return (List) multipleResults.get(0);
    } else {
      return multipleResults;
    }
  }

  //
  // HANDLE ROWS
  //

  private void handleRowValues(ResultSet rs, ResultMap resultMap, ResultHandler resultHandler, RowLimit rowLimit) throws SQLException {
    final DefaultResultContext resultContext = new DefaultResultContext();
    skipRows(rs, rowLimit);
    while (shouldProcessMoreRows(rs, resultContext.getResultCount(), rowLimit)) {
      final ResultMap discriminatedResultMap = resolveDiscriminatedResultMap(rs, resultMap);
      final CacheKey rowKey = createRowKey(discriminatedResultMap, rs);
      final boolean knownValue = globalRowValueCache.containsKey(rowKey);
      Object rowValue = getRowValue(rs, discriminatedResultMap, rowKey);
      if (!knownValue) {
        resultContext.nextResultObject(rowValue);
        resultHandler.handleResult(resultContext);
      }
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
  // GET VALUE FROM ROW
  //

  private Object getRowValue(ResultSet rs, ResultMap resultMap, CacheKey rowKey) throws SQLException {
    if (globalRowValueCache.containsKey(rowKey)) {
      final Object resultObject = globalRowValueCache.get(rowKey);
      final MetaObject metaObject = MetaObject.forObject(resultObject);
      applyNestedResultMappings(rs, resultMap, metaObject);
      return resultObject;
    } else {
      final List<String> mappedColumnNames = new ArrayList<String>();
      final List<String> unmappedColumnNames = new ArrayList<String>();
      final ResultLoaderRegistry lazyLoader = instantiateResultLoaderRegistry();
      Object resultObject = createResultObject(rs, resultMap, lazyLoader);
      if (resultObject != null && !PlatformType.isPlatformType(resultMap.getType())) {
        final MetaObject metaObject = MetaObject.forObject(resultObject);
        loadMappedAndUnmappedColumnNames(rs, resultMap, mappedColumnNames, unmappedColumnNames);
        boolean foundValues = resultMap.getConstructorResultMappings().size() > 0;
        foundValues = applyPropertyMappings(rs, resultMap, mappedColumnNames, metaObject, lazyLoader) || foundValues;
        foundValues = applyAutomaticMappings(rs, unmappedColumnNames, metaObject) || foundValues;
        foundValues = applyNestedResultMappings(rs, resultMap, metaObject) || foundValues;
        resultObject = foundValues ? resultObject : null;
      }
      if (rowKey != NULL_ROW_KEY) {
        globalRowValueCache.put(rowKey, resultObject);
      }
      return resultObject;
    }
  }

  private ResultLoaderRegistry instantiateResultLoaderRegistry() {
    if (configuration.isLazyLoadingEnabled()) {
      return new ResultLoaderRegistry();
    } else {
      return null;
    }
  }

  //
  // PROPERTY MAPPINGS
  //

  private boolean applyPropertyMappings(ResultSet rs, ResultMap resultMap, List<String> mappedColumnNames, MetaObject metaObject, ResultLoaderRegistry lazyLoader) throws SQLException {
    boolean foundValues = false;
    final List<ResultMapping> propertyMappings = resultMap.getPropertyResultMappings();
    for (ResultMapping propertyMapping : propertyMappings) {
      final String column = propertyMapping.getColumn();
      if (propertyMapping.isCompositeResult() || (column != null && mappedColumnNames.contains(column.toUpperCase()))) {
        Object value = getPropertyMappingValue(rs, metaObject, propertyMapping, lazyLoader);
        if (value != null) {
          final String property = propertyMapping.getProperty();
          metaObject.setValue(property, value);
          foundValues = true;
        }
      }
    }
    return foundValues;
  }

  private Object getPropertyMappingValue(ResultSet rs, MetaObject metaResultObject, ResultMapping propertyMapping, ResultLoaderRegistry lazyLoader) throws SQLException {
    final TypeHandler typeHandler = propertyMapping.getTypeHandler();
    if (propertyMapping.getNestedQueryId() != null) {
      return getNestedQueryMappingValue(rs, metaResultObject, propertyMapping, lazyLoader);
    } else if (typeHandler != null) {
      final String column = propertyMapping.getColumn();
      return typeHandler.getResult(rs, column);
    }
    return null;
  }

  private boolean applyAutomaticMappings(ResultSet rs, List<String> unmappedColumnNames, MetaObject metaObject) throws SQLException {
    boolean foundValues = false;
    for (String columnName : unmappedColumnNames) {
      final String property = metaObject.findProperty(columnName);
      if (property != null) {
        final Class propertyType = metaObject.getSetterType(property);
        if (typeHandlerRegistry.hasTypeHandler(propertyType)) {
          final TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(propertyType);
          final Object value = typeHandler.getResult(rs, columnName);
          if (value != null) {
            metaObject.setValue(property, value);
            foundValues = true;
          }
        }
      }
    }
    return foundValues;
  }

  private void loadMappedAndUnmappedColumnNames(ResultSet rs, ResultMap resultMap, List<String> mappedColumnNames, List<String> unmappedColumnNames) throws SQLException {
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
    if (resultObject != null && configuration.isLazyLoadingEnabled()) {
      return ResultObjectProxy.createProxy(resultMap.getType(), resultObject, lazyLoader);
    }
    return resultObject;
  }

  private Object createResultObject(ResultSet rs, ResultMap resultMap) throws SQLException {
    final Class resultType = resultMap.getType();
    final List<ResultMapping> constructorMappings = resultMap.getConstructorResultMappings();
    if (PlatformType.isPlatformType(resultType)) {
      return createPrimitiveResultObject(rs, resultMap);
    } else if (constructorMappings.size() > 0) {
      return createParameterizedResultObject(rs, resultType, constructorMappings);
    } else {
      return objectFactory.create(resultType);
    }
  }

  private Object createParameterizedResultObject(ResultSet rs, Class resultType, List<ResultMapping> constructorMappings) throws SQLException {
    boolean foundValues = false;
    final List<Class> parameterTypes = new ArrayList<Class>();
    final List<Object> parameterValues = new ArrayList<Object>();
    for (ResultMapping constructorMapping : constructorMappings) {
      final Class parameterType = constructorMapping.getJavaType();
      final TypeHandler typeHandler = constructorMapping.getTypeHandler();
      final String column = constructorMapping.getColumn();
      final Object value = typeHandler.getResult(rs, column);
      parameterTypes.add(parameterType);
      parameterValues.add(value);
      foundValues = value != null || foundValues;
    }
    return foundValues ? objectFactory.create(resultType, parameterTypes, parameterValues) : null;
  }

  private Object createPrimitiveResultObject(ResultSet rs, ResultMap resultMap) throws SQLException {
    final Class resultType = resultMap.getType();
    final String columnName;
    if (resultMap.getResultMappings().size() > 0) {
      final List<ResultMapping> resultMappingList = resultMap.getResultMappings();
      final ResultMapping mapping = resultMappingList.get(0);
      columnName = mapping.getColumn();
    } else {
      final ResultSetMetaData rsmd = rs.getMetaData();
      columnName = configuration.isUseColumnLabel() ? rsmd.getColumnLabel(1) : rsmd.getColumnName(1);
    }
    final TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(resultType);
    return typeHandler.getResult(rs, columnName);
  }

  //
  // NESTED QUERY
  //

  private Object getNestedQueryMappingValue(ResultSet rs, MetaObject metaResultObject, ResultMapping propertyMapping, ResultLoaderRegistry lazyLoader) throws SQLException {
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
    return value;
  }

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

  private boolean applyNestedResultMappings(ResultSet rs, ResultMap resultMap, MetaObject metaObject) {
    boolean foundValues = false;
    for (ResultMapping resultMapping : resultMap.getPropertyResultMappings()) {
      final String nestedResultMapId = resultMapping.getNestedResultMapId();
      if (nestedResultMapId != null) {
        try {
          final ResultMap nestedResultMap = getNestedResultMap(rs, nestedResultMapId);
          final Object collectionProperty = instantiateCollectionPropertyIfAppropriate(resultMapping, metaObject);

          final CacheKey parentRowKey = createRowKey(resultMap, rs);
          final CacheKey rowKey = createRowKey(nestedResultMap, rs);
          final Set localRowValueCache = getRowValueCache(parentRowKey);
          final boolean knownValue = localRowValueCache .contains(rowKey);
          localRowValueCache.add(rowKey);
          Object rowValue = getRowValue(rs, nestedResultMap, rowKey);

          if (rowValue != null && rowValue != NO_VALUE) {
            if (collectionProperty != null && collectionProperty instanceof Collection) {
              if (!knownValue) {
                ((Collection) collectionProperty).add(rowValue);
              }
            } else {
              metaObject.setValue(resultMapping.getProperty(), rowValue);
            }
            foundValues = true;
          }

        } catch (Exception e) {
          throw new ExecutorException("Error getting nested result map values for '" + resultMapping.getProperty() + "'.  Cause: " + e, e);
        }
      }
    }
    return foundValues;
  }

  private Set getRowValueCache(CacheKey rowKey) {
    Set cache = (Set) localRowValueCaches.get(rowKey);
    if (cache == null) {
      cache = new HashSet();
      localRowValueCaches.put(rowKey,cache);
    }
    return cache;
  }

  private Object instantiateCollectionPropertyIfAppropriate(ResultMapping resultMapping, MetaObject metaObject) {
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

  private CacheKey createRowKey(ResultMap resultMap, ResultSet rs) throws SQLException {
    final CacheKey cacheKey = new CacheKey();
    List<ResultMapping> resultMappings = getResultMappingsForRowKey(resultMap);
    cacheKey.update(resultMap.getId());
    if (resultMappings.size() == 0) {
      if (Map.class.isAssignableFrom(resultMap.getType())) {
        createRowKeyForMap(rs, cacheKey);
      } else {
        createRowKeyForUnmappedProperties(resultMap, rs, cacheKey);
      }
    } else {
      createRowKeyForMappedProperties(rs, cacheKey, resultMappings);
    }
    if (cacheKey.getUpdateCount() < 2) {
      return NULL_ROW_KEY;
    }
    return cacheKey;
  }

  private List<ResultMapping> getResultMappingsForRowKey(ResultMap resultMap) {
    List<ResultMapping> resultMappings = resultMap.getIdResultMappings();
    if (resultMappings.size() == 0) {
      resultMappings = resultMap.getPropertyResultMappings();
    }
    return resultMappings;
  }

  private void createRowKeyForMappedProperties(ResultSet rs, CacheKey cacheKey, List<ResultMapping> resultMappings) {
    for (ResultMapping resultMapping : resultMappings) {
      if (resultMapping.getNestedQueryId() == null && resultMapping.getNestedResultMapId() == null) {
        final String column = resultMapping.getColumn();
        final TypeHandler th = resultMapping.getTypeHandler();
        if (column != null) {
          try {
            final Object value = th.getResult(rs, column);
            if (value != null) {
              cacheKey.update(column);
              cacheKey.update(value);
            }
          } catch (Exception e) {
            //ignore
          }
        }
      }
    }
  }

  private void createRowKeyForUnmappedProperties(ResultMap resultMap, ResultSet rs, CacheKey cacheKey) throws SQLException {
    final MetaClass metaType = MetaClass.forClass(resultMap.getType());
    final List<String> mappedColumnNames = new ArrayList<String>();
    final List<String> unmappedColumnNames = new ArrayList<String>();
    loadMappedAndUnmappedColumnNames(rs, resultMap, mappedColumnNames, unmappedColumnNames);
    for (String column : unmappedColumnNames) {
      if (metaType.findProperty(column) != null) {
        String value = rs.getString(column);
        if (value != null) {
          cacheKey.update(column);
          cacheKey.update(value);
        }
      }
    }
  }

  private void createRowKeyForMap(ResultSet rs, CacheKey cacheKey) throws SQLException {
    final ResultSetMetaData rsmd = rs.getMetaData();
    final int columnCount = rsmd.getColumnCount();
    for (int i = 1; i <= columnCount; i++) {
      final String columnName = configuration.isUseColumnLabel() ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i);
      final String value = rs.getString(columnName);
      if (value != null) {
        cacheKey.update(columnName);
        cacheKey.update(value);
      }
    }
  }

}