package owl.CCParams;

import javax.swing.JCheckBox;
import javax.swing.JLabel;



public class CCScript
{
	public static int INVALID_PARAM = -1;

	public CCScriptFrame frame;			// Script window
	public JCheckBox     appliedChkbx;	// Checkbox used to show the script has been applied
	public JLabel        appliedLabel;	// Label used to show script dependent details
	public String        script;		// Script string
	public String        text;			// Descriptive text ( for menu )
	public String        action;		// Action command ( for menu )
	public boolean       infoOnly;		// 'true' if info only, no script
	public int           bits;			// The required parameter for this script

	public CCScript( String script, String text, String action, int bits, boolean infoOnly )
	{
		this.frame  	= null;
		this.script 	= script;
		this.text		= text;
		this.action		= action;
		this.bits		= bits;
		this.infoOnly	= infoOnly;

		appliedLabel = new JLabel( "" );

		appliedChkbx = new JCheckBox();
		appliedChkbx.setEnabled( false );
	}

	//  Constructor - sets default values
	// ------------------------------------------
	public CCScript()
	{
		this( null, null, null, CCScript.INVALID_PARAM, false );
	}

	// Required by digester to set 'script' data
	// -------------------------------------------
	public void setScript( String script )
	{
		this.script = script;
	}

	// Required by digester to set 'text' data
	// -------------------------------------------
	public void setText( String text )
	{
		this.text = text;
	}

	// Required by digester to set 'action' data
	// -------------------------------------------
	public void setAction( String action )
	{
		this.action = action;
	}

	// Required by digester to set 'bits' data
	// -------------------------------------------
	public void setBits( String bits )
	{
		try
		{
			this.bits = Integer.parseInt( bits.replace( 'x', '0' ), 16 );
		}
		catch ( NumberFormatException nfe )
		{
			this.bits = INVALID_PARAM;

			javax.swing.JOptionPane.showMessageDialog( null,
							"Failed to successfully parse XML script ( " +
							 text + " )\n" + nfe.getMessage() );
		}
	}

	// Required by digester to set 'infoOnly' data
	// Ignoring parameter, only tags are needed
	// in the xml file.
	// -------------------------------------------
	public void setInfoOnly( boolean infoOnly )
	{
		this.infoOnly = true;
	}

	//  Set the applied label to the specified string in the
	//  following format: [ str ]
	// +---------------------------------------------------------+
	public void setAppliedLabel( String str )
	{
		if ( str != null && !str.equals( "" ) )
		{
			appliedLabel.setText( "<html><font color=\"#666666\"> [ " +
								   str + " ]</font></html>" );
		}
	}

	public void verify() throws Exception
	{
		if ( ( script == null || script.isEmpty() ) && !infoOnly )
		{
			throw new Exception(
				"NULL or empty script \"script\"! Verify XML file!" );
		}

		if ( ( action == null || action.isEmpty() ) && !infoOnly )
		{
			throw new Exception(
				"NULL or empty script \"action\"! Verify XML file!" );
		}

		if ( text == null || text.isEmpty() )
		{
			throw new Exception(
				"NULL or empty script \"text\"! Verify XML file" );
		}
	}

	public void frameDispose()
	{
		appliedLabel.setText( "" );
		appliedChkbx.setSelected( false );

		if ( frame != null )
		{
			frame.dispose();
		}
	}
}
