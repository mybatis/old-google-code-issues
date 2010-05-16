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

import java.util.List;
import java.util.Map;

import org.apache.ibatis.abator.config.FullyQualifiedTable;
import org.apache.ibatis.abator.config.TableConfiguration;
import org.apache.ibatis.abator.internal.db.ColumnDefinitions;

public interface JavaModelGenerator {
    
	/**
	 * Sets the properties of the generator taken from the 
	 * JavaModelGeneratorConfiguration element.  This method is
	 * called before any getXXX method.
	 * 
	 * @param properties the configuration element's properties
	 */
	void setProperties(Map properties);
	
	/**
	 * Sets the target package of the generator taken from the 
	 * JavaModelGeneratorConfiguration element.  This method is
	 * called before any getXXX method.
	 * 
	 * @param targetPackage the configuration element's target package
	 */
	void setTargetPackage(String targetPackage);

	/**
	 * Sets the target project of the generator taken from the 
	 * JavaModelGeneratorConfiguration element.  This method is
	 * called before any getXXX method.
	 * 
	 * @param targetProject the configuration element's target project
	 */
	void setTargetProject(String targetProject);
	
	FullyQualifiedJavaType getPrimaryKeyType(FullyQualifiedTable table);

	/**
	 * 
	 * @param table the table for which the name should be generated
	 * @return the type for the record (the class that holds non-primary
	 *  key and non-BLOB fields).  Note that
	 *  the value will be calculated regardless of whether the table has these columns or not.
	 */
	FullyQualifiedJavaType getRecordType(FullyQualifiedTable table);

	/**
	 * 
	 * @param table the table for which the name should be generated
	 * @return the type for the example class.
	 */
	FullyQualifiedJavaType getExampleType(FullyQualifiedTable table);

	/**
	 * 
	 * @param table the table for which the name should be generated
	 * @return the type for the record with BLOBs class.  Note that
	 *  the value will be calculated regardless of whether the table has BLOB columns or not.
	 */
	FullyQualifiedJavaType getRecordWithBLOBsType(FullyQualifiedTable table);

	/**
	 * This method returns a list of GenerateJavaFile objects.  The list may
	 * include any, or all, of the following types of generated java classes:
	 * 
	 * <ul>
	 *   <li>A Primary Key Class</li>
	 *   <li>A "record" class containing non-primary key and non-BLOB fields</li>
	 *   <li>A "record" class containing BLOB fields</li>
	 *   <li>An example class to be used on the "by example" queries</li>
	 * </ul>
	 * 
	 * @param columnDefinitions
	 * @param tableConfiguration
	 * @param callback
	 * @return a list of GeneratedJavaFile objects
	 */
	List getGeneratedJavaFiles(ColumnDefinitions columnDefinitions,
			TableConfiguration tableConfiguration, ProgressCallback callback);
}
