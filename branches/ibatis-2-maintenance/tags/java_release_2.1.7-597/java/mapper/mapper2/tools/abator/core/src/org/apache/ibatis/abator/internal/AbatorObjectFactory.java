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
package org.apache.ibatis.abator.internal;

import org.apache.ibatis.abator.api.DAOGenerator;
import org.apache.ibatis.abator.api.JavaModelGenerator;
import org.apache.ibatis.abator.api.JavaTypeResolver;
import org.apache.ibatis.abator.api.SqlMapGenerator;
import org.apache.ibatis.abator.config.DAOGeneratorConfiguration;
import org.apache.ibatis.abator.config.JavaModelGeneratorConfiguration;
import org.apache.ibatis.abator.config.JavaTypeResolverConfiguration;
import org.apache.ibatis.abator.config.SqlMapGeneratorConfiguration;
import org.apache.ibatis.abator.exception.GenerationRuntimeException;

/**
 * This class creates the different configurable Abator generators
 * 
 * @author Jeff Butler
 */
public class AbatorObjectFactory {

    /**
     * Utility class.  No instances allowed 
     */
    private AbatorObjectFactory() {
        super();
    }

	private static Object createObject(String className) {
		Object answer;

		try {
			answer = Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			throw new GenerationRuntimeException(
					"Cannot instantiate object of type: " + className
							+ " (InstantiationException)", e);
		} catch (ClassNotFoundException e) {
			throw new GenerationRuntimeException(
					"Cannot instantiate object of type: " + className
							+ " (ClassNotFoundException)", e);
		} catch (IllegalAccessException e) {
			throw new GenerationRuntimeException(
					"Cannot instantiate object of type: " + className
							+ " (IllegalAccessException)", e);
		}

		return answer;
	}
	
	public static JavaTypeResolver createJavaTypeResolver(JavaTypeResolverConfiguration configuration) {
	    JavaTypeResolver answer = (JavaTypeResolver) createObject(configuration.getType());
	    
	    answer.setProperties(configuration.getProperties());
	    
	    return answer;
	}
	
	public static SqlMapGenerator createSqlMapGenerator(SqlMapGeneratorConfiguration configuration,
	        JavaModelGenerator javaModelGenerator) {
	    SqlMapGenerator answer = (SqlMapGenerator) createObject(configuration.getType());

	    answer.setJavaModelGenerator(javaModelGenerator);
	    answer.setProperties(configuration.getProperties());
	    answer.setTargetPackage(configuration.getTargetPackage());
	    answer.setTargetProject(configuration.getTargetProject());
	    
	    return answer;
	    
	}
	
	public static JavaModelGenerator createJavaModelGenerator(JavaModelGeneratorConfiguration configuration) {
	    JavaModelGenerator answer = (JavaModelGenerator) createObject(configuration.getType());
	    
	    answer.setProperties(configuration.getProperties());
	    answer.setTargetPackage(configuration.getTargetPackage());
	    answer.setTargetProject(configuration.getTargetProject());
	    
	    return answer;
	}
	
	public static DAOGenerator createDAOGenerator(DAOGeneratorConfiguration configuration,
	        JavaModelGenerator javaModelGenerator, SqlMapGenerator sqlMapGenerator) {
	    if (!configuration.isEnabled()) {
	        return null;
	    }
	    
	    DAOGenerator answer = (DAOGenerator) createObject(configuration.getType());

	    answer.setJavaModelGenerator(javaModelGenerator);
	    answer.setProperties(configuration.getProperties());
	    answer.setSqlMapGenerator(sqlMapGenerator);
	    answer.setTargetPackage(configuration.getTargetPackage());
	    answer.setTargetProject(configuration.getTargetProject());
	    
	    return answer;
	}
}
