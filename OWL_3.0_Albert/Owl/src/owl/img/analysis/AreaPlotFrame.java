package owl.img.analysis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;

import owl.cameraAPI.CameraAPI;
import owl.main.MainApp;
import owl.main.owltypes.OwlNumberField;
import owl.plot.test.XYPlot;


public class AreaPlotFrame extends AnalysisFrame implements ChangeListener, ItemListener
{
	private static final long serialVersionUID = -3250377878473214959L;

	protected final String AREA_ROW_PLOT	 = "Area Row Plot";
	protected final String AREA_COL_PLOT	 = "Area Col Plot";

	protected OwlNumberField	m_rowStartTxtfld;
	protected OwlNumberField	m_rowEndTxtfld;
	protected OwlNumberField	m_colStartTxtfld;
	protected OwlNumberField	m_colEndTxtfld;
	protected CrosshairOverlay	m_crosshairOverlay;
	protected XYPlot			m_xyAreaRowPlot;
	protected XYPlot			m_xyAreaColPlot;
	protected String			m_bActivePlot;


	public AreaPlotFrame()
	{
		super( "Area Plot" );

		super.addComponent( createCtrlPanel(),
							super.SOUTH_CONTAINER_INDEX );

		Crosshair crosshair = new Crosshair();

		m_crosshairOverlay = new CrosshairOverlay();
		m_crosshairOverlay.addDomainCrosshair( crosshair );
		m_crosshairOverlay.addRangeCrosshair( crosshair );

		m_xyAreaRowPlot = new XYPlot();
		m_xyAreaRowPlot.setXLabel( "row" );
		m_xyAreaRowPlot.setYLabel( "adu" );

		JFreeChart areaRowChart = new JFreeChart( m_xyAreaRowPlot );
		areaRowChart.setBackgroundPaint( Color.white );
		areaRowChart.setTitle( "Area Row Plot" );
		areaRowChart.removeLegend();

		ChartPanel areaRowChartPanel = new ChartPanel( areaRowChart );
		areaRowChartPanel.setBackground( Color.white );
		areaRowChartPanel.addChartMouseListener( this );
		areaRowChartPanel.addOverlay( m_crosshairOverlay );

		m_xyAreaColPlot = new XYPlot();
		m_xyAreaColPlot.setXLabel( "column" );
		m_xyAreaColPlot.setYLabel( "adu" );

		JFreeChart areaColChart = new JFreeChart( m_xyAreaColPlot );
		areaColChart.setBackgroundPaint( Color.white );
		areaColChart.setTitle( "Area Column Plot" );
		areaColChart.removeLegend();

		ChartPanel areaColChartPanel = new ChartPanel( areaColChart );
		areaColChartPanel.setBackground( Color.white );
		areaColChartPanel.addChartMouseListener( this );
		areaColChartPanel.addOverlay( m_crosshairOverlay );

		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addChangeListener( this );
		tabPane.addTab( AREA_ROW_PLOT, areaRowChartPanel );
		tabPane.addTab( AREA_COL_PLOT, areaColChartPanel );

		super.setSize( new Dimension( 875, 600 ) );
		super.setComponent( tabPane );
		super.pack();

		loadPreferences();

		( new Thread( new ReadCtlrRunnable() ) ).start();
	}

	@Override
	public void stateChanged( ChangeEvent ce )
	{
		if ( ce.getSource() instanceof javax.swing.JTabbedPane )
		{
			JTabbedPane tabPane = ( JTabbedPane )ce.getSource();

			m_bActivePlot =
				tabPane.getTitleAt( tabPane.getSelectedIndex() );
		}
	}

	@Override
	public void itemStateChanged( ItemEvent arg0 )
	{
		if ( super.isShowCrosshair() )
		{
			m_xyAreaRowPlot.crosshairOverlayEnabled( true );
			m_xyAreaColPlot.crosshairOverlayEnabled( true );
		}
		else
		{
			m_xyAreaRowPlot.crosshairOverlayEnabled( false );
			m_xyAreaColPlot.crosshairOverlayEnabled( false );
		}

		m_crosshairOverlay.fireOverlayChanged();
	}

	@Override
	public void readCtlr()
	{
		try
		{
			if ( CameraAPI.IsDeviceOpen() && CameraAPI.IsControllerConnected() )
			{
				MainApp.infoStart( "Reading image dimensions from controller" );

				int dRowEnd = CameraAPI.GetImageRows();
				int dColEnd = CameraAPI.GetImageCols();

				m_rowStartTxtfld.setValue( 0 );
				m_rowEndTxtfld.setValue( dRowEnd );
					
				m_colStartTxtfld.setValue( 0 );
				m_colEndTxtfld.setValue( dColEnd );
						
				setImageRowsValue( dRowEnd );
				setImageColsValue( dColEnd );

				MainApp.infoEnd();
			}
			else
			{
				MainApp.error( "( ReadCtlrRunnable ): " +
							   "No device or controller connected!" );
			}
		}
		catch ( Exception e )
		{
			MainApp.error( e );
		}
	}

	@Override
	public void readDS9()
	{
		try
		{
			MainApp.info( "Reading image dimensions from DS9 ...." );
			MainApp.warn( "Requires POINT shape to be manually selected within DS9!" );

			long[] lCoords = getDS9Box();

			if ( m_bStop ) { m_runButton.setBlack( RUN_ACTION ); return; }

			long lRowStart = ( lCoords[ BOX_CENTER_ROW ] - ( lCoords[ BOX_ROWS ] / 2 ) );
			long lRowEnd   = ( lCoords[ BOX_CENTER_ROW ] + ( lCoords[ BOX_ROWS ] / 2 ) );
			long lColStart = ( lCoords[ BOX_CENTER_COL ] - ( lCoords[ BOX_COLS ] / 2 ) );
			long lColEnd   = ( lCoords[ BOX_CENTER_COL ] + ( lCoords[ BOX_COLS ] / 2 ) );

			m_rowStartTxtfld.setText( Long.toString( lRowStart ) );
			m_rowEndTxtfld.setText( Long.toString( lRowEnd ) );

			m_colStartTxtfld.setText( Long.toString( lColStart ) );
			m_colEndTxtfld.setText( Long.toString( lColEnd ) );

			setImageRowsValue( MainApp.mainFrame.cameraPanel.getRowLabelValue() );
			setImageColsValue( MainApp.mainFrame.cameraPanel.getColLabelValue() );
		}
		catch ( Exception e )
		{
			MainApp.error( e );
		}
	}

	@Override
	public void run()
	{
		Thread.currentThread().setName( "Owl - AreaPlotFrame" );

		Point2D.Float[] points = null;

		m_runButton.setColored( ABORT_ACTION );

		try
		{
			if ( m_bStop ) { m_runButton.setBlack( RUN_ACTION ); return; }

			//
			// Do an area row plot
			//
			if ( m_bActivePlot.equals( AREA_ROW_PLOT ) )
			{
				points = CameraAPI.GetImageRowArea( m_rowStartTxtfld.getInt(),
													m_rowEndTxtfld.getInt(),
													m_colStartTxtfld.getInt(),
													m_colEndTxtfld.getInt(),
													super.getImageRowsValue(),
													super.getImageColsValue() );

				m_xyAreaRowPlot.setPlot( points,
									   "Area Row [ " + m_rowStartTxtfld.getInt() +
									   ":" + m_rowEndTxtfld.getInt() + " ]" );
			}

			//
			// Do an area column plot
			//
			else if ( m_bActivePlot.equals( AREA_COL_PLOT ) )
			{
				points = CameraAPI.GetImageColArea( m_rowStartTxtfld.getInt(),
													m_rowEndTxtfld.getInt(),
													m_colStartTxtfld.getInt(),
													m_colEndTxtfld.getInt(),
													super.getImageRowsValue(),
													super.getImageColsValue() );

				m_xyAreaColPlot.setPlot( points,
									   "Area Col [ " + m_colStartTxtfld.getInt() +
									   ":" + m_colEndTxtfld.getInt() + " ]" );
			}

			//
			// Error, unknown plot type
			//
			else
			{
				MainApp.error( "Invalid area plot type: " + m_bActivePlot );
			}

			if ( m_bStop ) { m_runButton.setText( RUN_ACTION ); return; }

			//
			// Get area statistics
			//
			super.getStats( m_rowStartTxtfld.getInt(),
							m_rowEndTxtfld.getInt(),
							m_colStartTxtfld.getInt(),
							m_colEndTxtfld.getInt(),
							super.getImageRowsValue(),
							super.getImageColsValue() );
		}
		catch ( Exception e )
		{
			MainApp.error( e.getMessage() );
		}
		finally
		{
			m_runButton.setBlack( RUN_ACTION );
		}

		super.pack();
	}

	private JPanel createCtrlPanel()
	{
		JPanel rPanel = new JPanel();
		rPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );

		m_rowStartTxtfld = new OwlNumberField( 0, 7 );
		m_rowEndTxtfld   = new OwlNumberField( 10, 7 );

		rPanel.add( new JLabel( "<html><b>ROW</b></html>" ) );
		rPanel.add( javax.swing.Box.createHorizontalStrut( 3 ) );
		rPanel.add( new JLabel( "start: " ) );
		rPanel.add( m_rowStartTxtfld );
		rPanel.add( javax.swing.Box.createHorizontalStrut( 3 ) );
		rPanel.add( new JLabel( "end: " ) );
		rPanel.add( m_rowEndTxtfld );

		JPanel cPanel = new JPanel();
		cPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );

		m_colStartTxtfld = new OwlNumberField( 0, 7 );
		m_colEndTxtfld   = new OwlNumberField( 10, 7 );

		cPanel.add( new JLabel( "<html><b>COL</b></html>" ) );
		cPanel.add( javax.swing.Box.createHorizontalStrut( 3 ) );
		cPanel.add( new JLabel( "start: " ) );
		cPanel.add( m_colStartTxtfld );
		cPanel.add( javax.swing.Box.createHorizontalStrut( 3 ) );
		cPanel.add( new JLabel( "end: " ) );
		cPanel.add( m_colEndTxtfld );

		JPanel sPanel = createStatsPanel( true, this );

		JPanel panel = new JPanel();
		panel.setBorder( BorderFactory.createLoweredBevelBorder() );
		panel.setLayout( super.gbl );

		addComponent( panel, rPanel,              0, 0, 0, 0, 0, 0, 1, 1 );
		addComponent( panel, cPanel,              0, 0, 0, 0, 0, 1, 1, 1 );
		addComponent( panel, getCoordsSrcPanel(), 0, 0, 0, 0, 0, 2, 1, 1 );
		addComponent( panel, sPanel,              0, 0, 0, 0, 1, 0, 1, 3 );

		return panel;
	}
}
