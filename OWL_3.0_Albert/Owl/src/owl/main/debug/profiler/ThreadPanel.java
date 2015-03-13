package owl.main.debug.profiler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import owl.main.MainApp;


public class ThreadPanel extends PropertyPanel implements Runnable, ActionListener
{
	private static final long serialVersionUID = -1602440392367049322L;

	protected final String LIVE_ACTION	= "Live Update";
	protected final String STOP_ACTION	= "Stop Update";

	protected JButton m_liveButton;
	protected Thread  m_thread;
	protected boolean m_stop;

	public ThreadPanel()
	{
		super( ( String[] )null );

		m_thread = null;

		m_liveButton = new JButton( LIVE_ACTION );
		m_liveButton.addActionListener( this );

		add( m_liveButton );

		m_colNames = new String[ 3 ];
		m_colNames[ 0 ] = "ID";
		m_colNames[ 1 ] = "Name";
		m_colNames[ 2 ] = "State";

		Object[][] data = { { "", "", "" } };
		setData( data );
	}

	@Override
	public void actionPerformed( ActionEvent ae )
	{
		if ( ae.getActionCommand().equals( LIVE_ACTION ) )
		{
			m_stop = false;

			if ( m_thread == null )
			{
				m_thread = new Thread( this );
				m_thread.setName( "Owl - ThreadProfiler" );
				m_thread.setPriority( Thread.MAX_PRIORITY - 1 );
			}

			m_thread.start();
		}

		else if ( ae.getActionCommand().equals( STOP_ACTION ) )
		{
			m_stop = true;

			try
			{
				m_thread.join( 500 );
			}
			catch ( Exception e ) {}

			System.gc();
		}
	}

	@Override
	public void run()
	{
		m_liveButton.setText( STOP_ACTION );

		while ( !m_stop )
		{
			setData( MainApp.getThreadInfo() );
		}

		m_liveButton.setText( LIVE_ACTION );
	}

	public void setData( Object[][] data )
	{
		m_table.setTableData( m_colNames, data );
		m_table.setColumnWidth( 0, 60 );
		m_table.setColumnWidth( 1, 225 );
	}
}
