package com.ibatis.jpetstore.persistence;

import com.ibatis.common.resources.Resources;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.DaoManagerBuilder;

import java.io.Reader;

/**
 * <p/>
 * Date: Mar 6, 2004 11:24:18 PM
 * 
 * @author Clinton Begin
 */
public class DaoConfig {

  private static final DaoManager daoManager;

  static {

    try {
      String resource = "com/ibatis/jpetstore/persistence/dao.xml";
      Reader reader = Resources.getResourceAsReader(resource);
      daoManager = DaoManagerBuilder.buildDaoManager(reader);
    } catch (Exception e) {
      throw new RuntimeException("Could not initialize DaoConfig.  Cause: " + e);
    }
  }

  public static DaoManager getDaomanager() {
    return daoManager;
  }

}
