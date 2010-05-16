package org.apache.ibatis.plugin;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;

public class PluginTest {

  @Test
  public void mapPluginShouldInterceptGet() {
    Map map = new HashMap();
    map = (Map) new AlwaysMapPlugin().plugin(map);
    assertEquals("Always", map.get("Anything"));
  }

  @Test
  public void shouldNotInterceptToString() {
    Map map = new HashMap();
    map = (Map) new AlwaysMapPlugin().plugin(map);
    assertFalse("Always".equals(map.toString()));
  }

  @Intercepts({
    @Signature(type = Map.class, method = "get", args = {Object.class})})
  public static class AlwaysMapPlugin implements Interceptor {
    public Object intercept(Invocation invocation) throws Throwable {
      return "Always";
    }

    public Object plugin(Object target) {
      return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }
  }

}
