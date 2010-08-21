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
package org.apache.ibatis.abator.internal.util;

/**
 * 
 * @author Jeff Butler
 */
public class StringUtility {

	/**
	 * Utility class.  No instances allowed
	 */
	private StringUtility() {
		super();
	}

	public static boolean stringHasValue(String s) {
		return s != null && s.length() > 0;
	}

    public static String composeFullyQualifiedTableName(String catalog, String schema,
            String tableName) {
        StringBuffer sb = new StringBuffer();

        if (stringHasValue(catalog)) {
            sb.append(catalog);
            sb.append('.');
        }

        if (stringHasValue(schema)) {
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
}
