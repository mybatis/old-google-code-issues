package org.apache.ibatis.executor;

import org.apache.ibatis.cache.*;
import org.apache.ibatis.executor.result.ResultHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

public class CachingExecutor implements Executor {

  private Executor delegate;
  private TransactionalCacheManager tcm = new TransactionalCacheManager();

  public CachingExecutor(Executor delegate) {
    this.delegate = delegate;
  }

  public Transaction getTransaction() {
    return delegate.getTransaction();
  }

  public void close() {
    delegate.close();
  }

  public boolean isClosed() {
    return delegate.isClosed();
  }

  public int update(MappedStatement ms, Object parameterObject) throws SQLException {
    flushCacheIfRequired(ms);
    return delegate.update(ms, parameterObject);
  }


  public List query(MappedStatement ms, Object parameterObject, int offset, int limit, ResultHandler resultHandler) throws SQLException {
    if (ms != null) {
      Cache cache = ms.getCache();
      if (cache != null) {
        flushCacheIfRequired(ms);
        cache.getReadWriteLock().readLock().lock();
        try {
          if (ms.isUseCache()) {
            CacheKey key = createCacheKey(ms, parameterObject, offset, limit);
            if (cache.hasKey(key)) {
              return (List) cache.getObject(key);
            } else {
              List list = delegate.query(ms, parameterObject, offset, limit, resultHandler);
              tcm.putObject(cache, key, list);
              return list;
            }
          } else {
            return delegate.query(ms, parameterObject, offset, limit, resultHandler);
          }
        } finally {
          cache.getReadWriteLock().readLock().unlock();
        }
      }
    }
    return delegate.query(ms, parameterObject, offset, limit, resultHandler);
  }

  public List flushStatements() throws SQLException {
    return delegate.flushStatements();
  }

  public void commit(boolean required) throws SQLException {
    delegate.commit(required);
    tcm.commit();
  }

  public void rollback(boolean required) throws SQLException {
    try {
      delegate.rollback(required);
    } finally {
      tcm.rollback();
    }
  }

  public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, int offset, int limit) {
    return delegate.createCacheKey(ms, parameterObject, offset, limit);
  }

  public boolean isCached(MappedStatement ms, CacheKey key) {
    throw new UnsupportedOperationException("The CachingExecutor should not be used by result loaders and thus isCached() should never be called.");
  }

  public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key) {
    throw new UnsupportedOperationException("The CachingExecutor should not be used by result loaders and thus deferLoad() should never be called.");
  }

  private void flushCacheIfRequired(MappedStatement ms) {
    Cache cache = ms.getCache();
    if (cache != null) {
      if (ms.isFlushCacheRequired()) {
        tcm.clear(cache);
      }
    }
  }

}
