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

import org.apache.ibatis.abator.internal.db.DatabaseDialects;

/**
 * This class specifies that a key is auto-generated, either as an identity
 * column (post insert), or as some other query like a sequences (pre insert).
 * 
 * @author Jeff Butler
 */
public class GeneratedKey {
    private String column;

    private String sqlStatement;

    private boolean isIdentity;

    /**
     * 
     */
    public GeneratedKey(String column, String sqlStatement, boolean isIdentity) {
        super();
        this.column = column;
        this.isIdentity = isIdentity;

        if ("DB2".equalsIgnoreCase(sqlStatement)) { //$NON-NLS-1$
            this.sqlStatement = DatabaseDialects
                    .getIdentityClause(DatabaseDialects.DB2);
        } else if ("MySQL".equalsIgnoreCase(sqlStatement)) { //$NON-NLS-1$
            this.sqlStatement = DatabaseDialects
                    .getIdentityClause(DatabaseDialects.MYSQL);
        } else if ("SqlServer".equalsIgnoreCase(sqlStatement)) { //$NON-NLS-1$
            this.sqlStatement = DatabaseDialects
                    .getIdentityClause(DatabaseDialects.SQLSERVER);
        } else if ("Cloudscape".equalsIgnoreCase(sqlStatement)) { //$NON-NLS-1$
            this.sqlStatement = DatabaseDialects
                    .getIdentityClause(DatabaseDialects.CLOUDSCAPE);
        } else if ("Derby".equalsIgnoreCase(sqlStatement)) { //$NON-NLS-1$
            this.sqlStatement = DatabaseDialects
                    .getIdentityClause(DatabaseDialects.DERBY);
        } else if ("HSQLDB".equalsIgnoreCase(sqlStatement)) { //$NON-NLS-1$
            this.sqlStatement = DatabaseDialects
                    .getIdentityClause(DatabaseDialects.HSQLDB);
        } else {
            this.sqlStatement = sqlStatement;
        }
    }

    public String getColumn() {
        return column;
    }

    public boolean isIdentity() {
        return isIdentity;
    }

    public String getSqlStatement() {
        return sqlStatement;
    }
}
