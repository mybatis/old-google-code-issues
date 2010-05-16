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

import java.util.List;

import oracle.toplink.expressions.Expression;
import oracle.toplink.expressions.ExpressionBuilder;
import oracle.toplink.queryframework.ReportQuery;
import oracle.toplink.sessions.Session;
import oracle.toplink.sessions.UnitOfWork;

import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.engine.transaction.toplink.ToplinkDaoTransaction;

/**
 * A DaoTemplate for Toplink implementations that provides a convenient method
 * to access the Toplink Session.
 * 
 * @author Wayne Gentile
 * @version $Revision$ $Date$
 */
public abstract class ToplinkDaoTemplate extends DaoTemplate {

	//~ Constructors
	// -----------------------------------------------------------

	/**
	 * The DaoManager that manages this Dao instance will be passed in as the
	 * parameter to this constructor automatically upon instantiation.
	 * 
	 * @param daoManager
	 *            The Dao manager instance for this template
	 */
	public ToplinkDaoTemplate(DaoManager daoManager) {
		super(daoManager);

	}

	//~ Methods
	// ----------------------------------------------------------------

	/**
	 * Returns a count of the number of objects in a result set given the
	 * specified expression.
	 * 
	 * @param referenceClass
	 *            The reference class to use as instances in the result set
	 * @param expression
	 *            The expression to use as a where clause
	 * 
	 * @return The number of rows returned in the result set after executing the
	 *         query
	 */
	protected int getCount(Class referenceClass, Expression expression) {

		int count = 0;

		// Build the query to retrieve the object
		ExpressionBuilder builder = new ExpressionBuilder();
		ReportQuery query = new ReportQuery(builder);
		query.setReferenceClass(referenceClass);
		query.addCount();
		query.setSelectionCriteria(expression);

		// Execute the query
		List results = (List) getSession().executeQuery(query);
		if ((results != null) && (results.size() > 0)) {

			count = ((Integer) results.get(0)).intValue();

		}

		return count;

	}

	/**
	 * Gets the Toplink session associated with the current DaoTransaction that
	 * this Dao is working under.
	 * 
	 * @return A Toplink Session instance.
	 * 
	 * @throws DaoException
	 *             If a DaoException is thrown
	 */
	protected Session getSession() throws DaoException {

		ToplinkDaoTransaction trans = (ToplinkDaoTransaction) daoManager
				.getTransaction(this);

		return trans.getSession();

	}

	/**
	 * Gets the Toplink UnitOfWork associated with the current DaoTransaction
	 * that this Dao is working under.
	 * 
	 * @return A Toplink UnitOfWork instance.
	 * 
	 * @throws DaoException
	 *             If a DaoException is thrown
	 */
	protected UnitOfWork getUnitOfWork() throws DaoException {

		// Get a UnitOfWork using the TransactionManager
		ToplinkDaoTransaction trans = (ToplinkDaoTransaction) daoManager
				.getTransaction(this);
		UnitOfWork uow = trans.getUnitOfWork();

		if ((uow == null) || !uow.isActive()) {

			throw new DaoException("No active unit of work.");

		}

		return uow;

	}

}