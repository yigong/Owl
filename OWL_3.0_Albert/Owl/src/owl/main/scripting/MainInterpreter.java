package owl.main.scripting;

import java.io.File;
import java.net.URL;

import owl.main.MainApp;

import bsh.Interpreter;


public class MainInterpreter
{
    //--------------------------------------------------------------------------
    //   Public Variables:
    //--------------------------------------------------------------------------
    public static Interpreter interpreter = null;

	static
	{
		interpreter = new Interpreter();
		setStdInterpreterInfo();
	}

	public static Interpreter get()
	{
		return interpreter;
	}

	public static boolean containsVariable( String varName )
	{
		boolean bFound = false;

		String[] varNameArr =
			interpreter.getNameSpace().getVariableNames();

		for ( int i=0; i<varNameArr.length; i++ )
		{
			if ( varNameArr[ i ].equals( varName ) )
			{
				bFound = true;
				break;
			}
		}

		return bFound;
	}

	// +--------------------------------------------------------------------+
	// |  setInterpreterObjects                                             |
	// +--------------------------------------------------------------------+
	// |  Sets the specified variables and objects on the current           |
	// |  interpreter.                                                      |
	// +--------------------------------------------------------------------+
	public static void setInterpreterObjects( String[] varNames, Object[] varObjects )
	{
		if ( interpreter == null ) { return; }

		try
		{
			if ( varNames != null && varObjects != null && varNames.length == varObjects.length )
			{
				for ( int i=0; i<varNames.length; i++ )
				{
					interpreter.set( varNames[ i ], varObjects[ i ] );
					MainApp.info( "Providing script access for \"" + varNames[ i ] + "\"" );
				}
			}
		}
		catch ( Exception e )
		{
			MainApp.error( e );
		}
	}

	// +--------------------------------------------------------------------+
	// |  clearInterpreter                                                  |
	// +--------------------------------------------------------------------+
	// |  Clears all variables from the current interpreter and loads it    |
	// |  with the specified ones. Re-load the script classes in            |
	// |  Owl/Scripts. This needs to be done or changes made to user files  |
	// |  won't be seen by the interpreter.                                 |
	// +--------------------------------------------------------------------+
	public static void clearInterpreter()
	{
		try
		{
			setStdInterpreterInfo();
		}
		catch ( Exception e )
		{
			MainApp.error( e );
		}
	}

	// +--------------------------------------------------------------------+
	// |  setStdInterpreterInfo                                             |
	// +--------------------------------------------------------------------+
	// |  Sets the default paths and variable names that all scripts have   |
	// |  access to!                                                        |
	// +--------------------------------------------------------------------+
	public static void setStdInterpreterInfo()
	{
		setStdInterpreterInfo( interpreter );
	}

	// +--------------------------------------------------------------------+
	// |  setStdInterpreterInfo                                             |
	// +--------------------------------------------------------------------+
	// |  Sets the default paths and variable names that all scripts have   |
	// |  access to!                                                        |
	// +--------------------------------------------------------------------+
	public static void setStdInterpreterInfo( Interpreter interp )
	{
		try
		{
			interp.getNameSpace().clear();

			interp.getClassManager().addClassPath( ( new java.io.File( MainApp.getAppPath() ) ).toURI().toURL() );
			interp.getClassManager().addClassPath( ( new java.io.File( MainApp.getScriptsPath() ) ).toURI().toURL() );
			interp.getNameSpace().importCommands( "Scripts" );

			interp.set( "logger", MainApp.getLogger() );
			interp.set( "interp", interpreter );
			interp.set( "bitmapPath", MainApp.getBitmapPath() );
			interp.set( "xmlPath", MainApp.getXMLPath() );

			interp.eval( "import javax.swing.*;" );
			interp.eval( "import owl.cameraAPI.CameraAPI;" );
			interp.eval( "import owl.cameraAPI.ReplyException;" );
			interp.eval( "import owl.main.MainApp;" );
			interp.eval( "import owl.plot.*;" );
		}
		catch ( Exception e )
		{
			MainApp.error( e );
		}
	}

	// +--------------------------------------------------------------------+
	// |  getInterpreterInfo                                                |
	// +--------------------------------------------------------------------+
	// |  Prints the current interpreter variables and paths to log window. |
	// +--------------------------------------------------------------------+
	public static Object[][] getInterpreterInfo()
	{
		//  Get interpreter variable, method and classpath names
		// +-----------------------------------------------------+
		String[] varNames     = interpreter.getNameSpace().getVariableNames();
		String[] methodNames  = interpreter.getNameSpace().getMethodNames();
		URL[]    classPathURL = null;

		try
		{
			bsh.classpath.BshClassPath classPath = bsh.classpath.BshClassPath.getUserClassPath();
			classPathURL = classPath.getPathComponents();
		}
		catch ( bsh.ClassPathException cpe ) {}

		//  Create the 2-D object array
		// +-----------------------------------------------------+
		int dSize = ( varNames != null ? varNames.length : 0 ) +
					( methodNames != null ? methodNames.length : 0 ) +
					( classPathURL != null ? classPathURL.length : 0 ) + 1;

		Object[][] interpInfo = new Object[ dSize ][ dSize ];

		int dIndex = 0;

		//  Add the interpreter version
		// +-----------------------------------------------------+
		interpInfo[ dIndex ][ 0 ] = "Interpreter Version";
		interpInfo[ dIndex ][ 1 ] = Interpreter.VERSION;
		dIndex++;

		//  Add variable names if they exist
		// +-----------------------------------------------------+
		if ( varNames != null )
		{
			for ( int i=0; i<varNames.length; i++ )
			{
				try
				{
					String sVarVal = interpreter.getNameSpace().getVariable( varNames[ i ] ).toString();

					interpInfo[ dIndex ][ 0 ] = "[ Variable ]  \"" + varNames[ i ] + "\"";
					interpInfo[ dIndex ][ 1 ] = sVarVal;
					dIndex++;
				}
				catch ( bsh.UtilEvalError uee ) {}
			}
		}

		//  Add method names if they exist
		// +-----------------------------------------------------+
		if ( methodNames != null )
		{
			for ( int i=0; i<methodNames.length; i++ )
			{
				interpInfo[ dIndex ][ 0 ] = "[ Method ]";
				interpInfo[ dIndex ][ 1 ] = methodNames[ i ];
				dIndex++;
			}
		}

		//  Add classpath url's if they exist
		// +-----------------------------------------------------+
		if ( classPathURL != null )
		{
			for ( int i=0; i<classPathURL.length; i++ )
			{
				interpInfo[ dIndex ][ 0 ] = "[ Class Path ]";
				interpInfo[ dIndex ][ 1 ] = classPathURL[ i ].toString();
				dIndex++;
			}
		}

		return interpInfo;
	}

	// +--------------------------------------------------------------------+
	// |  addPathToInterpreter                                              |
	// +--------------------------------------------------------------------+
	// |  Adds the specified path to the current interpreter.               |
	// +--------------------------------------------------------------------+
	public static void addPathToInterpreter( String scriptFile )
	{
		try
		{
			// Split the user script path as follows:
			// /xxx/yyy/zzz/ --split--> userClassPath = /xxx/yyy/, userScriptPath = /zzz
			// This is the way paths must be added to the paths to the BSH interpreter.
			String scriptPath = ( new java.io.File ( scriptFile ) ).getParent();
			String userClassPath = scriptPath.substring( 0, scriptPath.lastIndexOf( System.getProperty( "file.separator" ) ) );
			String userScriptPath = scriptPath.substring( scriptPath.lastIndexOf( System.getProperty( "file.separator" ) ) + 1 );

			interpreter.getClassManager().addClassPath( ( new File( userClassPath ) ).toURI().toURL() );
			interpreter.getNameSpace().importCommands( userScriptPath );
		}
		catch ( java.lang.Exception e )
		{
			MainApp.error( "Failed to add user script path to bsh interpreter!" );
			MainApp.error( e );
		}
	}
}
