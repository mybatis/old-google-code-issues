package org.apache.ibatis.migration.commands;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public final class DescriptionValidator
    implements IParameterValidator
{

    public void validate( String name, String value )
        throws ParameterException
    {
        if ( value == null || value.length() == 0 )
        {
            throw new ParameterException( "No description specified for new migration." );
        }
    }

}
