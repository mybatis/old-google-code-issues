/**
 * User: Clinton Begin
 * Date: Jul 13, 2003
 * Time: 8:18:50 PM
 */
package com.ibatis.jpetstore.persistence.iface;

import com.ibatis.jpetstore.domain.Order;
import com.ibatis.common.util.PaginatedList;

import java.util.List;

public interface OrderDao {

  public PaginatedList getOrdersByUsername(String username);

  public Order getOrder(int orderId);

  public void insertOrder(Order order);

}
