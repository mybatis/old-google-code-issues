/*
 * Copyright 2011 The myBatis Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mybatis.scala

/** Provides main configuration classes.
  * = Basic usage =
  * Usual steps are:
  *  - Load the configuration
  *  - Add a configuration space
  *  - Create the persistenceContext
  *
  * == Code sample ==
  * {{{
  * val config = Configuration("META-INF/mybatis.xml")
  * config.addSpace("ns") { space =>
  *   space ++= MyDAO
  * }
  * val persistenceContext = config.createPersistenceContext
  * }}}
  */
package object config {

}