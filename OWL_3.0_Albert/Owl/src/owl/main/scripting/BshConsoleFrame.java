package owl.main.scripting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import owl.gui.utils.OwlUtilities;
import owl.main.MainApp;
import owl.main.owltypes.OwlFrame;

import bsh.Interpreter;
import bsh.util.JConsole;


public class BshConsoleFrame extends OwlFrame implements ActionListener
{
	private static final long serialVersionUID = 3209543809866802920L;
	private Interpreter interpreter;
	private Thread thread;

	public BshConsoleFrame()
	{
		super( "Owl Script Console", true );

		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		setIconImage( Toolkit.getDefaultToolkit().getImage( MainApp.getBitmapPath() + "TinyBean.gif" ) );
		getContentPane().setLayout( new BorderLayout() );

		super.addComponent( createToolbar(), super.TOOLBAR_INDEX );
		super.addComponent( createConsolePanel(), super.CENTER_CONTAINER_INDEX );

		pack();
		OwlUtilities.centerFrame( this );
	}

	//--------------------------------------------------------------------------
    //   Public Methods:
    //--------------------------------------------------------------------------
	@Override
	public void actionPerformed( ActionEvent e )
	{
		super.actionPerformed( e );
	}

	public void refreshInterpreterInfo()
	{
		MainInterpreter.setStdInterpreterInfo( interpreter );
	}

	//--------------------------------------------------------------------------
    //   Private Methods:
    //--------------------------------------------------------------------------
	private JToolBar createToolbar()
	{
		JToolBar toolbar = new JToolBar();

		toolbar.setFloatable( false );
		super.appendToolbar( toolbar );

		return toolbar;
	}

	private JPanel createConsolePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout( new BorderLayout() );

		JConsole bshConsole = new JConsole();
		bshConsole.setPreferredSize( new Dimension( 800, 400 ) );

		panel.add( bshConsole, BorderLayout.CENTER );

		interpreter = new Interpreter( bshConsole );
		MainInterpreter.setStdInterpreterInfo( interpreter );

		thread = new Thread( interpreter );
		thread.start();

		return panel;
	}
}
