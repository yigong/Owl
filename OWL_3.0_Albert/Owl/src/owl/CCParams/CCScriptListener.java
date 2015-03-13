package owl.CCParams;

import java.util.EventListener;

public interface CCScriptListener extends EventListener
{
	public void CCScriptChanged( CCScriptEvent event );
}
