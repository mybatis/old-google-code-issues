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
package com.ibatis.sqlmap.engine.scope;

/**
 * An interface to simplify access to different scopes (contexts?)
 */
public interface Scope {

  /**
   * Gets a named object out of the scope
   *
   * @param key - the name of the object to get
   * @return the object
   */
  public Object getAttribute(Object key);

  /**
   * Puts a named value into the scope
   *
   * @param key   - the name of the object to put
   * @param value - the value to associate with that name
   */
  public void setAttribute(Object key, Object value);

  /**
   * Clears all data out of the scope
   */
  public void reset();

}
