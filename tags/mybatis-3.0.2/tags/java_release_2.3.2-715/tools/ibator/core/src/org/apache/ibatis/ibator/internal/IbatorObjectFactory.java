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
package org.apache.ibatis.ibator.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.ibator.api.CommentGenerator;
import org.apache.ibatis.ibator.api.DAOGenerator;
import org.apache.ibatis.ibator.api.JavaModelGenerator;
import org.apache.ibatis.ibator.api.JavaTypeResolver;
import org.apache.ibatis.ibator.api.SqlMapGenerator;
import org.apache.ibatis.ibator.config.IbatorContext;
import org.apache.ibatis.ibator.config.CommentGeneratorConfiguration;
import org.apache.ibatis.ibator.config.DAOGeneratorConfiguration;
import org.apache.ibatis.ibator.config.JavaModelGeneratorConfiguration;
import org.apache.ibatis.ibator.config.JavaTypeResolverConfiguration;
import org.apache.ibatis.ibator.config.SqlMapGeneratorConfiguration;
import org.apache.ibatis.ibator.internal.util.messages.Messages;

/**
 * This class creates the different configurable iBATOR generators
 * 
 * @author Jeff Butler
 */
public class IbatorObjectFactory {
    private static ClassLoader classLoader;
    private static Map<String, Class<?>> loadedClasses;
    
    static {
        classLoader = Thread.currentThread().getContextClassLoader();
        loadedClasses =  new HashMap<String, Class<?>>();
    }

    /**
     * Utility class.  No instances allowed 
     */
    private IbatorObjectFactory() {
        super();
    }
    
    public static synchronized void setClassLoader(ClassLoader classLoader) {
        IbatorObjectFactory.classLoader = classLoader;
        loadedClasses.clear();
    }

    public static Class<?> loadClass(String type) throws ClassNotFoundException {
        if (loadedClasses.containsKey(type)) {
            return loadedClasses.get(type);
        }
        
        Class<?> clazz;

        try {
            clazz = classLoader.loadClass(type);
        } catch (ClassNotFoundException e) {
            // ignore - fail safe below
            clazz = null;
        }
        
        if (clazz == null) {
            clazz = Class.forName(type);
        }
        
        return clazz;
    }
    
	public static Object createObject(String type) {
        Object answer;
        
        try {
            Class<?> clazz = loadClass(type);
            
            answer = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(
              Messages.getString("RuntimeError.6", type), e); //$NON-NLS-1$
            
        }
        
        return answer;
	}
	
	public static JavaTypeResolver createJavaTypeResolver(IbatorContext context,
			List<String> warnings) {
        JavaTypeResolverConfiguration config = context.getJavaTypeResolverConfiguration();
        String type;
        
        if (config != null && config.getConfigurationType() != null) {
            type = config.getConfigurationType();
        } else {
            type = context.getGeneratorSet().getJavaTypeResolverType();
        }
        
	    JavaTypeResolver answer = (JavaTypeResolver) createObject(type);
	    answer.setWarnings(warnings);

        if (config != null) {
            answer.addConfigurationProperties(config.getProperties());
        }
        
        answer.setIbatorContext(context);
	    
	    return answer;
	}
	
	public static SqlMapGenerator createSqlMapGenerator(IbatorContext context,
	        JavaModelGenerator javaModelGenerator, List<String> warnings) {
        
        SqlMapGeneratorConfiguration config = context.getSqlMapGeneratorConfiguration();
        String type;

        if (config.getConfigurationType() != null) {
            type = config.getConfigurationType();
        } else {
            type = context.getGeneratorSet().getSqlMapGeneratorType();
        }
        
	    SqlMapGenerator answer = (SqlMapGenerator) createObject(type);
	    answer.setWarnings(warnings);

	    answer.setJavaModelGenerator(javaModelGenerator);
	    answer.addConfigurationProperties(config.getProperties());
        answer.setIbatorContext(context);
	    answer.setTargetPackage(config.getTargetPackage());
	    answer.setTargetProject(config.getTargetProject());
	    
	    return answer;
	}
	
	public static JavaModelGenerator createJavaModelGenerator(IbatorContext context,
			List<String> warnings) {

        JavaModelGeneratorConfiguration config = context.getJavaModelGeneratorConfiguration();
        String type;

        if (config.getConfigurationType() != null) {
            type = config.getConfigurationType();
        } else {
            type = context.getGeneratorSet().getJavaModelGeneratorType();
        }
        
	    JavaModelGenerator answer = (JavaModelGenerator) createObject(type);
	    answer.setWarnings(warnings);
	    
	    answer.addConfigurationProperties(config.getProperties());
        answer.setIbatorContext(context);
	    answer.setTargetPackage(config.getTargetPackage());
	    answer.setTargetProject(config.getTargetProject());
	    
	    return answer;
	}
	
	public static DAOGenerator createDAOGenerator(IbatorContext context,
	        JavaModelGenerator javaModelGenerator, SqlMapGenerator sqlMapGenerator, List<String> warnings) {
        
        DAOGeneratorConfiguration config = context.getDaoGeneratorConfiguration();
        
	    if (config == null) {
	        return null;
	    }
        
        String type = context.getGeneratorSet().translateDAOGeneratorType(config.getConfigurationType());
	    
	    DAOGenerator answer = (DAOGenerator) createObject(type);
	    answer.setWarnings(warnings);

	    answer.setJavaModelGenerator(javaModelGenerator);
	    answer.addConfigurationProperties(config.getProperties());
        answer.setIbatorContext(context);
	    answer.setSqlMapGenerator(sqlMapGenerator);
	    answer.setTargetPackage(config.getTargetPackage());
	    answer.setTargetProject(config.getTargetProject());
	    
	    return answer;
	}

    public static CommentGenerator createCommentGenerator(IbatorContext context) {
        
        CommentGeneratorConfiguration config = context.getCommentGeneratorConfiguration();
        CommentGenerator answer;
        
        String type;
        if (config == null || config.getConfigurationType() == null) {
            type = DefaultCommentGenerator.class.getName();
        } else {
            type = config.getConfigurationType();
        }
        
        answer = (CommentGenerator) createObject(type);
        
        if (config != null) {
            answer.addConfigurationProperties(config.getProperties());
        }
        
        return answer;
    }
}
