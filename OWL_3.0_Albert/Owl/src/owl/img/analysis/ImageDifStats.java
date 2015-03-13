package owl.img.analysis;

public class ImageDifStats extends Object
{
	public ImageStats img1Stats;
	public ImageStats img2Stats;
	public ImageStats imgDifStats;

	public ImageDifStats()
	{
		img1Stats   = new ImageStats();
		img2Stats   = new ImageStats();
		imgDifStats = new ImageStats();
	}
}
