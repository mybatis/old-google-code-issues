package org.apache.ibatis.executor.resultset;

import java.math.BigDecimal;
import java.util.*;

public class PlatformTypeHolder implements Map {

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

  private Object key;
  private Object value;

  public static boolean isPlatformType(Class type) {
    return platformTypes.contains(type);
  }

  public int size() {
    return 1;
  }

  public boolean isEmpty() {
    return key == null && value == null;
  }

  public boolean containsKey(Object other) {
    return key == null ? other == null : key.equals(other);
  }

  public boolean containsValue(Object other) {
    return value == null ? other == null : value.equals(other);
  }

  public Object get(Object key) {
    return value;
  }

  public Object put(Object key, Object value) {
    Object old = this.value;
    this.key = key;
    this.value = value;
    return old;
  }

  public Object remove(Object key) {
    Object old = this.value;
    this.key = null;
    this.value = null;
    return old;
  }

  public void putAll(Map t) {
    for (Map.Entry e : (Set<Map.Entry>) t.entrySet()) {
      this.key = e.getKey();
      this.value = e.getValue();
    }
  }

  public void clear() {
    this.key = null;
    this.value = null;
  }

  public Set keySet() {
    return new HashSet() {
      {
        add(key);
      }
    };
  }

  public Collection values() {
    return new ArrayList() {
      {
        add(value);
      }
    };
  }

  public Set entrySet() {
    final Map.Entry entry = new Map.Entry() {
      public Object getKey() {
        return key;
      }

      public Object getValue() {
        return value;
      }

      public Object setValue(Object v) {
        Object old = value;
        value = v;
        return old;
      }
    };
    return new HashSet() {
      {
        add(entry);
      }
    };
  }
}
