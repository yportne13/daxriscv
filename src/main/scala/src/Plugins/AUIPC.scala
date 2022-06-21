package src.Plugins

import spinal.core._
import spinal.lib._
import src._

case class AUIPC(implicit config: Config) extends Plug {
  import config._

  val AUIPC = Bool()

  decodeAdd(AUIPC, B"0010111", "U")

  regWriteAdd(AUIPC, U(PCSIMM))

}
