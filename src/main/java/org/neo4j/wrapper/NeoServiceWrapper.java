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
import java.util.logging.Logger;


public class NeoServiceWrapper
{
    private final static Logger LOGGER = Logger.getLogger(NeoServiceWrapper.class .getName());
    public static void main( String[] args ) throws Exception
    {
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
        final ServerProcess process = new ServerProcessConsole();
        LOGGER.info( "Params" );
        for ( String param : process.extraArgs )
        {
            LOGGER.info( param );
        }
        LOGGER.info( "Classpath: " + process.classpath );
        LOGGER.info( "Main class: " + process.mainClass );
        LOGGER.info( "Args: " );
        for ( String arg : process.appArgs )
        {
            LOGGER.info( arg );
        }
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
