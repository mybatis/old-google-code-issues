/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ibatis.dao.client.template;

import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.engine.transaction.hibernate.HibernateDaoTransaction;
import net.sf.hibernate.*;
import net.sf.hibernate.type.Type;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A DaoTemplate for Hibernate implementations that provides a
 * convenient method to access the Hibernate Session.
 */
public abstract class HibernateDaoTemplate extends DaoTemplate {

  /**
   * The DaoManager that manages this Dao instance will be passed
   * in as the parameter to this constructor automatically upon
   * instantiation.
   *
   * @param daoManager
   */
  public HibernateDaoTemplate(DaoManager daoManager) {
    super(daoManager);
  }

  /**
   * Gets the Hibernate session associated with the current
   * DaoTransaction that this Dao is working under.
   *
   * @return A Hibernate Session instance.
   */
  protected Session getSession() {
    HibernateDaoTransaction trans = (HibernateDaoTransaction) daoManager.getTransaction(this);
    return trans.getSession();
  }

  public void flush() {
    try {
      getSession().flush();
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void setFlushMode(FlushMode flushMode) {
    getSession().setFlushMode(flushMode);
  }

  public FlushMode getFlushMode() {
    return getSession().getFlushMode();
  }

  public SessionFactory getSessionFactory() {
    return getSession().getSessionFactory();
  }

  public Connection connection() {
    try {
      return getSession().connection();
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Connection disconnect() {
    try {
      return getSession().disconnect();
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void reconnect() {
    try {
      getSession().reconnect();
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void reconnect(Connection connection) {
    try {
      getSession().reconnect(connection);
    } catch (HibernateException e) {
      throw new DaoException(e);
    }
  }

  public Connection close() {
    try {
      return getSession().close();
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void cancelQuery() {
    try {
      getSession().cancelQuery();
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public boolean isOpen() {
    return getSession().isOpen();
  }

  public boolean isConnected() {
    return getSession().isConnected();
  }

  public boolean isDirty() {
    try {
      return getSession().isDirty();
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Serializable getIdentifier(Object o) {
    try {
      return getSession().getIdentifier(o);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public boolean contains(Object o) {
    return getSession().contains(o);
  }

  public void evict(Object o) {
    try {
      getSession().evict(o);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Object load(Class aClass, Serializable serializable, LockMode lockMode) {
    try {
      return getSession().load(aClass, serializable, lockMode);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Object load(Class aClass, Serializable serializable) {
    try {
      return getSession().load(aClass, serializable);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void load(Object o, Serializable serializable) {
    try {
      getSession().load(o, serializable);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void replicate(Object o, ReplicationMode replicationMode) {
    try {
      getSession().replicate(o, replicationMode);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Serializable save(Object o) {
    try {
      return getSession().save(o);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void save(Object o, Serializable serializable) {
    try {
      getSession().save(o, serializable);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void saveOrUpdate(Object o) {
    try {
      getSession().save(o);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void update(Object o) {
    try {
      getSession().update(o);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void update(Object o, Serializable serializable) {
    try {
      getSession().update(o, serializable);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Object saveOrUpdateCopy(Object o) {
    try {
      return getSession().saveOrUpdateCopy(o);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Object saveOrUpdateCopy(Object o, Serializable serializable) {
    try {
      return getSession().saveOrUpdateCopy(o, serializable);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void delete(Object o) {
    try {
      getSession().delete(o);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public List find(String s) {
    try {
      return getSession().find(s);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public List find(String s, Object o, Type type) {
    try {
      return getSession().find(s, o, type);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public List find(String s, Object[] objects, Type[] types) {
    try {
      return getSession().find(s, objects, types);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Iterator iterate(String s) {
    try {
      return getSession().iterate(s);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Iterator iterate(String s, Object o, Type type) {
    try {
      return getSession().iterate(s, o, type);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Iterator iterate(String s, Object[] objects, Type[] types) {
    try {
      return getSession().iterate(s, objects, types);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Collection filter(Object o, String s) {
    try {
      return getSession().filter(o, s);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Collection filter(Object o, String s, Object o1, Type type) {
    try {
      return getSession().filter(o, s, o1, type);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Collection filter(Object o, String s, Object[] objects, Type[] types) {
    try {
      return getSession().filter(o, s, objects, types);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public int delete(String s) {
    try {
      return getSession().delete(s);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public int delete(String s, Object o, Type type) {
    try {
      return getSession().delete(s, o, type);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public int delete(String s, Object[] objects, Type[] types) {
    try {
      return getSession().delete(s, objects, types);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void lock(Object o, LockMode lockMode) {
    try {
      getSession().lock(o, lockMode);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void refresh(Object o) {
    try {
      getSession().refresh(o);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public void refresh(Object o, LockMode lockMode) {
    try {
      getSession().refresh(o, lockMode);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public LockMode getCurrentLockMode(Object o) {
    try {
      return getSession().getCurrentLockMode(o);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Transaction beginTransaction() {
    try {
      return getSession().beginTransaction();
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Criteria createCriteria(Class aClass) {
    return getSession().createCriteria(aClass);
  }

  public Query createQuery(String s) {
    try {
      return getSession().createQuery(s);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Query createFilter(Object o, String s) {
    try {
      return getSession().createFilter(o, s);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Query getNamedQuery(String s) {
    try {
      return getSession().getNamedQuery(s);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Query createSQLQuery(String s, String s1, Class aClass) {
    return getSession().createSQLQuery(s, s1, aClass);
  }

  public Query createSQLQuery(String s, String[] strings, Class[] classes) {
    return getSession().createSQLQuery(s, strings, classes);
  }

  public void clear() {
    getSession().clear();
  }

  public Object get(Class aClass, Serializable serializable) {
    try {
      return getSession().get(aClass, serializable);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

  public Object get(Class aClass, Serializable serializable, LockMode lockMode) {
    try {
      return getSession().get(aClass, serializable, lockMode);
    } catch (HibernateException e) {
      throw new DaoException("Error occurred in a Hibernate DAO.  Cause: " + e, e);
    }
  }

}
