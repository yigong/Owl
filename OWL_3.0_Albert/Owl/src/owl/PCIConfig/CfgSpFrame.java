package owl.PCIConfig;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import owl.cameraAPI.CameraAPI;
import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;
import owl.main.device.DeviceConnect;
import owl.main.device.DeviceEvent;
import owl.main.device.DeviceListener;
import owl.main.owltypes.OwlFrame;



public class CfgSpFrame extends OwlFrame implements ActionListener, DeviceListener
{
	private static final long serialVersionUID = -7772108886927822079L;

	final String UPDATE_ACTION	= "UPDATE";

	protected JTabbedPane m_tabPane	= null;


	public CfgSpFrame()
	{
		super( "Configuration Space Header", false );

		super.setIconImage(
				( new ImageIcon( MainApp.getBitmapPath() + "CfpSpPCI.gif" ).getImage() ) );

		m_tabPane = new JTabbedPane();

		JToolBar toolbar = new JToolBar();
		toolbar.add( super.createNewToolbarButton( UPDATE_ACTION ) );
		appendToolbar( toolbar, OwlFrame.CLOSE );
		addComponent( toolbar, super.TOOLBAR_INDEX );
		addComponent( m_tabPane, super.CENTER_CONTAINER_INDEX );

		setPreferredSize( new Dimension( 750, 600 ) );
		pack();

		OwlUtilities.centerFrame( this );

		DeviceConnect.addDeviceListener( this );

		update();
	}

	public void actionPerformed( ActionEvent ae )
	{
		if ( ae.getActionCommand().equals( UPDATE_ACTION ) )
		{
			update();
		}
		else
		{
			super.actionPerformed( ae );
		}
	}

	public void deviceChanged( DeviceEvent event )
	{
		update();
	}

	public void update()
	{
		int tabIndex = m_tabPane.getSelectedIndex();

		if ( tabIndex < 0 )
		{
			tabIndex = 0;
		}

		if ( !CameraAPI.IsDeviceOpen() )
		{
			MainApp.error( "Not connected to any device!" );

			return;
		}

		m_tabPane.removeAll();

		//
		// Create PCI(e) config space header table
		//
		CfgSpTable table = new CfgSpTable();

		if ( CameraAPI.GetCfgSpCount() <= 0 )
		{
			MainApp.warn( "No configuration space header!" );

			return;
		}

		Object[][] rowData = new Object[ CameraAPI.GetCfgSpCount() ][ 4 ];

		for ( int i=0; i<CameraAPI.GetCfgSpCount(); i++ )
		{
			rowData[ i ][ 0 ] = CameraAPI.GetCfgSpAddr( i );
			rowData[ i ][ 1 ] = CameraAPI.GetCfgSpName( i );
			rowData[ i ][ 2 ] = CameraAPI.GetCfgSpValue( i );
			rowData[ i ][ 3 ] = new JButton();

			String[] sBitList = CameraAPI.GetCfgSpBitList( i );

			if ( sBitList != null )
			{
				JPopupMenu popupMenu = new JPopupMenu();
				popupMenu.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

				for ( int t=0; t<sBitList.length; t++ )
				{
					popupMenu.add( sBitList[ t ] );
				}

				( ( JButton )rowData[ i ][ 3 ] ).setComponentPopupMenu( popupMenu );
				table.setRowPopupMenu( i, ( ( JButton )rowData[ i ][ 3 ] ) );
			}
			else
			{
				JPopupMenu popupMenu = new JPopupMenu();
				popupMenu.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
				popupMenu.add( "No Details" );

				( ( JButton )rowData[ i ][ 3 ] ).setComponentPopupMenu( popupMenu );
				table.setRowPopupMenu( i, ( ( JButton )rowData[ i ][ 3 ] ) );
			}
		}

		table.setData( rowData );

		m_tabPane.add( "PCI Config Space", new JScrollPane( table ) );


		//
		// Create the PCI(e) BAR tables
		//
		for ( int i=0; i<CameraAPI.GetBarCount(); i++ )
		{
			table = new CfgSpTable();

			rowData = new Object[ CameraAPI.GetBarRegCount( i ) ][ 4 ];

			for ( int j=0; j<CameraAPI.GetBarRegCount( i ); j++ )
			{
				rowData[ j ][ 0 ] = CameraAPI.GetBarRegAddr( i, j );
				rowData[ j ][ 1 ] = CameraAPI.GetBarRegName( i, j );
				rowData[ j ][ 2 ] = CameraAPI.GetBarRegValue( i, j );
				rowData[ j ][ 3 ] = new JButton();

				if ( CameraAPI.GetBarRegBitListCount( i, j ) > 0 )
				{
					JPopupMenu popupMenu = new JPopupMenu();

					for ( int k=0; k<CameraAPI.GetBarRegBitListCount( i, j ); k++ )
					{
						popupMenu.add( CameraAPI.GetBarRegBitListDef( i, j, k ) );
					}

					( ( JButton )rowData[ j ][ 3 ] ).setComponentPopupMenu( popupMenu );
					table.setRowPopupMenu( j, ( ( JButton )rowData[ j ][ 3 ] ) );
				}
				else
				{
					JPopupMenu popupMenu = new JPopupMenu();
					popupMenu.add( "No Details" );

					( ( JButton )rowData[ j ][ 3 ] ).setComponentPopupMenu( popupMenu );
					table.setRowPopupMenu( j, ( ( JButton )rowData[ j ][ 3 ] ) );
				}
			}

			table.setData( rowData );

			m_tabPane.add( CameraAPI.GetBarName( i ), new JScrollPane( table ) );
		}

		m_tabPane.setSelectedIndex( tabIndex );
	}
}
