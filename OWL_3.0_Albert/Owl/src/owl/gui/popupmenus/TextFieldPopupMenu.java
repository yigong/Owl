package owl.gui.popupmenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import owl.main.MainApp;
import owl.main.owltypes.OwlPopupMenu;


public class TextFieldPopupMenu extends OwlPopupMenu
{
	private static final long serialVersionUID = 6383458416000903508L;

	private JTextField m_textfield;


	public TextFieldPopupMenu( JTextField txtfld )
	{
		super( "TextField Options" );

		m_textfield = txtfld;

		ImageIcon icon = new ImageIcon( MainApp.getBitmapPath() + "ArrowLeft.png" );
		add( new JMenuItem( "Clear", icon ) ).addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent ae )
			{
				m_textfield.setText( "" );
			}
		} );

		icon = new ImageIcon( MainApp.getBitmapPath() + "cut-blu.png" );
		add( new JMenuItem( "Cut", icon ) ).addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent ae )
			{
				m_textfield.selectAll();
				m_textfield.cut();
			}
		} );

		icon = new ImageIcon( MainApp.getBitmapPath() + "copy.png" );
		add( new JMenuItem( "Copy", icon ) ).addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent ae )
			{
				m_textfield.selectAll();
				m_textfield.copy();
				m_textfield.select( 0, 0 );
			}
		} );

		icon = new ImageIcon( MainApp.getBitmapPath() + "paste.png" );
		add( new JMenuItem( "Paste", icon ) ).addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent ae )
			{
				m_textfield.paste();
			}
		} );

		setPreferredSize( new java.awt.Dimension( 120, getPreferredSize().height ) );
	}
}
