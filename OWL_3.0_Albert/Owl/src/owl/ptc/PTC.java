package owl.ptc;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.Vector;
import owl.cameraAPI.CameraAPI;
import owl.gui.utils.ProgressWindow;
import owl.img.analysis.ImageDifStats;
import owl.img.analysis.ImageStats;
import owl.main.MainApp;
import owl.main.exposure.ExposeActionListener;
import owl.main.exposure.ExposeInfo;
import owl.main.exposure.ExposeRunnable;
import owl.main.fits.FitsHeaderFrame;


public class PTC implements Runnable, ExposeInfo
{
	private PTCFrame		m_ptcFrame;
	private PTCDataDisplay	m_dataDisplay;
	private ExposeRunnable	m_expRunnable;
	private ProgressWindow	m_progWin;
//	private FitsHeaderFrame m_fitsHeaderFrame;
	private boolean			m_bStop;
	private boolean			m_bCurrentOpenShutter;
	private double			m_gCurrentExpTime;
	private String			m_sCurrentFile;
	private int				m_dNumOfPoints;

	public PTC( PTCFrame ptcFrame ) //, FitsHeaderFrame fitsHeaderFrame )
	{
		m_ptcFrame			= ptcFrame;
//		m_fitsHeaderFrame	= fitsHeaderFrame;
		m_dataDisplay		= new PTCDataDisplay();
		m_expRunnable		= new ExposeRunnable( this, ( ExposeActionListener )null );
		m_progWin			= null;
	}

	public void run()
	{
		int dExposureStep = 0, dOldRows = 0, dOldCols = 0;
		String sTitle = m_ptcFrame.getTitle();

		Thread.currentThread().setName( "Owl - PTC" );

		try
		{
			// Save the old image dimensions
			// ------------------------------------------------
			dOldRows = CameraAPI.GetImageRows();
			dOldCols = CameraAPI.GetImageCols();

			m_ptcFrame.setExposeAction( ExposeActionListener.ABORT );
			m_dNumOfPoints = m_ptcFrame.getNumberOfPoints();
			m_bStop = false;

			if ( !m_dataDisplay.isVisible() )
			{
				m_dataDisplay.setVisible( true );
			}

			m_dataDisplay.setTitle( sTitle + " STATISTICS" );
			m_dataDisplay.clear();

			// Take the dark image
			// -------------------------------------------------
			m_sCurrentFile = m_ptcFrame.getImageDir() + m_ptcFrame.getDarkFile() + ".fit";

			if ( !m_ptcFrame.getUseDiskImages() )
			{
				int dDarkRows = m_ptcFrame.getDarkRows();
				int dDarkCols = m_ptcFrame.getDarkCols();

				CameraAPI.SetImageSize( dDarkRows, dDarkCols );

				m_progWin = new ProgressWindow( 0, ( dDarkRows * dDarkCols ) );
				m_progWin.setVisible( true );

				m_gCurrentExpTime = 0;
				m_bCurrentOpenShutter = false;
				m_expRunnable.run();

				m_progWin.close();

//				CameraAPI.WriteFitsKeyword( "EXPTIME",
//											Double.toString( m_gCurrentExpTime ),
//											null,
//											m_sCurrentFile );
			}

			if ( m_bStop )
			{
				cleanup( dOldRows, dOldCols );
				return;
			}

			// Calculate the dark statistics
			// -------------------------------------------------
			ImageStats darkStats =
					CameraAPI.GetImageStats( m_sCurrentFile,
											 m_ptcFrame.getDarkStartRow(),
											 m_ptcFrame.getDarkEndRow(),
											 m_ptcFrame.getDarkStartCol(),
											 m_ptcFrame.getDarkEndCol() );

			m_dataDisplay.addDarkData( darkStats );

			if ( m_ptcFrame.getDarkOnly() )
			{
				if ( m_ptcFrame.getDeleteImages() )
				{
					deleteFiles();
				}

				m_ptcFrame.setExposeAction( ExposeActionListener.EXPOSE );
				return;
			}

			if ( m_bStop )
			{
				cleanup( dOldRows, dOldCols );
				return;
			}

			// Calculate the number of flat exposures to take
			// -------------------------------------------------
			dExposureStep = ( m_ptcFrame.getEndTime() - m_ptcFrame.getStartTime() ) / m_dNumOfPoints;
			m_gCurrentExpTime = m_ptcFrame.getStartTime();

			// Exit if numberOfPoints is set to the default of 1
			// -------------------------------------------------
			if ( m_dNumOfPoints <= 1 )
			{
				System.err.println( "You cannot create a PTC with a single point!" );
				return;
			}

			// Take the flat images
			// -------------------------------------------------
			Vector<Point2D.Float> xyData = new Vector<Point2D.Float>();

			if ( !m_ptcFrame.getUseDiskImages() )
			{
				int dFlatRows = m_ptcFrame.getFlatRows();
				int dFlatCols = m_ptcFrame.getFlatCols();

				CameraAPI.SetImageSize( dFlatRows, dFlatCols );

				m_progWin = new ProgressWindow( 0, ( dFlatRows * dFlatCols ) );
				m_progWin.setVisible( true );
			}

			for ( int i=0, k=0; i<m_dNumOfPoints; i++, k+=2 )
			{
				for ( int j=0; j<2; j++ )
				{
					if ( m_bStop )
					{
						cleanup( dOldRows, dOldCols );
						return;
					}

					if ( !m_ptcFrame.getUseDiskImages() )
					{
						m_sCurrentFile = m_ptcFrame.getImageDir() + m_ptcFrame.getFlatFile() + ( k + j ) + ".fit";
						m_bCurrentOpenShutter = true;

						// Increment the exposure time for pairs of flats
						// ------------------------------------------------
						if ( j == 0 ) { m_gCurrentExpTime += dExposureStep; }

						MainApp.info( "EXPOSURE # " + ( k + j ) + "  time: " + ( m_gCurrentExpTime / 1000 ) );
						m_expRunnable.run();
					}

					if ( m_bStop )
					{
						cleanup( dOldRows, dOldCols );
						return;
					}

					// Calculate statistics for pairs of flats
					// ------------------------------------------------
					if ( j >= 1 )
					{
						ImageDifStats flatStats = CameraAPI.GetImageDifStats(
										m_ptcFrame.getImageDir() + m_ptcFrame.getFlatFile() + k + ".fit",
										m_ptcFrame.getImageDir() + m_ptcFrame.getFlatFile() + ( k + 1 ) + ".fit",
										m_ptcFrame.getFlatStartRow(),
										m_ptcFrame.getFlatEndRow(),
										m_ptcFrame.getFlatStartCol(),
										m_ptcFrame.getFlatEndCol() );

						double countVal =
								( flatStats.img1Stats.gMean + flatStats.img2Stats.gMean ) / 2.0 - darkStats.gMean;

						double varVal =
								flatStats.imgDifStats.gVariance / 2.0 - darkStats.gVariance;

						// Throw out negative variances
						// ---------------------------------------------
						if ( varVal >= 0 )
						{
							xyData.add( new Point2D.Float( ( float )countVal, ( float )varVal ) );
						}
						m_dataDisplay.addFlatData( k, flatStats, ( float )countVal, ( float )varVal );
					}
				}

				if ( m_bStop )
				{
					cleanup( dOldRows, dOldCols );
					return;
				}
			}

			if ( m_progWin != null ) m_progWin.close();

			PTCPlotFrame plotFrame = new PTCPlotFrame( sTitle );
			plotFrame.linearLeastSquaresFit( xyData, ( float )darkStats.gStdDev );
			plotFrame.setVisible( true );

			if ( m_ptcFrame.getDeleteImages() ) deleteFiles();
		}
		catch ( java.lang.Exception e )
		{
			MainApp.error( e );
		}
		finally
		{
			cleanup( dOldRows, dOldCols );
		}
	}

	public void deleteFiles()
	{
		String sFilename = m_ptcFrame.getImageDir() + m_ptcFrame.getDarkFile() + ".fit";
		( new File( sFilename ) ).delete();

		if ( !m_ptcFrame.getDarkOnly() )
		{
			for ( int i=0; i<( m_dNumOfPoints * 2 ); i++ )
			{
				sFilename = m_ptcFrame.getImageDir() + m_ptcFrame.getFlatFile() + i + ".fit";
				( new File( sFilename ) ).delete();
			}
		}
	}

	public void stop()
	{
		m_bStop = true;
		m_expRunnable.stop();
	}

	@Override
	public int getDeinterlaceAlgorithm()
	{
		return m_ptcFrame.getDeinterlaceAlgorithm();
	}

	@Override
	public int getBufferFill()
	{
		return 0;
	}

	@Override
	public String getDeinterlaceDescription()
	{
		return m_ptcFrame.getDeinterlaceDescription();
	}

	@Override
	public double getExposeTime() throws Exception
	{
		return ( m_gCurrentExpTime / 1000.0 );
	}

	@Override
	public double getDelay()
	{
		return 0.0;
	}

	@Override
	public String getFilename()
	{
		return m_sCurrentFile;
	}

	@Override
	public String getFileExtension()
	{
		return "fit";
	}

	@Override
	public FitsHeaderFrame getFitsHeaderFrame()
	{
		if ( MainApp.mainFrame != null && MainApp.mainFrame.exposePanel != null )
		{
			return MainApp.mainFrame.exposePanel.fitsHeaderFrame;
		}

		return null;
	}

	@Override
	public int getMultipleExposureCount() throws Exception
	{
		return 0;
	}

	@Override
	public void incrementFilename()
	{
	}

	@Override
	public boolean isDelay()
	{
		return false;
	}

	@Override
	public boolean isFillBuffer()
	{
		return false;
	}

	@Override
	public boolean isBeep()
	{
		return false;
	}

	@Override
	public boolean isDisplay()
	{
		return false;
	}

	@Override
	public boolean isMultipleExposure()
	{
		return false;
	}

	@Override
	public boolean isOpenShutter()
	{
		return m_bCurrentOpenShutter;
	}

	@Override
	public boolean isSaveToDisk()
	{
		return true;
	}

	@Override
	public boolean isSubtract()
	{
		return false;
	}

	@Override
	public boolean isSynthImage()
	{
		return false;
	}

	@Override
	public void randomPixelCountColor( java.awt.Color color )
	{
	}

	@Override
	public void setFilename( String sFilename )
	{
	}

	@Override
	public void setElapsedTime( int dElapsedTime )
	{
		m_progWin.setElapsedTime( dElapsedTime );		
	}

	@Override
	public void setFrameCount( long frame )
	{
	}

	@Override
	public void setPixelCount( int dPixelCount )
	{
		m_progWin.setReadoutValue( dPixelCount );
	}

	@Override
	public void setPixelRange( int dMin, int dMax )
	{
		m_progWin.setNewMax( dMax );
	}

	@Override
	public void setReadoutTime( double time )
	{
	}

	private void cleanup( int dRows, int dCols )
	{
		if ( m_progWin != null )
		{
			m_progWin.close();
		}

		m_ptcFrame.setExposeAction( ExposeActionListener.EXPOSE );

		// Reset the image dimensions so normal exposures can be taken
		// -----------------------------------------------------------
		try
		{
			CameraAPI.SetImageSize( dRows, dCols );
		}
		catch ( java.lang.Exception e )
		{
			MainApp.error( e );
		}
	}
}
