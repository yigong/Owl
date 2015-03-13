package owl.main.owltypes;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import owl.main.MainApp;


public class OwlNumberField extends OwlTextField implements KeyListener
{
	private static final long serialVersionUID = -7830988246468174355L;

	private boolean m_bHideErrors;
	private boolean m_bNumbersOnly;
	private double  m_gInvalidValue;

	public OwlNumberField( int dLength )
	{
		super( dLength );

		addKeyListener( this );

		m_bHideErrors   = false;
		m_bNumbersOnly  = true;
		m_gInvalidValue = 0.0;
	}

	public OwlNumberField( int dValue, int dLength )
	{
		super( Integer.toString( dValue ), dLength );

		addKeyListener( this );

		m_bHideErrors   = false;
		m_bNumbersOnly  = true;
		m_gInvalidValue = 0.0;
	}

	public OwlNumberField( double gValue, int dLength )
	{
		super( Double.toString( gValue ), dLength );

		addKeyListener( this );

		m_bHideErrors   = false;
		m_bNumbersOnly  = true;
		m_gInvalidValue = 0.0;
	}

	public OwlNumberField( String sText, int dLength )
	{
		super( sText, dLength );

		addKeyListener( this );

		m_bHideErrors   = false;
		m_bNumbersOnly  = true;
		m_gInvalidValue = 0.0;
	}
	
	public boolean isEmpty()
	{
		return super.getText().isEmpty();
	}

	public void setNumbersOnly( boolean bEnabled )
	{
		m_bNumbersOnly = bEnabled;
	}

	public void setInvalidValue( double gVal )
	{
		m_gInvalidValue = gVal;
	}

	public void setValue( int dVal )
	{
		super.setText( Integer.toString( dVal ) );
	}

	public void setValue( long lVal )
	{
		super.setText( Long.toString( lVal ) );
	}

	public void setValue( float fVal )
	{
		super.setText( Float.toString( fVal ) );
	}

	public void setValue( double gVal )
	{
		super.setText( Double.toString( gVal ) );
	}

	//  NOTE: The format MUST be of the form "%.Xf", where
	//        X is the decimal precision ( 1, 2, 3, etc ).
	// +---------------------------------------------------+
	public void setValue( double gVal, String sFormat )
	{
		super.setText( String.format( sFormat, gVal ) );
	}

	public void hideErrors( boolean bHideErrors )
	{
		m_bHideErrors = bHideErrors;
	}

	public int getInt()
	{
		return getInt( 10 );
	}

	//  NOTE: In JAVA 16-bit is signed: -32K to +32K
	// +---------------------------------------------------+
	public int getInt( int radix )
	{
		int dVal = 0;

		try
		{
			dVal = Integer.parseInt( super.getText(), radix );
		}
		catch ( Exception e )
		{
			if ( !m_bHideErrors )
			{
				MainApp.error( "( OwlNumberField ): Failed to parse INTEGER: "
							   + super.getText() + " ( radix: " + radix + " )" );
			}

			dVal = ( int )m_gInvalidValue;
		}

		return dVal;
	}

	public long getLong()
	{
		long lVal = 0;

		try
		{
			lVal = Long.parseLong( super.getText() );
		}
		catch ( Exception e )
		{
			if ( !m_bHideErrors )
			{
				MainApp.error( "( OwlNumberField ): Failed to parse LONG value" );
			}

			lVal = ( long )m_gInvalidValue;
		}

		return lVal;
	}

	public float getFloat()
	{
		float fVal = 0.f;

		try
		{
			fVal = Float.parseFloat( super.getText() );
		}
		catch ( Exception e )
		{
			if ( !m_bHideErrors )
			{
				MainApp.error( "( OwlNumberField ): Failed to parse FLOAT value" );
			}

			fVal = ( float )m_gInvalidValue;
		}

		return fVal;
	}

	public double getDouble()
	{
		double gVal = 0.0;

		try
		{
			gVal = Double.parseDouble( super.getText() );
		}
		catch ( Exception e )
		{
			if ( !m_bHideErrors )
			{
				MainApp.error( "( OwlNumberField ): Failed to parse DOUBLE value" );
			}

			gVal = m_gInvalidValue;
		}

		return gVal;
	}

	@Override
	public void keyPressed( KeyEvent ke )
	{
	}

	@Override
	public void keyReleased( KeyEvent ke )
	{
	}

	@Override
	public void keyTyped( KeyEvent ke )
	{
		if ( !m_bNumbersOnly ) { return; }

		char c = ke.getKeyChar();

		if ( !Character.isDigit( c ) && !isHexDigit( c ) && !isDecimalChar( c ) )
		{
			ke.consume();
		}
	}

	protected boolean isDecimalChar( char c )
	{
		if ( c == '.' || c == '-' || c == '+' )
		{
			return true;
		}

		return false;
	}

	protected boolean isHexDigit( char c )
	{
		int dChar = ( int )c;

		if ( ( dChar >= 0x41 && dChar <= 0x46 ) || ( dChar >= 0x61 && dChar <= 0x66 ) )
		{
			return true;
		}
		
		return false;
	}
}
