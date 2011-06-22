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

public class NeoServiceWrapper {

    private WindowsService service;

	/**
	 * @param args
	 */
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Starting" );
        if ( args.length > 2 )
        {
            throw new IllegalArgumentException( "invalid arguments" );
        }

        NeoServiceWrapper wrapper = new NeoServiceWrapper( args[0] );
        if ( args.length == 2 )
        {
            if ( "start".equals( args[1] ) )
            {
                wrapper.start();
            }
            else if ( "stop".equals( args[1] ) )
            {
                wrapper.stop();
            }
            else if ( "restart".equals( args[1] ) )
            {
                wrapper.restart();
            }
            else
            {
                throw new IllegalArgumentException( "wrong command" );
            }
        }
	}

    public NeoServiceWrapper(String serviceName)
    {
        this.service = new WindowsService( serviceName );
        service.init();
        Runtime.getRuntime().halt( 0 );
    }

    private void start()
    {
        service.start();
    }

    private void stop() throws Exception
    {
        service.stop();
    }

    private void restart() throws Exception
    {
        stop();
        start();
    }
}
