package owl.main.device;

import java.util.EventListener;

public interface DeviceListener extends EventListener
{
	public void deviceChanged( DeviceEvent event );
}
