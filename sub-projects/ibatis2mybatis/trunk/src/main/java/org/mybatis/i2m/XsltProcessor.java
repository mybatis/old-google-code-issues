/*
 *    Copyright 2010 The myBatis Team
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
package org.mybatis.i2m;

import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 
 * @version $Id$
 */
final class XsltProcessor implements Runnable {

    private final Source xmlSource;

    private final Result outputTarget;

    private final Transformer transformer;

    public XsltProcessor(File source, File dest, Transformer transformer) {
        this.xmlSource = new StreamSource(source);
        this.outputTarget = new StreamResult(dest);
        this.transformer = transformer;
    }

    public void run() {
        try {
            this.transformer.transform(this.xmlSource, this.outputTarget);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
