package owl.logging;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;


public class LogAppender extends AppenderSkeleton
{
	//--------------------------------------------------------------------------
    //   Constants:
    //--------------------------------------------------------------------------
	private final int DEFAULT_MAX_CHAR_COUNT	=	2048;
	private final String RESULT_SPACE			=	"           ";	// Has to be as long as "cancelled"
	public static final String APPENDER_NAME	=	"OWL_LOG_APPENDER";

	//--------------------------------------------------------------------------
    //   Private Variables:
    //--------------------------------------------------------------------------
	private DefaultStyledDocument	doc;
	private JTextPane				textPane;
	private int						msgPos;
	private int						maxCharCount;
	private boolean					clearLog;

    //--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
	public LogAppender( JTextPane textPane, DefaultStyledDocument doc )
	{
		super();
		super.setName( APPENDER_NAME );

		this.textPane	= textPane;
		this.doc		= doc;
		msgPos			= 0;
		maxCharCount	= DEFAULT_MAX_CHAR_COUNT;
		clearLog		= true;
	}

	public void clearLog( boolean shouldClear )
	{
		clearLog = shouldClear;
	}

	public void clearNow()
	{
		clearDocument();
	}

	public boolean isClearing()
	{
		return clearLog;
	}

	public int getMaxCharCount()
	{
		return maxCharCount;
	}

	public void setMaxCharCount( int count )
	{
		if ( count <= 0 )
		{
			maxCharCount = DEFAULT_MAX_CHAR_COUNT;
		}
		else
		{
			maxCharCount = count;
		}
	}

    //--------------------------------------------------------------------------
    //   Private Methods:
    //--------------------------------------------------------------------------
	private void tryToClearDocument()
	{
		if ( clearLog )
		{
			if ( doc.getLength() > maxCharCount )
			{
				clearDocument();
			}
		}
	}

	private void clearDocument()
	{
		try {
			doc.remove( 0, doc.getLength() );
		} catch ( BadLocationException ble ) {}
	}

    //--------------------------------------------------------------------------
    //   Methods For log4j AppenderSkeleton:
    //--------------------------------------------------------------------------

	// Release any resources allocated within the appender such as file
	// handles, network connections, etc. It is a programming error to
	// append to a closed appender.
	@Override
	public void close()
	{
	}

	// Configurators call this method to determine if the appender requires
	// a layout. If this method returns true, meaning that layout is required,
	// then the configurator will configure an layout using the configuration
	// information at its disposal. If this method returns false, meaning that
	// a layout is not required, then layout configuration will be skipped
	// even if there is available layout configuration information at the
	// disposal of the configurator. In the rather exceptional case, where the
	// appender implementation admits a layout but can also work without it,
	// then the appender should return true.
	@Override
	public boolean requiresLayout()
	{
		return true;
	}

	// Performs the actual logging.
	@Override
	protected void append( LoggingEvent event )
	{
		tryToClearDocument();

		try
		{
			Level eventLevel = event.getLevel();

			if ( eventLevel == OwlInfoLevel.INFO_START )
			{
				doc.insertString( doc.getLength(), "INFO - ", doc.getStyle( "INFO_TAG" ) );
				doc.insertString( doc.getLength(), event.getRenderedMessage() + " ... ", doc.getStyle( "INFO" ) );
				msgPos = doc.getEndPosition().getOffset();
				doc.insertString( doc.getLength(), RESULT_SPACE + "\n", doc.getStyle( "INFO" ) );
			}

			if ( eventLevel == OwlInfoLevel.INFO_END )
			{
				try
				{
					String str = event.getRenderedMessage();
					doc.replace( msgPos, str.length(), str, doc.getStyle( "INFO" ) );
				}
				catch ( BadLocationException ble ) {}
			}

			if ( eventLevel == OwlInfoLevel.INFO_FAIL )
			{
				try
				{
					String str = event.getRenderedMessage();

					String strToReplace =
								doc.getText(
										msgPos,
										RESULT_SPACE.length() ).replace( '\n', ' ' );
					
					//  This prevents a "fail" from overwritting the last "done"
					//  if infoFail() is called and no infoStart() has been called.
					// +-----------------------------------------------------------+
//					if ( doc.getText( msgPos, str.length() ).contains( RESULT_SPACE ) )
					if ( strToReplace.contains( RESULT_SPACE ) )
					{
//						doc.replace( msgPos, str.length(), str, doc.getStyle( "ERROR" ) );
						doc.replace( msgPos, strToReplace.length(), str + "\n", doc.getStyle( "ERROR" ) );
					}
				}
				catch ( BadLocationException ble ) {}
			}

			if ( eventLevel == OwlInfoLevel.INFO_CANCEL )
			{
				try
				{
					String str = event.getRenderedMessage();
					doc.replace( msgPos, str.length(), str, doc.getStyle( "WARNING" ) );
				}
				catch ( BadLocationException ble ) {}
			}

			if ( eventLevel == Level.INFO )
			{
				doc.insertString( doc.getLength(), "INFO - ", doc.getStyle( "INFO_TAG" ) );
				doc.insertString( doc.getLength(), event.getRenderedMessage() + "\n", doc.getStyle( "INFO" ) );
			}

			if ( eventLevel == Level.WARN )
			{
				doc.insertString( doc.getLength(), "WARN - ", doc.getStyle( "WARNING_TAG" ) );
				doc.insertString( doc.getLength(), event.getRenderedMessage() + "\n", doc.getStyle( "INFO" ) );
			}

			if ( eventLevel == Level.ERROR )
			{
				doc.insertString( doc.getLength(), "ERROR - ", doc.getStyle( "ERROR_TAG" ) );
				doc.insertString( doc.getLength(), event.getRenderedMessage() + "\n", doc.getStyle( "INFO" ) );
			}

			if ( eventLevel == Level.DEBUG )
			{
				doc.insertString( doc.getLength(), "DEBUG - ", doc.getStyle( "DEBUG" ) );
				doc.insertString( doc.getLength(), event.getRenderedMessage() + "\n", doc.getStyle( "INFO" ) );
			}

			try
			{
				textPane.setCaretPosition( doc.getLength() );
				textPane.moveCaretPosition( doc.getLength() );
			}
			catch ( IllegalArgumentException iae ) {}
		}
		catch ( BadLocationException ble ) {}
	}

}
