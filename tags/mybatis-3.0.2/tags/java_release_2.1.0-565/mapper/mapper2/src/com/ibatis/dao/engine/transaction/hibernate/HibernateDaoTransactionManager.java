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

import com.ibatis.common.resources.Resources;
import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoTransaction;
import com.ibatis.dao.engine.transaction.DaoTransactionManager;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import java.util.Iterator;
import java.util.Properties;

public class HibernateDaoTransactionManager implements DaoTransactionManager {

  private SessionFactory factory;

  public void configure(Properties properties) {
    try {
      Configuration config = new Configuration();

      Iterator it = properties.keySet().iterator();
      while (it.hasNext()) {
        String key = (String) it.next();
        String value = (String) properties.get(key);
        if (key.startsWith("class.")) {
          config.addClass(Resources.classForName(value));
        }
      }

      Properties props = new Properties();
      props.putAll(properties);
      config.setProperties(props);

      factory = config.buildSessionFactory();

    } catch (Exception e) {
      throw new DaoException("Error configuring Hibernate.  Cause: " + e);
    }
  }

  public DaoTransaction startTransaction() {
    return new HibernateDaoTransaction(factory);
  }

  public void commitTransaction(DaoTransaction trans) {
    ((HibernateDaoTransaction) trans).commit();
  }

  public void rollbackTransaction(DaoTransaction trans) {
    ((HibernateDaoTransaction) trans).rollback();
  }
}
