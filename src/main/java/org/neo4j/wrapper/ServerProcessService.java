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
import java.util.List;
import java.util.logging.Logger;

import org.rzo.yajsw.Process;
import org.rzo.yajsw.WindowsXPProcess;

public class ServerProcessService extends ServerProcess
{
    private final static Logger LOGGER = Logger.getLogger( ServerProcessService.class.getName() );

    private Process process;

    @Override
    protected boolean isRunning()
    {
        return !process.isRunning();
    }

    @Override
    protected void doStart( List<String> command, final File workingDir )
    {
        process = new WindowsXPProcess();

        process.setCommand( command.toArray( new String[]{} ) );
        process.setWorkingDir( workingDir.getAbsolutePath() );
        process.start();

        LOGGER.info( "Starting process: " + command );
        LOGGER.info( "Working dir: " + process.getWorkingDir() );
        LOGGER.info( "Process started: " + process.getTitle() );
        LOGGER.info( "PID: " + process.getPid() );
    }

    @Override
    public void stop()
    {
        process.stop( 3000, 0 );
    }

}
