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

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;

public class DirectoryBuilder
{
    class Jar
    {
        private final String directory;
        private final String filename;

        public Jar( String directory, String filename )
        {
            this.directory = directory;
            this.filename = filename;
        }

        public Path getPath( Path directoryPath )
        {
            return new File( directoryPath.toFile(), filename ).toPath();
        }

        public Path getDirectoryPath( Path tempDirectory )
        {
            return new File( tempDirectory.toFile(), directory ).toPath();
        }
    }

    Collection<Jar> jars = new LinkedHashSet<Jar>( asList(
            new Jar( "lib", "aneo4jlibrary.jar" ),
            new Jar( "system/lib", "asystemlibrary.jar" ) ) );

    public String build() throws IOException
    {
        Path tempDirectory = Files.createTempDirectory( new File( "target" ).toPath(), getClass().getSimpleName() );

        for ( Jar jar : jars )
        {
            Path directoryPath = jar.getDirectoryPath( tempDirectory );
            Files.createDirectories( directoryPath );
            Path path = jar.getPath( directoryPath );
            Files.createFile( path );
        }

        return tempDirectory.toAbsolutePath().toString();
    }

    public DirectoryBuilder withGremlinPlugin()
    {
        jars.add( new Jar( "plugins/gremlin1.5", "gremlinplugin.jar" ) );
        return this;
    }

    public DirectoryBuilder withSingleJarPlugin()
    {
        jars.add( new Jar( "plugins", "aplugin.jar" ) );
        return this;
    }


    public DirectoryBuilder withNestedPlugin()
    {
        jars.add( new Jar( "plugins/nested", "nested.jar" ) );
        jars.add( new Jar( "plugins/nested/evenmore", "evenmorenested.jar" ) );
        return this;
    }
}
