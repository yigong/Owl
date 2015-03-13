package owl.logging;

import java.io.PrintStream;
//import org.apache.log4j.Logger;
import owl.main.MainApp;

public class StdOutLogWriter extends PrintStream
{
	public StdOutLogWriter()
	{
		super( System.out );
	}

	@Override
	public void print( String s )
	{
		MainApp.getLogger().debug( s );
	}

	@Override
	public void println( String s )
	{
		MainApp.getLogger().debug( s );
	}

	public static void APIPrint( String s )
	{
		try
		{
			MainApp.getLogger().debug( s );
		}
		catch ( Exception e )
		{
			MainApp.getLogger().error( "CAUGHT EXCEPTION!" );
		}
	}
}
