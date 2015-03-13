package owl.CCParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import owl.main.MainApp;



public class CCFileReader
{
	private final static String CC_GROUP_XML_RULES = MainApp.getXMLPath() + "cc-group-rules.xml";
	private final static String CC_XML_RULES = MainApp.getXMLPath() + "cc-rules.xml";


	// +---------------------------------------------------------------------------+
	// | readGroupXml
	// +---------------------------------------------------------------------------+
	// | Parses a set of controller configuration scripts that belong to a group.
	// | For example, the video processor bits of the controller configuration all
	// | belong to one group.
    // +---------------------------------------------------------------------------+
	public static ArrayList<CCScriptGroup> readGroupXml( String xmlFileName )
	{
		ArrayList<CCScriptGroup> list = null;

		try
		{
			java.net.URL rules = ( new File( CC_GROUP_XML_RULES ) ).toURI().toURL();
			Digester digester = DigesterLoader.createDigester( rules );
			digester.setValidating( false );

			// IMPORTANT - Disable the logger for the digester during parsing, or
			// debug statements will be printed to the console and/or log window.
			// This is because debug level is turned on in log4j for the use of
			// the debug() function. Get the root logger and set its log level to
			// Level.INFO, with will prevent debug messages from the digester,
			// but allow info ones ( although I don't think there are any ).
			org.apache.log4j.Logger xmlLogger = org.apache.log4j.Logger.getRootLogger();
			xmlLogger.setLevel( org.apache.log4j.Level.INFO );
			org.apache.commons.logging.impl.Log4JLogger newLogger = new org.apache.commons.logging.impl.Log4JLogger( xmlLogger );
			digester.setLogger( newLogger );
			digester.setSAXLogger( newLogger );

			InputStream input = new FileInputStream( new File( xmlFileName ) );
			list = ( ArrayList<CCScriptGroup> )digester.parse( input );
		}
		catch ( Exception e )
		{
			MainApp.error( e );
		}

		return list;
	}

	// +---------------------------------------------------------------------------+
	// | readXml
	// +---------------------------------------------------------------------------+
	// | Parses individual controller configuration scripts. i.e. parameters that
	// | are represented by a single bit only.
    // +---------------------------------------------------------------------------+
	public static ArrayList<CCScript> readXml( String xmlFileName )
	{
		ArrayList<CCScript> list = null;

		try
		{
			java.net.URL rules = ( new File( CC_XML_RULES ) ).toURI().toURL();
			Digester digester = DigesterLoader.createDigester( rules );
			digester.setValidating( false );

			// IMPORTANT - Disable the logger for the digester during parsing, or
			// debug statements will be printed to the console and/or log window.
			// This is because debug level is turned on in log4j for the use of
			// the debug() function. Get the root logger and set its log level to
			// Level.INFO, with will prevent debug messages from the digester,
			// but allow info ones ( although I don't think there are any ).
			org.apache.log4j.Logger xmlLogger = org.apache.log4j.Logger.getRootLogger();
			xmlLogger.setLevel( org.apache.log4j.Level.INFO );
			org.apache.commons.logging.impl.Log4JLogger newLogger = new org.apache.commons.logging.impl.Log4JLogger( xmlLogger );
			digester.setLogger( newLogger );
			digester.setSAXLogger( newLogger );

			InputStream input = new FileInputStream( new File( xmlFileName ) );
			list = ( ArrayList<CCScript> )digester.parse( input );
		}
		catch ( Exception e )
		{
			MainApp.error( e );
		}

		return list;
	}
}
