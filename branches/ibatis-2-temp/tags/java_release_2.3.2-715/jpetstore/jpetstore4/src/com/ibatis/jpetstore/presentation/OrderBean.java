package com.ibatis.jpetstore.presentation;

import com.ibatis.jpetstore.domain.Account;
import com.ibatis.jpetstore.domain.Order;
import com.ibatis.jpetstore.service.AccountService;
import com.ibatis.jpetstore.service.OrderService;
import com.ibatis.common.util.PaginatedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.apache.struts.beanaction.ActionContext;

public class OrderBean extends AbstractBean {

  /* Constants */

  private static final List CARD_TYPE_LIST;

  /* Private Fields */

  private AccountService accountService;
  private OrderService orderService;

  private Order order;
  private int orderId;
  private boolean shippingAddressRequired;
  private boolean confirmed;
  private PaginatedList orderList;
  private String pageDirection;

  /* Static Initializer */

  static {
    List cardList = new ArrayList();
    cardList.add("Visa");
    cardList.add("MasterCard");
    cardList.add("American Express");
    CARD_TYPE_LIST = Collections.unmodifiableList(cardList);
  }

  /* Constructors */

  public OrderBean() {
    order = new Order();
    shippingAddressRequired = false;
    confirmed = false;
    accountService = AccountService.getInstance();
    orderService = OrderService.getInstance();
  }

  /* JavaBeans Properties */

  public int getOrderId() {
    return orderId;
  }

  public void setOrderId(int orderId) {
    this.orderId = orderId;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public boolean isShippingAddressRequired() {
    return shippingAddressRequired;
  }

  public void setShippingAddressRequired(boolean shippingAddressRequired) {
    this.shippingAddressRequired = shippingAddressRequired;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public void setConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }

  public List getCreditCardTypes() {
    return CARD_TYPE_LIST;
  }

  public List getOrderList() {
    return orderList;
  }

  public String getPageDirection() {
    return pageDirection;
  }

  public void setPageDirection(String pageDirection) {
    this.pageDirection = pageDirection;
  }

  /* Public Methods */

  public String newOrderForm() {
    Map sessionMap = ActionContext.getActionContext().getSessionMap();
    AccountBean accountBean = (AccountBean) sessionMap.get("accountBean");
    CartBean cartBean = (CartBean) sessionMap.get("cartBean");

    clear();
    if (accountBean == null || !accountBean.isAuthenticated()){
      setMessage("You must sign on before attempting to check out.  Please sign on and try checking out again.");
      return SIGNON;
    } else if (cartBean != null) {
      // Re-read account from DB at team's request.
      Account account = accountService.getAccount(accountBean.getAccount().getUsername());
      order.initOrder(account, cartBean.getCart());
      return SUCCESS;
    } else {
      setMessage("An order could not be created because a cart could not be found.");
      return FAILURE;
    }
  }

  public String newOrder() {
    Map sessionMap = ActionContext.getActionContext().getSessionMap();

    if (shippingAddressRequired) {
      shippingAddressRequired = false;
      return SHIPPING;
    } else if (!isConfirmed()) {
      return CONFIRM;
    } else if (getOrder() != null) {

      orderService.insertOrder(order);

      CartBean cartBean = (CartBean)sessionMap.get("cartBean");
      cartBean.clear();

      setMessage("Thank you, your order has been submitted.");

      return SUCCESS;
    } else {
      setMessage("An error occurred processing your order (order was null).");
      return FAILURE;
    }
  }

  public String listOrders() {
    Map sessionMap = ActionContext.getActionContext().getSessionMap();
    AccountBean accountBean = (AccountBean) sessionMap.get("accountBean");
    orderList = orderService.getOrdersByUsername(accountBean.getAccount().getUsername());
    return SUCCESS;
  }

  public String switchOrderPage() {
    if ("next".equals(pageDirection)) {
      orderList.nextPage();
    } else if ("previous".equals(pageDirection)) {
      orderList.previousPage();
    }
    return SUCCESS;
  }


  public String viewOrder() {
    Map sessionMap = ActionContext.getActionContext().getSessionMap();
    AccountBean accountBean = (AccountBean) sessionMap.get("accountBean");

    order = orderService.getOrder(orderId);

    if (accountBean.getAccount().getUsername().equals(order.getUsername())) {
      return SUCCESS;
    } else {
      order = null;
      setMessage("You may only view your own orders.");
      return FAILURE;
    }
  }

  public void reset() {
    shippingAddressRequired = false;
  }

  public void clear() {
    order = new Order();
    orderId = 0;
    shippingAddressRequired = false;
    confirmed = false;
    orderList = null;
    pageDirection = null;
  }

}
