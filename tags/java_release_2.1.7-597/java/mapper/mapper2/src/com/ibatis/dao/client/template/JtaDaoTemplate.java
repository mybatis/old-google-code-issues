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
package com.ibatis.dao.client.template;

import com.ibatis.dao.client.DaoManager;

/**
 * <b>DEPRECATED</b>
 * This is now exactly the same as the JdbcDaoTemplate, and therefore
 * has beend deprecated.  There is no behavioural difference between
 * the two.
 *
 * @deprecated Use JdbcDaoTemplate instead.  Both have the same
 *             interface, so simply changing your code to extend JdbcDaoTemplate
 *             should work without any change in behaviour.
 */
public abstract class JtaDaoTemplate extends JdbcDaoTemplate {

  public JtaDaoTemplate(DaoManager daoManager) {
    super(daoManager);
  }

}
