Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  /home/bearing/Documents/OWL/tim/tim.asm  Page 1



1                                 COMMENT *
2      
3                          This file is used to generate boot DSP code for the 250 MHz fiber optic
4                                  timing board using a DSP56303 as its main processor. It supports
5                                  frame transfer and binning.
6      
7                                  *
8                                    PAGE    132                               ; Printronix page width - 132 columns
9      
10                         ; Include the boot file so addressing is easy
11                                   INCLUDE "timboot.asm"
12                         ;  This file is used to generate boot DSP code for the Gen III 250 MHz fiber
13                         ;       optic timing board = ARC22 using a DSP56303 as its main processor.
14     
15                         ; Various addressing control registers
16        FFFFFB           BCR       EQU     $FFFFFB                           ; Bus Control Register
17        FFFFF9           AAR0      EQU     $FFFFF9                           ; Address Attribute Register, channel 0
18        FFFFF8           AAR1      EQU     $FFFFF8                           ; Address Attribute Register, channel 1
19        FFFFF7           AAR2      EQU     $FFFFF7                           ; Address Attribute Register, channel 2
20        FFFFF6           AAR3      EQU     $FFFFF6                           ; Address Attribute Register, channel 3
21        FFFFFD           PCTL      EQU     $FFFFFD                           ; PLL control register
22        FFFFFE           IPRP      EQU     $FFFFFE                           ; Interrupt Priority register - Peripheral
23        FFFFFF           IPRC      EQU     $FFFFFF                           ; Interrupt Priority register - Core
24     
25                         ; Port E is the Synchronous Communications Interface (SCI) port
26        FFFF9F           PCRE      EQU     $FFFF9F                           ; Port Control Register
27        FFFF9E           PRRE      EQU     $FFFF9E                           ; Port Direction Register
28        FFFF9D           PDRE      EQU     $FFFF9D                           ; Port Data Register
29        FFFF9C           SCR       EQU     $FFFF9C                           ; SCI Control Register
30        FFFF9B           SCCR      EQU     $FFFF9B                           ; SCI Clock Control Register
31     
32        FFFF9A           SRXH      EQU     $FFFF9A                           ; SCI Receive Data Register, High byte
33        FFFF99           SRXM      EQU     $FFFF99                           ; SCI Receive Data Register, Middle byte
34        FFFF98           SRXL      EQU     $FFFF98                           ; SCI Receive Data Register, Low byte
35     
36        FFFF97           STXH      EQU     $FFFF97                           ; SCI Transmit Data register, High byte
37        FFFF96           STXM      EQU     $FFFF96                           ; SCI Transmit Data register, Middle byte
38        FFFF95           STXL      EQU     $FFFF95                           ; SCI Transmit Data register, Low byte
39     
40        FFFF94           STXA      EQU     $FFFF94                           ; SCI Transmit Address Register
41        FFFF93           SSR       EQU     $FFFF93                           ; SCI Status Register
42     
43        000009           SCITE     EQU     9                                 ; X:SCR bit set to enable the SCI transmitter
44        000008           SCIRE     EQU     8                                 ; X:SCR bit set to enable the SCI receiver
45        000000           TRNE      EQU     0                                 ; This is set in X:SSR when the transmitter
46                                                                             ;  shift and data registers are both empty
47        000001           TDRE      EQU     1                                 ; This is set in X:SSR when the transmitter
48                                                                             ;  data register is empty
49        000002           RDRF      EQU     2                                 ; X:SSR bit set when receiver register is full
50        00000F           SELSCI    EQU     15                                ; 1 for SCI to backplane, 0 to front connector
51     
52     
53                         ; ESSI Flags
54        000006           TDE       EQU     6                                 ; Set when transmitter data register is empty
55        000007           RDF       EQU     7                                 ; Set when receiver is full of data
56        000010           TE        EQU     16                                ; Transmitter enable
57     
58                         ; Phase Locked Loop initialization
59        050003           PLL_INIT  EQU     $050003                           ; PLL = 25 MHz x 2 = 100 MHz
60     
61                         ; Port B general purpose I/O
62        FFFFC4           HPCR      EQU     $FFFFC4                           ; Control register (bits 1-6 cleared for GPIO)
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 2



63        FFFFC9           HDR       EQU     $FFFFC9                           ; Data register
64        FFFFC8           HDDR      EQU     $FFFFC8                           ; Data Direction Register bits (=1 for output)
65     
66                         ; Port C is Enhanced Synchronous Serial Port 0 = ESSI0
67        FFFFBF           PCRC      EQU     $FFFFBF                           ; Port C Control Register
68        FFFFBE           PRRC      EQU     $FFFFBE                           ; Port C Data direction Register
69        FFFFBD           PDRC      EQU     $FFFFBD                           ; Port C GPIO Data Register
70        FFFFBC           TX00      EQU     $FFFFBC                           ; Transmit Data Register #0
71        FFFFB8           RX0       EQU     $FFFFB8                           ; Receive data register
72        FFFFB7           SSISR0    EQU     $FFFFB7                           ; Status Register
73        FFFFB6           CRB0      EQU     $FFFFB6                           ; Control Register B
74        FFFFB5           CRA0      EQU     $FFFFB5                           ; Control Register A
75     
76                         ; Port D is Enhanced Synchronous Serial Port 1 = ESSI1
77        FFFFAF           PCRD      EQU     $FFFFAF                           ; Port D Control Register
78        FFFFAE           PRRD      EQU     $FFFFAE                           ; Port D Data direction Register
79        FFFFAD           PDRD      EQU     $FFFFAD                           ; Port D GPIO Data Register
80        FFFFAC           TX10      EQU     $FFFFAC                           ; Transmit Data Register 0
81        FFFFA7           SSISR1    EQU     $FFFFA7                           ; Status Register
82        FFFFA6           CRB1      EQU     $FFFFA6                           ; Control Register B
83        FFFFA5           CRA1      EQU     $FFFFA5                           ; Control Register A
84     
85                         ; Timer module addresses
86        FFFF8F           TCSR0     EQU     $FFFF8F                           ; Timer control and status register
87        FFFF8E           TLR0      EQU     $FFFF8E                           ; Timer load register = 0
88        FFFF8D           TCPR0     EQU     $FFFF8D                           ; Timer compare register = exposure time
89        FFFF8C           TCR0      EQU     $FFFF8C                           ; Timer count register = elapsed time
90        FFFF83           TPLR      EQU     $FFFF83                           ; Timer prescaler load register => milliseconds
91        FFFF82           TPCR      EQU     $FFFF82                           ; Timer prescaler count register
92        000000           TIM_BIT   EQU     0                                 ; Set to enable the timer
93        000009           TRM       EQU     9                                 ; Set to enable the timer preloading
94        000015           TCF       EQU     21                                ; Set when timer counter = compare register
95     
96                         ; Board specific addresses and constants
97        FFFFF1           RDFO      EQU     $FFFFF1                           ; Read incoming fiber optic data byte
98        FFFFF2           WRFO      EQU     $FFFFF2                           ; Write fiber optic data replies
99        FFFFF3           WRSS      EQU     $FFFFF3                           ; Write switch state
100       FFFFF5           WRLATCH   EQU     $FFFFF5                           ; Write to a latch
101       010000           RDAD      EQU     $010000                           ; Read A/D values into the DSP
102       000009           EF        EQU     9                                 ; Serial receiver empty flag
103    
104                        ; DSP port A bit equates
105       000000           PWROK     EQU     0                                 ; Power control board says power is OK
106       000001           LED1      EQU     1                                 ; Control one of two LEDs
107       000002           LVEN      EQU     2                                 ; Low voltage power enable
108       000003           HVEN      EQU     3                                 ; High voltage power enable
109       00000E           SSFHF     EQU     14                                ; Switch state FIFO half full flag
110       00000A           EXT_IN0   EQU     10                                ; External digital I/O to the timing board
111       00000B           EXT_IN1   EQU     11
112       00000C           EXT_OUT0  EQU     12
113       00000D           EXT_OUT1  EQU     13
114    
115                        ; Port D equate
116       000001           SSFEF     EQU     1                                 ; Switch state FIFO empty flag
117    
118                        ; Other equates
119       000002           WRENA     EQU     2                                 ; Enable writing to the EEPROM
120    
121                        ; Latch U25 bit equates
122       000000           CDAC      EQU     0                                 ; Clear the analog board DACs
123       000002           ENCK      EQU     2                                 ; Enable the clock outputs
124       000004           SHUTTER   EQU     4                                 ; Control the shutter
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 3



125       000005           TIM_U_RST EQU     5                                 ; Reset the utility board
126    
127                        ; Software status bits, defined at X:<STATUS = X:0
128       000000           ST_RCV    EQU     0                                 ; Set to indicate word is from SCI = utility board
129       000002           IDLMODE   EQU     2                                 ; Set if need to idle after readout
130       000003           ST_SHUT   EQU     3                                 ; Set to indicate shutter is closed, clear for open
131       000004           ST_RDC    EQU     4                                 ; Set if executing 'RDC' command - reading out
132       000005           SPLIT_S   EQU     5                                 ; Set if split serial
133       000006           SPLIT_P   EQU     6                                 ; Set if split parallel
134       000007           MPP       EQU     7                                 ; Set if parallels are in MPP mode
135       000008           NOT_CLR   EQU     8                                 ; Set if not to clear CCD before exposure
136       00000A           TST_IMG   EQU     10                                ; Set if controller is to generate a test image
137       00000B           SHUT      EQU     11                                ; Set if opening shutter at beginning of exposure
138       00000C           ST_DITH   EQU     12                                ; Set if to dither during exposure
139       00000D           ST_SYNC   EQU     13                                ; Set if starting exposure on SYNC = high signal
140       00000E           ST_CNRD   EQU     14                                ; Set if in continous readout mode
141       00000F           ST_DIRTY  EQU     15                                ; Set if waveform tables need to be updated
142       000010           ST_SA     EQU     16                                ; Set if in subarray readout mode
143    
144                        ; Address for the table containing the incoming SCI words
145       000400           SCI_TABLE EQU     $400
146    
147    
148                        ; Specify controller configuration bits of the X:STATUS word
149                        ;   to describe the software capabilities of this application file
150                        ; The bit is set (=1) if the capability is supported by the controller
151    
152    
153                                COMMENT *
154    
155                        BIT #'s         FUNCTION
156                        2,1,0           Video Processor
157                                                000     ARC41, CCD Rev. 3
158                                                001     CCD Gen I
159                                                010     ARC42, dual readout CCD
160                                                011     ARC44, 4-readout IR coadder
161                                                100     ARC45. dual readout CCD
162                                                101     ARC46 = 8-channel IR
163                                                110     ARC48 = 8 channel CCD
164                                                111     ARC47 = 4-channel CCD
165    
166                        4,3             Timing Board
167                                                00      ARC20, Rev. 4, Gen II
168                                                01      Gen I
169                                                10      ARC22, Gen III, 250 MHz
170    
171                        6,5             Utility Board
172                                                00      No utility board
173                                                01      ARC50
174    
175                        7               Shutter
176                                                0       No shutter support
177                                                1       Yes shutter support
178    
179                        9,8             Temperature readout
180                                                00      No temperature readout
181                                                01      Polynomial Diode calibration
182                                                10      Linear temperature sensor calibration
183    
184                        10              Subarray readout
185                                                0       Not supported
186                                                1       Yes supported
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 4



187    
188                        11              Binning
189                                                0       Not supported
190                                                1       Yes supported
191    
192                        12              Split-Serial readout
193                                                0       Not supported
194                                                1       Yes supported
195    
196                        13              Split-Parallel readout
197                                                0       Not supported
198                                                1       Yes supported
199    
200                        14              MPP = Inverted parallel clocks
201                                                0       Not supported
202                                                1       Yes supported
203    
204                        16,15           Clock Driver Board
205                                                00      ARC30 or ARC31
206                                                01      ARC32, CCD and IR
207                                                11      No clock driver board (Gen I)
208    
209                        19,18,17                Special implementations
210                                                000     Somewhere else
211                                                001     Mount Laguna Observatory
212                                                010     NGST Aladdin
213                                                xxx     Other
214                                *
215    
216                        CCDVIDREV3B
217       000000                     EQU     $000000                           ; CCD Video Processor Rev. 3
218       000000           ARC41     EQU     $000000
219       000001           VIDGENI   EQU     $000001                           ; CCD Video Processor Gen I
220       000002           IRREV4    EQU     $000002                           ; IR Video Processor Rev. 4
221       000002           ARC42     EQU     $000002
222       000003           COADDER   EQU     $000003                           ; IR Coadder
223       000003           ARC44     EQU     $000003
224       000004           CCDVIDREV5 EQU    $000004                           ; Differential input CCD video Rev. 5
225       000004           ARC45     EQU     $000004
226       000005           ARC46     EQU     $000005                           ; 8-channel IR video board
227       000006           ARC48     EQU     $000006                           ; 8-channel CCD video board
228       000007           ARC47     EQU     $000007                           ; 4-channel CCD video board
229       000000           TIMREV4   EQU     $000000                           ; Timing Revision 4 = 50 MHz
230       000000           ARC20     EQU     $000000
231       000008           TIMGENI   EQU     $000008                           ; Timing Gen I = 40 MHz
232       000010           TIMREV5   EQU     $000010                           ; Timing Revision 5 = 250 MHz
233       000010           ARC22     EQU     $000010
234       008000           ARC32     EQU     $008000                           ; CCD & IR clock driver board
235       000020           UTILREV3  EQU     $000020                           ; Utility Rev. 3 supported
236       000020           ARC50     EQU     $000020
237       000080           SHUTTER_CC EQU    $000080                           ; Shutter supported
238       000100           TEMP_POLY EQU     $000100                           ; Polynomial calibration
239                        TEMP_LINEAR
240       000200                     EQU     $000200                           ; Linear calibration
241       000400           SUBARRAY  EQU     $000400                           ; Subarray readout supported
242       000800           BINNING   EQU     $000800                           ; Binning supported
243                        SPLIT_SERIAL
244       001000                     EQU     $001000                           ; Split serial supported
245                        SPLIT_PARALLEL
246       002000                     EQU     $002000                           ; Split parallel supported
247       004000           MPP_CC    EQU     $004000                           ; Inverted clocks supported
248       018000           CLKDRVGENI EQU    $018000                           ; No clock driver board - Gen I
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 5



249       020000           MLO       EQU     $020000                           ; Set if Mount Laguna Observatory
250       040000           NGST      EQU     $040000                           ; NGST Aladdin implementation
251       100000           CONT_RD   EQU     $100000                           ; Continuous readout implemented
252    
253                        ; Special address for two words for the DSP to bootstrap code from the EEPROM
254                                  IF      @SCP("HOST","ROM")
261                                  ENDIF
262    
263                                  IF      @SCP("HOST","HOST")
264       P:000000 P:000000                   ORG     P:0,P:0
265       P:000000 P:000000 0C0190            JMP     <INIT
266       P:000001 P:000001 000000            NOP
267                                           ENDIF
268    
269                                 ;  This ISR receives serial words a byte at a time over the asynchronous
270                                 ;    serial link (SCI) and squashes them into a single 24-bit word
271       P:000002 P:000002 602400  SCI_RCV   MOVE              R0,X:<SAVE_R0           ; Save R0
272       P:000003 P:000003 052139            MOVEC             SR,X:<SAVE_SR           ; Save Status Register
273       P:000004 P:000004 60A700            MOVE              X:<SCI_R0,R0            ; Restore R0 = pointer to SCI receive regist
er
274       P:000005 P:000005 542300            MOVE              A1,X:<SAVE_A1           ; Save A1
275       P:000006 P:000006 452200            MOVE              X1,X:<SAVE_X1           ; Save X1
276       P:000007 P:000007 54A600            MOVE              X:<SCI_A1,A1            ; Get SRX value of accumulator contents
277       P:000008 P:000008 45E000            MOVE              X:(R0),X1               ; Get the SCI byte
278       P:000009 P:000009 0AD041            BCLR    #1,R0                             ; Test for the address being $FFF6 = last by
te
279       P:00000A P:00000A 000000            NOP
280       P:00000B P:00000B 000000            NOP
281       P:00000C P:00000C 000000            NOP
282       P:00000D P:00000D 205862            OR      X1,A      (R0)+                   ; Add the byte into the 24-bit word
283       P:00000E P:00000E 0E0013            JCC     <MID_BYT                          ; Not the last byte => only restore register
s
284       P:00000F P:00000F 545C00  END_BYT   MOVE              A1,X:(R4)+              ; Put the 24-bit word into the SCI buffer
285       P:000010 P:000010 60F400            MOVE              #SRXL,R0                ; Re-establish first address of SCI interfac
e
                            FFFF98
286       P:000012 P:000012 2C0000            MOVE              #0,A1                   ; For zeroing out SCI_A1
287       P:000013 P:000013 602700  MID_BYT   MOVE              R0,X:<SCI_R0            ; Save the SCI receiver address
288       P:000014 P:000014 542600            MOVE              A1,X:<SCI_A1            ; Save A1 for next interrupt
289       P:000015 P:000015 05A139            MOVEC             X:<SAVE_SR,SR           ; Restore Status Register
290       P:000016 P:000016 54A300            MOVE              X:<SAVE_A1,A1           ; Restore A1
291       P:000017 P:000017 45A200            MOVE              X:<SAVE_X1,X1           ; Restore X1
292       P:000018 P:000018 60A400            MOVE              X:<SAVE_R0,R0           ; Restore R0
293       P:000019 P:000019 000004            RTI                                       ; Return from interrupt service
294    
295                                 ; Clear error condition and interrupt on SCI receiver
296       P:00001A P:00001A 077013  CLR_ERR   MOVEP             X:SSR,X:RCV_ERR         ; Read SCI status register
                            000025
297       P:00001C P:00001C 077018            MOVEP             X:SRXL,X:RCV_ERR        ; This clears any error
                            000025
298       P:00001E P:00001E 000004            RTI
299    
300       P:00001F P:00001F                   DC      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
301       P:000030 P:000030                   DC      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
302       P:000040 P:000040                   DC      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
303    
304                                 ; Tune the table so the following instruction is at P:$50 exactly.
305       P:000050 P:000050 0D0002            JSR     SCI_RCV                           ; SCI receive data interrupt
306       P:000051 P:000051 000000            NOP
307       P:000052 P:000052 0D001A            JSR     CLR_ERR                           ; SCI receive error interrupt
308       P:000053 P:000053 000000            NOP
309    
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 6



310                                 ; *******************  Command Processing  ******************
311    
312                                 ; Read the header and check it for self-consistency
313       P:000054 P:000054 609F00  START     MOVE              X:<IDL_ADR,R0
314       P:000055 P:000055 018FA0            JSET    #TIM_BIT,X:TCSR0,EXPOSING         ; If exposing go check the timer
                            000348
315       P:000057 P:000057 0A00A4            JSET    #ST_RDC,X:<STATUS,CONTINUE_READING
                            000245
316       P:000059 P:000059 0AE080            JMP     (R0)
317    
318       P:00005A P:00005A 330700  TST_RCV   MOVE              #<COM_BUF,R3
319       P:00005B P:00005B 0D00A5            JSR     <GET_RCV
320       P:00005C P:00005C 0E005B            JCC     *-1
321    
322                                 ; Check the header and read all the remaining words in the command
323       P:00005D P:00005D 0C00FF  PRC_RCV   JMP     <CHK_HDR                          ; Update HEADER and NWORDS
324       P:00005E P:00005E 578600  PR_RCV    MOVE              X:<NWORDS,B             ; Read this many words total in the command
325       P:00005F P:00005F 000000            NOP
326       P:000060 P:000060 01418C            SUB     #1,B                              ; We've already read the header
327       P:000061 P:000061 000000            NOP
328       P:000062 P:000062 06CF00            DO      B,RD_COM
                            00006A
329       P:000064 P:000064 205B00            MOVE              (R3)+                   ; Increment past what's been read already
330       P:000065 P:000065 0B0080  GET_WRD   JSCLR   #ST_RCV,X:STATUS,CHK_FO
                            0000A9
331       P:000067 P:000067 0B00A0            JSSET   #ST_RCV,X:STATUS,CHK_SCI
                            0000D5
332       P:000069 P:000069 0E0065            JCC     <GET_WRD
333       P:00006A P:00006A 000000            NOP
334       P:00006B P:00006B 330700  RD_COM    MOVE              #<COM_BUF,R3            ; Restore R3 = beginning of the command
335    
336                                 ; Is this command for the timing board?
337       P:00006C P:00006C 448500            MOVE              X:<HEADER,X0
338       P:00006D P:00006D 579B00            MOVE              X:<DMASK,B
339       P:00006E P:00006E 459A4E            AND     X0,B      X:<TIM_DRB,X1           ; Extract destination byte
340       P:00006F P:00006F 20006D            CMP     X1,B                              ; Does header = timing board number?
341       P:000070 P:000070 0EA080            JEQ     <COMMAND                          ; Yes, process it here
342       P:000071 P:000071 0E909D            JLT     <FO_XMT                           ; Send it to fiber optic transmitter
343    
344                                 ; Transmit the command to the utility board over the SCI port
345       P:000072 P:000072 060600            DO      X:<NWORDS,DON_XMT                 ; Transmit NWORDS
                            00007E
346       P:000074 P:000074 60F400            MOVE              #STXL,R0                ; SCI first byte address
                            FFFF95
347       P:000076 P:000076 44DB00            MOVE              X:(R3)+,X0              ; Get the 24-bit word to transmit
348       P:000077 P:000077 060380            DO      #3,SCI_SPT
                            00007D
349       P:000079 P:000079 019381            JCLR    #TDRE,X:SSR,*                     ; Continue ONLY if SCI XMT is empty
                            000079
350       P:00007B P:00007B 445800            MOVE              X0,X:(R0)+              ; Write to SCI, byte pointer + 1
351       P:00007C P:00007C 000000            NOP                                       ; Delay for the status flag to be set
352       P:00007D P:00007D 000000            NOP
353                                 SCI_SPT
354       P:00007E P:00007E 000000            NOP
355                                 DON_XMT
356       P:00007F P:00007F 0C0054            JMP     <START
357    
358                                 ; Process the receiver entry - is it in the command table ?
359       P:000080 P:000080 0203DF  COMMAND   MOVE              X:(R3+1),B              ; Get the command
360       P:000081 P:000081 205B00            MOVE              (R3)+
361       P:000082 P:000082 205B00            MOVE              (R3)+                   ; Point R3 to the first argument
362       P:000083 P:000083 302800            MOVE              #<COM_TBL,R0            ; Get the command table starting address
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 7



363       P:000084 P:000084 062180            DO      #NUM_COM,END_COM                  ; Loop over the command table
                            00008B
364       P:000086 P:000086 47D800            MOVE              X:(R0)+,Y1              ; Get the command table entry
365       P:000087 P:000087 62E07D            CMP     Y1,B      X:(R0),R2               ; Does receiver = table entries address?
366       P:000088 P:000088 0E208B            JNE     <NOT_COM                          ; No, keep looping
367       P:000089 P:000089 00008C            ENDDO                                     ; Restore the DO loop system registers
368       P:00008A P:00008A 0AE280            JMP     (R2)                              ; Jump execution to the command
369       P:00008B P:00008B 205800  NOT_COM   MOVE              (R0)+                   ; Increment the register past the table addr
ess
370                                 END_COM
371       P:00008C P:00008C 0C008D            JMP     <ERROR                            ; The command is not in the table
372    
373                                 ; It's not in the command table - send an error message
374       P:00008D P:00008D 479D00  ERROR     MOVE              X:<ERR,Y1               ; Send the message - there was an error
375       P:00008E P:00008E 0C0090            JMP     <FINISH1                          ; This protects against unknown commands
376    
377                                 ; Send a reply packet - header and reply
378       P:00008F P:00008F 479800  FINISH    MOVE              X:<DONE,Y1              ; Send 'DON' as the reply
379       P:000090 P:000090 578500  FINISH1   MOVE              X:<HEADER,B             ; Get header of incoming command
380       P:000091 P:000091 469C00            MOVE              X:<SMASK,Y0             ; This was the source byte, and is to
381       P:000092 P:000092 330700            MOVE              #<COM_BUF,R3            ;     become the destination byte
382       P:000093 P:000093 46935E            AND     Y0,B      X:<TWO,Y0
383       P:000094 P:000094 0C1ED1            LSR     #8,B                              ; Shift right eight bytes, add it to the
384       P:000095 P:000095 460600            MOVE              Y0,X:<NWORDS            ;     header, and put 2 as the number
385       P:000096 P:000096 469958            ADD     Y0,B      X:<SBRD,Y0              ;     of words in the string
386       P:000097 P:000097 200058            ADD     Y0,B                              ; Add source board's header, set Y1 for abov
e
387       P:000098 P:000098 000000            NOP
388       P:000099 P:000099 575B00            MOVE              B,X:(R3)+               ; Put the new header on the transmitter stac
k
389       P:00009A P:00009A 475B00            MOVE              Y1,X:(R3)+              ; Put the argument on the transmitter stack
390       P:00009B P:00009B 570500            MOVE              B,X:<HEADER
391       P:00009C P:00009C 0C006B            JMP     <RD_COM                           ; Decide where to send the reply, and do it
392    
393                                 ; Transmit words to the host computer over the fiber optics link
394       P:00009D P:00009D 63F400  FO_XMT    MOVE              #COM_BUF,R3
                            000007
395       P:00009F P:00009F 060600            DO      X:<NWORDS,DON_FFO                 ; Transmit all the words in the command
                            0000A3
396       P:0000A1 P:0000A1 57DB00            MOVE              X:(R3)+,B
397       P:0000A2 P:0000A2 0D00EB            JSR     <XMT_WRD
398       P:0000A3 P:0000A3 000000            NOP
399       P:0000A4 P:0000A4 0C0054  DON_FFO   JMP     <START
400    
401                                 ; Check for commands from the fiber optic FIFO and the utility board (SCI)
402       P:0000A5 P:0000A5 0D00A9  GET_RCV   JSR     <CHK_FO                           ; Check for fiber optic command from FIFO
403       P:0000A6 P:0000A6 0E80A8            JCS     <RCV_RTS                          ; If there's a command, check the header
404       P:0000A7 P:0000A7 0D00D5            JSR     <CHK_SCI                          ; Check for an SCI command
405       P:0000A8 P:0000A8 00000C  RCV_RTS   RTS
406    
407                                 ; Because of FIFO metastability require that EF be stable for two tests
408       P:0000A9 P:0000A9 0A8989  CHK_FO    JCLR    #EF,X:HDR,TST2                    ; EF = Low,  Low  => CLR SR, return
                            0000AC
409       P:0000AB P:0000AB 0C00AF            JMP     <TST3                             ;      High, Low  => try again
410       P:0000AC P:0000AC 0A8989  TST2      JCLR    #EF,X:HDR,CLR_CC                  ;      Low,  High => try again
                            0000D1
411       P:0000AE P:0000AE 0C00A9            JMP     <CHK_FO                           ;      High, High => read FIFO
412       P:0000AF P:0000AF 0A8989  TST3      JCLR    #EF,X:HDR,CHK_FO
                            0000A9
413    
414       P:0000B1 P:0000B1 08F4BB            MOVEP             #$028FE2,X:BCR          ; Slow down RDFO access
                            028FE2
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 8



415       P:0000B3 P:0000B3 000000            NOP
416       P:0000B4 P:0000B4 000000            NOP
417       P:0000B5 P:0000B5 5FF000            MOVE                          Y:RDFO,B
                            FFFFF1
418       P:0000B7 P:0000B7 2B0000            MOVE              #0,B2
419       P:0000B8 P:0000B8 0140CE            AND     #$FF,B
                            0000FF
420       P:0000BA P:0000BA 0140CD            CMP     #>$AC,B                           ; It must be $AC to be a valid word
                            0000AC
421       P:0000BC P:0000BC 0E20D1            JNE     <CLR_CC
422       P:0000BD P:0000BD 4EF000            MOVE                          Y:RDFO,Y0   ; Read the MS byte
                            FFFFF1
423       P:0000BF P:0000BF 0C1951            INSERT  #$008010,Y0,B
                            008010
424       P:0000C1 P:0000C1 4EF000            MOVE                          Y:RDFO,Y0   ; Read the middle byte
                            FFFFF1
425       P:0000C3 P:0000C3 0C1951            INSERT  #$008008,Y0,B
                            008008
426       P:0000C5 P:0000C5 4EF000            MOVE                          Y:RDFO,Y0   ; Read the LS byte
                            FFFFF1
427       P:0000C7 P:0000C7 0C1951            INSERT  #$008000,Y0,B
                            008000
428       P:0000C9 P:0000C9 000000            NOP
429       P:0000CA P:0000CA 516300            MOVE              B0,X:(R3)               ; Put the word into COM_BUF
430       P:0000CB P:0000CB 0A0000            BCLR    #ST_RCV,X:<STATUS                 ; Its a command from the host computer
431       P:0000CC P:0000CC 000000  SET_CC    NOP
432       P:0000CD P:0000CD 0AF960            BSET    #0,SR                             ; Valid word => SR carry bit = 1
433       P:0000CE P:0000CE 08F4BB            MOVEP             #$028FE1,X:BCR          ; Restore RDFO access
                            028FE1
434       P:0000D0 P:0000D0 00000C            RTS
435       P:0000D1 P:0000D1 0AF940  CLR_CC    BCLR    #0,SR                             ; Not valid word => SR carry bit = 0
436       P:0000D2 P:0000D2 08F4BB            MOVEP             #$028FE1,X:BCR          ; Restore RDFO access
                            028FE1
437       P:0000D4 P:0000D4 00000C            RTS
438    
439                                 ; Test the SCI (= synchronous communications interface) for new words
440       P:0000D5 P:0000D5 44F000  CHK_SCI   MOVE              X:(SCI_TABLE+33),X0
                            000421
441       P:0000D7 P:0000D7 228E00            MOVE              R4,A
442       P:0000D8 P:0000D8 209000            MOVE              X0,R0
443       P:0000D9 P:0000D9 200045            CMP     X0,A
444       P:0000DA P:0000DA 0EA0D1            JEQ     <CLR_CC                           ; There is no new SCI word
445       P:0000DB P:0000DB 44D800            MOVE              X:(R0)+,X0
446       P:0000DC P:0000DC 446300            MOVE              X0,X:(R3)
447       P:0000DD P:0000DD 220E00            MOVE              R0,A
448       P:0000DE P:0000DE 0140C5            CMP     #(SCI_TABLE+32),A                 ; Wrap it around the circular
                            000420
449       P:0000E0 P:0000E0 0EA0E4            JEQ     <INIT_PROCESSED_SCI               ;   buffer boundary
450       P:0000E1 P:0000E1 547000            MOVE              A1,X:(SCI_TABLE+33)
                            000421
451       P:0000E3 P:0000E3 0C00E9            JMP     <SCI_END
452                                 INIT_PROCESSED_SCI
453       P:0000E4 P:0000E4 56F400            MOVE              #SCI_TABLE,A
                            000400
454       P:0000E6 P:0000E6 000000            NOP
455       P:0000E7 P:0000E7 567000            MOVE              A,X:(SCI_TABLE+33)
                            000421
456       P:0000E9 P:0000E9 0A0020  SCI_END   BSET    #ST_RCV,X:<STATUS                 ; Its a utility board (SCI) word
457       P:0000EA P:0000EA 0C00CC            JMP     <SET_CC
458    
459                                 ; Transmit the word in B1 to the host computer over the fiber optic data link
460                                 XMT_WRD
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 9



461       P:0000EB P:0000EB 08F4BB            MOVEP             #$028FE2,X:BCR          ; Slow down RDFO access
                            028FE2
462       P:0000ED P:0000ED 60F400            MOVE              #FO_HDR+1,R0
                            000002
463       P:0000EF P:0000EF 060380            DO      #3,XMT_WRD1
                            0000F3
464       P:0000F1 P:0000F1 0C1D91            ASL     #8,B,B
465       P:0000F2 P:0000F2 000000            NOP
466       P:0000F3 P:0000F3 535800            MOVE              B2,X:(R0)+
467                                 XMT_WRD1
468       P:0000F4 P:0000F4 60F400            MOVE              #FO_HDR,R0
                            000001
469       P:0000F6 P:0000F6 61F400            MOVE              #WRFO,R1
                            FFFFF2
470       P:0000F8 P:0000F8 060480            DO      #4,XMT_WRD2
                            0000FB
471       P:0000FA P:0000FA 46D800            MOVE              X:(R0)+,Y0              ; Should be MOVEP  X:(R0)+,Y:WRFO
472       P:0000FB P:0000FB 4E6100            MOVE                          Y0,Y:(R1)
473                                 XMT_WRD2
474       P:0000FC P:0000FC 08F4BB            MOVEP             #$028FE1,X:BCR          ; Restore RDFO access
                            028FE1
475       P:0000FE P:0000FE 00000C            RTS
476    
477                                 ; Check the command or reply header in X:(R3) for self-consistency
478       P:0000FF P:0000FF 46E300  CHK_HDR   MOVE              X:(R3),Y0
479       P:000100 P:000100 579600            MOVE              X:<MASK1,B              ; Test for S.LE.3 and D.LE.3 and N.LE.7
480       P:000101 P:000101 20005E            AND     Y0,B
481       P:000102 P:000102 0E208D            JNE     <ERROR                            ; Test failed
482       P:000103 P:000103 579700            MOVE              X:<MASK2,B              ; Test for either S.NE.0 or D.NE.0
483       P:000104 P:000104 20005E            AND     Y0,B
484       P:000105 P:000105 0EA08D            JEQ     <ERROR                            ; Test failed
485       P:000106 P:000106 579500            MOVE              X:<SEVEN,B
486       P:000107 P:000107 20005E            AND     Y0,B                              ; Extract NWORDS, must be > 0
487       P:000108 P:000108 0EA08D            JEQ     <ERROR
488       P:000109 P:000109 44E300            MOVE              X:(R3),X0
489       P:00010A P:00010A 440500            MOVE              X0,X:<HEADER            ; Its a correct header
490       P:00010B P:00010B 550600            MOVE              B1,X:<NWORDS            ; Number of words in the command
491       P:00010C P:00010C 0C005E            JMP     <PR_RCV
492    
493                                 ;  *****************  Boot Commands  *******************
494    
495                                 ; Test Data Link - simply return value received after 'TDL'
496       P:00010D P:00010D 47DB00  TDL       MOVE              X:(R3)+,Y1              ; Get the data value
497       P:00010E P:00010E 0C0090            JMP     <FINISH1                          ; Return from executing TDL command
498    
499                                 ; Read DSP or EEPROM memory ('RDM' address): read memory, reply with value
500       P:00010F P:00010F 47DB00  RDMEM     MOVE              X:(R3)+,Y1
501       P:000110 P:000110 20EF00            MOVE              Y1,B
502       P:000111 P:000111 0140CE            AND     #$0FFFFF,B                        ; Bits 23-20 need to be zeroed
                            0FFFFF
503       P:000113 P:000113 21B000            MOVE              B1,R0                   ; Need the address in an address register
504       P:000114 P:000114 20EF00            MOVE              Y1,B
505       P:000115 P:000115 000000            NOP
506       P:000116 P:000116 0ACF14            JCLR    #20,B,RDX                         ; Test address bit for Program memory
                            00011A
507       P:000118 P:000118 07E087            MOVE              P:(R0),Y1               ; Read from Program Memory
508       P:000119 P:000119 0C0090            JMP     <FINISH1                          ; Send out a header with the value
509       P:00011A P:00011A 0ACF15  RDX       JCLR    #21,B,RDY                         ; Test address bit for X: memory
                            00011E
510       P:00011C P:00011C 47E000            MOVE              X:(R0),Y1               ; Write to X data memory
511       P:00011D P:00011D 0C0090            JMP     <FINISH1                          ; Send out a header with the value
512       P:00011E P:00011E 0ACF16  RDY       JCLR    #22,B,RDR                         ; Test address bit for Y: memory
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 10



                            000122
513       P:000120 P:000120 4FE000            MOVE                          Y:(R0),Y1   ; Read from Y data memory
514       P:000121 P:000121 0C0090            JMP     <FINISH1                          ; Send out a header with the value
515       P:000122 P:000122 0ACF17  RDR       JCLR    #23,B,ERROR                       ; Test address bit for read from EEPROM memo
ry
                            00008D
516       P:000124 P:000124 479400            MOVE              X:<THREE,Y1             ; Convert to word address to a byte address
517       P:000125 P:000125 220600            MOVE              R0,Y0                   ; Get 16-bit address in a data register
518       P:000126 P:000126 2000B8            MPY     Y0,Y1,B                           ; Multiply
519       P:000127 P:000127 20002A            ASR     B                                 ; Eliminate zero fill of fractional multiply
520       P:000128 P:000128 213000            MOVE              B0,R0                   ; Need to address memory
521       P:000129 P:000129 0AD06F            BSET    #15,R0                            ; Set bit so its in EEPROM space
522       P:00012A P:00012A 0D0178            JSR     <RD_WORD                          ; Read word from EEPROM
523       P:00012B P:00012B 21A700            MOVE              B1,Y1                   ; FINISH1 transmits Y1 as its reply
524       P:00012C P:00012C 0C0090            JMP     <FINISH1
525    
526                                 ; Program WRMEM ('WRM' address datum): write to memory, reply 'DON'.
527       P:00012D P:00012D 47DB00  WRMEM     MOVE              X:(R3)+,Y1              ; Get the address to be written to
528       P:00012E P:00012E 20EF00            MOVE              Y1,B
529       P:00012F P:00012F 0140CE            AND     #$0FFFFF,B                        ; Bits 23-20 need to be zeroed
                            0FFFFF
530       P:000131 P:000131 21B000            MOVE              B1,R0                   ; Need the address in an address register
531       P:000132 P:000132 20EF00            MOVE              Y1,B
532       P:000133 P:000133 46DB00            MOVE              X:(R3)+,Y0              ; Get datum into Y0 so MOVE works easily
533       P:000134 P:000134 0ACF14            JCLR    #20,B,WRX                         ; Test address bit for Program memory
                            000138
534       P:000136 P:000136 076086            MOVE              Y0,P:(R0)               ; Write to Program memory
535       P:000137 P:000137 0C008F            JMP     <FINISH
536       P:000138 P:000138 0ACF15  WRX       JCLR    #21,B,WRY                         ; Test address bit for X: memory
                            00013C
537       P:00013A P:00013A 466000            MOVE              Y0,X:(R0)               ; Write to X: memory
538       P:00013B P:00013B 0C008F            JMP     <FINISH
539       P:00013C P:00013C 0ACF16  WRY       JCLR    #22,B,WRR                         ; Test address bit for Y: memory
                            000140
540       P:00013E P:00013E 4E6000            MOVE                          Y0,Y:(R0)   ; Write to Y: memory
541       P:00013F P:00013F 0C008F            JMP     <FINISH
542       P:000140 P:000140 0ACF17  WRR       JCLR    #23,B,ERROR                       ; Test address bit for write to EEPROM
                            00008D
543       P:000142 P:000142 013D02            BCLR    #WRENA,X:PDRC                     ; WR_ENA* = 0 to enable EEPROM writing
544       P:000143 P:000143 460E00            MOVE              Y0,X:<SV_A1             ; Save the datum to be written
545       P:000144 P:000144 479400            MOVE              X:<THREE,Y1             ; Convert word address to a byte address
546       P:000145 P:000145 220600            MOVE              R0,Y0                   ; Get 16-bit address in a data register
547       P:000146 P:000146 2000B8            MPY     Y1,Y0,B                           ; Multiply
548       P:000147 P:000147 20002A            ASR     B                                 ; Eliminate zero fill of fractional multiply
549       P:000148 P:000148 213000            MOVE              B0,R0                   ; Need to address memory
550       P:000149 P:000149 0AD06F            BSET    #15,R0                            ; Set bit so its in EEPROM space
551       P:00014A P:00014A 558E00            MOVE              X:<SV_A1,B1             ; Get the datum to be written
552       P:00014B P:00014B 060380            DO      #3,L1WRR                          ; Loop over three bytes of the word
                            000154
553       P:00014D P:00014D 07588D            MOVE              B1,P:(R0)+              ; Write each EEPROM byte
554       P:00014E P:00014E 0C1C91            ASR     #8,B,B
555       P:00014F P:00014F 469E00            MOVE              X:<C100K,Y0             ; Move right one byte, enter delay = 1 msec
556       P:000150 P:000150 06C600            DO      Y0,L2WRR                          ; Delay by 12 milliseconds for EEPROM write
                            000153
557       P:000152 P:000152 060CA0            REP     #12                               ; Assume 100 MHz DSP56303
558       P:000153 P:000153 000000            NOP
559                                 L2WRR
560       P:000154 P:000154 000000            NOP                                       ; DO loop nesting restriction
561                                 L1WRR
562       P:000155 P:000155 013D22            BSET    #WRENA,X:PDRC                     ; WR_ENA* = 1 to disable EEPROM writing
563       P:000156 P:000156 0C008F            JMP     <FINISH
564    
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 11



565                                 ; Load application code from P: memory into its proper locations
566       P:000157 P:000157 47DB00  LDAPPL    MOVE              X:(R3)+,Y1              ; Application number, not used yet
567       P:000158 P:000158 0D015A            JSR     <LOAD_APPLICATION
568       P:000159 P:000159 0C008F            JMP     <FINISH
569    
570                                 LOAD_APPLICATION
571       P:00015A P:00015A 60F400            MOVE              #$8000,R0               ; Starting EEPROM address
                            008000
572       P:00015C P:00015C 0D0178            JSR     <RD_WORD                          ; Number of words in boot code
573       P:00015D P:00015D 21A600            MOVE              B1,Y0
574       P:00015E P:00015E 479400            MOVE              X:<THREE,Y1
575       P:00015F P:00015F 2000B8            MPY     Y0,Y1,B
576       P:000160 P:000160 20002A            ASR     B
577       P:000161 P:000161 213000            MOVE              B0,R0                   ; EEPROM address of start of P: application
578       P:000162 P:000162 0AD06F            BSET    #15,R0                            ; To access EEPROM
579       P:000163 P:000163 0D0178            JSR     <RD_WORD                          ; Read number of words in application P:
580       P:000164 P:000164 61F400            MOVE              #(X_BOOT_START+1),R1    ; End of boot P: code that needs keeping
                            00022B
581       P:000166 P:000166 06CD00            DO      B1,RD_APPL_P
                            000169
582       P:000168 P:000168 0D0178            JSR     <RD_WORD
583       P:000169 P:000169 07598D            MOVE              B1,P:(R1)+
584                                 RD_APPL_P
585       P:00016A P:00016A 0D0178            JSR     <RD_WORD                          ; Read number of words in application X:
586       P:00016B P:00016B 61F400            MOVE              #END_COMMAND_TABLE,R1
                            000036
587       P:00016D P:00016D 06CD00            DO      B1,RD_APPL_X
                            000170
588       P:00016F P:00016F 0D0178            JSR     <RD_WORD
589       P:000170 P:000170 555900            MOVE              B1,X:(R1)+
590                                 RD_APPL_X
591       P:000171 P:000171 0D0178            JSR     <RD_WORD                          ; Read number of words in application Y:
592       P:000172 P:000172 310100            MOVE              #1,R1                   ; There is no Y: memory in the boot code
593       P:000173 P:000173 06CD00            DO      B1,RD_APPL_Y
                            000176
594       P:000175 P:000175 0D0178            JSR     <RD_WORD
595       P:000176 P:000176 5D5900            MOVE                          B1,Y:(R1)+
596                                 RD_APPL_Y
597       P:000177 P:000177 00000C            RTS
598    
599                                 ; Read one word from EEPROM location R0 into accumulator B1
600       P:000178 P:000178 060380  RD_WORD   DO      #3,L_RDBYTE
                            00017B
601       P:00017A P:00017A 07D88B            MOVE              P:(R0)+,B2
602       P:00017B P:00017B 0C1C91            ASR     #8,B,B
603                                 L_RDBYTE
604       P:00017C P:00017C 00000C            RTS
605    
606                                 ; Come to here on a 'STP' command so 'DON' can be sent
607                                 STOP_IDLE_CLOCKING
608       P:00017D P:00017D 305A00            MOVE              #<TST_RCV,R0            ; Execution address when idle => when not
609       P:00017E P:00017E 601F00            MOVE              R0,X:<IDL_ADR           ;   processing commands or reading out
610       P:00017F P:00017F 0A0002            BCLR    #IDLMODE,X:<STATUS                ; Don't idle after readout
611       P:000180 P:000180 0C008F            JMP     <FINISH
612    
613                                 ; Routines executed after the DSP boots and initializes
614       P:000181 P:000181 305A00  STARTUP   MOVE              #<TST_RCV,R0            ; Execution address when idle => when not
615       P:000182 P:000182 601F00            MOVE              R0,X:<IDL_ADR           ;   processing commands or reading out
616       P:000183 P:000183 44F400            MOVE              #50000,X0               ; Delay by 500 milliseconds
                            00C350
617       P:000185 P:000185 06C400            DO      X0,L_DELAY
                            000188
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 12



618       P:000187 P:000187 06E8A3            REP     #1000
619       P:000188 P:000188 000000            NOP
620                                 L_DELAY
621       P:000189 P:000189 57F400            MOVE              #$020002,B              ; Normal reply after booting is 'SYR'
                            020002
622       P:00018B P:00018B 0D00EB            JSR     <XMT_WRD
623       P:00018C P:00018C 57F400            MOVE              #'SYR',B
                            535952
624       P:00018E P:00018E 0D00EB            JSR     <XMT_WRD
625    
626       P:00018F P:00018F 0C0054            JMP     <START                            ; Start normal command processing
627    
628                                 ; *******************  DSP  INITIALIZATION  CODE  **********************
629                                 ; This code initializes the DSP right after booting, and is overwritten
630                                 ;   by application code
631       P:000190 P:000190 08F4BD  INIT      MOVEP             #PLL_INIT,X:PCTL        ; Initialize PLL to 100 MHz
                            050003
632       P:000192 P:000192 000000            NOP
633    
634                                 ; Set operation mode register OMR to normal expanded
635       P:000193 P:000193 0500BA            MOVEC             #$0000,OMR              ; Operating Mode Register = Normal Expanded
636       P:000194 P:000194 0500BB            MOVEC             #0,SP                   ; Reset the Stack Pointer SP
637    
638                                 ; Program the AA = address attribute pins
639       P:000195 P:000195 08F4B9            MOVEP             #$FFFC21,X:AAR0         ; Y = $FFF000 to $FFFFFF asserts commands
                            FFFC21
640       P:000197 P:000197 08F4B8            MOVEP             #$008909,X:AAR1         ; P = $008000 to $00FFFF accesses the EEPROM
                            008909
641       P:000199 P:000199 08F4B7            MOVEP             #$010C11,X:AAR2         ; X = $010000 to $010FFF reads A/D values
                            010C11
642       P:00019B P:00019B 08F4B6            MOVEP             #$080621,X:AAR3         ; Y = $080000 to $0BFFFF R/W from SRAM
                            080621
643    
644       P:00019D P:00019D 0A0F00            BCLR    #CDAC,X:<LATCH                    ; Enable clearing of DACs
645       P:00019E P:00019E 0A0F02            BCLR    #ENCK,X:<LATCH                    ; Disable clock and DAC output switches
646       P:00019F P:00019F 09F0B5            MOVEP             X:LATCH,Y:WRLATCH       ; Execute these two operations
                            00000F
647    
648                                 ; Program the DRAM memory access and addressing
649       P:0001A1 P:0001A1 08F4BB            MOVEP             #$028FE1,X:BCR          ; Bus Control Register
                            028FE1
650    
651                                 ; Program the Host port B for parallel I/O
652       P:0001A3 P:0001A3 08F484            MOVEP             #>1,X:HPCR              ; All pins enabled as GPIO
                            000001
653       P:0001A5 P:0001A5 08F489            MOVEP             #$810C,X:HDR
                            00810C
654       P:0001A7 P:0001A7 08F488            MOVEP             #$B10E,X:HDDR           ; Data Direction Register
                            00B10E
655                                                                                     ;  (1 for Output, 0 for Input)
656    
657                                 ; Port B conversion from software bits to schematic labels
658                                 ;       PB0 = PWROK             PB08 = PRSFIFO*
659                                 ;       PB1 = LED1              PB09 = EF*
660                                 ;       PB2 = LVEN              PB10 = EXT-IN0
661                                 ;       PB3 = HVEN              PB11 = EXT-IN1
662                                 ;       PB4 = STATUS0           PB12 = EXT-OUT0
663                                 ;       PB5 = STATUS1           PB13 = EXT-OUT1
664                                 ;       PB6 = STATUS2           PB14 = SSFHF*
665                                 ;       PB7 = STATUS3           PB15 = SELSCI
666    
667                                 ; Program the serial port ESSI0 = Port C for serial communication with
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 13



668                                 ;   the utility board
669       P:0001A9 P:0001A9 07F43F            MOVEP             #>0,X:PCRC              ; Software reset of ESSI0
                            000000
670       P:0001AB P:0001AB 07F435            MOVEP             #$180809,X:CRA0         ; Divide 100 MHz by 20 to get 5.0 MHz
                            180809
671                                                                                     ; DC[4:0] = 0 for non-network operation
672                                                                                     ; WL0-WL2 = 3 for 24-bit data words
673                                                                                     ; SSC1 = 0 for SC1 not used
674       P:0001AD P:0001AD 07F436            MOVEP             #$020020,X:CRB0         ; SCKD = 1 for internally generated clock
                            020020
675                                                                                     ; SCD2 = 0 so frame sync SC2 is an output
676                                                                                     ; SHFD = 0 for MSB shifted first
677                                                                                     ; FSL = 0, frame sync length not used
678                                                                                     ; CKP = 0 for rising clock edge transitions
679                                                                                     ; SYN = 0 for asynchronous
680                                                                                     ; TE0 = 1 to enable transmitter #0
681                                                                                     ; MOD = 0 for normal, non-networked mode
682                                                                                     ; TE0 = 0 to NOT enable transmitter #0 yet
683                                                                                     ; RE = 1 to enable receiver
684       P:0001AF P:0001AF 07F43F            MOVEP             #%111001,X:PCRC         ; Control Register (0 for GPIO, 1 for ESSI)
                            000039
685       P:0001B1 P:0001B1 07F43E            MOVEP             #%000110,X:PRRC         ; Data Direction Register (0 for In, 1 for O
ut)
                            000006
686       P:0001B3 P:0001B3 07F43D            MOVEP             #%000100,X:PDRC         ; Data Register - WR_ENA* = 1
                            000004
687    
688                                 ; Port C version = Analog boards
689                                 ;       MOVEP   #$000809,X:CRA0 ; Divide 100 MHz by 20 to get 5.0 MHz
690                                 ;       MOVEP   #$000030,X:CRB0 ; SCKD = 1 for internally generated clock
691                                 ;       MOVEP   #%100000,X:PCRC ; Control Register (0 for GPIO, 1 for ESSI)
692                                 ;       MOVEP   #%000100,X:PRRC ; Data Direction Register (0 for In, 1 for Out)
693                                 ;       MOVEP   #%000000,X:PDRC ; Data Register: 'not used' = 0 outputs
694    
695       P:0001B5 P:0001B5 07F43C            MOVEP             #0,X:TX00               ; Initialize the transmitter to zero
                            000000
696       P:0001B7 P:0001B7 000000            NOP
697       P:0001B8 P:0001B8 000000            NOP
698       P:0001B9 P:0001B9 013630            BSET    #TE,X:CRB0                        ; Enable the SSI transmitter
699    
700                                 ; Conversion from software bits to schematic labels for Port C
701                                 ;       PC0 = SC00 = UTL-T-SCK
702                                 ;       PC1 = SC01 = 2_XMT = SYNC on prototype
703                                 ;       PC2 = SC02 = WR_ENA*
704                                 ;       PC3 = SCK0 = TIM-U-SCK
705                                 ;       PC4 = SRD0 = UTL-T-STD
706                                 ;       PC5 = STD0 = TIM-U-STD
707    
708                                 ; Program the serial port ESSI1 = Port D for serial transmission to
709                                 ;   the analog boards and two parallel I/O input pins
710       P:0001BA P:0001BA 07F42F            MOVEP             #>0,X:PCRD              ; Software reset of ESSI0
                            000000
711       P:0001BC P:0001BC 07F425            MOVEP             #$000809,X:CRA1         ; Divide 100 MHz by 20 to get 5.0 MHz
                            000809
712                                                                                     ; DC[4:0] = 0
713                                                                                     ; WL[2:0] = ALC = 0 for 8-bit data words
714                                                                                     ; SSC1 = 0 for SC1 not used
715       P:0001BE P:0001BE 07F426            MOVEP             #$000030,X:CRB1         ; SCKD = 1 for internally generated clock
                            000030
716                                                                                     ; SCD2 = 1 so frame sync SC2 is an output
717                                                                                     ; SHFD = 0 for MSB shifted first
718                                                                                     ; CKP = 0 for rising clock edge transitions
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 14



719                                                                                     ; TE0 = 0 to NOT enable transmitter #0 yet
720                                                                                     ; MOD = 0 so its not networked mode
721       P:0001C0 P:0001C0 07F42F            MOVEP             #%100000,X:PCRD         ; Control Register (0 for GPIO, 1 for ESSI)
                            000020
722                                                                                     ; PD3 = SCK1, PD5 = STD1 for ESSI
723       P:0001C2 P:0001C2 07F42E            MOVEP             #%000100,X:PRRD         ; Data Direction Register (0 for In, 1 for O
ut)
                            000004
724       P:0001C4 P:0001C4 07F42D            MOVEP             #%000100,X:PDRD         ; Data Register: 'not used' = 0 outputs
                            000004
725       P:0001C6 P:0001C6 07F42C            MOVEP             #0,X:TX10               ; Initialize the transmitter to zero
                            000000
726       P:0001C8 P:0001C8 000000            NOP
727       P:0001C9 P:0001C9 000000            NOP
728       P:0001CA P:0001CA 012630            BSET    #TE,X:CRB1                        ; Enable the SSI transmitter
729    
730                                 ; Conversion from software bits to schematic labels for Port D
731                                 ; PD0 = SC10 = 2_XMT_? input
732                                 ; PD1 = SC11 = SSFEF* input
733                                 ; PD2 = SC12 = PWR_EN
734                                 ; PD3 = SCK1 = TIM-A-SCK
735                                 ; PD4 = SRD1 = PWRRST
736                                 ; PD5 = STD1 = TIM-A-STD
737    
738                                 ; Program the SCI port to communicate with the utility board
739       P:0001CB P:0001CB 07F41C            MOVEP             #$0B04,X:SCR            ; SCI programming: 11-bit asynchronous
                            000B04
740                                                                                     ;   protocol (1 start, 8 data, 1 even parity
,
741                                                                                     ;   1 stop); LSB before MSB; enable receiver
742                                                                                     ;   and its interrupts; transmitter interrup
ts
743                                                                                     ;   disabled.
744       P:0001CD P:0001CD 07F41B            MOVEP             #$0003,X:SCCR           ; SCI clock: utility board data rate =
                            000003
745                                                                                     ;   (390,625 kbits/sec); internal clock.
746       P:0001CF P:0001CF 07F41F            MOVEP             #%011,X:PCRE            ; Port Control Register = RXD, TXD enabled
                            000003
747       P:0001D1 P:0001D1 07F41E            MOVEP             #%000,X:PRRE            ; Port Direction Register (0 = Input)
                            000000
748    
749                                 ;       PE0 = RXD
750                                 ;       PE1 = TXD
751                                 ;       PE2 = SCLK
752    
753                                 ; Program one of the three timers as an exposure timer
754       P:0001D3 P:0001D3 07F403            MOVEP             #$C34F,X:TPLR           ; Prescaler to generate millisecond timer,
                            00C34F
755                                                                                     ;  counting from the system clock / 2 = 50 M
Hz
756       P:0001D5 P:0001D5 07F40F            MOVEP             #$208200,X:TCSR0        ; Clear timer complete bit and enable presca
ler
                            208200
757       P:0001D7 P:0001D7 07F40E            MOVEP             #0,X:TLR0               ; Timer load register
                            000000
758    
759                                 ; Enable interrupts for the SCI port only
760       P:0001D9 P:0001D9 08F4BF            MOVEP             #$000000,X:IPRC         ; No interrupts allowed
                            000000
761       P:0001DB P:0001DB 08F4BE            MOVEP             #>$80,X:IPRP            ; Enable SCI interrupt only, IPR = 1
                            000080
762       P:0001DD P:0001DD 00FCB8            ANDI    #$FC,MR                           ; Unmask all interrupt levels
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 15



763    
764                                 ; Initialize the fiber optic serial receiver circuitry
765       P:0001DE P:0001DE 061480            DO      #20,L_FO_INIT
                            0001E3
766       P:0001E0 P:0001E0 5FF000            MOVE                          Y:RDFO,B
                            FFFFF1
767       P:0001E2 P:0001E2 0605A0            REP     #5
768       P:0001E3 P:0001E3 000000            NOP
769                                 L_FO_INIT
770    
771                                 ; Pulse PRSFIFO* low to revive the CMDRST* instruction and reset the FIFO
772       P:0001E4 P:0001E4 44F400            MOVE              #1000000,X0             ; Delay by 10 milliseconds
                            0F4240
773       P:0001E6 P:0001E6 06C400            DO      X0,*+3
                            0001E8
774       P:0001E8 P:0001E8 000000            NOP
775       P:0001E9 P:0001E9 0A8908            BCLR    #8,X:HDR
776       P:0001EA P:0001EA 0614A0            REP     #20
777       P:0001EB P:0001EB 000000            NOP
778       P:0001EC P:0001EC 0A8928            BSET    #8,X:HDR
779    
780                                 ; Reset the utility board
781       P:0001ED P:0001ED 0A0F05            BCLR    #5,X:<LATCH
782       P:0001EE P:0001EE 09F0B5            MOVEP             X:LATCH,Y:WRLATCH       ; Clear reset utility board bit
                            00000F
783       P:0001F0 P:0001F0 06C8A0            REP     #200                              ; Delay by RESET* low time
784       P:0001F1 P:0001F1 000000            NOP
785       P:0001F2 P:0001F2 0A0F25            BSET    #5,X:<LATCH
786       P:0001F3 P:0001F3 09F0B5            MOVEP             X:LATCH,Y:WRLATCH       ; Clear reset utility board bit
                            00000F
787       P:0001F5 P:0001F5 56F400            MOVE              #200000,A               ; Delay 2 msec for utility boot
                            030D40
788       P:0001F7 P:0001F7 06CE00            DO      A,*+3
                            0001F9
789       P:0001F9 P:0001F9 000000            NOP
790    
791                                 ; Put all the analog switch inputs to low so they draw minimum current
792       P:0001FA P:0001FA 012F23            BSET    #3,X:PCRD                         ; Turn the serial clock on
793       P:0001FB P:0001FB 56F400            MOVE              #$0C3000,A              ; Value of integrate speed and gain switches
                            0C3000
794       P:0001FD P:0001FD 20001B            CLR     B
795       P:0001FE P:0001FE 241000            MOVE              #$100000,X0             ; Increment over board numbers for DAC write
s
796       P:0001FF P:0001FF 45F400            MOVE              #$001000,X1             ; Increment over board numbers for WRSS writ
es
                            001000
797       P:000201 P:000201 060F80            DO      #15,L_ANALOG                      ; Fifteen video processor boards maximum
                            000209
798       P:000203 P:000203 0D020C            JSR     <XMIT_A_WORD                      ; Transmit A to TIM-A-STD
799       P:000204 P:000204 200040            ADD     X0,A
800       P:000205 P:000205 5F7000            MOVE                          B,Y:WRSS    ; This is for the fast analog switches
                            FFFFF3
801       P:000207 P:000207 0620A3            REP     #800                              ; Delay for the serial data transmission
802       P:000208 P:000208 000000            NOP
803       P:000209 P:000209 200068            ADD     X1,B                              ; Increment the video and clock driver numbe
rs
804                                 L_ANALOG
805       P:00020A P:00020A 012F03            BCLR    #3,X:PCRD                         ; Turn the serial clock off
806       P:00020B P:00020B 0C0223            JMP     <SKIP
807    
808                                 ; Transmit contents of accumulator A1 over the synchronous serial transmitter
809                                 XMIT_A_WORD
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 16



810       P:00020C P:00020C 07F42C            MOVEP             #0,X:TX10               ; This helps, don't know why
                            000000
811       P:00020E P:00020E 547000            MOVE              A1,X:SV_A1
                            00000E
812       P:000210 P:000210 000000            NOP
813       P:000211 P:000211 01A786            JCLR    #TDE,X:SSISR1,*                   ; Start bit
                            000211
814       P:000213 P:000213 07F42C            MOVEP             #$010000,X:TX10
                            010000
815       P:000215 P:000215 060380            DO      #3,L_X
                            00021B
816       P:000217 P:000217 01A786            JCLR    #TDE,X:SSISR1,*                   ; Three data bytes
                            000217
817       P:000219 P:000219 04CCCC            MOVEP             A1,X:TX10
818       P:00021A P:00021A 0C1E90            LSL     #8,A
819       P:00021B P:00021B 000000            NOP
820                                 L_X
821       P:00021C P:00021C 01A786            JCLR    #TDE,X:SSISR1,*                   ; Zeroes to bring transmitter low
                            00021C
822       P:00021E P:00021E 07F42C            MOVEP             #0,X:TX10
                            000000
823       P:000220 P:000220 54F000            MOVE              X:SV_A1,A1
                            00000E
824       P:000222 P:000222 00000C            RTS
825    
826                                 SKIP
827    
828                                 ; Set up the circular SCI buffer, 32 words in size
829       P:000223 P:000223 64F400            MOVE              #SCI_TABLE,R4
                            000400
830       P:000225 P:000225 051FA4            MOVE              #31,M4
831       P:000226 P:000226 647000            MOVE              R4,X:(SCI_TABLE+33)
                            000421
832    
833                                           IF      @SCP("HOST","ROM")
841                                           ENDIF
842    
843       P:000228 P:000228 44F400            MOVE              #>$AC,X0
                            0000AC
844       P:00022A P:00022A 440100            MOVE              X0,X:<FO_HDR
845    
846       P:00022B P:00022B 0C0181            JMP     <STARTUP
847    
848                                 ;  ****************  X: Memory tables  ********************
849    
850                                 ; Define the address in P: space where the table of constants begins
851    
852                                  X_BOOT_START
853       00022A                              EQU     @LCV(L)-2
854    
855                                           IF      @SCP("HOST","ROM")
857                                           ENDIF
858                                           IF      @SCP("HOST","HOST")
859       X:000000 X:000000                   ORG     X:0,X:0
860                                           ENDIF
861    
862                                 ; Special storage area - initialization constants and scratch space
863       X:000000 X:000000         STATUS    DC      4                                 ; Controller status bits
864    
865       000001                    FO_HDR    EQU     STATUS+1                          ; Fiber optic write bytes
866       000005                    HEADER    EQU     FO_HDR+4                          ; Command header
867       000006                    NWORDS    EQU     HEADER+1                          ; Number of words in the command
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 17



868       000007                    COM_BUF   EQU     NWORDS+1                          ; Command buffer
869       00000E                    SV_A1     EQU     COM_BUF+7                         ; Save accumulator A1
870    
871                                           IF      @SCP("HOST","ROM")
876                                           ENDIF
877    
878                                           IF      @SCP("HOST","HOST")
879       X:00000F X:00000F                   ORG     X:$F,X:$F
880                                           ENDIF
881    
882                                 ; Parameter table in P: space to be copied into X: space during
883                                 ;   initialization, and is copied from ROM by the DSP boot
884       X:00000F X:00000F         LATCH     DC      $7A                               ; Starting value in latch chip U25
885                                  EXPOSURE_TIME
886       X:000010 X:000010                   DC      0                                 ; Exposure time in milliseconds
887                                  ELAPSED_TIME
888       X:000011 X:000011                   DC      0                                 ; Time elapsed so far in the exposure
889       X:000012 X:000012         ONE       DC      1                                 ; One
890       X:000013 X:000013         TWO       DC      2                                 ; Two
891       X:000014 X:000014         THREE     DC      3                                 ; Three
892       X:000015 X:000015         SEVEN     DC      7                                 ; Seven
893       X:000016 X:000016         MASK1     DC      $FCFCF8                           ; Mask for checking header
894       X:000017 X:000017         MASK2     DC      $030300                           ; Mask for checking header
895       X:000018 X:000018         DONE      DC      'DON'                             ; Standard reply
896       X:000019 X:000019         SBRD      DC      $020000                           ; Source Identification number
897       X:00001A X:00001A         TIM_DRB   DC      $000200                           ; Destination = timing board number
898       X:00001B X:00001B         DMASK     DC      $00FF00                           ; Mask to get destination board number
899       X:00001C X:00001C         SMASK     DC      $FF0000                           ; Mask to get source board number
900       X:00001D X:00001D         ERR       DC      'ERR'                             ; An error occurred
901       X:00001E X:00001E         C100K     DC      100000                            ; Delay for WRROM = 1 millisec
902       X:00001F X:00001F         IDL_ADR   DC      TST_RCV                           ; Address of idling routine
903       X:000020 X:000020         EXP_ADR   DC      0                                 ; Jump to this address during exposures
904    
905                                 ; Places for saving register values
906       X:000021 X:000021         SAVE_SR   DC      0                                 ; Status Register
907       X:000022 X:000022         SAVE_X1   DC      0
908       X:000023 X:000023         SAVE_A1   DC      0
909       X:000024 X:000024         SAVE_R0   DC      0
910       X:000025 X:000025         RCV_ERR   DC      0
911       X:000026 X:000026         SCI_A1    DC      0                                 ; Contents of accumulator A1 in RCV ISR
912       X:000027 X:000027         SCI_R0    DC      SRXL
913    
914                                 ; Command table
915       000028                    COM_TBL_R EQU     @LCV(R)
916       X:000028 X:000028         COM_TBL   DC      'TDL',TDL                         ; Test Data Link
917       X:00002A X:00002A                   DC      'RDM',RDMEM                       ; Read from DSP or EEPROM memory
918       X:00002C X:00002C                   DC      'WRM',WRMEM                       ; Write to DSP memory
919       X:00002E X:00002E                   DC      'LDA',LDAPPL                      ; Load application from EEPROM to DSP
920       X:000030 X:000030                   DC      'STP',STOP_IDLE_CLOCKING
921       X:000032 X:000032                   DC      'DON',START                       ; Nothing special
922       X:000034 X:000034                   DC      'ERR',START                       ; Nothing special
923    
924                                  END_COMMAND_TABLE
925       000036                              EQU     @LCV(R)
926    
927                                 ; The table at SCI_TABLE is for words received from the utility board, written by
928                                 ;   the interrupt service routine SCI_RCV. Note that it is 32 words long,
929                                 ;   hard coded, and the 33rd location contains the pointer to words that have
930                                 ;   been processed by moving them from the SCI_TABLE to the COM_BUF.
931    
932                                           IF      @SCP("HOST","ROM")
934                                           ENDIF
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timboot.asm  Page 18



935    
936       000036                    END_ADR   EQU     @LCV(L)                           ; End address of P: code written to ROM
937    
938                                 ; CCDVIDREV3B     EQU     $000000         ; CCD Video Processor Rev. 3
939                                 ; TIMREV4         EQU     $000000         ; Timing Rev. 4
940                                 ; UTILREV3        EQU     $000020         ; Utility Rev. 3 supported
941                                 ; SHUTTER_CC      EQU     $000080         ; Shutter supported
942                                 ; TEMP_POLY       EQU     $000100         ; Polynomial calibration
943                                 ; SUBARRAY        EQU     $000400         ; Subarray readout supported
944                                 ; BINNING         EQU     $000800         ; Binning supported
945                                 ; SPLIT_SERIAL    EQU     $001000         ; Split serial supported
946                                 ; SPLIT_PARALLEL  EQU     $002000         ; Split parallel supported
947    
948       P:00022C P:00022C                   ORG     P:,P:
949                                 ;CC     EQU     CCDVIDREV3B+TIMREV4+UTILREV3+SHUTTER_CC+TEMP_POLY+SUBARRAY+SPLIT_SERIAL+SPLIT_PA
RALLEL
950       003416                    CC        EQU     ARC22+SUBARRAY+SPLIT_PARALLEL+SPLIT_SERIAL+ARC48
951    
952                                 ; Put number of words of application in P: for loading application from EEPROM
953       P:00022C P:00022C                   DC      TIMBOOT_X_MEMORY-@LCV(L)-1
954    
955                                 ; Define CLOCK as a macro to produce in-line code to reduce execution time
956                                 CLOCK     MACRO
957  m                                        JCLR    #SSFHF,X:HDR,*                    ; Don't overfill the WRSS FIFO
958  m                                        REP     Y:(R0)+                           ; Repeat
959  m                                        MOVEP   Y:(R0)+,Y:WRSS                    ; Write the waveform to the FIFO
960  m                                        ENDM
961    
962                                 ; Set software to IDLE mode
963                                 START_IDLE_CLOCKING
964       P:00022D P:00022D 60F400            MOVE              #IDLE,R0                ; Exercise clocks when idling
                            000232
965       P:00022F P:00022F 601F00            MOVE              R0,X:<IDL_ADR
966       P:000230 P:000230 0A0022            BSET    #IDLMODE,X:<STATUS                ; Idle after readout
967       P:000231 P:000231 0C008F            JMP     <FINISH                           ; Need to send header and 'DON'
968    
969                                 ; Keep the CCD idling when not reading out
970       P:000232 P:000232 060140  IDLE      DO      Y:<NSR,IDL1                       ; Loop over number of pixels per line
                            00023E
971       P:000234 P:000234 303E00            MOVE              #<SERIAL_IDLE,R0        ; Serial transfer on pixel
972                                           CLOCK                                     ; Go to it
976       P:000239 P:000239 330700            MOVE              #COM_BUF,R3
977       P:00023A P:00023A 0D00A5            JSR     <GET_RCV                          ; Check for FO or SSI commands
978       P:00023B P:00023B 0E023E            JCC     <NO_COM                           ; Continue IDLE if no commands received
979       P:00023C P:00023C 00008C            ENDDO
980       P:00023D P:00023D 0C005D            JMP     <PRC_RCV                          ; Go process header and command
981       P:00023E P:00023E 000000  NO_COM    NOP
982                                 IDL1
983       P:00023F P:00023F 302A00            MOVE              #<PARALLEL_SPLIT,R0     ; Address of parallel clocking waveform
984                                           CLOCK                                     ; Go clock out the CCD charge
988       P:000244 P:000244 0C0232            JMP     <IDLE
989    
990                                 ;  *****************  Exposure and readout routines  *****************
991    
992                                 ; Calculate readout parameters for whole image readout
993       P:000245 P:000245 0A00B0  RDCCD     JSET    #ST_SA,X:STATUS,SUB_IMG
                            000251
994       P:000247 P:000247 240000            MOVE              #0,X0
995       P:000248 P:000248 4C1100            MOVE                          X0,Y:<NP_SKIP
996       P:000249 P:000249 4C1200            MOVE                          X0,Y:<NS_SKP1
997       P:00024A P:00024A 4C1300            MOVE                          X0,Y:<NS_SKP2
998       P:00024B P:00024B 4C0A00            MOVE                          X0,Y:<NR_BIAS
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  /home/bearing/Documents/OWL/tim/tim.asm  Page 19



999       P:00024C P:00024C 4C8100            MOVE                          Y:<NSR,X0   ; NS_READ = NSR
1000      P:00024D P:00024D 4C0800            MOVE                          X0,Y:<NS_READ
1001      P:00024E P:00024E 4C8200            MOVE                          Y:<NPR,X0   ; NP_READ = NPR
1002      P:00024F P:00024F 4C0900            MOVE                          X0,Y:<NP_READ
1003      P:000250 P:000250 0C0265            JMP     <GO_ON
1004   
1005                                ; Set up for subarray readout
1006      P:000251 P:000251 67F400  SUB_IMG   MOVE              #READ_TABLE,R7          ; Parameter table for subimage readout
                            000017
1007      P:000253 P:000253 000000            NOP
1008      P:000254 P:000254 000000            NOP
1009      P:000255 P:000255 4CDF00            MOVE                          Y:(R7)+,X0
1010      P:000256 P:000256 4C1100            MOVE                          X0,Y:<NP_SKIP
1011      P:000257 P:000257 4CDF00            MOVE                          Y:(R7)+,X0  ; NS_SKP1 = # to skip before the read
1012      P:000258 P:000258 4C1200            MOVE                          X0,Y:<NS_SKP1
1013      P:000259 P:000259 5EDF00            MOVE                          Y:(R7)+,A   ; NS_SKP2 = # to skip after the read
1014      P:00025A P:00025A 200003            TST     A
1015      P:00025B P:00025B 0E725E            JGT     <SKP2_OK                          ; If NS_SKP2 .LE. = then set to zero
1016      P:00025C P:00025C 200013            CLR     A
1017      P:00025D P:00025D 000000            NOP
1018      P:00025E P:00025E 5C1300  SKP2_OK   MOVE                          A1,Y:<NS_SKP2
1019      P:00025F P:00025F 4C9500            MOVE                          Y:<NSREAD,X0 ; NS_READ = # of pixels to read
1020      P:000260 P:000260 4C0800            MOVE                          X0,Y:<NS_READ
1021      P:000261 P:000261 4C9600            MOVE                          Y:<NPREAD,X0 ; NP_READ = # of rows to read
1022      P:000262 P:000262 4C0900            MOVE                          X0,Y:<NP_READ
1023      P:000263 P:000263 4C9400            MOVE                          Y:<NRBIAS,X0 ; NR_BIAS = # of bias pixels to read
1024      P:000264 P:000264 4C0A00            MOVE                          X0,Y:<NR_BIAS
1025   
1026                                ; Generate new waveform and image dimensions
1027      P:000265 P:000265 0A0085  GO_ON     JCLR    #SPLIT_S,X:STATUS,SPL_PAR
                            000271
1028      P:000267 P:000267 5E8800            MOVE                          Y:<NS_READ,A ; Split serials require / 2
1029      P:000268 P:000268 000000            NOP
1030      P:000269 P:000269 200023            LSR     A
1031      P:00026A P:00026A 000000            NOP
1032      P:00026B P:00026B 5C0800            MOVE                          A1,Y:<NS_READ
1033      P:00026C P:00026C 5E8A00            MOVE                          Y:<NR_BIAS,A ; Number of bias pixels to read
1034      P:00026D P:00026D 000000            NOP
1035      P:00026E P:00026E 200023            LSR     A
1036      P:00026F P:00026F 000000            NOP
1037      P:000270 P:000270 5E0A00            MOVE                          A,Y:<NR_BIAS
1038   
1039      P:000271 P:000271 0A0086  SPL_PAR   JCLR    #SPLIT_P,X:STATUS,P_SHIFT
                            000278
1040      P:000273 P:000273 5E8900            MOVE                          Y:<NP_READ,A ; Split parallels require / 2
1041      P:000274 P:000274 000000            NOP
1042      P:000275 P:000275 200023            LSR     A
1043      P:000276 P:000276 000000            NOP
1044      P:000277 P:000277 5C0900            MOVE                          A1,Y:<NP_READ
1045   
1046                                ; Skip over the required number of rows for subimage readout
1047      P:000278 P:000278 061140  P_SHIFT   DO      Y:<NP_SKIP,L_PSKIP
                            00027E
1048      P:00027A P:00027A 688E00            MOVE                          Y:<PARALLEL,R0
1049                                          CLOCK
1053                                L_PSKIP
1054   
1055                                ; *******  Begin readout over the entire array  ******
1056      P:00027F P:00027F 060940            DO      Y:<NP_READ,LPR
                            0002B0
1057      P:000281 P:000281 688E00            MOVE                          Y:<PARALLEL,R0
1058                                          CLOCK
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  /home/bearing/Documents/OWL/tim/tim.asm  Page 20



1062   
1063                                ; Check for a command once per line. Only the ABORT command should be issued.
1064      P:000286 P:000286 330700            MOVE              #COM_BUF,R3
1065      P:000287 P:000287 0D00A5            JSR     <GET_RCV                          ; Was a command received?
1066      P:000288 P:000288 0E028E            JCC     <CONTINUE_READ                    ; If no, continue reading out
1067      P:000289 P:000289 0C005D            JMP     <PRC_RCV                          ; If yes, go process it
1068   
1069                                ; Abort the readout currently underway
1070      P:00028A P:00028A 0A0084  ABR_RDC   JCLR    #ST_RDC,X:<STATUS,ABORT_EXPOSURE
                            000385
1071      P:00028C P:00028C 00008C            ENDDO                                     ; Properly terminate readout loop
1072      P:00028D P:00028D 0C0385            JMP     <ABORT_EXPOSURE
1073                                CONTINUE_READ
1074   
1075                                ; Do a fast skip over NS_SKP1 pixels
1076      P:00028E P:00028E 061240            DO      Y:<NS_SKP1,L_S                    ; If NS_SKP1 = 0 this won't be
                            000294
1077      P:000290 P:000290 689000            MOVE                          Y:<SERIAL_SKIP,R0 ;   executed
1078                                          CLOCK
1082                                L_S
1083   
1084                                ; Clock, video process and pixels
1085      P:000295 P:000295 060840            DO      Y:<NS_READ,L_RD
                            00029B
1086      P:000297 P:000297 688F00            MOVE                          Y:<SERIAL_READ,R0
1087                                          CLOCK
1091                                L_RD
1092   
1093                                ; Skip over NS_SKP2 pixels if needed for subimage readout
1094      P:00029C P:00029C 5E9300            MOVE                          Y:<NS_SKP2,A ; Protect against negative values
1095      P:00029D P:00029D 200003            TST     A
1096      P:00029E P:00029E 0EF2A6            JLE     <RDBIAS
1097      P:00029F P:00029F 061340            DO      Y:<NS_SKP2,L_SB
                            0002A5
1098      P:0002A1 P:0002A1 689000            MOVE                          Y:<SERIAL_SKIP,R0
1099                                          CLOCK
1103                                L_SB
1104   
1105                                ; Read the bias pixels if needed for subimage readout
1106      P:0002A6 P:0002A6 5E8A00  RDBIAS    MOVE                          Y:<NR_BIAS,A ; Protect against negative values
1107      P:0002A7 P:0002A7 200003            TST     A
1108      P:0002A8 P:0002A8 0EF2B0            JLE     <L_RB
1109      P:0002A9 P:0002A9 060A40            DO      Y:<NR_BIAS,L_RB
                            0002AF
1110      P:0002AB P:0002AB 688F00            MOVE                          Y:<SERIAL_READ,R0
1111                                          CLOCK
1115      P:0002B0 P:0002B0 000000  L_RB      NOP
1116                                LPR
1117   
1118                                ; Restore the controller to non-image data transfer and idling if necessary
1119      P:0002B1 P:0002B1 0A0082  RDC_END   JCLR    #IDLMODE,X:<STATUS,NO_IDL
                            0002B7
1120      P:0002B3 P:0002B3 60F400            MOVE              #IDLE,R0
                            000232
1121      P:0002B5 P:0002B5 601F00            MOVE              R0,X:<IDL_ADR
1122      P:0002B6 P:0002B6 0C02B9            JMP     <RDC_E
1123      P:0002B7 P:0002B7 305A00  NO_IDL    MOVE              #TST_RCV,R0             ; Don't idle after readout
1124      P:0002B8 P:0002B8 601F00            MOVE              R0,X:<IDL_ADR
1125      P:0002B9 P:0002B9 0D03D5  RDC_E     JSR     <WAIT_TO_FINISH_CLOCKING
1126      P:0002BA P:0002BA 0A0004            BCLR    #ST_RDC,X:<STATUS                 ; Set status to not reading out
1127      P:0002BB P:0002BB 0C0054            JMP     <START
1128   
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  /home/bearing/Documents/OWL/tim/tim.asm  Page 21



1129                                ; ******  Include many routines not directly needed for readout  *******
1130                                          INCLUDE "timCCDmisc.asm"
1131                                ; Miscellaneous CCD control routines
1132                                POWER_OFF
1133      P:0002BC P:0002BC 0D02FE            JSR     <CLEAR_SWITCHES                   ; Clear all analog switches
1134      P:0002BD P:0002BD 0A8922            BSET    #LVEN,X:HDR
1135      P:0002BE P:0002BE 0A8923            BSET    #HVEN,X:HDR
1136      P:0002BF P:0002BF 0C008F            JMP     <FINISH
1137   
1138                                POWER_ON
1139      P:0002C0 P:0002C0 0D02FE            JSR     <CLEAR_SWITCHES                   ; Clear all analog switches
1140      P:0002C1 P:0002C1 0D02D6            JSR     <PON                              ; Turn on the power control board
1141      P:0002C2 P:0002C2 0A8980            JCLR    #PWROK,X:HDR,PWR_ERR              ; Test if the power turned on properly
                            0002D3
1142      P:0002C4 P:0002C4 0D02E3            JSR     <SET_BIASES                       ; Turn on the DC bias supplies
1143   
1144                                ; Turn the DACs ON
1145      P:0002C5 P:0002C5 56F400            MOVE              #$0C0004,A              ; Turn ON the DACs on all ARC-48s
                            0C0004
1146      P:0002C7 P:0002C7 241000            MOVE              #$100000,X0             ; Increment over board numbers
1147      P:0002C8 P:0002C8 060F80            DO      #15,L_ON                          ; 15 video processor boards
                            0002CD
1148      P:0002CA P:0002CA 0D020C            JSR     <XMIT_A_WORD                      ; Transmit A to TIM-A-STD
1149      P:0002CB P:0002CB 200040            ADD     X0,A
1150      P:0002CC P:0002CC 0D03D8            JSR     <PAL_DLY                          ; Delay for all this to happen
1151      P:0002CD P:0002CD 000000            NOP
1152                                L_ON
1153      P:0002CE P:0002CE 012F03            BCLR    #3,X:PCRD                         ; Turn the serial clock off
1154      P:0002CF P:0002CF 60F400            MOVE              #IDLE,R0                ; Put controller in IDLE state
                            000232
1155      P:0002D1 P:0002D1 601F00            MOVE              R0,X:<IDL_ADR
1156      P:0002D2 P:0002D2 0C008F            JMP     <FINISH
1157   
1158                                ; The power failed to turn on because of an error on the power control board
1159      P:0002D3 P:0002D3 0A8922  PWR_ERR   BSET    #LVEN,X:HDR                       ; Turn off the low voltage emable line
1160      P:0002D4 P:0002D4 0A8923            BSET    #HVEN,X:HDR                       ; Turn off the high voltage emable line
1161      P:0002D5 P:0002D5 0C008D            JMP     <ERROR
1162   
1163                                ; As a subroutine, turn on the low voltages (+/- 6.5V, +/- 16.5V) and delay
1164      P:0002D6 P:0002D6 0A8902  PON       BCLR    #LVEN,X:HDR                       ; Set these signals to DSP outputs
1165      P:0002D7 P:0002D7 44F400            MOVE              #5000000,X0
                            4C4B40
1166      P:0002D9 P:0002D9 06C400            DO      X0,*+3                            ; Wait 50 millisec for settling
                            0002DB
1167      P:0002DB P:0002DB 000000            NOP
1168   
1169                                ; Turn on the high +36 volt power line and then delay
1170      P:0002DC P:0002DC 0A8903            BCLR    #HVEN,X:HDR                       ; HVEN = Low => Turn on +36V
1171      P:0002DD P:0002DD 44F400            MOVE              #5000000,X0
                            4C4B40
1172      P:0002DF P:0002DF 06C400            DO      X0,*+3                            ; Wait 50 millisec for settling
                            0002E1
1173      P:0002E1 P:0002E1 000000            NOP
1174      P:0002E2 P:0002E2 00000C            RTS
1175   
1176                                ; Set all the DC bias voltages and video processor offset values, reading
1177                                ;   them from the 'DACS' table
1178                                SET_BIASES
1179      P:0002E3 P:0002E3 012F23            BSET    #3,X:PCRD                         ; Turn on the serial clock
1180      P:0002E4 P:0002E4 0A0F01            BCLR    #1,X:<LATCH                       ; Separate updates of clock driver
1181      P:0002E5 P:0002E5 0A0F20            BSET    #CDAC,X:<LATCH                    ; Disable clearing of DACs
1182      P:0002E6 P:0002E6 0A0F22            BSET    #ENCK,X:<LATCH                    ; Enable clock and DAC output switches
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 22



1183      P:0002E7 P:0002E7 09F0B5            MOVEP             X:LATCH,Y:WRLATCH       ; Write it to the hardware
                            00000F
1184      P:0002E9 P:0002E9 0D03D8            JSR     <PAL_DLY                          ; Delay for all this to happen
1185   
1186                                ; Read DAC values from a table, and write them to the DACs
1187      P:0002EA P:0002EA 60F400            MOVE              #DACS,R0                ; Get starting address of DAC values
                            0000BD
1188      P:0002EC P:0002EC 000000            NOP
1189      P:0002ED P:0002ED 000000            NOP
1190      P:0002EE P:0002EE 065840            DO      Y:(R0)+,L_DAC                     ; Repeat Y:(R0)+ times
                            0002F2
1191      P:0002F0 P:0002F0 5ED800            MOVE                          Y:(R0)+,A   ; Read the table entry
1192      P:0002F1 P:0002F1 0D020C            JSR     <XMIT_A_WORD                      ; Transmit it to TIM-A-STD
1193      P:0002F2 P:0002F2 000000            NOP
1194                                L_DAC
1195   
1196                                ; Let the DAC voltages all ramp up before exiting
1197      P:0002F3 P:0002F3 44F400            MOVE              #400000,X0
                            061A80
1198      P:0002F5 P:0002F5 06C400            DO      X0,*+3                            ; 4 millisec delay
                            0002F7
1199      P:0002F7 P:0002F7 000000            NOP
1200      P:0002F8 P:0002F8 00000C            RTS
1201   
1202                                SET_BIAS_VOLTAGES
1203      P:0002F9 P:0002F9 0D02E3            JSR     <SET_BIASES
1204      P:0002FA P:0002FA 012F03            BCLR    #3,X:PCRD                         ; Turn the serial clock off
1205      P:0002FB P:0002FB 0C008F            JMP     <FINISH
1206   
1207      P:0002FC P:0002FC 0D02FE  CLR_SWS   JSR     <CLEAR_SWITCHES
1208      P:0002FD P:0002FD 0C008F            JMP     <FINISH
1209   
1210                                ; Clear all video processor analog switches to lower their power dissipation
1211                                CLEAR_SWITCHES
1212      P:0002FE P:0002FE 012F23            BSET    #3,X:PCRD                         ; Turn the serial clock on
1213      P:0002FF P:0002FF 2E0C00            MOVE              #$0C0000,A              ; Turn OFF the DACs on ARC-48
1214      P:000300 P:000300 20001B            CLR     B
1215      P:000301 P:000301 241000            MOVE              #$100000,X0             ; Increment over board numbers for DAC write
s
1216      P:000302 P:000302 45F400            MOVE              #$001000,X1             ; Increment over board numbers for WRSS writ
es
                            001000
1217      P:000304 P:000304 060F80            DO      #15,L_VIDEO                       ; Fifteen video processor boards maximum
                            00030B
1218      P:000306 P:000306 0D020C            JSR     <XMIT_A_WORD                      ; Transmit A to TIM-A-STD
1219      P:000307 P:000307 200040            ADD     X0,A
1220      P:000308 P:000308 5F7000            MOVE                          B,Y:WRSS
                            FFFFF3
1221      P:00030A P:00030A 0D03D8            JSR     <PAL_DLY                          ; Delay for the serial data transmission
1222      P:00030B P:00030B 200068            ADD     X1,B
1223                                L_VIDEO
1224      P:00030C P:00030C 0A0F00            BCLR    #CDAC,X:<LATCH                    ; Enable clearing of DACs
1225      P:00030D P:00030D 0A0F02            BCLR    #ENCK,X:<LATCH                    ; Disable clock and DAC output switches
1226      P:00030E P:00030E 09F0B5            MOVEP             X:LATCH,Y:WRLATCH       ; Execute these two operations
                            00000F
1227      P:000310 P:000310 012F03            BCLR    #3,X:PCRD                         ; Turn the serial clock off
1228      P:000311 P:000311 00000C            RTS
1229   
1230                                ; Open the shutter by setting the backplane bit TIM-LATCH0
1231      P:000312 P:000312 0A0023  OSHUT     BSET    #ST_SHUT,X:<STATUS                ; Set status bit to mean shutter open
1232      P:000313 P:000313 0A0F24            BSET    #SHUTTER,X:<LATCH                 ; Set (DC) hardware shutter bit to open
1233      P:000314 P:000314 09F0B5            MOVEP             X:LATCH,Y:WRLATCH       ; Write it to the hardware
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 23



                            00000F
1234      P:000316 P:000316 00000C            RTS
1235   
1236                                ; Close the shutter by clearing the backplane bit TIM-LATCH0
1237      P:000317 P:000317 0A0003  CSHUT     BCLR    #ST_SHUT,X:<STATUS                ; Clear status to mean shutter closed
1238      P:000318 P:000318 0A0F04            BCLR    #SHUTTER,X:<LATCH                 ; Clear (DC) hardware shutter bit to close
1239      P:000319 P:000319 09F0B5            MOVEP             X:LATCH,Y:WRLATCH       ; Write it to the hardware
                            00000F
1240      P:00031B P:00031B 00000C            RTS
1241   
1242                                ; Open the shutter from the timing board, executed as a command
1243                                OPEN_SHUTTER
1244      P:00031C P:00031C 0D0312            JSR     <OSHUT
1245      P:00031D P:00031D 0C008F            JMP     <FINISH
1246   
1247                                ; Close the shutter from the timing board, executed as a command
1248                                CLOSE_SHUTTER
1249      P:00031E P:00031E 0D0317            JSR     <CSHUT
1250      P:00031F P:00031F 0C008F            JMP     <FINISH
1251   
1252                                ; Clear the CCD, executed as a command
1253      P:000320 P:000320 0D0322  CLEAR     JSR     <CLR_CCD
1254      P:000321 P:000321 0C008F            JMP     <FINISH
1255   
1256                                ; Default clearing routine with serial clocks inactive
1257                                ; Fast clear image before each exposure, executed as a subroutine
1258      P:000322 P:000322 060340  CLR_CCD   DO      Y:<NP_CLR,LPCLR2                  ; Loop over number of lines in image
                            000333
1259      P:000324 P:000324 60F400            MOVE              #PARALLEL_SPLIT,R0      ; Address of parallel transfer waveform
                            00002A
1260                                          CLOCK
1264      P:00032A P:00032A 0A8989            JCLR    #EF,X:HDR,LPCLR1                  ; Simple test for fast execution
                            000333
1265      P:00032C P:00032C 330700            MOVE              #COM_BUF,R3
1266      P:00032D P:00032D 0D00A5            JSR     <GET_RCV                          ; Check for FO command
1267      P:00032E P:00032E 0E0333            JCC     <LPCLR1                           ; Continue no commands received
1268   
1269      P:00032F P:00032F 60F400            MOVE              #LPCLR1,R0              ; Return to LPCLR1 after processing command
                            000333
1270      P:000331 P:000331 601F00            MOVE              R0,X:<IDL_ADR
1271      P:000332 P:000332 0C005D            JMP     <PRC_RCV
1272      P:000333 P:000333 000000  LPCLR1    NOP
1273                                LPCLR2
1274      P:000334 P:000334 305A00            MOVE              #TST_RCV,R0             ; Process commands during the exposure
1275      P:000335 P:000335 601F00            MOVE              R0,X:<IDL_ADR
1276      P:000336 P:000336 0D03D5            JSR     <WAIT_TO_FINISH_CLOCKING
1277      P:000337 P:000337 00000C            RTS
1278   
1279                                ; Start the exposure timer and monitor its progress
1280      P:000338 P:000338 07F40E  EXPOSE    MOVEP             #0,X:TLR0               ; Load 0 into counter timer
                            000000
1281      P:00033A P:00033A 240000            MOVE              #0,X0
1282      P:00033B P:00033B 441100            MOVE              X0,X:<ELAPSED_TIME      ; Set elapsed exposure time to zero
1283      P:00033C P:00033C 579000            MOVE              X:<EXPOSURE_TIME,B
1284      P:00033D P:00033D 20000B            TST     B                                 ; Special test for zero exposure time
1285      P:00033E P:00033E 0EA34A            JEQ     <END_EXP                          ; Don't even start an exposure
1286      P:00033F P:00033F 01418C            SUB     #1,B                              ; Timer counts from X:TCPR0+1 to zero
1287      P:000340 P:000340 010F20            BSET    #TIM_BIT,X:TCSR0                  ; Enable the timer #0
1288      P:000341 P:000341 577000            MOVE              B,X:TCPR0
                            FFFF8D
1289      P:000343 P:000343 0A8989  CHK_RCV   JCLR    #EF,X:HDR,CHK_TIM                 ; Simple test for fast execution
                            000348
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 24



1290      P:000345 P:000345 330700            MOVE              #COM_BUF,R3             ; The beginning of the command buffer
1291      P:000346 P:000346 0D00A5            JSR     <GET_RCV                          ; Check for an incoming command
1292      P:000347 P:000347 0E805D            JCS     <PRC_RCV                          ; If command is received, go check it
1293      P:000348 P:000348 018F95  CHK_TIM   JCLR    #TCF,X:TCSR0,CHK_RCV              ; Wait for timer to equal compare value
                            000343
1294      P:00034A P:00034A 010F00  END_EXP   BCLR    #TIM_BIT,X:TCSR0                  ; Disable the timer
1295      P:00034B P:00034B 0AE780            JMP     (R7)                              ; This contains the return address
1296   
1297                                ; Start the exposure, expose, and initiate the CCD readout
1298                                START_EXPOSURE
1299      P:00034C P:00034C 57F400            MOVE              #$020102,B
                            020102
1300      P:00034E P:00034E 0D00EB            JSR     <XMT_WRD
1301      P:00034F P:00034F 57F400            MOVE              #'IIA',B                ; Initialize the PCI image address
                            494941
1302      P:000351 P:000351 0D00EB            JSR     <XMT_WRD
1303      P:000352 P:000352 0D0322            JSR     <CLR_CCD
1304   
1305                                ; Operate the shutter if needed and begin exposure
1306      P:000353 P:000353 0A008B            JCLR    #SHUT,X:STATUS,L_SEX0
                            000356
1307      P:000355 P:000355 0D0312            JSR     <OSHUT
1308      P:000356 P:000356 67F400  L_SEX0    MOVE              #L_SEX1,R7              ; Return address at end of exposure
                            000359
1309      P:000358 P:000358 0C0338            JMP     <EXPOSE                           ; Delay for specified exposure time
1310                                L_SEX1
1311   
1312      P:000359 P:000359 0D03C8  STR_RDC   JSR     <PCI_READ_IMAGE                   ; Get the PCI board reading the image
1313      P:00035A P:00035A 0A0024            BSET    #ST_RDC,X:<STATUS                 ; Set status to reading out
1314      P:00035B P:00035B 0A008B            JCLR    #SHUT,X:STATUS,TST_SYN
                            00035E
1315      P:00035D P:00035D 0D0317            JSR     <CSHUT                            ; Close the shutter if necessary
1316      P:00035E P:00035E 0A00AA  TST_SYN   JSET    #TST_IMG,X:STATUS,SYNTHETIC_IMAGE
                            000395
1317   
1318                                ; Delay readout until the shutter has fully closed
1319      P:000360 P:000360 5E8D00            MOVE                          Y:<SHDEL,A
1320      P:000361 P:000361 200003            TST     A
1321      P:000362 P:000362 0EF36B            JLE     <S_DEL0
1322      P:000363 P:000363 44F400            MOVE              #100000,X0
                            0186A0
1323      P:000365 P:000365 06CE00            DO      A,S_DEL0                          ; Delay by Y:SHDEL milliseconds
                            00036A
1324      P:000367 P:000367 06C400            DO      X0,S_DEL1
                            000369
1325      P:000369 P:000369 000000            NOP
1326      P:00036A P:00036A 000000  S_DEL1    NOP
1327      P:00036B P:00036B 000000  S_DEL0    NOP
1328   
1329      P:00036C P:00036C 0C0245            JMP     <RDCCD                            ; Finally, go read out the CCD
1330   
1331                                ; Set the desired exposure time
1332                                SET_EXPOSURE_TIME
1333      P:00036D P:00036D 46DB00            MOVE              X:(R3)+,Y0
1334      P:00036E P:00036E 461000            MOVE              Y0,X:EXPOSURE_TIME
1335      P:00036F P:00036F 04C68D            MOVEP             Y0,X:TCPR0
1336      P:000370 P:000370 0C008F            JMP     <FINISH
1337   
1338                                ; Read the time remaining until the exposure ends
1339                                READ_EXPOSURE_TIME
1340      P:000371 P:000371 018FA0            JSET    #TIM_BIT,X:TCSR0,RD_TIM           ; Read DSP timer if its running
                            000375
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 25



1341      P:000373 P:000373 479100            MOVE              X:<ELAPSED_TIME,Y1
1342      P:000374 P:000374 0C0090            JMP     <FINISH1
1343      P:000375 P:000375 47F000  RD_TIM    MOVE              X:TCR0,Y1               ; Read elapsed exposure time
                            FFFF8C
1344      P:000377 P:000377 0C0090            JMP     <FINISH1
1345   
1346                                ; Pause the exposure - close the shutter and stop the timer
1347                                PAUSE_EXPOSURE
1348      P:000378 P:000378 07700C            MOVEP             X:TCR0,X:ELAPSED_TIME   ; Save the elapsed exposure time
                            000011
1349      P:00037A P:00037A 010F00            BCLR    #TIM_BIT,X:TCSR0                  ; Disable the DSP exposure timer
1350      P:00037B P:00037B 0D0317            JSR     <CSHUT                            ; Close the shutter
1351      P:00037C P:00037C 0C008F            JMP     <FINISH
1352   
1353                                ; Resume the exposure - open the shutter if needed and restart the timer
1354                                RESUME_EXPOSURE
1355      P:00037D P:00037D 010F29            BSET    #TRM,X:TCSR0                      ; To be sure it will load TLR0
1356      P:00037E P:00037E 07700C            MOVEP             X:TCR0,X:TLR0           ; Restore elapsed exposure time
                            FFFF8E
1357      P:000380 P:000380 010F20            BSET    #TIM_BIT,X:TCSR0                  ; Re-enable the DSP exposure timer
1358      P:000381 P:000381 0A008B            JCLR    #SHUT,X:STATUS,L_RES
                            000384
1359      P:000383 P:000383 0D0312            JSR     <OSHUT                            ; Open the shutter if necessary
1360      P:000384 P:000384 0C008F  L_RES     JMP     <FINISH
1361   
1362                                ; Abort exposure - close the shutter, stop the timer and resume idle mode
1363                                ABORT_EXPOSURE
1364      P:000385 P:000385 0D0317            JSR     <CSHUT                            ; Close the shutter
1365      P:000386 P:000386 010F00            BCLR    #TIM_BIT,X:TCSR0                  ; Disable the DSP exposure timer
1366      P:000387 P:000387 0A0082            JCLR    #IDLMODE,X:<STATUS,NO_IDL2        ; Don't idle after readout
                            00038D
1367      P:000389 P:000389 60F400            MOVE              #IDLE,R0
                            000232
1368      P:00038B P:00038B 601F00            MOVE              R0,X:<IDL_ADR
1369      P:00038C P:00038C 0C038F            JMP     <RDC_E2
1370      P:00038D P:00038D 305A00  NO_IDL2   MOVE              #TST_RCV,R0
1371      P:00038E P:00038E 601F00            MOVE              R0,X:<IDL_ADR
1372      P:00038F P:00038F 0D03D5  RDC_E2    JSR     <WAIT_TO_FINISH_CLOCKING
1373      P:000390 P:000390 0A0004            BCLR    #ST_RDC,X:<STATUS                 ; Set status to not reading out
1374      P:000391 P:000391 06A08F            DO      #4000,*+3                         ; Wait 40 microsec for the fiber
                            000393
1375      P:000393 P:000393 000000            NOP                                       ;  optic to clear out
1376      P:000394 P:000394 0C008F            JMP     <FINISH
1377   
1378                                ; Generate a synthetic image by simply incrementing the pixel counts
1379                                SYNTHETIC_IMAGE
1380      P:000395 P:000395 200013            CLR     A
1381      P:000396 P:000396 060240            DO      Y:<NPR,LPR_TST                    ; Loop over each line readout
                            0003A1
1382      P:000398 P:000398 060140            DO      Y:<NSR,LSR_TST                    ; Loop over number of pixels per line
                            0003A0
1383      P:00039A P:00039A 0614A0            REP     #20                               ; #20 => 1.0 microsec per pixel
1384      P:00039B P:00039B 000000            NOP
1385      P:00039C P:00039C 014180            ADD     #1,A                              ; Pixel data = Pixel data + 1
1386      P:00039D P:00039D 000000            NOP
1387      P:00039E P:00039E 21CF00            MOVE              A,B
1388      P:00039F P:00039F 0D03A3            JSR     <XMT_PIX                          ;  transmit them
1389      P:0003A0 P:0003A0 000000            NOP
1390                                LSR_TST
1391      P:0003A1 P:0003A1 000000            NOP
1392                                LPR_TST
1393      P:0003A2 P:0003A2 0C02B1            JMP     <RDC_END                          ; Normal exit
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 26



1394   
1395                                ; Transmit the 16-bit pixel datum in B1 to the host computer
1396      P:0003A3 P:0003A3 0C1DA1  XMT_PIX   ASL     #16,B,B
1397      P:0003A4 P:0003A4 000000            NOP
1398      P:0003A5 P:0003A5 216500            MOVE              B2,X1
1399      P:0003A6 P:0003A6 0C1D91            ASL     #8,B,B
1400      P:0003A7 P:0003A7 000000            NOP
1401      P:0003A8 P:0003A8 216400            MOVE              B2,X0
1402      P:0003A9 P:0003A9 000000            NOP
1403      P:0003AA P:0003AA 09C532            MOVEP             X1,Y:WRFO
1404      P:0003AB P:0003AB 09C432            MOVEP             X0,Y:WRFO
1405      P:0003AC P:0003AC 00000C            RTS
1406   
1407                                ; Test the hardware to read A/D values directly into the DSP instead
1408                                ;   of using the SXMIT option, A/Ds #2 and 3.
1409      P:0003AD P:0003AD 57F000  READ_AD   MOVE              X:(RDAD+2),B
                            010002
1410      P:0003AF P:0003AF 0C1DA1            ASL     #16,B,B
1411      P:0003B0 P:0003B0 000000            NOP
1412      P:0003B1 P:0003B1 216500            MOVE              B2,X1
1413      P:0003B2 P:0003B2 0C1D91            ASL     #8,B,B
1414      P:0003B3 P:0003B3 000000            NOP
1415      P:0003B4 P:0003B4 216400            MOVE              B2,X0
1416      P:0003B5 P:0003B5 000000            NOP
1417      P:0003B6 P:0003B6 09C532            MOVEP             X1,Y:WRFO
1418      P:0003B7 P:0003B7 09C432            MOVEP             X0,Y:WRFO
1419      P:0003B8 P:0003B8 060AA0            REP     #10
1420      P:0003B9 P:0003B9 000000            NOP
1421      P:0003BA P:0003BA 57F000            MOVE              X:(RDAD+3),B
                            010003
1422      P:0003BC P:0003BC 0C1DA1            ASL     #16,B,B
1423      P:0003BD P:0003BD 000000            NOP
1424      P:0003BE P:0003BE 216500            MOVE              B2,X1
1425      P:0003BF P:0003BF 0C1D91            ASL     #8,B,B
1426      P:0003C0 P:0003C0 000000            NOP
1427      P:0003C1 P:0003C1 216400            MOVE              B2,X0
1428      P:0003C2 P:0003C2 000000            NOP
1429      P:0003C3 P:0003C3 09C532            MOVEP             X1,Y:WRFO
1430      P:0003C4 P:0003C4 09C432            MOVEP             X0,Y:WRFO
1431      P:0003C5 P:0003C5 060AA0            REP     #10
1432      P:0003C6 P:0003C6 000000            NOP
1433      P:0003C7 P:0003C7 00000C            RTS
1434   
1435                                ; Alert the PCI interface board that images are coming soon
1436                                PCI_READ_IMAGE
1437      P:0003C8 P:0003C8 57F400            MOVE              #$020104,B              ; Send header word to the FO xmtr
                            020104
1438      P:0003CA P:0003CA 0D00EB            JSR     <XMT_WRD
1439      P:0003CB P:0003CB 57F400            MOVE              #'RDA',B
                            524441
1440      P:0003CD P:0003CD 0D00EB            JSR     <XMT_WRD
1441      P:0003CE P:0003CE 5FF000            MOVE                          Y:NSR,B     ; Number of columns to read
                            000001
1442      P:0003D0 P:0003D0 0D00EB            JSR     <XMT_WRD
1443      P:0003D1 P:0003D1 5FF000            MOVE                          Y:NPR,B     ; Number of rows to read
                            000002
1444      P:0003D3 P:0003D3 0D00EB            JSR     <XMT_WRD
1445      P:0003D4 P:0003D4 00000C            RTS
1446   
1447                                ; Wait for the clocking to be complete before proceeding
1448                                WAIT_TO_FINISH_CLOCKING
1449      P:0003D5 P:0003D5 01ADA1            JSET    #SSFEF,X:PDRD,*                   ; Wait for the SS FIFO to be empty
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 27



                            0003D5
1450      P:0003D7 P:0003D7 00000C            RTS
1451   
1452                                ; Delay for serial writes to the PALs and DACs by 8 microsec
1453      P:0003D8 P:0003D8 062083  PAL_DLY   DO      #800,*+3                          ; Wait 8 usec for serial data xmit
                            0003DA
1454      P:0003DA P:0003DA 000000            NOP
1455      P:0003DB P:0003DB 00000C            RTS
1456   
1457                                ; Let the host computer read the controller configuration
1458                                READ_CONTROLLER_CONFIGURATION
1459      P:0003DC P:0003DC 4F8700            MOVE                          Y:<CONFIG,Y1 ; Just transmit the configuration
1460      P:0003DD P:0003DD 0C0090            JMP     <FINISH1
1461   
1462                                ; Set a particular DAC numbers of the ARC32 clock driver voltages.
1463                                ;
1464                                ; SBN  #BOARD  #DAC  ['CLK' or 'VID'] voltage
1465                                ;
1466                                ;                               #BOARD is from 0 to 15
1467                                ;                               #DAC number
1468                                ;                               #voltage is from 0 to 4095
1469   
1470                                SET_BIAS_NUMBER                                     ; Set bias number
1471      P:0003DE P:0003DE 012F23            BSET    #3,X:PCRD                         ; Turn on the serial clock
1472      P:0003DF P:0003DF 56DB00            MOVE              X:(R3)+,A               ; First argument is board number, 0 to 15
1473      P:0003E0 P:0003E0 0614A0            REP     #20
1474      P:0003E1 P:0003E1 200033            LSL     A
1475      P:0003E2 P:0003E2 000000            NOP
1476      P:0003E3 P:0003E3 21C500            MOVE              A,X1                    ; Save the board number
1477      P:0003E4 P:0003E4 21C600            MOVE              A,Y0                    ; Save again just because...DC
1478      P:0003E5 P:0003E5 56DB00            MOVE              X:(R3)+,A               ; Second argument is DAC number
1479      P:0003E6 P:0003E6 57DB00            MOVE              X:(R3)+,B               ; Third argument is 'VID' or 'CLK' string
1480      P:0003E7 P:0003E7 0140CD            CMP     #'VID',B
                            564944
1481      P:0003E9 P:0003E9 0E23F2            JNE     <CLK_DRV
1482      P:0003EA P:0003EA 060EA0            REP     #14
1483      P:0003EB P:0003EB 200033            LSL     A
1484      P:0003EC P:0003EC 200052            OR      Y0,A
1485      P:0003ED P:0003ED 000000            NOP
1486      P:0003EE P:0003EE 0ACC73            BSET    #19,A1                            ; Set bits to mean video processor DAC
1487      P:0003EF P:0003EF 000000            NOP
1488      P:0003F0 P:0003F0 0ACC72            BSET    #18,A1
1489      P:0003F1 P:0003F1 0C03F6            JMP     <VID_BRD
1490   
1491      P:0003F2 P:0003F2 0140CD  CLK_DRV   CMP     #'CLK',B
                            434C4B
1492      P:0003F4 P:0003F4 0E2435            JNE     <ERR_SBN
1493      P:0003F5 P:0003F5 0C0400            JMP     <CLK_BRD
1494   
1495      P:0003F6 P:0003F6 21C400  VID_BRD   MOVE              A,X0
1496      P:0003F7 P:0003F7 56DB00            MOVE              X:(R3)+,A               ; Fourth argument is voltage value, 0 to $ff
f
1497      P:0003F8 P:0003F8 46F400            MOVE              #$000FFF,Y0             ; Mask off just 12 bits to be sure
                            000FFF
1498      P:0003FA P:0003FA 200056            AND     Y0,A
1499      P:0003FB P:0003FB 200042            OR      X0,A
1500      P:0003FC P:0003FC 0D020C            JSR     <XMIT_A_WORD                      ; Transmit A to TIM-A-STD
1501      P:0003FD P:0003FD 0D03D8            JSR     <PAL_DLY                          ; Wait for the number to be sent
1502      P:0003FE P:0003FE 012F03            BCLR    #3,X:PCRD                         ; Turn the serial clock off
1503      P:0003FF P:0003FF 0C008F            JMP     <FINISH
1504   
1505                                ; For ARC32 do some trickiness to set the chip select and address bits
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 28



1506      P:000400 P:000400 218F00  CLK_BRD   MOVE              A1,B
1507      P:000401 P:000401 060EA0            REP     #14
1508      P:000402 P:000402 200033            LSL     A
1509      P:000403 P:000403 240E00            MOVE              #$0E0000,X0
1510      P:000404 P:000404 200046            AND     X0,A
1511      P:000405 P:000405 44F400            MOVE              #>7,X0
                            000007
1512      P:000407 P:000407 20004E            AND     X0,B                              ; Get 3 least significant bits of clock #
1513      P:000408 P:000408 01408D            CMP     #0,B
1514      P:000409 P:000409 0E240C            JNE     <CLK_1
1515      P:00040A P:00040A 0ACE68            BSET    #8,A
1516      P:00040B P:00040B 0C0427            JMP     <BD_SET
1517      P:00040C P:00040C 01418D  CLK_1     CMP     #1,B
1518      P:00040D P:00040D 0E2410            JNE     <CLK_2
1519      P:00040E P:00040E 0ACE69            BSET    #9,A
1520      P:00040F P:00040F 0C0427            JMP     <BD_SET
1521      P:000410 P:000410 01428D  CLK_2     CMP     #2,B
1522      P:000411 P:000411 0E2414            JNE     <CLK_3
1523      P:000412 P:000412 0ACE6A            BSET    #10,A
1524      P:000413 P:000413 0C0427            JMP     <BD_SET
1525      P:000414 P:000414 01438D  CLK_3     CMP     #3,B
1526      P:000415 P:000415 0E2418            JNE     <CLK_4
1527      P:000416 P:000416 0ACE6B            BSET    #11,A
1528      P:000417 P:000417 0C0427            JMP     <BD_SET
1529      P:000418 P:000418 01448D  CLK_4     CMP     #4,B
1530      P:000419 P:000419 0E241C            JNE     <CLK_5
1531      P:00041A P:00041A 0ACE6D            BSET    #13,A
1532      P:00041B P:00041B 0C0427            JMP     <BD_SET
1533      P:00041C P:00041C 01458D  CLK_5     CMP     #5,B
1534      P:00041D P:00041D 0E2420            JNE     <CLK_6
1535      P:00041E P:00041E 0ACE6E            BSET    #14,A
1536      P:00041F P:00041F 0C0427            JMP     <BD_SET
1537      P:000420 P:000420 01468D  CLK_6     CMP     #6,B
1538      P:000421 P:000421 0E2424            JNE     <CLK_7
1539      P:000422 P:000422 0ACE6F            BSET    #15,A
1540      P:000423 P:000423 0C0427            JMP     <BD_SET
1541      P:000424 P:000424 01478D  CLK_7     CMP     #7,B
1542      P:000425 P:000425 0E2427            JNE     <BD_SET
1543      P:000426 P:000426 0ACE70            BSET    #16,A
1544   
1545      P:000427 P:000427 200062  BD_SET    OR      X1,A                              ; Add on the board number
1546      P:000428 P:000428 000000            NOP
1547      P:000429 P:000429 21C400            MOVE              A,X0
1548      P:00042A P:00042A 56DB00            MOVE              X:(R3)+,A               ; Fourth argument is voltage value, 0 to $ff
f
1549      P:00042B P:00042B 0604A0            REP     #4
1550      P:00042C P:00042C 200023            LSR     A                                 ; Convert 12 bits to 8 bits for ARC32
1551      P:00042D P:00042D 46F400            MOVE              #>$FF,Y0                ; Mask off just 8 bits
                            0000FF
1552      P:00042F P:00042F 200056            AND     Y0,A
1553      P:000430 P:000430 200042            OR      X0,A
1554   
1555      P:000431 P:000431 0D020C            JSR     <XMIT_A_WORD                      ; Transmit A to TIM-A-STD
1556      P:000432 P:000432 0D03D8            JSR     <PAL_DLY                          ; Wait for the number to be sent
1557      P:000433 P:000433 012F03            BCLR    #3,X:PCRD                         ; Turn the serial clock off
1558      P:000434 P:000434 0C008F            JMP     <FINISH
1559      P:000435 P:000435 56DB00  ERR_SBN   MOVE              X:(R3)+,A               ; Read and discard the fourth argument
1560      P:000436 P:000436 012F03            BCLR    #3,X:PCRD                         ; Turn the serial clock off
1561      P:000437 P:000437 0C008D            JMP     <ERROR
1562   
1563                                ; Specify the MUX value to be output on the clock driver board
1564                                ; Command syntax is  SMX  #clock_driver_board #MUX1 #MUX2
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 29



1565                                ;                               #clock_driver_board from 0 to 15
1566                                ;                               #MUX1, #MUX2 from 0 to 23
1567   
1568      P:000438 P:000438 012F23  SET_MUX   BSET    #3,X:PCRD                         ; Turn on the serial clock
1569      P:000439 P:000439 56DB00            MOVE              X:(R3)+,A               ; Clock driver board number
1570      P:00043A P:00043A 0614A0            REP     #20
1571      P:00043B P:00043B 200033            LSL     A
1572      P:00043C P:00043C 44F400            MOVE              #$003000,X0
                            003000
1573      P:00043E P:00043E 200042            OR      X0,A
1574      P:00043F P:00043F 000000            NOP
1575      P:000440 P:000440 21C500            MOVE              A,X1                    ; Move here for storage
1576   
1577                                ; Get the first MUX number
1578      P:000441 P:000441 56DB00            MOVE              X:(R3)+,A               ; Get the first MUX number
1579      P:000442 P:000442 0AF0A9            JLT     ERR_SM1
                            000486
1580      P:000444 P:000444 44F400            MOVE              #>24,X0                 ; Check for argument less than 32
                            000018
1581      P:000446 P:000446 200045            CMP     X0,A
1582      P:000447 P:000447 0AF0A1            JGE     ERR_SM1
                            000486
1583      P:000449 P:000449 21CF00            MOVE              A,B
1584      P:00044A P:00044A 44F400            MOVE              #>7,X0
                            000007
1585      P:00044C P:00044C 20004E            AND     X0,B
1586      P:00044D P:00044D 44F400            MOVE              #>$18,X0
                            000018
1587      P:00044F P:00044F 200046            AND     X0,A
1588      P:000450 P:000450 0E2453            JNE     <SMX_1                            ; Test for 0 <= MUX number <= 7
1589      P:000451 P:000451 0ACD63            BSET    #3,B1
1590      P:000452 P:000452 0C045E            JMP     <SMX_A
1591      P:000453 P:000453 44F400  SMX_1     MOVE              #>$08,X0
                            000008
1592      P:000455 P:000455 200045            CMP     X0,A                              ; Test for 8 <= MUX number <= 15
1593      P:000456 P:000456 0E2459            JNE     <SMX_2
1594      P:000457 P:000457 0ACD64            BSET    #4,B1
1595      P:000458 P:000458 0C045E            JMP     <SMX_A
1596      P:000459 P:000459 44F400  SMX_2     MOVE              #>$10,X0
                            000010
1597      P:00045B P:00045B 200045            CMP     X0,A                              ; Test for 16 <= MUX number <= 23
1598      P:00045C P:00045C 0E2486            JNE     <ERR_SM1
1599      P:00045D P:00045D 0ACD65            BSET    #5,B1
1600      P:00045E P:00045E 20006A  SMX_A     OR      X1,B1                             ; Add prefix to MUX numbers
1601      P:00045F P:00045F 000000            NOP
1602      P:000460 P:000460 21A700            MOVE              B1,Y1
1603   
1604                                ; Add on the second MUX number
1605      P:000461 P:000461 56DB00            MOVE              X:(R3)+,A               ; Get the next MUX number
1606      P:000462 P:000462 0E908D            JLT     <ERROR
1607      P:000463 P:000463 44F400            MOVE              #>24,X0                 ; Check for argument less than 32
                            000018
1608      P:000465 P:000465 200045            CMP     X0,A
1609      P:000466 P:000466 0E108D            JGE     <ERROR
1610      P:000467 P:000467 0606A0            REP     #6
1611      P:000468 P:000468 200033            LSL     A
1612      P:000469 P:000469 000000            NOP
1613      P:00046A P:00046A 21CF00            MOVE              A,B
1614      P:00046B P:00046B 44F400            MOVE              #$1C0,X0
                            0001C0
1615      P:00046D P:00046D 20004E            AND     X0,B
1616      P:00046E P:00046E 44F400            MOVE              #>$600,X0
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 30



                            000600
1617      P:000470 P:000470 200046            AND     X0,A
1618      P:000471 P:000471 0E2474            JNE     <SMX_3                            ; Test for 0 <= MUX number <= 7
1619      P:000472 P:000472 0ACD69            BSET    #9,B1
1620      P:000473 P:000473 0C047F            JMP     <SMX_B
1621      P:000474 P:000474 44F400  SMX_3     MOVE              #>$200,X0
                            000200
1622      P:000476 P:000476 200045            CMP     X0,A                              ; Test for 8 <= MUX number <= 15
1623      P:000477 P:000477 0E247A            JNE     <SMX_4
1624      P:000478 P:000478 0ACD6A            BSET    #10,B1
1625      P:000479 P:000479 0C047F            JMP     <SMX_B
1626      P:00047A P:00047A 44F400  SMX_4     MOVE              #>$400,X0
                            000400
1627      P:00047C P:00047C 200045            CMP     X0,A                              ; Test for 16 <= MUX number <= 23
1628      P:00047D P:00047D 0E208D            JNE     <ERROR
1629      P:00047E P:00047E 0ACD6B            BSET    #11,B1
1630      P:00047F P:00047F 200078  SMX_B     ADD     Y1,B                              ; Add prefix to MUX numbers
1631      P:000480 P:000480 000000            NOP
1632      P:000481 P:000481 21AE00            MOVE              B1,A
1633      P:000482 P:000482 0D020C            JSR     <XMIT_A_WORD                      ; Transmit A to TIM-A-STD
1634      P:000483 P:000483 0D03D8            JSR     <PAL_DLY                          ; Delay for all this to happen
1635      P:000484 P:000484 012F03            BCLR    #3,X:PCRD                         ; Turn the serial clock off
1636      P:000485 P:000485 0C008F            JMP     <FINISH
1637      P:000486 P:000486 56DB00  ERR_SM1   MOVE              X:(R3)+,A
1638      P:000487 P:000487 012F03            BCLR    #3,X:PCRD                         ; Turn the serial clock off
1639      P:000488 P:000488 0C008D            JMP     <ERROR
1640   
1641                                ; Specify subarray readout coordinates, one rectangle only
1642                                SET_SUBARRAY_SIZES
1643      P:000489 P:000489 0A0010            BCLR    #ST_SA,X:<STATUS                  ; Subarray bit cleared until SSP is executed
1644      P:00048A P:00048A 44DB00            MOVE              X:(R3)+,X0
1645      P:00048B P:00048B 4C1400            MOVE                          X0,Y:<NRBIAS ; Number of bias pixels to read
1646      P:00048C P:00048C 44DB00            MOVE              X:(R3)+,X0
1647      P:00048D P:00048D 4C1500            MOVE                          X0,Y:<NSREAD ; Number of columns in subimage read
1648      P:00048E P:00048E 44DB00            MOVE              X:(R3)+,X0
1649      P:00048F P:00048F 4C1600            MOVE                          X0,Y:<NPREAD ; Number of rows in subimage read
1650      P:000490 P:000490 0C008F            JMP     <FINISH
1651   
1652                                SET_SUBARRAY_POSITIONS
1653      P:000491 P:000491 67F400            MOVE              #READ_TABLE,R7
                            000017
1654      P:000493 P:000493 44DB00            MOVE              X:(R3)+,X0
1655      P:000494 P:000494 000000            NOP
1656      P:000495 P:000495 4C5F00            MOVE                          X0,Y:(R7)+  ; Number of rows (parallels) to clear
1657      P:000496 P:000496 44DB00            MOVE              X:(R3)+,X0
1658      P:000497 P:000497 4C5F00            MOVE                          X0,Y:(R7)+  ; Number of columns (serials) clears before
1659      P:000498 P:000498 44DB00            MOVE              X:(R3)+,X0              ;  the box readout
1660      P:000499 P:000499 4C5F00            MOVE                          X0,Y:(R7)+  ; Number of columns (serials) clears after
1661      P:00049A P:00049A 0A0030            BSET    #ST_SA,X:<STATUS                  ; Subarray bit set
1662      P:00049B P:00049B 0C008F            JMP     <FINISH
1663   
1664                                ; Select the amplifier and readout mode
1665                                ;   'SOS'  Amplifier_name = '__C', '__D', '__B', '__A' or 'ALL'
1666   
1667                                SELECT_OUTPUT_SOURCE
1668      P:00049C P:00049C 46DB00            MOVE              X:(R3)+,Y0
1669      P:00049D P:00049D 4E0B00            MOVE                          Y0,Y:<OS
1670      P:00049E P:00049E 0D04A0            JSR     <SEL_OS
1671      P:00049F P:00049F 0C008F            JMP     <FINISH
1672   
1673      P:0004A0 P:0004A0 4C8B00  SEL_OS    MOVE                          Y:<OS,X0    ; Get amplifier(s) name
1674      P:0004A1 P:0004A1 56F400            MOVE              #'ALL',A                ; All Amplifiers = readout #0 to #3
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 31



                            414C4C
1675      P:0004A3 P:0004A3 200045            CMP     X0,A
1676      P:0004A4 P:0004A4 0E24B3            JNE     <CMP_LL
1677      P:0004A5 P:0004A5 44F400            MOVE              #PARALLEL_SPLIT,X0
                            00002A
1678      P:0004A7 P:0004A7 4C7000            MOVE                          X0,Y:PARALLEL
                            00000E
1679      P:0004A9 P:0004A9 44F400            MOVE              #SERIAL_SKIP_SPLIT,X0
                            000062
1680      P:0004AB P:0004AB 4C7000            MOVE                          X0,Y:SERIAL_SKIP
                            000010
1681      P:0004AD P:0004AD 44F400            MOVE              #SERIAL_READ_SPLIT,X0
                            00008F
1682      P:0004AF P:0004AF 4C0F00            MOVE                          X0,Y:<SERIAL_READ
1683      P:0004B0 P:0004B0 0A0025            BSET    #SPLIT_S,X:STATUS
1684      P:0004B1 P:0004B1 0A0026            BSET    #SPLIT_P,X:STATUS
1685      P:0004B2 P:0004B2 00000C            RTS
1686   
1687      P:0004B3 P:0004B3 56F400  CMP_LL    MOVE              #'__L',A                ; Lower Left Amplifier = readout #0
                            5F5F4C
1688      P:0004B5 P:0004B5 200045            CMP     X0,A
1689      P:0004B6 P:0004B6 0E24C9            JNE     <CMP_LR
1690      P:0004B7 P:0004B7 44F400            MOVE              #PARALLEL_DOWN,X0
                            000022
1691      P:0004B9 P:0004B9 4C7000            MOVE                          X0,Y:PARALLEL
                            00000E
1692      P:0004BB P:0004BB 44F400            MOVE              #SERIAL_SKIP_LEFT,X0
                            000050
1693      P:0004BD P:0004BD 4C7000            MOVE                          X0,Y:SERIAL_SKIP
                            000010
1694      P:0004BF P:0004BF 44F400            MOVE              #SERIAL_READ_LEFT,X0
                            00006B
1695      P:0004C1 P:0004C1 4C0F00            MOVE                          X0,Y:<SERIAL_READ
1696      P:0004C2 P:0004C2 44F400            MOVE              #$00F000,X0
                            00F000
1697      P:0004C4 P:0004C4 4C7000            MOVE                          X0,Y:SXL
                            000074
1698      P:0004C6 P:0004C6 0A0005            BCLR    #SPLIT_S,X:STATUS
1699      P:0004C7 P:0004C7 0A0006            BCLR    #SPLIT_P,X:STATUS
1700      P:0004C8 P:0004C8 00000C            RTS
1701   
1702      P:0004C9 P:0004C9 56F400  CMP_LR    MOVE              #'_2L',A                ; Lower Right Amplifier = readout #1
                            5F324C
1703      P:0004CB P:0004CB 200045            CMP     X0,A
1704      P:0004CC P:0004CC 0E24DF            JNE     <CMP_UR
1705      P:0004CD P:0004CD 44F400            MOVE              #PARALLEL_DOWN,X0
                            000022
1706      P:0004CF P:0004CF 4C7000            MOVE                          X0,Y:PARALLEL
                            00000E
1707      P:0004D1 P:0004D1 44F400            MOVE              #SERIAL_SKIP_RIGHT,X0
                            000059
1708      P:0004D3 P:0004D3 4C7000            MOVE                          X0,Y:SERIAL_SKIP
                            000010
1709      P:0004D5 P:0004D5 44F400            MOVE              #SERIAL_READ_RIGHT,X0
                            00007D
1710      P:0004D7 P:0004D7 4C0F00            MOVE                          X0,Y:<SERIAL_READ
1711      P:0004D8 P:0004D8 44F400            MOVE              #$00F041,X0
                            00F041
1712      P:0004DA P:0004DA 4C7000            MOVE                          X0,Y:SXR
                            000086
1713      P:0004DC P:0004DC 0A0005            BCLR    #SPLIT_S,X:STATUS
1714      P:0004DD P:0004DD 0A0006            BCLR    #SPLIT_P,X:STATUS
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 32



1715      P:0004DE P:0004DE 00000C            RTS
1716   
1717      P:0004DF P:0004DF 56F400  CMP_UR    MOVE              #'_2R',A                ; Upper Right Amplifier = readout #2
                            5F3252
1718      P:0004E1 P:0004E1 200045            CMP     X0,A
1719      P:0004E2 P:0004E2 0E24F5            JNE     <CMP_UL
1720      P:0004E3 P:0004E3 44F400            MOVE              #PARALLEL_UP,X0
                            00001A
1721      P:0004E5 P:0004E5 4C7000            MOVE                          X0,Y:PARALLEL
                            00000E
1722      P:0004E7 P:0004E7 44F400            MOVE              #SERIAL_SKIP_RIGHT,X0
                            000059
1723      P:0004E9 P:0004E9 4C7000            MOVE                          X0,Y:SERIAL_SKIP
                            000010
1724      P:0004EB P:0004EB 44F400            MOVE              #SERIAL_READ_RIGHT,X0
                            00007D
1725      P:0004ED P:0004ED 4C0F00            MOVE                          X0,Y:<SERIAL_READ
1726      P:0004EE P:0004EE 44F400            MOVE              #$00F082,X0
                            00F082
1727      P:0004F0 P:0004F0 4C7000            MOVE                          X0,Y:SXR
                            000086
1728      P:0004F2 P:0004F2 0A0005            BCLR    #SPLIT_S,X:STATUS
1729      P:0004F3 P:0004F3 0A0006            BCLR    #SPLIT_P,X:STATUS
1730      P:0004F4 P:0004F4 00000C            RTS
1731   
1732      P:0004F5 P:0004F5 56F400  CMP_UL    MOVE              #'__R',A                ; Upper Left Amplifier = readout #3
                            5F5F52
1733      P:0004F7 P:0004F7 200045            CMP     X0,A
1734      P:0004F8 P:0004F8 0E208D            JNE     <ERROR
1735      P:0004F9 P:0004F9 44F400            MOVE              #PARALLEL_UP,X0
                            00001A
1736      P:0004FB P:0004FB 4C7000            MOVE                          X0,Y:PARALLEL
                            00000E
1737      P:0004FD P:0004FD 44F400            MOVE              #SERIAL_SKIP_LEFT,X0
                            000050
1738      P:0004FF P:0004FF 4C7000            MOVE                          X0,Y:SERIAL_SKIP
                            000010
1739      P:000501 P:000501 44F400            MOVE              #SERIAL_READ_LEFT,X0
                            00006B
1740      P:000503 P:000503 4C0F00            MOVE                          X0,Y:<SERIAL_READ
1741      P:000504 P:000504 44F400            MOVE              #$00F0C3,X0
                            00F0C3
1742      P:000506 P:000506 4C7000            MOVE                          X0,Y:SXL
                            000074
1743      P:000508 P:000508 0A0005            BCLR    #SPLIT_S,X:STATUS
1744      P:000509 P:000509 0A0006            BCLR    #SPLIT_P,X:STATUS
1745      P:00050A P:00050A 00000C            RTS
1746   
1747                                SUBSTRATE_BIAS_ON
1748      P:00050B P:00050B 012F23            BSET    #3,X:PCRD                         ; Turn on the serial clock
1749      P:00050C P:00050C 0D03D8            JSR     <PAL_DLY
1750      P:00050D P:00050D 60F400            MOVE              #BIAS_ON,R0
                            00012E
1751      P:00050F P:00050F 000000            NOP
1752      P:000510 P:000510 000000            NOP
1753      P:000511 P:000511 5ED800            MOVE                          Y:(R0)+,A   ; Read the table entry
1754      P:000512 P:000512 0D020C            JSR     <XMIT_A_WORD                      ; Transmit it to TIM-A-STD
1755      P:000513 P:000513 0D03D8            JSR     <PAL_DLY
1756      P:000514 P:000514 5EE000            MOVE                          Y:(R0),A    ; Read the table entry
1757      P:000515 P:000515 0D020C            JSR     <XMIT_A_WORD                      ; Transmit it to TIM-A-STD
1758      P:000516 P:000516 0D03D8            JSR     <PAL_DLY
1759      P:000517 P:000517 012F03            BCLR    #3,X:PCRD                         ; Turn off the serial clock
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 33



1760      P:000518 P:000518 0C008F            JMP     <FINISH
1761   
1762                                SUBSTRATE_BIAS_OFF
1763      P:000519 P:000519 012F23            BSET    #3,X:PCRD                         ; Turn on the serial clock
1764      P:00051A P:00051A 0D03D8            JSR     <PAL_DLY
1765      P:00051B P:00051B 60F400            MOVE              #BIAS_OFF,R0
                            00012F
1766      P:00051D P:00051D 000000            NOP
1767      P:00051E P:00051E 000000            NOP
1768      P:00051F P:00051F 5ED800            MOVE                          Y:(R0)+,A   ; Read the table entry
1769      P:000520 P:000520 0D020C            JSR     <XMIT_A_WORD                      ; Transmit it to TIM-A-STD
1770      P:000521 P:000521 0D03D8            JSR     <PAL_DLY
1771      P:000522 P:000522 5EE000            MOVE                          Y:(R0),A    ; Read the table entry
1772      P:000523 P:000523 0D020C            JSR     <XMIT_A_WORD                      ; Transmit it to TIM-A-STD
1773      P:000524 P:000524 0D03D8            JSR     <PAL_DLY
1774      P:000525 P:000525 012F03            BCLR    #3,X:PCRD                         ; Turn off the serial clock
1775      P:000526 P:000526 0C008F            JMP     <FINISH
1776   
1777                                ; **********************************************************************************************
1778                                ; Set the video offset for the ARC-48 8-channel CCD video board
1779                                ; SVO  Board  DAC  voltage Board number is from 0 to 15
1780                                ; DAC number from 0 to 7
1781                                ; voltage number is from 0 to 16,383 (14 bits)
1782   
1783                                SET_VIDEO_OFFSET
1784      P:000527 P:000527 012F23            BSET    #3,X:PCRD                         ; Turn on the serial clock
1785      P:000528 P:000528 56DB00            MOVE              X:(R3)+,A               ; First argument is board number, 0 to 15
1786      P:000529 P:000529 200003            TST     A
1787      P:00052A P:00052A 0E9572            JLT     <ERR_SV1
1788      P:00052B P:00052B 014F85            CMP     #15,A
1789      P:00052C P:00052C 0E7572            JGT     <ERR_SV1
1790      P:00052D P:00052D 0C1EA8            LSL     #20,A
1791      P:00052E P:00052E 000000            NOP
1792      P:00052F P:00052F 21C500            MOVE              A,X1                    ; Board number is in X1 bits #23-20
1793      P:000530 P:000530 56DB00            MOVE              X:(R3)+,A               ; Second argument is the video channel numbe
r
1794      P:000531 P:000531 014085            CMP     #0,A
1795      P:000532 P:000532 0E2537            JNE     <CMP1
1796      P:000533 P:000533 56F400            MOVE              #$0E0018,A              ; Magic number for channel #0
                            0E0018
1797      P:000535 P:000535 200062            OR      X1,A
1798      P:000536 P:000536 0C0560            JMP     <SVO_XMT
1799      P:000537 P:000537 014185  CMP1      CMP     #1,A
1800      P:000538 P:000538 0E253D            JNE     <CMP2
1801      P:000539 P:000539 56F400            MOVE              #$0E0019,A              ; Magic number for channel #1
                            0E0019
1802      P:00053B P:00053B 200062            OR      X1,A
1803      P:00053C P:00053C 0C0560            JMP     <SVO_XMT
1804      P:00053D P:00053D 014285  CMP2      CMP     #2,A
1805      P:00053E P:00053E 0E2543            JNE     <CMP3
1806      P:00053F P:00053F 56F400            MOVE              #$0E0028,A              ; Magic number for channel #2
                            0E0028
1807      P:000541 P:000541 200062            OR      X1,A
1808      P:000542 P:000542 0C0560            JMP     <SVO_XMT
1809      P:000543 P:000543 014385  CMP3      CMP     #3,A
1810      P:000544 P:000544 0E2549            JNE     <CMP4
1811      P:000545 P:000545 56F400            MOVE              #$0E0029,A              ; Magic number for channel #3
                            0E0029
1812      P:000547 P:000547 200062            OR      X1,A
1813      P:000548 P:000548 0C0560            JMP     <SVO_XMT
1814      P:000549 P:000549 014485  CMP4      CMP     #4,A
1815      P:00054A P:00054A 0E254F            JNE     <CMP5
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  timCCDmisc.asm  Page 34



1816      P:00054B P:00054B 56F400            MOVE              #$0E0048,A              ; Magic number for channel #4
                            0E0048
1817      P:00054D P:00054D 200062            OR      X1,A
1818      P:00054E P:00054E 0C0560            JMP     <SVO_XMT
1819      P:00054F P:00054F 014585  CMP5      CMP     #5,A
1820      P:000550 P:000550 0E2555            JNE     <CMP6
1821      P:000551 P:000551 56F400            MOVE              #$0E0049,A              ; Magic number for channel #5
                            0E0049
1822      P:000553 P:000553 200062            OR      X1,A
1823      P:000554 P:000554 0C0560            JMP     <SVO_XMT
1824      P:000555 P:000555 014685  CMP6      CMP     #6,A
1825      P:000556 P:000556 0E255B            JNE     <CMP7
1826      P:000557 P:000557 56F400            MOVE              #$0E0088,A              ; Magic number for channel #6
                            0E0088
1827      P:000559 P:000559 200062            OR      X1,A
1828      P:00055A P:00055A 0C0560            JMP     <SVO_XMT
1829      P:00055B P:00055B 014785  CMP7      CMP     #7,A
1830      P:00055C P:00055C 0E2576            JNE     <ERR_SV2
1831      P:00055D P:00055D 56F400            MOVE              #$0E0089,A              ; Magic number for channel #7
                            0E0089
1832      P:00055F P:00055F 200062            OR      X1,A
1833   
1834      P:000560 P:000560 000000  SVO_XMT   NOP
1835      P:000561 P:000561 5C0000            MOVE                          A1,Y:0
1836   
1837      P:000562 P:000562 0D020C            JSR     <XMIT_A_WORD                      ; Transmit A to TIM-A-STD
1838      P:000563 P:000563 0D03D8            JSR     <PAL_DLY                          ; Wait for the number to be sent
1839      P:000564 P:000564 56DB00            MOVE              X:(R3)+,A               ; Third argument is the DAC voltage number
1840      P:000565 P:000565 200003            TST     A
1841      P:000566 P:000566 0E9579            JLT     <ERR_SV3                          ; Voltage number needs to be positive
1842      P:000567 P:000567 0140C5            CMP     #$3FFF,A                          ; Voltage number needs to be 14 bits
                            003FFF
1843      P:000569 P:000569 0E7579            JGT     <ERR_SV3
1844      P:00056A P:00056A 200062            OR      X1,A                              ; Add in the board number
1845      P:00056B P:00056B 0140C2            OR      #$0FC000,A
                            0FC000
1846      P:00056D P:00056D 000000            NOP
1847      P:00056E P:00056E 0D020C            JSR     <XMIT_A_WORD                      ; Transmit A to TIM-A-STD
1848      P:00056F P:00056F 0D03D8            JSR     <PAL_DLY
1849      P:000570 P:000570 012F03            BCLR    #3,X:PCRD                         ; Turn off the serial clock
1850      P:000571 P:000571 0C008F            JMP     <FINISH
1851      P:000572 P:000572 012F03  ERR_SV1   BCLR    #3,X:PCRD                         ; Turn off the serial clock
1852      P:000573 P:000573 56DB00            MOVE              X:(R3)+,A
1853      P:000574 P:000574 56DB00            MOVE              X:(R3)+,A
1854      P:000575 P:000575 0C008D            JMP     <ERROR
1855      P:000576 P:000576 012F03  ERR_SV2   BCLR    #3,X:PCRD                         ; Turn off the serial clock
1856      P:000577 P:000577 56DB00            MOVE              X:(R3)+,A
1857      P:000578 P:000578 0C008D            JMP     <ERROR
1858      P:000579 P:000579 012F03  ERR_SV3   BCLR    #3,X:PCRD                         ; Turn off the serial clock
1859      P:00057A P:00057A 0C008D            JMP     <ERROR
1860   
1861   
1862   
1863   
1864   
1865                                 TIMBOOT_X_MEMORY
1866      00057B                              EQU     @LCV(L)
1867   
1868                                ;  ****************  Setup memory tables in X: space ********************
1869   
1870                                ; Define the address in P: space where the table of constants begins
1871   
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  /home/bearing/Documents/OWL/tim/tim.asm  Page 35



1872                                          IF      @SCP("HOST","HOST")
1873      X:000036 X:000036                   ORG     X:END_COMMAND_TABLE,X:END_COMMAND_TABLE
1874                                          ENDIF
1875   
1876                                          IF      @SCP("HOST","ROM")
1878                                          ENDIF
1879   
1880                                ; Application commands
1881      X:000036 X:000036                   DC      'PON',POWER_ON
1882      X:000038 X:000038                   DC      'POF',POWER_OFF
1883      X:00003A X:00003A                   DC      'SBV',SET_BIAS_VOLTAGES
1884      X:00003C X:00003C                   DC      'IDL',START_IDLE_CLOCKING
1885      X:00003E X:00003E                   DC      'RDC',STR_RDC
1886      X:000040 X:000040                   DC      'CLR',CLEAR
1887   
1888                                ; Exposure and readout control routines
1889      X:000042 X:000042                   DC      'SET',SET_EXPOSURE_TIME
1890      X:000044 X:000044                   DC      'RET',READ_EXPOSURE_TIME
1891      X:000046 X:000046                   DC      'SEX',START_EXPOSURE
1892      X:000048 X:000048                   DC      'PEX',PAUSE_EXPOSURE
1893      X:00004A X:00004A                   DC      'REX',RESUME_EXPOSURE
1894      X:00004C X:00004C                   DC      'AEX',ABORT_EXPOSURE
1895      X:00004E X:00004E                   DC      'ABR',ABR_RDC
1896      X:000050 X:000050                   DC      'CRD',CONTINUE_READ
1897      X:000052 X:000052                   DC      'OSH',OPEN_SHUTTER
1898      X:000054 X:000054                   DC      'CSH',CLOSE_SHUTTER
1899      X:000056 X:000056                   DC      'SVO',SET_VIDEO_OFFSET
1900   
1901                                ; Support routines
1902                                ;       DC      'SGN',SET_GAIN          ; Need a routine for ARC-48
1903      X:000058 X:000058                   DC      'SBN',SET_BIAS_NUMBER
1904      X:00005A X:00005A                   DC      'SMX',SET_MUX
1905      X:00005C X:00005C                   DC      'CSW',CLR_SWS
1906      X:00005E X:00005E                   DC      'SOS',SELECT_OUTPUT_SOURCE
1907      X:000060 X:000060                   DC      'SSS',SET_SUBARRAY_SIZES
1908      X:000062 X:000062                   DC      'SSP',SET_SUBARRAY_POSITIONS
1909      X:000064 X:000064                   DC      'RCC',READ_CONTROLLER_CONFIGURATION
1910   
1911                                ; LBNL high voltage bias board commands
1912      X:000066 X:000066                   DC      'BON',SUBSTRATE_BIAS_ON
1913      X:000068 X:000068                   DC      'BOF',SUBSTRATE_BIAS_OFF
1914   
1915                                 END_APPLICATON_COMMAND_TABLE
1916      00006A                              EQU     @LCV(L)
1917   
1918                                          IF      @SCP("HOST","HOST")
1919      000021                    NUM_COM   EQU     (@LCV(R)-COM_TBL_R)/2             ; Number of boot +
1920                                                                                    ;  application commands
1921      000348                    EXPOSING  EQU     CHK_TIM                           ; Address if exposing
1922                                 CONTINUE_READING
1923      000245                              EQU     RDCCD                             ; Address if reading out
1924                                          ENDIF
1925   
1926                                          IF      @SCP("HOST","ROM")
1928                                          ENDIF
1929   
1930                                ; Now let's go for the timing waveform tables
1931                                          IF      @SCP("HOST","HOST")
1932      Y:000000 Y:000000                   ORG     Y:0,Y:0
1933                                          ENDIF
1934   
1935      Y:000000 Y:000000         GAIN      DC      END_APPLICATON_Y_MEMORY-@LCV(L)-1
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  /home/bearing/Documents/OWL/tim/tim.asm  Page 36



1936   
1937      Y:000001 Y:000001         NSR       DC      2200                              ; Number Serial Read, set by host computer
1938      Y:000002 Y:000002         NPR       DC      2200                              ; Number Parallel Read, set by host computer
1939      Y:000003 Y:000003         NP_CLR    DC      NPCLR                             ; To clear the parallel register
1940      Y:000004 Y:000004         TST_DATA  DC      0                                 ; For synthetic image
1941      Y:000005 Y:000005         NSBIN     DC      1                                 ; Serial binning parameter
1942      Y:000006 Y:000006         NPBIN     DC      1                                 ; Parallel binning parameter
1943      Y:000007 Y:000007         CONFIG    DC      CC                                ; Controller configuration
1944      Y:000008 Y:000008         NS_READ   DC      0                                 ; Number of serials to read
1945      Y:000009 Y:000009         NP_READ   DC      0                                 ; Number of parallels to read
1946      Y:00000A Y:00000A         NR_BIAS   DC      0                                 ; Number of bias pixels to read
1947      Y:00000B Y:00000B         OS        DC      'ALL'                             ; Name of the output source(s)
1948      Y:00000C Y:00000C         SYN_DAT   DC      0                                 ; Synthetic image mode pixel count
1949      Y:00000D Y:00000D         SHDEL     DC      SH_DEL                            ; Delay from shutter close to start of reado
ut
1950   
1951                                ; Waveform table addresses
1952      Y:00000E Y:00000E         PARALLEL  DC      PARALLEL_SPLIT
1953                                 SERIAL_READ
1954      Y:00000F Y:00000F                   DC      SERIAL_READ_SPLIT
1955                                 SERIAL_SKIP
1956      Y:000010 Y:000010                   DC      SERIAL_SKIP_SPLIT
1957   
1958                                ; These three parameters are read from the READ_TABLE when needed by the
1959                                ;   RDCCD routine as it loops through the required number of boxes
1960      Y:000011 Y:000011         NP_SKIP   DC      0                                 ; Number of rows to skip
1961      Y:000012 Y:000012         NS_SKP1   DC      0                                 ; Number of serials to clear before read
1962      Y:000013 Y:000013         NS_SKP2   DC      0                                 ; Number of serials to clear after read
1963   
1964                                ; Subimage readout parameters. One subimage box only
1965      Y:000014 Y:000014         NRBIAS    DC      0                                 ; Number of bias pixels to read
1966      Y:000015 Y:000015         NSREAD    DC      0                                 ; Number of columns in subimage read
1967      Y:000016 Y:000016         NPREAD    DC      0                                 ; Number of rows in subimage read
1968      Y:000017 Y:000017         READ_TABLE DC     0,0,0                             ; #1 = Number of rows to clear
1969                                                                                    ; #2 = Number of columns to skip before
1970                                                                                    ;   subimage read
1971                                                                                    ; #3 = Number of rows to clear after
1972                                                                                    ;   subimage clear
1973   
1974                                ; Include the waveform table for the designated type of CCD
1975                                          INCLUDE "LBNL_3.5k.waveforms"             ; Readout and clocking waveform file
1976                                ; Waveform tables and definitions for the LBNL 3.5k x 3.5k 4-readout CCD,
1977                                ;   written for the ARC-22, ARC-32, ARC-48 and LBNL bias boards, Rev. 7.
1978                                ;
1979                                ; CCD clock voltage definitions
1980      000000                    VIDEO     EQU     $000000                           ; Video processor board select = 0 (SAME)
1981      000000                    VID0      EQU     $000000                           ; Address of the DACs on the ARC-48 video bo
ard (DIFF)
1982                                ;BIAS   EQU     $002000 ; Bias Generator board select = 3 (LBNL)
1983                                ;HVBIAS EQU     $003000 ; Bias Generator board select = 3 (LBNL)
1984      002000                    CLK2      EQU     $002000                           ; Clock driver board select = 2  (SAME)
1985      003000                    CLK3      EQU     $003000                           ; Clock driver board select = 3  (SAME)
1986      200000                    CLKV      EQU     $200000                           ; Clock driver board DAC voltage selection a
ddress  (NEW)
1987      000708                    NSCLR     EQU     1800                              ; Horizontal clocks to clear (NEW)
1988      000708                    NPCLR     EQU     1800                              ; Parallel clocks to clear (SAME)
1989      000032                    SH_DEL    EQU     50                                ; Shutter delay (SAME)
1990                                ;I_DELAY        EQU     $8A0000 ; Integration period (OLD)
1991                                ;I_DELAY                EQU     $990000 ; Integration period 25*160+80 = 4080 ns (LBNL)
1992      8C0000                    I_DELAY   EQU     $8C0000                           ; Integration period 12*320 + 80 = 3920 ns (
ARC22)
1993   
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 37



1994                                ; Delay number for parallel clocking
1995      BC0000                    P_DELAY   EQU     $BC0000                           ; Parallel Clock Delay 60*320 + 80 = 19280 n
s (ARC22)
1996                                ;R_DELAY                EQU     $600000 ; Serial register transfer delay (LBNL)
1997   
1998                                ; Clocking wavefors (Using 98 KHz clocking wavefors)
1999                                ;S_DELAY        EQU     $0B0000 ; Serial register skipping delay (SAME) 300ns
2000      050000                    S_DELAY   EQU     $050000                           ; Serial register skipping delay 5*40 + 80 =
 280ns (ARC22)
2001                                ;SW_DELAY       EQU     $150000 ; Sum_well  clock delay = 96*20+80 = 2000 ns (SAME)
2002      0A0000                    SW_DELAY  EQU     $0A0000                           ; Sum_well  clock delay = 10*40+80 = 480 ns 
(ARC22)
2003                                ;PRE_SET_DLY    EQU     $040000 ; settling time  clock delay = 64*20+80 = 1360 ns (SAME)
2004                                ;POST_SET_DLY   EQU     $040000 ; settling time  clock delay = 64*20+80 = 1360 ns (SAME)
2005                                ;DCRST_DELAY    EQU     $040000 ; settling time  clock delay = 64*20+80 = 1360 ns (SAME)
2006                                 PRE_SET_DLY
2007      020000                              EQU     $020000                           ; settling time  clock delay = 64*20+80 = 16
0 ns (ARC22)
2008                                 POST_SET_DLY
2009      020000                              EQU     $020000                           ; settling time  clock delay = 64*20+80 = 16
0 ns (ARC22)
2010                                 DCRST_DELAY
2011      020000                              EQU     $020000                           ; settling time  clock delay = 64*20+80 = 16
0 ns (ARC22)
2012   
2013                                ;PS_DELAY EQU   $2C0000 ; 2x SERIAL  clock delay = 2 microsec (LBNL ADD)
2014   
2015                                ; ARC-48 video processor board definitions for writing to its DACs
2016      0E0000                    DAC_ADDR  EQU     $0E0000                           ; DAC Channel Address (NEW)
2017      0F4000                    DAC_RegM  EQU     $0F4000                           ; DAC m Register (NEW)
2018      0F8000                    DAC_RegC  EQU     $0F8000                           ; DAC c Register (NEW)
2019      0FC000                    DAC_RegD  EQU     $0FC000                           ; DAC X1 Register (NEW)
2020   
2021                                ; Macros to help getting from volts to bits.
2022   
2023                                VDEF      MACRO   NAME,BRDTYP,BRDNUM,DAC,ALO,AHI
2024 m 
2025 m                              LO_\NAME  EQU     ALO
2026 m                              HI_\NAME  EQU     AHI
2027 m                              DAC_\NAME EQU     DAC
2028 m                               BRDNUM_\NAME
2029 m                                        EQU     BRDNUM
2030 m                                        IF      @SCP("BRDTYP",'VID')
2031 m                               BRDTYP_\NAME
2032 m                                        EQU     3
2033 m                                        ELSE
2034 m                               BRDTYP_\NAME
2035 m                                        EQU     0
2036 m                                        ENDIF
2037 m 
2038 m                                        MSG     'Defining voltage ',"NAME",' type ',"BRDTYP",' board ',"BRDNUM",' dac ',"DAC",
' with limits ',"ALO",' ',"AHI"
2039 m                                        ENDM
2040   
2041                                VOLTS     MACRO   NAME,F
2042 m 
2043 m                              DUMMY     SET     @CVI(@MIN(4095,@MAX(0,(F-LO_\NAME)/(HI_\NAME-LO_\NAME)*4096.)))
2044 m                              DUMMY2    SET     @CVI((BRDNUM_\NAME<<20)|(BRDTYP_\NAME<<18)|(DAC_\NAME<<14)|DUMMY)
2045 m                                        DC      DUMMY2
2046 m                                        MSG     'Setting voltage ',"NAME ","F",'V ',DUMMY
2047 m                                        ENDM
2048   
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 38



2049                                ;*********************************************************************
2050                                                                                    ; define video board voltage symbols
2051                                          VDEF    OFFA0,VID,0,1,-10.0,10.0
**** 2063 [LBNL_3.5k.waveforms 70]: Defining voltage OFFA0 type VID board 0 dac 1 with limits -10.0 10.0
2064                                          VDEF    OFFA1,VID,1,1,-10.0,10.0
**** 2076 [LBNL_3.5k.waveforms 71]: Defining voltage OFFA1 type VID board 1 dac 1 with limits -10.0 10.0
2077                                          VDEF    OFFB0,VID,0,3,-10.0,10.0
**** 2089 [LBNL_3.5k.waveforms 72]: Defining voltage OFFB0 type VID board 0 dac 3 with limits -10.0 10.0
2090                                          VDEF    OFFB1,VID,1,3,-10.0,10.0
**** 2102 [LBNL_3.5k.waveforms 73]: Defining voltage OFFB1 type VID board 1 dac 3 with limits -10.0 10.0
2103   
2104                                                                                    ; define bias board voltage symbols
2105                                          VDEF    VDD1,VID,2,0,0.0,-25.0
**** 2117 [LBNL_3.5k.waveforms 76]: Defining voltage VDD1 type VID board 2 dac 0 with limits 0.0 -25.0
2118                                          VDEF    VDD2,VID,2,1,0.0,-25.0
**** 2130 [LBNL_3.5k.waveforms 77]: Defining voltage VDD2 type VID board 2 dac 1 with limits 0.0 -25.0
2131                                          VDEF    VDD3,VID,2,2,0.0,-25.0
**** 2143 [LBNL_3.5k.waveforms 78]: Defining voltage VDD3 type VID board 2 dac 2 with limits 0.0 -25.0
2144                                          VDEF    VDD4,VID,2,3,0.0,-25.0
**** 2156 [LBNL_3.5k.waveforms 79]: Defining voltage VDD4 type VID board 2 dac 3 with limits 0.0 -25.0
2157                                          VDEF    VR1,VID,2,4,0.0,-25.0
**** 2169 [LBNL_3.5k.waveforms 80]: Defining voltage VR1 type VID board 2 dac 4 with limits 0.0 -25.0
2170                                          VDEF    VR2,VID,2,5,0.0,-25.0
**** 2182 [LBNL_3.5k.waveforms 81]: Defining voltage VR2 type VID board 2 dac 5 with limits 0.0 -25.0
2183                                          VDEF    VR3,VID,2,6,0.0,-25.0
**** 2195 [LBNL_3.5k.waveforms 82]: Defining voltage VR3 type VID board 2 dac 6 with limits 0.0 -25.0
2196                                          VDEF    VR4,VID,2,7,0.0,-25.0
**** 2208 [LBNL_3.5k.waveforms 83]: Defining voltage VR4 type VID board 2 dac 7 with limits 0.0 -25.0
2209                                          VDEF    VOG1,VID,2,8,0.0,5
**** 2221 [LBNL_3.5k.waveforms 84]: Defining voltage VOG1 type VID board 2 dac 8 with limits 0.0 5
2222                                          VDEF    VOG2,VID,2,9,0.0,5
**** 2234 [LBNL_3.5k.waveforms 85]: Defining voltage VOG2 type VID board 2 dac 9 with limits 0.0 5
2235                                          VDEF    VOG3,VID,2,10,0.0,5
**** 2247 [LBNL_3.5k.waveforms 86]: Defining voltage VOG3 type VID board 2 dac 10 with limits 0.0 5
2248                                          VDEF    VOG4,VID,2,11,0.0,5
**** 2260 [LBNL_3.5k.waveforms 87]: Defining voltage VOG4 type VID board 2 dac 11 with limits 0.0 5
2261                                          VDEF    VSUB,VID,2,12,0.0,200.8
**** 2273 [LBNL_3.5k.waveforms 88]: Defining voltage VSUB type VID board 2 dac 12 with limits 0.0 200.8
2274                                          VDEF    RAMP,VID,2,13,0.0,10.0            ;  for ramping p.s.
**** 2286 [LBNL_3.5k.waveforms 89]: Defining voltage RAMP type VID board 2 dac 13 with limits 0.0 10.0
2287   
2288                                                                                    ; define clock board (ARC22 is +/-13V) symbo
ls bank0
2289   
2290                                          VDEF    V1_HI,CLK,2,0,-13.0,+13.0         ; Vertical High
**** 2302 [LBNL_3.5k.waveforms 93]: Defining voltage V1_HI type CLK board 2 dac 0 with limits -13.0 +13.0
2303                                          VDEF    V1_LO,CLK,2,1,-13.0,+13.0         ; Vertical Low
**** 2315 [LBNL_3.5k.waveforms 94]: Defining voltage V1_LO type CLK board 2 dac 1 with limits -13.0 +13.0
2316                                          VDEF    V2_HI,CLK,2,2,-13.0,+13.0         ; Vertical High
**** 2328 [LBNL_3.5k.waveforms 95]: Defining voltage V2_HI type CLK board 2 dac 2 with limits -13.0 +13.0
2329                                          VDEF    V2_LO,CLK,2,3,-13.0,+13.0         ; Vertical Low
**** 2341 [LBNL_3.5k.waveforms 96]: Defining voltage V2_LO type CLK board 2 dac 3 with limits -13.0 +13.0
2342                                          VDEF    V3_HI,CLK,2,4,-13.0,+13.0         ; Vertical High
**** 2354 [LBNL_3.5k.waveforms 97]: Defining voltage V3_HI type CLK board 2 dac 4 with limits -13.0 +13.0
2355                                          VDEF    V3_LO,CLK,2,5,-13.0,+13.0         ; Vertical Low
**** 2367 [LBNL_3.5k.waveforms 98]: Defining voltage V3_LO type CLK board 2 dac 5 with limits -13.0 +13.0
2368   
2369                                          VDEF    FS1_HI,CLK,2,6,-13.0,+13.0        ; Frame Stores
**** 2381 [LBNL_3.5k.waveforms 100]: Defining voltage FS1_HI type CLK board 2 dac 6 with limits -13.0 +13.0
2382                                          VDEF    FS1_LO,CLK,2,7,-13.0,+13.0        ;
**** 2394 [LBNL_3.5k.waveforms 101]: Defining voltage FS1_LO type CLK board 2 dac 7 with limits -13.0 +13.0
2395                                          VDEF    FS2_HI,CLK,2,8,-13.0,+13.0        ;
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 39



**** 2407 [LBNL_3.5k.waveforms 102]: Defining voltage FS2_HI type CLK board 2 dac 8 with limits -13.0 +13.0
2408                                          VDEF    FS2_LO,CLK,2,9,-13.0,+13.0        ;
**** 2420 [LBNL_3.5k.waveforms 103]: Defining voltage FS2_LO type CLK board 2 dac 9 with limits -13.0 +13.0
2421                                          VDEF    FS3_HI,CLK,2,10,-13.0,+13.0       ;
**** 2433 [LBNL_3.5k.waveforms 104]: Defining voltage FS3_HI type CLK board 2 dac 10 with limits -13.0 +13.0
2434                                          VDEF    FS3_LO,CLK,2,11,-13.0,+13.0       ;
**** 2446 [LBNL_3.5k.waveforms 105]: Defining voltage FS3_LO type CLK board 2 dac 11 with limits -13.0 +13.0
2447   
2448                                          VDEF    T1_HI,CLK,2,12,-13.0,+13.0        ; Transfer gate High
**** 2460 [LBNL_3.5k.waveforms 107]: Defining voltage T1_HI type CLK board 2 dac 12 with limits -13.0 +13.0
2461                                          VDEF    T1_LO,CLK,2,13,-13.0,+13.0        ; Transfer gate Low
**** 2473 [LBNL_3.5k.waveforms 108]: Defining voltage T1_LO type CLK board 2 dac 13 with limits -13.0 +13.0
2474                                          VDEF    T2_HI,CLK,2,14,-13.0,+13.0        ; Transfer gate High
**** 2486 [LBNL_3.5k.waveforms 109]: Defining voltage T2_HI type CLK board 2 dac 14 with limits -13.0 +13.0
2487                                          VDEF    T2_LO,CLK,2,15,-13.0,+13.0        ; Transfer gate Low
**** 2499 [LBNL_3.5k.waveforms 110]: Defining voltage T2_LO type CLK board 2 dac 15 with limits -13.0 +13.0
2500   
2501                                                                                    ; define clock board symbols bank1
2502                                          VDEF    H1U_HI,CLK,2,24,-13.0,+13.0       ; Upper Horizontals
**** 2514 [LBNL_3.5k.waveforms 113]: Defining voltage H1U_HI type CLK board 2 dac 24 with limits -13.0 +13.0
2515                                          VDEF    H1U_LO,CLK,2,25,-13.0,+13.0       ;
**** 2527 [LBNL_3.5k.waveforms 114]: Defining voltage H1U_LO type CLK board 2 dac 25 with limits -13.0 +13.0
2528                                          VDEF    H2U_HI,CLK,2,26,-13.0,+13.0       ;
**** 2540 [LBNL_3.5k.waveforms 115]: Defining voltage H2U_HI type CLK board 2 dac 26 with limits -13.0 +13.0
2541                                          VDEF    H2U_LO,CLK,2,27,-13.0,+13.0       ;
**** 2553 [LBNL_3.5k.waveforms 116]: Defining voltage H2U_LO type CLK board 2 dac 27 with limits -13.0 +13.0
2554                                          VDEF    H3U_HI,CLK,2,28,-13.0,+13.0       ;
**** 2566 [LBNL_3.5k.waveforms 117]: Defining voltage H3U_HI type CLK board 2 dac 28 with limits -13.0 +13.0
2567                                          VDEF    H3U_LO,CLK,2,29,-13.0,+13.0       ;
**** 2579 [LBNL_3.5k.waveforms 118]: Defining voltage H3U_LO type CLK board 2 dac 29 with limits -13.0 +13.0
2580                                          VDEF    H1L_HI,CLK,2,30,-13.0,+13.0       ; Lower Horizontal High
**** 2592 [LBNL_3.5k.waveforms 119]: Defining voltage H1L_HI type CLK board 2 dac 30 with limits -13.0 +13.0
2593                                          VDEF    H1L_LO,CLK,2,31,-13.0,+13.0       ; Lower Horizontal Low
**** 2605 [LBNL_3.5k.waveforms 120]: Defining voltage H1L_LO type CLK board 2 dac 31 with limits -13.0 +13.0
2606                                          VDEF    H2L_HI,CLK,2,32,-13.0,+13.0       ; Lower Horizontal High
**** 2618 [LBNL_3.5k.waveforms 121]: Defining voltage H2L_HI type CLK board 2 dac 32 with limits -13.0 +13.0
2619                                          VDEF    H2L_LO,CLK,2,33,-13.0,+13.0       ; Lower Horizontal Low
**** 2631 [LBNL_3.5k.waveforms 122]: Defining voltage H2L_LO type CLK board 2 dac 33 with limits -13.0 +13.0
2632                                          VDEF    H3L_HI,CLK,2,34,-13.0,+13.0       ; Lower Horizontal High
**** 2644 [LBNL_3.5k.waveforms 123]: Defining voltage H3L_HI type CLK board 2 dac 34 with limits -13.0 +13.0
2645                                          VDEF    H3L_LO,CLK,2,35,-13.0,+13.0       ; Lower Horizontal Low
**** 2657 [LBNL_3.5k.waveforms 124]: Defining voltage H3L_LO type CLK board 2 dac 35 with limits -13.0 +13.0
2658                                          VDEF    SWU_HI,CLK,2,36,-13.0,+13.0       ; Upper Summing Well
**** 2670 [LBNL_3.5k.waveforms 125]: Defining voltage SWU_HI type CLK board 2 dac 36 with limits -13.0 +13.0
2671                                          VDEF    SWU_LO,CLK,2,37,-13.0,+13.0       ;
**** 2683 [LBNL_3.5k.waveforms 126]: Defining voltage SWU_LO type CLK board 2 dac 37 with limits -13.0 +13.0
2684                                          VDEF    SWL_HI,CLK,2,38,-13.0,+13.0       ; Output transfer gate High
**** 2696 [LBNL_3.5k.waveforms 127]: Defining voltage SWL_HI type CLK board 2 dac 38 with limits -13.0 +13.0
2697                                          VDEF    SWL_LO,CLK,2,39,-13.0,+13.0       ; Output transfer gate Low
**** 2709 [LBNL_3.5k.waveforms 128]: Defining voltage SWL_LO type CLK board 2 dac 39 with limits -13.0 +13.0
2710                                          VDEF    RU_HI,CLK,2,40,-13.0,+13.0        ; Reset High wrong polarity....
**** 2722 [LBNL_3.5k.waveforms 129]: Defining voltage RU_HI type CLK board 2 dac 40 with limits -13.0 +13.0
2723                                          VDEF    RU_LO,CLK,2,41,-13.0,+13.0        ; upper reset
**** 2735 [LBNL_3.5k.waveforms 130]: Defining voltage RU_LO type CLK board 2 dac 41 with limits -13.0 +13.0
2736                                          VDEF    RL_HI,CLK,2,42,-13.0,+13.0        ; Reset High wrong polarity....
**** 2748 [LBNL_3.5k.waveforms 131]: Defining voltage RL_HI type CLK board 2 dac 42 with limits -13.0 +13.0
2749                                          VDEF    RL_LO,CLK,2,43,-13.0,+13.0        ; lower reset
**** 2761 [LBNL_3.5k.waveforms 132]: Defining voltage RL_LO type CLK board 2 dac 43 with limits -13.0 +13.0
2762   
2763                                ; Video offsets - 0 to $3fff video offset value,Rang=0~4.3V
2764      002280                    OFFSET    EQU     $2280                             ; 2.43V @ $2400
2765   
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 40



2766      002280                    OFFSET0   EQU     OFFSET
2767      002280                    OFFSET1   EQU     OFFSET
2768      002280                    OFFSET2   EQU     OFFSET
2769      002280                    OFFSET3   EQU     OFFSET
2770      002280                    OFFSET4   EQU     OFFSET
2771      002280                    OFFSET5   EQU     OFFSET
2772      002280                    OFFSET6   EQU     OFFSET
2773      002280                    OFFSET7   EQU     OFFSET
2774   
2775                                ; Define switch state bits for the lower CCD clock driver bank CLK3
2776      000001                    RGL       EQU     1                                 ; Reset Gate Left Pin 1
2777      000002                    H1L       EQU     2                                 ; Horizontal #1 Left Pin 2
2778      000004                    H2L       EQU     4                                 ; Horizontal #2 Left Pin 3
2779      000008                    H3L       EQU     8                                 ; Horizontal #3 Left Pin 4
2780      000010                    SWL       EQU     $10                               ; Summing Well Left Pin 5
2781      000020                    RGR       EQU     $20                               ; Reset Gate Right Pin 6
2782      000040                    H1R       EQU     $40                               ; Horizontal #1 Right Pin 7
2783      000080                    H2R       EQU     $80                               ; Horizontal #2 Right Pin 8
2784      000100                    H3R       EQU     $100                              ; Horizontal #3 Right Pin 9
2785      000200                    SWR       EQU     $200                              ; Summing Well Lower Pin 10
2786      000021                    RG        EQU     RGL+RGR                           ; Left and Right are always clocked the same
2787      000210                    SW        EQU     SWL+SWR
2788      000042                    H1        EQU     H1L+H1R
2789      000084                    H2        EQU     H2L+H2R
2790      000108                    H3        EQU     H3L+H3R
2791                                ; Pins 11-12 are not used
2792   
2793                                ; Define switch state bits for the CCD clocks of the LBNL CCD
2794                                ;Bank 0
2795      000000                    V1L       EQU     0
2796      000001                    V1H       EQU     $1
2797      000000                    V2L       EQU     0                                 ; VERTICAL register, phase #2
2798      000002                    V2H       EQU     $2
2799      000000                    V3L       EQU     0                                 ; VERTICAL register, phase #3
2800      000004                    V3H       EQU     $4
2801      000000                    FS1L      EQU     0
2802      000008                    FS1H      EQU     $8
2803      000000                    FS2L      EQU     0
2804      000010                    FS2H      EQU     $10
2805      000000                    FS3L      EQU     0
2806      000020                    FS3H      EQU     $20
2807   
2808      000000                    TL        EQU     0                                 ; Transfer gate
2809      0000C0                    TH        EQU     $c0                               ; both transfer gates concurrently
2810   
2811                                ;Bank 1
2812      000000                    HU1L      EQU     0
2813      000001                    HU1H      EQU     $1
2814      000000                    HU2L      EQU     0
2815      000002                    HU2H      EQU     $2
2816      000000                    HU3L      EQU     0
2817      000004                    HU3H      EQU     $4
2818      000000                    HL1L      EQU     0                                 ; Serial shift register, phase #1
2819      000008                    HL1H      EQU     $8
2820      000000                    HL2L      EQU     0                                 ; Serial shift register, phase #2
2821      000010                    HL2H      EQU     $10
2822      000000                    HL3L      EQU     0                                 ; Serial shift register, phase #3
2823      000020                    HL3H      EQU     $20
2824      000000                    WL        EQU     0                                 ; Both summing wells clocked together
2825      0000C0                    WH        EQU     $0c0
2826      000000                    RL        EQU     0                                 ; Reset both output nodes
2827      000300                    RH        EQU     $300
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 41



2828   
2829                                ; Define switch state bits for the upper CCD clock driver bank CLK2
2830      000008                    TGL       EQU     8                                 ; Transfer Gate Left, Pin 16
2831      000010                    V1R       EQU     $10                               ; Image, phase #1 Right, Pin 17
2832      000020                    V2R       EQU     $20                               ; Image, phase #2 Right, Pin 18
2833      000040                    V3R       EQU     $40                               ; Image, phase #3 Right, Pin 19
2834      000800                    TGR       EQU     $800                              ; Transfer Gate Right, Pin 37
2835      000010                    V1        EQU     V1L+V1R
2836      000020                    V2        EQU     V2L+V2R
2837      000040                    V3        EQU     V3L+V3R
2838      000808                    TG        EQU     TGL+TGR
2839                                ;  ***  Definitions for Y: memory waveform tables  *****
2840                                PARALLEL_UP                                         ;this is the parallel split waveform since h
ardware is restrictive
2841      Y:00001A Y:00001A                   DC      END_PARALLEL_UP-PARALLEL_UP-1
2842      Y:00001B Y:00001B                   DC      CLK3+$000000+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;SW->lo
2843      Y:00001C Y:00001C                   DC      CLK2+P_DELAY+FS1H+FS2L+FS3H+V1H+V2L+V3H+TL
2844      Y:00001D Y:00001D                   DC      CLK2+P_DELAY+FS1H+FS2L+FS3L+V1H+V2L+V3L+TL
2845      Y:00001E Y:00001E                   DC      CLK2+P_DELAY+FS1H+FS2H+FS3L+V1H+V2H+V3L+TL
2846      Y:00001F Y:00001F                   DC      CLK2+P_DELAY+FS1L+FS2H+FS3L+V1L+V2H+V3L+TL
2847      Y:000020 Y:000020                   DC      CLK2+P_DELAY+FS1L+FS2H+FS3H+V1L+V2H+V3H+TL
2848      Y:000021 Y:000021                   DC      CLK2+P_DELAY+FS1L+FS2L+FS3H+V1L+V2L+V3H+TH ; shut TG
2849                                END_PARALLEL_UP
2850   
2851                                PARALLEL_DOWN                                       ;this is the parallel split waveform since h
ardware is restrictive
2852      Y:000022 Y:000022                   DC      END_PARALLEL_DOWN-PARALLEL_DOWN-1
2853      Y:000023 Y:000023                   DC      CLK3+$000000+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;SW->lo
2854      Y:000024 Y:000024                   DC      CLK2+P_DELAY+FS1H+FS2L+FS3H+V1H+V2L+V3H+TL
2855      Y:000025 Y:000025                   DC      CLK2+P_DELAY+FS1L+FS2L+FS3H+V1L+V2L+V3H+TL
2856      Y:000026 Y:000026                   DC      CLK2+P_DELAY+FS1L+FS2H+FS3H+V1L+V2H+V3H+TL
2857      Y:000027 Y:000027                   DC      CLK2+P_DELAY+FS1L+FS2H+FS3L+V1L+V2H+V3L+TL
2858      Y:000028 Y:000028                   DC      CLK2+P_DELAY+FS1H+FS2H+FS3L+V1H+V2H+V3L+TL
2859      Y:000029 Y:000029                   DC      CLK2+P_DELAY+FS1H+FS2L+FS3L+V1H+V2L+V3L+TH ; shut TG
2860                                END_PARALLEL_DOWN
2861   
2862                                PARALLEL_SPLIT
2863      Y:00002A Y:00002A                   DC      END_PARALLEL_SPLIT-PARALLEL_SPLIT-1
2864      Y:00002B Y:00002B                   DC      CLK3+$000000+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;SW->lo
2865      Y:00002C Y:00002C                   DC      CLK2+P_DELAY+FS1H+FS2L+FS3H+V1H+V2L+V3H+TL
2866      Y:00002D Y:00002D                   DC      CLK2+P_DELAY+FS1H+FS2L+FS3L+V1H+V2L+V3L+TL
2867      Y:00002E Y:00002E                   DC      CLK2+P_DELAY+FS1H+FS2H+FS3L+V1H+V2H+V3L+TL
2868      Y:00002F Y:00002F                   DC      CLK2+P_DELAY+FS1L+FS2H+FS3L+V1L+V2H+V3L+TL
2869      Y:000030 Y:000030                   DC      CLK2+P_DELAY+FS1L+FS2H+FS3H+V1L+V2H+V3H+TL
2870      Y:000031 Y:000031                   DC      CLK2+P_DELAY+FS1L+FS2L+FS3H+V1L+V2L+V3H+TH ; shut TG
2871                                END_PARALLEL_SPLIT
2872   
2873                                PARALLEL_CLEAR_SPLIT                                ;just another copy of parallel split
2874      Y:000032 Y:000032                   DC      END_PARALLEL_CLEAR_SPLIT-PARALLEL_CLEAR_SPLIT-1
2875      Y:000033 Y:000033                   DC      CLK3+$000000+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;SW->lo
2876      Y:000034 Y:000034                   DC      CLK2+P_DELAY+FS1H+FS2L+FS3H+V1H+V2L+V3H+TL
2877      Y:000035 Y:000035                   DC      CLK2+P_DELAY+FS1H+FS2L+FS3L+V1H+V2L+V3L+TL
2878      Y:000036 Y:000036                   DC      CLK2+P_DELAY+FS1H+FS2H+FS3L+V1H+V2H+V3L+TL
2879      Y:000037 Y:000037                   DC      CLK2+P_DELAY+FS1L+FS2H+FS3L+V1L+V2H+V3L+TL
2880      Y:000038 Y:000038                   DC      CLK2+P_DELAY+FS1L+FS2H+FS3H+V1L+V2H+V3H+TL
2881      Y:000039 Y:000039                   DC      CLK2+P_DELAY+FS1L+FS2L+FS3H+V1L+V2L+V3H+TH ; shut TG
2882                                END_PARALLEL_CLEAR_SPLIT
2883   
2884                                PARALLELS_DURING_EXPOSURE                           ; this is redundant since clocks are already
 in proper state
2885      Y:00003A Y:00003A                   DC      END_PARALLELS_DURING_EXPOSURE-PARALLELS_DURING_EXPOSURE-1
2886      Y:00003B Y:00003B                   DC      CLK2+P_DELAY+FS1L+FS2L+FS3H+V1L+V2L+V3H+TH ; shut TG
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 42



2887                                END_PARALLELS_DURING_EXPOSURE
2888   
2889                                PARALLELS_DURING_READOUT                            ; redundant, clocks should be O.K.
2890      Y:00003C Y:00003C                   DC      END_PARALLELS_DURING_READOUT-PARALLELS_DURING_READOUT-1
2891      Y:00003D Y:00003D                   DC      CLK2+P_DELAY+FS1H+FS2L+FS3H+V1H+V2L+V3H+TH
2892                                END_PARALLELS_DURING_READOUT
2893   
2894                                ; Video processor bit definition
2895                                ; ARC48      xfer, A/D, integ, polarity, not used, not used, rst (1 => switch open)
2896   
2897                                SERIAL_IDLE                                         ; Clock serial charge from both L and R ends
2898      Y:00003E Y:00003E                   DC      END_SERIAL_IDLE-SERIAL_IDLE-1
2899      Y:00003F Y:00003F                   DC      VIDEO+$000000+%1110100
2900      Y:000040 Y:000040                   DC      CLK3+S_DELAY+RH+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;h3->lo,SW->lo,Reset_On
2901      Y:000041 Y:000041                   DC      CLK3+S_DELAY+RH+HU1L+HU2L+HU3H+HL1H+HL2L+HL3L+WL ;h2->hi
2902      Y:000042 Y:000042                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3H+HL1H+HL2H+HL3L+WL ;h1->lo
2903      Y:000043 Y:000043                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3L+HL1L+HL2H+HL3L+WL ;h3->hi
2904      Y:000044 Y:000044                   DC      CLK3+S_DELAY+RH+HU1H+HU2H+HU3L+HL1L+HL2H+HL3H+WL ;h2->lo
2905      Y:000045 Y:000045                   DC      CLK3+S_DELAY+RH+HU1H+HU2L+HU3L+HL1L+HL2L+HL3H+WL ;h1->hi
2906      Y:000046 Y:000046                   DC      CLK3+PRE_SET_DLY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;Reset_Off+Delay*/
2907      Y:000047 Y:000047                   DC      CLK3+$000000+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;dummy for transmit delay
2908      Y:000048 Y:000048                   DC      VIDEO+$000000+%1110111
2909      Y:000049 Y:000049                   DC      VIDEO+I_DELAY+%0000111
2910      Y:00004A Y:00004A                   DC      VIDEO+$000000+%0011011
2911      Y:00004B Y:00004B                   DC      CLK3+SW_DELAY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WH ;SW->hi
2912      Y:00004C Y:00004C                   DC      CLK3+POST_SET_DLY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;SW->lo
2913      Y:00004D Y:00004D                   DC      VIDEO+I_DELAY+%0001011
2914      Y:00004E Y:00004E                   DC      VIDEO+$000000+%0011011
2915      Y:00004F Y:00004F                   DC      VIDEO+DCRST_DELAY+%1110111
2916                                END_SERIAL_IDLE
2917   
2918                                ; These are the three skipping tables
2919                                SERIAL_SKIP_LEFT                                    ; Serial clocking waveform for skipping left
2920      Y:000050 Y:000050                   DC      END_SERIAL_SKIP_LEFT-SERIAL_SKIP_LEFT-1
2921      Y:000051 Y:000051                   DC      VIDEO+$000000+%1110100
2922      Y:000052 Y:000052                   DC      CLK3+S_DELAY+RH+HU1L+HU2L+HU3H+HL1L+HL2L+HL3H+WL ;h2->hi
2923      Y:000053 Y:000053                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3H+HL1L+HL2H+HL3H+WL ;h1->lo
2924      Y:000054 Y:000054                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3L+HL1L+HL2H+HL3L+WL ;h3->hi
2925      Y:000055 Y:000055                   DC      CLK3+S_DELAY+RH+HU1H+HU2H+HU3L+HL1H+HL2H+HL3L+WL ;h2->lo
2926      Y:000056 Y:000056                   DC      CLK3+S_DELAY+RH+HU1H+HU2L+HU3L+HL1H+HL2L+HL3L+WL ;h1->hi
2927      Y:000057 Y:000057                   DC      CLK3+S_DELAY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;Reset_Off+Delay
2928      Y:000058 Y:000058                   DC      CLK3+SW_DELAY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WH ;SW->hi
2929                                END_SERIAL_SKIP_LEFT
2930   
2931                                SERIAL_SKIP_RIGHT                                   ; Serial clocking waveform for skipping righ
t
2932      Y:000059 Y:000059                   DC      END_SERIAL_SKIP_RIGHT-SERIAL_SKIP_RIGHT-1
2933      Y:00005A Y:00005A                   DC      VIDEO+$000000+%1110100
2934      Y:00005B Y:00005B                   DC      CLK3+S_DELAY+RH+HU1L+HU2L+HU3H+HL1L+HL2L+HL3H+WL ;h2->hi
2935      Y:00005C Y:00005C                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3H+HL1L+HL2H+HL3H+WL ;h1->lo
2936      Y:00005D Y:00005D                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3L+HL1L+HL2H+HL3L+WL ;h3->hi
2937      Y:00005E Y:00005E                   DC      CLK3+S_DELAY+RH+HU1H+HU2H+HU3L+HL1H+HL2H+HL3L+WL ;h2->lo
2938      Y:00005F Y:00005F                   DC      CLK3+S_DELAY+RH+HU1H+HU2L+HU3L+HL1H+HL2L+HL3L+WL ;h1->hi
2939      Y:000060 Y:000060                   DC      CLK3+S_DELAY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;Reset_Off+Delay
2940      Y:000061 Y:000061                   DC      CLK3+SW_DELAY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WH ;SW->hi
2941                                END_SERIAL_SKIP_RIGHT
2942   
2943                                SERIAL_SKIP_SPLIT                                   ; Serial clocking waveform for skipping spli
t
2944      Y:000062 Y:000062                   DC      END_SERIAL_SKIP_SPLIT-SERIAL_SKIP_SPLIT-1
2945      Y:000063 Y:000063                   DC      VIDEO+$000000+%1110100
2946      Y:000064 Y:000064                   DC      CLK3+S_DELAY+RH+HU1H+HU2L+HU3L+HL1L+HL2L+HL3H+WL ;h2->hi
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 43



2947      Y:000065 Y:000065                   DC      CLK3+S_DELAY+RH+HU1H+HU2H+HU3L+HL1L+HL2H+HL3H+WL ;h1->lo
2948      Y:000066 Y:000066                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3L+HL1L+HL2H+HL3L+WL ;h3->hi
2949      Y:000067 Y:000067                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3H+HL1H+HL2H+HL3L+WL ;h2->lo
2950      Y:000068 Y:000068                   DC      CLK3+S_DELAY+RH+HU1L+HU2L+HU3H+HL1H+HL2L+HL3L+WL ;h1->hi
2951      Y:000069 Y:000069                   DC      CLK3+S_DELAY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;Reset_Off+Delay
2952      Y:00006A Y:00006A                   DC      CLK3+SW_DELAY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WH ;SW->hi
2953                                END_SERIAL_SKIP_SPLIT
2954   
2955                                SERIAL_READ_LEFT                                    ; Berkeley calls this the LOWER readout of t
he CCD
2956      Y:00006B Y:00006B                   DC      END_SERIAL_READ_LEFT-SERIAL_READ_LEFT-1
2957      Y:00006C Y:00006C                   DC      VIDEO+$000000+%1110100
2958      Y:00006D Y:00006D                   DC      CLK3+S_DELAY+RH+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;h3->lo,SW->lo,Reset_On
2959      Y:00006E Y:00006E                   DC      CLK3+S_DELAY+RH+HU1L+HU2L+HU3H+HL1L+HL2L+HL3H+WL ;h2->hi
2960      Y:00006F Y:00006F                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3H+HL1L+HL2H+HL3H+WL ;h1->lo
2961      Y:000070 Y:000070                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3L+HL1L+HL2H+HL3L+WL ;h3->hi
2962      Y:000071 Y:000071                   DC      CLK3+S_DELAY+RH+HU1H+HU2H+HU3L+HL1H+HL2H+HL3L+WL ;h2->lo
2963      Y:000072 Y:000072                   DC      CLK3+S_DELAY+RH+HU1H+HU2L+HU3L+HL1H+HL2L+HL3L+WL ;h1->hi
2964      Y:000073 Y:000073                   DC      CLK3+PRE_SET_DLY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;Reset_Off+Delay
2965      Y:000074 Y:000074         SXL       DC      $00F000
2966      Y:000075 Y:000075                   DC      VIDEO+$000000+%1110111
2967      Y:000076 Y:000076                   DC      VIDEO+I_DELAY+%0000111
2968      Y:000077 Y:000077                   DC      VIDEO+$000000+%0011011
2969      Y:000078 Y:000078                   DC      CLK3+SW_DELAY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WH ;SW->hi
2970      Y:000079 Y:000079                   DC      CLK3+POST_SET_DLY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;SW->lo
2971      Y:00007A Y:00007A                   DC      VIDEO+I_DELAY+%0001011
2972      Y:00007B Y:00007B                   DC      VIDEO+$000000+%0011011
2973      Y:00007C Y:00007C                   DC      VIDEO+DCRST_DELAY+%1110111
2974                                END_SERIAL_READ_LEFT
2975   
2976                                SERIAL_READ_RIGHT                                   ; Berkeley calls this the UPPER readout of t
he CCD
2977      Y:00007D Y:00007D                   DC      END_SERIAL_READ_RIGHT-SERIAL_READ_RIGHT-1
2978      Y:00007E Y:00007E                   DC      VIDEO+$000000+%1110100
2979      Y:00007F Y:00007F                   DC      CLK3+S_DELAY+RH+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;h3->lo,SW->lo,Reset_On
2980      Y:000080 Y:000080                   DC      CLK3+S_DELAY+RH+HU1H+HU2L+HU3L+HL1H+HL2L+HL3L+WL ;h2->hi
2981      Y:000081 Y:000081                   DC      CLK3+S_DELAY+RH+HU1H+HU2H+HU3L+HL1H+HL2H+HL3L+WL ;h1->lo
2982      Y:000082 Y:000082                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3L+HL1L+HL2H+HL3L+WL ;h3->hi
2983      Y:000083 Y:000083                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3H+HL1L+HL2H+HL3H+WL ;h2->lo
2984      Y:000084 Y:000084                   DC      CLK3+S_DELAY+RH+HU1L+HU2L+HU3H+HL1L+HL2L+HL3H+WL ;h1->hi
2985      Y:000085 Y:000085                   DC      CLK3+PRE_SET_DLY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;Reset_Off+Delay
2986      Y:000086 Y:000086         SXR       DC      $00F0C2
2987      Y:000087 Y:000087                   DC      VIDEO+$000000+%1110111
2988      Y:000088 Y:000088                   DC      VIDEO+I_DELAY+%0000111
2989      Y:000089 Y:000089                   DC      VIDEO+$000000+%0011011
2990      Y:00008A Y:00008A                   DC      CLK3+SW_DELAY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WH ;SW->hi
2991      Y:00008B Y:00008B                   DC      CLK3+POST_SET_DLY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;SW->lo
2992      Y:00008C Y:00008C                   DC      VIDEO+I_DELAY+%0001011
2993      Y:00008D Y:00008D                   DC      VIDEO+$000000+%0011011
2994      Y:00008E Y:00008E                   DC      VIDEO+DCRST_DELAY+%1110111
2995                                END_SERIAL_READ_RIGHT
2996   
2997                                SERIAL_READ_SPLIT
2998      Y:00008F Y:00008F                   DC      END_SERIAL_READ_SPLIT-SERIAL_READ_SPLIT-1
2999      Y:000090 Y:000090                   DC      VIDEO+$000000+%1110100
3000      Y:000091 Y:000091                   DC      CLK3+S_DELAY+RH+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;h3->lo,SW->lo,Reset_On
3001      Y:000092 Y:000092                   DC      CLK3+S_DELAY+RH+HU1L+HU2L+HU3H+HL1H+HL2L+HL3L+WL ;h2->hi
3002      Y:000093 Y:000093                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3H+HL1H+HL2H+HL3L+WL ;h1->lo
3003      Y:000094 Y:000094                   DC      CLK3+S_DELAY+RH+HU1L+HU2H+HU3L+HL1L+HL2H+HL3L+WL ;h3->hi
3004      Y:000095 Y:000095                   DC      CLK3+S_DELAY+RH+HU1H+HU2H+HU3L+HL1L+HL2H+HL3H+WL ;h2->lo
3005      Y:000096 Y:000096                   DC      CLK3+S_DELAY+RH+HU1H+HU2L+HU3L+HL1L+HL2L+HL3H+WL ;h1->hi
3006      Y:000097 Y:000097                   DC      CLK3+PRE_SET_DLY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;Reset_Off+Delay
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 44



3007      Y:000098 Y:000098         SXRL      DC      $00F0C0
3008      Y:000099 Y:000099                   DC      VIDEO+$000000+%1110111
3009      Y:00009A Y:00009A                   DC      VIDEO+I_DELAY+%0000111
3010      Y:00009B Y:00009B                   DC      VIDEO+$000000+%0011011
3011      Y:00009C Y:00009C                   DC      CLK3+SW_DELAY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WH ;SW->hi
3012      Y:00009D Y:00009D                   DC      CLK3+POST_SET_DLY+RL+HU1H+HU2L+HU3H+HL1H+HL2L+HL3H+WL ;SW->lo
3013      Y:00009E Y:00009E                   DC      VIDEO+I_DELAY+%0001011
3014      Y:00009F Y:00009F                   DC      VIDEO+$000000+%0011011
3015      Y:0000A0 Y:0000A0                   DC      VIDEO+DCRST_DELAY+%1110111
3016                                END_SERIAL_READ_SPLIT
3017   
3018                                VSUBN
3019                                          VOLTS   VSUB,20.0                         ; Vsub  0.0 140 V, pin #
**** 3024 [LBNL_3.5k.waveforms 390]: Setting voltage VSUB 20.0V 407
3025      Y:0000A2 Y:0000A2         ERHI      DC      EREND-ERHI-1
3026                                          VOLTS   VSUB,0                            ; Vsub  0.0 140 V, pin #
**** 3031 [LBNL_3.5k.waveforms 392]: Setting voltage VSUB 0V 0
3032                                          VOLTS   V1_HI,9                           ; Vertical High
**** 3037 [LBNL_3.5k.waveforms 393]: Setting voltage V1_HI 9V 3465
3038                                          VOLTS   V1_LO,9                           ; Vertical Low
**** 3043 [LBNL_3.5k.waveforms 394]: Setting voltage V1_LO 9V 3465
3044                                          VOLTS   V2_HI,9                           ; Vertical High
**** 3049 [LBNL_3.5k.waveforms 395]: Setting voltage V2_HI 9V 3465
3050                                          VOLTS   V2_LO,9                           ; Vertical Low
**** 3055 [LBNL_3.5k.waveforms 396]: Setting voltage V2_LO 9V 3465
3056                                          VOLTS   V3_HI,9                           ; Vertical High
**** 3061 [LBNL_3.5k.waveforms 397]: Setting voltage V3_HI 9V 3465
3062                                          VOLTS   V3_LO,9                           ; Vertical Low
**** 3067 [LBNL_3.5k.waveforms 398]: Setting voltage V3_LO 9V 3465
3068                                          VOLTS   FS1_HI,9                          ; Vertical High
**** 3073 [LBNL_3.5k.waveforms 399]: Setting voltage FS1_HI 9V 3465
3074                                          VOLTS   FS1_LO,9                          ; Vertical Low
**** 3079 [LBNL_3.5k.waveforms 400]: Setting voltage FS1_LO 9V 3465
3080                                          VOLTS   FS2_HI,9                          ; Vertical High
**** 3085 [LBNL_3.5k.waveforms 401]: Setting voltage FS2_HI 9V 3465
3086                                          VOLTS   FS2_LO,9                          ; Vertical Low
**** 3091 [LBNL_3.5k.waveforms 402]: Setting voltage FS2_LO 9V 3465
3092                                          VOLTS   FS3_HI,9                          ; Vertical High
**** 3097 [LBNL_3.5k.waveforms 403]: Setting voltage FS3_HI 9V 3465
3098                                          VOLTS   FS3_LO,9                          ; Vertical Low
**** 3103 [LBNL_3.5k.waveforms 404]: Setting voltage FS3_LO 9V 3465
3104      Y:0000B0 Y:0000B0         EREND     DC      EREND2-EREND-1
3105                                          VOLTS   V1_HI,5.0                         ; Vertical High
**** 3110 [LBNL_3.5k.waveforms 406]: Setting voltage V1_HI 5.0V 2835
3111                                          VOLTS   V1_LO,-3.0                        ; Vertical Low
**** 3116 [LBNL_3.5k.waveforms 407]: Setting voltage V1_LO -3.0V 1575
3117                                          VOLTS   V2_HI,5.0                         ; Vertical High
**** 3122 [LBNL_3.5k.waveforms 408]: Setting voltage V2_HI 5.0V 2835
3123                                          VOLTS   V2_LO,-3.0                        ; Vertical Low
**** 3128 [LBNL_3.5k.waveforms 409]: Setting voltage V2_LO -3.0V 1575
3129                                          VOLTS   V3_HI,5.0                         ; Vertical High
**** 3134 [LBNL_3.5k.waveforms 410]: Setting voltage V3_HI 5.0V 2835
3135                                          VOLTS   V3_LO,-3.0                        ; Vertical Low
**** 3140 [LBNL_3.5k.waveforms 411]: Setting voltage V3_LO -3.0V 1575
3141                                          VOLTS   FS1_HI,5.0                        ; Vertical High
**** 3146 [LBNL_3.5k.waveforms 412]: Setting voltage FS1_HI 5.0V 2835
3147                                          VOLTS   FS1_LO,-3.0                       ; Vertical Low
**** 3152 [LBNL_3.5k.waveforms 413]: Setting voltage FS1_LO -3.0V 1575
3153                                          VOLTS   FS2_HI,5.0                        ; Vertical High
**** 3158 [LBNL_3.5k.waveforms 414]: Setting voltage FS2_HI 5.0V 2835
3159                                          VOLTS   FS2_LO,-3.0                       ; Vertical Low
**** 3164 [LBNL_3.5k.waveforms 415]: Setting voltage FS2_LO -3.0V 1575
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 45



3165                                          VOLTS   FS3_HI,5.0                        ; Vertical High
**** 3170 [LBNL_3.5k.waveforms 416]: Setting voltage FS3_HI 5.0V 2835
3171                                          VOLTS   FS3_LO,-3.0                       ; Vertical Low
**** 3176 [LBNL_3.5k.waveforms 417]: Setting voltage FS3_LO -3.0V 1575
3177                                EREND2
3178   
3179                                ; Initialization of clock driver and video processor DACs and switches
3180      Y:0000BD Y:0000BD         DACS      DC      END_DACS-DACS-1
3181                                          VOLTS   V1_HI,5.0                         ; Vertical High
**** 3186 [LBNL_3.5k.waveforms 422]: Setting voltage V1_HI 5.0V 2835
3187                                          VOLTS   V1_LO,-3.0                        ; Vertical Low
**** 3192 [LBNL_3.5k.waveforms 423]: Setting voltage V1_LO -3.0V 1575
3193                                          VOLTS   V2_HI,5.0                         ; Vertical High
**** 3198 [LBNL_3.5k.waveforms 424]: Setting voltage V2_HI 5.0V 2835
3199                                          VOLTS   V2_LO,-3.0                        ; Vertical Low
**** 3204 [LBNL_3.5k.waveforms 425]: Setting voltage V2_LO -3.0V 1575
3205                                          VOLTS   V3_HI,5.0                         ; Vertical High
**** 3210 [LBNL_3.5k.waveforms 426]: Setting voltage V3_HI 5.0V 2835
3211                                          VOLTS   V3_LO,-3.0                        ; Vertical Low
**** 3216 [LBNL_3.5k.waveforms 427]: Setting voltage V3_LO -3.0V 1575
3217                                          VOLTS   FS1_HI,5.0                        ; frame store High
**** 3222 [LBNL_3.5k.waveforms 428]: Setting voltage FS1_HI 5.0V 2835
3223                                          VOLTS   FS1_LO,-3.0                       ; frame store Low
**** 3228 [LBNL_3.5k.waveforms 429]: Setting voltage FS1_LO -3.0V 1575
3229                                          VOLTS   FS2_HI,5.0                        ; frame store High
**** 3234 [LBNL_3.5k.waveforms 430]: Setting voltage FS2_HI 5.0V 2835
3235                                          VOLTS   FS2_LO,-3.0                       ; frame store Low
**** 3240 [LBNL_3.5k.waveforms 431]: Setting voltage FS2_LO -3.0V 1575
3241                                          VOLTS   FS3_HI,5.0                        ; frame store High
**** 3246 [LBNL_3.5k.waveforms 432]: Setting voltage FS3_HI 5.0V 2835
3247                                          VOLTS   FS3_LO,-3.0                       ; frame store Low
**** 3252 [LBNL_3.5k.waveforms 433]: Setting voltage FS3_LO -3.0V 1575
3253                                          VOLTS   T1_HI,5.0                         ; Transfer gate High
**** 3258 [LBNL_3.5k.waveforms 434]: Setting voltage T1_HI 5.0V 2835
3259                                          VOLTS   T1_LO,-3.0                        ; Transfer gate Low
**** 3264 [LBNL_3.5k.waveforms 435]: Setting voltage T1_LO -3.0V 1575
3265                                          VOLTS   T2_HI,5.0                         ; Transfer gate High
**** 3270 [LBNL_3.5k.waveforms 436]: Setting voltage T2_HI 5.0V 2835
3271                                          VOLTS   T2_LO,-3.0                        ; Transfer gate Low
**** 3276 [LBNL_3.5k.waveforms 437]: Setting voltage T2_LO -3.0V 1575
3277   
3278                                          VOLTS   H1U_HI,+6.0                       ; Horizontal High
**** 3283 [LBNL_3.5k.waveforms 439]: Setting voltage H1U_HI +6.0V 2993
3284                                          VOLTS   H1U_LO,-4.0                       ; Horizontal Low
**** 3289 [LBNL_3.5k.waveforms 440]: Setting voltage H1U_LO -4.0V 1417
3290                                          VOLTS   H2U_HI,+6.0                       ; HoVR2rizontal High
**** 3295 [LBNL_3.5k.waveforms 441]: Setting voltage H2U_HI +6.0V 2993
3296                                          VOLTS   H2U_LO,-4.0                       ; Horizontal Low
**** 3301 [LBNL_3.5k.waveforms 442]: Setting voltage H2U_LO -4.0V 1417
3302                                          VOLTS   H3U_HI,+6.0                       ; Horizontal High
**** 3307 [LBNL_3.5k.waveforms 443]: Setting voltage H3U_HI +6.0V 2993
3308                                          VOLTS   H3U_LO,-4.0                       ; Horizontal Low
**** 3313 [LBNL_3.5k.waveforms 444]: Setting voltage H3U_LO -4.0V 1417
3314                                          VOLTS   H1L_HI,+6.0                       ; Horizontal High
**** 3319 [LBNL_3.5k.waveforms 445]: Setting voltage H1L_HI +6.0V 2993
3320                                          VOLTS   H1L_LO,-4.0                       ; Horizontal Low
**** 3325 [LBNL_3.5k.waveforms 446]: Setting voltage H1L_LO -4.0V 1417
3326                                          VOLTS   H2L_HI,+6.0                       ; Horizontal High
**** 3331 [LBNL_3.5k.waveforms 447]: Setting voltage H2L_HI +6.0V 2993
3332                                          VOLTS   H2L_LO,-4.0                       ; Horizontal Low
**** 3337 [LBNL_3.5k.waveforms 448]: Setting voltage H2L_LO -4.0V 1417
3338                                          VOLTS   H3L_HI,+6.0                       ; Horizontal High
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 46



**** 3343 [LBNL_3.5k.waveforms 449]: Setting voltage H3L_HI +6.0V 2993
3344                                          VOLTS   H3L_LO,-4.0                       ; Horizontal Low
**** 3349 [LBNL_3.5k.waveforms 450]: Setting voltage H3L_LO -4.0V 1417
3350                                          VOLTS   SWU_HI,5.0                        ; Output transfer gate High
**** 3355 [LBNL_3.5k.waveforms 451]: Setting voltage SWU_HI 5.0V 2835
3356                                          VOLTS   SWU_LO,-5.0                       ; Output transfer gate Low
**** 3361 [LBNL_3.5k.waveforms 452]: Setting voltage SWU_LO -5.0V 1260
3362                                          VOLTS   SWL_HI,5.0
**** 3367 [LBNL_3.5k.waveforms 453]: Setting voltage SWL_HI 5.0V 2835
3368                                          VOLTS   SWL_LO,-5.0
**** 3373 [LBNL_3.5k.waveforms 454]: Setting voltage SWL_LO -5.0V 1260
3374                                          VOLTS   RU_HI,-6.0                        ; Reset ACTIVE wrong polarity....
**** 3379 [LBNL_3.5k.waveforms 455]: Setting voltage RU_HI -6.0V 1102
3380                                          VOLTS   RU_LO,0.0                         ; Reset INACTIVE
**** 3385 [LBNL_3.5k.waveforms 456]: Setting voltage RU_LO 0.0V 2048
3386                                          VOLTS   RL_HI,-6.0
**** 3391 [LBNL_3.5k.waveforms 457]: Setting voltage RL_HI -6.0V 1102
3392                                          VOLTS   RL_LO,0.0
**** 3397 [LBNL_3.5k.waveforms 458]: Setting voltage RL_LO 0.0V 2048
3398   
3399                                ; Set the ARC-48 video gain, one of 16 possible values
3400      Y:0000E2 Y:0000E2                   DC      VID0+$0C0008                      ; Image data FIFO Reset
3401      Y:0000E3 Y:0000E3                   DC      VID0+$0D000C                      ; Gain from 0 to $F
3402   
3403                                ; Initialize the DAC gain and offset registers for the ARC-48 video board
3404      Y:0000E4 Y:0000E4                   DC      VID0+DAC_ADDR+$0000F8             ; Select all #8 channels of the DAC.
3405      Y:0000E5 Y:0000E5                   DC      VID0+DAC_RegC+$003FFF             ; Set c(Offset register)=0x3FFF (at max)
3406      Y:0000E6 Y:0000E6                   DC      VID0+DAC_ADDR+$0000F9             ; Select all #9 channels of the DAC.
3407      Y:0000E7 Y:0000E7                   DC      VID0+DAC_RegC+$003FFF             ; Set c=0x3FFF
3408   
3409      Y:0000E8 Y:0000E8                   DC      VID0+DAC_ADDR+$0000F8             ; Select all channels #8 of the DAC.
3410      Y:0000E9 Y:0000E9                   DC      VID0+DAC_RegM+$000FFF             ; Set m(Gain register)=0x0FFF,Rang=0--4.3V
3411      Y:0000EA Y:0000EA                   DC      VID0+DAC_ADDR+$0000F9             ; Select all channels #9 of the DAC.
3412      Y:0000EB Y:0000EB                   DC      VID0+DAC_RegM+$000FFF             ; Set m=0x0FFF,Rang=0--4.3V
3413   
3414      Y:0000EC Y:0000EC                   DC      VID0+DAC_ADDR+$0000F8             ; Output register of 9th, 10th channels so o
utput
3415      Y:0000ED Y:0000ED                   DC      VID0+DAC_RegD+$002500             ;  is 2.5 volts
3416      Y:0000EE Y:0000EE                   DC      VID0+DAC_ADDR+$0000F9
3417      Y:0000EF Y:0000EF                   DC      VID0+DAC_RegD+$002500
3418   
3419      Y:0000F0 Y:0000F0                   DC      VID0+DAC_ADDR+$000030             ; Gain register of channels #0 to 7 at max.
3420      Y:0000F1 Y:0000F1                   DC      VID0+DAC_RegM+$001FFF             ;  This is for the groups A&B
3421      Y:0000F2 Y:0000F2                   DC      VID0+DAC_ADDR+$000031
3422      Y:0000F3 Y:0000F3                   DC      VID0+DAC_RegM+$001FFF
3423      Y:0000F4 Y:0000F4                   DC      VID0+DAC_ADDR+$000032
3424      Y:0000F5 Y:0000F5                   DC      VID0+DAC_RegM+$001FFF
3425      Y:0000F6 Y:0000F6                   DC      VID0+DAC_ADDR+$000033
3426      Y:0000F7 Y:0000F7                   DC      VID0+DAC_RegM+$001FFF
3427      Y:0000F8 Y:0000F8                   DC      VID0+DAC_ADDR+$000034
3428      Y:0000F9 Y:0000F9                   DC      VID0+DAC_RegM+$001FFF
3429      Y:0000FA Y:0000FA                   DC      VID0+DAC_ADDR+$000035
3430      Y:0000FB Y:0000FB                   DC      VID0+DAC_RegM+$001FFF
3431      Y:0000FC Y:0000FC                   DC      VID0+DAC_ADDR+$000036
3432      Y:0000FD Y:0000FD                   DC      VID0+DAC_RegM+$001FFF
3433      Y:0000FE Y:0000FE                   DC      VID0+DAC_ADDR+$000037
3434      Y:0000FF Y:0000FF                   DC      VID0+DAC_RegM+$001FFF
3435   
3436      Y:000100 Y:000100                   DC      VID0+DAC_ADDR+$000030             ; Offset register of channels #0 to 7 at max
.
3437      Y:000101 Y:000101                   DC      VID0+DAC_RegC+$003FFF             ;  This is for the groups A&B
3438      Y:000102 Y:000102                   DC      VID0+DAC_ADDR+$000031
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 47



3439      Y:000103 Y:000103                   DC      VID0+DAC_RegC+$003FFF
3440      Y:000104 Y:000104                   DC      VID0+DAC_ADDR+$000032
3441      Y:000105 Y:000105                   DC      VID0+DAC_RegC+$003FFF
3442      Y:000106 Y:000106                   DC      VID0+DAC_ADDR+$000033
3443      Y:000107 Y:000107                   DC      VID0+DAC_RegC+$003FFF
3444      Y:000108 Y:000108                   DC      VID0+DAC_ADDR+$000034
3445      Y:000109 Y:000109                   DC      VID0+DAC_RegC+$003FFF
3446      Y:00010A Y:00010A                   DC      VID0+DAC_ADDR+$000035
3447      Y:00010B Y:00010B                   DC      VID0+DAC_RegC+$003FFF
3448      Y:00010C Y:00010C                   DC      VID0+DAC_ADDR+$000036
3449      Y:00010D Y:00010D                   DC      VID0+DAC_RegC+$003FFF
3450      Y:00010E Y:00010E                   DC      VID0+DAC_ADDR+$000037
3451      Y:00010F Y:00010F                   DC      VID0+DAC_RegC+$003FFF
3452   
3453                                ; ARC-48 video Offsets
3454      Y:000110 Y:000110                   DC      VID0+$0E0000+$000018
3455      Y:000111 Y:000111                   DC      VID0+$0FC000+OFFSET0
3456      Y:000112 Y:000112                   DC      VID0+$0E0000+$000019
3457      Y:000113 Y:000113                   DC      VID0+$0FC000+OFFSET1
3458      Y:000114 Y:000114                   DC      VID0+$0E0000+$000028
3459      Y:000115 Y:000115                   DC      VID0+$0FC000+OFFSET2
3460      Y:000116 Y:000116                   DC      VID0+$0E0000+$000029
3461      Y:000117 Y:000117                   DC      VID0+$0FC000+OFFSET3
3462      Y:000118 Y:000118                   DC      VID0+$0E0000+$000048
3463      Y:000119 Y:000119                   DC      VID0+$0FC000+OFFSET4
3464      Y:00011A Y:00011A                   DC      VID0+$0E0000+$000049
3465      Y:00011B Y:00011B                   DC      VID0+$0FC000+OFFSET5
3466      Y:00011C Y:00011C                   DC      VID0+$0E0000+$000088
3467      Y:00011D Y:00011D                   DC      VID0+$0FC000+OFFSET6
3468      Y:00011E Y:00011E                   DC      VID0+$0E0000+$000089
3469      Y:00011F Y:00011F                   DC      VID0+$0FC000+OFFSET7
3470   
3471                                ; LBNL high voltage bias board
3472                                          VOLTS   VSUB,20.0                         ; Vsub  0.0 140 V
**** 3477 [LBNL_3.5k.waveforms 533]: Setting voltage VSUB 20.0V 407
3478                                          VOLTS   RAMP,5.0                          ; Vsub  AVG RAMP RATE
**** 3483 [LBNL_3.5k.waveforms 534]: Setting voltage RAMP 5.0V 2048
3484                                          VOLTS   VDD1,-22.0                        ; Vdd  -5.1 -25V
**** 3489 [LBNL_3.5k.waveforms 535]: Setting voltage VDD1 -22.0V 3604
3490                                          VOLTS   VDD2,-22.0                        ; Vdd  -5.1 -25V
**** 3495 [LBNL_3.5k.waveforms 536]: Setting voltage VDD2 -22.0V 3604
3496                                          VOLTS   VDD3,-22.0                        ; Vdd  -5.1 -25V
**** 3501 [LBNL_3.5k.waveforms 537]: Setting voltage VDD3 -22.0V 3604
3502                                          VOLTS   VDD4,-22.0                        ; Vdd  -5.1 -25V
**** 3507 [LBNL_3.5k.waveforms 538]: Setting voltage VDD4 -22.0V 3604
3508                                          VOLTS   VR1,-12.5                         ; Vr   -5.1 -25V
**** 3513 [LBNL_3.5k.waveforms 539]: Setting voltage VR1 -12.5V 2048
3514                                          VOLTS   VR2,-12.5                         ; Vr   -5.1 -25V
**** 3519 [LBNL_3.5k.waveforms 540]: Setting voltage VR2 -12.5V 2048
3520                                          VOLTS   VR3,-12.5                         ; Vr   -5.1 -25V
**** 3525 [LBNL_3.5k.waveforms 541]: Setting voltage VR3 -12.5V 2048
3526                                          VOLTS   VR4,-12.5                         ; Vr   -5.1 -25V
**** 3531 [LBNL_3.5k.waveforms 542]: Setting voltage VR4 -12.5V 2048
3532                                          VOLTS   VOG1,2.16                         ; Vopg  -10  10 V
**** 3537 [LBNL_3.5k.waveforms 543]: Setting voltage VOG1 2.16V 1769
3538                                          VOLTS   VOG2,2.16                         ; Vopg  -10  10 V
**** 3543 [LBNL_3.5k.waveforms 544]: Setting voltage VOG2 2.16V 1769
3544                                          VOLTS   VOG3,2.16                         ; Vopg  -10  10 V
**** 3549 [LBNL_3.5k.waveforms 545]: Setting voltage VOG3 2.16V 1769
3550                                          VOLTS   VOG4,2.16                         ; Vopg  -10  10 V
**** 3555 [LBNL_3.5k.waveforms 546]: Setting voltage VOG4 2.16V 1769
3556   
Motorola DSP56300 Assembler  Version 6.3.4   13-01-07  10:38:42  LBNL_3.5k.waveforms  Page 48



3557                                END_DACS
3558   
3559                                BIAS_ON   VOLTS   VSUB,20.0                         ; Vsub  0.0 140 V
**** 3564 [LBNL_3.5k.waveforms 550]: Setting voltage VSUB 20.0V 407
3565   
3566                                BIAS_OFF  VOLTS   VSUB,0.0                          ; Vsub  0.0 140 V
**** 3571 [LBNL_3.5k.waveforms 552]: Setting voltage VSUB 0.0V 0
3572   
3573                                 END_APPLICATON_Y_MEMORY
3574      000130                              EQU     @LCV(L)
3575   
3576                                ; End of program
3577                                          END

0    Errors
0    Warnings


