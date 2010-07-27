package org.apache.ibatis.binding;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

public class MapperMethod {

  private SqlSession sqlSession;
  private Configuration config;

  private SqlCommandType type;
  private String commandName;

  private Method method;
  private int argCount;

  private boolean returnsList;
  private boolean hasListBounds;

  public MapperMethod(Method method, SqlSession sqlSession) {

    this.sqlSession = sqlSession;
    this.method = method;
    this.config = sqlSession.getConfiguration();

    setupFields();
    determineSelectMethod();
    determineCommandType();
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

  private void setupFields() {
    this.commandName = method.getDeclaringClass().getName() + "." + method.getName();
    this.argCount = method.getParameterTypes().length;
  }

  private void determineSelectMethod() {
    if (List.class.isAssignableFrom(method.getReturnType())) {
      returnsList = true;
      if (argCount == 2) {
        hasListBounds = true;
      } else if (argCount == 3) {
        hasListBounds = true;
      }
    }
  }

  private void determineCommandType() {
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

  private Object executeForList(Object[] args) throws SQLException {
    Object result;
    if (hasListBounds) {
      Object param = getParam(args);
      int offset = Executor.NO_ROW_OFFSET;
      int limit = Executor.NO_ROW_LIMIT;
      if (args.length == 3) {
        offset = ((Integer) args[1]);
        limit = ((Integer) args[2]);
      } else if (args.length == 2) {
        offset = ((Integer) args[0]);
        limit = ((Integer) args[1]);
      }
      result = sqlSession.selectList(commandName, param, offset, limit);
    } else {
      Object param = getParam(args);
      result = sqlSession.selectList(commandName, param);
    }
    return result;
  }

  private Object getParam(Object[] args) {
    if (args == null) {
      return null;
    }
    return args.length == 1 || args.length == 3 ? args[0] : null;
  }

}
