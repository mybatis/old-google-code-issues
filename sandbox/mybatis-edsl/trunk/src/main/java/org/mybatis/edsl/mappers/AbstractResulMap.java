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
package org.mybatis.edsl.mappers;

public abstract class AbstractResulMap<T>
    implements ResultMap<T>
{

    private ResultMapper mapper;

    public final void configure( ResultMapper mapper, T mapped )
    {
        if ( this.mapper != null )
        {
            throw new IllegalStateException( "re-entry is not allowed" );
        }

        try
        {
            configure( mapped );
        }
        finally
        {
            this.mapper = null;
        }
    }

    protected abstract void configure( T mapped );

    protected final <E> ToColumnLinkedBuilder<E> fromColumn( String columnName )
    {
        return mapper.fromColumn( columnName );
    }

    protected final <M> M usingMapper( Class<M> mapperClass )
    {
        return mapper.usingMapper( mapperClass );
    }

}
