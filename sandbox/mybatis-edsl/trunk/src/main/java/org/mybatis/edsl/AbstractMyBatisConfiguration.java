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

import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.ExecutorType;

public abstract class AbstractMyBatisConfiguration
    implements MyBatisConfiguration
{

    private Configurator configurator;

    public final void configure( Configurator configurator )
    {
        if ( configurator != null )
        {
            throw new IllegalStateException( "Re-entry cannot be allowed" );
        }

        this.configurator = configurator;

        try
        {
            configure();
        }
        catch ( Throwable t )
        {
            this.configurator = null;
        }
    }

    public abstract void configure();

    // properties configuration

    protected final PropertySetter setProperty( String name )
    {
        return configurator.setProperty( name );
    }

    protected final void lazyLoadingEnabled( boolean lazyLoadingEnabled )
    {
        configurator.lazyLoadingEnabled( lazyLoadingEnabled );
    }

    protected final void aggressiveLazyLoading( boolean aggressiveLazyLoading )
    {
        configurator.aggressiveLazyLoading( aggressiveLazyLoading );
    }

    protected final void multipleResultSetsEnabled( boolean multipleResultSetsEnabled )
    {
        configurator.multipleResultSetsEnabled( multipleResultSetsEnabled );
    }

    protected final void useGeneratedKeys( boolean useGeneratedKeys )
    {
        configurator.useGeneratedKeys( useGeneratedKeys );
    }

    protected final void useColumnLabel( boolean useColumnLabel )
    {
        configurator.useColumnLabel( useColumnLabel );
    }

    protected final void useCacheEnabled(boolean useCacheEnabled)
    {
        configurator.useCacheEnabled( useCacheEnabled );
    }

    protected final void failFast(boolean failFast)
    {
        configurator.failFast( failFast );
    }

    // session

    protected final void executorType( ExecutorType executorType)
    {
        configurator.executorType( executorType );
    }

    protected final void autoMappingBehavior( AutoMappingBehavior autoMappingBehavior )
    {
        configurator.autoMappingBehavior( autoMappingBehavior );
    }

    // type aliases

    protected final void simpleAlias( Class<?> javaType )
    {
        configurator.simpleAlias( javaType );
    }

    protected final AliasBinder alias( String alias )
    {
        return configurator.alias( alias );
    }

    // type handlers

    protected final <T> TypeHandlerBinder<T> handleJavaType( Class<T> type )
    {
        return configurator.handleJavaType( type );
    }

}
