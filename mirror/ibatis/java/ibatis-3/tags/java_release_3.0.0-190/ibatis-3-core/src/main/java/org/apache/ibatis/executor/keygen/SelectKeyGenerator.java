package org.apache.ibatis.executor.keygen;

import org.apache.ibatis.executor.*;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;

import java.sql.Statement;
import java.util.List;

public class SelectKeyGenerator implements KeyGenerator {
  public static final String SELECT_KEY_SUFFIX = "!selectKey";
  private boolean executeBefore;
  private MappedStatement keyStatement;

  public SelectKeyGenerator(MappedStatement keyStatement, boolean executeBefore) {
    this.executeBefore = executeBefore;
    this.keyStatement = keyStatement;
  }

  public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
    if (executeBefore) {
      processGeneratedKeys(executor, ms, stmt, parameter);
    }
  }

  public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
    if (!executeBefore) {
      processGeneratedKeys(executor, ms, stmt, parameter);
    }
  }

  private void processGeneratedKeys(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
    try {
      final Configuration configuration = ms.getConfiguration();
      if (parameter != null) {
        String keyStatementName = ms.getId() + SELECT_KEY_SUFFIX;
        if (configuration.hasStatement(keyStatementName)) {

          if (keyStatement != null) {
            String keyProperty = keyStatement.getKeyProperty();
            final MetaObject metaParam = MetaObject.forObject(parameter);
            if (keyProperty != null && metaParam.hasSetter(keyProperty)) {
              // Do not close keyExecutor.
              // The transaction will be closed by parent executor.
              Executor keyExecutor = configuration.newExecutor(executor.getTransaction(), ExecutorType.SIMPLE);
              List values = keyExecutor.query(keyStatement, parameter, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
              if (values.size() > 1) {
                throw new ExecutorException("Select statement for SelectKeyGenerator returned more than one value.");
              }
              metaParam.setValue(keyProperty, values.get(0));
            }
          }
        }
      }
    } catch (Exception e) {
      throw new ExecutorException("Error selecting key or setting result to parameter object. Cause: " + e, e);
    }
  }

}
