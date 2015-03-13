package owl.main.exposure;

import java.util.Vector;

import owl.main.fits.FitsHeaderFrame;


public interface ExposeInfo
{
	public static Vector<ExposeListener> exposeListeners = new Vector<ExposeListener>();

	public void setElapsedTime( int dElapsedTime );
	public void setPixelRange( int min, int max );
	public void setPixelCount( int dPixelCount );
	public void setReadoutTime( double gTime );
	public void setFrameCount( long frame );
	public void setFilename( String sFilename );

	public void randomPixelCountColor( java.awt.Color color );  // null => random
	public void incrementFilename();
	public FitsHeaderFrame getFitsHeaderFrame();

	public boolean isBeep();
	public boolean isDelay();
	public boolean isSubtract();
	public boolean isDisplay();
	public boolean isSaveToDisk();
	public boolean isSynthImage();
	public boolean isOpenShutter();
	public boolean isMultipleExposure();
	public boolean isFillBuffer();
	public double  getExposeTime() throws Exception;
	public double  getDelay();
	public int     getBufferFill();
	public int     getMultipleExposureCount() throws Exception;
	public int     getDeinterlaceAlgorithm();
	public String  getDeinterlaceDescription();
	public String  getFileExtension();
	public String  getFilename();
}
