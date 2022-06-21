package mylib

import spinal.core._
import spinal.lib._
import spinal.core.sim._
import spinal.sim._

object Wallace extends App {
  class VecNumPimped(pimped: Seq[UInt]) extends Bundle {
    def reduceWallaceTree(levelBridge: (UInt, Int) => UInt): UInt = {
      def stage(elements: List[UInt], level: Int): UInt = {
        if(elements.length == 1) {
          levelBridge(elements.head, level)
        }else if(elements.length == 2) {
          levelBridge(elements(0) +^ elements(1), level)
        }else {
          val ret = (0 until elements.length/3)
            .map{case idx =>
              val (a,b,c) = (elements(idx*3),elements(idx*3+1),elements(idx*3+2))
              List(U(False ## (a ^ b ^ c))
              ,U(((a & b) | (c & (a ^ b))) ## False))
            }.reduce(_ ::: _) ::: (elements.drop(elements.length/3*3).map(x => U(False ## x)))
          stage(ret.map(levelBridge(_, level)), level+1)
        }
      }
      require(pimped.length > 0)
      stage(pimped.toList, 0)
    }
    def reduceWallaceTree(): UInt = {
      reduceWallaceTree((s, l) => s)
    }
  }
  class test(wide: Int) extends Component {
    val io = new Bundle {
      val x = in Vec(UInt(8 bits), wide)
      val y = out UInt(16 bits)
    }

    val x = new VecNumPimped(io.x)
    io.y := x.reduceWallaceTree().resized

    val useless = Reg(Bool()) init(False)
    useless := !useless

  }

  val wide = 9
  SimConfig.withWave.doSim(new test(wide)){dut =>
    dut.clockDomain.forkStimulus(period = 10)
    import scala.util.Random
    for(idx <- 0 until 99) {
      val input = (0 until wide).map(idx => Random.nextInt(256))
      for(i <- 0 until wide) {
        dut.io.x(i) #= input(i)
      }
      dut.clockDomain.waitRisingEdge()
      println(input.reduce(_+_) + "," + dut.io.y.toInt)
      assert(input.reduce(_+_) == dut.io.y.toInt)
    }
  }
}
