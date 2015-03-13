package owl.main.scripting;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import owl.gui.utils.OwlButtonFactory;
import owl.main.MainApp;
import owl.main.owltypes.OwlIconedFileChooser;
import owl.main.owltypes.OwlPanel;
import owl.main.owltypes.OwlTextField;
import owl.main.scripting.help.HelpFrame;


public class ScriptPanel extends OwlPanel implements ActionListener
{
	private static final long serialVersionUID = 1416005912388208448L;

	//--------------------------------------------------------------------------
    //   Public Constants:
    //--------------------------------------------------------------------------
	public final String RUN_ACTION				=	"RUN";
	public final String STOP_ACTION				=	"STOP";
	public final String EDIT_ACTION				=	"EDIT";
	public final String CONSOLE_ACTION			=	"CONSOLE";
	public final String HELPER_ACTION			=	"HELPER";
	public final String HELP_ACTION				=	"HELP";
	public final String FILE_BROWSE_ACTION		=	"FILE BROWSE";
	public final String ADD_SCRIPT_ACTION		=	"ADD_SCRIPT";
	public final String ADD_REMOVE_USER_ACTION	=	"ADD_REMOVE_USER";
	public final String REMOVE_SCRIPT_ACTION	=	"REMOVE_SCRIPT";
	public final String DO_NOTHING_ACTION		=	"DO_NOTHING_ACTION";

	//--------------------------------------------------------------------------
    //   Private Constants:
    //--------------------------------------------------------------------------
	private final String BSH_ICON =	MainApp.getBitmapPath() + "TinyBean.gif";
	private final String SCRIPT_FILE_PREF = "ScriptFilename";

    //--------------------------------------------------------------------------
    //   Private Variables:
    //--------------------------------------------------------------------------
	private OwlTextField		fileTxtfld;
	private JButton				runButton;
	private ImageIcon			runIcon;
	private ImageIcon			stopIcon;
	private File				file;
    private Thread				scriptThread;
    private ScriptRunnable		scriptRunnable;
    private BshConsoleFrame		bshConsoleFrame;
    private HelpFrame			helpFrame;

	//--------------------------------------------------------------------------
    //   Constructor:
    //--------------------------------------------------------------------------
	public ScriptPanel()
	{
		super( "Script" );
		setName( "ScriptPanel" );

		fileTxtfld      = new OwlTextField( "", 23 );
//		fileTxtfld.setTransferHandler( null );

		file            = new File( MainApp.getScriptsPath() );
		Dimension dim	= new Dimension( 25, 20 );

		//
		// Create script input sub-panel
		// --------------------------------------------------------------
		JPanel panel = new JPanel();
		panel.setLayout( gbl );

		ImageIcon browseIcon = new ImageIcon( MainApp.getBitmapPath() + "folder.gif" );
		JButton browseButton = OwlButtonFactory.create( browseIcon, FILE_BROWSE_ACTION, dim, this );
		browseButton.setToolTipText( "Browse for script" );

		ImageIcon editIcon = new ImageIcon( MainApp.getBitmapPath() + "edit.gif" );
		JButton editButton = OwlButtonFactory.create( editIcon, EDIT_ACTION, dim, this );
		editButton.setToolTipText( "Edit script" );

		runIcon   = new ImageIcon( MainApp.getBitmapPath() + "Run.gif" );
		stopIcon  = new ImageIcon( MainApp.getBitmapPath() + "Off.gif" );
		runButton = OwlButtonFactory.create( runIcon, RUN_ACTION, dim, this );
		runButton.setToolTipText( "Run script" );

		addComponent( panel, fileTxtfld,   0, 3, 0, 0, GridBagConstraints.WEST, 0, 0, 1, 1 );
		addComponent( panel, browseButton, 0, 0, 0, 0, GridBagConstraints.WEST, 0, 1, 1, 1 );
		addComponent( panel, editButton,   0, 0, 0, 0, GridBagConstraints.WEST, 0, 2, 1, 1 );
		addComponent( panel, runButton,    0, 0, 0, 0, GridBagConstraints.WEST, 0, 3, 1, 1 );

		addComponent( panel, 0, 0, 0, 0, GridBagConstraints.WEST, 0, 0, 1, 1 );

		//
		// Create script input/edit/run/etc sub-panel
		// --------------------------------------------------------------
		panel = new JPanel();
		panel.setLayout( gbl );

		ImageIcon consoleIcon = new ImageIcon( MainApp.getBitmapPath() + "Console.gif" );
		JButton consoleButton = OwlButtonFactory.create( consoleIcon, CONSOLE_ACTION, dim, this );
		consoleButton.setToolTipText( "Show script console" );

		ImageIcon helpIcon = new ImageIcon( MainApp.getBitmapPath() + "HelpLeaf.gif" );
		JButton helpButton = OwlButtonFactory.create( helpIcon, HELP_ACTION, dim, this );
		helpButton.setToolTipText( "Show script help" );

		ScriptListMenuBar scriptListMenuBar = new ScriptListMenuBar( "Scripts" );

		addComponent( panel, consoleButton,     0, 3, 0, 0, GridBagConstraints.WEST, 0, 0, 1, 1 );
		addComponent( panel, helpButton,        0, 0, 0, 0, GridBagConstraints.WEST, 0, 1, 1, 1 );
		addComponent( panel, scriptListMenuBar, 0, 0, 0, 0, GridBagConstraints.WEST, 0, 2, 1, 1 );

		addComponent( panel, 0, 0, 0, 0, GridBagConstraints.WEST, 1, 0, 1, 1 );

		loadPreferences();
		scriptListMenuBar.loadPreferences();
	}

	@Override
	public void actionPerformed( ActionEvent ae )
	{
		// |  "Browse for script" button handler
		// +--------------------------------------------------------------
		if ( ae.getActionCommand().equals( FILE_BROWSE_ACTION ) )
		{
			String[] ext      = { "bsh" };
			String[] desc     = { "Bean Shell Script File ( *.bsh )" };
			ImageIcon[] icons = { new ImageIcon( BSH_ICON ) };

			OwlIconedFileChooser fileChooser
							= new OwlIconedFileChooser( file, ext, desc, icons );

			if ( fileChooser.openDialog() )
			{
				try
				{
					file = fileChooser.getSelectedFile();
					fileTxtfld.setText( file.getCanonicalPath() );
				}
				catch ( IOException ioe ) {}
			}
		}

		// |  "Run script" button handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( RUN_ACTION ) )
		{
			if ( !fileTxtfld.getText().equals( "" ) )
			{
				String scriptFilename = fileTxtfld.getText();

				if ( scriptRunnable == null )
				{
					scriptRunnable = new ScriptRunnable( scriptFilename,
														 runButton,
														 runIcon,
														 stopIcon,
														 RUN_ACTION,
														 STOP_ACTION );
				}
				else
				{
					scriptRunnable.setNewFile( scriptFilename, null, null );
				}

				scriptThread = new Thread( scriptRunnable );
				scriptThread.start();

				MainApp.info( "Running script: \"" + scriptFilename + "\"" );
			}
			else
			{
				MainApp.error( "You must select a file!" );
			}
		}

		// |  "Stop script" button handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( STOP_ACTION ) )
		{
			if ( scriptRunnable != null && scriptThread.isAlive() )
			{
				scriptRunnable.stop();
			}
		}

		// |  "Edit script" button handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( EDIT_ACTION ) )
		{
			file = new File( fileTxtfld.getText() );

			if ( file.isFile() )
			{
				MainApp.launchTextEditor( file.getPath() );
			}
			else
			{
				MainApp.launchTextEditor( "" );
				MainApp.warn( "No existing script file. Opening empty text editor." );
			}
		}

		// |  "Show console" button handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( CONSOLE_ACTION ) )
		{
			if ( bshConsoleFrame == null )
			{
				bshConsoleFrame = new BshConsoleFrame();
			}

			bshConsoleFrame.setVisible( true );
		}

		// |  "Show help window" button handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( HELP_ACTION ) )
		{
			if ( helpFrame == null )
			{
				helpFrame = new HelpFrame();
			}

			helpFrame.setVisible( true );
		}

		// |  "Warn that no script exists" handler
		// +--------------------------------------------------------------
		else if ( ae.getActionCommand().equals( DO_NOTHING_ACTION ) )
		{
			MainApp.warn( "Sorry, but no script is defined for this button!" );
		}

		else
		{
			super.actionPerformed( ae );
		}
	}

	// +--------------------------------------------------------------------+
	// |  savePreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Saves the buttons and their scripts upon exit                     |
	// +--------------------------------------------------------------------+
	@Override
	protected void savePreferences()
	{
		String prefKey = null;

		if ( fileTxtfld != null )
		{
			prefKey = getName() + SCRIPT_FILE_PREF;
			MainApp.getPreferences().remove( prefKey );

			if ( !fileTxtfld.getText().isEmpty() )
			{
				MainApp.getPreferences().put( prefKey, fileTxtfld.getText() );
			}
		}
	}

	// +--------------------------------------------------------------------+
	// |  loadPreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Loads the buttons and their scripts upon startup.                 |
	// +--------------------------------------------------------------------+
	protected void loadPreferences()
	{
		String prefKey = null;

		if ( fileTxtfld != null )
		{
			prefKey = getName() + SCRIPT_FILE_PREF;
			String scriptFile = MainApp.getPreferences().get( prefKey, null );

			if ( scriptFile != null && !scriptFile.isEmpty() )
			{
				fileTxtfld.setText( scriptFile );
				file = new File( scriptFile );
			}
		}
	}
}
