package owl.gui.popupmenus;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.WindowConstants;


public class ScriptPopupDialog extends JDialog implements MouseListener, WindowFocusListener
{
	private static final long serialVersionUID = -5029068342344253495L;

	public ScriptPopupDialog( JComponent aComponent )
	{
		super();
		super.setIconImage( owl.main.MainApp.getProgramIcon() );

		setLayout( new BoxLayout( getContentPane(), BoxLayout.Y_AXIS ) );
		addMouseListener( this );
		addWindowFocusListener( this );

		Component[] comps = aComponent.getComponents();
		for ( int i=0; i<comps.length; i++ )
			super.getContentPane().add( comps[ i ] );

		pack();
		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
	}

	public void show( int x, int y )
	{
		setLocation( x, y );
		setVisible( true );
		validate();
	}

	@Override
	public void mouseClicked( MouseEvent e ) { processEvent( e ); }

	@Override
	public void mousePressed( MouseEvent e ) { processEvent( e ); }

	@Override
	public void mouseReleased( MouseEvent e ) {}

	@Override
	public void mouseEntered( MouseEvent e ) {}

	@Override
	public void mouseExited( MouseEvent e ) {}

	private void processEvent( MouseEvent e )
	{
		( ( JDialog )e.getComponent() ).dispose();
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0)
	{
	}

	@Override
	public void windowLostFocus(WindowEvent arg0)
	{
		dispose();
	}
}
