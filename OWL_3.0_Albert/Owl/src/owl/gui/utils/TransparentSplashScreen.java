package owl.gui.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import owl.main.MainApp;


public class TransparentSplashScreen extends SplashScreen
{
	private static final long serialVersionUID = -7007358696326806410L;
	private static final String SPLASH_BMP	  = MainApp.getBitmapPath() + "Splash3.gif";
	private static final String SOUND_FILE	  = MainApp.getSoundPath() + "GreatHornedOwl1.wav";

	public TransparentSplashScreen( boolean playAudio )
	{
		super();

		if ( playAudio ) setAudioFile( SOUND_FILE );

		JLabel splashLabel = new JLabel( new javax.swing.ImageIcon( SPLASH_BMP ) );

		TransparentBackground backgnd = new TransparentBackground( this );
		backgnd.setLayout( new BorderLayout() );
		backgnd.add( BorderLayout.CENTER, splashLabel );

		getContentPane().setLayout( new BoxLayout( getContentPane(), BoxLayout.Y_AXIS ) );
		getContentPane().setBackground( Color.BLACK );
		getContentPane().add( backgnd );

		pack();

		// Center the window on the screen.
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( d.width/4, d.height/6 );
	}

	@Override
	public void setStatus( String text )
	{
	}
}
