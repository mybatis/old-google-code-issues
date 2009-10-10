package org.apache.ibatis.binding;

import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MapperProxy implements InvocationHandler {

  private SqlSession sqlSession;

  private <T> MapperProxy(SqlSession sqlSession) {
    this.sqlSession = sqlSession;
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return new MapperMethod(method, sqlSession).execute(args);
  }

  public static <T> T newMapperProxy(Class<T> mapperInterface, SqlSession sqlSession) {
    ClassLoader classLoader = mapperInterface.getClassLoader();
    Class[] interfaces = new Class[]{mapperInterface};
    MapperProxy proxy = new MapperProxy(sqlSession);
    return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
  }

}
