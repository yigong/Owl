; Miscellaneous CCD control routines
POWER_OFF
	JSR	<CLEAR_SWITCHES		; Clear all analog switches
	BSET	#LVEN,X:HDR 
	BSET	#HVEN,X:HDR 
	JMP	<FINISH

POWER_ON
	JSR	<CLEAR_SWITCHES		; Clear all analog switches
	JSR	<PON			; Turn on the power control board
	JCLR	#PWROK,X:HDR,PWR_ERR	; Test if the power turned on properly
	JSR	<SET_BIASES		; Turn on the DC bias supplies

; Turn the DACs ON
	MOVE	#$0C0004,A		; Turn ON the DACs on all ARC-48s
	MOVE	#$100000,X0		; Increment over board numbers
	DO	#15,L_ON		; 15 video processor boards
	JSR	<XMIT_A_WORD		; Transmit A to TIM-A-STD
	ADD	X0,A
	JSR	<PAL_DLY		; Delay for all this to happen
	NOP
L_ON
	BCLR	#3,X:PCRD		; Turn the serial clock off
	MOVE	#IDLE,R0		; Put controller in IDLE state
	MOVE	R0,X:<IDL_ADR
	JMP	<FINISH

; The power failed to turn on because of an error on the power control board
PWR_ERR	BSET	#LVEN,X:HDR		; Turn off the low voltage emable line
	BSET	#HVEN,X:HDR		; Turn off the high voltage emable line
	JMP	<ERROR

; As a subroutine, turn on the low voltages (+/- 6.5V, +/- 16.5V) and delay
PON	BCLR	#LVEN,X:HDR		; Set these signals to DSP outputs 
	MOVE	#5000000,X0
	DO      X0,*+3			; Wait 50 millisec for settling
	NOP 	

; Turn on the high +36 volt power line and then delay
	BCLR	#HVEN,X:HDR		; HVEN = Low => Turn on +36V
	MOVE	#5000000,X0
	DO      X0,*+3			; Wait 50 millisec for settling
	NOP
	RTS

; Set all the DC bias voltages and video processor offset values, reading
;   them from the 'DACS' table
SET_BIASES
	BSET	#3,X:PCRD		; Turn on the serial clock
	BCLR	#1,X:<LATCH		; Separate updates of clock driver
	BSET	#CDAC,X:<LATCH		; Disable clearing of DACs
	BSET	#ENCK,X:<LATCH		; Enable clock and DAC output switches
	MOVEP	X:LATCH,Y:WRLATCH	; Write it to the hardware
	JSR	<PAL_DLY		; Delay for all this to happen

; Read DAC values from a table, and write them to the DACs
	MOVE	#DACS,R0		; Get starting address of DAC values
	NOP
	NOP
	DO      Y:(R0)+,L_DAC		; Repeat Y:(R0)+ times
	MOVE	Y:(R0)+,A		; Read the table entry
	JSR	<XMIT_A_WORD		; Transmit it to TIM-A-STD
	NOP
L_DAC

; Let the DAC voltages all ramp up before exiting
	MOVE	#400000,X0
	DO	X0,*+3			; 4 millisec delay
	NOP
	RTS

SET_BIAS_VOLTAGES
	JSR	<SET_BIASES
	BCLR	#3,X:PCRD		; Turn the serial clock off
	JMP	<FINISH

CLR_SWS	JSR	<CLEAR_SWITCHES
	JMP	<FINISH

; Clear all video processor analog switches to lower their power dissipation
CLEAR_SWITCHES
	BSET	#3,X:PCRD	; Turn the serial clock on
	MOVE	#$0C0000,A	; Turn OFF the DACs on ARC-48
	CLR	B
	MOVE	#$100000,X0	; Increment over board numbers for DAC writes
	MOVE	#$001000,X1	; Increment over board numbers for WRSS writes
	DO	#15,L_VIDEO	; Fifteen video processor boards maximum
	JSR	<XMIT_A_WORD	; Transmit A to TIM-A-STD
	ADD	X0,A
	MOVE	B,Y:WRSS
	JSR	<PAL_DLY	; Delay for the serial data transmission
	ADD	X1,B
L_VIDEO	
	BCLR	#CDAC,X:<LATCH		; Enable clearing of DACs
	BCLR	#ENCK,X:<LATCH		; Disable clock and DAC output switches
	MOVEP	X:LATCH,Y:WRLATCH 	; Execute these two operations
	BCLR	#3,X:PCRD		; Turn the serial clock off
	RTS

; Open the shutter by setting the backplane bit TIM-LATCH0
OSHUT	BSET    #ST_SHUT,X:<STATUS 	; Set status bit to mean shutter open
	BSET	#SHUTTER,X:<LATCH	; Set (DC) hardware shutter bit to open
	MOVEP	X:LATCH,Y:WRLATCH	; Write it to the hardware
        RTS

; Close the shutter by clearing the backplane bit TIM-LATCH0
CSHUT	BCLR    #ST_SHUT,X:<STATUS 	; Clear status to mean shutter closed
	BCLR	#SHUTTER,X:<LATCH	; Clear (DC) hardware shutter bit to close
	MOVEP	X:LATCH,Y:WRLATCH	; Write it to the hardware
        RTS

; Open the shutter from the timing board, executed as a command
OPEN_SHUTTER
	JSR	<OSHUT
	JMP	<FINISH

; Close the shutter from the timing board, executed as a command
CLOSE_SHUTTER
	JSR	<CSHUT
	JMP	<FINISH

; Clear the CCD, executed as a command
CLEAR	JSR	<CLR_CCD
	JMP     <FINISH

; Default clearing routine with serial clocks inactive
; Fast clear image before each exposure, executed as a subroutine
CLR_CCD	DO      Y:<NP_CLR,LPCLR2	; Loop over number of lines in image
	MOVE    #PARALLEL_SPLIT,R0	; Address of parallel transfer waveform
	CLOCK
	JCLR    #EF,X:HDR,LPCLR1 	; Simple test for fast execution
	MOVE	#COM_BUF,R3
	JSR	<GET_RCV		; Check for FO command
	JCC	<LPCLR1			; Continue no commands received

	MOVE	#LPCLR1,R0		; Return to LPCLR1 after processing command 
	MOVE	R0,X:<IDL_ADR
	JMP	<PRC_RCV
LPCLR1	NOP
LPCLR2
	MOVE	#TST_RCV,R0		; Process commands during the exposure
	MOVE	R0,X:<IDL_ADR
	JSR	<WAIT_TO_FINISH_CLOCKING
	RTS
		
; Start the exposure timer and monitor its progress
EXPOSE	MOVEP	#0,X:TLR0		; Load 0 into counter timer
	MOVE	#0,X0
	MOVE	X0,X:<ELAPSED_TIME	; Set elapsed exposure time to zero
	MOVE	X:<EXPOSURE_TIME,B
	TST	B			; Special test for zero exposure time
	JEQ	<END_EXP		; Don't even start an exposure
	SUB	#1,B			; Timer counts from X:TCPR0+1 to zero
	BSET	#TIM_BIT,X:TCSR0	; Enable the timer #0
	MOVE	B,X:TCPR0
CHK_RCV	JCLR    #EF,X:HDR,CHK_TIM	; Simple test for fast execution
	MOVE	#COM_BUF,R3		; The beginning of the command buffer
	JSR	<GET_RCV		; Check for an incoming command
	JCS	<PRC_RCV		; If command is received, go check it
CHK_TIM	JCLR	#TCF,X:TCSR0,CHK_RCV	; Wait for timer to equal compare value
END_EXP	BCLR	#TIM_BIT,X:TCSR0	; Disable the timer
	JMP	(R7)			; This contains the return address

; Start the exposure, expose, and initiate the CCD readout
START_EXPOSURE
	MOVE	#$020102,B
	JSR	<XMT_WRD
	MOVE	#'IIA',B		; Initialize the PCI image address
	JSR	<XMT_WRD
	JSR	<CLR_CCD

; Operate the shutter if needed and begin exposure
	JCLR	#SHUT,X:STATUS,L_SEX0
	JSR	<OSHUT
L_SEX0	MOVE	#L_SEX1,R7		; Return address at end of exposure
	JMP	<EXPOSE			; Delay for specified exposure time
L_SEX1

STR_RDC	JSR	<PCI_READ_IMAGE		; Get the PCI board reading the image
	BSET	#ST_RDC,X:<STATUS 	; Set status to reading out
	JCLR	#SHUT,X:STATUS,TST_SYN
	JSR	<CSHUT			; Close the shutter if necessary
TST_SYN	JSET	#TST_IMG,X:STATUS,SYNTHETIC_IMAGE

; Delay readout until the shutter has fully closed
	MOVE	Y:<SHDEL,A
	TST	A
	JLE	<S_DEL0
	MOVE	#100000,X0
	DO	A,S_DEL0		; Delay by Y:SHDEL milliseconds
	DO	X0,S_DEL1
	NOP
S_DEL1	NOP
S_DEL0	NOP

	JMP	<RDCCD			; Finally, go read out the CCD

; Set the desired exposure time
SET_EXPOSURE_TIME
	MOVE	X:(R3)+,Y0
	MOVE	Y0,X:EXPOSURE_TIME
	MOVEP	Y0,X:TCPR0
	JMP	<FINISH

; Read the time remaining until the exposure ends
READ_EXPOSURE_TIME
	JSET	#TIM_BIT,X:TCSR0,RD_TIM	; Read DSP timer if its running
	MOVE	X:<ELAPSED_TIME,Y1
	JMP	<FINISH1
RD_TIM	MOVE	X:TCR0,Y1		; Read elapsed exposure time
	JMP	<FINISH1

; Pause the exposure - close the shutter and stop the timer
PAUSE_EXPOSURE
	MOVEP	X:TCR0,X:ELAPSED_TIME	; Save the elapsed exposure time
	BCLR    #TIM_BIT,X:TCSR0	; Disable the DSP exposure timer
	JSR	<CSHUT			; Close the shutter
	JMP	<FINISH

; Resume the exposure - open the shutter if needed and restart the timer
RESUME_EXPOSURE
	BSET	#TRM,X:TCSR0		; To be sure it will load TLR0
	MOVEP	X:TCR0,X:TLR0		; Restore elapsed exposure time
	BSET	#TIM_BIT,X:TCSR0	; Re-enable the DSP exposure timer
	JCLR	#SHUT,X:STATUS,L_RES
	JSR	<OSHUT			; Open the shutter if necessary
L_RES	JMP	<FINISH

; Abort exposure - close the shutter, stop the timer and resume idle mode
ABORT_EXPOSURE
	JSR	<CSHUT			; Close the shutter
	BCLR    #TIM_BIT,X:TCSR0	; Disable the DSP exposure timer
	JCLR	#IDLMODE,X:<STATUS,NO_IDL2 ; Don't idle after readout
	MOVE	#IDLE,R0
	MOVE	R0,X:<IDL_ADR
	JMP	<RDC_E2
NO_IDL2	MOVE	#TST_RCV,R0
	MOVE	R0,X:<IDL_ADR
RDC_E2	JSR	<WAIT_TO_FINISH_CLOCKING
	BCLR	#ST_RDC,X:<STATUS	; Set status to not reading out
	DO      #4000,*+3		; Wait 40 microsec for the fiber
	NOP				;  optic to clear out 
	JMP	<FINISH

; Generate a synthetic image by simply incrementing the pixel counts
SYNTHETIC_IMAGE
	CLR	A
	DO      Y:<NPR,LPR_TST      	; Loop over each line readout
	DO      Y:<NSR,LSR_TST		; Loop over number of pixels per line
	REP	#20			; #20 => 1.0 microsec per pixel
	NOP
	ADD	#1,A			; Pixel data = Pixel data + 1
	NOP
	MOVE	A,B
	JSR	<XMT_PIX		;  transmit them
	NOP
LSR_TST	
	NOP
LPR_TST	
        JMP     <RDC_END		; Normal exit

; Transmit the 16-bit pixel datum in B1 to the host computer
XMT_PIX	ASL	#16,B,B
	NOP
	MOVE	B2,X1
	ASL	#8,B,B
	NOP
	MOVE	B2,X0
	NOP
	MOVEP	X1,Y:WRFO
	MOVEP	X0,Y:WRFO
	RTS

; Test the hardware to read A/D values directly into the DSP instead
;   of using the SXMIT option, A/Ds #2 and 3.
READ_AD	MOVE	X:(RDAD+2),B
	ASL	#16,B,B
	NOP
	MOVE	B2,X1
	ASL	#8,B,B
	NOP
	MOVE	B2,X0
	NOP
	MOVEP	X1,Y:WRFO
	MOVEP	X0,Y:WRFO
	REP	#10
	NOP
	MOVE	X:(RDAD+3),B
	ASL	#16,B,B
	NOP
	MOVE	B2,X1
	ASL	#8,B,B
	NOP
	MOVE	B2,X0
	NOP
	MOVEP	X1,Y:WRFO
	MOVEP	X0,Y:WRFO
	REP	#10
	NOP
	RTS

; Alert the PCI interface board that images are coming soon
PCI_READ_IMAGE
	MOVE	#$020104,B		; Send header word to the FO xmtr
	JSR	<XMT_WRD
	MOVE	#'RDA',B
	JSR	<XMT_WRD
	MOVE	Y:NSR,B			; Number of columns to read
	JSR	<XMT_WRD
	MOVE	Y:NPR,B			; Number of rows to read
	JSR	<XMT_WRD
	RTS

; Wait for the clocking to be complete before proceeding
WAIT_TO_FINISH_CLOCKING
	JSET	#SSFEF,X:PDRD,*		; Wait for the SS FIFO to be empty
	RTS

; Delay for serial writes to the PALs and DACs by 8 microsec
PAL_DLY	DO	#800,*+3		; Wait 8 usec for serial data xmit
	NOP
	RTS

; Let the host computer read the controller configuration
READ_CONTROLLER_CONFIGURATION
	MOVE	Y:<CONFIG,Y1		; Just transmit the configuration
	JMP	<FINISH1

; Set a particular DAC numbers of the ARC32 clock driver voltages.
;
; SBN  #BOARD  #DAC  ['CLK' or 'VID'] voltage
;
;				#BOARD is from 0 to 15
;				#DAC number
;				#voltage is from 0 to 4095

SET_BIAS_NUMBER			; Set bias number
	BSET	#3,X:PCRD	; Turn on the serial clock
	MOVE	X:(R3)+,A	; First argument is board number, 0 to 15
	REP	#20
	LSL	A
	NOP
	MOVE	A,X1		; Save the board number
	MOVE	A,Y0		; Save again just because...DC
	MOVE	X:(R3)+,A	; Second argument is DAC number
	MOVE	X:(R3)+,B	; Third argument is 'VID' or 'CLK' string
	CMP	#'VID',B
	JNE	<CLK_DRV
	REP	#14
	LSL	A
	OR	Y0,A
	NOP
	BSET	#19,A1		; Set bits to mean video processor DAC
	NOP
	BSET	#18,A1
	JMP	<VID_BRD

CLK_DRV	CMP	#'CLK',B
	JNE	<ERR_SBN
	JMP	<CLK_BRD

VID_BRD	MOVE	A,X0
	MOVE	X:(R3)+,A	; Fourth argument is voltage value, 0 to $fff
	MOVE	#$000FFF,Y0	; Mask off just 12 bits to be sure
	AND	Y0,A
	OR	X0,A
	JSR	<XMIT_A_WORD	; Transmit A to TIM-A-STD
	JSR	<PAL_DLY	; Wait for the number to be sent
	BCLR	#3,X:PCRD	; Turn the serial clock off
	JMP	<FINISH

; For ARC32 do some trickiness to set the chip select and address bits
CLK_BRD	MOVE	A1,B
	REP	#14
	LSL	A
	MOVE	#$0E0000,X0
	AND	X0,A
	MOVE	#>7,X0
	AND	X0,B		; Get 3 least significant bits of clock #
	CMP	#0,B
	JNE	<CLK_1
	BSET	#8,A
	JMP	<BD_SET
CLK_1	CMP	#1,B
	JNE	<CLK_2
	BSET	#9,A
	JMP	<BD_SET
CLK_2	CMP	#2,B
	JNE	<CLK_3
	BSET	#10,A
	JMP	<BD_SET
CLK_3	CMP	#3,B
	JNE	<CLK_4
	BSET	#11,A
	JMP	<BD_SET
CLK_4	CMP	#4,B
	JNE	<CLK_5
	BSET	#13,A
	JMP	<BD_SET
CLK_5	CMP	#5,B
	JNE	<CLK_6
	BSET	#14,A
	JMP	<BD_SET
CLK_6	CMP	#6,B
	JNE	<CLK_7
	BSET	#15,A
	JMP	<BD_SET
CLK_7	CMP	#7,B
	JNE	<BD_SET
	BSET	#16,A

BD_SET	OR	X1,A		; Add on the board number
	NOP
	MOVE	A,X0
	MOVE	X:(R3)+,A	; Fourth argument is voltage value, 0 to $fff
	REP	#4
	LSR	A		; Convert 12 bits to 8 bits for ARC32
	MOVE	#>$FF,Y0	; Mask off just 8 bits
	AND	Y0,A
	OR	X0,A

	JSR	<XMIT_A_WORD	; Transmit A to TIM-A-STD
	JSR	<PAL_DLY	; Wait for the number to be sent
	BCLR	#3,X:PCRD	; Turn the serial clock off
	JMP	<FINISH
ERR_SBN	MOVE	X:(R3)+,A	; Read and discard the fourth argument
	BCLR	#3,X:PCRD	; Turn the serial clock off
	JMP	<ERROR

; Specify the MUX value to be output on the clock driver board
; Command syntax is  SMX  #clock_driver_board #MUX1 #MUX2
;				#clock_driver_board from 0 to 15
;				#MUX1, #MUX2 from 0 to 23

SET_MUX	BSET	#3,X:PCRD	; Turn on the serial clock
	MOVE	X:(R3)+,A	; Clock driver board number
	REP	#20
	LSL	A
	MOVE	#$003000,X0
	OR	X0,A
	NOP
	MOVE	A,X1		; Move here for storage

; Get the first MUX number
	MOVE	X:(R3)+,A	; Get the first MUX number
	JLT	ERR_SM1
	MOVE	#>24,X0		; Check for argument less than 32
	CMP	X0,A
	JGE	ERR_SM1
	MOVE	A,B
	MOVE	#>7,X0
	AND	X0,B
	MOVE	#>$18,X0
	AND	X0,A
	JNE	<SMX_1		; Test for 0 <= MUX number <= 7
	BSET	#3,B1
	JMP	<SMX_A
SMX_1	MOVE	#>$08,X0
	CMP	X0,A		; Test for 8 <= MUX number <= 15
	JNE	<SMX_2
	BSET	#4,B1
	JMP	<SMX_A
SMX_2	MOVE	#>$10,X0
	CMP	X0,A		; Test for 16 <= MUX number <= 23
	JNE	<ERR_SM1
	BSET	#5,B1
SMX_A	OR	X1,B1		; Add prefix to MUX numbers
	NOP
	MOVE	B1,Y1

; Add on the second MUX number
	MOVE	X:(R3)+,A	; Get the next MUX number
	JLT	<ERROR
	MOVE	#>24,X0		; Check for argument less than 32
	CMP	X0,A
	JGE	<ERROR
	REP	#6
	LSL	A
	NOP
	MOVE	A,B
	MOVE	#$1C0,X0
	AND	X0,B
	MOVE	#>$600,X0
	AND	X0,A
	JNE	<SMX_3		; Test for 0 <= MUX number <= 7
	BSET	#9,B1
	JMP	<SMX_B
SMX_3	MOVE	#>$200,X0
	CMP	X0,A		; Test for 8 <= MUX number <= 15
	JNE	<SMX_4
	BSET	#10,B1
	JMP	<SMX_B
SMX_4	MOVE	#>$400,X0
	CMP	X0,A		; Test for 16 <= MUX number <= 23
	JNE	<ERROR
	BSET	#11,B1
SMX_B	ADD	Y1,B		; Add prefix to MUX numbers
	NOP
	MOVE	B1,A
	JSR	<XMIT_A_WORD	; Transmit A to TIM-A-STD
	JSR	<PAL_DLY	; Delay for all this to happen
	BCLR	#3,X:PCRD	; Turn the serial clock off
	JMP	<FINISH
ERR_SM1	MOVE	X:(R3)+,A
	BCLR	#3,X:PCRD	; Turn the serial clock off
	JMP	<ERROR

; Specify subarray readout coordinates, one rectangle only
SET_SUBARRAY_SIZES
	BCLR	#ST_SA,X:<STATUS	; Subarray bit cleared until SSP is executed
	MOVE    X:(R3)+,X0
	MOVE	X0,Y:<NRBIAS		; Number of bias pixels to read
	MOVE    X:(R3)+,X0
	MOVE	X0,Y:<NSREAD		; Number of columns in subimage read
	MOVE    X:(R3)+,X0
	MOVE	X0,Y:<NPREAD		; Number of rows in subimage read	
	JMP	<FINISH

SET_SUBARRAY_POSITIONS
	MOVE	#READ_TABLE,R7
	MOVE	X:(R3)+,X0
	NOP
	MOVE	X0,Y:(R7)+	; Number of rows (parallels) to clear
	MOVE	X:(R3)+,X0
	MOVE	X0,Y:(R7)+	; Number of columns (serials) clears before
	MOVE	X:(R3)+,X0	;  the box readout
	MOVE	X0,Y:(R7)+	; Number of columns (serials) clears after	
	BSET	#ST_SA,X:<STATUS ; Subarray bit set
	JMP	<FINISH

; Select the amplifier and readout mode
;   'SOS'  Amplifier_name = '__C', '__D', '__B', '__A' or 'ALL'

SELECT_OUTPUT_SOURCE
	MOVE    X:(R3)+,Y0
	MOVE	Y0,Y:<OS
	JSR	<SEL_OS
	JMP	<FINISH
	
SEL_OS	MOVE	Y:<OS,X0		; Get amplifier(s) name
	MOVE	#'ALL',A		; All Amplifiers = readout #0 to #3
	CMP	X0,A
	JNE	<CMP_LL
	MOVE	#PARALLEL_SPLIT,X0
	MOVE	X0,Y:PARALLEL
	MOVE	#SERIAL_SKIP_SPLIT,X0
	MOVE	X0,Y:SERIAL_SKIP
	MOVE	#SERIAL_READ_SPLIT,X0
	MOVE	X0,Y:<SERIAL_READ
	BSET	#SPLIT_S,X:STATUS
	BSET	#SPLIT_P,X:STATUS
	RTS

CMP_LL	MOVE	#'__L',A		; Lower Left Amplifier = readout #0
	CMP	X0,A
	JNE	<CMP_LR
	MOVE	#PARALLEL_DOWN,X0
	MOVE	X0,Y:PARALLEL
	MOVE	#SERIAL_SKIP_LEFT,X0
	MOVE	X0,Y:SERIAL_SKIP
	MOVE	#SERIAL_READ_LEFT,X0
	MOVE	X0,Y:<SERIAL_READ
	MOVE	#$00F000,X0
	MOVE	X0,Y:SXL
	BCLR	#SPLIT_S,X:STATUS
	BCLR	#SPLIT_P,X:STATUS
	RTS

CMP_LR	MOVE	#'_2L',A		; Lower Right Amplifier = readout #1
	CMP	X0,A
	JNE	<CMP_UR
	MOVE	#PARALLEL_DOWN,X0
	MOVE	X0,Y:PARALLEL
	MOVE	#SERIAL_SKIP_RIGHT,X0
	MOVE	X0,Y:SERIAL_SKIP
	MOVE	#SERIAL_READ_RIGHT,X0
	MOVE	X0,Y:<SERIAL_READ
	MOVE	#$00F041,X0
	MOVE	X0,Y:SXR
	BCLR	#SPLIT_S,X:STATUS
	BCLR	#SPLIT_P,X:STATUS
	RTS

CMP_UR	MOVE	#'_2R',A		; Upper Right Amplifier = readout #2
	CMP	X0,A
	JNE	<CMP_UL
	MOVE	#PARALLEL_UP,X0
	MOVE	X0,Y:PARALLEL
	MOVE	#SERIAL_SKIP_RIGHT,X0
	MOVE	X0,Y:SERIAL_SKIP
	MOVE	#SERIAL_READ_RIGHT,X0
	MOVE	X0,Y:<SERIAL_READ
	MOVE	#$00F082,X0
	MOVE	X0,Y:SXR
	BCLR	#SPLIT_S,X:STATUS
	BCLR	#SPLIT_P,X:STATUS
	RTS

CMP_UL	MOVE	#'__R',A		; Upper Left Amplifier = readout #3
	CMP	X0,A
	JNE	<ERROR
	MOVE	#PARALLEL_UP,X0
	MOVE	X0,Y:PARALLEL
	MOVE	#SERIAL_SKIP_LEFT,X0
	MOVE	X0,Y:SERIAL_SKIP
	MOVE	#SERIAL_READ_LEFT,X0
	MOVE	X0,Y:<SERIAL_READ
	MOVE	#$00F0C3,X0
	MOVE	X0,Y:SXL
	BCLR	#SPLIT_S,X:STATUS
	BCLR	#SPLIT_P,X:STATUS
	RTS

SUBSTRATE_BIAS_ON
	BSET	#3,X:PCRD		; Turn on the serial clock
	JSR	<PAL_DLY
	MOVE	#BIAS_ON,R0
	NOP
	NOP
	MOVE	Y:(R0)+,A		; Read the table entry
	JSR	<XMIT_A_WORD		; Transmit it to TIM-A-STD
	JSR	<PAL_DLY
	MOVE	Y:(R0),A		; Read the table entry
	JSR	<XMIT_A_WORD		; Transmit it to TIM-A-STD
	JSR	<PAL_DLY
	BCLR	#3,X:PCRD		; Turn off the serial clock
	JMP	<FINISH
	
SUBSTRATE_BIAS_OFF
	BSET	#3,X:PCRD		; Turn on the serial clock
	JSR	<PAL_DLY
	MOVE	#BIAS_OFF,R0
	NOP
	NOP
	MOVE	Y:(R0)+,A		; Read the table entry
	JSR	<XMIT_A_WORD		; Transmit it to TIM-A-STD
	JSR	<PAL_DLY
	MOVE	Y:(R0),A		; Read the table entry
	JSR	<XMIT_A_WORD		; Transmit it to TIM-A-STD
	JSR	<PAL_DLY
	BCLR	#3,X:PCRD		; Turn off the serial clock
	JMP	<FINISH

; **********************************************************************************************
; Set the video offset for the ARC-48 8-channel CCD video board
; SVO  Board  DAC  voltage Board number is from 0 to 15
; DAC number from 0 to 7
; voltage number is from 0 to 16,383 (14 bits)

SET_VIDEO_OFFSET
	BSET #3,X:PCRD ; Turn on the serial clock
	MOVE X:(R3)+,A ; First argument is board number, 0 to 15
	TST A
	JLT <ERR_SV1
	CMP #15,A
	JGT <ERR_SV1
	LSL #20,A
	NOP
	MOVE A,X1 ; Board number is in X1 bits #23-20
	MOVE X:(R3)+,A ; Second argument is the video channel number
	CMP #0,A
	JNE <CMP1
	MOVE #$0E0018,A ; Magic number for channel #0
	OR X1,A
	JMP <SVO_XMT
CMP1 CMP #1,A
	JNE <CMP2
	MOVE #$0E0019,A ; Magic number for channel #1
	OR X1,A
	JMP <SVO_XMT
CMP2 CMP #2,A
	JNE <CMP3
	MOVE #$0E0028,A ; Magic number for channel #2
	OR X1,A
	JMP <SVO_XMT
CMP3 CMP #3,A
	JNE <CMP4
	MOVE #$0E0029,A ; Magic number for channel #3
	OR X1,A
	JMP <SVO_XMT
CMP4 CMP #4,A
	JNE <CMP5
	MOVE #$0E0048,A ; Magic number for channel #4
	OR X1,A
	JMP <SVO_XMT
CMP5 CMP #5,A
	JNE <CMP6
	MOVE #$0E0049,A ; Magic number for channel #5
	OR X1,A
	JMP <SVO_XMT
CMP6 CMP #6,A
	JNE <CMP7
	MOVE #$0E0088,A ; Magic number for channel #6
	OR X1,A
	JMP <SVO_XMT
CMP7 CMP #7,A
	JNE <ERR_SV2
	MOVE #$0E0089,A ; Magic number for channel #7
	OR X1,A

SVO_XMT NOP
	MOVE A1,Y:0

	JSR <XMIT_A_WORD ; Transmit A to TIM-A-STD
	JSR <PAL_DLY ; Wait for the number to be sent
	MOVE X:(R3)+,A ; Third argument is the DAC voltage number
	TST A
	JLT <ERR_SV3 ; Voltage number needs to be positive
	CMP #$3FFF,A ; Voltage number needs to be 14 bits
	JGT <ERR_SV3
	OR X1,A ; Add in the board number
	OR #$0FC000,A
	NOP
	JSR <XMIT_A_WORD ; Transmit A to TIM-A-STD
	JSR <PAL_DLY
	BCLR #3,X:PCRD ; Turn off the serial clock
	JMP <FINISH
ERR_SV1 BCLR #3,X:PCRD ; Turn off the serial clock
	MOVE X:(R3)+,A
	MOVE X:(R3)+,A
	JMP <ERROR
ERR_SV2 BCLR #3,X:PCRD ; Turn off the serial clock
	MOVE X:(R3)+,A
	JMP <ERROR
ERR_SV3 BCLR #3,X:PCRD ; Turn off the serial clock
	JMP <ERROR

;  *************** Routines from "timCCDmisc.asm"  *******************

; Set the video processor gain and integrator speed for all video boards
;  Command syntax is  SGN  #GAIN  #SPEED, #GAIN = 1, 2, 5 or 10	
;					  #SPEED = 0 for slow, 1 for fast
SET_GAIN
	JSR		<SAVE_ALL_REGISTERS
	MOVE	B1,X1		; Save second argument B1 for later use
	BSET	#3,X:PCRD	; Turn on the serial clock
	MOVE	#>1,X0		; Check for gain = x1
	CMP		X0,A		; Gain value (1,2,5 or 10) is in Acc A1
	JNE		<STG2
	MOVE	#>$77,B
	JMP		<STG_A
STG2
	MOVE	#>2,X0		; Check for gain = x2
	CMP		X0,A
	JNE		<STG5
	MOVE	#>$BB,B
	JMP		<STG_A
STG5
	MOVE	#>5,X0		; Check for gain = x5
	CMP		X0,A
	JNE		<STG10
	MOVE	#>$DD,B
	JMP		<STG_A
STG10	
	MOVE	#>10,X0		; Check for gain = x10
	CMP		X0,A
	JNE		<ERROR
	MOVE	#>$EE,B

STG_A
	MOVE	X1,A		; Integrator Speed (0 for slow, 1 for fast)
	NOP
	JCLR	#0,A1,STG_B
	BSET	#8,B1
	NOP
	BSET	#9,B1
STG_B
	MOVE	#$0C3C00,X0
	OR		X0,B

; Send this same value to 15 video processor boards whether they exist or not
	MOVE	#$100000,X0		; Increment value
	DO		#15,STG_LOOP
	MOVE	B1,A1
	JSR		<XMIT_A_WORD	; Transmit A to TIM-A-STD
	JSR		<PAL_DLY		; Wait for SSI and PAL to be empty
	ADD		X0,B			; Increment the video processor board number
	NOP
STG_LOOP
	JSR		<RESTORE_ALL_REGISTERS
	BCLR	#3,X:PCRD		; Turn the serial clock off
	JMP		<FINISH

ERR_SGN
	JSR		<RESTORE_ALL_REGISTERS
	BCLR	#3,X:PCRD		; Turn the serial clock off
	JMP		<ERROR

