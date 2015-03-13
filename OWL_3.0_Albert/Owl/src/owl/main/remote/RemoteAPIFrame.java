package owl.main.remote;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;

import owl.cameraAPI.CameraAPI;
import owl.gui.utils.OwlButtonFactory;
import owl.main.MainApp;
import owl.main.MainFrame;
import owl.main.device.DeviceConnect;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlInputDialog;
import owl.main.owltypes.OwlMessageBox;
import owl.main.setup.SetupFrame;



public class RemoteAPIFrame extends OwlFrame implements ActionListener, ItemListener, Runnable
{
	private static final long serialVersionUID = -932563324962827412L;

	private final String ADD_SERVER_ACTION	= "Add Server";
	private final String DEL_SERVER_ACTION	= "Delete Server";
	private final String ENABLE_LOG_ACTION	= "Enable Server Logging";

	private final String SERVER_XML_PARSE_RULES	= MainApp.getXMLPath() + "server-rules.xml";
	private final String SERVER_XML_FILE		= MainApp.getAppPath() + "ServerList.xml";

	private JComboBox		serverComboBox;
	private JCheckBox		enableLogChkbox;
	private ConnectButton	connectButton;
	private JLabel			connectedLabel;
	private JPanel			botPanel;
	private String			localDriver;
	private int				dPort;


	// +--------------------------------------------------------------------+
	// |  Constructor                                                       |
	// +--------------------------------------------------------------------+
	public RemoteAPIFrame()
	{
		super( "Remote API", false );

		JToolBar toolbar = new JToolBar();
		super.appendToolbar( toolbar );

		super.addComponent( toolbar, super.TOOLBAR_INDEX );
		super.addComponent( createPanel(), super.CENTER_CONTAINER_INDEX );

		super.pack();

		localDriver	= null;
		dPort		= 5000;

		owl.gui.utils.OwlUtilities.centerFrame( this );
	}

	// +--------------------------------------------------------------------+
	// |  getServer                                                         |
	// +--------------------------------------------------------------------+
	// |  Returns the current server in use.                                |
	// +--------------------------------------------------------------------+
	public String getServer()
	{
		return ( String )serverComboBox.getSelectedItem();
	}

	// +--------------------------------------------------------------------+
	// |  getPort                                                           |
	// +--------------------------------------------------------------------+
	// |  Returns the current port in use.                                  |
	// +--------------------------------------------------------------------+
	public int getPort()
	{
		return dPort;
	}

	// +--------------------------------------------------------------------+
	// |  getHostName                                                       |
	// +--------------------------------------------------------------------+
	// |  Returns the hostname for the current server in use.               |
	// +--------------------------------------------------------------------+
	public String getHostName()
	{
		String sHostName = null;

		try
		{
			InetAddress inetAddr = InetAddress.getByName(
							( String )serverComboBox.getSelectedItem() );

			sHostName = inetAddr.getHostName();
		}
		catch ( UnknownHostException e )
		{
			sHostName = null;
		}

		return sHostName;
	}

	// +--------------------------------------------------------------------+
	// |  switchToRemoteAPI                                                 |
	// +--------------------------------------------------------------------+
	// |  Switches the program libraries to use the remote API.             |
	// +--------------------------------------------------------------------+
	public void switchToRemoteAPI()
	{
		localDriver = DeviceConnect.getCurrentDriver();

		CameraAPI.UseRemoteAPI( true );
	}

	// +--------------------------------------------------------------------+
	// |  switchToLocalAPI                                                  |
	// +--------------------------------------------------------------------+
	// |  Switches the program libraries to use the local API.              |
	// +--------------------------------------------------------------------+
	public void switchToLocalAPI()
	{
		Runnable r = new Runnable()
		{
			public void run()
			{
				Icon msgIcon = new ImageIcon( MainApp.getBitmapPath() + "ServConn.gif" );
				OwlMessageBox msgBox = new OwlMessageBox( "Switching to local API", msgIcon );
				msgBox.start();

				MainApp.mainFrame.setTitle( MainFrame.DEFAULT_TITLE );
		
				connectedLabel.setText( "NONE" );

				enableLogChkbox.setEnabled( false );
		
				CameraAPI.UseRemoteAPI( false );
		
				if ( localDriver != null )
				{
					MainApp.info( "Connecting to driver -> " + localDriver );
					DeviceConnect.callDeviceListeners( localDriver );
				}

				if ( CameraAPI.IsControllerConnected() )
				{
					SetupFrame.callSetupListeners(
								MainApp.mainFrame.cameraPanel.setupFrame );
				}

				msgBox.stop();
			}
		};

		( new Thread( r ) ).start();
	}

	// +--------------------------------------------------------------------+
	// |  itemStateChanged                                                  |
	// +--------------------------------------------------------------------+
	// |  ItemListener override.                                            |
	// +--------------------------------------------------------------------+
	@Override
	public void itemStateChanged( ItemEvent ie )
	{
		JCheckBox src = ( JCheckBox )ie.getSource();

		//  Enable Log
		// +--------------------------------------------------+
		if ( src.getText().equals( ENABLE_LOG_ACTION ) )
		{
			try
			{
				if ( ie.getStateChange() == ItemEvent.SELECTED )
				{
					CameraAPI.EnableServerLog( true );
				}
		
				else
				{
					CameraAPI.EnableServerLog( false );
				}
			}
			catch ( Exception e )
			{
				MainApp.error( e.getMessage() );
			}
		}
	}

	// +--------------------------------------------------------------------+
	// |  actionPerformed                                                   |
	// +--------------------------------------------------------------------+
	// |  ItemListener override.                                            |
	// +--------------------------------------------------------------------+
	@Override
	public void actionPerformed( ActionEvent ae )
	{
		//  Connect to Server
		// +--------------------------------------------------+
		if ( ae.getActionCommand().equals( ConnectButton.CONNECT_ACTION ) )
		{
			switchToRemoteAPI();

			( new Thread( this ) ).start();
		}

		//  Disconnect Server
		// +--------------------------------------------------+
		if ( ae.getActionCommand().equals( ConnectButton.DISCONNECT_ACTION ) )
		{
			try
			{
				connectButton.setConnect();

				CameraAPI.DisconnectServer();

				switchToLocalAPI();
			}
			catch ( Exception e )
			{
				MainApp.error( e.getMessage() );
			}
		}

		//  Add a New Server to the List
		// +--------------------------------------------------+
		else if ( ae.getActionCommand().equals( ADD_SERVER_ACTION ) )
		{
			String[] labels = { "IP Address", "Description" };

			OwlInputDialog inDialog = new OwlInputDialog();

			int choice = inDialog.showDialog( this,
											  "Add Server",
											  labels );

			if ( choice == OwlInputDialog.OK )
			{
				String[] values = inDialog.getInputValues();

				serverComboBox.addItem(
						formatServStr( values[ 0 ], values[ 1 ] ) );

				writeXml( new File( SERVER_XML_FILE ), values );
			}
		}

		//  Delete a Server From the List
		// +--------------------------------------------------+
		else if ( ae.getActionCommand().equals( DEL_SERVER_ACTION ) )
		{
			Vector<String> vServers = new Vector<String>();

			for ( int i=0; i<serverComboBox.getItemCount(); i++ )
			{
				vServers.add(
						( String )serverComboBox.getItemAt( i ) );
			}

			String[] serverArray =
						vServers.toArray(
							new String[ serverComboBox.getItemCount() ] );

			String itemToDel =
					( String )JOptionPane.showInputDialog(
									this,
							        "Select Server To Delete From List",
							        "Delete Server",
							        JOptionPane.QUESTION_MESSAGE,
							        new ImageIcon( MainApp.getBitmapPath() + "Delete.gif" ),
							        serverArray,
							        serverComboBox.getSelectedItem() );

			if ( itemToDel != null )
			{
				serverComboBox.removeItem( itemToDel );

				writeXml( new File( SERVER_XML_FILE ), null );
			}
		}

		//  Pass to Super Class
		// +--------------------------------------------------+
		else
		{
			super.actionPerformed( ae );
		}
	}

	// +--------------------------------------------------------------------+
	// |  run                                                               |
	// +--------------------------------------------------------------------+
	// |  Runnable override.                                                |
	// +--------------------------------------------------------------------+
	@Override
	public void run()
	{
		connectToServer( ( String )serverComboBox.getSelectedItem(),
						  true,
						  ( MainApp.isApiOk() ? false : true ) );
	}

	// +--------------------------------------------------------------------+
	// |  connectToServer                                                   |
	// +--------------------------------------------------------------------+
	// |  Connects to a remote API server.                                  |
	// +--------------------------------------------------------------------+
	public void connectToServer( String sAPIServer, boolean bDeviceConnect, boolean bInitAPI )
	{
		Icon icon = null;

		if ( sAPIServer != null )
	  	{
			try
			{
				//  Connect To Server
				// +----------------------------------------------+
				String msg = "Establishing server connection [ " +
							  sAPIServer + ":" + dPort +
							  " ] ... May take awhile!";

				icon = new ImageIcon( MainApp.getBitmapPath() + "loading26.gif" );

				OwlMessageBox svrBox = new OwlMessageBox( msg, icon, 45000 );
				svrBox.start();

				try
				{
					CameraAPI.ConnectToServer( sAPIServer, dPort );
				}
				catch ( Exception e )
				{
					svrBox.stop();
					throw e;
				}
				finally
				{
					svrBox.stop();
				}

				//  Connect To Device
				// +----------------------------------------------+
		  		if ( bDeviceConnect )
		  		{
		  			DeviceConnect.connectToDevice( false );
		  		}

				//  Initialize API Constants
				// +----------------------------------------------+
 		  		if ( bInitAPI )
 		  		{
					OwlMessageBox apiBox =
							new OwlMessageBox( "Initializing API", icon );

					apiBox.start();
	
					try
					{
						CameraAPI.GetAPIConstants();
						CameraAPI.VerifyFieldInitialization();
					}
					catch ( Exception e )
					{
						apiBox.stop();

						throw e;
					}
					finally
					{
						apiBox.stop();
					}
 		  		}

 				connectButton.setDisConnect();
 				connectedLabel.setText( sAPIServer );
 				enableLogChkbox.setEnabled( true );

				if ( MainApp.mainFrame != null )
				{
					MainApp.mainFrame.setTitle( MainFrame.DEFAULT_TITLE +
												" - REMOTE [ "			+
												getHostName() + " : "	+
												getServer() + " : "		+
												getPort() + " ]" );
				}
			}
			catch ( Exception e )
			{
				MainApp.error( e.getMessage() );
				MainApp.info( "Reverting back to \"LOCAL\" mode!" );
			}
	  	}
	  	else
	  	{
	  		MainApp.error( "No remote API server specified!" );
	  	}
	}

	// +--------------------------------------------------------------------+
	// |  createPanel                                                       |
	// +--------------------------------------------------------------------+
	// |  Create the primary components.                                    |
	// +--------------------------------------------------------------------+
	private JPanel createPanel()
	{
		//  Create top panel
		// +---------------------------------------------------------------+
		JPanel topPanel = new JPanel();
		topPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );

		connectedLabel = new JLabel( "None" );

		topPanel.add( new JLabel( "<html><font color=\"#903428\">CONNECTED TO: </font></html>" ) );
		topPanel.add( connectedLabel );
		
		//  Create bottom panel
		// +---------------------------------------------------------------+
		botPanel = new JPanel();
		botPanel.setBorder( javax.swing.BorderFactory.createTitledBorder( "Server Options" ) );
		botPanel.setLayout( gbl );

		serverComboBox = new JComboBox( getServerList() );
		serverComboBox.addActionListener( this );

		JButton addButton = OwlButtonFactory.createIconButton( "Plus2.gif",
															   "Add Server",
															    ADD_SERVER_ACTION,
															    this );

		JButton delButton = OwlButtonFactory.createIconButton( "Minus2.gif",
															   "Delete Server",
															    DEL_SERVER_ACTION,
															    this );

		connectButton = new ConnectButton();
		connectButton.addActionListener( this );

		enableLogChkbox = new JCheckBox( ENABLE_LOG_ACTION );
		enableLogChkbox.addItemListener( this );
		enableLogChkbox.setEnabled( false );

		addComponent( botPanel, serverComboBox,  8, 8, 7, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 0, 0, 1, 1 );
		addComponent( botPanel, addButton,       0, 8, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 1, 1, 1 );
		addComponent( botPanel, delButton,       0, 4, 0, 8, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 2, 1, 1 );
		addComponent( botPanel, connectButton,   0, 8, 7, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 0, 1, 1 );
		addComponent( botPanel, enableLogChkbox, 0, 8, 8, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 2, 0, 1, 3 );

		//  Combine all panels
		// +---------------------------------------------------------------+
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
		panel.add( topPanel );
		panel.add( botPanel );

		return panel;
	}

	// +--------------------------------------------------------------------+
	// |  getRemoteIPs                                                      |
	// +--------------------------------------------------------------------+
	// |  Returns the list of server IP's listed in startup.ini             |
	// +--------------------------------------------------------------------+
	private String formatServStr( String serverName, String description )
	{
		return ( serverName + " - " + description );
	}

	// +--------------------------------------------------------------------+
	// |  getRemoteIPs                                                      |
	// +--------------------------------------------------------------------+
	// |  Returns the list of server IP's listed in startup.ini             |
	// +--------------------------------------------------------------------+
	private Vector<String> getServerList()
	{
		Vector<String> vServers = new Vector<String>();

		try
		{
			ArrayList<ServerElement> serverList =
							readXml( new java.io.File( SERVER_XML_FILE ) );

			for ( int i=0; i<serverList.size(); i++ )
			{
				vServers.add(
						formatServStr( serverList.get( i ).getIP(),
									   serverList.get( i ).getDescription() ) );
			}
		}
		catch ( Exception e )
		{
			JOptionPane.showMessageDialog(
									null,
									"( RemoteAPIFrame::getServerList() ): " +
									"Make sure server xml file is readable " +
									"( " + SERVER_XML_FILE + " ) " +
									e.getMessage() );
		}

		return vServers;
	}

	// +--------------------------------------------------------------------+
	// |  writeXml                                                          |
	// +--------------------------------------------------------------------+
	// |  Write all the server data to an xml file. File is overwritten.    |
	// +--------------------------------------------------------------------+
	private void writeXml( File xmlFile, String[] values )
	{
		FileWriter fw = null;

		try
		{
			if ( !xmlFile.getAbsolutePath().contains( ".xml" ) )
			{
				xmlFile = new File( xmlFile.getAbsolutePath() + ".xml" );
			}

			fw = new FileWriter( xmlFile );
			fw.append( "<?xml version=\"1.0\"?>\n\n" );
			fw.append( "<ServerList>\n" );

			if ( values != null )
			{
				fw.append( "\n\t<server>" );
				fw.append( "\n\t\t<ip>" + values[ 0 ] + "</ip>" );
				fw.append( "\n\t\t<description>" + values[ 1 ] + "</description>" );
				fw.append( "\n\t</server>\n" );
			}

			for ( int i=0; i<serverComboBox.getItemCount(); i++ )
			{
				String[] tokens = ( ( String )serverComboBox.getItemAt( i ) ).split( "-" );
	
				fw.append( "\n\t<server>" );
				fw.append( "\n\t\t<ip>" + tokens[ 0 ].trim() + "</ip>" );
				fw.append( "\n\t\t<description>" + tokens[ 1 ].trim() + "</description>" );
				fw.append( "\n\t</server>\n" );
			}
	
			fw.append( "\n</ServerList>\n\n" );
			fw.close();
		}
		catch ( Exception e )
		{
			MainApp.error( e.getMessage() );
		}
		finally
		{
			try
			{
				if ( fw != null ) { fw.close(); }
			}
			catch ( Exception e ) {}
		}
	}

	// +--------------------------------------------------------------------+
	// |  readXml                                                           |
	// +--------------------------------------------------------------------+
	// |  Reads the and parses the server xml file                          |
	// +--------------------------------------------------------------------+
	private ArrayList<ServerElement> readXml( File xmlFile )
	{
		ArrayList<ServerElement> list = null;

		try
		{
			java.net.URL rules = ( new File( SERVER_XML_PARSE_RULES ) ).toURI().toURL();
			Digester digester = DigesterLoader.createDigester( rules );
			digester.setValidating( false );

			// IMPORTANT - Disable the logger for the digester during parsing, or
			// debug statements will be printed to the console and/or log window.
			// This is because debug level is turned on in log4j for the use of
			// the debug() function. Get the root logger and set its log level to
			// Level.INFO, with will prevent debug messages from the digester,
			// but allow info ones ( although I don't think there are any ).
			org.apache.log4j.Logger xmlLogger = org.apache.log4j.Logger.getRootLogger();
			xmlLogger.setLevel( org.apache.log4j.Level.INFO );

			org.apache.commons.logging.impl.Log4JLogger newLogger
						= new org.apache.commons.logging.impl.Log4JLogger( xmlLogger );

			digester.setLogger( newLogger );
			digester.setSAXLogger( newLogger );

			InputStream input = new FileInputStream( xmlFile );
			list = ( ArrayList<ServerElement> )digester.parse( input );
		}
		catch ( Exception e )
		{
			MainApp.error( "Failed to properly parse FITS XML file! " + e.getMessage() );

			list = null;
		}

		return list;
	}

	private class ConnectButton extends JButton
	{
		private static final long serialVersionUID = 2957067539068523459L;

		public static final String CONNECT_ACTION		= "CONNECT";
		public static final String DISCONNECT_ACTION	= "DISCONNECT";

		private Color m_connBkgColor;
		private Color m_discBkgColor;
		private Color m_connTxtColor;
		private Color m_discTxtColor;

		public ConnectButton()
		{
			super();

			setFocusPainted( false );

			m_connBkgColor = getBackground();
			m_discBkgColor = new Color( 0x90, 0x34, 0x28 );

			m_connTxtColor = Color.black;
			m_discTxtColor = Color.white;

			setConnect();
		}

		public void setConnect()
		{
			setText( CONNECT_ACTION );
			setActionCommand( CONNECT_ACTION );
			setBackground( m_connBkgColor );
			setForeground( m_connTxtColor );
		}

		public void setDisConnect()
		{
			connectButton.setText( DISCONNECT_ACTION );
			connectButton.setActionCommand( DISCONNECT_ACTION );
			connectButton.setBackground( m_discBkgColor );
			connectButton.setForeground( m_discTxtColor );
		}
	}
}
