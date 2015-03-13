package owl.main.owltypes;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

import owl.main.MainApp;

public class OwlDirView extends FileView
{
	private final ImageIcon FOLDER_ICON = new ImageIcon( MainApp.getBitmapPath()
														 + "folder.gif" );

	@Override
	public Icon getIcon( File file )
	{
		if ( file.isDirectory() )
		{
			return FOLDER_ICON;
		}
		else
		{
			return null;
		}
	}
}
