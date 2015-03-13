package owl.PCIConfig;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


public class CfgSpTable extends JTable
{
	private static final long serialVersionUID = -6919400494567575685L;
	private final String[] COL_NAMES = { "Reg", "Name", "Value", "Details" };
	private CfgSpRowEditorModel m_rowEditorModel;


	public CfgSpTable()
	{
		super();

		m_rowEditorModel = new CfgSpRowEditorModel();

		setRowEditorModel( m_rowEditorModel );

		setFillsViewportHeight( true );
		setDefaultRenderer( Object.class, new CfgSpCellPaneRenderer() );
	}

	public void setRowEditorModel( CfgSpRowEditorModel rm )
	{
		m_rowEditorModel = rm;
	}

	public CfgSpRowEditorModel getRowEditorModel()
	{
		return m_rowEditorModel;
	}

	public TableCellEditor getCellEditor( int row, int col )
	{
		TableCellEditor tmpEditor = null;

		if ( m_rowEditorModel != null )
		{
			tmpEditor = m_rowEditorModel.getEditor( row );
		}

		if ( tmpEditor!=null )
		{
			return tmpEditor;
		}

		return super.getCellEditor( row,col );
	}

	public void setTableValue( Object obj, int row, int col )
	{
		setValueAt( obj, row, col );
	}

	public void setData( Object[][] data )
	{
		setModel( new CfgSpTableModel( COL_NAMES, data ) );
		getColumn( getColumnName( 0 ) ).setCellRenderer( new CfgSpCellPaneRenderer( "%02X" ) );
		getColumn( getColumnName( 3 ) ).setCellRenderer( new CfgSpButtonCellRenderer() );

		setColumnWidth( 0, 100 );
		setColumnWidth( 1, 500 );
		setColumnWidth( 2, 100 );
		setColumnWidth( 3, 30 );
	}

	public void setRowPopupMenu( int row, JButton button )
	{
		CfgSpButtonCellEditor ed = new CfgSpButtonCellEditor( button );
		getRowEditorModel().addEditorForRow( row, ed );
	}

	public void setColumnWidth( int column, int size )
	{
		JTableHeader tableHeader = getTableHeader();

		TableColumnModel tableColumnModel = tableHeader.getColumnModel();

		TableColumn tableColumn = tableColumnModel.getColumn( column );
		tableColumn.setMinWidth( size );
		tableColumn.setMaxWidth( size );
		tableColumn.setWidth( size );
		tableColumn.setPreferredWidth( size );

		tableHeader.setColumnModel( tableColumnModel );

		setTableHeader( tableHeader );
	}

	public void addColumn( Object colName, Object[] colData, int colWidth )
	{
		( ( DefaultTableModel )getModel() ).addColumn( colName, colData );

		int col = ( ( DefaultTableModel )getModel() ).findColumn( ( String )colName );

		if ( col > 0 )
		{
			setColumnWidth( col, colWidth );
		}
	}

	public void addRow( Object[] rowData )
	{
		( ( DefaultTableModel )getModel() ).addRow( rowData );
	}
}
