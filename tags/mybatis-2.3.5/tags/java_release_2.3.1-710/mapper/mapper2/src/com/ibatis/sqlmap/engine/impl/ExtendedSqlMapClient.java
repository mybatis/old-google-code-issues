package com.ibatis.sqlmap.engine.impl;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @deprecated - this class is uneccessary and should be removed as soon as possible. Currently spring integration depends on it.
 */
public interface ExtendedSqlMapClient extends SqlMapClient {

  /**
   * only here to avoid Spring breakage. DO NOT USE.
   * @deprecated 
   * @return delegate
   */
  public SqlMapExecutorDelegate getDelegate();


}
