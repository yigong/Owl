package owl.main.owltypes;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

import owl.main.MainApp;


//  Used by OwlIconedFileChooser class
// +--------------------------------------------------------------+
public class OwlFileView extends FileView
{
	private final ImageIcon FOLDER_ICON = new ImageIcon( MainApp.getBitmapPath()
														 + "folder.gif" );


	protected ImageIcon[]	m_icon;
	protected String[]		m_ext;
	protected String[]		m_desc;

	public OwlFileView( String[] ext, String[] desc, ImageIcon[] icon )
	{
		m_icon = icon;
		m_ext  = ext;
		m_desc = desc;
	}

	public OwlFileView()
	{
		//  Set a default instance of each
		// +-----------------------------------------------------------+
		this( new String[ 1 ], new String[ 1 ], new ImageIcon[ 1 ] );
	}

	@Override
	public String getName( File file ) { return null; }

	@Override
	public String getDescription( File file ) { return null; }

	@Override
	public String getTypeDescription( File file )
	{
		String extension = getExtension( file );
		String type = null;

		for ( int i=0; i<m_ext.length; i++ )
		{
			if ( extension != null && extension.equals( m_ext[ i ] ) )
			{
				type = m_desc [ i ];

				break;
			}
		}

		return type;
	}

	@Override
	public Icon getIcon( File file )
	{
		String extension = getExtension( file );
		Icon localIcon = null;

		if ( !file.isDirectory() )
		{
			for ( int i=0; i<m_ext.length; i++ )
			{
				if ( extension != null && extension.equals( m_ext[ i ] ) )
				{
					localIcon = m_icon[ i ];

					break;
				}
			}
		}

		else
		{
			localIcon = FOLDER_ICON;
		}

		return localIcon;
	}

	@Override
	public Boolean isTraversable( File file )
	{
		return null;
	}

	private String getExtension( File file )
	{
		String filename = file.getName();
		int periodIndex = filename.lastIndexOf( '.' );
		String extension = null;

		if ( periodIndex > 0 && periodIndex < filename.length() - 1 )
		{
			extension = filename.substring( periodIndex + 1 ).toLowerCase();
		}

		return extension;
	}
}
