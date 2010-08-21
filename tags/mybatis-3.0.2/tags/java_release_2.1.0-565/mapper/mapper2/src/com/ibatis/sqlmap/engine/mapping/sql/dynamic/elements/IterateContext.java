/*
 * Created on Apr 17, 2005
 *
 */
package com.ibatis.sqlmap.engine.mapping.sql.dynamic.elements;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapException;

/**
 * @author Brandon Goodin
 *
 */
public class IterateContext implements Iterator {

  private Iterator iterator;
  private int index = -1;

  private String property;
  private boolean allowNext = true;
  private SqlTag tag;
  
  public IterateContext(Object collection,SqlTag tag) {
    this.tag = tag;
    if (collection instanceof Collection) {
      this.iterator = ((Collection) collection).iterator();
    } else if (collection instanceof Iterator) {
      this.iterator = ((Iterator) collection);
    } else if (collection.getClass().isArray()) {
      List list = arrayToList(collection);
      this.iterator = list.iterator();
    } else {
      throw new SqlMapException("ParameterObject or property was not a Collection, Array or Iterator.");
    }
  }

  public boolean hasNext() {
    return iterator != null && iterator.hasNext();
  }

  public Object next() {
    index++;
    return iterator.next();
  }

  public void remove() {
    iterator.remove();
  }

  public int getIndex() {
    return index;
  }

  public boolean isFirst() {
    return index == 0;
  }

  public boolean isLast() {
    return iterator != null && !iterator.hasNext();
  }
  
  private List arrayToList(Object array) {
    List list = null;
    if (array instanceof Object[]) {
      list = Arrays.asList((Object[]) array);
    } else {
      list = new ArrayList();
      for (int i = 0, n = Array.getLength(array); i < n; i++) {
        list.add(Array.get(array, i));
      }
    }
    return list;
  }

  /**
   * @return Returns the property.
   */
  public String getProperty() {
    return property;
  }
  
  /**
   * @param property The property to set.
   */
  public void setProperty(String property) {
    this.property = property;
  }
  
  /**
   * @return Returns the allowNext.
   */
  public boolean isAllowNext() {
    return allowNext;
  }
  
  /**
   * @param allowNext The allowNext to set.
   */
  public void setAllowNext(boolean performIterate) {
    this.allowNext = performIterate;
  }
  /**
   * @return Returns the tag.
   */
  public SqlTag getTag() {
    return tag;
  }
  /**
   * @param tag The tag to set.
   */
  public void setTag(SqlTag tag) {
    this.tag = tag;
  }
}