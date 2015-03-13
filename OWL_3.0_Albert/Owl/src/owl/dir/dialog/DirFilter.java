package owl.dir.dialog;

import java.io.File;
import java.io.FileFilter;

public class DirFilter implements FileFilter
{
	public boolean accept( File arg0 )
	{
		if ( arg0.isDirectory() && !arg0.isHidden() )
			return true;

		return false;
	}
}
