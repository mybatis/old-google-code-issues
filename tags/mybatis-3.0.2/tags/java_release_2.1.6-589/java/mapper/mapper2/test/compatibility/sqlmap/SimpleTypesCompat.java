package compatibility.sqlmap;

import com.ibatis.db.sqlmap.SqlMap;
import compatibility.BaseCompat;

import java.sql.SQLException;
import java.util.List;

public class SimpleTypesCompat extends BaseCompat {

  public static void simpleTypesCompatibility()
      throws SQLException {
    // Get our configured SqlMap instance
    SqlMap sqlMap = SqlMapConfigCompat.getSqlMap();

    try {
      // -- Start the transaction
      sqlMap.startTransaction();

      // -- Get the List of email Strings that are 'like' the String
      // -- passed in as the key. This demonstrates how simple types like
      // -- String and Integer can be used as both parameters and results.
      List list = sqlMap.executeQueryForList("getEmailAddresses", "%somewhere%");

      for (int i = 0; i < list.size(); i++) {
        println("String: " + list.get(i));
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
