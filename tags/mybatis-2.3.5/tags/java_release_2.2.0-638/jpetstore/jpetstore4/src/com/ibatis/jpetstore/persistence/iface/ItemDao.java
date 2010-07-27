/**
 * User: Clinton Begin
 * Date: Jul 13, 2003
 * Time: 8:18:30 PM
 */
package com.ibatis.jpetstore.persistence.iface;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.jpetstore.domain.Item;
import com.ibatis.jpetstore.domain.Order;

public interface ItemDao {

  public void updateQuantity(Order order);

  public boolean isItemInStock(String itemId);

  public PaginatedList getItemListByProduct(String productId);

  public Item getItem(String itemId);

}
