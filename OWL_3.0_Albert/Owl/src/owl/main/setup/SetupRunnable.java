package owl.main.setup;

import owl.cameraAPI.CameraAPI;
import owl.cameraAPI.ReplyException;
import owl.main.MainApp;


public class SetupRunnable implements Runnable
{
	private final int TDL_MAX = 0x1000000;

	private SetupFrame setupFrame;
	private boolean stop;

	public SetupRunnable( SetupFrame setupFrame )
	{
		this.setupFrame = setupFrame;
	}

	public void stop()
	{
		CameraAPI.SetAbort( true );
		stop = true;
	}

	public void run()
	{
		boolean bTimFileLoaded = false;
		boolean bImageSizeSet  = false;
		boolean bPoweredOn     = false;

		Thread.currentThread().setName( "Owl - Setup Runnable" );

		setupFrame.setApplyAction( setupFrame.ABORT_ACTION );
		stop = false;

		try
		{
			if ( !CameraAPI.IsDeviceOpen() )
			{
				throw new Exception( "Not connected to any device!" );
			}

			//
			// Reset the PCI(e) board. This prevents any previous settings
			// from affecting a fresh setup. For example, if 2x FO's has
			// been set, then a fresh setup will reset the controller back
			// to single FO, but not the PCI(e) board. This will fix that
			// by putting the PCI(e) back into single FO mode.
			//
			MainApp.infoStart( "Resetting PCI(e) board" );
			CameraAPI.ResetDevice();
			MainApp.infoEnd();

			if ( !stop && setupFrame.doResetController() )
			{
				MainApp.infoStart( "Resetting controller" );
				CameraAPI.ResetController();
				MainApp.infoEnd();
			}

			if ( !stop && setupFrame.doHardwareTest() && setupFrame.doPCIHWTest() )
			{
				int dIncr = TDL_MAX / setupFrame.getPCIHWTCount();

				MainApp.infoStart( "Testing PCI(e) data link" );
				for ( int i=0; i<setupFrame.getPCIHWTCount(); i++ )
				{
					CameraAPI.Cmd( CameraAPI.PCI_ID, CameraAPI.TDL, i * dIncr, i * dIncr );
					if ( stop ) { break; }
				}
				MainApp.infoEnd();
			}

			if ( !stop && setupFrame.doHardwareTest() && setupFrame.doTIMHWTest() )
			{
				int dIncr = TDL_MAX / setupFrame.getTIMHWTCount();

				MainApp.infoStart( "Testing TIM data link" );
				for ( int i=0; i<setupFrame.getTIMHWTCount(); i++ )
				{
					CameraAPI.Cmd( CameraAPI.TIM_ID, CameraAPI.TDL, i * dIncr, i * dIncr );
					if ( stop ) { break; }
				}
				if ( !stop ) MainApp.infoEnd();
			}

			if ( !stop && setupFrame.doHardwareTest() && setupFrame.doUTLHWTest() )
			{
				int dIncr = TDL_MAX / setupFrame.getUTLHWTCount();

				MainApp.infoStart( "Testing UTIL data link" );
				for ( int i=0; i<setupFrame.getUTLHWTCount(); i++ )
				{
					CameraAPI.Cmd( CameraAPI.UTIL_ID, CameraAPI.TDL, i * dIncr, i * dIncr );
					if ( stop ) { break; }
				}
				MainApp.infoEnd();
			}

			if ( !stop && setupFrame.doLoadTIM() )
			{
				MainApp.infoStart( "Loading TIM file" );

				CameraAPI.LoadControllerFile( setupFrame.getTIMLoadFile(), 1 );
				bTimFileLoaded = true;

				MainApp.infoEnd();
			}

			if ( !stop && setupFrame.doLoadUTL() )
			{
				MainApp.infoStart( "Loading UTL file" );
				CameraAPI.LoadControllerFile( setupFrame.getUTLLoadFile(), 1 );
				MainApp.infoEnd();
			}

			if ( !stop && setupFrame.doPowerOn() )
			{
				MainApp.infoStart( "Powering on controller" );
				CameraAPI.Cmd( CameraAPI.TIM_ID, CameraAPI.PON, CameraAPI.DON );
				MainApp.infoEnd();
				bPoweredOn = true;
			}

			if ( !stop && setupFrame.doImageSize() )
			{
				int dRows = setupFrame.getRowSize();
				int dCols = setupFrame.getColSize();

				bImageSizeSet = true;

				if ( ( dRows * dCols * 2 ) > CameraAPI.GetImageBufferSize() )
				{
					throw new Exception( "Image dimensions ( " + dCols + "x" + dRows +
										 " = " + ( dRows * dCols * 2 ) +
										 " bytes ) too large for image buffer ( " +
										 CameraAPI.GetImageBufferSize() + " bytes )" );
				}

				MainApp.infoStart( "Setting image dimensions" );
				CameraAPI.SetImageSize( dRows, dCols );
				MainApp.infoEnd();
			}

			if ( !stop && ( bTimFileLoaded || bPoweredOn || bImageSizeSet ) )
			{
				SetupFrame.callSetupListeners( setupFrame );
			}

			MainApp.info( "Setup complete" );
		}
		catch ( ReplyException re )
		{
			MainApp.infoFail();
			MainApp.error( re.toString() );
		}
		catch ( Exception e )
		{
			MainApp.infoFail();
			MainApp.error( "SetupThread error: " + e );
		}
		finally
		{
			CameraAPI.SetAbort( false );
			setupFrame.setApplyAction( setupFrame.APPLY_ACTION );
		}
	}
}
