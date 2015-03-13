package owl.cameraAPI;

import owl.gui.utils.OwlUtilities;


/**
********************************************************************************
*	This class provides an exception if the expected reply does not match the
*	actual reply returned from the controller / host device.
*
*	@see Throwable
*	@version 2.00
*	@author Scott Streit
********************************************************************************
*/
public class ReplyException extends Throwable
{
	private static final long serialVersionUID = 4553942474896047147L;

	private int actualReply;
	private int expectedReply;
	private int command;

    //--------------------------------------------------------------------------
    //   Constructors:
    //--------------------------------------------------------------------------
	public ReplyException( int reply )
	{
		actualReply   = reply;
		expectedReply = -1;
	}

	public ReplyException( int actualReply, int expectedReply )
	{
		this.actualReply   = actualReply;
		this.expectedReply = expectedReply;
	}

	public ReplyException( int cmd, int actualReply, int expectedReply )
	{
		this.command       = cmd;
		this.actualReply   = actualReply;
		this.expectedReply = expectedReply;
	}

	public ReplyException( String message )
	{
		super( message );
	}

    //--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------

	public int getCommand()
	{
		return command;
	}

	public int getActualReply()
	{
		return actualReply;
	}

	public String getActualHexString()
	{
		return "0x" + Integer.toHexString( actualReply );
	}

	public int getExpectedReply()
	{
		return expectedReply;
	}

	public String getExpectedHexString()
	{
		return "0x" + Integer.toHexString( expectedReply );
	}

	public String toString()
	{
		return ( OwlUtilities.intToAscii( getCommand() ) +
				 " Reply expected: " +
				 getExpectedHexString() +
				 " actual: " +
				 getActualHexString() );
	}
}
