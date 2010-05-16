package compatibility.sqlmap;

import com.ibatis.db.sqlmap.SqlMap;
import compatibility.BaseCompat;
import compatibility.domain.Account;

import java.sql.SQLException;

public class InsertCompat extends BaseCompat {

  public static int insertCompatibility()
      throws SQLException {
    // Get our configured SqlMap instance
    SqlMap sqlMap = SqlMapConfigCompat.getSqlMap();

    int insertedId = getNextId();

    try {
      // -- Start the transaction
      sqlMap.startTransaction();

      // -- Create a new Account
      Account newAccount = new Account();
      newAccount.setId(insertedId);
      newAccount.setFirstName("Thomas");
      newAccount.setLastName("Anderson");
      newAccount.setEmailAddress("thomas.anderson@softwarecompany.com");

      // -- INSERT the new Account
      sqlMap.executeUpdate("insertAccount", newAccount);

      // -- Commit the transaction
      sqlMap.commitTransaction();

      // -- Check to see if the record was inserted correctly (just for testing)
      checkInsert(sqlMap, insertedId, newAccount);
    } catch (Exception e) {
      sqlMap.rollbackTransaction();
      e.printStackTrace();
      throw new SQLException(e.toString());
    }
    return insertedId;
  }

  private static void checkInsert(SqlMap sqlMap, int insertedId, Account newAccount) throws SQLException {
    // -- Check to see if the record was inserted correctly (just for testing)
    sqlMap.startTransaction();
    Account account = (Account) sqlMap.executeQueryForObject("getAccount", new Integer(insertedId));
    if (account == null) throw new SQLException("Account was not inserted to the database correctly.");
    boolean isEqual = newAccount.getId() == account.getId();
    if (!isEqual) throw new SQLException("Account was not inserted to the database correctly.");
    println("Inserted: " + account);
    sqlMap.commitTransaction();
  }


}
