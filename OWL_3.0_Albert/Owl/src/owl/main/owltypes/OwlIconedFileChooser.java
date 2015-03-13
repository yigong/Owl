package owl.main.owltypes;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import owl.main.MainApp;


public class OwlIconedFileChooser extends JFileChooser
{
	private static final long serialVersionUID = 4464330588374069289L;

	private ImageIcon m_winIcon;

	public OwlIconedFileChooser( File initFile, String[] ext, String[] desc, ImageIcon[] icons )
	{
		super( initFile.getPath() );

		m_winIcon = new ImageIcon( MainApp.getBitmapPath() + "owl.gif" );

		if ( ext != null && desc != null && ext.length == desc.length )
		{
			OwlFileView fileView = new OwlFileView( ext, desc, icons );

			// Remove the "All Files *.*" filter
			FileFilter[] existingFileFilters = getChoosableFileFilters();
			removeChoosableFileFilter( existingFileFilters[ 0 ] );

			for ( int i=0; i<ext.length; i++ )
			{
				addChoosableFileFilter( new OwlFileFilter( ext[ i ], desc[ i ] ) );
			}

			// Select the first filter in the list
			existingFileFilters = getChoosableFileFilters();
			setFileFilter( existingFileFilters[ 0 ] );

			setFileView( fileView );
		}
	}

	public void setFrameIcon( String iconFileName )
	{
		m_winIcon = new ImageIcon( MainApp.getBitmapPath() + iconFileName );
	}

	public boolean openDialog()
	{
		// Show the Open dialog box (returns the option selected)
		int selected = showOpenDialog( this );

		// If the Open button is pressed.
		if ( selected == JFileChooser.APPROVE_OPTION )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean saveDialog()
	{
		// Show the Save dialog box (returns the option selected)
		int selected = showSaveDialog( this );

		// If the Save button is pressed.
		if ( selected == JFileChooser.APPROVE_OPTION )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean filterEquals( String str )
	{
		if ( getFileFilter().getDescription().equals( str ) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	//  This method is overridden to put the Owl icon on the JFileChooser
	// +-------------------------------------------------------------------+
	@Override
	protected JDialog createDialog( Component parent ) throws HeadlessException
	{
        JDialog dialog = super.createDialog( parent );
        dialog.setIconImage( m_winIcon.getImage() );
 
        return dialog;
    }
}
