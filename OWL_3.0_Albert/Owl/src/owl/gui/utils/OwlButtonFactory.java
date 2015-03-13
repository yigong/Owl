package owl.gui.utils;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import owl.main.MainApp;



public class OwlButtonFactory
{
	// +--------------------------------------------------------------------------------------------+
    // |  Create ICON ToolBar Buttons                                                               |
    // +--------------------------------------------------------------------------------------------+
    // |  Properties: Set ICON, ACTION, ACTION LISTENER                                             |
    // +--------------------------------------------------------------------------------------------+
	public static JButton createNewToolbarButton( Icon icon, String action, ActionListener listener )
	{
		JButton button = new JButton( icon );
		button.setBorderPainted( false );
		button.setFocusPainted( false );
		button.setActionCommand( action );
		button.addActionListener( listener );

		return button;
	}

	// +--------------------------------------------------------------------------------------------+
    // |  Create TEXT ToolBar Buttons                                                               |
    // +--------------------------------------------------------------------------------------------+
    // |  Properties: Set ACTION, ACTION LISTENER                                                   |
    // +--------------------------------------------------------------------------------------------+
	public static JButton createNewToolbarButton( String action, ActionListener listener )
	{
		JButton button = new JButton( action );
		button.setBorderPainted( false );
		button.setFocusPainted( false );
		button.setActionCommand( action );
		button.addActionListener( listener );

		return button;
	}

    // +--------------------------------------------------------------------------------------------+
    // |  Create "Normal" Text Button                                                               |
    // +--------------------------------------------------------------------------------------------+
    // |  Properties: Set SIZE, TOOLTIP, ACTION, ACTION LISTENER                                    |
    // +--------------------------------------------------------------------------------------------+
	public static JButton createButton( int width, int height, String tooltip, String action, ActionListener listener )
	{
		return OwlButtonFactory.createButton( action, width, height, tooltip, action, listener );
	}

    // +--------------------------------------------------------------------------------------------+
    // |  Create "Normal" Text Button                                                               |
    // +--------------------------------------------------------------------------------------------+
    // |  Properties: Set TEXT, SIZE, TOOLTIP, ACTION, ACTION LISTENER                              |
    // +--------------------------------------------------------------------------------------------+
	public static JButton createButton( String text, int width, int height, String tooltip, String action, ActionListener listener )
	{
		JButton button = new JButton( text );

		button.setFocusPainted( false );
		button.setToolTipText( tooltip );
		button.setPreferredSize( new Dimension( width, height ) );
		button.setActionCommand( action );
		button.addActionListener( listener );
		
		return button;
	}

	// +--------------------------------------------------------------------------------------------+
    // |  Create ICON Button                                                                        |
    // +--------------------------------------------------------------------------------------------+
	// |  Properties: Set ICON ( name only ), TOOLTIP, ACTION, ACTION LISTENER                      |
    // +--------------------------------------------------------------------------------------------+
	public static JButton createIconButton( String iconName, String tooltip, String action, ActionListener listener )
	{
		return OwlButtonFactory.createIconButton( iconName, 25, 20, tooltip, action, listener );
	}

	// +--------------------------------------------------------------------------------------------+
    // |  Create ICON Button                                                                        |
    // +--------------------------------------------------------------------------------------------+
	// |  Properties: Set ICON ( name only ), SIZE, TOOLTIP, ACTION, ACTION LISTENER                |
    // +--------------------------------------------------------------------------------------------+
	public static JButton createIconButton( String iconName, int width, int height, String tooltip,
											String action, ActionListener listener )
	{
		JButton button = new JButton( new ImageIcon( MainApp.getBitmapPath() + iconName ) );

		button.setFocusPainted( false );
		button.setToolTipText( tooltip );
		button.setPreferredSize( new Dimension( width, height ) );
		button.setActionCommand( action );
		button.addActionListener( listener );

		return button;
	}

	// +--------------------------------------------------------------------------------------------+
    // |  Create ICON Button                                                                        |
    // +--------------------------------------------------------------------------------------------+
	// |  Properties: Set ICON ( full path ), TOOLTIP, DIMEN, ACTION, ACTION LISTENER               |
    // +--------------------------------------------------------------------------------------------+
	public static JButton create( Icon icon, String tooltip, Dimension dim, String ActionCmd, ActionListener al )
	{
		JButton button = new JButton( icon );

		button.setPreferredSize( dim );
		button.setHorizontalAlignment( SwingConstants.CENTER );
		button.setToolTipText( tooltip );
		button.addActionListener( al );
		button.setActionCommand( ActionCmd );
		button.setFocusPainted( false );

		return button;
	}

	// +--------------------------------------------------------------------------------------------+
    // |  Create ICON Button                                                                        |
    // +--------------------------------------------------------------------------------------------+
	// |  Properties: Set ICON ( full path ), TOOLTIP, DIMEN, ACTION LISTENER                       |
    // +--------------------------------------------------------------------------------------------+
	public static JButton create( Icon icon, String tooltip, Dimension dim, ActionListener al )
	{
		return OwlButtonFactory.create( icon, tooltip, dim, tooltip, al );
	}
}
