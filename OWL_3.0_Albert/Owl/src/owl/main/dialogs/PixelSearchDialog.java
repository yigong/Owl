package owl.main.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import owl.cameraAPI.CameraAPI;
import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;
import owl.main.owltypes.OwlBoldButton;
import owl.main.owltypes.OwlDialog;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlInfoTable;
import owl.main.owltypes.OwlNumberField;


public class PixelSearchDialog extends OwlDialog implements ActionListener, Runnable
{
	private static final long serialVersionUID = 8950037338035450710L;

	private final String[] COL_NAMES	= { "ROW", "COL", "PIXEL", "VALUE" };

	private final String SEARCH_ACTION	= "Search";
	private final String STOP_ACTION	= "Stop";

	private final int MAX_ERROR_COUNT	= 2048;

	private OwlNumberField	m_pixelTxtfld;
	private OwlInfoTable	m_table;
	private OwlBoldButton	m_searchButton;
	private boolean			m_stop;


	public PixelSearchDialog( OwlFrame owner )
	{
		super( owner, "Pixel Search" );

		JPanel inPanel = new JPanel();
		inPanel.setLayout( new FlowLayout( FlowLayout.CENTER, 5, 5 ) );

		JLabel searchLabel = new JLabel( "Pixel Value: " );
		inPanel.add( searchLabel );

		m_pixelTxtfld = new OwlNumberField( "", 8 );
		inPanel.add( m_pixelTxtfld );

		JRadioButton hexRButton = new JRadioButton( "hex", true );
		inPanel.add( hexRButton );

		JRadioButton decRButton = new JRadioButton( "dec" );
		inPanel.add( decRButton );

		ButtonGroup radixGrp = new ButtonGroup();
		radixGrp.add( hexRButton );
		radixGrp.add( decRButton );

		m_searchButton = new OwlBoldButton( SEARCH_ACTION, Color.red );
		m_searchButton.setActionCommand( SEARCH_ACTION );
		m_searchButton.addActionListener( this );
		inPanel.add( m_searchButton );

		String[][] rowData = null;

		m_table = new OwlInfoTable();
		setTableData( rowData );

		JScrollPane jScrollPane = new JScrollPane( m_table );
		jScrollPane.setPreferredSize( new Dimension( 210, 650 ) );

		getContentPane().setLayout( new java.awt.BorderLayout() );
		getContentPane().add( inPanel, java.awt.BorderLayout.PAGE_START );
		getContentPane().add( jScrollPane, java.awt.BorderLayout.CENTER );

		m_stop = false;

		pack();

		OwlUtilities.centerFrame( this );

		loadPreferences();
	}

	public void actionPerformed( ActionEvent ae )
	{
		if ( ae.getActionCommand().equals( SEARCH_ACTION ) )
		{
			if ( !m_pixelTxtfld.isEmpty() )
			{
				( new Thread( this ) ).start();
			}
			else
			{
				MainApp.error( "Please enter a search value!" );
			}
		}

		if ( ae.getActionCommand().equals( STOP_ACTION ) )
		{
			m_stop = true;
		}

		else
		{
			super.actionPerformed( ae );
		}
	}

	public void run()
	{
		try
		{
			m_searchButton.setActionCommand( STOP_ACTION );
			m_searchButton.setText( STOP_ACTION );
			m_searchButton.setColored();
			m_stop = false;

			setTableData( null );

			int rows = CameraAPI.GetImageRows();
			int cols = CameraAPI.GetImageCols();

			String[][] data = new String[ cols ][ COL_NAMES.length ];
			int index = 0;

			long lStart = System.currentTimeMillis();

			for ( int r=0; r<rows; r++ )
			{
				if ( m_stop ) { break; }

//				System.out.println( "Searching row #" + r );

				Point2D.Float[] rowData = CameraAPI.GetImageRow( r,
														   		 0,
														   		 cols,
														   		 rows,
																 cols );

				if ( m_stop ) { break; }

				if ( rowData != null )
				{
					for ( Point2D.Float pixel : rowData )
					{
						if ( m_stop ) { break; }
	
						if ( ( int )pixel.y == m_pixelTxtfld.getInt( 16 ) )
						{
							data[ index ][ 0 ] = Integer.toString( r );
							data[ index ][ 1 ] = Integer.toString( ( int )pixel.x );
							data[ index ][ 2 ] = Integer.toString( ( int )pixel.x + ( r * cols ) );
							data[ index ][ 3 ] = "0x" + Integer.toHexString( ( int )pixel.y );

							index++;

							if ( index >= data.length || index >= MAX_ERROR_COUNT )
							{
								m_stop = true;
							}
						}

						if ( m_stop ) { break; }
					}
				}

				if ( m_stop ) { break; }

				Thread.sleep( 1 );
			}

			long lEnd = System.currentTimeMillis();
			MainApp.debug( "Time to read: " + ( lEnd - lStart ) + "ms " + ( ( lEnd - lStart ) / 1000.f ) + " sec" );

			if ( index > 0 )
			{
				setTableData( data );
			}
		}
		catch ( Exception e )
		{
			MainApp.error( e );
		}
		finally
		{
			m_searchButton.setActionCommand( SEARCH_ACTION );
			m_searchButton.setText( SEARCH_ACTION );
			m_searchButton.setBlack();
		}
	}

	private void setTableData( Object[][] data )
	{
		m_table.setTableData( COL_NAMES, data );
		m_table.setColumnWidth( 0, 65 );
		m_table.setColumnWidth( 1, 65 );
	}
}
