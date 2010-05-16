package com.ibatis.sqlmap.engine.mapper.metadata;

import java.util.HashMap;
import java.util.Map;

public class Table {

  private String name;

  private Map columns = new HashMap();

  public Table(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void addColumn (Column col) {
    columns.put(col.getName(), col);
  }

  public Column getColumn (String name) {
    return (Column) columns.get(name);
  }

  public String[] getColumnNames () {
    return (String[])columns.keySet().toArray(new String[columns.size()]);
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Table table = (Table) o;

    if (name != null ? !name.equals(table.name) : table.name != null) return false;

    return true;
  }

  public int hashCode() {
    return (name != null ? name.hashCode() : 0);
  }

}
