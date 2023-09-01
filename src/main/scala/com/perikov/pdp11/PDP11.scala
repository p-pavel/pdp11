package com.perikov.pdp11
// import compiletime.ops.int.S

// type Nat[n <: Int] =
//   n match
//     case 0    => 0
//     case S[n] => S[n] | Nat[n]

type Nat[n <: Int] = Int

type Octal = Nat[7]



trait ISA:
  export Registers.*
  type Mode
  type BasicMode <: Mode

  given Conversion[Register, Mode] = _.direct

  extension (r: Register)
    def direct: BasicMode
    def inc: BasicMode
    def dec: BasicMode
    def index(offset: Short): BasicMode
    inline def apply(offset: Short) = r.index(offset)

  extension (mode: BasicMode) def defer: Mode

  type Command
  type SizedCommand <: Command
  extension (s: SizedCommand) def b: Command

  type TwoArgSized = (Mode, Mode) => SizedCommand
  type TwoArg = (Mode, Mode) => Command
  val mov, cmp, bit, bic, bis: TwoArgSized
  val add, sub: TwoArg
  // TODO: mul,div, ash, ashc

  type SingleArg = Mode => Command
  type SingleArgSized = Mode => SizedCommand
  val jmp, swab, mtps, mfps, sxt: SingleArg
  val clr, com, inc, dec, neg, adc, sbc, tst, ror, rol, asr, asl: SingleArg

  type Offset = Byte
  type Branch = Offset => Command
  val br, bne, beq, bge, blt, bgt, ble, bpl, bmi, bhi, blos, bvc, bvs, bcc,
  bcs: Branch
  def sob(r: Register, off: Offset): Command

  def jsr(r: Register, addr: Mode): Command
  def rts(r: Register): Command
  def mark(n: Nat[31]): Command

  type VectorId = Nat[255]
  def emt(v: VectorId): Command
  def trap(v: VectorId): Command

  val rti, bpt, iot, rtt, halt, Wait, reset: Command // TODO: wait?

  def clearFlags(f: Flags): Command
  def setFlags(f: Flags): Command
