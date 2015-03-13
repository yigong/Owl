package owl.main.remote;


public class ServerElement
{
	private String m_description;
	private String m_ip;

	public ServerElement()
	{
		m_description = "";
		m_ip          = "";
	}

	public String getIP()
	{
		return m_ip;
	}

	public String getDescription()
	{
		return m_description;
	}

	// Required by digester to set 'ip'
	// -------------------------------------------
	public void setIp( String ip )
	{
		m_ip = ip;
	}

	// Required by digester to set 'description'
	// -------------------------------------------
	public void setDescription( String description )
	{
		m_description = description;
	}
}
