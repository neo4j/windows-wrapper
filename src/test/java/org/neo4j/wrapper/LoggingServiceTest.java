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
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LoggingServiceTest
{
    @Before
    public void setup()
    {
        System.setProperty( "java.io.tmpdir", "C:\\tmpDir" );
        System.setProperty( "user.home", "C:\\userHome" );
    }

    @Test
    public void shouldUseSystemTempDirAsParentFolder() throws Exception
    {
        // Given
        String namePattern = "%t\\a\\b.log";
        // When
        File logDir = LoggingService.getLogDir( namePattern );
        // Then
        assertEquals( "C:\\tmpDir\\a", logDir.getAbsolutePath() );
    }

    @Test
    public void shouldUseUserHomeDirAsParentFolder() throws Exception
    {
        // Given
        String namePattern = "%h\\a\\b.log";
        // When
        File logDir = LoggingService.getLogDir( namePattern );
        // Then
        assertEquals( "C:\\userHome\\a", logDir.getAbsolutePath() );
    }

    @Test
    public void shouldThrowIOExceptionForDirPathsConstaningIllegalSymbols() throws Exception
    {
        // Given
        String legalNamePattern1 = "C:\\a\\b%u%g.log"; // the file name could contains %u or %g
        String legalNamePattern2 = "C:\\a%%%%u\\b.log"; // as long as the count of % is even, the name is okay

        String illegalNamePattern1 = "a%%%%%u\\b.log"; // if the count of % is odd, the name is wrong
        String illegalNamePattern2 = "a%%%%%g\\b.log";
        String illegalNamePattern3 = "a\\%%%t\\b.log"; // should not contain %t or %h in the middle of a folder name
        String illegalNamePattern4 = "a\\%%%h\\b.log";

        // When & Then
        assertEquals( "C:\\a", LoggingService.getLogDir( legalNamePattern1 ).getAbsolutePath() );
        assertEquals( "C:\\a%%u", LoggingService.getLogDir( legalNamePattern2 ).getAbsolutePath() );

        try
        {
            LoggingService.getLogDir( illegalNamePattern1 );
            fail( "Should not handle directory names containing %u or %g: " + illegalNamePattern1 );
        }
        catch( IOException e )
        {
            assertTrue( e.toString().contains(
                    "Cannot make directories automatically for directory paths containing %u or %g" ) );
        }
        try
        {
            LoggingService.getLogDir( illegalNamePattern2 );
            fail( "Should not handle directory names containing %u or %g: " + illegalNamePattern1 );
        }
        catch ( IOException e )
        {
            assertTrue( e.toString().contains(
                    "Cannot make directories automatically for directory paths containing %u or %g" ) );
        }
        try
        {
            LoggingService.getLogDir( illegalNamePattern3 );
            fail( "Should not handle directory names containing %u or %g: " + illegalNamePattern1 );
        }
        catch ( IOException e )
        {
            assertTrue( e.toString().contains( "Cannot understand %t or %h in the middle of a path" ) );
        }
        try
        {
            LoggingService.getLogDir( illegalNamePattern4 );
            fail( "Should not handle directory names containing %u or %g: " + illegalNamePattern1 );
        }
        catch ( IOException e )
        {
            assertTrue( e.toString().contains( "Cannot understand %t or %h in the middle of a path" ) );
        }
    }
}
