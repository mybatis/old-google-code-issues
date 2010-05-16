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

import oracle.toplink.exceptions.TopLinkException;
import oracle.toplink.sessions.Session;
import oracle.toplink.sessions.UnitOfWork;
import oracle.toplink.threetier.Server;

import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoTransaction;

/**
 * The <code>ToplinkDaoTransaction</code> class represents an abstract
 * DaoTransaction implemented using Toplink. Toplink provides transaction
 * management methods to commit and rollback transactions.
 * 
 * @author Wayne Gentile
 * @version $Revision$ $Date$
 */
public class ToplinkDaoTransaction implements DaoTransaction {

	//~ Instance fields
	// --------------------------------------------------------

	private Server server;

	private Session session;

	private UnitOfWork unitOfWork;

	private boolean commmitted = false;

	//~ Constructors
	// -----------------------------------------------------------

	/**
	 * Constructor for the <code>ToplinkDaoTransaction</code> class that
	 * accepts a Toplink Server object for creating client sessions.
	 * 
	 * @param uow
	 *            The Toplink UnitOfWork associated with the transaction
	 * @param server
	 *            The Toplink server used to acquire client sessions
	 *
	 * @throws DaoException
	 *             If a DaoException is thrown
	 */
	public ToplinkDaoTransaction(UnitOfWork uow, Server server)
			throws DaoException {

		// Check arguments
		if (server == null) {

			throw new DaoException("Toplink Server not available");

		}

		// Set the server settings
		this.unitOfWork = uow;
		this.server = server;

	}

	//~ Methods
	// ----------------------------------------------------------------

	/**
	 * Gets the Toplink Session associated with this transaction.
	 * 
	 * @return The Toplink Session
	 * 
	 * @throws DaoException
	 *             If a data access exception occurs
	 */
	public Session getSession() throws DaoException {

		// Get a session
		try {

			if (session == null) {

				session = server.acquireClientSession();

			}

			return session;

		} catch (TopLinkException e) {

			throw new DaoException("Error aquiring Session", e);

		}

	}

	/**
	 * Gets the active unit of work.
	 * 
	 * @return The unitOfWork instance
	 * 
	 * @throws DaoException
	 *             If a data access exception occurs
	 */
	public UnitOfWork getUnitOfWork() throws DaoException {

		try {

			if (unitOfWork == null) {

				unitOfWork = getSession().acquireUnitOfWork();

			}

			return unitOfWork;

		} catch (TopLinkException e) {

			throw new DaoException("Error acquiring UnitOfWork.", e);

		}

	}

	/**
	 * Commits all pending changes to persistent storage.
	 * 
	 * @throws DaoException
	 *             If a data access exception occurs
	 */
	public void commit() throws DaoException {

		// Make sure the transaction has not been committed
		if (commmitted) {

			throw new DaoException("Transaction already committed");

		}

		// Now try to commit the transaction
		try {

			// Check for UnitOfWork
			if (unitOfWork != null) {

				// UnitOfWork was not lazily loaded
				unitOfWork.commit();
				unitOfWork.release();
				session.release();

			}

		} catch (TopLinkException e) {

			throw new DaoException("Error committing transaction", e);

		}
		commmitted = true;

	}

	/**
	 * Rollback all pending changes to persistent storage and revert changes
	 * back to the original state.
	 * 
	 * @throws DaoException
	 *             If a data access exception occurs
	 */
	public void rollback() throws DaoException {

		// Commit the transaction if it has not been committed
		if (!commmitted) {

			try {

				// Make sure the UOW is still active before rollback
				if ((unitOfWork != null) && unitOfWork.isActive()) {

					unitOfWork.revertAndResume();
					unitOfWork.release();
					session.release();

				}

			} catch (TopLinkException e) {

				throw new DaoException("Error rolling back transaction", e);

			}

		}

	}

	/**
	 * Clean up outstanding units of work and sessions.
	 * 
	 * @throws Throwable
	 *             If any exception occurs
	 */
	protected void finalize() throws Throwable {

		super.finalize();

		// Commit outstanding transactions
		if (unitOfWork.isActive()) {

			commit();

		}

		// Make sure everything is cleaned up
		if ((session != null) && (session.isConnected())) {

			session.release();

		}

	}

}