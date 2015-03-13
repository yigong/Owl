package owl.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class OwlLogger extends Logger
{
	//  It's usually a good idea to add a dot suffix to the fully
	//  qualified class name. This makes caller localization to work
	//  properly even from classes that have almost the same fully
	//  qualified class name as MyLogger, e.g. MyLoggerTest.
	// +------------------------------------------------------------+
	static String FQCN = OwlLogger.class.getName() + ".";

	//  It's enough to instantiate a factory once and for all.
	// +------------------------------------------------------------+
	private static OwlLoggerFactory owlFactory = new OwlLoggerFactory();

	//  This variable is set to 'true' to log the trace, otherwise
	//  only the message is shown.
	// +------------------------------------------------------------+
	private boolean bShowStackTrace;

	public OwlLogger( String name )
	{
		super( name );

		//  Constructor called by getLogger below. Why this needs
		//  to be set for the debug mode to work I don't know!
		//  ANSWER: The standard levels are ordered as follows:
		//  DEBUG < INFO < WARN < ERROR < FATAL
		// +------------------------------------------------------------+
		setLevel( Level.DEBUG );

		bShowStackTrace = false;
	}

	public void showStackTrace( boolean onOff )
	{
		bShowStackTrace = onOff;
	}

	public void infoStart( Object message )
	{
		if ( message != null )
		{
			super.log( FQCN, OwlInfoLevel.INFO_START, message, null );
		}
	}

	public void infoEnd()
	{
		super.log( FQCN, OwlInfoLevel.INFO_END, "done", null );
	}

	public void infoFail()
	{
		super.log( FQCN, OwlInfoLevel.INFO_FAIL, "failed", null );
	}

	public void infoCancel()
	{
		super.log( FQCN, OwlInfoLevel.INFO_CANCEL, "cancelled", null );
	}

	public void warn( Exception e )
	{
		if ( !bShowStackTrace )
		{
			if ( e.getMessage() != null )
			{
				super.warn( e.getMessage() );
			}
		}
		else
		{
			e.printStackTrace();
		}
	}

	public void error( Exception e )
	{
		if ( !bShowStackTrace )
		{
			if ( e.getMessage() != null )
			{
				super.error( e.getMessage() );
			}
		}
		else
		{
			e.printStackTrace();
		}
	}

	//  This method overrides {@link Logger#getLogger} by supplying
	//  its own factory type as a parameter.
	// +------------------------------------------------------------+
	public static Logger getLogger( String name )
	{
		Logger root = Logger.getRootLogger();
//		Layout layout = new PatternLayout("%p [%t] %c (%F:%L) - %m%n");
//		root.addAppender( new ConsoleAppender( layout, ConsoleAppender.SYSTEM_OUT ) );
//		root.addAppender( new ConsoleAppender( new SimpleLayout(), ConsoleAppender.SYSTEM_OUT ) );
		root.addAppender( new org.apache.log4j.varia.NullAppender() );

		return Logger.getLogger( name, owlFactory );
	}

//	public void trace( Object message )
//	{
//		super.log( FQCN, XLevel.TRACE, message, null ); 
//	}
}
