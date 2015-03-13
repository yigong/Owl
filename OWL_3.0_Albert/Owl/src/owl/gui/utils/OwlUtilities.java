package owl.gui.utils;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import owl.main.MainApp;



// +--------------------------------------------------------------------------------------------+
// |  OwlUtilities Class                                                                        |
// +--------------------------------------------------------------------------------------------+
// |  Provides a wide variety of utility functions for all of Owl.                              |
// +--------------------------------------------------------------------------------------------+
public class OwlUtilities
{
	// +----------------------------------------------------------------------------------------+
	// |  asciiToInt                                                                            |
	// +----------------------------------------------------------------------------------------+
	// |  Converts the specified ASCII string to an integer of the specifed radix.  This is     |
	// |  primarily used to convert ASCII commands, such as 'TDL', to its hex value (0x54444C). |
	// |                                                                                        |
	// |  @param str - The ASCII string to convert.                                             |
	// |  @param radix - The radix to convert the string to. 10=decimal, 16=hexadecimal, etc.   |
	// +----------------------------------------------------------------------------------------+
	public static int asciiToInt( String str, int radix ) throws NumberFormatException, Exception
	{
		byte[] byteValues = { '0', '0', '0' };
		int retVal = 0;

		if ( str.equals( "" ) ) { return -1; }

		if ( str.charAt( 0 ) == '-' )
		{
			throw new Exception( "Negative numbers not allowed!" );
		}

		try
		{
			byteValues = str.toUpperCase().getBytes( "US-ASCII" );
		}
		catch ( java.io.UnsupportedEncodingException nee )
		{
			MainApp.error( nee.getMessage() );
		}

		//  First try to parse the input as an ASCII command
		// +-------------------------------------------------+
		if ( byteValues.length == 3 &&
			 isAsciiCharacter( byteValues[ 0 ] ) &&
			 isAsciiCharacter( byteValues[ 1 ] ) &&
			 isAsciiCharacter( byteValues[ 2 ] ) )
		{
			for ( int i=0; i<byteValues.length; i++ )
			{
				retVal |= byteValues[ i ] << ( byteValues.length - 1 - i ) * 8;
			}
		 }

		//  Then try to parse the input as a standard number
		// +-------------------------------------------------+
		else
		{
			try
			{
				retVal = Integer.parseInt( str, radix );
			}
			catch ( NumberFormatException nfe )
			{
				throw new NumberFormatException(
									"Failed to parse " + str +
									" as base " + radix + " value!" );
			}
		}

		return retVal;
	}

	// +----------------------------------------------------------------------------------------+
	// |  quotedAsciiToInt                                                                      |
	// +----------------------------------------------------------------------------------------+
	// |  Converts the specified ASCII string to an integer of the specifed radix.  The         |
	// |  difference between this method and the asciiToInt method is that is one requires all  |
	// |  ASCII strings to be quoted with ' or ".  This method is primarily used to convert     |
	// |  ASCII command arguments, such as '__R', to its equivalent integer value.  Any         |
	// |  non-quoted string parameters are expected to be non-ASCII integers and are converted  |
	// |  as such ( if possible ).                                                              |
	// |                                                                                        |
	// |  @param str - The quoted ASCII string to convert.                                      |
	// |  @param radix - The radix to convert the string to. 10=decimal, 16=hexadecimal, etc.   |
	// +----------------------------------------------------------------------------------------+
	public static int quotedAsciiToInt( String str, int radix ) throws NumberFormatException, Exception
	{
		byte[] byteValues = { '0', '0', '0' };
		int retVal = 0;

		if ( str.isEmpty() || str.equals( "" ) ) { return -1; }

		//  First try to parse the input as an ASCII string
		// +-------------------------------------------------+
		if ( str.contains( "\"" ) || str.contains( "\'" ) )
		{
			str = str.replace( '\'', ' ' );
			str = str.replace( '\"', ' ' );
			str = str.trim();

			try
			{
				byteValues = str.toUpperCase().getBytes( "US-ASCII" );
			}
			catch ( java.io.UnsupportedEncodingException nee )
			{
				MainApp.error( nee.getMessage() );
			}

			if ( byteValues.length == 3 &&
				 isAsciiCharacter( byteValues[ 0 ] ) &&
				 isAsciiCharacter( byteValues[ 1 ] ) &&
				 isAsciiCharacter( byteValues[ 2 ] ) )
			{
				for ( int i=0; i<byteValues.length; i++ )
				{
					retVal |= byteValues[ i ] << ( byteValues.length - 1 - i ) * 8;
				}
			 }
			else
			{
				throw new NumberFormatException(
									"Failed to parse \"" + str +
									"\" as valid ASCII argument [ _, A-Z, a-z, 0-9 ]!" );				
			}
		}

		//  Then try to parse the input as a standard number
		// +-------------------------------------------------+
		else
		{
			if ( str.charAt( 0 ) == '-' )
			{
				throw new Exception( "Negative numbers not allowed!" );
			}

			try
			{
				retVal = Integer.parseInt( str, radix );
			}
			catch ( NumberFormatException nfe )
			{
				throw new NumberFormatException(
									"Failed to parse " + str +
									" as base " + radix + " value!" );
			}
		}

		return retVal;
	}

	// +----------------------------------------------------------------------------------------+
	// |  isAsciiCharacter                                                                      |
	// +----------------------------------------------------------------------------------------+
	// |  Tests whether the specified byte is one of the following ASCII characters:            |
	// |  _, A-Z, a-z, or 0-9.  Returns true if the character is found; false otherwise.        |
	// |                                                                                        |
	// |  @param value - The ASCII byte to check         .                                      |
	// +----------------------------------------------------------------------------------------+
	private static boolean isAsciiCharacter( byte value )
	{
		boolean retVal = false;

		//  Accept '_'
		// +---------------------------------------------------+
		if ( value == 0x5F ) { retVal = true; }

		//  Accept "A-Z"
		// +---------------------------------------------------+
		if ( value >= 0x41 && value <= 0x5A ) { retVal = true; }

		//  Accept "a-z"
		// +---------------------------------------------------+
		if ( value >= 0x61 && value <= 0x7A ) { retVal = true; }

		//  Accept "0-9"
		// +---------------------------------------------------+
		if ( value >= 0x30 && value <= 0x39 ) { retVal = true; }

		return retVal;
	}

	// +----------------------------------------------------------------------------------------+
	// |  intToAscii                                                                            |
	// +----------------------------------------------------------------------------------------+
	// |  Performs the inverse conversion of the asciiToInt method.  Here the specified integer |
	// |  value is converted into a three character ASCII value.  If no conversion is possible, |
	// |  then a hexadecimal version of the number is returned as a string.                     |
	// |                                                                                        |
	// |  @param value - The integer to convert to an ASCII string.  The ASCII string MUST be   |
	// |                 in uppercase (0x41-0x5A) and may contain the numbers 0-9 (0x30-0x39 ). |
	// +----------------------------------------------------------------------------------------+
	public static String intToAscii( int value )
	{
		char[] charVals = { ' ', ' ', ' ', ' ' };	// Assumes int is 4 bytes
		int byteMask = 0x000000FF;

		for ( int i=charVals.length-1; i>=0; i-- )
		{
			charVals[ i ] = ( char )( ( value & byteMask ) >> ( ( charVals.length - 1 - i ) * 8 ) );

			if ( ( charVals[ i ] < 0x41 || charVals[ i ] > 0x5A ) &&
				 ( charVals[ i ] < 0x30 || charVals[ i ] > 0x39 ) )
			{
				charVals[ i ] = ' ';
			}

			byteMask = byteMask << 8;
		}

		// Make sure the reply is at least 3 chars long, or it's definitely
		// invalid.
		String str = new String( charVals ).trim();
		if ( str.length() < 3 ) { str = ""; }
		else {  str = "'" + str + "'"; }

		// If the conversion failed, just return the value
		if ( str.equals( "" ) || str.equals( "''" ) )
		{
			str = "0x" + Integer.toHexString( value );
		}

		return str;
	}

	// +----------------------------------------------------------------------------------------+
	// |  FilenameIncr                                                                          |
	// +----------------------------------------------------------------------------------------+
	// |  Increments the specified filename by increasing the number attached to the end of the |
	// |  filename, but before the .extension.  If no number exists in the filename, then a "0" |
	// |  is appended.  For example, "Image.fit" becomes "Image0.fit" and "Image57.fit" becomes |
	// |  "Image58.fit".                                                                        |
	// |                                                                                        |
	// |  @param filename - The string filename to increment.                                   |
	// +----------------------------------------------------------------------------------------+
	public static String FilenameIncr( String filename )
	{
		String file = ( new File( filename ) ).getName();
		String prefix = null;
		String suffix = null;
		StringBuffer incrementString = new StringBuffer( "0" );
		char[] charString = file.toCharArray();
		int incrementStartLength = 0;
		int incrementStartIndex = 0;
		int increment = 0;
		int length = 0;
		int diff = 0;

		// Get the index of the period (.).
		int dotIndex = file.lastIndexOf( "." );

		// Handle filenames with no extension, Example: m1dark1
		if ( dotIndex == -1 )
		{
			length = file.length() - 1;
			dotIndex = length + 1;
		}

		// Handle filenames with an extension, Example: m1dark1.fit
		else
		{
			length = file.length() - ( file.substring( dotIndex, file.length() ).length() );
			suffix = file.substring( dotIndex, file.length() );		
		}

		// Find the start index of the increment number. If the first character is not
		// a number, then set the start index to the period index.
		if ( charString[ dotIndex - 1 ] >= 0x30 && charString[ dotIndex - 1 ] <= 0x39 )
		{
			for ( int i=length; i>=1; i-- )
			{
				if ( ( charString[ i ] >= 0x30 && charString[ i ] <= 0x39 ) &&
   	   			     ( charString[ i-1 ] < 0x30 || charString[ i-1 ] > 0x39 ) )
				{
   					incrementStartIndex = i;
   					break;
				}
			}
		}
		else
			incrementStartIndex = dotIndex;

		// Parse the increment number String -> int. Also, handle the case of multiple zeros
		// in the image filename, i.e. preserve Test00004.fit the zeros.
		try
		{
			incrementStartLength = file.substring( incrementStartIndex, dotIndex ).length();
			increment = Integer.parseInt( file.substring( incrementStartIndex, dotIndex ) );
			++increment;

			diff = incrementStartLength - Integer.toString( increment ).length();

			if ( diff > 0 )
			{
				for ( int k=1; k<diff; k++ )
				{
					incrementString.append( "0" );
				}

				incrementString.append( Integer.toString( increment ) );
			}
			else
			{
				incrementString.delete( 0, incrementString.length() );
				incrementString.insert( 0, Integer.toString( increment ) );
			}
		}
		catch ( NumberFormatException nfe ) {}

		// Get the filename prefix, Example: prefix(m1dark1.fit) = m1dark
		prefix = file.substring( 0, incrementStartIndex );

		// Set the new image filename. Handle both cases, with and without file extension.
		if ( suffix != null )
		{
			file = prefix.concat( incrementString.toString() ).concat( suffix );
		}
		else
		{
			file = prefix.concat( incrementString.toString() );
		}

		return file;
	}

	// +----------------------------------------------------------------------------------------+
	// |  FilenameIncr                                                                          |
	// +----------------------------------------------------------------------------------------+
	// |  Convenience method to process a File object instead of a String.  The return value is |
	// |  still a String, but the input object is a File. See FilenameIncr for details.         |
	// |                                                                                        |
	// |  @param file - The File object to increment the name for.                              |
	// +----------------------------------------------------------------------------------------+
	public static String FilenameIncr( File file )
	{
		String fname = file.getParent() +
					   System.getProperty( "file.separator" ) +
					   OwlUtilities.FilenameIncr( file.getName() );

		return fname;
	}

	// +----------------------------------------------------------------------------------------+
	// |  grep                                                                                  |
	// +----------------------------------------------------------------------------------------+
	// |  Searches the file for the specified Pattern.  Returns true if the file contents       |
	// |  contain the pattern; returns false otherwise.                                         |
	// |                                                                                        |
	// |  @param filename - The file to search.                                                 |
	// |  @param searchPattern - The search pattern.                                            |
	// +----------------------------------------------------------------------------------------+
   public static boolean grep( String filename, String searchPattern ) throws IOException, PatternSyntaxException
    {
    	boolean retVal = false;

    	// Charset and decoder for ISO-8859-15
    	Charset charset = Charset.forName( "ISO-8859-15" );
    	CharsetDecoder decoder = charset.newDecoder();

    	// Pattern used to parse lines
    	Pattern linePattern = Pattern.compile( ".*\r?\n" );

        // The input pattern that we're looking for
        Pattern pattern = Pattern.compile( searchPattern );

    	// Open the file and then get a channel from the stream
    	FileInputStream fis = new FileInputStream( filename );
    	FileChannel fc = fis.getChannel();

    	// Get the file's size and then map it into memory
    	int sz = (int)fc.size();
    	MappedByteBuffer bb = fc.map( FileChannel.MapMode.READ_ONLY, 0, sz );

    	// Decode the file into a char buffer
    	CharBuffer cb = decoder.decode( bb );

    	// Perform the search
    	Matcher lm = linePattern.matcher( cb );	// Line matcher
    	Matcher pm = null;			// Pattern matcher
    	int lines  = 0;

    	while ( lm.find() )
    	{
    	    lines++;

    	    CharSequence cs = lm.group(); 	// The current line

    	    if ( pm == null )
    	    {
    	    	pm = pattern.matcher( cs );
    	    }
    	    else
    	    {
    	    	pm.reset( cs );
    	    }

    	    if ( pm.find() )
    	    {
    	    	retVal = true;
    	    	break;
    	    }

    	    if ( lm.end() == cb.limit() )
    	    {
    	    	break;
    	    }
    	}

    	// Close the channel and the stream
    	fc.close();
    	fis.close();

    	return retVal;
    }

	// +--------------------------------------------------------------------------------------------+
    // |  createIconLabel                                                                           |
	// +--------------------------------------------------------------------------------------------+
	// |  Creates an HTML based icon label that is returned as a string that can be used as the     |
	// |  label for components, such as JRadioButton. This will allow the radio button to be        |
	// |  visible next to the label.                                                                |
	// |                                                                                            |
	// |  @param icon - The icon for the label.                                                     |
	// |  @param text - The text for the label.                                                     |
	// +--------------------------------------------------------------------------------------------+
    public static String createIconLabel( Icon icon, String text )
    {
    	String label = new String();

    	if ( text != null )
    	{
			label = "<html><table cellpadding=0><tr><td><img src=file:"

			// The location of the icon
		    + icon
		    + "/></td><td width="
		
		    // The gap, in pixels, between icon and text
		    + 3
		    + "><td>"
		
		    // Retrieve the current label text
		    + "</td></tr></table></html>";
    	}
    	else
    	{
			label = "<html><img src=file:" + icon + "/></html>";
    	}

    	return label;
    }


    // +--------------------------------------------------------------------------------------------+
    // |  readINIFile                                                                               |
    // +--------------------------------------------------------------------------------------------+
    // |  Returns a HashMap<String,String> of <KEY,VALUE> pairs as read from the specified .ini     |
    // |  file.  The format of the file is as shown below.                                          |
    // |                                                                                            |
    // |  File format:                                                                              |
    // |                                                                                            |
    // |  Lines starting with # are comments.                                                       |
    // |  Keys are contained within [].                                                             |
	// |  Values are strings on the line below a key.                                               |
    // |                                                                                            |
    // |  Example:		# This is a comment                                                         |
    // |				[SOME_KEY]                                                                  |
    // |				SomeKeysValue                                                               |
	// |                                                                                            |
	// |  @param filename - The INI filename.                                                       |
    // +--------------------------------------------------------------------------------------------+
	public static HashMap<String, String> readINIFile( String filename )
	{
		RandomAccessFile raf        = null;
		HashMap<String, String> map = null;
		String line                 = null;
		String key                  = null;

		try
		{
			raf = new RandomAccessFile( filename, "r" );

			map = new HashMap<String, String>();

			while ( ( line = raf.readLine() ) != null )
			{
				if ( !line.startsWith( "#" ) && line.contains( "[" ) )
				{
					key  = line.substring( 1, line.lastIndexOf( ']' ) ).toUpperCase();
					line = raf.readLine();

					if ( line != null && !line.equals( "" ) )
					{
						if ( line.contains( "/" ) )
						{
							line = line.replace(
										"/", System.getProperty( "file.separator" ) );
						}

						map.put( key, line.trim() );
					}
				}
			}

			raf.close();
		}
		catch ( FileNotFoundException fnfe )
		{
			JOptionPane.showMessageDialog(
							null,
							"Failed to open initialization file:\n" +
							filename,
							"Read INI File Error",
							JOptionPane.ERROR_MESSAGE );
		}
		catch ( IOException ioe )
		{
			MainApp.error( ioe.getMessage() );
		}

		return map;
	}

 	// +-----------------------------------------------------------------------------------+
	// |  centerFrame                                                                      |
	// +-----------------------------------------------------------------------------------+
	// |  Centers a window/frame on the screen.                                            |
	// |                                                                                   |
	// |  @param win - The window to center.                                               |
	// +-----------------------------------------------------------------------------------+
	public static void centerFrame( java.awt.Window win )
	{
		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

		int xPos = d.width / 2 - win.getWidth() / 2 - 50;
		int yPos = d.height / 4  - win.getHeight() / 2;

		if ( xPos < 0 ) { xPos = 20; }
		if ( yPos < 0 ) { yPos = 20; }

		win.setLocation( xPos, yPos );
	}

 	// +-----------------------------------------------------------------------------------+
	// |  stripHTML                                                                        |
	// +-----------------------------------------------------------------------------------+
	// |  Strips HTML tags from a string.                                                  |
	// |                                                                                   |
	// |  @param text - The string to strip HTML tags from.                                |
	// +-----------------------------------------------------------------------------------+
	public static String stripHTML( String text )
	{
		//
		// Could also use: String s = HTMLParser.parse( aJarLabel );
		//
		return text.replaceAll( "\\<.*?>", "" );
	}

	// +-----------------------------------------------------------------------------------+
	// |  addNativeLibraryPath                                                             |
	// +-----------------------------------------------------------------------------------+
	// |  The code modifies the static String array which the property java.library.path   |
	// |  is initially read into. This string array is then used to check paths when       |
	// |  libraries are loaded. Please note that this code uses some internal knowledge of |
	// |  non-public classes so although it works on the Sun JVMs I have tested it on I    | 
	// |  can't guarantee that it will work on other JVMs (in such cases it should throw   |
	// |  an IOException).  Note also that the changes will not show up if you query       | 
	// |  'java.library.path' (but it would be trivial to add code to do a setProperty()   |
	// |  when a new path is added).                                                       |
	// |                                                                                   |
	// |  @param s - The library path to be added.                                         |
	// +-----------------------------------------------------------------------------------+
	public static void addNativeLibraryPath( String s )
	{
		try
		{
			Field field = ClassLoader.class.getDeclaredField( "usr_paths" );
			field.setAccessible( true );

			String[] paths = ( String[] )field.get( null );

			for ( int i=0; i<paths.length; i++ )
			{
				if ( s.equals( paths[ i ] ) )
				{
					return;
				}
			}

			String[] tmp = new String[ paths.length + 1 ];
			System.arraycopy( paths, 0, tmp, 0, paths.length );
			tmp[ paths.length ] = s;

			field.set( null, tmp );

			System.setProperty( "java.library.path",
					 			 s + File.pathSeparator +
					 			 System.getProperty( "java.library.path" ) );
		}
		catch ( IllegalAccessException e )
		{
			MainApp.error(
					"Failed to get permissions to set library path ( " + s + " )!" );
		}
		catch ( NoSuchFieldException e )
		{
			MainApp.error(
					"Failed to get field handle to set library path ( " + s + " )!" );
		}
	}

	// +-----------------------------------------------------------------------------------+
	// |  loadNativeLibrary                                                                |
	// +-----------------------------------------------------------------------------------+
	// |  The code modifies the static String array which the property java.library.path   | 
	// |  is initially read into. This string array is then used to check paths when       | 
	// |  libraries are loaded. Please note that this code uses some internal knowledge of |
	// |  non-public classes so although it works on the Sun JVMs I have tested it on I    | 
	// |  can't guarantee that it will work on other JVMs ( in such cases it should throw  |
	// |  an IOException ).  Note also that the changes will not show up if you query      | 
	// |  'java.library.path' ( but it would be trivial to add code to do a setProperty()  |
	// |  when a new path is added ).  After the path is updated, the library ( .dll/.so ) |
	// |  is loaded.                                                                       |
	// |                                                                                   |
	// |  @param f - The native library to load.                                           |
	// +-----------------------------------------------------------------------------------+
	public static void loadNativeLibrary( File f )
	{
		try
		{
			addNativeLibraryPath( f.getParent() );

			String[] nameTokens = f.getName().split( "[.]" );

			if ( nameTokens.length == 0 )
			{
				throw new Exception( "Invalid library name: " + f.getName() );
			}

			addNativeLibraryPath( f.getParent() );
			System.loadLibrary( nameTokens[ 0 ] );

			MainApp.info( "Library SUCCESSFULLY loaded!" );
		}
		catch ( Exception e )
		{
			MainApp.error( "Library NOT loaded!\n" + e.getMessage() );
		}
	}

	// +-----------------------------------------------------------------------------------+
	// |  getPath                                                                          |
	// +-----------------------------------------------------------------------------------+
	// |  Takes the fully qualified filename ( path + filename ) and replaces all slashes  |
	// |  in the path with the system dependent separator. The filename is stripped off    |
	// |  and the returned path is forced to end with the separator.                       |
	// |                                                                                   |
	// |  Examples:  C:/some_path/somefile.jar  --- becomes ---> C:\some_path\  ( windows )|
	// |             C:\abcdef\file.txt         --- becomes ---> C:\abcdef\     ( windows )|
	// |                                                                                   |
	// |  @param filename - The filename.                                                  |
	// +-----------------------------------------------------------------------------------+
	public static String getPath( String filename )
	{
		String sysSep = System.getProperty( "file.separator" );

		filename = filename.replace( "/",  sysSep );
		filename = filename.replace( "\\", sysSep );
		filename = filename.substring( 0, filename.lastIndexOf( sysSep ) + 1 );

		if ( !filename.endsWith( sysSep ) )
		{
			filename = filename.concat( sysSep );
		}

		return filename;
	}

	// +-----------------------------------------------------------------------------------+
	// |  getFileName                                                                      |
	// +-----------------------------------------------------------------------------------+
	// |  Takes the fully qualified filename ( path + filename ) and seperates and returns |
	// |  the filename part of the file. The path is stripped off.                         |
	// |                                                                                   |
	// |  Examples:  C:/some_path/somefile.jar  --- becomes ---> somefile.jar              |
	// |             C:\abcdef\file.txt         --- becomes ---> file.txt                  |
	// |                                                                                   |
	// |  @param filename - The filename.                                                  |
	// +-----------------------------------------------------------------------------------+
	public static String getFileName( String filename )
	{
		int dFileNameIndex = filename.lastIndexOf( System.getProperty( "file.separator" ) );

		return ( filename.substring( dFileNameIndex + 1 ) );
	}

	// +-----------------------------------------------------------------------------------+
	// |  exceptionTraceToFile                                                             |
	// +-----------------------------------------------------------------------------------+
	// |  Writes an exception stack trace to the specified file.                           |
	// |                                                                                   |
	// |  @param filename - The filename.                                                  |
	// |  @param ex - The exception.                                                       |
	// +-----------------------------------------------------------------------------------+
	public static void exceptionTraceToFile( String filename, Throwable ex )
	{
		try
		{
			FileWriter  fw = new FileWriter( filename, false );
			PrintWriter pw = new PrintWriter( fw, true );
	
			ex.printStackTrace( pw );
	
			fw.close();
			pw.close();
		}
		catch ( Exception e )
		{
  			JOptionPane.showMessageDialog( null,
					   					   e.getMessage(),
					   					   "exceptionTraceToFile Error",
					   					   JOptionPane.ERROR_MESSAGE,
					   					   new javax.swing.ImageIcon( owl.main.MainApp.getBitmapPath() + "ErrMsg.gif" ) );
		}
	}
}
