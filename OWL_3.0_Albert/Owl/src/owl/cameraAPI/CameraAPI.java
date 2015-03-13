package owl.cameraAPI;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;
import javax.swing.JOptionPane;

import owl.img.analysis.ImageDifStats;
import owl.img.analysis.ImageStats;
import owl.main.MainApp;


public class CameraAPI
{
	public CameraAPI() {}

	// +--------------------------------------------------------------------+
	// |  API initialization and device connection commands                 |
	// +--------------------------------------------------------------------+
	public native static void     GetAPIConstants() throws Exception;
	public native static String[] GetDeviceList( String[] deviceList );
  	public native static boolean  IsDeviceOpen();
  	public native static boolean  IsCCD();
  	public native static int      GetDeviceId();
  	public native static String   GetDeviceString();
	public native static long     GetImageBufferVA();
	public native static long     GetImageBufferPA();
  	public native static int      GetImageBufferSize();
  	public native static void     FillImageBuffer( int dValue ) throws Exception;
  	public native static int[]    LoadFitsToImageBuffer( String fitsFilename ) throws Exception;
  	public native static void     SetAbort( boolean onOff );
  	public native static void     LoadTmpCtrlFile( String filename ) throws Exception;
  	public native static void     SaveTmpCtrlFile( String filename ) throws Exception;

	// +--------------------------------------------------------------------+
	// |  API logging commands                                              |
	// +--------------------------------------------------------------------+
	public native static void     LogAPICmds( boolean logCmds ) throws Exception;
	public native static String[] GetLoggedAPICmds() throws Exception;

	// +--------------------------------------------------------------------+
	// |  Commands that require API command logging                         |
	// +--------------------------------------------------------------------+
  	public native static void		OpenDeviceAPI( String deviceName ) throws Exception;
  	public native static void		CloseDeviceAPI() throws Exception;
  	public native static void		MapDeviceAPI( int bytes ) throws Exception;
  	public native static void		RemapDeviceAPI( int bytes ) throws Exception;
  	public native static void		UnmapDeviceAPI() throws Exception;
  	public native static void		ClearDeviceStatusAPI() throws Exception;
  	public native static int		GetDeviceStatusAPI() throws Exception;
	public native static int		GetPixelCountAPI() throws Exception;
	public native static int		GetCRPixelCountAPI() throws Exception;
	public native static int		GetFrameCountAPI() throws Exception;
	public native static boolean	IsReadoutAPI() throws Exception;
	public native static int		CommandAPI( int boardId, int command, int arg1, int arg2, int arg3, int arg4 ) throws Exception;
	public native static void		LoadDeviceFileAPI( String filename ) throws Exception;
	public native static void		LoadControllerFileAPI( String filename, int validate ) throws Exception;
	public native static void		SetImageSizeAPI( int rows, int cols ) throws Exception;
	public native static int		GetImageRowsAPI() throws Exception;
	public native static int		GetImageColsAPI() throws Exception;
	public native static int		GetCCParamsAPI() throws Exception;
	public native static boolean	IsCCParamSupportedAPI( int parameter ) throws Exception;
	public native static boolean	IsControllerConnectedAPI();
	public native static double		GetArrayTemperatureAPI() throws Exception;
	public native static double		GetArrayTemperatureDNAPI() throws Exception;
	public native static void		SetArrayTemperatureAPI( double tempVal ) throws Exception;
	public native static void		SetBinningAPI( int rows, int cols, int rowFactor, int colFactor ) throws Exception;
	public native static void		UnsetBinningAPI( int rows, int cols ) throws Exception;
	public native static void		SetOpenShutterAPI( boolean shouldOpen ) throws Exception;
	public native static void		SetSyntheticImageModeAPI( boolean mode ) throws Exception;
	public native static boolean	IsSyntheticImageModeAPI() throws Exception;
	public native static void		ResetDeviceAPI() throws Exception;
	public native static void		ResetControllerAPI() throws Exception;
	public native static void		StopExposureAPI() throws Exception;
 	public native static void		Set2xFOTransmitterAPI( boolean onOff ) throws Exception;
	public native static int		GetCfgByteAPI( int offset ) throws Exception;
	public native static int		GetCfgWordAPI( int offset ) throws Exception;
	public native static int		GetCfgDWordAPI( int offset ) throws Exception;
	public native static void		SetCfgByteAPI( int offset, int value ) throws Exception;
	public native static void		SetCfgWordAPI( int offset, int value ) throws Exception;
	public native static void		SetCfgDWordAPI( int offset, int value ) throws Exception;
	public native static Object[][]	GetProperties();

	// +--------------------------------------------------------------------+
	// |  Image buffer manipulation commands                                |
	// +--------------------------------------------------------------------+
	public native static void DeinterlaceImage( int rows, int cols, int algorithm ) throws Exception;
	public native static void DeinterlaceImage( int rows, int cols, int pixelOffset, int algorithm ) throws Exception;
	public native static void DeinterlaceImage( int rows, int cols, int pixelOffset, int algorithm, int arg ) throws Exception;
	public native static String[] GetCustomDeinterlaceAlgorithms( String path ) throws Exception;
	public native static void SubtractImageHalves( int rows, int cols ) throws Exception;
	public native static void VerifySyntheticImage( int rows, int cols ) throws Exception;
	public native static void LaunchDisplay() throws Exception;
	public native static void TerminateDisplay() throws Exception;
	public native static void DisplayImage( int rows, int cols ) throws Exception;
	public native static void WriteBufferTextFile( String filename, int rows, int cols ) throws Exception;
	public native static void LoadBufferTextFile( String filename ) throws Exception;
	public native static Point2D.Float[] ImageHistogram( int llrow, int urrow, int llcol, int urcol, int rows, int cols ) throws Exception;
	public native static Point2D.Float[] GetImageRow( int row, int col1, int col2, int rows, int cols ) throws Exception;
	public native static Point2D.Float[] GetImageCol( int col, int row1, int row2, int rows, int cols ) throws Exception;
	public native static Point2D.Float[] GetImageRowArea( int row1, int row2, int col1, int col2, int rows, int cols ) throws Exception;
	public native static Point2D.Float[] GetImageColArea( int row1, int row2, int col1, int col2, int rows, int cols ) throws Exception;
	public native static ImageStats GetImageStats( int row1, int row2, int col1, int col2, int rows, int cols ) throws Exception;
	public native static Point[] PixelSearch( int value );

	// +--------------------------------------------------------------------+
	// |  Image file manipulation commands                                  |
	// +--------------------------------------------------------------------+
	public native static void DeinterlaceFitsFile( String filename, int algorithm ) throws Exception;
	public native static void DeinterlaceFitsFile( String filename, int algorithm, int arg ) throws Exception;
	public native static void Deinterlace3DFitsFile( String filename, int algorithm ) throws Exception;
	public native static void Deinterlace3DFitsFile( String filename, int algorithm, int arg ) throws Exception;
	public native static void WriteTiffFile( String filename, int rows, int cols ) throws Exception;
	public native static void WriteFitsFile( String filename, int rows, int cols ) throws Exception;
	public native static void WriteFitsKeyword( String key, String keyVal, String comment, String filename ) throws Exception;
	public native static void WriteFitsComment( String comment, String filename ) throws Exception;
	public native static void WriteFitsHistory( String history, String filename ) throws Exception;
	public native static void WriteFitsDate( String filename ) throws Exception;
	public native static long CreateFitsFile( String filename, int rows, int cols, int bitsPerPixel ) throws Exception;
	public native static void WriteToFitsFile( long fitsFileHandle, int bytes, int bytesToSkip, int fileFirstPixel ) throws Exception;
	public native static long Create3DFitsFile( String filename, int rows, int cols, int bitsPerPixel ) throws Exception;
	public native static void WriteTo3DFitsFile( long fitsFileHandle, int ushortOffset ) throws Exception;
	public native static void CloseFitsFile( long fitsFileHandle ) throws Exception;
	public native static ImageStats GetImageStats( String fitsFile, int row1, int row2, int col1, int col2 ) throws Exception;
	public native static ImageDifStats GetImageDifStats( String fitsFile1, String fitsFile2, int row1, int row2, int col1, int col2 ) throws Exception;

	// +--------------------------------------------------------------------+
	// |  Script only commands ( not used directly by Owl )                 |
	// +--------------------------------------------------------------------+
	public native static void SetupController( boolean reset, boolean tdl, boolean power, int rows, int cols, String timFile, String utilFile, String pciFile );

	// +--------------------------------------------------------------------+
	// |  Temperature control variable access                               |
	// +--------------------------------------------------------------------+
	public native static double   GetTempCtrlAduOffset();
	public native static double   GetTempCtrlAduPerVolt();
	public native static double[] GetTempCtrlAduCoeff( int range );

	// +--------------------------------------------------------------------+
	// |  Remote API access                                                 |
	// +--------------------------------------------------------------------+
	public native static void	 UseRemoteAPI( boolean bOnOff );
	public native static void    ConnectToServer( String IPAddr, int port );
	public native static void	 DisconnectServer();
	public native static void    EnableServerLog( boolean enable ) throws Exception;
	public native static boolean IsServerLogging();
	public native static File[]  GetDirList( String targetDir );

	// +--------------------------------------------------------------------+
	// |  PCI/PCIe configuration space access                               |
	// +--------------------------------------------------------------------+
	public native static int      GetCfgSpCount();
	public native static int      GetCfgSpAddr( int index );
	public native static int      GetCfgSpValue( int index );
	public native static String[] GetCfgSpBitList( int index );
	public native static String   GetCfgSpName( int index );

	public native static int      GetBarCount();
	public native static String   GetBarName( int index );

	public native static int      GetBarRegCount( int index );
	public native static int      GetBarRegAddr( int index, int regIndex );
	public native static int      GetBarRegValue( int index, int regIndex );
	public native static String   GetBarRegName( int index, int regIndex );

	public native static int      GetBarRegBitListCount( int index, int regIndex );
	public native static String   GetBarRegBitListDef( int index, int regIndex, int bitListIndex );

	// +--------------------------------------------------------------------+
	// |  PCI/PCIe base address register access                             |
	// +--------------------------------------------------------------------+
	public native static void  WriteBar( int bar, int reg, int value );
	public native static int   ReadBar( int bar, int reg );


  	// Load the ARC API C++ libraries
  	static { LoadAPI(); }

  	// Used by reflection package to verify field values
  	public final static int UNDEFINED_VALUE		= -1;
 
  	public static int PCI_ID			=	UNDEFINED_VALUE;
  	public static int TIM_ID			=	UNDEFINED_VALUE;
  	public static int UTIL_ID			=	UNDEFINED_VALUE;

  	public static int X_MEM				=	UNDEFINED_VALUE;
  	public static int Y_MEM				=	UNDEFINED_VALUE;
  	public static int P_MEM				=	UNDEFINED_VALUE;
  	public static int R_MEM				=	UNDEFINED_VALUE;

  	public static int DON				=	UNDEFINED_VALUE;
  	public static int ERR				=	UNDEFINED_VALUE;
  	public static int SYR				=	UNDEFINED_VALUE;
  	public static int TOUT				=	UNDEFINED_VALUE;
  	public static int READOUT			=	UNDEFINED_VALUE;

 	public static int CLR				=	UNDEFINED_VALUE;
 	public static int CSH				=	UNDEFINED_VALUE;
 	public static int IDL				=	UNDEFINED_VALUE;
 	public static int OSH				=	UNDEFINED_VALUE;
 	public static int POF				=	UNDEFINED_VALUE;
  	public static int PON				=	UNDEFINED_VALUE;
  	public static int RDM				=	UNDEFINED_VALUE;
  	public static int RET				=	UNDEFINED_VALUE;
  	public static int SET				=	UNDEFINED_VALUE;
 	public static int SEX				=	UNDEFINED_VALUE;
  	public static int PEX				=	UNDEFINED_VALUE;
  	public static int REX				=	UNDEFINED_VALUE;
 	public static int STP				=	UNDEFINED_VALUE;
  	public static int TDL				=	UNDEFINED_VALUE;
  	public static int WRM				=	UNDEFINED_VALUE;
  	public static int RNC				=	UNDEFINED_VALUE;
  	public static int SID				=	UNDEFINED_VALUE;

	// +---------------------------------------------------------------------------
	// | Controller configuration commands and dependent constants. These are
  	// | supported here and not left to the individual scripts because the
  	// | parameters themselves are controlled within Owl.
	// +---------------------------------------------------------------------------
  	public static int SBN				=	UNDEFINED_VALUE;	// Video processor
  	public static int SGN				=	UNDEFINED_VALUE;
  	public static int VID				=	UNDEFINED_VALUE;

  	public static int CLK				=	UNDEFINED_VALUE;	// Clock device
  	public static int SMX				=	UNDEFINED_VALUE;

  	public static int SOS				=	UNDEFINED_VALUE;	// Readout output source
 	public static int SSP				=	UNDEFINED_VALUE;
 	public static int SSS				=	UNDEFINED_VALUE;

  	public static int AMP_0				=	UNDEFINED_VALUE;	//  Ascii __C amp 0
  	public static int AMP_1				=	UNDEFINED_VALUE;	//  Ascii __D amp 1
  	public static int AMP_2				=	UNDEFINED_VALUE;	//  Ascii __B amp 2
  	public static int AMP_3				=	UNDEFINED_VALUE;	//  Ascii __A amp 3
  	public static int AMP_L				=	UNDEFINED_VALUE;	//  Ascii __L left amp
  	public static int AMP_R				=	UNDEFINED_VALUE;	//  Ascii __R left amp
  	public static int AMP_LR			=	UNDEFINED_VALUE;	//  Ascii _LR right two amps
  	public static int AMP_ALL			=	UNDEFINED_VALUE;	//  Ascii ALL four amps (quad)

	// +---------------------------------------------------------------------------
	// | Controller configuration parameters
	// +---------------------------------------------------------------------------
  	public static int ARC41				=	UNDEFINED_VALUE;
  	public static int VIDGENI			=	UNDEFINED_VALUE;	// CCD Video Processor Gen I
  	public static int ARC42				=	UNDEFINED_VALUE;
  	public static int ARC44				=	UNDEFINED_VALUE;
  	public static int ARC45				=	UNDEFINED_VALUE;
  	public static int ARC46				=	UNDEFINED_VALUE;
  	public static int ARC47				=	UNDEFINED_VALUE;
  	public static int ARC48				=	UNDEFINED_VALUE;
  	public static int ARC20				=	UNDEFINED_VALUE;
  	public static int TIMGENI			=	UNDEFINED_VALUE;	// Timing Gen I = 40 MHz
  	public static int ARC22				=	UNDEFINED_VALUE;
  	public static int ARC50				=	UNDEFINED_VALUE;
  	public static int SHUTTER_CC		=	UNDEFINED_VALUE;	// Shutter supported
  	public static int TEMP_SIDIODE		=	UNDEFINED_VALUE;	// Silicon Diode calibration
  	public static int TEMP_LINEAR		=	UNDEFINED_VALUE;	// Linear calibration
  	public static int SUBARRAY			=	UNDEFINED_VALUE;	// Subarray readout supported
  	public static int BINNING			=	UNDEFINED_VALUE;	// Binning supported
  	public static int SPLIT_SERIAL		=	UNDEFINED_VALUE;	// Split serial supported
  	public static int SPLIT_PARALLEL	=	UNDEFINED_VALUE;	// Split parallel supported
  	public static int MPP_CC			=	UNDEFINED_VALUE;	// Inverted clocks supported
  	public static int ARC32				=	UNDEFINED_VALUE;
  	public static int CLKDRVGENI		=	UNDEFINED_VALUE;	// No clock device board - Gen I
  	public static int MLO				=	UNDEFINED_VALUE;	// Set if Mount Laguna Observatory
  	public static int NGST				=	UNDEFINED_VALUE;	// NGST Aladdin implementation
  	public static int CONT_RD			=	UNDEFINED_VALUE;	// Continuous readout
  	public static int FO_2X_TRANSMITR	=	UNDEFINED_VALUE;	// 2x FO transmitters

  	public static int DEINTERLACE_NONE			=	UNDEFINED_VALUE;
  	public static int DEINTERLACE_PARALLEL		=	UNDEFINED_VALUE;
  	public static int DEINTERLACE_SERIAL		=	UNDEFINED_VALUE;
  	public static int DEINTERLACE_CCD_QUAD		=	UNDEFINED_VALUE;
  	public static int DEINTERLACE_IR_QUAD		=	UNDEFINED_VALUE;
  	public static int DEINTERLACE_CDS_IR_QUAD	=	UNDEFINED_VALUE;
  	public static int DEINTERLACE_HAWAII_RG		=	UNDEFINED_VALUE;
  	public static int DEINTERLACE_STA1600		=	UNDEFINED_VALUE;

   	public static int CR_WRITE			= UNDEFINED_VALUE;
  	public static int CR_COADD			= UNDEFINED_VALUE;
  	public static int CR_DEBUG			= UNDEFINED_VALUE;
  	public static int CR_DISABLED		= 0;

  	public static void VerifyFieldInitialization() throws Exception
  	{
  		CameraAPI api = new CameraAPI();
 
  		Field[] fields = api.getClass().getFields();

   		for ( int i=0; i<fields.length; i++ )
  		{
  			try
  			{
   				//  Only verifies integer values
  				// +----------------------------------------------------+
  				if ( !fields[ i ].getName().equals( "UNDEFINED_VALUE" ) &&
  					 !fields[ i ].getType().getName().equals( java.lang.String.class.getName() ) &&
  					 !fields[ i ].getType().getName().equals( java.util.TreeMap.class.getName() ) )
  				{
  					if ( fields[ i ].getInt( api ) == UNDEFINED_VALUE )
  					{
  						throw new Exception( "CameraAPI field \"" + fields[ i ].getName() +
  											 "\" not inialized!" );
  					}
  				}
  			}
  			catch ( IllegalAccessException iae )
  			{
				throw new Exception( "CameraAPI field \"" + fields[ i ].getName() +
									 "\" was not found or could not be accessed!" );
  			}
  		}
  	}

  	public static void PrintMethodNames()
  	{
  		Method[] methodList = CameraAPI.class.getMethods();

  		for ( int i=0; i<methodList.length; i++ )
  		{
  			MainApp.info( methodList[ i ].toString().replace( "owl.cameraAPI.CameraAPI.", "" ).replace( "owl.cameraAPI.", "" ).replace( "java.lang.", "" ).replace( ",", ", " ) );
  		}
 	}

  	public static void PrintFieldNames()
  	{
  		try
  		{
  			Field[] fieldList = CameraAPI.class.getFields();

  			for ( int i=0; i<fieldList.length; i++ )
  			{
  				MainApp.info( fieldList[ i ].toString() + " = 0x" +
  					Integer.toHexString( fieldList[ i ].getInt( fieldList[ i ] ) ) );
  			}
  		}
  		catch ( IllegalAccessException lae ) {}
 	}

  	public static String[] GetMethodNames()
  	{
  		String[] methodNameList = null;
  		Vector<String> methodVec = new Vector<String>();

  		Method[] methodList = CameraAPI.class.getDeclaredMethods();

   		for ( int i=0; i<methodList.length; i++ )
  		{
  			if ( !methodList[ i ].toString().contains( "API" ) )
  			{
  				String methodName = methodList[ i ].toString();
  				methodName = methodName.replace( "native", "" );
  				methodName = methodName.replace( "(", "( " );
  				methodName = methodName.replace( ")", " )" );
  				methodName = methodName.replace( "java.lang.", "" );
  				methodName = methodName.replace( ",", ", " );
  				methodName = methodName.replace( "owl.cameraAPI.", "" );
  				methodName = methodName.replace( "CameraAPI.", "" );
  				methodName = methodName.replace( "public static ", "" );
  				methodVec.add( methodName );
  			}
  		}
  		methodNameList = new String[ methodVec.size() ];

  		return methodVec.toArray( methodNameList );
  	}

  	public static String[] GetFieldNames()
  	{
  		String[] fieldNameList = null;

		Field[] fieldList = CameraAPI.class.getFields();
		fieldNameList = new String[ fieldList.length ];

		for ( int i=0; i<fieldList.length; i++ )
		{
			fieldNameList[ i ] = fieldList[ i ].toString();
			fieldNameList[ i ] = fieldNameList[ i ].substring( fieldNameList[ i ].lastIndexOf( "." ) + 1 );
  		}

  		return fieldNameList;
 	}

  	public static String GetFieldName( int fieldValue )
  	{
  		String fieldName = null;

		Field[] fieldList = CameraAPI.class.getFields();

		for ( int i=0; i<fieldList.length; i++ )
		{
			try
			{
				if ( fieldList[ i ].getInt( new CameraAPI() ) == fieldValue )
				{
					fieldName = fieldList[ i ].toString();
					fieldName = fieldName.substring( fieldName.lastIndexOf( "." ) + 1 );
				}
			}
			catch ( Exception e ) {}
  		}

  		return fieldName;
 	}

  	public static int Command( int brdId, int cmd, int arg1, int arg2, int arg3, int arg4 ) throws Exception
  	{
  		int retVal = 0;

  		// +----------------------------------------------------------+
 		// | The exception is caught and re-thrown here because       |
  		// | FunctorRunnable uses the Method class, which will just   |
  		// | report the error as a MethodInvocationException, losing  |
  		// | the original exception message.                          |
  		// +----------------------------------------------------------+
  		try
  		{
  			retVal = CommandAPI( brdId, cmd, arg1, arg2, arg3, arg4 );
  		}
  		finally { LogAPICommands(); }
  		

  		return retVal;
  	}

  	// +---------------------------------------------------------------------+
  	// |  This set of command methods do not return a value, but verify the  |
  	// |  specified reply value and throws a ReplyValueException if expected |
  	// |  reply and returned reply don't match.                              |
  	// +---------------------------------------------------------------------+
  	public static void Cmd( int brdId, int cmd, int expectedReply ) throws ReplyException, Exception
  	{
  		int retVal = Command( brdId, cmd, -1, -1, -1, -1 );

  		if ( cmd == SEX )
  		{
  			if ( retVal != DON && retVal != READOUT )
  			{
  				throw new ReplyException( cmd, retVal, expectedReply );
  			}
  		}
  		else
  		{
  			if ( retVal != expectedReply )
  			{
  				throw new ReplyException( cmd, retVal, expectedReply );
  			}
  		}
  	}

  	public static void Cmd( int brdId, int cmd, int arg, int expectedReply ) throws ReplyException, Exception
  	{
  		int retVal = Command( brdId, cmd, arg, -1, -1, -1 );

  		if ( retVal != expectedReply )
  		{
  			throw new ReplyException( cmd, retVal, expectedReply );
  		}
  	}

  	public static void Cmd( int brdId, int cmd, int arg1, int arg2, int expectedReply ) throws ReplyException, Exception
  	{
  		int retVal = Command( brdId, cmd, arg1, arg2, -1, -1 );

  		if ( retVal != expectedReply )
  		{
  			throw new ReplyException( cmd, retVal, expectedReply );
  		}
  	}

  	public static void Cmd( int brdId, int cmd, int arg1, int arg2, int arg3, int expectedReply ) throws ReplyException, Exception
  	{
  		int retVal = Command( brdId, cmd, arg1, arg2, arg3, -1 );

  		if ( retVal != expectedReply )
  		{
  			throw new ReplyException( cmd, retVal, expectedReply );
  		}
  	}

  	public static void Cmd( int brdId, int cmd, int arg1, int arg2, int arg3, int arg4, int expectedReply ) throws ReplyException, Exception
  	{
  		int retVal = Command( brdId, cmd, arg1, arg2, arg3, arg4 );

  		if ( retVal != expectedReply )
  		{
  			throw new ReplyException( cmd, retVal, expectedReply );
  		}
 	}

  	// +---------------------------------------------------------------------+
  	// |  This set of command methods return a value, but do not check for   |
  	// |  an expected reply.                                                 |
  	// +---------------------------------------------------------------------+
  	public static int Cmd2( int brdId, int cmd ) throws Exception
  	{
  		return Command( brdId, cmd, -1, -1, -1, -1 );
  	}

  	public static int Cmd2( int brdId, int cmd, int arg ) throws Exception
  	{
  		return Command( brdId, cmd, arg, -1, -1, -1 );
  	}

  	public static int Cmd2( int brdId, int cmd, int arg1, int arg2, int arg3, int arg4 ) throws Exception
  	{
  		return Command( brdId, cmd, arg1, arg2, arg3, arg4 );
  	}

  	public static void OpenDevice( String deviceName ) throws Exception
	{
  		try
  		{
	  		OpenDeviceAPI( deviceName );
  		}
	  	finally { LogAPICommands(); }
	}

  	public static void CloseDevice() throws Exception
	{
  		try
  		{
  			CloseDeviceAPI();
  		}
	  	finally { LogAPICommands(); }
	}

  	public static void MapDevice( int bytes ) throws Exception
	{
  		try
  		{
			MapDeviceAPI( bytes );
  		}
	  	finally { LogAPICommands(); }
	}

  	public static void RemapDevice( int bytes ) throws Exception
  	{
  		try
  		{
	  		RemapDeviceAPI( bytes );
  		}
	  	finally { LogAPICommands(); }
  	}

  	public static void UnmapDevice() throws Exception
	{
  		try
  		{
	  		UnmapDeviceAPI();
  		}
	  	finally { LogAPICommands(); }
	}

	public static void ClearDeviceStatus() throws Exception
	{
  		try
 		{
			ClearDeviceStatusAPI();
  		}
	  	finally { LogAPICommands(); }
	}

 	public static int GetDeviceStatus() throws Exception
	{
 		int status = -1;

 		try
 		{
			status = GetDeviceStatusAPI();
  		}
	  	finally { LogAPICommands(); }

		return status;
	}

 	public static int GetPixelCount() throws Exception
	{
 		int pixCount = -1;

 		try
 		{
			pixCount = GetPixelCountAPI();
  		}
	  	finally { LogAPICommands(); }

		return pixCount;
	}

 	public static int GetCRPixelCount() throws Exception
	{
 		int pixCount = -1;

 		try
 		{
			pixCount = GetCRPixelCountAPI();
  		}
	  	finally { LogAPICommands(); }

		return pixCount;
	}

 	public static int GetFrameCount() throws Exception
 	{
 		int frameCount = -1;

 		try
 		{
			frameCount = GetFrameCountAPI();
  		}
	  	finally { LogAPICommands(); }

 		return frameCount;
 	}

 	public static boolean IsReadout() throws Exception
	{
 		boolean isROUT = false;

 		try
 		{
			isROUT = IsReadoutAPI();
  		}
	  	finally { LogAPICommands(); }

		return isROUT;
	}

 	public static void SetImageSize( int rows, int cols ) throws Exception
	{
 		try
 		{
 			SetImageSizeAPI( rows, cols );
  		}
	  	finally { LogAPICommands(); }
	}

	public static int GetImageRows() throws Exception
	{
		int rows = -1;

		try
		{
			rows = GetImageRowsAPI();

	  		if ( rows < 0 || rows > 900000 )
			{
	  			rows = 0;
			}
		}
	  	finally { LogAPICommands(); }

		return rows;
	}

	public static int GetImageCols() throws Exception
	{
		int cols = -1;

		try
		{
			cols = GetImageColsAPI();
 
	  		if ( cols < 0 || cols > 900000 )
			{
	  			cols = 0;
			}
		}
	  	finally { LogAPICommands(); }

		return cols;
	}

  	public static int[] GetImageSize() throws Exception
  	{
  		int imageSize[] = { -1, -1 };

  		try
  		{
	  		imageSize[ 0 ] = GetImageRows();
	  		imageSize[ 1 ] = GetImageCols();

	  		// Check validity of image dimensions
	  		if ( imageSize[ 0 ] == TOUT || imageSize[ 1 ] == TOUT )
	  		{
	  			throw new Exception( "Controller has invalid image dimensions. " +
	  								 "It's likely the controller has not been properly setup." );
	  		}

	  		if ( imageSize[ 0 ] < 0 || imageSize[ 0 ] > 900000 )
			{
				imageSize[ 0 ] = 0;
			}

			if ( imageSize[ 1 ] < 0 || imageSize[ 1 ] > 900000 )
			{
				imageSize[ 1 ] = 0;
			}

			LogAPICommands();
  		}
	  	finally { LogAPICommands(); }

  		return imageSize;
  	}

	public static void LoadDeviceFile( String filename ) throws Exception
	{
		try
		{
			LoadDeviceFileAPI( filename );
  		}
	  	finally { LogAPICommands(); }
	}

	public static void LoadControllerFile( String filename, int validate ) throws Exception
	{
		try
		{
			LoadControllerFileAPI( filename, validate );
  		}
	  	finally { LogAPICommands(); }
	}

	public static int GetCCParams() throws Exception
	{
		int CCParams = -1;

		try
		{
			CCParams = GetCCParamsAPI();
  		}
	  	finally { LogAPICommands(); }

		return CCParams;
	}

	public static boolean IsCCParamSupported( int parameter ) throws Exception
	{
		boolean retVal = false;

		try
		{
			retVal = IsCCParamSupportedAPI( parameter );
  		}
	  	finally { LogAPICommands(); }

		return retVal;
	}

	public static boolean IsControllerConnected()
	{
		boolean retVal = IsControllerConnectedAPI();

 		try
		{
			LogAPICommands();
		}
		catch ( Exception e )
		{
			if ( e.getMessage() != null && e.getMessage().length() > 0 )
				owl.main.MainApp.error( e.getMessage() );
		}

		return retVal;
	}

	public static double GetArrayTemperatureDN() throws Exception
	{
		double retVal = 0.0;

		try
		{
			retVal = GetArrayTemperatureDNAPI();
  		}
	  	finally { LogAPICommands(); }

		return retVal;
	}

	public static double GetArrayTemperature() throws Exception
	{
		double retVal = 0.0;

		try
		{
			retVal = GetArrayTemperatureAPI();
  		}
	  	finally { LogAPICommands(); }

		return retVal;
	}

	public static void SetArrayTemperature( double tempVal ) throws Exception
	{
		try
		{
			SetArrayTemperatureAPI( tempVal );
  		}
	  	finally { LogAPICommands(); }
	}

	public static void SetBinning( int rows, int cols, int rowFactor, int colFactor ) throws Exception
	{
		try
		{
			SetBinningAPI( rows, cols, rowFactor, colFactor );
  		}
	  	finally { LogAPICommands(); }
	}

	public static void UnsetBinning( int rows, int cols ) throws Exception
	{
		try
		{
			UnsetBinningAPI( rows, cols );
  		}
	  	finally { LogAPICommands(); }
	}

	public static void SetOpenShutter( boolean shouldOpen ) throws Exception
	{
		try
		{
			SetOpenShutterAPI( shouldOpen );
  		}
	  	finally { LogAPICommands(); }
	}

	public static boolean IsSyntheticImageMode() throws Exception
	{
		boolean retVal = false;

		try
		{
			retVal = IsSyntheticImageModeAPI();
  		}
	  	finally { LogAPICommands(); }

 		return retVal;
	}

	public static void SetSyntheticImageMode( boolean mode ) throws Exception
	{
		try
		{
			SetSyntheticImageModeAPI( mode );
  		}
	  	finally { LogAPICommands(); }
	}

	public static void ResetDevice() throws Exception
	{
		try
		{
			ResetDeviceAPI();
  		}
	  	finally { LogAPICommands(); }
	}

	public static void ResetController() throws Exception
	{
		try
		{
			ResetControllerAPI();
  		}
	  	finally { LogAPICommands(); }
	}

	public static void StopExposure() throws Exception
	{
		try
		{
			StopExposureAPI();
  		}
	  	finally { LogAPICommands(); }
	}

//	public static boolean Is2xFOReceiver() throws Exception
//	{
//		boolean bFO2x = false;
//
//		try
//		{
//			bFO2x = Is2xFOReceiverAPI();
//		}
//		finally { LogAPICommands(); }
//
//		return bFO2x;
//	}

	public static void Set2xFOTransmitter( boolean onOff ) throws Exception
	{
		try
		{
			Set2xFOTransmitterAPI( onOff );
		}
		finally { LogAPICommands(); }
	}

	public static int GetCfgByte( int offset ) throws Exception
	{
		int retVal = 0;

		try
		{
			retVal = GetCfgByteAPI( offset );
		}
		finally { LogAPICommands(); }

		return retVal;
	}

	public static int GetCfgWord( int offset ) throws Exception
	{
		int retVal = 0;

		try
		{
			retVal = GetCfgWordAPI( offset );
		}
		finally { LogAPICommands(); }

		return retVal;
	}

	public static int GetCfgDWord( int offset ) throws Exception
	{
		int retVal = 0;

		try
		{
			retVal = GetCfgDWordAPI( offset );
		}
		finally { LogAPICommands(); }

		return retVal;
	}

	public static void SetCfgByte( int offset, int value ) throws Exception
	{
		try
		{
			SetCfgByteAPI( offset, value );
		}
		finally { LogAPICommands(); }
	}

	public static void SetCfgWord( int offset, int value ) throws Exception
	{
		try
		{
			SetCfgWordAPI( offset, value );
		}
		finally { LogAPICommands(); }
	}

	public static void SetCfgDWord( int offset, int value ) throws Exception
	{
		try
		{
			SetCfgDWordAPI( offset, value );
		}
		finally { LogAPICommands(); }
	}

	private static void LoadAPI()
	{
  		try
  		{
	  		System.loadLibrary( "CArcDevice" );
	  		System.loadLibrary( "CDeinterlace" );
	  		System.loadLibrary( "CFitsFile" );
	  		System.loadLibrary( "CTiffFile" );
	  		System.loadLibrary( "CDisplay" );
	  		System.loadLibrary( "CImage" );
	  		System.loadLibrary( "CameraAPI" );
  		}
  		catch( UnsatisfiedLinkError ule )  // If the library does not exist
  		{
  			String iconName = "ErrMsg2.gif";
  			java.util.Random rand = new java.util.Random();
  			int val = rand.nextInt( 3 );
  			if ( val == 1 ) { iconName = "ErrMsg.gif"; }
  			else if ( val == 2 ) { iconName = "ErrMsg3.gif"; }

  			owl.gui.utils.OwlUtilities.exceptionTraceToFile( "LibraryLoadULE.txt", ule );

  			String sNote = "The message \"The specified procedure could not be found\" for UnsatisfiedLinkError\n" +
  						   "indicates that a function in the \"root dll\" or in a \"dependent dll\" ( of the root dll )\n" +
  						   "could not be found! i.e. Make sure all dependent libraries of the faulting library are\n" +
  						   "compiled and up-to-date in the API directory! Make sure no expected functions are missing!";

   			JOptionPane.showMessageDialog( null,
  										   "Library unsatisfied link error.\n" + ule.getMessage() +
  										   "\nAPI PATH: " + System.getProperty( "java.library.path" ) +
  										   "\n\nNOTES:\n" + sNote,
  										   "Initialization Error",
  										   JOptionPane.ERROR_MESSAGE,
  										   new javax.swing.ImageIcon( owl.main.MainApp.getBitmapPath() + iconName ) );

  			System.exit( 1 );
 		}
  		catch( Exception e )
  		{
  			owl.gui.utils.OwlUtilities.exceptionTraceToFile( "LibraryLoadULE.txt", e );

  			String msg = e.getMessage();
 
  			if ( NullPointerException.class.isInstance( e ) )
  			{
  				msg = "Attempted to load NULL library for Controller API!";
  			}

  			String iconName = "ErrMsg2.gif";
  			java.util.Random rand = new java.util.Random();
  			int val = rand.nextInt( 3 );
  			if ( val == 1 ) { iconName = "ErrMsg.gif"; }
  			else if ( val == 2 ) { iconName = "ErrMsg3.gif"; }

  			JOptionPane.showMessageDialog( null,
  										   "Library initialization error.\n" + msg +
  										   "\nAPI PATH: " + System.getProperty( "java.library.path" ),
  										   "Initialization Error",
  										   JOptionPane.ERROR_MESSAGE,
  										   new javax.swing.ImageIcon( owl.main.MainApp.getBitmapPath() + iconName ) );
  			System.exit( 1 );
  		}
	}

	private static void LogAPICommands() throws Exception
	{
		if ( MainApp.mainFrame.logFrame.isAPILogging() )
		{
			String[] loggedCmds = GetLoggedAPICmds();

			if ( loggedCmds != null )
			{
				for ( int i=0; i<loggedCmds.length; i++ )
				{
					if ( !loggedCmds[ i ].equals( "" ) )
					{
						owl.main.MainApp.debug( loggedCmds[ i ] );
					}
				}
			}
		}
	}
}
