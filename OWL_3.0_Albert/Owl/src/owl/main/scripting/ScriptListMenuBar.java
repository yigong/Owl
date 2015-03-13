package owl.main.scripting;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import owl.gui.popupmenus.ScriptPopupMenu;
import owl.gui.utils.HTMLParser;
import owl.main.MainApp;
import owl.main.owltypes.OwlFileChooser;


public class ScriptListMenuBar extends JMenuBar implements ActionListener
{
	private static final long serialVersionUID = 463569804404942719L;

	private static final String ADD_SCRIPT_ACTION		= "ADD_SCRIPT";
	private static final String DELETE_SCRIPT_ACTION	= "DELETE_SCRIPT";
	private static final String RUN_SCRIPT_ACTION		= "RUN_SCRIPT";
	private static final String STOP_SCRIPT_ACTION		= "<html><font color=\"#ff0000\">STOP SCRIPT</font></html>";
	private static final String EDIT_SCRIPT_ACTION		= "EDIT_SCRIPT";
	private static final String LIST_NODE				= "SciptList";

	private JMenu scriptMenu;
	private ScriptRunnable scriptRunnable;

	public ScriptListMenuBar( String aLabel )
	{
		super();
		super.setBorderPainted( false );
		super.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

		JMenuItem addMenuItem = new JMenuItem( "Add script ..." );
		addMenuItem.setActionCommand( ADD_SCRIPT_ACTION );
		addMenuItem.addActionListener( this );
		addMenuItem.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

		scriptMenu = new JMenu( aLabel );
		scriptMenu.add( addMenuItem );
		scriptMenu.addSeparator();

		add( scriptMenu );
	}

	public void actionPerformed( ActionEvent ae )
	{
		// |  "Add user script" handler
		// +--------------------------------------------------------------
		if ( ae.getActionCommand().equals( ADD_SCRIPT_ACTION ) )
		{
			String userDir = System.getProperty( "user.dir" );
			OwlFileChooser chooser = new OwlFileChooser( new File( userDir ), "bsh" );
	
			if ( chooser.openDialog() )
			{
				String scriptLabel = 
					 JOptionPane.showInputDialog( null, "Enter a descriptive label" );
	
				if ( scriptLabel != null )
				{
					add( scriptLabel, chooser.getSelectedFile().getAbsolutePath() );

					saveScript( scriptLabel,
								chooser.getSelectedFile().getAbsolutePath() );
				}
			}
		}

		// |  "Run user script" handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( RUN_SCRIPT_ACTION ) )
		{
			ScriptPopupMenu popup = ( ScriptPopupMenu )
			( ( JMenuItem )ae.getSource() ).getComponentPopupMenu();

			scriptRunnable = new ScriptRunnable( popup.getScriptName(),
												 ( ( JMenuItem )ae.getSource() ),
												 RUN_SCRIPT_ACTION,
												 STOP_SCRIPT_ACTION );

			new Thread( scriptRunnable ).start();
		}

		// |  "Delete user script" handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( DELETE_SCRIPT_ACTION ) )
		{
			scriptMenu.remove( ( JMenuItem )ae.getSource() );
			removeScript( ( ( JMenuItem )ae.getSource() ).getText() );
		}

		// |  "Stop script" handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( STOP_SCRIPT_ACTION ) )
		{
			if ( scriptRunnable != null )
			{
				scriptRunnable.stop();
			}
		}

		else if ( ae.getActionCommand().equals( EDIT_SCRIPT_ACTION ) )
		{
			ScriptPopupMenu popup = ( ScriptPopupMenu )
			( ( JMenuItem )ae.getSource() ).getComponentPopupMenu();

			popup.actionPerformed( ae );
		}
	}

	private void add( String aScriptLabel, String aScript )
	{
		String htmlLabel = "<html><font color=\"#000000\">" +
						   aScriptLabel + "</font></html>";

		JMenuItem menuItem = new JMenuItem( htmlLabel );
		menuItem.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

		String[] actions = { null,
							 RUN_SCRIPT_ACTION,
							 EDIT_SCRIPT_ACTION,
							 DELETE_SCRIPT_ACTION };

		ScriptPopupMenu cpm = new ScriptPopupMenu( menuItem,
												   actions,
												   this );

		PopupMenuMouseListener pml = new PopupMenuMouseListener( cpm );

		cpm.setScript( aScript, true );
		menuItem.setComponentPopupMenu( cpm );
		menuItem.addActionListener( this );
		menuItem.addMouseListener( pml );
		scriptMenu.add( menuItem );
	}

	public void loadPreferences()
	{
		try
		{
			if ( scriptMenu != null )
			{
				if ( MainApp.getPreferences().nodeExists( LIST_NODE ) )
				{
					Preferences prefSubNode = MainApp.getPreferences().node( LIST_NODE );
	
					String[] keys = prefSubNode.keys();
	
					for ( int i=0; i<keys.length; i++ )
					{
						String script = prefSubNode.get( keys[ i ], null );

						if ( script != null && !script.isEmpty() )
							add( keys[ i ], script );
					}
				}
			}
		}
		catch ( BackingStoreException bse )
		{
			MainApp.error( bse.getMessage() );
		}
	}

	private void saveScript( String aScriptLabel, String aScript )
	{
		if ( scriptMenu != null )
		{
			Preferences prefSubNode = MainApp.getPreferences().node( LIST_NODE );
			prefSubNode.remove( aScriptLabel );
			prefSubNode.put( aScriptLabel, aScript );
		}
	}

	private void removeScript( String aScriptLabel )
	{
		Preferences prefSubNode = MainApp.getPreferences().node( LIST_NODE );
		prefSubNode.remove( HTMLParser.parse( aScriptLabel ) );
	}

	private class PopupMenuMouseListener extends MouseAdapter
	{
		private ScriptPopupMenu	invokerPopMenu;

		public PopupMenuMouseListener( ScriptPopupMenu aPopMenu )
		{
			invokerPopMenu = aPopMenu;
		}

		@Override
		public void mousePressed( MouseEvent e )
		{
			if ( SwingUtilities.isRightMouseButton( e ) )
			{
				invokerPopMenu.showAsDialog( e.getXOnScreen(), e.getYOnScreen() );
			}
		}
	}
}
