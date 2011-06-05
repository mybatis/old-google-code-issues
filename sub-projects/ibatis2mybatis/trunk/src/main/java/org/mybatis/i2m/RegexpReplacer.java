/*
 *    Copyright 2010 The myBatis Team
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
package org.mybatis.i2m;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version $Id$
 */
public final class RegexpReplacer
{

    private static final RegexpReplacer INSTANCE = new RegexpReplacer();

    private static final String JDBC_TYPE_PREFIX = ",jdbcType=";

    private static final int PROPERTY_MARKER = 1;

    private static final int PROPERTY_NAME = 2;

    private static final int PROPERTY_JDBC_TYPE = 3;

    public static String replace( String text )
    {
        return INSTANCE.covertProperties( text );
    }

    private final Pattern propertyPattern = Pattern.compile( "([\\$#])([a-zA-Z0-9.\\[\\]_]+)(?::([A-Z]+))?\\1",
                                                             Pattern.MULTILINE );

    /**
     * This class can't be instantiated
     */
    private RegexpReplacer()
    {
        // do nothing
    }

    private String covertProperties( String text )
    {
        Matcher matcher = this.propertyPattern.matcher( text );

        StringBuffer result = new StringBuffer();
        while ( matcher.find() )
        {
            StringBuilder replacement =
                new StringBuilder( Matcher.quoteReplacement( matcher.group( PROPERTY_MARKER ) ) ).append( '{' ).append( matcher.group( PROPERTY_NAME ) );

            String jdbcType = matcher.group( PROPERTY_JDBC_TYPE );
            if ( jdbcType != null && jdbcType.length() != 0 )
            {
                replacement.append( JDBC_TYPE_PREFIX ).append( jdbcType );
            }

            replacement.append( '}' );

            matcher.appendReplacement( result, replacement.toString() );
        }
        matcher.appendTail( result );

        return result.toString();
    }

}
