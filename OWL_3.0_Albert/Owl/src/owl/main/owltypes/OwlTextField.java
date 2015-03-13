package owl.main.owltypes;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import owl.gui.popupmenus.TextFieldPopupMenu;


public class OwlTextField extends JTextField implements FocusListener
{
	private static final long serialVersionUID = 5115561879902899053L;


	public OwlTextField()
	{
		super();

		stdInit();
	}

	public OwlTextField( String text )
	{
		super( text );

		stdInit();
	}

	public OwlTextField( int columns )
	{
		super( new String() );
		super.setColumns( columns );

		stdInit();
	}

	public OwlTextField( String text, int columns )
	{
		super( text, columns );

		stdInit();
	}

	protected void stdInit()
	{
		setComponentPopupMenu( new TextFieldPopupMenu( this ) );
		addFocusListener( this );
	}

	@Override
	public void focusGained( FocusEvent fe )
	{
		selectAll();
	}

	@Override
	public void focusLost( FocusEvent fe )
	{
	}
}
