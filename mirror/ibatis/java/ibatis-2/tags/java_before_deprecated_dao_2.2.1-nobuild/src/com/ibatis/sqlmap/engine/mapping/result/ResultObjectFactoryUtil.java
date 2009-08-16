/*
 *  Copyright 2006 The Apache Software Foundation
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
package com.ibatis.sqlmap.engine.mapping.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibatis.common.resources.Resources;

/**
 * @author Jeff Butler
 * 
 */
public class ResultObjectFactoryUtil {

  /**
   * Utility class - no instances
   */
  private ResultObjectFactoryUtil() {
    super();
  }

  /**
   * Algorithm:
   * 
   * <ul>
   *   <li>If factory is null, then create object internally()</li>
   *   <li>Otherwise try to create object through factory</li>
   *   <li>If null returned from factory, then create object internally</li>
   * </ul>
   * 
   * This allows the factory to selectively create objects, also allows for
   * the common possibility that a factory is not configured.
   * 
   * @param factory
   *          the factory to use. May be null!
   * @param statementId
   *          the ID of the statement that generated the call to this method
   * @param clazz
   *          the type of object to create
   * @return a new instance of the specified class.  The instance must
   *   be castable to the specified class.
   * @throws InstantiationException
   *           if the instance cannot be created. If you throw this Exception,
   *           iBATIS will throw a runtime exception in response and will end.
   * @throws IllegalAccessException
   *           if the constructor cannot be accessed. If you throw this
   *           Exception, iBATIS will throw a runtime exception in response and
   *           will end.
   */
  public static Object createObjectThroughFactory(ResultObjectFactory factory,
      String statementId, Class clazz) throws InstantiationException,
      IllegalAccessException {
    
    Object obj;
    if (factory == null) {
      obj = createObjectInternally(clazz);
    } else {
      obj = factory.createInstance(statementId, clazz);
      if (obj == null) {
        obj = createObjectInternally(clazz);
      }
    }
    
    return obj;
  }

  /**
   * This method creates object using iBATIS' normal mechanism.  We
   * translate List and Collection to ArrayList, and Set to HashSet
   * because these interfaces may be requested in nested resultMaps
   * and we want to supply default implementations.
   * 
   * @param clazz
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  private static Object createObjectInternally(Class clazz) throws InstantiationException, IllegalAccessException {
    Class classToCreate;
    if (clazz == List.class || clazz == Collection.class) {
      classToCreate = ArrayList.class;
    } else if (clazz == Set.class) {
      classToCreate = HashSet.class;
    } else {
      classToCreate = clazz;
    }
    
    Object obj = Resources.instantiate(classToCreate);
    return obj;
  }
}
