package compatibility.sqlmap;

import com.ibatis.db.sqlmap.RowHandler;
import com.ibatis.db.sqlmap.SqlMap;
import compatibility.BaseCompat;

import java.sql.SQLException;

public class RowHandlerCompat extends BaseCompat {

  public static void rowHandlerCompatibility()
      throws SQLException {
    // Get our configured SqlMap instance
    SqlMap sqlMap = SqlMapConfigCompat.getSqlMap();

    try {
      // -- Start the transaction
      sqlMap.startTransaction();

      // -- Get the List of Account objects.  No key is required for this
      // -- particular query.

      RowHandler rowHandler = new RowHanderCompat();
      sqlMap.executeQueryWithRowHandler("getAllAccounts", null, rowHandler);

      // -- Commit the transaction
      sqlMap.commitTransaction();
    } catch (Exception e) {
      sqlMap.rollbackTransaction();
      e.printStackTrace();
      throw new SQLException(e.toString());
    }
  }


}
