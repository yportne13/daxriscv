package analyse

import spinal.core._
import spinal.lib._
import src.Plugins._
import src._

class IMM extends Component {
  val io = new Bundle {
    val RES = in Bool()
    val HLT = in Bool()

    val IDATA = in Bits(32 bits)

    //val UIMM = out UInt(32 bits)
    val SIMM = out SInt(32 bits)

  }

  implicit val conf = Config()

  val plugins = List(
    LUI(),
    AUIPC(),
    MRCC(),
    JumpBranch(),
    LSCC(),
    MAC(),
    FCC(),
    CCC()
  )

  //XLUI...XMAC is define in `INST`
  
  val IMM = conf.genIMM(io.IDATA, io.RES, io.HLT)
  val XUIMM = IMM._1
  val XSIMM = IMM._2

  //io.UIMM := XUIMM
  io.SIMM := XSIMM

  conf.DEBUG(3 downto 2) := 0

}

object analyseTop extends App {
  SpinalVerilog(new IMM)
}
