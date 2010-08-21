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

import org.apache.ibatis.abator.internal.types.ResolvedJavaType;

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

    public ColumnDefinition() {
        super();
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

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("Column Name: ");
        sb.append(columnName);
        sb.append(", JDBC Type: ");
        sb.append(jdbcType);
        sb.append(", Type Name: ");
        sb.append(typeName);
        sb.append(", Nullable: ");
        sb.append(nullable);
        sb.append(", Length: ");
        sb.append(length);
        sb.append(", Scale: ");
        sb.append(scale);
        sb.append(", Identity: ");
        sb.append(identity);

        return sb.toString();
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
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

    public boolean isCharacterColumn() {
        return "java.lang.String".equals(resolvedJavaType //$NON-NLS-1$
                .getFullyQualifiedJavaType().getFullyQualifiedName());
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
}
