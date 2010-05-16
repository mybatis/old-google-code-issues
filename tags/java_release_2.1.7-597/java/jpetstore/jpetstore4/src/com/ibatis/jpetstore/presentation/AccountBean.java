package com.ibatis.jpetstore.presentation;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.jpetstore.domain.Account;
import com.ibatis.jpetstore.service.AccountService;
import com.ibatis.jpetstore.service.CatalogService;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.apache.struts.beanaction.BeanActionException;
import org.apache.struts.beanaction.ActionContext;

public class AccountBean extends AbstractBean {

  /* Constants */

  private static final List LANGUAGE_LIST;
  private static final List CATEGORY_LIST;

  /* Private Fields */

  private AccountService accountService;
  private CatalogService catalogService;

  private Account account;
  private String repeatedPassword;
  private String pageDirection;
  private String validation;
  private PaginatedList myList;
  private boolean authenticated;

  /* Static Initializer */

  static {
    List langList = new ArrayList();
    langList.add("english");
    langList.add("japanese");
    LANGUAGE_LIST = Collections.unmodifiableList(langList);

    List catList = new ArrayList();
    catList.add("FISH");
    catList.add("DOGS");
    catList.add("REPTILES");
    catList.add("CATS");
    catList.add("BIRDS");
    CATEGORY_LIST = Collections.unmodifiableList(catList);
  }

  /* Constructors */

  public AccountBean() {
    account = new Account();
    accountService = AccountService.getInstance();
    catalogService = CatalogService.getInstance();
  }

  /* JavaBeans Properties */

  public String getUsername() {
    return account.getUsername();
  }

  public void setUsername(String username) {
    account.setUsername(username);
  }

  public String getPassword() {
    return account.getPassword();
  }

  public void setPassword(String password) {
    account.setPassword(password);
  }

  public PaginatedList getMyList() {
    return myList;
  }

  public void setMyList(PaginatedList myList) {
    this.myList = myList;
  }

  public String getRepeatedPassword() {
    return repeatedPassword;
  }

  public void setRepeatedPassword(String repeatedPassword) {
    this.repeatedPassword = repeatedPassword;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }


  public List getLanguages() {
    return LANGUAGE_LIST;
  }

  public List getCategories() {
    return CATEGORY_LIST;
  }

  public String getPageDirection() {
    return pageDirection;
  }

  public void setPageDirection(String pageDirection) {
    this.pageDirection = pageDirection;
  }

  public String getValidation() {
    return validation;
  }

  public void setValidation(String validation) {
    this.validation = validation;
  }

  /* Public Methods */

  public String newAccount() {
    try {
      accountService.insertAccount(account);
      account = accountService.getAccount(account.getUsername());
      myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
      authenticated = true;
      repeatedPassword = null;
      return SUCCESS;
    } catch (Exception e) {
      throw new BeanActionException ("There was a problem creating your Account Information.  Cause: " + e, e);
    }
  }

  public String editAccountForm() {
    try {
      account = accountService.getAccount(account.getUsername());
      return SUCCESS;
    } catch (Exception e) {
      throw new BeanActionException ("There was a problem retrieving your Account Information. Cause: "+e, e);
    }
  }

  public String editAccount() {
    try {
      accountService.updateAccount(account);
      account = accountService.getAccount(account.getUsername());
      myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
      return SUCCESS;
    } catch (Exception e) {
      throw new BeanActionException ("There was a problem updating your Account Information. Cause: "+e, e);
    }
  }

  public String switchMyListPage () {
    if ("next".equals(pageDirection)) {
      myList.nextPage();
    } else if ("previous".equals(pageDirection)) {
      myList.previousPage();
    }
    return SUCCESS;
  }

  public String signon() {

    account = accountService.getAccount(account.getUsername(), account.getPassword());

    if (account == null || account == null) {
      String value = "Invalid username or password.  Signon failed.";
      setMessage(value);
      clear();
      return FAILURE;
    } else {
      account.setPassword(null);

      myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());

      authenticated = true;

      return SUCCESS;
    }
  }

  public String signoff() {
    ActionContext.getActionContext().getRequest().getSession().invalidate();
    clear();
    return SUCCESS;
  }

  public boolean isAuthenticated() {
    return authenticated && account != null && account.getUsername() != null;
  }

  public void reset() {
    if (account != null) {
      account.setBannerOption(false);
      account.setListOption(false);
    }
  }

  public void clear() {
    account = new Account();
    repeatedPassword = null;
    pageDirection = null;
    myList = null;
    authenticated = false;
  }

}
