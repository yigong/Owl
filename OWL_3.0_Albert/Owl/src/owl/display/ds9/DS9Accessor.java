package owl.display.ds9;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Vector;

import owl.main.MainApp;


public class DS9Accessor
{
	Runtime runtime = null;

	public DS9Accessor()
	{
		runtime = Runtime.getRuntime();
	}

	public String version() throws Exception
	{
		return version( 0 );
	}

	public String version( int minVersion ) throws Exception
	{
		String sVersion = "";

		try
		{
			Vector<String> vRetVals = doCmd( "xpaget -p ds9 version" );

			if ( vRetVals != null )
			{
				for ( int i=0; i<vRetVals.size(); i++ )
				{
					String aString = ( vRetVals.get( i ) );

					if ( aString.contains( "ds9" ) )
					{
						String[] tokens = aString.split( "[ \t]" );

						if ( tokens.length >= 2 )
						{
							sVersion = tokens[ 1 ];

							if ( minVersion > 0 )
							{
								if ( Character.digit( sVersion.charAt( 0 ), 10 ) < minVersion )
								{
									throw new Exception(
											"( DS9Accessor.version ): Minimum version not met! Need: " +
											 minVersion + " DS9: " + sVersion );
								}
							}
							break;
						}
					}
				}
			}
		}
		catch ( IOException ioe ) {}

		return sVersion;
	}

	public boolean isDS9Ready( boolean showErrMsg ) throws IOException
	{
		Vector<String> vRetVals = doCmd( "xpaaccess ds9" );

		boolean bIsReady = false;

		try
		{
			String yesNo = vRetVals.firstElement();

			if ( yesNo.indexOf( "yes" ) >= 0 )
			{
				bIsReady = true;
			}
		}
		catch( NoSuchElementException nsee ) {}

		if ( !bIsReady && showErrMsg )
		{
			MainApp.error(
				"DS9 may not be running. Please start the program." );
		}

		return bIsReady;
	}

	public void clearAllFrames()
	{
		try
		{
			doCmd( "xpaset -p ds9 frame clear all" );
		}
		catch ( IOException ioe ) {}
	}

	public void showFits( String file ) throws IOException
	{
		Vector<String> vRetVals =
				doCmd( "xpaset -p ds9 file \"{" +
						file.replace( '\\', '/' ) +
						"}\"" );
	}

	public Vector<String> doCmd( String ds9cmd ) throws IOException
	{
		Vector<String> vRetVals = new Vector<String>();
		Process proc = runtime.exec( ds9cmd );
		int dFailCount = 0;

		try
		{
			// Put a BufferedReader on the proc output
			InputStream inputstream = proc.getInputStream();
			InputStreamReader inputstreamreader = new InputStreamReader( inputstream );
			BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

			// Read the proc output
			String line;

			while ( ( line = bufferedreader.readLine() ) != null )
			{
				vRetVals.add( line );
	
				dFailCount++;

				if ( ( dFailCount++ ) > 1000 )
				{
					MainApp.error( "Failed to read DS9 return values!" );

					break;
				}
			}

			// Check for proc failure
			if ( proc.waitFor() != 0 )
			{
				vRetVals.add( "exit value = " + proc.exitValue() );
			}
		}
		catch ( InterruptedException e )
		{
			proc.destroy();
			MainApp.error( e );
		}

		return vRetVals;
	}

	private void printOutput( Vector<String> outputVector )
	{
		if ( outputVector != null )
		{
			for ( int i=0; i<outputVector.size(); i++ )
			{
				MainApp.debug( "( DS9Accessor ) -> vec[ " + i + " ]: " +
								outputVector.get( i ) );
			}
		}
	}
}
