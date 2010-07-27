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

import org.apache.ibatis.abator.internal.util.StringUtility;

/**
 * This class specifies that a key is auto-generated, either as an identity column
 * (post insert), or as some other query like a sequences (pre insert).
 * 
 * @author Jeff Butler
 */
public class GeneratedKey {
	private String column;

	private String sqlStatement;
	
	private boolean identity;

	/**
	 *  
	 */
	public GeneratedKey() {
		super();
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}
	
    public boolean isIdentity() {
        return identity;
    }
    
    public void setIdentity(boolean identity) {
        this.identity = identity;
    }
    
    public String getSqlStatement() {
        return sqlStatement;
    }
    
    public void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }
    
    public boolean isConfigured() {
        return StringUtility.stringHasValue(column);
    }
}
