/**
 * Copyright (c) 2002-2015 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.wrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class LoggingService
{
    private static final String LOGGING_CONFIG_FILE_KEY = "java.util.logging.config.file";
    private static final String DEFAULT_LOGGING_CONFIG_FILE_PATH = getDefaultLoggingConfigFilePath();
    private static final String LOGGING_FILE_NAME_PATTERN_KEY = "java.util.logging.FileHandler.pattern";

    public static void initLogger() throws FileNotFoundException, IOException
    {
        // Load all the properties from logging.properties
        String logConfigFileName = System.getProperty( LOGGING_CONFIG_FILE_KEY, DEFAULT_LOGGING_CONFIG_FILE_PATH );
        Properties logProperties = new Properties();
        logProperties.load( new FileInputStream( new File( logConfigFileName ) ) );

        // Find the logging directory and create the directory if not exists.
        String logFileNamePattern = logProperties.getProperty( LOGGING_FILE_NAME_PATTERN_KEY );
        File logDir = getLogDir( logFileNamePattern );

        if ( !logDir.exists() )
        {
            logDir.mkdirs();
        }
    }

    /**
     * The input pattern is used by the {@link java.util.logging.FileHandler} to generate log files.
     * The input pattern consists of some special components that will be replaced at runtime by the FileHandler class:
     * <ul>
     * <li>    "/"    the local pathname separator
     * <li>     "%t"   the system temporary directory
     * <li>     "%h"   the value of the "user.home" system property
     * <li>     "%g"   the generation number to distinguish rotated logs
     * <li>     "%u"   a unique number to resolve conflicts
     * <li>     "%%"   translates to a single percent sign "%"
     * </ul>
     *
     * This method will return a file representing the parent directory of the input pattern.
     * However, this method will throw {@link java.io.IOException} if the directory contains components in wrong positions:
     *
     * <ul>
     * <li>     having "%t" or "%h" in the middle of the directory path
     * <li>     having "%g" or "%u" in the directory path
     * </ul>
     **/
    protected static File getLogDir( String logFileNamePattern ) throws IOException
    {
        String logDirNamePattern = new File( logFileNamePattern ).getParent();
        String logDirParentDirName = null;

        //
        if ( logDirNamePattern.startsWith( "%t" ) )
        {
            String tmpDirName = System.getProperty( "java.io.tmpdir" );
            if ( tmpDirName == null )
            {
                tmpDirName = System.getProperty( "user.home" );
            }
            logDirParentDirName = tmpDirName;
            logDirNamePattern = logDirNamePattern.substring( 2 );
        }
        else if ( logDirNamePattern.startsWith( "%h" ) )
        {
            logDirParentDirName = System.getProperty( "user.home" );
            logDirNamePattern = logDirNamePattern.substring( 2 );
        }

        boolean translationSymbolFound = false;
        for ( int i = 0; i < logDirNamePattern.length(); i++ )
        {
            char ch = logDirNamePattern.charAt( i );
            if ( ch == '%' )
            {
                char nextCh = logDirNamePattern.charAt( i + 1 );
                switch ( nextCh )
                {
                case '%':
                    i++;
                    translationSymbolFound = true;
                    break;
                case 'u':
                case 'g':
                    throw new IOException(
                            "Cannot make directories automatically for directory paths containing %u or %g: "
                                    + new File( logFileNamePattern ).getParent() + "; Please change \""
                                    + LOGGING_FILE_NAME_PATTERN_KEY + "=" + logFileNamePattern + "\"" );
                case 't':
                case 'h':
                    throw new IOException( "Cannot understand %t or %h in the middle of a path: " + logFileNamePattern
                            + "; Please change \"" + LOGGING_FILE_NAME_PATTERN_KEY + "=" + logFileNamePattern + "\"" );
                }
            }
        }
        if ( translationSymbolFound )
        {
            logDirNamePattern = logDirNamePattern.replaceAll( "%%", "%" );
        }
        return new File( logDirParentDirName, logDirNamePattern );
    }

    private static String getDefaultLoggingConfigFilePath()
    {
        //Default: JDK_HOME/jre/lib/logging.properties
        String javaHome = System.getProperty( "java.home" );
        return new File( javaHome, "/lib/logging.properties" ).getAbsolutePath();
    }
}
