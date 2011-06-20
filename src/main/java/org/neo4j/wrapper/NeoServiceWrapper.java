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
