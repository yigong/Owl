package owl.PCIConfig;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import owl.main.MainApp;



public class CfgSpButtonCellRenderer extends JButton implements TableCellRenderer
{
	private static final long serialVersionUID = -9112433950935495842L;


	public Component getTableCellRendererComponent( JTable table, Object value,
													boolean isSelected, boolean hasFocus,
													int row, int col )
	{
		if ( value instanceof javax.swing.JButton )
		{
    		if ( isSelected )
     		{
     			setIcon( new ImageIcon( MainApp.getBitmapPath() + "PopupMenu5.gif" ) );
     		}
     		else
     		{
     			setIcon( new ImageIcon( MainApp.getBitmapPath() + "PopupMenu.gif" ) );     			
     		}
		}

		return this;
	}
}
