package net.sf.hibernate;
import net.sf.hibernate.exception.SQLExceptionConverter;
import net.sf.hibernate.metadata.ClassMetadata;
import net.sf.hibernate.metadata.CollectionMetadata;

import javax.naming.Referenceable;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
public interface SessionFactory extends Referenceable, Serializable {
	public Session openSession(Connection connection);
	public Session openSession(Interceptor interceptor) throws HibernateException;
	public Session openSession(Connection connection, Interceptor interceptor);
	public Session openSession() throws HibernateException;
	public Databinder openDatabinder() throws HibernateException;
	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException;
	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException;
	public Map getAllClassMetadata() throws HibernateException;
	public Map getAllCollectionMetadata() throws HibernateException;
	public void close() throws HibernateException;
	public void evict(Class persistentClass) throws HibernateException;
	public void evict(Class persistentClass, Serializable id) throws HibernateException;
	public void evictCollection(String roleName) throws HibernateException;
	public void evictCollection(String roleName, Serializable id) throws HibernateException;
	public void evictQueries() throws HibernateException;
	public void evictQueries(String cacheRegion) throws HibernateException;
	public SQLExceptionConverter getSQLExceptionConverter();
}
