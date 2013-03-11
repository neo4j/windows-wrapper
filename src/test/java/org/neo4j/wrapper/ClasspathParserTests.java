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

import static java.lang.String.format;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

public class ClasspathParserTests
{
    @Test
    public void shouldParseRegularClasspath() throws Exception
    {
        String workingDirectory = new DirectoryBuilder().build();

        String classpath = new ClasspathParser().parse( new File( workingDirectory ),
                "lib/*.jar;system/lib/*.jar;plugins/*.jar;" );

        assertThat( classpath, is( format( "\"-classpath\" \"%1$s/lib/aneo4jlibrary.jar;" +
                "%1$s/system/lib/asystemlibrary.jar;\"", workingDirectory ) ) );
    }

    @Test
    public void shouldParseRecursiveClasspath() throws Exception
    {
        String workingDirectory = new DirectoryBuilder().withGremlinPlugin().build();

        String classpath = new ClasspathParser().parse( new File( workingDirectory ),
                "lib/*.jar;system/lib/*.jar;plugins/**/*.jar;" );

        assertThat( classpath, is( format( "\"-classpath\" \"%1$s/lib/aneo4jlibrary.jar;" +
                "%1$s/system/lib/asystemlibrary.jar;" +
                "%1$s/plugins/gremlin1.5/gremlinplugin.jar;\"", workingDirectory ) ) );
    }

    @Test
    public void shouldFindJarsInNonLeafDirectories() throws Exception
    {
        String workingDirectory = new DirectoryBuilder().withSingleJarPlugin().build();

        String classpath = new ClasspathParser().parse( new File( workingDirectory ),
                "lib/*.jar;system/lib/*.jar;plugins/**/*.jar;" );

        assertThat( classpath, is( format( "\"-classpath\" \"%1$s/lib/aneo4jlibrary.jar;" +
                "%1$s/system/lib/asystemlibrary.jar;" +
                "%1$s/plugins/aplugin.jar;\"", workingDirectory ) ) );
    }

    @Test
    public void shouldFindJarsAtMultipleLevels() throws Exception
    {
        String workingDirectory = new DirectoryBuilder().withNestedPlugin().build();

        String classpath = new ClasspathParser().parse( new File( workingDirectory ),
                "lib/*.jar;system/lib/*.jar;plugins/**/*.jar;" );

        assertThat( classpath, is( format( "\"-classpath\" \"%1$s/lib/aneo4jlibrary.jar;" +
                "%1$s/system/lib/asystemlibrary.jar;" +
                "%1$s/plugins/nested/nested.jar;" +
                "%1$s/plugins/nested/evenmore/evenmorenested.jar;\"", workingDirectory ) ) );
    }

    @Test
    public void shouldNotParseDoubleRecursiveClasspath() throws Exception
    {
        String workingDirectory = new DirectoryBuilder().withNestedPlugin().build();

        try
        {
            new ClasspathParser().parse( new File( workingDirectory ),
                    "lib/*.jar;system/lib/*.jar;plugins/**/foo/**/*.jar;" );

            fail();
        }
        catch ( UnsupportedOperationException e )
        {
            assertThat( e.getMessage(), is( "Double **'s not supported." ) );
        }
    }
}
