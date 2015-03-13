package owl.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;


public class OwlDebugLevel extends Level
{
	private static final long serialVersionUID = -2077981302786343551L;

	public static final int DEBUG_INT	=	Priority.FATAL_INT  + 1;
	private static String   DEBUG_STR	=	"OWL_DEBUG";

	public static final OwlDebugLevel DEBUG = new OwlDebugLevel( DEBUG_INT, DEBUG_STR, 0 );

	protected OwlDebugLevel( int level, String strLevel, int syslogEquiv )
	{
		super( level, strLevel, syslogEquiv );
	}

	/**
	Convert the string passed as argument to a level. If the
	conversion fails, then this method returns Level.DEBUG. 
	*/
	public static Level toLevel( String sArg )
	{
		return toLevel( sArg, Level.INFO );
	}

	public static Level toLevel( String sArg, Level defaultValue )
	{
		if( sArg == null )
		{
			return defaultValue;
		}

		String stringVal = sArg.toUpperCase();

		if( stringVal.equals( DEBUG_STR ) )
		{
			return OwlDebugLevel.DEBUG;
		}

		return Level.toLevel( sArg, defaultValue );    
	}

	public static Level toLevel( int i ) throws  IllegalArgumentException
	{
		if ( i == DEBUG_INT ) return OwlDebugLevel.DEBUG;
		else return Level.toLevel( i );
	}
}
