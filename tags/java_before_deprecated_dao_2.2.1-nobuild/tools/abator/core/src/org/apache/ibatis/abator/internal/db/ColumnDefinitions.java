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

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * This class holds the results of introspecting the database table.
 * 
 * @author Jeff Butler
 */
public class ColumnDefinitions {
	
    private LinkedHashMap primaryKeyColumns;
	private LinkedHashMap baseColumns;
    private LinkedHashMap blobColumns;
    private boolean hasJDBCDateColumns;
    private boolean hasJDBCTimeColumns;

	public ColumnDefinitions() {
		super();
		primaryKeyColumns = new LinkedHashMap();
        baseColumns = new LinkedHashMap();
        blobColumns = new LinkedHashMap();
	}

	public Collection getBLOBColumns() {
        return blobColumns.values();
	}

	public Collection getBaseColumns() {
        return baseColumns.values();
	}
	
	public Collection getPrimaryKeyColumns() {
		return primaryKeyColumns.values();
	}

	public void addColumn(ColumnDefinition cd) {
        if (cd.isBLOBColumn()) {
            blobColumns.put(cd.getActualColumnName().toUpperCase(), cd);
        } else {
            baseColumns.put(cd.getActualColumnName().toUpperCase(), cd);
        }
        
        if (cd.isJDBCDateColumn()) {
            hasJDBCDateColumns = true;
        }
        
        if (cd.isJDBCTimeColumn()) {
            hasJDBCTimeColumns = true;
        }
	}

	public void addPrimaryKeyColumn(String columnName) {
		String key = columnName.toUpperCase();
		if (baseColumns.containsKey(key)) {
			primaryKeyColumns.put(key, baseColumns.remove(key));
		} else if (blobColumns.containsKey(key)) {
            // in the wierd event that a BLOB is a key column
            primaryKeyColumns.put(key, blobColumns.remove(key));
        }
    }

	public boolean hasPrimaryKeyColumns() {
		return primaryKeyColumns.size() > 0;
	}

	public boolean hasBLOBColumns() {
        return blobColumns.size() > 0;
	}

	public boolean hasBaseColumns() {
        return baseColumns.size() > 0;
	}
	
	public ColumnDefinition getColumn(String columnName) {
        if (columnName == null) {
            return null;
        } else {
            String key = columnName.toUpperCase();
            ColumnDefinition cd = (ColumnDefinition) primaryKeyColumns.get(key);

            if (cd == null) {
                cd = (ColumnDefinition) baseColumns.get(key);
            }

            if (cd == null) {
                cd = (ColumnDefinition) blobColumns.get(key);
            }
            
            return cd;
        }
	}
    
    public boolean hasJDBCDateColumns() {
        return hasJDBCDateColumns;
    }
    
    public boolean hasJDBCTimeColumns() {
        return hasJDBCTimeColumns;
    }
    
    public boolean hasAnyColumns() {
        return primaryKeyColumns.size() > 0
            || baseColumns.size() > 0
            || blobColumns.size() > 0;
    }
}
