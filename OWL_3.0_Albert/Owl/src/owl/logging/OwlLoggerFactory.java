package owl.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;


public class OwlLoggerFactory implements LoggerFactory
{
	public OwlLoggerFactory()
	{
	}

	public Logger makeNewLoggerInstance( String name )
	{
		return new OwlLogger( name );
	}
}
