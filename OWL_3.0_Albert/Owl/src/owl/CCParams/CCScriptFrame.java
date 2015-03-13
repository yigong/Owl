package owl.CCParams;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import owl.main.MainApp;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlPrefs;


public class CCScriptFrame extends OwlFrame implements ActionListener
{
	private static final long serialVersionUID = 6614889680379850276L;

	private CCScriptEvent ccEvent;
	private ArrayList<CCScriptListener> scriptListeners;
	private boolean hasBeenApplied;
	private OwlPrefs owlPrefs;

	public CCScriptFrame( String title )
	{
		super( title, false );

		ccEvent         = new CCScriptEvent( this, null, null, null );
		hasBeenApplied  = false;
		scriptListeners = null;
		owlPrefs        = new OwlPrefs( this );

		MainApp.mainFrame.exitListener.add( this );
	}

	public String getAction()
	{
		return ccEvent.action;
	}

	public void setAction( String aString )
	{
		ccEvent.action = aString;
	}

	public String getDescription()
	{
		return ccEvent.description;
	}

	public void setDescription( String aDescription )
	{
		ccEvent.description = aDescription;
	}

	public void setObject( Object anObject )
	{
		ccEvent.object = anObject;
	}

	public void addScriptListener( CCScriptListener ccsl )
	{
		if ( scriptListeners == null )
		{
			scriptListeners = new ArrayList<CCScriptListener>();
		}

		scriptListeners.add( ccsl );
	}

	public void fireScriptApplied()
	{
		if ( scriptListeners != null )
		{
			for ( int i=0; i<scriptListeners.size(); i++ )
			{
				scriptListeners.get( i ).CCScriptChanged( ccEvent );
			}
		}
	}

	public boolean isApplied()
	{
		return hasBeenApplied;
	}

	public void setApplied( boolean aFlag )
	{
		hasBeenApplied = aFlag;
	}

	@Override
	public void loadPreferences()
	{
		super.loadPreferences();
		owlPrefs.loadPreferences();
	}

	@Override
	public void savePreferences()
	{
		super.savePreferences();
		owlPrefs.savePreferences();
	}
}
