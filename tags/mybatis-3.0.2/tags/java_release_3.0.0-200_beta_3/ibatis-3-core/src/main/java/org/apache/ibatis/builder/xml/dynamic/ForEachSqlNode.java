package org.apache.ibatis.builder.xml.dynamic;

import org.apache.ibatis.parsing.GenericTokenParser;

import java.util.Map;

public class ForEachSqlNode implements SqlNode {
  private ExpressionEvaluator evaluator;
  private String collectionExpression;
  private SqlNode contents;
  private String open;
  private String close;
  private String separator;
  private String item;
  private String index;

  public ForEachSqlNode(SqlNode contents, String collectionExpression, String index, String item, String open, String close, String separator) {
    this.evaluator = new ExpressionEvaluator();
    this.collectionExpression = collectionExpression;
    this.contents = contents;
    this.open = open;
    this.close = close;
    this.separator = separator;
    this.index = index;
    this.item = item;
  }

  public boolean apply(DynamicContext context) {
    Map<String, Object> bindings = context.getBindings();
    final Iterable iterable = evaluator.evaluateIterable(collectionExpression, bindings);    
    boolean first = true;
    applyOpen(context);
    int i = 0;
    for (Object o : iterable) {
      first = applySeparator(context, first);
      int uniqueNumber = context.getUniqueNumber();
      applyItem(context, o, uniqueNumber);
      applyIndex(context, i);
      contents.apply(new FilteredDynamicContext(context, item, uniqueNumber));
      i++;
    }
    applyClose(context);
    return true;
  }

  private void applyIndex(DynamicContext context, int i) {
    if (index != null) {
      context.bind(index, i);
    }
  }

  private void applyItem(DynamicContext context, Object o, int i) {
    if (item != null) {
      context.bind(item, o);
      context.bind(itemizeItem(item,i), o);
    }
  }

  private void applyOpen(DynamicContext context) {
    if (open != null) {
      context.appendSql(open);
    }
  }

  private boolean applySeparator(DynamicContext context, boolean first) {
    if (first) {
      first = false;
    } else {
      if (separator != null) {
        context.appendSql(separator);
      }
    }
    return first;
  }

  private void applyClose(DynamicContext context) {
    if (close != null) {
      context.appendSql(close);
    }
  }

  private static String itemizeItem(String item, int i) {
    return new StringBuilder("__").append(item).append("_").append(i).toString();
  }


  
  private static class FilteredDynamicContext extends DynamicContext {
    private DynamicContext delegate;
    private int index;
    private String item;

    public FilteredDynamicContext(DynamicContext delegate, String item, int i) {
      super(null);
      this.delegate = delegate;
      this.index = i;
      this.item = item;
    }

    public Map<String, Object> getBindings() {
      return delegate.getBindings();
    }

    public void bind(String name, Object value) {
      delegate.bind(name, value);
    }

    public String getSql() {
      return delegate.getSql();
    }

    public void appendSql(String sql) {
      GenericTokenParser parser = new GenericTokenParser("#{", "}", new GenericTokenParser.TokenHandler() {
        public String handleToken(String content) {
          String newContent = content.replaceFirst(item, itemizeItem(item,index));
          return new StringBuilder("#{").append(newContent).append("}").toString();
        }
      });

      delegate.appendSql(parser.parse(sql));
    }
    
    @Override
    public int getUniqueNumber() {
      return delegate.getUniqueNumber();
    }

  }

}
