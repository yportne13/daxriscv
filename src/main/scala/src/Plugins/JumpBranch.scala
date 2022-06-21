package src.Plugins

import spinal.core._
import spinal.lib._
import src._

case class JumpBranch(implicit config: Config) extends Plug {
  import config._

  val JAL = Bool()
  val JALR = Bool()
  val BCC = Bool()

  decodeAdd(JAL,  B"1101111", "J")
  decodeAdd(JALR, B"1100111", "I")
  decodeAdd(BCC,  B"1100011", "B")

  val BMUX = BCC && (
    FCT3.mux(
      (4, S1REG < S2REGX),
      (5, S1REG >= S2REG),
      (6, U1REG < U2REGX),
      (7, U1REG >= U2REG),
      (0, U1REG === U(S2REGX)),
      (`default`, U1REG =/= U(S2REGX))
    )
  )

  PCSIMM := PC + SIMM
  JREQ := JAL || JALR || BMUX
  JVAL := JALR ? DADDR | PCSIMM

  regWriteAdd(JAL||JALR, U(NXPC))

}
