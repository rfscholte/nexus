/**
 * Sonatype Nexus (TM) Open Source Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://nexus.sonatype.org/dev/attributions.html
 * This program is licensed to you under Version 3 only of the GNU General Public License as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License Version 3 for more details.
 * You should have received a copy of the GNU General Public License Version 3 along with this program.
 * If not, see http://www.gnu.org/licenses/.
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.nexus.test.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class TestProperties
{

    private static Properties contextProperties;

    private static ThreadLocal<Properties> runtimeProperties = new ThreadLocal<Properties>();

    // i hate doing this, but its really easy, and this is for tests. this class can be replaced easy, if we need to...
    static
    {
        contextProperties = new Properties();
        try
        {
            contextProperties.load( TestProperties.class.getResourceAsStream( "/baseTest.properties" ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static String getString( String key )
    {
        String value = contextProperties.getProperty( key );
        if ( value == null && runtimeProperties.get() != null )
        {
            value = runtimeProperties.get().getProperty( key );
        }
        return value;
    }

    public static Integer getInteger( String key )
    {
        String value = getString( key );
        return new Integer( value );
    }

    public static Integer getInteger( String key, Integer defaultValue )
    {
        String value = getString( key );
        if ( value == null )
        {
            return defaultValue;
        }
        return new Integer( value );
    }

    public static Boolean getBoolean( String key )
    {
        String value = getString( key );
        return Boolean.parseBoolean( value );
    }

    public static Map<String, String> getAll()
    {
        Map<String, String> properties = new LinkedHashMap<String, String>();
        Set<String> keys = contextProperties.stringPropertyNames();
        for ( String key : keys )
        {
            properties.put( key, contextProperties.getProperty( key ) );
        }

        Properties runProps = runtimeProperties.get();
        if ( runProps != null )
        {
            keys = runProps.stringPropertyNames();
            for ( String key : keys )
            {
                if ( properties.containsKey( key ) )
                {
                    properties.remove( key );
                }
                properties.put( key, runProps.getProperty( key ) );
            }
        }

        return properties;
    }

    public static File getFile( String key )
    {
        return new File( getString( key ) );
    }

    public static String getPath( String key, String extraPath )
    {
        File file = new File( getFile( key ), extraPath );
        try
        {
            return file.getCanonicalPath();
        }
        catch ( IOException e )
        {
            return file.getAbsolutePath();
        }
    }

    public static String getPath( String key )
    {
        File file = getFile( key );
        try
        {
            return file.getCanonicalPath();
        }
        catch ( IOException e )
        {
            return file.getAbsolutePath();
        }
    }

    public static void setRuntimeProperties( Properties props )
    {
        runtimeProperties.set( props );
    }

}
