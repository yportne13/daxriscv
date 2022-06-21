package src

import spinal.core._
import spinal.lib._

object IMMtype {
  def R: Bits => Bits = null
  def I: Bits => Bits = i => i(31 downto 20)
  def S: Bits => Bits = i => i(31 downto 25) ## i(11 downto 7)
  def B: Bits => Bits = i => i(31) ## i(7) ## i(30 downto 25) ## i(11 downto 8) ## False
  def U: Bits => Bits = i => i(31 downto 12) ## spinal.core.B(0, 12 bits)
  def J: Bits => Bits = i => i(31) ## i(19 downto 12) ## i(20) ## i(30 downto 21) ## False
}

abstract class Plug(
  implicit config: Config
) extends Area {
  import config._

  this.setName("")

}


