package com.ibatis.dao;

import com.ibatis.common.exception.NestedRuntimeException;
import com.ibatis.common.jdbc.ScriptRunner;
import com.ibatis.common.resources.Resources;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.DaoTransaction;
import com.ibatis.dao.engine.transaction.jdbc.JdbcDaoTransaction;
import com.ibatis.dao.engine.transaction.sqlmap.SqlMapDaoTransaction;
import com.ibatis.dao.iface.AccountDao;
import junit.framework.TestCase;
import testdomain.Account;

import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.Connection;

public abstract class BaseDaoTest extends TestCase {

  protected DaoManager daoManager;

  public void testCreateAccount() {
    AccountDao dao = (AccountDao) daoManager.getDao(AccountDao.class);

    Account account = newAccount();

    // Create Account (autocommit)
    dao.createAccount(account);

    account = (Account) dao.findAccount(account.getId());
    assertNotNull(account);
    assertEquals("clinton.begin@ibatis.com", account.getEmailAddress());

  }

  public void testTransactionRollback() {
    AccountDao dao = (AccountDao) daoManager.getDao(AccountDao.class);

    Account account = newAccount();
    Account account2 = dao.findAccount(1);
    account2.setEmailAddress("someotherAddress@somewhere.com");

    try {
      daoManager.startTransaction();
      dao.createAccount(account);
      dao.saveAccount(account2);
      throw new NestedRuntimeException("BOOM!");
      //daoManager.commitTransaction();
    } catch (Exception e) {
      // ignore
    } finally {
      daoManager.endTransaction();
    }

    account = dao.findAccount(account.getId());
    account2 = dao.findAccount(1);

    assertNull(account);
    assertEquals("clinton.begin@ibatis.com", account2.getEmailAddress());

  }

  public void testTransactionCommit() {
    AccountDao dao = (AccountDao) daoManager.getDao(AccountDao.class);

    Account account = newAccount();
    Account account2 = dao.findAccount(1);
    account2.setEmailAddress("someotherAddress@somewhere.com");

    try {
      daoManager.startTransaction();
      dao.createAccount(account);
      dao.saveAccount(account2);
      daoManager.commitTransaction();
    } finally {
      daoManager.endTransaction();
    }

    account = dao.findAccount(account.getId());
    account2 = dao.findAccount(1);

    assertNotNull(account);
    assertEquals("someotherAddress@somewhere.com", account2.getEmailAddress());

  }

  public void testDeleteAccount() {
    AccountDao dao = (AccountDao) daoManager.getDao(AccountDao.class);

    Account account = newAccount();

    // Create Account (autocommit)
    dao.createAccount(account);

    account = (Account) dao.findAccount(account.getId());
    assertNotNull(account);
    assertEquals("clinton.begin@ibatis.com", account.getEmailAddress());

    dao.removeAccount(account);

    account = (Account) dao.findAccount(account.getId());
    assertNull(account);

  }

  public void testException() {
    AccountDao dao = (AccountDao) daoManager.getDao(AccountDao.class);

    try {
      dao.createAccount(null);
    } catch (Exception e) {
      assertFalse(e instanceof UndeclaredThrowableException);
      assertFalse(e instanceof InvocationTargetException);
    }

  }

  private Account newAccount() {
    Account account = new Account();
    account.setId(1001);
    account.setFirstName("Clinton");
    account.setLastName("Begin");
    account.setEmailAddress("clinton.begin@ibatis.com");
    return account;
  }

  protected void initScript(String script) throws Exception {
    daoManager.startTransaction();
    DaoTransaction trans = daoManager.getTransaction(daoManager.getDao(AccountDao.class));


    Connection conn = null;
    if (trans instanceof JdbcDaoTransaction) {
      conn = ((JdbcDaoTransaction) trans).getConnection();
    } else if (trans instanceof SqlMapDaoTransaction) {
      conn = ((SqlMapDaoTransaction) trans).getSqlMap().getDataSource().getConnection();
    }

    Reader reader = Resources.getResourceAsReader(script);

    ScriptRunner runner = new ScriptRunner(conn, false, false);
    runner.setLogWriter(null);
    runner.setErrorLogWriter(null);

    runner.runScript(reader);

    daoManager.commitTransaction();
  }

  public void setUp() throws Exception {
    super.setUp();
  }

}
