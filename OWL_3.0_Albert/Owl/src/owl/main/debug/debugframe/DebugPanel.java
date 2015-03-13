package owl.main.debug.debugframe;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import owl.main.owltypes.OwlBoldButton;


public class DebugPanel extends JPanel
{
	private static final long serialVersionUID = -2922252927286871022L;

	protected GridBagLayout			gbl;
	protected GridBagConstraints	gbc;
 	protected JButton				button;
	protected String				applyAction;
	protected String				abortAction;

	public DebugPanel()
	{
		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();

		setLayout( gbl );
	}

	public void setButtonAction( String action )
	{
		button.setText( action );
		button.setActionCommand( action );

		if ( OwlBoldButton.class.isInstance( button ) )
		{
			if ( action == abortAction )
			{
				( ( OwlBoldButton )button ).setColored();
			}
			else
			{
				( ( OwlBoldButton )button ).setBlack();
			}
		}
	}

	protected String changeRadix( String text, int radix )
	{
		String retVal = text;

		try
		{
			if ( radix == 10 )
			{
				int val = Integer.parseInt( text, 16 );
				retVal = Integer.toString( val );
			}
			else if ( radix == 16 )
			{
				int val = Integer.parseInt( text, 10 );
				retVal = Integer.toHexString( val );
			}
			else
			{
				retVal = text;
			}
		}
		catch ( NumberFormatException nfe ) { retVal = text; }

		return retVal;
	}

	protected int changeIntegerRadix( String text, int radix )
	{
		int retVal = 0;

		try
		{
			if ( radix == 10 )
			{
				retVal = Integer.parseInt( text.trim(), 16 );
			}

			else if ( radix == 16 )
			{
				retVal = Integer.parseInt( text.trim(), 10 );
			}
		}
		catch ( NumberFormatException nfe ) { retVal = 0; }

		return retVal;
	}

	protected void addComponent( JPanel panel, JComponent comp, int insetTOP,
			 int insetLEFT, int insetBOTTOM, int insetRIGHT,
			 int fill, int anchr, int row, int col,
			 int rowSpan, int colSpan )
	{
		// Set the grid layout constraints.
		gbc.fill = fill;
		gbc.anchor = anchr;
		gbc.insets = new Insets( insetTOP, insetLEFT, insetBOTTOM, insetRIGHT );
		gbc.gridx = col;
		gbc.gridy = row;
		gbc.gridwidth = colSpan;
		gbc.gridheight = rowSpan;

		// Add the component to the panel.
		gbl.setConstraints( comp, gbc );
		panel.add( comp );
	}
}
