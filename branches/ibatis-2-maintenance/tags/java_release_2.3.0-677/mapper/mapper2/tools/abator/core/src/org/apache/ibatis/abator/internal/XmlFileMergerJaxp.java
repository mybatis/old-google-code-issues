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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.ibatis.abator.api.GeneratedXmlFile;
import org.apache.ibatis.abator.exception.ShellException;
import org.apache.ibatis.abator.internal.sqlmap.XmlConstants;
import org.apache.ibatis.abator.internal.util.messages.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
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
     * Utility class - no instances allowed  
     */
    private XmlFileMergerJaxp() {
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
            StringReader sr = new StringReader(generatedXmlFile.getFormattedContent());
            Document newDocument = builder.parse(new InputSource(sr));

            DocumentType newDocType = newDocument.getDoctype();
            DocumentType existingDocType = existingDocument.getDoctype();

            if (!newDocType.getName().equals(existingDocType.getName())) {
                throw new ShellException(Messages.getString("Warning.12", //$NON-NLS-1$
                        existingFile.getName()));
            }

            Element existingRootElement = existingDocument.getDocumentElement();
            Element newRootElement = newDocument.getDocumentElement();

            // reconcile the namespace
            String namespace = newRootElement.getAttribute("namespace"); //$NON-NLS-1$
            existingRootElement.setAttribute("namespace", namespace); //$NON-NLS-1$

            // remove the old Abator generated elements and any
            // white space before the old abator nodes
            List nodesToDelete = new ArrayList();
            NodeList children = existingRootElement.getChildNodes();
            int length = children.getLength();
            for (int i = 0; i < length; i++) {
                Node node = children.item(i);
                if (isAnAbatorNode(node)) {
                    nodesToDelete.add(node);
                }
                
                short nodeType = node.getNodeType();
                if (nodeType == Element.TEXT_NODE) {
                    // remove any nodes that are only white space
                    // if the next node is an Abator node, or if this
                    // is the last node.
                    // this ensures that we don't end up with
                    // lots of blank lines at the end of a merged file
                    Text tn = (Text) node;
                    String text = tn.getData();
                    
                    if (text.trim().length() == 0) {
                        // node is just whitespace. if next node is an Abator
                        //node, then remove the node.  Or if this is the last node
                        // then delete the node
                        if (i == length - 1) {
                            nodesToDelete.add(tn);
                        } else if (isAnAbatorNode(children.item(i + 1))) {
                            nodesToDelete.add(tn);
                        }
                    }
                }
            }
            
            Iterator iter = nodesToDelete.iterator();
            while (iter.hasNext()) {
                existingRootElement.removeChild((Node) iter.next());
            }

            // add the new Abator generated elements
            children = newRootElement.getChildNodes();
            length = children.getLength();
            Node firstChild = existingRootElement.getFirstChild();
            for (int i = 0; i < length; i++) {
                Node node = children.item(i);
                // don't add the last node if it is only white space
                if (i == length - 1) {
                    // last node - only add if it isn't whitespace
                    if (node.getNodeType() == Node.TEXT_NODE) {
                        Text tn = (Text) node;
                        if (tn.getData().trim().length() == 0) {
                            break;
                        }
                    }
                }
                
                Node newNode = existingDocument.importNode(node, true);
                if (firstChild == null) {
                    existingRootElement.appendChild(newNode);
                } else {
                    existingRootElement.insertBefore(newNode, firstChild);
                }
            }

            // pretty print the result
            return prettyPrint(existingDocument);
        } catch (Exception e) {
            throw new ShellException(Messages.getString("Warning.13", //$NON-NLS-1$
                existingFile.getName()), e);
        }
    }

    private static String prettyPrint(Document document) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();

        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); //$NON-NLS-1$
        transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, XmlConstants.SQL_MAP_PUBLIC_ID);
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, XmlConstants.SQL_MAP_SYSTEM_ID);

        ByteArrayOutputStream bas = new ByteArrayOutputStream();

        transformer.transform(new DOMSource(document), new StreamResult(bas));

        return bas.toString();
    }
    
    private static boolean isAnAbatorNode(Node node) {
        boolean rc = false;
        
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            String id = element.getAttribute("id"); //$NON-NLS-1$
            if (id != null && id.startsWith("abatorgenerated_")) { //$NON-NLS-1$
                rc = true;
            }
        }
        
        return rc;
    }
}
