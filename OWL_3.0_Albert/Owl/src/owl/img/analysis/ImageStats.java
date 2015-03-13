package owl.img.analysis;

public class ImageStats extends Object
{
	public double gMin;
	public double gMax;
	public double gMean;
	public double gVariance;
	public double gStdDev;
	public double gSaturatedPixCnt;

	public ImageStats()
	{
		gMin             = 0.0;
		gMax             = 0.0;
		gMean            = 0.0;
		gVariance        = 0.0;
		gStdDev			 = 0.0;
		gSaturatedPixCnt = 0.0;
	}
}
