package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.*;

public class SetFieldInvoker implements Invoker {
  private Field field;

  public SetFieldInvoker(Field field) {
    this.field = field;
  }

  public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
    field.set(target, args[0]);
    return null;
  }

  public Class getType() {
    return field.getType();
  }
}
