package net.sf.hibernate;

/**
 * Created by IntelliJ IDEA.
 * User: Clinton
 * Date: 18-Jan-2005
 * Time: 11:00:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class HibernateException extends Exception {

    public HibernateException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public HibernateException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public HibernateException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public HibernateException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
