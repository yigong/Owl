package owl.PCIConfig;

import javax.swing.table.AbstractTableModel;


public class CfgSpTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 4765365040471453252L;
	private String[] columnNames = null;
    private Object[][] data = null;

    public CfgSpTableModel( String[] columnNames, Object[][] data )
    {
    	this.columnNames = columnNames;
    	this.data = data;
    }

    public boolean isCellEditable( int row, int column )
    {
    	if( column == 3 )
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
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

    public String getColumnName(int col)
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
