/**
 * User: Clinton Begin
 * Date: Jul 13, 2003
 * Time: 8:18:13 PM
 */
package com.ibatis.jpetstore.persistence.iface;

import com.ibatis.jpetstore.domain.Category;

import java.util.List;

public interface CategoryDao {

  public List getCategoryList();

  public Category getCategory(String categoryId);

}
