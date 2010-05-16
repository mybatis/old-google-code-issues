package compatibility.dao;

import com.ibatis.common.exception.NestedRuntimeException;
import com.ibatis.common.resources.Resources;
import com.ibatis.db.dao.DaoManager;
import compatibility.BaseCompat;

public class DaoConfigCompat extends BaseCompat {

  // -- DaoManager is thread safe
  protected static final DaoManager daoManager;

  static {
    try {
      // -- Configure the DaoManager by passing in the dao.xml as a Reader.
      // -- Once this is done you can request a DaoManager instance for
      // -- each context that you defined in the dao.xml.

      DaoManager.configure(Resources.getResourceAsReader("compatibility/dao/dao.xml"));


      // -- Return the default DaoManager instance.  Optionally you can
      // -- explicitly state the name of the instance you want to get.
      // -- Explicitly naming the instance is highly recommended when dealing
      // -- with multiple databases.
      // return DaoManager.getInstance ("SqlMap");

      daoManager = DaoManager.getInstance("SqlMap");

    } catch (Exception e) {
      // If we fail here we just want to fail gracefully.
      // Errors at this point will be unrecoverable and we
      // want to know about them at system startup time,
      // not after hours of operation.
      throw new NestedRuntimeException("Error initializing BaseDaoCompat.  Cause: " + e, e);
    }
  }


  public static DaoManager getDaoManager() {
    return daoManager;
  }

}
