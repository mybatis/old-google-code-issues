package com.ibatis.jpetstore.service;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.jpetstore.domain.Account;
import com.ibatis.jpetstore.persistence.DaoConfig;
import com.ibatis.jpetstore.persistence.iface.AccountDao;

import java.util.List;

/**
 * <p/>
 * Date: Mar 6, 2004 11:22:43 PM
 * 
 * @author Clinton Begin
 */
public class AccountService {

  /* Constants */

  private static final AccountService instance = new AccountService();

  /* Private Fields */

  private DaoManager daoManager = DaoConfig.getDaomanager();

  private AccountDao accountDao;

  /* Constructors */

  public AccountService() {
    accountDao = (AccountDao) daoManager.getDao(AccountDao.class);
  }

  /* Public Methods */

  public static AccountService getInstance() {
    return instance;
  }

  /* ACCOUNT */

  public Account getAccount(String username) {
    return accountDao.getAccount(username);
  }

  public Account getAccount(String username, String password) {
    return accountDao.getAccount(username, password);
  }

  public void insertAccount(Account account) {
    accountDao.insertAccount(account);
  }

  public void updateAccount(Account account) {
    accountDao.updateAccount(account);
  }

  public List getUsernameList() {
    return accountDao.getUsernameList();
  }

}
