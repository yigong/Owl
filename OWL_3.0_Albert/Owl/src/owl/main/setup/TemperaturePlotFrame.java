package owl.main.setup;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import owl.cameraAPI.CameraAPI;
import owl.main.MainApp;
import owl.main.owltypes.OwlBoldButton;
import owl.main.owltypes.OwlFrame;
import owl.plot.test.XYDataSet;
import owl.plot.test.XYPlot;
import owl.plot.test.XYRendererFactory;



public class TemperaturePlotFrame extends OwlFrame implements ActionListener
{
	private static final long serialVersionUID = -144574696318870673L;

	public final String RUN_ACTION			= "RUN";
	public final String STOP_ACTION			= "STOP";
	public final String SHOW_POINTS_ACTION	= "SHOW POINTS";
	public final String HIDE_POINTS_ACTION	= "HIDE POINTS";
	public final String TIME_RES_ACTION		= "TIME RESOLUTION";
	public final String SHOW_DN_PLOT_ACTION	= "SHOW DN PLOT";
	public final String HIDE_DN_PLOT_ACTION	= "HIDE DN PLOT";
	public final float  DEFAULT_TIME_SEC	= 30.0f;
	
	private TimeResPopup			m_timeResPopup;
	private XYPlot					m_tempPlot;
	private XYPlot					m_dnPlot;
	private ChartPanel				m_dnPanel;
	private ChartPanel				m_tempPanel;
	private XYLineAndShapeRenderer	m_renderer;
	private OwlBoldButton			m_runButton;
	private JButton					m_pointsButton;
	private JButton					m_timeResButton;
	private JButton					m_dnButton;
	private Timer					m_timer;
	private float					m_fDelay;


	// +--------------------------------------------------------------------+
	// |  Constructor                                                       |
	// +--------------------------------------------------------------------+
	public TemperaturePlotFrame( String title, boolean loadPrefs )
	{
		super( title, loadPrefs );

		m_renderer = XYRendererFactory.getDotRenderer( 6 );
		m_renderer.setSeriesShapesVisible( 0, false );
		m_renderer.setSeriesLinesVisible( 0, true );

		m_tempPlot = new XYPlot();
		m_tempPlot.setXLabel( "Time ( sec )" );
		m_tempPlot.setYLabel( "Temperature ( C )" );
		m_tempPlot.setDomainCrosshairVisible( false );
		m_tempPlot.setRangeCrosshairVisible( false );
		m_tempPlot.setRenderer( m_renderer );

		JFreeChart tempChart = new JFreeChart( m_tempPlot );
		tempChart.setBackgroundPaint( Color.white );
		tempChart.setTitle( "Temperature Value: [ 0.0 ]" );
		tempChart.removeLegend();

		m_tempPanel = new ChartPanel( tempChart );
		m_tempPanel.setBackground( Color.white );

		m_dnPlot = new XYPlot();
		m_dnPlot.setXLabel( "Time ( sec )" );
		m_dnPlot.setYLabel( "Digital Number" );
		m_dnPlot.setDomainCrosshairVisible( false );
		m_dnPlot.setRangeCrosshairVisible( false );

		JFreeChart dnChart = new JFreeChart( m_dnPlot );
		dnChart.setBackgroundPaint( Color.white );
		dnChart.setTitle( "DN Value: [ 0.0 ]" );
		dnChart.removeLegend();

		m_dnPanel = new ChartPanel( dnChart );
		m_dnPanel.setBackground( Color.white );
		m_dnPanel.setVisible( false );

		m_timeResPopup	= new TimeResPopup();
		m_timer			= null;
		m_fDelay		= DEFAULT_TIME_SEC;

		super.addComponent( createToolBar(), super.TOOLBAR_INDEX );
		super.addComponent( m_tempPanel, super.CENTER_CONTAINER_INDEX );
		super.addComponent( m_dnPanel, super.SOUTH_CONTAINER_INDEX );

		super.setSize( new java.awt.Dimension( 600, 450 ) );

		pack();
		owl.gui.utils.OwlUtilities.centerFrame( this );
		loadPreferences();
	}

	// +--------------------------------------------------------------------+
	// |  actionPerformed ( ActionListener )                                |
	// +--------------------------------------------------------------------+
	// |  Event listener                                                    |
	// +--------------------------------------------------------------------+
	public void actionPerformed( ActionEvent e )
	{
		//
		//   RUN action
		// +----------------------------------------------------------------+
		if ( e.getActionCommand().equals( RUN_ACTION ) )
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
					m_runButton.setColored( STOP_ACTION );

					m_timeResButton.setEnabled( false );

					m_timer = new Timer( ( int )( m_fDelay * 1000.0f ), new PlotUpdater() );
					m_timer.setInitialDelay( 0 );
					m_timer.start();
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

		//
		//   STOP action
		// +----------------------------------------------------------------+
		else if ( e.getActionCommand().equals( STOP_ACTION ) )
		{
			stopAndCleanup();
		}

		//
		//   SHOW POINTS action
		// +----------------------------------------------------------------+
		else if ( e.getActionCommand().equals( SHOW_POINTS_ACTION ) )
		{
			m_renderer.setSeriesShapesVisible( 0, true );
			m_pointsButton.setActionCommand( HIDE_POINTS_ACTION );
			m_pointsButton.setText( HIDE_POINTS_ACTION );
		}

		//
		//   HIDE POINTS action
		// +----------------------------------------------------------------+
		else if ( e.getActionCommand().equals( HIDE_POINTS_ACTION ) )
		{
			m_renderer.setSeriesShapesVisible( 0, false );
			m_pointsButton.setActionCommand( SHOW_POINTS_ACTION );
			m_pointsButton.setText( SHOW_POINTS_ACTION );
		}

		//
		//   TIME RESOLUTION action
		// +----------------------------------------------------------------+
		else if ( e.getActionCommand().equals( TIME_RES_ACTION ) )
		{
			m_timeResPopup.show( m_timeResButton,
								 m_timeResButton.getX() - m_timeResButton.getWidth(),
								 m_timeResButton.getY() + 20 );
		}

		//
		//   SHOW DN PLOT action
		// +----------------------------------------------------------------+
		else if ( e.getActionCommand().equals( SHOW_DN_PLOT_ACTION ) )
		{
			super.setSize(
					new java.awt.Dimension( 600, super.getSize().height * 2 ) );

			bSouthCompVisible = true;

			m_dnPanel.setVisible( true );
			m_dnButton.setText( HIDE_DN_PLOT_ACTION );
			m_dnButton.setActionCommand( HIDE_DN_PLOT_ACTION );
			pack();
		}

		//
		//   HIDE DN PLOT action
		// +----------------------------------------------------------------+
		else if ( e.getActionCommand().equals( HIDE_DN_PLOT_ACTION ) )
		{
			super.setSize(
					new java.awt.Dimension( 600, super.getSize().height / 2 ) );

			bSouthCompVisible = false;

			m_dnPanel.setVisible( false );
			m_dnButton.setText( SHOW_DN_PLOT_ACTION );
			m_dnButton.setActionCommand( SHOW_DN_PLOT_ACTION );
			pack();
		}

		//
		//   CLOSE action
		// +----------------------------------------------------------------+
		else if ( e.getActionCommand().equals( super.CLOSE_ACTION ) ||
				  e.getActionCommand().equals( super.EXIT_ACTION ) )
		{
			if ( m_timer != null )
			{
				stopAndCleanup();
			}

			super.actionPerformed( e );
		}

//		//
//		//   HIDE action ( OwlFrame override to properly handle south panel )
//		// +----------------------------------------------------------------+
//		else if ( e.getActionCommand().equals( HIDE_ACTION ) )
//		{
//			//  Test for the existence of a south container object
//			// +---------------------------------------------------+
//			try
//			{
//				JComponent comp =
//					( JComponent )getContentPane().getComponent( SOUTH_CONTAINER_INDEX );
//
//				if ( comp != null )
//				{
//					bSouthCompVisible = comp.isVisible();
//
//					comp.setVisible( false );
//					setSize( getWidth(), getHeight() - comp.getHeight() );
//				}
//			}
//			catch ( ArrayIndexOutOfBoundsException aioobe ) {}
//
//			//  Hide the center container object
//			// +---------------------------------------------------+
//			try
//			{
//				JComponent comp =
//					( JComponent )getContentPane().getComponent( CENTER_CONTAINER_INDEX );
//
//				if ( comp != null )
//				{
//					comp.setVisible( false );
//					setSize( new Dimension( getWidth(), getHeight() - comp.getHeight() ) );
//					pack();
//
//					hideOptionButton.setText( SHOW_ACTION );
//					hideOptionButton.setActionCommand( SHOW_ACTION );
//				}
//			}
//			catch ( ArrayIndexOutOfBoundsException aioobe ) {}
//		}
//
//		//
//		//   SHOW action ( OwlFrame override to properly handle south panel )
//		// +----------------------------------------------------------------+
//		else if ( e.getActionCommand().equals( SHOW_ACTION ) )
//		{
//			//  Show the south container object ( if it exists )
//			// +---------------------------------------------------+
//			try
//			{
//				JComponent comp =
//					( JComponent )getContentPane().getComponent( SOUTH_CONTAINER_INDEX );
//
//				if ( comp != null )
//				{
//					if ( bSouthCompVisible )
//					{
//						comp.setVisible( true );
//					}
//				}
//			}
//			catch ( ArrayIndexOutOfBoundsException aioobe ) {}
//
//			//  Show the center container object
//			// +---------------------------------------------------+
//			try
//			{
//				JComponent comp =
//					( JComponent )getContentPane().getComponent( CENTER_CONTAINER_INDEX );
//
//				if ( comp != null )
//				{
//					comp.setVisible( true );
//					setSize( comp.getSize() );
//					pack();
//
//					hideOptionButton.setText( HIDE_ACTION );
//					hideOptionButton.setActionCommand( HIDE_ACTION );
//				}
//			}
//			catch ( ArrayIndexOutOfBoundsException aioobe ) {}
//		}

		//
		//   Pass to super class
		// +----------------------------------------------------------------+
		else
		{
			super.actionPerformed( e );
		}
	}

	// +--------------------------------------------------------------------+
	// |  stopAndCleanup                                                    |
	// +--------------------------------------------------------------------+
	// |  Stops the timer and resets the run button                         |
	// +--------------------------------------------------------------------+
	protected void stopAndCleanup()
	{
		m_timer.stop();
		m_runButton.setBlack( RUN_ACTION );
		m_timeResButton.setEnabled( true );
	}

	// +--------------------------------------------------------------------+
	// |  createToolBar                                                     |
	// +--------------------------------------------------------------------+
	// |  Creates and returns the window toolbar                            |
	// +--------------------------------------------------------------------+
	protected JToolBar createToolBar()
	{
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable( false );

		m_runButton = super.createBoldToolbarButton( RUN_ACTION, Color.RED );
		toolbar.add( m_runButton );

		m_pointsButton = super.createNewToolbarButton( SHOW_POINTS_ACTION );
		toolbar.add( m_pointsButton );

		m_timeResButton = super.createNewToolbarButton( TIME_RES_ACTION );
		toolbar.add( m_timeResButton );

		m_dnButton = super.createNewToolbarButton( SHOW_DN_PLOT_ACTION );
		toolbar.add( m_dnButton );

		super.appendToolbar( toolbar );

		return toolbar;
	}

	// +--------------------------------------------------------------------+
	// |  loadPreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Loads the buttons and their scripts upon startup.                 |
	// +--------------------------------------------------------------------+
	protected void loadPreferences()
	{
		super.loadPreferences();
	}

	// +--------------------------------------------------------------------+
	// |  savePreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Saves the setup button script and state upon exit                 |
	// +--------------------------------------------------------------------+
	protected void savePreferences()
	{
		super.savePreferences();
	}

	// +--------------------------------------------------------------------+
	// |  Class TimeResPopup                                                |
	// +--------------------------------------------------------------------+
	// |  The time resolution popup menu that also handles the selections   |
	// +--------------------------------------------------------------------+
	protected class TimeResPopup extends JPopupMenu implements ActionListener
	{
		private static final long serialVersionUID = 9125226466838650178L;

		protected final String TIME_1_ACTION		= "1.0 sec";
		protected final String TIME_30_ACTION		= "30.0 sec ( default )";
		protected final String TIME_CUSTOM_ACTION	= "Custom";

		protected float m_fCustomValue;


		//   Constructor
		// +------------------------------------------------------+
		public TimeResPopup()
		{
			super();

			JMenuItem menuItem;

			menuItem = new JMenuItem( TIME_1_ACTION );
			menuItem.setActionCommand( TIME_1_ACTION );
			menuItem.addActionListener( this );
			super.add( menuItem );

			menuItem = new JMenuItem( TIME_30_ACTION );
			menuItem.setActionCommand( TIME_30_ACTION );
			menuItem.addActionListener( this );
			menuItem.setIcon( new ImageIcon( MainApp.getBitmapPath() + "On.gif" ) );
			super.add( menuItem );

			menuItem = new JMenuItem( TIME_CUSTOM_ACTION );
			menuItem.setActionCommand( TIME_CUSTOM_ACTION );
			menuItem.addActionListener( this );
			super.add( menuItem );

			m_fCustomValue = 30.f;
		}

		@Override
		public void actionPerformed( ActionEvent e )
		{
			//   Set 1.0 sec time resolution
			// +------------------------------------------------------+
			if ( e.getActionCommand().equals( TIME_1_ACTION ) )
			{
				m_fDelay = 1.0f;

				setItemChecked( ( JMenuItem )e.getSource() );
			}

			//   Set 3.0 sec time resolution
			// +------------------------------------------------------+
			else if ( e.getActionCommand().equals( TIME_30_ACTION ) )
			{
				m_fDelay = 30.0f;

				setItemChecked( ( JMenuItem )e.getSource() );
			}

			//   Set custom time resolution
			// +------------------------------------------------------+
			else if ( e.getActionCommand().equals( TIME_CUSTOM_ACTION ) )
			{
				owl.main.owltypes.OwlInputDialog dialog =
									new owl.main.owltypes.OwlInputDialog();
	
				String[] labels = { "Time Resolution ( >= 1 sec )" };
				String[] initText = { Float.toString( m_fCustomValue ) };

				int dRet = dialog.showDialog( null,
											  "Set Temperature Resolution",
											  labels,
											  initText );

				if ( dRet == owl.main.owltypes.OwlInputDialog.OK )
				{
					String[] sValues = dialog.getInputValues();

					if ( sValues.length > 0 && !sValues[ 0 ].isEmpty() )
					{
						float fValue = Float.valueOf( sValues[ 0 ] ).floatValue();

						if ( fValue >= 1.f )
						{
							m_fDelay = fValue;
							m_fCustomValue = m_fDelay;
	
							( ( JMenuItem )e.getSource() ).setText( TIME_CUSTOM_ACTION +
																	" [ " + m_fDelay +
																	" sec ]" );
		
							setItemChecked( ( JMenuItem )e.getSource() );
						}
						else
						{
							MainApp.error( "Time value unchanged! Value must be >= 1 second!" );
						}
					}
				}

//				String sTime =
//					( String )JOptionPane.showInputDialog( this,
//														   "Time Resolution ( >= 1 sec )",
//														   "Set Temperature Resolution",
//														   JOptionPane.QUESTION_MESSAGE,
//														   new ImageIcon( MainApp.getBitmapPath() + "XYPlot.gif" ),
//														   null,
//														   null );
//	
//				if ( sTime != null && !sTime.isEmpty() )
//				{
//					m_fDelay = Float.valueOf( sTime ).floatValue();
//
//					( ( JMenuItem )e.getSource() ).setText( TIME_CUSTOM_ACTION +
//															" [ " + m_fDelay +
//															" sec ]" );
//
//					setItemChecked( ( JMenuItem )e.getSource() );
//				}
			}
		}

		private void setItemChecked( JMenuItem item )
		{
			Component[] comps = super.getComponents();

			for ( int i=0; i<comps.length; i++ )
			{
				( ( JMenuItem )comps[ i ] ).setIcon( null );
			}

			item.setIcon( new ImageIcon( MainApp.getBitmapPath() + "On.gif" ) );
		}
	}

	// +--------------------------------------------------------------------+
	// |  Class PlotUpdater                                                 |
	// +--------------------------------------------------------------------+
	// |  Called every fDelay seconds and reads the temperature from the    |
	// |  controller and updates the plot. This class is threaded.          |
	// +--------------------------------------------------------------------+
	protected class PlotUpdater implements ActionListener, Runnable
	{
		private final DecimalFormat tempDF	= new DecimalFormat( "#0.0" );
		private final DecimalFormat dnDF	= new DecimalFormat( "0000" );
		private final int DATA_CLEAR_COUNT	= 300;

		private XYDataSet m_tempData;
		private XYDataSet m_dnData;
		private double m_gTmpture;
		private double m_gTime;
		private double m_gTimeCount;
		private double m_gDN;
		private Thread m_thread;

		//   Constructor
		// +------------------------------------------------------+
		public PlotUpdater()
		{
			m_thread 		= new Thread( this );
			m_tempData		= new XYDataSet( "Temperature Data" );
			m_dnData		= new XYDataSet( "DN Data" );
			m_gTmpture		= 0.0;
			m_gTime			= 0.0;
			m_gTimeCount	= 0.0;
			m_gDN			= 0.0;
		}

		//   ActionListener
		// +------------------------------------------------------+
		@Override
		public void actionPerformed( ActionEvent evt )
		{
			if ( m_thread.getState() != Thread.State.RUNNABLE )
			{
				if ( m_thread.getState() != Thread.State.NEW )
				{
					m_thread = new Thread( this );
				}

				m_thread.start();
			}
		}

		//   Thread Runnable
		// +------------------------------------------------------+
		@Override
		public void run()
		{
			Thread.currentThread().setName( "Owl - Temperature Plot Frame" );

			//   Read array temperature and DN if driver is open
			// +------------------------------------------------------+
			if ( CameraAPI.IsDeviceOpen() )
			{
				try {
					m_gTmpture = CameraAPI.GetArrayTemperature();
					m_gDN = CameraAPI.GetArrayTemperatureDN();
				}
				catch ( Exception e )
				{
					MainApp.warn( e.getMessage() );
				}
			}
			else
			{
				MainApp.error( "No driver opened!" );
				stopAndCleanup();
			}

			//   Don't report temperatures below -300 C or above 300 C
			// +------------------------------------------------------+
			if ( m_gTmpture < -300.0 ) { m_gTmpture = -300.0; }
			if ( m_gTmpture >  300.0 ) { m_gTmpture =  300.0; }

			//   Clear the data if using the 1 second test delay
			// +------------------------------------------------------+
			if ( ( m_fDelay == 1.f && m_tempData.getItemCount( 0 ) >= DATA_CLEAR_COUNT ) )
			{
				ValueAxis tempXAxis = m_tempPlot.getDomainAxis();
				tempXAxis.setRange( m_gTime, m_gTime + DATA_CLEAR_COUNT );

				ValueAxis dnXAxis = m_dnPlot.getDomainAxis();
				dnXAxis.setRange( m_gTime, m_gTime + DATA_CLEAR_COUNT );

				m_tempData.clear();
				m_dnData.clear();

				MainApp.warn( "Temperature data cleared because it's growing too large!" );
			}

			//   Set the point data and redraw plot
			// +------------------------------------------------------+
			m_tempData.addPoint( new Point2D.Float( ( float )m_gTime, ( float )m_gTmpture ) );
			m_tempPlot.setDataset( m_tempData );

			m_dnData.addPoint( new Point2D.Float( ( float )m_gTime, ( float )m_gDN ) );
			m_dnPlot.setDataset( m_dnData );

			//   Set current temperature in plot title
			// +------------------------------------------------------+
			m_tempPanel.getChart().setTitle( "Temperature Value: [ " + tempDF.format( m_gTmpture ) + " ]" );
			m_dnPanel.getChart().setTitle( "DN Value: [ " + dnDF.format( m_gDN ) + " ]" );

			m_gTime = m_fDelay * ( ++m_gTimeCount );
		}
	}
}
