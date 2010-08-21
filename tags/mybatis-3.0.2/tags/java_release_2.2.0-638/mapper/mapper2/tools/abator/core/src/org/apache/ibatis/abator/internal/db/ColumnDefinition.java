/*
 * Copyright 2005 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.ibatis.abator.internal.db;

import java.sql.Types;

import org.apache.ibatis.abator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.abator.internal.types.ResolvedJavaType;
import org.apache.ibatis.abator.internal.util.StringUtility;

/**
 * 
 * @author Jeff Butler
 */
public class ColumnDefinition {
    private String columnName;

    private int jdbcType;

    private boolean nullable;

    private int length;

    private int scale;

    private String typeName;

    private boolean identity;

    private String javaProperty;

    private ResolvedJavaType resolvedJavaType;
    
    private String tableAlias;
    
    private String typeHandler;

    /**
     * The aliased column name for a select statement.  If there
     * is a table alias, the value will be alias.columnName
     */
    private String aliasedColumnName;

    /**
     * The renamed column name for a select statement.  If there
     * is a table alias, the value will be alias_columnName
     */
    private String renamedColumnName;
    
    /**
     * The phrase to use in a select list.  If there
     * is a table alias, the value will be 
     * "alias.columnName as alias_columnName"
     */
    private String selectListPhrase;
    
    /**
     * Constructs a Column definition.  This object holds all the 
     * information about a column that is required to generate
     * Java objects and SQL maps;
     * 
     * @param tableAlias The specified table alias, or null.  This
     *   value is used to rename and alias column names for select statements
     */
    public ColumnDefinition(String tableAlias) {
        super();
        this.tableAlias = tableAlias;
    }

    public int getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

	/*
	 * This method is primarily used for debugging, so we don't externalize the strings
	 */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("Column Name: "); //$NON-NLS-1$
        sb.append(columnName);
        sb.append(", JDBC Type: "); //$NON-NLS-1$
        sb.append(jdbcType);
        sb.append(", Type Name: "); //$NON-NLS-1$
        sb.append(typeName);
        sb.append(", Nullable: "); //$NON-NLS-1$
        sb.append(nullable);
        sb.append(", Length: "); //$NON-NLS-1$
        sb.append(length);
        sb.append(", Scale: "); //$NON-NLS-1$
        sb.append(scale);
        sb.append(", Identity: "); //$NON-NLS-1$
        sb.append(identity);

        return sb.toString();
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
        
        if (StringUtility.stringHasValue(tableAlias)) {
            StringBuffer sb = new StringBuffer();
            
            sb.append(tableAlias);
            sb.append('.');
            sb.append(columnName);
            aliasedColumnName = sb.toString();
            
            sb.setLength(0);
            sb.append(tableAlias);
            sb.append('_');
            sb.append(columnName);
            renamedColumnName = sb.toString();
            
            sb.setLength(0);
            sb.append(tableAlias);
            sb.append('.');
            sb.append(columnName);
            sb.append(" as "); //$NON-NLS-1$
            sb.append(tableAlias);
            sb.append('_');
            sb.append(columnName);
            selectListPhrase = sb.toString();
        } else {
            aliasedColumnName = columnName;
            renamedColumnName = columnName;
            selectListPhrase = columnName;
        }
    }

    /**
     * @return Returns the identity.
     */
    public boolean isIdentity() {
        return identity;
    }

    /**
     * @param identity
     *            The identity to set.
     */
    public void setIdentity(boolean identity) {
        this.identity = identity;
    }

    public boolean isBLOBColumn() {
        String typeName = resolvedJavaType.getJdbcTypeName();

        return "BLOB".equals(typeName) || "LONGVARBINARY".equals(typeName) //$NON-NLS-1$ //$NON-NLS-2$
                || "LONGVARCHAR".equals(typeName) || "CLOB".equals(typeName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public boolean isStringColumn() {
        return resolvedJavaType.getFullyQualifiedJavaType() == 
            FullyQualifiedJavaType.getStringInstance();
    }
    
    public boolean isJdbcCharacterColumn() {
        return jdbcType == Types.CHAR
            || jdbcType == Types.CLOB
            || jdbcType == Types.LONGVARCHAR
            || jdbcType == Types.VARCHAR;
    }

    public String getJavaProperty() {
        return javaProperty;
    }

    public void setJavaProperty(String javaProperty) {
        this.javaProperty = javaProperty;
    }

    public ResolvedJavaType getResolvedJavaType() {
        return resolvedJavaType;
    }

    public void setResolvedJavaType(ResolvedJavaType resolvedJavaType) {
        this.resolvedJavaType = resolvedJavaType;
    }
    
    public String getByExampleIndicatorProperty() {
        return javaProperty + "_Indicator"; //$NON-NLS-1$
    }
    
    public String getRenamedColumnName() {
        return renamedColumnName;
    }

    public String getAliasedColumnName() {
        return aliasedColumnName;
    }
    
    public String getSelectListPhrase() {
        return selectListPhrase;
    }
    
    public boolean isJDBCDateColumn() {
        return resolvedJavaType.isJDBCDate()
            && !StringUtility.stringHasValue(typeHandler);
    }
    
    public boolean isJDBCTimeColumn() {
        return resolvedJavaType.isJDBCTime()
            && !StringUtility.stringHasValue(typeHandler);
    }
    
    public String getIbatisFormattedParameterClause() {
        StringBuffer sb = new StringBuffer();
        
        sb.append('#');
        sb.append(getJavaProperty());
        
        if (StringUtility.stringHasValue(typeHandler)) {
            sb.append(",jdbcType="); //$NON-NLS-1$
            sb.append(getResolvedJavaType().getJdbcTypeName());
            sb.append(",handler="); //$NON-NLS-1$
            sb.append(typeHandler);
        } else {
            sb.append(':');
            sb.append(getResolvedJavaType().getJdbcTypeName());
        }
        
        sb.append('#');
        
        return sb.toString();
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }
}
