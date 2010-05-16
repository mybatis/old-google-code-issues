package org.apache.ibatis.builder.xml.dynamic;

public class WhereSqlNode extends TrimSqlNode {

  public WhereSqlNode(SqlNode contents) {
    super(contents, "WHERE", "AND |OR ", null, null);
  }


}
