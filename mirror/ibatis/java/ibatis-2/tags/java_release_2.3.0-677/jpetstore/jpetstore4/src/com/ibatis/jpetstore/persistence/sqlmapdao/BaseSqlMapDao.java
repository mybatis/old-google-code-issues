/**
 * User: Clinton Begin
 * Date: Jul 13, 2003
 * Time: 7:24:04 PM
 */
package com.ibatis.jpetstore.persistence.sqlmapdao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;

public class BaseSqlMapDao extends SqlMapDaoTemplate {

  protected static final int PAGE_SIZE = 4;

  public BaseSqlMapDao(DaoManager daoManager) {
    super(daoManager);
  }

}
