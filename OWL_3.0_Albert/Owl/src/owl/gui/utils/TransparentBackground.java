package owl.gui.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JWindow;

//
// NOTE: See http://www.onjava.com/pub/a/onjava/excerpt/swinghks_hack41/index.html?page=1
// for details
//
public class TransparentBackground extends JComponent
implements ComponentListener, WindowFocusListener, Runnable
{
	private static final long serialVersionUID = 7086360132635269883L;

	public boolean refreshRequested;
	private JWindow frame;
	private Image background;
	private long lastupdate;

	public TransparentBackground( JWindow frame )
	{
		lastupdate = 0;
		refreshRequested = true;

		this.frame = frame;
		updateBackground();

		frame.addComponentListener( this ); 
		frame.addWindowFocusListener( this );
		new Thread( this ).start();
	}

	public void updateBackground()
	{
		try
		{
			Robot rbt = new Robot();
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension dim = tk.getScreenSize();
			background = rbt.createScreenCapture( new Rectangle(0,0,(int)dim.getWidth( ), (int)dim.getHeight( )));
		}
		catch ( Exception ex )
		{
			ex.printStackTrace( );
		}
	}

	public void paintComponent( Graphics g )
	{
		Point pos = this.getLocationOnScreen();
		Point offset = new Point( -pos.x, -pos.y );
		g.drawImage( background, offset.x, offset.y, null );
	}

	public void refresh()
	{
		if( frame.isVisible() )
		{
			repaint();
			refreshRequested = true;
			lastupdate = new Date().getTime();
		}
	}

	public void run()
	{
		Thread.currentThread().setName( "Owl - TransparentBackground" );

		try
		{
			while( true )
			{
				Thread.sleep( 250 );
				long now = new Date().getTime();

				if( refreshRequested && ( ( now - lastupdate ) > 1000 ) )
				{
					if( frame.isVisible() )
					{
						Point location = frame.getLocation();
						frame.setVisible( false );
						updateBackground();
						frame.setVisible( true );
						frame.setLocation( location );
						refresh();
					}

					lastupdate = now;
					refreshRequested = false;
				}
			}
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
		} 
	}

	// Component listener methods
	public void componentResized(ComponentEvent arg0) { repaint(); }
	public void componentMoved(ComponentEvent arg0)   { repaint(); }
	public void componentShown(ComponentEvent arg0)   { repaint(); }
	public void componentHidden(ComponentEvent arg0)  {}

	// Window listener methods
	public void windowGainedFocus(WindowEvent arg0) { refresh(); }
	public void windowLostFocus(WindowEvent arg0)   { refresh(); }
}
