package compatibility.sqlmap;

import com.ibatis.db.sqlmap.SqlMap;
import compatibility.BaseCompat;
import compatibility.domain.Account;

import java.sql.SQLException;

public class QueryForObjectCompat extends BaseCompat {

  public static void queryForObjectCompatibility()
      throws SQLException {
    // Get our configured SqlMap instance
    SqlMap sqlMap = SqlMapConfigCompat.getSqlMap();

    try {
      // -- Start the transaction
      sqlMap.startTransaction();

      // -- Get the Account object using a simple type as a key
      Account account = (Account) sqlMap.executeQueryForObject("getAccount", new Integer(1));

      // -- You can also preallocate the object (and prepopulate it) and
      // -- pass it in as a parameter
      account = new Account();
      account = (Account) sqlMap.executeQueryForObject("getAccountIdAndName", new Integer(1), account);
      account = (Account) sqlMap.executeQueryForObject("getAccountEmail", new Integer(1), account);

      println("Account: " + account);

      // -- Commit the transaction
      sqlMap.commitTransaction();
    } catch (Exception e) {
      // -- With the introduction of stacked transactions, you'll
      // -- want to get in the habit of only calling rollback
      // -- in the event of an error.  Otherwise you might rollback
      // -- the next transaction in the stack.
      System.err.print("CAUSE >>>>>>>>> ");
      e.printStackTrace(System.err);
      sqlMap.rollbackTransaction();
      throw new SQLException(e.toString());
    }
  }

}
