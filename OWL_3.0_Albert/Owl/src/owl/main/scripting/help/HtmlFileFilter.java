package owl.main.scripting.help;

import java.io.File;
import java.io.FilenameFilter;

public class HtmlFileFilter implements FilenameFilter
{
	public boolean accept( File dir, String name )
	{
		if ( name.endsWith( "html" ) )
			return true;

		return false;
	}
}
