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
package com.ibatis.dao.engine.transaction.ojb;

import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoTransaction;
import org.apache.ojb.broker.PersistenceBroker;

public class OjbBrokerDaoTransaction
    implements DaoTransaction {

  private PersistenceBroker broker;

  public OjbBrokerDaoTransaction(final PersistenceBroker brk) {

    broker = brk;

    try {
      broker.beginTransaction();
    } catch (final Throwable t) {
      throw new DaoException("Error starting OJB broker transaction.  Cause: " + t, t);
    }
  }

  public void commit() {
    try {
      broker.commitTransaction();
    } catch (final Throwable t) {
      throw new DaoException("Error committing OJB broker transaction. Cause: " + t);
    } finally {
        if (broker != null) {
            broker.close();
        }
    }
  }

  public void rollback() {
    try {
      broker.abortTransaction();
    } catch (final Throwable t) {
      throw new DaoException("Error ending OJB broker transaction.  Cause: " + t);
    } finally {
        if (broker != null) {
            broker.close();
        }
    }
  }

  public PersistenceBroker getBroker() {
    return broker;
  }

}
