package owl.plot.test;

import java.awt.Font;
import java.awt.geom.Point2D;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;


public class XYPlot extends org.jfree.chart.plot.XYPlot
{
	private static final long serialVersionUID = -2843674362332080193L;


	public XYPlot()
	{
		super( null, null, null, new StandardXYItemRenderer() );

		super.setDomainCrosshairVisible( false );
		super.setRangeCrosshairVisible( false );

		super.setNoDataMessageFont( new Font( Font.SANS_SERIF, Font.PLAIN, 24 ) );
		super.setNoDataMessagePaint( java.awt.Color.LIGHT_GRAY );
		super.setNoDataMessage( "No Data Available" );
	}

	public void setPlot( Point2D.Float[] points, String seriesKey ) throws Exception
	{
		if ( points == null )
		{
			throw new Exception( "( XYPlot ): Invalid point data ( null )!" );
		}

		setDataset( new XYDataSet( points, seriesKey ) );
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

	public void crosshairOverlayEnabled( boolean onOff )
	{
		if ( onOff )
		{
			setDomainCrosshairVisible( true );
			setRangeCrosshairVisible( true );
		}
		else
		{
			setDomainCrosshairVisible( false );
			setRangeCrosshairVisible( false );
		}

		super.fireChangeEvent();
	}
}
