package owl.gui.popupmenus;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import owl.main.MainApp;


// +-----------------------------------------------------------------------+
// |  RecentListPopupMenu Class
// +-----------------------------------------------------------------------+
// |  This class creates a JPopupMenu that will hold a limited number of
// |  items.  The older menu items are replaced as new ones are added when
// |  the max item count has been achieved.
// +-----------------------------------------------------------------------+
public class RecentPopupMenu extends JPopupMenu implements MouseListener
{
	private static final long serialVersionUID = 8751534702087233824L;

	private final String EMPTY_ITEM			= "No Current List";
	private final String ITEM_COUNT_PREF	= "ItemCount";
	private final String ITEM_PREF			= "Item";

	private ActionListener m_menuActionListener;
	private int m_maxCount;


	// +------------------------------------------------------------------+
	// |  Default Constructor
	// +------------------------------------------------------------------+
	public RecentPopupMenu()
	{
		this( null, 10, null );
	}

	// +------------------------------------------------------------------+
	// |  Constructor
	// +------------------------------------------------------------------+
	// |  Sets the max menu item count.
	// |
	// |  @param sName  The component name.
	// |  @param maxCount  The max menu item count.
	// +------------------------------------------------------------------+
	public RecentPopupMenu( String sName, int maxCount )
	{
		this( sName, maxCount, null );
	}

	// +------------------------------------------------------------------+
	// |  Constructor
	// +------------------------------------------------------------------+
	// |  Sets the menu item action listener.
	// |
	// |  @see java.awt.event.ActionListener
	// |  @param sName  The component name.
	// |  @param al  The action listener.
	// +------------------------------------------------------------------+
	public RecentPopupMenu( String sName, ActionListener al )
	{
		this( sName, 10, al );
	}

	// +------------------------------------------------------------------+
	// |  Constructor
	// +------------------------------------------------------------------+
	// |  Sets the max menu item count.
	// |
	// |  @see java.awt.event.ActionListener
	// |  @param sName  The component name.
	// |  @param maxCount  The max menu item count.
	// |  @param al  The action listener.
	// +------------------------------------------------------------------+
	public RecentPopupMenu( String sName, int maxCount, ActionListener al )
	{
		m_maxCount = maxCount;

		setActionListener( al );
		setName( sName );
		add( EMPTY_ITEM );

		loadPreferences();
	}

	// +------------------------------------------------------------------+
	// |  setActionListener
	// +------------------------------------------------------------------+
	// |  Sets the action listener that all menu items will use.
	// |
	// |  @see java.awt.event.ActionListener
	// |  @param al  The action listener.
	// +------------------------------------------------------------------+
	public void setActionListener( ActionListener al )
	{
		m_menuActionListener = al;
	}

	// +------------------------------------------------------------------+
	// |  add
	// +------------------------------------------------------------------+
	// |  Adds a new item to the menu, but only if it's not empty or
	// |  already in the menu.
	// |
	// |  @param sItem  The item to add to the menu.
	// +------------------------------------------------------------------+
	public JMenuItem add( String sItem )
	{
		//
		// Don't add if the item's already in the menu or empty
		//
		if ( contains( sItem ) || sItem.isEmpty() )
		{
			return null;
		}

		//
		// Remove the EMPTY_ITEM if it's there
		//
		if ( contains( EMPTY_ITEM ) )
		{
			remove( 0 );
		}

		//
		// Remove the oldest component in the menu
		//
		if ( getComponentCount() >= m_maxCount )
		{
			remove( getComponentCount() - 1 );
		}

		//
		// Create and insert the item into the first slot
		//
		JMenuItem item = new JMenuItem( sItem );
		item.setActionCommand( sItem );
		item.setName( sItem );

		insert( item, 0 );

		//
		// Set the item action listener if it exists
		//
		if ( m_menuActionListener != null )
		{
			item.addActionListener( m_menuActionListener );
		}

		return item;
	}

	// +------------------------------------------------------------------+
	// |  contains
	// +------------------------------------------------------------------+
	// |  Returns 'true' if the menu already contains the specified item.
	// |  Returns 'false' otherwise.
	// |
	// |  @param sItem  The item to check against the menu.
	// +------------------------------------------------------------------+
	public boolean contains( String sItem )
	{
		Component[] comps = getComponents();

		for ( int i=0; i<comps.length; i++ )
		{
			if ( sItem.equals( comps[ i ].getName() ) )
			{
				return true;
			}
		}

		return false;
	}

	// +------------------------------------------------------------------+
	// |  getItem
	// +------------------------------------------------------------------+
	// |  Checks that the specified item is in the menu and breaks it into
	// |  a set of tokens if it exists. The string is tokenized using
	// |  spaces and tabs " \t".
	// |
	// |  @param sItem  The item to add to the menu.
	// +------------------------------------------------------------------+
	public String[] getItem( String sItem )
	{
		String[] tokens = null;

		if ( !sItem.equals( EMPTY_ITEM ) && contains( sItem ) )
		{
			tokens = sItem.split( "[ \t]" );
		}

		return tokens;
	}

	// +----------------------------------------------------------------------+
	// |  Mouse Listener Events                                               |
	// +----------------------------------------------------------------------+
	@Override
	public void mouseClicked( MouseEvent e )
	{
		if ( SwingUtilities.isLeftMouseButton( e ) )
		{
			this.show( e.getComponent(), e.getX(), e.getY() );
		}
	}

	@Override
	public void mouseEntered( MouseEvent e ) {}

	@Override
	public void mouseExited( MouseEvent e ) {}

	@Override
	public void mousePressed( MouseEvent e ) {}

	@Override
	public void mouseReleased( MouseEvent e ) {}

	// +--------------------------------------------------------------------+
	// |  loadPreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Loads the previously saved component fields and states on startup |
	// +--------------------------------------------------------------------+
	public void loadPreferences()
	{
		try
		{
			String winName = getName();

			if ( winName.isEmpty() )
			{
				return;
			}

			int itemCount =
				MainApp.getPreferences().getInt( winName + ITEM_COUNT_PREF, 0 );

			for ( int i=0; i<itemCount; i++ )
			{
				add( MainApp.getPreferences().get( winName + ITEM_PREF + i, "" ) );
			}
		}
		catch ( NullPointerException npe ) {}
	}

	// +--------------------------------------------------------------------+
	// |  savePreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Saves the component fields and states upon exit                   |
	// +--------------------------------------------------------------------+
	public void savePreferences()
	{
		try
		{
			String winName = getName();

			if ( winName.isEmpty() )
			{
				return;
			}

			Component[] comps = getComponents();

			MainApp.getPreferences().putInt( winName + ITEM_COUNT_PREF, comps.length );

			for ( int i=0; i<comps.length; i++ )
			{
				JMenuItem item = ( JMenuItem )comps[ i ];
				MainApp.getPreferences().put( winName + ITEM_PREF + i, item.getText() );
			}
		}
		catch ( Exception e )
		{
			MainApp.error( e.toString() );
		}
	}
}
