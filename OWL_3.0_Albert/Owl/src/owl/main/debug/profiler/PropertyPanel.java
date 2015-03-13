package owl.main.debug.profiler;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import owl.main.owltypes.OwlInfoTable;



public class PropertyPanel extends JPanel
{
	private static final long serialVersionUID = -940700281207970272L;

	protected String[] m_colNames = { "Property", "Value" };
	protected OwlInfoTable m_table;


	public PropertyPanel( Object[][] data )
	{
		this( ( String[] )null );

		setData( data );
	}

	public PropertyPanel( String[] columnNames )
	{
		super( null );

		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

		m_table = new OwlInfoTable();

		add( new JScrollPane( m_table ) );

		if ( columnNames != null )
		{
			m_colNames = columnNames;
		}

		Object[][] data = { { "", "" } };
		setData( data );
	}

	public void setData( Object[][] data )
	{
		m_table.setTableData( m_colNames, data );
		m_table.setColumnWidth( 0, 150 );
	}

	public void setColWidth( int col, int width )
	{
		m_table.setColumnWidth( col, width );
	}
}
