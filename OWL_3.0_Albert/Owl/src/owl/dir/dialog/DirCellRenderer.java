package owl.dir.dialog;

import java.awt.Component;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


public class DirCellRenderer extends JLabel implements ListCellRenderer
{
	private static final long serialVersionUID = -2542854821214624493L;

	ImageIcon folderIcon = null;

	public DirCellRenderer( String iconPath )
	{
		folderIcon = new ImageIcon( iconPath + "folder.gif" );
	}

	public Component getListCellRendererComponent(JList list,
												  Object obj,
												  int index,
												  boolean bSelected,
												  boolean bHasFocus )
	{
		setFont( list.getFont() );

		if ( obj != null && obj instanceof File )
		{
			File file = ( File )obj;

			if ( !file.getName().equals( "" ) )
				setText( file.getName() );
			else
				setText( file.getAbsolutePath() );

			setIcon( folderIcon );
		}

		return this;
	}
}
