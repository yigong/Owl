package owl.main.owltypes;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
********************************************************************************
*   OwlFileFilter Class
********************************************************************************
*	This class creates a javax.swing.filechooser.FileFilter for use with swing
*   filechooser dialogs.
*
*	@see javax.swing.filechooser.FileFilter
*	@version 1.00
*	@author Scott Streit
********************************************************************************
*/
public class OwlFileFilter extends FileFilter
{
	String extension;
	String description;

	public OwlFileFilter( String ext, String desc )
	{
		extension   = ext;
		description = desc;
	}

	// Accept all directories and all files.
	@Override
	public boolean accept( File file )
	{
		if ( file.isDirectory() )
		{
			return true;
		}

		// Retrieve the file name.
		String filename = file.getName();

		// Get the location of the '.' in the filename.
		int periodIndex = filename.lastIndexOf( '.' );

		String ext = filename.substring( periodIndex + 1 ).toLowerCase();

		if ( ext.equals( extension ) )
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
	*   getDescription
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
    	return description;
    }
}
