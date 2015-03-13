package owl.main.exposure;

import java.util.EventListener;

public interface ReadoutListener extends EventListener
{
	public void readoutColorChanged( ReadoutEvent event );
	public void minMaxPixelsChanged( ReadoutEvent event );
	public void elapsedTimeChanged( ReadoutEvent event );
	public void pixelCountChanged( ReadoutEvent event );
}
