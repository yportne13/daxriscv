# daxriscv

一个用 spinalHDL 写的，非常简陋的 riscv cpu core。电路设计完全参照 [darkriscv](https://github.com/darklife/darkriscv)，代码结构参考了 [vexriscv](https://github.com/SpinalHDL/VexRiscv)。

这个代码我没仿真过，也不知道对不对。

`src/main/verilog` 内的代码是 darkriscv 的代码，我把参数配置部分的代码全删了按照最简单的来，这样代码看起来会短一点。理论上我的代码所描述的电路应该与这个代码完全一致，但到底是不是我也懒得仔细检查。

`src/main/mylib` 当中，`muxList.scala` 是一个多路选择器，我以为 spinal 的 lib 里有但好像没有，自己先随手糊一个。`wallace.scala` 和这个项目没啥关系，是个 wallace tree，懒得删了。

`src/main/src/daxriscv.scala` 是顶层。略微参考了一点 vexriscv 的设计，所有指令都以独立的 plugin 的形式提供。理论上添加新指令只需要单独为新指令设计一个 plugin 即可，但本人学艺不精，可能顶层有设计不好的地方，可能加新指令还是要东改西改。大部分内容就是 verilog 代码的简单翻译。

`src/main/src/config.scala` 相当于是 vexriscv 中 config pipeline stage service 等几个东西的简陋合并版本。每个 plugin 会调用 decodeAdd 把自己这个 plugin 所要描述的指令加到 decodeList 中，之后顶层就能根据这个 list 生成译码得到使能和立即数的电路。其中我代码里生成立即数 genIMM 这玩意是按 darkriscv 写的，但是如果你有时间可以看看 vexriscv 的设计，能够自动化简出最优的电路。然后如果指令涉及到寄存器读写就调 regWriteAdd 把使能和要写入的数据加上。后面就是定义一些 plugin 间以及 plugin 与顶层间会交互的信号。

`src/main/src/Plugins` 就是各指令的实现了，对照着 darkriscv 的代码很容易看懂。可能 plugin 的划分不太合理，懒得仔细琢磨了。
