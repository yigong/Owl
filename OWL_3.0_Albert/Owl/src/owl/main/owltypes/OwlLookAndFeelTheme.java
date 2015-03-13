package owl.main.owltypes;

import java.awt.Font;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;


public class OwlLookAndFeelTheme extends DefaultMetalTheme
{
	private Font font;

	public OwlLookAndFeelTheme()
	{
		super();
		font = new Font( "Comic Sans MS", Font.PLAIN, 11 );
//		font = new Font( Font.SANS_SERIF, Font.PLAIN, 11 );
//		font = new Font( "MS Sans Serif", Font.PLAIN, 11 );
	}

	@Override
	public FontUIResource getControlTextFont()
	{
		return new FontUIResource( font );
	}

	@Override
	public FontUIResource getMenuTextFont()
	{
		return new FontUIResource( font );
	}

	@Override
	public FontUIResource getSubTextFont()
	{
		return new FontUIResource( font );
	}

	@Override
	public FontUIResource getSystemTextFont()
	{
		return new FontUIResource( font );
	}
	            
	@Override
	public FontUIResource getUserTextFont()
	{
		return new FontUIResource( font );
	}
	            
	@Override
	public FontUIResource getWindowTitleFont()
	{
		return new FontUIResource( font );
	}

	@Override
	public ColorUIResource getFocusColor()
	{
		return new ColorUIResource( new java.awt.Color( 221, 221, 221 ) );
	}

	@Override
	protected ColorUIResource getPrimary1()
	{
		return new ColorUIResource( new java.awt.Color( 90, 90, 90 ) );
//		return new ColorUIResource( java.awt.Color.yellow );
	}

	@Override
	protected ColorUIResource getPrimary2()
	{
		return new ColorUIResource( new java.awt.Color( 203, 203, 153 ) );
//		return new ColorUIResource( java.awt.Color.orange );
	}

	@Override
	protected ColorUIResource getPrimary3()
	{
		return new ColorUIResource( new java.awt.Color( 255, 255, 128 ) );
//		return new ColorUIResource( java.awt.Color.red );
	}




//	@Override
//	protected ColorUIResource getSecondary1()
//	{
//		return new ColorUIResource( java.awt.Color.green );
//	}
//
//	@Override
//	protected ColorUIResource getSecondary2()
//	{
//		return new ColorUIResource( java.awt.Color.green );
//	}
//
//	@Override
//	protected ColorUIResource getSecondary3()
//	{
//		return new ColorUIResource( java.awt.Color.black );
//	}
//
//	@Override
//	protected ColorUIResource getWhite()
//	{
//		return new ColorUIResource( java.awt.Color.black );
//	}
//
//	@Override
//	protected ColorUIResource getBlack()
//	{
//		return new ColorUIResource( java.awt.Color.white );
//	}
}
