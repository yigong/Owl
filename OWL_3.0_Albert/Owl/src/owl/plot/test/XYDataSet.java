package owl.plot.test;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Vector;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;


public class XYDataSet implements org.jfree.data.xy.XYDataset
{
	private Vector<Point2D.Float>	m_points;
	private DatasetGroup			m_dataSetGrp;
	private String					m_seriesKey;


	public XYDataSet( String seriesKey )
	{
		m_points = new Vector<Point2D.Float>();

		m_dataSetGrp = new DatasetGroup( "Row Data" );
		m_seriesKey  = seriesKey;
	}

	public XYDataSet( Point2D.Float[] dataSet, String seriesKey )
	{
		m_points = new Vector<Point2D.Float>( Arrays.asList( dataSet ) );

		m_dataSetGrp = new DatasetGroup( "Row Data" );
		m_seriesKey  = seriesKey;
	}

	public void addPoint( Point2D.Float dataPoint )
	{
		m_points.add( dataPoint );
	}

	public void clear()
	{
		m_points.clear();
	}

	@Override
	public DomainOrder getDomainOrder()
	{
		return DomainOrder.ASCENDING;
	}

	@Override
	public int getItemCount( int series )
	{
		return ( m_points == null ? 0 : m_points.size() );
	}

	@Override
	public Number getX( int series, int item )
	{
		if ( item < m_points.size() )
		{
			return ( new Float( m_points.get( item ).getX() ) );
		}

		return 0;
	}

	@Override
	public double getXValue( int series, int item )
	{
		return getX( series, item ).doubleValue();
	}

	@Override
	public Number getY( int series, int item )
	{
		if ( item < m_points.size() )
		{
			return ( new Float( m_points.get( item ).getY() ) );
		}

		return 0;
	}

	@Override
	public double getYValue( int series, int item )
	{
		return getY( series, item ).doubleValue();
	}

	@Override
	public int getSeriesCount()
	{
		return 1;
	}

	@Override
	public Comparable getSeriesKey( int series )
	{
		return ( new String( ( m_seriesKey != null ? m_seriesKey : "" ) + " pixel data" ) );
	}

	@Override
	public int indexOf( Comparable seriesKey )
	{
		return 0;
	}

	@Override
	public void addChangeListener( DatasetChangeListener listener )
	{
	}

	@Override
	public DatasetGroup getGroup()
	{
		return m_dataSetGrp;
	}

	@Override
	public void removeChangeListener( DatasetChangeListener listener )
	{
	}

	@Override
	public void setGroup( DatasetGroup dataSetGrp )
	{
		m_dataSetGrp = dataSetGrp;
	}
}
