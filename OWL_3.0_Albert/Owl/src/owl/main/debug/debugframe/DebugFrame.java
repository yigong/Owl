package owl.main.debug.debugframe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import owl.cameraAPI.CameraAPI;
import owl.gui.utils.OwlButtonFactory;
import owl.main.MainApp;
import owl.main.device.DeviceConnect;
import owl.main.device.DeviceListener;
import owl.main.owltypes.OwlBoldButton;
import owl.main.owltypes.OwlFileChooser;
import owl.main.owltypes.OwlFrame;
import owl.main.setup.SetupFrame;
import owl.main.setup.SetupListener;



public class DebugFrame extends OwlFrame implements ChangeListener
{
	private static final long serialVersionUID = -5958781854607475901L;

	//--------------------------------------------------------------------------
    //   Constants:
    //--------------------------------------------------------------------------
	private final String RUN_ACTION				=	"RUN";
	private final String ABORT_ACTION			=	"ABORT";
	private final String TMPCTRL_LOAD_ACTION	=	"TMPCTRL LOAD";
	private final String TMPCTRL_SAVE_ACTION	=	"TMPCTRL SAVE";
	private final String OTHER_NAME				=	"Other";
	private final String SYS_INFO_NAME			=	"System Info";
	private final String TDL_NAME				=	"TDL";
	private final String RDM_WRM_NAME			=	"RDM/WRM";
	private final String CMD_NAME				=	"CMD";

	//--------------------------------------------------------------------------
    //   Private Variables:
    //--------------------------------------------------------------------------
	private OwlBoldButton applyButton;
	private JTabbedPane   tabbedPane;
	private JCheckBox     tmpCtrlViewChkbox;

	//--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
	public DebugFrame()
	{
		super( "Debug/Developer Options", true );

		super.addComponent( createToolBar(), super.TOOLBAR_INDEX );
		super.addComponent( createComponents(), super.CENTER_CONTAINER_INDEX );

		pack();
	}

	//--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( e.getActionCommand().equals( TMPCTRL_LOAD_ACTION ) || 
			 e.getActionCommand().equals( TMPCTRL_SAVE_ACTION ) )
		{
			String sUserHome = System.getProperty( "user.home" );

			if ( sUserHome == null )
				sUserHome = ".";

			OwlFileChooser fileChooser =
							new OwlFileChooser( new java.io.File( sUserHome ) );

			boolean bOk = ( e.getActionCommand().equals( TMPCTRL_LOAD_ACTION ) ?
						    fileChooser.openDialog() :
						    fileChooser.saveDialog() );

			if ( bOk )
			{
				try
				{
					if ( e.getActionCommand().equals( TMPCTRL_LOAD_ACTION ) )
					{
						MainApp.infoStart( "Loading temperature control file" );
						CameraAPI.LoadTmpCtrlFile( fileChooser.getFile().getPath() );
					}
					else
					{
						MainApp.infoStart( "Saving temperature control file" );
						CameraAPI.SaveTmpCtrlFile( fileChooser.getFile().getPath() );
					}
					MainApp.infoEnd();

					if( tmpCtrlViewChkbox.isSelected() )
					{
						MainApp.launchTextEditor( fileChooser.getFile().getPath() );
					}
				}
				catch ( Exception ex )
				{
					MainApp.infoFail();
					MainApp.error( ex );
				}
			}
		}

		else if ( e.getActionCommand().equals( RUN_ACTION ) )
		{
			if ( tabbedPane != null )
			{
				Component comp = tabbedPane.getSelectedComponent();

				if ( comp != null && DebugRunnablePanel.class.isInstance( comp ) )
				{
					new Thread( ( DebugRunnablePanel )comp ).start();
				}
				else if ( comp != null && DebugRunnablePanel.class.isInstance( comp ) )
				{
					new Thread( ( DebugRunnablePanel )comp ).start();
				}
			}
		}

		else if ( e.getActionCommand().equals( ABORT_ACTION ) )
		{
			MainApp.warn( "Aborting current action .... " );

			if ( tabbedPane != null )
			{
				Component comp = tabbedPane.getSelectedComponent();

				if ( comp != null )
				{
					( ( DebugRunnablePanel )comp ).stop();
				}
			}
		}

		else
		{
			super.actionPerformed( e );
		}
	}

	public void stateChanged( ChangeEvent e )
	{
		if ( e.getSource() != null )
		{
			Component comp = ( ( JTabbedPane )e.getSource() ).getSelectedComponent();

			if ( comp != null )
			{
				if ( comp.getName().equals( OTHER_NAME ) )
				{
					setPreferredSize( new Dimension( 400, 200 ) );
				}

				else if ( comp.getName().equals( SYS_INFO_NAME ) )
				{
					setPreferredSize( new Dimension( 500, 250 ) );
				}

				else if ( comp.getName().equals( TDL_NAME ) )
				{
					setPreferredSize( new Dimension( 350, 280 ) );
				}

				else if ( comp.getName().equals( RDM_WRM_NAME ) )
				{
					setPreferredSize( new Dimension( 475, 550 ) );
				}

				else if ( comp.getName().equals( CMD_NAME ) )
				{
					setPreferredSize( new Dimension( 350, 300 ) );
				}
			}
		}

		pack();
	}

	//--------------------------------------------------------------------------
    //   Protected Methods:
    //--------------------------------------------------------------------------
	protected JToolBar createToolBar()
	{
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable( false );

		applyButton = super.createBoldToolbarButton( RUN_ACTION, Color.RED );
		toolbar.add( applyButton );

		super.appendToolbar( toolbar );

		return toolbar;
	}

	//--------------------------------------------------------------------------
    //   Private Methods:
    //--------------------------------------------------------------------------
	private JPanel createComponents()
	{
		JPanel panel = new JPanel( new BorderLayout() );

		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener( this );

		JPanel tdlPanel = new TDLPanel( applyButton, RUN_ACTION, ABORT_ACTION );
		tdlPanel.setName( TDL_NAME );
		tabbedPane.add( TDL_NAME, tdlPanel );
		SetupFrame.addSetupListener( ( SetupListener )tdlPanel );

		JPanel rdmWrmPanel = new RDM_WRMPanel( applyButton, RUN_ACTION, ABORT_ACTION );
		rdmWrmPanel.setName( RDM_WRM_NAME );
		tabbedPane.add( RDM_WRM_NAME, rdmWrmPanel );
		SetupFrame.addSetupListener( ( SetupListener )rdmWrmPanel );
		DeviceConnect.addDeviceListener( ( DeviceListener )rdmWrmPanel );

		JPanel cmdPanel = new CMDPanel( applyButton, RUN_ACTION, ABORT_ACTION );
		cmdPanel.setName( CMD_NAME );
		tabbedPane.add( CMD_NAME, cmdPanel );
		SetupFrame.addSetupListener( ( SetupListener )cmdPanel );

		tabbedPane.add( OTHER_NAME, createOtherPanel() );

		panel.add( tabbedPane, BorderLayout.CENTER );

		return panel;
	}

	private JPanel createOtherPanel()
	{
		JLabel tmpCtrlLoadLabel = new JLabel( "Load Temperature Ctrl File" );

		JButton tmpCtrlLoadIconButton = OwlButtonFactory.createIconButton( "LoadTmpCtrl.gif",
																			28,
																			25,
																			null,
																			TMPCTRL_LOAD_ACTION,
																			this );

		JLabel tmpCtrlSaveLabel = new JLabel( "Save Temperature Ctrl File" );

		JButton tmpCtrlSaveIconButton = OwlButtonFactory.createIconButton( "SaveTmpCtrl.gif",
																			28,
																			25,
																			null,
																			TMPCTRL_SAVE_ACTION,
																			this );

		tmpCtrlViewChkbox = new JCheckBox( "View File In Text Editor" );
		tmpCtrlViewChkbox.setSelected( false );

		JPanel tmpCtrlPanel = new JPanel( gbl );
		tmpCtrlPanel.setBorder( BorderFactory.createTitledBorder( "Temperature Ctrl Params" ) );

		addComponent( tmpCtrlPanel, tmpCtrlLoadIconButton, 0, 5, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0, 1, 1 );
		addComponent( tmpCtrlPanel, tmpCtrlLoadLabel,      0, 5, 0, 5, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 1, 1, 1 );
		addComponent( tmpCtrlPanel, tmpCtrlSaveIconButton, 0, 5, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 0, 1, 1 );
		addComponent( tmpCtrlPanel, tmpCtrlSaveLabel,      0, 5, 0, 5, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 1, 1, 1 );
		addComponent( tmpCtrlPanel, tmpCtrlViewChkbox,     0, 0, 0, 5, GridBagConstraints.NONE, GridBagConstraints.WEST, 2, 0, 1, 2 );

		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );
		panel.setPreferredSize( new Dimension( 100, 150 ) );
		panel.setName( OTHER_NAME );

		addComponent( panel, tmpCtrlPanel, 0, 5, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0, 1, 1 );

		return panel;
	}
}
