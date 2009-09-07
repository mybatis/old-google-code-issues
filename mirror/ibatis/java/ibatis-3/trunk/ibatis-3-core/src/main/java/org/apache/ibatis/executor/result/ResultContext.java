package org.apache.ibatis.executor.result;

public interface ResultContext {

  Object getResultObject();

  int getResultCount();

  boolean isStopped();

  void stop();

}
