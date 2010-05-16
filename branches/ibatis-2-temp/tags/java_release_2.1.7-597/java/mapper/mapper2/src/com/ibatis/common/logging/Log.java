package com.ibatis.common.logging;

public interface Log {

  boolean isDebugEnabled();

  void error(String s, Exception e);

  public void debug(String s);

  public void warn(String s);

}
