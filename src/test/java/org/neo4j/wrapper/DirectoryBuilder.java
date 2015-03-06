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

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.UUID;

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

        public void createIn( File tempDirectory ) throws IOException
        {
            File dir = new File( tempDirectory, directory );
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs) throw new UnsupportedOperationException("TODO");
            File file = new File( dir, filename );
            boolean newFile = file.createNewFile();
            if (! newFile) throw new UnsupportedOperationException("TODO");
        }
    }

    Collection<Jar> jars = new LinkedHashSet<Jar>( asList(
            new Jar( "lib", "aneo4jlibrary.jar" ),
            new Jar( "system/lib", "asystemlibrary.jar" ) ) );

    public String build() throws IOException
    {
        File tempDirectory = createTemporaryDirectoryInTargetDirectory();

        for ( Jar jar : jars )
        {
            jar.createIn( tempDirectory );
        }

        return tempDirectory.getAbsolutePath();
    }

    private File createTemporaryDirectoryInTargetDirectory()
    {
        File targetDirectory = new File( "target" );
        File testDirectory = new File( targetDirectory, getClass().getSimpleName() );
        File temporaryDirectory = new File( testDirectory, UUID.randomUUID().toString() );
        if (! temporaryDirectory.mkdirs()) throw new UnsupportedOperationException("TODO");
        return temporaryDirectory;
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
