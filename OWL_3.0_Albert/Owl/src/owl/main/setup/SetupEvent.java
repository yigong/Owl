package owl.main.setup;


public class SetupEvent extends java.util.EventObject
{
	private static final long serialVersionUID = 3459054498247955492L;

	public boolean powerOn;		// Do power on
	public boolean setupOk;		// Setup ok - 'RCC' is valid

	public SetupEvent( Object source, boolean powerOn, boolean setupOk )
	{
		super( source );

		this.powerOn = powerOn;
		this.setupOk = setupOk;
	}
}
