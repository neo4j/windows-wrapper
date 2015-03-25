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

import java.util.logging.Level;
import java.util.logging.Logger;


public class NeoServiceWrapper
{
    private static final Logger LOGGER = Logger.getLogger( NeoServiceWrapper.class.getName() );

    public static void main( String[] args ) throws Exception
    {
        try
        {
            new LoggingService().initLogger();
        }
        catch ( Exception e )
        {
            // user should see the log info from terminal even if we failed to create the logging file
            LOGGER.log( Level.SEVERE, e.toString(), e );
        }
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
        LOGGER.info( "Launched as windows service." );

        WindowsService service = new WindowsService( serviceName );
        service.init();
        Runtime.getRuntime().halt( 0 );
    }

    private static void launchAsConsoleApp() throws Exception
    {
        LOGGER.info( "Launched as console application." );
        final ServerProcess process = new ServerProcessConsole();

        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                process.stop();
            }
        } ) );
        // process.waitFor();
        Runtime.getRuntime().halt( 0 );
    }
}
