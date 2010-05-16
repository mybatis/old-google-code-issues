/*
 * Copyright 2005 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.ibatis.abator.api;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.abator.config.FullyQualifiedTable;
import org.apache.ibatis.abator.config.TableConfiguration;
import org.apache.ibatis.abator.internal.db.ColumnDefinitions;

public interface SqlMapGenerator {
    void setProperties(Map properties);

    void setTargetPackage(String targetPackage);

    void setTargetProject(String targetProject);

    void setJavaModelGenerator(JavaModelGenerator javaModelGenerator);

    String getSqlMapNamespace(FullyQualifiedTable table);

    String getInsertStatementId();

    String getUpdateByPrimaryKeyWithBLOBsStatementId();

    String getUpdateByPrimaryKeyStatementId();

    String getDeleteByPrimaryKeyStatementId();

    String getDeleteByExampleStatementId();

    String getSelectByPrimaryKeyStatementId();

    String getSelectByExampleStatementId();

    String getSelectByExampleWithBLOBsStatementId();

    List getGeneratedXMLFiles(ColumnDefinitions columnDefinitions,
            TableConfiguration tableConfiguration, ProgressCallback callback);
}
