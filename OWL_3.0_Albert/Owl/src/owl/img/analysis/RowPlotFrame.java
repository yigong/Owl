package owl.img.analysis;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;

import owl.cameraAPI.CameraAPI;
import owl.main.MainApp;
import owl.main.owltypes.OwlNumberField;
import owl.plot.test.XYPlot;


public class RowPlotFrame extends AnalysisFrame implements ItemListener
{
	private static final long serialVersionUID = -8892907780755290767L;

	protected OwlNumberField	m_rowTxtfld;
	protected OwlNumberField	m_colStartTxtfld;
	protected OwlNumberField	m_colEndTxtfld;
	protected CrosshairOverlay	m_crosshairOverlay;
	protected XYPlot			m_xyPlot;


	public RowPlotFrame()
	{
		super( "Row Plot" );

		m_xyPlot = new XYPlot();
		m_xyPlot.setXLabel( "Column" );
		m_xyPlot.setYLabel( "ADU" );

		JFreeChart chart = new JFreeChart( m_xyPlot );
		chart.setBackgroundPaint( Color.white );
		chart.setTitle( super.getTitle() );
		chart.removeLegend();

		Crosshair crosshair = new Crosshair();

		m_crosshairOverlay = new CrosshairOverlay();
		m_crosshairOverlay.addDomainCrosshair( crosshair );
		m_crosshairOverlay.addRangeCrosshair( crosshair );

		ChartPanel chartPanel = new ChartPanel( chart );
		chartPanel.setBackground( Color.white );
		chartPanel.addChartMouseListener( this );
		chartPanel.addOverlay( m_crosshairOverlay );

		super.setPlot( chartPanel );

		super.addComponent( createCtrlPanel(),
							super.SOUTH_CONTAINER_INDEX );

		super.pack();

		loadPreferences();

		( new Thread( new ReadCtlrRunnable() ) ).start();
	}

	@Override
	public void itemStateChanged( ItemEvent arg0 )
	{
		if ( m_linesChkbox.isSelected() )
		{
			m_xyPlot.crosshairOverlayEnabled( true );
		}
		else
		{
			m_xyPlot.crosshairOverlayEnabled( false );
		}

		m_crosshairOverlay.fireOverlayChanged();
	}

	@Override
	public void run()
	{
		Thread.currentThread().setName( "Owl - RowPlotFrame" );

		Point2D.Float[] points = null;

		m_runButton.setColored( ABORT_ACTION );

		try
		{
			if ( m_bStop ) { m_runButton.setBlack( RUN_ACTION ); return; }

			points = CameraAPI.GetImageRow( m_rowTxtfld.getInt(),
											m_colStartTxtfld.getInt(),
											m_colEndTxtfld.getInt(),
											super.getImageRowsValue(),
											super.getImageColsValue() );

			m_xyPlot.setPlot( points, "Row " + m_rowTxtfld.getInt() );

			if ( m_bStop ) { m_runButton.setBlack( RUN_ACTION ); return; }

			super.getStats( m_rowTxtfld.getInt(),
							m_rowTxtfld.getInt(),
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

	public void readCtlr()
	{
		try
		{
			if ( CameraAPI.IsDeviceOpen() && CameraAPI.IsControllerConnected() )
			{
				MainApp.infoStart( "Reading image dimensions from controller" );

				int dRowEnd = CameraAPI.GetImageRows();
				int dColEnd = CameraAPI.GetImageCols();
						
				m_rowTxtfld.setValue( 0 );
					
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

	public void readDS9()
	{
		try
		{
			MainApp.info( "Reading image dimensions from DS9 ...." );
			MainApp.warn( "Requires POINT shape to be manually selected within DS9!" );

			long[] lCoords = getDS9Point();

			if ( m_bStop ) { m_runButton.setBlack( RUN_ACTION ); return; }

			m_rowTxtfld.setText( Long.toString( lCoords[ POINT_ROW ] ) );

			setImageRowsValue( MainApp.mainFrame.cameraPanel.getRowLabelValue() );
			setImageColsValue( MainApp.mainFrame.cameraPanel.getColLabelValue() );
		}
		catch ( Exception e )
		{
			MainApp.error( e );
		}
	}

	private JPanel createCtrlPanel()
	{
		JPanel rPanel = new JPanel();
		rPanel.setLayout( new FlowLayout( FlowLayout.CENTER ) );
		rPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );

		m_rowTxtfld = new OwlNumberField( 0, 6 );

		rPanel.add( new JLabel( "<html><b>ROW</b></html>" ) );
		rPanel.add( javax.swing.Box.createHorizontalStrut( 5 ) );
		rPanel.add( m_rowTxtfld );
		rPanel.add( javax.swing.Box.createHorizontalStrut( 5 ) );

		JPanel cPanel = new JPanel();
		cPanel.setLayout( new FlowLayout( FlowLayout.CENTER ) );
		cPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );

		m_colStartTxtfld = new OwlNumberField( 0, 6 );
		m_colEndTxtfld   = new OwlNumberField( 10, 6 );

		cPanel.add( new JLabel( "<html><b>COL</b></html>" ) );
		cPanel.add( javax.swing.Box.createHorizontalStrut( 5 ) );
		cPanel.add( new JLabel( "start: " ) );
		cPanel.add( m_colStartTxtfld );
		cPanel.add( javax.swing.Box.createHorizontalStrut( 5 ) );
		cPanel.add( new JLabel( "end: " ) );
		cPanel.add( m_colEndTxtfld );
		cPanel.add( javax.swing.Box.createHorizontalStrut( 5 ) );

		JPanel sPanel = createStatsPanel( true, this );

		JPanel panel = new JPanel();
		panel.setLayout( super.gbl );
		addComponent( panel, rPanel,              0, 0, 0, 0, 0, 0, 1, 1 );
		addComponent( panel, cPanel,              0, 0, 0, 0, 0, 1, 1, 1 );
		addComponent( panel, getCoordsSrcPanel(), 0, 0, 0, 0, 0, 2, 1, 1 );
		addComponent( panel, sPanel,              0, 0, 0, 0, 1, 0, 1, 3 );

		return panel;
	}
}
