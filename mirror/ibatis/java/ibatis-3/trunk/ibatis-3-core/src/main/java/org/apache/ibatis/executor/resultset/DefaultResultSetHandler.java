package org.apache.ibatis.executor.resultset;

import static org.apache.ibatis.executor.resultset.NoValue.*;
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

  private final Configuration configuration;
  private final ObjectFactory objectFactory;
  private final TypeHandlerRegistry typeHandlerRegistry;
  private final MappedStatement mappedStatement;
  private final int rowOffset;
  private final int rowLimit;
  private final Object parameterObject;

  private final ResultHandler resultHandler;
  private final BoundSql boundSql;
  private final NestedSelectHandler nestedSelectHandler;
  private final NestedResultSetHandler nestedResultSetHandler;
  private final DiscriminatorHandler discriminatorHandler;

  public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql, int rowOffset, int rowLimit) {
    this.configuration = mappedStatement.getConfiguration();
    this.objectFactory = mappedStatement.getConfiguration().getObjectFactory();
    this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    this.mappedStatement = mappedStatement;
    this.rowOffset = rowOffset;
    this.rowLimit = rowLimit;
    this.parameterObject = parameterHandler.getParameterObject();
    this.resultHandler = resultHandler;
    this.boundSql = boundSql;

    this.nestedResultSetHandler = new NestedResultSetHandler(configuration,mappedStatement,this);
    this.nestedSelectHandler = new NestedSelectHandler(executor,configuration,mappedStatement);
    this.discriminatorHandler = new DiscriminatorHandler(mappedStatement);
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
            nestedResultSetHandler.reset();
            rs = statement.getResultSet();
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
      DefaultResultContext context = new DefaultResultContext();
      while ((maxResults == Executor.NO_ROW_LIMIT || context.getResultCount() < maxResults)
          && !context.isStopped() && rs.next()) {
        ResultMap rm = discriminatorHandler.resolveSubMap(rs, resultMap);
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

  public Object loadResultObject(ResultSet rs, ResultMap rm, Reference<Boolean> foundValues) throws SQLException {
    if (rm.getType() == null) {
      throw new ExecutorException("The result class was null when trying to get results for ResultMap " + rm.getId());
    }
    Object resultObject = createResultObject(rs, rm);
    ResultLoaderRegistry lazyLoader = null;
    if (this.mappedStatement.getConfiguration().isLazyLoadingEnabled()) {
      lazyLoader = new ResultLoaderRegistry();
      resultObject = ResultObjectProxy.createProxy(rm.getType(), resultObject, lazyLoader);
    }
    List<ResultMapping> appliedResultMappings = new ArrayList<ResultMapping>();
    resultObject = mapResults(rs, rm, lazyLoader, resultObject, appliedResultMappings, foundValues);
    resultObject = nestedResultSetHandler.processNestedJoinResults(rs, appliedResultMappings, resultObject);
    return resultObject;
  }

  private Object mapResults(ResultSet rs, ResultMap rm, ResultLoaderRegistry lazyLoader, Object resultObject, List<ResultMapping> appliedResultMappings, Reference<Boolean> foundValues) throws SQLException {
    Set<String> colSet = new HashSet<String>();
    Map<String, ResultMapping> autoMappings = createResultMappingsForColumnsThatMatchPropertyNames(rs, resultObject, colSet);
    // Map results/ignore missing
    resultObject = mapSpecifiedResultsIgnoringMissingColumns(rs, rm, lazyLoader, resultObject, appliedResultMappings, foundValues, colSet, autoMappings);
    // Automap remaining results
    resultObject = automaticallyMapRemainingColumnsThatMatchPropertyNames(rs, rm, lazyLoader, resultObject, appliedResultMappings, foundValues, autoMappings);
    return resultObject;
  }

  private Map<String, ResultMapping> createResultMappingsForColumnsThatMatchPropertyNames(ResultSet rs, Object resultObject, Set<String> colSet) throws SQLException {
    MetaObject metaResultObject = MetaObject.forObject(resultObject);
    Map<String, ResultMapping> autoMappings = new HashMap<String, ResultMapping>();
    ResultSetMetaData rsmd = rs.getMetaData();
    for (int i = 1, n = rsmd.getColumnCount(); i <= n; i++) {
      boolean useLabel = mappedStatement.getConfiguration().isUseColumnLabel();
      String columnLabel = (useLabel ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i));
      columnLabel = columnLabel.toUpperCase();
      String propName = metaResultObject.findProperty(columnLabel);
      colSet.add(columnLabel);
      if (propName != null && metaResultObject.hasSetter(propName)) {
        Class javaType = metaResultObject.getSetterType(propName);
        TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(javaType);
        ResultMapping resultMapping = new ResultMapping.Builder(configuration, propName, columnLabel, typeHandler)
            .javaType(javaType).build();
        autoMappings.put(propName, resultMapping);
      }
    }
    return autoMappings;
  }

  private Object automaticallyMapRemainingColumnsThatMatchPropertyNames(ResultSet rs, ResultMap rm, ResultLoaderRegistry lazyLoader, Object resultObject, List<ResultMapping> appliedResultMappings, Reference<Boolean> foundValues, Map<String, ResultMapping> autoMappings) throws SQLException {
    for (String key : autoMappings.keySet()) {
      ResultMapping autoMapping = autoMappings.get(key);
      if (autoMapping.getTypeHandler() != null) {
        resultObject = processResult(rs, rm, autoMapping, lazyLoader, resultObject, foundValues);
        appliedResultMappings.add(autoMapping);
      }
    }
    return resultObject;
  }

  private Object mapSpecifiedResultsIgnoringMissingColumns(ResultSet rs, ResultMap rm, ResultLoaderRegistry lazyLoader, Object resultObject, List<ResultMapping> appliedResultMappings, Reference<Boolean> foundValues, Set<String> colSet, Map<String, ResultMapping> autoMappings) throws SQLException {
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

  private Object processResult(ResultSet rs, ResultMap rm, ResultMapping resultMapping, ResultLoaderRegistry lazyLoader, Object resultObject, Reference<Boolean> foundValues) throws SQLException {
    if (resultMapping.getNestedQueryId() != null) {
      resultObject = nestedSelectHandler.processNestedSelect(rs, rm, resultMapping, lazyLoader, resultObject);
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


  //////////////////////////////////////////
  // UTILITY METHODS
  //////////////////////////////////////////


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


}
