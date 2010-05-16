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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Butler
 */
public class DatabaseDialects {
    public static final Integer DB2 = new Integer(1);

    public static final Integer MYSQL = new Integer(2);

    public static final Integer SQLSERVER = new Integer(3);

    public static final Integer CLOUDSCAPE = new Integer(4);

    public static final Integer DERBY = new Integer(5);
    
    public static final Integer HSQLDB = new Integer(6);
    
    private static final Map identityClauses;

    static {
        identityClauses = new HashMap();

        identityClauses.put(DB2, "VALUES IDENTITY_VAL_LOCAL()"); //$NON-NLS-1$
        identityClauses.put(MYSQL, "SELECT LAST_INSERT_ID()"); //$NON-NLS-1$
        identityClauses.put(SQLSERVER, "SELECT SCOPE_IDENTITY()"); //$NON-NLS-1$
        identityClauses.put(CLOUDSCAPE, "VALUES IDENTITY_VAL_LOCAL()"); //$NON-NLS-1$
        identityClauses.put(DERBY, "VALUES IDENTITY_VAL_LOCAL()"); //$NON-NLS-1$
        identityClauses.put(HSQLDB, "CALL IDENTITY()"); //$NON-NLS-1$
    }

    public static String getIdentityClause(Integer dialect) {
        return (String) identityClauses.get(dialect);
    }

    /**
     *  
     */
    private DatabaseDialects() {
        super();
    }
}
