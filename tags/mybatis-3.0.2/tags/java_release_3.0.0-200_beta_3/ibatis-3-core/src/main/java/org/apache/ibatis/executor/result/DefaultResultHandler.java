package org.apache.ibatis.executor.result;

import java.util.*;

public class DefaultResultHandler implements ResultHandler {

  private final List list = new ArrayList();

  public void handleResult(ResultContext context) {
    list.add(context.getResultObject());
  }

  public List getResultList() {
    return list;
  }

}
