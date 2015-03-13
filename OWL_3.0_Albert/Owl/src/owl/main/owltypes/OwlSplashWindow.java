package owl.main.owltypes;

import javax.swing.JWindow;
import javax.swing.JLabel;
import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;


public class OwlSplashWindow extends JWindow implements Runnable
{
	private static final long serialVersionUID = -4314899245696761006L;

	protected JLabel statusLabel;
	protected boolean stop;
	private AudioClip audio;

	public OwlSplashWindow()
	{
		super();

		statusLabel = null;
		audio       = null;
	}

	public void setAudioFile( String soundFilename )
	{
		try
		{
			File file = new File( soundFilename );
			audio = Applet.newAudioClip( file.toURI().toURL() );
		}
		catch ( java.net.MalformedURLException murle ) {}
	}

	public void start()
	{
		stop = false;
		new Thread( this ).start();
	}

	public void stop()
	{
		stop = true;
		dispose();
	}

	public void setStatus( String text )
	{
		if ( statusLabel != null )
		{
			statusLabel.setText( text );
		}
	}

	public void run()
	{
		Thread.currentThread().setName( "Owl - Owl Splash Window" );

		setVisible( true );

		if ( audio != null ) audio.play();

		while ( !stop )
		{
			try {
				Thread.sleep( 1 );
			} catch ( InterruptedException ie ) {}
		}
	}
}
