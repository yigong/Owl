package owl.main.debug.debugframe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;

import owl.cameraAPI.CameraAPI;
import owl.cameraAPI.ReplyException;
import owl.main.MainApp;
import owl.main.device.DeviceEvent;
import owl.main.device.DeviceListener;
import owl.main.setup.SetupEvent;
import owl.main.setup.SetupListener;


public class RDM_WRMPanel extends DebugRunnablePanel implements SetupListener, DeviceListener
{
	private static final long serialVersionUID = 3468807393064749185L;

	private final String RDM_ACTION	= "READ MEMORY";
	private final String WRM_ACTION	= "WRITE MEMORY";

	private ButtonGroup      brdGroup;
	private RadixPanel       rdmRadixPanel;
	private BoardMemoryPanel rdmBrdMemPanel;
	private AddressPanel     rdmAddrPanel;
	private OutputPanel      rdmResultPanel;
	private RadixPanel       wrmRadixPanel;
	private BoardMemoryPanel wrmBrdMemPanel;
	private AddressPanel     wrmAddrPanel;
	private ValuePanel       wrmValPanel;
	private OutputPanel      wrmResultPanel;

	public RDM_WRMPanel( JButton button, String applyAction, String abortAction )
	{
		super();

		this.button      = button;
		this.applyAction = applyAction;
		this.abortAction = abortAction;

		JPanel wrmPanel = new JPanel();
		wrmPanel.setLayout( new BoxLayout( wrmPanel, BoxLayout.Y_AXIS ) );

		JRadioButton wrmLabel = new JRadioButton( WRM_ACTION );
		wrmLabel.setActionCommand( WRM_ACTION );
		wrmLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
		wrmLabel.setForeground( Color.RED );
		wrmLabel.setFont( new Font( wrmLabel.getFont().getName(), Font.BOLD, wrmLabel.getFont().getSize() ) );

		wrmRadixPanel  = new RadixPanel();
		wrmBrdMemPanel = new BoardMemoryPanel( BoardMemoryPanel.BRD_MEM, BoardMemoryPanel.PCI_ONLY );
		wrmAddrPanel   = new AddressPanel();
		wrmValPanel    = new ValuePanel();
		wrmResultPanel = new OutputPanel( OutputPanel.MULTIPLE );

		wrmRadixPanel.addRadixListener( wrmValPanel );
		wrmRadixPanel.addRadixListener( wrmResultPanel );

		wrmPanel.add( wrmLabel );
		wrmPanel.add( wrmRadixPanel );
		wrmPanel.add( wrmBrdMemPanel );
		wrmPanel.add( wrmAddrPanel );
		wrmPanel.add( wrmValPanel );
		wrmPanel.add( wrmResultPanel );

		JPanel rdmPanel = new JPanel();
		rdmPanel.setLayout( new BoxLayout( rdmPanel, BoxLayout.Y_AXIS ) );

		rdmRadixPanel  = new RadixPanel();
		rdmBrdMemPanel = new BoardMemoryPanel( BoardMemoryPanel.BRD_MEM, BoardMemoryPanel.PCI_ONLY );
		rdmAddrPanel   = new AddressPanel();
		rdmResultPanel = new OutputPanel( OutputPanel.MULTIPLE );

		rdmRadixPanel.addRadixListener( rdmResultPanel );

		JRadioButton rdmRadioButton = new JRadioButton( RDM_ACTION, true );
		rdmRadioButton.setActionCommand( RDM_ACTION );
		rdmRadioButton.setAlignmentX( Component.CENTER_ALIGNMENT );
		rdmRadioButton.setForeground( Color.BLUE );
		rdmRadioButton.setFont( new Font( rdmRadioButton.getFont().getName(), Font.BOLD, rdmRadioButton.getFont().getSize() ) );

		rdmPanel.add( rdmRadioButton );
		rdmPanel.add( rdmRadixPanel );
		rdmPanel.add( rdmBrdMemPanel );
		rdmPanel.add( rdmAddrPanel );
		rdmPanel.add( Box.createRigidArea( new Dimension( 215, (int)wrmValPanel.getPreferredSize().getHeight() ) ) );
		rdmPanel.add( rdmResultPanel );

		brdGroup = new ButtonGroup();
		brdGroup.add( wrmLabel );
		brdGroup.add( rdmRadioButton );

		JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, rdmPanel, wrmPanel );
		splitPane.setDividerSize( 4 );
		add( splitPane );
	}

	@Override
	public void run()
	{
		if ( !CameraAPI.IsDeviceOpen() ) { return; }

		Thread.currentThread().setName( "Owl - RDM/WRM Panel" );

		setButtonAction( abortAction );
		String action = brdGroup.getSelection().getActionCommand();

		stop = false;

		try
		{
			if ( action.equals( RDM_ACTION ) )
			{
				rdmResultPanel.clear();

				int startAddr = rdmAddrPanel.getStartAddress();
				int endAddr   = rdmAddrPanel.getEndAddress();

				if ( endAddr == 0 ) { endAddr = startAddr; }

				MainApp.infoStart( "Reading DSP memory" );

				for ( int i=startAddr; i<=endAddr; i++ )
				{
					if ( stop ) { break; }

					int retVal = CameraAPI.Cmd2( rdmBrdMemPanel.getBoardID(),
												 CameraAPI.RDM,
												 ( rdmBrdMemPanel.getMemID() | i ) );

					rdmResultPanel.setValue( i,
											 "  >>>  ",
											 retVal,
											 rdmRadixPanel.getRadix() );
				}

				MainApp.infoEnd();
			}

			else if ( action.equals( WRM_ACTION ) )
			{
				wrmResultPanel.clear();

				int startAddr = wrmAddrPanel.getStartAddress();
				int endAddr   = wrmAddrPanel.getEndAddress();
				int value     = wrmValPanel.getValue( wrmRadixPanel.getRadix() );

				if ( endAddr == 0 ) { endAddr = startAddr; }

				MainApp.infoStart( "Writing DSP memory" );

				for ( int i=startAddr; i<=endAddr; i++ )
				{
					if ( stop ) { break; }

					CameraAPI.Cmd( wrmBrdMemPanel.getBoardID(),
								   CameraAPI.WRM,
								   ( wrmBrdMemPanel.getMemID() | i ),
								   value,
								   CameraAPI.DON );

					wrmResultPanel.setValue( i,
											 "  <<<  ",
											 value,
											 wrmRadixPanel.getRadix() );

					if ( wrmValPanel.isIncrement() ) { value++; }
				}

				MainApp.infoEnd();
			}
		}
		catch ( ReplyException re )
		{
			MainApp.infoFail();
			MainApp.error( re.toString() );
		}
		catch ( Exception e )
		{
			MainApp.infoFail();
			MainApp.error( e );
		}
		finally
		{
			setButtonAction( applyAction );
		}
	}

	@Override
	public void setupChanged( SetupEvent event )
	{
		try
		{
			if ( event.setupOk && CameraAPI.IsCCParamSupported( CameraAPI.ARC50 ) )
			{
				rdmBrdMemPanel.supportsUtilBrdOption( true );
				wrmBrdMemPanel.supportsUtilBrdOption( true );
			}
			else
			{
				rdmBrdMemPanel.supportsUtilBrdOption( false );
				wrmBrdMemPanel.supportsUtilBrdOption( false );
			}
		}
		catch ( Exception ex )
		{
			MainApp.error( ex );
		}
	}

	@Override
	public void deviceChanged( DeviceEvent event )
	{
		if ( event.deviceName.toLowerCase().contains( "pcie" ) )
		{
			rdmBrdMemPanel.supportsPCIeBrdOption( false );
			wrmBrdMemPanel.supportsPCIeBrdOption( false );
		}
		else
		{
			rdmBrdMemPanel.supportsPCIeBrdOption( true );
			wrmBrdMemPanel.supportsPCIeBrdOption( true );
		}
	}
}
