package compatibility.dao.iface;

import com.ibatis.db.dao.Dao;
import com.ibatis.db.dao.DaoException;
import compatibility.domain.Account;

import java.util.List;


public interface AccountDao extends Dao {

  public Account getAccount(int accountId) throws DaoException;

  public List getAccountList() throws DaoException;

  public void insertAccount(Account account) throws DaoException;

  public void updateAccount(Account account) throws DaoException;

  public void deleteAccount(Account account) throws DaoException;


}
