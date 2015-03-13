package owl.main.owltypes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.ImageIcon;


public class OwlLabledIcon extends ImageIcon
{
	private static final long serialVersionUID = -4381849152555286851L;
	private String m_sLabel;

	public OwlLabledIcon( String filename, String label )
	{
		this( filename, label, label );
	}

	public OwlLabledIcon( String filename, String label, String description )
	{
		super( filename, description );

		m_sLabel = label;
	}

	public void paintIcon( Component c, Graphics g, int x, int y )
	{
		Color oldColor = g.getColor();
		Font  oldFont  = g.getFont();

		super.paintIcon( c, g, x, y );

		g.setFont( new Font( Font.SANS_SERIF, Font.PLAIN, 10 ) );

		FontMetrics fm = g.getFontMetrics();
		int dX = ( ( c == null ? getIconWidth() : c.getSize().width ) - fm.stringWidth( m_sLabel ) ) / 2;
		int dY = getIconHeight();

		g.setColor( Color.BLACK );
		g.drawString( m_sLabel, dX, dY );
		g.setColor( oldColor );

		g.setFont( oldFont );
	}
}
