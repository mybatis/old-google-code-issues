package org.apache.ibatis.migration.commands;

import java.math.BigDecimal;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

public final class VersionConverterValidator
    implements IParameterValidator, IStringConverter<BigDecimal>
{

    public BigDecimal convert( String value )
    {
        return new BigDecimal( value );
    }

    public void validate( String name, String value )
        throws ParameterException
    {
        try {
          new BigDecimal( value );
        } catch (Exception e) {
          throw new ParameterException( "The version number must be a numeric integer." );
        }
    }

}
