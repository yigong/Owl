package owl.main.debug.profiler;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import owl.cameraAPI.CameraAPI;
import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;
import owl.main.device.DeviceEvent;
import owl.main.device.DeviceListener;
import owl.main.owltypes.OwlFrame;
import owl.main.scripting.MainInterpreter;



// +--------------------------------------------------------------------------------+
// |  ProfilerFrame Class                                                           |
// +--------------------------------------------------------------------------------+
// |  Displays tabular information about the ARC computer interface board, Java     |
// |  environment, scripting info, etc.                                             |
// +--------------------------------------------------------------------------------+
public class ProfilerFrame extends OwlFrame implements ActionListener, DeviceListener
{
	private static final long serialVersionUID = 3011698283867775047L;

	private final String UPDATE_ACTION	= "UPDATE";

	private JTabbedPane		m_tabbedPane;
	private ThreadPanel		m_threadPanel;
	private MemoryPanel		m_memoryPanel;
	private PropertyPanel	m_systemPanel;
	private PropertyPanel	m_scriptPanel;
	private PropertyPanel	m_libraryPanel;
	private PropertyPanel	m_devicePanel;

	private final Object[][] NO_INFO
				= { { "N/A", "No Additional Information Available" } };

	// +---------------------------------------------------------------------------+
	// |  Constructor                                                              |
	// +---------------------------------------------------------------------------+
	public ProfilerFrame()
	{
		super( "Owl Profiler", false );

		JToolBar toolbar = new JToolBar();
		toolbar.add( super.createNewToolbarButton( UPDATE_ACTION ) );
		appendToolbar( toolbar, OwlFrame.CLOSE );
		addComponent( toolbar, super.TOOLBAR_INDEX );

		m_tabbedPane = new JTabbedPane();
		addComponent( m_tabbedPane, super.CENTER_CONTAINER_INDEX );

		m_systemPanel = new PropertyPanel( MainApp.getProgramInfo() );
		m_tabbedPane.addTab( "Program Info", m_systemPanel );

		Object[][] propList = CameraAPI.GetProperties();

		if ( propList == null )
		{
			propList = NO_INFO;
		}

		m_devicePanel = new PropertyPanel( propList );
		m_tabbedPane.addTab( "Device Properties", m_devicePanel );

		m_scriptPanel = new PropertyPanel( MainInterpreter.getInterpreterInfo() );
		m_tabbedPane.addTab( "Script Interpreter Info", m_scriptPanel );

		m_threadPanel = new ThreadPanel();
		m_tabbedPane.addTab( "Threads", m_threadPanel );

		m_libraryPanel = new LibraryPanel();
		m_tabbedPane.addTab( "Libraries", m_libraryPanel );

		m_memoryPanel = new MemoryPanel();
		m_tabbedPane.addTab( "Memory", m_memoryPanel );

		setPreferredSize( new Dimension( 900, 600 ) );
		pack();

		OwlUtilities.centerFrame( this );

		owl.main.device.DeviceConnect.addDeviceListener( this );
	}

	// +---------------------------------------------------------------------------+
	// |  actionPerformed                                                          |
	// +---------------------------------------------------------------------------+
	// |  Implements ActionListener interface                                      |
	// +---------------------------------------------------------------------------+
	@Override
	public void actionPerformed( ActionEvent event )
	{
		if ( event.getActionCommand().equals( UPDATE_ACTION ) )
		{
			update();
		}

		else
		{
			super.actionPerformed( event );
		}
	}

	// +---------------------------------------------------------------------------+
	// |  deviceChanged                                                            |
	// +---------------------------------------------------------------------------+
	// |  Implements DeviceListener interface                                      |
	// +---------------------------------------------------------------------------+
	@Override
	public void deviceChanged( DeviceEvent event )
	{
		update();
	}

	// +---------------------------------------------------------------------------+
	// |  update                                                                   |
	// +---------------------------------------------------------------------------+
	// |  Updates all the necessary property data                                  |
	// +---------------------------------------------------------------------------+
	public void update()
	{
		m_systemPanel.setData( MainApp.getProgramInfo() );

		Object[][] propList = CameraAPI.GetProperties();

		if ( propList == null )
		{
			propList = NO_INFO;
		}

		m_devicePanel.setData( propList );

		m_scriptPanel.setData( MainInterpreter.getInterpreterInfo() );
	}
}
