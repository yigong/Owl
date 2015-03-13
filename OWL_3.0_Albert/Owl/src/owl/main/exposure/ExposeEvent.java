package owl.main.exposure;

import java.awt.Color;
import java.util.EventObject;


public class ExposeEvent extends EventObject
{
	private static final long serialVersionUID = 8961159959385359915L;

	public Color color;
	public int   elapsedTime;
	public int   minPixelCount;
	public int   maxPixelCount;
	public int   pixelCount;
	public int   frameCount;

	public ExposeEvent( Object source )
	{
		super( source );
		setToZero();
	}

	//  Parameter passed as a float for overloading
	// +--------------------------------------------+
	public ExposeEvent( Object source, float aFloat )
	{
		super( source );
		setToZero();
		elapsedTime = ( int )aFloat;
	}

	public ExposeEvent( Object source, int aNum )
	{
		super( source );
		setToZero();
		pixelCount = aNum;
	}

	public ExposeEvent( Object source, long frame )
	{
		super( source );
		setToZero();
		frameCount = ( int )frame;
	}

	public ExposeEvent( Object source, int min, int max )
	{
		super( source );
		setToZero();
		minPixelCount = min;
		maxPixelCount = max;
	}

	public ExposeEvent( Object source, Color aColor )
	{
		super( source );
		setToZero();
		color = aColor;
	}

	private void setToZero()
	{
		color         = Color.BLACK;
		elapsedTime   = 0;
		minPixelCount = 0;
		maxPixelCount = 0;
		pixelCount    = 0;
		frameCount    = 0;
	}
}
