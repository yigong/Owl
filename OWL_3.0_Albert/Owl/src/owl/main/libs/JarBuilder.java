package owl.main.libs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import owl.gui.utils.OwlButtonFactory;
import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;
import owl.main.owltypes.OwlDirChooser;
import owl.main.owltypes.OwlFileChooser;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlIconedFileChooser;


public class JarBuilder extends OwlFrame implements ActionListener
{
	private static final long serialVersionUID = -7641330395902661451L;

	private final String CREATE_JAR_ACTION	= "CREATE";
	private final String CLASS_ADD_ACTION	= "CLASS ADD";
	private final String CLASS_DEL_ACTION	= "CLASS DEL";
	private final String PKG_ADD_ACTION		= "PKG ADD";
	private final String DLL_ADD_ACTION		= "DLL ADD";
	private final String DLL_DEL_ACTION		= "DLL DEL";

	private final String DLL_PATH_PREF		= "DllFilePath";
	private final String CLZ_PATH_PREF		= "ClassFilePath";
	private final String PKG_PATH_PREF		= "PkgFilePath";
	private final String JAR_PATH_PREF		= "JarFilePath";

	private Vector<String>	dllVec;
	private Vector<String>	classVec;
	private Vector<PkgInfo>	pkgVec;
	private JList			dllList;
	private JList			classList;
	private String			dllFilePath;
	private String			classFilePath;
	private String			pkgFilePath;
	private String			jarFilePath;


	public class PkgInfo extends Object
	{
		public String sPkgName;
		public String sPkgPath;
		public String sClassName;

		public PkgInfo( String pkgName, String pkgPath )
		{
			sPkgName   = pkgName;
			sPkgPath   = pkgPath;
			sClassName = ( String )null;
		}
	}


	public JarBuilder()
	{
		super( "JAR Builder", true );

		super.setIconImage(
				( new ImageIcon(
						MainApp.getBitmapPath() + "jar.gif" ) ).getImage() );

		JPanel panel = new JPanel();
		panel.setLayout( gbl );

		addComponent( panel, createDllPanel(),   0, 0, 0, 0, 0, 0, 1, 1 );
		addComponent( panel, createClassPanel(), 0, 0, 0, 0, 1, 0, 1, 1 );

		JToolBar toolbar = new JToolBar();
		toolbar.add( super.createBoldToolbarButton( CREATE_JAR_ACTION, Color.black ) );
		toolbar.add( super.createNewToolbarButton( "LOAD" ) );
		super.appendToolbar( toolbar );

		addComponent( toolbar, super.TOOLBAR_INDEX );
		addComponent( panel,   super.CENTER_CONTAINER_INDEX );

		pack();

		OwlUtilities.centerFrame( this );

		dllVec    = new Vector<String>();
		classVec  = new Vector<String>();
		pkgVec    = new Vector<PkgInfo>();

		dllFilePath   = System.getProperty( "user.dir" );
		classFilePath = System.getProperty( "user.dir" );
		pkgFilePath   = System.getProperty( "user.dir" );
		jarFilePath   = System.getProperty( "user.dir" );

		loadPreferences();
	}

	public void actionPerformed( ActionEvent e )
	{
		// +---------------------------------------------------+
		// | CREATE JAR FILE
		// +---------------------------------------------------+
		if ( e.getActionCommand().equals( CREATE_JAR_ACTION ) )
		{
			OwlFileChooser fileChooser =
						new OwlFileChooser( new File( jarFilePath ),
											"jar" );

			if ( fileChooser.saveDialog() )
			{
				Vector<String> cmdVec = new Vector<String>();

				jarFilePath = fileChooser.getSelectedFile().getParent();

				if ( fileChooser.getSelectedFile().exists() )
				{
					if ( !fileChooser.getSelectedFile().delete() )
					{
						MainApp.warn( "Failed to delete existing file: " +
									   fileChooser.getSelectedFile() );
					}
				}

				cmdVec.add( "jar" );
				cmdVec.add( "cf" );
				cmdVec.add( fileChooser.getSelectedFile().getPath() );

				if ( !dllVec.isEmpty() )
				{
					for ( int i=0; i<dllVec.size(); i++ )
					{
						cmdVec.add( "-C" );
						cmdVec.add( OwlUtilities.getPath( dllVec.get( i ) ) );

						cmdVec.add( dllVec.get( i ).substring( dllVec.get( i ).lastIndexOf(
									System.getProperty( "file.separator" ) ) + 1 ) );
					}
				}

				if ( !pkgVec.isEmpty() )
				{
					PkgInfo pkgInfo = ( PkgInfo )null;

					for ( int i=0; i<pkgVec.size(); i++ )
					{
						pkgInfo = pkgVec.get( i );

						cmdVec.add( "-C" );
						cmdVec.add( pkgInfo.sPkgPath );
						cmdVec.add( pkgInfo.sClassName );
					}
				}

				if ( !classVec.isEmpty() )
				{
					for ( int i=0; i<classVec.size(); i++ )
					{
						if ( !( new File( classVec.get( i ) ) ).isDirectory() )
						{
							cmdVec.add( "-C" );
							cmdVec.add( OwlUtilities.getPath( classVec.get( i ) ) );

							cmdVec.add( classVec.get( i ).substring( classVec.get( i ).lastIndexOf(
										System.getProperty( "file.separator" ) ) + 1 ) );
						}
					}
				}

				try
				{
					Process proc =
						Runtime.getRuntime().exec( cmdVec.toArray( new String[ cmdVec.size() ] ) );
				}
				catch ( Exception ex )
				{
					MainApp.error( ex );
				}

				MainApp.info( "JAR successfully built!" );
			}
		}

		// +---------------------------------------------------+
		// | ADD DLL(S)
		// +---------------------------------------------------+
		else if ( e.getActionCommand().equals( DLL_ADD_ACTION ) )
		{
			OwlIconedFileChooser fileChooser
					= new OwlIconedFileChooser(
							  new File( dllFilePath ),
							  new String[] { "dll" },
							  new String[] { "Native Library ( *.dll )" },
							  new ImageIcon[] { new ImageIcon( MainApp.getBitmapPath() +
									  						   "libs.gif" ) } );

			if ( !System.getProperty( "os.name" ).toLowerCase().contains( "win" ) )
			{
				fileChooser = new OwlIconedFileChooser( new File( System.getProperty( "user.dir" ) ),
						  	  new String[] { "so" },
						  	  new String[] { "Native Library ( *.so )" },
						  	  new ImageIcon[] { new ImageIcon( MainApp.getBitmapPath() + "libs.gif" ) } );
			}

			fileChooser.setMultiSelectionEnabled( true );

			if ( fileChooser.openDialog() )
			{
				File[] files = fileChooser.getSelectedFiles();

				if ( files != null )
				{
					dllFilePath = files[ 0 ].getParent();
	
					for ( int i=0; i<files.length; i++ )
					{
						dllVec.add( files[ i ].getAbsolutePath() );
					}
	
					dllList.setListData( dllVec );
				}
			}
		}

		// +---------------------------------------------------+
		// | DELETE DLL ( from list )
		// +---------------------------------------------------+
		else if ( e.getActionCommand().equals( DLL_DEL_ACTION ) )
		{
			dllVec.removeAll( Arrays.asList( dllList.getSelectedValues() ) );
			dllList.setListData( dllVec );
		}

		// +---------------------------------------------------+
		// | ADD CLASS(ES)
		// +---------------------------------------------------+
		else if ( e.getActionCommand().equals( CLASS_ADD_ACTION ) )
		{
			OwlIconedFileChooser fileChooser
					= new OwlIconedFileChooser(
							  new File( classFilePath ),
							  new String[] { "class" },
							  new String[] { "Java Class ( *.class )" },
							  new ImageIcon[] { new ImageIcon( MainApp.getBitmapPath() +
									  						   "class.gif" ) } );

			fileChooser.setMultiSelectionEnabled( true );

			if ( fileChooser.openDialog() )
			{
				File[] files = fileChooser.getSelectedFiles();

				if ( files != null )
				{
					classFilePath = files[ 0 ].getParent();

					for ( int i=0; i<files.length; i++ )
					{
						classVec.add( files[ i ].getAbsolutePath() );
					}
	
					classList.setListData( classVec );
				}
			}
		}

		// +---------------------------------------------------+
		// | ADD PACKAGE
		// +---------------------------------------------------+
		else if ( e.getActionCommand().equals( PKG_ADD_ACTION ) )
		{
			OwlDirChooser dirChooser =
						new OwlDirChooser( new File( pkgFilePath ) );

			if ( dirChooser.openDialog() )
			{
				File pkgDir = dirChooser.getSelectedFile();

				if ( pkgDir != null )
				{
					pkgFilePath = pkgDir.getPath();

					readPackageDir( pkgDir );
				}
			}
		}

		// +---------------------------------------------------+
		// | DELETE CLASS(ES)/JARS ( from list )
		// +---------------------------------------------------+
		else if ( e.getActionCommand().equals( CLASS_DEL_ACTION ) )
		{
			classVec.removeAll( Arrays.asList( classList.getSelectedValues() ) );
			classList.setListData( classVec );
		}

		// +---------------------------------------------------+
		// | PASS TO SUPER
		// +---------------------------------------------------+
		else
		{
			super.actionPerformed( e );
		}
	}

	private void readPackageDir( File pkgDir )
	{
		readPackageDir( pkgDir, null );
	}

	private void readPackageDir( File pkgDir, String superPkgName )
	{
		String  pkgName = ( superPkgName == null ? pkgDir.getName() : ( superPkgName + "/" + pkgDir.getName() ) );
		String  pkgPath = ( superPkgName == null ? pkgDir.getParent() : getPkgParentPath( pkgName, pkgDir.getAbsolutePath() ) );
		PkgInfo pkgInfo = ( PkgInfo )null;

		FileFilter filter = new FileFilter()
		{
		    public boolean accept( File file )
		    {
				if ( file.isDirectory() )
				{
					return true;
				}

		        return file.getName().endsWith( ".class" );
		    }
		};

		File[] pkgFiles = pkgDir.listFiles( filter );

		for ( int i=0; i<pkgFiles.length; i++ )
		{
			if ( pkgFiles[ i ].isDirectory() )
			{
				readPackageDir( pkgFiles[ i ], pkgName );
				continue;
			}

			pkgInfo = new PkgInfo( pkgName, pkgPath );
			pkgInfo.sClassName = pkgName + "/" + pkgFiles[ i ].getName();

			pkgVec.add( pkgInfo );
		}

		if ( superPkgName == null )
		{
			classVec.add( pkgDir.getPath() );
			classList.setListData( classVec );
		}
	}

	private String getPkgParentPath( String pkgName, String pkgPath )
	{
		String retStr = null;

		String newName = pkgName.replace( "/", System.getProperty( "file.separator" ) );
		int pkgIndex = pkgPath.lastIndexOf( newName );	

		if ( pkgIndex >= 0 )
		{
			retStr = pkgPath.substring( 0, pkgIndex );
		}

		return retStr;
	}

	private JPanel createDllPanel()
	{
		JPanel panel = new JPanel();
		panel.setBorder( BorderFactory.createTitledBorder( "Dynamic Link Library List" ) );
		panel.setLayout( super.gbl );

		dllList = new JList();
		JScrollPane dllListPane = new JScrollPane( dllList );
		Dimension dimen = dllListPane.getPreferredSize();
		dllListPane.setPreferredSize( new Dimension( dimen.width + 120, dimen.height ) );
		addComponent( panel, dllListPane, 0, 0, 0, 0, 0, 0, 2, 1 );

		JButton dllAddButton = OwlButtonFactory.createIconButton( "Plus2.gif", 26, 26, null, DLL_ADD_ACTION, this );
		addComponent( panel, dllAddButton, 0, 0, 0, 0, GridBagConstraints.NORTHWEST, 0, 1, 1, 1 );

		JButton dllDelButton = OwlButtonFactory.createIconButton( "Off.gif", 26, 26, null, DLL_DEL_ACTION, this );
		addComponent( panel, dllDelButton, 0, 0, 0, 0, GridBagConstraints.NORTHWEST, 1, 1, 1, 1 );

		return panel;
	}

	private JPanel createClassPanel()
	{
		JPanel panel = new JPanel();
		panel.setBorder( BorderFactory.createTitledBorder( "Class List" ) );
		panel.setLayout( super.gbl );

		classList = new JList();
		JScrollPane classListPane = new JScrollPane( classList );
		Dimension dimen = classListPane.getPreferredSize();
		classListPane.setPreferredSize( new Dimension( dimen.width + 120, dimen.height ) );
		super.addComponent( panel, classListPane, 0, 0, 0, 0, 0, 0, 3, 1 );

		JButton classAddButton = OwlButtonFactory.createIconButton( "class.gif", 26, 26, null, CLASS_ADD_ACTION, this );
		super.addComponent( panel, classAddButton, 0, 0, 0, 0, GridBagConstraints.NORTHWEST, 0, 1, 1, 1 );

		JButton pkgAddButton = OwlButtonFactory.createIconButton( "pkg.gif", 26, 26, null, PKG_ADD_ACTION, this );
		super.addComponent( panel, pkgAddButton, 0, 0, 0, 0, GridBagConstraints.NORTHWEST, 1, 1, 1, 1 );

		JButton classDelButton = OwlButtonFactory.createIconButton( "Off.gif", 26, 26, null, CLASS_DEL_ACTION, this );
		super.addComponent( panel, classDelButton, 0, 0, 0, 0, GridBagConstraints.NORTHWEST, 2, 1, 1, 1 );

		return panel;
	}

	//--------------------------------------------------------------------------
    //   Protected Methods:
    //--------------------------------------------------------------------------
	protected void loadPreferences()
	{
		super.loadPreferences();

		try
		{
			String winName = super.getTitle();

			dllFilePath  = MainApp.getPreferences().get(
						winName + DLL_PATH_PREF, System.getProperty( "user.dir" ) );

			classFilePath = MainApp.getPreferences().get(
						winName + CLZ_PATH_PREF, System.getProperty( "user.dir" ) );

			pkgFilePath   = MainApp.getPreferences().get(
						winName + PKG_PATH_PREF, System.getProperty( "user.dir" ) );

			jarFilePath   = MainApp.getPreferences().get(
						winName + JAR_PATH_PREF, System.getProperty( "user.dir" ) );
		}
		catch ( NullPointerException npe ) {}
	}

	protected void savePreferences()
	{
		super.savePreferences();

		try
		{
			String winName = super.getTitle();

			MainApp.getPreferences().put( winName + DLL_PATH_PREF, dllFilePath );
			MainApp.getPreferences().put( winName + CLZ_PATH_PREF, classFilePath );
			MainApp.getPreferences().put( winName + PKG_PATH_PREF, pkgFilePath );
			MainApp.getPreferences().put( winName + JAR_PATH_PREF, jarFilePath );
		}
		catch ( Exception e )
		{
			MainApp.error( e.toString() );
		}
	}
}
