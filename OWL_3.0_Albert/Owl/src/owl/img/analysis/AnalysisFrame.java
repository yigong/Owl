package owl.img.analysis;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.ui.RectangleEdge;

import owl.cameraAPI.CameraAPI;
import owl.display.ds9.DS9Accessor;
import owl.gui.utils.OwlButtonFactory;
import owl.main.MainApp;
import owl.main.owltypes.OwlBoldButton;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlNumberField;
import owl.main.owltypes.OwlNumberLabel;
import owl.plot.test.XYPlot;



public abstract class AnalysisFrame extends OwlFrame implements Runnable, ChartMouseListener
{
	private static final long serialVersionUID = 4516926105794159373L;

	protected final String RUN_ACTION		= "RUN";
	protected final String ABORT_ACTION		= "ABORT";
	protected final String PRINT_ACTION		= "PRINT";
	protected final String DS9_ACTION		= "DS9";
	protected final String CTLR_ACTION		= "CTLR";

	protected final int BOX_CENTER_COL		= 0;
	protected final int BOX_CENTER_ROW		= 1;
	protected final int BOX_COLS			= 2;
	protected final int BOX_ROWS			= 3;

	protected final int POINT_COL			= 0;
	protected final int POINT_ROW			= 1;

	protected OwlNumberLabel	m_minLabel;
	protected OwlNumberLabel	m_maxLabel;
	protected OwlNumberLabel	m_meanLabel;
	protected OwlNumberLabel	m_varLabel;
	protected OwlNumberLabel	m_stdDevLabel;
	protected OwlNumberLabel	m_satPixLabel;
	protected OwlNumberLabel	m_xLabel;
	protected OwlNumberLabel	m_yLabel;
	protected OwlNumberLabel	m_aduLabel;
	protected OwlNumberField	m_imgRowsTxtfld;
	protected OwlNumberField	m_imgColsTxtfld;
	protected JCheckBox      	m_linesChkbox;
	protected OwlBoldButton  	m_runButton;
	protected JButton        	m_printButton;
	protected JToolBar       	m_toolbar;
	protected boolean        	m_bStop;


	public AnalysisFrame( String sTitle )
	{
		super( sTitle, false );

		m_toolbar = new JToolBar();
		m_toolbar.setFloatable( false );
		m_toolbar.setBorder( BorderFactory.createRaisedBevelBorder() );

		m_runButton = createBoldToolbarButton( RUN_ACTION, Color.red );
		m_toolbar.add( m_runButton );

		m_printButton = createNewToolbarButton( PRINT_ACTION );
		m_toolbar.add( m_printButton );

		super.appendToolbar( m_toolbar );
		super.addComponent( m_toolbar, super.TOOLBAR_INDEX );
		super.addComponent( new JPanel(), super.CENTER_CONTAINER_INDEX );
		super.setSize( new Dimension( 740, 500 ) );
		super.pack();

		loadPreferences();
	}

	public AnalysisFrame()
	{
		this( "Image Analysis" );
	}

	public void setPlot( ChartPanel plotPanel )
	{
		super.getContentPane().remove( super.CENTER_CONTAINER_INDEX );
		super.addComponent( plotPanel, super.CENTER_CONTAINER_INDEX );
		super.pack();
	}

	//
	// Used to add a component other than a plot canvas to the frame.
	// For example, to add a tabbed pane of plot canvases.
	//
	public void setComponent( JComponent comp )
	{
		super.getContentPane().remove( super.CENTER_CONTAINER_INDEX );
		super.addComponent( comp, super.CENTER_CONTAINER_INDEX );
	}

	@Override
	public void setVisible( boolean showHide )
	{
		if ( showHide )
		{
			centerFrame();
		}

		super.setVisible( showHide );
	}

	public void setImageRowsValue( int dRows )
	{
		m_imgRowsTxtfld.setValue( dRows );
	}

	public int getImageRowsValue()
	{
		return m_imgRowsTxtfld.getInt();
	}

	public void setImageColsValue( int dCols )
	{
		m_imgColsTxtfld.setValue( dCols );
	}

	public int getImageColsValue()
	{
		return m_imgColsTxtfld.getInt();
	}

	public boolean isShowCrosshair()
	{
		return m_linesChkbox.isSelected();
	}

	public void printPlot()
	{
		Component comp =
				super.getContentPane().getComponent( super.CENTER_CONTAINER_INDEX );

		if ( comp != null )
		{
			owl.gui.utils.PrintUtilities pu = new owl.gui.utils.PrintUtilities();
			pu.printComponent( comp );
		}
	}

	public abstract void run();
	public abstract void readCtlr();
	public abstract void readDS9();

	// +--------------------------------------------------------------------------+
	// | ActionListener Methods                                                   |
	// +--------------------------------------------------------------------------+
	@Override
	public void actionPerformed( ActionEvent event )
	{
		if ( event.getActionCommand().equals( RUN_ACTION ) )
		{
			m_bStop = false;

			new Thread( this ).start();
		}

		else if ( event.getActionCommand().equals( ABORT_ACTION ) )
		{
			m_bStop = true;

			m_runButton.setBlack( RUN_ACTION );
		}

		else if ( event.getActionCommand().equals( PRINT_ACTION ) )
		{
			printPlot();
		}

		else if ( event.getActionCommand().equals( CTLR_ACTION ) )
		{
			( new Thread( new ReadCtlrRunnable() ) ).start();
		}

		else if ( event.getActionCommand().equals( DS9_ACTION ) )
		{
			( new Thread( new ReadDS9Runnable() ) ).start();
		}

		else
		{
			super.actionPerformed( event );
		}
	}

	@Override
	public void chartMouseClicked( ChartMouseEvent cme )
	{
	}

	@Override
	public void chartMouseMoved( ChartMouseEvent cme )
	{
		int mouseX = cme.getTrigger().getX();
		int mouseY = cme.getTrigger().getY();

		ChartPanel chartPanel = null;

		Component centerComp =
					super.getContentPane().getComponent( super.CENTER_CONTAINER_INDEX );

		if ( centerComp instanceof javax.swing.JTabbedPane )
		{
			chartPanel =
					( ChartPanel )( ( javax.swing.JTabbedPane )centerComp ).getSelectedComponent();
		}
		else
		{
			chartPanel =
					( ChartPanel )super.getContentPane().getComponent( super.CENTER_CONTAINER_INDEX );
		}

		if ( chartPanel == null ) { return; }

		Point2D p = chartPanel.translateScreenToJava2D( new Point( mouseX, mouseY ) );

		if ( cme.getChart().getPlot() instanceof owl.plot.test.XYPlot )
		{
			XYPlot plot = ( XYPlot )cme.getChart().getPlot();
	
			Rectangle2D plotArea = chartPanel.getScreenDataArea();
	
			ValueAxis domainAxis = plot.getDomainAxis();
			RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
	
			ValueAxis rangeAxis = plot.getRangeAxis();
			RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
	
			double chartX = domainAxis.java2DToValue( p.getX(), plotArea, domainAxisEdge );
			double chartY = rangeAxis.java2DToValue( p.getY(), plotArea, rangeAxisEdge );
	
			if ( plotArea.contains( p ) )
			{
				m_xLabel.setNumber( chartX );
				m_yLabel.setNumber( chartY );

				if ( plot.getDataset() != null )
				{
					m_aduLabel.setNumber( plot.getDataset().getYValue( 0, ( int )chartX ) );
				}

				if ( m_linesChkbox.isSelected() )
				{
					plot.setDomainCrosshairValue( chartX );
					plot.setRangeCrosshairValue( chartY );
				}
			}
		}
	}

	protected JPanel getCoordsSrcPanel()
	{
		return getCoordsSrcPanel( 0 );
	}

	protected JPanel getCoordsSrcPanel( int dStrutSize )
	{
		JPanel fieldPanel = new JPanel();

		fieldPanel.setBorder(
					BorderFactory.createEtchedBorder(
									EtchedBorder.RAISED ) );

		m_imgRowsTxtfld = new OwlNumberField( 0, 7 );
		m_imgColsTxtfld = new OwlNumberField( 0, 7 );

		fieldPanel.add( new JLabel( "<html><b>IMG</b></html>" ) );
		fieldPanel.add( javax.swing.Box.createHorizontalStrut( 3 ) );
		fieldPanel.add( new JLabel( "rows: " ) );
		fieldPanel.add( m_imgRowsTxtfld );
		fieldPanel.add( javax.swing.Box.createHorizontalStrut( 3 ) );
		fieldPanel.add( new JLabel( "cols: " ) );
		fieldPanel.add( m_imgColsTxtfld );

		JButton ds9Button =
					OwlButtonFactory.create( new ImageIcon( MainApp.getBitmapPath() + "ds9.gif" ),
											 "Use DS9 For Coordinates",
											 new Dimension( 30, 28 ),
											 DS9_ACTION,
											 this );

		JButton ctlrButton =
					OwlButtonFactory.create( new ImageIcon( MainApp.getBitmapPath() + "ReadCtlr.gif" ),
											 "Read Image Dimensions From Controller",
											 new Dimension( 30, 28 ),
											 CTLR_ACTION,
											 this );

		JPanel panel = new JPanel();

		panel.add( fieldPanel );
		panel.add( ds9Button );
		panel.add( ctlrButton );
		panel.add( Box.createHorizontalStrut( dStrutSize ) );

		return panel;
	}

	protected JPanel createStatsPanel()
	{
		return createStatsPanel( false, null );
	}

	protected JPanel createStatsPanel( boolean bShowPosInfo, ItemListener l )
	{
		JPanel panel = new JPanel();
		panel.setLayout( super.gbl );

		m_minLabel    = new OwlNumberLabel( "Min",    0.0, Color.BLUE );
		m_maxLabel    = new OwlNumberLabel( "Max",    0.0, Color.BLUE );
		m_meanLabel   = new OwlNumberLabel( "Mean",   0.0, Color.BLUE );
		m_varLabel    = new OwlNumberLabel( "Var",    0.0, Color.BLUE );
		m_stdDevLabel = new OwlNumberLabel( "StdDev", 0.0, Color.BLUE );
		m_satPixLabel = new OwlNumberLabel( "SatPix", 0.0, Color.BLUE );

		m_varLabel.setFixedWidth( 105 );
		m_stdDevLabel.setFixedWidth( 105 );

		addComponent( panel, m_minLabel,    0, 5, 0, 5, 0, 0, 1, 1 );
		addComponent( panel, m_maxLabel,    0, 5, 0, 5, 0, 1, 1, 1 );
		addComponent( panel, m_meanLabel,   0, 5, 0, 5, 0, 2, 1, 1 );
		addComponent( panel, m_varLabel,    0, 5, 0, 5, 0, 3, 1, 1 );
		addComponent( panel, m_stdDevLabel, 0, 5, 0, 5, 0, 4, 1, 1 );
		addComponent( panel, m_satPixLabel, 0, 5, 0, 5, 0, 5, 1, 1 );

		if ( bShowPosInfo )
		{
			m_xLabel   = new OwlNumberLabel( "X", 0.0, Color.blue );
			m_yLabel   = new OwlNumberLabel( "Y", 0.0, Color.blue );
			m_aduLabel = new OwlNumberLabel( "ADU", 0.0, Color.blue );

			m_linesChkbox = new JCheckBox( "Show Position Lines", false );

			if ( l != null )
			{
				m_linesChkbox.addItemListener( l );
			}

			addComponent( panel, m_xLabel,      0, 5, 0, 5, 1, 0, 1, 1 );
			addComponent( panel, m_yLabel,      0, 5, 0, 5, 1, 1, 1, 1 );
			addComponent( panel, m_aduLabel,    0, 5, 0, 5, 1, 2, 1, 1 );
			addComponent( panel, m_linesChkbox, 0, 5, 0, 5, 1, 3, 1, 3 );
		}

		return panel;
	}

	protected void getStats( int dRowStart, int dRowEnd, int dColStart, int dColEnd,
							 int dCameraRows, int dCameraCols ) throws Exception
	{
		ImageStats stats = CameraAPI.GetImageStats( dRowStart,
													dRowEnd,
													dColStart,
													dColEnd,
													dCameraRows,
													dCameraCols );

		m_minLabel.setNumber( stats.gMin );
		m_maxLabel.setNumber( stats.gMax );
		m_meanLabel.setNumber( stats.gMean );
		m_varLabel.setNumber( stats.gVariance );
		m_stdDevLabel.setNumber( stats.gStdDev );
		m_satPixLabel.setNumber( stats.gSaturatedPixCnt );
	}

	protected long[] getDS9Box() throws Exception
	{
		long[] lCoords = null;
		String[] tokens = null;

		DS9Accessor ds9a = new DS9Accessor();

		if ( ds9a.isDS9Ready( true ) )
		{
			Vector<String> aVec = ds9a.doCmd( "xpaget ds9 regions" );

			if ( aVec != null )
			{
				for ( int i=0; i<aVec.size(); i++ )
				{
					String aString = ( aVec.get( i ) );

					//  Search for a box region
					// +--------------------------------------------------+
					if ( aString.contains( "box" ) )
					{
						int leftIndex  = aString.indexOf( '(' );
						int rightIndex = aString.indexOf( ')' );

						//  Remove the "box" parentheses
						// +--------------------------------------------------+
						if ( leftIndex >= 0 && rightIndex >= 0 )
						{
							String subString = aString.substring( leftIndex + 1,
																  rightIndex );

							tokens = subString.trim().split( "," );
							break;
						}
					}
				}
			}
			else
			{
				throw ( new Exception( "( getDS9Box ): Failed to read DS9 region!" ) );
			}
		}
		else
		{
			throw ( new Exception( "( getDS9Box ): DS9 is NOT ready!" ) );
		}

		if ( tokens == null )
		{
			throw ( new Exception( "( getDS9Box ): Failed to read DS9 region!" ) );
		}

		lCoords = new long[ tokens.length ];

		//  Round the coordinates off
		// +--------------------------------------------------+
		for ( int t=0; t<tokens.length; t++ )
		{
			lCoords[ t ] = Math.round( Float.parseFloat( tokens[ t ] ) );
		}

		return lCoords;
	}

	protected long[] getDS9Point() throws Exception
	{
		long[] lCoords = null;
		String[] tokens = null;

		DS9Accessor ds9a = new DS9Accessor();

		if ( ds9a.isDS9Ready( true ) )
		{
			MainApp.warn( "NOTE: You must manually select \"Cross Point\" " +
						  "or \"X Point\" from the Regions->Shape menu on DS9!" );

			Vector<String> aVec = ds9a.doCmd( "xpaget ds9 regions" );

			if ( aVec != null )
			{
				for ( int i=0; i<aVec.size(); i++ )
				{
					String aString = ( aVec.get( i ) );

					//  Search for a vector region
					// +--------------------------------------------------+
					if ( aString.contains( "point" ) )
					{
						int leftIndex  = aString.indexOf( '(' );
						int rightIndex = aString.indexOf( ')' );

						//  Remove the "vector" parentheses
						// +--------------------------------------------------+
						if ( leftIndex >= 0 && rightIndex >= 0 )
						{
							String subString = aString.substring( leftIndex + 1,
																  rightIndex );

							tokens = subString.trim().split( "," );
							break;
						}
					}
				}
			}
			else
			{
				throw ( new Exception( "( getDS9Box ): Failed to read DS9 region!" ) );
			}
		}
		else
		{
			throw ( new Exception( "( getDS9Box ): DS9 is NOT ready!" ) );
		}

		if ( tokens == null )
		{
			throw ( new Exception( "( getDS9Box ): Failed to read DS9 region!" ) );
		}

		lCoords = new long[ tokens.length ];

		//  Round the coordinates off
		// +--------------------------------------------------+
		for ( int t=0; t<tokens.length; t++ )
		{
			lCoords[ t ] = Math.round( Float.parseFloat( tokens[ t ] ) );
		}

		return lCoords;
	}

	private void centerFrame()
	{
		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int xPos = d.width / 2 - super.getWidth() / 2 - 50;
		int yPos = d.height / 2  - super.getHeight() / 2;
		super.setLocation( xPos, yPos );
	}

	public class ReadCtlrRunnable implements Runnable
	{
		@Override
		public void run()
		{
			readCtlr();
		}
	}

	public class ReadDS9Runnable implements Runnable
	{
		@Override
		public void run()
		{
			readDS9();
		}
	}
}
