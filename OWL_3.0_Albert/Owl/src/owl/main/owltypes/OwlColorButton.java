package owl.main.owltypes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JColorChooser;


public class OwlColorButton extends JButton
{
	private static final long serialVersionUID = 297327274480252043L;
	private Color  m_color;

	public OwlColorButton( Color color )
	{
		super();
		super.setFocusPainted( false );
		super.setPreferredSize( new Dimension( 18, 18 ) );

		m_color = color;
	}

	public Color getColor()
	{
		return m_color;
	}

	public void setColor( Color color )
	{
		m_color = color;
	}

	public void paintComponent( Graphics g )
	{
		super.paintComponent( g );

		g.setColor( m_color );
		g.fillRect( 2, 2, 13, 13 );

		g.setColor( Color.black );
		g.drawRect( 2, 2, 13, 13 );
	}

	public Color chooseColor()
	{
		Color newColor = JColorChooser.showDialog( this,
												   "Select Color",
													m_color );

		if ( newColor != null )
		{
			m_color = newColor;
			repaint();
		}

		return m_color;
	}
}
