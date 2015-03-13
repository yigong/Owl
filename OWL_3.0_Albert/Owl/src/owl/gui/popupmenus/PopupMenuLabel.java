package owl.gui.popupmenus;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;


public class PopupMenuLabel extends JLabel implements ActionListener, MouseListener
{
	private static final long serialVersionUID = 7120600441274610075L;

	private final String BRACKET_REPLACE_STRING = "#$%";
	private final String BRACKET_STRING			= " " + BRACKET_REPLACE_STRING + " ";

	private ArrayList<ActionListener> actionListeners;
	private JPopupMenu                popup;
	private String                    bracketText;
	private boolean                   allowLeftButton;

	public PopupMenuLabel( String[] textArray, int initIndex )
	{
		super( textArray[ initIndex ] );
		super.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

		popup = new JPopupMenu();
		popup.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

		for ( int i=0; i<textArray.length; i++ )
		{
			JMenuItem menuItem = new JMenuItem( textArray[ i ] );
			menuItem.addActionListener( this );
			popup.add( menuItem );
		}
		setComponentPopupMenu( popup );
		addMouseListener( this );

		actionListeners = new ArrayList<ActionListener>();

		allowLeftButton = false;
		bracketText     = BRACKET_STRING;
	}

	public void addActionListener( ActionListener aListener )
	{
		actionListeners.add( aListener );
	}

	public void allowLeftButtonActivation()
	{
		allowLeftButton = true;
	}

	public void setSquareBrackets()
	{
		bracketText = "[ " + BRACKET_REPLACE_STRING + " ]";
		setText( bracketText.replace( BRACKET_REPLACE_STRING, getText() ) );
	}

	public void setRoundedBrackets()
	{
		bracketText = "( " + BRACKET_REPLACE_STRING + " )";
		setText( bracketText.replace( BRACKET_REPLACE_STRING, getText() ) );
	}

	public void setCurlyBrackets()
	{
		bracketText = "{ " + BRACKET_REPLACE_STRING + " }";
		setText( bracketText.replace( BRACKET_REPLACE_STRING, getText() ) );
	}

	public void clearBrackets()
	{
		bracketText = BRACKET_STRING;
	}

	public String getRawText()
	{
		return getText().substring( 1, getText().length() - 2 ).trim();
	}

	// +---------------------------------------------------------------------------
	// |  setSelected
	// +---------------------------------------------------------------------------
	// |  Sets the label to the specified value IF the value exists as the text
	// |  for one of the label's popup menu component's text.
	// |
	// |  @param itemName The value to set the label's text too.
	// +---------------------------------------------------------------------------
	public void setSelected( String itemName )
	{
		javax.swing.MenuElement[] elems = popup.getSubElements();

		for ( int i=0; i<elems.length; i++ )
		{
			if ( elems[ i ].getComponent() instanceof javax.swing.JMenuItem )
			{
				JMenuItem item = ( JMenuItem )elems[ i ].getComponent();

				if ( item.getText().contains( itemName ) )
				{
					setText( bracketText.replace( BRACKET_REPLACE_STRING, item.getText() ) );
				}
			}
		}
	}

	public void actionPerformed( ActionEvent ae )
	{
		setText( bracketText.replace( BRACKET_REPLACE_STRING, ae.getActionCommand() ) );

		for ( int i=0; i<actionListeners.size(); i++ )
		{
			actionListeners.get( i ).actionPerformed( ae );
		}
	}

	public void mouseClicked( MouseEvent me )
	{
		processEvent( me );
	}

	public void mouseEntered( MouseEvent me )
	{
	}

	public void mouseExited( MouseEvent me )
	{
	}

	public void mousePressed( MouseEvent me )
	{
		processEvent( me );
	}

	public void mouseReleased( MouseEvent me )
	{
	}

	private void processEvent( MouseEvent me )
	{
		if ( ( !allowLeftButton && SwingUtilities.isRightMouseButton( me ) ) ||
			 (  allowLeftButton && SwingUtilities.isLeftMouseButton( me )  ) )
		{
			popup.show( this, this.getWidth(), 0 );
		}
	}
}
