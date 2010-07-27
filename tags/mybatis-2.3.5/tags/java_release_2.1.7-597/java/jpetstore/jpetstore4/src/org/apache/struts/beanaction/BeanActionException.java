package org.apache.struts.beanaction;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * This exception is thrown internally by BeanAction and
 * <p/>
 * can also be used by bean action methods as a general
 * <p/>
 * or base exception.
 * <p/>
 * <p/>
 * <p/>
 * Date: Mar 13, 2004 8:17:00 PM
 *
 * @author Clinton Begin
 */
public class BeanActionException extends NestableRuntimeException {
  public BeanActionException() {
    super();
  }

  public BeanActionException(String string) {
    super(string);
  }

  public BeanActionException(Throwable throwable) {
    super(throwable);
  }

  public BeanActionException(String string, Throwable throwable) {
    super(string, throwable);
  }
}

