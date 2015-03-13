package owl.main.owltypes;

import java.io.File;
import java.io.FileFilter;

/**
********************************************************************************
*   OwlFileFilterAdapter Class
********************************************************************************
*	This class creates a java.io.FileFilter for use with java.io.File classes
*   to filter the list of files when calling list() and listFiles().
*
*	@see java.io.FileFilter
*	@version 1.00
*	@author Scott Streit
********************************************************************************
*/
public class OwlFileFilterAdapter implements FileFilter
{
	String extension;
	String description;

	public OwlFileFilterAdapter( String ext, String desc )
	{
		extension   = ext;
		description = desc;
	}

	// Accept all directories and all files.
	@Override
	public boolean accept( File file )
	{
		if ( file.isDirectory() )
			return true;

		// Retrieve the file name.
		String filename = file.getName();

		// Get the location of the '.' in the filename.
		int periodIndex = filename.lastIndexOf( '.' );

		String ext = filename.substring( periodIndex + 1 ).toLowerCase();

		System.out.println( "######## FILTER --> " + filename + "  EXT --> " + ext + "  SEARCH EXT --> " + extension );

		if ( ext.equals( extension ) )
			return true;
		else
			return false;
    }
}
