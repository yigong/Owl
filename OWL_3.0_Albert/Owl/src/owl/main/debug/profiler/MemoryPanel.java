package owl.main.debug.profiler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;

import owl.main.MainApp;


public class MemoryPanel extends JPanel implements Runnable, ActionListener
{
	private static final long serialVersionUID = -3729641228491165451L;

	protected final String LIVE_ACTION	= "Live Update";
	protected final String STOP_ACTION	= "Stop Update";

	protected JLabel  m_totalMemLabel;
	protected JLabel  m_maxMemLabel;
	protected JLabel  m_freeMemLabel;
	protected JButton m_liveButton;
	protected Thread  m_thread;
	protected boolean m_stop;

	public MemoryPanel()
	{
		addComponents();

		m_thread = null;
	}

	private void addComponents()
	{
		setBackground( java.awt.Color.white );

		String sTotalMem = "";
		String sMaxMem   = "";
		String sFreeMem  = "";

		try
		{
			sTotalMem = String.format( "%d bytes [ %.2f MB ]",
									   Runtime.getRuntime().totalMemory(),
									   ( Runtime.getRuntime().totalMemory() / 1.E6 ) );

			sMaxMem   = String.format( "%d bytes [ %.2f MB ]",
									   Runtime.getRuntime().maxMemory(),
									   ( Runtime.getRuntime().maxMemory() / 1.E6 ) );

			sFreeMem  = String.format( "%d bytes [ %.2f MB ]",
									   Runtime.getRuntime().freeMemory(),
									   ( Runtime.getRuntime().freeMemory() / 1.E6 ) );
		}
		catch ( Exception e )
		{
			MainApp.error( "Failed to read JVM memory status!" );
			MainApp.error( e.getMessage() );
		}

		JLabel totalMemLabel = new JLabel( "Java TOTAL Memory: " );
		JLabel maxMemLabel   = new JLabel( "Java MAX Memory: " );
		JLabel freeMemLabel  = new JLabel( "Java FREE Memory: " );

		m_totalMemLabel = new JLabel( sTotalMem );
		m_maxMemLabel   = new JLabel( sMaxMem );
		m_freeMemLabel  = new JLabel( sFreeMem );

		m_liveButton = new JButton( LIVE_ACTION );
		m_liveButton.addActionListener( this );

		GroupLayout layout = new GroupLayout( this );

		setLayout( layout );

		// Turn on automatically adding gaps between components
		layout.setAutoCreateGaps( true );

		// Turn on automatically creating gaps between components that touch
		// the edge of the container and the container.
		layout.setAutoCreateContainerGaps( true );

		// Create a sequential group for the horizontal axis.
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup( layout.createParallelGroup().addComponent( totalMemLabel ).
													  addComponent( maxMemLabel ).
													  addComponent( freeMemLabel ).
													  addComponent( m_liveButton ) );

		hGroup.addGroup( layout.createParallelGroup().addComponent( m_totalMemLabel ).
													  addComponent( m_maxMemLabel ).
													  addComponent( m_freeMemLabel ) );
		layout.setHorizontalGroup( hGroup );

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		  
		vGroup.addGroup( layout.createParallelGroup( Alignment.BASELINE ).addComponent( totalMemLabel ).
																		  addComponent( m_totalMemLabel ) );

		vGroup.addGroup( layout.createParallelGroup( Alignment.BASELINE ).addComponent( maxMemLabel ).
																		  addComponent( m_maxMemLabel ) );

		vGroup.addGroup( layout.createParallelGroup( Alignment.BASELINE ).addComponent( freeMemLabel ).
																		  addComponent( m_freeMemLabel ) );

		vGroup.addGroup( layout.createParallelGroup( Alignment.BASELINE ).addComponent( m_liveButton ) );

		layout.setVerticalGroup( vGroup );
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
				m_thread.setName( "Owl - MemoryProfiler" );
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
		Runtime runtime = Runtime.getRuntime();

		m_liveButton.setText( STOP_ACTION );

		while ( !m_stop )
		{
			try
			{
				m_totalMemLabel.setText( String.format( "%d bytes [ %.2f MB ]",
										 runtime.totalMemory(),
										 ( runtime.totalMemory() / 1.E6 ) ) );

				m_maxMemLabel.setText( String.format( "%d bytes [ %.2f MB ]",
									   runtime.maxMemory(),
									   ( runtime.maxMemory() / 1.E6 ) ) );

				m_freeMemLabel.setText( String.format( "%d bytes [ %.2f MB ]",
										runtime.freeMemory(),
										( runtime.freeMemory() / 1.E6 ) ) );

				Thread.sleep( 500 );
			}
			catch ( Exception e ) {}
		}

		m_liveButton.setText( LIVE_ACTION );
	}
}
