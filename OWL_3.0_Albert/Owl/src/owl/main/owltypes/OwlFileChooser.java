package owl.main.owltypes;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import owl.main.MainApp;
import java.util.Vector;


public class OwlFileChooser extends JFileChooser
{
	private static final long serialVersionUID = -1043063766202558285L;
	private ImageIcon m_winIcon;
	private File file;


	public OwlFileChooser( File aFile )
	{
		super( aFile.getPath() );

		file = aFile;
		setFileView( new OwlFileView() );

		m_winIcon = new ImageIcon( MainApp.getBitmapPath() + "owl.gif" );
	}

	public OwlFileChooser( File aFile, String filter )
	{
		super( aFile.getPath() );

		file = aFile;

		// Assign the file filter and discard all others.
		setFileFilter( new OwlFileFilter( filter ) );
		setFileView( new OwlFileView() );

		m_winIcon = new ImageIcon( MainApp.getBitmapPath() + "owl.gif" );
	}

	public OwlFileChooser( File aFile, String[] filters )
	{
		super( aFile.getPath() );

		file = aFile;

		// Assign the file filter and discard all others.
		OwlFileFilter fileFilter = new OwlFileFilter();

		for ( int i=0; i<filters.length; i++ )
		{
			fileFilter.addFilter( filters[ i ] );
		}

		setFileFilter( fileFilter );
		setFileView( new OwlFileView() );

		m_winIcon = new ImageIcon( MainApp.getBitmapPath() + "owl.gif" );
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
			file = getSelectedFile();

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

		// If the Open button is pressed.
		if ( selected == JFileChooser.APPROVE_OPTION )
		{
			file = getSelectedFile();

			// Add a file extension to the file it doesn't have one.
			if ( file.getName().lastIndexOf( "." ) < 0 )
			{
				String extension = ( ( OwlFileFilter )getFileFilter() ).getExtension();

				if ( extension != null )
				{
					file = new File( file.getAbsoluteFile() + "." + extension );
				}
			}

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

	//  This method is overridden to put the Owl icon on the JFileChooser
	// +-------------------------------------------------------------------+
	@Override
	protected JDialog createDialog( Component parent ) throws HeadlessException
	{
        JDialog dialog = super.createDialog( parent );
        dialog.setIconImage( m_winIcon.getImage() );
 
        return dialog;
    }

	public class OwlFileFilter extends FileFilter
	{
		private Vector<String> filters;

		public OwlFileFilter( String filter )
		{
			filters = new Vector<String>();
			filters.add( filter );
		}

		public OwlFileFilter()
		{
			filters = new Vector<String>();
		}

		public void addFilter( String filter )
		{
			filters.add( filter );
		}

		// Parses the description and returns the extension
		public String getExtension()
		{
			int periodIndex  = -1;
			String extension = null;

			periodIndex = getDescription().lastIndexOf( '.' );

			if ( periodIndex >= 0 )
			{
				extension = getDescription().substring( periodIndex + 1 );
			}

			if ( extension != null && !extension.matches( "[a-zA-Z]+" ) )
			{
				extension = null;
			}

			return extension;
		}

		// Accept all directories and all DSP lod files.
		@Override
		public boolean accept( File aFile )
		{
			if ( aFile.isDirectory() )
			{
				return true;
			}

			// Retrieve the file name.
			String filename = aFile.getName();

			// Get the location of the '.' in the filename.
			int periodIndex = filename.lastIndexOf( '.' );

			String extension = filename.substring( periodIndex + 1 ).toLowerCase();

			if ( filters.contains( extension ) )
			{
				return true;
			}
			else
			{
				return false;
			}
	    }

		/**
		********************************************************************************
		*	This method returns the String that will be displayed in the file
		*	description section of the file dialog box.
		*
		*	@see FileFilter
		*	@version 1.00
		*	@author Scott Streit
		********************************************************************************
		*/
	    @Override
		public String getDescription()
	    {
	    	String filterString = "";

	    	for ( int i=0; i<filters.size(); i++ )
	    	{
	    		filterString += "*." + filters.get( i );
	    		if ( filters.size() > 1 )  { filterString += " , "; }
	    	}

	    	return filterString;
	    }
	}
}
