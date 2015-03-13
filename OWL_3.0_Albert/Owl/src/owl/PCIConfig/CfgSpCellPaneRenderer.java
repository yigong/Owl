package owl.PCIConfig;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;



public class CfgSpCellPaneRenderer extends JLabel implements TableCellRenderer
{
	private static final long serialVersionUID = -4896064504324210660L;
	protected String m_hexFormat;


	public CfgSpCellPaneRenderer()
	{
		this( "%08X" );
	}

	public CfgSpCellPaneRenderer( String hexFormat )
	{
		m_hexFormat = hexFormat;
	}

	public Component getTableCellRendererComponent( JTable table, Object value,
													boolean isSelected, boolean hasFocus,
													int row, int col )
	{
		if ( value instanceof java.lang.Integer )
		{
			setHorizontalAlignment( SwingConstants.CENTER );
			setText( String.format( m_hexFormat, ( ( Integer ) value ) ) );
		}
		else if ( value instanceof java.lang.String )
		{
			setHorizontalAlignment( SwingConstants.LEFT );
			setText( ( String )value );
		}

		if ( isSelected || hasFocus )
		{
			setForeground( Color.red );
		}
		else
		{
			setForeground( Color.black );
		}

		return this;
	}
}
