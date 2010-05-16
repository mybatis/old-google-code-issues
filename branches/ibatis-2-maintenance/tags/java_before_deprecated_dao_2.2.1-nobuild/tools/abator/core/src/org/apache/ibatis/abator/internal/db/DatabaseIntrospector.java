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
package org.apache.ibatis.abator.internal.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.ibatis.abator.api.FullyQualifiedTable;
import org.apache.ibatis.abator.api.JavaTypeResolver;
import org.apache.ibatis.abator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.abator.config.ColumnOverride;
import org.apache.ibatis.abator.config.GeneratedKey;
import org.apache.ibatis.abator.config.TableConfiguration;
import org.apache.ibatis.abator.exception.UnsupportedDataTypeException;
import org.apache.ibatis.abator.internal.util.JavaBeansUtil;
import org.apache.ibatis.abator.internal.util.StringUtility;
import org.apache.ibatis.abator.internal.util.messages.Messages;

/**
 * 
 * @author Jeff Butler
 */
public class DatabaseIntrospector {

    private DatabaseIntrospector() {
        super();
    }

    public static Collection introspectTables(Connection connection,
            TableConfiguration tc, JavaTypeResolver javaTypeResolver,
            List warnings) throws SQLException {

        Map introspectedTables = new HashMap();
        DatabaseMetaData dbmd = connection.getMetaData();
        
        String localCatalog;
        String localSchema;
        String localTableName;

        if (dbmd.storesLowerCaseIdentifiers()) {
            localCatalog = tc.getCatalog() == null ? null : tc.getCatalog()
                    .toLowerCase();
            localSchema = tc.getSchema() == null ? null : tc.getSchema()
                    .toLowerCase();
            localTableName = tc.getTableName() == null ? null : tc
                    .getTableName().toLowerCase();
        } else if (dbmd.storesUpperCaseIdentifiers()) {
            localCatalog = tc.getCatalog() == null ? null : tc.getCatalog()
                    .toUpperCase();
            localSchema = tc.getSchema() == null ? null : tc.getSchema()
                    .toUpperCase();
            localTableName = tc.getTableName() == null ? null : tc
                    .getTableName().toUpperCase();
        } else {
            localCatalog = tc.getCatalog();
            localSchema = tc.getSchema();
            localTableName = tc.getTableName();
        }

        if (tc.isWildcardEscapingEnabled()) {
            String escapeString = dbmd.getSearchStringEscape();
            
            StringBuffer sb = new StringBuffer();
            StringTokenizer st;
            if (localSchema != null) {
                st = new StringTokenizer(localSchema, "_%", true); //$NON-NLS-1$
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (token.equals("_") //$NON-NLS-1$
                            || token.equals("%")) { //$NON-NLS-1$
                        sb.append(escapeString);
                    }
                    sb.append(token);
                }
                localSchema = sb.toString();
            }
            
            sb.setLength(0);
            st = new StringTokenizer(localTableName, "_%", true); //$NON-NLS-1$
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.equals("_") //$NON-NLS-1$
                        || token.equals("%")) { //$NON-NLS-1$
                    sb.append(escapeString);
                }
                sb.append(token);
            }
            localTableName = sb.toString();
        }

        ResultSet rs = dbmd.getColumns(localCatalog, localSchema,
                localTableName, null);

        int returnedColumns = 0;
        while (rs.next()) {
            returnedColumns++;
            
            ColumnDefinition cd = new ColumnDefinition(tc.getAlias());

            cd.setJdbcType(rs.getInt("DATA_TYPE")); //$NON-NLS-1$
            cd.setLength(rs.getInt("COLUMN_SIZE")); //$NON-NLS-1$
            cd.setActualColumnName(rs.getString("COLUMN_NAME")); //$NON-NLS-1$
            cd
                    .setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable); //$NON-NLS-1$
            cd.setScale(rs.getInt("DECIMAL_DIGITS")); //$NON-NLS-1$
            cd.setTypeName(rs.getString("TYPE_NAME")); //$NON-NLS-1$
            
            String tableName = rs.getString("TABLE_NAME"); //$NON-NLS-1$
            String catalog = rs.getString("TABLE_CAT"); //$NON-NLS-1$
            String schema = rs.getString("TABLE_SCHEM"); //$NON-NLS-1$

            // we only use the returned catalog and schema if something was actually
            // specified on the table configuration.  If something was returned
            // from the DB for these fields, but nothing was specified on the table
            // configuration, then some sort of DB default is being returned
            // and we don't want that in our SQL
            FullyQualifiedTable table = new FullyQualifiedTable(
                    StringUtility.stringHasValue(tc.getCatalog()) ? catalog : null,
                    StringUtility.stringHasValue(tc.getSchema()) ? schema : null,
                    tableName, tc.getDomainObjectName(), tc.getAlias(),
                    "true".equalsIgnoreCase((String) tc.getProperties().get("ignoreQualifiersAtRuntime")), //$NON-NLS-1$ //$NON-NLS-2$
                    (String) tc.getProperties().get("runtimeTableName"));//$NON-NLS-1$
            
            ColumnOverride columnOverride = tc.getColumnOverride(cd
                    .getActualColumnName());

            if (columnOverride == null
                    || !StringUtility.stringHasValue(columnOverride
                            .getJavaProperty())) {
                if ("true".equalsIgnoreCase((String) tc.getProperties().get("useActualColumnNames"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    cd.setJavaProperty(JavaBeansUtil.getValidPropertyName(cd
                            .getActualColumnName()));
                } else {
                    cd.setJavaProperty(JavaBeansUtil.getCamelCaseString(cd
                            .getActualColumnName(), false));
                }
            } else {
                cd.setJavaProperty(columnOverride.getJavaProperty());
            }

            try {
                javaTypeResolver.initializeResolvedJavaType(cd);
            } catch (UnsupportedDataTypeException e) {
                // if the type is not supported, then we'll report a warning and
                // ignore the column
                warnings.add(Messages.getString("Warning.14", //$NON-NLS-1$
                        table.getFullyQualifiedTableNameAsConfigured(),
                        cd.getActualColumnName()));
                continue;
            }

            if (columnOverride != null
                    && StringUtility.stringHasValue(columnOverride
                            .getJavaType())) {
                cd.getResolvedJavaType()
                        .setFullyQualifiedJavaType(
                                new FullyQualifiedJavaType(columnOverride
                                        .getJavaType()));
            }

            if (columnOverride != null
                    && StringUtility.stringHasValue(columnOverride
                            .getJdbcType())) {
                cd.getResolvedJavaType().setJdbcTypeName(
                        columnOverride.getJdbcType());
            }

            if (columnOverride != null
                    && StringUtility.stringHasValue(columnOverride
                            .getTypeHandler())) {
                cd.setTypeHandler(columnOverride.getTypeHandler());
            }

            if (tc.getGeneratedKey() != null
                    && tc.getGeneratedKey().isIdentity()
                    && cd.getActualColumnName().equalsIgnoreCase(
                            tc.getGeneratedKey().getColumn())) {
                cd.setIdentity(true);
            } else {
                cd.setIdentity(false);
            }

            if (!tc.isColumnIgnored(cd.getActualColumnName())) {
                IntrospectedTableImpl introspectedTable =
                    (IntrospectedTableImpl) introspectedTables.get(table.getFullyQualifiedTableNameAsConfigured());
                if (introspectedTable == null) {
                    introspectedTable = new IntrospectedTableImpl(tc, new ColumnDefinitions(), table);
                    introspectedTables.put(table.getFullyQualifiedTableNameAsConfigured(), introspectedTable);
                }
                
                introspectedTable.getColumnDefinitions().addColumn(cd);
            }
        }

        rs.close();
        
        if (returnedColumns == 0) {
            warnings.add(Messages.getString("Warning.19", tc.getCatalog(), //$NON-NLS-1$
                    tc.getSchema(), tc.getTableName()));
        }
        
        Iterator iter = introspectedTables.values().iterator();
        while (iter.hasNext()) {
            IntrospectedTableImpl it = (IntrospectedTableImpl) iter.next();
            calculatePrimaryKey(dbmd, it, warnings);
        }
        
        // now introspectedTables has all the columns from all the 
        // tables in the configuration.  Do some validation...

        iter = introspectedTables.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            
            IntrospectedTableImpl introspectedTable = 
                (IntrospectedTableImpl) entry.getValue();
            
            ColumnDefinitions cds = introspectedTable.getColumnDefinitions();
            
            if (!cds.hasAnyColumns()) {
                // add warning that the table has no columns, remove from the list
                warnings.add(Messages.getString("Warning.1", introspectedTable.getTable().getFullyQualifiedTableNameAsConfigured())); //$NON-NLS-1$
                iter.remove();
            } else if (!cds.hasPrimaryKeyColumns()
                    && !cds.hasBaseColumns()) {
                // add warning that the table has only BLOB columns, remove from the list
                warnings.add(Messages.getString("Warning.18", introspectedTable.getTable().getFullyQualifiedTableNameAsConfigured())); //$NON-NLS-1$
                iter.remove();
            } else {
                // now make sure that all columns called out in the configuration
                // actually exist
                reportIntrospectionWarnings(cds, tc, introspectedTable.getTable(), warnings);
            }
        }

        return introspectedTables.values();
    }

    private static void calculatePrimaryKey(DatabaseMetaData dbmd,
            IntrospectedTableImpl introspectedTable, List warnings) {
        ResultSet rs = null;

        try {
            rs = dbmd.getPrimaryKeys(introspectedTable.getTable().getCatalog(),
                    introspectedTable.getTable().getSchema(),
                    introspectedTable.getTable().getTableName());
        } catch (SQLException e) {
            closeResultSet(rs);
            warnings.add(Messages.getString("Warning.15")); //$NON-NLS-1$
            return;
        }

        try {
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME"); //$NON-NLS-1$
                
                introspectedTable.getColumnDefinitions().addPrimaryKeyColumn(columnName);
            }
        } catch (SQLException e) {
            // ignore the primary key if there's any error
        } finally {
            closeResultSet(rs);
        }
    }

    private static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // ignore
                ;
            }
        }
    }

    private static void reportIntrospectionWarnings(
            ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, 
            FullyQualifiedTable table, List warnings) {
        // make sure that every column listed in column overrides
        // actually exists in the table
        Iterator iter = tableConfiguration.getColumnOverrides();
        while (iter.hasNext()) {
            ColumnOverride columnOverride = (ColumnOverride) iter.next();
            if (columnDefinitions.getColumn(columnOverride.getColumnName()) == null) {
                warnings.add(Messages.getString("Warning.3", //$NON-NLS-1$
                        columnOverride.getColumnName(), table.toString()));
            }
        }

        // make sure that every column listed in ignored columns
        // actually exists in the table
        iter = tableConfiguration.getIgnoredColumnsInError();
        while (iter.hasNext()) {
            String ignoredColumn = (String) iter.next();

            warnings.add(Messages.getString("Warning.4", //$NON-NLS-1$
                    ignoredColumn, table.toString()));
        }

        GeneratedKey generatedKey = tableConfiguration.getGeneratedKey();
        if (generatedKey != null
                && columnDefinitions.getColumn(generatedKey.getColumn()
                        .toUpperCase()) == null) {
            if (generatedKey.isIdentity()) {
                warnings.add(Messages.getString("Warning.5", //$NON-NLS-1$
                        generatedKey.getColumn(), table.toString()));
            } else {
                warnings.add(Messages.getString("Warning.6", //$NON-NLS-1$
                        generatedKey.getColumn(), table.toString()));
            }
        }
    }
}
