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
package com.ibatis.common.util;



import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * This is a pool of Throttle objects (!)
 */
public class ThrottledPool {

  private Throttle throttle;

  private Class type;
  private List pool;

  /**
   * Create a ThrottledPool for a Class
   * @param type - the type of objects being managed
   * @param size - the size of the pool
   */
  public ThrottledPool(Class type, int size) {
    try {
      this.throttle = new Throttle(size);
      this.type = type;
      this.pool = Collections.synchronizedList(new ArrayList(size));
      for (int i=0; i < size; i++) {
        this.pool.add(type.newInstance());
      }
    } catch (Exception e) {
      throw new RuntimeException("Error instantiating class.  Cause: " + e, e);
    }
  }

  /**
   * Pop an object from the pool
   * @return - the Object
   */
  public Object pop() {
    throttle.increment();
    return pool.remove(0);
  }

  /**
   * Push an object onto the pool
   * @param o - the object to put into the pool
   */
  public void push(Object o) {
    if (o != null && o.getClass() == type) {
      pool.add(o);
      throttle.decrement();
    }
  }

}