package owl.main.device;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import owl.cameraAPI.CameraAPI;
import owl.main.MainApp;
import owl.main.owltypes.OwlMessageBox;
import owl.main.setup.SetupFrame;



public class DeviceConnect implements Runnable
{
    //--------------------------------------------------------------------------
    //   Public Constants and Variables:
    //--------------------------------------------------------------------------
	public final static int DEFAULT_BUFFER_SIZE = 4200 * 4200 * 2;
    public static String driverInUse = null;


    //--------------------------------------------------------------------------
    //   Private Constants:
    //--------------------------------------------------------------------------
    private final static String NO_DRIVER_STR = "No PCI(e) devices found!";


    //--------------------------------------------------------------------------
    //   Private Constants and Variables:
    //--------------------------------------------------------------------------
	private static Vector<DeviceListener> deviceListeners = null;
    private static boolean bShowList = false;


    //--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------

	// +--------------------------------------------------------------------+
	// |  connectToDevice                                                   |
	// +--------------------------------------------------------------------+
	// |  Called when a driver connection needs to be opened/re-opened      |
	// +--------------------------------------------------------------------+
	public static void connectToDevice( boolean bShowList )
	{
		DeviceConnect.bShowList = bShowList;

		new Thread( new DeviceConnect() ).start();
	}

	// +--------------------------------------------------------------------+
	// |  getCurrentDriver                                                  |
	// +--------------------------------------------------------------------+
	// |  Returns the current driver in use                                 |
	// +--------------------------------------------------------------------+
	public static String getCurrentDriver()
	{
		return DeviceConnect.driverInUse;
	}

	// +--------------------------------------------------------------------+
	// |  run                                                               |
	// +--------------------------------------------------------------------+
	// |  Runnable interface override                                       |
	// +--------------------------------------------------------------------+
	@Override
	public void run()
	{
		Thread.currentThread().setName( "Owl - DeviceConnect" );

		ImageIcon icon = new ImageIcon( MainApp.getBitmapPath() + "pci.gif" );

		OwlMessageBox msgBox =
					new OwlMessageBox(
							"Mapping device image buffer ... please wait.",
							 icon );

		Object selectedDriver = null;
		String driverList[]   = { NO_DRIVER_STR };
		int bufferSize        = 0;

  		String[] userDriverList = getDriverOverrides();

		//  Get the list of devices
		// +---------------------------------------------------------------+
		try
		{
			driverList = CameraAPI.GetDeviceList( userDriverList );
		}
		catch( Exception dle )
		{
			JOptionPane.showMessageDialog( 
								null, "Device List Exception Caught!!" );

			MainApp.error( dle );
		}

		if ( driverList == null )
		{
			driverList = new String[ 1 ];
			driverList[ 0 ] = NO_DRIVER_STR;
		}

		if ( driverList.length > 1 || bShowList )
		{
			selectedDriver = JOptionPane.showInputDialog(
										null,
										"Choose device ...",
										"Select Device",
										JOptionPane.INFORMATION_MESSAGE,
										new javax.swing.ImageIcon( MainApp.getBitmapPath() + "PCI.gif" ),
										driverList,
										( ( MainApp.getDriverName() != null ) ? MainApp.getDriverName() : driverList[ 0 ] ) );
		}
		else
		{
			selectedDriver = driverList[ 0 ];
		}

		if ( selectedDriver != null && !( ( String )selectedDriver ).equals( NO_DRIVER_STR ) )
		{
			msgBox.start();

			try
			{
				if ( CameraAPI.IsDeviceOpen() )
				{
					CameraAPI.CloseDevice();
				}

				CameraAPI.OpenDevice( ( String )selectedDriver );
				driverInUse = ( String )selectedDriver;

				MainApp.info( "Device \"" +
							  MainApp.getDriverName() +
							  "\" opened" );

				try
				{
					bufferSize = determineBufferSize();
					CameraAPI.MapDevice( bufferSize );
				}
				catch ( Exception e )
				{
					MainApp.warn( e.getMessage() );
				}

				if ( CameraAPI.GetImageBufferSize() <= 0 )
				{
					throw new Exception(
							"Driver returned invalid image buffer size: " +
							 CameraAPI.GetImageBufferSize() );
				}

				MainApp.info( "Mapped buffer of " +
							  CameraAPI.GetImageBufferSize() +
							  " bytes" );

				//  Set the logging to current selection
				// +---------------------------------------------------------------+
				CameraAPI.LogAPICmds( MainApp.mainFrame.logFrame.isAPILogging() );

				//  Call the device listeners
				//  ( To be handled correctly, this MUST be done before checking
				//    for controllers )
				// +---------------------------------------------------------------+
				DeviceConnect.callDeviceListeners();

				//  Check for controllers
				// +---------------------------------------------------------------+
				icon = new ImageIcon( MainApp.getBitmapPath() + "Controller.gif" );
				msgBox.setText( "Checking for controllers ... please wait!", icon );

				if ( CameraAPI.IsControllerConnected() )
				{
					SetupFrame.callSetupListeners(
										MainApp.mainFrame.cameraPanel.setupFrame );
				}
				msgBox.stop();
			}
			catch ( Exception e )
			{
				MainApp.error( "Driver \"" +
							   ( String )selectedDriver +
							   "\" failed to open" );

				MainApp.error( e );
			}
			finally
			{
				if ( msgBox != null ) { msgBox.stop(); }
			}
		}
	}

	public static void addDeviceListener( DeviceListener listener )
	{
		if ( deviceListeners == null )
		{
			deviceListeners = new Vector<DeviceListener>( 0 );
		}

		if ( deviceListeners != null )
		{
			deviceListeners.add( listener );
		}
	}

	public static void callDeviceListeners()
	{
		if ( deviceListeners != null )
		{
			for ( int i=0; i<deviceListeners.size(); i++ )
			{
				DeviceEvent de =
					new DeviceEvent( new DeviceConnect(), DeviceConnect.driverInUse );

				deviceListeners.get( i ).deviceChanged( de );
			}
		}
	}

	public static void callDeviceListeners( String driverName )
	{
		DeviceConnect.driverInUse = driverName;

		if ( deviceListeners != null )
		{
			for ( int i=0; i<deviceListeners.size(); i++ )
			{
				DeviceEvent de =
					new DeviceEvent( new DeviceConnect(), DeviceConnect.driverInUse );

				deviceListeners.get( i ).deviceChanged( de );
			}
		}
	}

	//--------------------------------------------------------------------------
    //   Private Methods:
    //--------------------------------------------------------------------------

	// +--------------------------------------------------------------------+
	// |  getDriverOverrides                                                |
	// +--------------------------------------------------------------------+
	// |  Returns an array of override driver names set in 'startup.ini' or |
	// |  'null' if none exist.                                             |
	// +--------------------------------------------------------------------+
	private static String[] getDriverOverrides()
	{
		ArrayList<String> driverList = new ArrayList<String>();

		try
		{
			for ( int i=0; i<10; i++ )
			{
				String str = MainApp.getStartupINIFileValue( "DEVICE " + i );

				if ( str != null )
				{
					driverList.add( str );
				}
				else
				{
					break;
				}
			}
		}
		catch ( Exception e ) {}

		return driverList.toArray( new String[ driverList.size() ] );
	}

	// +--------------------------------------------------------------------+
	// |  determineBufferSize                                               |
	// +--------------------------------------------------------------------+
	// |  Returns the size of the image buffer that should be allocated.    |
	// |  Tries to allocate a buffer for the existing image size as read    |
	// |  from the controller or returns DeviceConnect.DEFAULT_BUFFER_SIZE. |
	// +--------------------------------------------------------------------+
	private static int determineBufferSize()
	{
		int bufferSize = DEFAULT_BUFFER_SIZE;

		try
		{
			int[] imageSize = CameraAPI.GetImageSize();

			if ( imageSize[ 0 ] > 0 && imageSize[ 0 ] <= 40000 &&
				 imageSize[ 1 ] > 0 && imageSize[ 1 ] <= 40000  )
			{
				int testSize = imageSize[ 0 ] * imageSize[ 1 ] * 2;

				if ( testSize > DEFAULT_BUFFER_SIZE )
				{
					bufferSize = testSize;
				}
			}
		}
		catch ( Exception e )
		{
			bufferSize = DEFAULT_BUFFER_SIZE;
		}

		return bufferSize;
	}
}
