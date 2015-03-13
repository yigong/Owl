package owl.CCParams;

import java.util.ArrayList;
import java.util.Vector;


public class CCScriptGroup extends Vector<CCScript>
{
	private static final long serialVersionUID = -8847311533313186289L;

	public String name;

	public void setName( String name )
	{
		this.name = name;
	}

	public CCScript find( String action )
	{
		for ( int i=0; i<size(); i++ )
		{
			if ( get( i ).action != null )
			{
				if ( get( i ).action.equals( action ) )
				{
					return get( i );
				}
			}
		}

		return ( CCScript )null;
	}

	public void frameDispose()
	{
		CCScript[] ccs = toArray( new CCScript[ size() ] );

		for ( int i=0; i<ccs.length; i++ )
		{
			ccs[ i ].frameDispose();
		}
	}

	public ArrayList<String> getFrameOpenList()
	{
		ArrayList<String>	list = new ArrayList<String>();

		for ( int i=0; i<size(); i++ )
		{
			if ( get( i ).frame != null && get( i ).frame.isVisible() )
			{
				if ( get( i ).text != null )
				{
					list.add( get( i ).text );
				}
			}
		}

		return list;
	}
}
