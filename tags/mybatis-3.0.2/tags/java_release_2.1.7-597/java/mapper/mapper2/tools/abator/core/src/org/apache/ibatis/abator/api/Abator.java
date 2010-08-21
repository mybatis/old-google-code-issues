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
package org.apache.ibatis.abator.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.abator.config.AbatorConfiguration;
import org.apache.ibatis.abator.config.AbatorContext;
import org.apache.ibatis.abator.exception.InvalidConfigurationException;
import org.apache.ibatis.abator.exception.ShellException;
import org.apache.ibatis.abator.internal.DefaultShellCallback;
import org.apache.ibatis.abator.internal.NullProgressCallback;
import org.apache.ibatis.abator.internal.XmlFileMergerJaxp;

/**
 * This class is the main interface to the Abator for iBATIS code generator. A
 * typical execution of the tool involves these steps:
 * 
 * <ol>
 * <li>Create an AbatorConfiguration object. The AbatorConfiguration can
 * be the result of a parsing the XML configuration file, or it can be created
 * solely in Java.</li>
 * <li>Create an Abator object</li>
 * <li>Call the generate() method</li>
 * </ol>
 * 
 * @see org.apache.ibatis.abator.config.xml.AbatorConfigurationParser
 * 
 * @author Jeff Butler
 */
public class Abator {

    private AbatorConfiguration abatorConfiguration;

    private ShellCallback shellCallback;

    private List generatedJavaFiles;

    private List generatedXmlFiles;

    private List warnings;

    private Set projects;

    /**
     * Constructs an Abator object.
     * 
     * @param abatorConfiguration The configuration for this run of Abator
     * @param shellCallback an instance of a ShellCallback interface.  You may specify
     *    <code>null</code> in which case Abator will use the DefaultShellCallback.
     * @param warnings Any warnings generated during execution will be added to this
     *            list. Warnings do not affect the running of the tool, but they
     *            may affect the results. A typical warning is an unsupported
     *            data type. In that case, the column will be ignored and
     *            generation will continue. Abator will only add Strings to the
     *            list.  You may specify <code>null</code> if you do not
     *            want warnings returned.
     */
    public Abator(AbatorConfiguration abatorConfiguration,
            ShellCallback shellCallback, List warnings) {
        super();
        if (abatorConfiguration == null) {
            throw new IllegalArgumentException("Abator Configuration is required");
        } else {
            this.abatorConfiguration = abatorConfiguration;
        }
        
        if (shellCallback == null) {
            this.shellCallback = new DefaultShellCallback(false);
        } else {
            this.shellCallback = shellCallback;
        }
        
        if (warnings == null) {
            this.warnings = new ArrayList();
        } else {
            this.warnings = warnings;
        }
        generatedJavaFiles = new ArrayList();
        generatedXmlFiles = new ArrayList();
        projects = new HashSet();
    }

    /**
     * This is the main method for generating code.  This method is long running, but
     * progress can be provided and the method can be calncelled through the ProgressCallbac
     * interface.
     * 
     * @param callback an instance of the ProgressCallback interface, or <code>null</code>
     *   if you do not require progress information
     * @throws InvalidConfigurationException
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException if the method is cancelled through the ProgressCallback
     */
    public void generate(ProgressCallback callback)
            throws InvalidConfigurationException, SQLException, IOException,
            InterruptedException {

        if (callback == null) {
            callback = new NullProgressCallback();
        }

        generatedJavaFiles.clear();
        generatedXmlFiles.clear();

        int totalSteps = 0;
        totalSteps++; // validation

        Iterator iter = abatorConfiguration.getAbatorContexts().iterator();
        while (iter.hasNext()) {
            AbatorContext abatorContext = (AbatorContext) iter.next();

            totalSteps += abatorContext.getTotalSteps();
        }

        callback.setNumberOfSubTasks(totalSteps);

        callback.startSubTask("Validating Configuration");
        abatorConfiguration.validate();

        iter = abatorConfiguration.getAbatorContexts().iterator();
        while (iter.hasNext()) {
            AbatorContext abatorContext = (AbatorContext) iter.next();

            abatorContext.generateFiles(callback, generatedJavaFiles,
                    generatedXmlFiles, warnings);
        }

        iter = generatedXmlFiles.iterator();
        while (iter.hasNext()) {
            GeneratedXmlFile gxf = (GeneratedXmlFile) iter.next();
            projects.add(gxf.getTargetProject());

            File targetFile;
            String source;
            try {
                File directory = shellCallback.getDirectory(gxf
                        .getTargetProject(), gxf.getTargetPackage(), warnings);
                targetFile = new File(directory, gxf.getFileName());
                if (targetFile.exists()) {
                    source = XmlFileMergerJaxp.getMergedSource(gxf, targetFile);
                } else {
                    source = gxf.getContent();
                }
            } catch (ShellException e) {
                warnings.add(e.getMessage());
                continue;
            }

            writeFile(targetFile, source);
        }

        iter = generatedJavaFiles.iterator();
        while (iter.hasNext()) {
            GeneratedJavaFile gjf = (GeneratedJavaFile) iter.next();
            projects.add(gjf.getTargetProject());

            File targetFile;
            String source;
            try {
                File directory = shellCallback.getDirectory(gjf
                        .getTargetProject(), gjf.getTargetPackage(), warnings);
                targetFile = new File(directory, gjf.getFileName());
                if (targetFile.exists()) {
                    if (shellCallback.mergeSupported()) {
                        source = shellCallback.mergeJavaFile(gjf,
                            "@abatorgenerated", warnings); //$NON-NLS-1$
                    } else {
                        source = gjf.getContent();
                        targetFile = getUniqueFileName(directory, gjf);
                        warnings.add("Existing file not overwritten, the generated Java file is saved as " + targetFile.getAbsolutePath());
                    }
                } else {
                    source = gjf.getContent();
                }
                
                writeFile(targetFile, source);
            } catch (ShellException e) {
                warnings.add(e.getMessage());
            }
        }

        iter = projects.iterator();
        while (iter.hasNext()) {
            shellCallback.refreshProject((String) iter.next());
        }
    }

    /**
     * Writes, or overwrites, the contents of the specified file
     * 
     * @param file
     * @param content
     */
    private void writeFile(File file, String content) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
        bw.write(content);
        bw.close();
    }
    
    private File getUniqueFileName(File directory, GeneratedJavaFile gjf) {
        File answer = null;
        
        // try up to 10000 times to generate a unique file name
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < 10000; i++) {
            sb.setLength(0);
            sb.append(gjf.getFileName());
            sb.append('.');
            sb.append(i);
            
            answer = new File(directory, sb.toString());
            if (!answer.exists()) {
                break;
            }
        }
        
        return answer;
    }
}
