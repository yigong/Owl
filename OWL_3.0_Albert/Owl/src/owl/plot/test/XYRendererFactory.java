package owl.plot.test;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer ;


public class XYRendererFactory extends XYLineAndShapeRenderer
{
	private static final long serialVersionUID = 7908183767180436521L;


	public static XYLineAndShapeRenderer getCrossRenderer( int dSize )
	{
		double n = dSize / 2.0 * ( -1 );
		double p = dSize / 2.0;

		int[] polyX = { 0, 0, 0, ( int )n, ( int )p, 0, 0 };
		int[] polyY = { ( int )n, ( int )p, 0, 0, 0, 0, ( int )n };

		XYLineAndShapeRenderer r = new XYLineAndShapeRenderer( false, true );

		r.setSeriesShape( 0, new java.awt.Polygon( polyX, polyY, polyX.length ) );

		return r;
	}

	public static XYLineAndShapeRenderer getDotRenderer( int dSize )
	{
		double n = dSize / 2.0 * ( -1 );

		XYLineAndShapeRenderer r = new XYLineAndShapeRenderer( false, true );

		r.setSeriesShape( 0, new java.awt.geom.Ellipse2D.Double( n, n, dSize, dSize ) );

		return r;
	}
}
