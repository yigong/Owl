package owl.gui.utils;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.RepaintManager;
import owl.main.MainApp;

/** A simple utility class that lets you very simply print
* an arbitrary component. Just pass the component to the
* PrintUtilities.printComponent. The component you want to
* print doesn't need a print method and doesn't have to
* implement any interface or do anything special at all.
* 
* If you are going to be printing many times, it is marginally more 
* efficient to first do the following:
* 
*    PrintUtilities printHelper = new PrintUtilities( theComponent );
*  
* then later do printHelper.print(). But this is a very tiny
* difference, so in most cases just do the simpler
* PrintUtilities.printComponent( componentToBePrinted )
*
* 7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/ 
* May be freely used or adapted.
*/

public class PrintUtilities implements Printable
{
	private Component componentToBePrinted;

	public void printComponent( Component c )
	{
		componentToBePrinted = c;

		print();
	}

	public void print()
	{
		PrinterJob printJob = PrinterJob.getPrinterJob();

		PageFormat pf1 = new PageFormat();
		printJob.setPrintable( this, pf1 );

		if ( printJob.printDialog() )
		{
			try
			{
				printJob.print();
			}
			catch( PrinterException pe )
			{
				MainApp.error( "Error printing: " + pe );
			}
		}
	}

	public int print( Graphics g, PageFormat pageFormat, int pageIndex )
	{
		if ( pageIndex > 0 )
		{
			return( Printable.NO_SUCH_PAGE );
		}
		else
		{
			Graphics2D g2d = ( Graphics2D )g;
			g2d.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
			disableDoubleBuffering( componentToBePrinted );
			componentToBePrinted.paint( g2d );
			enableDoubleBuffering( componentToBePrinted );
			return( Printable.PAGE_EXISTS );
		}
	}

	/** The speed and quality of printing suffers dramatically if
	* any of the containers have double buffering turned on.
	* So this turns if off globally.
	* @see enableDoubleBuffering
	*/
	public void disableDoubleBuffering( Component c )
	{
		RepaintManager currentManager = RepaintManager.currentManager( c );
		currentManager.setDoubleBufferingEnabled( false );
	}

	/** Re-enables double buffering globally. */
	public void enableDoubleBuffering( Component c )
	{
		RepaintManager currentManager = RepaintManager.currentManager( c );
		currentManager.setDoubleBufferingEnabled( true );
	}
}
