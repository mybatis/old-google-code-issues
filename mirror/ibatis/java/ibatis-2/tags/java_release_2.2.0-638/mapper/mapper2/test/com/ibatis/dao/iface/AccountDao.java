package com.ibatis.dao.iface;

import testdomain.Account;

public interface AccountDao {

  public void createAccount(Account account);

  public void saveAccount(Account account);

  public void removeAccount(Account account);

  public Account findAccount(int id);

}
