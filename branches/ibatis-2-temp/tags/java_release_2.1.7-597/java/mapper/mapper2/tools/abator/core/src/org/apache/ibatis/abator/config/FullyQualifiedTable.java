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

import org.apache.ibatis.abator.internal.util.EqualsUtil;
import org.apache.ibatis.abator.internal.util.HashCodeUtil;
import org.apache.ibatis.abator.internal.util.JavaBeansUtil;
import org.apache.ibatis.abator.internal.util.StringUtility;

/**
 * @author Jeff Butler
 */
public class FullyQualifiedTable {

	private String catalog;

	private String schema;

	private String tableName;

	private String domainObjectName;

	/**
	 *  
	 */
	public FullyQualifiedTable() {
		super();
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFullyQualifiedTableName() {
		StringBuffer sb = new StringBuffer();

		if (StringUtility.stringHasValue(catalog)) {
			sb.append(catalog);
			sb.append('.');
		}

		if (StringUtility.stringHasValue(schema)) {
			sb.append(schema);
			sb.append('.');
		} else {
		    if (sb.length() > 0) {
				sb.append('.');
		    }
		}

		sb.append(tableName);

		return sb.toString();
	}

	public String getFullyQualifiedTableNameWithUnderscores() {
		StringBuffer sb = new StringBuffer();

		if (StringUtility.stringHasValue(catalog)) {
			sb.append(catalog);
			sb.append('_');
		}

		if (StringUtility.stringHasValue(schema)) {
			sb.append(schema);
			sb.append('_');
		} else {
		    if (sb.length() > 0) {
				sb.append('_');
		    }
		}

		sb.append(tableName);

		return sb.toString();
	}
	
	public String getDomainObjectName() {
		if (StringUtility.stringHasValue(domainObjectName)) {
			return domainObjectName;
		} else {
			return JavaBeansUtil.getCamelCaseString(tableName, true);
		}
	}

	public void setDomainObjectName(String domainObjectName) {
		this.domainObjectName = domainObjectName;
	}
	
    public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof FullyQualifiedTable)) {
			return false;
		}

		FullyQualifiedTable other = (FullyQualifiedTable) obj;
		
		return EqualsUtil.areEqual(this.tableName, other.tableName)
		        && EqualsUtil.areEqual(this.catalog, other.catalog)
				&& EqualsUtil.areEqual(this.schema, other.schema);
    }
    
    public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, tableName);
		result = HashCodeUtil.hash(result, catalog);
		result = HashCodeUtil.hash(result, schema);

		return result;
    }
    
    public String toString() {
        return getFullyQualifiedTableName();
    }
}
