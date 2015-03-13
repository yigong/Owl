package owl.main.debug.debugframe;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.border.Border;



public class DebugRunnablePanel extends DebugPanel implements Runnable
{
	private static final long serialVersionUID = -4953979377633349237L;

	protected boolean stop;

	public DebugRunnablePanel()
	{
		Border loweredBevel =
					BorderFactory.createLoweredBevelBorder();

		setBorder( loweredBevel );
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
	}

	public void stop() { stop = true; }
	public void run() {}
}
