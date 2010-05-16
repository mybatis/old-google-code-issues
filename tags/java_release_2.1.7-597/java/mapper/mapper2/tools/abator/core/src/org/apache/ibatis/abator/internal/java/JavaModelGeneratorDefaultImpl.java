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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.abator.api.FullyQualifiedJavaType;
import org.apache.ibatis.abator.api.GeneratedJavaFile;
import org.apache.ibatis.abator.api.JavaModelGenerator;
import org.apache.ibatis.abator.api.ProgressCallback;
import org.apache.ibatis.abator.config.FullyQualifiedTable;
import org.apache.ibatis.abator.config.TableConfiguration;
import org.apache.ibatis.abator.internal.db.ColumnDefinition;
import org.apache.ibatis.abator.internal.db.ColumnDefinitions;
import org.apache.ibatis.abator.internal.sqlmap.ExampleClause;
import org.apache.ibatis.abator.internal.util.JavaBeansUtil;
import org.apache.ibatis.abator.internal.util.StringUtility;

public class JavaModelGeneratorDefaultImpl extends BaseJavaCodeGenerator implements JavaModelGenerator {

    /**
     * The properties from the JavaModelGenerator congiguration element
     */
    protected Map properties;

    /**
     * The target package from the JavaModelGenerator congiguration element
     */
	protected String targetPackage;

    /**
     * The target project from the JavaModelGenerator congiguration element
     */
	protected String targetProject;

	private Map tableValueMaps;

	public JavaModelGeneratorDefaultImpl() {
		super();
		tableValueMaps = new HashMap();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.apache.ibatis.abator.api.JavaModelGenerator#setProperties(java.util.Map)
	 */
	public void setProperties(Map properties) {
		this.properties = properties;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.apache.ibatis.abator.api.JavaModelGenerator#setTargetPackage(java.lang.String)
	 */
	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	private Map getTableValueMap(FullyQualifiedTable table) {
	    Map map = (Map) tableValueMaps.get(table);
	    if (map == null) {
	        map = new HashMap();
	        tableValueMaps.put(table, map);
	    }
	    
	    return map;
	}

	/**
	 * Adds fields and getter/setter methods for each ColumnDefinition
	 * passed into the method.
	 * 
	 * @param table the table from which the ColumnDefinitions are derived.  This
	 *  is used to generate appropriate JavaDoc comments for the generated fields and
	 *  methods.
	 * @param columnDefinitions the collection of ColumnDefinitions used to generate
	 *   fields and getter/setter methods.
	 * @param answer the generated fields and methods will be added to this object
	 */
	protected void generateClassParts(FullyQualifiedTable table, Collection columnDefinitions,
			GeneratedJavaFile answer) {

		boolean trimStrings = "true".equalsIgnoreCase((String) properties //$NON-NLS-1$
				.get("trimStrings")); //$NON-NLS-1$
		
		StringBuffer buffer = new StringBuffer();

		Iterator iter = columnDefinitions.iterator();
		while (iter.hasNext()) {
			ColumnDefinition cd = (ColumnDefinition) iter.next();
			FullyQualifiedJavaType fqjt = cd.getResolvedJavaType().getFullyQualifiedJavaType();
			
			answer.addImportedType(fqjt);

			String property = cd.getJavaProperty();

			buffer.setLength(0);
			buffer.append(getFieldComment(table, cd.getColumnName()));
			newLine(buffer);
			indent(buffer, 1);
			buffer.append("private "); //$NON-NLS-1$
			buffer.append(fqjt.getShortName());
			buffer.append(' ');
			buffer.append(property);
			buffer.append(';');
			answer.addField(buffer.toString());

			buffer.setLength(0);
			buffer.append(getGetterMethodComment(table, cd));
			newLine(buffer);
			indent(buffer, 1);
			buffer.append("public "); //$NON-NLS-1$
			buffer.append(fqjt.getShortName());
			buffer.append(' ');
			buffer.append(JavaBeansUtil.getGetterMethodName(property));
			buffer.append("() {"); //$NON-NLS-1$
			newLine(buffer);
			indent(buffer, 2);
			buffer.append("return "); //$NON-NLS-1$
			buffer.append(property);
			buffer.append(';');
			newLine(buffer);
			indent(buffer, 1);
			buffer.append('}');
			answer.addMethod(buffer.toString());

			buffer.setLength(0);
			buffer.append(getSetterMethodComment(table, cd));
			newLine(buffer);
			indent(buffer, 1);
			buffer.append("public void "); //$NON-NLS-1$
			buffer.append(JavaBeansUtil.getSetterMethodName(property));
			buffer.append('(');
			buffer.append(fqjt.getShortName());
			buffer.append(' ');
			buffer.append(property);
			buffer.append(") {"); //$NON-NLS-1$
			if (trimStrings && cd.isCharacterColumn()) {
				newLine(buffer);
				indent(buffer, 2);
				buffer.append("if ("); //$NON-NLS-1$
				buffer.append(property);
				buffer.append(" != null) {"); //$NON-NLS-1$
				newLine(buffer);
				indent(buffer, 3);
				buffer.append(property);
				buffer.append(" = "); //$NON-NLS-1$
				buffer.append(property);
				buffer.append(".trim();"); //$NON-NLS-1$
				newLine(buffer);
				indent(buffer, 2);
				buffer.append('}');
			}
			newLine(buffer);
			indent(buffer, 2);
			buffer.append("this."); //$NON-NLS-1$
			buffer.append(property);
			buffer.append(" = "); //$NON-NLS-1$
			buffer.append(property);
			buffer.append(';');
			newLine(buffer);
			indent(buffer, 1);
			buffer.append('}');
			answer.addMethod(buffer.toString());
		}
	}

	/**
	 * Calculates the package for generated domain objects.
	 * 
	 * @param table the current table
	 * @return the calculated package
	 */
	protected String getJavaModelPackage(FullyQualifiedTable table) {
		String key = "getJavaModelPackage"; //$NON-NLS-1$
		String s;

		Map map = getTableValueMap(table);
		s = (String) map.get(key);
		if (s == null) {
			if ("true".equals(properties.get("enableSubPackages"))) { //$NON-NLS-1$  //$NON-NLS-2$
				StringBuffer sb = new StringBuffer(targetPackage);

				if (StringUtility.stringHasValue(table.getCatalog())) {
					sb.append('.');
					sb.append(table.getCatalog().toLowerCase());
				}

				if (StringUtility.stringHasValue(table.getSchema())) {
					sb.append('.');
					sb.append(table.getSchema().toLowerCase());
				}

				s = sb.toString();
			} else {
				s = targetPackage;
			}

			map.put(key, s);
		}

		return s;
	}
	
	protected GeneratedJavaFile getExample(ColumnDefinitions columnDefinitions,
			TableConfiguration tableConfiguration) {
		if (!columnDefinitions.generateExampleExtendingPrimaryKey(tableConfiguration)
				&& !columnDefinitions.generateExampleExtendingRecord(tableConfiguration)) {
			return null;
		}
		
		GeneratedJavaFile answer = new GeneratedJavaFile(getExampleType(tableConfiguration.getTable()));
		
		answer.setJavaInterface(false);
		
		if (columnDefinitions.generateExampleExtendingPrimaryKey(tableConfiguration)) {
			answer.setSuperClass(getPrimaryKeyType(tableConfiguration.getTable()));
		} else {
			answer.setSuperClass(getRecordType(tableConfiguration.getTable()));
		}
		
		answer.setTargetProject(targetProject);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(getFieldComment(tableConfiguration.getTable()));
		newLine(buffer);
		indent(buffer, 1);
		buffer.append("public static final int EXAMPLE_IGNORE = 0;"); //$NON-NLS-1$
		answer.addField(buffer.toString());
		
		Iterator iter = ExampleClause.getAllExampleClauses();
		while (iter.hasNext()) {
			ExampleClause clause = (ExampleClause) iter.next();
			buffer.setLength(0);
			buffer.append(getFieldComment(tableConfiguration.getTable()));
			newLine(buffer);
			indent(buffer, 1);
			buffer.append("public static final int "); //$NON-NLS-1$
			buffer.append(clause.getExamplePropertyName());
			buffer.append(" = "); //$NON-NLS-1$
			buffer.append(clause.getExamplePropertyValue());
			buffer.append(';');
			answer.addField(buffer.toString());
		}

		buffer.setLength(0);
		buffer.append(getFieldComment(tableConfiguration.getTable()));
		newLine(buffer);
		indent(buffer, 1);
		buffer.append("private boolean combineTypeOr;"); //$NON-NLS-1$
		answer.addField(buffer.toString());

		buffer.setLength(0);
		buffer.append(getMethodComment(tableConfiguration.getTable()));
		newLine(buffer);
		indent(buffer, 1);
		buffer
				.append("public void setCombineTypeOr(boolean combineTypeOr) {"); //$NON-NLS-1$
		newLine(buffer);
		indent(buffer, 2);
		buffer.append("this.combineTypeOr = combineTypeOr;"); //$NON-NLS-1$
		newLine(buffer);
		indent(buffer, 1);
		buffer.append('}');
		answer.addMethod(buffer.toString());

		buffer.setLength(0);
		buffer.append(getMethodComment(tableConfiguration.getTable()));
		newLine(buffer);
		indent(buffer, 1);
		buffer.append("public boolean isCombineTypeOr() {"); //$NON-NLS-1$
		newLine(buffer);
		indent(buffer, 2);
		buffer.append("return combineTypeOr;"); //$NON-NLS-1$
		newLine(buffer);
		indent(buffer, 1);
		buffer.append('}');
		answer.addMethod(buffer.toString());

		iter = columnDefinitions.getAllColumns().iterator();
		while (iter.hasNext()) {
			ColumnDefinition cd = (ColumnDefinition) iter.next();

			if (cd.isBLOBColumn()) {
				continue;
			}

			String fieldName = cd.getJavaProperty() + "_Indicator"; //$NON-NLS-1$

			buffer.setLength(0);
			buffer.append(getFieldComment(tableConfiguration.getTable()));
			newLine(buffer);
			indent(buffer, 1);
			buffer.append("private int "); //$NON-NLS-1$
			buffer.append(fieldName);
			buffer.append(';');
			answer.addField(buffer.toString());

			buffer.setLength(0);
			buffer.append(getMethodComment(tableConfiguration.getTable()));
			newLine(buffer);
			indent(buffer, 1);
			buffer.append("public int "); //$NON-NLS-1$
			buffer.append(JavaBeansUtil.getGetterMethodName(fieldName));
			buffer.append("() {"); //$NON-NLS-1$
			newLine(buffer);
			indent(buffer, 2);
			buffer.append("return "); //$NON-NLS-1$
			buffer.append(fieldName);
			buffer.append(';');
			newLine(buffer);
			indent(buffer, 1);
			buffer.append('}');
			answer.addMethod(buffer.toString());

			buffer.setLength(0);
			buffer.append(getMethodComment(tableConfiguration.getTable()));
			newLine(buffer);
			indent(buffer, 1);
			buffer.append("public void "); //$NON-NLS-1$
			buffer.append(JavaBeansUtil.getSetterMethodName(fieldName));
			buffer.append("("); //$NON-NLS-1$
			buffer.append("int "); //$NON-NLS-1$
			buffer.append(fieldName);
			buffer.append(") {"); //$NON-NLS-1$
			newLine(buffer);
			indent(buffer, 2);
			buffer.append("this."); //$NON-NLS-1$
			buffer.append(fieldName);
			buffer.append(" = "); //$NON-NLS-1$
			buffer.append(fieldName);
			buffer.append(';');
			newLine(buffer);
			indent(buffer, 1);
			buffer.append('}');
			answer.addMethod(buffer.toString());
		}

		return answer;
	}
	
	protected GeneratedJavaFile getPrimaryKey(ColumnDefinitions columnDefinitions,
			TableConfiguration tableConfiguration) {

		if (!columnDefinitions.generatePrimaryKey()) {
			return null;
		}
		
		GeneratedJavaFile answer = new GeneratedJavaFile(getPrimaryKeyType(tableConfiguration.getTable()));
		
		answer.setJavaInterface(false);
		answer.setTargetProject(targetProject);

		generateClassParts(tableConfiguration.getTable(), columnDefinitions.getPrimaryKey(), answer);

		return answer;
	}
	
	protected GeneratedJavaFile getRecord(ColumnDefinitions columnDefinitions,
			TableConfiguration tableConfiguration) {
		
		if (!columnDefinitions.generateRecordExtendingNothing()
				&& ! columnDefinitions.generateRecordExtendingPrimaryKey()) {
			return null;
		}
		
		GeneratedJavaFile answer = new GeneratedJavaFile(getRecordType(tableConfiguration.getTable()));
		
		answer.setJavaInterface(false);

		if (columnDefinitions.generateRecordExtendingPrimaryKey()) {
			answer.setSuperClass(getPrimaryKeyType(tableConfiguration.getTable()));
		}
		
		answer.setTargetProject(targetProject);
		
		generateClassParts(tableConfiguration.getTable(), columnDefinitions.getNonBLOBColumns(), answer);

		return answer;
	}
	
	protected GeneratedJavaFile getRecordWithBLOBs(ColumnDefinitions columnDefinitions,
			TableConfiguration tableConfiguration) {
		
		if (!columnDefinitions.generateRecordWithBLOBsExtendingPrimaryKey()
				&& !columnDefinitions.generateRecordWithBLOBsExtendingRecord()) {
			return null;
		}
		
		GeneratedJavaFile answer = new GeneratedJavaFile(getRecordWithBLOBsType(tableConfiguration.getTable()));
		
		answer.setJavaInterface(false);

		if (columnDefinitions.generateRecordWithBLOBsExtendingPrimaryKey()) {
			answer.setSuperClass(getPrimaryKeyType(tableConfiguration.getTable()));
		} else {
			answer.setSuperClass(getRecordType(tableConfiguration.getTable()));
		}
		
		answer.setTargetProject(targetProject);

		generateClassParts(tableConfiguration.getTable(), columnDefinitions.getBLOBColumns(), answer);

		return answer;
	}
	
    public void setTargetProject(String targetProject) {
        this.targetProject = targetProject;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.JavaModelGenerator#getExampleType(org.apache.ibatis.abator.config.FullyQualifiedTable)
     */
    public FullyQualifiedJavaType getExampleType(FullyQualifiedTable table) {
		String key = "getExampleType"; //$NON-NLS-1$

		Map map = getTableValueMap(table);
		FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) map.get(key);
		if (fqjt == null) {
			StringBuffer sb = new StringBuffer();
			sb.append(getJavaModelPackage(table));
			sb.append('.');
			sb.append(table.getDomainObjectName());
			sb.append("Example"); //$NON-NLS-1$

			fqjt = new FullyQualifiedJavaType(sb.toString());
			map.put(key, fqjt);
		}

		return fqjt;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.JavaModelGenerator#getGeneratedJavaFiles(org.apache.ibatis.abator.internal.db.ColumnDefinitions, org.apache.ibatis.abator.config.TableConfiguration, org.apache.ibatis.abator.api.ProgressCallback)
     */
    public List getGeneratedJavaFiles(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, ProgressCallback callback) {
        List list = new ArrayList();
        
		String tableName = tableConfiguration.getTable().getFullyQualifiedTableName();
		
		callback.startSubTask("Generating Example class for table " + tableName);
        GeneratedJavaFile gjf = getExample(columnDefinitions, tableConfiguration);
        if (gjf != null) {
            list.add(gjf);
        }
        
		callback.startSubTask("Generating Primary Key class for table " + tableName);
        gjf = getPrimaryKey(columnDefinitions, tableConfiguration);
        if (gjf != null) {
            list.add(gjf);
        }
        
		callback.startSubTask("Generating Record Class for table " + tableName);
        gjf = getRecord(columnDefinitions, tableConfiguration);
        if (gjf != null) {
            list.add(gjf);
        }
        
		callback.startSubTask("Generating Record Class(With BLOBs) for table " + tableName);
        gjf = getRecordWithBLOBs(columnDefinitions, tableConfiguration);
        if (gjf != null) {
            list.add(gjf);
        }

        return list;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.JavaModelGenerator#getPrimaryKeyType(org.apache.ibatis.abator.config.FullyQualifiedTable)
     */
    public FullyQualifiedJavaType getPrimaryKeyType(FullyQualifiedTable table) {
		String key = "getPrimaryKeyType"; //$NON-NLS-1$

		Map map = getTableValueMap(table);
		FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) map.get(key);
		if (fqjt == null) {
			StringBuffer sb = new StringBuffer();
			sb.append(getJavaModelPackage(table));
			sb.append('.');
			sb.append(table.getDomainObjectName());
			sb.append("Key"); //$NON-NLS-1$

			fqjt = new FullyQualifiedJavaType(sb.toString());
			map.put(key, fqjt);
		}

		return fqjt;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.JavaModelGenerator#getRecordType(org.apache.ibatis.abator.config.FullyQualifiedTable)
     */
    public FullyQualifiedJavaType getRecordType(FullyQualifiedTable table) {
		String key = "getRecordType"; //$NON-NLS-1$

		Map map = getTableValueMap(table);
		FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) map.get(key);
		if (fqjt == null) {
			StringBuffer sb = new StringBuffer();
			sb.append(getJavaModelPackage(table));
			sb.append('.');
			sb.append(table.getDomainObjectName());

			fqjt = new FullyQualifiedJavaType(sb.toString());
			map.put(key, fqjt);
		}

		return fqjt;
    }
    
    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.JavaModelGenerator#getRecordWithBLOBsType(org.apache.ibatis.abator.config.FullyQualifiedTable)
     */
    public FullyQualifiedJavaType getRecordWithBLOBsType(
            FullyQualifiedTable table) {
		String key = "getRecordWithBLOBsType"; //$NON-NLS-1$

		Map map = getTableValueMap(table);
		FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) map.get(key);
		if (fqjt == null) {
			StringBuffer sb = new StringBuffer();
			sb.append(getJavaModelPackage(table));
			sb.append('.');
			sb.append(table.getDomainObjectName());
			sb.append("WithBLOBs"); //$NON-NLS-1$

			fqjt = new FullyQualifiedJavaType(sb.toString());
			map.put(key, fqjt);
		}

		return fqjt;
    }
}
