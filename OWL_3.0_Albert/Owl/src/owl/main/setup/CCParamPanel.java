package owl.main.setup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import owl.CCParams.CCFileReader;
import owl.CCParams.CCScript;
import owl.CCParams.CCScriptEvent;
import owl.CCParams.CCScriptFrame;
import owl.CCParams.CCScriptGroup;
import owl.CCParams.CCScriptListener;
import owl.cameraAPI.CameraAPI;
import owl.gui.utils.OwlButtonFactory;
import owl.main.MainApp;
import owl.main.device.DeviceConnect;
import owl.main.device.DeviceEvent;
import owl.main.device.DeviceListener;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlPanel;
import owl.main.scripting.MainInterpreter;



public class CCParamPanel extends OwlPanel
implements SetupListener, DeviceListener, ActionListener, CCScriptListener
{
	private static final long serialVersionUID = -6575503022580234157L;

	private final String ACTION_DETAILS	= "DETAILS";
	private final Component BLANK_COMP  = Box.createRigidArea( new Dimension( 160, 80 ) );
	private final String    INIT_FILE   = MainApp.getAppPath() + "CCScriptList.xml";

	private ArrayList<CCScriptGroup> scriptList;
	private ArrayList<String>        detailList;
	private DetailFrame              detailFrame;


	public CCParamPanel()
	{
		super( "Supported Configuration" );

		scriptList  = CCFileReader.readGroupXml( INIT_FILE );
		detailList  = new ArrayList<String>();
		detailFrame = new DetailFrame();

		add( BLANK_COMP );

		SetupFrame.addSetupListener( this );
		DeviceConnect.addDeviceListener( this );
	}

	// +--------------------------------------------------------------------+
	// |  CCScriptChanged ( CCScriptListener )                              |
	// +--------------------------------------------------------------------+
	// |  Called when a CCParam script is executed                          |
	// +--------------------------------------------------------------------+
	@Override
	public void CCScriptChanged( CCScriptEvent ccse )
	{
		for ( int i=0; i<scriptList.size(); i++ )
		{
			CCScript ccs = scriptList.get( i ).find( ccse.action );

			if ( ccs != null )
			{
				ccs.appliedChkbx.setSelected( true );

				if ( ccse.description != null && !ccse.description.isEmpty() )
				{
					ccs.setAppliedLabel( ccse.description );
				}
			}
		}
	}

	// +--------------------------------------------------------------------+
	// |  setupChanged ( SetupListener )                                    |
	// +--------------------------------------------------------------------+
	// |  Called when a new setup is applied                                |
	// +--------------------------------------------------------------------+
	@Override
	public void setupChanged( SetupEvent event )
	{
		ArrayList<String> openList = null;

		if ( !event.setupOk )
		{
			return;
		}

		//
		//  Kill any open CC script windows. This is necessary
		//  to prevent old/invalid windows from staying around.
		// +---------------------------------------------------+
		if ( scriptList != null )
		{
			for ( int i=0; i<scriptList.size(); i++ )
			{
				//  Save the actions for all open windows so they can
				//  be re-opened ( if necessary ).
				// +-------------------------------------------------+
				CCScriptGroup grp = scriptList.get( i );

				for ( int t=0; t<grp.size(); t++ )
				{
					CCScript script = grp.get( t );

					if ( script.frame != null && script.frame.isVisible() )
					{
						if ( openList == null )
						{
							openList = new ArrayList<String>();
						}

						openList.add( script.action );
					}
				}

				scriptList.get( i ).frameDispose();
			}
		}

		removeAll();

		if ( detailFrame != null )
		{
			detailFrame.dispose();
		}

		detailList.clear();

		createPanel();

		//  Re-Open any script windows that were already open
		//  before the setup was applied.
		// +-------------------------------------------------+
		if ( openList != null )
		{
			for ( int q=0; q<openList.size(); q++ )
			{
				for ( int i=0; i<scriptList.size(); i++ )
				{
					CCScript script = scriptList.get( i ).find( openList.get( q ) );

					if ( script != null )
					{
						try
						{
							if ( CameraAPI.IsCCParamSupported( script.bits ) )
							{
								actionPerformed(
										new ActionEvent( new JButton(),
														 ActionEvent.ACTION_PERFORMED,
														 script.action ) );
							}
						}
						catch ( Exception e ) {}
					}
				}
			}

			openList.clear();
		}

		MainApp.mainFrame.pack();
		MainApp.mainFrame.repaint();
		repaint();
	}

	// +--------------------------------------------------------------------+
	// |  deviceChanged ( DeviceListener )                                  |
	// +--------------------------------------------------------------------+
	// |  Called when a new device is selected                              |
	// +--------------------------------------------------------------------+
	@Override
	public void deviceChanged( DeviceEvent event )
	{
		//  Kill any open CC script windows. This is necessary
		//  to prevent old/invalid windows from staying around.
		// +---------------------------------------------------+
		if ( scriptList != null )
		{
			for ( int i=0; i<scriptList.size(); i++ )
			{
				scriptList.get( i ).frameDispose();
			}
		}

		if ( detailFrame != null )
		{
			detailFrame.dispose();
		}

		detailList.clear();

		removeAll();

		setNoParamLabel();

		MainApp.mainFrame.pack();
		MainApp.mainFrame.repaint();
		repaint();
	}

	// +--------------------------------------------------------------------+
	// |  actionPerformed ( ActionListener )                                |
	// +--------------------------------------------------------------------+
	@Override
	public void actionPerformed( ActionEvent e )
	{
		// +-----------------------------------------------------+
		// |  List all available CC parameters                   |
		// +-----------------------------------------------------+
		if ( e.getActionCommand().equals( ACTION_DETAILS ) )
		{
			detailFrame.setVisible( true );
		}

		// +-----------------------------------------------------+
		// |  Run a CC parameters script                         |
		// +-----------------------------------------------------+
		else
		{
			CCScript ccs = null;

			try
			{
				for ( int i=0; i<scriptList.size(); i++ )
				{
					ccs = scriptList.get( i ).find( e.getActionCommand() );
		
					if ( ccs != null )
					{
						if ( ccs.frame == null )
						{
							MainInterpreter.clearInterpreter();
			
							ccs.frame = ( CCScriptFrame )
										  MainInterpreter.get().eval( ccs.script );

							if ( ccs.frame != null )
							{
								ccs.frame.setAction( ccs.action );
								ccs.frame.addScriptListener( this );
								ccs.frame.addScriptListener( MainApp.mainFrame.cameraPanel );
								ccs.frame.addScriptListener( MainApp.mainFrame.exposePanel );
								ccs.frame.addScriptListener( MainApp.mainFrame );
							}
						}

						if ( ccs.frame != null )
						{
							ccs.frame.setVisible( true );
						}

						break;
					}
				}
			}
			catch ( Exception ex )
			{
				String msg = "Error while trying to execute action: " + e.getActionCommand();

				if ( ccs != null )
				{
					msg = "Details, SCRIPT: " + ccs.script +
					      " TEXT: " + ccs.text +
					      " ACTION: " + ccs.action;
				}

				MainApp.error( msg );
				MainApp.error( ex );
			}
		}
	}

	protected void setNoParamLabel()
	{
		JLabel noPicLabel =
				new JLabel( new ImageIcon( MainApp.getBitmapPath() + "NoCCParams.gif" ) );

		addComponent( noPicLabel, 0, 0, 0, 0, GridBagConstraints.CENTER, 0, 0, 1, 1 );
	}

	protected void createPanel()
	{
		int i = 0;

		if ( scriptList == null )
		{
			return;
		}

		try
		{
			//
			//  Generate parameter buttons
			// +--------------------------------------------------------------------+
			for ( i=0; i<scriptList.size(); i++ )
			{
				CCScriptGroup grp = scriptList.get( i );
	
				CCScript[] ccs = grp.toArray( new CCScript[ grp.size() ] );

				for ( int j=0; j<ccs.length; j++ )
				{
					ccs[ j ].verify();

					if ( CameraAPI.IsCCParamSupported( ccs[ j ].bits ) )
					{
						if ( !ccs[ j ].infoOnly )
						{
							JButton runButton =
									OwlButtonFactory.createIconButton( "Window2.png",
																		25, 25,
																		ccs[ j ].text + " Options",
																		ccs[ j ].action,
																		this );

							JPanel textPanel = new JPanel();
							textPanel.setLayout( new java.awt.FlowLayout() );
							textPanel.add( new JLabel( ccs[ j ].text ) );
							textPanel.add( ccs[ j ].appliedLabel );

							addComponent( runButton,             0,  0, 0,  0, GridBagConstraints.WEST, i, 1, 1, 1 );
							addComponent( ccs[ j ].appliedChkbx, 0, 10, 0,  3, GridBagConstraints.WEST, i, 0, 1, 1 );
							addComponent( textPanel,             0,  0, 0, 10, GridBagConstraints.WEST, i, 2, 1, 1 );
						}

						detailList.add( ccs[ j ].text );

						break;
					}
				}
			}

			//
			//  Add the details button, that will display ALL parameter settings
			// +--------------------------------------------------------------------+
			if ( getComponentCount() > 0 )
			{
				detailFrame.setText( detailList );

				JButton detailsButton =
						OwlButtonFactory.createIconButton( "Window2.png",
															25, 25,
															"Parameter Details",
															ACTION_DETAILS,
															this );

				addComponent( detailsButton, 0, 0, 0, 0, GridBagConstraints.WEST, i, 1, 1, 1 );
	
				JLabel listAllLabel = new JLabel( "Details" );
				addComponent( listAllLabel, 0, 3, 0, 0, GridBagConstraints.WEST, i, 2, 1, 1 );
			}

			//
			//  Display "No Parameters" icon ( if no parameters )
			// +--------------------------------------------------------------------+
			else
			{
				setNoParamLabel();
			}
		}
		catch ( Exception e )
		{
			setNoParamLabel();
		}
	}

	private class DetailFrame extends OwlFrame
	{
		private static final long serialVersionUID = 8895652614479618565L;

		private JPanel textPanel;
		private ImageIcon icon;

		public DetailFrame()
		{
			super( "All Available Parameters", false );

			getContentPane().setLayout( new BorderLayout() );

			JToolBar toolbar = new JToolBar();
			toolbar.setFloatable( false );
			appendToolbar( toolbar, OwlFrame.CLOSE );

			icon = new ImageIcon( MainApp.getBitmapPath() + "TinyCheck.gif" );

			JPanel panel = new JPanel();
			panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );
			panel.setBackground( Color.white );

			textPanel = new JPanel();
			textPanel.setLayout( new BoxLayout( textPanel, BoxLayout.Y_AXIS ) );
			textPanel.setBackground( Color.white );

			panel.add( Box.createRigidArea( new Dimension( 10,0 ) ) );
			panel.add( textPanel );
			panel.add( Box.createRigidArea( new Dimension( 10,0 ) ) );

			getContentPane().add( toolbar, BorderLayout.PAGE_START, TOOLBAR_INDEX );
			getContentPane().add( panel, BorderLayout.CENTER, CENTER_CONTAINER_INDEX );
			pack();

			//  Center the frame
			// +-----------------------------------------------+
			Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			int xPos = d.width / 2 - getWidth() / 2 - 50;
			int yPos = d.height / 4  - getHeight() / 2;
			setLocation( xPos, yPos );
		}

		public void setText( ArrayList<String> list )
		{
			int ccParamValue = 0;

			textPanel.removeAll();

			try
			{
				ccParamValue = CameraAPI.GetCCParams();
			}
			catch ( Exception e ) { ccParamValue = 0; MainApp.error( e ); }

			JLabel ccParamLabel =
					new JLabel( "<html><b>\tOn Controller Value:</b> 0x" +
								Integer.toHexString( ccParamValue ).toUpperCase() +
								"</html>" );

			textPanel.add( ccParamLabel );
			textPanel.add( Box.createRigidArea( new Dimension( 0,5 ) ) );

			for ( int i=0; i<list.size(); i++ )
			{
				JLabel label = new JLabel( list.get( i ), icon, SwingConstants.LEFT );
				textPanel.add( label );
			}

			textPanel.add( Box.createRigidArea( new Dimension( 0,5 ) ) );

			pack();
		}
	}
}
