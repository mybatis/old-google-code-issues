/**
 * User: Clinton Begin
 * Date: Jul 13, 2003
 * Time: 8:19:08 PM
 */
package com.ibatis.jpetstore.persistence.iface;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.jpetstore.domain.Product;

public interface ProductDao {

  public PaginatedList getProductListByCategory(String categoryId);

  public Product getProduct(String productId);

  public PaginatedList searchProductList(String keywords);

}
