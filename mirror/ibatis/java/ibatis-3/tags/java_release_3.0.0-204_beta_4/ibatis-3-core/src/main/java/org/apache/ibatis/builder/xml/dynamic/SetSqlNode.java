package org.apache.ibatis.builder.xml.dynamic;

public class SetSqlNode extends TrimSqlNode {

  public SetSqlNode(SqlNode contents) {
    super(contents, "SET", null, null, ",");
  }

}
