package owl.main.debug.debugframe;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultStyledDocument;

import owl.gui.utils.OwlUtilities;


public class OutputPanel extends DebugPanel implements ActionListener
{
	private static final long serialVersionUID = 3628820606208373945L;

	public static final int SINGLE		=	0;
	public static final int MULTIPLE	=	1;

	private DefaultStyledDocument doc;
	private JTextArea textPane;
	private JLabel textLabel;

	public OutputPanel( int option )
	{
		super();

		doc       = null;
		textPane  = null;
		textLabel = null;

		TitledBorder title;
		title = BorderFactory.createTitledBorder( "Results" );
		setBorder( title );

		if ( option == MULTIPLE )
		{
			doc = new DefaultStyledDocument();
			textPane = new JTextArea( doc, "", 10, 20 );

			JScrollPane scrollPane =
						new JScrollPane( textPane,
										 ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
										 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

			addComponent( this, scrollPane, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0, 1, 2 );
		}
		else
		{
			textLabel = new JLabel( " " );
			addComponent( this, textLabel, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0, 1, 2 );
		}
	}

	public void clear()
	{
		try
		{
			if ( textPane != null )
			{
				doc.remove( 0, doc.getLength() );
			}
			else if ( textLabel != null )
			{
				textLabel.setText( " " );
			}
		}
		catch( javax.swing.text.BadLocationException ble ) {}
	}

	public void setValue( int addr, String sep, int val, int radix )
	{
		if ( textPane != null )
		{
			if ( radix == 10 )
			{
				textPane.append(
						String.format( "%8d", addr ).replace( ' ', '0' ) + sep + String.format( "%8d", val ).replace( ' ', '0' ) + "\n" );
			}
			else
			{
				textPane.append(
						String.format( "%6X", addr ).replace( ' ', '0' ) + sep + String.format( "%6X", val ).replace( ' ', '0' ) + "\n" );
			}
		}
	}

	public void setValue( int val, int radix )
	{
		String asciiVal = OwlUtilities.intToAscii( val );

		if ( textPane != null )
		{
			textPane.append( Integer.toString( val, radix ) + "\n" );
		}
		else if ( textLabel != null )
		{
			textLabel.setText(
					Integer.toString( val, radix ) +
					( !asciiVal.equals( "" ) ? String.format( "  ( %s )", asciiVal ) : "" ) + "\n" );
		}
	}

	public void actionPerformed( ActionEvent e )
	{
		String[] tokens = textPane.getText().split( "[ \t\n\r]" );

		if ( tokens.length < 5 ) { return; }

		if ( e.getActionCommand().equals( RadixPanel.DEC_ACTION ) )
		{
			clear();

			for ( int i=0; i<tokens.length; i+=5 )
			{
				int addr = changeIntegerRadix( tokens[ i ], 10 ); 
				int val  = changeIntegerRadix( tokens[ i + 4 ], 10 );
				String sep = "  " + tokens[ i + 2 ] + "  ";

				setValue( addr, sep, val, 10 );
			}
		}

		else if ( e.getActionCommand().equals( RadixPanel.HEX_ACTION ) )
		{
			clear();

			for ( int i=0; i<tokens.length; i+=5 )
			{
				int addr   = changeIntegerRadix( tokens[ i ], 16 );
				int val    = changeIntegerRadix( tokens[ i + 4 ], 16 );
				String sep = "  " + tokens[ i + 2 ] + "  ";

				setValue( addr, sep, val, 16 );
			}
		}
	}
}
