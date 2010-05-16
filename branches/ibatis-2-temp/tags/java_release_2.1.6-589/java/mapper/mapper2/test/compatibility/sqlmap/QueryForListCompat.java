package compatibility.sqlmap;

import com.ibatis.db.sqlmap.SqlMap;
import compatibility.BaseCompat;

import java.sql.SQLException;
import java.util.List;

public class QueryForListCompat extends BaseCompat {

  public static void queryForListCompatibility()
      throws SQLException {
    // Get our configured SqlMap instance
    SqlMap sqlMap = SqlMapConfigCompat.getSqlMap();

    try {
      // -- Start the transaction
      sqlMap.startTransaction();

      // -- Get the List of Account objects.  No key is required for this
      // -- particular query.
      List list = sqlMap.executeQueryForList("getAllAccounts", null);

      // -- You can also specify a range of results
      // int skipRecords = 2;
      // int maxResults = 3;
      // list = sqlMap.executeQueryForList("getAllAccounts", null, skipRecords, maxResults);

      for (int i = 0; i < list.size(); i++) {
        println("Account: " + list.get(i));
      }

      list = sqlMap.executeQueryForList("getAllAccountsAsHashMaps", null);

      for (int i = 0; i < list.size(); i++) {
        println("AccountMap: " + list.get(i));
      }

      list = sqlMap.executeQueryForList("getAccount", new Integer(1));
      for (int i = 0; i < list.size(); i++) {
        println("Single: " + list.get(i));
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
