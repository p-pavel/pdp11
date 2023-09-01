package com.perikov.pdp11

object ISAPrinter extends ISA:
  import cats.Show
  import cats.syntax.show.*

  private enum modes:
    case direct, inc, dec

  opaque type Mode = ModeImpl
  private sealed trait ModeImpl:
    def deferred: ModeImpl
    def defer: Boolean
    def show: String
    def deferSym = if defer then "@" else ""

  given Show[Mode] = _.show

  private case class Simple(reg: Register, mod: modes, defer: Boolean = false)
      extends ModeImpl:
    override def deferred: ModeImpl = copy(defer = true)
    override def show: String =
      val p = deferSym
      mod match
        case modes.dec    => show"$p-($reg)"
        case modes.inc    => show"$p($reg)+"
        case modes.direct => 
          if defer 
          then show"($reg)" 
          else Show[Register].show(reg) // prevent loop due to conversion regisger -> mode

  private case class Indexed(reg: Register, offset: Short, defer: Boolean = false)
      extends ModeImpl:
    override def deferred: ModeImpl = copy(defer = true)
    override def show: String =
      val p = deferSym
      show"$p$offset($reg)"

  type BasicMode = Mode

  extension (r: Register)
    def direct: BasicMode = Simple(r, modes.direct)
    def inc: BasicMode = Simple(r, modes.inc)
    def dec: BasicMode = Simple(r, modes.dec)
    def index(offset: Short): BasicMode = Indexed(r, offset)

  extension (mode: BasicMode) def defer: Mode= mode.deferred

  private enum Cmd:
    case RegArgs(name: String, byte: Boolean, args: Seq[Mode])
    case Print(s: String)
    case Branch(name: String, offset: Byte)

  given Show[Command] =
    case Cmd.RegArgs(name, byte, args) =>
      val b = if byte then "B" else ""
      s"$name$b\t" + args.map(_.show).mkString(", ")

    case Cmd.Print(s)             => s
    case Cmd.Branch(name, offset) => show"$name\t$offset"


  private def cmd(name: String, args: Mode*): SizedCommand =
    Cmd.RegArgs(name, false, args)
  opaque type Command = Cmd
  opaque type SizedCommand <: Command = Cmd.RegArgs
  extension (s: SizedCommand) def b: Command = s.copy(byte = true)

  type TwoArgSized = (Mode, Mode) => SizedCommand
  type TwoArg = (Mode, Mode) => Command
  val mov = cmd("MOV", _, _)
  val cmp = cmd("CMP", _, _)
  val bit = cmd("BIT", _, _)
  val bic = cmd("BIC", _, _)
  val bis = cmd("BIS", _, _)
  val add = cmd("ADD", _, _)
  val sub = cmd("SUB", _, _)
  // TODO: mul,div, ash, ashc

  type SingleArg = Mode=> Command
  type SingleArgSized = Mode=> SizedCommand
  val jmp = cmd("JMP", _)
  val swab = cmd("SWAB", _)
  val mtps = cmd("MTPS", _)
  val mfps = cmd("MFPS", _)
  val sxt = cmd("SXT", _)
  val clr = cmd("CLR", _)
  val com = cmd("COM", _)
  val inc = cmd("INC", _)
  val dec = cmd("DEC", _)
  val neg = cmd("NEG", _)
  val adc = cmd("ADC", _)
  val sbc = cmd("SBC", _)
  val tst = cmd("TST", _)
  val ror = cmd("ROR", _)
  val rol = cmd("ROL", _)
  val asr = cmd("ASR", _)
  val asl = cmd("ASL", _)

  val br = Cmd.Branch("BR", _)
  val bne = Cmd.Branch("BNE", _)
  val beq = Cmd.Branch("BEQ", _)
  val bge = Cmd.Branch("BGE", _)
  val blt = Cmd.Branch("BLT", _)
  val bgt = Cmd.Branch("BGT", _)
  val ble = Cmd.Branch("BLE", _)
  val bpl = Cmd.Branch("BPL", _)
  val bmi = Cmd.Branch("BMI", _)
  val bhi = Cmd.Branch("BNI", _)
  val blos = Cmd.Branch("BLOS", _)
  val bvc = Cmd.Branch("BVC", _)
  val bvs = Cmd.Branch("BVS", _)
  val bcc = Cmd.Branch("BCC", _)
  val bcs = Cmd.Branch("BCS", _)
  def sob(r: Register, off: Offset) = Cmd.Print(show"SOB\t$r, $off")

  def jsr(r: Register, addr: Mode) = Cmd.Print(show"JSR\t$r, $addr")
  def rts(r: Register) = Cmd.Print(show"RTS\t$r")
  def mark(n: Nat[31]) = Cmd.Print(show"MARK\t$n")

  def emt(v: VectorId) = Cmd.Print(show"EMT\t $v")
  def trap(v: VectorId) = Cmd.Print(show"TRAP\t$v")

  val rti = Cmd.Print("RTI")
  val bpt = Cmd.Print("BPT")
  val iot = Cmd.Print("IOT")
  val rtt = Cmd.Print("RTT")
  val halt = Cmd.Print("HALT")
  val Wait = Cmd.Print("WAIT")
  val reset = Cmd.Print("RESET")

  def clearFlags(f: Flags): Command = ???
  def setFlags(f: Flags): Command = ???

@main
def testPrintCommands =
  import cats.*
  import cats.syntax.show.*
  import ISAPrinter.{*, given}
  val cmd: Command = mov(R0(123).defer, R1)
  println(cmd.show)
