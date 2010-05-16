package compatibility.dao.impl.map;

import com.ibatis.db.dao.DaoException;
import com.ibatis.db.sqlmap.SqlMap;
import compatibility.dao.iface.AddressDao;
import compatibility.domain.Address;

public class AddressMapDao extends BaseMapDao implements AddressDao {

  public Address getAddress(int addressId) throws DaoException {
    try {
      // See BaseMapDao for how the sqlMap is retreived.
      SqlMap sqlMap = getSqlMapFromLocalTransaction();
      return (Address) sqlMap.executeQueryForObject("getAddress", new Integer(addressId));
    } catch (Exception e) {
      throw new DaoException(e.toString());
    }
  }

}
