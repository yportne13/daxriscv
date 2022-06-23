package mylib

import spinal.core._
import spinal.lib._

/**
  * in verilog you may need
  * ```
  * output <= a ? sigA :
              b ? sigB :
              c ? sigC :
                  sigD
  * ```
  * use this function you can do like this:
  * ```
  * output := muxList(
  *       (a, sigA),
  *       (b, sigB),
  *       (c, sigC),
  *       (True, sigD)
  * )
  * ```
  */
object muxList {
  def apply[T <: Data](list: List[(Bool, T)]): T = {
    list.reverse.drop(1).scanLeft(list.last._2)((b,a) => a._1?a._2|b).last
  }
  def apply[T <: Data](list: (Bool, T) *): T = {
    apply(list.toList)
  }
}
