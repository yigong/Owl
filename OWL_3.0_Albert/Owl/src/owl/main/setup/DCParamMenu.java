package owl.main.setup;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import owl.CCParams.CCScriptGroup;
import owl.CCParams.CCFileReader;
import owl.CCParams.CCScript;
import owl.CCParams.CCScriptFrame;
import owl.cameraAPI.CameraAPI;
import owl.main.MainApp;
import owl.main.device.DeviceConnect;
import owl.main.device.DeviceEvent;
import owl.main.device.DeviceListener;
import owl.main.scripting.MainInterpreter;



public class DCParamMenu extends JMenu implements SetupListener, DeviceListener, ActionListener
{
	private static final long serialVersionUID = -172224945557192000L;

	private final String PCIe_DEV	= "PCIe";
	private final String PCI_DEV	= "PCI";
	private final String NA_TEXT	= "N/A";
	private final String CTLR_FILE	= MainApp.getAppPath() + "DCScriptList.xml";
	private final String DEV_FILE	= MainApp.getAppPath() + "DevScriptList.xml";

	private ArrayList<CCScriptGroup>	ctlrList;
	private ArrayList<CCScriptGroup>	devList;
	private ArrayList<String>			openList;
	private JMenu						devMenu;
	private JMenu						ctlrMenu;



	// +--------------------------------------------------------------------+
	// |  Constructor                                                       |
	// +--------------------------------------------------------------------+
	public DCParamMenu()
	{
		super( "Developer Parameters .... " );
		super.setIcon( new ImageIcon( MainApp.getBitmapPath() + "wins.gif" ) );

		ctlrList = CCFileReader.readGroupXml( CTLR_FILE );
		devList  = CCFileReader.readGroupXml( DEV_FILE );
		openList = new ArrayList<String>();

		devMenu = ( JMenu )add( new JMenu( "Host Device" ) );
		devMenu.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

		ctlrMenu = ( JMenu )add( new JMenu( "Controller" ) );
		ctlrMenu.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

		setNA( devMenu );
		setNA( ctlrMenu );

		SetupFrame.addSetupListener( this );
		DeviceConnect.addDeviceListener( this );
	}

	// +--------------------------------------------------------------------+
	// |  setupChanged ( SetupListener )                                    |
	// +--------------------------------------------------------------------+
	// |  Called when a new setup has been applied                          |
	// +--------------------------------------------------------------------+
	@Override
	public void setupChanged( SetupEvent se )
	{
		//
		// Kill any open DC script windows. This is necessary
		// to prevent old/invalid windows from staying around.
		//
		clearMenusAndFrames( ctlrMenu, ctlrList );

		createCtlrMenu();

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
		if ( event.deviceName.contains( PCIe_DEV ) )
		{
			clearMenusAndFrames( devMenu, devList );
			createDeviceMenu( PCIe_DEV );
		}

		else if ( event.deviceName.contains( PCI_DEV ) )
		{
			clearMenusAndFrames( devMenu, devList );
			createDeviceMenu( PCI_DEV );
		}

		clearMenusAndFrames( ctlrMenu, ctlrList );

		repaint();
	}

	// +--------------------------------------------------------------------+
	// |  actionPerformed                                                   |
	// +--------------------------------------------------------------------+
	// |  Menu handler, see ActionListener                                  |
	// +--------------------------------------------------------------------+
	@Override
	public void actionPerformed( ActionEvent ae )
	{
		CCScript ccs = null;

		try
		{
			//   Find controller script
			// +----------------------------------------------------------------+
			if ( ctlrList != null )
			{
				for ( int i=0; i<ctlrList.size(); i++ )
				{
					ccs = ctlrList.get( i ).find( ae.getActionCommand() );
					
					if ( ccs != null ){ break; }
				}
			}

			//   Find device script
			// +----------------------------------------------------------------+
			if ( ccs == null && devList != null )
			{
				for ( int i=0; i<devList.size(); i++ )
				{
					ccs = devList.get( i ).find( ae.getActionCommand() );

					if ( ccs != null ){ break; }
				}
			}

			//   Actually use the script
			// +----------------------------------------------------------------+
			if ( ccs != null )
			{
				runTheScript( ccs );

//				if ( ccs.frame == null )
//				{
//					MainInterpreter.clearInterpreter();
//		
//					ccs.frame =
//							( CCScriptFrame )MainInterpreter.get().eval( ccs.script );
//				}
//
////				if ( ccs.frame != null )
//				else
//				{
//					ccs.frame.setVisible( true );
//				}		
			}
		}
		catch ( Exception ex )
		{
			MainApp.error( ex.getMessage() );
		}
	}

	// +--------------------------------------------------------------------+
	// |  runTheScript                                                      |
	// +--------------------------------------------------------------------+
	// |  Runs the specified CCScript. Throws exception on error.           |
	// +--------------------------------------------------------------------+
	protected void runTheScript( CCScript ccs ) throws Exception
	{
		if ( ccs.frame == null )
		{
			MainInterpreter.clearInterpreter();

			ccs.frame =
					( CCScriptFrame )MainInterpreter.get().eval( ccs.script );
		}

		else
		{
			ccs.frame.setVisible( true );
		}		
	}

	// +--------------------------------------------------------------------+
	// |  createDeviceMenu                                                  |
	// +--------------------------------------------------------------------+
	// |  Create the host device interface menu                             |
	// +--------------------------------------------------------------------+
	protected void createDeviceMenu( String sDevice )
	{
		JMenuItem item = null;

		if ( devList != null )
		{
			if ( getMenuComponentCount() > 0 )
			{
				devMenu.removeAll();
			}

			try
			{
				for ( int i=0; i<devList.size(); i++ )
				{
					CCScriptGroup grp = devList.get( i );

					if ( grp.name.toLowerCase().equals( sDevice.toLowerCase() ) )
					{
						CCScript[] ccs = grp.toArray( new CCScript[ grp.size() ] );
		
						for ( int j=0; j<ccs.length; j++ )
						{
							ccs[ j ].verify();
		
							item = devMenu.add( new JMenuItem( ccs[ j ].text + " ... " ) );
							item.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
							item.setActionCommand( ccs[ j ].action );
							item.addActionListener( this );
						}
					}
				}
			}
			catch ( Exception e )
			{
				MainApp.error( e );
			}
	
			if ( devMenu.getMenuComponentCount() <= 0 )
			{
				setNA( devMenu );
			}

			repaint();
		}
	}

	// +--------------------------------------------------------------------+
	// |  createCtlrMenu                                                    |
	// +--------------------------------------------------------------------+
	// |  Create the controller menu                                        |
	// +--------------------------------------------------------------------+
	protected void createCtlrMenu()
	{
		JMenuItem item = null;

		if ( ctlrList != null )
		{
			if ( getMenuComponentCount() > 0 )
			{
				ctlrMenu.removeAll();
			}

			try
			{
				for ( int i=0; i<ctlrList.size(); i++ )
				{
					CCScriptGroup grp = ctlrList.get( i );

					CCScript[] ccs = grp.toArray( new CCScript[ grp.size() ] );
	
					for ( int j=0; j<ccs.length; j++ )
					{
						ccs[ j ].verify();
	
						if ( CameraAPI.IsCCParamSupported( ccs[ j ].bits ) )
						{
							item = ctlrMenu.add( new JMenuItem( ccs[ j ].text + " ... " ) );
							item.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
							item.setActionCommand( ccs[ j ].action );
							item.addActionListener( this );

							//
							// Re-Open any windows that were previously visible
							//
							for ( Object obj : openList.toArray() )
							{
								if ( ccs[ j ].text.equals( ( String )obj ) )
								{
									runTheScript( ccs[ j ] );

									if ( ccs[ j ].frame.isVisible() ) { break; }
								}

//								if ( ccs[ j ].frame == null && ccs[ j ].text.equals( ( String )obj ) )
//								{
//									MainInterpreter.clearInterpreter();
//
//									ccs[ j ].frame =
//										( CCScriptFrame )MainInterpreter.get().eval( ccs[ j ].script );
//								}
//
//								else if ( ccs[ j ].text.equals( ( String )obj ) )
//								{
//									ccs[ j ].frame.setVisible( true );
//
//									break;
//								}		
							}

							break;
						}
					}
				}
			}
			catch ( Exception e ) {}
	
			if ( ctlrMenu.getMenuComponentCount() <= 0 )
			{
				setNA( ctlrMenu );
			}

			repaint();
		}
	}

	// +--------------------------------------------------------------------+
	// |  setNA                                                             |
	// +--------------------------------------------------------------------+
	// |  Removes all components from the specified menu and adds a new     |
	// |  N/A menu item.                                                    |
	// +--------------------------------------------------------------------+
	protected void setNA( JMenu menu )
	{
		menu.removeAll();

		menu.add(
				new JMenuItem( NA_TEXT ) ).setCursor(
						Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
	}

	// +--------------------------------------------------------------------+
	// |  clearMenusAndFrames                                               |
	// +--------------------------------------------------------------------+
	// |  Removes all components from the specified menu, disposes of all   |
	// |  open windows, and adds a new N/A menu item.                       |
	// +--------------------------------------------------------------------+
	protected void clearMenusAndFrames( JMenu menu, ArrayList<CCScriptGroup> list )
	{
		if ( list != null )
		{
			getFrameVisibleList();

			for ( int i=0; i<list.size(); i++ )
			{
				list.get( i ).frameDispose();
			}

			menu.removeAll();

			setNA( menu );

			System.gc();
		}
	}

	// +--------------------------------------------------------------------+
	// |  getFrameVisibleList                                               |
	// +--------------------------------------------------------------------+
	// |  Returns an array list of actions for any DC Param windows that    |
	// |  are currently visible.  This info can be used to re-open any      |
	// |  visible windows after a controller setup.                         |
	// +--------------------------------------------------------------------+
	protected void getFrameVisibleList()
	{
		openList.clear();

		for ( int i=0; i<ctlrList.size(); i++ )
		{
			ArrayList<String> grpOpenList =
									ctlrList.get( i ).getFrameOpenList();

			if ( grpOpenList != null && !grpOpenList.isEmpty() )
			{
				openList.addAll( grpOpenList );
			}
		}
	}
}
