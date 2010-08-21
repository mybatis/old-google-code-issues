package com.ibatis.sqlmap.engine.binding;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.mapping.statement.GeneralStatement;
import com.ibatis.sqlmap.engine.mapping.statement.StatementType;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MapperCommand {

  private SqlMapClient client;
  private SqlMapExecutorDelegate delegate;

  private boolean hasSuppliedValueObject;

  private Method method;
  private int argCount;

  private boolean returnsList;
  private boolean hasListBounds;
  private boolean hasPageSize;

  private boolean returnsMap;
  private boolean hasMapValueKey;

  private StatementType type;
  private String statementName;
  private Class parameterClass;
  private Class resultClass;

  public MapperCommand(Method method, SqlMapClient client) {

    this.client = client;
    this.method = method;

    setupFields();
    determineStatementType();
    determineSelectMethod();
    validateStatement();
    validateResult();
    validateParameter();
  }

  private void setupFields() {
    this.statementName = method.getName();
    this.delegate = ((ExtendedSqlMapClient) client).getDelegate();
    GeneralStatement statement = (GeneralStatement) delegate.getMappedStatement(statementName);

    this.parameterClass = statement.getParameterClass();
    this.resultClass = statement.getResultMap() == null ? null : statement.getResultMap().getResultClass();
    this.type = statement.getStatementType();

    this.argCount = method.getParameterTypes().length;

  }

  private void determineStatementType() {
    // If proc or unkown, determine which type of statement we should execute using method names
    if (StatementType.PROCEDURE == type || StatementType.UNKNOWN == type) {
      if (statementName.startsWith("insert")) {
        type = StatementType.INSERT;
      } else if (statementName.startsWith("create")) {
        type = StatementType.INSERT;
      } else if (statementName.startsWith("update")) {
        type = StatementType.UPDATE;
      } else if (statementName.startsWith("save")) {
        type = StatementType.UPDATE;
      } else if (statementName.startsWith("delete")) {
        type = StatementType.DELETE;
      } else if (statementName.startsWith("remove")) {
        type = StatementType.DELETE;
      } else if (statementName.startsWith("select")) {
        type = StatementType.SELECT;
      } else if (statementName.startsWith("query")) {
        type = StatementType.SELECT;
      } else if (statementName.startsWith("get")) {
        type = StatementType.SELECT;
      } else if (statementName.startsWith("fetch")) {
        type = StatementType.SELECT;
      }
    }
  }

  private void determineSelectMethod() {
    if (StatementType.SELECT == type) {
      if (List.class.isAssignableFrom(method.getReturnType())) {
        // queryForList
        returnsList = true;
        if (argCount == 2) {
          hasPageSize = true;
        } else if (argCount == 3) {
          hasListBounds = true;
        }
      } else if (Map.class.isAssignableFrom(method.getReturnType())
          && argCount > 1
          && String.class.isAssignableFrom(method.getParameterTypes()[1])) {
        // queryForMap
        if (argCount == 2) {
          returnsMap = true;
          hasMapValueKey = false;
        } else if (argCount == 3) {
          returnsMap = true;
          hasMapValueKey = true;
        }
      } else {
        // queryForObject
        if (argCount == 2) {
          hasSuppliedValueObject = true;
        }
      }
    }
  }

  private void validateStatement() {
    try {
      delegate.getMappedStatement(statementName);
    } catch (Exception e) {
      throw new SqlMapException("Invalid bound statement (not found): " + statementName);
    }

    if (StatementType.UNKNOWN == type || StatementType.PROCEDURE == type) {
      throw new SqlMapException("Unkown statement type for statement: " + statementName);
    }
  }

  private void validateResult() {
    if (!returnsList && !returnsMap) {
//      if (method.getReturnType() == null || method.getReturnType() == Class.class) {
//        // Make sure both aren't null
//        if (resultClass != null) {
//          throw new SqlMapException("Invalid bound result for statement (mismatched null/void): " + statementName);
//        }
//      } else if (resultClass == null) {
//        // Make sure both aren't null
//        if (method.getReturnType() != null || method.getReturnType() == Class.class) {
//          System.out.println (method.getReturnType().getClass());
//          throw new SqlMapException("Invalid bound result for statement (mismatched null/void): " + statementName);
//        }
//      } else {
//        // Make sure types are compatible
//        if (!method.getReturnType().isAssignableFrom(resultClass)) {
//          throw new SqlMapException("Invalid bound result for statement (incompatible types): " + statementName);
//        }
//      }
    }
  }

  private void validateParameter() {
    if (argCount > 0) {
      // Make sure types are compatible
      Class paramType = method.getParameterTypes()[0];
      if (parameterClass != null) {
//        if (!parameterClass.isAssignableFrom(paramType.getClass())) {
//          throw new SqlMapException("Invalid bound parameter for statement (incompatible types): " + statementName);
//        }
      }
    }
    if (argCount > 1 && type != StatementType.SELECT) {
      // Only select statements can have multiple parameters
      throw new SqlMapException("Too many parameters for statement (must be 1 or 0): " + statementName);
    }
    if (argCount > 1 && type != StatementType.SELECT) {
      // Only select statements can have multiple parameters
      throw new SqlMapException("Too many parameters for statement (must be 1 or 0): " + statementName);
    }
  }

  public Object execute(Object[] args) throws SQLException {
    Object result = null;
    if (StatementType.INSERT == type) {
      Object param = getParam(args);
      result = client.insert(statementName, param);
    } else if (StatementType.UPDATE == type) {
      Object param = getParam(args);
      result = new Integer(client.update(statementName, param));
    } else if (StatementType.DELETE == type) {
      Object param = getParam(args);
      result = new Integer(client.delete(statementName, param));
    } else if (StatementType.SELECT == type) {
      if (returnsList) {
        result = executeForList(args);
      } else if (returnsMap) {
        result = executeForMap(args);
      } else {
        result = executeForObject(args);
      }
    } else {
      throw new SqlMapException("Unkown execution method for: " + statementName);
    }

    return result;
  }

  private Object executeForObject(Object[] args) throws SQLException {
    Object result;
    if (hasSuppliedValueObject) {
      Object param = getParam(args);
      Object valueObject = args[1];
      result = client.queryForObject(statementName, param, valueObject);
    } else {
      Object param = getParam(args);
      result = client.queryForObject(statementName, param);
    }
    return result;
  }

  private Object executeForMap(Object[] args) throws SQLException {
    Object result;
    if (hasMapValueKey) {
      Object param = getParam(args);
      String keyProp = (String) args[1];
      String valueProp = (String) args[2];
      result = client.queryForMap(statementName, param, keyProp, valueProp);
    } else {
      Object param = getParam(args);
      String keyProp = (String) args[1];
      result = client.queryForMap(statementName, param, keyProp);
    }
    return result;
  }

  private Object executeForList(Object[] args) throws SQLException {
    Object result;
    if (hasListBounds) {
      Object param = getParam(args);
      int skip = ((Integer) args[1]).intValue();
      int max = ((Integer) args[2]).intValue();
      result = client.queryForList(statementName, param, skip, max);
    } else if (hasPageSize) {
      Object param = getParam(args);
      int pageSize = ((Integer) args[1]).intValue();
      result = client.queryForPaginatedList(statementName, param, pageSize);
    } else {
      Object param = getParam(args);
      result = client.queryForList(statementName, param);
    }
    return result;
  }

  private Object getParam(Object[] args) {
    if (args == null) {
      return null;
    }
    return args.length > 0 ? args[0] : null;

  }

}
