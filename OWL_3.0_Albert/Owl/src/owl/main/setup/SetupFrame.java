package owl.main.setup;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import owl.cameraAPI.CameraAPI;
import owl.gui.utils.OwlButtonFactory;
import owl.main.MainApp;
import owl.main.owltypes.OwlBoldButton;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlIconedFileChooser;
import owl.main.owltypes.OwlNumberField;
import owl.main.owltypes.OwlTextField;



public class SetupFrame extends OwlFrame implements ActionListener, ItemListener
{
	private static final long serialVersionUID = 2938976238387340454L;

	//--------------------------------------------------------------------------
    //   Constants:
    //--------------------------------------------------------------------------
	public final String APPLY_ACTION			=	"APPLY";
	public final String ABORT_ACTION			=	"ABORT";
	public final String LOAD_ACTION				=	"LOAD";
	public final String SAVE_ACTION				=	"SAVE";
	public final String TOGGLE_ACTION			=	"TOGGLE";
	public final String TIM_BROWSE_ACTION		=	"TIM BROWSE";
	public final String UTL_BROWSE_ACTION		=	"UTL BROWSE";
	public final String TIM_EDIT_ACTION			=	"TIM EDIT";
	public final String UTL_EDIT_ACTION			=	"UTL EDIT";

	private final String RESET_CONTROLLER		= "Reset Controller";
	private final String HARDWARE_TEST			= "Hardware Test";
	private final String PCI_HARDWARE_TEST		= "PCI(e) Hardware Test";
	private final String TIM_HARDWARE_TEST		= "Timing Hardware Test";
	private final String UTIL_HARDWARE_TEST		= "Utility Hardware Test";
	private final String TIM_DOWNLOAD			= "Timing Download";
	private final String UTIL_DOWNLOAD			= "Utility Download";
	private final String POWER_ON				= "Power On";
	private final String IMAGE_DIMENSIONS		= "Image Dimensions";
	private final String NUMBER_OF_PCI_TESTS	= "Number Of PCI(e) Tests";
	private final String NUMBER_OF_TIM_TESTS	= "Number Of Timing Tests";
	private final String NUMBER_OF_UTIL_TESTS	= "Number Of Utility Tests";
	private final String TIM_FILENAME			= "Timing Filename";
	private final String UTIL_FILENAME			= "Utility Filename";
	private final String IMAGE_ROWS				= "Image Rows";
	private final String IMAGE_COLS				= "Image Cols";

	private final String PCI_BRD_STR			= "PCI(e)";
	private final String TIM_BRD_STR			= "TIM";
	private final String UTL_BRD_STR			= "UTL";

    //--------------------------------------------------------------------------
    //   Private Variables:
    //--------------------------------------------------------------------------
	private OwlBoldButton	applyButton;
	private JCheckBox		resetControllerChkbox;
	private JCheckBox		hardwareTestChkbox;
	private JCheckBox		pciHWTChkbox;
	private JCheckBox		timHWTChkbox;
	private JCheckBox		utilHWTChkbox;
	private JCheckBox		timLoadChkbox;
	private JCheckBox		utilLoadChkbox;
	private JCheckBox		powerOnChkbox;
	private JCheckBox		imageSizeChkbox;
	private OwlTextField	timLoadTxtfld;
	private OwlTextField	utilLoadTxtfld;
	private OwlNumberField	pciHWTTxtfld;
	private OwlNumberField	timHWTTxtfld;
	private OwlNumberField	utilHWTTxtfld;
	private OwlNumberField	rowTxtfld;
	private OwlNumberField	colTxtfld;
	private File			file;
	private SetupRunnable	thread;
    private boolean			toggleOn;

	private static Vector<SetupListener> setupListeners = null;


    //--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
	public SetupFrame()
	{
		super( "Setup", true );

		super.addComponent( createToolBar(), super.TOOLBAR_INDEX );
		super.addComponent( createComponents(), super.CENTER_CONTAINER_INDEX );

		pack();

		file     = new File( "." );
		thread   = new SetupRunnable( this );
		toggleOn = true;

		loadPreferences();
	}

    //--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------

	public void actionPerformed( ActionEvent e )
	{
		if ( e.getActionCommand().equals( APPLY_ACTION ) )
		{
			new Thread( thread ).start();
		}

		else if ( e.getActionCommand().equals( ABORT_ACTION ) )
		{
			thread.stop();
		}

		else if ( e.getActionCommand().equals( TIM_BROWSE_ACTION ) ||
				  e.getActionCommand().equals( UTL_BROWSE_ACTION ) )
		{
			String[] ext = { "lod" };
			String[] desc = { "DSP Load File ( *.lod )" };
			ImageIcon[] icons = { new ImageIcon( MainApp.getBitmapPath() + "LOD2.gif" ) };
			OwlIconedFileChooser lodfc = new OwlIconedFileChooser( file, ext, desc, icons );

			if ( lodfc.openDialog() )
			{
				file = lodfc.getSelectedFile();

				if ( e.getActionCommand().equals( TIM_BROWSE_ACTION ) )
				{
					timLoadTxtfld.setText( file.getPath() );
				}

				else if ( e.getActionCommand().equals( UTL_BROWSE_ACTION ) )
				{
					utilLoadTxtfld.setText( file.getPath() );
				}
			}
		}

		else if ( e.getActionCommand().equals( TIM_EDIT_ACTION ) ||
				  e.getActionCommand().equals( UTL_EDIT_ACTION ) )
		{
			File file = null;

			if ( e.getActionCommand().equals( TIM_EDIT_ACTION ) )
			{
				file = new File( timLoadTxtfld.getText() );
			}

			else if ( e.getActionCommand().equals( UTL_EDIT_ACTION ) )
			{
				file = new File( utilLoadTxtfld.getText() );
			}

			if ( file != null && file.isFile() )
			{
				MainApp.launchTextEditor( file.getPath() );
			}
			else
			{
				MainApp.launchTextEditor( "" );
				MainApp.warn( "File doesn't exist! Opening empty text editor." );
			}
		}

		else if ( e.getActionCommand().equals( LOAD_ACTION ) )
		{
			try
			{
				loadSetupFile();
			}
			catch( FileNotFoundException fnfe )
			{
				MainApp.error( "Failed to open selected file" );
			}
			catch ( IOException ioe )
			{
				MainApp.error( ioe );
			}
		}

		else if ( e.getActionCommand().equals( SAVE_ACTION ) )
		{
			try
			{
				String[] ext = { "setup", "bsh" };
				String[] desc = { "Owl Setup File ( *.setup )", "BeanShell Script ( *.bsh )" };

				ImageIcon[] icons = { new ImageIcon( MainApp.getBitmapPath() + "setup.gif" ),
									  new ImageIcon( MainApp.getBitmapPath() + "TinyBean.gif" ) };

				OwlIconedFileChooser sfc = new OwlIconedFileChooser( file, ext, desc, icons );

				// If the Open button is pressed.
				if ( sfc.saveDialog() )
				{
					// Get the selected file.
					file = sfc.getSelectedFile();

					if ( file.getName().contains( ".setup" ) )
					{
						writeSetupFile();
					}
					else
					{
						writeScriptFile();
					}
				}
			}
			catch( FileNotFoundException fnfe )
			{
				MainApp.error( "Failed to save selected file" );
			}
			catch ( IOException ioe )
			{
				MainApp.error( ioe );
			}
		}

		else if ( e.getActionCommand().equals( TOGGLE_ACTION ) )
		{
			JPanel panel = ( JPanel )getContentPane().getComponent( super.CENTER_CONTAINER_INDEX );
			Component[] comps = panel.getComponents();

			for ( int i=0; i<comps.length; i++ )
			{
				if ( JCheckBox.class.isInstance( comps[ i ] ) )
				{
					// Ignore hardware test checkboxes
					if ( !( ( JCheckBox )comps[i]).getText().equals( PCI_BRD_STR ) &&
						 !( ( JCheckBox )comps[i]).getText().equals( TIM_BRD_STR ) &&
						 !( ( JCheckBox )comps[i]).getText().equals( UTL_BRD_STR ) )
					{
						( ( JCheckBox )comps[i]).setSelected( toggleOn );
					}
				}
			}
			toggleOn = !toggleOn;
		}

		else
		{
			super.actionPerformed( e );
		}
	}

	@Override
	public void itemStateChanged( ItemEvent arg0 )
	{
		if ( arg0.getSource() instanceof javax.swing.JCheckBox )
		{
			JCheckBox src = ( JCheckBox )arg0.getSource();

			if ( src.getText().equals( PCI_BRD_STR ) && src.isSelected() )
			{
				if ( pciHWTTxtfld.getText().isEmpty() )
				{
					pciHWTTxtfld.setText( "1234" );
				}
			}

			else if ( src.getText().equals( TIM_BRD_STR ) && src.isSelected() )
			{
				if ( timHWTTxtfld.getText().isEmpty() )
				{
					timHWTTxtfld.setText( "1234" );
				}
			}

			else if ( src.getText().equals( UTL_BRD_STR ) && src.isSelected() )
			{
				if ( utilHWTTxtfld.getText().isEmpty() )
				{
					utilHWTTxtfld.setText( "1234" );
				}
			}
		}
	}

	public void setApplyAction( String action )
	{
		applyButton.setText( action );
		applyButton.setActionCommand( action );

		if ( action == ABORT_ACTION ) { applyButton.setColored(); }
		else { applyButton.setBlack(); }
	}

	public boolean doLoadTIM()         { return timLoadChkbox.isSelected();         }
	public boolean doLoadUTL()         { return utilLoadChkbox.isSelected();        }
	public boolean doPCIHWTest()       { return pciHWTChkbox.isSelected();          }
	public boolean doTIMHWTest()       { return timHWTChkbox.isSelected();          }
	public boolean doUTLHWTest()       { return utilHWTChkbox.isSelected();         }
	public boolean doHardwareTest()    { return hardwareTestChkbox.isSelected();    }
	public boolean doResetController() { return resetControllerChkbox.isSelected(); }
	public boolean doPowerOn()		   { return powerOnChkbox.isSelected();			}
	public boolean doImageSize()	   { return imageSizeChkbox.isSelected();		}

	public String getTIMLoadFile() { return timLoadTxtfld.getText();  }
	public String getUTLLoadFile() { return utilLoadTxtfld.getText(); }

	public int getPCIHWTCount() throws Exception { return getIntegerField( pciHWTTxtfld.getText() );  }
	public int getTIMHWTCount() throws Exception { return getIntegerField( timHWTTxtfld.getText() );  }
	public int getUTLHWTCount() throws Exception { return getIntegerField( utilHWTTxtfld.getText() ); }
	public int getRowSize()		throws Exception { return getIntegerField( rowTxtfld.getText() );	  }
	public int getColSize()		throws Exception { return getIntegerField( colTxtfld.getText() );	  }

	public static void addSetupListener( SetupListener listener )
	{
		if ( setupListeners == null )
		{
			setupListeners = new Vector<SetupListener>( 0 );
		}

		if ( setupListeners != null )
		{
			setupListeners.add( listener );
		}
	}

	public static void callSetupListeners( SetupFrame setupFrame )
	{
		if ( setupListeners != null )
		{
			boolean bSetupOk = true;

			try
			{
				CameraAPI.GetCCParams();
			}
			catch ( Exception e ) { bSetupOk = false; }

			for ( int i=0; i<setupListeners.size(); i++ )
			{
				SetupEvent se =
						new SetupEvent( setupFrame,
										setupFrame.doPowerOn(),
										bSetupOk );

				setupListeners.get( i ).setupChanged( se );
			}
		}
	}

	//--------------------------------------------------------------------------
    //   Protected Methods:
    //--------------------------------------------------------------------------

	protected JToolBar createToolBar()
	{
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable( false );

		applyButton = super.createBoldToolbarButton( APPLY_ACTION, Color.RED );
		toolbar.add( applyButton );

		JButton loadButton = super.createNewToolbarButton( LOAD_ACTION );
		toolbar.add( loadButton );

		JButton saveButton = super.createNewToolbarButton( SAVE_ACTION );
		toolbar.add( saveButton );

		JButton clearButton = super.createNewToolbarButton( TOGGLE_ACTION );
		toolbar.add( clearButton );

		super.appendToolbar( toolbar );

		return toolbar;
	}

	protected JPanel createComponents()
	{
		JPanel panel = new JPanel();
		panel.setLayout( gbl );

		resetControllerChkbox = new JCheckBox( "Reset Controller" );
		hardwareTestChkbox = new JCheckBox( "Hardware Test" );

		pciHWTChkbox  = new JCheckBox( PCI_BRD_STR );
		pciHWTChkbox.addItemListener( this );
		pciHWTTxtfld  = new OwlNumberField( 4 );

		timHWTChkbox  = new JCheckBox( TIM_BRD_STR );
		timHWTChkbox.addItemListener( this );
		timHWTTxtfld  = new OwlNumberField( 4 );

		utilHWTChkbox = new JCheckBox( UTL_BRD_STR );
		utilHWTChkbox.addItemListener( this );
		utilHWTTxtfld = new OwlNumberField( 4 );

		timLoadChkbox = new JCheckBox( "TIM Download" );
		timLoadTxtfld = new OwlTextField( 33 );

		JButton timBrowseButton = OwlButtonFactory.createIconButton( "folder.gif",
																	 "Browse",
																	  TIM_BROWSE_ACTION,
																	  this );

		JButton timEditButton = OwlButtonFactory.createIconButton( "edit.gif",
																   "Edit File",
																    TIM_EDIT_ACTION,
																    this );

		utilLoadChkbox = new JCheckBox( "UTIL Download" );
		utilLoadTxtfld = new OwlTextField( 33 );

		JButton utilBrowseButton = OwlButtonFactory.createIconButton( "folder.gif",
																	  "Browse",
																	   UTL_BROWSE_ACTION,
																	   this );

		JButton utilEditButton = OwlButtonFactory.createIconButton( "edit.gif",
																	"Edit File",
																	 UTL_EDIT_ACTION,
																	 this );

		powerOnChkbox = new JCheckBox( "Power On" );
		imageSizeChkbox = new JCheckBox( "Image Size" );
		rowTxtfld = new OwlNumberField( 5 );
		colTxtfld = new OwlNumberField( 5 );

		JPanel imgSizePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 6, 0 ) );
		imgSizePanel.add( new JLabel( "cols" ) );
		imgSizePanel.add( colTxtfld );
		imgSizePanel.add( Box.createHorizontalStrut( 12 ) );
		imgSizePanel.add( new JLabel( "rows" ) );
		imgSizePanel.add( rowTxtfld );

		addComponent( panel, resetControllerChkbox, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0, 1, 1 );
		addComponent( panel, hardwareTestChkbox,    0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 0, 1, 1 );
		addComponent( panel, pciHWTChkbox,			0, 0, 0, 2, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 1, 1, 1 );
		addComponent( panel, pciHWTTxtfld,          0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 2, 1, 1 );
		addComponent( panel, timHWTChkbox,			0, 5, 0, 2, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 3, 1, 1 );
		addComponent( panel, timHWTTxtfld,          0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 4, 1, 1 );
		addComponent( panel, utilHWTChkbox,			0, 5, 0, 2, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 5, 1, 1 );
		addComponent( panel, utilHWTTxtfld,         0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 6, 1, 1 );
		addComponent( panel, timLoadChkbox,         0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 2, 0, 1, 1 );
		addComponent( panel, timLoadTxtfld,         0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 2, 1, 1, 6 );
		addComponent( panel, timBrowseButton,       0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 2, 7, 1, 1 );
		addComponent( panel, timEditButton,         0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 2, 8, 1, 1 );
		addComponent( panel, utilLoadChkbox,        0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 3, 0, 1, 1 );
		addComponent( panel, utilLoadTxtfld,        0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 3, 1, 1, 6 );
		addComponent( panel, utilBrowseButton,      0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 3, 7, 1, 1 );
		addComponent( panel, utilEditButton,        0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 3, 8, 1, 1 );
		addComponent( panel, powerOnChkbox,         0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 4, 0, 1, 1 );
		addComponent( panel, imageSizeChkbox,       0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 5, 0, 1, 1 );
		addComponent( panel, imgSizePanel,          0, 0, 0, 2, GridBagConstraints.NONE, GridBagConstraints.WEST, 5, 1, 1, 8 );

		return panel;
	}

	protected void loadSetupFile() throws FileNotFoundException, IOException
	{
		String[] ext = { "setup" };
		String[] desc = { "Owl Setup File ( *.setup )" };
		ImageIcon[] icons = { new ImageIcon( MainApp.getBitmapPath() + "setup.gif" ) };
		OwlIconedFileChooser sfc = new OwlIconedFileChooser( file, ext, desc, icons );

		if ( sfc.openDialog() )
		{
			file = sfc.getSelectedFile();
			readSetupFile( file );
		}
	}

	protected void readSetupFile( File setupfile ) throws FileNotFoundException, IOException
	{
		// NOTE - I think the file chooser should just be popped open
		// either from within this function or from within action performed!

		RandomAccessFile inFile;
		boolean boolVal;
		String line;
		String keyword;
		String value;

		// Open the file and output.
		inFile = new RandomAccessFile( setupfile, "r" );

		while ( ( line = inFile.readLine() ) != null )
		{
			// Make sure a keyword exists.
			if ( line.indexOf( ":" ) < 0 )
			{
				continue;
			}

			keyword = line.substring( 0, line.indexOf( ":" ) );
			value	= line.substring( line.lastIndexOf( "\t" ) + 1  );

			// Reset controller checkbox.
			if ( keyword.equals( RESET_CONTROLLER ) )
			{
				boolVal = new Boolean( value ).booleanValue();
				resetControllerChkbox.setSelected( boolVal );
			}

			// Hardware test checkbox.
			else if ( keyword.equals( HARDWARE_TEST ) )
			{
				boolVal = new Boolean( value ).booleanValue();
				hardwareTestChkbox.setSelected( boolVal );
			}

			// PCI hardware test checkbox.
			else if ( keyword.equals( PCI_HARDWARE_TEST ) )
			{
				boolVal = new Boolean( value ).booleanValue();
				pciHWTChkbox.setSelected( boolVal );
			}

			// Timing hardware test checkbox.
			else if ( keyword.equals( TIM_HARDWARE_TEST ) )
			{
				boolVal = new Boolean( value ).booleanValue();
				timHWTChkbox.setSelected( boolVal );
			}

			// Utility hardware test checkbox.
			else if ( keyword.equals( UTIL_HARDWARE_TEST ) )
			{
				boolVal = new Boolean( value ).booleanValue();
				utilHWTChkbox.setSelected( boolVal );
			}

			// Timing board download checkbox.
			else if ( keyword.equals( TIM_DOWNLOAD ) )
			{
				boolVal = new Boolean( value ).booleanValue();
				timLoadChkbox.setSelected( boolVal );
			}

			// Utility board download checkbox.
			else if ( keyword.equals( UTIL_DOWNLOAD ) )
			{
				boolVal = new Boolean( value ).booleanValue();
				utilLoadChkbox.setSelected( boolVal );
			}

			// Power on checkbox.
			else if ( keyword.equals( POWER_ON ) )
			{
				boolVal = new Boolean( value ).booleanValue();
				powerOnChkbox.setSelected( boolVal );
			}

			// Image dimensions checkbox.
			else if ( keyword.equals( IMAGE_DIMENSIONS ) )
			{
				boolVal = new Boolean( value ).booleanValue();
				imageSizeChkbox.setSelected( boolVal );
			}

			// PCI hardware test textfield.
			else if ( keyword.equals( NUMBER_OF_PCI_TESTS ) )
			{
				pciHWTTxtfld.setText( value );
			}

			// Timing hardware test textfield.
			else if ( keyword.equals( NUMBER_OF_TIM_TESTS ) )
			{
				timHWTTxtfld.setText( value );
			}

			// Utility hardware test textfield.
			else if ( keyword.equals( NUMBER_OF_UTIL_TESTS ) )
			{
				utilHWTTxtfld.setText( value );
			}

			// Timing filename textfield.
			else if ( keyword.equals( TIM_FILENAME ) )
			{
				timLoadTxtfld.setText( value );
			}

			// Utility filename textfield.
			else if ( keyword.equals( UTIL_FILENAME ) )
			{
				utilLoadTxtfld.setText( value );
			}

			// Image rows textfield.
			else if ( keyword.equals( IMAGE_ROWS ) )
			{
				rowTxtfld.setText( value );
			}

			// Image cols textfield.
			else if ( keyword.equals( IMAGE_COLS ) )
			{
				colTxtfld.setText( value );
			}

		} // End while

		// Inform user that setup is complete.
		MainApp.info( "Setup loaded" );

		inFile.close();
	}

	protected void writeSetupFile() throws IOException
	{
		RandomAccessFile outFile = new RandomAccessFile( file, "rw" );

		// Set the file length to 1 byte. This is to ensure the new
		// data is properly written.
		outFile.setLength(1);

		// Set the informational message.
		outFile.writeBytes( "**** NOTE: All labels and data values MUST be separated by a single tab. ****\n" );

		// Reset controller checkbox.
		outFile.writeBytes( RESET_CONTROLLER + ":\t" + String.valueOf( resetControllerChkbox.isSelected() ) + "\n" );

		// Hardware test checkbox.
		outFile.writeBytes( HARDWARE_TEST + ":\t" + String.valueOf( hardwareTestChkbox.isSelected() ) + "\n" );
							
		// PCI hardware test checkbox.
		outFile.writeBytes( PCI_HARDWARE_TEST + ":\t" + String.valueOf( pciHWTChkbox.isSelected() ) + "\n" );

		// Timing hardware test checkbox.
		outFile.writeBytes( TIM_HARDWARE_TEST + ":\t" + String.valueOf( timHWTChkbox.isSelected() ) + "\n" );

		// Utility hardware test checkbox.
		outFile.writeBytes( UTIL_HARDWARE_TEST + ":\t" + String.valueOf( utilHWTChkbox.isSelected() ) + "\n" );

		// Timing board download checkbox.
		outFile.writeBytes( TIM_DOWNLOAD + ":\t" + String.valueOf( timLoadChkbox.isSelected() ) + "\n" );
				
		// Utility board download checkbox.
		outFile.writeBytes( UTIL_DOWNLOAD + ":\t" + String.valueOf( utilLoadChkbox.isSelected() ) + "\n" );
				
		// Power on checkbox.
		outFile.writeBytes( POWER_ON + ":\t" + String.valueOf( powerOnChkbox.isSelected() ) + "\n" );

		// Image dimensions checkbox.
		outFile.writeBytes( IMAGE_DIMENSIONS + ":\t" + String.valueOf( imageSizeChkbox.isSelected() ) + "\n" );

		// Save all the textbox info.
		outFile.writeBytes( NUMBER_OF_PCI_TESTS + ":\t" + pciHWTTxtfld.getText() + "\n" );
		outFile.writeBytes( NUMBER_OF_TIM_TESTS + ":\t" + timHWTTxtfld.getText() + "\n" );
		outFile.writeBytes( NUMBER_OF_UTIL_TESTS + ":\t" + utilHWTTxtfld.getText() + "\n" );
		outFile.writeBytes( TIM_FILENAME + ":\t" + timLoadTxtfld.getText() + "\n" );
		outFile.writeBytes( UTIL_FILENAME + ":\t" + utilLoadTxtfld.getText() + "\n" );
		outFile.writeBytes( IMAGE_ROWS + ":\t" + rowTxtfld.getText() + "\n" );
		outFile.writeBytes( IMAGE_COLS + ":\t" + colTxtfld.getText() + "\n" );

		outFile.close();

		// Inform user that setup is complete.
		MainApp.info( "Setup saved." );
	}

	protected void writeScriptFile() throws IOException
	{
		//  Attach a bsh extension if it doesn't exist
		// +-------------------------------------------+
		if ( !file.getName().contains( ".bsh" ) )
		{
			file = new File( file.getAbsolutePath() + ".bsh" );
		}

		//  Create the file
		// +-------------------------------------------+
		RandomAccessFile outFile = new RandomAccessFile( file, "rw" );

		// Set the file length to 1 byte. This is to ensure the new
		// data is properly written.
		outFile.setLength(1);

		outFile.writeBytes( "setup = StandardSetup();\n" );
		outFile.writeBytes( "setup.enableResetController( " + resetControllerChkbox.isSelected() + " );\n" );

		if ( hardwareTestChkbox.isSelected() )
		{
			outFile.writeBytes( "setup.enablePCITests( " + pciHWTChkbox.isSelected() + ", " + getIntValueOf( pciHWTTxtfld ) + " );\n" );
			outFile.writeBytes( "setup.enableTIMTests( " + timHWTChkbox.isSelected() + ", " + getIntValueOf( timHWTTxtfld ) + " );\n" );
			outFile.writeBytes( "setup.enableUTLTests( " + utilHWTChkbox.isSelected() + ", " + getIntValueOf( utilHWTTxtfld ) + " );\n" );
		}

		outFile.writeBytes( "setup.enableTIMDownload( " + timLoadChkbox.isSelected() + ", " + getTextValueOf( timLoadTxtfld ) + " );\n" );
		outFile.writeBytes( "setup.enableUTLDownload( " + utilLoadChkbox.isSelected() + ", " + getTextValueOf( utilLoadTxtfld ) + " );\n" );
		outFile.writeBytes( "setup.enablePowerOn( " + powerOnChkbox.isSelected() + " );\n" );
		outFile.writeBytes( "setup.enableImageSize( " + imageSizeChkbox.isSelected() + ", " + getIntValueOf( rowTxtfld ) + ", " + getIntValueOf( colTxtfld ) + " );\n" );
		outFile.writeBytes( "setup.run();\n" );

		outFile.close();

		// Inform user that setup is complete.
		MainApp.info( "Setup script saved." );
	}

	// +--------------------------------------------------------------------+
	// |  loadPreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Loads the buttons and their scripts upon startup.                 |
	// +--------------------------------------------------------------------+
	protected void loadPreferences()
	{
		boolean aBool    = false;
		String  prefKey  = null;
		String  aString  = null;

		super.loadPreferences();

		if ( timLoadChkbox != null )
		{
			prefKey = getTitle() + " " + RESET_CONTROLLER;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			resetControllerChkbox.setSelected( aBool );
	
			prefKey = getTitle() + " " + HARDWARE_TEST;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			hardwareTestChkbox.setSelected( aBool );
	
			prefKey = getTitle() + " " + PCI_HARDWARE_TEST;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			pciHWTChkbox.setSelected( aBool );
	
			prefKey = getTitle() + " " + TIM_HARDWARE_TEST;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			timHWTChkbox.setSelected( aBool );
	
			prefKey = getTitle() + " " + UTIL_HARDWARE_TEST;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			utilHWTChkbox.setSelected( aBool );
	
			prefKey = getTitle() + " " + TIM_DOWNLOAD;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			timLoadChkbox.setSelected( aBool );
	
			prefKey = getTitle() + " " + UTIL_DOWNLOAD;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			utilLoadChkbox.setSelected( aBool );
	
			prefKey = getTitle() + " " + POWER_ON;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			powerOnChkbox.setSelected( aBool );
	
			prefKey = getTitle() + " " + IMAGE_DIMENSIONS;
			aBool = MainApp.getPreferences().getBoolean( prefKey, false );
			imageSizeChkbox.setSelected( aBool );

			prefKey = getTitle() + " " + TIM_FILENAME;
			aString = MainApp.getPreferences().get( prefKey, null );

			if ( aString != null && !aString.isEmpty() )
			{
				if ( ( new File( aString ) ).exists() )
				{
					timLoadTxtfld.setText( aString );
					file = new File( aString ).getParentFile();
				}
			}
	
			prefKey = getTitle() + " " + UTIL_FILENAME;
			aString = MainApp.getPreferences().get( prefKey, null );

			if ( aString != null && !aString.isEmpty() )
			{
				if ( ( new File( aString ) ).exists() )
				{
					utilLoadTxtfld.setText( aString );
					file = new File( aString ).getParentFile();
				}
			}

			prefKey = getTitle() + " " + NUMBER_OF_PCI_TESTS;
			aString = MainApp.getPreferences().get( prefKey, null );
			if ( aString != null ) { pciHWTTxtfld.setText( aString ); }
	
			prefKey = getTitle() + " " + NUMBER_OF_TIM_TESTS;
			aString = MainApp.getPreferences().get( prefKey, null );
			if ( aString != null ) { timHWTTxtfld.setText( aString ); }
	
			prefKey = getTitle() + " " + NUMBER_OF_UTIL_TESTS;
			aString = MainApp.getPreferences().get( prefKey, null );
			if ( aString != null ) { utilHWTTxtfld.setText( aString ); }
	
			prefKey = getTitle() + " " + IMAGE_ROWS;
			aString = MainApp.getPreferences().get( prefKey, null );
			if ( aString != null ) { rowTxtfld.setText( aString ); }
	
			prefKey = getTitle() + " " + IMAGE_COLS;
			aString = MainApp.getPreferences().get( prefKey, null );
			if ( aString != null ) { colTxtfld.setText( aString ); }
		}
	}

	// +--------------------------------------------------------------------+
	// |  savePreferences                                                   |
	// +--------------------------------------------------------------------+
	// |  Saves the setup button script and state upon exit                 |
	// +--------------------------------------------------------------------+
	protected void savePreferences()
	{
		super.savePreferences();

		String prefKey = null;

		prefKey = getTitle() + " " + RESET_CONTROLLER;
		MainApp.getPreferences().putBoolean( prefKey, resetControllerChkbox.isSelected() );

		prefKey = getTitle() + " " + HARDWARE_TEST;
		MainApp.getPreferences().putBoolean( prefKey, hardwareTestChkbox.isSelected() );

		prefKey = getTitle() + " " + PCI_HARDWARE_TEST;
		MainApp.getPreferences().putBoolean( prefKey, pciHWTChkbox.isSelected() );

		prefKey = getTitle() + " " + TIM_HARDWARE_TEST;
		MainApp.getPreferences().putBoolean( prefKey, timHWTChkbox.isSelected() );

		prefKey = getTitle() + " " + UTIL_HARDWARE_TEST;
		MainApp.getPreferences().putBoolean( prefKey, utilHWTChkbox.isSelected() );

		prefKey = getTitle() + " " + TIM_DOWNLOAD;
		MainApp.getPreferences().putBoolean( prefKey, timLoadChkbox.isSelected() );

		prefKey = getTitle() + " " + UTIL_DOWNLOAD;
		MainApp.getPreferences().putBoolean( prefKey, utilLoadChkbox.isSelected() );

		prefKey = getTitle() + " " + POWER_ON;
		MainApp.getPreferences().putBoolean( prefKey, powerOnChkbox.isSelected() );

		prefKey = getTitle() + " " + IMAGE_DIMENSIONS;
		MainApp.getPreferences().putBoolean( prefKey, imageSizeChkbox.isSelected() );

		prefKey = getTitle() + " " + TIM_FILENAME;
		MainApp.getPreferences().put( prefKey, timLoadTxtfld.getText() );

		prefKey = getTitle() + " " + UTIL_FILENAME;
		MainApp.getPreferences().put( prefKey, utilLoadTxtfld.getText() );

		prefKey = getTitle() + " " + NUMBER_OF_PCI_TESTS;
		MainApp.getPreferences().put( prefKey, pciHWTTxtfld.getText() );

		prefKey = getTitle() + " " + NUMBER_OF_TIM_TESTS;
		MainApp.getPreferences().put( prefKey, timHWTTxtfld.getText() );

		prefKey = getTitle() + " " + NUMBER_OF_UTIL_TESTS;
		MainApp.getPreferences().put( prefKey, utilHWTTxtfld.getText() );

		prefKey = getTitle() + " " + IMAGE_ROWS;
		MainApp.getPreferences().put( prefKey, rowTxtfld.getText() );

		prefKey = getTitle() + " " + IMAGE_COLS;
		MainApp.getPreferences().put( prefKey, colTxtfld.getText() );
	}

	private String getTextValueOf( JTextField txtfld )
	{
		String text = txtfld.getText();

		if ( text.equals( "" ) )
		{
			text = "\"\"";
		}
		else
		{
			text = text.replace( "\\", "\\\\" );
			text = "\"" + text + "\"";
		}

		return text;
	}

	private String getIntValueOf( JTextField txtfld )
	{
		String text = txtfld.getText();

		if ( text.equals( "" ) ) { text = "0"; }

		return text;
	}
}

