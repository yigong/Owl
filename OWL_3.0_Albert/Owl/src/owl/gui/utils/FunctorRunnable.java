package owl.gui.utils;

import java.lang.reflect.Method;
import javax.swing.ImageIcon;
import owl.main.MainApp;
import owl.main.owltypes.OwlMessageBox;


public class FunctorRunnable implements Runnable
{
	private Method method;
	private Method callbackMethod;
	private Object callbackObject;
	private Integer[] args = { -1, -1, -1, -1, -1, -1 };
	private Boolean boolArg;
	private Object retVal;
	private boolean useCameraCommand;
	private String infoMsg;
	private ImageIcon icon;

	public FunctorRunnable( Class<?> clazz, String methodName, Object callbackObj, String callbackName, String infoMsg )
	{
		useCameraCommand = false;
		this.infoMsg     = infoMsg;
		this.icon        = null;
		this.boolArg     = null;
		GenericConstructorCore( clazz, methodName, callbackObj, callbackName );
	}

	public FunctorRunnable( Class<?> clazz, String methodName, Object callbackObj, String callbackName, String infoMsg, ImageIcon icon )
	{
		useCameraCommand = false;
		this.infoMsg     = infoMsg;
		this.icon        = icon;
		this.boolArg     = null;
		GenericConstructorCore( clazz, methodName, callbackObj, callbackName );
	}

	public FunctorRunnable( Class<?> clazz, String methodName, Object callbackObj, String callbackName, Boolean boolArg, String infoMsg, ImageIcon icon, Object ExpectedRetVal )
	{
		useCameraCommand  = false;
		this.infoMsg      = infoMsg;
		this.retVal       = ExpectedRetVal;
		this.icon         = icon;
		this.boolArg      = boolArg;
		GenericConstructorCore( clazz, methodName, callbackObj, callbackName );
	}
	
	public FunctorRunnable( Class<?> clazz, String methodName, Object callbackObj, String callbackName, Object[] args, String infoMsg, ImageIcon icon, Object ExpectedRetVal )
	{
		useCameraCommand  = true;
		this.infoMsg      = infoMsg;
		this.retVal       = ExpectedRetVal;
		this.icon         = icon;
		this.boolArg      = null;

		if ( args != null )
		{
			// Ensure the correct number of arguments exist
	 		if ( args.length < 1 || args.length > this.args.length )
			{
				MainApp.error( "Invalid number of arguments" );
				return;
			}
	
	   		for ( int i=0; i<args.length; i++ )
	   		{
				this.args[ i ] = ( ( Integer )args[ i ] ).intValue();
	   		}
		}

		// Get the method to invoke
		Method[] m = clazz.getMethods();
		method = null;

		for ( int i=0; i<m.length; i++ )
		{
			if ( m[ i ].getName().equals( methodName ) )
			{
				method = m[ i ];
				break;
			}
		}

		// Get the callback method to invoke when done
		if ( callbackObj != null && callbackName != null )
		{
			Method[] cm = callbackObj.getClass().getMethods();
			callbackMethod = null;
			callbackObject = callbackObj;

			for ( int i=0; i<cm.length; i++ )
			{
				if ( cm[ i ].getName().equals( callbackName ) )
				{
					callbackMethod = cm[ i ];
					break;
				}
			}
		}
	}

	public void run()
	{
		Thread.currentThread().setName( "Owl - Functor Runnable( " + method.getName() + " )" );

		OwlMessageBox msgDialog = null;

		// Display a small tool-tip type message for user satisfaction
		if ( infoMsg != null && !infoMsg.equals( "" ) )
		{
			msgDialog = new OwlMessageBox( infoMsg + " ... please wait!", icon );
			msgDialog.start();
		}

		if ( !useCameraCommand )
		{
			callGenericMethod();
		}
		else
		{
			callCameraCommand();
		}

		if ( msgDialog != null )
		{
			msgDialog.stop();
		}
	}

	private void GenericConstructorCore( Class<?> clazz, String methodName, Object callbackObj, String callbackName )
	{
		// Get the method to invoke
		Method[] m = clazz.getMethods();
		method = null;

		for ( int i=0; i<m.length; i++ )
		{
			if ( m[ i ].getName().equals( methodName ) )
			{
				method = m[ i ];
				break;
			}
		}

		// Get the callback method to invoke when done
		if ( callbackObj != null && callbackName != null )
		{
			Method[] cm = callbackObj.getClass().getMethods();
			callbackMethod = null;
			callbackObject = callbackObj;

			for ( int i=0; i<cm.length; i++ )
			{
				if ( cm[ i ].getName().equals( callbackName ) )
				{
					callbackMethod = cm[ i ];
					break;
				}
			}
		}
	}

	private void callGenericMethod()
	{
		try
		{
			if ( method != null )
			{
				if ( infoMsg != null ) { MainApp.infoStart( infoMsg ); }

				Object classInstance = method.getDeclaringClass().newInstance();
				Object retVal = null;

				if ( boolArg != null )
				{
					retVal = method.invoke( classInstance, boolArg );
				}
				else
				{
					retVal = method.invoke( classInstance, ( Object[] )null );
				}

				if ( callbackObject != null && callbackMethod != null )
				{
					callbackMethod.invoke( callbackObject, retVal );
				}

				if ( infoMsg != null ) { MainApp.infoEnd(); }
			}
			else
			{
				MainApp.error( "Attempt to invoke NULL method!" );
			}
		}
		catch ( java.lang.InstantiationException ie )
		{
			if ( infoMsg != null ) { MainApp.infoFail(); }
			MainApp.error( ie );
		}
		catch ( java.lang.IllegalAccessException iae )
		{
			if ( infoMsg != null ) { MainApp.infoFail(); }
			MainApp.error( iae );
		}
		catch ( java.lang.IllegalArgumentException iarge )
		{
			if ( infoMsg != null ) { MainApp.infoFail(); }
			MainApp.error( iarge );
		}
		catch ( java.lang.reflect.InvocationTargetException ite )
		{
			if ( infoMsg != null ) { MainApp.infoFail(); }
			MainApp.error( ite.getCause().getMessage() );
		}
		catch ( Exception e )
		{
			if ( infoMsg != null ) { MainApp.infoFail(); }
			MainApp.error( e );
		}
	}

	private void callCameraCommand()
	{
		try
		{
			if ( method != null )
			{
				if ( infoMsg != null ) { MainApp.infoStart( infoMsg ); }

				Object classInstance = method.getDeclaringClass().newInstance();
				Object retVal = null;

				if ( method.getName().contains( "PCI" ) )
				{
					retVal = method.invoke( classInstance, args[ 0 ] );
				}
				else
				{
					retVal = method.invoke( classInstance,
											args[ 0 ],
											args[ 1 ],
											args[ 2 ],
											args[ 3 ],
											args[ 4 ],
											args[ 5 ] );
				}

				if ( callbackObject != null && callbackMethod != null )
				{
					callbackMethod.invoke( callbackObject, retVal );
				}

				if ( infoMsg != null && this.retVal != null )
				{
					if ( retVal.equals( this.retVal ) )
					{
						MainApp.infoEnd();
					}
					else
					{
						MainApp.infoFail();
					}
				}
				else if ( infoMsg != null && this.retVal == null )
				{
					MainApp.infoEnd();
				}
			}
			else
			{
				MainApp.error( "Attempt to invoke NULL method!" );
			}
		}
		catch ( Exception e )
		{
			if ( infoMsg != null ) { MainApp.infoFail(); }
			MainApp.error( e );
		}
	}
}
