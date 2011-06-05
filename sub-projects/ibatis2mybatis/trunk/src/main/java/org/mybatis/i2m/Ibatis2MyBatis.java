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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.beust.jcommander.JCommander;

/**
 * @version $Id$
 */
public final class Ibatis2MyBatis
{

    private final StreamSource stylesheet;

    private final ExecutorService executors;

    private final XMLFilter xmlFilter = new XMLFilter();

    private final TransformerFactory saxTransformerFactory;

    private Ibatis2MyBatis( final int threadPoolSize )
    {
        this.executors = Executors.newFixedThreadPool( threadPoolSize );
        this.stylesheet = new StreamSource( this.getClass().getResource( "sqlMap2mapper.xslt" ).toString() );

        this.saxTransformerFactory = TransformerFactory.newInstance();
        this.saxTransformerFactory.setAttribute( "translet-name", "SqlMap2Mapper" );
        this.saxTransformerFactory.setAttribute( "package-name", "org.mybatis.i2m" );
    }

    private void transform( File sqlMapFile, File destination )
        throws Exception
    {
        if ( sqlMapFile.isDirectory() )
        {
            for ( File file : sqlMapFile.listFiles( this.xmlFilter ) )
            {
                this.transform( file, destination );
            }
            return;
        }

        File dest = destination.isDirectory() ? new File( destination, sqlMapFile.getName() ) : destination;
        Transformer transformer = this.saxTransformerFactory.newTransformer( this.stylesheet );
        this.executors.execute( new XsltProcessor( sqlMapFile, dest, transformer ) );
    }

    public static void main( String[] args )
        throws Exception
    {
        Options config = new Options();
        JCommander commander = new JCommander( config, args );

        if ( config.isHelp() || config.getSource() == null || config.getDest() == null )
        {
            commander.usage();
            System.exit( 0 );
        }

        if ( config.isPrintVersion() )
        {
            Properties properties = new Properties();
            InputStream input =
                Ibatis2MyBatis.class.getClassLoader().getResourceAsStream( "META-INF/maven/org.mybatis/ibatis2mybatis/pom.properties" );

            if ( input != null )
            {
                try
                {
                    properties.load( input );
                }
                catch ( IOException e )
                {
                    // ignore, just don't load the properties
                }
                finally
                {
                    try
                    {
                        input.close();
                    }
                    catch ( IOException e )
                    {
                        // close quietly
                    }
                }
            }

            System.out.printf( "iBATIS to MyBatis %s (%s)%n", properties.getProperty( "version" ),
                               properties.getProperty( "build" ) );
            System.out.printf( "Java version: %s, vendor: %s%n", System.getProperty( "java.version" ),
                               System.getProperty( "java.vendor" ) );
            System.out.printf( "Java home: %s%n", System.getProperty( "java.home" ) );
            System.out.printf( "Default locale: %s_%s, platform encoding: %s%n", System.getProperty( "user.language" ),
                               System.getProperty( "user.country" ), System.getProperty( "sun.jnu.encoding" ) );
            System.out.printf( "OS name: \"%s\", version: \"%s\", arch: \"%s\", family: \"%s\"%n",
                               System.getProperty( "os.name" ), System.getProperty( "os.version" ),
                               System.getProperty( "os.arch" ), getOsFamily() );

            System.exit( -1 );
        }

        if ( !config.getSource().exists() )
        {
            System.out.println( "-s --source must be an existing dir/XML file" );
            System.exit( -1 );
        }

        // logging stuff
        final Logger logger = LoggerFactory.getLogger( Ibatis2MyBatis.class );

        // assume SLF4J is bound to logback in the current environment
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        try
        {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext( lc );
            // the context was probably already configured by default configuration
            // rules
            lc.reset();
            configurator.doConfigure( Ibatis2MyBatis.class.getClassLoader().getResourceAsStream( "logback-config.xml" ) );
        }
        catch ( JoranException je )
        {
            // StatusPrinter should handle this
        }

        config.getDest().mkdirs();

        Ibatis2MyBatis ibatis2MyBatis = new Ibatis2MyBatis( config.getThreads() );
        ibatis2MyBatis.transform( config.getSource(), config.getDest() );
    }

    private static final String getOsFamily()
    {
        String osName = System.getProperty( "os.name" ).toLowerCase();
        String pathSep = System.getProperty( "path.separator" );

        if ( osName.indexOf( "windows" ) != -1 )
        {
            return "windows";
        }
        else if ( osName.indexOf( "os/2" ) != -1 )
        {
            return "os/2";
        }
        else if ( osName.indexOf( "z/os" ) != -1 || osName.indexOf( "os/390" ) != -1 )
        {
            return "z/os";
        }
        else if ( osName.indexOf( "os/400" ) != -1 )
        {
            return "os/400";
        }
        else if ( pathSep.equals( ";" ) )
        {
            return "dos";
        }
        else if ( osName.indexOf( "mac" ) != -1 )
        {
            if ( osName.endsWith( "x" ) )
            {
                return "mac"; // MACOSX
            }
            return "unix";
        }
        else if ( osName.indexOf( "nonstop_kernel" ) != -1 )
        {
            return "tandem";
        }
        else if ( osName.indexOf( "openvms" ) != -1 )
        {
            return "openvms";
        }
        else if ( pathSep.equals( ":" ) )
        {
            return "unix";
        }

        return "undefined";
    }

}
