package org.apache.ibatis.reflection;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.lang.reflect.*;

public class ExceptionUtilTest {

  @Test
  public void shouldUnwrapThrowable() {
    Exception exception = new Exception();
    assertEquals(exception, ExceptionUtil.unwrapThrowable(exception));
    assertEquals(exception, ExceptionUtil.unwrapThrowable(new InvocationTargetException(exception, "test")));
    assertEquals(exception, ExceptionUtil.unwrapThrowable(new UndeclaredThrowableException(exception, "test")));
  }


}
