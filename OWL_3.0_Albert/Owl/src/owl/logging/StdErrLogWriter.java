package owl.logging;

import java.io.PrintStream;
import owl.main.MainApp;

public class StdErrLogWriter extends PrintStream
{
	public StdErrLogWriter()
	{
		super( System.err );
	}

	@Override
	public void print( String s )
	{
		MainApp.getLogger().error( s );
	}

	@Override
	public void println( String s )
	{
		MainApp.getLogger().error( s );
	}

	public static void APIPrint( String s )
	{
		try
		{
			MainApp.getLogger().error( s );
		}
		catch ( Exception e )
		{
			MainApp.getLogger().error( "CAUGHT EXCEPTION!" );
		}
	}
}
