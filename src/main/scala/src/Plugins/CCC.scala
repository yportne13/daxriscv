package src.Plugins

import spinal.core._
import spinal.lib._
import src._

case class CCC(implicit config: Config) extends Plug {
  import config._

  val CCC = Bool()
  
  decodeAdd(CCC, B"1110011", "I")

}
