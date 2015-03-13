package owl.main.owltypes;

import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import owl.main.MainApp;


public class OwlToggleButton extends JToggleButton
{
	private static final long serialVersionUID = -3120945540222441802L;


	public OwlToggleButton( Icon normalIcon, Icon selectedIcon )
	{
		super( normalIcon );

		setSelectedIcon( selectedIcon );
		setFocusPainted( false );

		int w = normalIcon.getIconWidth();
		int h = normalIcon.getIconHeight();

		if ( selectedIcon.getIconWidth() > w )
		{
			w = selectedIcon.getIconWidth();
		}

		if ( selectedIcon.getIconHeight() > h )
		{
			h = selectedIcon.getIconHeight();
		}

		setPreferredSize( new Dimension( w + 4, h + 4 ) );
	}

	public OwlToggleButton( String normalIconFilename, String selectedIconFilename )
	{
		this( ( new ImageIcon( MainApp.getBitmapPath() + normalIconFilename ) ),
			  ( new ImageIcon( MainApp.getBitmapPath() + selectedIconFilename ) ) );
	}
}
