package owl.gui.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

//+---------------------------------------------------------------------+
//  This class is used to extract a text string from within an
// 	HTML string. In particular, it's used to parse a component
// 	label that is expressed in HTML, as in:
//
//  new JLabel( "<html><font color=\"#ff0000\">Text</font></html>" );
//
//  The parse method will then extract the word "Text".
// +--------------------------------------------------------------------+
public class HTMLParser extends HTMLEditorKit.ParserCallback
{
	public String foundString = null;

	public static String parse( String anHTMLString )
	{
		HTMLParser callback = null;
	    Reader reader       = null;

		try
		{
		    reader   = new StringReader( anHTMLString );
			callback = new HTMLParser();

		    new ParserDelegator().parse( reader, callback, true );

		    reader.close();
		}
		catch ( IOException ioe ) {}

		return callback.foundString;
	}

	@Override
	public void handleText( char[] data, int pos )
	{
		foundString = new String( data );
	}
}
