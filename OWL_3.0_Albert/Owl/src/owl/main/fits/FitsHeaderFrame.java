package owl.main.fits;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;

import owl.cameraAPI.CameraAPI;
import owl.gui.utils.OwlButtonFactory;
import owl.main.MainApp;
import owl.main.owltypes.OwlFileChooser;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlIconedFileChooser;
import owl.main.owltypes.OwlPanel;
import owl.main.owltypes.OwlTextField;
import owl.main.scripting.ScriptRunnable;



public class FitsHeaderFrame extends OwlFrame
{
	private static final long serialVersionUID = -3303979116717608695L;

	private final String LOAD_ACTION			= "LOAD";
	private final String SAVE_ACTION			= "SAVE";
	private final String EDIT_ACTION			= "EDIT";
	private final String CLEAR_ACTION			= "CLEAR";
	private final String INSERT_ACTION			= "INSERT";
	private final String DELETE_ACTION			= "DELETE";
	private final String SCRIPT_BROWSE_ACTION	= "SCRIPT DIR BROWSE";
	private final String XML_FILE_PREF			= "XMLFile";
	private final String SCRIPT_FILE_PREF		= "ScriptFile";
	private final String COMMENT_KEYWORD		= "COMMENT";
	private final String HISTORY_KEYWORD		= "HISTORY";
	private final String DATE_KEYWORD			= "DATE";
	private final String EXPTIME_KEY			= "EXPTIME";
	private final String DATEOBS_KEY			= "DATE-OBS";
	private final String XBINNING_KEY			= "XBINNING";
	private final String YBINNING_KEY			= "YBINNING";

	private final String FITS_XML_PARSE_RULES	= MainApp.getXMLPath() +
												  "fits-rules.xml";

	private ArrayList<FitsHeaderElement> fieldList;
	private OwlTextField scriptTxtfld;
	private JPanel       panel;
	private File         scriptFile;
	private File         xmlFile;
	private OwlPanel     exposePanel;
	
	public FitsHeaderFrame( OwlPanel theExposePanel )
	{
		super( "Fits Header", true );

		exposePanel = theExposePanel;
		fieldList   = new ArrayList<FitsHeaderElement>();
		scriptFile  = new File( System.getProperty( "user.dir" ) );

		panel = new JPanel();
		panel.setLayout( new GridLayout( 0, 1 ) );
		panel.add( createHeaderTitles() );

		JScrollPane scrollPane = new JScrollPane( panel );

		super.addComponent( createToolBar(), super.TOOLBAR_INDEX );
		super.addComponent( scrollPane, super.CENTER_CONTAINER_INDEX );
		super.addComponent( createScriptPanel(), super.SOUTH_CONTAINER_INDEX );

		addField( EXPTIME_KEY, "0.0", "Exposure time" );
		addField( DATEOBS_KEY, "yyyy-mm-ddTHH:MM:SS", "Date and time of observation" );

		pack();

		loadPreferences();
	}

	public void runUpdateScript()
	{
		try
		{
			if ( !scriptTxtfld.getText().equals( "" ) )
			{
				//  DO NOT run this script in a thread, as the thread will
				//  compete with the expose thread and cause problems.
				// +------------------------------------------------------+
				String[] varNames = { "fits", "expose" };
				Object[] varObjects = { this, exposePanel };

				( new ScriptRunnable( scriptTxtfld.getText(), varNames, varObjects ) ).run();
			}
		}
		catch ( Exception e ) {}
	}

	public void writeToFile( String filename )
	{
		String key = null, val = null, com = null;

		if ( fieldList == null ) { return; }

		for ( int i=0; i<fieldList.size(); i++ )
		{
			FitsHeaderElement elem = fieldList.get( i );

			try
			{
				key = elem.getKey().toUpperCase();
				val = elem.getValue();
				com = elem.getComment();

				if ( key.equals( COMMENT_KEYWORD ) )
				{
					CameraAPI.WriteFitsComment( val, filename );
				}

				else if ( key.equals( HISTORY_KEYWORD ) )
				{
					CameraAPI.WriteFitsHistory( val, filename );
				}

				else if ( key.equals( DATE_KEYWORD ) )
				{
					CameraAPI.WriteFitsDate( filename );
				}

				else if ( !key.equals( "" ) )
				{
					CameraAPI.WriteFitsKeyword( key,
												val,
												com,
												filename );
				}
			}
			catch ( Exception e )
			{
				MainApp.error( "Failed to write fits keyword. keyword: "
								+ key + " value: " + val
								+ " comment: " + com );
				MainApp.error( e );
			}
		}
	}

	@Override
	public void actionPerformed( ActionEvent ae )
	{
		// |  "Load header from XML" button handler
		// +--------------------------------------------------------------
		if ( ae.getActionCommand().equals( LOAD_ACTION ) )
		{
			String[] ext      = { "xml" };
			String[] desc     = { "XML file ( *.xml )" };
			ImageIcon[] icons = { new ImageIcon( MainApp.getBitmapPath() + "xml.gif" ) };

			OwlIconedFileChooser chooser =
				new OwlIconedFileChooser( new File( System.getProperty( "user.dir" ) ),
										  ext, desc, icons );

			if ( chooser.openDialog() )
			{
				xmlFile   = chooser.getSelectedFile();
				fieldList = readXml( xmlFile );

				if ( fieldList != null )
				{
					addFieldList();
				}
			}
		}

		// |  "Save header to XML" button handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( SAVE_ACTION ) )
		{
			File file = new File( System.getProperty( "user.dir" ) );
			OwlFileChooser chooser = new OwlFileChooser( file, "xml" );

			if ( chooser.saveDialog() )
			{
				xmlFile = chooser.getSelectedFile();
				writeXml( xmlFile );
			}
		}

		// |  "Edit update script" button handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( EDIT_ACTION ) )
		{
			scriptFile = new File( scriptTxtfld.getText() );

			if ( scriptFile.isFile() )
			{
				MainApp.launchTextEditor( scriptFile.getPath() );
			}
			else
			{
				MainApp.launchTextEditor( "" );
				MainApp.warn( "No existing script file. Opening empty text editor." );
			}
		}

		// |  "Insert header key" button handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( INSERT_ACTION ) )
		{
			if ( fieldList != null )
			{
				FitsHeaderElement elem = new FitsHeaderElement();
				elem.setKeyPopupMenuListener( this, DELETE_ACTION );
				panel.add( elem );
				fieldList.add( elem );
				pack();

				//  Prevent the window from growing indefinitely
				//  off the bottom of the screen.
				// +----------------------------------------------+
				if ( getHeight() > 800 )
				{
					setSize( getWidth(), 700 );
				}
			}
		}

		// |  "Delete header key" button handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( DELETE_ACTION ) )
		{
			JMenuItem src = ( ( JMenuItem )ae.getSource() );

			for ( int i=0; i<fieldList.size(); i++ )
			{
				FitsHeaderElement elem = fieldList.get( i );

				if ( elem.getIdTag().equals( src.getName() ) )
				{
					panel.remove( elem );
					fieldList.remove( i );
					pack();
					break;
				}
			}
		}

		// |  "Clear header" button handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( CLEAR_ACTION ) )
		{
			clearAll();
			xmlFile = null;
		}

		// |  "Browse update script" button handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( SCRIPT_BROWSE_ACTION ) )
		{
			String[] ext  = { "bsh" };
			String[] desc = { "BeanShell Script ( *.bsh )" };

			ImageIcon[] icons = { new ImageIcon( MainApp.getBitmapPath() +
												 "TinyBean.gif" ) };

			OwlIconedFileChooser fileChooser = new OwlIconedFileChooser(
																scriptFile,
																ext,
																desc,
																icons );

			if ( fileChooser.openDialog() )
			{
				scriptFile = fileChooser.getSelectedFile();	// Save file for next time
				scriptTxtfld.setText( scriptFile.getPath() );
			}
		}

		// |  Pass everything else to superclass
		// +--------------------------------------------------------------
		else
		{
			super.actionPerformed( ae );
		}
	}

	public void clearAll()
	{
		if ( fieldList != null )
		{
			fieldList.clear();
		}

		panel.removeAll();
		panel.add( createHeaderTitles() );

		pack();
		System.gc();
	}

	public void removeField( String key )
	{
		for ( int i=0; i<fieldList.size(); i++ )
		{
			FitsHeaderElement elem = fieldList.get( i );

			if ( elem.hasKey( key ) )
			{
				fieldList.remove( i );
			}
		}

		panel.removeAll();
		panel.add( createHeaderTitles() );

		addFieldList();

		pack();

	}

	public boolean hasField( String key )
	{
		Iterator<FitsHeaderElement> listIt = fieldList.iterator();

		while ( listIt.hasNext() )
		{
			FitsHeaderElement elem = listIt.next();

			if ( elem.hasKey( key ) )
			{
				return true;
			}
		}

		return false;
	}

	public String getField( String key )
	{
		String value = null;

		Iterator<FitsHeaderElement> listIt = fieldList.iterator();

		while ( listIt.hasNext() )
		{
			FitsHeaderElement elem = listIt.next();

			if ( elem.hasKey( key ) )
			{
				value = elem.getValue();
				break;
			}
		}

		return value;
	}

	public void addField( String key, String value )
	{
		addField( key, value, "" );
	}

	public void addField( String key, String value, String comment )
	{
		FitsHeaderElement elem = new FitsHeaderElement();
		elem.setComment( comment );
		elem.setValue( value );
		elem.setKey( key );

		fieldList.add( elem );

		addFieldList();
	}

	public void setField( String key, String value )
	{
		setField( key, value, "" );
	}

	public void setField( String key, String value, String comment )
	{
		Iterator<FitsHeaderElement> listIt = fieldList.iterator();

		while ( listIt.hasNext() )
		{
			FitsHeaderElement elem = listIt.next();

			if ( elem.hasKey( key ) )
			{
				elem.setComment( comment );
				elem.setValue( value );
			}
		}
	}

	public void setExpTimeField( double gExpTime )
	{
		setField( EXPTIME_KEY, Double.toString( gExpTime ) );
	}

	public void setDateObsField()
	{
		DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );
 
		setField( DATEOBS_KEY, dateFormat.format( new Date() ) );
	}

	public void setXBinningField( int xFactor )
	{
		if ( hasField( XBINNING_KEY ) )
		{
			setField( XBINNING_KEY, Integer.toString( xFactor ) );
		}
		else
		{
			addField( XBINNING_KEY,
					  Integer.toString( xFactor ),
					  "X axis binning factor" );
		}
	}

	public void removeXBinningField()
	{
		removeField( XBINNING_KEY );
	}

	public void setYBinningField( int yFactor )
	{
		if ( hasField( YBINNING_KEY ) )
		{
			setField( YBINNING_KEY, Integer.toString( yFactor ) );
		}
		else
		{
			addField( YBINNING_KEY,
					  Integer.toString( yFactor ),
					  "Y axis binning factor" );
		}
	}

	public void removeYBinningField()
	{
		removeField( YBINNING_KEY );
	}

	protected JToolBar createToolBar()
	{
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable( false );

		JButton loadButton = super.createNewToolbarButton( LOAD_ACTION );
		toolbar.add( loadButton );

		JButton saveButton = super.createNewToolbarButton( SAVE_ACTION );
		toolbar.add( saveButton );

		JButton insertButton = super.createNewToolbarButton( INSERT_ACTION );
		toolbar.add( insertButton );

		JButton clearButton = super.createNewToolbarButton( CLEAR_ACTION );
		toolbar.add( clearButton );

		super.appendToolbar( toolbar );

		return toolbar;
	}

	// +--------------------------------------------------------------------+
	// |  savePreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Saves the xml and script and filenames upon exit                  |
	// +--------------------------------------------------------------------+
	@Override
	protected void savePreferences()
	{
		super.savePreferences();

		String prefKey = null;

		try
		{
			if ( xmlFile != null )
			{
				prefKey = getTitle() + XML_FILE_PREF;
				MainApp.getPreferences().put( prefKey, xmlFile.getCanonicalPath() );
			}
			else
			{
				prefKey = getTitle() + XML_FILE_PREF;
				MainApp.getPreferences().remove( prefKey );
			}

			if ( scriptTxtfld != null && !scriptTxtfld.equals( "" ) )
			{
				prefKey = getTitle() + SCRIPT_FILE_PREF;
				MainApp.getPreferences().put( prefKey, scriptTxtfld.getText() );
			}
			else
			{
				prefKey = getTitle() + SCRIPT_FILE_PREF;
				MainApp.getPreferences().remove( prefKey );
			}
		}
		catch ( IOException ioe ) {}
	}

	// +--------------------------------------------------------------------+
	// |  loadPreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Loads the xml and script and filenames upon startup               |
	// +--------------------------------------------------------------------+
	@Override
	protected void loadPreferences()
	{
		String prefKey = null;
		String aString = null;

		super.loadPreferences();

		if ( scriptTxtfld != null )
		{
			prefKey = getTitle() + XML_FILE_PREF;
			aString = MainApp.getPreferences().get( prefKey, null );
	
			if ( aString != null && !aString.isEmpty() )
			{
				xmlFile   = new File( aString );
				fieldList = readXml( xmlFile );
	
				if ( fieldList != null )
				{
					addFieldList();
				}
			}

			prefKey = getTitle() + SCRIPT_FILE_PREF;
			aString = MainApp.getPreferences().get( prefKey, null );
	
			if ( aString != null && !aString.isEmpty() )
			{
				try
				{
					scriptFile = new File( aString );
	
					if ( scriptTxtfld != null && scriptFile.isFile() )
					{
						scriptTxtfld.setText( scriptFile.getCanonicalPath() );
					}
				}
				catch ( IOException ioe ) {}
			}
		}
	}

	private void addFieldList()
	{
		Iterator<FitsHeaderElement> listIt = fieldList.iterator();

		while ( listIt.hasNext() )
		{
			FitsHeaderElement elem = listIt.next();
			elem.setKeyPopupMenuListener( this, DELETE_ACTION );
			panel.add( elem );
		}

		pack();
	}

	private JPanel createHeaderTitles()
	{
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );

		JTextField keyTxtfld = new JTextField( "KEY", FitsHeaderElement.KEY_FIELD_WIDTH );
		keyTxtfld.setBackground( new java.awt.Color( 255, 223, 223 ) );
		keyTxtfld.setEnabled( false );
		panel.add( keyTxtfld );

		JTextField valueTxtfld = new JTextField( "VALUE", FitsHeaderElement.VAL_FIELD_WIDTH );
		valueTxtfld.setBackground( new java.awt.Color( 223, 255, 223 ) );
		valueTxtfld.setEnabled( false );
		panel.add( valueTxtfld );

		JTextField commentTxtfld = new JTextField( "COMMENT", FitsHeaderElement.COM_FIELD_WIDTH );
		commentTxtfld.setBackground( new java.awt.Color( 255, 255, 217 ) );
		commentTxtfld.setEnabled( false );
		panel.add( commentTxtfld );

		return panel;
	}

	private JPanel createScriptPanel()
	{
		JPanel panel = new JPanel();

		ImageIcon browseIcon = new ImageIcon( MainApp.getBitmapPath() +
											  "folder.gif",
											  "Browse" );

		JButton browseButton = OwlButtonFactory.create( browseIcon,
														SCRIPT_BROWSE_ACTION,
													    new Dimension( 25, 20 ),
													    this );

		browseButton.setToolTipText( "Browse for script" );

		ImageIcon editIcon = new ImageIcon( MainApp.getBitmapPath() +
											"edit.gif" );

		JButton editButton = OwlButtonFactory.create( editIcon,
													  EDIT_ACTION,
													  new Dimension( 25, 20 ),
													  this );
		editButton.setToolTipText( "Edit script" );

		scriptTxtfld = new OwlTextField( "", 28 );

		panel.add( new JLabel( "Update Script: " ) );
		panel.add( scriptTxtfld );
		panel.add( editButton );
		panel.add( browseButton );

		return panel;
	}

	private void writeXml( File xmlFile )
	{
		FileWriter fw = null;

		if ( fieldList != null )
		{
			try
			{
				if ( !xmlFile.getAbsolutePath().contains( ".xml" ) )
				{
					xmlFile = new File( xmlFile.getAbsolutePath() + ".xml" );
				}

				fw = new FileWriter( xmlFile );
				fw.append( "<?xml version=\"1.0\"?>\n\n" );
				fw.append( "<header>\n" );

				for ( int i=0; i<fieldList.size(); i++ )
				{
					FitsHeaderElement elem = fieldList.get( i );

					if ( elem.hasAKey() )
					{
						fw.append( "\n\t<card>" );
						fw.append( "\n\t\t<key>" + elem.getKey() + "</key>" );
						fw.append( "\n\t\t<value>" + elem.getValue() + "</value>" );
						fw.append( "\n\t\t<comment>" + elem.getComment() + "</comment>" );
						fw.append( "\n\t</card>\n" );
					}
				}

				fw.append( "\n</header>\n\n" );
				fw.close();
			}
			catch ( Exception e )
			{
				MainApp.error( e );

				try
				{
					if ( fw != null ) fw.close();
				}
				catch ( Exception e2 ) {}
			}
		}
	}

	private ArrayList<FitsHeaderElement> readXml( File xmlFile )
	{
		ArrayList<FitsHeaderElement> list = null;

		try
		{
			clearAll();

			java.net.URL rules = ( new File( FITS_XML_PARSE_RULES ) ).toURI().toURL();
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

			org.apache.commons.logging.impl.Log4JLogger newLogger
						= new org.apache.commons.logging.impl.Log4JLogger( xmlLogger );

			digester.setLogger( newLogger );
			digester.setSAXLogger( newLogger );

			InputStream input = new FileInputStream( xmlFile );
			list = ( ArrayList<FitsHeaderElement> )digester.parse( input );
		}
		catch ( Exception e )
		{
			MainApp.error( "Failed to properly parse FITS XML file! " + e.getMessage() );

			list = null;
		}

		return list;
	}
}
