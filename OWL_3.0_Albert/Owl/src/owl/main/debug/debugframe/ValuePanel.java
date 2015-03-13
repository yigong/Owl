package owl.main.debug.debugframe;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;

import owl.main.owltypes.OwlTextField;


public class ValuePanel extends DebugPanel implements ActionListener
{
	private static final long serialVersionUID = 8933471970868842375L;

	private OwlTextField valueTxtfld;
	private JCheckBox incrValueChkbox;

	public ValuePanel()
	{
		super();

		TitledBorder title;
		title = BorderFactory.createTitledBorder( "Value" );
		setBorder( title );

		valueTxtfld = new OwlTextField( 7 );
		incrValueChkbox = new JCheckBox( "Increment Value", true );

		addComponent( this, valueTxtfld,     0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0, 1, 1 );
		addComponent( this, incrValueChkbox, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 1, 1, 1 );
	}

	public void setValue( int value, int radix )
	{
		valueTxtfld.setText( Integer.toString( value, radix ) );
	}

	public int getValue( int radix ) throws NumberFormatException
	{
		int retVal = 0;

		try
		{
			retVal = Integer.parseInt( valueTxtfld.getText(), radix );
		}
		catch ( NumberFormatException nfe )
		{
			throw new NumberFormatException( "Failed to parse value: " +
											  valueTxtfld.getText() );
		}

		return retVal;
	}

	public boolean isIncrement()
	{
		return incrValueChkbox.isSelected();
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( e.getActionCommand().equals( RadixPanel.DEC_ACTION ) )
		{
			valueTxtfld.setText( changeRadix( valueTxtfld.getText(), 10 ) );
		}

		else if ( e.getActionCommand().equals( RadixPanel.HEX_ACTION ) )
		{
			valueTxtfld.setText( changeRadix( valueTxtfld.getText(), 16 ) );
		}
	}
}
