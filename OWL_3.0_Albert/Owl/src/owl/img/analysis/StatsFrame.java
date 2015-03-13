package owl.img.analysis;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import owl.cameraAPI.CameraAPI;
import owl.main.MainApp;
import owl.main.owltypes.OwlNumberField;


public class StatsFrame extends AnalysisFrame
{
	private static final long serialVersionUID = -5030866633661826706L;

	protected OwlNumberField m_rowStartTxtfld;
	protected OwlNumberField m_rowEndTxtfld;
	protected OwlNumberField m_colStartTxtfld;
	protected OwlNumberField m_colEndTxtfld;


	public StatsFrame()
	{
		super( "Image Statistics" );

		super.addComponent( createCtrlPanel(),
							super.SOUTH_CONTAINER_INDEX );

		super.setSize( new Dimension( 825, 165 ) );

		super.pack();

		loadPreferences();

		( new Thread( new ReadCtlrRunnable() ) ).start();
	}

	@Override
	public void readCtlr()
	{
		try
		{
			if ( CameraAPI.IsDeviceOpen() && CameraAPI.IsControllerConnected() )
			{
				MainApp.infoStart( "Reading image dimensions from controller" );

				int dRowEnd = CameraAPI.GetImageRows();
				int dColEnd = CameraAPI.GetImageCols();

				m_rowStartTxtfld.setValue( 0 );
				m_rowEndTxtfld.setValue( dRowEnd );

				m_colStartTxtfld.setValue( 0 );
				m_colEndTxtfld.setValue( dColEnd );

				setImageRowsValue( dRowEnd );
				setImageColsValue( dColEnd );

				MainApp.infoEnd();
			}
			else
			{
				MainApp.error( "( ReadCtlrRunnable ): " +
							   "No device or controller connected!" );
			}
		}
		catch ( Exception e )
		{
			MainApp.error( e );
		}

		super.pack();
	}

	@Override
	public void readDS9()
	{
		try
		{
			MainApp.info( "Reading image dimensions from DS9 ...." );
			MainApp.warn( "Requires POINT shape to be manually selected within DS9!" );

			long[] lCoords = getDS9Box();
			
			if ( m_bStop ) { m_runButton.setBlack( RUN_ACTION ); return; }
			
			long lRowStart = ( lCoords[ BOX_CENTER_ROW ] - ( lCoords[ BOX_ROWS ] / 2 ) );
			long lRowEnd   = ( lCoords[ BOX_CENTER_ROW ] + ( lCoords[ BOX_ROWS ] / 2 ) );
			long lColStart = ( lCoords[ BOX_CENTER_COL ] - ( lCoords[ BOX_COLS ] / 2 ) );
			long lColEnd   = ( lCoords[ BOX_CENTER_COL ] + ( lCoords[ BOX_COLS ] / 2 ) );
			
			m_rowStartTxtfld.setText( Long.toString( lRowStart ) );
			m_rowEndTxtfld.setText( Long.toString( lRowEnd ) );
			
			m_colStartTxtfld.setText( Long.toString( lColStart ) );
			m_colEndTxtfld.setText( Long.toString( lColEnd ) );
			
			setImageRowsValue( MainApp.mainFrame.cameraPanel.getRowLabelValue() );
			setImageColsValue( MainApp.mainFrame.cameraPanel.getColLabelValue() );
		}
		catch ( Exception e )
		{
			MainApp.error( e );
		}
	}

	@Override
	public void run()
	{
		Thread.currentThread().setName( "Owl - StatsFrame" );

		m_runButton.setColored( ABORT_ACTION );

		try
		{
			if ( m_bStop ) { m_runButton.setBlack( RUN_ACTION ); return; }

			super.getStats( m_rowStartTxtfld.getInt(),
							m_rowEndTxtfld.getInt(),
							m_colStartTxtfld.getInt(),
							m_colEndTxtfld.getInt(),
							super.getImageRowsValue(),
							super.getImageColsValue() );

			if ( m_bStop ) { m_runButton.setBlack( RUN_ACTION ); return; }
		}
		catch ( NumberFormatException nfe )
		{
			MainApp.error( "( StatsFrame ): " + nfe.getMessage() );
		}
		catch ( Exception e )
		{
			MainApp.error( "( StatsFrame ): " + e.getMessage() );
		}
		finally
		{
			m_runButton.setBlack( RUN_ACTION );
		}

		super.validate();
		super.pack();
	}

	private JPanel createCtrlPanel()
	{
		JPanel rPanel = new JPanel();
		rPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );

		m_rowStartTxtfld = new OwlNumberField( 0, 6 );
		m_rowEndTxtfld   = new OwlNumberField( 10, 6 );

		rPanel.add( new JLabel( "<html><b>ROW</b></html>" ) );
		rPanel.add( javax.swing.Box.createHorizontalStrut( 2 ) );
		rPanel.add( new JLabel( "start: " ) );
		rPanel.add( m_rowStartTxtfld );
		rPanel.add( javax.swing.Box.createHorizontalStrut( 2 ) );
		rPanel.add( new JLabel( "end: " ) );
		rPanel.add( m_rowEndTxtfld );

		JPanel cPanel = new JPanel();
		cPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );

		m_colStartTxtfld = new OwlNumberField( 0, 6 );
		m_colEndTxtfld   = new OwlNumberField( 10, 6 );

		cPanel.add( new JLabel( "<html><b>COL</b></html>" ) );
		cPanel.add( javax.swing.Box.createHorizontalStrut( 2 ) );
		cPanel.add( new JLabel( "start: " ) );
		cPanel.add( m_colStartTxtfld );
		cPanel.add( javax.swing.Box.createHorizontalStrut( 2 ) );
		cPanel.add( new JLabel( "end: " ) );
		cPanel.add( m_colEndTxtfld );

		JPanel panel = new JPanel();
		panel.setLayout( super.gbl );

		addComponent( panel, createStatsPanel(),  15, 5, 15, 0, 0, 0, 1, 3 );
		addComponent( panel, rPanel,               0, 0,  0, 0, 1, 0, 1, 1 );
		addComponent( panel, cPanel,               0, 0,  0, 0, 1, 1, 1, 1 );
		addComponent( panel, getCoordsSrcPanel(),  0, 0,  0, 0, 1, 2, 1, 1 );

		return panel;
	}
}
