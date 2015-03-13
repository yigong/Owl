package owl.main.libs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import owl.gui.utils.OwlButtonFactory;
import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlIconedFileChooser;
import owl.main.scripting.MainInterpreter;


public class JarLibFrame extends OwlFrame implements ActionListener
{
	private static final long serialVersionUID = -2010023075228835486L;

	private final String FILE_PATH_PREF		= "FilePath";
	private final String ADD_JAR_ACTION		= "Add Jar";
	private final String DEL_JAR_ACTION		= "Remove Jar";
	private final String UNPAK_JAR_ACTION	= "UnPack Jar";
	private final String VALID_INI_KEY		= "[EXTERNAL JAR LIBS]";
	private final String INI_FILE			= "ExternalJars.ini";

	private JList m_jarList;
	private File  m_file;


	// +----------------------------------------------------------------------+
	// |  Constructor                                                         |
	// +----------------------------------------------------------------------+
	public JarLibFrame()
	{
		super( "External Libraries", false );

		setIconImage(
			( new ImageIcon(
					MainApp.getBitmapPath() + "libs.gif" ) ).getImage() );

		JToolBar toolbar = new JToolBar();
		appendToolbar( toolbar );

		addComponent( toolbar, super.TOOLBAR_INDEX );
		addComponent( new JScrollPane( createComponents() ), super.CENTER_CONTAINER_INDEX );

		OwlUtilities.centerFrame( this );

		pack();

		m_file = new File( System.getProperty( "user.dir" ) );

		loadPreferences();
	}

	// +----------------------------------------------------------------------+
	// |  STATIC - loadLibraries                                              |
	// +----------------------------------------------------------------------+
	public static void loadLibraries( boolean bShowMsg )
	{
		JarLibFrame frame = new JarLibFrame();

		try
		{
			Vector<String> vList = frame.getList();
	
			for ( int i=0; i<vList.size(); i++ )
			{
				frame.updatePaths( new File( vList.get( i ) ) );
			}

			if ( bShowMsg )
			{
				MainApp.info( "External archives SUCCESSFULLY loaded!" );
			}
		}
		catch ( Exception e )
		{
			MainApp.error( e.getMessage() );
		}
	}

	// +----------------------------------------------------------------------+
	// |  actionPerformed                                                     |
	// +----------------------------------------------------------------------+
	// |  See ActionListener for details                                      |
	// +----------------------------------------------------------------------+
	@Override
	public void actionPerformed( ActionEvent ae )
	{
		//  Add Jar
		// +-------------------------------------------------+
		if ( ae.getActionCommand().equals( ADD_JAR_ACTION ) )
		{
			OwlIconedFileChooser fileChooser
					= new OwlIconedFileChooser( m_file,
					  new String[] { "jar" },
					  new String[] { "Java Archive ( *.jar )" },
					  new ImageIcon[] { new ImageIcon( MainApp.getBitmapPath() + "jar.gif" ) } );

			fileChooser.setFrameIcon( "jar.gif" );
			
			if ( fileChooser.openDialog() )
			{
				m_file = fileChooser.getSelectedFile();
				
				try
				{
					extractDlls( m_file );
					addListItem( m_file );
					updatePaths( m_file );
					writeListToFile();

					MainApp.info( "External archives SUCCESSFULLY loaded!" );
				}
				catch ( Exception e )
				{
					MainApp.error( e.getMessage() );
				}
			}
		}

		//  Remove Jar
		// +-------------------------------------------------+
		else if ( ae.getActionCommand().equals( DEL_JAR_ACTION ) )
		{
			try
			{
				Object[] objArr = m_jarList.getSelectedValues();
	
				delListItems(
						Arrays.copyOf(
								objArr, objArr.length, String[].class ) );

				writeListToFile();
			}
			catch ( Exception e )
			{
				MainApp.error( e.getMessage() );
			}
		}

		//  Un-Pack Jar
		// +-------------------------------------------------+
		else if ( ae.getActionCommand().equals( UNPAK_JAR_ACTION ) )
		{
			try
			{
				extractDlls( m_file );

				MainApp.info( "External archives SUCCESSFULLY unpacked!" );
			}
			catch ( Exception e )
			{
				MainApp.error( e.getMessage() );
			}
		}

		//  Pass to super
		// +-------------------------------------------------+
		else
		{
			super.actionPerformed( ae );
		}
	}

	// +----------------------------------------------------------------------+
	// |  updatePaths                                                         |
	// +----------------------------------------------------------------------+
	// |  Updates the JRE class and library paths along with the script       |
	// |  interpreter class path.                                             |
	// +----------------------------------------------------------------------+
	public void updatePaths( File file ) throws Exception
	{
		//  Update runtime classpath
		// +-------------------------------------------+
		String sClassPath = System.getProperty( "java.class.path" );
			
		if ( !sClassPath.contains( file.getPath() ) )
		{
			System.setProperty( "java.class.path",
								 file.getPath() + ";" +
								 sClassPath );
		}

		//  Update library path in case dll's exist
		// +-------------------------------------------+
		String sLibPath = file.getParent();

		System.setProperty( "java.library.path",
							 System.getProperty( "java.library.path" ) +
							 System.getProperty( "path.separator" ) + sLibPath );

		//  Add classpath to script interpreter
		// +-------------------------------------------+
		MainInterpreter.get().getClassManager().addClassPath( file.toURI().toURL() );
	}

	// +----------------------------------------------------------------------+
	// |  addListItem                                                         |
	// +----------------------------------------------------------------------+
	// |  Adds a new item to the JList.                                       |
	// +----------------------------------------------------------------------+
	public void addListItem( File file ) throws Exception
	{
		Vector<String> vList = new Vector<String>();

		vList.add( file.getPath() );

		for ( int i=0; i<m_jarList.getModel().getSize(); i++ )
		{
			//  Prevent duplicate values
			if ( file.getPath().equals( ( String )m_jarList.getModel().getElementAt( i ) ) )
			{
				throw new Exception( "Library already loaded into system!" );
			}

			vList.add( ( String )m_jarList.getModel().getElementAt( i ) );
		}

		m_jarList.setListData( vList );
	}

	// +----------------------------------------------------------------------+
	// |  delListItems                                                        |
	// +----------------------------------------------------------------------+
	// |  Removes all specified items from the JList.                         |
	// +----------------------------------------------------------------------+
	public void delListItems( String[] sItems )
	{
		Vector<String> vList = getList();

		for ( int i=0; i<sItems.length; i++ )
		{
			vList.remove( sItems[ i ] );
		}

		m_jarList.setListData( vList );		
	}

	// +----------------------------------------------------------------------+
	// |  getList                                                             |
	// +----------------------------------------------------------------------+
	// |  Returns the JList as a Vector<String>.                              |
	// +----------------------------------------------------------------------+
	public Vector<String> getList()
	{
		Vector<String> vList = new Vector<String>();

		for ( int i=0; i<m_jarList.getModel().getSize(); i++ )
		{
			vList.add( ( String )m_jarList.getModel().getElementAt( i ) );
		}

		return vList;
	}

	// +----------------------------------------------------------------------+
	// |  readListFromFile                                                    |
	// +----------------------------------------------------------------------+
	// |  Reads the library list from the backing file.                       |
	// +----------------------------------------------------------------------+
	public Vector<String> readListFromFile() throws Exception
	{
		Vector<String> vList = new Vector<String>();
		BufferedReader br = null;
		String sLine = "";

		br = new BufferedReader( new FileReader( INI_FILE ) );

		while ( br.ready() )
		{
			sLine = br.readLine();

			if ( !sLine.contains( VALID_INI_KEY ) )
			{
				vList.add( sLine );
			}
		}

		br.close();

		return vList;
	}

	// +----------------------------------------------------------------------+
	// |  writeList                                                           |
	// +----------------------------------------------------------------------+
	// |  Writes the JList to the backing file.                               |
	// +----------------------------------------------------------------------+
	public void writeListToFile() throws Exception
	{
		FileWriter     fw    = null;
		PrintWriter    pw    = null;

		fw = new FileWriter( INI_FILE, false );
		pw = new PrintWriter( fw, true );

		pw.println( VALID_INI_KEY );

		for ( int i=0; i<m_jarList.getModel().getSize(); i++ )
		{
			pw.println( m_jarList.getModel().getElementAt( i ) );
		}

		pw.close();
		fw.close();
	}

	//--------------------------------------------------------------------------
    //   Protected Methods:
    //--------------------------------------------------------------------------
	protected void loadPreferences()
	{
		super.loadPreferences();

		try
		{
			m_file = new File( MainApp.getPreferences().get(
										super.getTitle() + FILE_PATH_PREF,
										System.getProperty( "user.dir" ) ) );
		}
		catch ( NullPointerException npe ) {}
	}

	protected void savePreferences()
	{
		super.savePreferences();

		try
		{
			MainApp.getPreferences().put( super.getTitle() + FILE_PATH_PREF,
										  m_file.getPath() );
		}
		catch ( Exception e )
		{
			MainApp.error( e.toString() );
		}
	}

	// +----------------------------------------------------------------------+
	// |  createComponents                                                    |
	// +----------------------------------------------------------------------+
	// |  Creates the window components.                                      |
	// +----------------------------------------------------------------------+
	private JPanel createComponents()
	{
		JPanel listPanel = new JPanel();
		listPanel.setBorder( BorderFactory.createTitledBorder( "External Library List" ) );
		listPanel.setLayout( super.gbl );

		m_jarList = new JList();
		m_jarList.setCellRenderer( new IconCellRenderer() );

		JScrollPane jarListPane = new JScrollPane( m_jarList );
		Dimension dimen = jarListPane.getPreferredSize();
		jarListPane.setPreferredSize( new Dimension( dimen.width + 180, dimen.height ) );

		JButton jarAddButton = OwlButtonFactory.createIconButton( "Plus2.gif", 26, 26,
																  "Add Library", ADD_JAR_ACTION,
																   this );

		JButton jarDelButton = OwlButtonFactory.createIconButton( "Off.gif", 26, 26,
																  "Remove Library", DEL_JAR_ACTION,
																   this );

		JButton jarUnPkButton = OwlButtonFactory.createIconButton( "jarUnPack.gif", 26, 26,
																   "Un-Pack Library", UNPAK_JAR_ACTION,
																    this );

		addComponent( listPanel, jarListPane,   0, 0, 0, 0, 1, 0, 3, 1 );
		addComponent( listPanel, jarAddButton,  0, 0, 0, 0, GridBagConstraints.NORTHWEST, 1, 1, 1, 1 );
		addComponent( listPanel, jarUnPkButton, 0, 0, 0, 0, GridBagConstraints.NORTHWEST, 2, 1, 1, 1 );
		addComponent( listPanel, jarDelButton,  0, 0, 0, 0, GridBagConstraints.NORTHWEST, 3, 1, 1, 1 );

		try
		{
			Vector<String> vList = readListFromFile();
			m_jarList.setListData( vList );
		}
		catch( Exception e ) {}

		JPanel panel = new JPanel();
		panel.setLayout( super.gbl );

		String msg = "<html><font color=\"#656161\"><b>NOTE</b>: &nbsp;&nbsp;If your file "  +
					 "contains libraries ( dll/so ), then you must remove and re-add the "   +
					 "file<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"  +
					 "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;in order for the libraries to be " +
					 "extracted!</font></html>";

		JLabel msgLabel = new JLabel( msg );

		addComponent( panel, msgLabel,  5, 5, 5, 5, 0, 0, 1, 1 );
		addComponent( panel, listPanel, 5, 5, 5, 5, 1, 0, 1, 1 );

		return panel;
	}

	// +----------------------------------------------------------------------+
	// |  extractDlls                                                         |
	// +----------------------------------------------------------------------+
	// |  Extracts all dlls ( .dll/.so ) from the specified jar file.  The    |
	// |  files are extracted to the same directory as the jar file.          |
	// +----------------------------------------------------------------------+
	private void extractDlls( File file ) throws Exception
	{
		try
		{
		    ZipInputStream in =
		    		new ZipInputStream( new FileInputStream( file.getPath() ) );

		    ZipEntry zipEntry = null;

		    while ( ( zipEntry = in.getNextEntry() ) != null )
		    {
		    	String sEntryName = zipEntry.getName();

		    	if ( sEntryName.contains( ".dll" ) || sEntryName.contains( ".so" ) )
		    	{
		    		String outFilename = OwlUtilities.getPath( file.getPath() ) +
		    							 ( new File( sEntryName ) ).getName();

		    		OutputStream out = new FileOutputStream( outFilename );

				    // Transfer bytes from the ZIP file to the output file
				    byte[] buf = new byte[ 1024 ];
				    int    len = 0;
				    
				    while ( ( len = in.read( buf ) ) > 0 )
				    {
				    	out.write( buf, 0, len );
				    }
	
				    out.close();
		    	}
		    }

		    in.close();
		}
		catch ( Exception e )
		{
			MainApp.error( e.getMessage() );
		}
	}

	// +----------------------------------------------------------------------+
	// |  Class IconCellRenderer                                              |
	// +----------------------------------------------------------------------+
	// |  The icon cell renderer for the JList component.                     |
	// +----------------------------------------------------------------------+
	private class IconCellRenderer extends DefaultListCellRenderer
	{
		private static final long serialVersionUID = 6244055527295675685L;

		final ImageIcon JAR_ICON = new ImageIcon( MainApp.getBitmapPath() + "loadJar.gif" );

	    public Component getListCellRendererComponent( JList list, Object value, int index,	boolean isSelected, boolean bHasFocus )
	    {
	    	// The DefaultListCellRenderer class will take care of
	    	// the JLabels text property, it's foreground and background
	    	//colors, and so on.
	    	super.getListCellRendererComponent( list, value, index, isSelected, bHasFocus );

	    	setIcon( JAR_ICON );

	    	return this;
	    }
	}
}
