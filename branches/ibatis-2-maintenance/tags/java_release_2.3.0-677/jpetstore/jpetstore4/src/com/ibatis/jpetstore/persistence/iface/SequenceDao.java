/**
 * User: Clinton Begin
 * Date: Jul 13, 2003
 * Time: 8:19:29 PM
 */
package com.ibatis.jpetstore.persistence.iface;


public interface SequenceDao {

  public int getNextId(String name);

}
