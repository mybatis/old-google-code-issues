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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.abator.api.DAOGenerator;
import org.apache.ibatis.abator.api.FullyQualifiedJavaType;
import org.apache.ibatis.abator.api.GeneratedJavaFile;
import org.apache.ibatis.abator.api.JavaModelGenerator;
import org.apache.ibatis.abator.api.ProgressCallback;
import org.apache.ibatis.abator.api.SqlMapGenerator;
import org.apache.ibatis.abator.config.FullyQualifiedTable;
import org.apache.ibatis.abator.config.TableConfiguration;
import org.apache.ibatis.abator.internal.db.ColumnDefinition;
import org.apache.ibatis.abator.internal.db.ColumnDefinitions;
import org.apache.ibatis.abator.internal.sqlmap.ExampleClause;
import org.apache.ibatis.abator.internal.util.JavaBeansUtil;
import org.apache.ibatis.abator.internal.util.StringUtility;

/**
 * This class generates DAO classes based on the values in the supplied
 * DAOGeneratorTemplate.
 * 
 * @author Jeff Butler
 */
public abstract class DAOGeneratorBaseImpl extends BaseJavaCodeGenerator implements DAOGenerator {

    protected Map properties;

    protected String targetPackage;

    protected String targetProject;

    private Map tableValueMaps;

    protected JavaModelGenerator javaModelGenerator;

    protected SqlMapGenerator sqlMapGenerator;

    protected DAOGeneratorTemplate daoGeneratorTemplate;

    /**
     *  
     */
    public DAOGeneratorBaseImpl() {
        super();
        tableValueMaps = new HashMap();
        this.daoGeneratorTemplate = getDAOGeneratorTemplate();
    }

    /**
     * Returns the template used to generate DAOs from the implementing final class.
     * This method will be called before any other method in the class is called, and will
     * only be called once.
     * 
     * @return the DAOGeneraotrTemplate used for this instance
     */
    public abstract DAOGeneratorTemplate getDAOGeneratorTemplate();

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public void setJavaModelGenerator(JavaModelGenerator javaModelGenerator) {
        this.javaModelGenerator = javaModelGenerator;
    }

    public void setSqlMapGenerator(SqlMapGenerator sqlMapGenerator) {
        this.sqlMapGenerator = sqlMapGenerator;
    }

    private Map getTableValueMap(FullyQualifiedTable table) {
        Map map = (Map) tableValueMaps.get(table);
        if (map == null) {
            map = new HashMap();
            tableValueMaps.put(table, map);
        }

        return map;
    }

    protected FullyQualifiedJavaType getDAOImplementationType(FullyQualifiedTable table) {
        String key = "getDAOImplementationType"; //$NON-NLS-1$

        Map map = getTableValueMap(table);
        FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) map.get(key);
        if (fqjt == null) {
            StringBuffer sb = new StringBuffer();
            sb.append(getDAOPackage(table));
            sb.append('.');
            sb.append(table.getDomainObjectName());
            sb.append("DAOImpl"); //$NON-NLS-1$

            fqjt = new FullyQualifiedJavaType(sb.toString());
            map.put(key, fqjt);
        }

        return fqjt;
    }

    protected FullyQualifiedJavaType getDAOInterfaceType(FullyQualifiedTable table) {
        String key = "getDAOInterfaceType"; //$NON-NLS-1$

        Map map = getTableValueMap(table);
        FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) map.get(key);
        if (fqjt == null) {
            StringBuffer sb = new StringBuffer();
            sb.append(getDAOPackage(table));
            sb.append('.');
            sb.append(table.getDomainObjectName());
            sb.append("DAO"); //$NON-NLS-1$

            fqjt = new FullyQualifiedJavaType(sb.toString());
            map.put(key, fqjt);
        }

        return fqjt;
    }

    protected String getDAOPackage(FullyQualifiedTable table) {
        String key = "getDAOPackage"; //$NON-NLS-1$
        String s;

        Map map = getTableValueMap(table);
        s = (String) map.get(key);
        if (s == null) {
            StringBuffer sb = new StringBuffer(targetPackage);
            if ("true".equals(properties.get("enableSubPackages"))) { //$NON-NLS-1$  //$NON-NLS-2$
                if (StringUtility.stringHasValue(table.getCatalog())) {
                    sb.append('.');
                    sb.append(table.getCatalog().toLowerCase());
                }

                if (StringUtility.stringHasValue(table.getSchema())) {
                    sb.append('.');
                    sb.append(table.getSchema().toLowerCase());
                }
            }

            s = sb.toString();
            map.put(key, s);
        }

        return s;
    }

    protected GeneratedJavaFile getDAOImplementation(
            ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration) {
        
        FullyQualifiedTable table = tableConfiguration.getTable();
        GeneratedJavaFile answer = new GeneratedJavaFile(getDAOImplementationType(table));

        answer.setJavaInterface(false);
        if (daoGeneratorTemplate.getSuperClass() != null) {
            answer.setSuperClass(daoGeneratorTemplate.getSuperClass());
        }

        answer.addSuperInterfaceType(getDAOInterfaceType(table));

        answer.setTargetProject(targetProject);

        Iterator iter = daoGeneratorTemplate.getImplementationImports()
                .iterator();
        while (iter.hasNext()) {
            answer.addImportedType((FullyQualifiedJavaType) iter.next());
        }

        StringBuffer buffer = new StringBuffer();

        // add constructor
        buffer.append(getMethodComment(table));
        newLine(buffer);
        buffer.append(daoGeneratorTemplate
                .getConstructor(getDAOImplementationType(table)));
        answer.addMethod(buffer.toString());

        // add any fields from the template
        iter = daoGeneratorTemplate.getFields().iterator();
        while (iter.hasNext()) {
            buffer.setLength(0);
            buffer.append(getFieldComment(table));
            newLine(buffer);
            indent(buffer, 1);
            buffer.append(iter.next());
            answer.addField(buffer.toString());
        }
        
        // add any methods from the template
        iter = daoGeneratorTemplate.getMethods().iterator();
        while (iter.hasNext()) {
            buffer.setLength(0);
            buffer.append(getMethodComment(table));
            newLine(buffer);
            buffer.append(iter.next());
            answer.addMethod(buffer.toString());
        }

        List methods = getInsertMethods(columnDefinitions, tableConfiguration, false, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }

        methods = getUpdateByPrimaryKeyMethods(columnDefinitions, tableConfiguration, false, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getUpdateByPrimaryKeyWithBLOBsMethods(columnDefinitions, tableConfiguration, false, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getSelectByExampleMethods(columnDefinitions, tableConfiguration, false, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getSelectByExampleWithBLOBsMethods(columnDefinitions, tableConfiguration, false, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getSelectByPrimaryKeyMethods(columnDefinitions, tableConfiguration, false, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getDeleteByExampleMethods(columnDefinitions, tableConfiguration, false, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getDeleteByPrimaryKeyMethods(columnDefinitions, tableConfiguration, false, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getGetExampleParmsMethods(columnDefinitions, tableConfiguration, false, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        return answer;
    }
    
    protected GeneratedJavaFile getDAOInterface(
            ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration) {
        FullyQualifiedTable table = tableConfiguration.getTable();
        GeneratedJavaFile answer = new GeneratedJavaFile(getDAOInterfaceType(table));

        answer.setJavaInterface(true);

        answer.setTargetProject(targetProject);

        Iterator iter = daoGeneratorTemplate.getInterfaceImports().iterator();
        while (iter.hasNext()) {
            answer.addImportedType((FullyQualifiedJavaType) iter.next());
        }

        List methods = getInsertMethods(columnDefinitions, tableConfiguration, true, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }

        methods = getUpdateByPrimaryKeyMethods(columnDefinitions, tableConfiguration, true, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getUpdateByPrimaryKeyWithBLOBsMethods(columnDefinitions, tableConfiguration, true, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getSelectByExampleMethods(columnDefinitions, tableConfiguration, true, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getSelectByExampleWithBLOBsMethods(columnDefinitions, tableConfiguration, true, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getSelectByPrimaryKeyMethods(columnDefinitions, tableConfiguration, true, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getDeleteByExampleMethods(columnDefinitions, tableConfiguration, true, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        methods = getDeleteByPrimaryKeyMethods(columnDefinitions, tableConfiguration, true, answer.getImportedTypes());
        if (methods != null) {
            iter = methods.iterator();
            while (iter.hasNext()) {
                answer.addMethod((String) iter.next());
            }
        }
        
        return answer;
    }
    
    public void setTargetProject(String targetProject) {
        this.targetProject = targetProject;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.DAOGenerator#getGeneratedJavaFiles(org.apache.ibatis.abator.internal.db.ColumnDefinitions, org.apache.ibatis.abator.config.TableConfiguration, org.apache.ibatis.abator.api.ProgressCallback)
     */
    public List getGeneratedJavaFiles(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, ProgressCallback callback) {
        List list = new ArrayList();

		String tableName = tableConfiguration.getTable().getFullyQualifiedTableName();
		
	    callback.startSubTask("Generating DAO Implementation for table " + tableName);
        list.add(getDAOImplementation(columnDefinitions, tableConfiguration));
        
	    callback.startSubTask("Generating DAO Interface for table " + tableName);
        list.add(getDAOInterface(columnDefinitions, tableConfiguration));

        return list;
    }
    
    protected List getInsertMethods(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, boolean interfaceMethod, Set imports) {
        
        if (!columnDefinitions.generateInsert(tableConfiguration)) {
            return null;
        }

        String returnType;
        if (tableConfiguration.getGeneratedKey().isConfigured()) {
            ColumnDefinition cd = columnDefinitions
                    .getColumn(tableConfiguration.getGeneratedKey()
                            .getColumn());
            FullyQualifiedJavaType fqjt = cd.getResolvedJavaType()
                    .getFullyQualifiedJavaType();
            returnType = fqjt.getShortName();
            imports.add(fqjt);
        } else {
            returnType = "void"; //$NON-NLS-1$
        }
        
        StringBuffer buffer = new StringBuffer();
        FullyQualifiedTable table = tableConfiguration.getTable();
        buffer.append(getMethodComment(table));
        newLine(buffer);
        indent(buffer, 1);
        if (!interfaceMethod) {
            buffer.append("public "); //$NON-NLS-1$
        }
        buffer.append(returnType);
        buffer.append(" insert("); //$NON-NLS-1$
        if (columnDefinitions.generateRecordWithBLOBsExtendingPrimaryKey()
                || columnDefinitions
                        .generateRecordWithBLOBsExtendingRecord()) {
            buffer.append(javaModelGenerator.getRecordWithBLOBsType(table)
                    .getShortName());
            imports.add(javaModelGenerator.getRecordWithBLOBsType(table));
        } else if (columnDefinitions.generateRecordExtendingNothing()
                || columnDefinitions.generateRecordExtendingPrimaryKey()) {
            buffer.append(javaModelGenerator.getRecordType(table)
                    .getShortName());
            imports.add(javaModelGenerator.getRecordType(table));
        } else {
            buffer.append(javaModelGenerator.getPrimaryKeyType(table)
                    .getShortName());
            imports.add(javaModelGenerator.getPrimaryKeyType(table));
        }

        buffer.append(" record)"); //$NON-NLS-1$
        if (daoGeneratorTemplate.getCheckedExceptions().size() > 0) {
            buffer.append(" throws "); //$NON-NLS-1$
            
            Iterator iter = daoGeneratorTemplate.getCheckedExceptions().iterator();
            boolean comma = false;
            while (iter.hasNext()) {
                if (comma) {
                    buffer.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                
                FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) iter.next();
                buffer.append(fqjt.getShortName());
                imports.add(fqjt);
            }
        }
        
        if (interfaceMethod) {
            // generate the interface method
            buffer.append(';');
        } else {
            // generate the implementation method
            buffer.append(" {"); //$NON-NLS-1$

            newLine(buffer);
            indent(buffer, 2);
            if (!"void".equals(returnType)) { //$NON-NLS-1$
                buffer.append("Object newKey = "); //$NON-NLS-1$
            }

            buffer.append(daoGeneratorTemplate.getInsertMethod());
            buffer.append("(\""); //$NON-NLS-1$
            buffer.append(sqlMapGenerator.getSqlMapNamespace(table));
            buffer.append('.');
            buffer.append(sqlMapGenerator.getInsertStatementId());
            buffer.append("\", record);"); //$NON-NLS-1$

            if ("Object".equals(returnType)) { //$NON-NLS-1$
                newLine(buffer);
                newLine(buffer);
                indent(buffer, 2);
                buffer.append("return newKey;"); //$NON-NLS-1$
            } else if (!"void".equals(returnType)) { //$NON-NLS-1$
                newLine(buffer);
                newLine(buffer);
                indent(buffer, 2);
                buffer.append("return ("); //$NON-NLS-1$
                buffer.append(returnType);
                buffer.append(") newKey;"); //$NON-NLS-1$
            }

            newLine(buffer);
            indent(buffer, 1);
            buffer.append('}');
        }
        
        ArrayList answer = new ArrayList();
        answer.add(buffer.toString());
        
        return answer;
    }

    /**
     * 
     * @param columnDefinitions column definitions for the current table
     * @param tableConfiguration table configuration for the current table
     * @param interfaceMethod true if the method is an interface method, false if the
     *   method is an implementation method
     * @param imports the method can add FullyQualifiedJavaType objects to this set
     *  if they are required by the resulting method
     * @return a List of methods (as Strings).  A method includes Javadoc.
     */
    protected List getUpdateByPrimaryKeyMethods(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, boolean interfaceMethod, Set imports) {

        if (!columnDefinitions.generateUpdateByPrimaryKey(tableConfiguration)) {
            return null;
        }
        
        StringBuffer buffer = new StringBuffer();
        FullyQualifiedTable table = tableConfiguration.getTable();
        imports.add(javaModelGenerator.getRecordType(table));

        buffer.append(getMethodComment(table));
        newLine(buffer);
        indent(buffer, 1);
        if (!interfaceMethod) {
            buffer.append("public "); //$NON-NLS-1$
        }
        buffer.append("int updateByPrimaryKey("); //$NON-NLS-1$
        buffer.append(javaModelGenerator.getRecordType(table)
                .getShortName());
        buffer.append(" record)"); //$NON-NLS-1$
        if (daoGeneratorTemplate.getCheckedExceptions().size() > 0) {
            buffer.append(" throws "); //$NON-NLS-1$
            
            Iterator iter = daoGeneratorTemplate.getCheckedExceptions().iterator();
            boolean comma = false;
            while (iter.hasNext()) {
                if (comma) {
                    buffer.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                
                FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) iter.next();
                buffer.append(fqjt.getShortName());
                imports.add(fqjt);
            }
        }
        
        if (interfaceMethod) {
            // generate the interface method
            buffer.append(';');
        } else {
            // generate the implementation method
            buffer.append(" {"); //$NON-NLS-1$

            newLine(buffer);
            indent(buffer, 2);
            buffer.append("int rows = "); //$NON-NLS-1$
            buffer.append(daoGeneratorTemplate.getUpdateMethod());
            buffer.append("(\""); //$NON-NLS-1$
            buffer.append(sqlMapGenerator.getSqlMapNamespace(table));
            buffer.append('.');
            buffer.append(sqlMapGenerator.getUpdateByPrimaryKeyStatementId());
            buffer.append("\", record);"); //$NON-NLS-1$

            newLine(buffer);
            newLine(buffer);
            indent(buffer, 2);
            buffer.append("return rows;"); //$NON-NLS-1$

            newLine(buffer);
            indent(buffer, 1);
            buffer.append('}');
        }
        
        ArrayList answer = new ArrayList();
        answer.add(buffer.toString());
        
        return answer;
    }

    protected List getUpdateByPrimaryKeyWithBLOBsMethods(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, boolean interfaceMethod, Set imports) {

        if (!columnDefinitions.generateUpdateByPrimaryKeyWithBLOBs(tableConfiguration)) {
            return null;
        }
        
        StringBuffer buffer = new StringBuffer();
        FullyQualifiedTable table = tableConfiguration.getTable();
        imports.add(javaModelGenerator.getRecordWithBLOBsType(table));

        buffer.append(getMethodComment(table));
        newLine(buffer);
        indent(buffer, 1);
        if (!interfaceMethod) {
            buffer.append("public "); //$NON-NLS-1$
        }
        buffer.append("int updateByPrimaryKey("); //$NON-NLS-1$
        buffer.append(javaModelGenerator.getRecordWithBLOBsType(table)
                .getShortName());
        buffer.append(" record)"); //$NON-NLS-1$
        if (daoGeneratorTemplate.getCheckedExceptions().size() > 0) {
            buffer.append(" throws "); //$NON-NLS-1$
            
            Iterator iter = daoGeneratorTemplate.getCheckedExceptions().iterator();
            boolean comma = false;
            while (iter.hasNext()) {
                if (comma) {
                    buffer.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                
                FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) iter.next();
                buffer.append(fqjt.getShortName());
                imports.add(fqjt);
            }
        }

        if (interfaceMethod) {
            // generate the interface method
            buffer.append(';');
        } else {
            // generate the implementation method
            buffer.append(" {"); //$NON-NLS-1$

            newLine(buffer);
            indent(buffer, 2);
            buffer.append("int rows = "); //$NON-NLS-1$
            buffer.append(daoGeneratorTemplate.getUpdateMethod());
            buffer.append("(\""); //$NON-NLS-1$
            buffer.append(sqlMapGenerator.getSqlMapNamespace(table));
            buffer.append('.');
            buffer.append(sqlMapGenerator
                    .getUpdateByPrimaryKeyWithBLOBsStatementId());
            buffer.append("\", record);"); //$NON-NLS-1$

            newLine(buffer);
            newLine(buffer);
            indent(buffer, 2);
            buffer.append("return rows;"); //$NON-NLS-1$

            newLine(buffer);
            indent(buffer, 1);
            buffer.append('}');
        }
        
        ArrayList answer = new ArrayList();
        answer.add(buffer.toString());
        
        return answer;
    }

    protected List getSelectByExampleMethods(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, boolean interfaceMethod, Set imports) {

        if (!columnDefinitions.generateSelectByExample(tableConfiguration)) {
            return null;
        }
        
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        FullyQualifiedTable table = tableConfiguration.getTable();
        imports.add(javaModelGenerator.getExampleType(table));
        imports.add(new FullyQualifiedJavaType("java.util.List")); //$NON-NLS-1$
        
        buffer1.append(getMethodComment(table));
        newLine(buffer1);
        indent(buffer1, 1);
        if (!interfaceMethod) {
            buffer1.append("public "); //$NON-NLS-1$
        }
        buffer1.append("List selectByExample("); //$NON-NLS-1$
        buffer1.append(javaModelGenerator.getExampleType(table)
                .getShortName());
        buffer1.append(" example, String orderByClause)"); //$NON-NLS-1$
        
        buffer2.append(getMethodComment(table));
        newLine(buffer2);
        indent(buffer2, 1);
        if (!interfaceMethod) {
            buffer2.append("public "); //$NON-NLS-1$
        }
        buffer2.append("List selectByExample("); //$NON-NLS-1$
        buffer2.append(javaModelGenerator.getExampleType(table)
                .getShortName());
        buffer2.append(" example)"); //$NON-NLS-1$

        if (daoGeneratorTemplate.getCheckedExceptions().size() > 0) {
            buffer1.append(" throws "); //$NON-NLS-1$
            buffer2.append(" throws "); //$NON-NLS-1$
            
            Iterator iter = daoGeneratorTemplate.getCheckedExceptions().iterator();
            boolean comma = false;
            while (iter.hasNext()) {
                if (comma) {
                    buffer1.append(", "); //$NON-NLS-1$
                    buffer2.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                
                FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) iter.next();
                buffer1.append(fqjt.getShortName());
                buffer2.append(fqjt.getShortName());
                imports.add(fqjt);
            }
        }
        
        if (interfaceMethod) {
            // generate the interface method
            buffer1.append(';');
            buffer2.append(';');
        } else {
            // generate the implementation method
            imports.add(new FullyQualifiedJavaType("java.util.Map")); //$NON-NLS-1$

            buffer1.append(" {"); //$NON-NLS-1$

            newLine(buffer1);
            indent(buffer1, 2);
            buffer1.append("Map parms = getExampleParms(example);"); //$NON-NLS-1$

            newLine(buffer1);
            newLine(buffer1);
            indent(buffer1, 2);
            buffer1.append("if (orderByClause != null) {"); //$NON-NLS-1$
            newLine(buffer1);
            indent(buffer1, 3);
            buffer1
                    .append("parms.put(\"ABATOR_ORDER_BY_CLAUSE\", orderByClause);"); //$NON-NLS-1$
            newLine(buffer1);
            indent(buffer1, 2);
            buffer1.append("}"); //$NON-NLS-1$

            newLine(buffer1);
            newLine(buffer1);
            indent(buffer1, 2);
            buffer1.append("List list = "); //$NON-NLS-1$
            buffer1.append(daoGeneratorTemplate.getQueryForListMethod());
            buffer1.append("(\""); //$NON-NLS-1$
            buffer1.append(sqlMapGenerator.getSqlMapNamespace(table));
            buffer1.append('.');
            buffer1.append(sqlMapGenerator.getSelectByExampleStatementId());
            buffer1.append("\", parms);"); //$NON-NLS-1$

            newLine(buffer1);
            newLine(buffer1);
            indent(buffer1, 2);
            buffer1.append("return list;"); //$NON-NLS-1$
            newLine(buffer1);
            indent(buffer1, 1);
            buffer1.append('}');

            buffer2.append(" {"); //$NON-NLS-1$
            
            newLine(buffer2);
            indent(buffer2, 2);
            buffer2.append("return selectByExample(example, null);"); //$NON-NLS-1$
            newLine(buffer2);
            indent(buffer2, 1);
            buffer2.append('}');
        }

        ArrayList answer = new ArrayList();
        answer.add(buffer1.toString());
        answer.add(buffer2.toString());
        
        return answer;
    }

    protected List getSelectByExampleWithBLOBsMethods(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, boolean interfaceMethod, Set imports) {

        if (!columnDefinitions.generateSelectByExampleWithBLOBs(tableConfiguration)) {
            return null;
        }
        
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        FullyQualifiedTable table = tableConfiguration.getTable();
        imports.add(javaModelGenerator.getExampleType(table));
        imports.add(new FullyQualifiedJavaType("java.util.List")); //$NON-NLS-1$
        
        buffer1.append(getMethodComment(table));
        newLine(buffer1);
        indent(buffer1, 1);
        if (!interfaceMethod) {
            buffer1.append("public "); //$NON-NLS-1$
        }
        buffer1.append("List selectByExampleWithBLOBs("); //$NON-NLS-1$
        buffer1.append(javaModelGenerator.getExampleType(table)
                .getShortName());
        buffer1.append(" example, String orderByClause)"); //$NON-NLS-1$

        buffer2.append(getMethodComment(table));
        newLine(buffer2);
        indent(buffer2, 1);
        if (!interfaceMethod) {
            buffer2.append("public "); //$NON-NLS-1$
        }
        buffer2.append("List selectByExampleWithBLOBs("); //$NON-NLS-1$
        buffer2.append(javaModelGenerator.getExampleType(table)
                .getShortName());
        buffer2.append(" example)"); //$NON-NLS-1$

        if (daoGeneratorTemplate.getCheckedExceptions().size() > 0) {
            buffer1.append(" throws "); //$NON-NLS-1$
            buffer2.append(" throws "); //$NON-NLS-1$
            
            Iterator iter = daoGeneratorTemplate.getCheckedExceptions().iterator();
            boolean comma = false;
            while (iter.hasNext()) {
                if (comma) {
                    buffer1.append(", "); //$NON-NLS-1$
                    buffer2.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                
                FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) iter.next();
                buffer1.append(fqjt.getShortName());
                buffer2.append(fqjt.getShortName());
                imports.add(fqjt);
            }
        }
        
        if (interfaceMethod) {
            // generate the interface method
            buffer1.append(';');
            buffer2.append(';');
        } else {
            // generate the implementation method
            imports.add(new FullyQualifiedJavaType("java.util.Map")); //$NON-NLS-1$

            buffer1.append(" {"); //$NON-NLS-1$

            newLine(buffer1);
            indent(buffer1, 2);
            buffer1.append("Map parms = getExampleParms(example);"); //$NON-NLS-1$

            newLine(buffer1);
            newLine(buffer1);
            indent(buffer1, 2);
            buffer1.append("if (orderByClause != null) {"); //$NON-NLS-1$
            newLine(buffer1);
            indent(buffer1, 3);
            buffer1
                    .append("parms.put(\"ABATOR_ORDER_BY_CLAUSE\", orderByClause);"); //$NON-NLS-1$
            newLine(buffer1);
            indent(buffer1, 2);
            buffer1.append('}');

            newLine(buffer1);
            newLine(buffer1);
            indent(buffer1, 2);
            buffer1.append("List list = "); //$NON-NLS-1$
            buffer1.append(daoGeneratorTemplate.getQueryForListMethod());
            buffer1.append("(\""); //$NON-NLS-1$
            buffer1.append(sqlMapGenerator.getSqlMapNamespace(table));
            buffer1.append('.');
            buffer1.append(sqlMapGenerator
                    .getSelectByExampleWithBLOBsStatementId());
            buffer1.append("\", parms);"); //$NON-NLS-1$

            newLine(buffer1);
            newLine(buffer1);
            indent(buffer1, 2);
            buffer1.append("return list;"); //$NON-NLS-1$
            newLine(buffer1);
            indent(buffer1, 1);
            buffer1.append('}');

            buffer2.append(" {"); //$NON-NLS-1$
            newLine(buffer2);
            indent(buffer2, 2);
            buffer2.append("return selectByExampleWithBLOBs(example, null);"); //$NON-NLS-1$
            newLine(buffer2);
            indent(buffer2, 1);
            buffer2.append('}');
        }
        
        ArrayList answer = new ArrayList();
        answer.add(buffer1.toString());
        answer.add(buffer2.toString());
        
        return answer;
    }

    protected List getSelectByPrimaryKeyMethods(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, boolean interfaceMethod, Set imports) {

        if (!columnDefinitions.generateSelectByPrimaryKey(tableConfiguration)) {
            return null;
        }
        
        StringBuffer buffer = new StringBuffer();
        FullyQualifiedTable table = tableConfiguration.getTable();
        imports.add(javaModelGenerator.getPrimaryKeyType(table));
        
        buffer.append(getMethodComment(table));
        newLine(buffer);
        indent(buffer, 1);

        if (!interfaceMethod) {
            buffer.append("public "); //$NON-NLS-1$
        }
        
        FullyQualifiedJavaType returnType;
        if (columnDefinitions.generateRecordWithBLOBsExtendingPrimaryKey()
                || columnDefinitions
                        .generateRecordWithBLOBsExtendingRecord()) {
            returnType = javaModelGenerator.getRecordWithBLOBsType(table);

        } else {
            returnType = javaModelGenerator.getRecordType(table);
        }
        buffer.append(returnType.getShortName());
        imports.add(returnType);

        buffer.append(" selectByPrimaryKey("); //$NON-NLS-1$
        buffer.append(javaModelGenerator.getPrimaryKeyType(table)
                .getShortName());
        buffer.append(" key)"); //$NON-NLS-1$
        if (daoGeneratorTemplate.getCheckedExceptions().size() > 0) {
            buffer.append(" throws "); //$NON-NLS-1$
            
            Iterator iter = daoGeneratorTemplate.getCheckedExceptions().iterator();
            boolean comma = false;
            while (iter.hasNext()) {
                if (comma) {
                    buffer.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                
                FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) iter.next();
                buffer.append(fqjt.getShortName());
                imports.add(fqjt);
            }
        }
        
        if (interfaceMethod) {
            // generate the interface method
            buffer.append(';');
        } else {
            // generate the implementation method
            buffer.append(" {"); //$NON-NLS-1$

            newLine(buffer);
            indent(buffer, 2);
            buffer.append(returnType.getShortName());
            buffer.append(" record = ("); //$NON-NLS-1$
            buffer.append(returnType.getShortName());
            buffer.append(") "); //$NON-NLS-1$
            buffer.append(daoGeneratorTemplate.getQueryForObjectMethod());
            buffer.append("(\""); //$NON-NLS-1$
            buffer.append(sqlMapGenerator.getSqlMapNamespace(table));
            buffer.append('.');
            buffer.append(sqlMapGenerator.getSelectByPrimaryKeyStatementId());
            buffer.append("\", key);"); //$NON-NLS-1$

            newLine(buffer);
            newLine(buffer);
            indent(buffer, 2);
            buffer.append("return record;"); //$NON-NLS-1$

            newLine(buffer);
            indent(buffer, 1);
            buffer.append('}');
        }
        
        ArrayList answer = new ArrayList();
        answer.add(buffer.toString());
        
        return answer;
    }

    protected List getDeleteByExampleMethods(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, boolean interfaceMethod, Set imports) {

        if (!columnDefinitions.generateDeleteByExample(tableConfiguration)) {
            return null;
        }
        
        StringBuffer buffer = new StringBuffer();
        FullyQualifiedTable table = tableConfiguration.getTable();
        imports.add(javaModelGenerator.getExampleType(table));

        buffer.append(getMethodComment(table));
        newLine(buffer);
        indent(buffer, 1);
        if (!interfaceMethod) {
            buffer.append("public "); //$NON-NLS-1$
        }
        buffer.append("int deleteByExample("); //$NON-NLS-1$
        buffer.append(javaModelGenerator.getExampleType(table)
                .getShortName());
        buffer.append(" example)"); //$NON-NLS-1$
        if (daoGeneratorTemplate.getCheckedExceptions().size() > 0) {
            buffer.append(" throws "); //$NON-NLS-1$
            
            Iterator iter = daoGeneratorTemplate.getCheckedExceptions().iterator();
            boolean comma = false;
            while (iter.hasNext()) {
                if (comma) {
                    buffer.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                
                FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) iter.next();
                buffer.append(fqjt.getShortName());
                imports.add(fqjt);
            }
        }
        
        if (interfaceMethod) {
            // generate the interface method
            buffer.append(';');
        } else {
            // generate the implementation method
            buffer.append(" {"); //$NON-NLS-1$

            newLine(buffer);
            indent(buffer, 2);
            buffer.append("int rows = "); //$NON-NLS-1$
            buffer.append(daoGeneratorTemplate.getDeleteMethod());
            buffer.append("(\""); //$NON-NLS-1$
            buffer.append(sqlMapGenerator.getSqlMapNamespace(table));
            buffer.append('.');
            buffer.append(sqlMapGenerator.getDeleteByExampleStatementId());
            buffer.append("\", getExampleParms(example));"); //$NON-NLS-1$

            newLine(buffer);
            newLine(buffer);
            indent(buffer, 2);
            buffer.append("return rows;"); //$NON-NLS-1$

            newLine(buffer);
            indent(buffer, 1);
            buffer.append('}');
        }

        ArrayList answer = new ArrayList();
        answer.add(buffer.toString());
        
        return answer;
    }

    protected List getDeleteByPrimaryKeyMethods(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, boolean interfaceMethod, Set imports) {

        if (!columnDefinitions.generateDeleteByPrimaryKey(tableConfiguration)) {
            return null;
        }
        
        StringBuffer buffer = new StringBuffer();
        FullyQualifiedTable table = tableConfiguration.getTable();
        imports.add(javaModelGenerator.getPrimaryKeyType(table));

        buffer.append(getMethodComment(table));
        newLine(buffer);
        indent(buffer, 1);
        if (!interfaceMethod) {
            buffer.append("public "); //$NON-NLS-1$
        }
        buffer.append("int deleteByPrimaryKey("); //$NON-NLS-1$
        buffer.append(javaModelGenerator.getPrimaryKeyType(table)
                .getShortName());
        buffer.append(" key)"); //$NON-NLS-1$
        if (daoGeneratorTemplate.getCheckedExceptions().size() > 0) {
            buffer.append(" throws "); //$NON-NLS-1$
            
            Iterator iter = daoGeneratorTemplate.getCheckedExceptions().iterator();
            boolean comma = false;
            while (iter.hasNext()) {
                if (comma) {
                    buffer.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                
                FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) iter.next();
                buffer.append(fqjt.getShortName());
                imports.add(fqjt);
            }
        }

        if (interfaceMethod) {
            // generate the interface method
            buffer.append(';');
        } else {
            // generate the implementation method
            buffer.append(" {"); //$NON-NLS-1$

            newLine(buffer);
            indent(buffer, 2);
            buffer.append("int rows = "); //$NON-NLS-1$
            buffer.append(daoGeneratorTemplate.getDeleteMethod());
            buffer.append("(\""); //$NON-NLS-1$
            buffer.append(sqlMapGenerator.getSqlMapNamespace(table));
            buffer.append('.');
            buffer.append(sqlMapGenerator.getDeleteByPrimaryKeyStatementId());
            buffer.append("\", key);"); //$NON-NLS-1$

            newLine(buffer);
            newLine(buffer);
            indent(buffer, 2);
            buffer.append("return rows;"); //$NON-NLS-1$

            newLine(buffer);
            indent(buffer, 1);
            buffer.append('}');
        }

        ArrayList answer = new ArrayList();
        answer.add(buffer.toString());
        
        return answer;
    }

    protected List getGetExampleParmsMethods(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, boolean interfaceMethod, Set imports) {
    
        if (!columnDefinitions.generateDeleteByExample(tableConfiguration)
                && !columnDefinitions.generateSelectByExample(tableConfiguration)) {
            return null;
        }
        
        if (interfaceMethod) {
            return null;
        }
        
        StringBuffer buffer = new StringBuffer();
        FullyQualifiedTable table = tableConfiguration.getTable();
        
        imports.add(new FullyQualifiedJavaType("java.util.Map")); //$NON-NLS-1$
        imports.add(new FullyQualifiedJavaType("java.util.HashMap")); //$NON-NLS-1$
        imports.add(javaModelGenerator.getExampleType(table));
    
        buffer.append(getMethodComment(table));
        newLine(buffer);
        indent(buffer, 1);
        buffer.append("private Map getExampleParms("); //$NON-NLS-1$
        buffer.append(javaModelGenerator.getExampleType(table).getShortName());
        buffer.append(" example) {"); //$NON-NLS-1$
    
        newLine(buffer);
        indent(buffer, 2);
        buffer.append("Map parms = new HashMap();"); //$NON-NLS-1$
    
        Iterator iter = columnDefinitions.getAllColumns().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();
    
            if (cd.isBLOBColumn()) {
                continue;
            }
    
            newLine(buffer);
            newLine(buffer);
            indent(buffer, 2);
            buffer.append("switch (example."); //$NON-NLS-1$
            String property = cd.getJavaProperty() + "_Indicator"; //$NON-NLS-1$
            buffer.append(JavaBeansUtil.getGetterMethodName(property));
            buffer.append("()) {"); //$NON-NLS-1$
    
            Iterator clauseIterator = ExampleClause.getAllExampleClauses();
            while (clauseIterator.hasNext()) {
                ExampleClause clause = (ExampleClause) clauseIterator.next();
    
                if (clause.isCharacterOnly() && !cd.isCharacterColumn()) {
                    continue;
                }
    
                newLine(buffer);
                indent(buffer, 2);
                buffer.append("case "); //$NON-NLS-1$
                buffer.append(javaModelGenerator.getExampleType(table)
                        .getShortName());
                buffer.append('.');
                buffer.append(clause.getExamplePropertyName());
                buffer.append(':');
                newLine(buffer);
                indent(buffer, 3);
                buffer.append("if (example.isCombineTypeOr()) {"); //$NON-NLS-1$
                newLine(buffer);
                indent(buffer, 4);
                buffer.append("parms.put(\""); //$NON-NLS-1$
                buffer.append(clause.getSelectorOrProperty(cd));
                buffer.append("\", \"Y\");"); //$NON-NLS-1$
                newLine(buffer);
                indent(buffer, 3);
                buffer.append("} else {"); //$NON-NLS-1$
                newLine(buffer);
                indent(buffer, 4);
                buffer.append("parms.put(\""); //$NON-NLS-1$
                buffer.append(clause.getSelectorAndProperty(cd));
                buffer.append("\", \"Y\");"); //$NON-NLS-1$
                newLine(buffer);
                indent(buffer, 3);
                buffer.append('}'); //$NON-NLS-1$
    
                if (clause.isPropertyInMapRequired()) {
                    String exampleProperty = cd.getJavaProperty();
                    newLine(buffer);
                    indent(buffer, 3);
                    buffer.append("parms.put(\""); //$NON-NLS-1$
                    buffer.append(exampleProperty);
                    buffer.append("\", ");
                    FullyQualifiedJavaType fqjt = cd.getResolvedJavaType().getFullyQualifiedJavaType();
                    if (fqjt.isPrimitive()) {
                        buffer.append("new ");
                        buffer.append(fqjt.getWrapperClass());
                        buffer.append('(');
                        buffer.append("example."); //$NON-NLS-1$
                        buffer.append(JavaBeansUtil
                            .getGetterMethodName(exampleProperty));
                        buffer.append("()));"); //$NON-NLS-1$
                    } else {
                        buffer.append("example."); //$NON-NLS-1$
                        buffer.append(JavaBeansUtil
                            .getGetterMethodName(exampleProperty));
                        buffer.append("());"); //$NON-NLS-1$
                    }
                }
                
                newLine(buffer);
                indent(buffer, 3);
                buffer.append("break;"); //$NON-NLS-1$
                newLine(buffer);
            }
    
            indent(buffer, 2);
            buffer.append('}');
        }
    
        newLine(buffer);
        newLine(buffer);
        indent(buffer, 2);
        buffer.append("return parms;"); //$NON-NLS-1$
        newLine(buffer);
        indent(buffer, 1);
        buffer.append('}');
    
        ArrayList answer = new ArrayList();
        answer.add(buffer.toString());
        
        return answer;
    }
}
