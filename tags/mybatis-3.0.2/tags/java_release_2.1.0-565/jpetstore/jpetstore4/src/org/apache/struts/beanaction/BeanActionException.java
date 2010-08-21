package org.apache.struts.beanaction;

import com.ibatis.common.exception.NestedRuntimeException;

/**
 * This exception is thrown internally by BeanAction and
 * can also be used by bean action methods as a general
 * or base exception.
 * <p/>
 * Date: Mar 13, 2004 8:17:00 PM
 *
 * @author Clinton Begin
 */
public class BeanActionException extends NestedRuntimeException {

  public BeanActionException() {
    super();
  }

  public BeanActionException(String s) {
    super(s);
  }

  public BeanActionException(Throwable throwable) {
    super(throwable);
  }

  public BeanActionException(String s, Throwable throwable) {
    super(s, throwable);
  }

}
