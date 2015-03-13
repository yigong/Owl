package owl.main.owltypes;

import java.awt.Component;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.JTable;


public class OwlInfoTable extends JTable
{
	private static final long serialVersionUID = -5856339892070921246L;
	protected JTableHeader m_tableHeader = null;


	public OwlInfoTable()
	{
		setFillsViewportHeight( true );

		m_tableHeader = getTableHeader();

		MouseListener[] tableHeaderMouseListeners =
								m_tableHeader.getMouseListeners();

	    if ( tableHeaderMouseListeners != null )
	    {
	    	for ( int i=0; i<tableHeaderMouseListeners.length; i++ )
	    	{
	    		m_tableHeader.removeMouseListener(
	    							tableHeaderMouseListeners[ i ] );
	    	}
	    }
	}

	public void setTableData( String[] columnNames, Object[][] data )
	{
		try
		{
			setModel( new TableModel( columnNames, data ) );
		}
		catch ( Exception e ) {}
	}

	public void setColumnWidth( int column, int size )
	{
		TableColumnModel tableColumnModel = m_tableHeader.getColumnModel();

		if ( column < tableColumnModel.getColumnCount() )
		{
			TableColumn tableColumn = tableColumnModel.getColumn( column );
			tableColumn.setMinWidth( size );
			tableColumn.setMaxWidth( size );
			tableColumn.setWidth( size );
			tableColumn.setPreferredWidth( size );
	
		    m_tableHeader.setColumnModel( tableColumnModel );
	
		    setTableHeader( m_tableHeader );
		}
	}

	protected class TableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 7180462513143455226L;
		private String[] columnNames = null;
	    private Object[][] data = null;

	    public TableModel( String[] columnNames, Object[][] data )
	    {
	    	this.columnNames = columnNames;
	    	this.data = data;
	    }

	    public int getColumnCount()
	    {
	    	if ( columnNames != null )
	    	{
	    		return columnNames.length;
	    	}
	    	else
	    	{
	    		return 0;
	    	}
	    }

	    public int getRowCount()
	    {
	    	if ( data != null )
	    	{
	    		return data.length;
	    	}
	    	else
	    	{
	    		return 0;
	    	}
	    }

	    public String getColumnName( int col )
	    {
	    	if ( columnNames != null )
	    	{
	    		return columnNames[ col ];
	    	}
	    	else
	    	{
	    		return ( String )null;
	    	}
	    }

	    public Object getValueAt( int row, int col )
	    {
	    	if ( data != null )
	    	{
	    		return data[ row ][ col ];
	    	}
	    	else
	    	{
	    		return ( Object )null;
	    	}
	    }
	}

	public class CellPaneRenderer extends JTextPane implements TableCellRenderer
	{
		private static final long serialVersionUID = -4735127659975281188L;

		public Component getTableCellRendererComponent( JTable table, Object value,
														boolean isSelected, boolean hasFocus,
														int row, int col )
		{
			if ( value instanceof java.lang.Integer )
			{
				setText( Integer.toHexString( ( java.lang.Integer )value ) );
			}

			else if ( value instanceof java.lang.String )
			{
				setText( ( String )value );
			}

			if ( col < table.getColumnModel().getColumnCount() )
			{
				setSize( table.getColumnModel().getColumn( col ).getWidth(),
						 getPreferredSize().height );
			}

			if ( table.getRowHeight( row ) != getPreferredSize().height )
			{
				table.setRowHeight( row, getPreferredSize().height );
			}

			return this;
		}
	}

	public class CellLabelRenderer extends JLabel implements TableCellRenderer
	{
		private static final long serialVersionUID = -4735127659975281188L;

		public CellLabelRenderer()
		{
			super();
		}

		public Component getTableCellRendererComponent( JTable table, Object value,
														boolean isSelected, boolean hasFocus,
														int row, int col )
		{
			if ( value instanceof java.lang.Integer )
			{
				setText( Integer.toHexString( ( java.lang.Integer )value ) );
			}

			else if ( value instanceof java.lang.String )
			{
				setText( ( String )value );
			}

			if ( col < table.getColumnModel().getColumnCount() )
			{
				setSize( table.getColumnModel().getColumn( col ).getWidth(),
						 getPreferredSize().height );
			}

			if ( table.getRowHeight( row ) != getPreferredSize().height )
			{
				table.setRowHeight( row, getPreferredSize().height );
			}

			return this;
		}
	}
}
