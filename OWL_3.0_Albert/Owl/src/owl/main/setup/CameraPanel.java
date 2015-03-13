package owl.main.setup;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import owl.CCParams.CCScriptEvent;
import owl.CCParams.CCScriptListener;
import owl.cameraAPI.CameraAPI;
import owl.gui.scriptable.ScriptableTextButton;
import owl.gui.utils.FunctorRunnable;
import owl.gui.utils.OwlButtonFactory;
import owl.main.MainApp;
import owl.main.device.DeviceConnect;
import owl.main.device.DeviceEvent;
import owl.main.device.DeviceListener;
import owl.main.owltypes.OwlPanel;


public class CameraPanel extends OwlPanel
implements ActionListener, DeviceListener, SetupListener, CCScriptListener
{
	private static final long serialVersionUID = 1L;

	//--------------------------------------------------------------------------
    //   Constants:
    //--------------------------------------------------------------------------
	private final ImageIcon OPEN_SHUTTER_ICON	=	new ImageIcon( MainApp.getBitmapPath() + "OpenShutter.gif" );
	private final ImageIcon CLOSE_SHUTTER_ICON	=	new ImageIcon( MainApp.getBitmapPath() + "CloseShutter.gif" );
	private final ImageIcon CLEAR_ARRAY_ICON	=	new ImageIcon( MainApp.getBitmapPath() + "ClearArray.gif" );
	private final ImageIcon IDLE_ARRAY_ICON		=	new ImageIcon( MainApp.getBitmapPath() + "Idle.gif" );
	private final ImageIcon STOP_ARRAY_ICON		=	new ImageIcon( MainApp.getBitmapPath() + "StopIdle.gif" );
	private final ImageIcon POWER_ON_ICON		=	new ImageIcon( MainApp.getBitmapPath() + "PowerOn.gif" );
	private final ImageIcon POWER_OFF_ICON		=	new ImageIcon( MainApp.getBitmapPath() + "PowerOff.gif" );
	private final ImageIcon RESET_CTLR_ICON		=	new ImageIcon( MainApp.getBitmapPath() + "ResetCtlr.gif" );
	private final ImageIcon RESET_PCI_ICON		=	new ImageIcon( MainApp.getBitmapPath() + "ResetPCI.gif" );
	private final ImageIcon TEMPERATURE_ICON	=	new ImageIcon( MainApp.getBitmapPath() + "Temperature2.gif" );
	private final ImageIcon TEMPTURE_PLOT_ICON	=	new ImageIcon( MainApp.getBitmapPath() + "TemperaturePlot.gif" );

	private final String OPEN_SHUTTER_ACTION	=	"Open Camera Shutter";
	private final String SHUT_SHUTTER_ACTION	=	"Close Camera Shutter";
	private final String CLEAR_ARRAY_ACTION		=	"Clear Camera Array";
	private final String IDLE_ARRAY_ACTION		=	"Idle Camera Array";
	private final String STOP_ARRAY_ACTION		=	"Stop Camera Idle";
	private final String POWER_ON_ACTION		=	"Power Controller On";
	private final String POWER_OFF_ACTION		=	"Power Controller Off";
	private final String RESET_CTLR_ACTION		=	"Reset Controller";
	private final String RESET_PCI_ACTION		=	"Reset PCI(e)";
	private final String TEMPERATURE_ACTION		=	"Read Camera Temperature";
	private final String TEMPTURE_PLOT_ACTION	=	"Show Temperature Plot";
	private final String SETUP_STD_ACTION		=	"Perform Standard Setup Action";

	private final String SETUP_SCRIPT_PREF		=	"Script";
	private final String SETUP_USE_SCRIPT_PREF	=	"UseScript";

    //--------------------------------------------------------------------------
    //   Public Variables:
    //--------------------------------------------------------------------------
	public SetupFrame setupFrame;

	//--------------------------------------------------------------------------
    //   Private Variables:
    //--------------------------------------------------------------------------
	private ScriptableTextButton	setupButton;
	private TemperaturePlotFrame	tempturePlotFrame;
	private JButton     			shutterButton;
	private JButton     			idleArrayButton;
	private JButton     			powerButton;
    private Thread					quickActionThread;
    private JLabel					rowsValueLabel;
    private JLabel					colsValueLabel;
    private JLabel					tmptureValueLabel;
    private JLabel					tmptureDNLabel;


	//--------------------------------------------------------------------------
    //   Constructor:
    //--------------------------------------------------------------------------
	public CameraPanel()
	{
		super( "Controller" );

		setupFrame = new SetupFrame();
		SetupFrame.addSetupListener( this );
		DeviceConnect.addDeviceListener( this );

		tempturePlotFrame = null;

		try
		{
			setupButton = new ScriptableTextButton( "Setup", this, SETUP_STD_ACTION );
			setupButton.setName( "CameraPanelButton" );
			setupButton.setPreferredSize( new Dimension( 65, 20 ) );
			setupButton.setAlignmentY( java.awt.Component.TOP_ALIGNMENT );

			Class<?>[] paramClasses = { setupFrame.getClass() };
			Method callbackMethod = this.getClass().getDeclaredMethod( "setupScriptCallback", paramClasses );
			Object[] callbackObjects = { setupFrame };

			setupButton.setScriptCallbackMethod( callbackMethod, callbackObjects );
		}
		catch ( Exception e )
		{
			MainApp.error( e.toString() );
		}

		JPanel setupPanel = new JPanel();
		setupPanel.setLayout( new BoxLayout( setupPanel, BoxLayout.X_AXIS ) );
		setupPanel.setAlignmentX( java.awt.Component.LEFT_ALIGNMENT );
		setupPanel.setAlignmentY( java.awt.Component.TOP_ALIGNMENT );
		setupPanel.add( setupButton );

		JLabel rowsLabel = new JLabel( "Rows: " );
		rowsValueLabel   = new JLabel( "0" );

		JLabel colsLabel = new JLabel( "Cols: " );
		colsValueLabel   = new JLabel( "0" );

		JPanel dimPanel = new JPanel();
		dimPanel.setLayout( new FlowLayout( FlowLayout.LEFT, 10, 0 ) );
		dimPanel.setAlignmentY( java.awt.Component.TOP_ALIGNMENT );
		dimPanel.add( rowsLabel );
		dimPanel.add( rowsValueLabel );
		dimPanel.add( colsLabel );
		dimPanel.add( colsValueLabel );

		JLabel tmptureLabel = new JLabel( "Temp ( C ): " );
		tmptureValueLabel = new JLabel( "000.0" );
		tmptureDNLabel = new JLabel( "[0000 DN]" );

		JPanel tmpPanel = new JPanel();
		tmpPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
		tmpPanel.setAlignmentY( java.awt.Component.TOP_ALIGNMENT );
		tmpPanel.add( Box.createHorizontalStrut( 1 ) );
		tmpPanel.add( tmptureLabel );
		tmpPanel.add( tmptureValueLabel );
		tmpPanel.add( tmptureDNLabel );

		JPanel dimTmpPanel = new JPanel();
		dimTmpPanel.setLayout( new BoxLayout( dimTmpPanel, BoxLayout.Y_AXIS ) );
		dimTmpPanel.setAlignmentY( java.awt.Component.TOP_ALIGNMENT );
		dimTmpPanel.add( dimPanel );
		dimTmpPanel.add( tmpPanel );

		JPanel actPanel = createActionPanel();
		actPanel.setAlignmentX( java.awt.Component.LEFT_ALIGNMENT );
		setupPanel.add( dimTmpPanel );

		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		setAlignmentY( java.awt.Component.TOP_ALIGNMENT );
		setAlignmentX( java.awt.Component.LEFT_ALIGNMENT );
		add( setupPanel );
		add( actPanel );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );
		add( Box.createVerticalGlue() );

		//  Needed to test if the runnable is still alive
		// +----------------------------------------------
		quickActionThread = null;

		loadPreferences();
	}

	public static void setupScriptCallback( SetupFrame param )
	{
		if ( CameraAPI.IsControllerConnected() )
		{
			SetupFrame.callSetupListeners( param );
		}
		else
		{
			MainApp.error( "No controller connected to system!" );
		}
	}

	public int getRowLabelValue()
	{
		int val = 0;

		try {
			val = Integer.parseInt( rowsValueLabel.getText() );
		}
		catch ( Exception e ) { MainApp.error( e.getMessage() ); }

		return val;
	}

	public int getColLabelValue()
	{
		int val = 0;

		try {
			val = Integer.parseInt( colsValueLabel.getText() );
		}
		catch ( Exception e ) { MainApp.error( e.getMessage() ); }

		return val;
	}

	private JPanel createActionPanel()
	{
		JPanel topPanel = new JPanel();
		topPanel.setLayout( new FlowLayout( FlowLayout.LEFT, 2, 0 ) );
		topPanel.setBorder( new TitledBorder( "Quick Actions" ) );

		Dimension dim	= new Dimension( 35, 35 );
		Dimension dim2	= new Dimension( 20, 35 );

		shutterButton   = OwlButtonFactory.create( OPEN_SHUTTER_ICON, OPEN_SHUTTER_ACTION, dim, this );
		idleArrayButton = OwlButtonFactory.create( IDLE_ARRAY_ICON, IDLE_ARRAY_ACTION,     dim, this );
		powerButton     = OwlButtonFactory.create( POWER_ON_ICON, POWER_ON_ACTION,         dim, this );

		JButton clearArrayButton  = OwlButtonFactory.create( CLEAR_ARRAY_ICON,   CLEAR_ARRAY_ACTION,   dim,  this );
		JButton resetCtlrButton   = OwlButtonFactory.create( RESET_CTLR_ICON,    RESET_CTLR_ACTION,    dim,  this );
		JButton resetPCIButton    = OwlButtonFactory.create( RESET_PCI_ICON,     RESET_PCI_ACTION,     dim,  this );
		JButton tmptureButton     = OwlButtonFactory.create( TEMPERATURE_ICON,   TEMPERATURE_ACTION,   dim2, this );
		JButton tmpturePlotButton = OwlButtonFactory.create( TEMPTURE_PLOT_ICON, TEMPTURE_PLOT_ACTION, dim2, this );

		topPanel.add( tmptureButton );
		topPanel.add( tmpturePlotButton );
		topPanel.add( shutterButton );
		topPanel.add( clearArrayButton );
		topPanel.add( idleArrayButton );
		topPanel.add( powerButton );
		topPanel.add( resetCtlrButton );
		topPanel.add( resetPCIButton );

		return topPanel;
	}

	@Override
	public void actionPerformed( ActionEvent aEvent )
	{
		String action = aEvent.getActionCommand();

		if ( action.equals( SETUP_STD_ACTION ) )
		{
			setupFrame.setVisible( true );
		}

		else if ( action.equals( OPEN_SHUTTER_ACTION ) )
		{
			if ( quickActionThread == null ||
				 quickActionThread.getState() == Thread.State.TERMINATED )
			{
				Integer[] args = { CameraAPI.TIM_ID, CameraAPI.OSH };
				quickActionThread = new Thread( new FunctorRunnable( 
						/* Method class       */ CameraAPI.class,
						/* Method             */ "Command",
						/* Callback class obj */ this,
						/* Callback method    */ "shutterCallback",
						/* Method args Obj[]  */ args,
						/* Display msg box    */ "Open shutter",
						/* Optional msg icon  */ OPEN_SHUTTER_ICON,
						/* Expected reply     */ new Integer( CameraAPI.DON ) ) );
				quickActionThread.start();
			}
		}

		else if ( action.equals( SHUT_SHUTTER_ACTION ) )
		{
			if ( quickActionThread == null ||
				 quickActionThread.getState() == Thread.State.TERMINATED )
			{
				Integer[] args = { CameraAPI.TIM_ID, CameraAPI.CSH };
				quickActionThread = new Thread( new FunctorRunnable( 
						/* Method class       */ CameraAPI.class,
						/* Method             */ "Command",
						/* Callback class obj */ this,
						/* Callback method    */ "shutterCallback",
						/* Method args Obj[]  */ args,
						/* Display msg box    */ "Close shutter",
						/* Optional msg icon  */ CLOSE_SHUTTER_ICON,
						/* Expected reply     */ new Integer( CameraAPI.DON ) ) );
				quickActionThread.start();
			}
		}

		else if ( action.equals( CLEAR_ARRAY_ACTION ) )
		{
			if ( quickActionThread == null ||
				 quickActionThread.getState() == Thread.State.TERMINATED )
			{
				Integer[] args = { CameraAPI.TIM_ID, CameraAPI.CLR };
				quickActionThread = new Thread( new FunctorRunnable( 
						/* Method class       */ CameraAPI.class,
						/* Method             */ "Command",
						/* Callback class obj */ null,
						/* Callback method    */ null,
						/* Method args Obj[]  */ args,
						/* Display msg box    */ "Clear camera array",
						/* Optional msg icon  */ CLEAR_ARRAY_ICON,
						/* Expected reply     */ new Integer( CameraAPI.DON ) ) );
				quickActionThread.start();
			}
		}

		else if ( action.equals( IDLE_ARRAY_ACTION ) )
		{
			if ( quickActionThread == null ||
				 quickActionThread.getState() == Thread.State.TERMINATED )
			{
				Integer[] args = { CameraAPI.TIM_ID, CameraAPI.IDL };
				quickActionThread = new Thread( new FunctorRunnable( 
						/* Method class       */ CameraAPI.class,
						/* Method             */ "Command",
						/* Callback class obj */ this,
						/* Callback method    */ "idleArrayCallback",
						/* Method args Obj[]  */ args,
						/* Display msg box    */ "Idle camera array",
						/* Optional msg icon  */ IDLE_ARRAY_ICON,
						/* Expected reply     */ new Integer( CameraAPI.DON ) ) );
				quickActionThread.start();
			}
		}

		else if ( action.equals( STOP_ARRAY_ACTION ) )
		{
			if ( quickActionThread == null ||
				 quickActionThread.getState() == Thread.State.TERMINATED )
			{
				Integer[] args = { CameraAPI.TIM_ID, CameraAPI.STP };
				quickActionThread = new Thread( new FunctorRunnable( 
						/* Method class       */ CameraAPI.class,
						/* Method             */ "Command",
						/* Callback class obj */ this,
						/* Callback method    */ "idleArrayCallback",
						/* Method args Obj[]  */ args,
						/* Display msg box    */ "Stop idle camera array",
						/* Optional msg icon  */ STOP_ARRAY_ICON,
						/* Expected reply     */ new Integer( CameraAPI.DON ) ) );
				quickActionThread.start();
			}
		}

		else if ( action.equals( POWER_ON_ACTION ) )
		{
			if ( quickActionThread == null ||
				 quickActionThread.getState() == Thread.State.TERMINATED )
			{
				Integer[] args = { CameraAPI.TIM_ID, CameraAPI.PON };
				quickActionThread = new Thread( new FunctorRunnable( 
						/* Method class       */ CameraAPI.class,
						/* Method             */ "Command",
						/* Callback class obj */ this,
						/* Callback method    */ "powerCallback",
						/* Method args Obj[]  */ args,
						/* Display msg box    */ "Power on controller",
						/* Optional msg icon  */ POWER_ON_ICON,
						/* Expected reply     */ new Integer( CameraAPI.DON ) ) );
				quickActionThread.start();
			}
		}

		else if ( action.equals( POWER_OFF_ACTION ) )
		{
			if ( quickActionThread == null ||
				 quickActionThread.getState() == Thread.State.TERMINATED )
			{
				Integer[] args = { CameraAPI.TIM_ID, CameraAPI.POF };
				quickActionThread = new Thread( new FunctorRunnable( 
						/* Method class       */ CameraAPI.class,
						/* Method             */ "Command",
						/* Callback class obj */ this,
						/* Callback method    */ "powerCallback",
						/* Method args Obj[]  */ args,
						/* Display msg box    */ "Power off controller",
						/* Optional msg icon  */ POWER_OFF_ICON,
						/* Expected reply     */ new Integer( CameraAPI.DON ) ) );
				quickActionThread.start();
			}
		}

		else if ( action.equals( RESET_CTLR_ACTION ) )
		{
			if ( quickActionThread == null ||
				 quickActionThread.getState() == Thread.State.TERMINATED )
			{
				quickActionThread = new Thread( new FunctorRunnable( 
						/* Method class       */ CameraAPI.class,
						/* Method             */ "ResetController",
						/* Callback class obj */ null,
						/* Callback method    */ null,
						/* Display msg box    */ "Resetting controller",
						/* Optional msg icon  */ RESET_CTLR_ICON ) );
				quickActionThread.start();
			}
		}

		else if ( action.equals( RESET_PCI_ACTION ) )
		{
			quickActionThread = new Thread( new FunctorRunnable( 
					/* Method class       */ CameraAPI.class,
					/* Method             */ "ResetDevice",
					/* Callback class obj */ null,
					/* Callback method    */ null,
					/* Display msg box    */ "Resetting PCI(e)",
					/* Optional msg icon  */ RESET_PCI_ICON ) );
			quickActionThread.start();
		}

		else if ( action.equals( TEMPERATURE_ACTION ) )
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
					if ( quickActionThread == null ||
							 quickActionThread.getState() == Thread.State.TERMINATED )
					{
						quickActionThread = new Thread( new FunctorRunnable( 
										/* Method class       */ CameraAPI.class,
										/* Method             */ "GetArrayTemperature",
										/* Callback class obj */ this,
										/* Callback method    */ "temperatureCallback",
										/* Display msg box    */ "Reading Camera Temperature",
										/* Optional msg icon  */ TEMPERATURE_ICON ) );
						quickActionThread.start();
					}
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

		else if ( action.equals( TEMPTURE_PLOT_ACTION ) )
		{
			if ( tempturePlotFrame == null )
			{
				tempturePlotFrame =
					new TemperaturePlotFrame( "Array Temperature", true );
			}

			tempturePlotFrame.setVisible( true );
		}

		else
		{
			super.actionPerformed( aEvent );
		}
	}

	// +----------------------------------------------------------------+
	// |  deviceChanged                                                 |
	// +----------------------------------------------------------------+
	// |  DeviceListener callback                                       |
	// +----------------------------------------------------------------+
	@Override
	public void deviceChanged( DeviceEvent event )
	{
		rowsValueLabel.setText( "0" );
		colsValueLabel.setText( "0" );
	}

	// +----------------------------------------------------------------+
	// |  setupChanged                                                  |
	// +----------------------------------------------------------------+
	// |  SetupListener callback                                        |
	// +----------------------------------------------------------------+
	@Override
	public void setupChanged( SetupEvent event )
	{
		try
		{
			// Read and update the current image size display
			//
			// NOTE: join() is used because this thread will
			// contend with the RCC command that is issued by
			// the CCParamPanel and can result in out-of-order
			// or missed replies that cause incorrect row and
			// column values.
			// ------------------------------------------------
			if ( event.setupOk )
			{
				Thread thread = new Thread( new FunctorRunnable( 
						/* Method class       */ CameraAPI.class,
						/* Method             */ "GetImageSize",
						/* Callback class obj */ this,
						/* Callback method    */ "updateImageSizeCallback",
						/* Logger info msg    */ ( String )null ) );
				thread.start();
				thread.join( 1500 );
			}

			// Adjust power on/off and idle array buttons
			// ------------------------------------------------
			if ( event.powerOn )
			{
				powerButton.setActionCommand( POWER_OFF_ACTION );
				powerButton.setToolTipText( POWER_OFF_ACTION );
				powerButton.setIcon( POWER_OFF_ICON );

				idleArrayButton.setIcon( STOP_ARRAY_ICON );
				idleArrayButton.setToolTipText( STOP_ARRAY_ACTION );
				idleArrayButton.setActionCommand( STOP_ARRAY_ACTION );
			}
			else
			{
				powerButton.setActionCommand( POWER_ON_ACTION );
				powerButton.setToolTipText( POWER_ON_ACTION );
				powerButton.setIcon( POWER_ON_ICON );

				idleArrayButton.setIcon( IDLE_ARRAY_ICON );
				idleArrayButton.setToolTipText( IDLE_ARRAY_ACTION );
				idleArrayButton.setActionCommand( IDLE_ARRAY_ACTION );
			}
		}
		catch ( Exception e )
		{
			MainApp.error( "CameraPanel.setupChanged error: " +
							e.toString() );
		}
	}

	// +----------------------------------------------------------------+
	// |  CCScriptChanged                                               |
	// +----------------------------------------------------------------+
	// |  CCScriptListener callback                                     |
	// +----------------------------------------------------------------+
	@Override
	public void CCScriptChanged( CCScriptEvent event )
	{
		//  Check the current image dimension on the controller
		// +------------------------------------------------------+
		try
		{
			int[] imgSize = CameraAPI.GetImageSize();

			rowsValueLabel.setText( Integer.toString( imgSize[ 0 ] ) );
			colsValueLabel.setText( Integer.toString( imgSize[ 1 ] ) );
		}
		catch ( Exception e )
		{
			MainApp.error( "Failed to read current image dimensions" +
						   " from the controller. " + e.getMessage() );
		}
	}

	public void updateImageSizeCallback( Object arg )
	{
		int[] imgSize = ( int[] )arg;

		if ( ( imgSize[ 0 ] * imgSize[ 1 ] * 2 ) > CameraAPI.GetImageBufferSize() )
		{
			MainApp.error( "Image dimensions ( " + imgSize[ 1 ] +
						   "x" + imgSize[ 0 ] + " = " +
						   ( imgSize[ 0 ] * imgSize[ 1 ] * 2 ) +
						   " bytes ) too large for image buffer ( " +
						   CameraAPI.GetImageBufferSize() + " bytes )" );
		}
		else
		{
			rowsValueLabel.setText( Integer.toString( imgSize[ 0 ] ) );
			colsValueLabel.setText( Integer.toString( imgSize[ 1 ] ) );
		}
	}

	public void shutterCallback( int retVal )
	{
		if ( retVal != CameraAPI.DON ) { return; }

		if ( shutterButton.getActionCommand().equals( OPEN_SHUTTER_ACTION ) )
		{
			shutterButton.setIcon( CLOSE_SHUTTER_ICON );
			shutterButton.setToolTipText( SHUT_SHUTTER_ACTION );
			shutterButton.setActionCommand( SHUT_SHUTTER_ACTION );
		}
		else
		{
			shutterButton.setIcon( OPEN_SHUTTER_ICON );
			shutterButton.setToolTipText( OPEN_SHUTTER_ACTION );
			shutterButton.setActionCommand( OPEN_SHUTTER_ACTION );
		}
	}

	public void idleArrayCallback( int retVal )
	{
		if ( retVal != CameraAPI.DON ) return;

		if ( idleArrayButton.getActionCommand().equals( IDLE_ARRAY_ACTION ) )
		{
			idleArrayButton.setIcon( STOP_ARRAY_ICON );
			idleArrayButton.setToolTipText( STOP_ARRAY_ACTION );
			idleArrayButton.setActionCommand( STOP_ARRAY_ACTION );
		}
		else
		{
			idleArrayButton.setIcon( IDLE_ARRAY_ICON );
			idleArrayButton.setToolTipText( IDLE_ARRAY_ACTION );
			idleArrayButton.setActionCommand( IDLE_ARRAY_ACTION );
		}
	}

	public void powerCallback( int retVal )
	{
		if ( retVal != CameraAPI.DON ) return;

		if ( powerButton.getActionCommand().equals( POWER_ON_ACTION ) )
		{
			powerButton.setIcon( POWER_OFF_ICON );
			powerButton.setToolTipText( POWER_OFF_ACTION );
			powerButton.setActionCommand( POWER_OFF_ACTION );
		}
		else
		{
			powerButton.setIcon( POWER_ON_ICON );
			powerButton.setToolTipText( POWER_ON_ACTION );
			powerButton.setActionCommand( POWER_ON_ACTION );
		}
	}

	public void temperatureCallback( double val )
	{
		java.text.DecimalFormat df = new java.text.DecimalFormat( "#0.0" );
		tmptureValueLabel.setText( df.format( val ) + " C" );

		if ( quickActionThread == null ||
			 quickActionThread.getState() == Thread.State.RUNNABLE )
		{
				quickActionThread = new Thread( new FunctorRunnable( 
							/* Method class       */ CameraAPI.class,
							/* Method             */ "GetArrayTemperatureDN",
							/* Callback class obj */ this,
							/* Callback method    */ "temperatureDNCallback",
							/* Display msg box    */ "Reading Temperature DN",
							/* Optional msg icon  */ TEMPERATURE_ICON ) );
				quickActionThread.start();
		}

		MainApp.mainFrame.pack();
	}

	public void temperatureDNCallback( double gDN )
	{
		java.text.DecimalFormat df = new java.text.DecimalFormat( "0000" );
		df.setMaximumIntegerDigits( 4 );
		tmptureDNLabel.setText( "[" + df.format( gDN ) + " DN]" );
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

		String  prefKey  = null;
		String  tempStr  = null;
		boolean tempBool = false;

		prefKey = setupButton.getName() + SETUP_SCRIPT_PREF;
		tempStr = setupButton.getScriptFilename();
		if ( tempStr != null ) { MainApp.getPreferences().put( prefKey, tempStr ); }
	
		prefKey = setupButton.getName() + SETUP_USE_SCRIPT_PREF;
		tempBool = setupButton.isScriptSelected();
		MainApp.getPreferences().putBoolean( prefKey, tempBool );
	}

	// +--------------------------------------------------------------------+
	// |  loadPreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Loads the buttons and their scripts upon startup.                 |
	// +--------------------------------------------------------------------+
	protected void loadPreferences()
	{
		if ( setupButton != null )
		{
			boolean useScript      = false;
			String  scriptFilename = null;
			String  prefKey        = null;

			prefKey        = setupButton.getName() + SETUP_SCRIPT_PREF;
			scriptFilename = MainApp.getPreferences().get( prefKey, null );

			prefKey   = setupButton.getName() + SETUP_USE_SCRIPT_PREF;
			useScript = MainApp.getPreferences().getBoolean( prefKey, false );

			if ( scriptFilename != null && !scriptFilename.isEmpty() )
			{
				setupButton.setScript( scriptFilename, useScript );
			}
		}
	}
}
