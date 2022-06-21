package src.Plugins

import spinal.core._
import spinal.lib._
import src._

case class MAC(implicit config: Config) extends Plug{
  import config._

  val MAC = Bool()
  
  decodeAdd(MAC, B"1111111", "R")

}
