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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.abator.api.dom.xml.Attribute;
import org.apache.ibatis.abator.api.dom.xml.XmlElement;
import org.apache.ibatis.abator.internal.util.EqualsUtil;
import org.apache.ibatis.abator.internal.util.HashCodeUtil;
import org.apache.ibatis.abator.internal.util.StringUtility;

/**
 * 
 * @author Jeff Butler
 */
public class TableConfiguration extends PropertyHolder {
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
    
    private String catalog;
    private String schema;
    private String tableName;
    private String domainObjectName;
    private String alias;
    private ModelType modelType;
    private boolean wildcardEscapingEnabled;
    
	public TableConfiguration(AbatorContext abatorContext) {
		super();
        
        this.modelType = abatorContext.getDefaultModelType();
		
		columnOverrides = new HashMap();
		ignoredColumns = new HashMap();

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
        
        boolean rc = ignoredColumns.containsKey(key);
        
        if (rc) {
            ignoredColumns.put(key, Boolean.TRUE);
        }
	    
	    return rc;
	}

	public void addIgnoredColumn(String column) {
		ignoredColumns.put(column.toUpperCase(), Boolean.FALSE);
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

		return EqualsUtil.areEqual(this.catalog, other.catalog)
        && EqualsUtil.areEqual(this.schema, other.schema)
        && EqualsUtil.areEqual(this.tableName, other.tableName);
	}

	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, catalog);
        result = HashCodeUtil.hash(result, schema);
        result = HashCodeUtil.hash(result, tableName);

		return result;
	}

	public boolean isSelectByExampleStatementEnabled() {
		return selectByExampleStatementEnabled;
	}

	public void setSelectByExampleStatementEnabled(
			boolean selectByExampleStatementEnabled) {
		this.selectByExampleStatementEnabled = selectByExampleStatementEnabled;
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
	
	public boolean areAnyStatementsEnabled() {
	    return selectByExampleStatementEnabled
	    	|| selectByPrimaryKeyStatementEnabled
	    	|| insertStatementEnabled
	    	|| updateByPrimaryKeyStatementEnabled
	    	|| deleteByExampleStatementEnabled
	    	|| deleteByPrimaryKeyStatementEnabled;
	}

    public void setGeneratedKey(GeneratedKey generatedKey) {
        this.generatedKey = generatedKey;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getDomainObjectName() {
        return domainObjectName;
    }

    public void setDomainObjectName(String domainObjectName) {
        this.domainObjectName = domainObjectName;
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

    public Iterator getColumnOverrides() {
        return columnOverrides.values().iterator();
    }

    /**
     * This method returns an iterator of Strings.  The values
     * are the columns that were specified to be ignored in the
     * table, but do not exist in the table. 
     * 
     * @return an Iterator of Strings - the columns that were improperly
     *  configured as ignored columns 
     */
    public Iterator getIgnoredColumnsInError() {
        List answer = new ArrayList();
        
        Iterator iter = ignoredColumns.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            
            if (Boolean.FALSE.equals(entry.getValue())) {
                answer.add(entry.getKey());
            }
        }
        
        return answer.iterator();
    }

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }

    public boolean isWildcardEscapingEnabled() {
        return wildcardEscapingEnabled;
    }

    public void setWildcardEscapingEnabled(boolean wildcardEscapingEnabled) {
        this.wildcardEscapingEnabled = wildcardEscapingEnabled;
    }
    
    public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("table"); //$NON-NLS-1$
        xmlElement.addAttribute(new Attribute("tableName", tableName)); //$NON-NLS-1$
        
        if (StringUtility.stringHasValue(catalog)) {
            xmlElement.addAttribute(new Attribute("catalog", catalog)); //$NON-NLS-1$
        }
        
        if (StringUtility.stringHasValue(schema)) {
            xmlElement.addAttribute(new Attribute("schema", schema)); //$NON-NLS-1$
        }
        
        if (StringUtility.stringHasValue(alias)) {
            xmlElement.addAttribute(new Attribute("alias", alias)); //$NON-NLS-1$
        }
        
        if (StringUtility.stringHasValue(domainObjectName)) {
            xmlElement.addAttribute(new Attribute("domainObjectName", domainObjectName)); //$NON-NLS-1$
        }
        
        if (!insertStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableInsert", "false")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if (!selectByPrimaryKeyStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableSelectByPrimaryKey", "false")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if (!selectByExampleStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableSelectByExample", "false")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if (!updateByPrimaryKeyStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableUpdateByPrimaryKey", "false")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if (!deleteByPrimaryKeyStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableDeleteByPrimaryKey", "false")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if (!deleteByExampleStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableDeleteByExample", "false")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (StringUtility.stringHasValue(selectByPrimaryKeyQueryId)) {
            xmlElement.addAttribute(new Attribute("selectByPrimaryKeyQueryId", selectByPrimaryKeyQueryId)); //$NON-NLS-1$
        }
        
        if (StringUtility.stringHasValue(selectByExampleQueryId)) {
            xmlElement.addAttribute(new Attribute("selectByExampleQueryId", selectByExampleQueryId)); //$NON-NLS-1$
        }
        
        if (modelType != null) {
            xmlElement.addAttribute(new Attribute("modelType", modelType.getModelType())); //$NON-NLS-1$
        }
        
        if (wildcardEscapingEnabled) {
            xmlElement.addAttribute(new Attribute("escapeWildcards", "true")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        addPropertyXmlElements(xmlElement);
        
        if (generatedKey != null) {
            xmlElement.addElement(generatedKey.toXmlElement());
        }
        
        if (ignoredColumns.size() > 0) {
            Iterator iter = ignoredColumns.keySet().iterator();
            while (iter.hasNext()) {
                String column = (String) iter.next();
                XmlElement ignoreColumn = new XmlElement("ignoreColumn"); //$NON-NLS-1$
                ignoreColumn.addAttribute(new Attribute("column", column)); //$NON-NLS-1$
                xmlElement.addElement(ignoreColumn);
            }
        }
        
        if (columnOverrides.size() > 0) {
            Iterator iter = columnOverrides.values().iterator();
            while (iter.hasNext()) {
                ColumnOverride columnOverride = (ColumnOverride) iter.next();
                xmlElement.addElement(columnOverride.toXmlElement());
            }
        }
        
        return xmlElement;
    }
}
