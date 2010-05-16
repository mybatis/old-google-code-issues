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
package com.ibatis.dao.engine.transaction.toplink;

import java.util.Properties;

import oracle.toplink.publicinterface.UnitOfWork;
import oracle.toplink.threetier.Server;
import oracle.toplink.tools.sessionmanagement.SessionManager;

import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoTransaction;
import com.ibatis.dao.engine.transaction.DaoTransactionManager;

/**
 * The <code>ToplinkDaoTransactionManager</code> is used by the Dao framework
 * to manage transactions for the Toplink DAO implementation.
 * 
 * @author Wayne Gentile
 * @version $Revision$ $Date$
 */
public class ToplinkDaoTransactionManager implements DaoTransactionManager {

	//~ Instance fields
	// --------------------------------------------------------

	private Server server;

	private UnitOfWork uow;

	//~ Methods
	// ----------------------------------------------------------------

	/**
	 * Commits pending object changes to permanent storage.
	 * 
	 * @param transaction
	 *            A previously started transaction.
	 * 
	 * @throws DaoException
	 *             If a data access exception is thrown
	 */
	public void commitTransaction(DaoTransaction transaction)
			throws DaoException {

		((ToplinkDaoTransaction) transaction).commit();

	}

	/**
	 * Called by the DAO framework upon instantiation to set configuration
	 * properties for the manager.
	 * 
	 * <p>
	 * Properties are specified in the iBATIS dao.xml file.
	 * </p>
	 * 
	 * @param properties
	 *            The properties associated with the transaction manager
	 * 
	 * @throws DaoException
	 *             If a DaoException occurs
	 */
	public void configure(Properties properties) throws DaoException {

		// Get the name of the session and create it
		String sessionName = null;

		try {

			SessionManager manager = SessionManager.getManager();

			// Get the name of the session and create it
			sessionName = properties.getProperty("session.name");
			server = (Server) manager.getSession(sessionName,
					ToplinkDaoTransactionManager.class.getClassLoader());

		} catch (Exception e) {

			throw new DaoException(
					"Error configuring Toplink environment for session: "
							+ sessionName);

		}

	}

	/**
	 * Reverts pending object changes and returns objects to their original
	 * state.
	 * 
	 * @param transaction
	 *            A previously started transaction.
	 * 
	 * @throws DaoException
	 *             If a data access exception is thrown
	 */
	public void rollbackTransaction(DaoTransaction transaction)
			throws DaoException {

		((ToplinkDaoTransaction) transaction).rollback();

	}

	/**
	 * Starts a new transaction. Changes to objects will not be committed until
	 * further instructions are executed.
	 * 
	 * @return The DaoTransaction that has been started
	 * 
	 * @throws DaoException
	 *             If a data access exception is thrown
	 */
	public DaoTransaction startTransaction() throws DaoException {

		ToplinkDaoTransaction trans = new ToplinkDaoTransaction(uow, server);

		return trans;

	}

}