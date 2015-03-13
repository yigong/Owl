package owl.main.dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import owl.cameraAPI.CameraAPI;
import owl.dir.dialog.DirDialog;
import owl.gui.utils.OwlButtonFactory;
import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;
import owl.main.owltypes.OwlDialog;
import owl.main.owltypes.OwlFrame;
import owl.main.owltypes.OwlMessageBox;
import owl.main.owltypes.OwlNumberField;
import owl.main.owltypes.OwlTextField;


public class ImgBufDumpDialog extends OwlDialog implements ChangeListener, ActionListener
{
	private static final long serialVersionUID = 3896081882327093126L;

	private final String SAVE_ACTION			= "Save";
	private final String FITS_ACTION			= "fit";
	private final String TEXT_ACTION			= "text";
	private final String DIR_BROWSE_ACTION		= "Dir Browse";
	private final String READ_FROM_CTLR_ACTION	= "ReadFromCtlr";

	private final String FILE_PREF				= "FileName";
	private final String FITS_TYPE_PREF			= "FITSFileType";
	private final String ROW_PREF				= "Rows";
	private final String COL_PREF				= "Cols";

	private final String DEFAULT_FILE			= System.getProperty( "user.dir" ) +
												  System.getProperty( "file.separator" ) +
												  "Image.fit";

	protected OwlTextField   m_fileTxtfld;
	protected OwlNumberField m_rowTxtfld;
	protected OwlNumberField m_colTxtfld;
	protected JButton        m_okButton;
	protected JButton        m_cancelButton;
	protected JButton        m_browseButton;
	protected JButton        m_getFromCtlrButton;
	protected JCheckBox      m_fitsChkBox;
	protected JCheckBox      m_textChkBox;
	protected DirDialog      m_dirDialog;


	public ImgBufDumpDialog( OwlFrame owner )
	{
		super( owner, "Dump Image Buffer" );

		getContentPane().setLayout( new BoxLayout( getContentPane(), BoxLayout.Y_AXIS ) );

		m_dirDialog = null;

		m_fileTxtfld = new OwlTextField( DEFAULT_FILE, 30 );

		m_rowTxtfld = new OwlNumberField( 0, 6 );
		m_colTxtfld = new OwlNumberField( 0, 6 );

		m_okButton = OwlButtonFactory.createButton( 70,
													27,
													null,
													SAVE_ACTION,
													this );

		m_cancelButton = OwlButtonFactory.createButton( "Close",
														70,
														27,
														null,
														super.CLOSE_ACTION,
														this );

		m_browseButton = OwlButtonFactory.createIconButton( "folder.gif",
															"Browse",
															DIR_BROWSE_ACTION,
															this );

		m_getFromCtlrButton = OwlButtonFactory.createIconButton( "GetSetCtlr.gif",
																 27,
																 25,
																 "Read image dimensions from controller",
																 READ_FROM_CTLR_ACTION,
																 this );

		m_fitsChkBox = new JCheckBox( FITS_ACTION, true );
		m_fitsChkBox.addChangeListener( this );

		m_textChkBox = new JCheckBox( TEXT_ACTION );
		m_textChkBox.addChangeListener( this );

		ButtonGroup typeGroup = new ButtonGroup();
		typeGroup.add( m_fitsChkBox );
		typeGroup.add( m_textChkBox );

		JPanel filePanel = new JPanel();
		filePanel.setLayout( new FlowLayout( FlowLayout.LEFT, 4, 2 ) );
		filePanel.add( new JLabel( "Filename: " ) );
		filePanel.add( m_fileTxtfld );
		filePanel.add( m_browseButton );
		filePanel.add( m_fitsChkBox );
		filePanel.add( m_textChkBox );
		getContentPane().add( filePanel );

		JPanel sizePanel = new JPanel();
		sizePanel.setLayout( new FlowLayout( FlowLayout.LEFT, 4, 2 ) );
		sizePanel.add( new JLabel( "Rows: " ) );
		sizePanel.add( m_rowTxtfld );
		sizePanel.add( Box.createHorizontalStrut( 2 ) );
		sizePanel.add( new JLabel( "Cols: " ) );
		sizePanel.add( m_colTxtfld );
		sizePanel.add( Box.createHorizontalStrut( 2 ) );
		sizePanel.add( m_getFromCtlrButton );

		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize( new Dimension( 3 * m_okButton.getPreferredSize().width - 10, m_okButton.getPreferredSize().height + 2 ) );
		buttonPanel.setLayout( new FlowLayout( FlowLayout.RIGHT, 4, 2 ) );
		buttonPanel.add( m_okButton );
		buttonPanel.add( m_cancelButton );
		sizePanel.add( buttonPanel );
		getContentPane().add( sizePanel );

		pack();

		owl.gui.utils.OwlUtilities.centerFrame( this );

		loadPreferences();
	}

	public boolean isFits()  { return m_fitsChkBox.isSelected(); }
	public String  getFile() { return m_fileTxtfld.getText(); }

	public int getRows() throws Exception
	{
		int dRows = Integer.parseInt( m_rowTxtfld.getText() );

		if ( dRows <=0 )
		{
			throw new Exception( "( ImgBufDumpDialog ): Invalid row size: " + dRows );
		}

		return dRows;
	}

	public int getCols() throws Exception
	{
		int dCols = Integer.parseInt( m_colTxtfld.getText() );

		if ( dCols <=0 )
		{
			throw new Exception( "( ImgBufDumpDialog ): Invalid column size: " + dCols );
		}

		return dCols;
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( e.getActionCommand().equals( DIR_BROWSE_ACTION ) )
		{
			if ( m_dirDialog == null )
			{
				m_dirDialog = new DirDialog( null );
			}

			if ( m_dirDialog != null )
			{
				if ( !m_fileTxtfld.getText().isEmpty() )
				{
					m_dirDialog.setPath( OwlUtilities.getPath( m_fileTxtfld.getText() ) );
				}
				else
				{
					m_dirDialog.setPath( OwlUtilities.getPath( DEFAULT_FILE ) );
				}

				if ( m_dirDialog.showDialog() )
				{
					try
					{
						m_fileTxtfld.setText( m_dirDialog.getPath() +
											  System.getProperty( "file.separator" ) +
											  OwlUtilities.getFileName( m_fileTxtfld.getText() ) );
					}
					catch ( Exception ex )
					{
						MainApp.error( ex );
					}
				}
			}
		}

		else if ( e.getActionCommand().equals( SAVE_ACTION ) )
		{
			//  Run it in a thread
			// +------------------------------------------+
			new Thread(
					new Runnable()
					{
						OwlMessageBox msgBox =
								new OwlMessageBox( "Saving image data ... please wait.",
													new ImageIcon( MainApp.getBitmapPath() + "Image.gif" ),
													300000 );

						public void run()
						{
							Thread.currentThread().setName( "Owl - ImgBuf Dump Dialog [ OK ACTION ]" );

							MainApp.infoStart( "Saving image data" );

							if ( CameraAPI.IsDeviceOpen() )
							{
								try
								{
									msgBox.start();
					
									if ( isFits() )
									{
										CameraAPI.WriteFitsFile( getFile(),
																 getRows(),
																 getCols() );
									}
									else
									{
										CameraAPI.WriteBufferTextFile( getFile(),
																	   getRows(),
																	   getCols() );
									}
								}
								catch ( Exception e )
								{
									MainApp.infoFail();
									MainApp.error( e.getMessage() );
								}
								finally
								{
									msgBox.stop();
									MainApp.infoEnd();
								}
							}
							else
							{
								MainApp.infoFail();
								MainApp.error( "Not connected to any device!" );
							}
						}
					} ).start();
		}

		else if ( e.getActionCommand().equals( READ_FROM_CTLR_ACTION ) )
		{
			//  Run it in a thread
			// +------------------------------------------+
			new Thread(
					new Runnable()
					{
						public void run()
						{
							Thread.currentThread().setName( "Owl - ImgBuf Dump Dialog [ CTRLR ACTION ]" );

							MainApp.infoStart( "Reading image dimensions from controller" );
			
							if ( CameraAPI.IsDeviceOpen() && CameraAPI.IsControllerConnected() )
							{
								try
								{
									int[] imgDimen = CameraAPI.GetImageSize();

									MainApp.infoEnd();
			
									m_rowTxtfld.setValue( imgDimen[ 0 ] );
									m_colTxtfld.setValue( imgDimen[ 1 ] );
								}
								catch ( Exception ex )
								{
									MainApp.infoFail();
									MainApp.error( ex.getMessage() );
								}
							}
							else
							{
								MainApp.infoFail();
								MainApp.error( "Not connected to any device or controller!" );
							}
						}
					} ).start();
		}

		else
		{
			super.actionPerformed( e );
		}
	}

	@Override
	public void stateChanged( ChangeEvent e )
	{
		if ( e.getSource() instanceof javax.swing.JCheckBox )
		{
			JCheckBox chkbox = ( JCheckBox )e.getSource();

			File file = new File( m_fileTxtfld.getText() );

			if ( chkbox.getText().equals( TEXT_ACTION ) )
			{
				if ( file.getName().contains( "." ) )
				{
					m_fileTxtfld.setText( file.getPath().replace( "fit", "txt" ) );
				}
				else
				{
					m_fileTxtfld.setText( file.getPath() + ".txt" );
				}
			}
			else
			{
				if ( file.getName().contains( "." ) )
				{
					m_fileTxtfld.setText( file.getPath().replace( "txt", "fit" ) );
				}
				else
				{
					m_fileTxtfld.setText( file.getPath() + ".fit" );
				}
			}
		}
	}

	protected void loadPreferences()
	{
		m_fileTxtfld.setText( MainApp.getPreferences().get( super.getTitle() + FILE_PREF, DEFAULT_FILE ) );

		m_fitsChkBox.setSelected( MainApp.getPreferences().getBoolean( super.getTitle() + FITS_TYPE_PREF, true ) );

		m_rowTxtfld.setValue( MainApp.getPreferences().getInt( super.getTitle() + ROW_PREF, ( int )0 ) );
		m_colTxtfld.setValue( MainApp.getPreferences().getInt( super.getTitle() + COL_PREF, ( int )0 ) );
	}

	@Override
	protected void savePreferences()
	{
		MainApp.getPreferences().put( super.getTitle() + FILE_PREF, m_fileTxtfld.getText() );

		MainApp.getPreferences().putBoolean( super.getTitle() + FITS_TYPE_PREF, m_fitsChkBox.isSelected() );

		MainApp.getPreferences().putInt( super.getTitle() + ROW_PREF, m_rowTxtfld.getInt() );
		MainApp.getPreferences().putInt( super.getTitle() + COL_PREF, m_colTxtfld.getInt() );
	}
}
