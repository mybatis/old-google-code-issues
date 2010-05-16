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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.ibatis.abator.config.TableConfiguration;

/**
 * This class holds the results of introspecting the database table.
 * Based on the type of table, the class calculates the resulting
 * objects and methods that should be generated.
 * 
 * @author Jeff Butler
 */
public class ColumnDefinitions {
	private static final int TABLE_TYPE_0_UNKNOWN = 0;

	/**
	 * A table that has no primary key and all columns are not BLOBs.
	 * Generate these objects/methods:
	 * <ul>
	 *   <li>Base Result Map (if either select method enabled)</li>
	 *   <li>Select By Example Method (if enabled)</li>
	 *   <li>Insert Method (if enabled)</li>
	 *   <li>Delete By Example Method (if enabled)</li>
	 *   <li>Record with No Superclass</li>
	 *   <li>Example class extending Record (if either "by example" method enabled)</li>
	 * </ul>
	 */
	private static final int TABLE_TYPE_1_NONBLOBS = 1;
	
	/**
	 * A table that has no primary key and columns that are both BLOBs and non BLOBs.
	 * Generate these objects/methods:
	 * <ul>
	 *   <li>Base Result Map (if either select method enabled)</li>
	 *   <li>Result Map with BLOBs (if either select method enabled)</li>
	 *   <li>Select By Example Method (if enabled)</li>
	 *   <li>Select By Example with BLOBs Method (if enabled)</li>
	 *   <li>Insert Method (if enabled)</li>
	 *   <li>Delete By Example Method (if enabled)</li>
	 *   <li>Record with No Superclass</li>
	 *   <li>Record with BLOBS extending Record</li>
	 *   <li>Example class extending Record (if either "by example" method enabled)</li>
	 * </ul>
	 */
	private static final int TABLE_TYPE_2_BLOBS_NONBLOBS = 2;
	
	/**
	 * A table with a primary key only.
	 * Generate these objects/methods:
	 * <ul>
	 *   <li>Base Result Map (if either select method enabled)</li>
	 *   <li>Select By Example Method (if enabled)</li>
	 *   <li>Insert Method (if enabled)</li>
	 *   <li>Delete By Primary Key Method (if enabled)</li>
	 *   <li>Delete By Example Method (if enabled)</li>
	 *   <li>Primary Key</li>
	 *   <li>Example class extending Primary Key (if either "by example" method enabled)</li>
	 * </ul>
	 */
	private static final int TABLE_TYPE_3_PK = 3;
	
	/**
	 * A table with a primary key, and all other columns are not BLOBs.
	 * Generate these objects/methods:
	 * <ul>
	 *   <li>Base Result Map (if either select method enabled)</li>
	 *   <li>Select By Example Method (if enabled)</li>
	 *   <li>Select By Primary Key Method (if enabled)</li>
	 *   <li>Insert Method (if enabled)</li>
	 *   <li>Update By Primary Key (No BLOBs) Method (if enabled)</li>
	 *   <li>Delete By Primary Key Method (if enabled)</li>
	 *   <li>Delete By Example Method (if enabled)</li>
	 *   <li>Primary Key</li>
	 *   <li>Record class extending Primary Key</li>
	 *   <li>Example class extending Record class (if either "by example" method enabled)</li>
	 * </ul>
	 */
	private static final int TABLE_TYPE_4_PK_NONBLOBS = 4;
	
	/**
	 * A table with a primary key, and all other columns are BLOBs.
	 * Generate these objects/methods:
	 * <ul>
	 *   <li>Base Result Map (if either select method enabled)</li>
	 *   <li>Result Map with BLOBs (if either select method enabled)</li>
	 *   <li>Select By Example Method (if enabled)</li>
	 *   <li>Select By Example with BLOBs Method (if enabled)</li>
	 *   <li>Select By Primary Key Method (if enabled)</li>
	 *   <li>Insert Method (if enabled)</li>
	 *   <li>Update By Primary Key (BLOBs) Method (if enabled)</li>
	 *   <li>Delete By Primary Key Method (if enabled)</li>
	 *   <li>Delete By Example Method (if enabled)</li>
	 *   <li>Primary Key</li>
	 *   <li>Record with BLOBs class extending Primary Key</li>
	 *   <li>Example class extending primary key class (if either "by example" method enabled)</li>
	 * </ul>
	 */
	private static final int TABLE_TYPE_5_PK_BLOBS = 5;
	
	/**
	 * A table with a primary key and other columns that are both BLOBs and
	 * non BLOBs.
	 * Generate these objects/methods:
	 * <ul>
	 *   <li>Base Result Map (if either select method enabled)</li>
	 *   <li>Result Map with BLOBs (if either select method enabled)</li>
	 *   <li>Select By Example Method (if enabled)</li>
	 *   <li>Select By Example with BLOBs Method (if enabled)</li>
	 *   <li>Select By Primary Key Method (if enabled)</li>
	 *   <li>Insert Method (if enabled)</li>
	 *   <li>Update By Primary Key (BLOBs) Method (if enabled)</li>
	 *   <li>Update By Primary Key (no BLOBs) Method (if enabled)</li>
	 *   <li>Delete By Primary Key Method (if enabled)</li>
	 *   <li>Delete By Example Method (if enabled)</li>
	 *   <li>Primary Key</li>
	 *   <li>Record class extending Primary Key</li>
	 *   <li>Record with BLOBs class extending record class</li>
	 *   <li>Example class extending record class (if either "by example" method enabled)</li>
	 * </ul>
	 */
	private static final int TABLE_TYPE_6_PK_BLOBS_NONBLOBS = 6;
	
	private String fullyQualifiedTableName;
	private LinkedHashMap columns;
	private LinkedHashMap primaryKey;
	private int tableType;

	// used by the getAllColumns convenience method only
	private ArrayList allColumns;

	public ColumnDefinitions(String fullyQualifiedTableName) {
		super();
		columns = new LinkedHashMap();
		primaryKey = new LinkedHashMap();
		this.fullyQualifiedTableName = fullyQualifiedTableName;
	}

	public Collection getBLOBColumns() {
		Collection answer = new ArrayList();
		Iterator iter = columns.values().iterator();
		while (iter.hasNext()) {
			ColumnDefinition cd = (ColumnDefinition) iter.next();
			if (cd.isBLOBColumn()) {
				answer.add(cd);
			}
		}
		
		return answer;
	}

	public Collection getNonBLOBColumns() {
		Collection answer = new ArrayList();
		Iterator iter = columns.values().iterator();
		while (iter.hasNext()) {
			ColumnDefinition cd = (ColumnDefinition) iter.next();
			if (!cd.isBLOBColumn()) {
				answer.add(cd);
			}
		}
		
		return answer;
	}
	
	public Collection getNonPrimaryKeyColumns() {
		return columns.values();
	}

	public Collection getPrimaryKey() {
		return primaryKey.values();
	}

	public void addColumn(ColumnDefinition cd) {
		columns.put(cd.getColumnName().toUpperCase(), cd);

		allColumns = null;
	}

	public void addPrimaryKeyColumn(String columnName) {
		String key = columnName.toUpperCase();
		if (columns.containsKey(key)) {
			primaryKey.put(key, columns.remove(key));

			allColumns = null;
		}
	}

	public String toString() {
	    String newLine = System.getProperty("line.separator");
	    if (newLine == null) {
	        newLine = "\n";
	    }
	    
		StringBuffer sb = new StringBuffer();

		sb.append("Table: ");
		sb.append(fullyQualifiedTableName);
		sb.append(newLine);

		sb.append("Primary Key:");
		sb.append(newLine);
		Iterator iter = primaryKey.values().iterator();
		while (iter.hasNext()) {
			sb.append("   ");
			sb.append(iter.next());
			sb.append(newLine);
		}

		sb.append("Columns:");
		sb.append(newLine);
		iter = columns.values().iterator();
		while (iter.hasNext()) {
			sb.append("   ");
			sb.append(iter.next());
			sb.append(newLine);
		}
		return sb.toString();
	}

	private boolean hasPrimaryKey() {
		return primaryKey.size() > 0;
	}

	private boolean hasBLOBColumns() {
		boolean rc = false;
		Iterator iter = columns.values().iterator();
		while (iter.hasNext()) {
			ColumnDefinition cd = (ColumnDefinition) iter.next();
			if (cd.isBLOBColumn()) {
				rc = true;
				break;
			}
		}
		
		return rc;
	}

	private boolean hasNonBLOBColumns() {
		boolean rc = false;
		Iterator iter = columns.values().iterator();
		while (iter.hasNext()) {
			ColumnDefinition cd = (ColumnDefinition) iter.next();
			if (!cd.isBLOBColumn()) {
				rc = true;
				break;
			}
		}
		
		return rc;
	}
	
	public Collection getAllColumns() {
		if (allColumns == null) {
			allColumns = new ArrayList();

			allColumns.addAll(primaryKey.values());
			allColumns.addAll(columns.values());
		}

		return allColumns;
	}

	public ColumnDefinition getColumn(String columnName) {
		String key = columnName.toUpperCase();
		ColumnDefinition cd = (ColumnDefinition) primaryKey.get(key);

		if (cd == null) {
			cd = (ColumnDefinition) columns.get(key);
		}

		return cd;
	}
	
	private void calculateTableType() {
		if (!hasPrimaryKey()
				&& !hasBLOBColumns()
				&& hasNonBLOBColumns()) {
			tableType = TABLE_TYPE_1_NONBLOBS;
		} else if (!hasPrimaryKey() 
				&& hasBLOBColumns()
				&& hasNonBLOBColumns()) {
			tableType = TABLE_TYPE_2_BLOBS_NONBLOBS;
		} else if (hasPrimaryKey()
				&& !hasBLOBColumns()
				&& !hasNonBLOBColumns()) {
			tableType = TABLE_TYPE_3_PK;
		} else if (hasPrimaryKey()
				&& !hasBLOBColumns()
				&& hasNonBLOBColumns()) {
			tableType = TABLE_TYPE_4_PK_NONBLOBS;
		} else if (hasPrimaryKey()
				&& hasBLOBColumns()
				&& !hasNonBLOBColumns()) {
			tableType = TABLE_TYPE_5_PK_BLOBS;
		} else if (hasPrimaryKey()
				&& hasBLOBColumns()
				&& hasNonBLOBColumns()) {
			tableType = TABLE_TYPE_6_PK_BLOBS_NONBLOBS;
		} else {
			tableType = TABLE_TYPE_0_UNKNOWN;
		}
	}
	
	public boolean generatePrimaryKey() {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_3_PK:
		case TABLE_TYPE_4_PK_NONBLOBS:
		case TABLE_TYPE_5_PK_BLOBS:
		case TABLE_TYPE_6_PK_BLOBS_NONBLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		return rc;
	}
	
	public boolean generateRecordExtendingPrimaryKey() {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_4_PK_NONBLOBS:
		case TABLE_TYPE_6_PK_BLOBS_NONBLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		return rc;
	}
	
	public boolean generateRecordExtendingNothing() {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_1_NONBLOBS:
		case TABLE_TYPE_2_BLOBS_NONBLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		return rc;
	}

	public boolean generateRecordWithBLOBsExtendingPrimaryKey() {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_5_PK_BLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		return rc;
	}

	public boolean generateRecordWithBLOBsExtendingRecord() {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_2_BLOBS_NONBLOBS:
		case TABLE_TYPE_6_PK_BLOBS_NONBLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		return rc;
	}
	
	public boolean generateExampleExtendingPrimaryKey(TableConfiguration tc) {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_3_PK:
		case TABLE_TYPE_5_PK_BLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		if (rc) {
			rc = tc.isSelectByExampleStatementEnabled()
				|| tc.isDeleteByExampleStatementEnabled();
		}
		
		return rc;
	}

	public boolean generateExampleExtendingRecord(TableConfiguration tc) {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_1_NONBLOBS:
		case TABLE_TYPE_2_BLOBS_NONBLOBS:
		case TABLE_TYPE_4_PK_NONBLOBS:
		case TABLE_TYPE_6_PK_BLOBS_NONBLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		if (rc) {
			rc = tc.isSelectByExampleStatementEnabled()
				|| tc.isDeleteByExampleStatementEnabled();
		}
		
		return rc;
	}
	
	public boolean generateBaseResultMap(TableConfiguration tc) {
		return tc.isSelectByExampleStatementEnabled()
			|| tc.isSelectByPrimaryKeyStatementEnabled();
	}
	
	public boolean generateResultMapWithBLOBs(TableConfiguration tc) {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_2_BLOBS_NONBLOBS:
		case TABLE_TYPE_5_PK_BLOBS:
		case TABLE_TYPE_6_PK_BLOBS_NONBLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		if (rc) {
			rc = tc.isSelectByExampleStatementEnabled()
				|| tc.isSelectByPrimaryKeyStatementEnabled();
		}
		
		return rc;
	}

	public boolean generateSelectByExample(TableConfiguration tc) {
		return tc.isSelectByExampleStatementEnabled();
	}
	
	public boolean generateSelectByExampleWithBLOBs(TableConfiguration tc) {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_2_BLOBS_NONBLOBS:
		case TABLE_TYPE_5_PK_BLOBS:
		case TABLE_TYPE_6_PK_BLOBS_NONBLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		if (rc) {
			rc = tc.isSelectByExampleStatementEnabled();
		}
		
		return rc;
	}
	
	public boolean generateSelectByPrimaryKey(TableConfiguration tc) {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_4_PK_NONBLOBS:
		case TABLE_TYPE_5_PK_BLOBS:
		case TABLE_TYPE_6_PK_BLOBS_NONBLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		if (rc) {
			rc = tc.isSelectByPrimaryKeyStatementEnabled();
		}
		
		return rc;
	}

	public boolean generateInsert(TableConfiguration tc) {
		return tc.isInsertStatementEnabled();
	}
	
	public boolean generateUpdateByPrimaryKeyWithBLOBs(TableConfiguration tc) {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_5_PK_BLOBS:
		case TABLE_TYPE_6_PK_BLOBS_NONBLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		if (rc) {
			rc = tc.isUpdateByPrimaryKeyStatementEnabled();
		}
		
		return rc;
	}
	
	public boolean generateUpdateByPrimaryKey(TableConfiguration tc) {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_4_PK_NONBLOBS:
		case TABLE_TYPE_6_PK_BLOBS_NONBLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		if (rc) {
			rc = tc.isUpdateByPrimaryKeyStatementEnabled();
		}
		
		return rc;
	}
	
	public boolean generateDeleteByPrimaryKey(TableConfiguration tc) {
		if (tableType == TABLE_TYPE_0_UNKNOWN) {
			calculateTableType();
		}
		
		boolean rc;
		
		switch(tableType) {
		case TABLE_TYPE_3_PK:
		case TABLE_TYPE_4_PK_NONBLOBS:
		case TABLE_TYPE_5_PK_BLOBS:
		case TABLE_TYPE_6_PK_BLOBS_NONBLOBS:
			rc = true;
			break;
			
		default:
			rc = false;
			break;
		}
		
		if (rc) {
			rc = tc.isDeleteByPrimaryKeyStatementEnabled();
		}
		
		return rc;
	}
	
	public boolean generateDeleteByExample(TableConfiguration tc) {
		return tc.isDeleteByExampleStatementEnabled();
	}
}
