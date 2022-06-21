package src

import spinal.core._
import spinal.lib._

case class Config(

) extends Area {
  
  this.setName("")//ctrl the signal name in verilog file

  var decodeList: List[(Bool, Bool, Bits, String)] = List()

  /**
    * example:
    * decodeAdd(LCC, B"0000011", "I")
    * decodeAdd(SCC, B"0100011", "S")
    *
    * @param inst
    * @param opcode
    * @param imm
    * @return
    */
  def decodeAdd(inst: Bool, opcode: Bits, imm: String): Bool = {
    val xinst = Reg(Bool())
    xinst.setName("X"+inst.getName)
    decodeList = decodeList :+ (xinst, inst, opcode, imm)
    xinst
  }

  def genIMM(IDATA: Bits, RES: Bool, HLT: Bool): (UInt, SInt) = {
    val XUIMM = Reg(UInt(32 bits))
    val XSIMM = Reg(SInt(32 bits))

    val orderedList = List(decodeList.filter(_._4 == "S"),
                           decodeList.filter(_._4 == "B"),
                           decodeList.filter(_._4 == "J"),
                           decodeList.filter(_._4 == "U"))

    when(RES) {
      XUIMM := 0
    }.elsewhen(HLT) {
      XUIMM := XUIMM
    }.elsewhen(orderedList(0).map(IDATA(6 downto 0) === _._3).reduce(_ || _)) {
      XUIMM := U(IMMtype.S(IDATA)).resized
    }.elsewhen(orderedList(1).map(IDATA(6 downto 0) === _._3).reduce(_ || _)) {
      XUIMM := U(IMMtype.B(IDATA)).resized
    }.elsewhen(orderedList(2).map(IDATA(6 downto 0) === _._3).reduce(_ || _)) {
      XUIMM := U(IMMtype.J(IDATA)).resized
    }.elsewhen(orderedList(3).map(IDATA(6 downto 0) === _._3).reduce(_ || _)) {
      XUIMM := U(IMMtype.U(IDATA)).resized
    }.otherwise {
      XUIMM := U(IMMtype.I(IDATA)).resized
    }

    when(RES) {
      XSIMM := 0
    }.elsewhen(HLT) {
      XSIMM := XSIMM
    }.elsewhen(orderedList(0).map(IDATA(6 downto 0) === _._3).reduce(_ || _)) {
      XSIMM := S(IMMtype.S(IDATA)).resized
    }.elsewhen(orderedList(1).map(IDATA(6 downto 0) === _._3).reduce(_ || _)) {
      XSIMM := S(IMMtype.B(IDATA)).resized
    }.elsewhen(orderedList(2).map(IDATA(6 downto 0) === _._3).reduce(_ || _)) {
      XSIMM := S(IMMtype.J(IDATA)).resized
    }.elsewhen(orderedList(3).map(IDATA(6 downto 0) === _._3).reduce(_ || _)) {
      XSIMM := S(IMMtype.U(IDATA)).resized
    }.otherwise {
      XSIMM := S(IMMtype.I(IDATA)).resized
    }

    (XUIMM, XSIMM)

  }

  var regWriteList: List[(Bool, UInt)] = List()

  def regWriteAdd(cond: Bool, data: UInt) = {
    regWriteList = regWriteList :+ (cond, data)
  }

  val DADDR = SInt(32 bits)
  val DATAI = Bits(32 bits)

  val OPCODE = Bits(7 bits)
  val FCT3 = Bits(3 bits)
  val FCT7 = Bits(7 bits)

  val SIMM = SInt(32 bits)
  val UIMM = UInt(32 bits)

  val NXPC = Reg(SInt(32 bits))
  val PC   = Reg(SInt(32 bits))
  
  val U1REG = UInt(32 bits)
  val U2REG = UInt(32 bits)
  val S1REG = S(U1REG)
  val S2REG = S(U2REG)

  val S2REGX = SInt(32 bits)
  val U2REGX = UInt(32 bits)

  val PCSIMM = SInt(32 bits)
  val JREQ = Bool()
  val JVAL = SInt(32 bits)

  val RW = Bool()
  val DLEN = Bits(3 bits)
  val DEBUG = Bits(4 bits)

}
