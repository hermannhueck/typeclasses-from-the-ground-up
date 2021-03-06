package typeclasses

import scala.util.chaining._
import hutil.stringformat._
import pprint._

/*
  Implement implicit instances with SAM types:
  CellDecoder[A] is a SAM type (single abstract method type).
  SAM type instances can be simply implemented with a Function1.
  This makes the definition of the implicit instance even more concise.
  (Adding the annotation @FunctionalInterface avoids compiler warnings.)
 */
object Step10 extends hutil.App {

  val csv =
    """|1, 2, 3
       |4, 5, 6
       |7, 8, 9
       |""".stripMargin

  // typeclass CellDecoder
  @FunctionalInterface
  trait CellDecoder[A] {
    def decode(cell: String): A
  }

  object CellDecoder {

    // summoner
    def apply[A](
        implicit
        decoder: CellDecoder[A]
    ): CellDecoder[A] =
      decoder

    implicit val stringDecoder: CellDecoder[String] =
      identity

    implicit val intDecoder: CellDecoder[Int] =
      _.toInt
  }

  def parseCsv[A: CellDecoder](csv: String): List[List[A]] =
    csv
      .split("\n")
      .toList
      .map {
        _.split(",")
          .toList
          .map(_.strip)
          .map(CellDecoder[A].decode)
      }

  s"$dash10 parse csv to List[List[String]] $dash10".magenta.println()
  parseCsv[String](csv)
    .pipe { pprintln(_, width = 32, indent = 2) }

  s"$dash10 parse csv to List[List[Int]] $dash10".magenta.println()
  parseCsv[Int](csv)
    .pipe { pprintln(_, width = 32, indent = 2) }
}
