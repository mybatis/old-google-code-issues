/*
 *    Copyright 2009-2011 The MyBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.migration;

import org.apache.ibatis.parsing.PropertyParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

public class MigrationReader extends Reader {

  private static final String LINE_SEPARATOR = System.getProperty("line.separator","\n");

  private Reader target;

  public MigrationReader(Reader source, boolean undo, Properties variables) throws IOException {
    try {
      BufferedReader reader = new BufferedReader(source);
      StringBuilder doBuilder = new StringBuilder();
      StringBuilder undoBuilder = new StringBuilder();
      StringBuilder currentBuilder = doBuilder;
      String line;
      while ((line = reader.readLine()) != null) {
        if (line != null) {
          if (line.trim().startsWith("--//")) {
            if (line.contains("@UNDO")) {
              currentBuilder = undoBuilder;
            }
            line = line.replace("--//", "-- ");
          }
          currentBuilder.append(line);
          currentBuilder.append(LINE_SEPARATOR);
        }
      }
      if (undo) {
        target = new StringReader(PropertyParser.parse(undoBuilder.toString(), variables));
      } else {
        target = new StringReader(PropertyParser.parse(doBuilder.toString(), variables));
      }
    } finally {
      source.close();
    }
  }

  public int read(char[] cbuf, int off, int len) throws IOException {
    return target.read(cbuf, off, len);
  }

  public void close() throws IOException {
    target.close();
  }

}
