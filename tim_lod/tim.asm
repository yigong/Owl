       COMMENT *

This file is used to generate boot DSP code for the 250 MHz fiber optic
	timing board using a DSP56303 as its main processor. It supports 
	frame transfer and binning.

	*
	PAGE    132     ; Printronix page width - 132 columns

; Include the boot file so addressing is easy
	INCLUDE	"timboot.asm"
	
; CCDVIDREV3B     EQU     $000000         ; CCD Video Processor Rev. 3
; TIMREV4         EQU     $000000         ; Timing Rev. 4
; UTILREV3        EQU     $000020         ; Utility Rev. 3 supported
; SHUTTER_CC      EQU     $000080         ; Shutter supported
; TEMP_POLY       EQU     $000100         ; Polynomial calibration
; SUBARRAY        EQU     $000400         ; Subarray readout supported
; BINNING         EQU     $000800         ; Binning supported
; SPLIT_SERIAL    EQU     $001000         ; Split serial supported
; SPLIT_PARALLEL  EQU     $002000         ; Split parallel supported

	ORG	P:,P:
;CC	EQU	CCDVIDREV3B+TIMREV4+UTILREV3+SHUTTER_CC+TEMP_POLY+SUBARRAY+SPLIT_SERIAL+SPLIT_PARALLEL
CC	EQU	ARC22+SUBARRAY+SPLIT_PARALLEL+SPLIT_SERIAL+ARC48

; Put number of words of application in P: for loading application from EEPROM
	DC	TIMBOOT_X_MEMORY-@LCV(L)-1

; Define CLOCK as a macro to produce in-line code to reduce execution time
CLOCK	MACRO
	JCLR	#SSFHF,X:HDR,*		; Don't overfill the WRSS FIFO
	REP	Y:(R0)+			; Repeat
	MOVEP	Y:(R0)+,Y:WRSS		; Write the waveform to the FIFO
	ENDM

; Set software to IDLE mode
START_IDLE_CLOCKING
	MOVE	#IDLE,R0		; Exercise clocks when idling
	MOVE	R0,X:<IDL_ADR
	BSET	#IDLMODE,X:<STATUS	; Idle after readout
	JMP     <FINISH			; Need to send header and 'DON'

; Keep the CCD idling when not reading out
IDLE	DO      Y:<NSR,IDL1     	; Loop over number of pixels per line
	MOVE    #<SERIAL_IDLE,R0 	; Serial transfer on pixel
	CLOCK  				; Go to it
	MOVE	#COM_BUF,R3
	JSR	<GET_RCV		; Check for FO or SSI commands
	JCC	<NO_COM			; Continue IDLE if no commands received
	ENDDO
	JMP     <PRC_RCV		; Go process header and command
NO_COM	NOP
IDL1
	MOVE    #<PARALLEL_SPLIT,R0	; Address of parallel clocking waveform
	CLOCK  				; Go clock out the CCD charge
	JMP     <IDLE

;  *****************  Exposure and readout routines  *****************

; Calculate readout parameters for whole image readout
RDCCD	JSET	#ST_SA,X:STATUS,SUB_IMG
	MOVE	#0,X0
	MOVE	X0,Y:<NP_SKIP	
	MOVE	X0,Y:<NS_SKP1
	MOVE	X0,Y:<NS_SKP2
	MOVE	X0,Y:<NR_BIAS
	MOVE	Y:<NSR,X0		; NS_READ = NSR
	MOVE	X0,Y:<NS_READ
	MOVE	Y:<NPR,X0		; NP_READ = NPR
	MOVE	X0,Y:<NP_READ
	JMP	<GO_ON

; Set up for subarray readout
SUB_IMG	MOVE	#READ_TABLE,R7		; Parameter table for subimage readout
	NOP
	NOP
	MOVE	Y:(R7)+,X0
	MOVE	X0,Y:<NP_SKIP
	MOVE	Y:(R7)+,X0		; NS_SKP1 = # to skip before the read
	MOVE	X0,Y:<NS_SKP1
	MOVE	Y:(R7)+,A		; NS_SKP2 = # to skip after the read
	TST	A
	JGT	<SKP2_OK		; If NS_SKP2 .LE. = then set to zero
	CLR	A
	NOP
SKP2_OK	MOVE	A1,Y:<NS_SKP2
	MOVE	Y:<NSREAD,X0		; NS_READ = # of pixels to read
	MOVE	X0,Y:<NS_READ
	MOVE	Y:<NPREAD,X0		; NP_READ = # of rows to read
	MOVE	X0,Y:<NP_READ
	MOVE	Y:<NRBIAS,X0		; NR_BIAS = # of bias pixels to read
	MOVE	X0,Y:<NR_BIAS

; Generate new waveform and image dimensions
GO_ON	JCLR	#SPLIT_S,X:STATUS,SPL_PAR
	MOVE	Y:<NS_READ,A		; Split serials require / 2
	NOP
	LSR	A
	NOP
	MOVE	A1,Y:<NS_READ
	MOVE	Y:<NR_BIAS,A		; Number of bias pixels to read
	NOP
	LSR	A
	NOP
	MOVE	A,Y:<NR_BIAS

SPL_PAR	JCLR	#SPLIT_P,X:STATUS,P_SHIFT
	MOVE	Y:<NP_READ,A		; Split parallels require / 2
	NOP
	LSR	A
	NOP
	MOVE	A1,Y:<NP_READ

; Skip over the required number of rows for subimage readout
P_SHIFT	DO      Y:<NP_SKIP,L_PSKIP	
	MOVE    Y:<PARALLEL,R0
	CLOCK
L_PSKIP

; *******  Begin readout over the entire array  ******
	DO	Y:<NP_READ,LPR
	MOVE    Y:<PARALLEL,R0
	CLOCK

; Check for a command once per line. Only the ABORT command should be issued.
	MOVE	#COM_BUF,R3
	JSR	<GET_RCV		; Was a command received?
	JCC	<CONTINUE_READ		; If no, continue reading out
	JMP	<PRC_RCV		; If yes, go process it

; Abort the readout currently underway
ABR_RDC	JCLR	#ST_RDC,X:<STATUS,ABORT_EXPOSURE
	ENDDO				; Properly terminate readout loop
	JMP	<ABORT_EXPOSURE
CONTINUE_READ	

; Do a fast skip over NS_SKP1 pixels
	DO	Y:<NS_SKP1,L_S		; If NS_SKP1 = 0 this won't be 
	MOVE	Y:<SERIAL_SKIP,R0	;   executed
	CLOCK 
L_S

; Clock, video process and pixels
	DO	Y:<NS_READ,L_RD
	MOVE	Y:<SERIAL_READ,R0
	CLOCK
L_RD

; Skip over NS_SKP2 pixels if needed for subimage readout
	MOVE	Y:<NS_SKP2,A		; Protect against negative values
	TST	A
	JLE	<RDBIAS
	DO	Y:<NS_SKP2,L_SB
	MOVE	Y:<SERIAL_SKIP,R0
	CLOCK
L_SB

; Read the bias pixels if needed for subimage readout
RDBIAS	MOVE	Y:<NR_BIAS,A		; Protect against negative values
	TST	A
	JLE	<L_RB
	DO      Y:<NR_BIAS,L_RB
	MOVE	Y:<SERIAL_READ,R0
	CLOCK
L_RB	NOP
LPR

; Restore the controller to non-image data transfer and idling if necessary
RDC_END	JCLR	#IDLMODE,X:<STATUS,NO_IDL
	MOVE	#IDLE,R0
	MOVE	R0,X:<IDL_ADR
	JMP	<RDC_E
NO_IDL	MOVE	#TST_RCV,R0	 	; Don't idle after readout
	MOVE	R0,X:<IDL_ADR
RDC_E	JSR	<WAIT_TO_FINISH_CLOCKING
	BCLR	#ST_RDC,X:<STATUS	; Set status to not reading out
        JMP     <START

; ******  Include many routines not directly needed for readout  *******
	INCLUDE "timCCDmisc.asm"


TIMBOOT_X_MEMORY	EQU	@LCV(L)

;  ****************  Setup memory tables in X: space ********************

; Define the address in P: space where the table of constants begins

	IF	@SCP("DOWNLOAD","HOST")
	ORG     X:END_COMMAND_TABLE,X:END_COMMAND_TABLE
	ENDIF

	IF	@SCP("DOWNLOAD","ROM")
	ORG     X:END_COMMAND_TABLE,P:
	ENDIF

; Application commands
	DC	'PON',POWER_ON
	DC	'POF',POWER_OFF
	DC	'SBV',SET_BIAS_VOLTAGES
	DC	'IDL',START_IDLE_CLOCKING
	DC	'RDC',STR_RDC    
	DC	'CLR',CLEAR   

; Exposure and readout control routines
	DC	'SET',SET_EXPOSURE_TIME
	DC	'RET',READ_EXPOSURE_TIME
	DC	'SEX',START_EXPOSURE
	DC	'PEX',PAUSE_EXPOSURE
	DC	'REX',RESUME_EXPOSURE
	DC	'AEX',ABORT_EXPOSURE
	DC	'ABR',ABR_RDC
	DC	'CRD',CONTINUE_READ
	DC	'OSH',OPEN_SHUTTER
	DC	'CSH',CLOSE_SHUTTER
	DC	'SVO',SET_VIDEO_OFFSET
	
; Support routines
;	DC	'SGN',SET_GAIN 		; Need a routine for ARC-48     
	DC	'SBN',SET_BIAS_NUMBER
	DC	'SMX',SET_MUX
	DC	'CSW',CLR_SWS
	DC	'SOS',SELECT_OUTPUT_SOURCE
	DC	'SSS',SET_SUBARRAY_SIZES
	DC	'SSP',SET_SUBARRAY_POSITIONS
	DC	'RCC',READ_CONTROLLER_CONFIGURATION

; LBNL high voltage bias board commands
	DC	'BON',SUBSTRATE_BIAS_ON
	DC	'BOF',SUBSTRATE_BIAS_OFF

END_APPLICATON_COMMAND_TABLE	EQU	@LCV(L)

	IF	@SCP("DOWNLOAD","HOST")
NUM_COM			EQU	(@LCV(R)-COM_TBL_R)/2	; Number of boot + 
							;  application commands
EXPOSING		EQU	CHK_TIM			; Address if exposing
CONTINUE_READING	EQU	RDCCD	 		; Address if reading out
	ENDIF

	IF	@SCP("DOWNLOAD","ROM")
	ORG     Y:0,P:
	ENDIF

; Now let's go for the timing waveform tables
	IF	@SCP("DOWNLOAD","HOST")
        ORG     Y:0,Y:0
	ENDIF

GAIN		DC	END_APPLICATON_Y_MEMORY-@LCV(L)-1

NSR     	DC      2200   	 	; Number Serial Read, set by host computer
NPR     	DC      2200	     	; Number Parallel Read, set by host computer
NP_CLR		DC	NPCLR		; To clear the parallel register 
TST_DATA 	DC	0		; For synthetic image
NSBIN   	DC      1       	; Serial binning parameter
NPBIN   	DC      1       	; Parallel binning parameter
CONFIG		DC	CC		; Controller configuration
NS_READ		DC	0		; Number of serials to read
NP_READ		DC	0		; Number of parallels to read
NR_BIAS		DC	0		; Number of bias pixels to read
OS		DC	'ALL'		; Name of the output source(s)
SYN_DAT		DC	0		; Synthetic image mode pixel count
SHDEL		DC	SH_DEL		; Delay from shutter close to start of readout

; Waveform table addresses
PARALLEL 		DC	PARALLEL_SPLIT
SERIAL_READ		DC	SERIAL_READ_SPLIT
SERIAL_SKIP 		DC	SERIAL_SKIP_SPLIT

; These three parameters are read from the READ_TABLE when needed by the
;   RDCCD routine as it loops through the required number of boxes
NP_SKIP	DC	0		; Number of rows to skip
NS_SKP1	DC	0		; Number of serials to clear before read
NS_SKP2	DC	0		; Number of serials to clear after read

; Subimage readout parameters. One subimage box only
NRBIAS	DC	0		; Number of bias pixels to read
NSREAD	DC	0		; Number of columns in subimage read
NPREAD	DC	0		; Number of rows in subimage read
READ_TABLE DC	0,0,0		; #1 = Number of rows to clear 
				; #2 = Number of columns to skip before 
				;   subimage read 
				; #3 = Number of rows to clear after 
				;   subimage clear

; Include the waveform table for the designated type of CCD
	INCLUDE "WAVEFORM_FILE" ; Readout and clocking waveform file

END_APPLICATON_Y_MEMORY	EQU	@LCV(L)

; End of program
	END

