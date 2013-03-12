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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.rzo.yajsw.Process;
import org.rzo.yajsw.WindowsXPProcess;

public class ServerProcessConsole extends ServerProcess
{
    private Process process;

    @Override
    protected void doStart( List<String> command, File workingDir ) throws IOException
    {

        process = new WindowsXPProcess();


        process.setCommand( command.toArray( new String[] {} ) );
        process.setWorkingDir( workingDir.getAbsolutePath() );
        process.setPipeStreams( true, false );
        process.start();

        new PipingThread( process.getInputStream(), System.out ).start();
        new PipingThread( process.getErrorStream(), System.err ).start();
    }

    @Override
    public boolean isRunning()
    {
        return process.isRunning();
    }

    @Override
    public void stop()
    {
        try
        {
            process.wait( 3000, 0 );
            process.destroy();
        } catch ( InterruptedException i )
        {
            i.printStackTrace();
        }
    }

    private static class PipingThread extends Thread
    {
        private final InputStream source;
        private final PrintStream target;

        public PipingThread( InputStream source, PrintStream target )
        {
            this.source = source;
            this.target = target;
            setDaemon( true );
        }

        @Override
        public void run()
        {
            int read;
            byte[] buffer = new byte[1024];
            try
            {
                while ( ( read = source.read( buffer ) ) > 0 )
                {
                    target.write( buffer, 0, read );
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }
}
