package net.sf.hibernate;

import net.sf.hibernate.type.Type;

import java.sql.Connection;
import java.io.Serializable;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;


/**
 * Created by IntelliJ IDEA.
 * User: Clinton
 * Date: 18-Jan-2005
 * Time: 11:00:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Session {
    public void flush() throws HibernateException {
    }

    public void setFlushMode(FlushMode flushMode) {
    }

    public FlushMode getFlushMode() {
        return null;
    }

    public SessionFactory getSessionFactory() {
        return null;
    }

    public Connection connection() throws HibernateException {
        return null;
    }

    public Connection disconnect() throws HibernateException {
        return null;
    }

    public void reconnect() throws HibernateException {
    }

    public void reconnect(Connection connection) throws HibernateException {
    }

    public Connection close() throws HibernateException {
        return null;
    }

    public void cancelQuery() throws HibernateException {
    }

    public boolean isOpen() {
        return false;
    }

    public boolean isConnected() {
        return false;
    }

    public boolean isDirty() throws HibernateException {
        return false;
    }

    public Serializable getIdentifier(Object o) throws HibernateException {
        return null;
    }

    public boolean contains(Object o) {
        return false;
    }

    public void evict(Object o) throws HibernateException {
    }

    public Object load(Class aClass, Serializable serializable, LockMode lockMode) throws HibernateException {
        return null;
    }

    public Object load(Class aClass, Serializable serializable) throws HibernateException {
        return null;
    }

    public void load(Object o, Serializable serializable) throws HibernateException {
    }

    public void replicate(Object o, ReplicationMode replicationMode) throws HibernateException {
    }

    public Serializable save(Object o) throws HibernateException {
        return null;
    }

    public void save(Object o, Serializable serializable) throws HibernateException {
    }

    public void saveOrUpdate(Object o) throws HibernateException {
    }

    public void update(Object o) throws HibernateException {
    }

    public void update(Object o, Serializable serializable) throws HibernateException {
    }

    public Object saveOrUpdateCopy(Object o) throws HibernateException {
        return null;
    }

    public Object saveOrUpdateCopy(Object o, Serializable serializable) throws HibernateException {
        return null;
    }

    public void delete(Object o) throws HibernateException {
    }

    public List find(String s) throws HibernateException {
        return null;
    }

    public List find(String s, Object o, Type type) throws HibernateException {
        return null;
    }

    public List find(String s, Object[] objects, Type[] types) throws HibernateException {
        return null;
    }

    public Iterator iterate(String s) throws HibernateException {
        return null;
    }

    public Iterator iterate(String s, Object o, Type type) throws HibernateException {
        return null;
    }

    public Iterator iterate(String s, Object[] objects, Type[] types) throws HibernateException {
        return null;
    }

    public Collection filter(Object o, String s) throws HibernateException {
        return null;
    }

    public Collection filter(Object o, String s, Object o1, Type type) throws HibernateException {
        return null;
    }

    public Collection filter(Object o, String s, Object[] objects, Type[] types) throws HibernateException {
        return null;
    }

    public int delete(String s) throws HibernateException {
        return 0;
    }

    public int delete(String s, Object o, Type type) throws HibernateException {
        return 0;
    }

    public int delete(String s, Object[] objects, Type[] types) throws HibernateException {
        return 0;
    }

    public void lock(Object o, LockMode lockMode) throws HibernateException {
    }

    public void refresh(Object o) throws HibernateException {
    }

    public void refresh(Object o, LockMode lockMode) throws HibernateException {
    }

    public LockMode getCurrentLockMode(Object o) throws HibernateException {
        return null;
    }

    public Transaction beginTransaction() throws HibernateException {
        return null;
    }

    public Criteria createCriteria(Class aClass) {
        return null;
    }

    public Query createQuery(String s) throws HibernateException {
        return null;
    }

    public Query createFilter(Object o, String s) throws HibernateException {
        return null;
    }

    public Query getNamedQuery(String s) throws HibernateException {
        return null;
    }

    public Query createSQLQuery(String s, String s1, Class aClass) {
        return null;
    }

    public Query createSQLQuery(String s, String[] strings, Class[] classes) {
        return null;
    }

    public void clear() {
    }

    public Object get(Class aClass, Serializable serializable) throws HibernateException {
        return null;
    }

    public Object get(Class aClass, Serializable serializable, LockMode lockMode) throws HibernateException {
        return null;
    }
}
