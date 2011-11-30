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
