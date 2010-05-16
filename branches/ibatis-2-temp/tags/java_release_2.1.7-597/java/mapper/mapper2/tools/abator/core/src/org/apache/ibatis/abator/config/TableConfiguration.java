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
package org.apache.ibatis.abator.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.abator.internal.db.ColumnDefinitions;
import org.apache.ibatis.abator.internal.util.EqualsUtil;
import org.apache.ibatis.abator.internal.util.HashCodeUtil;

/**
 * 
 * @author Jeff Butler
 */
public class TableConfiguration extends PropertyHolder {
	private FullyQualifiedTable table;

	private boolean insertStatementEnabled;

	private boolean selectByPrimaryKeyStatementEnabled;

	private boolean selectByExampleStatementEnabled;

	private boolean updateByPrimaryKeyStatementEnabled;

	private boolean deleteByPrimaryKeyStatementEnabled;

	private boolean deleteByExampleStatementEnabled;

	private Map columnOverrides;

	private Map ignoredColumns;

	private GeneratedKey generatedKey;

	private String selectByPrimaryKeyQueryId;

	private String selectByExampleQueryId;

	public TableConfiguration() {
		super();
		
		columnOverrides = new HashMap();
		ignoredColumns = new HashMap();
		table = new FullyQualifiedTable();
		generatedKey = new GeneratedKey();

		insertStatementEnabled = true;
		selectByPrimaryKeyStatementEnabled = true;
		selectByExampleStatementEnabled = true;
		updateByPrimaryKeyStatementEnabled = true;
		deleteByPrimaryKeyStatementEnabled = true;
		deleteByExampleStatementEnabled = true;
	}

	public boolean isDeleteByPrimaryKeyStatementEnabled() {
		return deleteByPrimaryKeyStatementEnabled;
	}

	public void setDeleteByPrimaryKeyStatementEnabled(
			boolean deleteByPrimaryKeyStatementEnabled) {
		this.deleteByPrimaryKeyStatementEnabled = deleteByPrimaryKeyStatementEnabled;
	}

	public boolean isInsertStatementEnabled() {
		return insertStatementEnabled;
	}

	public void setInsertStatementEnabled(boolean insertStatementEnabled) {
		this.insertStatementEnabled = insertStatementEnabled;
	}

	public boolean isSelectByPrimaryKeyStatementEnabled() {
		return selectByPrimaryKeyStatementEnabled;
	}

	public void setSelectByPrimaryKeyStatementEnabled(
			boolean selectByPrimaryKeyStatementEnabled) {
		this.selectByPrimaryKeyStatementEnabled = selectByPrimaryKeyStatementEnabled;
	}

	public boolean isUpdateByPrimaryKeyStatementEnabled() {
		return updateByPrimaryKeyStatementEnabled;
	}

	public void setUpdateByPrimaryKeyStatementEnabled(
			boolean updateByPrimaryKeyStatementEnabled) {
		this.updateByPrimaryKeyStatementEnabled = updateByPrimaryKeyStatementEnabled;
	}

	public boolean isColumnIgnored(String column) {
	    String key = column.toUpperCase();
	    boolean rc = false;
	    
	    if (ignoredColumns.containsKey(key)) {
	        // column has been accessed, it must exist in the table
	        rc = true;
	        ignoredColumns.put(key, new Boolean(true));
	    }
	    
		return rc;
	}

	public void addIgnoredColumn(String column) {
	    // put a false in the map to designate that the column has not
	    // been accessed yet.  We use this to report warnings if the user
	    // specifies an ignored column that does not exist in the table.
		ignoredColumns.put(column.toUpperCase(), new Boolean(false));
	}

	public void addColumnOverride(ColumnOverride columnOverride) {
		columnOverrides.put(columnOverride.getColumnName().toUpperCase(),
				columnOverride);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof TableConfiguration)) {
			return false;
		}

		TableConfiguration other = (TableConfiguration) obj;

		return EqualsUtil.areEqual(this.table, other.table);
	}

	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, table);

		return result;
	}

	public boolean isSelectByExampleStatementEnabled() {
		return selectByExampleStatementEnabled;
	}

	public void setSelectByExampleStatementEnabled(
			boolean selectByExampleStatementEnabled) {
		this.selectByExampleStatementEnabled = selectByExampleStatementEnabled;
	}

	public FullyQualifiedTable getTable() {
		return table;
	}

	public void setTable(FullyQualifiedTable tableName) {
		this.table = tableName;
	}

	/**
	 * May return null if the column has not been overridden
	 * 
	 * @param columnName
	 * @return the column override (if any) related to this column
	 */
	public ColumnOverride getColumnOverride(String columnName) {
		return (ColumnOverride) columnOverrides.get(columnName.toUpperCase());
	}

	public GeneratedKey getGeneratedKey() {
		return generatedKey;
	}

	public String getSelectByExampleQueryId() {
		return selectByExampleQueryId;
	}

	public void setSelectByExampleQueryId(String selectByExampleQueryId) {
		this.selectByExampleQueryId = selectByExampleQueryId;
	}

	public String getSelectByPrimaryKeyQueryId() {
		return selectByPrimaryKeyQueryId;
	}

	public void setSelectByPrimaryKeyQueryId(String selectByPrimaryKeyQueryId) {
		this.selectByPrimaryKeyQueryId = selectByPrimaryKeyQueryId;
	}

	public boolean isDeleteByExampleStatementEnabled() {
		return deleteByExampleStatementEnabled;
	}

	public void setDeleteByExampleStatementEnabled(
			boolean deleteByExampleStatementEnabled) {
		this.deleteByExampleStatementEnabled = deleteByExampleStatementEnabled;
	}
	
	public void reportWarnings(ColumnDefinitions columnDefinitions, List warnings) {
	    Iterator iter = columnOverrides.values().iterator();
	    while (iter.hasNext()) {
	        ColumnOverride columnOverride = (ColumnOverride) iter.next();
	        if (columnDefinitions.getColumn(columnOverride.getColumnName().toUpperCase()) == null) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("Specified column override \"");
	            sb.append(columnOverride.getColumnName());
	            sb.append("\" in table ");
	            sb.append(table.toString());
	            sb.append(" does not exist in the table.");
	            warnings.add(sb.toString());
	        }
	    }
	    
	    iter = ignoredColumns.entrySet().iterator();
	    while (iter.hasNext()) {
	        Map.Entry entry = (Map.Entry) iter.next();
	        
	        Boolean value = (Boolean) entry.getValue();
	        
	        if (!value.booleanValue()) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("Specified ignored column \"");
	            sb.append(entry.getKey());
	            sb.append("\" in table ");
	            sb.append(table.toString());
	            sb.append(" does not exist in the table.");
	            warnings.add(sb.toString());
	        }
	    }
	    
	    if (generatedKey.isConfigured()
	            && columnDefinitions.getColumn(generatedKey.getColumn().toUpperCase()) == null) {
            StringBuffer sb = new StringBuffer();
            if (generatedKey.isIdentity()) {
                sb.append("Specified identity column \"");
            } else {
                sb.append("Specified sequenced column \"");
            }
            sb.append(generatedKey.getColumn());
            sb.append("\" in table ");
            sb.append(table.toString());
            sb.append(" does not exist in the table.");
            warnings.add(sb.toString());
	    }
	}
	
	public boolean areAnyStatementsEnabled() {
	    return selectByExampleStatementEnabled
	    	|| selectByPrimaryKeyStatementEnabled
	    	|| insertStatementEnabled
	    	|| updateByPrimaryKeyStatementEnabled
	    	|| deleteByExampleStatementEnabled
	    	|| deleteByPrimaryKeyStatementEnabled;
	}
}
