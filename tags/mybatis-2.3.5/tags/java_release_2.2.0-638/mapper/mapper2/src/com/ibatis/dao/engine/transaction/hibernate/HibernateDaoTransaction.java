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
package com.ibatis.dao.engine.transaction.hibernate;

import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.engine.transaction.ConnectionDaoTransaction;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;

import java.sql.Connection;

public class HibernateDaoTransaction implements ConnectionDaoTransaction {

  private Session session;
  private Transaction transaction;

  public HibernateDaoTransaction(SessionFactory factory) {
    try {
      this.session = factory.openSession();
      this.transaction = session.beginTransaction();
    } catch (HibernateException e) {
      throw new DaoException("Error starting Hibernate transaction.  Cause: " + e, e);
    }
  }

  public void commit() {
    try {
      try {
        transaction.commit();
      } finally {
        session.close();
      }
    } catch (HibernateException e) {
      throw new DaoException("Error committing Hibernate transaction.  Cause: " + e);
    }
  }

  public void rollback() {
    try {
      try {
        transaction.rollback();
      } finally {
        session.close();
      }
    } catch (HibernateException e) {
      throw new DaoException("Error ending Hibernate transaction.  Cause: " + e);
    }
  }

  public Session getSession() {
    return session;
  }

  public Connection getConnection() {
    try {
      return session.connection();
    } catch (HibernateException e) {
      throw new DaoException("Error occurred getting connection from Hibernate Session.  Cause: " + e, e);
    }
  }

}
