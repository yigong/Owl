package owl.main.exposure;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;

import javax.swing.ImageIcon;

import owl.cameraAPI.CameraAPI;
import owl.cameraAPI.ReplyException;
import owl.display.ds9.DS9Accessor;
import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;
import owl.main.owltypes.OwlMessageBox;


// +--------------------------------------------------------------------------------+
// |  Class ExposeRunnable                                                          |
// +--------------------------------------------------------------------------------+
// |  Handles standard, single image exposures                                      |
// |                                                                                |
// |  NOTES:                                                                        |
// |                                                                                |
// |  1. The FitsHeaderFrame convenience member was removed and is now called from  |
// |     the ExposeInfo interface.  This prevents the variable from being set to    |
// |     null when the ExposeRunnable constructor is called.                        |
// +--------------------------------------------------------------------------------+
public class ExposeRunnable implements Runnable
{
	// +--------------------------------------------------------------+
	// |  Constants                                                   |
	// +--------------------------------------------------------------+
	protected final String STOP_MSG				= "User aborted expose";
	protected final String READ_TIMEOUT_MSG		= "Read image timed out";
	protected final int    READ_TIMEOUT			= 150;

	protected final String SOUND_FILE1			= MainApp.getSoundPath() +
												  "EngExpStrt.wav";

	protected final String SOUND_FILE2			= MainApp.getSoundPath() +
		  										  "EngExpFin.wav";

	protected enum ExposeState { RUN, PAUSE, RESUME }

	// +--------------------------------------------------------------+
	// |  Class members                                               |
	// +--------------------------------------------------------------+
	protected boolean				m_bInReadout;
	protected boolean				m_bStop;
	protected boolean				m_bPause;
	protected ExposeState			m_exposeState;
	protected ExposeInfo			m_exposeInfo;
	protected ExposeActionListener	m_exposeActionListener;
	protected AudioClip				m_audio1;
	protected AudioClip				m_audio2;
	protected DS9Accessor			m_ds9Accessor;



	// +--------------------------------------------------------------------------------+
	// |  Class Constructor                                                             |
	// +--------------------------------------------------------------------------------+
	public ExposeRunnable( ExposeInfo expInfo, ExposeActionListener expActListener )
	{
		m_exposeInfo			= expInfo;
		m_exposeActionListener	= expActListener;
		m_ds9Accessor			= new DS9Accessor();

		m_audio1 = null;
		m_audio2 = null;

		String useAudioString = MainApp.getStartupINIFileValue( "USE_EXPOSE_AUDIO" );

		if ( useAudioString != null && useAudioString.toLowerCase().equals( "true" ) )
		{
			try
			{
				File file = new File( SOUND_FILE1 );
				m_audio1 = Applet.newAudioClip( file.toURI().toURL() );
	
				file = new File( SOUND_FILE2 );
				m_audio2 = Applet.newAudioClip( file.toURI().toURL() );
			}
			catch ( java.net.MalformedURLException murle ) {}
		}
	}

	// +--------------------------------------------------------------------------------+
	// |  stop                                                                          |
	// +--------------------------------------------------------------------------------+
	// |  Stops the exposure                                                            |
	// +--------------------------------------------------------------------------------+
	public void stop()
	{
		try
		{
			//  Do these first, or they can get skipped!
			// +---------------------------------------+
			m_bStop = true;

			//  Call abort exposure/readout
			// +---------------------------------------+
			MainApp.infoStart( "Aborting exposure" );
			CameraAPI.StopExposure();
			MainApp.infoEnd();
		}
		catch ( Exception ex )
		{
			MainApp.infoFail();
			MainApp.error( ex );
		}
	}

	// +--------------------------------------------------------------------------------+
	// |  pause                                                                         |
	// +--------------------------------------------------------------------------------+
	// |  Pause the exposure                                                            |
	// +--------------------------------------------------------------------------------+
	public void pause()
	{
		m_exposeState = ExposeState.PAUSE;
	}

	// +--------------------------------------------------------------------------------+
	// |  resume                                                                        |
	// +--------------------------------------------------------------------------------+
	// |  Resumes a paused exposure                                                     |
	// +--------------------------------------------------------------------------------+
	public void resume()
	{
		m_exposeState = ExposeState.RESUME;
	}

	// +--------------------------------------------------------------------------------+
	// |  run                                                                           |
	// +--------------------------------------------------------------------------------+
	// |  Primary execution method.  This is where the exposure is handled. See the     |
	// |  java.lang.Runnable interface for details.                                     |
	// +--------------------------------------------------------------------------------+
	public void run()
	{
		boolean bImageSaved = false;

		if ( m_exposeInfo == null )
		{
			MainApp.error( "No ExposeInfo interface exists!" );
			return;
		}

		Thread.currentThread().setName( "Owl - ExposeRunnable" );

		try
		{
			//
			//  Get and verify the image buffer size
			// --------------------------------------------------------------
			int dImgBufSize = CameraAPI.GetImageBufferSize();

			if ( dImgBufSize <= 0 )
			{
				throw new Exception(
						"Sorry, but it's not safe to start the exposure. " +
						"The driver returned invalid image buffer size: " +
						 dImgBufSize );
			}

			//
			//  Get and verify the image dimensions
			// --------------------------------------------------------------
			int[] dImageSize = CameraAPI.GetImageSize();
			int dRows = dImageSize[ 0 ];
			int dCols = dImageSize[ 1 ];

			//
			// Verify the image dimensions
			//
			if ( dRows <= 0 )
			{
				throw new Exception(
							"Invalid ROW size: " + dRows +
							" Controller may not have been properly initialized!" );
			}

			if ( dCols <= 0 )
			{
				throw new Exception(
							"Invalid COL size: " + dCols +
							" Controller may not have been properly initialized!" );
			}

			if ( ( dRows * dCols * 2 ) > dImgBufSize )
			{
				MainApp.error( "Image dimensions ( " + dCols +
							   "x" + dRows + " = " + ( dRows * dCols * 2 ) +
							   " bytes ) too large for image buffer ( " +
							   dImgBufSize + " bytes )" );
			}

			int dExpTime = ( int )( m_exposeInfo.getExposeTime() * 1000.0 );
			int dExposureCount = 1;

			if ( m_exposeInfo.getFitsHeaderFrame() != null )
			{
				m_exposeInfo.getFitsHeaderFrame().setExpTimeField( m_exposeInfo.getExposeTime() );
				m_exposeInfo.getFitsHeaderFrame().setDateObsField();
			}

			if ( !CameraAPI.IsDeviceOpen() )
			{
				MainApp.error( "Not connected to any device driver!" );
				return;
			}

			m_exposeState	= ExposeState.RUN;
			m_bStop			= false;

			if ( m_exposeActionListener != null )
			{
				m_exposeActionListener.setExposeAction( ExposeActionListener.ABORT );
			}

			if ( m_exposeInfo.isMultipleExposure() )
			{
				dExposureCount = m_exposeInfo.getMultipleExposureCount();
			}

			// Pre-fill image buffer if requested
			// --------------------------------------------------------------
			if ( m_exposeInfo.isFillBuffer() )
			{
				MainApp.infoStart( "Filling image buffer" );
				CameraAPI.FillImageBuffer( m_exposeInfo.getBufferFill() );
				MainApp.infoEnd();
			}

			// Pre-get deinterlacing algorithm and channel count ( optional )
			// --------------------------------------------------------------
			int arg = -1;
			int algorithm = m_exposeInfo.getDeinterlaceAlgorithm();
			String algString = m_exposeInfo.getDeinterlaceDescription();

			if ( algorithm == CameraAPI.DEINTERLACE_HAWAII_RG )
			{
				arg = CameraAPI.Cmd2( CameraAPI.TIM_ID, CameraAPI.RNC );
			}

			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			MainApp.info( "Reading " + dRows + " x " + dCols + " pixel image" );

			m_exposeInfo.setPixelRange( 0, dRows * dCols );

			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			//
			// Set synthetic image mode
			// --------------------------------------------------------------
			MainApp.infoStart( "Setting synthetic image mode to \"" +
								m_exposeInfo.isSynthImage() + "\"" );
			CameraAPI.SetSyntheticImageMode( m_exposeInfo.isSynthImage() );
			MainApp.infoEnd();

			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			// Set the exposure time
			// --------------------------------------------------------------
			MainApp.infoStart( "Setting exposure time" );
			CameraAPI.Cmd( CameraAPI.TIM_ID, CameraAPI.SET, dExpTime, CameraAPI.DON );
			MainApp.infoEnd();

			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			// Set the shutter position
			// --------------------------------------------------------------
			MainApp.infoStart( "Setting shutter position" );
			CameraAPI.SetOpenShutter( m_exposeInfo.isOpenShutter() );
			MainApp.infoEnd();

			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			// Expose and read image
			// --------------------------------------------------------------
			for ( int i=0; i<dExposureCount; i++ )
			{
				if ( m_bStop ) { throw new Exception( STOP_MSG ); }

				bImageSaved = false;

				// Delay N number of seconds ( if specified )
				// --------------------------------------------------------------
				if ( m_exposeInfo.isDelay() )
				{
					int dDelay = ( int )m_exposeInfo.getDelay();

					OwlMessageBox msgBox =
								new OwlMessageBox( "Delaying for " + dDelay +
							   					   " seconds .... Please wait ....",
							   					   new ImageIcon( MainApp.getBitmapPath() +
							   					   "Clock1.gif" ) );
					msgBox.start();

					try
					{
						// Now in loop to allow stop to affect the delay code
						// ------------------------------------------------------
						for ( int t=0; t<dDelay; t++ )
						{
							if ( m_bStop ) { throw new Exception( STOP_MSG ); }

							msgBox.setText( "Delaying for " + ( dDelay - t ) +
		   								    " seconds .... Please wait ...." );

							Thread.sleep( 1000 );
						}
					}
					catch ( InterruptedException e ) {}
					finally { msgBox.stop(); }
				}

				if ( m_bStop ) { throw new Exception( STOP_MSG ); }

				if ( dExposureCount > 1 )
				{
					MainApp.info( "Starting image #" + ( i + 1 ) );
				}

				if ( m_bStop ) { throw new Exception( STOP_MSG ); }

				// Randomly set the progress bar's color. Changed here because
				// otherwise the bar changes color before the actual exposure,
				// which is misleading; giving the appearence of an "extra"
				// exposure occurring.
				// --------------------------------------------------------------
				m_exposeInfo.setElapsedTime( 0 );
				m_exposeInfo.setPixelCount( 0 );
				m_exposeInfo.randomPixelCountColor( null );

				if ( m_exposeInfo.isBeep() )
				{
					if ( m_audio1 == null )
					{
						java.awt.Toolkit.getDefaultToolkit().beep();
					}
					else if ( dExpTime >= 2500 )
					{
						m_audio1.play();
					}
				}

				// Read elapsed time and pixel count
				// --------------------------------------------------------------
				readCamera( dRows, dCols , dExpTime );

				if ( m_exposeInfo.isBeep() )
				{
					if ( m_audio2 == null )
					{
						java.awt.Toolkit.getDefaultToolkit().beep();
					}
					else
					{
						m_audio2.play();
					}
				}

				if ( m_bStop ) throw new Exception( STOP_MSG );

				// Subtract image
				// --------------------------------------------------------------
				if ( m_exposeInfo.isSubtract() )
				{
					MainApp.infoStart( "Subtracting image" );
					CameraAPI.SubtractImageHalves( dRows, dCols );
					MainApp.infoEnd();
				}

				if ( m_bStop ) { throw new Exception( STOP_MSG ); }

				// Deinterlace image
				// --------------------------------------------------------------
				if ( !m_exposeInfo.isSynthImage() )
				{
					MainApp.infoStart( "Deinterlacing image (" + algString + ")" );
					CameraAPI.DeinterlaceImage( dRows, dCols, 0, algorithm, arg );
					MainApp.infoEnd();
				}

				if ( m_bStop ) { throw new Exception( STOP_MSG ); }

				// Save image to disk
				// --------------------------------------------------------------
				if ( m_exposeInfo.isSaveToDisk() )
				{
					int tempRows = dRows;

					// Run FITS header update script ( if there is one )
					// --------------------------------------------------------------
					if ( m_exposeInfo.getFitsHeaderFrame() != null )
					{
						m_exposeInfo.getFitsHeaderFrame().runUpdateScript();
					}

					// Attempt to clear any frames on an open DS9 program before
					// trying to save or it will fail. Catch and ignore/hide all
					// errors from attempting to clear DS9.
					// ----------------------------------------------------------
					if ( m_exposeInfo.isDisplay() && System.getProperty( "os.name" ).contains( "Windows" ) )
					{
						m_ds9Accessor.clearAllFrames();
					}

					if ( m_exposeInfo.isSubtract() )
					{
						tempRows = dRows / 2;
					}

					if ( m_bStop ) { throw new Exception( STOP_MSG ); }

					if ( m_exposeInfo.getFileExtension().equals( ExposePanel.FITS_FILEEXT ) )
					{
						MainApp.infoStart( "Writing FITS file" );

						CameraAPI.WriteFitsFile( m_exposeInfo.getFilename(), tempRows, dCols );

						if ( m_exposeInfo.getFitsHeaderFrame() != null )
						{
							m_exposeInfo.getFitsHeaderFrame().writeToFile( m_exposeInfo.getFilename() );
						}

						MainApp.infoEnd();
					}
					else
					{
						MainApp.infoStart( "Writing TIFF file" );
						CameraAPI.WriteTiffFile( m_exposeInfo.getFilename(), tempRows, dCols );
						MainApp.infoEnd();
					}

					bImageSaved = true;

					if ( m_bStop ) { throw new Exception( STOP_MSG ); }

					// Display the FITS file in DS9 ( windows only )
					// ---------------------------------------------------------
					if ( System.getProperty( "os.name" ).contains( "Windows" ) && m_exposeInfo.isDisplay() )
					{
						MainApp.infoStart( "Attempting to display FITS image" );
						m_ds9Accessor.showFits( m_exposeInfo.getFilename() );
						MainApp.infoEnd();
					}

					// Need to increment AFTER image display or DS9 will get
					// the wrong filename.
					// ---------------------------------------------------------
					m_exposeInfo.incrementFilename();
				}

				if ( m_bStop ) { throw new Exception( STOP_MSG ); }

				// Display image buffer ( linux/solaris only )
				// --------------------------------------------------------------
				if ( !System.getProperty( "os.name" ).contains( "Windows" ) && m_exposeInfo.isDisplay() )
				{
					MainApp.infoStart( "Attempting to display FITS image" );
					CameraAPI.DisplayImage( dRows, dCols );
					MainApp.infoEnd();
				}

				if ( m_bStop ) { throw new Exception( STOP_MSG ); }

				// Verify the data if synthetic image
				// --------------------------------------------------------------
				if ( m_exposeInfo.isSynthImage() )
				{
					MainApp.infoStart( "Verifying synthetic image data" );
					CameraAPI.VerifySyntheticImage( dRows, dCols );
					MainApp.infoEnd();
				}

				// Tell expose listeners it's over
				// --------------------------------------------------------------
				for ( int w=0; w<ExposeInfo.exposeListeners.size(); w++ )
				{
					ExposeInfo.exposeListeners.get( w ).exposureComplete( new ExposeEvent( this ) );
				}

			}	// end for loop
		}
		catch ( ReplyException re )
		{
			MainApp.infoFail();
			MainApp.error( re.toString() );
		}
		catch ( Exception e )
		{
			MainApp.infoFail();

			if ( e.getMessage().equals( STOP_MSG ) )
			{
				MainApp.warn( e.getMessage() );
			}
			else
			{
				MainApp.error( e );
			}

			if ( !bImageSaved )
			{
				MainApp.error( "No image file saved. Use \"buffer " +
				   			   " dump\" to rescue image data!" );
			}
		}
		finally
		{
			if ( m_exposeActionListener != null )
			{
				m_exposeActionListener.setExposeAction( ExposeActionListener.EXPOSE );
			}
		}
	}

	// +--------------------------------------------------------------------------------+
	// |  readCamera                                                                    |
	// +--------------------------------------------------------------------------------+
	// |  Starts the exposure and waits for readout to complete or a timeout to occur.  |
	// +--------------------------------------------------------------------------------+
	private void readCamera( int dRows, int dCols, int dExpTime ) throws ReplyException, Exception
	{
		int dCurrentPixelCount	= 0;
		int dLastPixelCount		= 0;
		int dElapsedTime		= 0;
		int dCameraElapsedTime	= 0;
		int dExposureCounter	= 0;
		int dTimeoutCounter		= 0;
		long lReadoutStartTime	= 0;
		boolean bFirstTime		= true;


		if ( m_bStop ) { throw new Exception( STOP_MSG ); }

	
		// Start the exposure
		// --------------------------------------------------------------
		MainApp.infoStart( "Starting exposure " );
		CameraAPI.Cmd( CameraAPI.TIM_ID, CameraAPI.SEX, CameraAPI.DON );
		MainApp.infoEnd();

		m_exposeInfo.setReadoutTime( 0 );

		while ( dCurrentPixelCount < ( dRows * dCols ) )
		{
			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			// Doing it this way allows the exposure counter to be correctly
			// set to zero at the start of readout.
			if ( CameraAPI.IsReadout() )
			{
				m_bInReadout = true;
			}
			else
			{
				m_bInReadout = false;
				bFirstTime	 = true;
			}

			// ----------------------------
			// READ ELAPSED EXPOSURE TIME
			// ----------------------------
			// Checking the elapsed time > 1 sec. is to prevent race conditions with
			// sending RET while the PCI(e) board is going into readout. Added check
			// for exposure_time > 1 sec. to prevent RET error.
			if ( !m_bInReadout && ( dExpTime - dCameraElapsedTime ) > 1000 && dExposureCounter >= 5 && dExpTime > 1000 )
			{
				// Allow PEX/REX to be sent
				if ( m_exposeState != ExposeState.RUN )
				{
					try
					{
						if ( m_exposeState == ExposeState.PAUSE )
						{
							CameraAPI.Cmd( CameraAPI.TIM_ID,
										   CameraAPI.PEX,
										   CameraAPI.DON );

							while ( m_exposeState == ExposeState.PAUSE )
							{
								try {
									Thread.sleep( 5 );
								} catch ( Exception e ) {}
							}
						}

						else if ( m_exposeState == ExposeState.RESUME )
						{
							m_exposeState = ExposeState.RUN;

							CameraAPI.Cmd( CameraAPI.TIM_ID,
										   CameraAPI.REX,
										   CameraAPI.DON );
						}

						else
						{
							throw new Exception(
										"Invalid exposure state: " +
										 m_exposeState.toString() );
						}
					}
					catch ( ReplyException re )
					{
						CameraAPI.StopExposure();

						throw new Exception(
								"Failed to change exposure state! " +
								 m_exposeState.toString() + " " + 
								 re.toString() +
								 " The PAUSE command may not be" +
								 " implemented on the controller!" );
					}
					catch ( Exception e )
					{
						CameraAPI.StopExposure();

						throw e;
					}
				}

				// Ignore all RET timeouts
				try
				{
					// Read the elapsed exposure time
					dCameraElapsedTime = CameraAPI.Cmd2( CameraAPI.TIM_ID,
														 CameraAPI.RET );
				}
				catch ( Exception e ) {}

				// Check for invalid elapsed time
				if ( dCameraElapsedTime != CameraAPI.READOUT )
				{
					if ( dCameraElapsedTime < 0 || dCameraElapsedTime > dExpTime )
					{
						CameraAPI.StopExposure();

						throw new Exception(
									"Invalid RET reply: " +
									 ( Integer.toHexString( dCameraElapsedTime ) + " [ " +
									 OwlUtilities.intToAscii( dCameraElapsedTime ) + " ]" ) );
					}
	
					if ( m_bStop ) { throw new Exception( STOP_MSG ); }
	
					dExposureCounter = 0;
					dTimeoutCounter  = 0;
	
					dElapsedTime = ( dExpTime / 1000 ) - ( dCameraElapsedTime / 1000 );
					m_exposeInfo.setElapsedTime( dElapsedTime );
				}
				else
				{
					// This is important!  During up-the-ramp (CCD) or CDS (IR),
					// this variable may be set to 'ROUT', which will result in
					// further 'RET's not being sent. i.e. the exposure for the
					// second image will not occur and you will get a timeout.
					// This only applies for exposures greater than the timeout
					// value ( ~5 secs ).
					dCameraElapsedTime = 0;
				}
			}

			dExposureCounter++;

			// ----------------------------
			// READOUT PIXEL COUNT
			// ----------------------------

			// Check for abort
			if ( m_bStop )
			{
				LogPixelCountError( dCurrentPixelCount, dRows, dCols );
				throw new Exception( STOP_MSG );
			}

			// Save the last pixel count for use by the timeout counter.
			dLastPixelCount = dCurrentPixelCount;
			dCurrentPixelCount = CameraAPI.GetPixelCount();

			if ( m_bStop )
			{
				LogPixelCountError( dCurrentPixelCount, dRows, dCols );
				throw new Exception( STOP_MSG );
			}

			// Set the progress bar with the current value.
			m_exposeInfo.setPixelCount( dCurrentPixelCount );

			// On PCIe, small images, such as 80x80 will result in the image
			// readout status bit to be available for such a short time that
			// Owl will miss it and this code will not execute. To fix this,
			// a check for the pixel count incrementing will prevent this
			// error. Also, setElapsedTime was moved here to make sure it's
			// not skipped.
			//
			// NOTE: Removed the above fix because it prevents IR systems
			// from doing read, expose, read ( up the ramp ).
			if ( m_bInReadout ) //|| dCurrentPixelCount > 0 )
			{
				m_exposeInfo.setElapsedTime( 0 );

				if ( bFirstTime )
				{
					lReadoutStartTime = System.currentTimeMillis();
					bFirstTime = false;
				}
				else
				{
					m_exposeInfo.setReadoutTime( ( System.currentTimeMillis() -
												   lReadoutStartTime ) / 1000.0 );
				}
			}

			// If the controller's in READOUT, then increment the timeout
			// counter. Checking for readout prevents timeouts when clearing
			// large and/or slow arrays.
			if ( dCurrentPixelCount == dLastPixelCount )
			{
				dTimeoutCounter++;
			}
			else
			{
				dTimeoutCounter = 0;
			}

			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			if ( dTimeoutCounter >= READ_TIMEOUT )
			{
				LogPixelCountError( dCurrentPixelCount, dRows, dCols );
				CameraAPI.StopExposure();
				throw new Exception( READ_TIMEOUT_MSG );
			}

			Thread.sleep( 25 );
		}
	}

	// +--------------------------------------------------------------------------------+
	// |  LogPixelCountError                                                            |
	// +--------------------------------------------------------------------------------+
	// |  Logs a warning that the image readout has stopped.                            |
	// +--------------------------------------------------------------------------------+
	private void LogPixelCountError( int currentPixelCount, int rows, int cols )
	{
		MainApp.warn( "Image readout ABORTED ... Read " +
					  Integer.toString( currentPixelCount ) + "/" +
					  Integer.toString( rows*cols ) + " pixels" );
	}
}
