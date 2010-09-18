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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;

import com.beust.jcommander.JCommander;

/**
 * 
 * @version $Id$
 */
public final class Ibatis2MyBatis {

    private final ExecutorService executors = Executors.newFixedThreadPool(10);

    private final XMLFilter xmlFilter = new XMLFilter();

    private final SAXTransformerFactory saxTransformerFactory;

    private Ibatis2MyBatis() {
        this.saxTransformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
        this.saxTransformerFactory.setAttribute("translet-name", "SqlMap2Mapper");
        this.saxTransformerFactory.setAttribute("package-name", "org.mybatis.i2m");
    }

    private void transform(File sqlMapFile, File destination) throws Exception {
        if (sqlMapFile.isDirectory()) {
            for (File file : sqlMapFile.listFiles(this.xmlFilter)) {
                this.transform(file, destination);
            }
            return;
        }

        File dest = destination.isDirectory() ? new File(destination, sqlMapFile.getName()) : destination;
        System.out.println("Converting " + sqlMapFile + " into " + dest);
        this.executors.execute(new XsltProcessor(sqlMapFile, dest, this.saxTransformerFactory.newTransformer()));
    }

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        JCommander commander = new JCommander(config, args);

        if (config.isHelp()
                || config.getSource() == null
                || config.getDest() == null) {
            commander.usage();
            System.exit(0);
        }

        if (!config.getSource().exists()) {
            System.out.println("-s --source must be an existing dir/XML file");
            System.exit(-1);
        }

        config.getDest().mkdirs();

        Ibatis2MyBatis ibatis2MyBatis = new Ibatis2MyBatis();
        ibatis2MyBatis.transform(config.getSource(), config.getDest());
    }

}
