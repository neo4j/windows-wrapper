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

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.LogManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LoggingServiceTest
{
    private static final File tempDir = new File( "/tmpDir" );
    private static final File userHomeDir = new File( "/userHome" );

    @Before
    public void setup()
    {
        System.setProperty( "java.io.tmpdir", tempDir.getAbsolutePath() );
        System.setProperty( "user.home", userHomeDir.getAbsolutePath() );
    }

    @Test
    public void shouldUseSystemTempDirAsParentFolder() throws Exception
    {
        // Given
        String namePattern = platformize( "%t/a/b.log" );
        // When
        File logDir = new LoggingService( namePattern ).getLogDir();
        // Then
        assertEquals( new File( tempDir, "a" ).getAbsolutePath(), logDir.getAbsolutePath() );
    }

    private String platformize( String string )
    {
        return string.replace( '/', File.separatorChar );
    }

    @Test
    public void shouldUseUserHomeDirAsParentFolder() throws Exception
    {
        // Given
        String namePattern = platformize( "%h/a/b.log" );
        // When
        File logDir = new LoggingService( namePattern ).getLogDir();
        // Then
        assertEquals( new File( userHomeDir, "a" ).getAbsolutePath(), logDir.getAbsolutePath() );
    }

    @Test
    public void shouldThrowIOExceptionForDirPathsConstaningIllegalSymbols() throws Exception
    {
        // Given
        Assume.assumeTrue( System.getProperty( "os.name" ).contains( "Windows" ) );
        String legalNamePattern1 = "C:\\a\\b%u%g.log"; // the file name could contains %u or %g
        String legalNamePattern2 = "C:\\a%%%%u\\b.log"; // as long as the count of % is even, the name is okay

        String illegalNamePattern1 = "a%%%%%u\\b.log"; // if the count of % is odd, the name is wrong
        String illegalNamePattern2 = "a%%%%%g\\b.log";
        String illegalNamePattern3 = "a\\%%%t\\b.log"; // should not contain %t or %h in the middle of a folder name
        String illegalNamePattern4 = "a\\%%%h\\b.log";

        // When & Then
        assertEquals( "C:\\a", new LoggingService( legalNamePattern1 ).getLogDir().getAbsolutePath() );
        assertEquals( "C:\\a%%u", new LoggingService( legalNamePattern2 ).getLogDir().getAbsolutePath() );

        try
        {
            new LoggingService( illegalNamePattern1 ).getLogDir();
            fail( "Should not handle directory names containing %u or %g: " + illegalNamePattern1 );
        }
        catch( IOException e )
        {
            assertTrue( e.toString().contains(
                    "Cannot make directories automatically for directory paths containing %u or %g" ) );
        }
        try
        {
            new LoggingService( illegalNamePattern2 ).getLogDir();
            fail( "Should not handle directory names containing %u or %g: " + illegalNamePattern1 );
        }
        catch ( IOException e )
        {
            assertTrue( e.toString().contains(
                    "Cannot make directories automatically for directory paths containing %u or %g" ) );
        }
        try
        {
            new LoggingService( illegalNamePattern3 ).getLogDir();
            fail( "Should not handle directory names containing %u or %g: " + illegalNamePattern1 );
        }
        catch ( IOException e )
        {
            assertTrue( e.toString().contains( "Cannot understand %t or %h in the middle of a path" ) );
        }
        try
        {
            new LoggingService( illegalNamePattern4 ).getLogDir();
            fail( "Should not handle directory names containing %u or %g: " + illegalNamePattern1 );
        }
        catch ( IOException e )
        {
            assertTrue( e.toString().contains( "Cannot understand %t or %h in the middle of a path" ) );
        }
    }

    @Test
    public void shouldResetLogNamePattern() throws Exception
    {
        // Given
        String workingDir = new File( "/%NEO4J_HOME%" ).getAbsolutePath();
        String pattern = platformize( "data/log/windows-wrapper.%u.%g.log" );

        // A property config file with several properties in it
        File configFile = new File( "windows-wrapper.properties" );
        Properties properties = new Properties();
        properties.setProperty( LoggingService.LOGGING_FILE_NAME_PATTERN_KEY, pattern );
        properties.setProperty( "A_KEY", "value" );
        try ( BufferedWriter out = new BufferedWriter( new FileWriter( configFile ) ) )
        {
            properties.store( out, null );
        }

        System.setProperty( LoggingService.LOGGING_CONFIG_FILE_KEY, configFile.getPath() );
        System.setProperty( ServerProcess.WorkingDir, workingDir );

        // When
        final Properties newProperties = new Properties();
        new LoggingService( pattern ).resetLogNamePatternProperty( new LogManager()
        {
            @Override
            public void readConfiguration( InputStream ins ) throws IOException, SecurityException
            {
                newProperties.load( ins );
            }
        } );

        // Then
        String expectedPattern = new File( "/%%NEO4J_HOME%%", pattern ).getAbsolutePath();
        assertEquals( expectedPattern, newProperties.getProperty( LoggingService.LOGGING_FILE_NAME_PATTERN_KEY ) );
        assertEquals( "value", newProperties.getProperty( "A_KEY" ) );
        configFile.deleteOnExit();
    }
}
