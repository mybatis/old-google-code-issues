/*
 *    Copyright 2010-2011 The myBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.edsl;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.ExecutorType;

public interface Configurator
{

    // properties configuration

    PropertySetter setProperty( String name );

    void lazyLoadingEnabled( boolean lazyLoadingEnabled );

    void aggressiveLazyLoading( boolean aggressiveLazyLoading );

    void multipleResultSetsEnabled( boolean multipleResultSetsEnabled );

    void useGeneratedKeys( boolean useGeneratedKeys );

    void useColumnLabel( boolean useColumnLabel );

    void useCacheEnabled(boolean useCacheEnabled);

    void failFast(boolean failFast);

    // data source

    void useDataSource( DataSource dataSource );

    void useDataSource( DataSourceFactory dataSourceFactory );

    <DSF extends DataSourceFactory> void useDataSource( Class<DSF> dataSourceFactoryType );

    // session

    void executorType( ExecutorType executorType);

    void autoMappingBehavior( AutoMappingBehavior autoMappingBehavior );

    // type aliases

    void simpleAlias( Class<?> javaType );

    AliasBinder alias( String alias );

    // type handlers

    <T> TypeHandlerBinder<T> handleJavaType( Class<T> type );

}
