package com.ibatis.common.logging;

import java.lang.reflect.Constructor;

public class LogFactory {

  private static Constructor logConstructor;

  static {
    tryImplementation("org.apache.commons.logging.LogFactory", "com.ibatis.common.logging.jakarta.JakartaCommonsLoggingImpl");
    tryImplementation("org.apache.log4j.Logger", "com.ibatis.common.logging.log4j.Log4jImpl");
    tryImplementation("java.util.logging.Logger", "com.ibatis.common.logging.jdk14.Jdk14LoggingImpl");
    tryImplementation("java.lang.Object", "com.ibatis.common.logging.nologging.NoLoggingImpl");
  }

  private static void tryImplementation(String testClassName, String implClassName) {
    if (logConstructor == null) {
      try {
        Class.forName(testClassName);
        Class implClass = Class.forName(implClassName);
        logConstructor = implClass.getConstructor(new Class[]{Class.class});
      } catch (Throwable t) {
      }
    }
  }

  public static Log getLog(Class aClass) {
    try {
      return (Log)logConstructor.newInstance(new Object[]{aClass});
    } catch (Throwable t) {
      throw new RuntimeException("Error creating logger for class " + aClass + ".  Cause: " + t, t);
    }
  }

}
