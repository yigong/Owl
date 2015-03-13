package owl.ptc;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import owl.dir.dialog.DirDialog;
import owl.gui.utils.OwlButtonFactory;
import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;
import owl.main.exposure.ExposeActionListener;
import owl.main.owltypes.OwlBoldButton;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlTextField;



public class PTCFrame extends OwlFrame implements ActionListener, ExposeActionListener
{
	private static final long serialVersionUID = 6155183986398097128L;

	private final String RUN_ACTION			= "RUN";
	private final String STOP_ACTION		= "ABORT";
	private final String LOAD_ACTION		= "LOAD";
	private final String SAVE_ACTION		= "SAVE";
	private final String DIR_BROWSE_ACTION	= "DIR BROWSE";

	private final String LOAD_PATH_PREF		= "LoadPath";
	private final String NUM_OF_PTS_PREF	= "NumOfPts";
	private final String START_TIME_PREF	= "StartTime";
	private final String END_TIME_PREF		= "EndTime";
	private final String TITLE_PREF			= "Title";
	private final String FLT_COLS_PREF		= "FltCols";
	private final String FLT_ROWS_PREF		= "FltRows";
	private final String FLT_START_ROW_PREF	= "FltStartRow";
	private final String FLT_END_ROW_PREF	= "FltEndRow";
	private final String FLT_START_COL_PREF	= "FltStartCol";
	private final String FLT_END_COL_PREF	= "FltEndCol";
	private final String FLT_FILE_PREF		= "FltFile";
	private final String DRK_COLS_PREF		= "DrkCols";
	private final String DRK_ROWS_PREF		= "DrkRows";
	private final String DRK_START_ROW_PREF	= "DrkStartRow";
	private final String DRK_END_ROW_PREF	= "DrkEndRow";
	private final String DRK_START_COL_PREF	= "DrkStartCol";
	private final String DRK_END_COL_PREF	= "DrkEndCol";
	private final String DRK_FILE_PREF		= "DrkFile";
	private final String IMG_DIR_PREF		= "ImageDir";
	private final String DEL_IMGS_PREF		= "DelImages";
	private final String DARK_ONLY_PREF		= "DarkOnly";
	private final String USE_DISK_IMGS_PREF	= "UseDiskImages";
	private final String DEINTERLACE_PREF	= "Deinterlace";

	private final String PLOT_TITLE			= "[ Plot Title ]";
	private final String START_TIME			= "[ Start Time ]";
	private final String END_TIME			= "[ End Time ]";
	private final String NUMBER_OF_POINTS	= "[ Number Of Points ]";
	private final String DARK_COLS			= "[ Dark Columns ]";
	private final String DARK_ROWS			= "[ Dark Rows ]";
	private final String FLAT_COLS			= "[ Flat Columns ]";
	private final String FLAT_ROWS			= "[ Flat Rows ]";
	private final String DARK_START_ROW		= "[ Dark Start Row ]";
	private final String DARK_END_ROW		= "[ Dark End Row ]";
	private final String DARK_START_COL		= "[ Dark Start Column ]";
	private final String DARK_END_COL		= "[ Dark End Column ]";
	private final String FLAT_START_ROW		= "[ Flat Start Row ]";
	private final String FLAT_END_ROW		= "[ Flat End Row ]";
	private final String FLAT_START_COL		= "[ Flat Start Column ]";
	private final String FLAT_END_COL		= "[ Flat End Column ]";
	private final String FILE_DIR			= "[ File Dir] ";
	private final String DARK_FILE			= "[ Dark File ]";
	private final String FLAT_FILE			= "[ Flat File ]";
	private final String DELETE_FILES		= "[ Delete Files ]";
	private final String DARK_ONLY			= "[ Dark Only ]";
	private final String USE_DISK_FILES		= "[ Use Disk Files ]";
	private final String DEINTERLACE		= "[ Deinterlace Index ]";

	private DirDialog     dirDialog;
	private JTextField    numberOfPointsTxtfld;
	private JTextField    startTimeTxtfld;
	private JTextField    endTimeTxtfld;
	private OwlTextField  titleTxtfld;
	private JTextField    fColsTxtfld;
	private JTextField    fRowsTxtfld;
	private JTextField    fStartRowTxtfld;
	private JTextField    fEndRowTxtfld;
	private JTextField    fStartColTxtfld;
	private JTextField    fEndColTxtfld;
	private OwlTextField  fFileTxtfld;
	private JTextField    dColsTxtfld;
	private JTextField    dRowsTxtfld;
	private JTextField    dStartRowTxtfld;
	private JTextField    dEndRowTxtfld;
	private JTextField    dStartColTxtfld;
	private JTextField    dEndColTxtfld;
	private OwlTextField  dFileTxtfld;
	private OwlTextField  imageDirTxtfld;
	private JCheckBox     deleteImagesChkbox;
	private JCheckBox     darkOnlyChkbox;
	private JCheckBox     useDiskImagesChkbox;
	private OwlBoldButton runButton;
	private JComboBox     deinterlaceComboBox;
	private Object[]      deinterlaceAlgorithms;
	private Thread        thread;
	private File          loadSavePath;
	private PTC           ptc;

	// +-----------------------------------------------------------------+
	// |  Constructor code                                               |
	// +-----------------------------------------------------------------+
	public PTCFrame()
	{
		super( "Photon Transfer Curve", true );

		super.setIconImage(
				Toolkit.getDefaultToolkit().createImage(
						MainApp.getBitmapPath() + "PTC2.gif" ) );

		dirDialog 	 = null;
		thread    	 = null;
		ptc       	 = new PTC( this );
		loadSavePath = new File( System.getProperty( "user.home" ) );

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable( false );

		runButton = super.createBoldToolbarButton( RUN_ACTION, Color.RED );
		runButton.setActionCommand( RUN_ACTION );
		toolbar.add( runButton );

		JButton loadButton = createNewToolbarButton( LOAD_ACTION );
		toolbar.add( loadButton );

		JButton saveButton = createNewToolbarButton( SAVE_ACTION );
		toolbar.add( saveButton );

		super.appendToolbar( toolbar, OwlFrame.CLOSE );

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout( new BoxLayout( centerPanel, BoxLayout.Y_AXIS ) );
		centerPanel.add( createOptionsPanel() );
		centerPanel.add( createDarkPanel() );
		centerPanel.add( createFlatPanel() );

		super.addComponent( toolbar, super.TOOLBAR_INDEX );
		super.addComponent( centerPanel, super.CENTER_CONTAINER_INDEX );
		pack();

		OwlUtilities.centerFrame( this );

		loadPreferences();
	}

	public int getIntegerValue( String text )
	{
		int iVal = 0;

		try
		{
			iVal = Integer.parseInt( text );
		}
		catch ( NumberFormatException nfe )
		{
			System.err.println( "Failed to parse value: \"" + text + "\"" );
			throw nfe;
		}

		return iVal;
	}

	@Override
	public void setExposeAction( byte action )
	{
		if ( action == ExposeActionListener.ABORT )
		{
			runButton.setText( STOP_ACTION );
			runButton.setActionCommand( STOP_ACTION );
			runButton.setColored();
		}
		else
		{
			runButton.setText( RUN_ACTION );
			runButton.setActionCommand( RUN_ACTION );
			runButton.setBlack();
		}
	}

	public void actionPerformed( ActionEvent event )
	{
		if ( event.getActionCommand().equals( RUN_ACTION ) )
		{
			thread = new Thread( ptc );

			if ( thread != null ) { thread.start(); }
		}

		else if ( event.getActionCommand().equals( STOP_ACTION ) )
		{
			if ( thread != null )
			{
				ptc.stop();
				thread = null;
			}
		}

		else if ( event.getActionCommand().equals( DIR_BROWSE_ACTION ) )
		{
			if ( dirDialog == null )
				dirDialog = new DirDialog( null );

			dirDialog.setPath( imageDirTxtfld.getText() );

			if ( dirDialog.showDialog() )
			{
				imageDirTxtfld.setText( dirDialog.getPath() );
			}
		}

		else if ( event.getActionCommand().equals( LOAD_ACTION ) )
		{
			try
			{
				String line = "";

				JFileChooser chooser = new JFileChooser( loadSavePath );
				int selection = chooser.showOpenDialog( this );
	
				if ( selection == JFileChooser.APPROVE_OPTION )
				{
					loadSavePath = chooser.getCurrentDirectory();
	
					RandomAccessFile inFile =
						new RandomAccessFile( chooser.getSelectedFile().getCanonicalPath(), "r" );
	
					while (  ( line = inFile.readLine() ) != null )
					{
						// Make sure a keyword exists.
						if ( line.indexOf( ":" ) < 0 ) continue;
	
						String keyword = line.substring( 0, line.indexOf( ":" ) );
				
						// Plot title textfield.
						if ( keyword.equals( PLOT_TITLE ) )
						{
							titleTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}

						else if ( keyword.equals( FILE_DIR ) )
						{
							imageDirTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}

						else if ( keyword.equals( START_TIME ) )
						{
							startTimeTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( END_TIME ) )
						{
							endTimeTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( NUMBER_OF_POINTS ) )
						{
							numberOfPointsTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( DARK_FILE ) )
						{
							dFileTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( DARK_COLS ) )
						{
							dColsTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( DARK_ROWS ) )
						{
							dRowsTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( DARK_START_ROW ) )
						{
							dStartRowTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( DARK_END_ROW ) )
						{
							dEndRowTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( DARK_START_COL ) )
						{
							dStartColTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( DARK_END_COL ) )
						{
							dEndColTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( FLAT_FILE ) )
						{
							fFileTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( FLAT_COLS ) )
						{
							fColsTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( FLAT_ROWS ) )
						{
							fRowsTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( FLAT_START_ROW ) )
						{
							fStartRowTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( FLAT_END_ROW ) )
						{
							fEndRowTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( FLAT_START_COL ) )
						{
							fStartColTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( FLAT_END_COL ) )
						{
							fEndColTxtfld.setText( line.substring( line.lastIndexOf( "\t" ) + 1 ) );
						}
	
						else if ( keyword.equals( DELETE_FILES ) )
						{
							deleteImagesChkbox.setSelected(
										new Boolean(
												line.substring(
														line.lastIndexOf( "\t" ) + 1 ) ).booleanValue() );
						}
	
						else if ( keyword.equals( DARK_ONLY ) )
						{
							darkOnlyChkbox.setSelected(
										new Boolean(
												line.substring(
														line.lastIndexOf( "\t" ) + 1 ) ).booleanValue() );
						}
	
						else if ( keyword.equals( USE_DISK_FILES ) )
						{
							useDiskImagesChkbox.setSelected(
										new Boolean(
												line.substring(
														line.lastIndexOf( "\t" ) + 1 ) ).booleanValue() );
						}
	
						else if ( keyword.equals( DEINTERLACE ) )
						{
							deinterlaceComboBox.setSelectedIndex(
										Integer.parseInt(
												line.substring( line.lastIndexOf( "\t" ) + 1 ) ) );
						}
					}
				}
			}
			catch ( Exception e )
			{
				System.err.println( "( PTCFrame ): Failed to load PTC setup!" +
									 e.toString() );
			}
		}

		else if ( event.getActionCommand().equals( SAVE_ACTION ) )
		{
			try
			{
				JFileChooser chooser = new JFileChooser( loadSavePath );
				int selection = chooser.showSaveDialog( this );
	
				if ( selection == JFileChooser.APPROVE_OPTION )
				{
					loadSavePath = chooser.getCurrentDirectory();
	
					DataOutputStream output =
						new DataOutputStream( new FileOutputStream( chooser.getSelectedFile().getCanonicalPath() ) );
	
					// Set the informational message
					output.writeBytes("**** NOTE: All labels and data values MUST be separated by a single tab. ****\n");
	
					// Save all the textfield info
					output.writeBytes( PLOT_TITLE + ":\t" + titleTxtfld.getText() + "\n" );
					output.writeBytes( FILE_DIR + ":\t" + imageDirTxtfld.getText() + "\n");
					output.writeBytes( START_TIME + ":\t" + startTimeTxtfld.getText() + "\n" );
					output.writeBytes( END_TIME + ":\t" + endTimeTxtfld.getText() + "\n" );
					output.writeBytes( NUMBER_OF_POINTS + ":\t" + numberOfPointsTxtfld.getText() + "\n" );
					output.writeBytes( DARK_FILE + ":\t" + dFileTxtfld.getText() + "\n" );
					output.writeBytes( DARK_COLS + ":\t" + dColsTxtfld.getText() + "\n" );
					output.writeBytes( DARK_ROWS + ":\t" + dRowsTxtfld.getText() + "\n" );
					output.writeBytes( DARK_START_ROW + ":\t" + dStartRowTxtfld.getText() + "\n" );
					output.writeBytes( DARK_END_ROW + ":\t" + dEndRowTxtfld.getText() + "\n" );
					output.writeBytes( DARK_START_COL + ":\t" + dStartColTxtfld.getText() + "\n" );
					output.writeBytes( DARK_END_COL + ":\t" + dEndColTxtfld.getText() + "\n" );
					output.writeBytes( FLAT_FILE + ":\t" + fFileTxtfld.getText() + "\n" );
					output.writeBytes( FLAT_COLS + ":\t" + fColsTxtfld.getText() + "\n" );
					output.writeBytes( FLAT_ROWS + ":\t" + fRowsTxtfld.getText() + "\n" );
					output.writeBytes( FLAT_START_ROW + ":\t" + fStartRowTxtfld.getText() + "\n" );
					output.writeBytes( FLAT_END_ROW + ":\t" + fEndRowTxtfld.getText() + "\n" );
					output.writeBytes( FLAT_START_COL + ":\t" + fStartColTxtfld.getText() + "\n" );
					output.writeBytes( FLAT_END_COL + ":\t" + fEndColTxtfld.getText() + "\n" );
					output.writeBytes( DELETE_FILES + ":\t" + String.valueOf( deleteImagesChkbox.isSelected() ) + "\n" );
					output.writeBytes( DARK_ONLY + ":\t" + String.valueOf( darkOnlyChkbox.isSelected() ) + "\n" );
					output.writeBytes( USE_DISK_FILES + ":\t" + String.valueOf( useDiskImagesChkbox.isSelected() ) + "\n" );
					output.writeBytes( DEINTERLACE + ":\t" + String.valueOf( deinterlaceComboBox.getSelectedIndex() ) + "\n" );
	
					output.close();
					MainApp.info( "PTC parameters saved" );	
				}
			}
			catch ( Exception e )
			{
				MainApp.error( "( PTCFrame ): Failed to save PTC setup!" + e.toString() );
			}
		}

		else
			super.actionPerformed( event );
	}

	public void setNumberOfPoints( String str )	{ numberOfPointsTxtfld.setText( str ); }
	public void setStartTime( String str )		{ startTimeTxtfld.setText( str ); }
	public void setEndTime( String str )		{ endTimeTxtfld.setText( str ); }
	public void setTitle( String str )			{ titleTxtfld.setText( str ); }
	public void setFlatCols( String str )		{ fColsTxtfld.setText( str ); }
	public void setFlatRows( String str )		{ fRowsTxtfld.setText( str ); }
	public void setFlatStartRow( String str )	{ fStartRowTxtfld.setText( str ); }
	public void setFlatEndRow( String str )		{ fEndRowTxtfld.setText( str ); }
	public void setFlatStartCol( String str )	{ fStartColTxtfld.setText( str ); }
	public void setFlatEndCol( String str )		{ fEndColTxtfld.setText( str ); }
	public void setFlatFile( String str )		{ fFileTxtfld.setText( str ); }
	public void setDarkCols( String str )		{ dColsTxtfld.setText( str ); }
	public void setDarkRows( String str )		{ dRowsTxtfld.setText( str ); }
	public void setDarkStartRow( String str )	{ dStartRowTxtfld.setText( str ); }
	public void setDarkEndRow( String str )		{ dEndRowTxtfld.setText( str ); }
	public void setDarkStartCol( String str )	{ dStartColTxtfld.setText( str ); }
	public void setDarkEndCol( String str )		{ dEndColTxtfld.setText( str ); }
	public void setDarkFile( String str )		{ dFileTxtfld.setText( str ); }
	public void setImageDir( String str )		{ imageDirTxtfld.setText( str ); }
	public void setDeleteImages( String str )	{ deleteImagesChkbox.setSelected( Boolean.valueOf( str ).booleanValue() ); }
	public void setDarkOnly( String str )		{ darkOnlyChkbox.setSelected( Boolean.valueOf( str ).booleanValue() ); }
	public void setUseDiskImages( String str )  { useDiskImagesChkbox.setSelected( Boolean.valueOf( str ).booleanValue() ); }
	public void setDeinterlaceAlgorithm( String str ) { deinterlaceComboBox.setSelectedIndex( Integer.parseInt( str ) ); }

	public int getNumberOfPoints()		{ return getIntegerValue( numberOfPointsTxtfld.getText() ); }
	public int getStartTime()			{ return getIntegerValue( startTimeTxtfld.getText() ); }
	public int getEndTime()				{ return getIntegerValue( endTimeTxtfld.getText() ); }
	public String getTitle()			{ return titleTxtfld.getText(); }
	public int getFlatCols()			{ return getIntegerValue( fColsTxtfld.getText() ); }
	public int getFlatRows()			{ return getIntegerValue( fRowsTxtfld.getText() ); }
	public int getFlatStartRow()		{ return getIntegerValue( fStartRowTxtfld.getText() ); }
	public int getFlatEndRow()			{ return getIntegerValue( fEndRowTxtfld.getText() ); }
	public int getFlatStartCol()		{ return getIntegerValue( fStartColTxtfld.getText() ); }
	public int getFlatEndCol()			{ return getIntegerValue( fEndColTxtfld.getText() ); }
	public String getFlatFile()			{ return fFileTxtfld.getText(); }
	public int getDarkCols()			{ return getIntegerValue( dColsTxtfld.getText() ); }
	public int getDarkRows()			{ return getIntegerValue( dRowsTxtfld.getText() ); }
	public int getDarkStartRow()		{ return getIntegerValue( dStartRowTxtfld.getText() ); }
	public int getDarkEndRow()			{ return getIntegerValue( dEndRowTxtfld.getText() ); }
	public int getDarkStartCol()		{ return getIntegerValue( dStartColTxtfld.getText() ); }
	public int getDarkEndCol()			{ return getIntegerValue( dEndColTxtfld.getText() ); }
	public String getDarkFile()			{ return dFileTxtfld.getText(); }
	public String getImageDir()			{ return imageDirTxtfld.getText() + System.getProperty( "file.separator" ); }
	public boolean getDeleteImages()	{ return deleteImagesChkbox.isSelected(); }
	public boolean getDarkOnly()		{ return darkOnlyChkbox.isSelected(); }
	public boolean getUseDiskImages()  	{ return useDiskImagesChkbox.isSelected(); }
	public int getDeinterlaceAlgorithm(){ return deinterlaceComboBox.getSelectedIndex(); }
	public String getDeinterlaceDescription() { return ( String )deinterlaceAlgorithms[ deinterlaceComboBox.getSelectedIndex() ]; }

	protected void loadPreferences()
	{
		super.loadPreferences();

		if ( loadSavePath != null )
		{
			loadSavePath = new File(
					MainApp.getPreferences().get( super.getTitle() +
												  LOAD_PATH_PREF,
												  System.getProperty( "user.home" ) ) );

			numberOfPointsTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  NUM_OF_PTS_PREF,
												  "" ) );

			startTimeTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  START_TIME_PREF,
												  "" ) );

			endTimeTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  END_TIME_PREF,
												  "" ) );

			titleTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  TITLE_PREF,
												  "" ) );

			fColsTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  FLT_COLS_PREF,
												  "" ) );

			fRowsTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  FLT_ROWS_PREF,
												  "" ) );

			fStartRowTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  FLT_START_ROW_PREF,
												  "" ) );

			fEndRowTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  FLT_END_ROW_PREF,
												  "" ) );

			fStartColTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  FLT_START_COL_PREF,
												  "" ) );

			fEndColTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  FLT_END_COL_PREF,
												  "" ) );

			fFileTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  FLT_FILE_PREF,
												  "" ) );

			dColsTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  DRK_COLS_PREF,
												  "" ) );

			dRowsTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  DRK_ROWS_PREF,
												  "" ) );

			dStartRowTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  DRK_START_ROW_PREF,
												  "" ) );

			dEndRowTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  DRK_END_ROW_PREF,
												  "" ) );

			dStartColTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  DRK_START_COL_PREF,
												  "" ) );

			dEndColTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  DRK_END_COL_PREF,
												  "" ) );

			dFileTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  DRK_FILE_PREF,
												  "" ) );

			imageDirTxtfld.setText(
					MainApp.getPreferences().get( super.getTitle() +
												  IMG_DIR_PREF,
												  "" ) );

			deleteImagesChkbox.setSelected( 
							MainApp.getPreferences().getBoolean( super.getTitle() +
																 DEL_IMGS_PREF,
																 false ) );

			darkOnlyChkbox.setSelected(
							MainApp.getPreferences().getBoolean( super.getTitle() +
																 DARK_ONLY_PREF,
												 				 false ) );

			useDiskImagesChkbox.setSelected(
							MainApp.getPreferences().getBoolean( super.getTitle() +
																 USE_DISK_IMGS_PREF,
												 				 false ) );

			deinterlaceComboBox.setSelectedIndex(
							MainApp.getPreferences().getInt( super.getTitle() +
															 DEINTERLACE_PREF,
											 				 0 ) );
		}
	}

	protected void savePreferences()
	{
		super.savePreferences();

		if ( loadSavePath != null )
		{
			try
			{
				MainApp.getPreferences().put( super.getTitle() + LOAD_PATH_PREF, loadSavePath.getPath() );

				MainApp.getPreferences().put( super.getTitle() + NUM_OF_PTS_PREF, numberOfPointsTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + START_TIME_PREF, startTimeTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + END_TIME_PREF, endTimeTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + TITLE_PREF, titleTxtfld.getText() );

				MainApp.getPreferences().put( super.getTitle() + FLT_COLS_PREF, fColsTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + FLT_ROWS_PREF, fRowsTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + FLT_START_ROW_PREF, fStartRowTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + FLT_END_ROW_PREF, fEndRowTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + FLT_START_COL_PREF, fStartColTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + FLT_END_COL_PREF, fEndColTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + FLT_FILE_PREF, fFileTxtfld.getText() );

				MainApp.getPreferences().put( super.getTitle() + DRK_COLS_PREF, dColsTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + DRK_ROWS_PREF, dRowsTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + DRK_START_ROW_PREF, dStartRowTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + DRK_END_ROW_PREF, dEndRowTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + DRK_START_COL_PREF, dStartColTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + DRK_END_COL_PREF, dEndColTxtfld.getText() );
				MainApp.getPreferences().put( super.getTitle() + DRK_FILE_PREF, dFileTxtfld.getText() );

				MainApp.getPreferences().put( super.getTitle() + IMG_DIR_PREF, imageDirTxtfld.getText() );

				MainApp.getPreferences().putBoolean( super.getTitle() + DEL_IMGS_PREF,
													 deleteImagesChkbox.isSelected() );

				MainApp.getPreferences().putBoolean( super.getTitle() + DARK_ONLY_PREF,
													 darkOnlyChkbox.isSelected() );

				MainApp.getPreferences().putBoolean( super.getTitle() + USE_DISK_IMGS_PREF,
													 useDiskImagesChkbox.isSelected() );

				MainApp.getPreferences().putInt( super.getTitle() + DEINTERLACE_PREF,
												 deinterlaceComboBox.getSelectedIndex() );
			}
			catch ( Exception e ) {}
		}
	}

	private JPanel createOptionsPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout( gbl );

		JLabel startTimeLabel  = new JLabel( "Start Time (ms): " );
		startTimeTxtfld = new JTextField( "100", 5 );

		JLabel endTimeLabel  = new JLabel( "End Time (ms): " );
		endTimeTxtfld = new JTextField( "1000", 5 );

		JLabel numberOfPointsLabel  = new JLabel( "Number Of Points: " );
		numberOfPointsTxtfld = new JTextField( "5", 5 );

		JLabel titleLabel  = new JLabel( "Plot Title: " );
		titleTxtfld = new OwlTextField( "Photon Transfer Curve", 20 );

		deleteImagesChkbox  = new JCheckBox( "Delete Images" );
		darkOnlyChkbox      = new JCheckBox( "Dark Image Only" );
		useDiskImagesChkbox = new JCheckBox( "Use Existing Images" );

		JLabel deinterlaceLabel = new JLabel( "Deinterlace: " );
		deinterlaceAlgorithms = new Object[ 7 ];
		deinterlaceAlgorithms[ 0 ] = new String( "None" );
		deinterlaceAlgorithms[ 1 ] = new String( "Parallel" );
		deinterlaceAlgorithms[ 2 ] = new String( "Serial" );
		deinterlaceAlgorithms[ 3 ] = new String( "CCD Quad" );
		deinterlaceAlgorithms[ 4 ] = new String( "CCD 4x Serial" );
		deinterlaceAlgorithms[ 5 ] = new String( "IR Quad" );
		deinterlaceAlgorithms[ 6 ] = new String( "IR Quad CDS" );

		deinterlaceComboBox = new JComboBox( deinterlaceAlgorithms );

		JLabel imageDirLabel = new JLabel( "Image Directrory: " );
		imageDirTxtfld = new OwlTextField( System.getProperty( "user.home" ), 20 );

		JButton dirBrowseButton = OwlButtonFactory.createIconButton( "folder.gif",
																	 "Browse",
																	  DIR_BROWSE_ACTION,
																	  this );

		addComponent( panel, startTimeLabel,       5, 8, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0, 1, 1 );
		addComponent( panel, startTimeTxtfld,      5, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 1, 1, 1 );
		addComponent( panel, deleteImagesChkbox,   5, 3, 0, 8, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 2, 1, 1 );

		addComponent( panel, endTimeLabel,         0, 8, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 0, 1, 1 );
		addComponent( panel, endTimeTxtfld,        0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 1, 1, 1 );
		addComponent( panel, darkOnlyChkbox,       0, 3, 0, 8, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 2, 1, 1 );

		addComponent( panel, numberOfPointsLabel,  0, 8, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 2, 0, 1, 1 );
		addComponent( panel, numberOfPointsTxtfld, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 2, 1, 1, 1 );
		addComponent( panel, useDiskImagesChkbox,  0, 3, 0, 8, GridBagConstraints.NONE, GridBagConstraints.WEST, 2, 2, 1, 2 );

		addComponent( panel, deinterlaceLabel,     0, 8, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 3, 0, 1, 1 );
		addComponent( panel, deinterlaceComboBox,  0, 0, 0, 8, GridBagConstraints.NONE, GridBagConstraints.WEST, 3, 1, 1, 3 );

		addComponent( panel, titleLabel,           0, 8, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 4, 0, 1, 1 );
		addComponent( panel, titleTxtfld,          0, 0, 0, 8, GridBagConstraints.NONE, GridBagConstraints.WEST, 4, 1, 1, 3 );

		addComponent( panel, imageDirLabel,        0, 8, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 5, 0, 1, 1 );
		addComponent( panel, imageDirTxtfld,       0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 5, 1, 1, 2 );
		addComponent( panel, dirBrowseButton,      0, 0, 0, 8, GridBagConstraints.NONE, GridBagConstraints.WEST, 5, 3, 1, 1 );

		return panel;
	}

	private JPanel createFlatPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout( gbl );

		JPanel coordPanel = new JPanel();
		coordPanel.setLayout( gbl );

		// Set the coordinates panel background color.
		coordPanel.setBackground( Color.WHITE );

		// Create the flat panel border.
		TitledBorder border = new TitledBorder( new EtchedBorder(), "Flat Settings" );
		border.setTitleFont( new Font( panel.getFont().getFontName(), Font.BOLD, 14 ) );
		border.setTitleColor( Color.BLACK );
		panel.setBorder( border );

		JLabel startLabel = new JLabel( "START" );
		startLabel.setFont( new Font( startLabel.getFont().getFontName(), Font.BOLD, startLabel.getFont().getSize() ) );

		JLabel endLabel = new JLabel( "END" );
		endLabel.setFont( new Font( endLabel.getFont().getFontName(), Font.BOLD, endLabel.getFont().getSize() ) );

		JLabel fillLabel = new JLabel( " " );

		JLabel fFileLabel = new JLabel( "File Prefix: " );
		fFileTxtfld = new OwlTextField( "PTCFlat", 20 );

		JLabel fRowsLabel = new JLabel( "Flat Rows: " );
		fRowsTxtfld = new JTextField( "", 5 );

		JLabel fColsLabel = new JLabel( "Flat Cols: " );
		fColsTxtfld = new JTextField( "", 5 );

		JLabel fStartRowLabel = new JLabel( "Row: " );
		fStartRowTxtfld = new JTextField( "", 5 );

		JLabel fEndRowLabel = new JLabel( "Row: " );
		fEndRowTxtfld = new JTextField( "", 5 );

		JLabel fStartColLabel = new JLabel( "Col: " );
		fStartColTxtfld = new JTextField( "", 5 );

		JLabel fEndColLabel = new JLabel( "Col: " );
		fEndColTxtfld = new JTextField( "", 5 );

		// Add the coordinate coordinates to the coordinates panel
		addComponent( coordPanel, startLabel,     0, 0, 0, 0,   GridBagConstraints.NONE, GridBagConstraints.CENTER, 0, 0, 1, 2 );
		addComponent( coordPanel, fillLabel,      0, 40, 0, 40, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 2, 1, 1 );
		addComponent( coordPanel, endLabel,       0, 0, 0, 0,   GridBagConstraints.NONE, GridBagConstraints.CENTER, 0, 3, 1, 2 );

		addComponent( coordPanel, fStartRowLabel,  0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 0, 1, 1 );
		addComponent( coordPanel, fStartRowTxtfld, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 1, 1, 1 );
		addComponent( coordPanel, fEndRowLabel,    0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 3, 1, 1 );
		addComponent( coordPanel, fEndRowTxtfld,   0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 4, 1, 1 );

		addComponent( coordPanel, fStartColLabel,  0, 0, 5, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   2, 0, 1, 1 );
		addComponent( coordPanel, fStartColTxtfld, 0, 0, 5, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   2, 1, 1, 1 );
		addComponent( coordPanel, fEndColLabel,    0, 0, 5, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   2, 3, 1, 1 );
		addComponent( coordPanel, fEndColTxtfld,   0, 0, 5, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   2, 4, 1, 1 );

		// Add the components to the panel
		addComponent( panel, fFileLabel,  0, 0, 3, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   0, 0, 1, 1 );
		addComponent( panel, fFileTxtfld, 0, 0, 3, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   0, 1, 1, 3 );
		addComponent( panel, fRowsLabel,  0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.EAST,   1, 0, 1, 1 );
		addComponent( panel, fRowsTxtfld, 0, 0, 0, 5, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 1, 1, 1 );
		addComponent( panel, fColsLabel,  0, 5, 0, 0, GridBagConstraints.NONE, GridBagConstraints.EAST,   1, 2, 1, 1 );
		addComponent( panel, fColsTxtfld, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 3, 1, 1 );
		addComponent( panel, coordPanel,  5, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.CENTER, 2, 0, 1, 4 );

		return panel;
	}

	private JPanel createDarkPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout( gbl );

		JPanel coordPanel = new JPanel();
		coordPanel.setLayout( gbl );

		// Set the coordinates panel background color.
		coordPanel.setBackground( Color.BLACK );

		// Create the flat panel border.
		TitledBorder border = new TitledBorder( new EtchedBorder(), "Dark Settings" );
		border.setTitleFont( new Font( panel.getFont().getFontName(), Font.BOLD, 14 ) );
		border.setTitleColor( Color.BLACK );
		panel.setBorder( border );

		JLabel startLabel = new JLabel( "START" );
		startLabel.setForeground( Color.WHITE );
		startLabel.setFont( new Font( startLabel.getFont().getFontName(), Font.BOLD, startLabel.getFont().getSize() ) );

		JLabel endLabel = new JLabel( "END" );
		endLabel.setForeground( Color.WHITE );
		endLabel.setFont( new Font( endLabel.getFont().getFontName(), Font.BOLD, endLabel.getFont().getSize() ) );

		JLabel fillLabel = new JLabel( " " );

		JLabel dFileLabel = new JLabel( "File Prefix: " );
		dFileTxtfld = new OwlTextField( "PTCDark", 20 );

		JLabel dRowsLabel = new JLabel( "Dark Rows: " );
		dRowsTxtfld = new JTextField( "", 5 );

		JLabel dColsLabel = new JLabel( "Dark Cols: " );
		dColsTxtfld = new JTextField( "", 5 );

		JLabel dStartRowLabel = new JLabel( "Row: " );
		dStartRowLabel.setForeground( Color.WHITE );
		dStartRowTxtfld = new JTextField( "", 5 );

		JLabel dEndRowLabel = new JLabel( "Row: " );
		dEndRowLabel.setForeground( Color.WHITE );
		dEndRowTxtfld = new JTextField( "", 5 );

		JLabel dStartColLabel = new JLabel( "Col: " );
		dStartColLabel.setForeground( Color.WHITE );
		dStartColTxtfld = new JTextField( "", 5 );

		JLabel dEndColLabel = new JLabel( "Col: " );
		dEndColLabel.setForeground( Color.WHITE );
		dEndColTxtfld = new JTextField( "", 5 );

		// Add the coordinate coordinates to the coordinates panel
		addComponent( coordPanel, startLabel,     0, 0, 0, 0,   GridBagConstraints.NONE, GridBagConstraints.CENTER, 0, 0, 1, 2 );
		addComponent( coordPanel, fillLabel,      0, 40, 0, 40, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 2, 1, 1 );
		addComponent( coordPanel, endLabel,       0, 0, 0, 0,   GridBagConstraints.NONE, GridBagConstraints.CENTER, 0, 3, 1, 2 );

		addComponent( coordPanel, dStartRowLabel,  0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 0, 1, 1 );
		addComponent( coordPanel, dStartRowTxtfld, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 1, 1, 1 );
		addComponent( coordPanel, dEndRowLabel,    0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 3, 1, 1 );
		addComponent( coordPanel, dEndRowTxtfld,   0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 4, 1, 1 );

		addComponent( coordPanel, dStartColLabel,  0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   2, 0, 1, 1 );
		addComponent( coordPanel, dStartColTxtfld, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   2, 1, 1, 1 );
		addComponent( coordPanel, dEndColLabel,    0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   2, 3, 1, 1 );
		addComponent( coordPanel, dEndColTxtfld,   0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   2, 4, 1, 1 );

		// Add the components to the panel
		addComponent( panel, dFileLabel,  0, 0, 3, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   0, 0, 1, 1 );
		addComponent( panel, dFileTxtfld, 0, 0, 3, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   0, 1, 1, 3 );
		addComponent( panel, dRowsLabel,  0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.EAST,   1, 0, 1, 1 );
		addComponent( panel, dRowsTxtfld, 0, 0, 0, 5, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 1, 1, 1 );
		addComponent( panel, dColsLabel,  0, 5, 0, 0, GridBagConstraints.NONE, GridBagConstraints.EAST,   1, 2, 1, 1 );
		addComponent( panel, dColsTxtfld, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST,   1, 3, 1, 1 );
		addComponent( panel, coordPanel,  5, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.CENTER, 2, 0, 1, 4 );

		return panel;
	}
}
