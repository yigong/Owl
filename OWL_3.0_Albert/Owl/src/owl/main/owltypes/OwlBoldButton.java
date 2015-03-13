package owl.main.owltypes;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;


// +---------------------------------------------------------------------+
// |  Class OwlBoldButton                                                |
// +---------------------------------------------------------------------+
// |  This class is used to create a bold labeled JButton. The text also |
// |  can be switched between black and a user specified color, or it    |
// |  can constantly remain black.                                       |
// +---------------------------------------------------------------------+
public class OwlBoldButton extends JButton
{
	private static final long serialVersionUID = -5328148657887620394L;

	private Color color;

	// |  Constructor
	// +--------------------------------------------------------
	public OwlBoldButton( String text )
	{
		super( text );

		setFont( new Font( getFont().getFontName(),
				 Font.BOLD, getFont().getSize() ) );

		color = Color.BLACK;
	}

	// |  Constructor
	// +--------------------------------------------------------
	public OwlBoldButton( String text, Color color )
	{
		super( text );

		setFont( new Font( getFont().getFontName(),
				 Font.BOLD, getFont().getSize() ) );

		this.color = color;
	}

	// |  setBlack
	// +--------------------------------------------------------
	// |  Sets the buttons text color to black.
	// ---------------------------------------------------------
	public void setBlack()
	{
		setForeground( Color.BLACK );
	}

	// |  setBlack
	// +--------------------------------------------------------
	// | Sets the buttons text color to black and sets the
	// | buttons text and action command to the specified text.
	// ---------------------------------------------------------
	public void setBlack( String text )
	{
		setBlack();
		setActionCommand( text );
		setText( text );
	}

	// |  setColored
	// +--------------------------------------------------------
	// | Sets the button's text color to user specified value.
	// ---------------------------------------------------------
	public void setColored()
	{
		setForeground( color );
	}

	// |  setColored
	// +--------------------------------------------------------
	// | Sets the buttons text color to user specified value
	// | and sets the buttons text and action command to the
	// | specified text.
	// ---------------------------------------------------------
	public void setColored( String text )
	{
		setColored();
		setActionCommand( text );
		setText( text );
	}
}
