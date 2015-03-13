package owl.ptc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import owl.gui.utils.PrintUtilities;
import owl.img.analysis.ImageDifStats;
import owl.img.analysis.ImageStats;
import owl.main.MainApp;



public class PTCDataDisplay extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 4345933109929435520L;

	private final String PRINT_ACTION	=	"PRINT";
	private final String SAVE_ACTION	=	"SAVE";
	private final String CLOSE_ACTION	=	"CLOSE";

	private JTextPane textPane;
	private DefaultStyledDocument doc;
	private DecimalFormat df;
	private JCheckBox autoClearChkbox;


	public PTCDataDisplay()
	{
		super();
		super.setPreferredSize( new java.awt.Dimension( 400, 600 ) );

		super.setIconImage(
				Toolkit.getDefaultToolkit().createImage(
						MainApp.getBitmapPath() + "PTC2.gif" ) );

		df = new DecimalFormat( "##0.#####E0" );

		StyleContext sc = new StyleContext();
		doc = new DefaultStyledDocument( sc );

	    Style titleStyle = sc.addStyle( "BOLD", null );
	    titleStyle.addAttribute( StyleConstants.Foreground, Color.black );
	    titleStyle.addAttribute( StyleConstants.Bold, new Boolean( true ) );

	    Style infoStyle = sc.addStyle( "INFO", null );
	    infoStyle.addAttribute( StyleConstants.Foreground, Color.black );

	    Style errorStyle = sc.addStyle( "ERROR", null );
	    errorStyle.addAttribute( StyleConstants.Foreground, Color.red );

	    Style warningStyle = sc.addStyle( "WARNING", null );
	    warningStyle.addAttribute( StyleConstants.Foreground, Color.orange );
	    warningStyle.addAttribute( StyleConstants.Bold, new Boolean( true ) );

		textPane = new JTextPane( doc );
		textPane.setEditable( false );

		JScrollPane scrollPane = new JScrollPane(
									textPane,
									ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
									ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable( false );

		JButton printButton = new JButton( PRINT_ACTION );
		printButton.setBorderPainted( false );
		printButton.setFocusPainted( false );
		printButton.addActionListener( this );
		toolbar.add( printButton );

		JButton saveButton = new JButton( SAVE_ACTION );
		saveButton.setBorderPainted( false );
		saveButton.setFocusPainted( false );
		saveButton.addActionListener( this );
		toolbar.add( saveButton );

		JButton closeButton = new JButton( CLOSE_ACTION );
		closeButton.setBorderPainted( false );
		closeButton.setFocusPainted( false );
		closeButton.addActionListener( this );
		toolbar.add( closeButton );

		toolbar.addSeparator();

		autoClearChkbox = new JCheckBox( "AUTO CLEAR", false );
		autoClearChkbox.setBorderPainted( false );
		autoClearChkbox.setFocusPainted( false );

		JPanel panel = new JPanel();
		panel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
		panel.add( autoClearChkbox );
		toolbar.add( panel );

		getContentPane().setLayout( new BorderLayout() );
		getContentPane().add( toolbar, BorderLayout.PAGE_START );
		getContentPane().add( scrollPane, BorderLayout.CENTER );

		pack();
	}

	public void actionPerformed( ActionEvent event )
	{
		if ( event.getActionCommand().equals( PRINT_ACTION ) )
		{
			( new PrintUtilities() ).printComponent( textPane );
		}

		else if ( event.getActionCommand().equals( SAVE_ACTION ) )
		{
			JFileChooser chooser = new JFileChooser();
			int selection = chooser.showSaveDialog( this );

			if ( selection == JFileChooser.APPROVE_OPTION )
			{
				try
				{
					FileOutputStream outFile = new FileOutputStream( chooser.getSelectedFile().getCanonicalPath() );
					outFile.write( textPane.getText().getBytes() );
					outFile.close();
				}
				catch ( Exception e )
				{
					e.toString();
				}
			}
		}

		else if ( event.getActionCommand().equals( CLOSE_ACTION ) )
		{
			super.dispose();
		}
	}

	public void addDarkData( ImageStats stats )
	{
		try
		{
			append( "  --------------------- DARK ---------------------\n", true );
			append( "  Mean: ", stats.gMean );
			append( "  Standard Deviation: ", stats.gStdDev, "\n\n" );
		}
		catch ( NullPointerException npe ) {}
		catch ( Exception e )
		{
			System.err.println( e.toString() );
		}
	}

	public void addFlatData( int flatPair, ImageDifStats stats, float counts, float variance )
	{
		float ratio = 0.0f;
		if ( variance != 0.0 ) ratio = counts / variance;

		try
		{
			append( "  ----------------- FLAT " + flatPair + " and " + ( flatPair + 1 ) + " ------------------\n", true );

			append( "  Frame #" + flatPair );
			append( "  Mean: ", stats.img1Stats.gMean );
			append( "  Standard Deviation: ", stats.img1Stats.gStdDev, "\n" );

			append( "  Frame #" + ( flatPair + 1 ) );
			append( "  Mean: ", stats.img2Stats.gMean );
			append( "  Standard Deviation: ", stats.img2Stats.gStdDev, "\n" );

			append( "  Difference Mean: ", stats.imgDifStats.gMean );
			append( "  Standard Deviation: ", stats.imgDifStats.gStdDev, "\n" );

			append( "  Counts: ", counts );
			append( "  Variance: ", variance );
			append( "  Ratio: ", ratio, "\n" );

			if ( stats.img1Stats.gSaturatedPixCnt > 0 )
			{
				appendWarning( " WARNING: Frame #" +
							   flatPair +
							   "Saturated Pixel Count: " +
							   stats.img1Stats.gSaturatedPixCnt );
			}

			if ( stats.img2Stats.gSaturatedPixCnt > 0 )
			{
				appendWarning( " WARNING: Frame #" +
						 	   ( flatPair + 1 ) +
						 	   " Saturated Pixel Count: " +
						 	   stats.img2Stats.gSaturatedPixCnt );
			}

			append( "\n" );
		}
		catch ( NullPointerException npe ) {}
		catch ( Exception e )
		{
			System.err.println( e.toString() );
		}
	}

	public void clear()
	{
		if ( autoClearChkbox.isSelected() )
		{
			try
			{
				textPane.getDocument().remove( 0, textPane.getDocument().getLength() );
			}
			catch ( BadLocationException ble )
			{
				textPane = new JTextPane( doc );
				textPane.setEditable( false );
			}
		}
	}

	@Override
	public void setTitle( String title )
	{
		super.setTitle( title );
	}

	private void appendWarning( String text ) throws Exception
	{
		doc.insertString( doc.getLength(),
						  text,
						  doc.getStyle( "WARNING" ) );

		setCaretPosition();
	}

	private void append( String text ) throws Exception
	{
		append( text, false );
	}

	private void append( String text, boolean isBold ) throws Exception
	{
		doc.insertString(
				doc.getLength(),
				text,
				( isBold == true ? doc.getStyle( "BOLD" ): doc.getStyle( "INFO" ) ) );

		setCaretPosition();
	}

	private void append( String label, double value ) throws Exception
	{
		append( label, value, null );
	}

	private void append( String label, double value, String appendEnd ) throws Exception
	{
		if ( value > 0 )
		{
			doc.insertString( doc.getLength(),
							  label + df.parse( df.format( value ) ),
							  doc.getStyle( "INFO" ) );
		}

		else
		{
			doc.insertString( doc.getLength(),
							  label,
							  doc.getStyle( "INFO" ) );

			doc.insertString( doc.getLength(),
							  "" + df.parse( df.format( value ) ),
							  doc.getStyle( "ERROR" ) );
		}

		if ( appendEnd != null )
		{
			doc.insertString( doc.getLength(), appendEnd, doc.getStyle( "INFO" ) );
		}

		setCaretPosition();
	}

	private void setCaretPosition()
	{
		try
		{
			textPane.setCaretPosition( doc.getLength() );
			textPane.moveCaretPosition( doc.getLength() );
		}
		catch ( IllegalArgumentException iae ) {}
	}
}
