package org.neo4j.wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import jnacontrib.win32.Win32Service;

public class WindowsService extends Win32Service
{
    /**
     * The prefix of parameters that mark a directory whose *.jar entries must
     * be appended to the server classpath.
     */
    public static final String ClasspathEntryPrefix = "serverClassPath";

    /**
     * The runtime parameter that points to the working dir. Every other file is
     * relative to that.
     */
    public static final String WorkingDir = "workingDir";

    /**
     * The runtime parameter that holds the file name of the configuration file.
     */
    public static final String ConfigFile = "configFile";

    public static final String ExtraArgsPrefix = "java.additional";

    public static final String MainClassPrefix = "serverMainClass";

    private Process process;

    public WindowsService( String serviceName )
    {
        super( serviceName );

        File workingDir = new File( System.getProperty( WorkingDir ) );
        List<String> command = new LinkedList<String>();
        File configFile = new File( workingDir, System.getProperty( ConfigFile ) );
        command.add( "\"java\"" );
        command.add( getClasspath( workingDir, configFile ) );
        command.addAll(getExtraArgs( workingDir, configFile ));
        command.add( getMainClass( workingDir, configFile ) );

        try
        {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command( command );
            builder.directory( workingDir );
            process = builder.start();

            InputStream outStr = process.getInputStream();
            InputStream errStr = process.getErrorStream();
            Thread out = new Thread( new StreamConsumer( outStr, System.out ) );
            Thread err = new Thread( new StreamConsumer( errStr, System.err ) );
            out.start();
            err.start();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
        process.destroy();
    }

    private static String getMainClass( File workingDir, File configFile )
    {
        String result = "";
        FileReader fileReader = null;
        try
        {
            fileReader = new FileReader( configFile );
            BufferedReader in = new BufferedReader( fileReader );
            String currentLine;
            while ( ( currentLine = in.readLine() ) != null )
            {
                currentLine = currentLine.trim();
                if ( currentLine.startsWith( "#" ) )
                {// Skip comments
                    continue;
                }
                if ( currentLine.startsWith( MainClassPrefix ) )
                {
                    int startFrom = currentLine.indexOf( "=" );
                    result = "\"" + currentLine.substring( startFrom + 1 )
                             + "\"";
                    break;
                }
            }

        }
        catch ( IOException e )
        {
            e.printStackTrace();
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
        return result;
    }

    private static List<String> getExtraArgs( File workingDir, File configFile )
    {
        List<String> result = new LinkedList<String>();
        FileReader fileReader = null;
        try
        {
            fileReader = new FileReader( configFile );
            BufferedReader in = new BufferedReader( fileReader );
            String currentLine;
            StringBuffer currentBuffer;
            while ( ( currentLine = in.readLine() ) != null )
            {
                currentBuffer = new StringBuffer();
                currentLine = currentLine.trim();
                if ( currentLine.startsWith( "#" ) )
                {// Skip comments
                    continue;
                }
                if ( currentLine.startsWith( ExtraArgsPrefix ) )
                {
                    int startFrom = currentLine.indexOf( "=" );
                    String value = currentLine.substring( startFrom + 1 );
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
                    currentBuffer.append( " " );
                }
                result.add( currentBuffer.toString() );
            }

        }
        catch ( IOException e )
        {
            e.printStackTrace();
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
        return result;
    }

    private static String getClasspath( File workingDir, File configFile )
    {
        StringBuffer result = new StringBuffer( "\"-classpath\" \"" );
        FileReader fileReader = null;
        try
        {
            fileReader = new FileReader( configFile );
            BufferedReader in = new BufferedReader( fileReader );
            String currentLine;
            while ( ( currentLine = in.readLine() ) != null )
            {
                currentLine = currentLine.trim();
                if ( currentLine.startsWith( "#" ) )
                {// Skip comments
                    continue;
                }
                if ( currentLine.trim().startsWith( ClasspathEntryPrefix ) )
                {
                    String[] lineParts = currentLine.split( "=" );
                    if ( lineParts.length > 1 )
                    {
                        String classpathEntry = lineParts[1].substring( 0,
                                lineParts[1].lastIndexOf( File.separator ) );
                        List<File> jars = getWildcardEntries( new File(
                                workingDir, classpathEntry ), ".jar" );
                        for ( File jar : jars )
                        {
                            result.append( jar.getAbsolutePath() );
                            result.append( ";" );
                        }
                    }
                }
            }

        }
        catch ( IOException e )
        {
            e.printStackTrace();
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
        result.append( "\"" );
        return result.toString();
    }

    /**
     * @param directory The directory to search for.
     * @param endsWith The suffix of the filenames to match
     * @return A List of Files whose filenames end with the supplied suffix
     */
    private static List<File> getWildcardEntries( File directory,
            final String endsWith )
    {
        final FilenameFilter filter = new FilenameFilter()
        {
            @Override
            public boolean accept( File dir, String name )
            {
                return name.endsWith( endsWith );
            }

        };
        List<File> result = new LinkedList<File>();
        String[] contents = directory.list( filter );
        if ( contents != null )
        {
            for ( String filename : contents )
            {
                result.add( new File( directory, filename ) );
            }
        }
        return result;
    }
}
