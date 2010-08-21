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
package org.apache.ibatis.abator.internal.sqlmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.ibatis.abator.api.GeneratedXmlFile;
import org.apache.ibatis.abator.api.JavaModelGenerator;
import org.apache.ibatis.abator.api.ProgressCallback;
import org.apache.ibatis.abator.api.SqlMapGenerator;
import org.apache.ibatis.abator.config.FullyQualifiedTable;
import org.apache.ibatis.abator.config.TableConfiguration;
import org.apache.ibatis.abator.internal.db.ColumnDefinition;
import org.apache.ibatis.abator.internal.db.ColumnDefinitions;
import org.apache.ibatis.abator.internal.util.StringUtility;

/**
 * @author Jeff Butler
 */
public class SqlMapGeneratorDefaultImpl implements SqlMapGenerator {

    /**
     * Contains any properties passed in from the SqlMap configuration element.
     */
    protected Map properties;

    /**
     * This is the target package from the SqlMap configuration element
     */
    protected String targetPackage;

    /**
     * This is the target project from the SqlMap configuration element
     */
    protected String targetProject;

    /**
     * This is the java model generator associated with the current generation
     * context. Methods in this interface can be used to determine the
     * appropriate result and parameter class names.
     */
    protected JavaModelGenerator javaModelGenerator;

    /**
     * This is a map of maps. The map is keyed by a FullyQualifiedTable object.
     * The inner map holds generated strings keyed by the String name. This
     * Map is used to cache generated Strings.
     */
    private Map tableStringMaps;

    private String lineSeparator;

    /**
     * Constructs an instance of SqlMapGeneratorDefaultImpl
     */
    public SqlMapGeneratorDefaultImpl() {
        super();
        tableStringMaps = new HashMap();
        lineSeparator = System.getProperty("line.separator");
        if (lineSeparator == null) {
            lineSeparator = "\n";
        }
    }

    private Map getTableStringMap(FullyQualifiedTable table) {
        Map map = (Map) tableStringMaps.get(table);
        if (map == null) {
            map = new HashMap();
            tableStringMaps.put(table, map);
        }

        return map;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#setProperties(java.util.Map)
     */
    public void setProperties(Map properties) {
        this.properties = properties;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#setTargetPackage(java.lang.String)
     */
    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#setJavaModelGenerator(org.apache.ibatis.abator.api.JavaModelGenerator)
     */
    public void setJavaModelGenerator(JavaModelGenerator javaModelGenerator) {
        this.javaModelGenerator = javaModelGenerator;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#getGeneratedXMLFiles(org.apache.ibatis.abator.internal.db.ColumnDefinitions, org.apache.ibatis.abator.config.TableConfiguration, org.apache.ibatis.abator.api.ProgressCallback)
     */
    public List getGeneratedXMLFiles(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, ProgressCallback callback) {
        ArrayList list = new ArrayList();

        callback.startSubTask("Generating SQL Map for table "
                + tableConfiguration.getTable().getFullyQualifiedTableName());
        list.add(getSqlMap(columnDefinitions, tableConfiguration));

        return list;
    }

    /**
     * Creates the default implementation of the Sql Map
     * 
     * @param columnDefinitions introspected column definitions for the current table
     * @param tableConfiguration table configuration for the current table
     * @return A GeneratedXMLFile for the current table
     */
    protected GeneratedXmlFile getSqlMap(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration) {
        StringBuffer xml = new StringBuffer();

        xml.append("<?xml"); //$NON-NLS-1$
        addAttribute(xml, "version", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
        addAttribute(xml, "encoding", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
        xml.append("?>"); //$NON-NLS-1$

        newLine(xml);
        xml.append(getDocType());
        newLine(xml);

        String comment = getFileComment(tableConfiguration);
        if (StringUtility.stringHasValue(comment)) {
            newLine(xml);
            xml.append(comment);
        }

        newLine(xml);
        xml.append(getSqlMapElement(columnDefinitions, tableConfiguration));
        newLine(xml);

        GeneratedXmlFile answer = new GeneratedXmlFile();
        answer.setContent(xml.toString());
        answer.setFileName(getSqlMapFileName(tableConfiguration.getTable()));
        answer
                .setTargetPackage(getSqlMapPackage(tableConfiguration
                        .getTable()));
        answer.setTargetProject(targetProject);

        return answer;
    }

    /**
     * Creates the DOCTYPE for the SqlMap
     * 
     * @return the DOCTYPE
     */
    protected String getDocType() {
        StringBuffer xml = new StringBuffer();

        xml.append("<!DOCTYPE sqlMap PUBLIC"); //$NON-NLS-1$
        addQuotedString(xml, XmlConstants.SQL_MAP_PUBLIC_ID, true);
        addQuotedString(xml, XmlConstants.SQL_MAP_SYSTEM_ID, true);
        xml.append('>');

        return xml.toString();
    }

    /**
     * Creates the sqlMap element (the root element, and all child elements).
     * 
     * @param columnDefinitions introspected column definitions for the current table
     * @param tableConfiguration table configuration for the current table
     * @return a well formatted String containing the sqlMap element, and all
     *         child elements
     */
    protected String getSqlMapElement(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration) {
        StringBuffer xml = new StringBuffer();

        xml.append("<sqlMap"); //$NON-NLS-1$
        addAttribute(xml,
                "namespace", getSqlMapNamespace(tableConfiguration.getTable())); //$NON-NLS-1$
        xml.append('>');
        newLine(xml);

        String element;
        if (columnDefinitions.generateBaseResultMap(tableConfiguration)) {
            element = getBaseResultMapElement(columnDefinitions,
                    tableConfiguration, 1);
            if (StringUtility.stringHasValue(element)) {
                newLine(xml);
                xml.append(element);
            }
        }

        if (columnDefinitions.generateResultMapWithBLOBs(tableConfiguration)) {
            element = getResultMapWithBLOBsElement(columnDefinitions,
                    tableConfiguration, 1);
            if (StringUtility.stringHasValue(element)) {
                newLine(xml);
                xml.append(element);
            }
        }

        if (tableConfiguration.isSelectByExampleStatementEnabled()
                || tableConfiguration.isDeleteByExampleStatementEnabled()) {
            element = getByExampleWhereClauseFragment(columnDefinitions,
                    tableConfiguration, 1);
            if (StringUtility.stringHasValue(element)) {
                newLine(xml);
                xml.append(element);
            }
        }

        if (columnDefinitions.generateSelectByPrimaryKey(tableConfiguration)) {
            element = getSelectByPrimaryKey(columnDefinitions,
                    tableConfiguration, 1);
            if (StringUtility.stringHasValue(element)) {
                newLine(xml);
                xml.append(element);
            }
        }

        if (columnDefinitions.generateSelectByExample(tableConfiguration)) {
            element = getSelectByExample(columnDefinitions, tableConfiguration,
                    1);
            if (StringUtility.stringHasValue(element)) {
                newLine(xml);
                xml.append(element);
            }
        }

        if (columnDefinitions
                .generateSelectByExampleWithBLOBs(tableConfiguration)) {
            element = getSelectByExampleWithBLOBs(columnDefinitions,
                    tableConfiguration, 1);
            if (StringUtility.stringHasValue(element)) {
                newLine(xml);
                xml.append(element);
            }
        }

        if (columnDefinitions.generateDeleteByPrimaryKey(tableConfiguration)) {
            element = getDeleteByPrimaryKey(columnDefinitions,
                    tableConfiguration, 1);
            if (StringUtility.stringHasValue(element)) {
                newLine(xml);
                xml.append(element);
            }
        }

        if (columnDefinitions.generateDeleteByExample(tableConfiguration)) {
            element = getDeleteByExample(columnDefinitions, tableConfiguration,
                    1);
            if (StringUtility.stringHasValue(element)) {
                newLine(xml);
                xml.append(element);
            }
        }

        if (columnDefinitions.generateInsert(tableConfiguration)) {
            element = getInsertElement(columnDefinitions, tableConfiguration, 1);
            if (StringUtility.stringHasValue(element)) {
                newLine(xml);
                xml.append(element);
            }
        }

        if (columnDefinitions
                .generateUpdateByPrimaryKeyWithBLOBs(tableConfiguration)) {
            element = getUpdateByPrimaryKeyWithBLOBs(columnDefinitions,
                    tableConfiguration, 1);
            if (StringUtility.stringHasValue(element)) {
                newLine(xml);
                xml.append(element);
            }
        }

        if (columnDefinitions.generateUpdateByPrimaryKey(tableConfiguration)) {
            element = getUpdateByPrimaryKey(columnDefinitions,
                    tableConfiguration, 1);
            if (StringUtility.stringHasValue(element)) {
                newLine(xml);
                xml.append(element);
            }
        }

        newLine(xml);
        xml.append("</sqlMap>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * Returns a file level comment. The default implementation returns null,
     * but this can be overridden to provide a file level comment (such as a
     * copyright notice).
     * 
     * @param tableConfiguration table configuration for the current table
     * @return a properly formatted comment
     */
    protected String getFileComment(TableConfiguration tableConfiguration) {
        return null;
    }

    /**
     * Returns a suitable comment to warn users that the element was generated,
     * and when it was generated. The returned string should be well formatted,
     * and indented at the specified level.
     * 
     * @param indentLevel the required indent level
     * @return a properly formatted comment
     */
    protected String getElementComment(int indentLevel) {
        StringBuffer sb = new StringBuffer();

        indent(sb, indentLevel);
        sb.append("<!--"); //$NON-NLS-1$
        newLine(sb);
        indent(sb, indentLevel);
        sb
                .append("  WARNING - This element is automatically generated by Abator for iBATIS, do not modify."); //$NON-NLS-1$
        newLine(sb);
        indent(sb, indentLevel);
        sb.append("  This element was generated on "); //$NON-NLS-1$
        sb.append(new Date());
        sb.append('.');
        newLine(sb);
        indent(sb, indentLevel);
        sb.append("-->"); //$NON-NLS-1$

        return sb.toString();
    }

    /**
     * This method should return a String which is formatted XML for the result
     * map (without any BLOBs if they exist in the table).
     * 
     * @param columnDefinitions
     *            introspected column definitions for the current table
     * @param tableConfiguration
     *            table configuration for the current table
     * @param indentLevel
     *            the required indent level
     * @return a well formatted String containing the resultMap element
     */
    protected String getBaseResultMapElement(
            ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, int indentLevel) {
        StringBuffer xml = new StringBuffer();

        indent(xml, indentLevel);
        xml.append("<resultMap"); //$NON-NLS-1$
        addAttribute(xml, "id", getResultMapName(tableConfiguration.getTable())); //$NON-NLS-1$
        if (columnDefinitions.generateRecordExtendingNothing()
                || columnDefinitions.generateRecordExtendingPrimaryKey()) {
            addAttribute(xml, "class", javaModelGenerator //$NON-NLS-1$
                    .getRecordType(tableConfiguration.getTable())
                    .getFullyQualifiedName());
        } else {
            addAttribute(xml, "class", javaModelGenerator //$NON-NLS-1$
                    .getPrimaryKeyType(tableConfiguration.getTable())
                    .getFullyQualifiedName());
        }
        xml.append('>');
        newLine(xml);

        xml.append(getElementComment(indentLevel + 1));
        newLine(xml);

        Iterator iter = columnDefinitions.getAllColumns().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();
            if (cd.isBLOBColumn()) {
                continue;
            }

            indent(xml, indentLevel + 1);
            xml.append("<result"); //$NON-NLS-1$
            addAttribute(xml, "column", cd.getColumnName()); //$NON-NLS-1$
            addAttribute(xml, "property", cd.getJavaProperty()); //$NON-NLS-1$
            addAttribute(xml,
                    "jdbcType", cd.getResolvedJavaType().getJdbcTypeName()); //$NON-NLS-1$
            xml.append((" />")); //$NON-NLS-1$
            newLine(xml);
        }

        indent(xml, indentLevel);
        xml.append("</resultMap>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * This method should return a String which is formatted XML for the result
     * map (with any BLOBs if they exist in the table). Typically this result
     * map extends the base result map.
     * 
     * @param columnDefinitions
     *            introspected column definitions for the current table
     * @param tableConfiguration
     *            table configuration for the current table
     * @param indentLevel
     *            the required indent level
     * @return a well formatted String containing the resultMap element
     */
    protected String getResultMapWithBLOBsElement(
            ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, int indentLevel) {
        StringBuffer xml = new StringBuffer();

        indent(xml, indentLevel);
        xml.append("<resultMap"); //$NON-NLS-1$
        addAttribute(
                xml,
                "id", getResultMapName(tableConfiguration.getTable()) + "WithBLOBs"); //$NON-NLS-1$
        addAttribute(xml, "class", javaModelGenerator //$NON-NLS-1$
                .getRecordWithBLOBsType(tableConfiguration.getTable())
                .getFullyQualifiedName());

        addAttribute(xml, "extends", getSqlMapNamespace(tableConfiguration
                .getTable())
                + "." + getResultMapName(tableConfiguration.getTable()));

        xml.append('>');
        newLine(xml);

        xml.append(getElementComment(indentLevel + 1));
        newLine(xml);

        Iterator iter = columnDefinitions.getAllColumns().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();
            if (!cd.isBLOBColumn()) {
                continue;
            }

            indent(xml, indentLevel + 1);
            xml.append("<result"); //$NON-NLS-1$
            addAttribute(xml, "column", cd.getColumnName()); //$NON-NLS-1$
            addAttribute(xml, "property", cd.getJavaProperty()); //$NON-NLS-1$
            addAttribute(xml,
                    "jdbcType", cd.getResolvedJavaType().getJdbcTypeName()); //$NON-NLS-1$
            xml.append((" />")); //$NON-NLS-1$
            newLine(xml);
        }

        indent(xml, indentLevel);
        xml.append("</resultMap>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * This method should return a String which is formatted XML for the insert
     * statement.
     * 
     * @param columnDefinitions
     *            introspected column definitions for the current table
     * @param tableConfiguration
     *            table configuration for the current table
     * @param indentLevel
     *            the required indent level
     * @return a well formatted String containing the insert element
     */
    protected String getInsertElement(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, int indentLevel) {

        StringBuffer xml = new StringBuffer();

        ColumnDefinition identityColumn = null;

        indent(xml, indentLevel);
        xml.append("<insert"); //$NON-NLS-1$
        addAttribute(xml, "id", getInsertStatementId()); //$NON-NLS-1$
        if (columnDefinitions.generateRecordWithBLOBsExtendingPrimaryKey()
                || columnDefinitions.generateRecordWithBLOBsExtendingRecord()) {
            addAttribute(xml, "parameterClass", javaModelGenerator //$NON-NLS-1$
                    .getRecordWithBLOBsType(tableConfiguration.getTable())
                    .getFullyQualifiedName());
        } else if (columnDefinitions.generateRecordExtendingNothing()
                || columnDefinitions.generateRecordExtendingPrimaryKey()) {
            addAttribute(xml, "parameterClass", javaModelGenerator //$NON-NLS-1$
                    .getRecordType(tableConfiguration.getTable())
                    .getFullyQualifiedName());
        } else {
            addAttribute(xml, "parameterClass", javaModelGenerator //$NON-NLS-1$
                    .getPrimaryKeyType(tableConfiguration.getTable())
                    .getFullyQualifiedName());
        }
        xml.append('>');
        newLine(xml);

        xml.append(getElementComment(indentLevel + 1));
        newLine(xml);

        if (tableConfiguration.getGeneratedKey().isConfigured()
                && !tableConfiguration.getGeneratedKey().isIdentity()) {
            // pre-generated key
            newLine(xml);
            xml.append(getSelectKey(
                    columnDefinitions.getColumn(tableConfiguration
                            .getGeneratedKey().getColumn()),
                    tableConfiguration, indentLevel + 1));
            newLine(xml);
        }

        StringBuffer insertClause = new StringBuffer();
        StringBuffer valuesClause = new StringBuffer();
        StringBuffer insertFragment = new StringBuffer();
        StringBuffer valuesFragment = new StringBuffer();

        insertClause.append("insert into "); //$NON-NLS-1$
        insertClause.append(tableConfiguration.getTable()
                .getFullyQualifiedTableName());
        insertClause.append(" ("); //$NON-NLS-1$

        valuesClause.append("values ("); //$NON-NLS-1$

        boolean comma = false;
        Iterator iter = columnDefinitions.getAllColumns().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();

            if (cd.isIdentity()) {
                identityColumn = cd;
                // cannot set values on identity fields
                continue;
            }

            insertFragment.setLength(0);
            valuesFragment.setLength(0);

            valuesFragment.setLength(0);
            valuesFragment.append('#');
            valuesFragment.append(cd.getJavaProperty());
            valuesFragment.append(':');
            valuesFragment.append(cd.getResolvedJavaType().getJdbcTypeName());
            valuesFragment.append('#');

            insertFragment.append(cd.getColumnName());

            if (comma) {
                insertFragment.insert(0, ", "); //$NON-NLS-1$
                valuesFragment.insert(0, ", "); //$NON-NLS-1$
            } else {
                comma = true;
            }

            insertClause.append(insertFragment);
            valuesClause.append(valuesFragment);
        }
        insertClause.append(')');
        valuesClause.append(')');

        xml.append(formatLongString(insertClause.toString(), 80,
                indentLevel + 1));
        xml.append(formatLongString(valuesClause.toString(), 80,
                indentLevel + 1));

        if (identityColumn != null) {
            newLine(xml);
            xml.append(getSelectKey(identityColumn, tableConfiguration,
                    indentLevel + 1));
            newLine(xml);
        }

        indent(xml, indentLevel);
        xml.append("</insert>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * This method should return well formatted XML for the update by primary
     * key statement that updates all fields in the table (including BLOB
     * fields).
     * 
     * @param columnDefinitions
     *            introspected column definitions for the current table
     * @param tableConfiguration
     *            table configuration for the current table
     * @param indentLevel
     *            the required indent level
     * @return a well formatted String containing the update element
     */
    protected String getUpdateByPrimaryKeyWithBLOBs(
            ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, int indentLevel) {

        StringBuffer xml = new StringBuffer();

        indent(xml, indentLevel);
        xml.append("<update"); //$NON-NLS-1$
        addAttribute(xml, "id", getUpdateByPrimaryKeyWithBLOBsStatementId()); //$NON-NLS-1$
        addAttribute(xml, "parameterClass", javaModelGenerator //$NON-NLS-1$
                .getRecordWithBLOBsType(tableConfiguration.getTable())
                .getFullyQualifiedName());
        xml.append('>');
        newLine(xml);

        xml.append(getElementComment(indentLevel + 1));
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("update "); //$NON-NLS-1$
        xml.append(tableConfiguration.getTable().getFullyQualifiedTableName());
        newLine(xml);
        indent(xml, indentLevel + 1);
        xml.append("set "); //$NON-NLS-1$

        StringBuffer fragment = new StringBuffer();
        boolean comma = false;
        Iterator iter = columnDefinitions.getNonPrimaryKeyColumns().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();
            fragment.setLength(0);

            fragment.append(cd.getColumnName());
            fragment.append(" = #"); //$NON-NLS-1$
            fragment.append(cd.getJavaProperty());
            fragment.append(':');
            fragment.append(cd.getResolvedJavaType().getJdbcTypeName());
            fragment.append('#');

            if (comma) {
                xml.append(',');
                newLine(xml);
                indent(xml, indentLevel + 2);
            } else {
                comma = true;
            }

            xml.append(fragment);
        }

        newLine(xml);
        indent(xml, indentLevel + 1);
        xml.append("where "); //$NON-NLS-1$
        boolean and = false;
        iter = columnDefinitions.getPrimaryKey().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();

            if (and) {
                xml.append(" and"); //$NON-NLS-1$
                newLine(xml);
                indent(xml, indentLevel + 2);
            } else {
                and = true;
            }

            xml.append(cd.getColumnName());
            xml.append(" = #"); //$NON-NLS-1$
            xml.append(cd.getJavaProperty());
            xml.append('#');
        }

        newLine(xml);
        indent(xml, indentLevel);
        xml.append("</update>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * This method should return well formatted XML for the update by primary
     * key statement that updates all fields in the table (excluding BLOB
     * fields).
     * 
     * @param columnDefinitions
     *            introspected column definitions for the current table
     * @param tableConfiguration
     *            table configuration for the current table
     * @param indentLevel
     *            the required indent level
     * @return a well formatted String containing the update element
     */
    protected String getUpdateByPrimaryKey(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, int indentLevel) {

        StringBuffer xml = new StringBuffer();

        indent(xml, indentLevel);
        xml.append("<update"); //$NON-NLS-1$
        addAttribute(xml, "id", getUpdateByPrimaryKeyStatementId()); //$NON-NLS-1$
        addAttribute(xml, "parameterClass", javaModelGenerator //$NON-NLS-1$
                .getRecordType(tableConfiguration.getTable())
                .getFullyQualifiedName());
        xml.append('>');
        newLine(xml);

        xml.append(getElementComment(indentLevel + 1));
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("update "); //$NON-NLS-1$
        xml.append(tableConfiguration.getTable().getFullyQualifiedTableName());
        newLine(xml);
        indent(xml, indentLevel + 1);
        xml.append("set "); //$NON-NLS-1$

        StringBuffer fragment = new StringBuffer();
        boolean comma = false;
        Iterator iter = columnDefinitions.getNonBLOBColumns().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();

            fragment.setLength(0);

            fragment.append(cd.getColumnName());
            fragment.append(" = #"); //$NON-NLS-1$
            fragment.append(cd.getJavaProperty());
            fragment.append(':');
            fragment.append(cd.getResolvedJavaType().getJdbcTypeName());
            fragment.append('#');

            if (comma) {
                xml.append(',');
                newLine(xml);
                indent(xml, indentLevel + 2);
            } else {
                comma = true;
            }

            xml.append(fragment);
        }

        newLine(xml);
        indent(xml, indentLevel + 1);
        xml.append("where "); //$NON-NLS-1$
        boolean and = false;
        iter = columnDefinitions.getPrimaryKey().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();
            if (cd.isBLOBColumn()) {
                continue;
            }

            if (and) {
                xml.append(" and"); //$NON-NLS-1$
                newLine(xml);
                indent(xml, indentLevel + 2);
            } else {
                and = true;
            }

            xml.append(cd.getColumnName());
            xml.append(" = #"); //$NON-NLS-1$
            xml.append(cd.getJavaProperty());
            xml.append('#');
        }

        newLine(xml);
        indent(xml, indentLevel);
        xml.append("</update>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * This method should return well formatted XML for the delete by primary
     * key statement.
     * 
     * @param columnDefinitions
     *            introspected column definitions for the current table
     * @param tableConfiguration
     *            table configuration for the current table
     * @param indentLevel
     *            the required indent level
     * @return a well formatted String containing the delete element
     */
    protected String getDeleteByPrimaryKey(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, int indentLevel) {
        StringBuffer xml = new StringBuffer();

        indent(xml, indentLevel);
        xml.append("<delete"); //$NON-NLS-1$
        addAttribute(xml, "id", getDeleteByPrimaryKeyStatementId()); //$NON-NLS-1$
        addAttribute(xml, "parameterClass", javaModelGenerator //$NON-NLS-1$
                .getPrimaryKeyType(tableConfiguration.getTable())
                .getFullyQualifiedName());
        xml.append('>');
        newLine(xml);

        xml.append(getElementComment(indentLevel + 1));
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("delete from "); //$NON-NLS-1$
        xml.append(tableConfiguration.getTable().getFullyQualifiedTableName());
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("where "); //$NON-NLS-1$
        boolean and = false;
        Iterator iter = columnDefinitions.getPrimaryKey().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();
            if (and) {
                xml.append(" and"); //$NON-NLS-1$
                newLine(xml);
                indent(xml, indentLevel + 2);
            } else {
                and = true;
            }

            xml.append(cd.getColumnName());
            xml.append(" = #"); //$NON-NLS-1$
            xml.append(cd.getJavaProperty());
            xml.append('#');
        }

        newLine(xml);
        indent(xml, indentLevel);
        xml.append("</delete>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * This method should return well formatted XML for the delete by example
     * statement. This statement uses the "by example" SQL fragment
     * 
     * @param columnDefinitions
     *            introspected column definitions for the current table
     * @param tableConfiguration
     *            table configuration for the current table
     * @param indentLevel
     *            the required indent level
     * @return a well formatted String containing the delete element
     */
    protected String getDeleteByExample(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, int indentLevel) {

        StringBuffer xml = new StringBuffer();

        indent(xml, indentLevel);
        xml.append("<delete"); //$NON-NLS-1$
        addAttribute(xml, "id", getDeleteByExampleStatementId()); //$NON-NLS-1$
        addAttribute(xml, "parameterClass", "java.util.Map"); //$NON-NLS-1$ //$NON-NLS-2$
        xml.append('>');
        newLine(xml);

        xml.append(getElementComment(indentLevel + 1));
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("delete from "); //$NON-NLS-1$
        xml.append(tableConfiguration.getTable().getFullyQualifiedTableName());
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("<include"); //$NON-NLS-1$
        addAttribute(xml, "refid", //$NON-NLS-1$
                getSqlMapNamespace(tableConfiguration.getTable())
                        + "." + getExampleWhereClauseId()); //$NON-NLS-1$
        xml.append("/>"); //$NON-NLS-1$
        newLine(xml);

        indent(xml, indentLevel);
        xml.append("</delete>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * This method should return well formatted XML for the select by primary
     * key statement. The statement should include all fields in the table,
     * including BLOB fields.
     * 
     * @param columnDefinitions
     *            introspected column definitions for the current table
     * @param tableConfiguration
     *            table configuration for the current table
     * @param indentLevel
     *            the required indent level
     * @return a well formatted String containing the select element
     */
    protected String getSelectByPrimaryKey(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, int indentLevel) {

        StringBuffer xml = new StringBuffer();

        indent(xml, indentLevel);
        xml.append("<select"); //$NON-NLS-1$
        addAttribute(xml, "id", getSelectByPrimaryKeyStatementId()); //$NON-NLS-1$
        if (columnDefinitions.generateResultMapWithBLOBs(tableConfiguration)) {
            addAttribute(
                    xml,
                    "resultMap", getResultMapName(tableConfiguration.getTable()) + "WithBLOBs"); //$NON-NLS-1$
        } else {
            addAttribute(
                    xml,
                    "resultMap", getResultMapName(tableConfiguration.getTable())); //$NON-NLS-1$
        }
        addAttribute(xml, "parameterClass", javaModelGenerator //$NON-NLS-1$
                .getPrimaryKeyType(tableConfiguration.getTable())
                .getFullyQualifiedName());
        xml.append('>');
        newLine(xml);

        xml.append(getElementComment(indentLevel + 1));
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("select "); //$NON-NLS-1$

        boolean comma = false;

        if (StringUtility.stringHasValue(tableConfiguration
                .getSelectByPrimaryKeyQueryId())) {
            xml.append('\'');
            xml.append(tableConfiguration.getSelectByPrimaryKeyQueryId());
            xml.append("' as QUERYID"); //$NON-NLS-1$
            comma = true;
        }

        Iterator iter = columnDefinitions.getAllColumns().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();
            if (comma) {
                xml.append(',');
                newLine(xml);
                indent(xml, indentLevel + 2);
            } else {
                comma = true;
            }

            xml.append(cd.getColumnName());
        }

        newLine(xml);
        indent(xml, indentLevel + 1);
        xml.append("from "); //$NON-NLS-1$
        xml.append(tableConfiguration.getTable().getFullyQualifiedTableName());
        newLine(xml);
        indent(xml, indentLevel + 1);
        xml.append("where "); //$NON-NLS-1$

        boolean and = false;
        iter = columnDefinitions.getPrimaryKey().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();
            if (and) {
                xml.append(" and"); //$NON-NLS-1$
                newLine(xml);
                indent(xml, indentLevel + 2);
            } else {
                and = true;
            }

            xml.append(cd.getColumnName());
            xml.append(" = #"); //$NON-NLS-1$
            xml.append(cd.getJavaProperty());
            xml.append('#');
        }

        newLine(xml);
        indent(xml, indentLevel);
        xml.append("</select>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * Utility method that indents the buffer by the default amount (two spaces
     * per indent level).
     * 
     * @param sb a StringBuffer to append to
     * @param indentLevel the required indent level
     */
    protected void indent(StringBuffer sb, int indentLevel) {
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  "); //$NON-NLS-1$
        }
    }

    /**
     * Utility method that adds an XML attribute to a StringBuffer.
     * 
     * @param sb the StringBuffer to append to
     * @param name the name of the attribute
     * @param value the value of the attribute (this method will add the enclosing
     *            quotation marks automatically)
     */
    protected void addAttribute(StringBuffer sb, String name, String value) {
        sb.append(' ');
        sb.append(name);
        sb.append('=');
        addQuotedString(sb, value, false);
    }

    /**
     * Utility method .  Adds a string surround with quotation marks to a
     * StringBuffer
     * 
     * @param sb the StringBuffer to append to
     * @param value the value to append (the value will be surrounded by quotation
     *            marks by this method)
     * @param spaceBefore if true, a space will be added before the value
     */
    protected void addQuotedString(StringBuffer sb, String value,
            boolean spaceBefore) {
        if (spaceBefore) {
            sb.append(' ');
        }

        sb.append('\"');
        sb.append(value);
        sb.append('\"');
    }

    /**
     * Utility method.  Adds a newline character to a StringBuffer.
     * 
     * @param sb the StringBuffer to be appended to
     */
    protected void newLine(StringBuffer sb) {
        sb.append(lineSeparator);
    }

    /**
     * Utility method.  Takes a long string and breaks it into multiple lines of
     * whose width is no longer that the specified maximum line length.
     * 
     * @param s the String to be formatted
     * @param maxLineLength the maximum line length
     * @param indentLevel the required indent level of all lines
     * @return the formatted String
     */
    protected String formatLongString(String s, int maxLineLength,
            int indentLevel) {
        ArrayList lines = new ArrayList();
        StringTokenizer st = new StringTokenizer(s, " "); //$NON-NLS-1$

        StringBuffer sb = new StringBuffer();
        indent(sb, indentLevel);

        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (sb.length() + token.length() + 1 > maxLineLength) {
                lines.add(sb.toString());
                sb.setLength(0);
                indent(sb, indentLevel + 1);
            }

            sb.append(token);
            sb.append(' ');
        }

        if (sb.length() > 0) {
            lines.add(sb.toString());
        }

        sb.setLength(0);
        Iterator iter = lines.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
            newLine(sb);
        }

        return sb.toString();
    }

    /**
     * This method should return well formatted XML for the select key element 
     * used to automatically generate keys.
     * 
     * @param columnDefinition the column related to the select key statement
     * @param tableConfiguration table configuration for the current table
     * @param indentLevel the required indent level
     * @return a well formatted String containing the selectKey element
     */
    protected String getSelectKey(ColumnDefinition columnDefinition,
            TableConfiguration tableConfiguration, int indentLevel) {
        StringBuffer xml = new StringBuffer();

        //ResolvedJavaType rjt = columnDefinition.getResolvedJavaType();
        String identityColumnType = columnDefinition.getResolvedJavaType()
                .getFullyQualifiedJavaType().getFullyQualifiedName();

        indent(xml, indentLevel);
        xml.append("<selectKey"); //$NON-NLS-1$
        addAttribute(xml, "resultClass", identityColumnType); //$NON-NLS-1$
        addAttribute(xml, "keyProperty", columnDefinition.getJavaProperty()); //$NON-NLS-1$
        xml.append('>');
        newLine(xml);
        indent(xml, indentLevel + 1);
        xml.append(tableConfiguration.getGeneratedKey().getSqlStatement());
        newLine(xml);
        indent(xml, indentLevel);
        xml.append("</selectKey>"); //$NON-NLS-1$

        return xml.toString();
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#getSqlMapNamespace(org.apache.ibatis.abator.config.FullyQualifiedTable)
     */
    public String getSqlMapNamespace(FullyQualifiedTable table) {
        String key = "getSqlMapNamespace"; //$NON-NLS-1$
        String s;

        Map map = getTableStringMap(table);
        s = (String) map.get(key);
        if (s == null) {
            s = table.getFullyQualifiedTableNameWithUnderscores();
            map.put(key, s);
        }

        return s;
    }

    /**
     * Calculates the name of the result map. Typically this is the String
     * "abatorgenerated_XXXXResult" where XXXX is the name of the domain object
     * related to this table. The prefix "abatorgenerated_" is important because it
     * allows Abator to regenerate this element on subsequent runs.
     * 
     * @param table the current table
     * @return the name of the result map
     */
    protected String getResultMapName(FullyQualifiedTable table) {
        String key = "getResultMapName"; //$NON-NLS-1$
        String s;

        Map map = getTableStringMap(table);
        s = (String) map.get(key);
        if (s == null) {
            StringBuffer sb = new StringBuffer();

            sb.append("abatorgenerated_");
            sb.append(table.getDomainObjectName());
            sb.append("Result"); //$NON-NLS-1$

            s = sb.toString();
            map.put(key, s);
        }

        return s;
    }

    /**
     * Calculates a file name for the current table. Typically the name is
     * "XXXX_SqlMap.xml" where XXXX is the fully qualified table name (delimited
     * with underscores).
     * 
     * @param table the current table
     * @return tha name of the SqlMap file
     */
    protected String getSqlMapFileName(FullyQualifiedTable table) {
        String key = "getSqlMapFileName"; //$NON-NLS-1$
        String s;

        Map map = getTableStringMap(table);
        s = (String) map.get(key);
        if (s == null) {
            StringBuffer sb = new StringBuffer();
            sb.append(table.getFullyQualifiedTableNameWithUnderscores());

            sb.append("_SqlMap.xml"); //$NON-NLS-1$

            s = sb.toString();
            map.put(key, s);
        }

        return s;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#getDeleteByPrimaryKeyStatementId()
     */
    public String getDeleteByPrimaryKeyStatementId() {
        return "abatorgenerated_deleteByPrimaryKey"; //$NON-NLS-1$
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#getDeleteByExampleStatementId()
     */
    public String getDeleteByExampleStatementId() {
        return "abatorgenerated_deleteByExample"; //$NON-NLS-1$
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#getInsertStatementId()
     */
    public String getInsertStatementId() {
        return "abatorgenerated_insert"; //$NON-NLS-1$
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#getSelectByPrimaryKeyStatementId()
     */
    public String getSelectByPrimaryKeyStatementId() {
        return "abatorgenerated_selectByPrimaryKey"; //$NON-NLS-1$
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#getSelectByExampleStatementId()
     */
    public String getSelectByExampleStatementId() {
        return "abatorgenerated_selectByExample"; //$NON-NLS-1$
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#getSelectByExampleWithBLOBsStatementId()
     */
    public String getSelectByExampleWithBLOBsStatementId() {
        return "abatorgenerated_selectByExampleWithBLOBs"; //$NON-NLS-1$
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#getUpdateByPrimaryKeyWithBLOBsStatementId()
     */
    public String getUpdateByPrimaryKeyWithBLOBsStatementId() {
        return "abatorgenerated_updateByPrimaryKeyWithBLOBs"; //$NON-NLS-1$
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#getUpdateByPrimaryKeyStatementId()
     */
    public String getUpdateByPrimaryKeyStatementId() {
        return "abatorgenerated_updateByPrimaryKey"; //$NON-NLS-1$
    }

    /**
     * Calculates the package for the current table.
     * 
     * @param table the current table
     * @return the package for the SqlMap for the current table
     */
    protected String getSqlMapPackage(FullyQualifiedTable table) {
        String key = "getSqlMapPackage"; //$NON-NLS-1$
        String s;

        Map map = getTableStringMap(table);
        s = (String) map.get(key);
        if (s == null) {
            if ("true".equals(properties.get("enableSubPackages"))) { //$NON-NLS-1$ //$NON-NLS-2$
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

    /**
     * Calculates the name of the example where clause element
     * 
     * @return the name of the example where clause element
     */
    protected String getExampleWhereClauseId() {
        return "abatorgenerated_Example_Where_Clause";
    }

    /**
     * This method should return well formatted XML for the example where clause
     * SQL fragment (an sql fragment).
     * 
     * @param columnDefinitions introspected column definitions for the current table
     * @param tableConfiguration table configuration for the current table
     * @param indentLevel the required indent level
     * @return a well formatted String containing the SQL element
     */
    protected String getByExampleWhereClauseFragment(
            ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, int indentLevel) {
        StringBuffer xml = new StringBuffer();

        indent(xml, indentLevel);
        xml.append("<sql"); //$NON-NLS-1$
        addAttribute(xml, "id", getExampleWhereClauseId()); //$NON-NLS-1$ //$NON-NLS-2$
        xml.append('>');
        newLine(xml);

        xml.append(getElementComment(indentLevel + 1));
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("<dynamic"); //$NON-NLS-1$
        addAttribute(xml, "prepend", "where"); //$NON-NLS-1$ //$NON-NLS-2$
        xml.append('>');
        newLine(xml);

        Iterator iter = columnDefinitions.getAllColumns().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();

            if (cd.isBLOBColumn()) {
                // don't generate select by example clauses for BLOBs
                continue;
            }

            Iterator clauseIterator = ExampleClause.getAllExampleClauses();
            while (clauseIterator.hasNext()) {
                ExampleClause ec = (ExampleClause) clauseIterator.next();

                if (ec.isCharacterOnly() && !cd.isCharacterColumn()) {
                    continue;
                }

                indent(xml, indentLevel + 2);
                xml.append("<isPropertyAvailable"); //$NON-NLS-1$
                addAttribute(xml, "prepend", "and"); //$NON-NLS-1$ //$NON-NLS-2$
                addAttribute(xml, "property", ec.getSelectorAndProperty(cd)); //$NON-NLS-1$
                xml.append('>');
                newLine(xml);

                indent(xml, indentLevel + 3);
                xml.append(ec.getClause(cd));
                newLine(xml);

                indent(xml, indentLevel + 2);
                xml.append("</isPropertyAvailable>"); //$NON-NLS-1$
                newLine(xml);

                indent(xml, indentLevel + 2);
                xml.append("<isPropertyAvailable"); //$NON-NLS-1$
                addAttribute(xml, "prepend", "or"); //$NON-NLS-1$ //$NON-NLS-2$
                addAttribute(xml, "property", ec.getSelectorOrProperty(cd)); //$NON-NLS-1$
                xml.append('>');
                newLine(xml);

                indent(xml, indentLevel + 3);
                xml.append(ec.getClause(cd));
                newLine(xml);

                indent(xml, indentLevel + 2);
                xml.append("</isPropertyAvailable>"); //$NON-NLS-1$
                newLine(xml);
            }
        }

        indent(xml, indentLevel + 1);
        xml.append("</dynamic>"); //$NON-NLS-1$
        newLine(xml);

        indent(xml, indentLevel);
        xml.append("</sql>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * This method should return well formatted XML for the select by example
     * statement that returns all fields in the table (except BLOB fields).
     * 
     * @param columnDefinitions introspected column definitions for the current table
     * @param tableConfiguration table configuration for the current table
     * @param indentLevel the required indent level
     * @return a well formatted String containing the update element
     */
    protected String getSelectByExample(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, int indentLevel) {

        StringBuffer xml = new StringBuffer();

        indent(xml, indentLevel);
        xml.append("<select"); //$NON-NLS-1$
        addAttribute(xml, "id", getSelectByExampleStatementId()); //$NON-NLS-1$
        addAttribute(xml,
                "resultMap", getResultMapName(tableConfiguration.getTable())); //$NON-NLS-1$
        addAttribute(xml, "parameterClass", "java.util.Map"); //$NON-NLS-1$ //$NON-NLS-2$
        xml.append('>');
        newLine(xml);

        xml.append(getElementComment(indentLevel + 1));
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("select "); //$NON-NLS-1$

        boolean comma = false;

        if (StringUtility.stringHasValue(tableConfiguration
                .getSelectByExampleQueryId())) {
            xml.append('\'');
            xml.append(tableConfiguration.getSelectByExampleQueryId());
            xml.append("' as QUERYID"); //$NON-NLS-1$
            comma = true;
        }

        Iterator iter = columnDefinitions.getAllColumns().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();

            if (cd.isBLOBColumn()) {
                // don't return BLOBs in select be examples
                continue;
            }

            if (comma) {
                xml.append(',');
                newLine(xml);
                indent(xml, indentLevel + 2);
            } else {
                comma = true;
            }

            xml.append(cd.getColumnName());
        }

        newLine(xml);
        indent(xml, indentLevel + 1);
        xml.append("from "); //$NON-NLS-1$
        xml.append(tableConfiguration.getTable().getFullyQualifiedTableName());
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("<include"); //$NON-NLS-1$
        addAttribute(xml, "refid", //$NON-NLS-1$
                getSqlMapNamespace(tableConfiguration.getTable())
                        + "." + getExampleWhereClauseId()); //$NON-NLS-1$
        xml.append("/>"); //$NON-NLS-1$
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("<isPropertyAvailable"); //$NON-NLS-1$
        addAttribute(xml, "property", "ABATOR_ORDER_BY_CLAUSE"); //$NON-NLS-1$ //$NON-NLS-2$
        xml.append('>');
        newLine(xml);

        indent(xml, indentLevel + 2);
        xml.append("order by $ABATOR_ORDER_BY_CLAUSE$"); //$NON-NLS-1$
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("</isPropertyAvailable>"); //$NON-NLS-1$
        newLine(xml);

        indent(xml, indentLevel);
        xml.append("</select>"); //$NON-NLS-1$

        return xml.toString();
    }

    /**
     * This method should return well formatted XML for the select by example
     * statement that returns all fields in the table (including BLOB fields).
     * 
     * @param columnDefinitions introspected column definitions for the current table
     * @param tableConfiguration table configuration for the current table
     * @param indentLevel the required indent level
     * @return a well formatted String containing the update element
     */
    protected String getSelectByExampleWithBLOBs(
            ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, int indentLevel) {

        StringBuffer xml = new StringBuffer();

        indent(xml, indentLevel);
        xml.append("<select"); //$NON-NLS-1$
        addAttribute(xml, "id", getSelectByExampleWithBLOBsStatementId()); //$NON-NLS-1$
        addAttribute(
                xml,
                "resultMap", getResultMapName(tableConfiguration.getTable()) + "WithBLOBs"); //$NON-NLS-1$
        addAttribute(xml, "parameterClass", "java.util.Map"); //$NON-NLS-1$ //$NON-NLS-2$
        xml.append('>');
        newLine(xml);

        xml.append(getElementComment(indentLevel + 1));
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("select "); //$NON-NLS-1$

        boolean comma = false;

        if (StringUtility.stringHasValue(tableConfiguration
                .getSelectByExampleQueryId())) {
            xml.append('\'');
            xml.append(tableConfiguration.getSelectByExampleQueryId());
            xml.append("' as QUERYID"); //$NON-NLS-1$
            comma = true;
        }

        Iterator iter = columnDefinitions.getAllColumns().iterator();
        while (iter.hasNext()) {
            ColumnDefinition cd = (ColumnDefinition) iter.next();

            if (comma) {
                xml.append(',');
                newLine(xml);
                indent(xml, indentLevel + 2);
            } else {
                comma = true;
            }

            xml.append(cd.getColumnName());
        }

        newLine(xml);
        indent(xml, indentLevel + 1);
        xml.append("from "); //$NON-NLS-1$
        xml.append(tableConfiguration.getTable().getFullyQualifiedTableName());
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("<include"); //$NON-NLS-1$
        addAttribute(
                xml,
                "refid", getSqlMapNamespace(tableConfiguration.getTable()) + "." + getExampleWhereClauseId()); //$NON-NLS-1$ //$NON-NLS-2$
        xml.append("/>"); //$NON-NLS-1$
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("<isPropertyAvailable"); //$NON-NLS-1$
        addAttribute(xml, "property", "ABATOR_ORDER_BY_CLAUSE"); //$NON-NLS-1$ //$NON-NLS-2$
        xml.append('>');
        newLine(xml);

        indent(xml, indentLevel + 2);
        xml.append("order by $ABATOR_ORDER_BY_CLAUSE$"); //$NON-NLS-1$
        newLine(xml);

        indent(xml, indentLevel + 1);
        xml.append("</isPropertyAvailable>"); //$NON-NLS-1$
        newLine(xml);

        indent(xml, indentLevel);
        xml.append("</select>"); //$NON-NLS-1$

        return xml.toString();
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.SqlMapGenerator#setTargetProject(java.lang.String)
     */
    public void setTargetProject(String targetProject) {
        this.targetProject = targetProject;
    }
}
