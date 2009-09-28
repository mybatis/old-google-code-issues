package org.apache.ibatis.executor.resultset;

import java.math.BigDecimal;
import java.util.*;

public class PlatformType {

  private static final Set<Class> platformTypes = new HashSet<Class>() {
    {
      add(byte[].class);
      add(byte.class);
      add(short.class);
      add(int.class);
      add(long.class);
      add(float.class);
      add(double.class);
      add(boolean.class);
      add(Byte.class);
      add(Short.class);
      add(Integer.class);
      add(Long.class);
      add(Float.class);
      add(Double.class);
      add(Boolean.class);

      add(String.class);
      add(BigDecimal.class);
      add(Date.class);
      add(Object.class);

      add(java.sql.Date.class);
      add(java.sql.Time.class);
      add(java.sql.Timestamp.class);
    }
  };

  public static boolean isPlatformType(Class type) {
    return platformTypes.contains(type);
  }

}
