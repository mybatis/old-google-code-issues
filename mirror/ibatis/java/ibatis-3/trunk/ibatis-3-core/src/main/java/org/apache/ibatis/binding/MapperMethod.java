package org.apache.ibatis.binding;

import org.apache.ibatis.executor.resultset.RowLimit;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class MapperMethod {

  private SqlSession sqlSession;
  private Configuration config;

  private SqlCommandType type;
  private String commandName;

  private Method method;

  private boolean returnsList;

  private Integer rowLimitIndex;
  private List<String> paramNames;
  private List<Integer> paramPositions;

  public MapperMethod(Method method, SqlSession sqlSession) {
    paramNames = new ArrayList<String>();
    paramPositions = new ArrayList<Integer>();
    this.sqlSession = sqlSession;
    this.method = method;
    this.config = sqlSession.getConfiguration();
    setupFields();
    setupMethodSignature();
    setupCommandType();
    validateStatement();
  }

  public Object execute(Object[] args) throws SQLException {
    Object result;
    if (SqlCommandType.INSERT == type) {
      Object param = getParam(args);
      result = sqlSession.insert(commandName, param);
    } else if (SqlCommandType.UPDATE == type) {
      Object param = getParam(args);
      result = sqlSession.update(commandName, param);
    } else if (SqlCommandType.DELETE == type) {
      Object param = getParam(args);
      result = sqlSession.delete(commandName, param);
    } else if (SqlCommandType.SELECT == type) {
      if (returnsList) {
        result = executeForList(args);
      } else {
        Object param = getParam(args);
        result = sqlSession.selectOne(commandName, param);
      }
    } else {
      throw new BindingException("Unkown execution method for: " + commandName);
    }
    return result;
  }

  private Object executeForList(Object[] args) throws SQLException {
    Object result;
    if (rowLimitIndex != null) {
      Object param = getParam(args);
      RowLimit rowLimit = (RowLimit) args[rowLimitIndex];
      result = sqlSession.selectList(commandName, param, rowLimit);
    } else {
      Object param = getParam(args);
      result = sqlSession.selectList(commandName, param);
    }
    return result;
  }

  private Object getParam(Object[] args) {
    final int paramCount = paramPositions.size();
    if (args == null || paramCount == 0) {
      return null;
    } else if (paramPositions.size() == 1) {
      return args[paramPositions.get(0)];
    } else {
      Map param = new HashMap();
      for (int i=0; i < paramCount; i++) {
        param.put(paramNames.get(i), args[paramPositions.get(i)]);
      }
      return param;
    }
  }

  // Setup //

  private void setupFields() {
    this.commandName = method.getDeclaringClass().getName() + "." + method.getName();
  }

  private void setupMethodSignature() {
    if (List.class.isAssignableFrom(method.getReturnType())) {
      returnsList = true;
    }
    final Class[] argTypes = method.getParameterTypes();
    for (int i=0; i < argTypes.length; i++) {
      if (RowLimit.class.isAssignableFrom(argTypes[i])) {
        rowLimitIndex = i;
      } else {
        final String paramName = String.valueOf(paramPositions.size());
        paramNames.add(paramName);
        paramPositions.add(i);
      }
    }
  }

  private void setupCommandType() {
    MappedStatement ms = config.getMappedStatement(commandName);
    type = ms.getSqlCommandType();
    if (type == SqlCommandType.UNKNOWN) {
      throw new BindingException("Unknown execution method for: " + commandName);
    }
  }

  private void validateStatement() {
    try {
      config.getMappedStatement(commandName);
    } catch (Exception e) {
      throw new BindingException("Invalid bound statement (not found): " + commandName, e);
    }
  }

}
