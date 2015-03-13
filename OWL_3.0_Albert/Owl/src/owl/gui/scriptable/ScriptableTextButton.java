package owl.gui.scriptable;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import owl.main.scripting.ScriptRunnable;


public class ScriptableTextButton extends ScriptableButton
{
	private static final long serialVersionUID = 2867269398920875364L;

	//--------------------------------------------------------------------------
    //   Private Constants:
    //--------------------------------------------------------------------------
	private final String ABORT_TEXT			= "Abort";

	//--------------------------------------------------------------------------
    //   Private Variables:
    //--------------------------------------------------------------------------
	private String			text;
	private Color			stdBkgColor;

    //--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
	public ScriptableTextButton( String aStr, ActionListener al, String stdAction )
	{
		super( al, stdAction );

		text = aStr;
		setText( text );

		stdBkgColor = getBackground();
	}

	public static ScriptableIconButton
					createButton( String aStr, ActionListener al, String stdAction )
	{
		ScriptableIconButton button = new ScriptableIconButton( aStr, al, stdAction );
		return button;
	}

    //--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------
	public void setColored( boolean aFlag )
	{
		if ( aFlag )
		{
			setBackground( Color.RED );
			setForeground( Color.WHITE );
		}
		else
		{
			setBackground( stdBkgColor );
			setForeground( Color.BLACK );
		}
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
											text,
											ABORT_TEXT,
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
