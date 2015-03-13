package owl.main.setup;

import java.lang.reflect.Method;

import owl.cameraAPI.CameraAPI;
import owl.main.MainApp;


public class TemperatureRunnable implements Runnable
{
	private Object  m_callbackObj;
	private String  m_callbackName;
	private boolean m_stop;

	public TemperatureRunnable( Object callbackObj, String callbackName )
	{
		m_callbackObj  = callbackObj;
		m_callbackName = callbackName;
		m_stop         = false;
	}

	public void start()
	{
		boolean bTempReadout = false;

		try
		{
			bTempReadout =
				CameraAPI.IsCCParamSupported( CameraAPI.TEMP_SIDIODE );

			if ( !bTempReadout )
			{
				bTempReadout =
					CameraAPI.IsCCParamSupported( CameraAPI.TEMP_LINEAR );
			}

			if ( bTempReadout )
			{
				( new Thread( this ) ).start();
			}
			else
			{
				throw new Exception(
						"No temperature readout feature available on controller!" +
						" CCParam: 0x" + Integer.toHexString( CameraAPI.GetCCParams() ) );
			}
		}
		catch ( Exception ex )
		{
			MainApp.error( ex.getMessage() );
		}
	}

	public void stop()
	{
		m_stop = true;
	}

	@Override
	public void run()
	{
		if ( !CameraAPI.IsDeviceOpen() ) { return; }
		if ( !CameraAPI.IsControllerConnected() ) { return; }

		Thread.currentThread().setName( "Owl - Temperature Runnable" );

		while ( !m_stop )
		{
			try
			{
				if ( !CameraAPI.IsReadout() )
				{
					callCallback( CameraAPI.GetArrayTemperature() );
				}

				Thread.sleep( 15000 );
			}
			catch ( Exception e ) {}
		}
	}

	private void callCallback( double gTemperature ) throws Exception
	{
		Method callbackMethod = null;
		Object callbackObject = null;

		// Get the callback method to invoke when done
		if ( m_callbackObj != null && m_callbackName != null )
		{
			Method[] cm = m_callbackObj.getClass().getMethods();
			callbackMethod = null;
			callbackObject = m_callbackObj;

			for ( int i=0; i<cm.length; i++ )
			{
				if ( cm[ i ].getName().equals( m_callbackName ) )
				{
					callbackMethod = cm[ i ];
					break;
				}
			}
		}

		if ( callbackObject != null && callbackMethod != null )
		{
			callbackMethod.invoke( callbackObject, gTemperature );
		}
	}
}
