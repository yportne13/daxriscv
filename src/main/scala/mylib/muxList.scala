package mylib

import spinal.core._
import spinal.lib._

object muxList {
  def apply[T <: Data](list: List[(Bool, T)]): T = {
    val ret = list.reverse.drop(1).scanLeft(list.last._2)((b,a) => a._1?a._2|b)
    ret.last
  }
}
