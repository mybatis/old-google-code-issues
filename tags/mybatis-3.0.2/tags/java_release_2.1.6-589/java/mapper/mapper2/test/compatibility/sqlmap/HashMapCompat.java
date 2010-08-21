package compatibility.sqlmap;

import com.ibatis.db.sqlmap.SqlMap;
import compatibility.BaseCompat;

import java.sql.SQLException;
import java.util.HashMap;

public class HashMapCompat extends BaseCompat {

  public static void hashMapCompatibility()
      throws SQLException {
    // Get our configured SqlMap instance
    SqlMap sqlMap = SqlMapConfigCompat.getSqlMap();

    try {
      // -- Start the transaction
      sqlMap.startTransaction();

      // -- Get the List of email Strings that are 'like' the String
      // -- passed in as the key. This demonstrates how simple types like
      // -- String and Integer can be used as both parameters and results.
      HashMap map = new HashMap();
      map.put("id", new Integer(1));

      map = (HashMap) sqlMap.executeQueryForObject("getAccountAsHashMap", map);

      println("HashMap: " + map);

      // -- Commit the transaction
      sqlMap.commitTransaction();
    } catch (Exception e) {
      sqlMap.rollbackTransaction();
      e.printStackTrace();
      throw new SQLException(e.toString());
    }
  }

}
