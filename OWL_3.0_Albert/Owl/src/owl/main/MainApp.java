package owl.main;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.plaf.metal.MetalLookAndFeel;
import owl.gui.utils.OwlUtilities;
import owl.gui.utils.TransparentSplashScreen;
import owl.cameraAPI.CameraAPI;
import owl.logging.OwlLogger;
import owl.main.device.DeviceConnect;
import owl.main.libs.JarLibFrame;
import owl.main.owltypes.OwlLookAndFeelTheme;



public class MainApp
{
    //--------------------------------------------------------------------------
    //   Constants:
    //--------------------------------------------------------------------------
	private final static String OWL_STARTUP_INI_FILE =	"startup.ini";

    //--------------------------------------------------------------------------
    //   Public Constants and Variables:
    //--------------------------------------------------------------------------
	public static final String	VERSION		= "3.0.1";
	public static MainFrame		mainFrame	= null;
    public static String		appPath		= null;

    //--------------------------------------------------------------------------
    //   Private Variables:
    //--------------------------------------------------------------------------
	private static OwlLogger				logger;
    private static Preferences				preferences;
    private static HashMap<String,String>	paramMap;
    private static boolean					apiOk;
    private static Process					txtEditorProc;

    //--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------

//	private static String[] findIPs() throws java.net.UnknownHostException, java.io.IOException
//	{
//		Vector<String> vList = new Vector<String>();
//
//		System.out.println( "Looking for IP addresses" );
//
//		java.net.InetAddress[] addr = java.net.InetAddress.getAllByName( "192.168.0.1" );
//
//	    for (int i = 0; i < addr.length; i++)
//	      System.out.println(addr[i]);
//
////		java.net.InetAddress localhost = java.net.InetAddress.getLocalHost();
////		// this code assumes IPv4 is used
////		byte[] ip = localhost.getAddress();
////		for ( int i=1; i<=254; i++ )
////		{
////		    ip[ 3 ] = ( byte )i;
////		    java.net.InetAddress address = java.net.InetAddress.getByAddress( ip );
////		    if ( address.isReachable( 250 ) )
////		    {
////		        System.out.println( "machine is turned on and can be pinged: " + address.toString() );
////		    }
////		    else if ( !address.getHostAddress().equals( address.getHostName() ) )
////		    {
////		        System.out.println( "machine is known in a DNS lookup: " + address.toString() );
////		    }
////		    else
////		    {
////		        System.out.println( "the host address and host name are equal," +
////		        		" meaning the host name could not be resolved: " + address.toString() );
////		    }
////		}
//
//		return vList.toArray( new String[ vList.size() ] );
//	}

	public static void main( String[] args )
	{
		try
		{
			MetalLookAndFeel mlaf = new MetalLookAndFeel();
			MetalLookAndFeel.setCurrentTheme( new OwlLookAndFeelTheme() );
			UIManager.setLookAndFeel( mlaf );

			//
			// Override the ToolTip.background color in Swing's defaults table
			//
			UIManager.put( "ToolTip.background", java.awt.Color.white );
		}
		catch ( Exception e ) { e.printStackTrace(); }

		logger        = ( OwlLogger )OwlLogger.getLogger( MainApp.class.getName() );
		preferences   = Preferences.userNodeForPackage( MainApp.class );
		appPath       = args[ 0 ];
		txtEditorProc = null;

		paramMap	  = OwlUtilities.readINIFile( MainApp.getAppPath() +
												  OWL_STARTUP_INI_FILE );

		String splashAudioStr   = getStartupINIFileValue( "SPLASH_AUDIO" );
		boolean playSplashAudio = false;

		if ( splashAudioStr != null )
		{
			playSplashAudio = ( new Boolean( splashAudioStr ) ).booleanValue();
		}

		TransparentSplashScreen splash = new TransparentSplashScreen( playSplashAudio );
		splash.toFront();
		splash.start();

		System.setOut( new owl.logging.StdOutLogWriter() );
		System.setErr( new owl.logging.StdErrLogWriter() );

		try
		{
			CameraAPI.GetAPIConstants();
			CameraAPI.VerifyFieldInitialization();
			apiOk = true;
		}
		catch ( Exception e )
		{
			splash.stop();

			String msg = "Failed to initialize API constants\n"
						 + e.getMessage();

  			String iconName = "CrossBone.gif";
  			java.util.Random rand = new java.util.Random();
  			int val = rand.nextInt( 3 );
  			if ( val == 1 ) iconName = "ErrMsg2.gif";
  			else if ( val == 2 ) iconName = "ErrMsg2.gif";

  			JOptionPane.showMessageDialog( null,
										   msg,
										   "API INITIALIZATION ERROR",
										   JOptionPane.ERROR_MESSAGE,
										   new javax.swing.ImageIcon( getBitmapPath() + iconName )
										  );
 
			System.exit( 1 );
		}

		mainFrame = new MainFrame();
		mainFrame.setVisible( true );

//		if ( RemoteAPIConnect.isAPIRemote() )
//		{
//			MainApp.mainFrame.setTitle( MainFrame.DEFAULT_TITLE +
//										" - REMOTE [ " +
//										RemoteAPIConnect.getHostName() + " : " +
//										RemoteAPIConnect.getServer() + " : " +
//										RemoteAPIConnect.getPort() + " ]" );
//		}

		splash.stop();

		MainApp.connectToDevice( false );

		//
		// Check DS9 version number
		//
		try
		{
			MainApp.infoStart( "Checking DS9 version" );
			owl.display.ds9.DS9Accessor ds9 = new owl.display.ds9.DS9Accessor();
			ds9.version( 6 );
			MainApp.infoEnd();
		}
		catch ( Exception e )
		{
			MainApp.warn( "DS9 version error : " + e.getMessage() );
		}


		//
		// Load external JARs
		//
		JarLibFrame.loadLibraries( false );

//		try {
//			Thread.sleep( 2000 );
////			String[] sIPs = findIPs();
//			File[] list = CameraAPI.GetDirList( "" );
//			for ( int i=0; i<list.length; i++ )
//			{
//				System.out.println( "File[ " + i + " ]: " + list[ i ].getAbsolutePath() );
//			}
//		} catch ( Exception e ) { System.err.println( e.getMessage() ); }
	}

	public static boolean isApiOk()
	{
		return apiOk;
	}

	public static MainFrame getAppFrame()
	{
		return mainFrame;
	}

	public static OwlLogger getLogger()
	{
		return logger;
	}

	public static void infoStart( Object objMsg )
	{
		logger.infoStart( objMsg );
	}

	public static void infoEnd()
	{
		logger.infoEnd();
	}

	public static void infoFail()
	{
		logger.infoFail();
	}

	public static void info( Object objMsg )
	{
		logger.info( objMsg );
	}

	public static void warn( Object objMsg )
	{
		logger.warn( objMsg );
	}

	public static void error( Object objMsg )
	{
		logger.error( objMsg );
	}

	public static void error( Exception e )		// Prints Stack Trace ( see OwlLogger )
	{
		logger.error( e );
	}

	public static void debug( Object objMsg )
	{
		logger.debug( objMsg );
	}

	public static Preferences getPreferences()
	{
		return preferences;
	}

	public static String getDriverName()
	{
		return DeviceConnect.driverInUse;
	}

	public static int getDefaultBufferSize()
	{
		return DeviceConnect.DEFAULT_BUFFER_SIZE;
	}

	public static String getAppPath()
	{
		return appPath + System.getProperty( "file.separator" );
	}

	public static String getBitmapPath()
	{
		return ( getAppPath() + "bitmaps" + System.getProperty( "file.separator" ) );
	}

	public static String getSoundPath()
	{
		return ( getAppPath() + "extras" + System.getProperty( "file.separator" ) );
	}

	public static String getScriptsPath()
	{
		return ( getAppPath() + "Scripts" + System.getProperty( "file.separator" ) );
	}

	public static String getXMLPath()
	{
		return ( getAppPath() + "xml" + System.getProperty( "file.separator" ) );
	}

	public static String getAPIPath()
	{
		String sLibPath = null;

		String sJavaLibPath = System.getProperty( "java.library.path" );

		String[] sTokens =
			sJavaLibPath.split( System.getProperty( "path.separator" ) );

		for ( String sPath : sTokens )
		{
			if ( sPath.contains( "API" ) )
			{
				sLibPath = sPath + System.getProperty( "file.separator" );
			}
		}

		return sLibPath;
	}

	public static String getCustomDeinterlacePath()
	{
		String sAPIPath = getAPIPath();

		if ( sAPIPath == null )
		{
			sAPIPath = "";
		}

		String sPluginPath = getStartupINIFileValue( "PLUGIN_PATH" );

		if ( sPluginPath == null )
		{
			sPluginPath = "";
		}

		return ( sAPIPath + sPluginPath );
	}

	public static Image getProgramIcon()
	{
		return Toolkit.getDefaultToolkit().getImage( MainApp.getBitmapPath() +
													 "owl.gif" );
	}

	public static String getTextEditor()
	{
		String textEditor = null;

		if ( System.getProperty( "os.name" ).toLowerCase().contains( "windows" ) )
		{
			textEditor = getStartupINIFileValue( "TEXTEDIT_WINDOWS" );
		}

		else if ( System.getProperty( "os.name" ).toLowerCase().contains( "sun" ) )
		{
			textEditor = getStartupINIFileValue( "TEXTEDIT_SOLARIS" );
		}

		else if ( System.getProperty( "os.name" ).toLowerCase().contains( "linux" ) )
		{
			textEditor = getStartupINIFileValue( "TEXTEDIT_LINUX" );
		}

		else if ( System.getProperty( "os.name" ).toLowerCase().contains( "mac" ) )
		{
			textEditor = getStartupINIFileValue( "TEXTEDIT_MAC" );
		}

		return textEditor;
	}

	public static void launchTextEditor( String filename )
	{
		try
		{
			Runtime r = Runtime.getRuntime();

			if ( System.getProperty( "os.name" ).contains( "Windows" ) )
			{
				txtEditorProc = r.exec( MainApp.getTextEditor() + 
										" \"" +
										filename +
										"\"" );
			}
			else
			{
				txtEditorProc = r.exec( MainApp.getTextEditor() +
										" " +
										filename );
			}
		}
		catch ( java.io.IOException ioe )
		{
			txtEditorProc = null;
			MainApp.getLogger().error( "Failed to start text editor: " + MainApp.getTextEditor() );
			MainApp.getLogger().error( ioe );
		}
	}

	public static void killTextEditor()
	{
		if ( txtEditorProc != null )
		{
			txtEditorProc.destroy();
		}
	}

	public static String getStartupINIFileValue( String key )
	{
		String str = null;

		if ( paramMap != null )
		{
			str = paramMap.get( key );
		}

		return str;
	}

	public static Object[][] getProgramInfo()
	{
		Object[][] data = { { "Author Name", "Scott Streit" },
							{ "Company", "Astronomical Research Cameras, Inc." },
							{ "Program Version", MainApp.VERSION },
							{ "Selected Device", CameraAPI.GetDeviceString() + " [ " + MainApp.getDriverName() + " ]" },
							{ "Image Buffer Size", String.format( "%d bytes [ %.2f MB ]", CameraAPI.GetImageBufferSize(), ( CameraAPI.GetImageBufferSize() / 1.E6 ) ) },
							{ "Image Buffer VA", "0x" + Long.toHexString( CameraAPI.GetImageBufferVA() ).toUpperCase() },
							{ "Image Buffer PA", "0x" +  Long.toHexString( CameraAPI.GetImageBufferPA() ).toUpperCase() },
							{ "OS Name", System.getProperty( "os.name" ) },
							{ "OS Version", System.getProperty( "os.version" ) },
							{ "Java Home", System.getProperty( "java.home" ) },
							{ "Java Version", System.getProperty( "java.version" ) },
							{ "Java Class Path", System.getProperty( "java.class.path" ) },
							{ "Java Library Path", System.getProperty( "java.library.path" ) },
							{ "Java Total Memory", String.format( "%d bytes [ %.2f MB ]", Runtime.getRuntime().totalMemory(), ( Runtime.getRuntime().totalMemory() / 1.E6 ) ) },
							{ "Java Total Memory", String.format( "%d bytes [ %.2f MB ]", Runtime.getRuntime().maxMemory(), ( Runtime.getRuntime().maxMemory() / 1.E6 ) ) },
							{ "Java Total Memory", String.format( "%d bytes [ %.2f MB ]", Runtime.getRuntime().freeMemory(), ( Runtime.getRuntime().freeMemory() / 1.E6 ) ) } };
		return data;
	}

	public static void connectToDevice( boolean bShowList )
	{
		DeviceConnect.connectToDevice( bShowList );
	}

	public static void onExit()
	{
		try
		{
			CameraAPI.CloseDevice();
			CameraAPI.TerminateDisplay();
		}
		catch ( Exception e ) {}

		System.exit( 1 );
	}

	public static Object[][] getThreadInfo()
	{
		Object[][] data = null;

		try
		{
			final int sleepMillis = 1500;

			java.lang.management.ThreadMXBean thb =
					java.lang.management.ManagementFactory.getThreadMXBean();

			long[] ids = thb.getAllThreadIds();

			java.lang.management.ThreadInfo[] infs = thb.getThreadInfo( ids, 0 );

			data = new Object[ infs.length ][ 3 ];

			for ( int j=0; j<infs.length; j++ )
			{
				data[ j ][ 0 ] = Long.toString( infs[ j ].getThreadId() );
				data[ j ][ 1 ] = infs[ j ].getThreadName();
				data[ j ][ 2 ] = infs[ j ].getThreadState().toString();
			}

			Thread.sleep( sleepMillis );
		}
		catch ( Throwable e ) {}

		return data;
	}
}
