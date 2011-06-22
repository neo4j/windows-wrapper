/**
 * Copyright (c) 2002-2011 "Neo Technology,"
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

public class NeoServiceWrapper
{
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Starting" );
        if ( args.length == 1 )
        {
            launchAsService( args[0] );
        }
        else
        {
            launchAsConsoleApp();
        }
    }

    private static void launchAsService( String serviceName )
    {
        WindowsService service = new WindowsService( serviceName );
        service.init();
        Runtime.getRuntime().halt( 0 );
    }

    private static void launchAsConsoleApp() throws Exception
    {
        final ServerProcess process = new ServerProcess();
        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                process.destroy();
            }
        } ) );
        process.waitFor();
    }
}
