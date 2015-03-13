package owl.main.owltypes;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import owl.main.MainApp;


public class OwlDirChooser extends JFileChooser implements FilenameFilter
{
	private static final long serialVersionUID = 5005805055088986464L;
	private File file;
	private String filter;


	public OwlDirChooser( File file )
	{
		super( file.getPath() );

		this.file = file;
		this.filter = "*";

		setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		setDialogType( JFileChooser.CUSTOM_DIALOG );
		setFileView( new OwlDirView() );
	}

	public OwlDirChooser( File file, String filter )
	{
		this( file );
		this.filter = filter;
	}

	public boolean openDialog()
	{
		// Show the Save dialog box (returns the option selected)
		int selected = showDialog( this, "Select" );

		// If the Open button is pressed.
		if ( selected == JFileChooser.APPROVE_OPTION )
		{
			file = getSelectedFile();

			return true;
		}
		else
		{
			return false;
		}
	}

	public File getFile()
	{
		return file;
	}

	public String[] getFileNames( String filter )
	{
		this.filter = filter;

		File[] fileList = file.listFiles( this );
		String[] filenames = new String[ fileList.length ];

		for ( int i=0; i<fileList.length; i++ )
		{
			filenames[i] = fileList[i].getName();
		}

		return filenames;
	}

	public boolean accept( File file, String name )
	{
		// Get the location of the '.' in the filename.
		int periodIndex = name.lastIndexOf( '.' );

		String extension = name.substring( periodIndex + 1 ).toLowerCase();

		// Check for ".lod" files.
		if ( extension.equals( filter ) )
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
		ImageIcon icon = new ImageIcon( MainApp.getBitmapPath() + "owl.gif" );

        JDialog dialog = super.createDialog( parent );
        dialog.setIconImage( icon.getImage() );
 
        return dialog;
    }
}
