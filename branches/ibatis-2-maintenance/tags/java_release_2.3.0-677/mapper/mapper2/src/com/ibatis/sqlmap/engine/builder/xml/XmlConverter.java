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

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Interface for converting XML
 */
public interface XmlConverter {

  /**
   * Get a Reader based on another Reader
   * @param reader - a Reader for the file to convert
   * @return - the converted file
   */
  public Reader convertXml(Reader reader);

  /**
   * Convert an XML file into another XML file
   * @param reader - a reader for the XML to be converted from
   * @param writer - a writer for the XML to be converted to
   */
  public void convertXml(Reader reader, Writer writer);

  /**
   * Get an InputStream based on another InputStream
   * @param inputStream - an InputStream for the file to convert
   * @return - the converted file
   */
  public InputStream convertXml(InputStream inputStream);
}
