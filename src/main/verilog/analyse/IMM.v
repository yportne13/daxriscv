`timescale 1ns / 1ps

// implemented opcodes:

`define LUI     7'b01101_11      // lui   rd,imm[31:12]
`define AUIPC   7'b00101_11      // auipc rd,imm[31:12]
`define JAL     7'b11011_11      // jal   rd,imm[xxxxx]
`define JALR    7'b11001_11      // jalr  rd,rs1,imm[11:0] 
`define BCC     7'b11000_11      // bcc   rs1,rs2,imm[12:1]
`define LCC     7'b00000_11      // lxx   rd,rs1,imm[11:0]
`define SCC     7'b01000_11      // sxx   rs1,rs2,imm[11:0]
`define MCC     7'b00100_11      // xxxi  rd,rs1,imm[11:0]
`define RCC     7'b01100_11      // xxx   rd,rs1,rs2 
`define MAC     7'b11111_11      // mac   rd,rs1,rs2

// not implemented opcodes:

`define FCC     7'b00011_11      // fencex
`define CCC     7'b11100_11      // exx, csrxx

// configuration file

module darkIMM
//#(
//    parameter [31:0] RESET_PC = 0,
//    parameter [31:0] RESET_SP = 4096
//) 
(
    input             CLK,   // clock
    input             RES,   // reset
    input             HLT,   // halt
    
    input      [31:0] IDATA, // instruction data bus

    output     [31:0] SIMM

);

    // dummy 32-bit words w/ all-0s and all-1s: 

    wire [31:0] ALL0  = 0;
    wire [31:0] ALL1  = -1;
    
    // pre-decode: IDATA is break apart as described in the RV32I specification

    reg [31:0] XIDATA;

    reg [31:0] XSIMM;
    reg [31:0] XUIMM;

    assign SIMM = XSIMM;
    always@(posedge CLK)
    begin
        // signal extended immediate, according to the instruction type:
        
        XSIMM  <= RES ? 0 : HLT ? XSIMM :
                 IDATA[6:0]==`SCC ? { IDATA[31] ? ALL1[31:12]:ALL0[31:12], IDATA[31:25],IDATA[11:7] } : // s-type
                 IDATA[6:0]==`BCC ? { IDATA[31] ? ALL1[31:13]:ALL0[31:13], IDATA[31],IDATA[7],IDATA[30:25],IDATA[11:8],ALL0[0] } : // b-type
                 IDATA[6:0]==`JAL ? { IDATA[31] ? ALL1[31:21]:ALL0[31:21], IDATA[31], IDATA[19:12], IDATA[20], IDATA[30:21], ALL0[0] } : // j-type
                 IDATA[6:0]==`LUI||
                 IDATA[6:0]==`AUIPC ? { IDATA[31:12], ALL0[11:0] } : // u-type
                                      { IDATA[31] ? ALL1[31:12]:ALL0[31:12], IDATA[31:20] }; // i-type
        // non-signal extended immediate, according to the instruction type:

        XUIMM  <= RES ? 0: HLT ? XUIMM :
                 IDATA[6:0]==`SCC ? { ALL0[31:12], IDATA[31:25],IDATA[11:7] } : // s-type
                 IDATA[6:0]==`BCC ? { ALL0[31:13], IDATA[31],IDATA[7],IDATA[30:25],IDATA[11:8],ALL0[0] } : // b-type
                 IDATA[6:0]==`JAL ? { ALL0[31:21], IDATA[31], IDATA[19:12], IDATA[20], IDATA[30:21], ALL0[0] } : // j-type
                 IDATA[6:0]==`LUI||
                 IDATA[6:0]==`AUIPC ? { IDATA[31:12], ALL0[11:0] } : // u-type
                                      { ALL0[31:12], IDATA[31:20] }; // i-type
    end

endmodule