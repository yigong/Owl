package owl.dir.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import owl.gui.utils.OwlButtonFactory;
import owl.main.MainApp;
import owl.main.owltypes.OwlDialog;
import owl.main.owltypes.OwlTextField;


public class DirDialog extends OwlDialog implements ActionListener
{
	private static final long serialVersionUID = 1714567098166255775L;

	public static final String OK_ACTION		= "OK_ACTION";
	public static final String CANCEL_ACTION	= "CANCEL_ACTION";
	public static final String UP_DIR_ACTION	= "UP_DIR_ACTION";
	public static final String ROOT_ACTION		= "ROOT_ACTION";
	public static final String USER_ACTION		= "USER_ACTION";
	public static final String DOCUM_ACTION		= "DOCUM_ACTION";

	protected JList			m_list;
	protected OwlTextField	m_display;
	protected JButton		m_OkButton;
	protected JButton		m_CancelButton;
	protected JButton		m_UpDirButton;
	protected String		m_iconPath;
	protected boolean		m_bIsOk;

	public DirDialog( Window owner )
	{
		super( ( owl.main.owltypes.OwlFrame )owner, "Directory Selection" );
		super.setIconImage( MainApp.getProgramIcon() );
		super.setPreferredSize( new Dimension( 400, 300 ) );
		super.setModalityType( Dialog.ModalityType.DOCUMENT_MODAL );

		getContentPane().setLayout( new BorderLayout() );

		m_bIsOk = false;

		String sIconPath = owl.main.MainApp.getBitmapPath();

		if ( !sIconPath.endsWith( System.getProperty( "file.separator" ) ) )
		{
			  sIconPath += System.getProperty( "file.separator" );
		}

		m_list = new JList( File.listRoots() );
		m_list.setCellRenderer( new DirCellRenderer( sIconPath ) );
		m_list.addMouseListener( new ListMouseListener() );

		JScrollPane s = new JScrollPane();
		s.getViewport().add( m_list );
		getContentPane().add( s, BorderLayout.CENTER );

		JButton rootButton = OwlButtonFactory.createIconButton( "computer-x16.png",
																 30,
																 28,
																 null,
																 ROOT_ACTION,
																 this );

		JButton userButton = OwlButtonFactory.createIconButton( "Home-x16.png",
																 30,
																 28,
																 null,
																 USER_ACTION,
																 this );

		JButton upButton = OwlButtonFactory.createIconButton( "ArrowUp-x16.png",
															   30,
															   28,
															   null,
															   UP_DIR_ACTION,
															   this );

		JButton docButton = null;
		if ( System.getProperty( "os.name" ).toLowerCase().contains( "win" ) )
		{
			docButton = OwlButtonFactory.createIconButton( "Doc-x16.png",
															30,
															28,
															null,
															DOCUM_ACTION,
															this );
		}

		JPanel buttPanel = new JPanel();
		buttPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
		buttPanel.add( upButton );
		if ( docButton != null ) buttPanel.add( docButton );
		buttPanel.add( userButton );
		buttPanel.add( rootButton );

		m_display = new OwlTextField();
		m_display.setEditable( false );
		m_display.setBackground( java.awt.Color.WHITE );

		JPanel navPanel = new JPanel();
		navPanel.setLayout( new BoxLayout( navPanel, BoxLayout.Y_AXIS ) );
		navPanel.add( buttPanel );
		navPanel.add( m_display );
		getContentPane().add( navPanel, BorderLayout.NORTH );

		m_OkButton = new JButton( "Ok" );
		m_OkButton.setPreferredSize( new Dimension( 70, 26 ) );
		m_OkButton.setActionCommand( OK_ACTION );
		m_OkButton.addActionListener( this );

		m_CancelButton = new JButton( "Cancel" );
		m_CancelButton.setPreferredSize( new Dimension( 70, 26 ) );
		m_CancelButton.setActionCommand( CANCEL_ACTION );
		m_CancelButton.addActionListener( this );

		JPanel buttonPanel = new JPanel();
		buttonPanel.add( m_OkButton );
		buttonPanel.add( m_CancelButton );
		getContentPane().add( buttonPanel, BorderLayout.SOUTH );

		pack();

		owl.gui.utils.OwlUtilities.centerFrame( this );
	}

	public String getPath()
	{
		return m_display.getText();
	}

	public File getFile()
	{
		return ( new File( m_display.getText() ) );
	}

	public void setPath( String path )
	{
		update( new File( path ) );
	}

	// NOTE: For this to work, the dialog MUST be modal!
	public boolean showDialog()
	{
		setVisible( true );

		return m_bIsOk;
	}

	@Override
	public void actionPerformed( ActionEvent ae )
	{
		if ( ae.getActionCommand().equals( OK_ACTION ) )
		{
			m_bIsOk = true;

			super.dispose();
		}

		else if ( ae.getActionCommand().equals( CANCEL_ACTION ) )
		{
			m_bIsOk = false;

			super.dispose();
		}

		else if ( ae.getActionCommand().equals( UP_DIR_ACTION ) )
		{
			update( ( new File( m_display.getText() ) ).getParentFile() );
		}

		else if ( ae.getActionCommand().equals( DOCUM_ACTION ) )
		{
			String docPath = System.getProperty( "user.home" ) +
							 System.getProperty( "file.separator" ) +
							 "Documents";

			File file = new File( docPath );

			if ( file.isDirectory() && file.exists() )
			{
				update( new File( docPath ) );
			}
		}

		else if ( ae.getActionCommand().equals( USER_ACTION ) )
		{
			update( new File( System.getProperty( "user.home" ) ) );
		}

		else if ( ae.getActionCommand().equals( ROOT_ACTION ) )
		{
			update( ( File )null );
		}
	}

	public class ListMouseListener extends MouseAdapter
	{
		@Override
		public void mouseClicked( MouseEvent e )
		{
			if ( e.getClickCount() == 1 )
			{
				Object selObj = m_list.getSelectedValue();

				if ( selObj instanceof File )
				{
					update( ( File )selObj );
				}
			}
		}
	}

	protected void update( File file )
	{
//		boolean bRemoteAPI =
//  				Boolean.valueOf(
//  						MainApp.getStartupINIFileValue( "REMOTE_API" ) ).booleanValue();

		if ( file != null && file.exists() )
		{
			File[] files = file.listFiles( new DirFilter() );

//			if ( bRemoteAPI )
//			{
//				files = CameraAPI.GetDirList( file.getAbsolutePath() );
//			}

			if ( files != null )
			{
				Arrays.sort( files, new DirSortComparator() );
				m_list.setListData( files );
				m_display.setText( file.getAbsolutePath() );
			}
		}
		else
		{
			File[] files = File.listRoots();
			Arrays.sort( files, new DirSortComparator() );
			m_list.setListData( files );
			m_display.setText( "" );
		}
	}
}
