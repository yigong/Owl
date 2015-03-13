package owl.main.debug.debugframe;

import javax.swing.JButton;

import owl.cameraAPI.CameraAPI;
import owl.main.MainApp;
import owl.main.setup.SetupEvent;
import owl.main.setup.SetupListener;


public class TDLPanel extends DebugRunnablePanel implements SetupListener
{
	private static final long serialVersionUID = -4807431409609918014L;

	private RadixPanel			radixPanel;
	private BoardMemoryPanel	brdMemPanel;
	private ValuePanel			valuePanel;
	private OutputPanel			resultPanel;

	public TDLPanel( JButton button, String applyAction, String abortAction )
	{
		super();

		this.button      = button;
		this.applyAction = applyAction;
		this.abortAction = abortAction;

		radixPanel  = new RadixPanel();
		brdMemPanel = new BoardMemoryPanel( BoardMemoryPanel.BRD );
		valuePanel  = new ValuePanel();
		resultPanel = new OutputPanel( OutputPanel.SINGLE );

		valuePanel.setValue( 0x112233, 16 );
		radixPanel.addRadixListener( valuePanel );

		add( radixPanel  );
		add( brdMemPanel );
		add( valuePanel  );
		add( resultPanel );
	}

	@Override
	public void run()
	{
		Thread.currentThread().setName( "Owl - TDLPanel" );

		if ( !CameraAPI.IsDeviceOpen() ) { return; }

		setButtonAction( abortAction );

		try
		{
			resultPanel.clear();

			int value  = valuePanel.getValue( radixPanel.getRadix() );
			int retVal = 0;

			MainApp.infoStart( "Sending Test Data Link" );
			retVal = CameraAPI.Cmd2( brdMemPanel.getBoardID(),
									 CameraAPI.TDL,
	 								 value );
			MainApp.infoEnd();

			resultPanel.setValue( retVal, radixPanel.getRadix() );

			if ( valuePanel.isIncrement() )
			{
				valuePanel.setValue( ++value, radixPanel.getRadix() );
			}
		}
		catch ( NumberFormatException nfe )
		{
			MainApp.infoFail();
			MainApp.error( nfe.getMessage() );
		}
		catch ( Exception e )
		{
			MainApp.infoFail();
			MainApp.error( e );
		}
		finally
		{
			setButtonAction( applyAction );
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
