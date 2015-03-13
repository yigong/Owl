// +-------------------------------------------------------------------------+
// |  Readout Progress Window                                                |
// |                                                                         |
// |  This class will allow the instantiation of a readout progress window.  |
// |  To test this window independent of real data, call the "test" method.  |
// |                                                                         |
// |  Example script to use this script:                                     |
// |                                                                         |
// |  pw = ProgressWindow( 0, 10 );                                          |
// |  pw.show();                                                             |
// |  pw.setReadoutValue( 0 );                                               |
// |  pw.setReadoutValue( 5 );                                               |
// |  pw.setReadoutValue( 10 );                                              |
// |  pw.close();                                                            |
// |                                                                         |
// |  Example script to test this script:                                    |
// |                                                                         |
// |  pw = ProgressWindow( 0, 10 );                                          |
// |  pw.show();                                                             |
// |  pw.test();                                                             |
// |  pw.close();                                                            |
// +-------------------------------------------------------------------------+
package owl.gui.utils;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import owl.main.MainApp;


public class ProgressWindow extends JFrame
{
	private static final long serialVersionUID = 7855763513094248777L;

	JProgressBar progressBar;
	JLabel elapsedTimeTextLabel;
	JLabel elapsedTimeLabel;

	public ProgressWindow( int min, int max )
	{
		super( "Readout Progress" );

		super.setIconImage(
				( new ImageIcon(
						MainApp.getBitmapPath() +
						"BookOpen.gif" ) ).getImage() );

		JPanel panel = new JPanel();

		elapsedTimeTextLabel = new JLabel( "Elapsed Time (sec): " );
		elapsedTimeLabel = new JLabel( "0" );
		panel.add( elapsedTimeTextLabel );
		panel.add( elapsedTimeLabel );

		progressBar = new JProgressBar( min, max );
		progressBar.setForeground( new Color( 110, 4, 9 ) );
		panel.add( progressBar );

		getContentPane().add( panel );
		pack();

		owl.gui.utils.OwlUtilities.centerFrame( this );
	}

	public void setNewMax( int max )
	{
		progressBar.setMaximum( max );
	}

	public void setReadoutValue( int value )
	{
		progressBar.setValue( value );
	}

	public void setElapsedTime( int value )
	{
		elapsedTimeLabel.setText( Integer.toString( value ) );
	}

	public void showElapsedTime( boolean yesNo )
	{
		elapsedTimeTextLabel.setVisible( yesNo );
		elapsedTimeLabel.setVisible( yesNo );
	}

	public void setCustomLabel( String text, boolean showsValue )
	{
		if ( text != null )
		{
			elapsedTimeTextLabel.setText( text );
		}
		if ( !showsValue ) elapsedTimeLabel.setVisible( false );

		pack();
	}

	public void setCustomTitle( String text )
	{
		setTitle( text );
	}

	public void setIcon( String file )
	{
		setIconImage( Toolkit.getDefaultToolkit().createImage( file ) );
	}

	public void close()
	{
		WindowEvent we = new WindowEvent( this, WindowEvent.WINDOW_CLOSING );
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent( we );
	}

	public void test()
	{
		setReadoutValue( progressBar.getMinimum() );

		for ( int i=12; i>=0; i-- )
		{
			setElapsedTime( i );

			try {
				Thread.sleep( 1000 );
			} catch ( InterruptedException e ) {}
		}

		for ( int i=0; i<=progressBar.getMaximum(); i++ )
			setReadoutValue( i );
	}
}
