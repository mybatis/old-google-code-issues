package compatibility.sqlmap;

import com.ibatis.common.exception.NestedRuntimeException;
import com.ibatis.common.resources.Resources;
import com.ibatis.db.sqlmap.SqlMap;
import com.ibatis.db.sqlmap.XmlSqlMapBuilder;

import java.io.Reader;

public class SqlMapConfigCompat {

  // -- SqlMap instances are thread safe.
  protected static final SqlMap sqlMap;

  static {

    try {

      // -- Load the SQL Map configuration XML file a Reader or a File.
      // -- The iBATIS common library includes a handy Resources utility class perfect for this.
      Reader reader = Resources.getResourceAsReader("compatibility/sqlmap/maps/SqlMapConfig.xml");

      // -- You can disable XML validation, it is enabled by default.
      // XmlSqlMapBuilder.setValidationEnabled(true);

      // Use the XmlSqlMapBuilder to build the SQL Map from the reader.
      sqlMap = XmlSqlMapBuilder.buildSqlMap(reader);

    } catch (Exception e) {
      // If we fail here we just want to fail gracefully.
      // Errors at this point will be unrecoverable and we
      // want to know about them at system startup time,
      // not after hours of operation.
      throw new NestedRuntimeException("Error initializing BaseSqlMapComat.  Cause: " + e, e);
    }
  }

  public static SqlMap getSqlMap() {
    return sqlMap;
  }

}
