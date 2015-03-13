package owl.main.exposure;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import owl.CCParams.CCScriptEvent;
import owl.CCParams.CCScriptListener;
import owl.cameraAPI.CameraAPI;
import owl.dir.dialog.DirDialog;
import owl.gui.popupmenus.PopupMenuLabel;
import owl.gui.scriptable.ScriptableIconButton;
import owl.gui.utils.OwlButtonFactory;
import owl.gui.utils.OwlUtilities;
import owl.img.analysis.AreaPlotFrame;
import owl.img.analysis.ColPlotFrame;
import owl.img.analysis.HistogramPlotFrame;
import owl.img.analysis.RowPlotFrame;
import owl.img.analysis.StatsFrame;
import owl.main.MainApp;
import owl.main.fits.FitsHeaderFrame;
import owl.main.owltypes.OwlLabledIcon;
import owl.main.owltypes.OwlNumberField;
import owl.main.owltypes.OwlPanel;
import owl.main.owltypes.OwlTextField;
import owl.main.setup.SetupEvent;
import owl.main.setup.SetupFrame;
import owl.main.setup.SetupListener;


public class ExposePanel extends OwlPanel
implements ExposeInfo, ActionListener, ItemListener, SetupListener, CCScriptListener
{
	private static final long serialVersionUID = -5987110842054126492L;

	public static final String FITS_FILEEXT		= "fit";
	public static final String TIFF_FILEEXT		= "tif";

	//--------------------------------------------------------------------------
    //   Constants:
    //--------------------------------------------------------------------------
	private final String DIR_BROWSE_ACTION		= "DIR_BROWSE";
	private final String EDIT_HEADER_ACTION		= "FITS";
	private final String LAUNCH_DISPLAY_ACTION	= "LAUNCH_DISPLAY";
	private final String ROW_PLOT_ACTION		= "ROW_PLOT";
	private final String COL_PLOT_ACTION		= "COL_PLOT";
	private final String AREA_PLOT_ACTION		= "AREA_PLOT";
	private final String HIST_PLOT_ACTION		= "HISTOGRAM_PLOT";
	private final String STATS_ACTION			= "IMAGE STATISTICS";

	private final String SAVE_CHECKED_HTML		= "<html><font color=\"#000000\">Save</font></html>";
	private final String SAVE_UNCHECKED_HTML	= "<html><font color=\"#ff0000\">Save</font></html>";

	private final String EXPOP_MULTEXP_PREF		= "MultExp";
	private final String EXPOP_MULTCNT_PREF		= "MultCnt";
	private final String EXPOP_SHUTTER_PREF		= "OpenShutter";
	private final String EXPOP_SYNIMG_PREF		= "SyntheticImg";
	private final String EXPOP_BEEP_PREF		= "Beep";
	private final String EXPOP_DEINT_PREF		= "Deinterlace";
	private final String EXPOP_DELAY_PREF		= "Delay";
	private final String EXPOP_DELAY_TIME_PREF	= "DelayTime";
	private final String EXPOP_FILL_BUF_PREF	= "FillBuffer";
	private final String EXPOP_FILL_VAL_PREF	= "FillValue";

	private final String FILEOP_DIR_PREF		= "Directory";
	private final String FILEOP_FILE_PREF		= "Filename";
	private final String FILEOP_SAVE_PREF		= "Save";
	private final String FILEOP_DISPLAY_PREF	= "Display";
	private final String FILEOP_INCR_PREF		= "Increment";
	private final String FILEOP_TYPE_PREF		= "FileType";

	private final String EXPTIME_PREF			= "ExpTime";

    //--------------------------------------------------------------------------
    //   Public Variables:
    //--------------------------------------------------------------------------
	public FitsHeaderFrame			fitsHeaderFrame;

	//--------------------------------------------------------------------------
    //   Private Variables:
    //--------------------------------------------------------------------------
	private JLabel       			readTimeLabel;
	private JProgressBar 			progressBar;
	private OwlNumberField 			expTimeNumFld;
	private OwlNumberField 			multExpNumFld;
	private OwlNumberField			delayNumFld;
	private OwlNumberField			fillBufNumFld;
	private JCheckBox 				multExpChkbox;
	private JCheckBox    			openShutterChkbox;
	private JCheckBox    			saveToDiskChkbox;
	private JCheckBox    			incrementFileChkbox;
	private JCheckBox    			synthImageChkbox;
	private JCheckBox    			displayChkbox;
	private JCheckBox	 			beepChkbox;
	private JCheckBox				subtractChkbox;
	private JCheckBox				delayChkbox;
	private JCheckBox				fillBufChkbox;
	private OwlTextField   			directoryTxtfld;
	private OwlTextField   			filenameTxtfld;
	private JComboBox    			deinterlaceComboBox;
	private JPanel       			optionsPanel;
	private JPanel       			imgFileOptPanel;
	private JPanel					imgAnalysisPanel;
	private File         			file;
	private PopupMenuLabel  		fileTypeLabel;
	private DirDialog				dirDialog;
	private RowPlotFrame			imageRowPlot;
	private ColPlotFrame			imageColPlot;
	private AreaPlotFrame			imageAreaPlot;
	private StatsFrame				imageStatsFrame;
	private HistogramPlotFrame		imageHistogramPlot;
	private TreeMap<String,String>	vToolTips;


	//--------------------------------------------------------------------------
    //   Constructor:
    //--------------------------------------------------------------------------
	public ExposePanel()
	{
		super( null );

		CreateMainPanel();

		fitsHeaderFrame = new FitsHeaderFrame( this );
		file = new File( "." );

		dirDialog			= null;
		imageRowPlot		= null;
		imageColPlot		= null;
		imageAreaPlot		= null;
		imageHistogramPlot	= null;

		equalizePanels();
		loadPreferences();

		SetupFrame.addSetupListener( this );
	}

	// +--------------------------------------------------------------------+
	// |  equalizePanels                                                    |
	// +--------------------------------------------------------------------+
	// |  Used to make sure all the sub-panels have the same width          |
	// +--------------------------------------------------------------------+
	public void equalizePanels()
	{
		Dimension opDim   = optionsPanel.getPreferredSize();
		Dimension ifopDim = imgFileOptPanel.getPreferredSize();
		Dimension iapDim  = imgAnalysisPanel.getPreferredSize();

		int newWidth = opDim.width;
		if ( ifopDim.width > newWidth ) newWidth = ifopDim.width;
		if ( iapDim.width > newWidth ) newWidth = iapDim.width;

		optionsPanel.setPreferredSize( new Dimension( newWidth, opDim.height ) );
		imgFileOptPanel.setPreferredSize( new Dimension( newWidth, ifopDim.height ) );
		imgAnalysisPanel.setPreferredSize( new Dimension( newWidth, iapDim.height ) );
	}

	public int getProgressMax() { return progressBar.getMaximum(); }

	// +--------------------------------------------------------------------+
	// |  getFitsHeaderFrame ( ExposeInfo )                                 |
	// +--------------------------------------------------------------------+
	// |  Returns a reference to the FitsHeaderFrame object.                |
	// +--------------------------------------------------------------------+
	public FitsHeaderFrame getFitsHeaderFrame() { return fitsHeaderFrame; }

	// +--------------------------------------------------------------------+
	// |  setElapsedTime ( ExposeInfo )                                     |
	// +--------------------------------------------------------------------+
	// |  Set elapsed exposure time. Set to zero to disable string.         |
	// +--------------------------------------------------------------------+
	public void setElapsedTime( int val )
	{
		if ( val == 0 )
		{
			progressBar.setString( null );
		}
		else
		{
			progressBar.setString( "Time Remaining: " + Integer.toString( val ) + " sec" );
		}

		if ( exposeListeners != null )
		{
			for ( int i=0; i<exposeListeners.size(); i++ )
			{
				exposeListeners.get( i ).elapsedTimeChanged( new ExposeEvent( this, ( float )val ) );
			}
		}
	}

	// +--------------------------------------------------------------------+
	// |  setPixelCount ( ExposeInfo )                                      |
	// +--------------------------------------------------------------------+
	// |  Set the current pixel count label for the progress bar.           |
	// +--------------------------------------------------------------------+
	public void setPixelCount( int val )
	{
		progressBar.setValue( val );

		if ( exposeListeners != null )
		{
			for ( int i=0; i<exposeListeners.size(); i++ )
			{
				exposeListeners.get( i ).pixelCountChanged( new ExposeEvent( this, val ) );
			}
		}
	}

	// +--------------------------------------------------------------------+
	// |  setPixelRange ( ExposeInfo )                                      |
	// +--------------------------------------------------------------------+
	// |  Set the pixel range for the progress bar.                         |
	// +--------------------------------------------------------------------+
	public void setPixelRange( int min, int max )
	{
		progressBar.setMinimum( min );
		progressBar.setMaximum( max );

		if ( exposeListeners != null )
		{
			for ( int i=0; i<exposeListeners.size(); i++ )
			{
				exposeListeners.get( i ).minMaxPixelsChanged( new ExposeEvent( this, min, max ) );
			}
		}
	}

	// +--------------------------------------------------------------------+
	// |  randomPixelCountColor ( ExposeInfo )                              |
	// +--------------------------------------------------------------------+
	// |  Sets the progress bar to the specified color or a random color if |
	// |  the color parameter is null.                                      |
	// +--------------------------------------------------------------------+
	public void randomPixelCountColor( Color color )
	{
		if ( color == null )
		{
			Random rand = new Random();
			color = new Color( rand.nextInt() );
		}

		progressBar.setForeground( color );

		if ( exposeListeners != null )
		{
			for ( int i=0; i<exposeListeners.size(); i++ )
			{
				exposeListeners.get( i ).readoutColorChanged( new ExposeEvent( this, color ) );
			}
		}
	}

	// +--------------------------------------------------------------------+
	// |  setReadoutTime ( ExposeInfo )                                     |
	// +--------------------------------------------------------------------+
	// |  Set the current readout time label.                               |
	// +--------------------------------------------------------------------+
	public void setReadoutTime( double aTime )
	{
		DecimalFormat decimalFormat = new DecimalFormat( "##0.00" );
		readTimeLabel.setText( decimalFormat.format( aTime ) + " sec" );
	}

	// +--------------------------------------------------------------------+
	// |  setFrameCount ( ExposeInfo )                                      |
	// +--------------------------------------------------------------------+
	// |  Set the current frame count string on the progress bar.           |
	// +--------------------------------------------------------------------+
	public void setFrameCount( long frame )
	{
		if ( frame == 0 )
		{
			progressBar.setValue( 0 );
			progressBar.setString( "Frame: 0" );
		}
		else
		{
			progressBar.setValue( ( int )frame );
			progressBar.setString( "Frame: " + Long.toString( frame ) );
		}

		if ( exposeListeners != null )
		{
			for ( int i=0; i<exposeListeners.size(); i++ )
			{
				exposeListeners.get( i ).frameCountChanged( new ExposeEvent( this, frame ) );
			}
		}
	}

	public void setFilePath( String sPath )
	{
		directoryTxtfld.setText( sPath );
	}

	// +--------------------------------------------------------------------+
	// |  setFilename ( ExposeInfo )                                        |
	// +--------------------------------------------------------------------+
	// |  Set the image filename to use.                                    |
	// +--------------------------------------------------------------------+
	public void setFilename( String sFilename )
	{
		filenameTxtfld.setText( sFilename );
	}

	// +--------------------------------------------------------------------+
	// |  More ( ExposeInfo ) methods                                       |
	// +--------------------------------------------------------------------+
	public int getBufferFill() { return ( fillBufNumFld.getInt() & 0xFFFF ); }
	public int getMultipleExposureCount() { return multExpNumFld.getInt(); }
	public double getExposeTime() { return expTimeNumFld.getDouble();  }
	public double getDelay() { return delayNumFld.getDouble();  }

	public String getFilePath()
	{
		return ( directoryTxtfld.getText() +
				 System.getProperty( "file.separator" ) );
	}

	// +--------------------------------------------------------------------+
	// |  getFilename ( ExposeInfo )                                        |
	// +--------------------------------------------------------------------+
	// |  Returns the fully qualified image file ( path + filename ) as a   |
	// |  single, combined string.                                          |
	// +--------------------------------------------------------------------+
	public String getFilename()
	{
		String fname = filenameTxtfld.getText();

		int dotIndex = fname.lastIndexOf( '.' );

		if ( dotIndex > 0 )
		{
			String ext = fname.substring( dotIndex + 1 );

			if ( !ext.equals( fileTypeLabel.getRawText() ) )
			{
				setFileExtension( fileTypeLabel.getRawText() );
			}
		}
		else
		{
			setFileExtension( fileTypeLabel.getRawText() );
		}
			

		return new String( directoryTxtfld.getText() +
						   System.getProperty( "file.separator" ) +
						   fname );
	}

	// +--------------------------------------------------------------------+
	// |  More ( ExposeInfo ) methods                                       |
	// +--------------------------------------------------------------------+
	public boolean isSynthImage()       { return synthImageChkbox.isSelected();  }
	public boolean isOpenShutter()      { return openShutterChkbox.isSelected(); }
	public boolean isSaveToDisk()       { return saveToDiskChkbox.isSelected();  }
	public boolean isDisplay()          { return displayChkbox.isSelected();     }
	public boolean isBeep()             { return beepChkbox.isSelected();        }
	public boolean isSubtract()			{ return subtractChkbox.isSelected();    }
	public boolean isDelay()			{ return delayChkbox.isSelected();       }
	public boolean isFillBuffer()		{ return fillBufChkbox.isSelected();	 }

	public boolean isMultipleExposure()
	{
		//  Make sure the multiple exposure checkbox is greater
		//  than zero ... or what's the point
		// +----------------------------------------------------+
		if ( multExpChkbox.isSelected() && multExpNumFld.getInt() <= 0 )
		{
			MainApp.error(
					"Multiple exposure option MUST be greater than zero! " +
					"Please enter a larger number and try again." );
		}

		return multExpChkbox.isSelected();
	}

	// +--------------------------------------------------------------------+
	// |  incrementFilename ( ExposeInfo )                                  |
	// +--------------------------------------------------------------------+
	// |  Increments the image filename by one.                             |
	// +--------------------------------------------------------------------+
	public void incrementFilename()
	{
		if ( incrementFileChkbox.isSelected() )
		{
			filenameTxtfld.setText( OwlUtilities.FilenameIncr( getFilename() ) );
		}
	}

	public int getDeinterlaceAlgorithm()
	{
		switch ( deinterlaceComboBox.getSelectedIndex() )
		{
			case 0:  return CameraAPI.DEINTERLACE_NONE;
			case 1:  return CameraAPI.DEINTERLACE_PARALLEL;
			case 2:  return CameraAPI.DEINTERLACE_SERIAL;
			case 3:  return CameraAPI.DEINTERLACE_CCD_QUAD;
			case 4:  return CameraAPI.DEINTERLACE_IR_QUAD;
			case 5:  return CameraAPI.DEINTERLACE_CDS_IR_QUAD;
			case 6:  return CameraAPI.DEINTERLACE_HAWAII_RG;
			case 7:  return CameraAPI.DEINTERLACE_STA1600;
			default: return deinterlaceComboBox.getSelectedIndex(); //return 99;
		}
	}

	public String getDeinterlaceDescription()
	{
		ImageIcon image =
			( ImageIcon )deinterlaceComboBox.getSelectedItem();

		return image.getDescription();
	}

	public void setDeinterlaceAlgorithm( int index )
	{
		try
		{
			deinterlaceComboBox.setSelectedIndex( index );
		}
		catch ( IllegalArgumentException iae )
		{
			deinterlaceComboBox.setSelectedIndex( 0 );
		}
	}

	public String getFileExtension()
	{
		return fileTypeLabel.getRawText();
	}

	public void setOptionsVisible( boolean isVisible )
	{
		optionsPanel.setVisible( isVisible );
	}

	public void setFileOptionsVisible( boolean isVisible )
	{
		imgFileOptPanel.setVisible( isVisible );
	}

	public void setImageAnalysisVisible( boolean isVisible )
	{
		imgAnalysisPanel.setVisible( isVisible );
	}

	private void CreateMainPanel()
	{
		setName( "Expose" );
		setLayout( gbl );

		JLabel expTimeLabel = new JLabel( "Exp Time (s):" );
		expTimeNumFld  = new OwlNumberField( ( double )0, 5 );

		JLabel readTimeTextLabel = new JLabel( "Read Time:" );
		readTimeLabel = new JLabel( "0" );

		progressBar = new JProgressBar( 0, 100 );
		progressBar.setStringPainted( true );
		progressBar.setPreferredSize( new Dimension( 260, 15 ) );

		optionsPanel     = createOptionsPanel();
		imgFileOptPanel  = createImgFileOptPanel();
		imgAnalysisPanel = createImgAnalysisPanel();

		addComponent( expTimeLabel,      3,  5, 0, 0, GridBagConstraints.WEST, 0, 0, 1, 1 );
		addComponent( expTimeNumFld,     3,  5, 0, 0, GridBagConstraints.WEST, 0, 1, 1, 1 );
		addComponent( readTimeTextLabel, 2, 15, 0, 0, GridBagConstraints.WEST, 0, 2, 1, 1 );
		addComponent( readTimeLabel,     2,  5, 0, 0, GridBagConstraints.WEST, 0, 3, 1, 1 );
		addComponent( progressBar,       2,  5, 5, 5, GridBagConstraints.WEST, 2, 0, 1, 4 );
		addComponent( optionsPanel,      0,  0, 0, 0, GridBagConstraints.WEST, 3, 0, 1, 4 );
		addComponent( imgFileOptPanel,   0,  0, 0, 0, GridBagConstraints.WEST, 4, 0, 1, 4 );
		addComponent( imgAnalysisPanel,  0,  0, 0, 0, GridBagConstraints.WEST, 5, 0, 1, 4 );
	}

	private JPanel createOptionsPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout( gbl );
		panel.setName( "Exposure Options" );
		panel.setBorder( new TitledBorder( panel.getName() ) );

		subtractChkbox    = new JCheckBox( "Subtract", false );
		beepChkbox		  = new JCheckBox( "Beep", false );
		synthImageChkbox  = new JCheckBox( "Synthetic Image", false );
		openShutterChkbox = new JCheckBox( "Open Shutter", true );

		delayChkbox = new JCheckBox( "Delay Exposure (sec):", false );
		delayNumFld = new OwlNumberField( 5, 5 );

		multExpChkbox = new JCheckBox( "Multiple Exposure:", false );
		multExpNumFld = new OwlNumberField( 0, 5 );

		fillBufChkbox     = new JCheckBox( "Pre-Fill Image Buffer:", false );
		fillBufNumFld     = new OwlNumberField( 0, 5 );
		fillBufNumFld.setToolTipText( "NOTE: Truncates to 16-bits!" );

		ImageIcon images[] = {
				new ImageIcon( MainApp.getBitmapPath() + "SDEINT1b.gif", "None" ),
				new ImageIcon( MainApp.getBitmapPath() + "SDEINT2b.gif", "Parallel" ),
				new ImageIcon( MainApp.getBitmapPath() + "SDEINT3b.gif", "Serial" ),
				new ImageIcon( MainApp.getBitmapPath() + "SDEINT4b.gif", "CCD Quad" ),
				new ImageIcon( MainApp.getBitmapPath() + "SDEINT5b.gif", "IR Quad" ),
				new ImageIcon( MainApp.getBitmapPath() + "SDEINT6b.gif", "CDS IR Quad" ),
				new ImageIcon( MainApp.getBitmapPath() + "SDEINT8b.gif", "Hawaii RG" ),
				new ImageIcon( MainApp.getBitmapPath() + "SDEINT9b.gif", "STA 1600" )
		};

		vToolTips = new TreeMap<String,String>();
		vToolTips.put( "None", "None - No de-interlacing will be performed" );
		vToolTips.put( "Parallel", "Parallel - Two channel de-interlacing" );
		vToolTips.put( "Serial", "Serial - Two channel de-interlacing" );
		vToolTips.put( "CCD Quad", "CCD Quad - Four channel CCD de-interlacing" );
		vToolTips.put( "IR Quad", "IR Quad - Four channel IR de-interlacing" );
		vToolTips.put( "CDS IR Quad", "CDS IR Quad - Four channel IR correlated double sampling de-interlacing" );
		vToolTips.put( "Hawaii RG", "Hawaii RG - Valid for any Hawaii RG array ( H2RG, H4RG, etc )" );
		vToolTips.put( "STA 1600", "STA 1600" );

		java.util.Vector<ImageIcon> list = new java.util.Vector<ImageIcon>( Arrays.asList( images ) );

		try
		{
			String[] sCustomNames =
						CameraAPI.GetCustomDeinterlaceAlgorithms( MainApp.getCustomDeinterlacePath() );

			for ( int i=0; i<sCustomNames.length; i++ )
			{
				list.add( new OwlLabledIcon( MainApp.getBitmapPath() + "SDEINT_CUST.gif",
											 sCustomNames[ i ] ) );
			}
		}
		catch ( Exception e ) {}

		deinterlaceComboBox = new JComboBox( list );
		deinterlaceComboBox.setRenderer( new ComboBoxRenderer() );
		deinterlaceComboBox.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		deinterlaceComboBox.setMaximumRowCount( deinterlaceComboBox.getMaximumRowCount() + 4 );

		addComponent( panel, multExpChkbox,       0,  0, 0, 0, GridBagConstraints.WEST,      0, 0, 1, 1 );
		addComponent( panel, multExpNumFld,       0,  0, 0, 0, GridBagConstraints.WEST,      0, 1, 1, 1 );
		addComponent( panel, deinterlaceComboBox, 0,  5, 3, 5, GridBagConstraints.NORTHWEST, 0, 2, 3, 1 );
		addComponent( panel, delayChkbox,         0,  0, 0, 0, GridBagConstraints.WEST,      1, 0, 1, 1 );
		addComponent( panel, delayNumFld,         0,  0, 0, 0, GridBagConstraints.WEST,      1, 1, 1, 1 );

		addComponent( panel, fillBufChkbox,       0,  0, 0, 0, GridBagConstraints.WEST,      2, 0, 1, 1 );
		addComponent( panel, fillBufNumFld,       0,  0, 0, 0, GridBagConstraints.WEST,      2, 1, 1, 1 );

		addComponent( panel, openShutterChkbox,   0,  0, 0, 0, GridBagConstraints.WEST,      3, 0, 1, 1 );
		addComponent( panel, beepChkbox,          0,  0, 0, 0, GridBagConstraints.WEST,      3, 1, 1, 1 );
		addComponent( panel, synthImageChkbox,    0,  0, 0, 0, GridBagConstraints.WEST,      4, 0, 1, 1 );
		addComponent( panel, subtractChkbox,      0,  0, 0, 0, GridBagConstraints.WEST,      4, 1, 1, 2 );

		return panel;
	}

	private JPanel createImgAnalysisPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
		panel.setName( "Image Analysis" );
		panel.setBorder( new TitledBorder( panel.getName() ) );

		displayChkbox = new JCheckBox( "Display", false );
		displayChkbox.setPreferredSize( new Dimension( 65, 20 ) );
		displayChkbox.addItemListener( this );

		ScriptableIconButton ds9Button =
							ScriptableIconButton.create( "ds9.gif",
													     "Launch DS9",
														  new Dimension( 25, 20 ),
														  this,
														  LAUNCH_DISPLAY_ACTION );

		ScriptableIconButton rowPlotButton =
							ScriptableIconButton.create( "rowplot.png",
													     "Row Plot",
														  new Dimension( 25, 20 ),
														  this,
														  ROW_PLOT_ACTION );

		ScriptableIconButton colPlotButton =
							ScriptableIconButton.create( "colplot.png",
													     "Column Plot",
														  new Dimension( 25, 20 ),
														  this,
														  COL_PLOT_ACTION );

		ScriptableIconButton areaPlotButton =
							ScriptableIconButton.create( "areaplot.png",
													     "Area Plot",
														  new Dimension( 25, 20 ),
														  this,
														  AREA_PLOT_ACTION );

		ScriptableIconButton histPlotButton =
							ScriptableIconButton.create( "hist2.png",
													     "Histogram",
														  new Dimension( 25, 20 ),
														  this,
														  HIST_PLOT_ACTION );

		ScriptableIconButton statsButton =
							ScriptableIconButton.create( "stats.gif",
													     "Image Statistics",
														  new Dimension( 25, 20 ),
														  this,
														  STATS_ACTION );

		panel.add( displayChkbox );
		panel.add( ds9Button );
		panel.add( rowPlotButton );
		panel.add( colPlotButton );
		panel.add( areaPlotButton );
		panel.add( histPlotButton );
		panel.add( statsButton );

		return panel;
	}

	private JPanel createImgFileOptPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout( gbl );
		panel.setName( "Image File Options" );
		panel.setBorder( new TitledBorder( panel.getName() ) );

		JLabel directoryLabel = new JLabel( "Dir:" );

		try
		{
			file = new File( "." );
			directoryTxtfld = new OwlTextField( file.getCanonicalPath(), 24 );
		}
		catch ( java.io.IOException ioe )
		{
			MainApp.warn( "Could not determine current directory for image file!" );
		}

		saveToDiskChkbox    = new JCheckBox( SAVE_CHECKED_HTML, true );
		saveToDiskChkbox.addItemListener( this );

		incrementFileChkbox = new JCheckBox( "Increment Filename", false );

		JLabel filenameLabel = new JLabel( "File:" );
		filenameTxtfld = new OwlTextField( "Image.fit", 24 );

		JButton dirBrowseButton = OwlButtonFactory.createIconButton( "folder.gif",
																	 "Browse",
																	 DIR_BROWSE_ACTION,
																	 this );

		ImageIcon fitsIcon = new ImageIcon( MainApp.getBitmapPath() + "FitsHeader.png" );

		JButton fitsHeaderButton = OwlButtonFactory.create( fitsIcon,
															"Edit FITS Header",
															 new Dimension( 22, 20 ),
															 this );

		fitsHeaderButton.setActionCommand( EDIT_HEADER_ACTION );

		String[] list = { FITS_FILEEXT, TIFF_FILEEXT };
		fileTypeLabel = new PopupMenuLabel( list, 0 );
		fileTypeLabel.addActionListener( this );
		fileTypeLabel.setSquareBrackets();
		fileTypeLabel.allowLeftButtonActivation();
		fileTypeLabel.setForeground( Color.DARK_GRAY );

		JPanel subpanel = new JPanel();
		subpanel.setLayout( gbl );
		addComponent( subpanel, saveToDiskChkbox,    0, 0, 0, 0, GridBagConstraints.WEST, 0, 0, 1, 1 );
		addComponent( subpanel, incrementFileChkbox, 0, 0, 0, 0, GridBagConstraints.WEST, 0, 1, 1, 1 );
		addComponent( subpanel, fitsHeaderButton,    0, 2, 0, 0, GridBagConstraints.WEST, 0, 2, 1, 1 );

		addComponent( panel, subpanel,         0, 0, 3,  0, GridBagConstraints.WEST, 0, 0, 1, 3 );
		addComponent( panel, directoryLabel,   0, 5, 0,  0, GridBagConstraints.EAST, 1, 0, 1, 1 );
		addComponent( panel, directoryTxtfld,  0, 5, 0,  0, GridBagConstraints.WEST, 1, 1, 1, 1 );
		addComponent( panel, dirBrowseButton,  0, 5, 0, 10, GridBagConstraints.WEST, 1, 2, 1, 1 );
		addComponent( panel, filenameLabel,    0, 0, 0,  0, GridBagConstraints.EAST, 2, 0, 1, 1 );
		addComponent( panel, filenameTxtfld,   0, 5, 0,  0, GridBagConstraints.WEST, 2, 1, 1, 1 );
		addComponent( panel, fileTypeLabel,    0, 5, 0,  0, GridBagConstraints.WEST, 2, 2, 1, 1 );

		return panel;
	}

	@Override
	public void actionPerformed( ActionEvent ae )
	{
		if ( ae.getActionCommand().equals( DIR_BROWSE_ACTION ) )
		{
			if ( dirDialog == null )
			{
				dirDialog = new DirDialog( null );
			}

			dirDialog.setPath( directoryTxtfld.getText() );

			if ( dirDialog.showDialog() )
			{
				file = new File( dirDialog.getPath() );
				directoryTxtfld.setText( dirDialog.getPath() );
			}
		}

		else if ( ae.getActionCommand().equals( EDIT_HEADER_ACTION ) )
		{
			fitsHeaderFrame.setVisible( true );
		}

		else if ( ae.getActionCommand().equals( FITS_FILEEXT ) ||
				  ae.getActionCommand().equals( TIFF_FILEEXT )  )
		{
			setFileExtension( ae.getActionCommand() );
		}

		else if ( ae.getActionCommand().equals( LAUNCH_DISPLAY_ACTION ) )
		{
			try
			{
				CameraAPI.LaunchDisplay();
			}
			catch ( Exception e )
			{
				MainApp.error( e.getMessage() );
			}
		}

		else if ( ae.getActionCommand().equals( ROW_PLOT_ACTION ) )
		{
			if ( imageRowPlot == null )
			{
				imageRowPlot = new RowPlotFrame();
			}

			imageRowPlot.setVisible( true );
		}

		else if ( ae.getActionCommand().equals( COL_PLOT_ACTION ) )
		{
			if ( imageColPlot == null )
			{
				imageColPlot = new ColPlotFrame();
			}

			imageColPlot.setVisible( true );
		}

		else if ( ae.getActionCommand().equals( AREA_PLOT_ACTION ) )
		{
			if ( imageAreaPlot == null )
			{
				imageAreaPlot = new AreaPlotFrame();
			}

			imageAreaPlot.setVisible( true );
		}

		else if ( ae.getActionCommand().equals( HIST_PLOT_ACTION ) )
		{
			if ( imageHistogramPlot == null )
			{
				imageHistogramPlot = new HistogramPlotFrame();
		}

			imageHistogramPlot.setVisible( true );
		}

		else if ( ae.getActionCommand().equals( STATS_ACTION ) )
		{
			if ( imageStatsFrame == null )
			{
				imageStatsFrame = new StatsFrame();
			}

			imageStatsFrame.setVisible( true );
		}

		else
		{
			super.actionPerformed( ae );
		}
	}

	private void setFileExtension( String ext )
	{
		String file = filenameTxtfld.getText();

		int dotIndex = file.lastIndexOf( '.' );

		if ( dotIndex < 0 )
		{
			file = file + "." + ext;
		}
		else
		{
			file = file.substring( 0, dotIndex + 1 ) + ext;
		}

		filenameTxtfld.setText( file );
	}

	public void itemStateChanged( ItemEvent ie )
	{
		JCheckBox src = ( JCheckBox )ie.getSource();

		//  Windows version MUST have save checked in order to
		//  display the FITS file image
		// +-----------------------------------------------------+
		if ( System.getProperty( "os.name" ).contains( "Windows" ) )
		{
			if ( src.getText().contains( "Save" ) )
			{
				if ( !src.isSelected() )
				{
					src.setText( SAVE_UNCHECKED_HTML );
					displayChkbox.setSelected( false );
				}
				else
				{
					src.setText( SAVE_CHECKED_HTML );
				}
			}
	
			else if ( src.getText().contains( "Display" )
					  && !saveToDiskChkbox.isSelected() )
			{
				src.setSelected( false );
			}
		}

		//  Unix version just sets save checkbox color
		// +--------------------------------------------------+
		else
		{
			if ( src.getText().contains( "Save" ) )
			{
				if ( !src.isSelected() )
				{
					src.setText( SAVE_UNCHECKED_HTML );
				}
				else
				{
					src.setText( SAVE_CHECKED_HTML );
				}
			}
		}
	}

	@Override
	public void CCScriptChanged( CCScriptEvent ccse )
	{
		if ( ccse.action.equals( "SPLIT_SERIAL" )   ||
			 ccse.action.equals( "SPLIT_PARALLEL" ) ||
			 ccse.action.equals( "SPLIT_SERIAL_PARALLEL" ) )
		{
			setDeinterlaceAlgorithm( ( ( Integer )ccse.object ).intValue() );
		}

		else if ( ccse.action.equals( "BINNING" ) )
		{
			String[] tokens = ccse.description.trim().split( "x" );

			if ( tokens.length == 2 )
			{
				fitsHeaderFrame.setXBinningField( Integer.parseInt( tokens[ 1 ] ) );
				fitsHeaderFrame.setYBinningField( Integer.parseInt( tokens[ 0 ] ) );
			}
		}
	}

	@Override
	public void setupChanged( SetupEvent se )
	{
		fitsHeaderFrame.removeXBinningField();
		fitsHeaderFrame.removeYBinningField();
	}

	// +--------------------------------------------------------------------+
	// |  loadPreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Loads the buttons and their scripts upon startup.                 |
	// +--------------------------------------------------------------------+
	@Override
	protected void loadPreferences()
	{
		boolean aBool    = false;
		String  prefKey  = null;
		String  aString  = null;
		int     dValue   = 0;

		if ( imgFileOptPanel != null )
		{
			prefKey = imgFileOptPanel.getName() + FILEOP_DIR_PREF;
			aString = MainApp.getPreferences().get( prefKey, "" );
			if ( !aString.isEmpty() ) directoryTxtfld.setText( aString );

			prefKey = imgFileOptPanel.getName() + FILEOP_FILE_PREF;
			aString = MainApp.getPreferences().get( prefKey, "" );
			if ( !aString.isEmpty() ) filenameTxtfld.setText( aString );

			prefKey = imgFileOptPanel.getName() + FILEOP_SAVE_PREF;
			aBool = MainApp.getPreferences().getBoolean( prefKey, true );
			saveToDiskChkbox.setSelected( aBool );

			prefKey = imgFileOptPanel.getName() + FILEOP_DISPLAY_PREF;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			displayChkbox.setSelected( aBool );

			prefKey = imgFileOptPanel.getName() + FILEOP_INCR_PREF;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			incrementFileChkbox.setSelected( aBool );

			prefKey = imgFileOptPanel.getName() + FILEOP_TYPE_PREF;
			aString = MainApp.getPreferences().get( prefKey, "" );
			if ( !aString.isEmpty() ) fileTypeLabel.setSelected( aString );
		}

		if ( optionsPanel != null )
		{
			prefKey = optionsPanel.getName() + EXPOP_MULTEXP_PREF;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			multExpChkbox.setSelected( aBool );

			prefKey = optionsPanel.getName() + EXPOP_MULTCNT_PREF;
			dValue = MainApp.getPreferences().getInt( prefKey, 0 );
			multExpNumFld.setValue( dValue );

			prefKey = optionsPanel.getName() + EXPOP_SHUTTER_PREF;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			openShutterChkbox.setSelected( aBool );

			prefKey = optionsPanel.getName() + EXPOP_SYNIMG_PREF;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			synthImageChkbox.setSelected( aBool );

			prefKey = optionsPanel.getName() + EXPOP_BEEP_PREF;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			beepChkbox.setSelected( aBool );

			prefKey = optionsPanel.getName() + EXPOP_DEINT_PREF;
			dValue = MainApp.getPreferences().getInt( prefKey, 0 );
			setDeinterlaceAlgorithm( dValue );

			prefKey = optionsPanel.getName() + EXPOP_DELAY_PREF;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			delayChkbox.setSelected( aBool );

			prefKey = optionsPanel.getName() + EXPOP_DELAY_TIME_PREF;
			dValue = MainApp.getPreferences().getInt( prefKey, 0 );
			delayNumFld.setValue( dValue );

			prefKey = optionsPanel.getName() + EXPOP_FILL_BUF_PREF;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			fillBufChkbox.setSelected( aBool );

			prefKey = optionsPanel.getName() + EXPOP_FILL_VAL_PREF;
			dValue = MainApp.getPreferences().getInt( prefKey, 0 );
			fillBufNumFld.setValue( dValue );
		}

		if ( expTimeNumFld != null )
		{
			prefKey = getName() + EXPTIME_PREF;
			aString = MainApp.getPreferences().get( prefKey, "" );
			if ( !aString.isEmpty() ) expTimeNumFld.setText( aString );
		}
	}

	// +--------------------------------------------------------------------+
	// |  savePreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Saves the setup button script and state upon exit                 |
	// +--------------------------------------------------------------------+
	@Override
	protected void savePreferences()
	{
		super.savePreferences();

		String prefKey = null;

		if ( imgFileOptPanel != null )
		{
			prefKey = imgFileOptPanel.getName() + FILEOP_DIR_PREF;
			MainApp.getPreferences().put( prefKey, directoryTxtfld.getText() );

			prefKey = imgFileOptPanel.getName() + FILEOP_FILE_PREF;
			MainApp.getPreferences().put( prefKey, filenameTxtfld.getText() );

			prefKey = imgFileOptPanel.getName() + FILEOP_SAVE_PREF;
			MainApp.getPreferences().putBoolean( prefKey, saveToDiskChkbox.isSelected() );

			prefKey = imgFileOptPanel.getName() + FILEOP_DISPLAY_PREF;
			MainApp.getPreferences().putBoolean( prefKey, displayChkbox.isSelected() );

			prefKey = imgFileOptPanel.getName() + FILEOP_INCR_PREF;
			MainApp.getPreferences().putBoolean( prefKey, incrementFileChkbox.isSelected() );

			prefKey = imgFileOptPanel.getName() + FILEOP_TYPE_PREF;
			MainApp.getPreferences().put( prefKey, fileTypeLabel.getRawText() );
		}

		if ( optionsPanel != null )
		{
			prefKey = optionsPanel.getName() + EXPOP_MULTEXP_PREF;
			MainApp.getPreferences().putBoolean( prefKey, multExpChkbox.isSelected() );

			prefKey = optionsPanel.getName() + EXPOP_MULTCNT_PREF;
			MainApp.getPreferences().putInt( prefKey, multExpNumFld.getInt() );

			prefKey = optionsPanel.getName() + EXPOP_SHUTTER_PREF;
			MainApp.getPreferences().putBoolean( prefKey, openShutterChkbox.isSelected() );

			prefKey = optionsPanel.getName() + EXPOP_SYNIMG_PREF;
			MainApp.getPreferences().putBoolean( prefKey, synthImageChkbox.isSelected() );

			prefKey = optionsPanel.getName() + EXPOP_BEEP_PREF;
			MainApp.getPreferences().putBoolean( prefKey, beepChkbox.isSelected() );

			prefKey = optionsPanel.getName() + EXPOP_DEINT_PREF;
			MainApp.getPreferences().putInt( prefKey, deinterlaceComboBox.getSelectedIndex() );

			prefKey = optionsPanel.getName() + EXPOP_DELAY_PREF;
			MainApp.getPreferences().putBoolean( prefKey, delayChkbox.isSelected() );

			prefKey = optionsPanel.getName() + EXPOP_DELAY_TIME_PREF;
			MainApp.getPreferences().putInt( prefKey, delayNumFld.getInt() );

			prefKey = optionsPanel.getName() + EXPOP_FILL_BUF_PREF;
			MainApp.getPreferences().putBoolean( prefKey, fillBufChkbox.isSelected() );

			prefKey = optionsPanel.getName() + EXPOP_FILL_VAL_PREF;
			MainApp.getPreferences().putInt( prefKey, fillBufNumFld.getInt() );
		}

		if ( expTimeNumFld != null )
		{
			prefKey = getName() + EXPTIME_PREF;
			MainApp.getPreferences().put( prefKey, expTimeNumFld.getText() );
		}
	}

	private class ComboBoxRenderer extends JLabel implements ListCellRenderer
	{
		private static final long serialVersionUID = 6182653477446617056L;
		private Random rand;

		public ComboBoxRenderer()
		{
			rand = new Random();
			setOpaque( true );
			setHorizontalAlignment( CENTER );
			setVerticalAlignment( CENTER );
		}

		public Component getListCellRendererComponent( JList list,
													   Object value,            // value to display
													   int index,               // cell index
													   boolean isSelected,      // is the cell selected
													   boolean cellHasFocus )   // the list and the cell have the focus
		{
			if ( value != null )
			{
				setIcon( ( ImageIcon )value );
				setToolTipText( vToolTips.get( ( ( ImageIcon )value ).getDescription() ) );
			}

			if ( isSelected )
			{
				setForeground( Color.WHITE );
				setBackground( new Color( rand.nextInt( 106 ) + 150,
										  rand.nextInt( 106 ) + 150,
										  rand.nextInt( 106 ) + 150
										 )
							 );
			}
			else
			{
				setForeground( list.getForeground() );
				setBackground( list.getBackground() );
			}

			return this;
		}
	}
}
