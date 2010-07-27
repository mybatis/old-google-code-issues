/*
 *  Copyright 2005 The Apache Software Foundation
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
package org.apache.ibatis.abator.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.ibatis.abator.api.GeneratedXmlFile;
import org.apache.ibatis.abator.exception.ShellException;
import org.apache.ibatis.abator.internal.sqlmap.XmlConstants;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class handles the task of merging changes into an existing XML file.
 * 
 * @author Jeff Butler
 */
public class XmlFileMergerJaxp {
    private static class NullEntityResolver implements EntityResolver {
        /**
         * returns an empty reader. This is done so that the parser doesn't
         * attempt to read a DTD. We don't need that support for the merge and
         * it can cause problems on systems that aren't Internet connected.
         */
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {

            StringReader sr = new StringReader(""); //$NON-NLS-1$

            return new InputSource(sr);
        }
    }

    /**
     *  
     */
    private XmlFileMergerJaxp(GeneratedXmlFile generatedXmlFile,
            File existingFile) {
        super();
    }

    public static String getMergedSource(GeneratedXmlFile generatedXmlFile,
            File existingFile) throws ShellException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new NullEntityResolver());

            Document existingDocument = builder.parse(existingFile);
            StringReader sr = new StringReader(generatedXmlFile.getContent());
            Document newDocument = builder.parse(new InputSource(sr));

            DocumentType newDocType = newDocument.getDoctype();
            DocumentType existingDocType = existingDocument.getDoctype();

            if (!newDocType.getName().equals(existingDocType.getName())) {
                throw new ShellException(
                        "The exisiting XML file "
                                + existingFile.getName()
                                + " is not the same format as the generated file.  The existing file will not be changed.");
            }

            Element existingRootElement = existingDocument.getDocumentElement();
            Element newRootElement = newDocument.getDocumentElement();

            // reconcile the namespace
            String namespace = newRootElement.getAttribute("namespace"); //$NON-NLS-1$
            existingRootElement.setAttribute("namespace", namespace); //$NON-NLS-1$

            // remove the old Abator generated elements
            NodeList children = existingRootElement.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id"); //$NON-NLS-1$
                    if (id != null && id.startsWith("abatorgenerated_")) { //$NON-NLS-1$
                        existingRootElement.removeChild(node);
                    }
                }
            }

            // add the new Abator generated elements
            children = newRootElement.getChildNodes();
            Node firstChild = existingRootElement.getFirstChild();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                Node newNode = existingDocument.importNode(node, true);
                if (firstChild == null) {
                    existingRootElement.appendChild(newNode);
                } else {
                    existingRootElement.insertBefore(newNode, firstChild);
                }
            }

            // pretty print the result
            return prettyPrint(existingDocument);
        } catch (ParserConfigurationException e) {
            throw new ShellException(
                    "ParserConfigurationException while attempting to merge the XML file "
                            + existingFile.getName()
                            + ".  The existing file will not be changed.", e);
        } catch (SAXException e) {
            throw new ShellException(
                    "SAXException while attempting to merge the XML file "
                            + existingFile.getName()
                            + ".  The existing file will not be changed.", e);
        } catch (IOException e) {
            throw new ShellException(
                    "IOException while attempting to merge the XML file "
                            + existingFile.getName()
                            + ".  The existing file will not be changed.", e);
        }
    }

    private static String prettyPrint(Document document) throws ShellException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();

            Transformer transformer = factory.newTransformer();
            transformer
                    .setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); //$NON-NLS-1$
            transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                    XmlConstants.SQL_MAP_PUBLIC_ID);
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                    XmlConstants.SQL_MAP_SYSTEM_ID);

            ByteArrayOutputStream bas = new ByteArrayOutputStream();

            transformer.transform(new DOMSource(document),
                    new StreamResult(bas));

            return bas.toString();
        } catch (TransformerConfigurationException e) {
            throw new ShellException(
                    "TransformerConfigurationException during prettyPrint", e);
        } catch (TransformerException e) {
            throw new ShellException("TransformerException during prettyPrint",
                    e);
        }
    }
}
