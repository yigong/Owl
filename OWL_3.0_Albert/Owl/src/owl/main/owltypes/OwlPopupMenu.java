package owl.main.owltypes;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;


public class OwlPopupMenu extends JPopupMenu
{
	private static final long serialVersionUID = 2232529108579175370L;

	private final Color BKGD_COLOR = Color.RED.darker().darker();
	private final Color LINE_COLOR = Color.LIGHT_GRAY;
	private final Color ITEM_COLOR = Color.WHITE;

	public OwlPopupMenu()
	{
		setBackground( BKGD_COLOR );
		setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
	}

	public OwlPopupMenu( String title )
	{
		this();

		setTitle( title );
	}

	public void setTitle( String title )
	{
		title = "<html><b>" + title + "</b></html>";

		JLabel item = ( JLabel )super.add( new JLabel( title ) );

		item.setHorizontalAlignment( SwingConstants.CENTER );
		item.setBackground( BKGD_COLOR );
		item.setForeground( ITEM_COLOR );
	}

	public JMenuItem add( Action a )
	{
		JMenuItem item = super.add( a );

		item.setBorder( javax.swing.BorderFactory.createMatteBorder( 0, 0, 1, 0, LINE_COLOR ) );
		item.setBackground( ITEM_COLOR );

		return item;
	}

	public JMenuItem add( JMenuItem menuItem )
	{
		JMenuItem item = super.add( menuItem );

		item.setBorder( javax.swing.BorderFactory.createMatteBorder( 0, 0, 1, 0, LINE_COLOR ) );
		item.setBackground( ITEM_COLOR );

		return item;
	}

	public JMenuItem add( String s )
	{
		JMenuItem item = super.add( s );

		item.setBorder( javax.swing.BorderFactory.createMatteBorder( 0, 0, 1, 0, LINE_COLOR ) );
		item.setBackground( ITEM_COLOR );

		return item;
	}
}
