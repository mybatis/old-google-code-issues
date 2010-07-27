package org.apache.ibatis.plugin;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Signature {
  Class type();

  String method();

  Class[] args();
}