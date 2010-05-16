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
package org.apache.ibatis.ibator.api;

import java.io.File;
import java.util.List;

import org.apache.ibatis.ibator.exception.ShellException;

/**
 * This interface defines methods that a shell should support to enable iBATOR
 * to work.  A "shell" is defined as the iBATOR execution environment (i.e. an Eclipse
 * plugin, and Ant task, a NetBeans plugin, etc.)
 * 
 * iBATOR provides a default ShellCallback that is very low function and does
 * not support the merging of Java files.  The default shell callback is only
 * appropriate for use in well controlled environments where no changes are ever made
 * to generated Java files. 
 * 
 * @author Jeff Butler
 */
public interface ShellCallback {
    /**
     * iBATOR will call this method to ask the shell to resolve a project/package
     * combination into a directory on the file system.  iBATOR will call this method
     * repeatedly (once for each generated file), so it would be wise for an implementing
     * class to cache results.  
     * 
     * The returned <code>java.io.File</code> object:
     * <ul>
     *   <li>Must be a directory</li>
     *   <li>Must exist</li>
     * </ul>
     * 
     * iBATOR default shell callback interprets both values as directories
     * and simply concatenates the two values to generate the default directory.  
     * 
     * @param targetProject
     * @param targetPackage
     * @param warnings
     * @return the directory (must exist)
     * @throws ShellException if the project/package cannot be resolved into 
     *  a directory on the file system.  In this case, iBATOR will not save the
     *  file it is currently working on. iBATOR
     *  will add the exception message to the list of warnings automatically. 
     */
    File getDirectory(String targetProject, String targetPackage, List<String> warnings) throws ShellException;
    
    /**
     * iBATOR will call this method if a newly generated Java file would overwrite an existing
     * file.  This method should return the merged source (formatted).  iBATOR will write the
     * merged source as-is to the file system.
     * 
     * A merge typically follows these steps:
     * <ol>
     *   <li>Delete any methods/fields in the existing file that have the specified JavaDoc tag</li>
     *   <li>Add any new super interfaces from the new file into the existing file</li>
     *   <li>Make sure that the existing file's super class matches the new file</li>
     *   <li>Make sure that the existing file is of the same type es the existing file
     *       (either interface or class)</li>
     *   <li>Add any new imports from the new file into the existing file</li>
     *   <li>Add all methods and fields from the new file into the existing file</li>
     *   <li>Format the resulting source string</li>
     * </ol>
     * 
     * If you do not want to support merging in your shell, simply return
     * <code>newFile.getFormattedContent()</code>.
     * However, this will overwrite any modifications to the generated files.
     * 
     * @param newFile the newly generated Java file
     * @param javadocTags the JavaDoc tags that denotes which methods and fields in the
     *                   old file to delete (if the Java element has any of these tags, the
     *                   element is eligible for merge)
     * @param warninigs Any warning strings during the merge can be added to this list.
     *                  Adding a warning will not stop iBATOR from saving the resulting
     *                  source. 
     * @return the merged source, properly formatted.  iBATOR will save the source
     *  exactly as returned from this method.
     * @throws ShellException if the file cannot be merged for some reason.  If this
     *                        exception is thrown, iBATOR will not save anything and
     *                        the existing file will remain undisturbed.  iBATOR
     *                        will add the exception message to the list of warnings automatically.
     */
    String mergeJavaFile(GeneratedJavaFile newFile, String[] javadocTags, List<String> warninigs)
    	throws ShellException;
    
    /**
     * After all files are saved to the file system, iBATOR will call this method
     * once for each unique project that was affected by the generation run.
     * This method is usefull if your IDE needs to be informed that file system objects
     * have been created or updated.  If you are using iBATOR outside of an IDE,
     * your implementation need not do anything in this method.
     * 
     * @param project the project to be refreshed
     */
    void refreshProject(String project);

    /**
     * Return true if the callback supports Java merging, otherwise false.
     * iBATOR will only call the <code>mergeJavaFile</code> method if this method
     * returns true;
     * 
     * @return a boolean specifying whether Java merge is supported or not
     */
    boolean mergeSupported();
}
