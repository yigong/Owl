package owl.PCIConfig;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import owl.main.MainApp;


public class CfgSpButtonCellEditor extends AbstractCellEditor implements TableCellEditor, MouseListener
{    
	private static final long serialVersionUID = -3854035096697911185L;
	private JButton m_button;
    
    public CfgSpButtonCellEditor( JButton button )
    {
        m_button = button;

        m_button.setFocusPainted( false );
        m_button.setBorderPainted( false );
        m_button.addMouseListener( this );
    }
 
    public Object getCellEditorValue()
    {
        return m_button.getText();
    }
 
    public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column )
    {
     	if ( value instanceof javax.swing.JButton )
    	{
     		if ( isSelected )
     		{
     			m_button.setIcon( new ImageIcon( MainApp.getBitmapPath() + "PopupMenu5.gif" ) );
     		}
     		else
     		{
     			m_button.setIcon( new ImageIcon( MainApp.getBitmapPath() + "PopupMenu.gif" ) );     			
     		}
    	}

        return m_button;
    }
 
	@Override
	public void mouseClicked( MouseEvent me )
	{
		this.fireEditingStopped();
	}

	@Override
	public void mouseEntered( MouseEvent me )
	{
		this.fireEditingStopped();
	}

	@Override
	public void mouseExited( MouseEvent me )
	{
		this.fireEditingStopped();
	}

	@Override
	public void mousePressed( MouseEvent me )
	{
		m_button.getComponentPopupMenu().show( me.getComponent(),
											   me.getX(),
											   me.getY() );

		this.fireEditingStopped();
	}

	@Override
	public void mouseReleased( MouseEvent me )
	{
		m_button.getComponentPopupMenu().show( me.getComponent(),
											   me.getX(),
											   me.getY() );

		this.fireEditingStopped();
	}
}
