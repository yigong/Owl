package owl.main.setup;

import java.util.EventListener;

public interface SetupListener extends EventListener
{
	public void setupChanged( SetupEvent event );
}
