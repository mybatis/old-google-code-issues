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

import java.sql.Timestamp;

import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.SqlTimestampTypeHandler;
import org.mybatis.edsl.AbstractMyBatisConfiguration;
import org.mybatis.edsl.Configurator;
import org.mybatis.edsl.MyBatisConfiguration;

public class EDSLTestCase
{

    public void bindHandlerTypeViaConfigurator()
    {
        new MyBatisConfiguration()
        {

            public void configure( Configurator configurator )
            {
                // properties configuration
                configurator.setProperty( "username" ).withValue( "dev_user" );
                configurator.setProperty( "password" ).withValue( "F2Fa3!33TYyg" );

                configurator.lazyLoadingEnabled( true );
                configurator.aggressiveLazyLoading( true );
                configurator.multipleResultSetsEnabled( true );
                configurator.useGeneratedKeys( true );
                configurator.useColumnLabel( true );
                configurator.useCacheEnabled( true );
                configurator.failFast( true );

                // session
                configurator.executorType( ExecutorType.BATCH );
                configurator.autoMappingBehavior( AutoMappingBehavior.PARTIAL );

                // type aliases
                configurator.simpleAlias( User.class );
                configurator.handleJavaType( Timestamp.class ).ofJdbcType( JdbcType.TIMESTAMP ).withHandler( SqlTimestampTypeHandler.class );
            }

        };
    }

    public void bindHandlerTypeViaAbstractConfiguration()
    {
        new AbstractMyBatisConfiguration()
        {

            public void configure()
            {
                // properties configuration
                setProperty( "username" ).withValue( "dev_user" );
                setProperty( "password" ).withValue( "F2Fa3!33TYyg" );

                lazyLoadingEnabled( true );
                aggressiveLazyLoading( true );
                multipleResultSetsEnabled( true );
                useGeneratedKeys( true );
                useColumnLabel( true );
                useCacheEnabled( true );
                failFast( true );

                // session
                executorType( ExecutorType.BATCH );
                autoMappingBehavior( AutoMappingBehavior.PARTIAL );

                // type aliases
                simpleAlias( User.class );
                handleJavaType( Timestamp.class ).ofJdbcType( JdbcType.TIMESTAMP ).withHandler( SqlTimestampTypeHandler.class );
            }

        };
    }

}
