package com.ibatis.common.xml;

import com.ibatis.common.exception.NestedException;

public class NodeletException extends NestedException {

  public NodeletException() {
    super();
  }

  public NodeletException(String msg) {
    super(msg);
  }

  public NodeletException(Throwable cause) {
    super(cause);
  }

  public NodeletException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
