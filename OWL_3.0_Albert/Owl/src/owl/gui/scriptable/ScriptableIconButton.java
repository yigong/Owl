package owl.gui.scriptable;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import owl.main.MainApp;
import owl.main.scripting.ScriptRunnable;


public class ScriptableIconButton extends ScriptableButton
{
	private static final long serialVersionUID = -4354505878518517689L;

	//--------------------------------------------------------------------------
    //   Private Variables:
    //--------------------------------------------------------------------------
	private ImageIcon stopIcon;
	private File iconFile;

    //--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
	public ScriptableIconButton( String iconName, ActionListener al, String stdAction )
	{
		super( al, stdAction );

		if ( iconName != null )
		{
			ImageIcon icon = new ImageIcon( MainApp.getBitmapPath() + iconName );
			setIcon( icon );
			setSize( icon.getIconWidth(), icon.getIconHeight() );
			setFocusPainted( false );
			setContentAreaFilled( false );
		}

		stopIcon       = new ImageIcon( MainApp.getBitmapPath() + "Stop5.gif" );
		iconFile       = null;
	}

    //--------------------------------------------------------------------------
    //   Static Constructors:
    //--------------------------------------------------------------------------
	public static ScriptableIconButton create( String iconName, ActionListener al, String stdAction )
	{
		return ( new ScriptableIconButton( iconName, al, stdAction ) );
	}

	public static ScriptableIconButton create( String iconName, String tooltip, Dimension dim, ActionListener al, String stdAction )
	{
		ScriptableIconButton button = create( iconName, al, stdAction );
		button.setPreferredSize( dim );
		button.setToolTipText( tooltip );
		return button;
	}

    //--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------
	public String getIconFilename()
	{
		String filename = null;

		try
		{
			if ( iconFile != null )
				filename = iconFile.getCanonicalPath();
		}
		catch ( java.io.IOException ioe ) {}

		return filename;
	}

	public ImageIcon getStopIcon()
	{
		return stopIcon;
	}

	public void setIcon( File icon_file ) throws Exception
	{
		iconFile = icon_file;

		if ( iconFile.exists() )
		{
			ImageIcon icon = new ImageIcon( iconFile.getAbsolutePath() );
			icon.setDescription( iconFile.getName() );
			super.setIcon( icon );
		}
		else
		{
			iconFile = null;
			throw new Exception( "Invalid icon passed to ScriptableButton!" );
		}
	}

	public void setStopIcon( String filename ) throws Exception
	{
		stopIcon = null;
		System.gc();
		stopIcon = new ImageIcon( filename );
	}

	public void setTextLabel( String text ) throws Exception
	{
		if ( !text.equals( "" ) )
			super.setText( text );

		else
			throw new Exception( "Invalid text label passed to ScriptableButton!" );
	}

	@Override
	public void actionPerformed( ActionEvent ae )
	{
		// |  "Run script" handler
		// +--------------------------------------------------------------
		if ( ae.getActionCommand().contains( RUN_SCRIPT_ACTION ) )
		{
			if ( scriptRunnable == null )
			{
				scriptRunnable = new ScriptRunnable(
											getScriptFilename(),
											this,
											getIcon(),
											getStopIcon(),
											RUN_SCRIPT_ACTION,
											STOP_SCRIPT_ACTION,
											callback,
											callbackObjs );
			}
			else
			{
				scriptRunnable.setNewFile( getScriptFilename(), null, null );
			}

			new Thread( scriptRunnable ).start();
		}
	}
}
