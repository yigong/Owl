package owl.logging;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.MenuElement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import owl.cameraAPI.CameraAPI;
import owl.gui.utils.PrintUtilities;
import owl.main.MainApp;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlPopupMenu;


public class LogFrame extends OwlFrame implements ActionListener
{
	private static final long serialVersionUID = -6524606108711947837L;

	//--------------------------------------------------------------------------
    //   Constants:
    //--------------------------------------------------------------------------
	private final String MAX_CHAR_COUNT_PREF	= "MaxCharCount";
	private final String IS_CLEARING_PREF		= "IsClearing";
	private final String LOG_API_PREF			= "LogAPI";
	private final String STACK_TRACE_PREF		= "ShowStackTrace";

	private final String STACK_TRACE_MENU_ITEM	= "Show Stack Trace ( On Error )";
	private final String COPY_MENU_ITEM			= "Copy";
	private final String SAVE_MENU_ITEM			= "Save";
	private final String PRINT_MENU_ITEM		= "Print";
	private final String SET_CHAR_MAX_MENU_ITEM	= "Set char max";
	private final String CLEAR_LOG_MENU_ITEM	= "Clear log";
	private final String CLEAR_NOW_MENU_ITEM	= "Clear log now";
	private final String LOG_API_ON_MENU_ITEM	= "Log API commands";

    //--------------------------------------------------------------------------
    //   Private Variables:
    //--------------------------------------------------------------------------
	private JTextPane    textPane;
	private JScrollPane  scrollPane;
	private OwlPopupMenu popupMenu;

    //--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
	public LogFrame()
	{
		super( "Owl Log", true );
		setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );

		popupMenu = new OwlPopupMenu( "Log Options" );
		popupMenu.add( new JCheckBoxMenuItem( LOG_API_ON_MENU_ITEM, false ) ).addActionListener( this );
		popupMenu.add( new JCheckBoxMenuItem( STACK_TRACE_MENU_ITEM, false ) ).addActionListener( this );
		popupMenu.add( COPY_MENU_ITEM ).addActionListener( this );
		popupMenu.add( SAVE_MENU_ITEM ).addActionListener( this );
		popupMenu.add( PRINT_MENU_ITEM ).addActionListener( this );
		popupMenu.add( SET_CHAR_MAX_MENU_ITEM ).addActionListener( this );
		popupMenu.add( CLEAR_NOW_MENU_ITEM ).addActionListener( this );
		popupMenu.add( new JCheckBoxMenuItem( CLEAR_LOG_MENU_ITEM, true ) ).addActionListener( this );

		StyleContext sc = new StyleContext();
		DefaultStyledDocument doc = new DefaultStyledDocument( sc );

	    Style infoTagStyle = sc.addStyle( "INFO_TAG", null );
	    infoTagStyle.addAttribute( StyleConstants.Foreground, Color.black );
	    infoTagStyle.addAttribute( StyleConstants.FontSize, new Integer( 12 ) );
	    infoTagStyle.addAttribute( StyleConstants.Bold, new Boolean( true ) );

	    Style infoStyle = sc.addStyle( "INFO", null );
	    infoStyle.addAttribute( StyleConstants.Foreground, Color.black );
	    infoStyle.addAttribute( StyleConstants.FontSize, new Integer( 12 ) );

	    Style warningTagStyle = sc.addStyle( "WARNING_TAG", null );
	    warningTagStyle.addAttribute( StyleConstants.Foreground, new Color( 0xFF8306 ) );
	    warningTagStyle.addAttribute( StyleConstants.FontSize, new Integer( 12 ) );
	    warningTagStyle.addAttribute( StyleConstants.Bold, new Boolean( true ) );

	    Style warningStyle = sc.addStyle( "WARNING", null );
	    warningStyle.addAttribute( StyleConstants.Foreground, new Color( 0xFF8306 ) );
	    warningStyle.addAttribute( StyleConstants.FontSize, new Integer( 12 ) );

	    Style errorTagStyle = sc.addStyle( "ERROR_TAG", null );
	    errorTagStyle.addAttribute( StyleConstants.Foreground, Color.red );
	    errorTagStyle.addAttribute( StyleConstants.FontSize, new Integer( 12 ) );
	    errorTagStyle.addAttribute( StyleConstants.Bold, new Boolean( true ) );

	    Style errorStyle = sc.addStyle( "ERROR", null );
	    errorStyle.addAttribute( StyleConstants.Foreground, Color.red );
	    errorStyle.addAttribute( StyleConstants.FontSize, new Integer( 12 ) );

	    Style debugStyle = sc.addStyle( "DEBUG", null );
	    debugStyle.addAttribute( StyleConstants.Foreground, Color.blue );
	    debugStyle.addAttribute( StyleConstants.FontSize, new Integer( 12 ) );
	    debugStyle.addAttribute( StyleConstants.Bold, new Boolean( true ) );

		textPane = new JTextPane();
		textPane.addMouseListener( new LogMouseListener() );
		textPane.setDocument( doc );
		textPane.setEditable( false );

		scrollPane = new JScrollPane( textPane,
									  ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
									  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );

		this.getContentPane().add( scrollPane );

		textPane.setBackground( java.awt.Color.white );

		MainApp.getLogger().addAppender( new LogAppender( textPane, doc ) );

		setPreferredSize( new Dimension( 100, 250 ) );
		pack();

		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int xPos = d.width / 2 - getWidth() / 2;
		int yPos = d.height  - getHeight() - 50;
		setLocation( xPos, yPos );

		loadPreferences();
	}

    //--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------
	public void clearLog( boolean shouldClear )
	{
		LogAppender appender = getAppender();

		if ( appender != null )
		{
			appender.clearLog( shouldClear );
		}
		else
		{
			printAppenderIsNull();
		}
	}

	public void clearNow()
	{
		LogAppender appender = getAppender();

		if ( appender != null )
		{
			appender.clearNow();
		}
		else
		{
			printAppenderIsNull();
		}
	}

	public boolean isClearing()
	{
		boolean isItClearing = true;

		LogAppender appender = getAppender();

		if ( appender != null )
		{
			isItClearing = appender.isClearing();
		}
		else
		{
			printAppenderIsNull();
		}

		return isItClearing;
	}

	public boolean isAPILogging()
	{
		return isPopupMenuCheckBoxSelected( LOG_API_ON_MENU_ITEM );
	}

	public int getMaxCharCount()
	{
		int maxCount = 0;

		LogAppender appender = getAppender();

		if ( appender != null )
		{
			maxCount = appender.getMaxCharCount();
		}
		else
		{
			printAppenderIsNull();
		}

		return maxCount;
	}

	public void setMaxCharCount( int count )
	{
		LogAppender appender = getAppender();

		if ( appender != null )
		{
			appender.setMaxCharCount( count );
		}
		else
		{
			printAppenderIsNull();
		}
	}

	public void printAppenderIsNull()
	{
		MainApp.getLogger().error( "Failed to get valid log appender! " +
								   "Owl may need to be restarted!" );
	}

	public LogAppender getAppender()
	{
		return ( LogAppender )MainApp.getLogger().getAppender( LogAppender.APPENDER_NAME );
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		String actionCommand = e.getActionCommand();

		if ( actionCommand.equals( SAVE_MENU_ITEM ) )
		{
			JFileChooser chooser = new JFileChooser( new java.io.File( "." ) );

			if ( chooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION )
			{
				try
				{
					java.io.File file = chooser.getSelectedFile();
					java.io.FileWriter fw = new java.io.FileWriter( file, false );
					java.io.PrintWriter pw = new java.io.PrintWriter( fw, true );
					pw.println( textPane.getText() );
					fw.close();
					pw.close();
				}
				catch ( Exception ex ) {}
			}
		}

		else if ( actionCommand.equals( PRINT_MENU_ITEM ) )
		{
			PrintUtilities printUtils = new PrintUtilities();
			printUtils.printComponent( textPane );
		}

		else if ( actionCommand.equals( SET_CHAR_MAX_MENU_ITEM ) )
		{
			String maxCharStr = JOptionPane.showInputDialog( this,
											"Enter the maximum number " +
											"of characters the log window " +
											"will show before clearing.\n" +
											"Current value: " +
											getMaxCharCount() );

			if ( maxCharStr != null && !maxCharStr.equals( "" ) )
			{
				try
				{
					int maxCount = Integer.parseInt( maxCharStr );
					setMaxCharCount( maxCount );
				}
				catch ( Exception ex ) { MainApp.getLogger().error( ex ); }
			}
		}

		else if ( actionCommand.equals( COPY_MENU_ITEM ) )
		{
			textPane.selectAll();
			textPane.copy();
			textPane.select( 0, 0 );
		}

		else if ( actionCommand.equals( CLEAR_LOG_MENU_ITEM ) )
		{
			if ( e.getSource() != null )
			{
				JCheckBoxMenuItem menuItem = ( JCheckBoxMenuItem )e.getSource();
				clearLog( menuItem.isSelected() );

				MainApp.info( "\"" + CLEAR_LOG_MENU_ITEM + "\" " +
							 ( menuItem.isSelected() ? "ENABLED!" : "DISABLED!" ) );
			}
		}

		else if ( actionCommand.equals( CLEAR_NOW_MENU_ITEM ) )
		{
			clearNow();
		}

		else if ( actionCommand.equals( STACK_TRACE_MENU_ITEM ) )
		{
			if ( e.getSource() != null )
			{
				JCheckBoxMenuItem menuItem = ( JCheckBoxMenuItem )e.getSource();
				MainApp.getLogger().showStackTrace( menuItem.isSelected() );

				MainApp.info( "\"" + STACK_TRACE_MENU_ITEM + "\" " +
						 	 ( menuItem.isSelected() ? "ENABLED!" : "DISABLED!" ) );
			}
		}
		else if ( actionCommand.equals( LOG_API_ON_MENU_ITEM ) )
		{
			try
			{
				if ( e.getSource() != null )
				{
					JCheckBoxMenuItem menuItem = ( JCheckBoxMenuItem )e.getSource();
					CameraAPI.LogAPICmds( menuItem.isSelected() );
					clearLog( !menuItem.isSelected() );

					MainApp.info( "\"" + LOG_API_ON_MENU_ITEM + "\" " +
								 ( menuItem.isSelected() ? "ENABLED!" : "DISABLED!" ) );
				}
			}
			catch ( Exception ex ) {}
		}

		else
			super.actionPerformed( e );
	}

    //--------------------------------------------------------------------------
    //   Protected Methods:
    //--------------------------------------------------------------------------
	@Override
	protected void loadPreferences()
	{
		super.loadPreferences();

		String prefKey = null;

		if ( getAppender() != null )
		{
			prefKey = getTitle() + MAX_CHAR_COUNT_PREF;
			int maxCharCount = MainApp.getPreferences().getInt( prefKey, 0 );
			setMaxCharCount( maxCharCount );

			prefKey = getTitle() + IS_CLEARING_PREF;
			boolean shouldClear = MainApp.getPreferences().getBoolean( prefKey, true );
			setPopupMenuCheckBox( CLEAR_LOG_MENU_ITEM, shouldClear );
			clearLog( shouldClear );

			prefKey = getTitle() + STACK_TRACE_PREF;
			shouldClear = MainApp.getPreferences().getBoolean( prefKey, false );
			setPopupMenuCheckBox( STACK_TRACE_MENU_ITEM, shouldClear );

			try
			{
				prefKey = getTitle() + LOG_API_PREF;
				shouldClear = MainApp.getPreferences().getBoolean( prefKey, false );
				setPopupMenuCheckBox( LOG_API_ON_MENU_ITEM, shouldClear );
				CameraAPI.LogAPICmds( shouldClear );
			}
			catch ( Exception e )
			{
				setPopupMenuCheckBox( LOG_API_ON_MENU_ITEM, false );
			}
		}
	}

	@Override
	protected void savePreferences()
	{
		super.savePreferences();

		String prefKey = null;

		if ( getAppender() != null )
		{
			prefKey = getTitle() + MAX_CHAR_COUNT_PREF;
			MainApp.getPreferences().putInt( prefKey, getMaxCharCount() );

			prefKey = getTitle() + IS_CLEARING_PREF;
			MainApp.getPreferences().putBoolean( prefKey, isClearing() );

			prefKey = getTitle() + STACK_TRACE_PREF;
			MainApp.getPreferences().putBoolean(
					prefKey, isPopupMenuCheckBoxSelected( STACK_TRACE_MENU_ITEM ) );

			prefKey = getTitle() + LOG_API_PREF;
			MainApp.getPreferences().putBoolean(
					prefKey, isPopupMenuCheckBoxSelected( LOG_API_ON_MENU_ITEM ) );
		}
	}

	protected boolean isPopupMenuCheckBoxSelected( String item )
	{
		boolean isSelected = false;

		MenuElement[] menuElements = popupMenu.getSubElements();

		for ( int i=0; i<menuElements.length; i++ )
		{
			Component comp = menuElements[ i ].getComponent();

			if ( JCheckBoxMenuItem.class.isInstance( comp ) )
			{
				JCheckBoxMenuItem menuItem = ( JCheckBoxMenuItem )comp;

				if ( menuItem.getText().equals( item ) )
				{
					isSelected = menuItem.isSelected();
				}
			}
		}

		return isSelected;
	}

	protected void setPopupMenuCheckBox( String item, boolean select )
	{
		MenuElement[] menuElements = popupMenu.getSubElements();

		for ( int i=0; i<menuElements.length; i++ )
		{
			Component comp = menuElements[ i ].getComponent();

			if ( JCheckBoxMenuItem.class.isInstance( comp ) )
			{
				JCheckBoxMenuItem menuItem = ( JCheckBoxMenuItem )comp;

				if ( menuItem.getText().equals( item ) )
				{
					menuItem.setSelected( select );
				}
			}
		}
	}

    //--------------------------------------------------------------------------
    //   Private Classes:
    //--------------------------------------------------------------------------
	private class LogMouseListener extends MouseAdapter
	{
		 @Override
		public void mouseClicked( MouseEvent e )
		 {
			if ( SwingUtilities.isRightMouseButton( e ) && popupMenu != null )
			{
				popupMenu.show( e.getComponent(), e.getX(), e.getY() );
			}
		 }
	}
}
