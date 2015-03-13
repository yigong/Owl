package owl.main.debug.debugframe;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import owl.main.owltypes.OwlTextField;


public class AddressPanel extends DebugPanel implements ActionListener
{
	private static final long serialVersionUID = -3426119874044637246L;

	private OwlTextField startAddrTxtfld;
	private OwlTextField endAddrTxtfld;
	private RadixPanel   radixPanel;

	public AddressPanel()
	{
		super();

		TitledBorder title;
		title = BorderFactory.createTitledBorder( "Address" );
		setBorder( title );

		JLabel startAddrLabel = new JLabel( "start: " );
		startAddrTxtfld = new OwlTextField( 7 );

		JLabel endAddrLabel = new JLabel( " end: " );
		endAddrTxtfld = new OwlTextField( 7 );

		radixPanel = new RadixPanel();
		radixPanel.addRadixListener( this );

		addComponent( this, startAddrLabel,  0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0, 1, 1 );
		addComponent( this, startAddrTxtfld, 0, 5, 0, 5, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 1, 1, 1 );
		addComponent( this, endAddrLabel,    0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 2, 1, 1 );
		addComponent( this, endAddrTxtfld,   0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 3, 1, 1 );
		addComponent( this, radixPanel,      0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 0, 1, 4 );
	}

	public int getStartAddress() throws NumberFormatException
	{
		int retVal = 0;

		try
		{
			retVal = Integer.parseInt( startAddrTxtfld.getText(), radixPanel.getRadix() );
		}
		catch ( NumberFormatException nfe )
		{
			throw new NumberFormatException( "Failed to parse start address: " + startAddrTxtfld.getText() );
		}

		return retVal;
	}

	public int getEndAddress() throws NumberFormatException
	{
		int retVal = 0;

		try
		{
			retVal = Integer.parseInt( endAddrTxtfld.getText(), radixPanel.getRadix() );
		}
		catch ( NumberFormatException nfe )
		{
			if ( endAddrTxtfld.getText().length() > 0 )
				throw new NumberFormatException( "Failed to parse end address: " + endAddrTxtfld.getText() );
		}

		return retVal;
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( e.getActionCommand().equals( RadixPanel.DEC_ACTION ) )
		{
			startAddrTxtfld.setText( changeRadix( startAddrTxtfld.getText(), 10 ) );
			endAddrTxtfld.setText( changeRadix( endAddrTxtfld.getText(), 10 ) );
		}

		else if ( e.getActionCommand().equals( RadixPanel.HEX_ACTION ) )
		{
			startAddrTxtfld.setText( changeRadix( startAddrTxtfld.getText(), 16 ) );
			endAddrTxtfld.setText( changeRadix( endAddrTxtfld.getText(), 16 ) );
		}
	}
}
