package owl.main.debug.profiler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Vector;
import javax.swing.JButton;

import owl.main.MainApp;


public class LibraryPanel extends PropertyPanel implements ActionListener
{
	private static final long serialVersionUID = -4009464353842170776L;

	protected final String UPDATE_ACTION	= "Update";


	public LibraryPanel()
	{
		super( ( String[] )null );

		JButton updateButton = new JButton( UPDATE_ACTION );
		updateButton.addActionListener( this );

		add( updateButton );

		m_colNames = new String[ 2 ];
		m_colNames[ 0 ] = "Name";
		m_colNames[ 1 ] = "Path";

		setData();
	}

	@Override
	public void actionPerformed( ActionEvent ae )
	{
		if ( ae.getActionCommand().equals( UPDATE_ACTION ) )
		{
			setData();
		}
	}

	public void setData()
	{
		Object[][] libData = { { "", "", "" } };
		Field libs = null;

		try
		{
			libs = ClassLoader.class.getDeclaredField( "loadedLibraryNames" );
			libs.setAccessible( true );

			final Vector<String> libVec =
							( Vector<String> )libs.get(
										ClassLoader.getSystemClassLoader() );

			Collections.sort( libVec );

			libData = new Object[ libVec.size() ][ 2 ];

			for ( int j=0; j<libVec.size(); j++ )
			{
				File libFile = new File( libVec.get( j ) );

				String libName = libFile.getName();

				if ( libVec.get( j ).contains( "\\API" ) )
				{
					libName = "<html><font color=#0000FF>" + libFile.getName() + "</font></html>";
				}
				else if ( !libVec.get( j ).contains( "jre" ) )
				{
					libName = "<html><font color=#CD0000>" + libFile.getName() + "</font></html>";
				}

				libData[ j ][ 0 ] = libName;
				libData[ j ][ 1 ] = libFile.getParentFile();
			}
		}
		catch ( Exception e )
		{
			MainApp.error( e.getMessage() );
		}

		super.setData( libData );
	}
}
