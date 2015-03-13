package owl.main.fits;

import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;


public class FitsHeaderElement extends JPanel
{
	private static final long serialVersionUID = 4800439511388570024L;

	public static final int KEY_FIELD_WIDTH	=  9;
	public static final int VAL_FIELD_WIDTH	= 17;
	public static final int COM_FIELD_WIDTH	= 21;

	private static int idTagCount = 0;

	private JTextField keyTxtfld;
	private JTextField valTxtfld;
	private JTextField comTxtfld;
	private JPopupMenu keyPopupMenu;
	private JMenuItem  deleteMenuItem;

	public FitsHeaderElement()
	{
		super();

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );

		deleteMenuItem = new JMenuItem( "Delete Key" );
		deleteMenuItem.setName( String.valueOf( idTagCount++ ) );

		keyPopupMenu   = new JPopupMenu();
		keyPopupMenu.add( deleteMenuItem );

		keyTxtfld = new JTextField( "", KEY_FIELD_WIDTH );
		keyTxtfld.setComponentPopupMenu( keyPopupMenu );
		add( keyTxtfld );

		valTxtfld = new JTextField( "", VAL_FIELD_WIDTH );
		add( valTxtfld );

		comTxtfld = new JTextField( "", COM_FIELD_WIDTH );
		add( comTxtfld );
	}

	public void clear()
	{
		keyTxtfld.setText( "" );
		valTxtfld.setText( "" );
		comTxtfld.setText( "" );
	}

	public boolean hasKey( String aKey )
	{
		if ( keyTxtfld.getText().equals( aKey ) )
		{
			return true;
		}

		return false;
	}

	public boolean hasAKey()
	{
		if ( keyTxtfld.getText().equals( "" ) )
		{
			return false;
		}

		return true;
	}

	public String getIdTag()
	{
		return deleteMenuItem.getName();
	}

	public String getKey()
	{
		return keyTxtfld.getText();
	}

	public String getValue()
	{
		return valTxtfld.getText();
	}

	public String getComment()
	{
		return comTxtfld.getText();
	}

	// Required by digester to set 'key' data
	// -------------------------------------------
	public void setKey( String aKey )
	{
		keyTxtfld.setText( aKey );
	}

	// Required by digester to set 'comment' data
	// -------------------------------------------
	public void setComment( String aComment )
	{
		if ( !aComment.isEmpty() )
		{
			comTxtfld.setText( aComment );
		}
	}

	// Required by digester to set 'value' data
	// -------------------------------------------
	public void setValue( String aValue )
	{
		valTxtfld.setText( aValue );
	}

	public void setKeyPopupMenuListener( ActionListener aListener, String anAction )
	{
		deleteMenuItem.addActionListener( aListener );
		deleteMenuItem.setActionCommand( anAction );
	}
}
