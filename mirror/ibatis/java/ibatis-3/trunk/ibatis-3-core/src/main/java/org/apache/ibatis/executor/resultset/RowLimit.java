package org.apache.ibatis.executor.resultset;

public class RowLimit {

  public final static int NO_ROW_OFFSET = 0;
  public final static int NO_ROW_LIMIT = Integer.MAX_VALUE;
  public final static RowLimit DEFAULT = new RowLimit();

  private int offset;
  private int limit;

  public RowLimit() {
    this.offset = NO_ROW_OFFSET;
    this.limit = NO_ROW_LIMIT;
  }

  public RowLimit(int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }

  public int getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }

}
