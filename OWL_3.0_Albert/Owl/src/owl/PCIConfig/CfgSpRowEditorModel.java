package owl.PCIConfig;

import java.util.TreeMap;
import javax.swing.table.TableCellEditor;


public class CfgSpRowEditorModel
{
	private TreeMap<Integer, TableCellEditor> data;

	public CfgSpRowEditorModel()
	{
		data = new TreeMap<Integer, TableCellEditor>();
	}

	public void addEditorForRow( int row, TableCellEditor cellEditor )
	{
		data.put( new Integer( row ), cellEditor );
	}

	public void removeEditorForRow( int row )
	{
		data.remove( new Integer( row ) );
	}

	public TableCellEditor getEditor( int row )
	{
		return ( TableCellEditor )data.get( new Integer( row ) );
	}
}
