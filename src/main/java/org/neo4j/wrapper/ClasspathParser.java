/**
 * Copyright (c) 2002-2013 "Neo Technology,"
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
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class ClasspathParser
{
    String parse( File workingDir, String classpathSystemProperty )
    {
        String preClasspath = classpathSystemProperty;
        String[] preClasspathEntries = preClasspath.split( ";" );
        StringBuffer classpathBuffer = new StringBuffer( "\"-classpath\" \"" );
        for ( String preClasspathEntry : preClasspathEntries )
        {
            if ( preClasspathEntry.contains( "**/" ) )
            {
                handleRecursiveCase( preClasspathEntry, workingDir, classpathBuffer );
            }

            int globSeparator = preClasspathEntry.lastIndexOf( "/" );
            String classpathEntryBaseDir = preClasspathEntry.substring( 0,
                    globSeparator );
            String classpathEntryGlob = preClasspathEntry.substring( globSeparator + 1 );
            File classpathDirectory = new File( workingDir,
                    classpathEntryBaseDir );
            if ( classpathDirectory.exists() )
            {
                List<File> jars = getWildcardEntries( classpathDirectory,
                        classpathEntryGlob );
                for ( File jar : jars )
                {
                    classpathBuffer.append( jar.getAbsolutePath() );
                    classpathBuffer.append( ";" );
                }
            }
        }
        classpathBuffer.append( "\"" );
        return classpathBuffer.toString();
    }

    private void handleRecursiveCase( String preClasspathEntry, File workingDir, StringBuffer classpathBuffer )
    {
        String[] baseAndGlob = split( preClasspathEntry );

        File classpathDirectory = new File( workingDir,
                baseAndGlob[0] );

        LinkedList<Path> directoriesToSearch = new LinkedList<Path>();

        directoriesToSearch.add( classpathDirectory.toPath() );

        while ( !directoriesToSearch.isEmpty() )
        {
            Path directoryToSearch = directoriesToSearch.remove();

            List<File> jars = getWildcardEntries( directoryToSearch.toFile(), baseAndGlob[1] );

            for ( File jar : jars )
            {
                classpathBuffer.append( jar.getAbsolutePath() );
                classpathBuffer.append( ";" );
            }

            try
            {
                DirectoryStream<Path> paths = Files.newDirectoryStream( directoryToSearch );

                for ( Path path : paths )
                {
                    File file = path.toFile();

                    if ( file.isDirectory() )
                    {
                        directoriesToSearch.add( path );
                    }
                }
            }
            catch ( IOException e )
            {
                throw new UnsupportedOperationException( "TODO", e );
            }
        }
    }

    private String[] split( String preClasspathEntry )
    {
        if (preClasspathEntry.indexOf( "**" ) != preClasspathEntry.lastIndexOf( "**" ))
            throw new UnsupportedOperationException("Double **'s not supported.");

        String[] strings = preClasspathEntry.split( "\\*\\*/" );

        return strings;
    }

    /**
     * @param directory The directory to search for.
     * @return A List of Files whose filenames end with the supplied suffix
     */
    private static List<File> getWildcardEntries( File directory,
                                                  final String glob )
    {
        final String regExp = glob.trim() == "" ? ".*"
                : compileToRegexpFromGlob( glob );

        final FilenameFilter filter = new FilenameFilter()
        {
            @Override
            public boolean accept( File dir, String name )
            {
                return name.matches( regExp );
            }
        };
        List<File> result = new LinkedList<File>();
        String[] contents = directory.list( filter );
        if ( contents != null )
        {
            for ( String filename : contents )
            {
                result.add( new File( directory, filename ) );
            }
        }
        return result;
    }

    private static String compileToRegexpFromGlob( String glob )
    {
        StringBuffer result = new StringBuffer();
        char currentChar;
        for ( int i = 0; i < glob.length(); i++ )
        {
            currentChar = glob.charAt( i );
            if ( currentChar == '*' )
            {
                result.append( ".*" );
            }
            else if ( currentChar == '.' )
            {
                result.append( "[.]" );
            }
            else
            {
                result.append( currentChar );
            }
        }
        return result.toString();
    }
}
