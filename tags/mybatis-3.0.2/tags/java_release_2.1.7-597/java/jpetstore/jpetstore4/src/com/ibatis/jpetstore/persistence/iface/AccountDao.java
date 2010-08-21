/**
 * User: Clinton Begin
 * Date: Jul 13, 2003
 * Time: 8:17:52 PM
 */
package com.ibatis.jpetstore.persistence.iface;

import com.ibatis.jpetstore.domain.Account;

import java.util.List;

public interface AccountDao {

  public Account getAccount(String username);

  public List getUsernameList();

  public Account getAccount(String username, String password);

  public void insertAccount(Account account);

  public void updateAccount(Account account);

}
