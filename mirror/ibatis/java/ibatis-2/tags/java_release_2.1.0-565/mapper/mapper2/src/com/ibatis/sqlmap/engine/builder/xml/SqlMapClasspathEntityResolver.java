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
package com.ibatis.sqlmap.engine.builder.xml;

import com.ibatis.common.resources.Resources;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.InputStream;

/**
 * Offline entity resolver for the iBATIS DTDs
 */
public class SqlMapClasspathEntityResolver implements EntityResolver {

  private static final String SYSTEM_ID_SQL_MAP_CONFIG = "http://www.ibatis.com/dtd/sql-map-config-2.dtd";
  private static final String SYSTEM_ID_SQL_MAP = "http://www.ibatis.com/dtd/sql-map-2.dtd";
  private static final String DTD_PATH_SQL_MAP_CONFIG = "com/ibatis/sqlmap/engine/builder/xml/sql-map-config-2.dtd";
  private static final String DTD_PATH_SQL_MAP = "com/ibatis/sqlmap/engine/builder/xml/sql-map-2.dtd";

  /**
   * Converts a public DTD into a local one
   *
   * @param publicId Unused but required by EntityResolver interface
   * @param systemId The DTD that is being requested
   * @return The InputSource for the DTD
   * @throws SAXException If anything goes wrong
   */
  public InputSource resolveEntity(String publicId, String systemId)
      throws SAXException {
    InputSource source = null;

    try {
      if (systemId.equals(SYSTEM_ID_SQL_MAP_CONFIG)) {
        InputStream in = Resources.getResourceAsStream(DTD_PATH_SQL_MAP_CONFIG);
        source = new InputSource(in);
      } else if (systemId.equals(SYSTEM_ID_SQL_MAP)) {
        InputStream in = Resources.getResourceAsStream(DTD_PATH_SQL_MAP);
        source = new InputSource(in);
      } else {
        source = null;
      }
    } catch (Exception e) {
      throw new SAXException(e.toString());
    }

    return source;
  }

}
