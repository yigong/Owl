package owl.main.owltypes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import owl.gui.utils.OwlButtonFactory;
import owl.main.MainApp;


public class OwlDialog extends JDialog implements ActionListener, WindowListener
{
	private static final long serialVersionUID = -6248086459315306712L;

	//--------------------------------------------------------------------------
    //   Constants:
    //--------------------------------------------------------------------------
	public final static String XPOS_PREF	=	"XPos";		// Preferences window position ID
	public final static String YPOS_PREF	=	"YPos";		// Preferences window poistion ID
	public final static String WIDTH_PREF	=	"Width";	// Preferences window width ID
	public final static String HEIGHT_PREF	=	"Height";	// Preferneces window height ID

	public final String CLOSE_ACTION		=	"CLOSE";
	public final String EXIT_ACTION			=	"EXIT";

	public final int TOOLBAR_INDEX			=	0;			// Content pane index of toolbar
	public final int CENTER_CONTAINER_INDEX	=	1;			// Content pane index of central container
	public final int SOUTH_CONTAINER_INDEX	=	2;			// Content pane index of southern container

	//--------------------------------------------------------------------------
    //   Protected Variables:
    //--------------------------------------------------------------------------
	protected GridBagLayout gbl;
	protected GridBagConstraints gbc;
	protected JButton closeButton;

	//--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
	public OwlDialog( OwlFrame owner, String title )
	{
		super( owner, title );

		setIconImage( MainApp.getProgramIcon() );
		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		addWindowListener( this );

		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();

		setLayout( gbl );
	}

	//--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------
	public Component addComponent( Component comp, int index )
	{
		switch ( index )
		{
			case TOOLBAR_INDEX:
			{
				this.getContentPane().add( comp,
										   BorderLayout.PAGE_START,
										   TOOLBAR_INDEX );
			} break;
	
			case CENTER_CONTAINER_INDEX:
			{
				this.getContentPane().add( comp,
										   BorderLayout.CENTER,
										   CENTER_CONTAINER_INDEX );
			} break;
	
			case SOUTH_CONTAINER_INDEX:
			{
				this.getContentPane().add( comp,
										   BorderLayout.PAGE_END,
										   SOUTH_CONTAINER_INDEX );
			} break;

			default:
			{
				MainApp.error( "Invalid component index: " +
								index +
								"! Must be in range 0 to 2." );
			}
		}

		return comp;
	}

	//  ActionListener methods
	// +------------------------------------------------------+
	public void actionPerformed( ActionEvent e )
	{
		if ( e.getActionCommand().equals( CLOSE_ACTION ) )
		{
			savePreferences();
			setVisible( false );
			dispose();
		}

		else if ( e.getActionCommand().equals( EXIT_ACTION ) )
		{
			savePreferences();
		}
	}

	//  WindowListener methods
	// +-------------------------------------------------------+
	@Override
	public void windowActivated( WindowEvent we ) {}

	@Override
	public void windowClosed( WindowEvent we ) {}

	@Override
	public void windowClosing( WindowEvent we )
	{
		savePreferences();
	}

	@Override
	public void windowDeactivated(WindowEvent we) {}

	@Override
	public void windowDeiconified(WindowEvent we) {}

	@Override
	public void windowIconified(WindowEvent we) {}

	@Override
	public void windowOpened(WindowEvent we) {}

    //--------------------------------------------------------------------------
    //   Protected Methods:
    //--------------------------------------------------------------------------

	protected JComponent
	addComponent( JPanel panel, JComponent comp, int insetTOP, int insetLEFT,
				  int insetBOTTOM, int insetRIGHT, int row, int col,
				  int rowSpan, int colSpan )
	{
		return addComponent( panel, comp, insetTOP, insetLEFT, insetBOTTOM,
							 insetRIGHT, GridBagConstraints.NONE,
							 GridBagConstraints.WEST, row, col, rowSpan, colSpan );
	}

	protected JComponent
	addComponent( JPanel panel, JComponent comp, int insetTOP, int insetLEFT,
				  int insetBOTTOM, int insetRIGHT, int fill, int anchr, int row,
				  int col, int rowSpan, int colSpan )
	{
      		// Set the grid layout constraints.
      		gbc.fill = fill;
      		gbc.anchor = anchr;
      		gbc.insets = new Insets( insetTOP, insetLEFT, insetBOTTOM, insetRIGHT );
      		gbc.gridx = col;
      		gbc.gridy = row;
      		gbc.gridwidth = colSpan;
      		gbc.gridheight = rowSpan;

      		// Add the component to the panel.
      		gbl.setConstraints( comp, gbc );
      		panel.add( comp );

      		return comp;
	}

	protected JComponent
	addComponent( Container container, JComponent comp, int insetTOP, int insetLEFT,
				  int insetBOTTOM, int insetRIGHT, int fill, int anchr, int row,
				  int col, int rowSpan, int colSpan )
	{
			// Set the grid layout constraints.
			gbc.fill = fill;
			gbc.anchor = anchr;
			gbc.insets = new Insets( insetTOP, insetLEFT, insetBOTTOM, insetRIGHT );
			gbc.gridx = col;
			gbc.gridy = row;
			gbc.gridwidth = colSpan;
			gbc.gridheight = rowSpan;
			
			// Add the component to the panel.
			gbl.setConstraints( comp, gbc );
			container.add( comp );

			return comp;
	}

	protected JButton createNewToolbarButton( String action )
	{
		return OwlButtonFactory.createNewToolbarButton( action, this );
	}

	protected void appendToolbar( JToolBar toolbar )
	{
		JButton closeButton = 
				OwlButtonFactory.createNewToolbarButton( CLOSE_ACTION, this );

		toolbar.add( closeButton );
	}

	protected void loadPreferences()
	{
		try
		{
			String winName = super.getTitle();

			int XPos = MainApp.getPreferences().getInt( winName + XPOS_PREF, getX() );
			int YPos = MainApp.getPreferences().getInt( winName + YPOS_PREF, getY() );
			this.setLocation( XPos, YPos );

			int width = MainApp.getPreferences().getInt( winName + WIDTH_PREF, 10 );
			int height = MainApp.getPreferences().getInt( winName + HEIGHT_PREF, 10 );

			if ( width != 10 && height != 10 )
			{
				this.setSize( new Dimension( width, height ) );
			}
		}
		catch ( NullPointerException npe ) {}
	}

	protected void savePreferences()
	{
		try
		{
			String winName = super.getTitle();

			MainApp.getPreferences().putInt( winName + XPOS_PREF, getX() );
			MainApp.getPreferences().putInt( winName + YPOS_PREF, getY() );

			MainApp.getPreferences().putInt( winName + WIDTH_PREF, getWidth() );
			MainApp.getPreferences().putInt( winName + HEIGHT_PREF, getHeight() );
		}
		catch ( Exception e )
		{
			MainApp.error( e.toString() );
		}
	}
}
