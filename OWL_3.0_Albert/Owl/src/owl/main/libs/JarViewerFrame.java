package owl.main.libs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlIconedFileChooser;



public class JarViewerFrame extends OwlFrame implements ActionListener
{
	private static final long serialVersionUID = -1958482253008875697L;
	private final String FILE_PATH_PREF	= "FilePath";
	private final String OPEN_ACTION	= "OPEN";

	private String m_sFilePath;
	private JList  m_list;


	// +------------------------------------------------------------------------+
    // | Contstructor                                                           |
    // +------------------------------------------------------------------------+
	public JarViewerFrame()
	{
		super( "Jar Content Viewer", false );

		super.setIconImage( (
					new ImageIcon( MainApp.getBitmapPath() +
								   "jar.gif" ) ).getImage() );

		JToolBar toolbar = new JToolBar();

		toolbar.add( super.createNewToolbarButton( OPEN_ACTION ) );
		appendToolbar( toolbar );

		addComponent( toolbar, super.TOOLBAR_INDEX );
		addComponent( createComponents(), super.CENTER_CONTAINER_INDEX );

		m_sFilePath = System.getProperty( "user.dir" );

		pack();

		OwlUtilities.centerFrame( this );

		loadPreferences();
	}

	// +------------------------------------------------------------------------+
    // | actionPerformed                                                        |
    // +------------------------------------------------------------------------+
	@Override
	public void actionPerformed( ActionEvent ae )
	{
		if ( ae.getActionCommand().equals( OPEN_ACTION ) )
		{
			OwlIconedFileChooser fileChooser
						= new OwlIconedFileChooser(
								new File( m_sFilePath ),
								new String[] { "jar" },
								new String[] { "Java Archive ( *.jar )" },
								new ImageIcon[] { new ImageIcon( MainApp.getBitmapPath() +
																 "jar.gif" ) } );

			fileChooser.setFrameIcon( "jar.gif" );
			
			if ( fileChooser.openDialog() )
			{
				m_sFilePath = fileChooser.getSelectedFile().getParent();
			
				showFileContents( fileChooser.getSelectedFile() );
			}
		}

		else
		{
			super.actionPerformed( ae );
		}
	}

	// +------------------------------------------------------------------------+
    // | loadPreferences                                                        |
    // +------------------------------------------------------------------------+
	protected void loadPreferences()
	{
		super.loadPreferences();

		try
		{
			m_sFilePath = MainApp.getPreferences().get(
											super.getTitle() + FILE_PATH_PREF,
											System.getProperty( "user.dir" ) );
		}
		catch ( NullPointerException npe ) {}
	}

	// +------------------------------------------------------------------------+
    // | savePreferences                                                        |
    // +------------------------------------------------------------------------+
	protected void savePreferences()
	{
		super.savePreferences();

		try
		{
			MainApp.getPreferences().put(
								super.getTitle() + FILE_PATH_PREF, m_sFilePath );
		}
		catch ( Exception e )
		{
			MainApp.error( e.toString() );
		}
	}

	// +------------------------------------------------------------------------+
    // | createComponents                                                       |
    // +------------------------------------------------------------------------+
	private JPanel createComponents()
	{
		JPanel panel = new JPanel();
		panel.setBorder( BorderFactory.createTitledBorder( "Jar Content List" ) );

		m_list = new JList();
		m_list.setCellRenderer( new IconCellRenderer() );

		JScrollPane listPane = new JScrollPane( m_list );

		Dimension dimen = listPane.getPreferredSize();

		listPane.setPreferredSize( new Dimension( dimen.width + 180,
												  dimen.height + 200 ) );

		panel.add( listPane );

		return panel;
	}

	// +------------------------------------------------------------------------+
    // | showFileContents                                                       |
    // +------------------------------------------------------------------------+
    // | Fills the JList component with the contents of the JAR file.           |
    // +------------------------------------------------------------------------+
	private void showFileContents( File file )
	{
		Vector<String> entryVec = new Vector<String>();

		try
		{
		    ZipInputStream in =
		    		new ZipInputStream(
		    				new FileInputStream( file.getPath() ) );

		    ZipEntry zipEntry = null;

		    while ( ( zipEntry = in.getNextEntry() ) != null )
		    {
		    	entryVec.add( zipEntry.getName() );
		    }

		    in.close();

		    m_list.setListData( entryVec  );
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
		private static final long serialVersionUID = -5008951279407041034L;

		final ImageIcon CLASS_ICON =
						new ImageIcon( MainApp.getBitmapPath() + "class.gif" );

		final ImageIcon LIBS_ICON =
						new ImageIcon( MainApp.getBitmapPath() + "libs.gif" );

		final ImageIcon DEFAULT_ICON =
						new ImageIcon( MainApp.getBitmapPath() + "edit.gif" );

	    public Component getListCellRendererComponent( JList list, Object value,
	    											   int index,	boolean isSelected,
	    											   boolean bHasFocus )
	    {
	    	// The DefaultListCellRenderer class will take care of
	    	// the JLabels text property, it's foreground and background
	    	//colors, and so on.
	    	super.getListCellRendererComponent( list, value, index, isSelected, bHasFocus );

	    	if ( ( ( String )value ).contains( ".class" ) )
	    	{
	    		setIcon( CLASS_ICON );
	    	}

	    	else if ( ( ( String )value ).contains( ".dll" ) || ( ( String )value ).contains( ".so" ) )
	    	{
	    		setIcon( LIBS_ICON );
	    	}

	    	else
	    	{
	    		setIcon( DEFAULT_ICON );
	    	}

	    	return this;
	    }
	}
}
