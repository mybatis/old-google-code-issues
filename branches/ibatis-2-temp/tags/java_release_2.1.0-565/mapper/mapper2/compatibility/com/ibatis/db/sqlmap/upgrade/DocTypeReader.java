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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * This class strips the doctype from an XML stream
 * because java.xml.transform can't disable validation
 * and/or loading of the DTD in a standard way, which
 * causes problems for those running without a network.
 * <p/>
 * Line terminators are converted to a \n
 */
public class DocTypeReader extends Reader {

  private Reader reader;
  private String docType;

  public DocTypeReader(Reader in) throws IOException {
    BufferedReader lineReader = new BufferedReader(in);
    StringBuffer buffer = new StringBuffer();
    StringBuffer docBuffer = new StringBuffer();
    String line = null;
    while ((line = lineReader.readLine()) != null) {
      if (line.indexOf("<!DOCTYPE") > -1) {
        docBuffer.append(line);
        while (line.indexOf(">") < 0) {
          line = lineReader.readLine();
          docBuffer.append(" ");
          docBuffer.append(line.trim());
        }
        line = lineReader.readLine();
      }
      buffer.append(line);
      buffer.append("\n");
    }
    reader = new StringReader(buffer.toString());
    docType = docBuffer.toString();
  }

  public String getDocType() {
    return docType;
  }

  public int read(char cbuf[], int off, int len) throws IOException {
    return reader.read(cbuf, off, len);
  }

  public void close() throws IOException {
    reader.close();
  }

}
