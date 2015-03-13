package owl.gui.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import owl.main.MainApp;
import owl.main.owltypes.OwlSplashWindow;


public class SplashScreen extends OwlSplashWindow
{
	private static final long serialVersionUID = 878831761772872636L;
	private static final String SPLASH_BMP	  = MainApp.getBitmapPath() + "Splash6.png";
	private static final String SOUND_FILE	  = MainApp.getSoundPath() + "GreatHornedOwl1.wav";
	private static final String STATUS_PREFIX = "  Astronomical Research Cameras, Inc - Status:  ";

	public SplashScreen( boolean playAudio )
	{
		super();

		if ( playAudio ) setAudioFile( SOUND_FILE );

		JLabel splashLabel = new JLabel( new javax.swing.ImageIcon( SPLASH_BMP ) );
		statusLabel = new JLabel( STATUS_PREFIX );
		statusLabel.setForeground( new java.awt.Color( 150, 150, 150 ) );
		statusLabel.setBackground( java.awt.Color.BLACK );

		getContentPane().setLayout( new BoxLayout( getContentPane(), BoxLayout.Y_AXIS ) );
		getContentPane().setBackground( Color.BLACK );
		getContentPane().add( splashLabel );
		getContentPane().add( statusLabel );

		pack();

		// Center the window on the screen.
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( d.width/4, d.height/4 );
	}

	public SplashScreen()
	{
		super();
	}

	@Override
	public void setStatus( String text )
	{
		super.setStatus( STATUS_PREFIX + text );
	}
}
