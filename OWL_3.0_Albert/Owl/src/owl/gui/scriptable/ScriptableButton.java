package owl.gui.scriptable;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import owl.gui.popupmenus.ScriptPopupMenu;
import owl.main.MainApp;
import owl.main.scripting.ScriptRunnable;


public class ScriptableButton extends JButton implements ActionListener
{
	private static final long serialVersionUID = 4168610366496639235L;

	//--------------------------------------------------------------------------
    //   Private Constants:
    //--------------------------------------------------------------------------
	protected final String RUN_SCRIPT_ACTION	= "ScriptableButton_RUN_SCRIPT";
	protected final String STOP_SCRIPT_ACTION	= "ScriptableButton_STOP_SCRIPT";
	protected final String EDIT_SCRIPT_ACTION	= "ScriptableButton_EDIT_SCRIPT";

	protected final String[] BUTTON_ACTIONS   =	{ null,
												  RUN_SCRIPT_ACTION,
												  EDIT_SCRIPT_ACTION,
												  null };

	//--------------------------------------------------------------------------
    //   Private Variables:
    //--------------------------------------------------------------------------
	protected ScriptPopupMenu	popupMenu;
	protected ScriptRunnable	scriptRunnable;
	protected Method			callback;
	protected Object[]			callbackObjs;

    //--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
	public ScriptableButton( ActionListener al, String stdAction )
	{
		BUTTON_ACTIONS[ ScriptPopupMenu.STD_ACTION ] = stdAction;
		popupMenu = new ScriptPopupMenu( this, BUTTON_ACTIONS );
		popupMenu.setBackground( Color.WHITE );

		setFocusPainted( false );

		scriptRunnable = null;
		callback       = null;

		setActionCommand( popupMenu.getSelectedAction() );
		addMouseListener( new ButtonMouseListener() );
		addActionListener( al );
		addActionListener( this );
	}

    //--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------
	public boolean isScriptSelected()
	{
		boolean isSelected = false;

		if ( popupMenu != null )
		{
			isSelected = popupMenu.isScriptSelected();
		}

		return isSelected;
	}

	public String getScriptFilename()
	{
		return popupMenu.getScriptName();
	}

	public void setRunnable( ScriptRunnable runnable )
	{
		scriptRunnable = runnable;
	}

	public ScriptRunnable getRunnable()
	{
		return scriptRunnable;
	}

	public static int stdActionIndex()
	{
		return ScriptPopupMenu.STD_ACTION;
	}

	public static int scriptActionIndex()
	{
		return ScriptPopupMenu.SCRIPT_ACTION;
	}

	public void setScript( String filename, boolean useScript )
	{
		if ( filename != null && !filename.equals( "" ) )
		{
			popupMenu.setScript( filename, useScript );
		}
		else
		{
			MainApp.warn( "Invalid script filename passed to ScriptableButton!" );
		}
	}

	public void setScriptCallbackMethod( Method callback, Object[] callbackObjs )
	{
		this.callback     = callback;
		this.callbackObjs = callbackObjs;
	}

	public void actionPerformed( ActionEvent ae )
	{
		// |  "Stop script" handler
		// +--------------------------------------------------------------
		if ( ae.getActionCommand().equals( STOP_SCRIPT_ACTION ) )
		{
			if ( scriptRunnable != null )
			{
				scriptRunnable.stop();
			}
		}

		// |  "Warn that no script exists" handler
		// +--------------------------------------------------------------
		else
		{
			if ( ( popupMenu.isScriptSelected() && popupMenu.getScriptName() == null ) ||
			     ( !popupMenu.isStdActionSelected() && popupMenu.getScriptName() == null ) )
			{
				MainApp.warn( "Sorry, but no script is defined for this button!" );
			}
		}
	}

	private class ButtonMouseListener extends MouseAdapter
	{
		// Invoked when the mouse button has been clicked ( pressed
		// and released ) on a component.
		@Override
		public void mouseClicked( MouseEvent e )
		{
			if ( SwingUtilities.isRightMouseButton( e ) && popupMenu != null )
			{
				if ( !getActionCommand().equals( STOP_SCRIPT_ACTION ) )
				{
					popupMenu.show( e.getComponent(), e.getX(), e.getY() );
				}
			}
		}
	}
}
