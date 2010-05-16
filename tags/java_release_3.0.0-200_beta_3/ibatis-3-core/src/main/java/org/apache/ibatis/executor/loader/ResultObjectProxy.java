package org.apache.ibatis.executor.loader;

import net.sf.cglib.proxy.*;
import org.apache.ibatis.reflection.*;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.io.Serializable;
import java.lang.reflect.Method;

public class ResultObjectProxy {

  private static final TypeHandlerRegistry registry = new TypeHandlerRegistry();

  public static Object createProxy(Class type, Object target, ResultLoaderRegistry lazyLoader) {
    return EnhancedResultObjectProxyImpl.createProxy(type, target, lazyLoader);
  }

  private static class EnhancedResultObjectProxyImpl implements InvocationHandler, Serializable {

    private Object target;
    private ResultLoaderRegistry lazyLoader;

    private EnhancedResultObjectProxyImpl(Object target, ResultLoaderRegistry lazyLoader) {
      this.target = target;
      this.lazyLoader = lazyLoader;
    }

    public static Object createProxy(Class type, Object target, ResultLoaderRegistry lazyLoader) {
      if (registry.hasTypeHandler(type)) {
        return target;
      } else {
        return Enhancer.create(type, new EnhancedResultObjectProxyImpl(target, lazyLoader));
      }
    }

    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
      try {
        Object value = method.invoke(target, args);
        if (value == null) {
          String methodName = method.getName();
          if (PropertyNamer.isGetter(methodName)) {
            if (lazyLoader.loadByMethod(methodName)) {
              value = method.invoke(target, args);
            }
          }
        }
        return value;
      } catch (Throwable t) {
        throw ExceptionUtil.unwrapThrowable(t);
      }
    }
  }

}
