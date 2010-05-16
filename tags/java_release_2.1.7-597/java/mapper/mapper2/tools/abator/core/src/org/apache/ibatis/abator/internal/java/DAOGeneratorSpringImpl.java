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
package org.apache.ibatis.abator.internal.java;

import org.apache.ibatis.abator.api.FullyQualifiedJavaType;

/**
 * This class generates DAO classes based on the Spring Framework DAO support.
 * 
 * @author Jeff Butler
 */
public class DAOGeneratorSpringImpl extends DAOGeneratorBaseImpl {
    /**
     *  
     */
    public DAOGeneratorSpringImpl() {
        super();
    }
    
    /* (non-Javadoc)
     * @see org.apache.ibatis.abator.internal.java.DAOGeneratorBaseImpl#getDAOGeneratorTemplate()
     */
    public DAOGeneratorTemplate getDAOGeneratorTemplate() {
        DAOGeneratorTemplate template = new DAOGeneratorTemplate();
        
        StringBuffer sb = new StringBuffer();
        indent(sb, 1);
        sb.append("public {0}() '{'");
        newLine(sb);
        indent(sb, 2);
        sb.append("super();");
        newLine(sb);
        indent(sb, 1);
        sb.append("}");
        template.setConstructorTemplate(sb.toString());

        template
                .setSuperClass(new FullyQualifiedJavaType(
                        "org.springframework.orm.ibatis.support.SqlMapClientDaoSupport")); //$NON-NLS-1$

        template.setDeleteMethod("getSqlMapClientTemplate().delete"); //$NON-NLS-1$
        template.setInsertMethod("getSqlMapClientTemplate().insert"); //$NON-NLS-1$
        template
                .setQueryForObjectMethod("getSqlMapClientTemplate().queryForObject"); //$NON-NLS-1$
        template
                .setQueryForListMethod("getSqlMapClientTemplate().queryForList"); //$NON-NLS-1$
        template.setUpdateMethod("getSqlMapClientTemplate().update"); //$NON-NLS-1$
        
        return template;
    }
}
