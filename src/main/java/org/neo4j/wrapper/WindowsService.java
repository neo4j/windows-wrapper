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

    private Process process;

    private File workingDir;

    private File configFile;

    private String classpath;

    private final List<String> extraArgs;

    private String mainClass;

    private final List<String> appArgs;

    public WindowsService( String serviceName )
    {
        super( serviceName );

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
            ProcessBuilder builder = new ProcessBuilder();
            builder.command( command );
            builder.directory( workingDir );
            process = builder.start();

            InputStream outStr = process.getInputStream();
            InputStream errStr = process.getErrorStream();
            Thread out = new Thread( new StreamConsumer( outStr, System.out ) );
            Thread err = new Thread( new StreamConsumer( errStr, System.err ) );
            out.setDaemon( true );
            err.setDaemon( true );
            out.start();
            err.start();
        }
        catch ( IOException e )
        {
            Runtime.getRuntime().halt( 1 );
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
                /*if ( currentLine.startsWith( MainClassPrefix ) )
                {
                    int startFrom = currentLine.indexOf( "=" );
                    mainClass = "\"" + currentLine.substring( startFrom + 1 )
                                + "\"";
                }
                else */if ( paramName.startsWith( ExtraArgsPrefix ) )
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
                    currentBuffer.append( " " );
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
                    extraArgs.add( "-Xms=" + value + MemoryUnit );
                }
                else if ( paramName.startsWith( MaxHeap ) )
                {
                    extraArgs.add( "-Xmx=" + value + MemoryUnit );
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

        String preClasspath = System.getProperty( ClasspathEntryPrefix );
        String[] preClasspathEntries = preClasspath.split( ";" );
        StringBuffer classpathBuffer = new StringBuffer( "\"-classpath\" \"" );
        for ( String preClasspathEntry : preClasspathEntries )
        {
            int globSeparator = preClasspathEntry.lastIndexOf( "/" );
            String classpathEntryBaseDir = preClasspathEntry.substring( 0,
                    globSeparator );
            String classpathEntryGlob = preClasspathEntry.substring( globSeparator + 1 );
            File classpathDirectory = new File( workingDir,
                    classpathEntryBaseDir );
            if ( classpathDirectory.exists() )
            {
                List<File> jars = getWildcardEntries( classpathDirectory,
                        classpathEntryGlob );
                for ( File jar : jars )
                {
                    classpathBuffer.append( jar.getAbsolutePath() );
                    classpathBuffer.append( ";" );
                }
            }
        }
        classpathBuffer.append( "\"" );
        classpath = classpathBuffer.toString();

        mainClass = System.getProperty( MainClassPrefix );
    }

    public static void main( String[] args )
    {
        WindowsService service = new WindowsService( "" );
        System.out.println( "Params" );
        for ( String param : service.extraArgs )
        {
            System.out.println( param );
        }
        System.out.println( "Classpath: " + service.classpath );
        System.out.println( "Main class: " + service.mainClass );
        System.out.println( "Args: " );
        for ( String arg : service.appArgs )
        {
            System.out.println( arg );
        }
    }

    /**
     * @param directory The directory to search for.
     * @param endsWith The suffix of the filenames to match
     * @return A List of Files whose filenames end with the supplied suffix
     */
    private static List<File> getWildcardEntries( File directory,
            final String glob )
    {
        final String regExp = glob.trim() == "" ? ".*"
                : compileToRegexpFromGlob( glob );

        final FilenameFilter filter = new FilenameFilter()
        {
            @Override
            public boolean accept( File dir, String name )
            {
                return name.matches( regExp );
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

    private static String compileToRegexpFromGlob( String glob )
    {
        StringBuffer result = new StringBuffer();
        char currentChar;
        for ( int i = 0; i < glob.length(); i++ )
        {
            currentChar = glob.charAt( i );
            if ( currentChar == '*' )
            {
                result.append( ".*" );
            }
            else if ( currentChar == '.' )
            {
                result.append( "[.]" );
            }
            else
            {
                result.append( currentChar );
            }
        }
        return result.toString();
    }
}
