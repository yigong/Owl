package owl.main.debug.debugframe;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import owl.cameraAPI.CameraAPI;


public class BoardMemoryPanel extends DebugPanel
{
	private static final long serialVersionUID = -6624477727409605112L;

	public static final int BRD				= 0;
	public static final int MEM				= 1;
	public static final int BRD_MEM			= 2;

	public static final int PCI_ONLY		= 0;
	public static final int PCI_ALL			= 1;

	private final String NO_ACTION  		= "NO_ACTION";
	private final String PCI_ONLY_ACTION	= "PCI";
	private final String ALL_PCI_ACTION		=  PCI_ONLY_ACTION + "(e)";
	private final String TIM_ACTION			= "TIM";
	private final String UTL_ACTION			= "UTL";
	private final String X_ACTION			= "X";
	private final String Y_ACTION			= "Y";
	private final String P_ACTION			= "P";
	private final String R_ACTION			= "R";

	private JRadioButton PCIRadioButton;
	private JRadioButton TIMRadioButton;
	private JRadioButton UTLRadioButton;
	private ButtonGroup  brdGroup;
	private ButtonGroup  memGroup;
	private String       PCIAction;

	public BoardMemoryPanel( int option )
	{
		this( option, PCI_ALL );
	}

	public BoardMemoryPanel( int option, int pciType )
	{
		super();

		PCIAction = ALL_PCI_ACTION;

		if ( pciType == PCI_ONLY )
		{
			PCIAction = PCI_ONLY_ACTION;
		}

		if ( option == BRD )
		{
			addBoardOptions();
			setAlignmentX( Component.RIGHT_ALIGNMENT );
		}

		else if ( option == MEM )
		{
			addMemoryOptions();
			setAlignmentX( Component.RIGHT_ALIGNMENT );
		}

		else if ( option == BRD_MEM )
		{
			addBoardOptions();
			addMemoryOptions();
		}
	}

	public int getBoardID()
	{
		int brdId = 0;

		String action = brdGroup.getSelection().getActionCommand();

		if ( action.equals( PCIAction ) )
		{
			brdId = CameraAPI.PCI_ID;
		}

		else if ( action.equals( TIM_ACTION ) )
		{
			brdId = CameraAPI.TIM_ID;
		}

		else if ( action.equals( UTL_ACTION ) )
		{
			brdId = CameraAPI.UTIL_ID;
		}

		return brdId;
	}

	public int getMemID()
	{
		int memId = 0;

		String action = memGroup.getSelection().getActionCommand();

		if ( action.equals( X_ACTION ) )
		{
			memId = CameraAPI.X_MEM;
		}

		else if ( action.equals( Y_ACTION ) )
		{
			memId = CameraAPI.Y_MEM;
		}

		else if ( action.equals( P_ACTION ) )
		{
			memId = CameraAPI.P_MEM;
		}

		else if ( action.equals( R_ACTION ) )
		{
			memId = CameraAPI.R_MEM;
		}

		return memId;
	}

	public void supportsPCIeBrdOption( boolean trueFalse )
	{
		PCIRadioButton.setVisible( trueFalse );

		if ( PCIRadioButton.isSelected() )
		{
			TIMRadioButton.setSelected( true );
		}
	}

	public void supportsUtilBrdOption( boolean trueFalse )
	{
		UTLRadioButton.setVisible( trueFalse );
	}

	private void addBoardOptions()
	{
		JPanel panel = new JPanel();
		panel.setLayout( new FlowLayout( FlowLayout.CENTER ) );

		JLabel label = new JLabel( "Board: " );
		TIMRadioButton = new JRadioButton( TIM_ACTION, true );
		TIMRadioButton.setActionCommand( TIM_ACTION );

		PCIRadioButton = new JRadioButton( PCIAction );
		PCIRadioButton.setActionCommand( PCIAction );

		UTLRadioButton = new JRadioButton( UTL_ACTION );
		UTLRadioButton.setActionCommand( UTL_ACTION );
		UTLRadioButton.setVisible( false );

		brdGroup = new ButtonGroup();
		brdGroup.add( PCIRadioButton );
		brdGroup.add( TIMRadioButton );
		brdGroup.add( UTLRadioButton );

		panel.add( label );
		panel.add( TIMRadioButton );
		panel.add( PCIRadioButton );
		panel.add( UTLRadioButton );

		addComponent( this, panel, 0, 3, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0, 1, 6 );
	}

	private void addMemoryOptions()
	{
		JLabel label = new JLabel( "Memory: " );
		JRadioButton RMEMRadioButton = new JRadioButton( R_ACTION );
		RMEMRadioButton.setActionCommand( R_ACTION );

		JRadioButton PMEMRadioButton = new JRadioButton( P_ACTION );
		PMEMRadioButton.setActionCommand( P_ACTION );

		JRadioButton XMEMRadioButton = new JRadioButton( X_ACTION );
		XMEMRadioButton.setActionCommand( X_ACTION );

		JRadioButton YMEMRadioButton = new JRadioButton( Y_ACTION, true );
		YMEMRadioButton.setActionCommand( Y_ACTION );

		memGroup = new ButtonGroup();
		memGroup.add( RMEMRadioButton );
		memGroup.add( PMEMRadioButton );
		memGroup.add( XMEMRadioButton );
		memGroup.add( YMEMRadioButton );

		addComponent( this, label,           0, 3, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 0, 1, 1 );
		addComponent( this, RMEMRadioButton, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 1, 1, 1 );
		addComponent( this, PMEMRadioButton, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 2, 1, 1 );
		addComponent( this, XMEMRadioButton, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 3, 1, 1 );
		addComponent( this, YMEMRadioButton, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 4, 1, 1 );
	}
}
