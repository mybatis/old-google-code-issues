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
package com.ibatis.sqlmap.engine.accessplan;

import com.ibatis.common.beans.ClassInfo;
import com.ibatis.common.exception.NestedRuntimeException;

import java.lang.reflect.Method;

/**
 * Property access plan (for working with beans)
 */
public class PropertyAccessPlan extends BaseAccessPlan {

  protected static final Object[] NO_ARGUMENTS = new Object[0];

  protected Method[] setters;
  protected Method[] getters;

  PropertyAccessPlan(Class clazz, String[] propertyNames) {
    super(clazz, propertyNames);
    setters = getSetters(propertyNames);
    getters = getGetters(propertyNames);
  }

  public void setProperties(Object object, Object[] values) {
    try {
      Object[] arg = new Object[1];
      for (int i = 0; i < propertyNames.length; i++) {
        arg[0] = values[i];
        try {
          setters[i].invoke(object, arg);
        } catch (Throwable t) {
          throw ClassInfo.unwrapThrowable(t);
        }
      }
    } catch (Throwable t) {
      throw new NestedRuntimeException("Error setting properties of '" + object + "'.  Cause: " + t, t);
    }
  }

  public Object[] getProperties(Object object) {
    Object[] values = new Object[propertyNames.length];
    try {
      //Object[] arg = new Object[1];
      for (int i = 0; i < propertyNames.length; i++) {
        try {
          values[i] = getters[i].invoke(object, NO_ARGUMENTS);
        } catch (Throwable t) {
          throw ClassInfo.unwrapThrowable(t);
        }
      }
    } catch (Throwable t) {
      throw new NestedRuntimeException("Error getting properties of '" + object + "'.  Cause: " + t, t);
    }
    return values;
  }

}
