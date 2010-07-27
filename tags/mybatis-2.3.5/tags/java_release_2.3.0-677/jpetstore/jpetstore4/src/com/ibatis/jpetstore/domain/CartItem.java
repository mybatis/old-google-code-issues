package com.ibatis.jpetstore.domain;

import java.io.Serializable;
import java.math.BigDecimal;


public class CartItem implements Serializable {

  /* Private Fields */

  private Item item;
  private int quantity;
  private boolean inStock;
  private BigDecimal total;

  /* JavaBeans Properties */

  public boolean isInStock() {
    return inStock;
  }

  public void setInStock(boolean inStock) {
    this.inStock = inStock;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
    calculateTotal();
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
    calculateTotal();
  }

  /* Public methods */

  public void incrementQuantity() {
    quantity++;
    calculateTotal();
  }

  /* Private methods */

  private void calculateTotal() {
    if (item != null && item.getListPrice() != null) {
      total = item.getListPrice().multiply(new BigDecimal(quantity));
    } else {
      total = null;
    }
  }

}
