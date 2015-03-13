package owl.main.owltypes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import owl.gui.utils.OwlButtonFactory;
import owl.main.MainApp;


public class OwlFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -7664137965452609256L;

	//--------------------------------------------------------------------------
    //   Constants:
    //--------------------------------------------------------------------------
	public final String HIDE_ACTION			=	"HIDE DETAILS";
	public final String SHOW_ACTION			=	"SHOW DETAILS";
	public final String CLOSE_ACTION		=	"CLOSE";
	public final String EXIT_ACTION			=	"EXIT";

	public final static String XPOS_PREF	=	"XPos";		// Preferences window position ID
	public final static String YPOS_PREF	=	"YPos";		// Preferences window poistion ID
	public final static String WIDTH_PREF	=	"Width";	// Preferences window width ID
	public final static String HEIGHT_PREF	=	"Height";	// Preferneces window height ID

	public final int TOOLBAR_INDEX			=	0;			// Content pane index of toolbar
	public final int CENTER_CONTAINER_INDEX	=	1;			// Content pane index of central container
	public final int SOUTH_CONTAINER_INDEX	=	2;			// Content pane index of southern container

	public final static int CLEAR_ON_HIDE	=	0x0800;
	public final static int HIDE			=	0x0100;
	public final static int CLOSE			=	0x0010;
	public final static int EXIT			=	0x0001;

    //--------------------------------------------------------------------------
    //   Protected Variables:
    //--------------------------------------------------------------------------
	protected GridBagLayout			gbl;
	protected GridBagConstraints	gbc;
	protected JButton				hideOptionButton;
	protected JButton				closeButton;
	protected JButton				exitButton;
	protected boolean				bSouthCompVisible;


	//--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
	public OwlFrame( String title, boolean loadPrefs )
	{
		super.setTitle( title );
		super.setName( title );
		super.setIconImage( MainApp.getProgramIcon() );
		super.getContentPane().setLayout( new BorderLayout() );

		super.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );

		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();

		hideOptionButton = new JButton( HIDE_ACTION );
		hideOptionButton.setBorderPainted( false );
		hideOptionButton.setFocusPainted( false );
		hideOptionButton.setActionCommand( HIDE_ACTION );
		hideOptionButton.addActionListener( this );

		closeButton = new JButton( CLOSE_ACTION );
		closeButton.setBorderPainted( false );
		closeButton.setFocusPainted( false );
		closeButton.setActionCommand( CLOSE_ACTION );
		closeButton.addActionListener( this );

		exitButton = new JButton( EXIT_ACTION );
		exitButton.setBorderPainted( false );
		exitButton.setFocusPainted( false );
		exitButton.setActionCommand( EXIT_ACTION );
		exitButton.addActionListener( this );

		bSouthCompVisible = false;

		if ( loadPrefs ) { loadPreferences(); }
	}

	//--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------
	public void addComponent( Component comp, int index )
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
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( e.getActionCommand().equals( HIDE_ACTION ) )
		{
			//  Test for the existence of a south container object
			// +---------------------------------------------------+
			try
			{
				JComponent comp =
					( JComponent )getContentPane().getComponent( SOUTH_CONTAINER_INDEX );

				if ( comp != null )
				{
					bSouthCompVisible = comp.isVisible();

					comp.setVisible( false );
					setSize( getWidth(), getHeight() - comp.getHeight() );
				}
			}
			catch ( ArrayIndexOutOfBoundsException aioobe ) {}

			//  Hide the center container object
			// +---------------------------------------------------+
			try
			{
				JComponent comp =
					( JComponent )getContentPane().getComponent( CENTER_CONTAINER_INDEX );

				if ( comp != null )
				{
					comp.setVisible( false );
					setSize( new Dimension( getWidth(), getHeight() - comp.getHeight() ) );
					pack();

					hideOptionButton.setText( SHOW_ACTION );
					hideOptionButton.setActionCommand( SHOW_ACTION );
				}
			}
			catch ( ArrayIndexOutOfBoundsException aioobe ) {}
		}

		else if ( e.getActionCommand().equals( SHOW_ACTION ) )
		{
			//  Show the south container object ( if it exists )
			// +---------------------------------------------------+
			try
			{
				JComponent comp =
					( JComponent )getContentPane().getComponent( SOUTH_CONTAINER_INDEX );

				if ( comp != null )
				{
					if ( bSouthCompVisible )
					{
						comp.setVisible( true );
					}
				}
			}
			catch ( ArrayIndexOutOfBoundsException aioobe ) {}

			//  Show the center container object
			// +---------------------------------------------------+
			try
			{
				JComponent comp =
					( JComponent )getContentPane().getComponent( CENTER_CONTAINER_INDEX );

				if ( comp != null )
				{
					comp.setVisible( true );
					setSize( comp.getSize() );
					pack();

					hideOptionButton.setText( HIDE_ACTION );
					hideOptionButton.setActionCommand( HIDE_ACTION );
				}
			}
			catch ( ArrayIndexOutOfBoundsException aioobe ) {}
		}

		else if ( e.getActionCommand().equals( CLOSE_ACTION ) )
		{
			setVisible( false );
			savePreferences();
			dispose();
		}

		else if ( e.getActionCommand().equals( EXIT_ACTION ) )
		{
			savePreferences();
		}
	}

	public void setSouthComponentVisible( boolean bVisible )
	{
		bSouthCompVisible = bVisible;
	}

	public void addComponent( JPanel panel, JComponent comp, int insetTOP,
							  int insetLEFT, int insetBOTTOM, int insetRIGHT,
							  int row, int col, int rowSpan, int colSpan )
	{
		// Set the grid layout constraints.
		gbc.fill   = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets( insetTOP, insetLEFT, insetBOTTOM, insetRIGHT );
		gbc.gridx  = col;
		gbc.gridy  = row;
		gbc.gridwidth  = colSpan;
		gbc.gridheight = rowSpan;
		
		// Add the component to the panel.
		gbl.setConstraints( comp, gbc );
		panel.add( comp );
	}

	public void addComponent( JPanel panel, JComponent comp, int insetTOP,
							  int insetLEFT, int insetBOTTOM, int insetRIGHT,
							  int anchr, int row, int col, int rowSpan,
							  int colSpan )
	{
		// Set the grid layout constraints.
		gbc.fill   = GridBagConstraints.NONE;
		gbc.anchor = anchr;
		gbc.insets = new Insets( insetTOP, insetLEFT, insetBOTTOM, insetRIGHT );
		gbc.gridx  = col;
		gbc.gridy  = row;
		gbc.gridwidth  = colSpan;
		gbc.gridheight = rowSpan;

		// Add the component to the panel.
		gbl.setConstraints( comp, gbc );
		panel.add( comp );
	}

	public void addComponent( JPanel panel, JComponent comp, int insetTOP,
							  int insetLEFT, int insetBOTTOM, int insetRIGHT,
							  int fill, int anchr, int row, int col,
							  int rowSpan, int colSpan )
	{
      		// Set the grid layout constraints.
      		gbc.fill   = fill;
      		gbc.anchor = anchr;
      		gbc.insets = new Insets( insetTOP, insetLEFT, insetBOTTOM, insetRIGHT );
      		gbc.gridx  = col;
      		gbc.gridy  = row;
      		gbc.gridwidth  = colSpan;
      		gbc.gridheight = rowSpan;

      		// Add the component to the panel.
      		gbl.setConstraints( comp, gbc );
      		panel.add( comp );
	}

	public boolean isHidingDetails()
	{
		if ( hideOptionButton.getActionCommand().equals( SHOW_ACTION ) )
		{
			return true;
		}

		return false;
	}

	public JButton createNewToolbarButton( Icon icon, String action )
	{
		return OwlButtonFactory.createNewToolbarButton( icon, action, this );
	}

	public JButton createNewToolbarButton( String action )
	{
		return OwlButtonFactory.createNewToolbarButton( action, this );
	}

	public OwlBoldButton createBoldToolbarButton( String action, Color color )
	{
		OwlBoldButton button = new OwlBoldButton( action, color );
		button.setBorderPainted( false );
		button.setFocusPainted( false );
		button.setActionCommand( action );
		button.addActionListener( this );

		return button;
	}

	public void appendToolbar( JToolBar toolbar )
	{
		appendToolbar( toolbar, ( HIDE | CLOSE ) );
	}

	public void appendToolbar( JToolBar toolbar, int flags )
	{
		if ( ( flags & HIDE ) > 0)
		{
			toolbar.add( hideOptionButton );
		}

		if ( ( flags & CLOSE ) > 0 )
		{
			toolbar.add( closeButton );
			toolbar.add( Box.createHorizontalStrut( 20 ) );
		}

		if ( ( flags & EXIT ) > 0 )
		{
			toolbar.add( exitButton );
		}

		toolbar.setBorder( BorderFactory.createMatteBorder( 0, 0, 1, 0, Color.GRAY ) );
		toolbar.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
	}

	public void appendToolbar( JToolBar toolbar, int flags, Color color )
	{
		hideOptionButton.setBackground( color );
		closeButton.setBackground( color );
		exitButton.setBackground( color );
		appendToolbar( toolbar, flags );
	}

	public int getIntegerField( String str ) throws Exception
	{
		int val = 0;

		try
		{
			val = Integer.parseInt( str );
		}
		catch ( NumberFormatException nfe )
		{
			throw new Exception( "Invalid parameter: " + str );
		}

		return val;
	}

	public double getDoubleField( String str ) throws Exception
	{
		double val = 0;

		try
		{
			val = Double.parseDouble( str );
		}
		catch ( NumberFormatException nfe )
		{
			throw new Exception( "Invalid parameter: " + str );
		}

		return val;
	}

    //--------------------------------------------------------------------------
    //   Protected Methods:
    //--------------------------------------------------------------------------
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
