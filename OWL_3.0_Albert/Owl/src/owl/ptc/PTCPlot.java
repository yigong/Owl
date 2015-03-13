package owl.ptc;

import java.awt.Font;
import java.awt.geom.Point2D;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import owl.plot.test.XYDataSet;
import owl.plot.test.XYRendererFactory;


public class PTCPlot extends XYPlot
{
	private static final long serialVersionUID = -8879084420255145995L;


	public PTCPlot()
	{
		super( null, null, null, null );

		org.jfree.chart.renderer.xy.XYLineAndShapeRenderer lineRenderer =
						new org.jfree.chart.renderer.xy.XYLineAndShapeRenderer( true, false );

		XYItemRenderer[] renderers = { XYRendererFactory.getCrossRenderer( 11 ), lineRenderer };

		super.setRenderers( renderers );

		super.setDomainCrosshairVisible( false );
		super.setRangeCrosshairVisible( false );

		super.setNoDataMessageFont( new Font( Font.SANS_SERIF, Font.BOLD, 16 ) );
		super.setNoDataMessage( "I Know You're Plotting Against Me!!!!" );
	}

	public void setPlot( Point2D.Float[] pointData, Point2D.Float[] LLSDData ) throws Exception
	{
		if ( pointData == null )
		{
			throw new Exception( "( PTCPlot ): Invalid point data ( null )!" );
		}

		if ( LLSDData == null )
		{
			throw new Exception( "( PTCPlot ): Invalid line data ( null )!" );
		}

		try
		{
			super.setDataset( 0, new XYDataSet( pointData, "PTC Data" ) );
			super.setDataset( 1, new XYDataSet( LLSDData, "Linear Least Squares Data" ) );
		}
		catch ( Exception e ) { System.err.println( e.getMessage() ); }
	}

	public void setXLabel( String text )
	{
		ValueAxis xAxis = new NumberAxis( text );

		Font font = xAxis.getLabelFont();
		xAxis.setLabelFont( new Font( font.getFamily(), Font.BOLD, font.getSize() ) );

		setDomainAxis( xAxis );
	}

	public void setYLabel( String text )
	{
		ValueAxis yAxis = new NumberAxis( text );

		Font font = yAxis.getLabelFont();
		yAxis.setLabelFont( new Font( font.getFamily(), Font.BOLD, font.getSize() ) );

		setRangeAxis( yAxis );
	}
}
