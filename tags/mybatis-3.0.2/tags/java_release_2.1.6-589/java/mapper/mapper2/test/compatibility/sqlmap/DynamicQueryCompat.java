package compatibility.sqlmap;

import com.ibatis.db.sqlmap.SqlMap;
import compatibility.BaseCompat;
import compatibility.domain.Account;

import java.sql.SQLException;
import java.util.List;

public class DynamicQueryCompat extends BaseCompat {

  public static void dynamicQueryCompatibility()
      throws SQLException {
    // Get our configured SqlMap instance
    SqlMap sqlMap = SqlMapConfigCompat.getSqlMap();

    try {
      // -- Start the transaction
      sqlMap.startTransaction();

      // -- This account will be used to build the dynamic SQL.
      // -- The effect is basically a query by example.
      Account account = new Account();
      account.setFirstName("Clinton");
      account.setLastName("Begin");

      // -- Get the List of Account objects that are like the account parameter.
      // -- Notice the API is exactly the same for normal mapped statements.
      List list = sqlMap.executeQueryForList("dynamicGetAccountList", account);

      for (int i = 0; i < list.size(); i++) {
        println("First/Last: " + list.get(i));
      }

      // -- Using the same query, we can search based on the email field only.
      account = new Account();
      account.setEmailAddress("%somewhere%");

      // -- Get the List of Account objects that are like the account parameter.
      list = sqlMap.executeQueryForList("dynamicGetAccountList", account);

      for (int i = 0; i < list.size(); i++) {
        println("Email/Like: " + list.get(i));
      }

      // -- Using the same query, we can search based on the id field only.
      account = new Account();
      account.setId(3);

      // -- Get the List of Account objects that are like the account parameter.
      list = sqlMap.executeQueryForList("dynamicGetAccountList", account);

      for (int i = 0; i < list.size(); i++) {
        println("ID/Equal: " + list.get(i));
      }

      // -- Commit the transaction
      sqlMap.commitTransaction();
    } catch (Exception e) {
      sqlMap.rollbackTransaction();
      e.printStackTrace();
      throw new SQLException(e.toString());
    }
  }


}
