package owl.main;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.JOptionPane;

import owl.CCParams.CCScriptEvent;
import owl.CCParams.CCScriptListener;
import owl.PCIConfig.CfgSpFrame;
import owl.cameraAPI.CameraAPI;
import owl.logging.LogFrame;
import owl.main.debug.debugframe.DebugFrame;
import owl.main.debug.profiler.ProfilerFrame;
import owl.main.dialogs.ImgBufDumpDialog;
import owl.main.exposure.ExposeActionListener;
import owl.main.exposure.ExposeInfo;
import owl.main.exposure.ExposePanel;
import owl.main.exposure.ExposeRunnable;
import owl.main.exposure.ExposeRunnableCR;
import owl.main.exposure.ExposeEvent;
import owl.main.exposure.ExposeListener;
import owl.main.libs.JarLibFrame;
import owl.main.libs.JarBuilder;
import owl.main.libs.JarViewerFrame;
import owl.main.owltypes.OwlBoldButton;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlInputDialog;
import owl.main.remote.RemoteAPIFrame;
import owl.main.scripting.ScriptPanel;
import owl.main.setup.CCParamPanel;
import owl.main.setup.CameraPanel;
import owl.main.setup.SetupEvent;
import owl.main.setup.SetupFrame;
import owl.main.setup.SetupListener;
import owl.ptc.PTCFrame;



public class MainFrame extends OwlFrame implements ExposeListener, ExposeActionListener, CCScriptListener, SetupListener
{
	private static final long serialVersionUID = -6601299851667795179L;

	//--------------------------------------------------------------------------
    //   Public STATIC constants:
    //--------------------------------------------------------------------------
	public static final String DEFAULT_TITLE	=	"Owl " + MainApp.VERSION;

	//--------------------------------------------------------------------------
    //   Public constants:
    //--------------------------------------------------------------------------
	public final String EXPOSE_ACTION			=	"EXPOSE";
	public final String PAUSE_ACTION			=	"PAUSE";
	public final String RESUME_ACTION			=	"RESUME";
	public final String ABORT_ACTION			=	"ABORT";
	public final String ABORT_PAUSE_ACTION		=	"ABORT_PAUSE";
	public final String DEBUG_ACTION			=	"DEBUG";
	public final String OPTIONS_ACTION			=	"OPTIONS";
	public final String CONNECT_OPT_ACTION		=	"Select device ...";
	public final String IMGBUF_DUMP_OPT_ACTION	=	"Dump image buffer ...";
	public final String FILL_IMAGE_BUF_ACTION	=	"Fill image buffer ...";
	public final String SERVER_OPT_ACTION		=	"Connect to Server ...";
	public final String DEBUG_OPT_ACTION		=	"Debug ...";
	public final String PTC_OPT_ACTION			=	"PTC ...";
	public final String LOAD_EXT_LIB_ACTION		=	"Load External Library";
	public final String BUILD_EXT_LIB_ACTION	=	"Build External Library";
	public final String VIEW_EXT_LIB_ACTION		=	"View External Library";
	public final String CFG_SP_ACTION			=	"Config Space Header ...";
	public final String PROGRAM_PROFILER_ACTION	=	"System Profiler ...";
	public final String NATIVE_LIBS_ACTION		=	"NATIVE_LIBS_ACTION";
	public final String ABOUT_ACTION			=	"About ...";

	//--------------------------------------------------------------------------
    //   Public variables:
    //--------------------------------------------------------------------------
	public ExposePanel				exposePanel;
	public CameraPanel				cameraPanel;
	public ScriptPanel				scriptPanel;
	public CCParamPanel				ccParamPanel;
	public LogFrame					logFrame;
	public ExitListener				exitListener;
	public RemoteAPIFrame			remoteAPIFrame;
	public JarLibFrame				extLibFrame;

	//--------------------------------------------------------------------------
    //   Private variables:
    //--------------------------------------------------------------------------
	private JPanel					mainPanel;
	private JPanel					leftPanel;
	private JPanel					rightPanel;
	private OwlBoldButton			exposeButton;
	private OwlBoldButton			pauseButton;
	private ExposeRunnable			expRunnable;
    private DebugFrame				debugFrame;
    private PTCFrame				ptcFrame;
    private CfgSpFrame				cfgSpFrame;
    private ProfilerFrame			profilerFrame;
    private JarBuilder				jarBuilder;
    private JProgressBar			menuProgressBar;
    private JToolBar				toolbar;
    private int						numOfFrames;

    private JButton					optionsButton;
    private JPopupMenu				optionsButtonPopupMenu;
	private ImgBufDumpDialog		imgBufDumpDialog;
	private JarViewerFrame			jarViewFrame;


	//--------------------------------------------------------------------------
    //   Constructor:
    //--------------------------------------------------------------------------
	public MainFrame()
	{
		super( DEFAULT_TITLE, true );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		exitListener = new ExitListener();
		exitListener.add( this );

		addWindowListener( exitListener );	// For frame 'X' button
		super.exitButton.addActionListener( exitListener );

		SetupFrame.addSetupListener( this );

		mainPanel = new JPanel();
		mainPanel.setLayout( new BoxLayout( mainPanel, BoxLayout.X_AXIS ) );

		leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.Y_AXIS ) );

		rightPanel = new JPanel();
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.Y_AXIS ) );

		exposePanel = new ExposePanel();
		exitListener.add( exposePanel );
		exitListener.add( exposePanel.fitsHeaderFrame );

		scriptPanel = new ScriptPanel();
		exitListener.add( scriptPanel );

		cameraPanel = new CameraPanel();
		cameraPanel.setAlignmentX( java.awt.Component.LEFT_ALIGNMENT );
		exitListener.add( cameraPanel );
		exitListener.add( cameraPanel.setupFrame );

		ccParamPanel = new CCParamPanel();
		ccParamPanel.setAlignmentX( java.awt.Component.LEFT_ALIGNMENT );

		debugFrame = new DebugFrame();
		exitListener.add( debugFrame );

		remoteAPIFrame = new RemoteAPIFrame();

		leftPanel.add( exposePanel );
		leftPanel.add( scriptPanel );

		rightPanel.add( cameraPanel );
		rightPanel.add( ccParamPanel );

		mainPanel.add( leftPanel );
		mainPanel.add( rightPanel );

		menuProgressBar = new JProgressBar( 0, 100 );
		menuProgressBar.setStringPainted( true );
		menuProgressBar.setMaximumSize( new Dimension( 260, 15 ) );

		ExposeInfo.exposeListeners.add( this );
		toolbar = createToolBar();

		addComponent( toolbar,   super.TOOLBAR_INDEX );
		addComponent( mainPanel, super.CENTER_CONTAINER_INDEX );
		pack();

		ptcFrame = new PTCFrame();
		exitListener.add( ptcFrame );

		logFrame = new LogFrame();
		logFrame.setVisible( true );
		exitListener.add( logFrame );

	    cfgSpFrame       = null;
	    profilerFrame    = null;
	    jarBuilder       = null;

		imgBufDumpDialog = null;
		extLibFrame      = null;
		jarViewFrame     = null;

		loadPreferences();
	}

	// +--------------------------------------------------------------------+
	// |  setExposeAction                                                   |
	// +--------------------------------------------------------------------+
	// |  Called to set all the necessary button actions.  Also called      |
	// |  from expose runnable.                                             |
	// +--------------------------------------------------------------------+
	public void setExposeAction( byte action )
	{
		if ( action == ExposeActionListener.ABORT )
		{
			exposeButton.setText( ABORT_ACTION );
			exposeButton.setActionCommand( ABORT_ACTION );
			exposeButton.setColored();

			if ( exposePanel.getExposeTime() > 1.0 )
			{
				pauseButton.setVisible( true );
			}
		}

		else
		{
			exposeButton.setText( EXPOSE_ACTION );
			exposeButton.setActionCommand( EXPOSE_ACTION );
			exposeButton.setBlack();

			pauseButton.setText( PAUSE_ACTION );
			pauseButton.setActionCommand( PAUSE_ACTION );
			pauseButton.setVisible( false );
		}
	}

	// +--------------------------------------------------------------------+
	// |  actionPerformed ( ActionListener )                                |
	// +--------------------------------------------------------------------+
	// |  ActionListener override                                           |
	// +--------------------------------------------------------------------+
	@Override
	public void actionPerformed( ActionEvent ae )
	{
		//  Expose Menu Handler
		// +---------------------------------------------------+
		if ( ae.getActionCommand().equals( EXPOSE_ACTION ) )
		{
			try
			{
				if ( !CameraAPI.IsControllerConnected() )
				{
					throw new Exception( "Not connected to any controller!" );
				}

				if ( numOfFrames <= CameraAPI.CR_DISABLED )
				{
					expRunnable = new ExposeRunnable( exposePanel, this );
				}
				else
				{
					expRunnable = new ExposeRunnableCR( numOfFrames, exposePanel, this );
				}

				new Thread( expRunnable ).start();
			}
			catch ( Exception ex )
			{
				MainApp.error( ex );
			}
		}

		//  Expose Abort Handler
		// +---------------------------------------------------+
		else if ( ae.getActionCommand().equals( ABORT_ACTION ) )
		{
			expRunnable.stop();
			setExposeAction( ExposeActionListener.EXPOSE );
		}

		//  Pause Handler
		// +---------------------------------------------------+
		else if ( ae.getActionCommand().equals( PAUSE_ACTION ) )
		{
			expRunnable.pause();
			pauseButton.setColored( RESUME_ACTION );
		}

		//  Resume Handler
		// +---------------------------------------------------+
		else if ( ae.getActionCommand().equals( RESUME_ACTION ) )
		{
			expRunnable.resume();
			pauseButton.setColored( PAUSE_ACTION );
		}

		//  Hide Window Handler
		// +---------------------------------------------------+
		else if ( ae.getActionCommand().equals( HIDE_ACTION ) )
		{
			toolbar.add( menuProgressBar );

			if ( rightPanel.getComponentCount() <= 0 )
			{
				setSize( new Dimension( getWidth() + 260, getHeight() ) );
				validate();
			}

			super.actionPerformed( ae );
		}

		//  Show Window Handler
		// +---------------------------------------------------+
		else if ( ae.getActionCommand().equals( SHOW_ACTION ) )
		{
			toolbar.remove( menuProgressBar );
			super.actionPerformed( ae );
		}

		else if ( ae.getActionCommand().equals( "test" ) )
		{
//			owl.main.libs.JarViewerFrame jv = new owl.main.libs.JarViewerFrame();
//			jv.setVisible( true );

//			PTCPlotFrame plotFrame = new PTCPlotFrame( "Test" );
//			plotFrame.setVisible( true );

			owl.main.dialogs.PixelSearchDialog psd = new owl.main.dialogs.PixelSearchDialog( this );
			psd.setVisible( true );
		}

		//  Call super ActionListener
		// +---------------------------------------------------+
		else
		{
			super.actionPerformed( ae );
		}
	}

	// +--------------------------------------------------------------------+
	// |  readoutColorChanged ( ExposeListener )                            |
	// +--------------------------------------------------------------------+
	// |  Called before readout to change the readout progress bar color    |
	// +--------------------------------------------------------------------+
	public void readoutColorChanged( ExposeEvent event )
	{
		menuProgressBar.setForeground( event.color );
	}

	// +--------------------------------------------------------------------+
	// |  minMaxPixelsChanged ( ExposeListener )                            |
	// +--------------------------------------------------------------------+
	// |  Called at the start of a new exposure                             |
	// +--------------------------------------------------------------------+
	public void minMaxPixelsChanged( ExposeEvent event )
	{
		menuProgressBar.setMinimum( event.minPixelCount );
		menuProgressBar.setMaximum( event.maxPixelCount );
	}

	// +--------------------------------------------------------------------+
	// |  elapsedTimeChanged ( ExposeListener )                             |
	// +--------------------------------------------------------------------+
	// |  Called when the elapsed exposure time has changed during expose   |
	// +--------------------------------------------------------------------+
	public void elapsedTimeChanged( ExposeEvent event )
	{
		if ( event.elapsedTime <= 0 )
		{
			menuProgressBar.setString( null );
		}
		else
		{
			menuProgressBar.setString( "Time Remaining: " +
								Integer.toString( event.elapsedTime ) );
		}
	}

	// +--------------------------------------------------------------------+
	// |  frameCountChanged ( ExposeListener )                              |
	// +--------------------------------------------------------------------+
	// |  Called when the elapsed exposure time has changed during expose   |
	// +--------------------------------------------------------------------+
	public void frameCountChanged( ExposeEvent event )
	{
		if ( event.frameCount == 0 )
		{
			menuProgressBar.setValue( 0 );
			menuProgressBar.setString( "Frame: 0" );
		}
		else
		{
			menuProgressBar.setValue( event.frameCount );

			menuProgressBar.setString( "Frame: " +
								Integer.toString( event.frameCount ) );
		}
	}

	// +--------------------------------------------------------------------+
	// |  pixelCountChanged ( ExposeListener )                              |
	// +--------------------------------------------------------------------+
	// |  Called when the pixel count has changed during readout            |
	// +--------------------------------------------------------------------+
	public void pixelCountChanged( ExposeEvent event )
	{
		menuProgressBar.setValue( event.pixelCount );
	}

	// +--------------------------------------------------------------------+
	// |  exposureComplete ( ExposeListener )                               |
	// +--------------------------------------------------------------------+
	// |  Called when the exposure sequence has finished                    |
	// +--------------------------------------------------------------------+
	public void exposureComplete( ExposeEvent event )
	{
	}

	// +--------------------------------------------------------------------+
	// |  setupChanged ( SetupListener )                                    |
	// +--------------------------------------------------------------------+
	// |  Called when a new setup is applied. This method un-does anything  |
	// |  changed by CC Parameter scripts ( i.e. reverses CCScriptChanged   |
	// |  method call ).                                                    |
	// +--------------------------------------------------------------------+
	@Override
	public void setupChanged( SetupEvent se )
	{
		numOfFrames = CameraAPI.CR_DISABLED;
		exposePanel.setElapsedTime( 0 );
		exposePanel.setPixelCount( 0 );
	}

	// +--------------------------------------------------------------------+
	// |  CCScriptChanged ( CCScriptListener )                              |
	// +--------------------------------------------------------------------+
	// |  Called when a CC Parameter script has executed ( changed )        |
	// +--------------------------------------------------------------------+
	@Override
	public void CCScriptChanged( CCScriptEvent ccse )
	{
		try
		{
			if ( ccse.action.equals( "CONT_RD" ) )
			{
				numOfFrames = ( ( Integer )ccse.object ).intValue();

				MainApp.info( "Continuous readout " +
							  ( numOfFrames > CameraAPI.CR_DISABLED ?
							  "enabled!" : "disabled" ) );
			}
		}
		catch ( Exception e ) { MainApp.error( e.getMessage() ); }
	}

	// +--------------------------------------------------------------------+
	// |  createToolBar                                                     |
	// +--------------------------------------------------------------------+
	// |  Creates the toolbar for the main window                           |
	// +--------------------------------------------------------------------+
	protected JToolBar createToolBar()
	{
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable( false );

		exposeButton =
				super.createBoldToolbarButton( EXPOSE_ACTION, Color.red );
		toolbar.add( exposeButton );

		pauseButton =
				super.createBoldToolbarButton( PAUSE_ACTION,
											   new Color( 0, 80, 207 ) );
		pauseButton.setVisible( false );
		pauseButton.setColored();
		toolbar.add( pauseButton );

		optionsButton = super.createNewToolbarButton( OPTIONS_ACTION );
		optionsButton.addActionListener( new OptionsMenuListener() );
		toolbar.add( optionsButton );

		optionsButtonPopupMenu = createOptionsPopupMenu();

		optionsButtonPopupMenu.setCursor(
					Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

		super.appendToolbar( toolbar,
							 ( OwlFrame.HIDE |
							   OwlFrame.EXIT |
							   OwlFrame.CLEAR_ON_HIDE ) );

		return toolbar;
	}

	// +--------------------------------------------------------------------+
	// |  loadPreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Loads the previously saved component fields and states on startup |
	// +--------------------------------------------------------------------+
	@Override
	protected void loadPreferences()
	{
		super.loadPreferences();
	}

	// +--------------------------------------------------------------------+
	// |  savePreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Saves the component fields and states upon exit                   |
	// +--------------------------------------------------------------------+
	@Override
	protected void savePreferences()
	{
		super.savePreferences();
	}

	// +--------------------------------------------------------------------+
	// |  createOptionsPopupMenu                                            |
	// +--------------------------------------------------------------------+
	// |  Create the OPTIONS popup menu                                     |
	// +--------------------------------------------------------------------+
	private JPopupMenu createOptionsPopupMenu()
	{
		JPopupMenu popupMenu = new JPopupMenu();
		OptionsMenuListener optionsListener = new OptionsMenuListener();

		JMenuItem debugMenuItem =
			new JMenuItem( DEBUG_OPT_ACTION, new ImageIcon( MainApp.getBitmapPath() + "debug.gif" ) );
		debugMenuItem.addActionListener( optionsListener );
		popupMenu.add( debugMenuItem );

		popupMenu.add( new owl.main.setup.DCParamMenu() );

		JMenuItem ptcMenuItem = 
			new JMenuItem( PTC_OPT_ACTION, new ImageIcon( MainApp.getBitmapPath() + "PTC3.png" ) );
		ptcMenuItem.addActionListener( optionsListener );
		popupMenu.add( ptcMenuItem );

		popupMenu.addSeparator();

		JMenuItem connectMenuItem =
			new JMenuItem( CONNECT_OPT_ACTION, new ImageIcon( MainApp.getBitmapPath() + "TinyPCI.gif" ) );
		connectMenuItem.addActionListener( optionsListener );
		popupMenu.add( connectMenuItem );

		JMenuItem imgBufDumpMenuItem =
			new JMenuItem( IMGBUF_DUMP_OPT_ACTION, new ImageIcon( MainApp.getBitmapPath() + "Image.gif" ) );
		imgBufDumpMenuItem.addActionListener( optionsListener );
		popupMenu.add( imgBufDumpMenuItem );

		JMenuItem fillImgBufItem =
			new JMenuItem( FILL_IMAGE_BUF_ACTION, new ImageIcon( MainApp.getBitmapPath() + "Fill.gif" ) );
		fillImgBufItem.addActionListener( optionsListener );
		popupMenu.add( fillImgBufItem );

		popupMenu.addSeparator();

		JMenuItem testMenu = new JMenuItem( "test" );
		testMenu.setActionCommand( "test" );
		testMenu.addActionListener( this );
		popupMenu.add( testMenu );

		popupMenu.addSeparator();

		JMenuItem serverConnItem =
			new JMenuItem( SERVER_OPT_ACTION, new ImageIcon( MainApp.getBitmapPath() + "ServConn.gif" ) );
		serverConnItem.setActionCommand( SERVER_OPT_ACTION );
		serverConnItem.addActionListener( optionsListener );
		popupMenu.add( serverConnItem );

		popupMenu.addSeparator();

		JMenu libraryMenu = new JMenu( "External Libraries ...." );
		libraryMenu.setIcon( new ImageIcon( MainApp.getBitmapPath() + "loadlibs.gif" ) );
		libraryMenu.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		popupMenu.add( libraryMenu );

		JMenuItem loadExternalJarItem =
				new JMenuItem( LOAD_EXT_LIB_ACTION, new ImageIcon( MainApp.getBitmapPath() + "loadJar.gif" ) );
		loadExternalJarItem.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		loadExternalJarItem.addActionListener( optionsListener );
		libraryMenu.add( loadExternalJarItem );

		JMenuItem buildJarItem =
				new JMenuItem( BUILD_EXT_LIB_ACTION, new ImageIcon( MainApp.getBitmapPath() + "BuildJar.gif" ) );
		buildJarItem.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		buildJarItem.addActionListener( optionsListener );
		libraryMenu.add( buildJarItem );

		JMenuItem viewExternalLibItem =
				new JMenuItem( VIEW_EXT_LIB_ACTION, new ImageIcon( MainApp.getBitmapPath() + "viewJar.gif" ) );
		viewExternalLibItem.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		viewExternalLibItem.addActionListener( optionsListener );
		libraryMenu.add( viewExternalLibItem );

		popupMenu.addSeparator();

		JMenuItem cfgSpHeaderMenu =
			new JMenuItem( CFG_SP_ACTION, new ImageIcon( MainApp.getBitmapPath() + "CfpSpPCI.gif" ) );
		cfgSpHeaderMenu.setActionCommand( CFG_SP_ACTION );
		cfgSpHeaderMenu.addActionListener( optionsListener );
		popupMenu.add( cfgSpHeaderMenu );

		JMenuItem programInfoMenu =
			new JMenuItem( PROGRAM_PROFILER_ACTION, new ImageIcon( MainApp.getBitmapPath() + "profiler.gif" ) );
		programInfoMenu.setActionCommand( PROGRAM_PROFILER_ACTION );
		programInfoMenu.addActionListener( optionsListener );
		popupMenu.add( programInfoMenu );

		popupMenu.addSeparator();

		JMenuItem aboutMenu =
			new JMenuItem( ABOUT_ACTION, new ImageIcon( MainApp.getBitmapPath() + "TinyOwl.gif" ) );
		aboutMenu.setActionCommand( ABOUT_ACTION );
		aboutMenu.addActionListener( optionsListener );
		popupMenu.add( aboutMenu );

		return popupMenu;
	}

	// +--------------------------------------------------------------------+
	// |  OptionsMenuListener Class                                         |
	// +--------------------------------------------------------------------+
	// |  Create the OPTIONS popup menu listener                            |
	// +--------------------------------------------------------------------+
	private class OptionsMenuListener implements ActionListener
	{
		// +--------------------------------------------------------------------+
		// |  Constructor                                                       |
		// +--------------------------------------------------------------------+
		public OptionsMenuListener()
		{
		}

		// +--------------------------------------------------------------------+
		// |  actionPerformed                                                   |
		// +--------------------------------------------------------------------+
		public void actionPerformed( ActionEvent ae )
		{
			//  Show the options popup menu
			// +--------------------------------------------------+
			if ( ae.getActionCommand().equals( OPTIONS_ACTION ) )
			{
				optionsButtonPopupMenu.show( optionsButton,
											 optionsButton.getX() - optionsButton.getWidth(),
											 optionsButton.getY() );
			}

			//  Debug option handler
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( DEBUG_OPT_ACTION ) )
			{
				debugFrame.setVisible( true );
			}

			//  PTC option handler
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( PTC_OPT_ACTION ) )
			{
				ptcFrame.setVisible( true );
			}

			//  Driver connect option handler
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( CONNECT_OPT_ACTION ) )
			{
				MainApp.connectToDevice( true );
			}

			//  Server connect option handler
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( SERVER_OPT_ACTION ) )
			{
				remoteAPIFrame.setVisible( true );
			}

			//  Dump image buffer option handler
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( IMGBUF_DUMP_OPT_ACTION ) )
			{
				if ( imgBufDumpDialog == null )
				{
					imgBufDumpDialog = new ImgBufDumpDialog( null );
				}
	
				if ( !imgBufDumpDialog.isVisible() )
				{
					imgBufDumpDialog.setVisible( true );
				}
			}

			//  Clear image buffer option handler
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( FILL_IMAGE_BUF_ACTION ) )
			{
				OwlInputDialog dialog = new OwlInputDialog();
	
				int status =
					dialog.showDialog( null,
									   "Fill Image Buffer",
									   new String[] { "Value to fill buffer" },
									   new String[] { "0" } );

				if ( status == OwlInputDialog.OK )
				{
					int[] values = dialog.getInputValuesAsInts();
	
					if ( values != null && values.length > 0 )
					{
						try
						{
							MainApp.infoStart( "Filling image buffer" );
							CameraAPI.FillImageBuffer( values[ 0 ] );
							MainApp.infoEnd();
						}
						catch ( Exception e )
						{
							MainApp.infoFail();
							MainApp.error( e.getMessage() );
						}
					}
				}
			}

			//  Show PCI(e) config space header option handler
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( CFG_SP_ACTION ) )
			{
				if  ( cfgSpFrame == null )
				{
					cfgSpFrame = new CfgSpFrame();
				}
	
				cfgSpFrame.setVisible( true );
			}

			//  Show program info option handler
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( PROGRAM_PROFILER_ACTION ) )
			{
				if  ( profilerFrame == null )
				{
					profilerFrame = new ProfilerFrame();
				}

				profilerFrame.setVisible( true );
			}

			//  Load external Jar file
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( LOAD_EXT_LIB_ACTION ) )
			{
				if ( extLibFrame == null )
				{
					extLibFrame = new JarLibFrame();
				}

				extLibFrame.setVisible( true );
			}

			//  Build external Jar library
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( BUILD_EXT_LIB_ACTION ) )
			{
				if ( jarBuilder == null )
				{
					jarBuilder = new JarBuilder();
				}

				jarBuilder.setVisible( true );
			}

			//  View external Jar library
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( VIEW_EXT_LIB_ACTION ) )
			{
				if ( jarViewFrame == null )
				{
					jarViewFrame = new JarViewerFrame();
				}

				jarViewFrame.setVisible( true );
			}

			//  About option handler
			// +--------------------------------------------------+
			else if ( ae.getActionCommand().equals( ABOUT_ACTION ) )
			{
				String msg = "<html><b><font size=4>Owl Image Aquisition Software</font></b></html>\n\n" +
							 "<html><font size=3 color=#800517>Scott Streit - streit@astro-cam.com</font></html>\n" +
							 "<html><font size=3 color=#800517>Astronomical Research Cameras, Inc.</font></html>\n\n";

				JOptionPane.showMessageDialog( null,
											   msg,
											   "About Owl",
											   JOptionPane.INFORMATION_MESSAGE,
											   new ImageIcon( MainApp.getBitmapPath() +
													   		  "Owl.gif" ) );
			}
		}
	}

	// +--------------------------------------------------------------------+
	// |  ExitListener Class                                                |
	// +--------------------------------------------------------------------+
	// |  Called when the program exits                                     |
	// +--------------------------------------------------------------------+
	public class ExitListener extends WindowAdapter implements ActionListener
	{
		ArrayList<ActionListener> listeners;

		public ExitListener()
		{
			listeners  = new ArrayList<ActionListener>();
		}

		public void add( ActionListener aListener )
		{
			listeners.add( aListener );
		}

		@Override
		public void windowClosing( WindowEvent e )
		{
			callListeners();
		}

		public void actionPerformed( ActionEvent e )
		{
			callListeners();
			MainApp.onExit();
		}

		private void callListeners()
		{
			for ( int i=0; i<listeners.size(); i++ )
			{
				ActionEvent event = new ActionEvent( exitButton,
													 ActionEvent.ACTION_PERFORMED,
													 EXIT_ACTION );

				listeners.get( i ).actionPerformed( event );
			}
		}
	}
}
