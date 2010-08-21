package org.apache.ibatis.executor;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.result.ResultHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

  int NO_ROW_OFFSET = 0;
  int NO_ROW_LIMIT = Integer.MAX_VALUE;
  ResultHandler NO_RESULT_HANDLER = null;

  int update(MappedStatement ms, Object parameter) throws SQLException;

  List query(MappedStatement ms, Object parameter, int offset, int limit, ResultHandler resultHandler) throws SQLException;

  List<BatchResult> flushStatements() throws SQLException;

  void commit(boolean required) throws SQLException;

  void rollback(boolean required) throws SQLException;

  CacheKey createCacheKey(MappedStatement ms, Object parameterObject, int offset, int limit);

  boolean isCached(MappedStatement ms, CacheKey key);

  void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key);

  Transaction getTransaction();

  void close();

  boolean isClosed();

}
