package com.ibatis.sqlmap.engine.binding;

import com.ibatis.sqlmap.client.SqlMapClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MapperProxy implements InvocationHandler {

  private SqlMapClient client;

  private MapperProxy(SqlMapClient client) {
    this.client = client;
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return new MapperCommand(method, client).execute(args);
  }

  public static Object newMapperProxy (SqlMapClient client, Class iface) {
    ClassLoader classLoader = iface.getClassLoader();
    Class[] interfaces = new Class[]{iface};
    MapperProxy handler = new MapperProxy(client);
    return Proxy.newProxyInstance(classLoader, interfaces, handler);
  }

}
