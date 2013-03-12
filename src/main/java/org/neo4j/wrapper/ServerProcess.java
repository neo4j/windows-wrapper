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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public abstract class ServerProcess
{
    private final static Logger LOGGER = Logger.getLogger(ServerProcess.class .getName());

    /**
     * The prefix of parameters that mark a directory whose *.jar entries must
     * be appended to the server classpath. A runtime parameter.
     */
    public static final String ClasspathEntryPrefix = "serverClasspath";

    /**
     * The runtime parameter that points to the working dir. Every other file is
     * relative to that.
     */
    public static final String WorkingDir = "workingDir";

    /**
     * The runtime parameter that holds the file name of the configuration file.
     */
    public static final String ConfigFile = "configFile";

    /**
     * The prefix in the configuration file where additional arguments
     * to the launched jvm are passed.
     */
    public static final String ExtraArgsPrefix = "wrapper.java.additional";

    /**
     * The configuration file parameter where the -Xms setting for the
     * launched jvm is passed.
     */
    public static final String InitHeap = "wrapper.java.initmemory";

    /**
     * The configuration file parameter where the -Xmx setting for the
     * launched jvm is passed.
     */
    public static final String MaxHeap = "wrapper.java.maxmemory";

    /**
     * The InitHeap and MaxHeap settings are a number. This is their
     * measurement unit.
     */
    public static final String MemoryUnit = "m";

    /**
     * The runtime parameter that names the main class to run.
     */
    public static final String MainClassPrefix = "serverMainClass";

    /**
     * The configuration file parameter prefix that names additional
     * parameters to pass to the launched application.
     */
    public static final String AppParamPrefix = "wrapper.app.parameter";

    private File workingDir;

    private File configFile;

    public String classpath;

    final List<String> extraArgs;

    String mainClass;

    final List<String> appArgs;

    public ServerProcess()
    {
        extraArgs = new LinkedList<String>();
        appArgs = new LinkedList<String>();

        parseEnvironment();
        parseConfig();

        List<String> command = new LinkedList<String>();
        command.add( "\"java\"" );
        command.add( classpath );
        command.addAll( extraArgs );
        command.add( mainClass );
        command.addAll( appArgs );

        try
        {
            doStart( command, workingDir );

            /*
            * We have to grab and consume the input and error stream
            * because otherwise the buffers might fill up and then
            * it might get stuck. Just launch off two daemon
            * threads.
            */
            // InputStream outStr = process.getInputStream();
            // InputStream errStr = process.getErrorStream();
            // Thread out = new Thread( new StreamConsumer( outStr, System.out )
            // );
            // Thread err = new Thread( new StreamConsumer( errStr, System.err )
            // );
            // out.setDaemon( true );
            // err.setDaemon( true );
            // out.start();
            // err.start();

            // Wait ten seconds
            Thread.sleep( 10000 );
            /*
             *  Get the exit value. If it returns something
             *  it means it has exited, which means the process
             *  failed for some reason. We forgive exit code of 0
             *  but the rest are reported with 128 added to differentiate
             *  from the launcer's exit codes.
             *  If it throws an exception, it means the process is still
             *  running, which is good (after 10 seconds). Catch it, swallow it.
             */
            if ( isRunning() )
            {
                Runtime.getRuntime().halt( 3 );
            }
        }
        catch ( Exception e )
        {
            LOGGER.throwing( this.getClass().toString(), "ServerProcess()", e );
            e.printStackTrace();
            Runtime.getRuntime().halt( 1 );
        }
    }

    protected abstract void doStart( List<String> command, File workingDir ) throws IOException;

    protected abstract boolean isRunning();

    protected abstract void stop();

    private void parseConfig()
    {
        FileReader fileReader = null;
        try
        {
            fileReader = new FileReader( configFile );
            BufferedReader in = new BufferedReader( fileReader );
            String currentLine;
            String paramName, value;

            while ( ( currentLine = in.readLine() ) != null )
            {
                currentLine = currentLine.trim();

                if ( currentLine.startsWith( "#" ) || currentLine.length() == 0 )
                {// Skip comments and empty lines
                    continue;
                }
                int startFrom = currentLine.indexOf( "=" );
                if ( startFrom > -1 )
                {
                    value = currentLine.substring( startFrom + 1 );
                    paramName = currentLine.substring( 0, startFrom );
                }
                else
                {
                    paramName = currentLine;
                    value = currentLine;
                }
                StringBuffer currentBuffer = new StringBuffer();
                if ( paramName.startsWith( ExtraArgsPrefix ) )
                {
                    if ( value.startsWith( "-D" ) )
                    {
                        int equalsIndex = value.indexOf( "=" );
                        currentBuffer.append( value.substring( 0, equalsIndex ) );
                        currentBuffer.append( "=" );
                        currentBuffer.append( "\"" );
                        currentBuffer.append( value.substring( equalsIndex + 1,
                                value.length() ) );
                    }
                    else
                    {
                        currentBuffer.append( "\"" );
                        currentBuffer.append( value );
                    }
                    currentBuffer.append( "\"" );
                    extraArgs.add( currentBuffer.toString() );
                }
                else if ( paramName.startsWith( AppParamPrefix ) )
                {
                    currentBuffer.append( "\"" );
                    currentBuffer.append( value );
                    currentBuffer.append( "\"" );
                    appArgs.add( currentBuffer.toString() );
                }
                else if ( paramName.startsWith( InitHeap ) )
                {
                    extraArgs.add( "-Xms" + value + MemoryUnit );
                }
                else if ( paramName.startsWith( MaxHeap ) )
                {
                    extraArgs.add( "-Xmx" + value + MemoryUnit );
                }
            }
        }
        catch ( IOException e )
        {
            Runtime.getRuntime().halt( 2 );
        }
        finally
        {
            if ( fileReader != null )
            {
                try
                {
                    fileReader.close();
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parseEnvironment()
    {
        workingDir = new File( System.getProperty( WorkingDir ) );
        configFile = new File( workingDir, System.getProperty( ConfigFile ) );

        classpath = new ClasspathParser().parse( workingDir, System.getProperty( ClasspathEntryPrefix ) );

        mainClass = System.getProperty( MainClassPrefix );
    }
}
