package src.Plugins

import spinal.core._
import spinal.lib._
import src._

case class LSCC(implicit config: Config) extends Plug {
  import config._

  val LCC = Bool()
  val SCC = Bool()

  decodeAdd(LCC, B"0000011", "I")
  decodeAdd(SCC, B"0100011", "S")

  RW := !SCC

  val LDATA = (FCT3(1 downto 0) === 0) ? (((FCT3(2) === False && DATAI(7)) ? Bits(24 bits).getAllTrue | B(0, 24 bits)) ## DATAI(7 downto 0)) |
             ((FCT3(1 downto 0) === 1) ? (((FCT3(2) === False && DATAI(15)) ? Bits(16 bits).getAllTrue | B(0, 16 bits)) ## DATAI(15 downto 0)) |
                                      DATAI)

  regWriteAdd(LCC, U(LDATA))

  DLEN(0) := (SCC||LCC)&&(FCT3(1 downto 0) === 0)
  DLEN(1) := (SCC||LCC)&&(FCT3(1 downto 0) === 1)
  DLEN(2) := (SCC||LCC)&&(FCT3(1 downto 0) === 2)

  DEBUG(0) := LCC
  DEBUG(1) := SCC

}
