package owl.main.exposure;

import java.awt.Color;
import java.util.EventObject;

public class ReadoutEvent extends EventObject
{
	private static final long serialVersionUID = 5017653548917790303L;

	public Color color;
	public int elapsedTime;
	public int minPixelCount;
	public int maxPixelCount;
	public int pixelCount;
	public int frameCount;

	//  Passed as a float for overloading
	// +--------------------------------------------+
	public ReadoutEvent( Object source, float aFloat )
	{
		super( source );
		elapsedTime = ( int )aFloat;
	}

	public ReadoutEvent( Object source, int aNum )
	{
		super( source );
		pixelCount = aNum;
	}

	public ReadoutEvent( Object source, long frame )
	{
		super( source );
		frameCount = ( int )frame;
	}

	public ReadoutEvent( Object source, int min, int max )
	{
		super( source );
		minPixelCount = min;
		maxPixelCount = max;
	}

	public ReadoutEvent( Object source, Color aColor )
	{
		super( source );
		color = aColor;
	}
}
