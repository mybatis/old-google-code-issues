package compatibility.dao;

import com.ibatis.db.dao.DaoException;
import com.ibatis.db.dao.DaoManager;
import compatibility.BaseCompat;
import compatibility.dao.iface.AccountDao;
import compatibility.domain.Account;

public class CrudCompat extends BaseCompat {

  public static int insertCompatibility() throws DaoException {
    int insertedId = getNextId();

    Account newAccount = new Account();
    newAccount.setId(insertedId);
    newAccount.setFirstName("Stan");
    newAccount.setLastName("Jobson");
    newAccount.setEmailAddress("stan@swordfish.com");

    DaoManager daoManager = DaoConfigCompat.getDaoManager();

    // Insert new account
    try {
      daoManager.startTransaction();

      AccountDao dao = (AccountDao) daoManager.getDao("Account");
      dao.insertAccount(newAccount);

      daoManager.commitTransaction();
    } catch (Exception e) {
      daoManager.rollbackTransaction();
      e.printStackTrace();
      throw new DaoException(e.toString());
    }

    // Check to see if insert was successful (just for testing)
    checkInsert(daoManager, insertedId);

    return insertedId;
  }

  private static void checkInsert(DaoManager daoManager, int insertedId) throws DaoException {
    // Check to see if insert was successful (just for testing)
    try {
      daoManager.startTransaction();
      AccountDao dao = (AccountDao) daoManager.getDao("Account");
      Account account = dao.getAccount(insertedId);
      if (account == null) {
        throw new DaoException("DAO failed to insert Account.");
      }
      println("Inserted: " + account);
      daoManager.commitTransaction();
    } catch (Exception e) {
      daoManager.rollbackTransaction();
      e.printStackTrace();
      throw new DaoException(e.toString());
    }
  }

  public static void updateCompatibility(int accountId) throws DaoException {

    DaoManager daoManager = DaoConfigCompat.getDaoManager();

    // Update email address
    try {
      daoManager.startTransaction();
      AccountDao dao = (AccountDao) daoManager.getDao("Account");
      Account account = dao.getAccount(accountId);
      account.setEmailAddress("stanley.jobson@swordfish.com");
      dao.updateAccount(account);
      daoManager.commitTransaction();
    } catch (Exception e) {
      daoManager.rollbackTransaction();
      e.printStackTrace();
      throw new DaoException(e.toString());
    }

    // Check to see if the update was successful (just for testing)
    checkUpdate(daoManager, accountId);

  }

  private static void checkUpdate(DaoManager daoManager, int accountId) throws DaoException {
    // Check to see if the update was successful (just for testing)
    try {
      daoManager.startTransaction();

      AccountDao dao = (AccountDao) daoManager.getDao("Account");
      Account account = dao.getAccount(accountId);

      if (!"stanley.jobson@swordfish.com".equals(account.getEmailAddress())) {
        throw new DaoException("DAO failed to update Account.");
      }

      println("Updated: " + account);

      daoManager.commitTransaction();
    } catch (Exception e) {
      daoManager.rollbackTransaction();
      e.printStackTrace();
      throw new DaoException(e.toString());
    }
  }


  public static void deleteCompatibility(int accountId) throws DaoException {

    DaoManager daoManager = DaoConfigCompat.getDaoManager();

    // Delete account
    try {
      daoManager.startTransaction();
      AccountDao dao = (AccountDao) daoManager.getDao("Account");
      Account account = new Account();
      account.setId(accountId);
      dao.deleteAccount(account);
      daoManager.commitTransaction();
    } catch (Exception e) {
      daoManager.rollbackTransaction();
      e.printStackTrace();
      throw new DaoException(e.toString());
    }

    // Check to see if the delete was successful (just for testing)
    checkDelete(daoManager, accountId);

  }

  private static void checkDelete(DaoManager daoManager, int accountId) throws DaoException {
    // Check to see if the delete was successful (just for testing)
    try {
      daoManager.startTransaction();
      AccountDao dao = (AccountDao) daoManager.getDao("Account");
      Account account = dao.getAccount(accountId);
      if (account != null) {
        throw new DaoException("DAO failed to delete Account.");
      }
      println("Deleted (should be null): " + account);
      daoManager.commitTransaction();
    } catch (Exception e) {
      daoManager.rollbackTransaction();
      e.printStackTrace();
      throw new DaoException(e.toString());
    }
  }

  public static void getAccountCompatibility() throws DaoException {
    DaoManager daoManager = DaoConfigCompat.getDaoManager();

    try {
      daoManager.startTransaction();

      AccountDao dao = (AccountDao) daoManager.getDao("Account");
      Account account = dao.getAccount(1);

      // Check to see if the get was successful (just for testing)
      checkGet(account);

      println("DAO Get: " + account);

      daoManager.commitTransaction();
    } catch (Exception e) {
      daoManager.rollbackTransaction();
      e.printStackTrace();
      throw new DaoException(e.toString());
    }
  }

  private static void checkGet(Account account) throws DaoException {
    // Check to see if the get was successful (just for testing)
    if (account == null || account.getAddress() == null) {
      throw new DaoException("DAO failed to get Account.");
    }
  }

}
