package owl.CCParams;

import java.util.EventObject;

public class CCScriptEvent extends EventObject
{
	private static final long serialVersionUID = 3523036534150243964L;

	public String action;
	public String description;
	public Object object;

	public CCScriptEvent( Object source,
						  String catString,
						  String desString,
						  Object anObject )
	{
		super( source );

		description	= desString;
		action		= catString;
		object		= anObject;
	}
}
