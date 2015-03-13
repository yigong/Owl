package owl.main.owltypes;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import javax.swing.ImageIcon;

import owl.main.MainApp;


public class OwlFileDialog extends FileDialog
{
	private static final long serialVersionUID = -411048985112453863L;

	private ImageIcon m_winIcon;

	public OwlFileDialog( File initFile )
	{
		super( ( Frame )null );

		m_winIcon = new ImageIcon( MainApp.getBitmapPath() + "owl.gif" );
		setIconImage( m_winIcon.getImage() );

		setFile( initFile.getAbsolutePath() );
	}

	public void setFrameIcon( String iconFileName )
	{
		m_winIcon = new ImageIcon( MainApp.getBitmapPath() + iconFileName );
	}

	public File getSelectedFile()
	{
		return new File( getFile() );
	}

	public boolean openDialog()
	{
		setVisible( true );

		// Show the Open dialog box (returns the option selected)
		setMode( FileDialog.LOAD );

		// If the Open button is pressed.
		if ( getFile() != null )
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
		setVisible( true );

		// Show the Save dialog box (returns the option selected)
		setMode( FileDialog.SAVE );

		// If the Save button is pressed.
		if ( getFile() != null )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
