package org.apache.ibatis.type;

import java.math.BigDecimal;
import java.util.*;

public class TypeAliasRegistry {

  private final HashMap<String, String> TYPE_ALIASES = new HashMap<String, String>();

  public TypeAliasRegistry() {
    registerAlias("string", String.class.getName());
    registerAlias("byte", Byte.class.getName());
    registerAlias("long", Long.class.getName());
    registerAlias("short", Short.class.getName());
    registerAlias("int", Integer.class.getName());
    registerAlias("integer", Integer.class.getName());
    registerAlias("double", Double.class.getName());
    registerAlias("float", Float.class.getName());
    registerAlias("boolean", Boolean.class.getName());
    registerAlias("date", Date.class.getName());
    registerAlias("decimal", BigDecimal.class.getName());
    registerAlias("bigdecimal", BigDecimal.class.getName());
    registerAlias("object", Object.class.getName());
    registerAlias("map", Map.class.getName());
    registerAlias("hashmap", HashMap.class.getName());
    registerAlias("list", List.class.getName());
    registerAlias("arraylist", ArrayList.class.getName());
    registerAlias("collection", Collection.class.getName());
    registerAlias("iterator", Iterator.class.getName());
  }

  public String resolveAlias(String string) {
    if (string == null) return null;
    String key = string.toLowerCase();
    String value = string;
    if (TYPE_ALIASES.containsKey(key)) {
      value = TYPE_ALIASES.get(key);
    }
    return value;
  }

  public void registerAlias(Class type) {
    registerAlias(type.getSimpleName(), type.getName());
  }

  public void registerAlias(String alias, Class type) {
    registerAlias(alias, type.getName());
  }

  public void registerAlias(String alias, String value) {
    assert alias != null;
    String key = alias.toLowerCase();
    if (TYPE_ALIASES.containsKey(key) && !TYPE_ALIASES.get(key).equals(value) && TYPE_ALIASES.get(alias) != null) {
      throw new TypeException("The alias '" + alias + "' is already mapped to the value '" + TYPE_ALIASES.get(alias) + "'.");
    }
    TYPE_ALIASES.put(key, value);
  }

}
