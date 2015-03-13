package owl.main.debug.debugframe;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import owl.cameraAPI.CameraAPI;
import owl.gui.popupmenus.RecentPopupMenu;
import owl.gui.utils.OwlButtonFactory;
import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;
import owl.main.owltypes.OwlNumberField;
import owl.main.owltypes.OwlTextField;
import owl.main.setup.SetupEvent;
import owl.main.setup.SetupListener;


public class CMDPanel extends DebugRunnablePanel implements ActionListener, SetupListener
{
	private static final long serialVersionUID = 7192821434845106975L;
	private final String CLEAR_ALL_FIELDS = "CLEAR_ALL_FIELDS";

	private BoardMemoryPanel	brdMemPanel;
	private RadixPanel			radixPanel;
	private OutputPanel			resultPanel;
	private OwlTextField		cmdTxtfld;
	private OwlNumberField[]	argTxtfld;
	private JLabel				recentCmdMenu;

	public CMDPanel( JButton button, String applyAction, String abortAction )
	{
		super();

		this.button      = button;
		this.applyAction = applyAction;
		this.abortAction = abortAction;

		brdMemPanel = new BoardMemoryPanel( BoardMemoryPanel.BRD );
		brdMemPanel.setAlignmentX( Component.CENTER_ALIGNMENT );

		resultPanel = new OutputPanel( OutputPanel.SINGLE );

		JPanel argPanel = new JPanel();
		argPanel.setLayout( gbl );

		JLabel cmdLabel = new JLabel( "Command  " );
		cmdTxtfld = new OwlTextField( 6 );

		JLabel argLabel = new JLabel();
		argTxtfld = new OwlNumberField[ 4 ];

		radixPanel = new RadixPanel();
		radixPanel.addRadixListener( this );

		addComponent( argPanel, cmdLabel,  0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0, 1, 1 );
		addComponent( argPanel, cmdTxtfld, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 0, 1, 1 );

		for ( int i=0; i<4; i++ )
		{
			argTxtfld[ i ] = new OwlNumberField( 6 );
			argTxtfld[ i ].setNumbersOnly( false );

			argLabel = new JLabel( "arg #" + i );

			addComponent( argPanel, argLabel,     0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, i+1, 1, 1 );
			addComponent( argPanel, argTxtfld[i], 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, i+1, 1, 1 );
		}

		JButton clearButton = OwlButtonFactory.createIconButton( "Off.gif", 24, 24, null, CLEAR_ALL_FIELDS, this );
		clearButton.setBorderPainted( false );
		addComponent( argPanel, clearButton, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 5, 1, 1 );

		ImageIcon recentMenuIcon = new ImageIcon( MainApp.getBitmapPath() + "PopupMenu8.gif" );
		recentCmdMenu = new JLabel( recentMenuIcon );
		recentCmdMenu.setComponentPopupMenu( new RecentPopupMenu( "CMDPanel", this ) );
		recentCmdMenu.addMouseListener( ( RecentPopupMenu )recentCmdMenu.getComponentPopupMenu() );
		addComponent( argPanel, recentCmdMenu, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 6, 1, 1 );

		JLabel infoLabel =
				new JLabel( "<html><font color=\"#345367\"><b>NOTE</b>: ASCII arguments must be quoted with \' or \".<br>" +
						    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
							"Commands should never be quoted.</font></html>" );

		JPanel infoPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		infoPanel.add( infoLabel );

		add( infoPanel   );
		add( brdMemPanel );
		add( argPanel    );
		add( radixPanel  );
		add( resultPanel );
	}

	public String toString()
	{
		String s = cmdTxtfld.getText()      + " " +
				   argTxtfld[ 0 ].getText() + " " +
				   argTxtfld[ 1 ].getText() + " " +
				   argTxtfld[ 2 ].getText() + " " +
				   argTxtfld[ 3 ].getText();

		return s.trim();
	}

	public void clearAllFields()
	{
		cmdTxtfld.setText( "" );

		argTxtfld[ 0 ].setText( "" );
		argTxtfld[ 1 ].setText( "" );
		argTxtfld[ 2 ].setText( "" );
		argTxtfld[ 3 ].setText( "" );
	}

	@Override
	public void run()
	{
		int retVal = 0;

		if ( !CameraAPI.IsDeviceOpen() ) { return; }

		if ( toString().isEmpty() )
		{
			MainApp.error( "( CMDPanel ): Empty fields! No command specified!" );

			return;
		}

		Thread.currentThread().setName( "Owl - CMD Panel" );

		setButtonAction( abortAction );

		try
		{
			resultPanel.clear();

			MainApp.infoStart( "Sending " + cmdTxtfld.getText().toUpperCase() + " command" );

			retVal = CameraAPI.Cmd2(
							brdMemPanel.getBoardID(),
							OwlUtilities.asciiToInt( cmdTxtfld.getText(),      radixPanel.getRadix() ),
							OwlUtilities.quotedAsciiToInt( argTxtfld[ 0 ].getText(), radixPanel.getRadix() ),
							OwlUtilities.quotedAsciiToInt( argTxtfld[ 1 ].getText(), radixPanel.getRadix() ),
							OwlUtilities.quotedAsciiToInt( argTxtfld[ 2 ].getText(), radixPanel.getRadix() ),
							OwlUtilities.quotedAsciiToInt( argTxtfld[ 3 ].getText(), radixPanel.getRadix() ) );

			MainApp.infoEnd();

			resultPanel.setValue( retVal, radixPanel.getRadix() );
		}
		catch ( NumberFormatException nfe )
		{
			MainApp.infoFail();
			MainApp.error( nfe.getMessage() );
		}
		catch ( Exception e )
		{
			resultPanel.clear();
			MainApp.infoFail();
			MainApp.error( e );
		}
		finally
		{
			recentCmdMenu.getComponentPopupMenu().add( toString() );

			( ( RecentPopupMenu )recentCmdMenu.getComponentPopupMenu() ).savePreferences();

			setButtonAction( applyAction );
		}
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( e.getActionCommand().equals( RadixPanel.DEC_ACTION ) )
		{
			for ( int i=0; i<argTxtfld.length; i++ )
			{
				argTxtfld[ i ].setText( changeRadix( argTxtfld[ i ].getText(), 10 ) );
			}
		}

		else if ( e.getActionCommand().equals( RadixPanel.HEX_ACTION ) )
		{
			for ( int i=0; i<argTxtfld.length; i++ )
			{
				argTxtfld[ i ].setText( changeRadix( argTxtfld[ i ].getText(), 16 ) );
			}
		}

		else if ( e.getActionCommand().equals( CLEAR_ALL_FIELDS ) )
		{
			clearAllFields();
		}

		else
		{
			RecentPopupMenu popup = ( RecentPopupMenu )recentCmdMenu.getComponentPopupMenu();

			String[] tokens = popup.getItem( e.getActionCommand() );

			if ( tokens != null )
			{
				clearAllFields();

				if ( tokens.length >= 1 )
				{
					cmdTxtfld.setText( tokens[ 0 ] );
				}

				for ( int i=1; i<tokens.length; i++ )
				{
					argTxtfld[ i - 1 ].setText( tokens[ i ] );
				}
			}
		}
	}

	@Override
	public void setupChanged( SetupEvent event )
	{
		try
		{
			if ( event.setupOk && CameraAPI.IsCCParamSupported( CameraAPI.ARC50 ) )
			{
				brdMemPanel.supportsUtilBrdOption( true );
			}
			else
			{
				brdMemPanel.supportsUtilBrdOption( false );
			}
		}
		catch ( Exception ex )
		{
			MainApp.error( ex );
		}
	}
}
