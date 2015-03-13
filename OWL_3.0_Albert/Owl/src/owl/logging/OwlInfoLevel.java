package owl.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;


public class OwlInfoLevel extends Level
{
	private static final long serialVersionUID = -5036663064639955778L;

	static public final int INFO_START_INT	=	Priority.INFO_INT  + 1;
	static public final int INFO_END_INT	=	Priority.FATAL_INT + 2;
	static public final int INFO_FAIL_INT	=	Priority.INFO_INT + 3;
	static public final int INFO_CANCEL_INT	=	Priority.INFO_INT  + 4;

	private static String INFO_START_STR	=	"INFO_START";
	private static String INFO_END_STR		=	"INFO_END";
	private static String INFO_FAIL_STR		=	"INFO_FAIL";
	private static String INFO_CANCEL_STR	=	"INFO_CANCEL";

	public static final OwlInfoLevel INFO_START  = new OwlInfoLevel( INFO_START_INT,  INFO_START_STR,  0 );
	public static final OwlInfoLevel INFO_END    = new OwlInfoLevel( INFO_END_INT,    INFO_END_STR,    0 );
	public static final OwlInfoLevel INFO_FAIL   = new OwlInfoLevel( INFO_FAIL_INT,   INFO_FAIL_STR,   0 );
	public static final OwlInfoLevel INFO_CANCEL = new OwlInfoLevel( INFO_CANCEL_INT, INFO_CANCEL_STR, 0 );

	protected OwlInfoLevel( int level, String strLevel, int syslogEquiv )
	{
		super( level, strLevel, syslogEquiv );
	}

	// +----------------------------------------------------------+
	// | Convert the string passed as argument to a level. If the
	// | conversion fails, then this method returns Level.INFO. 
	// +----------------------------------------------------------+
	public static Level toLevel( String sArg )
	{
		return toLevel( sArg, Level.INFO );
	}

	public static Level toLevel( String sArg, Level defaultValue )
	{
		if ( sArg == null )
		{
			return defaultValue;
		}

		String stringVal = sArg.toUpperCase();

		if ( stringVal.equals( INFO_START_STR ) )
		{
			return OwlInfoLevel.INFO_START;
		}

		else if ( stringVal.equals( INFO_END_STR ) )
		{
			return OwlInfoLevel.INFO_END;
		}

		else if ( stringVal.equals( INFO_FAIL_STR ) )
		{
			return OwlInfoLevel.INFO_FAIL;
		}

		else if ( stringVal.equals( INFO_CANCEL_STR ) )
		{
			return OwlInfoLevel.INFO_CANCEL;
		}

		return Level.toLevel( sArg, defaultValue );    
	}

	public static Level toLevel( int i ) throws  IllegalArgumentException
	{
		switch( i )
		{
			case INFO_START_INT:   { return OwlInfoLevel.INFO_START;  }
			case INFO_END_INT:     { return OwlInfoLevel.INFO_END;    }
			case INFO_FAIL_INT:    { return OwlInfoLevel.INFO_FAIL;   }
			case INFO_CANCEL_INT:  { return OwlInfoLevel.INFO_CANCEL; }
		}

		return Level.toLevel( i );
	}
}
