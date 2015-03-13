package owl.ptc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import owl.gui.utils.PrintUtilities;
import owl.main.MainApp;
import owl.main.owltypes.OwlInputDialog;



public class PTCPlotFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID	= -8776389157794502278L;

	private final String PRINT_ACTION			= "PRINT";
	private final String CLOSE_ACTION			= "CLOSE";
	private final String FRAME_ICON				= MainApp.getBitmapPath() + "PTC2.gif";
	private final String PROP_PREF_NAME			= "PTCPlot";

	private ChartPanel chartPanel;
	private JPanel plotPanel;
	private JPanel	statsPanel;
	private PTCPlot	plot;
	private float	A, B;


	//--------------------------------------------------------------------------
    //   Constructor:
    //--------------------------------------------------------------------------
	public PTCPlotFrame( String title )
	{
		super.setTitle( title );
		super.setSize( new java.awt.Dimension( 400, 600 ) );
		super.setIconImage( ( new javax.swing.ImageIcon( FRAME_ICON ) ).getImage() );

		JToolBar toolbar = new JToolBar();
		toolbar.setBorder( BorderFactory.createLineBorder( Color.gray, 1 ) );
		toolbar.setFloatable( false );

		JButton printButton = new JButton( PRINT_ACTION );
		printButton.setBorderPainted( false );
		printButton.setFocusPainted( false );
		printButton.addActionListener( this );
		toolbar.add( printButton );

		JButton closeButton = new JButton( CLOSE_ACTION );
		closeButton.setBorderPainted( false );
		closeButton.setFocusPainted( false );
		closeButton.addActionListener( this );
		toolbar.add( closeButton );

		// Create plot object
		plot = new PTCPlot();
		plot.setXLabel( "Mean Counts ( ADU )" );
		plot.setYLabel( "Variance ( ADU )" );

		JFreeChart chart = new JFreeChart( plot );
		chart.setBackgroundPaint( Color.white );
		chart.setTitle( title );
		chart.removeLegend();

		chartPanel = new ChartPanel( chart );
		chartPanel.setBackground( Color.white );

		plotPanel = new JPanel( new BorderLayout() );
		plotPanel.add( chartPanel, BorderLayout.CENTER );

		// Create a panel that will be used for the statistics info
		statsPanel = new JPanel();

		JPanel spacer = new JPanel();
		spacer.setPreferredSize( new java.awt.Dimension( 10, 5 ) );
		spacer.setBackground( Color.white );

		super.getContentPane().setLayout( new BorderLayout() );
		super.getContentPane().add( toolbar, BorderLayout.PAGE_START );
		super.getContentPane().add( plotPanel, BorderLayout.CENTER );
		super.getContentPane().add( spacer, BorderLayout.EAST );

		setPreferredSize( new java.awt.Dimension( 450, 640 ) );

		super.pack();
	}

	//--------------------------------------------------------------------------
    //  linearLeastSquaresFit
    //--------------------------------------------------------------------------
	//  Based on "y = A + Bx" from "An Introduction to Error Analysis".
	//  Gain is "1 / slope".
	//--------------------------------------------------------------------------
	public void linearLeastSquaresFit( Vector<Point2D.Float> xyData, float stdDev ) throws Exception
	{
		float sum   = 0.0f;
		float Xsum  = 0.0f;
		float Ysum  = 0.0f;
		float XYsum = 0.0f;
		float XXsum = 0.0f;

		A     		= 0.0f;
		B     		= 0.0f;

		Vector<Point2D.Float> newXYData = promptForMaxValue( xyData );

		// Sum all the data
		for ( int i=0; i<newXYData.size(); i++ )
		{
			Point2D.Float xyVal = newXYData.get( i );

			Xsum  += xyVal.x;
			Ysum  += xyVal.y;
			XYsum += xyVal.x * xyVal.y;
			XXsum += xyVal.x * xyVal.x;
		}
		sum = newXYData.size();

		A = ( XXsum * Ysum - Xsum * XYsum ) / ( sum * XXsum - Xsum * Xsum );
		B = ( sum * XYsum - Xsum * Ysum ) / ( sum * XXsum - Xsum * Xsum );

//		float[] xRange = plot.getXRange();
//		Point2D.Float[] points = new Point2D.Float[ 2 ];
//		points[ 0 ] = new Point2D.Float( 0, 0 );
//		points[ 1 ] = new Point2D.Float( xRange[ 1 ], ( float )( 0 + ( double )B * xRange[ 1 ] ) );

		float fXMax = 0.f;
		float fXMin = 1000000.f;

		for ( int i=0; i<xyData.size(); i++ )
		{
			fXMin = Math.min( xyData.get( i ).x, fXMin );
			fXMax = Math.max( xyData.get( i ).x, fXMax );
		}

//		Point2D.Float[] points = new Point2D.Float[ 2 ];
//		points[ 0 ] = new Point2D.Float( 0, 0 );
//		points[ 1 ] = new Point2D.Float( fXMax, ( float )( 0 + ( double )B * fXMax ) );

		Point2D.Float[] points = new Point2D.Float[ 2 ];
		points[ 0 ] = new Point2D.Float( fXMin, ( float )( A + ( double )B * fXMin ) );
		points[ 1 ] = new Point2D.Float( fXMax, ( float )( A + ( double )B * fXMax ) );

		// Make sure the line endpoint is positive. If not recalculate.
		if ( points[ 0 ].y < 0 )
		{
			points[ 0 ] = new Point2D.Float( ( float )( ( 0 - A ) / B ), 0 );
		}

		// Check that the coefficients A and B are valid numbers. If
		// not, then just draw a straight line along the X-axis.
		if ( Double.isNaN( A ) || Double.isNaN( B ) )
		{
			MainApp.error( "( PTCPlotFrame ): invalid LLS fit. A: " + A + " B: " + B );
			return;
		}

		// Prevent division by zero.
		if ( A == 0.0f )
		{
			MainApp.error( "( PTCPlotFrame ): invalid LLS fit. A: " + A + " B: " + B );
			return;
		}

		plot.setPlot( xyData.toArray( new Point2D.Float[ xyData.size() ] ), points );

		float gain = ( 1.0f / B );

		statsPanel = createStatisticsInfo( gain, stdDev );

		plotPanel.add( statsPanel, BorderLayout.SOUTH );
	}

	//--------------------------------------------------------------------------
    //  createStatisticsInfo
    //--------------------------------------------------------------------------
	//  Creates the statistics panel displayed at the bottom of the PTC plot.
	//--------------------------------------------------------------------------
	public JPanel createStatisticsInfo( float gain, float stdDev )
	{
		JPanel panel = new JPanel();
		panel.setBackground( Color.WHITE );
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );

		try
		{
			SimpleDateFormat dateFormatter = new SimpleDateFormat( "MMM dd, yyyy" );
			SimpleDateFormat timeFormatter = new SimpleDateFormat( "hh:mm a" );

			DecimalFormat df = new DecimalFormat( "##0.##E0" );
			panel.add( new JLabel( "  " + dateFormatter.format( new Date() ) + " | " + timeFormatter.format( new Date() ) ) );
			panel.add( new JLabel( "  Gain:    " + df.parse( df.format( gain ) ) + " e-/ADU" ) );
			panel.add( new JLabel( "  Std Dev: " + df.parse( df.format( stdDev ) ) + " ADU" ) );
			panel.add( new JLabel( "  Noise:   " + df.parse( df.format( gain * stdDev ) ) + " e-" ) );
		}
		catch ( ParseException e )
		{
			e.toString();
		}

		return panel;
	}

	//--------------------------------------------------------------------------
    //  promptForMaxValue
    //--------------------------------------------------------------------------
	//  Prompts the user for the maximum counts value using an input dialog.
	//--------------------------------------------------------------------------
	public Vector<Point2D.Float> promptForMaxValue( Vector<Point2D.Float> xyData )
	{
		Vector<Point2D.Float> newData = null;

		// Automatically find maximum data point before full well is reached
		float maxVariance = 0.0f;
		float maxCount    = 0.0f;

		for ( int i=0; i<xyData.size(); i++ )
		{
			Point2D.Float xyVal = xyData.get( i );

			if ( xyVal.getX() > maxCount && xyVal.getY() > maxVariance )
			{
				maxCount = ( float )xyVal.getX();
				maxVariance = ( float )xyVal.getY();
			}
		}

		String[] labels = { "Enter maximum count value" };
		String[] values = { Integer.toString( ( int )maxCount ) };

		OwlInputDialog maxDialog =
				new OwlInputDialog( false, OwlInputDialog.OK, "PTC2.gif" );

		int status = maxDialog.showDialog( this,
										   "PTC",
										   labels,
										   values );

		if ( status == OwlInputDialog.OK )
		{
			values = maxDialog.getInputValues();
	
			if ( values[ 0 ] != null )
			{
				try
				{
					newData  = new Vector<Point2D.Float>();
					maxCount = Float.parseFloat( values[ 0 ] );

					// Remove counts greater than maximum
					// for linear least squares.
					//
					for ( int i=0; i<xyData.size(); i++ )
					{
						Point2D.Float xyVal = xyData.get( i );
	
						if ( xyVal.getX() <= maxCount )
							newData.add( xyVal );
					}
				}
				catch ( NumberFormatException nfe )
				{
					System.err.println(
							"Failed to correctly parse maximum count value ( " +
							 values[ 0 ] +
							 " )" );
				}
			}
		}
		else
		{
			newData  = new Vector<Point2D.Float>( xyData );
		}

		return newData;
	}

	//--------------------------------------------------------------------------
    //  actionPerformed
    //--------------------------------------------------------------------------
	//  Handles button and menu events.
	//--------------------------------------------------------------------------
	public void actionPerformed( ActionEvent event )
	{
		if ( event.getActionCommand().equals( PRINT_ACTION ) )
		{
			( new PrintUtilities() ).printComponent( plotPanel );
		}

		else if ( event.getActionCommand().equals( CLOSE_ACTION ) )
		{
			super.dispose();
		}
	}
}
