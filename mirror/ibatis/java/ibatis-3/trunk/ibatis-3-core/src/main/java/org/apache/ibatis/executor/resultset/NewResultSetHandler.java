package org.apache.ibatis.executor.resultset;

import org.apache.ibatis.mapping.*;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.executor.resultset.RowLimit;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.result.DefaultResultHandler;
import org.apache.ibatis.executor.result.ResultHandler;
import org.apache.ibatis.executor.result.ResultContext;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.sql.*;

public class NewResultSetHandler implements ResultSetHandler {

  private final Configuration configuration;
  private final MappedStatement mappedStatement;
  private final RowLimit rowLimit;
  private final ParameterHandler parameterHandler;
  private final ResultHandler resultHandler;
  private final BoundSql boundSql;
  private TypeHandlerRegistry typeHandlerRegistry;


  public NewResultSetHandler(Configuration configuration, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql, int offset, int limit) {
    this.configuration = configuration;
    this.mappedStatement = mappedStatement;
    this.rowLimit = new RowLimit(offset, limit);
    this.parameterHandler = parameterHandler;
    this.resultHandler = resultHandler;
    this.boundSql = boundSql;
    this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
  }

  public void handleOutputParameters(CallableStatement cs) throws SQLException {
    ErrorContext.instance().activity("handling output parameters");
    final Object parameterObject = parameterHandler.getParameterObject();
    final MetaObject metaParam = MetaObject.forObject(parameterObject);
    final List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    for (int i = 0; i < parameterMappings.size(); i++) {
      final ParameterMapping parameterMapping = parameterMappings.get(i);
      if (parameterMapping.getMode() == ParameterMode.OUT || parameterMapping.getMode() == ParameterMode.INOUT) {
        if ("java.sql.ResultSet".equalsIgnoreCase(parameterMapping.getJavaType().getName())) {
          handleResultSetOutputParameter(cs, parameterMapping, i, metaParam);
        } else {
          metaParam.setValue(parameterMapping.getProperty(), parameterMapping.getTypeHandler().getResult(cs, i + 1));
        }
      }
    }
  }

  private void handleResultSetOutputParameter(CallableStatement cs, ParameterMapping parameterMapping, int parameterMappingIndex, MetaObject metaParam) throws SQLException {
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
    final ResultContext resultContext = new ResultContext();
    final List<String> unmappedColumnNames = getUnmappedColumnNames(rs, resultMap);
    skipRows(rs, rowLimit);
    while (shouldProcessMoreRows(rs, resultContext.getResultCount(), rowLimit)) {
      final Object resultObject = createResultObject(rs, resultMap);
      final MetaObject metaObject = MetaObject.forObject(resultObject);
      applyPropertyMappings(rs, resultMap, metaObject);
      applyAutomaticMappings(rs, unmappedColumnNames, metaObject);
      resultContext.nextResultObject(resultObject);
      resultHandler.handleResult(resultContext);
    }
  }

  private boolean shouldProcessMoreRows(ResultSet rs, int count, RowLimit rowLimit) throws SQLException {
    return rs.next() && count < rowLimit.getLimit();
  }

  private void skipRows(ResultSet rs, RowLimit rowLimit) throws SQLException {
    if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
      rs.absolute(rowLimit.getOffset());
    } else {
      for (int i=0; i < rowLimit.getOffset(); i++) rs.next();
    }
  }

  private void applyPropertyMappings(ResultSet rs, ResultMap resultMap, MetaObject metaObject) throws SQLException {
    final List<ResultMapping> propertyMappings = resultMap.getPropertyResultMappings();
    for (ResultMapping propertyMapping : propertyMappings) {
      final TypeHandler typeHandler = propertyMapping.getTypeHandler();
      final String property = propertyMapping.getProperty();
      final String column = propertyMapping.getColumn();
      final Object value = typeHandler.getResult(rs, column);
      metaObject.setValue(property, value);
    }
  }

  private void applyAutomaticMappings(ResultSet rs, List<String> unmappedColumnNames, MetaObject metaObject) throws SQLException {
    for(String columnName : unmappedColumnNames) {
      final String property = metaObject.findProperty(columnName);
      if (property != null) {
        final Class propertyType = metaObject.getSetterType(property);
        final TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(propertyType);
        final Object value = typeHandler.getResult(rs, columnName);
        metaObject.setValue(property, value);
      }
    }
  }

  private List<String> getUnmappedColumnNames(ResultSet rs, ResultMap resultMap) throws SQLException {
    final ResultSetMetaData rsmd = rs.getMetaData();
    final int columnCount = rsmd.getColumnCount();
    final List<String> columnNames = new ArrayList<String>();
    final Set<String> mappedColumns = resultMap.getMappedColumns();
    for(int i=1; i<=columnCount; i++) {
      final String columnName = configuration.isUseColumnLabel() ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i);
      final String upperColumnName = columnName.toUpperCase();
      if (!mappedColumns.contains(upperColumnName)) {
        columnNames.add(columnName);
      }
    }
    return columnNames;
  }

  private Object createResultObject(ResultSet rs, ResultMap resultMap) throws SQLException {
    final Class resultType = resultMap.getType();
    final ObjectFactory objectFactory = configuration.getObjectFactory();
    final List<ResultMapping> constructorMappings = resultMap.getConstructorResultMappings();
    if (constructorMappings.size() == 0) {
      return objectFactory.create(resultType);
    } else {
      final List<Class> parameterTypes = new ArrayList<Class>();
      final List<Object> parameterValues = new ArrayList<Object>();
      for(ResultMapping constructorMapping : constructorMappings) {
        final Class parameterType = constructorMapping.getJavaType();
        final TypeHandler typeHandler = constructorMapping.getTypeHandler();
        final String column = constructorMapping.getColumn();
        final Object value = typeHandler.getResult(rs, column);
        parameterTypes.add(parameterType);
        parameterValues.add(value);
      }
      return objectFactory.create(resultType, parameterTypes, parameterValues);
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

}
