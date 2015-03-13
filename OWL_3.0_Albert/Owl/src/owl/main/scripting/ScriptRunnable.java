package owl.main.scripting;

import java.io.File;
import java.lang.reflect.Method;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import owl.gui.scriptable.ScriptableTextButton;
import owl.main.MainApp;
import owl.main.owltypes.OwlBoldButton;


public class ScriptRunnable implements Runnable
{
	private AbstractButton	button;
	private Icon			normalIcon;
	private Icon			stopIcon;
	private String			normalText;
	private String			stopText;
	private String			normalActionCommand;
	private String			stopActionCommand;
	private String			scriptFilename;
	private String			scriptCode;
	private Method			finalMethodToCall;	// Must be a static method!
	private Object[]		finalMethodObjects;
	private String[]		variableNames;
	private Object[]		variableObjects;


	public ScriptRunnable( String file, AbstractButton button, Icon normal,
						   Icon stop, String normalActionCmd, String stopActionCmd )
	{
		this.scriptFilename			= file;
		this.scriptCode				= null;
		this.button					= button;
		this.normalIcon				= normal;
		this.stopIcon				= stop;
		this.normalText				= null;
		this.stopText				= null;
		this.normalActionCommand	= normalActionCmd;
		this.stopActionCommand		= stopActionCmd;
		this.finalMethodToCall		= null;
		this.finalMethodObjects		= null;
		this.variableNames			= null;
		this.variableObjects		= null;
	}

	public ScriptRunnable( String file, AbstractButton button, Icon normal,
						   Icon stop, String normalActionCmd, String stopActionCmd,
						   Method finalMethod )
	{
		this.scriptFilename			= file;
		this.scriptCode				= null;
		this.button					= button;
		this.normalIcon				= normal;
		this.stopIcon				= stop;
		this.normalText				= null;
		this.stopText				= null;
		this.normalActionCommand	= normalActionCmd;
		this.stopActionCommand		= stopActionCmd;
		this.finalMethodToCall		= finalMethod;
		this.finalMethodObjects		= null;
		this.variableNames			= null;
		this.variableObjects		= null;
	}

	public ScriptRunnable( String file, AbstractButton button, Icon normal,
						   Icon stop, String normalActionCmd, String stopActionCmd,
						   Method finalMethod, Object[] finalMethodObjects )
	{
		this.scriptFilename			= file;
		this.scriptCode				= null;
		this.button					= button;
		this.normalIcon				= normal;
		this.stopIcon				= stop;
		this.normalText				= null;
		this.stopText				= null;
		this.normalActionCommand	= normalActionCmd;
		this.stopActionCommand		= stopActionCmd;
		this.finalMethodToCall		= finalMethod;
		this.finalMethodObjects		= finalMethodObjects;
		this.variableNames			= null;
		this.variableObjects		= null;
	}

	public ScriptRunnable( String file, AbstractButton button, String normalText,
						   String stopText, String normalActionCmd,
						   String stopActionCmd, Method finalMethod,
						   Object[] finalMethodObjects )
	{
		this.scriptFilename			= file;
		this.scriptCode				= null;
		this.button					= button;
		this.normalIcon				= null;
		this.stopIcon				= null;
		this.normalText				= normalText;
		this.stopText				= stopText;
		this.normalActionCommand	= normalActionCmd;
		this.stopActionCommand		= stopActionCmd;
		this.finalMethodToCall		= finalMethod;
		this.finalMethodObjects		= finalMethodObjects;
		this.variableNames			= null;
		this.variableObjects		= null;
	}
	
	public ScriptRunnable( String file, AbstractButton button, String normalActionCmd,
						   String stopActionCmd )
	{
		this.scriptFilename			= file;
		this.scriptCode				= null;
		this.button					= button;
		this.normalIcon				= null;
		this.stopIcon				= null;
		this.normalText				= null;
		this.stopText				= null;
		this.normalActionCommand	= normalActionCmd;
		this.stopActionCommand		= stopActionCmd;
		this.finalMethodToCall		= null;
		this.finalMethodObjects		= null;
		this.variableNames			= null;
		this.variableObjects		= null;
	}

	public ScriptRunnable( String file, AbstractButton button, String stopLabel )
	{
		this.scriptFilename			= file;
		this.scriptCode				= null;
		this.button					= button;
		this.normalIcon				= null;
		this.stopIcon				= null;
		this.normalText				= null;
		this.stopText				= stopLabel;
		this.normalActionCommand	= null;
		this.stopActionCommand		= null;
		this.finalMethodToCall		= null;
		this.finalMethodObjects		= null;
		this.variableNames			= null;
		this.variableObjects		= null;
	}

	public ScriptRunnable( String file, String[] varNames, Object[] varObjs )
	{
		this.scriptFilename			= file;
		this.scriptCode				= null;
		this.button					= null;
		this.normalIcon				= null;
		this.stopIcon				= null;
		this.normalText				= null;
		this.stopText				= null;
		this.normalActionCommand	= null;
		this.stopActionCommand		= null;
		this.finalMethodToCall		= null;
		this.finalMethodObjects		= null;
		this.variableNames			= varNames;
		this.variableObjects		= varObjs;
	}

	public ScriptRunnable( String scriptCode )
	{
		this.scriptFilename			= null;
		this.scriptCode				= scriptCode;
		this.button					= null;
		this.normalIcon				= null;
		this.stopIcon				= null;
		this.normalText				= null;
		this.stopText				= null;
		this.normalActionCommand	= null;
		this.stopActionCommand		= null;
		this.finalMethodToCall		= null;
		this.finalMethodObjects		= null;
		this.variableNames			= null;
		this.variableObjects		= null;
	}

	public void setNewFile( String file, String[] varNames, Object[] varObjs )
	{
		this.scriptFilename			= file;
		this.variableNames			= varNames;
		this.variableObjects		= varObjs;
	}

	public void stop()
	{
		try
		{
			MainInterpreter.get().set( "stop", true );
		}
		catch ( bsh.EvalError ee )
		{
			MainApp.error( ee.toString() );
		}
		catch ( Exception e )
		{
		}
	}

	public void run()
	{
		//  Set the thread name for the profiler
		// +-----------------------------------------------------------------------------------+
		if ( scriptFilename != null )
		{
			Thread.currentThread().setName( "Owl - Script Runnable( " + scriptFilename  + " )" );
		}

		else if ( scriptCode != null )
		{
			Thread.currentThread().setName( "Owl - Script Runnable( " + scriptCode  + " )" );
		}

		else if ( finalMethodToCall != null )
		{
			Thread.currentThread().setName( "Owl - Script Runnable( " + finalMethodToCall  + " )" );
		}

		else
		{
			Thread.currentThread().setName( "Owl - Script Runnable( ???? )" );
		}

		//  Set the variable objects and names in the interpreter
		// +-----------------------------------------------------------------------------------+
		if ( variableNames != null && variableObjects != null )
		{
			MainInterpreter.setInterpreterObjects( variableNames, variableObjects );
		}

		//  Run the script
		// +-----------------------------------------------------------------------------------+
		runScript();
	}

	private void runScript()
	{
		if ( scriptFilename != null )
		{
			addPathToInterpreter( scriptFilename );
		}

		try
		{
			if ( stopActionCommand != null )
			{
				button.setActionCommand( stopActionCommand );

				if ( OwlBoldButton.class.isInstance( button ) )
				{
					( ( OwlBoldButton )button ).setColored();
				}
				else if ( ScriptableTextButton.class.isInstance( button ) )
				{
					( ( ScriptableTextButton )button ).setColored( true );
				}
			}

			if ( stopIcon != null )
			{
				button.setText( "" );
				button.setIcon( stopIcon );
			}
			else if ( stopText != null )
			{
				button.setText( stopText );
			}

			MainInterpreter.get().set( "stop", true );

			if ( scriptFilename != null )
			{
				MainInterpreter.get().source( scriptFilename );
			}
			else if ( scriptCode != null )
			{
				MainInterpreter.get().eval( scriptCode );
			}

			if ( finalMethodToCall != null )	// Must be a static method!
			{
				finalMethodToCall.invoke( null, finalMethodObjects );
			}
		}
		catch ( java.lang.StackOverflowError soe )
		{
			MainApp.error( soe.toString() );
		}
		catch ( java.lang.Exception e )
		{
			MainApp.error( e );
		}
		finally
		{
			if ( normalActionCommand != null )
			{
				button.setActionCommand( normalActionCommand );
			}

			if ( stopIcon != null )
			{
				if ( normalIcon == null )
				{
					if ( normalText != null )
					{
						button.setText( normalText );
					}

					button.setIcon( null );
				}
				else
					button.setIcon( normalIcon );
			}
			else if ( stopText != null )
			{
				if ( normalText != null )
				{
					button.setText( normalText );
				}
			}

			if ( OwlBoldButton.class.isInstance( button ) )
			{
				( ( OwlBoldButton )button ).setBlack();
			}
			else if ( ScriptableTextButton.class.isInstance( button ) )
			{
				( ( ScriptableTextButton )button ).setColored( false );
			}
		}
	}

	private void addPathToInterpreter( String scriptFile )
	{
		try
		{
			// Split the user script path as follows:
			// /xxx/yyy/zzz/ --split--> userClassPath = /xxx/yyy/, userScriptPath = /zzz
			// This is the way paths must be added to the paths to the BSH interpreter.
			String scriptPath = ( new java.io.File ( scriptFile ) ).getParent();
			String userClassPath = scriptPath.substring( 0, scriptPath.lastIndexOf( System.getProperty( "file.separator" ) ) );
			String userScriptPath = scriptPath.substring( scriptPath.lastIndexOf( System.getProperty( "file.separator" ) ) + 1 );

			MainInterpreter.get().getClassManager().addClassPath( ( new File( userClassPath ) ).toURI().toURL() );
			MainInterpreter.get().getNameSpace().importCommands( userScriptPath );
		}
		catch ( java.lang.Exception e )
		{
			MainApp.error( "Failed to add user script path to bsh interpreter!" );
			MainApp.error( e );
		}
	}
}
