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
package org.apache.ibatis.abator.api;

import java.util.Map;

import org.apache.ibatis.abator.exception.UnsupportedDataTypeException;
import org.apache.ibatis.abator.internal.db.ColumnDefinition;

public interface JavaTypeResolver {
	void setProperties(Map properties);

	/**
	 * Initializes the ResolvedJavaType property of the ColumnDescription based
	 * on the jdbc type, length, and scale of the column.
	 * 
	 * @param cd the JDBC type will be used first to resolve the Java type. If
	 *            the type cannot be resolved from this value, then we will try
	 *            from the type name (which may be the qualified UDT from the
	 *            database)
	 */
	void initializeResolvedJavaType(ColumnDefinition cd)
			throws UnsupportedDataTypeException;
}
