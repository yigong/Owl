// +--------------------------------------------------------------------------------+
// |  OwlMessageBox                                                                 |
// +--------------------------------------------------------------------------------+
// |  NOTES:  This class no longer subclasses JWindow; instead it now subclasses    |
// |          JDialog, which allows the automatic blocking of all other windows     |
// |          to allow the underlying task to complete unimpeded.                   |
// +--------------------------------------------------------------------------------+
package owl.main.owltypes;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Timer;



public class OwlMessageBox extends JDialog implements Runnable, ActionListener
{
	private static final long serialVersionUID = 672920963759427198L;
	private JLabel  txtLabel;
	private JLabel  picLabel;
	private Timer   timer;
	private boolean stop;

	private Thread  thread;

	public OwlMessageBox( String msg )
	{
		this( msg, null );
	}

	public OwlMessageBox( String msg, Icon icon )
	{
		this( msg, icon, 10000 );
	}

	public OwlMessageBox( String msg, Icon icon, int timeout_ms )
	{
		super();

		setAlwaysOnTop( true );
		setUndecorated( true );
		setModalityType( Dialog.ModalityType.APPLICATION_MODAL );

		// Create a panel for the message and icon
		JPanel panel = new JPanel();
		panel.setLayout( new FlowLayout() );
		panel.setBackground( Color.WHITE );

		// Set the window's background color to create a border
		getContentPane().setLayout( new FlowLayout( FlowLayout.CENTER, 5, 5 ) );
		getContentPane().setBackground( Color.RED );

		// Add any existing icon to the panel
		if ( icon != null )
		{
			picLabel = new JLabel( icon );
			panel.add( picLabel );
		}

		// Add the message to the panel
		txtLabel = new JLabel( msg + " " );
		panel.add( txtLabel );

		// Add the panel to the window
		getContentPane().add( panel );

		pack();
		centerFrame();

		timer = new Timer( timeout_ms, this );

		thread = null;
	}

	public Thread start()
	{
		stop = false;

		if ( thread == null )
		{
			thread = new Thread( this );
			thread.setName( "Owl - MessageBox" );
		}

		timer.start();
		thread.start();

		return thread;
	}

	public void run()
	{
		setVisible( true );

		while ( !stop )
		{
			try
			{
				Thread.sleep( 1 );
			}
			catch ( InterruptedException ie ) {}
		}
	}

	public void stop()
	{
		stop = true;

		timer.stop();
		dispose();
	}

	public void setText( String text )
	{
		txtLabel.setText( text );

		validate();
		pack();
	}

	public void setText( String msg, Icon icon )
	{
		picLabel.setIcon( icon );
		txtLabel.setText( msg );

		validate();
		pack();
	}

	public void actionPerformed( ActionEvent ae )
	{
		stop();
	}

	private void centerFrame()
	{
		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int xPos = d.width / 2 - getWidth() / 2;
		int yPos = d.height / 4  - getHeight() / 2;
		setLocation( xPos, yPos );
	}
}
