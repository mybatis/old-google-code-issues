package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.*;

public class MethodInvoker implements Invoker {

  private Class type;
  private Method method;

  public MethodInvoker(Method method) {
    this.method = method;

    if (method.getParameterTypes().length == 1) {
      type = method.getParameterTypes()[0];
    } else {
      type = method.getReturnType();
    }
  }

  public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
    return method.invoke(target, args);
  }

  public Class getType() {
    return type;
  }
}
