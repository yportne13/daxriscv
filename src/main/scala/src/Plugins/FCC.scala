package src.Plugins

import spinal.core._
import spinal.lib._
import src._

case class FCC(implicit config: Config) extends Plug {
  import config._

  val FCC = Bool()
  
  decodeAdd(FCC, B"0001111", "I")

}
