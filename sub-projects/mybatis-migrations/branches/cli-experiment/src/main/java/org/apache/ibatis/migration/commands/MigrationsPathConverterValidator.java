/*
 *    Copyright 2009-2011 The MyBatis Team
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
package org.apache.ibatis.migration.commands;

import static java.lang.String.format;

import java.io.File;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

public final class MigrationsPathConverterValidator
    implements IParameterValidator, IStringConverter<File>
{

    public File convert( String value )
    {
        return new File( value );
    }

    public void validate( String name, String value )
        throws ParameterException
    {
        File directory = convert( value );

        if ( !directory.exists() )
        {
            throw new ParameterException( format( "Migrations path '%s' does not exists", value ) );
        }

        if ( !directory.isDirectory() )
        {
            throw new ParameterException( format( "Migrations path '%s' must be a directory", value ) );
        }
    }

}
