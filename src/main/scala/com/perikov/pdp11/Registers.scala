package com.perikov.pdp11

object Registers:
  type Flags = Nat[15]
  val N: Flags = 8
  val Z: Flags = 4
  val V: Flags = 2
  val C: Flags = 1
  extension (f: Flags)
    inline def |(g: Flags) = f | g
    inline def &(g: Flags) = f & g
    inline def ^(g: Flags) = f ^ g
    inline def ~ = ~f

  extension (r: Register) inline def toOctal = r
  opaque type Register = Octal

  def r(i: Octal): Register = i
  val R0: Register = r(0)
  val R1: Register = r(1)
  val R2: Register = r(2)
  val R3: Register = r(3)
  val R4: Register = r(4)
  val R5: Register = r(5)
  val R6: Register = r(6)
  val R7: Register = r(7)
  inline def PC = R7
  inline def SP = R6

  import cats.Show
  given Show[Register] = "R" + _.toOctal