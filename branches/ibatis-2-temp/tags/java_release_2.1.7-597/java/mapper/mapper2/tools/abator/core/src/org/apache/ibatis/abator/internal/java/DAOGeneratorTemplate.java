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
package org.apache.ibatis.abator.internal.java;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.abator.api.FullyQualifiedJavaType;

/**
 * This class holds values that are used by the DAOGeneratorBaseImpl class to
 * generate DAOs. The values in this class can be used to alter the generated
 * DAOs.
 * 
 * @author Jeff Butler
 */
public class DAOGeneratorTemplate {
	private List interfaceImports;

	private List implementationImports;

	private String constructorTemplate;

	private FullyQualifiedJavaType superClass;

	private String deleteMethod;

	private String insertMethod;

	private String updateMethod;

	private String queryForObjectMethod;

	private String queryForListMethod;

	private List checkedExceptions;

	private List fields;

	private List methods;

	/**
	 *  
	 */
	public DAOGeneratorTemplate() {
		super();
		interfaceImports = new ArrayList();
		implementationImports = new ArrayList();
		fields = new ArrayList();
		methods = new ArrayList();
		checkedExceptions = new ArrayList();
	}

	public String getConstructor(FullyQualifiedJavaType type) {
		return MessageFormat.format(constructorTemplate,
				new Object[] { type.getShortName() });
	}

	public String getDeleteMethod() {
		return deleteMethod;
	}

	public List getInterfaceImports() {
		return interfaceImports;
	}

	public List getImplementationImports() {
		return implementationImports;
	}

	public String getInsertMethod() {
		return insertMethod;
	}

	public String getQueryForListMethod() {
		return queryForListMethod;
	}

	public String getQueryForObjectMethod() {
		return queryForObjectMethod;
	}

	public FullyQualifiedJavaType getSuperClass() {
		return superClass;
	}

	public String getUpdateMethod() {
		return updateMethod;
	}

	public List getCheckedExceptions() {
		return checkedExceptions;
	}

	public List getFields() {
		return fields;
	}

	public List getMethods() {
		return methods;
	}

	public String getConstructorTemplate() {
		return constructorTemplate;
	}

	public void setConstructorTemplate(String constructorTemplate) {
		this.constructorTemplate = constructorTemplate;
	}

	public void setDeleteMethod(String deleteMethod) {
		this.deleteMethod = deleteMethod;
	}

	public void addField(String field) {
		fields.add(field);
	}

	public void setInsertMethod(String insertMethod) {
		this.insertMethod = insertMethod;
	}

	public void addMethod(String method) {
		methods.add(method);
	}

	public void setQueryForListMethod(String queryForListMethod) {
		this.queryForListMethod = queryForListMethod;
	}

	public void setQueryForObjectMethod(String queryForObjectMethod) {
		this.queryForObjectMethod = queryForObjectMethod;
	}

	public void setSuperClass(FullyQualifiedJavaType superClass) {
		this.superClass = superClass;
	}

	public void setUpdateMethod(String updateMethod) {
		this.updateMethod = updateMethod;
	}

	public void addInterfaceImport(FullyQualifiedJavaType type) {
		interfaceImports.add(type);
	}

	public void addImplementationImport(FullyQualifiedJavaType type) {
		implementationImports.add(type);
	}

	public void addCheckedException(FullyQualifiedJavaType type) {
		checkedExceptions.add(type);
	}
}
