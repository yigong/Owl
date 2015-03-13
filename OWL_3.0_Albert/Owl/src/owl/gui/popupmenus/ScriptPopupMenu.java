package owl.gui.popupmenus;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import owl.main.MainApp;
import owl.main.owltypes.OwlIconedFileChooser;
import owl.main.owltypes.OwlPopupMenu;


public class ScriptPopupMenu extends OwlPopupMenu implements ActionListener
{
	private static final long serialVersionUID = 3293954011045293478L;

	private final String SCRIPT_MENU_LABEL	= "Use Script ... ";
	private final String SCRIPT_UNDEFINED	= "Undefined";

	public static final int STD_ACTION		= 0;
	public static final int SCRIPT_ACTION	= 1;
	public static final int EDIT_ACTION		= 2;
	public static final int DELETE_ACTION	= 3;

	private ActionListener		externListener;
	private JCheckBoxMenuItem	stdMenuItem;
	private JCheckBoxMenuItem	scriptMenuItem;
	private JMenuItem			deleteMenuItem;
	private JMenuItem			editMenuItem;
	private JComponent			component;
	private String[]			actions;
	private String				selectedAction;
	private String				scriptLabel;
	private File				file;
	private ScriptPopupDialog	dialog;

    //--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
    //   "actions" is a four element array ( String[ 4 ] ) that holds the
	//   following:
	//
	//   actions[ STD_ACTION = 0 ] -> External action command to be called if a
	//								  standard window is to be displayed
	//								  ( eg: "SETUP_STD" )
	//
	//   actions[ SCRIPT_ACTION = 1 ] -> External action command to be called if
	//                          	     a script is to be selected
	//									 ( eg: "SETUP_SCRIPT" )
	//
	//   actions[ EDIT_ACTION = 2 ] -> Internal action command that displays any
	//								   selected script in a text editor
	//
	//   actions[ DELETE_ACTION = 3 ] -> External action command that deletes
	//									 the component associated with this menu.
	//									 Of course, this menu is also deleted.
	//
	//   The external action commands are to be handled by the code that creates
	//   and uses the associated component. Every action EXCEPT SCRIPT_ACTION
	//   may be null.
	//--------------------------------------------------------------------------
	public ScriptPopupMenu( JComponent aComponent, String[] actionsArray )
	{
		this( aComponent, actionsArray, null );
	}

	public ScriptPopupMenu( JComponent aComponent, String[] actionsArray, ActionListener aListener )
	{
		super();

		setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

		ButtonGroup group = new ButtonGroup();
		actions           = actionsArray;
		component         = aComponent;
		externListener    = aListener;
		scriptLabel       = SCRIPT_UNDEFINED;
		file              = new File( "." );
		stdMenuItem       = null;
		scriptMenuItem    = null;
		editMenuItem      = null;
		deleteMenuItem    = null;
		dialog            = null;

		if ( actions != null )
		{
			//  Add standard action if it exists
			// +--------------------------------------------------+
			if ( actions.length > STD_ACTION && actions[ STD_ACTION ] != null )
			{
				stdMenuItem = new JCheckBoxMenuItem( "Use Standard Window" );
				stdMenuItem.setBackground( Color.WHITE );
				stdMenuItem.setSelected( true );
				stdMenuItem.setActionCommand( actions[ STD_ACTION ] );
				stdMenuItem.addActionListener( this );
				group.add( stdMenuItem );
				add( stdMenuItem );
			}

			//  Add default script action
			// +--------------------------------------------------+
			scriptMenuItem = new JCheckBoxMenuItem( SCRIPT_MENU_LABEL + SCRIPT_UNDEFINED );
			scriptMenuItem.setBackground( Color.WHITE );
			scriptMenuItem.setActionCommand( actions[ SCRIPT_ACTION ] );
			scriptMenuItem.addActionListener( this );
			group.add( scriptMenuItem );
			add( scriptMenuItem );

			//  Add edit action if it exists
			// +--------------------------------------------------+
			if ( actions.length > EDIT_ACTION && actions[ EDIT_ACTION ] != null )
			{
				editMenuItem = new JMenuItem( "Edit Script" );
				editMenuItem.setBackground( Color.WHITE );
				editMenuItem.setSelected( true );
				editMenuItem.setActionCommand( actions[ EDIT_ACTION ] );
				editMenuItem.addActionListener( this );
				group.add( editMenuItem );
				add( editMenuItem );
			}

			//  Add delete action if it exists
			// +--------------------------------------------------+
			if ( actions.length > DELETE_ACTION && actions[ DELETE_ACTION ] != null )
			{
				deleteMenuItem = new JMenuItem( "Delete" );
				deleteMenuItem.setBackground( Color.WHITE );
				deleteMenuItem.setSelected( true );
				deleteMenuItem.setActionCommand( actions[ DELETE_ACTION ] );
				deleteMenuItem.addActionListener( this );
				add( deleteMenuItem );
			}
		}
	}

	public void actionPerformed( ActionEvent ae )
	{
		selectedAction = ae.getActionCommand();

		if ( selectedAction.equals( actions[ SCRIPT_ACTION ] ) )
		{
			if ( !scriptLabel.equals( SCRIPT_UNDEFINED ) )
			{
				int option = JOptionPane.showConfirmDialog( this, "Select new script?" );

				if ( option == JOptionPane.YES_OPTION )
					handleScriptSelect();
			}
			else
				handleScriptSelect();

			( ( AbstractButton )component ).setActionCommand( selectedAction );
		}

		else if ( actions[ EDIT_ACTION ] != null &&
				  selectedAction.equals( actions[ EDIT_ACTION ] ) )
		{
			if ( file.isFile() )
				MainApp.launchTextEditor( file.getPath() );
			else
			{
				MainApp.launchTextEditor( "" );
				MainApp.warn( "No existing script file. Opening empty text editor." );
			}
		}

		else if ( actions[ DELETE_ACTION ] != null &&
				  selectedAction.equals( actions[ DELETE_ACTION ] ) )
		{
			if ( externListener != null )
			{
				ae.setSource( component );
				externListener.actionPerformed( ae );
			}
		}

		else
		{
			( ( AbstractButton )component ).setActionCommand( selectedAction );
		}

		disposeOfDialog();
	}

	public String getScriptName()
	{
		if ( file.equals( new File( "." ) ) ) { return null; }
		else { return file.getPath(); }
	}

	public String getSelectedAction()
	{
		String action = null;

		if ( actions != null && stdMenuItem != null && scriptMenuItem != null )
		{
			if ( stdMenuItem.isSelected() )
			{
				action = actions[ STD_ACTION ];
			}

			else
			{
				action = actions[ SCRIPT_ACTION ];
			}
		}

		return action;
	}

	public boolean isScriptSelected()
	{
		return scriptMenuItem.isSelected();
	}

	public boolean isStdActionSelected()
	{
		boolean bIsSelected = false;

		if ( stdMenuItem != null )
		{
			bIsSelected = stdMenuItem.isSelected();
		}

		return bIsSelected;
	}

	public void setScript( String scriptFile, boolean useScript )
	{
		if ( scriptFile != null )
		{
			file = new File( scriptFile );
			scriptMenuItem.setSelected( useScript );

			if ( file.exists() && file.isFile() )
			{
				scriptLabel = file.getPath();
				scriptMenuItem.setText( SCRIPT_MENU_LABEL + scriptLabel );

				if ( scriptMenuItem.isSelected() )
				{
					selectedAction = actions[ SCRIPT_ACTION ];
					( ( AbstractButton )component ).setActionCommand( selectedAction );
				}
			}
		}
	}

	public void showAsDialog( int x, int y )
	{
		if ( dialog == null )
			dialog = new ScriptPopupDialog( this );

		if ( dialog != null )
			dialog.show( x, y );
	}

	public void disposeOfDialog()
	{
		if ( dialog != null )
			dialog.dispose();
	}

	private void handleScriptSelect()
	{
		String[] ext = { "bsh" };
		String[] desc = { "BeanShell script ( *.bsh )" };
		ImageIcon[] icons = { new ImageIcon( MainApp.getBitmapPath() + "TinyBean.gif" ) };
		OwlIconedFileChooser lodfc = new OwlIconedFileChooser( file, ext, desc, icons );

		if ( lodfc.openDialog() )
		{
			file = lodfc.getSelectedFile();
		}

		if ( file.exists() && file.isFile() )
		{
			scriptLabel = file.getPath();
			scriptMenuItem.setText( SCRIPT_MENU_LABEL + scriptLabel );
		}
		else
		{
			scriptMenuItem.setText( SCRIPT_MENU_LABEL + SCRIPT_UNDEFINED );

			JOptionPane.showMessageDialog( this,
										   "Invalid script file:\n" +
										   file.getPath() );
		}
	}
}
