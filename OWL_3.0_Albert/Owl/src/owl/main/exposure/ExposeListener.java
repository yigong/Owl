package owl.main.exposure;

import java.util.EventListener;

public interface ExposeListener extends EventListener
{
	public void readoutColorChanged( ExposeEvent event );
	public void minMaxPixelsChanged( ExposeEvent event );
	public void elapsedTimeChanged( ExposeEvent event );
	public void pixelCountChanged( ExposeEvent event );
	public void frameCountChanged( ExposeEvent event );
	public void exposureComplete( ExposeEvent event );
}
