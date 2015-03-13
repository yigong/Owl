package owl.main.owltypes;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import owl.main.MainApp;



public class OwlInputDialog implements ActionListener
{
	public final static int OK     = 0;
	public final static int CANCEL = 1;

	private JDialog					m_dialog;
	private Vector<OwlTextField>	m_txtfldVector;
	private GridBagLayout			m_gbl;
	private GridBagConstraints		m_gbc;
	private String					m_sIconName;
	private boolean					m_bOk;
	private boolean					m_bModal;
	private boolean					m_bWait;
	private int						m_dButtonFlags;


	public OwlInputDialog()
	{
		this( true,
			  ( OwlInputDialog.OK | OwlInputDialog.CANCEL ),
			  "owl.gif" );
	}

	public OwlInputDialog( boolean bModal )
	{
		this( bModal,
			  ( OwlInputDialog.OK | OwlInputDialog.CANCEL ),
			  "owl.gif" );
	}

	public OwlInputDialog( boolean bModal, int dButtonFlags )
	{
		this( bModal,
			  dButtonFlags,
			  "owl.gif" );
	}

	public OwlInputDialog( boolean bModal, int dButtonFlags, String sIconName )
	{
		m_txtfldVector = new Vector<OwlTextField>();
		m_gbl          = new GridBagLayout();
		m_gbc          = new GridBagConstraints();
		m_bModal       = bModal;
		m_dButtonFlags = dButtonFlags;
		m_sIconName    = sIconName;
		m_bWait        = true;
	}

	public int showDialog( JFrame owner, String title, String[] labels )
	{
		return showDialog( owner, title, labels, null );
	}

	public int showDialog( JFrame owner, String title, String[] labels, String[] initText )
	{
		int retVal = CANCEL;

		m_bOk = false;

		if ( initText != null && labels.length != initText.length )
		{
			MainApp.error( "( UserDataInput.showDialog ): label string " +
						   "array must equal initText array in length!" );
			return retVal;
		}

		JPanel panel  = new JPanel();
		panel.setLayout( m_gbl );

		m_dialog = new JDialog( owner, title, m_bModal );
		m_dialog.getContentPane().add( panel );

		m_dialog.setIconImage(
				java.awt.Toolkit.getDefaultToolkit().createImage( MainApp.getBitmapPath() +
																  m_sIconName ) );

		for ( int i=0; i<labels.length; i++ )
		{
			OwlTextField txtfld;

			if( initText != null && initText[ i ] != null )
			{
				txtfld = new OwlTextField( initText[ i ], 20 );
			}
			else
			{
				txtfld = new OwlTextField( "", 20 );
			}

			m_txtfldVector.add( txtfld );

			addComponent( panel, new JLabel( labels[ i ] + ": " ), 5, 5, 0, 5, GridBagConstraints.WEST, i, 0, 1, 1 );
			addComponent( panel, txtfld, 5, 0, 0, 5, GridBagConstraints.WEST, i, 1, 1, 1 );
		}

		JButton okButton = new JButton( "OK" );
		okButton.setPreferredSize( new Dimension( 85, 25 ) );

		JPanel buttonPanel = new JPanel();
		( ( JButton )buttonPanel.add( okButton ) ).addActionListener( this );

		if ( ( m_dButtonFlags & OwlInputDialog.CANCEL ) > 0 )
		{
			JButton cancelButton = new JButton( "CANCEL" );
			cancelButton.setPreferredSize( new Dimension( 85, 25 ) );

			( ( JButton )buttonPanel.add( cancelButton ) ).addActionListener( this );
		}

		addComponent( panel, buttonPanel, 5, 5, 5, 5, GridBagConstraints.CENTER, labels.length, 0, 1, 3 );

		m_dialog.pack();
		centerDialog();

		// Code waits here for OK/CANCEL button to be clicked
		m_dialog.setVisible( true );

		while ( m_bWait ) { try { Thread.sleep( 100 ); } catch ( Exception e ) {} }

		if ( m_bOk ) { retVal = OK; }

		return retVal;
	}

	public String[] getInputValues()
	{
		String[] strVals = new String[ m_txtfldVector.size() ];

		for ( int i=0; i<m_txtfldVector.size(); i++ )
		{
			strVals[ i ] = m_txtfldVector.get( i ).getText();
		}

		return strVals;
	}

	public int[] getInputValuesAsInts()
	{
		int[] dVals = new int[ m_txtfldVector.size() ];

		try
		{
			for ( int i=0; i<m_txtfldVector.size(); i++ )
			{
				dVals[ i ] =
					Integer.parseInt( m_txtfldVector.get( i ).getText() );
			}
		}
		catch ( NumberFormatException nfe )
		{
			dVals = null;
		}

		return dVals;
	}

	public void actionPerformed( ActionEvent arg0 )
	{
		if ( arg0.getActionCommand().equals( "OK" ) )
		{
			m_bOk = true;
		}

		m_bWait = false;

		m_dialog.dispose();
	}

	private void addComponent( JPanel panel, JComponent comp, int insetTOP,
							   int insetLEFT, int insetBOTTOM, int insetRIGHT,
							   int anchr, int row, int col, int rowSpan,
							   int colSpan )
	{
		// Set the grid layout constraints.
		m_gbc.fill = GridBagConstraints.NONE;
		m_gbc.anchor = anchr;
		m_gbc.insets = new Insets( insetTOP, insetLEFT, insetBOTTOM, insetRIGHT );
		m_gbc.gridx = col;
		m_gbc.gridy = row;
		m_gbc.gridwidth = colSpan;
		m_gbc.gridheight = rowSpan;
		
		// Add the component to the panel.
		m_gbl.setConstraints( comp, m_gbc );
		panel.add( comp );
	}

	private void centerDialog()
	{
		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int xPos = d.width / 2 - m_dialog.getWidth() / 2 - 50;
		int yPos = d.height / 4  - m_dialog.getHeight() / 2;
		m_dialog.setLocation( xPos, yPos );
	}
}
