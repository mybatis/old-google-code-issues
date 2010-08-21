package org.apache.ibatis.executor.loader;

import org.apache.ibatis.reflection.MetaObject;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

public class ResultLoaderRegistry implements Serializable {

  private final Map<String, LoadPair> loaderMap = new HashMap<String, LoadPair>();

  public void registerLoader(String property, MetaObject metaResultObject, ResultLoader resultLoader) {
    // Converts property to method name strictly for performance.
    String upperFirst = getUppercaseFirstProperty(property);
    loaderMap.put(toGetter(upperFirst), new LoadPair(property, metaResultObject, resultLoader));
    loaderMap.put(toSetter(upperFirst), new LoadPair(property, metaResultObject, resultLoader));
  }

  public boolean loadByMethod(String methodName) throws SQLException {
    synchronized (loaderMap) {
      ResultLoaderRegistry.LoadPair pair = loaderMap.remove(methodName.toUpperCase());
      if (pair != null) {
        pair.load();
        return true;
      }
      return false;
    }
  }

  private String toGetter(String first) {
    return "GET" + first;
  }

  private String toSetter(String first) {
    return "SET" + first;
  }

  private static String getUppercaseFirstProperty(String property) {
    String[] parts = property.split("\\.");
    return parts[0].toUpperCase();
  }

  private class LoadPair {
    private String property;
    private MetaObject metaResultObject;
    private ResultLoader resultLoader;

    private LoadPair(String property, MetaObject metaResultObject, ResultLoader resultLoader) {
      this.property = property;
      this.metaResultObject = metaResultObject;
      this.resultLoader = resultLoader;
    }

    public void load() throws SQLException {
      metaResultObject.setValue(property, resultLoader.loadResult());
    }
  }
}
