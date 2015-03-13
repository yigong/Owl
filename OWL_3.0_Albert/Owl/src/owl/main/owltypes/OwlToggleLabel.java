package owl.main.owltypes;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLabel;


// +---------------------------------------------------------------------+
// |  Class OwlToggleLabel                                               |
// +---------------------------------------------------------------------+
// |  This class is used to create a toggle label, which is essentially  |
// |  an icon based checkbox, but without the checkbox. Two user supplied|
// |  icons are used to represent the two toggle states: true/false.     |
// +---------------------------------------------------------------------+
public class OwlToggleLabel extends JLabel
{
	private static final long serialVersionUID = 2032762747004715879L;

	private final boolean OFF_STATE = false;
	private final boolean ON_STATE  = true;

	private Icon onIcon;
	private Icon offIcon;
	private boolean state;

	// |  Constructor
	// +--------------------------------------------------------
	public OwlToggleLabel( Icon _off, Icon _on )
	{
		super();

		onIcon  = _on;
		offIcon = _off;
		state   = OFF_STATE;

		setIcon( offIcon );
		addMouseListener( new ToggleLabelMouseListener() );
	}

	// |  isSelected
	// +--------------------------------------------------------
	// |  Returns 'true' if the toggle label is in the 'on'
	// |  state.  Returns 'false' otherwise.
	// ---------------------------------------------------------
	public boolean isSelected()
	{
		return state;
	}

	// |  toggle
	// +--------------------------------------------------------
	// |  Toggles the state of the label.
	// ---------------------------------------------------------
	public void toggle()
	{
		if ( state == OFF_STATE )
		{
			setIcon( onIcon );
			state = ON_STATE;
		}

		else if ( state == ON_STATE )
		{
			setIcon( offIcon );
			state = OFF_STATE;
		}
	}

	// +---------------------------------------------------------------------+
	// |  Class ToggleLabelMouseListener                                     |
	// +---------------------------------------------------------------------+
	// |  This mouse listener class is used to set the state of the toggle   |
	// |  label. Only the left mouse button is accepted for changing the     |
	// |  state.                                                             |
	// +---------------------------------------------------------------------+
	private class ToggleLabelMouseListener extends MouseAdapter
	{
		// |  mouseClicked
		// +--------------------------------------------------------
		// |  Toggles the label's state and icon when the left
		// |  mouse button is clicked on the label.
		// ---------------------------------------------------------
		@Override
		public void mouseClicked( MouseEvent arg0 )
		{
			if ( javax.swing.SwingUtilities.isLeftMouseButton( arg0 ) )
			{
				toggle();
			}
		}
	}
}
