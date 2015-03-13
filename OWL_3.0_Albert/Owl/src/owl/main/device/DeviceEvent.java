package owl.main.device;


public class DeviceEvent extends java.util.EventObject
{
	private static final long serialVersionUID = 2027614213327518188L;

	public String deviceName;

	public DeviceEvent( Object source, String deviceName )
	{
		super( source );
		this.deviceName = deviceName;
	}
}
