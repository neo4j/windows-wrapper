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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.LogManager;

/**
 * The logging service is mainly responsible for doing two things:
 * <ul>
 * <li>    1) Reset the value of property {@code java.util.logging.FileHandler.pattern} (which is defined in
 * {@code windows-wrapper-logging.properties} file) to an absolute path if it is given as a relative path.
 * The absolute path uses our default working directory {@code %NEO4J_HOME%} as its parent directory.
 * <li>    2) Create directories if they do not exist before for the path specified by
 * {@code java.util.logging.FileHandler.pattern}.
 * </ul>
 *
 * The property {@code java.util.logging.FileHandler.pattern} defines where the windows-wrapper logs are stored.
 * If it is given in a relative path, the {@code java.util.logging} will use the place where the program runs as
 * the parent path. However by default, we assume that the parent directory should always be our working directory
 * {@code %NEO4J_HOME%}. Therefore we need to reset the relative path to a full path to avoid logs being created in
 * an unexpected place.
 *
 * Besides we also need to create missing directories on this path to avoid
 * {@link java.util.logging.FileHandler#openFiles IO exceptions}.
 * <p>
 * The value of property {@code java.util.logging.FileHandler.pattern} uses a special pattern format. More info
 * about the pattern format could be found in {@link java.util.logging.FileHandler FileHandler}
 */
public class LoggingService
{
    static final String LOGGING_CONFIG_FILE_KEY = "java.util.logging.config.file";
    static final String LOGGING_FILE_NAME_PATTERN_KEY = "java.util.logging.FileHandler.pattern";

    private static final String DEFAULT_LOGGING_CONFIG_FILE_PATH = getDefaultLoggingConfigFilePath();
    private String winWrapperLogNamePattern;

    public LoggingService()
    {
    }

    // only for testing
    LoggingService( String pattern )
    {
        this.winWrapperLogNamePattern = pattern;
    }

    public void initLogger() throws IOException
    {
        LogManager logManager = LogManager.getLogManager();
        winWrapperLogNamePattern = logManager.getProperty( LOGGING_FILE_NAME_PATTERN_KEY );

        // Reset the pattern property if is relative
        if ( namePatternIsRelative() )
        {
            resetLogNamePatternProperty( logManager );
        }

        // Retrieve the logging directory from the pattern property and create the directory if it does not exist.
        File logDir = getLogDir();
        if ( !logDir.exists() )
        {
            logDir.mkdirs();
        }
    }

    private boolean namePatternIsRelative()
    {
        boolean isInTempDir = winWrapperLogNamePattern.startsWith( "%h" );
        boolean isInUserHomeDir = winWrapperLogNamePattern.startsWith( "%t" );
        boolean isRelative = !new File( winWrapperLogNamePattern ).isAbsolute();
        return !isInTempDir && !isInUserHomeDir && isRelative;
    }

    /**
     * Change the value of property {@code java.util.logging.FileHandler.pattern} to use {@code %NEO4J_HOME%} as parent
     * directory and call {@link java.util.logging.LogManager LogManager} to reload the change.
     * <p>
     * By invoking this method, we ensure no matter where a user starts NEO4J, we will always use {@code %NEO4J_HOME%}
     * as parent directory for windows-wrapper logs. Otherwise, we will use the directory where the program
     * starts as the parent directory when creating windows-wrapper logs.
     * @param logManager
     * @throws IOException if we failed to read from the windows-wrapper property file or update configuration
     * properties for {@link java.util.logging.LogManager LogManager}
     */
    void resetLogNamePatternProperty( LogManager logManager )
            throws IOException
    {
        // Load all the properties from configuration file
        String logConfigFileName = System.getProperty( LOGGING_CONFIG_FILE_KEY, DEFAULT_LOGGING_CONFIG_FILE_PATH );
        try ( FileInputStream logConfigIn = new FileInputStream( new File( logConfigFileName ) ) )
        {
            Properties logProperties = new Properties();
            logProperties.load( logConfigIn );

            // Reset the logFileNamePattern to use %NEO4J_HOME% as its parent folder
            String workingDir = System.getProperty( ServerProcess.WorkingDir );
            workingDir = workingDir.replaceAll( "%", "%%" ); // translate to the format used by FileHandler
            winWrapperLogNamePattern = new File( workingDir, winWrapperLogNamePattern ).getAbsolutePath();

            // Reload the new change
            logProperties.setProperty( LOGGING_FILE_NAME_PATTERN_KEY, winWrapperLogNamePattern );
            ByteArrayOutputStream internalOut = new ByteArrayOutputStream();
            logProperties.store( internalOut, null );
            logManager.readConfiguration( new ByteArrayInputStream( internalOut.toByteArray() ) );
        }
    }

    /**
     * This method returns the directory in which the windows-wrapper logs will be generated. The path to this directory
     * could be retrieved from the property {@code java.util.logging.FileHandler.pattern}. However the property
     * utilizes a special path pattern.
     * The pattern consists of some special components that will be replaced at runtime by
     * {@link java.util.logging.FileHandler FileHandler}:
     * <ul>
     * <li>    "/"    the local pathname separator
     * <li>     "%t"   the system temporary directory
     * <li>     "%h"   the value of the "user.home" system property
     * <li>     "%g"   the generation number to distinguish rotated logs
     * <li>     "%u"   a unique number to resolve conflicts
     * <li>     "%%"   translates to a single percent sign "%"
     * </ul>
     *
     * This method will throw {@link java.io.IOException IOException} if the directory path contains components
     * in wrong positions:
     *
     * <ul>
     * <li>     having "%t" or "%h" in the middle of the directory path
     * <li>     having "%g" or "%u" in the directory path
     * </ul>
     **/
    File getLogDir() throws IOException
    {
        String logDirNamePattern = new File( winWrapperLogNamePattern ).getParent();
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
                                    + new File( winWrapperLogNamePattern ).getParent() + "; Please change \""
                                    + LOGGING_FILE_NAME_PATTERN_KEY + "=" + winWrapperLogNamePattern + "\"" );
                case 't':
                case 'h':
                    throw new IOException( "Cannot understand %t or %h in the middle of a path: " + winWrapperLogNamePattern
                            + "; Please change \"" + LOGGING_FILE_NAME_PATTERN_KEY + "=" + winWrapperLogNamePattern + "\"" );
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
