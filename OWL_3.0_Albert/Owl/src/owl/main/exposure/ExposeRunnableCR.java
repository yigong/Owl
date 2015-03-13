package owl.main.exposure;

import java.io.File;

import owl.cameraAPI.CameraAPI;
import owl.cameraAPI.ReplyException;
import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;


public class ExposeRunnableCR extends ExposeRunnable
{
	private final int FPB	=	0x465042;
	private final int SNF	=	0x534E46;

	private int  m_numOfFrames;
	private long m_hFits;

	public ExposeRunnableCR( int numOfFrames, ExposeInfo expInfo, ExposeActionListener expActListener )
	{
		super( expInfo, expActListener );
		m_numOfFrames = numOfFrames;
	}

	@Override
	public void run()
	{
		long lImageSize         = 0;
		long lBoundedImageSize  = 0;
		long lFramesPerBuffer   = 0;
		long lPCIFrameCount     = 0;
		long lLastPCIFrameCount = 0;
		long lFPBCount          = 0;
		long lFitsFrameCount	= 0;
		long lFitsNumber		= 0;
		long lReadoutStartTime	= 0;

		m_hFits = 0;

		Thread.currentThread().setName( "Owl - ExposeRunnableCR" );

		try
		{
			if ( m_exposeActionListener != null )
			{
				m_exposeActionListener.setExposeAction( ExposeActionListener.ABORT );
			}

			// Pre-fill image buffer if requested
			// --------------------------------------------------------------
			if ( m_exposeInfo.isFillBuffer() )
			{
				MainApp.infoStart( "Filling image buffer" );
				CameraAPI.FillImageBuffer( m_exposeInfo.getBufferFill() );
				MainApp.infoEnd();
			}

			MainApp.infoStart( "Initializing continuous readout" );

			m_exposeInfo.randomPixelCountColor( java.awt.Color.black );
			m_exposeInfo.setPixelRange( 0, m_numOfFrames );
			m_exposeInfo.setFrameCount( 0 );

			if ( m_exposeInfo.isDisplay() )
			{
				MainApp.warn( "Image display not supported during continuous readout!" );
			}

			// Check for valid frame count
			if ( m_numOfFrames <= 0 )
			{
				throw new Exception( "Number of frames must be > 0" );
			}
	
			// Pre-get deinterlacing algorithm and channel count ( optional )
			// --------------------------------------------------------------
			int arg = -1;
			int algorithm = m_exposeInfo.getDeinterlaceAlgorithm();

			if ( algorithm == CameraAPI.DEINTERLACE_HAWAII_RG )
			{
				arg = CameraAPI.Cmd2( CameraAPI.TIM_ID, CameraAPI.RNC );
			}

			// Get the image size
			// --------------------------------------------------------------
			int[] dImageSize = CameraAPI.GetImageSize();
			int dRows = dImageSize[ 0 ];
			int dCols = dImageSize[ 1 ];
			lImageSize = dRows * dCols * 2;
	
			if ( !CameraAPI.GetDeviceString().contains( "PCIe" ) && ( lImageSize & 0x3FF ) != 0 )
			{
				lBoundedImageSize = lImageSize - ( lImageSize & 0x3FF ) + 1024;
			}
			else
			{
				lBoundedImageSize = lImageSize;
			}
	
			if ( m_bStop ) { throw new Exception( STOP_MSG ); }
	
			if ( lBoundedImageSize <= 0 )
			{
				throw new Exception( "Calculated an image size of zero!" );
			}

			lFramesPerBuffer = ( long )Math.floor(
								( CameraAPI.GetImageBufferSize() / lBoundedImageSize ) );
	
			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			// Determine the max frames per FITS file
			// -------------------------------------------------------------
			long lMaxFitsFrames =
							( long )Math.floor( Math.pow( 2, 31 ) / lBoundedImageSize ) - 1;

			// Get FITS filename and check for valid extension
			// -------------------------------------------------------------
			if ( !m_exposeInfo.getFileExtension().equals( ExposePanel.FITS_FILEEXT ) )
			{
				throw new Exception( "File type MUST be FITS! Currently set to: " +
									  m_exposeInfo.getFileExtension() );
			}

			String fitsFilename = m_exposeInfo.getFilename();

			MainApp.infoEnd();

			// Create the FITS data cube
			// -------------------------------------------------------------
			MainApp.infoStart( "Creating FITS data cube" );
			m_hFits = CameraAPI.Create3DFitsFile( fitsFilename,
												  dRows,
												  dCols,
												  16 );
			MainApp.infoEnd();

			// Set the frames-per-buffer
			MainApp.infoStart( "Setting frames-per-buffer to " + lFramesPerBuffer );
			CameraAPI.Cmd( CameraAPI.TIM_ID, FPB, ( int )lFramesPerBuffer, CameraAPI.DON );
			MainApp.infoEnd();

			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			// Set the number of frames-to-take
			MainApp.infoStart( "Setting number-of-frames to " + m_numOfFrames );
			CameraAPI.Cmd( CameraAPI.TIM_ID, SNF, m_numOfFrames, CameraAPI.DON );
			MainApp.infoEnd();

			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			// Set the exposure time
			// --------------------------------------------------------------
			int dExpTime = ( int )( m_exposeInfo.getExposeTime() * 1000.0 );

			MainApp.infoStart( "Setting exposure time" );
			CameraAPI.Cmd( CameraAPI.TIM_ID, CameraAPI.SET, dExpTime, CameraAPI.DON );
			MainApp.infoEnd();

			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			// Start the exposure
			// --------------------------------------------------------------
			MainApp.infoStart( "Starting exposure " );
			CameraAPI.Cmd( CameraAPI.TIM_ID, CameraAPI.SEX, CameraAPI.DON );
			MainApp.infoEnd();

			if ( m_bStop ) { throw new Exception( STOP_MSG ); }

			lReadoutStartTime = System.currentTimeMillis();

			// Read the images
			while ( lPCIFrameCount < m_numOfFrames )
			{
				if ( m_bStop ) { throw new Exception( STOP_MSG ); }

				lPCIFrameCount = CameraAPI.GetFrameCount();

				if ( m_bStop ) { throw new Exception( STOP_MSG ); }

				if ( lFitsFrameCount > lMaxFitsFrames )
				{
					CameraAPI.CloseFitsFile( m_hFits );

					lFitsFrameCount = 0;
					lFitsNumber++;

					fitsFilename = OwlUtilities.FilenameIncr( new File( fitsFilename ) );

					m_hFits = CameraAPI.Create3DFitsFile( fitsFilename,
														  dRows,
														  dCols,
														  16 );
				}

				if ( m_bStop ) { throw new Exception( STOP_MSG ); }

				if ( lPCIFrameCount > lLastPCIFrameCount )
				{
					if ( lFPBCount >= lFramesPerBuffer )
					{
						lFPBCount = 0;
					}

					// Deinterlace image
					// --------------------------------------------------------------
					CameraAPI.DeinterlaceImage( dRows,
												dCols,
												( int )( lFPBCount * ( lBoundedImageSize / 2 ) ),
												algorithm,
												arg );

					if ( m_bStop ) { throw new Exception( STOP_MSG ); }

					// Write FITS file
					// --------------------------------------------------------------
					CameraAPI.WriteTo3DFitsFile( ( long )m_hFits, ( int )( lFPBCount * lBoundedImageSize ) );

					m_exposeInfo.setFrameCount( lPCIFrameCount );

					m_exposeInfo.setReadoutTime( ( System.currentTimeMillis() -
						      					  lReadoutStartTime ) / 1000.0 );

					lLastPCIFrameCount = lPCIFrameCount;
					lFitsFrameCount++;
					lFPBCount++;
				}
			}
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
		}
		finally
		{
			cleanup();

			if ( m_exposeActionListener != null )
			{
				m_exposeActionListener.setExposeAction( ExposeActionListener.EXPOSE );
			}
		}
	}

	// +--------------------------------------------------------------------+
	// |  cleanup()                                                         |
	// +--------------------------------------------------------------------+
	// |  Performs any necessary cleanup, such as setting the camera back   |
	// |  to single readout mode.                                           |
	// +--------------------------------------------------------------------+
	protected void cleanup()
	{
		try
		{
			CameraAPI.CloseFitsFile( m_hFits );

			CameraAPI.Cmd( CameraAPI.TIM_ID, SNF, 1, CameraAPI.DON );
		}
		catch ( ReplyException re ) { MainApp.error( re.toString() ); }
		catch ( Exception e ) {	MainApp.error( e ); }
	}
}
