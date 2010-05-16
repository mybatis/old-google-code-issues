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
package com.ibatis.db.sqlmap.upgrade;

import com.ibatis.common.exception.NestedRuntimeException;

import java.io.IOException;

public class SqlMapUpgradeTool {

  private static final SqlMapXmlConverter CONVERTER = new SqlMapXmlConverter();

  private SqlMapUpgradeTool() {
  }

  public static void main(String[] args) {
    try {
      if (args.length < 2 || args.length > 3) {
        System.out.println("Usage:\n\njava " + SqlMapUpgradeTool.class.getName() + " [InputXMLFile] [OutputXMLFile]\n\n");
        return;
      } else if (args.length == 2) {
        CONVERTER.convertFile(args[0], args[1]);
      } else if (args.length == 3) {
        // Backward compatibility before autodetect
        CONVERTER.convertFile(args[1], args[2]);
      }
    } catch (IOException e) {
      throw new NestedRuntimeException("Error running SQL Map Upgrade Tool.  Cause: " + e, e);
    }
  }


}
