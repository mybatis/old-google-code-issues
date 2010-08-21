package org.apache.ibatis.builder;

import org.apache.ibatis.plugin.*;

import java.util.Properties;

@Intercepts({})
public class ExamplePlugin implements Interceptor {

  public Object intercept(Invocation invocation) throws Throwable {
    return invocation.proceed();
  }

  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  public void setProperties(Properties properties) {

  }

}
