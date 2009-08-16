package com.ibatis.dao.impl.jdbc;

import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.JdbcDaoTemplate;
import com.ibatis.dao.iface.AccountDao;
import testdomain.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcAccountDao extends JdbcDaoTemplate implements AccountDao {

  private static final String INSERT =
      "insert into ACCOUNT ( ACC_ID, ACC_FIRST_NAME, ACC_LAST_NAME, ACC_EMAIL ) " +
      "  values ( ?, ?, ?, ? )";

  private static final String UPDATE =
      "update ACCOUNT set" +
      "    ACC_FIRST_NAME = ?," +
      "    ACC_LAST_NAME = ?," +
      "    ACC_EMAIL = ?" +
      "  where" +
      "    ACC_ID = ?";

  private static final String DELETE =
      "delete from ACCOUNT where ACC_ID = ?";

  private static final String SELECT =
      "select" +
      "    ACC_ID          as id," +
      "    ACC_FIRST_NAME  as firstName," +
      "    ACC_LAST_NAME   as lastName," +
      "    ACC_EMAIL       as emailAddress" +
      "  from ACCOUNT" +
      "  where ACC_ID = ?";

  public JdbcAccountDao(DaoManager daoManager) {
    super(daoManager);
  }

  public void createAccount(Account account) {
    Connection conn = getConnection();
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement(INSERT);
      ps.setInt(1, account.getId());
      ps.setString(2, account.getFirstName());
      ps.setString(3, account.getLastName());
      ps.setString(4, account.getEmailAddress());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new DaoException("Error creating Account.  Cause: " + e, e);
    } finally {
      closePreparedStatement(ps);
    }
  }

  public void saveAccount(Account account) {
    Connection conn = getConnection();
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement(UPDATE);
      ps.setString(1, account.getFirstName());
      ps.setString(2, account.getLastName());
      ps.setString(3, account.getEmailAddress());
      ps.setInt(4, account.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new DaoException("Error saving Account.  Cause: " + e, e);
    } finally {
      closePreparedStatement(ps);
    }
  }

  public void removeAccount(Account account) {
    Connection conn = getConnection();
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement(DELETE);
      ps.setInt(1, account.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new DaoException("Error removing Account.  Cause: " + e, e);
    } finally {
      closePreparedStatement(ps);
    }
  }

  public Account findAccount(int id) {
    Account account = null;
    Connection conn = getConnection();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(SELECT);
      ps.setInt(1, id);
      rs = ps.executeQuery();
      while (rs.next()) {
        account = new Account();
        account.setId(rs.getInt("id"));
        account.setFirstName(rs.getString("firstName"));
        account.setLastName(rs.getString("lastName"));
        account.setEmailAddress(rs.getString("emailAddress"));
      }
    } catch (SQLException e) {
      throw new DaoException("Error finding Account.  Cause: " + e, e);
    } finally {
      closeResultSet(rs);
      closePreparedStatement(ps);
    }
    return account;
  }

  private void closePreparedStatement(PreparedStatement ps) {
    try {
      if (ps != null) ps.close();
    } catch (SQLException e) {
      //ignore
    }
  }

  private void closeResultSet(ResultSet rs) {
    try {
      if (rs != null) rs.close();
    } catch (SQLException e) {
      //ignore
    }
  }

}
