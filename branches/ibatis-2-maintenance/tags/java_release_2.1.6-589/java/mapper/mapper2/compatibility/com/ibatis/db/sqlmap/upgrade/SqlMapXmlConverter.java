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
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.engine.builder.xml.XmlConverter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class SqlMapXmlConverter implements XmlConverter {

  public void convertXml(Reader reader, Writer writer) {
    try {
      DocTypeReader xml = new DocTypeReader(reader);
      String docType = xml.getDocType();
      Reader xsl = null;
      if (docType == null) {
        throw new SqlMapException("Could not convert document because DOCTYPE was null.");
      } else {
        if (docType.indexOf("sql-map-config") > -1) {
          xsl = Resources.getResourceAsReader("com/ibatis/db/sqlmap/upgrade/SqlMapConfig.xsl");
        } else if (docType.indexOf("sql-map") > -1) {
          xsl = Resources.getResourceAsReader("com/ibatis/db/sqlmap/upgrade/SqlMap.xsl");
        } else {
          throw new SqlMapException("Could not convert document because DOCTYPE was not recognized: " + docType);
        }
      }
      transformXml(xsl, xml, writer);
    } catch (IOException e) {
      throw new NestedRuntimeException("Error.  Cause: " + e, e);
    } catch (TransformerException e) {
      throw new NestedRuntimeException("Error.  Cause: " + e, e);
    }
  }

  public Reader convertXml(Reader reader) {
    StringWriter out = new StringWriter();
    convertXml(reader, out);
    return new StringReader(out.getBuffer().toString());
  }

  public void convertFile(String fromFileName, String toFileName) throws IOException {
    File fromFile = new File(fromFileName);
    File toFile = new File(toFileName);
    convertFile(fromFile, toFile);
  }

  public void convertFile(File fromFile, File toFile) throws IOException {
    Reader reader = new FileReader(fromFile);
    Writer writer = new FileWriter(toFile);
    convertXml(reader, writer);
    writer.flush();
    writer.close();
    reader.close();
  }

  protected void transformXml(Reader xslReader, Reader xmlReader, Writer xmlWriter) throws TransformerException {
    StreamSource xslSource = new StreamSource(xslReader);
    StreamSource xmlSource = new StreamSource(xmlReader);
    StreamResult xmlResult = new StreamResult(xmlWriter);

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer(xslSource);
    transformer.setParameter("http://xml.org/sax/features/validation", new Boolean(false));
    transformer.setParameter("http://apache.org/xml/features/nonvalidating/load-dtd", new Boolean(false));

    transformer.transform(xmlSource, xmlResult);
  }

}

