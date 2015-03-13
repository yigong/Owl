package owl.main.owltypes;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.JLabel;


public class OwlNumberLabel extends JLabel
{
	private static final long serialVersionUID = 5904207455079328329L;
	private final int FIXED_WIDTH	= 90;

	private String        m_sText;
	private double        m_gNumber;
	private boolean       m_bIsBold;
	private Color         m_color;
	private DecimalFormat m_formatter;

	// +----------------------------------------------------------------+
	//   Constructors
	// +----------------------------------------------------------------+
	public OwlNumberLabel( String sText, double gNumber, Color color, boolean bIsBold )
	{
		super();

		m_sText		= sText;
		m_gNumber	= gNumber;
		m_bIsBold	= bIsBold;
		m_color		= color;

		m_formatter	= new DecimalFormat( "#######0.00" );

		super.setText( "<html>" + formatStyle( sText, color, bIsBold ) +
			            m_formatter.format( gNumber ) + "</html>" );

		setPreferredSize(
				new java.awt.Dimension( FIXED_WIDTH,
						  				getPreferredSize().height ) );
	}

	public OwlNumberLabel( String sText, float fNumber, Color color, boolean bIsBold )
	{
		super();

		m_sText		= sText;
		m_gNumber	= fNumber;
		m_bIsBold	= bIsBold;
		m_color		= color;

		m_formatter = new DecimalFormat( "#######0.00" );

		super.setText( "<html>" + formatStyle( sText, color, bIsBold ) +
		   		        m_formatter.format( fNumber ) + "</html>" );

		setPreferredSize(
				new java.awt.Dimension( FIXED_WIDTH,
						  				getPreferredSize().height ) );
	}

	public OwlNumberLabel( String sText, long lNumber, Color color, boolean bIsBold )
	{
		super();

		m_sText   = sText;
		m_gNumber = lNumber;
		m_bIsBold = bIsBold;
		m_color   = color;

		super.setText( "<html>" + formatStyle( sText, color, bIsBold ) +
		   				Long.toString( lNumber ) + "</html>" );

		setPreferredSize(
				new java.awt.Dimension( FIXED_WIDTH,
						  				getPreferredSize().height ) );
	}

	public OwlNumberLabel( String sText, int dNumber, Color color, boolean bIsBold )
	{
		super();

		m_sText   = sText;
		m_gNumber = dNumber;
		m_bIsBold = bIsBold;
		m_color   = color;

		super.setText( "<html>" + formatStyle( sText, color, bIsBold ) +
		   				Integer.toString( dNumber ) + "</html>" );

		setPreferredSize(
				new java.awt.Dimension( FIXED_WIDTH,
						  				getPreferredSize().height ) );
	}

	public OwlNumberLabel( String sText, double gNumber, Color color )
	{
		this( sText, gNumber, color, false );
	}

	public OwlNumberLabel( String sText, float fNumber, Color color )
	{
		this( sText, fNumber, color, false );
	}

	public OwlNumberLabel( String sText, long lNumber, Color color )
	{
		this( sText, lNumber, color, false );
	}

	public OwlNumberLabel( String sText, int dNumber, Color color )
	{
		this( sText, dNumber, color, false );
	}

	public OwlNumberLabel( String sText, double gNumber )
	{
		this( sText, gNumber, Color.BLACK, true );
	}

	public OwlNumberLabel( String sText, float fNumber )
	{
		this( sText, fNumber, Color.BLACK, true );
	}

	public OwlNumberLabel( String sText, long lNumber )
	{
		this( sText, lNumber, Color.BLACK, true );
	}

	public OwlNumberLabel( String sText, int dNumber )
	{
		this( sText, dNumber, Color.BLACK, true );
	}

	public void setFixedWidth( int dWidth )
	{
		setPreferredSize(
				new java.awt.Dimension( dWidth,
						  				getPreferredSize().height ) );
	}

	// +----------------------------------------------------------------+
	//   Set number methods
	// +----------------------------------------------------------------+
	public void setNumber( double gNumber )
	{
		super.setText( "<html>" + formatStyle( m_sText, m_color, m_bIsBold ) +
						m_formatter.format( gNumber ) + "</html>" );

		m_gNumber = gNumber;
	}

	public void setNumber( float fNumber )
	{
		super.setText( "<html>" + formatStyle( m_sText, m_color, m_bIsBold ) +
						m_formatter.format( fNumber ) + "</html>" );

		m_gNumber = fNumber;
	}

	public void setNumber( long lNumber )
	{
		super.setText( "<html>" + formatStyle( m_sText, m_color, m_bIsBold ) +
   						Long.toString( lNumber ) + "</html>" );

		m_gNumber = lNumber;
	}

	public void setNumber( int dNumber )
	{
		super.setText( "<html>" + formatStyle( m_sText, m_color, m_bIsBold ) +
   						Integer.toString( dNumber ) + "</html>" );

		m_gNumber = dNumber;
	}

	// +----------------------------------------------------------------+
	//   Get number methods
	// +----------------------------------------------------------------+
	public double doubleValue()
	{
		return m_gNumber;
	}

	public double floatValue()
	{
		return ( float )m_gNumber;
	}

	public double longValue()
	{
		return ( long )m_gNumber;
	}

	public double intValue()
	{
		return ( int )m_gNumber;
	}

	// +----------------------------------------------------------------+
	//   Private methods
	// +----------------------------------------------------------------+
	private String formatStyle( String sText, Color color, boolean bIsBold )
	{
		return ( ( bIsBold ? "<b>" : "" ) + "<font color=\"#" +
				 getColorString( color ) + "\">" + sText + "</font>" +
				 ( bIsBold ? "</b>" : "" ) + ": ");
	}

	private String getColorString( Color color )
	{
		return ( Integer.toHexString( color.getRed() ) +
				 Integer.toHexString( color.getGreen() ) +
				 Integer.toHexString( color.getBlue() ) );
	}
}
