package src.Plugins

import spinal.core._
import spinal.lib._
import src._

case class MRCC(implicit config: Config) extends Plug {

  import config._

  val MCC = Bool()
  val RCC = Bool()

  val XMCC = decodeAdd(MCC, B"0010011", "I")
  val XRCC = decodeAdd(RCC, B"0110011", "R")

  S2REGX := XMCC ? SIMM | S2REG
  U2REGX := XMCC ? UIMM | U2REG

  val RMDATA = FCT3.mux(
      (7, B(U1REG) & B(S2REGX)),
      (6, B(U1REG) | B(S2REGX)),
      (4, B(U1REG) ^ B(S2REGX)),
      (3, (U1REG < U2REGX) ? B(1, 32 bits)| B(0, 32 bits)),
      (2, (S1REG < S2REGX) ? B(1, 32 bits)| B(0, 32 bits)),
      (0, (XRCC&&FCT7(5)) ? B(U1REG-U2REG) | B(S(U1REG)+S2REGX)),
      (1, B(U1REG |<< U2REGX(4 downto 0))),
      (`default`, (FCT7(5)?B(U1REG |>> U2REGX(4 downto 0))|B(S1REG |>> U2REGX(4 downto 0))))
  )

  regWriteAdd(MCC||RCC, U(RMDATA))

}
