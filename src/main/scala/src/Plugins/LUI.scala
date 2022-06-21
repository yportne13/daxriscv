package src.Plugins

import spinal.core._
import spinal.lib._
import src._

case class LUI(implicit config: Config) extends Plug {
  import config._

  val LUI = Bool()

  decodeAdd(LUI, B"0110111", "U")

  regWriteAdd(LUI, U(SIMM))

}
