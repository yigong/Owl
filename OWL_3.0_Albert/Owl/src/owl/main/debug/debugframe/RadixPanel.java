package owl.main.debug.debugframe;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;


public class RadixPanel extends DebugPanel implements ActionListener
{
	private static final long serialVersionUID = -2754743136664216441L;

	public static final String DEC_ACTION =	"Decimal";
	public static final String HEX_ACTION =	"Hexadecimal";

	private JRadioButton decRadioButton;
	private JRadioButton hexRadioButton;
	private ButtonGroup radixGroup;
	private ArrayList<ActionListener> alList;
	private String lastRadix;

	public RadixPanel()
	{
		decRadioButton = new JRadioButton( DEC_ACTION );
		decRadioButton.setActionCommand( DEC_ACTION );
		decRadioButton.addActionListener( this );

		hexRadioButton = new JRadioButton( HEX_ACTION, true );
		hexRadioButton.setActionCommand( HEX_ACTION );
		hexRadioButton.addActionListener( this );

		radixGroup = new ButtonGroup();
		radixGroup.add( decRadioButton );
		radixGroup.add( hexRadioButton );

		addComponent( this, decRadioButton, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 0, 1, 1 );
		addComponent( this, hexRadioButton, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 1, 1, 1 );

		alList = new ArrayList<ActionListener>();
		lastRadix = HEX_ACTION;
	}

	public void addRadixListener( ActionListener al )
	{
		alList.add( al );
	}

	public int getRadix()
	{
		String action = radixGroup.getSelection().getActionCommand();

		if ( action.equals( DEC_ACTION ) ) { return 10; }
		else return 16;
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( decRadioButton.isSelected() )
		{
			if ( lastRadix.equals( HEX_ACTION ) )
			{
				lastRadix = DEC_ACTION;

				for ( int i=0; i<alList.size(); i++ )
				{
					alList.get( i ).actionPerformed(
								new ActionEvent( decRadioButton,
												 ActionEvent.ACTION_PERFORMED,
												 DEC_ACTION ) );
				}
			}
		}
		else
		{
			if ( lastRadix.equals( DEC_ACTION ) )
			{
				lastRadix = HEX_ACTION;

				for ( int i=0; i<alList.size(); i++ )
				{
					alList.get( i ).actionPerformed(
								new ActionEvent( hexRadioButton,
												 ActionEvent.ACTION_PERFORMED,
												 HEX_ACTION ) );
				}
			}
		}
	}
}
