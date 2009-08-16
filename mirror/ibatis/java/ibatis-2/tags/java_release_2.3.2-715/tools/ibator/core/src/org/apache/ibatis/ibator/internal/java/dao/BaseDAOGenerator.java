/*
 *  Copyright 2006 The Apache Software Foundation
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
package org.apache.ibatis.ibator.internal.java.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.ibator.api.CommentGenerator;
import org.apache.ibatis.ibator.api.DAOGenerator;
import org.apache.ibatis.ibator.api.DAOMethodNameCalculator;
import org.apache.ibatis.ibator.api.FullyQualifiedTable;
import org.apache.ibatis.ibator.api.GeneratedJavaFile;
import org.apache.ibatis.ibator.api.IntrospectedTable;
import org.apache.ibatis.ibator.api.JavaModelGenerator;
import org.apache.ibatis.ibator.api.ProgressCallback;
import org.apache.ibatis.ibator.api.SqlMapGenerator;
import org.apache.ibatis.ibator.api.dom.java.CompilationUnit;
import org.apache.ibatis.ibator.api.dom.java.Field;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.java.InnerClass;
import org.apache.ibatis.ibator.api.dom.java.Interface;
import org.apache.ibatis.ibator.api.dom.java.JavaVisibility;
import org.apache.ibatis.ibator.api.dom.java.Method;
import org.apache.ibatis.ibator.api.dom.java.Parameter;
import org.apache.ibatis.ibator.api.dom.java.PrimitiveTypeWrapper;
import org.apache.ibatis.ibator.api.dom.java.TopLevelClass;
import org.apache.ibatis.ibator.config.IbatorContext;
import org.apache.ibatis.ibator.config.PropertyRegistry;
import org.apache.ibatis.ibator.internal.IbatorObjectFactory;
import org.apache.ibatis.ibator.internal.DefaultDAOMethodNameCalculator;
import org.apache.ibatis.ibator.internal.ExtendedDAOMethodNameCalculator;
import org.apache.ibatis.ibator.internal.db.ColumnDefinition;
import org.apache.ibatis.ibator.internal.rules.IbatorRules;
import org.apache.ibatis.ibator.internal.util.JavaBeansUtil;
import org.apache.ibatis.ibator.internal.util.StringUtility;
import org.apache.ibatis.ibator.internal.util.messages.Messages;

/**
 * This class generates DAO classes based on the values in the supplied
 * DAOTemplate.
 * 
 * This class supports the following properties:
 * 
 * <dl>
 * <dt>enableSubPackages</dt>
 * <dd>If true, the classes will be generated in sub-packaged based on the
 * database catalog and schema - else the will be generated in the specified
 * package (the targetPackage attribute). Default is false.</dd>
 * 
 * <dt>rootInterface</dt>
 * <dd>If specified, then the root interface of the DAO interface class will be
 * set to the specified value. No checking is done to see if the specified
 * interface exists, or if the generated interface overrides any root interface
 * methods.</dd>
 *
 * <dt>exampleMethodVisibility</dt>
 * <dd>This property can be used the change the vilsibility of the various
 * example methods (selectByExample, deleteByExample, etc.).  If "public" (the default)
 * then the implementation methods are public and the methods are declared in the
 * interface declaration.  If any of the other valid values (private, protected,
 * default), then the methods have the specified visibility in the implmentation
 * class and the methods are not declared in the interface class.</dd>
 * 
 * <dt>methodNameCalculator</dt>
 * <dd>This property can be used to specify different method name
 * calculators.  A method name calculator is used to create the DAO method
 * names.  iBATOR offers two choices - default, and extended.  If you wish to
 * supply a different version, you can specify the fully qualified name of a
 * class that implements the
 * <code>org.apache.ibatis.ibator.api.DAOMethodNameCalculator</code>
 * interface.</dd>
 * </dl>
 * 
 * 
 * @author Jeff Butler
 */
public class BaseDAOGenerator implements DAOGenerator {

    protected IbatorContext ibatorContext;
    protected AbstractDAOTemplate daoTemplate;

    protected Properties properties;

    protected List<String> warnings;

    protected String targetPackage;

    protected String targetProject;

    protected JavaModelGenerator javaModelGenerator;

    protected SqlMapGenerator sqlMapGenerator;

    private Map<FullyQualifiedTable, Map<String, Object>> tableValueMaps;

    private boolean useJava5Features;
    
    protected JavaVisibility exampleMethodVisibility = JavaVisibility.PUBLIC;
    
    protected DAOMethodNameCalculator methodNameCalculator = new DefaultDAOMethodNameCalculator();
    
    /**
     * 
     */
    public BaseDAOGenerator(AbstractDAOTemplate daoTemplate,
            boolean useJava5Features) {
        super();
        this.daoTemplate = daoTemplate;
        this.useJava5Features = useJava5Features;
        tableValueMaps = new HashMap<FullyQualifiedTable, Map<String, Object>>();
        properties = new Properties();
    }

    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        
        String value = properties.getProperty(PropertyRegistry.DAO_EXAMPLE_METHOD_VISIBILITY);
        if (StringUtility.stringHasValue(value)) {
            if ("public".equalsIgnoreCase(value)) { //$NON-NLS-1$
                exampleMethodVisibility = JavaVisibility.PUBLIC;
            } else if ("private".equalsIgnoreCase(value)) { //$NON-NLS-1$
                exampleMethodVisibility = JavaVisibility.PRIVATE;
            } else if ("protected".equalsIgnoreCase(value)) { //$NON-NLS-1$
                exampleMethodVisibility = JavaVisibility.PROTECTED;
            } else if ("default".equalsIgnoreCase(value)) { //$NON-NLS-1$
                exampleMethodVisibility = JavaVisibility.DEFAULT;
            } else {
                warnings.add(Messages.getString("Warning.16", value)); //$NON-NLS-1$
            }
        }
        
        value = properties.getProperty(PropertyRegistry.DAO_METHOD_NAME_CALCULATOR);
        if (StringUtility.stringHasValue(value)) {
            if ("extended".equalsIgnoreCase(value)) { //$NON-NLS-1$
                methodNameCalculator = new ExtendedDAOMethodNameCalculator();
            } else if (!"default".equalsIgnoreCase(value) //$NON-NLS-1$
                    && StringUtility.stringHasValue(value)) {
                try {
                    methodNameCalculator = (DAOMethodNameCalculator)
                        IbatorObjectFactory.createObject(value);
                } catch (Exception e) {
                    warnings.add(Messages.getString("Warning.17", value, e.getMessage())); //$NON-NLS-1$
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.ibator.api.DAOGenerator#setWarnings(java.util.List)
     */
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.ibator.api.DAOGenerator#setTargetPackage(java.lang.String)
     */
    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.ibator.api.DAOGenerator#setTargetProject(java.lang.String)
     */
    public void setTargetProject(String targetProject) {
        this.targetProject = targetProject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.ibator.api.DAOGenerator#setJavaModelGenerator(org.apache.ibatis.ibator.api.JavaModelGenerator)
     */
    public void setJavaModelGenerator(JavaModelGenerator javaModelGenerator) {
        this.javaModelGenerator = javaModelGenerator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.ibator.api.DAOGenerator#setSqlMapGenerator(org.apache.ibatis.ibator.api.SqlMapGenerator)
     */
    public void setSqlMapGenerator(SqlMapGenerator sqlMapGenerator) {
        this.sqlMapGenerator = sqlMapGenerator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.ibator.api.DAOGenerator#getGeneratedJavaFiles(org.apache.ibatis.ibator.internal.db.IntrospectedTable,
     *      org.apache.ibatis.ibator.api.ProgressCallback)
     */
    public List<GeneratedJavaFile> getGeneratedJavaFiles(IntrospectedTable introspectedTable,
            ProgressCallback callback) {
        List<GeneratedJavaFile> list = new ArrayList<GeneratedJavaFile>();

        callback.startSubTask(Messages.getString("Progress.10", //$NON-NLS-1$
                introspectedTable.getTable().toString()));
        CompilationUnit cu = getDAOImplementation(introspectedTable);
        GeneratedJavaFile gjf = new GeneratedJavaFile(cu, targetProject);
        list.add(gjf);

        callback.startSubTask(Messages.getString("Progress.11", //$NON-NLS-1$
                introspectedTable.getTable().toString()));
        cu = getDAOInterface(introspectedTable);
        gjf = new GeneratedJavaFile(cu, targetProject);
        list.add(gjf);

        return list;
    }

    protected TopLevelClass getDAOImplementation(
            IntrospectedTable introspectedTable) {

        CommentGenerator commentGenerator = ibatorContext.getCommentGenerator();
        
        FullyQualifiedTable table = introspectedTable.getTable();
        FullyQualifiedJavaType type = getDAOImplementationType(table);
        TopLevelClass answer = new TopLevelClass(type);
        answer.setVisibility(JavaVisibility.PUBLIC);
        answer.setSuperClass(daoTemplate.getSuperClass());
        answer.addImportedType(daoTemplate.getSuperClass());
        answer.addSuperInterface(getDAOInterfaceType(table));
        answer.addImportedType(getDAOInterfaceType(table));

        for (FullyQualifiedJavaType fqjt : daoTemplate.getImplementationImports()) {
            answer.addImportedType(fqjt);
        }
        
        commentGenerator.addJavaFileComment(answer);

        // add constructor
        answer.addMethod(daoTemplate.getConstructorClone(commentGenerator,
                getDAOImplementationType(table), table));

        // add any fields from the template
        for (Field field : daoTemplate.getFieldClones(commentGenerator, table)) {
            answer.addField(field);
        }

        // add any methods from the template
        for (Method method : daoTemplate.getMethodClones(commentGenerator, table)) {
            answer.addMethod(method);
        }

        IbatorRules rules = introspectedTable.getRules();
        List<Method> methods;
        
        if (rules.generateInsert()) {
            methods = getInsertMethods(introspectedTable, false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateUpdateByPrimaryKeyWithoutBLOBs()) {
            methods = getUpdateByPrimaryKeyWithoutBLOBsMethods(introspectedTable, false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateUpdateByPrimaryKeyWithBLOBs()) {
            methods = getUpdateByPrimaryKeyWithBLOBsMethods(introspectedTable,
                    false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateUpdateByPrimaryKeySelective()) {
            methods = getUpdateByPrimaryKeySelectiveMethods(introspectedTable,
                    false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateSelectByExampleWithoutBLOBs()) {
            methods = getSelectByExampleWithoutBLOBsMethods(introspectedTable, false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateSelectByExampleWithBLOBs()) {
            methods = getSelectByExampleWithBLOBsMethods(introspectedTable, false,
                    answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateSelectByPrimaryKey()) {
            methods = getSelectByPrimaryKeyMethods(introspectedTable, false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateDeleteByExample()) {
            methods = getDeleteByExampleMethods(introspectedTable, false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateDeleteByPrimaryKey()) {
            methods = getDeleteByPrimaryKeyMethods(introspectedTable, false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateCountByExample()) {
            methods = getCountByExampleMethods(introspectedTable, false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }
        
        if (rules.generateUpdateByExampleSelective()) {
            methods = getUpdateByExampleSelectiveMethods(introspectedTable, false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }
        
        if (rules.generateUpdateByExampleWithBLOBs()) {
            methods = getUpdateByExampleWithBLOBsMethods(introspectedTable, false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateUpdateByExampleWithoutBLOBs()) {
            methods = getUpdateByExampleWithoutBLOBsMethods(introspectedTable, false, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }
        
        if (rules.generateUpdateByExampleSelective()
                || rules.generateUpdateByExampleWithBLOBs()
                || rules.generateUpdateByExampleWithoutBLOBs()) {
            InnerClass innerClass = getUpdateByExampleParms(introspectedTable, answer);
            if (innerClass != null) {
                answer.addInnerClass(innerClass);
            }
        }

        afterImplementationGenerationHook(introspectedTable, answer);
        
        return answer;
    }

    /**
     * Override this method to provide any extra customization of 
     * the generated interface.
     * 
     * @param introspectedTable
     * @param generatedInterface the generated interface
     */
    protected void afterInterfaceGenerationHook(IntrospectedTable introspectedTable,
            Interface generatedInterface) {
        return;
    }
    
    /**
     * Override this method to provide any extra customization of 
     * the generated implementation class.
     * 
     * @param introspectedTable
     * @param generatedClass the generated class
     */
    protected void afterImplementationGenerationHook(IntrospectedTable introspectedTable,
            TopLevelClass generatedClass) {
        return;
    }

    protected Interface getDAOInterface(IntrospectedTable introspectedTable) {
        FullyQualifiedTable table = introspectedTable.getTable();
        Interface answer = new Interface(getDAOInterfaceType(table));
        answer.setVisibility(JavaVisibility.PUBLIC);

        String rootInterface = introspectedTable.getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (rootInterface == null) {
            rootInterface = properties.getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }
        
        if (StringUtility.stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(rootInterface);
            answer.addSuperInterface(fqjt);
            answer.addImportedType(fqjt);
        }

        for (FullyQualifiedJavaType fqjt : daoTemplate.getInterfaceImports()) {
            answer.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addJavaFileComment(answer);
        
        IbatorRules rules = introspectedTable.getRules();
        List<Method> methods;
        
        if (rules.generateInsert()) {
            methods = getInsertMethods(introspectedTable, true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateUpdateByPrimaryKeyWithoutBLOBs()) {
            methods = getUpdateByPrimaryKeyWithoutBLOBsMethods(introspectedTable, true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateUpdateByPrimaryKeyWithBLOBs()) {
            methods = getUpdateByPrimaryKeyWithBLOBsMethods(introspectedTable,
                    true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateUpdateByPrimaryKeySelective()) {
            methods = getUpdateByPrimaryKeySelectiveMethods(introspectedTable,
                    true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateSelectByExampleWithoutBLOBs()) {
            methods = getSelectByExampleWithoutBLOBsMethods(introspectedTable, true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateSelectByExampleWithBLOBs()) {
            methods = getSelectByExampleWithBLOBsMethods(introspectedTable, true,
                    answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateSelectByPrimaryKey()) {
            methods = getSelectByPrimaryKeyMethods(introspectedTable, true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateDeleteByExample()) {
            methods = getDeleteByExampleMethods(introspectedTable, true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateDeleteByPrimaryKey()) {
            methods = getDeleteByPrimaryKeyMethods(introspectedTable, true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateCountByExample()) {
            methods = getCountByExampleMethods(introspectedTable, true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }
        
        if (rules.generateUpdateByExampleSelective()) {
            methods = getUpdateByExampleSelectiveMethods(introspectedTable, true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }
        
        if (rules.generateUpdateByExampleWithBLOBs()) {
            methods = getUpdateByExampleWithBLOBsMethods(introspectedTable, true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }

        if (rules.generateUpdateByExampleWithoutBLOBs()) {
            methods = getUpdateByExampleWithoutBLOBsMethods(introspectedTable, true, answer);
            if (methods != null) {
                for (Method method : methods) {
                    answer.addMethod(method);
                }
            }
        }
        
        afterInterfaceGenerationHook(introspectedTable, answer);
        
        return answer;
    }

    protected FullyQualifiedJavaType getDAOImplementationType(
            FullyQualifiedTable table) {
        String key = "getDAOImplementationType"; //$NON-NLS-1$

        Map<String, Object> map = getTableValueMap(table);
        FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) map.get(key);
        if (fqjt == null) {
            StringBuffer sb = new StringBuffer();
            sb.append(getDAOPackage(table));
            sb.append('.');
            sb.append(table.getDomainObjectName());
            sb.append("DAOImpl"); //$NON-NLS-1$

            fqjt = new FullyQualifiedJavaType(sb.toString());
            map.put(key, fqjt);
        }

        return fqjt;
    }

    protected List<Method> getInsertMethods(IntrospectedTable introspectedTable,
            boolean interfaceMethod, CompilationUnit compilationUnit) {

        FullyQualifiedTable table = introspectedTable.getTable();
        Method method = new Method();

        FullyQualifiedJavaType returnType;
        if (introspectedTable.getGeneratedKey() != null) {
            ColumnDefinition cd = introspectedTable.getColumn(
                            introspectedTable.getGeneratedKey().getColumn());
            if (cd == null) {
                // the specified column doesn't exist, so don't do the generated
                // key
                // (the warning has already been reported)
                returnType = null;
            } else {
                returnType = cd.getResolvedJavaType()
                        .getFullyQualifiedJavaType();
                compilationUnit.addImportedType(returnType);
            }
        } else {
            returnType = null;
        }
        method.setReturnType(returnType);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(methodNameCalculator.getInsertMethodName(introspectedTable));

        FullyQualifiedJavaType parameterType =
            introspectedTable.getRules().calculateAllFieldsClass(javaModelGenerator, table);
        
        compilationUnit.addImportedType(parameterType);
        method.addParameter(new Parameter(parameterType, "record")); //$NON-NLS-1$

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);
        
        if (!interfaceMethod) {
            // generate the implementation method
            StringBuffer sb = new StringBuffer();

            if (returnType != null) {
                sb.append("Object newKey = "); //$NON-NLS-1$
            }

            sb.append(daoTemplate.getInsertMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getInsertStatementId(), "record")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());

            if (returnType != null) {
                if ("Object".equals(returnType.getShortName())) { //$NON-NLS-1$
                    // no need to cast if the return type is Object
                    method.addBodyLine("return newKey;"); //$NON-NLS-1$
                } else {
                    sb.setLength(0);

                    if (returnType.isPrimitive()) {
                        PrimitiveTypeWrapper ptw = returnType
                                .getPrimitiveTypeWrapper();
                        sb.append("return (("); //$NON-NLS-1$
                        sb.append(ptw.getShortName());
                        sb.append(") newKey"); //$NON-NLS-1$
                        sb.append(")."); //$NON-NLS-1$
                        sb.append(ptw.getToPrimitiveMethod());
                        sb.append(';');
                    } else {
                        sb.append("return ("); //$NON-NLS-1$
                        sb.append(returnType.getShortName());
                        sb.append(") newKey;"); //$NON-NLS-1$
                    }

                    method.addBodyLine(sb.toString());
                }
            }
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }

    protected List<Method> getUpdateByPrimaryKeyWithoutBLOBsMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {

        FullyQualifiedTable table = introspectedTable.getTable();
        FullyQualifiedJavaType parameterType = 
            javaModelGenerator.getBaseRecordType(table);
        compilationUnit.addImportedType(parameterType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(methodNameCalculator.getUpdateByPrimaryKeyWithoutBLOBsMethodName(introspectedTable));
        method.addParameter(new Parameter(parameterType, "record")); //$NON-NLS-1$

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);

        if (!interfaceMethod) {
            // generate the implementation method
            StringBuffer sb = new StringBuffer();

            sb.append("int rows = "); //$NON-NLS-1$
            sb.append(daoTemplate.getUpdateMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getUpdateByPrimaryKeyStatementId(), "record")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());

            method.addBodyLine("return rows;"); //$NON-NLS-1$
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }

    protected List<Method> getUpdateByPrimaryKeyWithBLOBsMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {

        FullyQualifiedTable table = introspectedTable.getTable();
        FullyQualifiedJavaType parameterType;
        
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            parameterType = javaModelGenerator.getRecordWithBLOBsType(table);
        } else {
            parameterType = javaModelGenerator.getBaseRecordType(table);
        }
        
        compilationUnit.addImportedType(parameterType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(methodNameCalculator.getUpdateByPrimaryKeyWithBLOBsMethodName(introspectedTable));
        method.addParameter(new Parameter(parameterType, "record")); //$NON-NLS-1$

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);
        
        if (!interfaceMethod) {
            // generate the implementation method
            StringBuffer sb = new StringBuffer();

            sb.append("int rows = "); //$NON-NLS-1$
            sb.append(daoTemplate.getUpdateMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getUpdateByPrimaryKeyWithBLOBsStatementId(), "record")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());

            method.addBodyLine("return rows;"); //$NON-NLS-1$
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }

    protected List<Method> getUpdateByPrimaryKeySelectiveMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {

        FullyQualifiedTable table = introspectedTable.getTable();
        FullyQualifiedJavaType parameterType;
        
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            parameterType = javaModelGenerator.getRecordWithBLOBsType(table);
        } else {
            parameterType = javaModelGenerator.getBaseRecordType(table);
        }
        
        compilationUnit.addImportedType(parameterType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(methodNameCalculator.getUpdateByPrimaryKeySelectiveMethodName(introspectedTable));
        method.addParameter(new Parameter(parameterType, "record")); //$NON-NLS-1$

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);

        if (!interfaceMethod) {
            // generate the implementation method
            StringBuffer sb = new StringBuffer();

            sb.append("int rows = "); //$NON-NLS-1$
            sb.append(daoTemplate.getUpdateMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getUpdateByPrimaryKeySelectiveStatementId(), "record")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());

            method.addBodyLine("return rows;"); //$NON-NLS-1$
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }

    protected List<Method> getSelectByExampleWithoutBLOBsMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {

        if (interfaceMethod && exampleMethodVisibility != JavaVisibility.PUBLIC) {
            return null;
        }
        
        FullyQualifiedTable table = introspectedTable.getTable();
        FullyQualifiedJavaType type = javaModelGenerator.getExampleType(table);
        compilationUnit.addImportedType(type);
        compilationUnit.addImportedType(FullyQualifiedJavaType
                .getNewListInstance());

        Method method = new Method();
        method.setVisibility(exampleMethodVisibility);

        FullyQualifiedJavaType returnType;
        if (useJava5Features) {
            FullyQualifiedJavaType fqjt;
            if (introspectedTable.getRules().generateBaseRecordClass()) {
                fqjt = javaModelGenerator.getBaseRecordType(table);
            } else if (introspectedTable.getRules().generatePrimaryKeyClass()) {
                fqjt = javaModelGenerator.getPrimaryKeyType(table);
            } else {
                throw new RuntimeException(Messages
                        .getString("RuntimeError.12")); //$NON-NLS-1$
            }

            compilationUnit.addImportedType(fqjt);
            returnType = FullyQualifiedJavaType.getNewListInstance();
            returnType.addTypeArgument(fqjt);
        } else {
            returnType = FullyQualifiedJavaType.getNewListInstance();
        }
        method.setReturnType(returnType);

        method.setName(methodNameCalculator.getSelectByExampleWithoutBLOBsMethodName(introspectedTable));
        method.addParameter(new Parameter(type, "example")); //$NON-NLS-1$

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);

        if (!interfaceMethod) {
            // generate the implementation method
            StringBuffer sb = new StringBuffer();

            if (useJava5Features) {
                method.addSuppressTypeWarningsAnnotation();
                sb.append(returnType.getShortName());
                sb.append(" list = ("); //$NON-NLS-1$
                sb.append(returnType.getShortName());
                sb.append(") "); //$NON-NLS-1$
            } else {
                sb.append("List list = "); //$NON-NLS-1$
            }

            sb.append(daoTemplate.getQueryForListMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getSelectByExampleStatementId(), "example")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
            method.addBodyLine("return list;"); //$NON-NLS-1$
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }

    protected List<Method> getSelectByExampleWithBLOBsMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {

        if (interfaceMethod && exampleMethodVisibility != JavaVisibility.PUBLIC) {
            return null;
        }
        
        FullyQualifiedTable table = introspectedTable.getTable();
        FullyQualifiedJavaType type = javaModelGenerator.getExampleType(table);
        compilationUnit.addImportedType(type);
        compilationUnit.addImportedType(FullyQualifiedJavaType
                .getNewListInstance());

        Method method = new Method();
        method.setVisibility(exampleMethodVisibility);

        FullyQualifiedJavaType returnType;
        if (useJava5Features) {
            FullyQualifiedJavaType fqjt;
            if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
                fqjt = javaModelGenerator.getRecordWithBLOBsType(table);
            } else {
                // the blob fileds must be rolled up into the base class
                fqjt = javaModelGenerator.getBaseRecordType(table);
            }
            
            compilationUnit.addImportedType(fqjt);
            returnType = FullyQualifiedJavaType.getNewListInstance();
            returnType.addTypeArgument(fqjt);
        } else {
            returnType = FullyQualifiedJavaType.getNewListInstance();
        }
        method.setReturnType(returnType);

        method.setName(methodNameCalculator.getSelectByExampleWithBLOBsMethodName(introspectedTable));
        method.addParameter(new Parameter(type, "example")); //$NON-NLS-1$

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);

        if (!interfaceMethod) {
            // generate the implementation method

            StringBuffer sb = new StringBuffer();

            if (useJava5Features) {
                method.addSuppressTypeWarningsAnnotation();
                sb.append(returnType.getShortName());
                sb.append(" list = ("); //$NON-NLS-1$
                sb.append(returnType.getShortName());
                sb.append(") "); //$NON-NLS-1$
            } else {
                sb.append("List list = "); //$NON-NLS-1$
            }

            sb.append(daoTemplate.getQueryForListMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getSelectByExampleWithBLOBsStatementId(), "example")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
            method.addBodyLine("return list;"); //$NON-NLS-1$
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }

    protected List<Method> getSelectByPrimaryKeyMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {

        FullyQualifiedTable table = introspectedTable.getTable();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType returnType =
            introspectedTable.getRules().calculateAllFieldsClass(javaModelGenerator, table);
        method.setReturnType(returnType);
        compilationUnit.addImportedType(returnType);

        method.setName(methodNameCalculator.getSelectByPrimaryKeyMethodName(introspectedTable));
        
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = javaModelGenerator.getPrimaryKeyType(table);
            compilationUnit.addImportedType(type);
            method.addParameter(new Parameter(type, "key")); //$NON-NLS-1$
        } else {
            for (ColumnDefinition cd : introspectedTable.getPrimaryKeyColumns()) {
                FullyQualifiedJavaType type = cd.getResolvedJavaType().getFullyQualifiedJavaType();
                compilationUnit.addImportedType(type);
                method.addParameter(new Parameter(type, cd.getJavaProperty()));
            }
        }

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);

        if (!interfaceMethod) {
            // generate the implementation method
            StringBuffer sb = new StringBuffer();

            if (!introspectedTable.getRules().generatePrimaryKeyClass()) {
                // no primary key class, but primary key is enabled.  Primary
                // key columns must be in the base class.
                FullyQualifiedJavaType keyType = javaModelGenerator.getBaseRecordType(table);
                compilationUnit.addImportedType(keyType);
                
                sb.setLength(0);
                sb.append(keyType.getShortName());
                sb.append(" key = new "); //$NON-NLS-1$
                sb.append(keyType.getShortName());
                sb.append("();"); //$NON-NLS-1$
                method.addBodyLine(sb.toString());
                
                for (ColumnDefinition cd : introspectedTable.getPrimaryKeyColumns()) {
                    sb.setLength(0);
                    sb.append("key."); //$NON-NLS-1$
                    sb.append(JavaBeansUtil.getSetterMethodName(cd.getJavaProperty()));
                    sb.append('(');
                    sb.append(cd.getJavaProperty());
                    sb.append(");"); //$NON-NLS-1$
                    method.addBodyLine(sb.toString());
                }
            }

            sb.setLength(0);
            sb.append(returnType.getShortName());
            sb.append(" record = ("); //$NON-NLS-1$
            sb.append(returnType.getShortName());
            sb.append(") "); //$NON-NLS-1$
            sb.append(daoTemplate.getQueryForObjectMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getSelectByPrimaryKeyStatementId(), "key")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
            method.addBodyLine("return record;"); //$NON-NLS-1$
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }

    protected List<Method> getDeleteByExampleMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {

        if (interfaceMethod && exampleMethodVisibility != JavaVisibility.PUBLIC) {
            return null;
        }
        
        FullyQualifiedTable table = introspectedTable.getTable();
        FullyQualifiedJavaType type = javaModelGenerator.getExampleType(table);
        compilationUnit.addImportedType(type);

        Method method = new Method();
        method.setVisibility(exampleMethodVisibility);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(methodNameCalculator.getDeleteByExampleMethodName(introspectedTable));
        method.addParameter(new Parameter(type, "example")); //$NON-NLS-1$

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);

        if (!interfaceMethod) {
            // generate the implementation method
            StringBuffer sb = new StringBuffer();

            sb.append("int rows = "); //$NON-NLS-1$
            sb.append(daoTemplate.getDeleteMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getDeleteByExampleStatementId(), "example")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
            method.addBodyLine("return rows;"); //$NON-NLS-1$
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }

    protected List<Method> getDeleteByPrimaryKeyMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {

        FullyQualifiedTable table = introspectedTable.getTable();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(methodNameCalculator.getDeleteByPrimaryKeyMethodName(introspectedTable));

        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = javaModelGenerator.getPrimaryKeyType(table);
            compilationUnit.addImportedType(type);
            method.addParameter(new Parameter(type, "key")); //$NON-NLS-1$
        } else {
            for (ColumnDefinition cd : introspectedTable.getPrimaryKeyColumns()) {
                FullyQualifiedJavaType type = cd.getResolvedJavaType().getFullyQualifiedJavaType();
                compilationUnit.addImportedType(type);
                method.addParameter(new Parameter(type, cd.getJavaProperty()));
            }
        }

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);

        if (!interfaceMethod) {
            // generate the implementation method
            StringBuffer sb = new StringBuffer();

            if (!introspectedTable.getRules().generatePrimaryKeyClass()) {
                // no primary key class, but primary key is enabled.  Primary
                // key columns must be in the base class.
                FullyQualifiedJavaType keyType = javaModelGenerator.getBaseRecordType(table);
                compilationUnit.addImportedType(keyType);
                
                sb.setLength(0);
                sb.append(keyType.getShortName());
                sb.append(" key = new "); //$NON-NLS-1$
                sb.append(keyType.getShortName());
                sb.append("();"); //$NON-NLS-1$
                method.addBodyLine(sb.toString());
                
                for (ColumnDefinition cd : introspectedTable.getPrimaryKeyColumns()) {
                    sb.setLength(0);
                    sb.append("key."); //$NON-NLS-1$
                    sb.append(JavaBeansUtil.getSetterMethodName(cd.getJavaProperty()));
                    sb.append('(');
                    sb.append(cd.getJavaProperty());
                    sb.append(");"); //$NON-NLS-1$
                    method.addBodyLine(sb.toString());
                }
            }
            
            sb.setLength(0);
            sb.append("int rows = "); //$NON-NLS-1$
            sb.append(daoTemplate.getDeleteMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getDeleteByPrimaryKeyStatementId(), "key")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
            method.addBodyLine("return rows;"); //$NON-NLS-1$
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }

    protected List<Method> getCountByExampleMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {

        if (interfaceMethod && exampleMethodVisibility != JavaVisibility.PUBLIC) {
            return null;
        }
        
        FullyQualifiedTable table = introspectedTable.getTable();
        FullyQualifiedJavaType type = javaModelGenerator.getExampleType(table);
        compilationUnit.addImportedType(type);

        Method method = new Method();
        method.setVisibility(exampleMethodVisibility);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(methodNameCalculator.getCountByExampleMethodName(introspectedTable));
        method.addParameter(new Parameter(type, "example")); //$NON-NLS-1$

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);

        if (!interfaceMethod) {
            // generate the implementation method
            StringBuffer sb = new StringBuffer();

            sb.append("Integer count = (Integer)  "); //$NON-NLS-1$
            sb.append(daoTemplate.getQueryForObjectMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getCountByExampleStatementId(), "example")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
            
            if (useJava5Features) {
                method.addBodyLine("return count;"); //$NON-NLS-1$
            } else {
                method.addBodyLine("return count.intValue();"); //$NON-NLS-1$
            }
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }

    protected String getDAOPackage(FullyQualifiedTable table) {
        String key = "getDAOPackage"; //$NON-NLS-1$
        String s;

        Map<String, Object> map = getTableValueMap(table);
        s = (String) map.get(key);
        if (s == null) {
            StringBuffer sb = new StringBuffer(targetPackage);
            if ("true".equalsIgnoreCase(properties.getProperty(PropertyRegistry.ANY_ENABLE_SUB_PACKAGES))) { //$NON-NLS-1$
                sb.append(table.getSubPackage());
            }

            s = sb.toString();
            map.put(key, s);
        }

        return s;
    }

    protected FullyQualifiedJavaType getDAOInterfaceType(
            FullyQualifiedTable table) {
        String key = "getDAOInterfaceType"; //$NON-NLS-1$

        Map<String, Object> map = getTableValueMap(table);
        FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) map.get(key);
        if (fqjt == null) {
            StringBuffer sb = new StringBuffer();
            sb.append(getDAOPackage(table));
            sb.append('.');
            sb.append(table.getDomainObjectName());
            sb.append("DAO"); //$NON-NLS-1$

            fqjt = new FullyQualifiedJavaType(sb.toString());
            map.put(key, fqjt);
        }

        return fqjt;
    }

    private Map<String, Object> getTableValueMap(FullyQualifiedTable table) {
        Map<String, Object> map = tableValueMaps.get(table);
        if (map == null) {
            map = new HashMap<String, Object>();
            tableValueMaps.put(table, map);
        }

        return map;
    }

    public void setIbatorContext(IbatorContext ibatorContext) {
        this.ibatorContext = ibatorContext;
    }

    protected List<Method> getUpdateByExampleSelectiveMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {

        FullyQualifiedTable table = introspectedTable.getTable();
        FullyQualifiedJavaType parameterType;
        
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            parameterType = javaModelGenerator.getRecordWithBLOBsType(table);
        } else if (introspectedTable.getRules().generateBaseRecordClass()) {
            parameterType = javaModelGenerator.getBaseRecordType(table);
        } else {
            parameterType = javaModelGenerator.getPrimaryKeyType(table);
        }
        
        compilationUnit.addImportedType(parameterType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(methodNameCalculator.getUpdateByExampleSelectiveMethodName(introspectedTable));
        method.addParameter(new Parameter(parameterType, "record")); //$NON-NLS-1$
        method.addParameter(new Parameter(javaModelGenerator.getExampleType(table), "example")); //$NON-NLS-1$
        
        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);

        if (!interfaceMethod) {
            // generate the implementation method
            method.addBodyLine("UpdateByExampleParms parms = new UpdateByExampleParms(record, example);"); //$NON-NLS-1$
            
            StringBuffer sb = new StringBuffer();
            
            sb.append("int rows = "); //$NON-NLS-1$
            
            sb.append(daoTemplate.getUpdateMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getUpdateByExampleSelectiveStatementId(), "parms")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());

            method.addBodyLine("return rows;"); //$NON-NLS-1$
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }
    
    protected InnerClass getUpdateByExampleParms (IntrospectedTable introspectedTable,
            CompilationUnit compilationUnit) {
        FullyQualifiedTable table = introspectedTable.getTable();
        compilationUnit.addImportedType(javaModelGenerator.getExampleType(table));
        
        InnerClass answer = new InnerClass(
                new FullyQualifiedJavaType("UpdateByExampleParms")); //$NON-NLS-1$
        answer.setVisibility(JavaVisibility.PRIVATE);
        answer.setStatic(true);
        answer.setSuperClass(javaModelGenerator.getExampleType(table));
        ibatorContext.getCommentGenerator().addClassComment(answer, table);
        
        Method method = new Method();
        method.setConstructor(true);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(answer.getType().getShortName());
        method.addParameter(
                new Parameter(FullyQualifiedJavaType.getObjectInstance(),
                        "record")); //$NON-NLS-1$
        method.addParameter(
                new Parameter(javaModelGenerator.getExampleType(table),
                        "example")); //$NON-NLS-1$
        method.addBodyLine("super(example);"); //$NON-NLS-1$
        method.addBodyLine("this.record = record;"); //$NON-NLS-1$
        answer.addMethod(method);
        
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(FullyQualifiedJavaType.getObjectInstance());
        field.setName("record"); //$NON-NLS-1$
        answer.addField(field);
        
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getObjectInstance());
        method.setName("getRecord"); //$NON-NLS-1$
        method.addBodyLine("return record;"); //$NON-NLS-1$
        answer.addMethod(method);
        
        return answer;
    }

    protected List<Method> getUpdateByExampleWithBLOBsMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {
        FullyQualifiedTable table = introspectedTable.getTable();
        FullyQualifiedJavaType parameterType; 
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            parameterType = javaModelGenerator.getRecordWithBLOBsType(table);
        } else {
            parameterType = javaModelGenerator.getBaseRecordType(table);
        }

        compilationUnit.addImportedType(parameterType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(methodNameCalculator.getUpdateByExampleWithBLOBsMethodName(introspectedTable));
        method.addParameter(new Parameter(parameterType, "record")); //$NON-NLS-1$
        method.addParameter(new Parameter(javaModelGenerator.getExampleType(table), "example")); //$NON-NLS-1$

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);

        if (!interfaceMethod) {
            // generate the implementation method
            method.addBodyLine("UpdateByExampleParms parms = new UpdateByExampleParms(record, example);"); //$NON-NLS-1$
            
            StringBuffer sb = new StringBuffer();

            sb.append("int rows = "); //$NON-NLS-1$
            sb.append(daoTemplate.getUpdateMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getUpdateByExampleWithBLOBsStatementId(), "parms")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());

            method.addBodyLine("return rows;"); //$NON-NLS-1$
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }

    protected List<Method> getUpdateByExampleWithoutBLOBsMethods(
            IntrospectedTable introspectedTable, boolean interfaceMethod,
            CompilationUnit compilationUnit) {

        FullyQualifiedTable table = introspectedTable.getTable();
        FullyQualifiedJavaType parameterType; 
        if (introspectedTable.getRules().generateBaseRecordClass()) {
            parameterType = javaModelGenerator.getBaseRecordType(table);
        } else {
            parameterType = javaModelGenerator.getPrimaryKeyType(table);
        }

        compilationUnit.addImportedType(parameterType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(methodNameCalculator.getUpdateByExampleWithoutBLOBsMethodName(introspectedTable));
        method.addParameter(new Parameter(parameterType, "record")); //$NON-NLS-1$
        method.addParameter(new Parameter(javaModelGenerator.getExampleType(table), "example")); //$NON-NLS-1$

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            compilationUnit.addImportedType(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method, table);

        if (!interfaceMethod) {
            // generate the implementation method
            method.addBodyLine("UpdateByExampleParms parms = new UpdateByExampleParms(record, example);"); //$NON-NLS-1$
            
            StringBuffer sb = new StringBuffer();

            sb.append("int rows = "); //$NON-NLS-1$
            sb.append(daoTemplate.getUpdateMethod(sqlMapGenerator
                    .getSqlMapNamespace(table), sqlMapGenerator
                    .getUpdateByExampleStatementId(), "parms")); //$NON-NLS-1$
            method.addBodyLine(sb.toString());

            method.addBodyLine("return rows;"); //$NON-NLS-1$
        }

        List<Method> answer = new ArrayList<Method>();
        answer.add(method);

        return answer;
    }
}
