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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @version $Id$
 */
final class XsltProcessor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(XsltProcessor.class);

    private static final String LOG_INFO_PATTERN = "Converted {} into {}";

    private static final String LOG_ERROR_PATTERN = "An error occurred while converting {} into {}, cause: {}";

    private final File source;

    private final File dest;

    private final Source xmlSource;

    private final Result outputTarget;

    private final Transformer transformer;

    public XsltProcessor(File source, File dest, Transformer transformer) {
        this.source = source;

        this.xmlSource = new StreamSource(source);
        this.dest = dest;

        this.outputTarget = new StreamResult(dest);
        this.transformer = transformer;
    }

    public void run() {
        try {
            this.transformer.transform(this.xmlSource, this.outputTarget);
            LOGGER.info(LOG_INFO_PATTERN, this.source, this.dest);
        } catch (Exception e) {
            LOGGER.error(LOG_ERROR_PATTERN, new Object[] {this.source, this.dest, e.getMessage()});
        }
    }

}
