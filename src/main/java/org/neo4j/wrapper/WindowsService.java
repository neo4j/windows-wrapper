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

    public static final String ExtraArgsPrefix = "java.additional";

    public static final String MainClassPrefix = "serverMainClass";

    public static final String AppParamPrefix = "appParam";

    private Process process;

    private final File workingDir;

    private final File configFile;

    private String classpath;

    private final List<String> extraArgs;

    private String mainClass;

    private final List<String> appArgs;

    public WindowsService( String serviceName )
    {
        super( serviceName );

        workingDir = new File( System.getProperty( WorkingDir ) );
        configFile = new File( workingDir, System.getProperty( ConfigFile ) );

        extraArgs = new LinkedList<String>();
        appArgs = new LinkedList<String>();

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

    private void parseConfig()
    {
        FileReader fileReader = null;
        try
        {
            fileReader = new FileReader( configFile );
            BufferedReader in = new BufferedReader( fileReader );
            String currentLine;
            StringBuffer classpathBuffer = new StringBuffer(
                    "\"-classpath\" \"" );
            while ( ( currentLine = in.readLine() ) != null )
            {
                currentLine = currentLine.trim();
                if ( currentLine.startsWith( "#" ) )
                {// Skip comments
                    continue;
                }
                StringBuffer currentBuffer = new StringBuffer();
                if ( currentLine.startsWith( MainClassPrefix ) )
                {
                    int startFrom = currentLine.indexOf( "=" );
                    mainClass = "\"" + currentLine.substring( startFrom + 1 )
                             + "\"";
                }
                else if ( currentLine.startsWith( ExtraArgsPrefix ) )
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
                    extraArgs.add( currentBuffer.toString() );
                }
                else if ( currentLine.startsWith( AppParamPrefix ) )
                {
                    int startFrom = currentLine.indexOf( "=" );
                    String value = currentLine.substring( startFrom + 1 );
                    currentBuffer.append( "\"" );
                    currentBuffer.append( value );
                    currentBuffer.append( "\"" );
                    appArgs.add( currentBuffer.toString() );
                }
                else if ( currentLine.trim().startsWith( ClasspathEntryPrefix ) )
                {
                    String[] lineParts = currentLine.split( "=" );
                    if ( lineParts.length > 1 )
                    {
                        int globSeparator = lineParts[1].lastIndexOf( "/" );
                        String classpathEntryBaseDir = lineParts[1].substring(
                                0,
                                 globSeparator);
                        String classpathEntryGlob = lineParts[1].substring( globSeparator + 1 );
                        List<File> jars = getWildcardEntries( new File(
                                workingDir, classpathEntryBaseDir ),
                                classpathEntryGlob );
                        for ( File jar : jars )
                        {
                            classpathBuffer.append( jar.getAbsolutePath() );
                            classpathBuffer.append( ";" );
                        }
                    }
                }
            }
            classpathBuffer.append( "\"" );
            classpath = classpathBuffer.toString();
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
        for (String arg : service.appArgs)
        {
            System.out.println(arg);
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
