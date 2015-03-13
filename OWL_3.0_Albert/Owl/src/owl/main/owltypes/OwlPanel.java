package owl.main.owltypes;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


public class OwlPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2837654172040204258L;

	//--------------------------------------------------------------------------
    //   Protected Variables:
    //--------------------------------------------------------------------------
	protected GridBagConstraints gbc;
	protected GridBagLayout      gbl;

	public OwlPanel()
	{
		this( null );
	}

	public OwlPanel( String borderTitle )
	{
		if ( borderTitle != null )
		{
			setBorder( new TitledBorder( borderTitle ) );
		}

		setName( borderTitle );

		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();
		setLayout( gbl );
	}

	public void actionPerformed( ActionEvent e )
	{
		savePreferences();
	}

    //--------------------------------------------------------------------------
    //   Protected Methods:
    //--------------------------------------------------------------------------
	protected void loadPreferences() {}
	protected void savePreferences() {}

	protected void addComponent( JPanel panel, JComponent comp, int insetTOP,
								 int insetLEFT, int insetBOTTOM, int insetRIGHT,
								 int fill, int anchr, int row, int col,
								 int rowSpan, int colSpan )
	{
		// Set the grid layout constraints.
		gbc.fill = fill;
		gbc.anchor = anchr;
		gbc.insets = new Insets( insetTOP, insetLEFT, insetBOTTOM, insetRIGHT );
		gbc.gridx = col;
		gbc.gridy = row;
		gbc.gridwidth = colSpan;
		gbc.gridheight = rowSpan;
		
		// Add the component to the panel.
		gbl.setConstraints( comp, gbc );
		panel.add( comp );
	}

	protected void addComponent( JPanel panel, JComponent comp, int insetTOP,
								 int insetLEFT, int insetBOTTOM, int insetRIGHT,
								 int anchr, int row, int col, int rowSpan,
								 int colSpan )
	{
  		// Set the grid layout constraints.
  		gbc.fill = GridBagConstraints.NONE;
  		gbc.anchor = anchr;
  		gbc.insets = new Insets( insetTOP, insetLEFT, insetBOTTOM, insetRIGHT );
  		gbc.gridx = col;
  		gbc.gridy = row;
  		gbc.gridwidth = colSpan;
  		gbc.gridheight = rowSpan;

  		// Add the component to the panel.
  		gbl.setConstraints( comp, gbc );
  		panel.add( comp );
	}

	protected void addComponent( JComponent comp, int insetTOP, int insetLEFT,
								 int insetBOTTOM, int insetRIGHT, int anchr,
								 int row, int col, int rowSpan, int colSpan )
	{
		addComponent( comp, insetTOP, insetLEFT, insetBOTTOM, insetRIGHT,
					  GridBagConstraints.NONE, anchr, row, col, rowSpan,
					  colSpan );
	}

	protected void addComponent( JComponent comp, int insetTOP, int insetLEFT,
								 int insetBOTTOM, int insetRIGHT, int row,
								 int col, int rowSpan, int colSpan )
	{
		addComponent( comp, insetTOP, insetLEFT, insetBOTTOM, insetRIGHT,
					  GridBagConstraints.NONE, GridBagConstraints.NORTHWEST,
					  row, col, rowSpan, colSpan );
	}

	protected void addComponent( JComponent comp, int insetTOP, int insetLEFT,
								 int insetBOTTOM, int insetRIGHT, int fill,
								 int anchr, int row, int col, int rowSpan,
								 int colSpan )
	{
      		// Set the grid layout constraints.
      		gbc.fill = fill;
      		gbc.anchor = anchr;
      		gbc.insets = new Insets( insetTOP, insetLEFT, insetBOTTOM, insetRIGHT );
      		gbc.gridx = col;
      		gbc.gridy = row;
      		gbc.gridwidth = colSpan;
      		gbc.gridheight = rowSpan;

      		// Add the component to the panel.
      		gbl.setConstraints( comp, gbc );
      		add( comp );
	}

	protected int getIntegerField( String str ) throws Exception
	{
		int val = 0;

		try
		{
			val = Integer.parseInt( str );
		}
		catch ( NumberFormatException nfe )
		{
			throw new Exception( "Invalid parameter: " + str );
		}

		return val;
	}

	protected double getDoubleField( String str ) throws Exception
	{
		double val = 0;

		try
		{
			val = Double.parseDouble( str );
		}
		catch ( NumberFormatException nfe )
		{
			throw new Exception( "Invalid parameter: " + str );
		}

		return val;
	}
}
