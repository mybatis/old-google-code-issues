package com.ibatis.dao.impl.sqlmap;

import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.ibatis.dao.iface.AccountDao;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import testdomain.Account;

import java.sql.SQLException;

public class SqlMapAccountDao extends SqlMapDaoTemplate implements AccountDao {

  public SqlMapAccountDao(DaoManager daoManager) {
    super(daoManager);
  }

  public void createAccount(Account account) {
    try {
      SqlMapExecutor sqlMap = getSqlMapExecutor();
      sqlMap.insert("insertAccount", account);
    } catch (SQLException e) {
      throw new DaoException("Error creating account.  Cause: " + e);
    }
  }

  public void saveAccount(Account account) {
    try {
      SqlMapExecutor sqlMap = getSqlMapExecutor();
      sqlMap.update("updateAccount", account);
    } catch (SQLException e) {
      throw new DaoException("Error saving account.  Cause: " + e);
    }
  }

  public void removeAccount(Account account) {
    try {
      SqlMapExecutor sqlMap = getSqlMapExecutor();
      sqlMap.delete("deleteAccount", account);
    } catch (SQLException e) {
      throw new DaoException("Error removing account.  Cause: " + e);
    }
  }

  public Account findAccount(int id) {
    try {
      SqlMapExecutor sqlMap = getSqlMapExecutor();
      return (Account) sqlMap.queryForObject("getAccount", new Integer(id));
    } catch (SQLException e) {
      throw new DaoException("Error finding account.  Cause: " + e);
    }
  }

}
