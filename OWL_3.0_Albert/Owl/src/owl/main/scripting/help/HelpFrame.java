package owl.main.scripting.help;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import owl.main.MainApp;


public class HelpFrame extends JFrame implements TreeSelectionListener, ActionListener
{
	private static final long serialVersionUID = -8036738958819827891L;

	private final String BACK_ACTION	= "Back";
	private final String PRINT_ACTION	= "Print";

	private JEditorPane htmlPane;
	private JTree		tree;
	private URL			defaultURL;
	private URL			lastURL;


	public HelpFrame()
	{
		super( "Scripting Help" );
		super.setIconImage( MainApp.getProgramIcon() );
		super.setSize( new Dimension( 900, 600 ) );

		// Set the default URL
		//
		try
		{
			defaultURL = ( new java.io.File( MainApp.getAppPath() +
											 "help" +
											 System.getProperty( "file.separator" ) +
											 "index.html" ) ).toURI().toURL();

			lastURL = defaultURL;
		}
		catch ( Exception e ) { MainApp.error( "Failed to locate default URL" ); }

		// Create the toolbar
		//
		JButton backButton = new JButton( "Back", new ImageIcon( MainApp.getBitmapPath() + "Back.gif" ) );
		backButton.setPreferredSize( new Dimension( 25, 25 ) );
		backButton.setToolTipText( "Back To Last URL" );
		backButton.setFocusPainted( false );
		backButton.setActionCommand( BACK_ACTION );
		backButton.addActionListener( this );

		JButton printButton = new JButton( "Print", new ImageIcon( MainApp.getBitmapPath() + "Print.gif" ) );
		printButton.setPreferredSize( new Dimension( 25, 25 ) );
		printButton.setToolTipText( "Print" );
		printButton.setFocusPainted( false );
		printButton.setActionCommand( PRINT_ACTION );
		printButton.addActionListener( this );

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable( false );
		toolbar.add( backButton );
		toolbar.add( printButton );
		super.add( toolbar, BorderLayout.NORTH );

		// Create help tree scroll pane
		//
		DefaultMutableTreeNode root = new DefaultMutableTreeNode( "Owl" );
		DefaultMutableTreeNode node = new DefaultMutableTreeNode( "CameraAPI" );
		createNodes( "Constants", node );
		createNodes( "Exceptions", node );
		createNodes( "Methods", node );
		root.add( node );

		node = new DefaultMutableTreeNode( "Scripts" );
		createNodes( "Interpreter", node, false );
		createNodes( "Scripts", node );
		root.add( node );

		tree = new JTree( root );
		tree.setCellRenderer( new HelpRenderer() );
		tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		tree.addTreeSelectionListener( this );
		JScrollPane treeView = new JScrollPane( tree );

		// Create the HTML scroll pane
		//
		htmlPane = new JEditorPane();
		htmlPane.setEditable( false );
		htmlPane.addHyperlinkListener( new HelpHyperlinkListener() );
		JScrollPane htmlView = new JScrollPane( htmlPane );

		// Add the scroll panes to a split pane
		//
		JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		splitPane.setTopComponent( treeView );
		splitPane.setBottomComponent( htmlView );
		splitPane.setDividerLocation( 200 );
		getContentPane().add( splitPane );

		// Set the HTML page to the default URL
		//
		try
		{
			htmlPane.setPage( defaultURL );
		}
		catch ( Exception e ) { MainApp.error( "Failed to load page: " + defaultURL ); }
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( e.getActionCommand().equals( BACK_ACTION ) )
			displayURL( lastURL );

		else if ( e.getActionCommand().equals( PRINT_ACTION ) )
		{
			try
			{
				htmlPane.print();
			}
			catch ( Exception err ) { MainApp.error( err ); }
		}
	}

	public void valueChanged( TreeSelectionEvent e )
	{
		DefaultMutableTreeNode node =
				( DefaultMutableTreeNode )tree.getLastSelectedPathComponent();

		if ( node == null ) return;

		Object nodeInfo = node.getUserObject();

		if ( node.isLeaf() )
		{
			LinkInfo linkInfo = ( LinkInfo )nodeInfo;
			displayURL( linkInfo.textURL );
        }
    }

	private void displayURL( URL url )
	{
		try
		{
			lastURL = htmlPane.getPage();

			if ( url != null )
				htmlPane.setPage( url );
			else
				htmlPane.setText( "File Not Found" );
		}
		catch ( IOException e )
		{
			MainApp.error( "Attempted to read a bad URL: " + url );
		}
	}

	private void createNodes( String nodeName, DefaultMutableTreeNode root, boolean branch )
	{
		DefaultMutableTreeNode branchNode = null;
		DefaultMutableTreeNode linkNode = null;

		// Create tree branch
		//
		if ( branch )
		{
			branchNode = new DefaultMutableTreeNode( nodeName );
			root.add( branchNode );
		}

		File htmlDir = new File( MainApp.getAppPath() +
								 "help" +
								 System.getProperty( "file.separator" ) +
								 nodeName.toLowerCase() +
								 System.getProperty( "file.separator" ) );

		if ( !htmlDir.exists() )
			MainApp.warn( "Help file not found: " + htmlDir.getPath() );

		String[] htmlList = htmlDir.list( new HtmlFileFilter() );

		if ( htmlList == null )
			return;

		Arrays.sort( htmlList );

		for ( int i=0; i<htmlList.length; i++ )
		{
			String linkText = htmlList[ i ].substring( 0, htmlList[ i ].lastIndexOf( '.' ) );
			linkNode = new DefaultMutableTreeNode( new LinkInfo( linkText, htmlDir, htmlList[ i ] ) );

			if ( branchNode != null ) branchNode.add( linkNode );
			else root.add( linkNode );
		}
	}

	private void createNodes( String nodeName, DefaultMutableTreeNode root )
	{
		createNodes( nodeName, root, true );
	}

	private void createCameraAPINodes( DefaultMutableTreeNode root )
	{
		DefaultMutableTreeNode classNode = null;
		DefaultMutableTreeNode linkNode = null;

		// Create CONSTANTS tree branch
		//
		classNode = new DefaultMutableTreeNode( "Constants" );
		root.add( classNode );

		File htmlDir = new File( MainApp.getAppPath() +
								 "help" +
								 System.getProperty( "file.separator" ) +
								 "constants" +
								 System.getProperty( "file.separator" ) );

		String[] htmlList = htmlDir.list( new HtmlFileFilter() );
		Arrays.sort( htmlList );

		for ( int i=0; i<htmlList.length; i++ )
		{
			String linkText = htmlList[ i ].substring( 0, htmlList[ i ].lastIndexOf( '.' ) );
			linkNode = new DefaultMutableTreeNode( new LinkInfo( linkText, htmlDir, htmlList[ i ] ) );
			classNode.add( linkNode );
		}

		// Create EXCEPTIONS tree branch
		//
		classNode = new DefaultMutableTreeNode( "Exceptions" );
		root.add( classNode );

		htmlDir = new File( MainApp.getAppPath() +
							"help" +
							System.getProperty( "file.separator" ) +
							"exceptions" +
							System.getProperty( "file.separator" ) );

		htmlList = htmlDir.list( new HtmlFileFilter() );
		Arrays.sort( htmlList );

		for ( int i=0; i<htmlList.length; i++ )
		{
			String linkText = htmlList[ i ].substring( 0, htmlList[ i ].lastIndexOf( '.' ) );
			linkNode = new DefaultMutableTreeNode( new LinkInfo( linkText, htmlDir, htmlList[ i ] ) );
			classNode.add( linkNode );
		}

		// Create METHODS tree branch
		//
		classNode = new DefaultMutableTreeNode( "Methods" );
		root.add( classNode );

		htmlDir = new File( MainApp.getAppPath() +
							"help" +
							System.getProperty( "file.separator" ) +
							"methods" +
							System.getProperty( "file.separator" ) );

		htmlList = htmlDir.list( new HtmlFileFilter() );
		Arrays.sort( htmlList );

		for ( int i=0; i<htmlList.length; i++ )
		{
			String linkText = htmlList[ i ].substring( 0, htmlList[ i ].lastIndexOf( '.' ) );
			linkNode = new DefaultMutableTreeNode( new LinkInfo( linkText, htmlDir, htmlList[ i ] ) );
			classNode.add( linkNode );
		}
	}

	private class LinkInfo
	{
        public String text;
        public URL textURL;

        public LinkInfo( String linkText, File path, String htmlFilename )
        {
            text = linkText;

            try
            {
				textURL = ( new java.io.File( path.getCanonicalFile() +
											  System.getProperty( "file.separator" ) +
											  htmlFilename ) ).toURI().toURL();
            }
            catch ( Exception e ) { MainApp.error( e ); }

            if ( textURL == null )
            {
                MainApp.error( "Couldn't find file: " + htmlFilename );
            }
        }

        @Override
		public String toString()
        {
            return text;
        }
    }

	private class HelpHyperlinkListener implements HyperlinkListener
	{
		public void hyperlinkUpdate( HyperlinkEvent e )
		{
			if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
			{
				displayURL( e.getURL() );
			}
		}
	}

	private class HelpRenderer extends DefaultTreeCellRenderer
	{
		private static final long serialVersionUID = 2597810500357933811L;

		private Icon closedIcon, openIcon, leafIcon;


		public HelpRenderer()
		{
			closedIcon = new ImageIcon( MainApp.getBitmapPath() + "BookClosed.gif" );
			openIcon   = new ImageIcon( MainApp.getBitmapPath() + "BookOpen.gif" );
			leafIcon   = new ImageIcon( MainApp.getBitmapPath() + "HelpLeaf.gif" );
		}

        @Override
		public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel,
        						boolean expanded, boolean leaf, int row, boolean hasFocus )
        {
        	super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );

        	if ( leaf )
        	{
        		setIcon( leafIcon );
        	}
        	else if ( !expanded )
        	{
        		setIcon( closedIcon );
        	}
        	else
        	{
        		setIcon( openIcon );
        	}

        	return this;
        }
    }
}
