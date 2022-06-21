package src

import spinal.core._
import spinal.lib._
import src.Plugins._
import mylib.muxList

class daxriscv extends Component {
  val io = new Bundle {
    val RES = in Bool()
    val HLT = in Bool()

    val IDATA = in Bits(32 bits)
    val IADDR = out Bits(32 bits)

    val DATAI = in Bits(32 bits)
    val DATAO = out Bits(32 bits)
    val DADDR = out Bits(32 bits)

    val DLEN = out Bits(3 bits)
    val RW   = out Bool()

    val IDLE = out Bool()

    val DEBUG = out Bits(4 bits)
  }

  implicit val conf = Config()
  import conf._

  DATAI := io.DATAI

  val plugins = List(
    LUI(),
    AUIPC(),
    MRCC(),
    JumpBranch(),
    LSCC(),
    MAC(),
    FCC(),
    CCC()
  )
/*  Plugin("LUI"  ,B"0110111", IMMtype.U),      // lui   rd,imm[31:12]
    Plugin("AUIPC",B"0010111", IMMtype.U),      // auipc rd,imm[31:12]
    Plugin("JAL"  ,B"1101111", IMMtype.J),      // jal   rd,imm[xxxxx]
    Plugin("JALR" ,B"1100111", IMMtype.I),      // jalr  rd,rs1,imm[11:0] 
    Plugin("BCC"  ,B"1100011", IMMtype.B),      // bcc   rs1,rs2,imm[12:1]
    Plugin("LCC"  ,B"0000011", IMMtype.I),      // lxx   rd,rs1,imm[11:0]
    Plugin("SCC"  ,B"0100011", IMMtype.S),      // sxx   rs1,rs2,imm[11:0]
    Plugin("MCC"  ,B"0010011", IMMtype.I),      // xxxi  rd,rs1,imm[11:0]
    Plugin("RCC"  ,B"0110011", IMMtype.R),      // xxx   rd,rs1,rs2 
    Plugin("MAC"  ,B"1111111", IMMtype.R),      // mac   rd,rs1,rs2
    Plugin("FCC"  ,B"0001111", IMMtype.I),      // fencex
    Plugin("CCC"  ,B"1110011", IMMtype.I)       // exx, csrxx
  )*/

  val XRES = Reg(Bool()) init(True)
  val XIDATA = Reg(Bits(32 bits))
  XIDATA := XRES ? B(0, 32 bits) | (io.HLT ? XIDATA | io.IDATA)

  //XLUI...XMAC is define in `INST`
  
  val IMM = conf.genIMM(io.IDATA, XRES, io.HLT)
  val XUIMM = IMM._1
  val XSIMM = IMM._2

  val FLUSH = Reg(Bool()) init(True)
  val RESMODE = Reg(UInt(5 bits)) init((1 << 5) - 1)

  val DPTR   = XRES ? RESMODE | U(XIDATA(11 downto 7)) // set SP_RESET when RES==1
  val S1PTR  = XIDATA(19 downto 15)
  val S2PTR  = XIDATA(24 downto 20)

  FCT3 := XIDATA(14 downto 12)
  FCT7 := XIDATA(31 downto 25)

  SIMM := XSIMM
  UIMM := XUIMM
  
  conf.decodeList.foreach{case (xinst, inst, opcode, imm) =>
    xinst := XRES?False|(io.HLT?xinst|io.IDATA(6 downto 0) === opcode)
    inst := FLUSH?False|xinst
  }

  val REG1 = Mem(UInt(32 bits), 32)
  val REG2 = Mem(UInt(32 bits), 32)

  U1REG := REG1.readAsync(U(S1PTR))
  U2REG := REG2.readAsync(U(S2PTR))
  val S1REG = S(U1REG)
  val S2REG = S(U2REG)

  val SDATA = U2REG;//TODO:in which plug?

  RESMODE := io.RES ? U((1 << 5) - 1, 5 bits) | (RESMODE.orR ? (RESMODE - 1) | U(0, 5 bits))
  XRES := RESMODE.orR
  FLUSH := XRES ? True | (io.HLT ? FLUSH | JREQ)

  REG1.write(DPTR,
    muxList(
      List((XRES, U(0, 32 bits)),
      (io.HLT, REG1.readAsync(DPTR)),
      (DPTR === 0, U(0, 32 bits))) :::
      (regWriteList :+
      (True, REG1.readAsync(DPTR)))
    ),
    True
  )

  REG2.write(DPTR,
    muxList(
      List((XRES, U(0, 32 bits)),
      (io.HLT, REG2.readAsync(DPTR)),
      (DPTR === 0, U(0, 32 bits))) :::
      (regWriteList :+
      (True, REG2.readAsync(DPTR)))
    ),
    True
  )

  NXPC := XRES ? S(0, 32 bits) | (io.HLT?NXPC|(JREQ?JVAL|(NXPC+4)))
  PC := io.HLT ? PC | NXPC

  io.DATAO := B(SDATA)
  DADDR := S(U1REG) + SIMM
  io.DADDR := B(DADDR)

  io.RW := RW
  io.DLEN := DLEN

  io.IADDR := B(NXPC)

  io.IDLE := FLUSH

  DEBUG(2) := FLUSH
  DEBUG(3) := XRES
  io.DEBUG := DEBUG

}

object coreTop extends App {
  SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(resetKind = BOOT)).generateVerilog(new daxriscv)
}
