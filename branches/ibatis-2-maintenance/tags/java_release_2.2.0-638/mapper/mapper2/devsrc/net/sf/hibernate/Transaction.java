package net.sf.hibernate;
public interface Transaction {
	public void commit() throws HibernateException;
	public void rollback() throws HibernateException;
	public boolean wasRolledBack() throws HibernateException;
	public boolean wasCommitted() throws HibernateException;
}