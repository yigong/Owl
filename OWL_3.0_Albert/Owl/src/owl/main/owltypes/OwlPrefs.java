package owl.main.owltypes;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import owl.main.MainApp;


//+-----------------------------------------------------------------+
//| Class OwlPrefs                                                  |
//+-----------------------------------------------------------------+
//| Any class implementing this one, MUST do the following:         |
//|                                                                 |
//| 1. Each frame, dialog, and panel must have a name assigned to   |
//|    is using .setName() with a non-null string.                  |
//|                                                                 |
//| 2. Each component belonging to the supplied frame, dialog, and  |
//|    panel that you wish to save settings for must have a name    |
//|    assigned to it using .setName().                             |
//|                                                                 |
//| 3. If a superclass declares OwlPrefs, then each subclass MUST   |
//|    instantiate the OwlPrefs constructor or it will not work!    |
//+-----------------------------------------------------------------+
public class OwlPrefs
{
	private Component[] m_comps;
	private JMenuBar    m_menubar;
	private JFrame      m_frame;
	private JDialog     m_dialog;
	private JPanel      m_panel;
	private String      m_sPrefName;

	public OwlPrefs( JFrame frame )
	{
		m_comps     = null;
		m_frame     = frame;
		m_dialog    = null;
		m_panel     = null;
		m_sPrefName = frame.getName();
		m_menubar   = frame.getJMenuBar();
	}

	public OwlPrefs( JDialog dialog )
	{
		m_comps     = null;
		m_frame     = null;
		m_dialog    = dialog;
		m_panel     = null;
		m_sPrefName = dialog.getName();
		m_menubar   = dialog.getJMenuBar();
	}

	public OwlPrefs( JPanel panel )
	{
		m_comps     = null;
		m_frame     = null;
		m_dialog    = null;
		m_panel     = panel;
		m_sPrefName = panel.getName();
		m_menubar   = null;
	}

	// +--------------------------------------------------------------------+
	// |  loadPreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Loads the previously saved component fields and states on startup |
	// +--------------------------------------------------------------------+
	public void loadPreferences()
	{
		if ( m_sPrefName == null || m_sPrefName.isEmpty() )
		{
			return;
		}

		if ( m_comps == null )
		{
	   		if ( m_frame != null )
	   		{
	   			m_comps = m_frame.getContentPane().getComponents();
	   		}

	   		else if ( m_dialog != null )
	   		{
	   			m_comps = m_dialog.getContentPane().getComponents();
	   		}

	   		else if ( m_panel != null )
	   		{
	   			m_comps = m_panel.getComponents();
	   		}
		}

		if ( m_comps == null ) { return; }

		for ( int i=0; i<m_comps.length; i++ )
		{
				loadComponentState( m_comps[ i ] );
		}

		if ( m_menubar != null )
		{
			loadComponentState( m_menubar );
		}
	}

	// +--------------------------------------------------------------------+
	// |  loadComponentState                                                |
	// +--------------------------------------------------------------------+
	// |  Loads the last saved state of the specified component             |
	// +--------------------------------------------------------------------+
	protected void loadComponentState( Component comp )
	{
		String  prefKey = null;
	   	String  strVal  = null;
    	boolean boolVal = false;
    	int     intVal  = 0;

    	if ( comp == null ) { return; }

    	prefKey = m_sPrefName;

		if ( comp instanceof javax.swing.JPanel )
		{
			Component[] panelComps = ( ( JPanel )comp ).getComponents();

			for ( int i=0; i<panelComps.length; i++ )
			{
				loadComponentState( panelComps[ i ] );
			}
		}

		else if ( comp instanceof javax.swing.JTabbedPane )
		{
			Component[] paneComps = ( ( JTabbedPane )comp ).getComponents();

			for ( int i=0; i<paneComps.length; i++ )
			{
				loadComponentState( paneComps[ i ] );
			}
		}

		else if ( comp instanceof javax.swing.JMenuBar )
		{
			Component[] menuComps = ( ( JMenuBar )comp ).getComponents();

			for ( int i=0; i<menuComps.length; i++ )
			{
				loadComponentState( menuComps[ i ] );
			}
		}

    	else if ( comp instanceof javax.swing.JMenu )
    	{
			Component[] menuComps = ( ( JMenu )comp ).getMenuComponents();

    		for ( int i=0; i<menuComps.length; i++ )
    		{
    			loadComponentState( menuComps[ i ] );
    		}
    	}

    	else if ( comp instanceof javax.swing.JCheckBox )
    	{
	 		JCheckBox jcb = ( JCheckBox )comp;
	 		prefKey += jcb.getName();
	 		boolVal = MainApp.getPreferences().getBoolean( prefKey, false );

            if ( ( !jcb.isSelected() && boolVal ) || ( jcb.isSelected() && !boolVal ) )
            {
                    jcb.doClick();
            }
    	}

	 	else if ( comp instanceof javax.swing.JTextField )
	 	{
	 		JTextField jtf = ( JTextField )comp;
	 		prefKey += jtf.getName();
	 		strVal = MainApp.getPreferences().get( prefKey, jtf.getText() );
	 		jtf.setText( strVal );
	 	}
	
	 	else if ( comp instanceof javax.swing.JComboBox )
	 	{
	 		JComboBox jcb = ( JComboBox )comp;
	 		prefKey += jcb.getName();
	 		intVal = MainApp.getPreferences().getInt( prefKey, jcb.getSelectedIndex() );
	 		jcb.setSelectedIndex( intVal );
	 	}

		else if ( comp instanceof javax.swing.JCheckBoxMenuItem )
		{
			JCheckBoxMenuItem jcbmi = ( JCheckBoxMenuItem )comp;
			prefKey += jcbmi.getName();
			boolVal = MainApp.getPreferences().getBoolean( prefKey, false );

			if ( ( !jcbmi.isSelected() && boolVal ) || ( jcbmi.isSelected() && !boolVal ) )
			{
				jcbmi.doClick();
			}
		}

		else if ( comp instanceof javax.swing.JRadioButtonMenuItem )
		{
			JRadioButtonMenuItem jrbmi = ( JRadioButtonMenuItem )comp;
			prefKey += jrbmi.getName();
			boolVal = MainApp.getPreferences().getBoolean( prefKey, jrbmi.isSelected() );
			if ( boolVal ) { jrbmi.doClick(); jrbmi.doClick(); }
		}

	 	else if ( comp instanceof javax.swing.JRadioButton )
	 	{
	 		JRadioButton jrb = ( JRadioButton )comp;
	 		prefKey += jrb.getName();
	 		boolVal = MainApp.getPreferences().getBoolean( prefKey, jrb.isSelected() );
	 		if ( boolVal ) { jrb.doClick(); jrb.doClick(); }
	 	}
	}
	
	// +--------------------------------------------------------------------+
	// |  savePreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Saves the parameter states upon exit                              |
	// +--------------------------------------------------------------------+
	public void savePreferences()
	{
		if ( m_sPrefName == null || m_sPrefName.isEmpty() )
		{
			return;
		}

		if ( m_comps == null )
		{
	   		if ( m_frame != null )
	   		{
	   			m_comps = m_frame.getContentPane().getComponents();
	   		}

	   		else if ( m_dialog != null )
	   		{
	   			m_comps = m_dialog.getContentPane().getComponents();
	   		}

	   		else if ( m_panel != null )
	   		{
	   			m_comps = m_panel.getComponents();
	   		}
		}

		if ( m_comps == null ) { return; }

		for ( int i=0; i<m_comps.length; i++ )
		{
			saveComponentState( m_comps[ i ] );
		}

		if ( m_menubar != null )
		{
			saveComponentState( m_menubar );
		}
	}

	// +--------------------------------------------------------------------+
	// |  saveComponentState                                                |
	// +--------------------------------------------------------------------+
	// |  Saves the state of the specified component                        |
	// +--------------------------------------------------------------------+
	 protected void saveComponentState( Component comp )
	 {
	 	String prefKey  = null;
	
		if ( comp == null ) { return; }
	
		prefKey = m_sPrefName;
	
		if ( comp instanceof javax.swing.JPanel )
		{
	    	Component[] panelComps = ( ( JPanel )comp ).getComponents();
	
			for ( int i=0; i<panelComps.length; i++ )
			{
				saveComponentState( panelComps[ i ] );
			}
		}
	
		else if ( comp instanceof javax.swing.JTabbedPane )
		{
	    	Component[] paneComps = ( ( JTabbedPane )comp ).getComponents();
	
			for ( int i=0; i<paneComps.length; i++ )
			{
				saveComponentState( paneComps[ i ] );
			}
		}
	
		else if ( comp instanceof javax.swing.JMenuBar )
		{
	    	Component[] menuComps = ( ( JMenuBar )comp ).getComponents();
	
	    	for ( int i=0; i<menuComps.length; i++ )
	    	{
	    		saveComponentState( menuComps[ i ] );
	    	}
		}
	
	   	else if ( comp instanceof javax.swing.JMenu )
	   	{
	   		Component[] menuComps = ( ( JMenu )comp ).getMenuComponents();
	
	   		for ( int i=0; i<menuComps.length; i++ )
	   		{
	    		saveComponentState( menuComps[ i ] );
	   		}
	   	}
	
	   	else if ( comp instanceof javax.swing.JCheckBox )
	 	{
	 		JCheckBox jcb = ( JCheckBox )comp;
	 		prefKey += jcb.getName();
		
	 		if ( jcb.getName() != null && !jcb.getName().isEmpty() )
	 		{
	 			MainApp.getPreferences().putBoolean( prefKey, jcb.isSelected() );
	 		}
	 	}
	
	 	else if ( comp instanceof javax.swing.JTextField )
	 	{
	 		JTextField jtf = ( JTextField )comp;
	 		prefKey += jtf.getName();
	
	 		if ( jtf.getName() != null && !jtf.getName().isEmpty() )
	 		{
	 			MainApp.getPreferences().put( prefKey, jtf.getText() );
	 		}
	 	}
	
	 	else if ( comp instanceof javax.swing.JComboBox )
	 	{
	 		JComboBox jcb = ( JComboBox )comp;
	 		prefKey += jcb.getName();
	
	 		if ( jcb.getName() != null && !jcb.getName().isEmpty() )
	 		{
	 			MainApp.getPreferences().putInt( prefKey, jcb.getSelectedIndex() );
	 		}
	 	}
	
	 	else if ( comp instanceof javax.swing.JCheckBoxMenuItem )
	 	{
	 		JCheckBoxMenuItem jcbmi = ( JCheckBoxMenuItem )comp;
	 		prefKey += jcbmi.getName();
	
			if ( jcbmi.getName() != null && !jcbmi.getName().isEmpty() )
			{
				MainApp.getPreferences().putBoolean( prefKey, jcbmi.isSelected() );
			}
		}
	
		else if ( comp instanceof javax.swing.JRadioButtonMenuItem )
		{
			JRadioButtonMenuItem jrbmi = ( JRadioButtonMenuItem )comp;
			prefKey += jrbmi.getName();
	
			if ( jrbmi.getName() != null && !jrbmi.getName().isEmpty() )
			{
				MainApp.getPreferences().putBoolean( prefKey, jrbmi.isSelected() );
			}
		}
	
		else if ( comp instanceof javax.swing.JRadioButton )
		{
			JRadioButton jrb = ( JRadioButton )comp;
			prefKey += jrb.getName();
	
			if ( jrb.getName() != null && !jrb.getName().isEmpty() )
			{
				MainApp.getPreferences().putBoolean( prefKey, jrb.isSelected() );
			}
		}
	}
}
