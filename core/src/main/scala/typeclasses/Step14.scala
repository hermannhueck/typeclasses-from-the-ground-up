package typeclasses

import scala.util.chaining._
import scala.util.Try
import hutil.stringformat._
import pprint._

/*
  Use context bound and summoner to implement equivalent CellDecoder[Option[A]]
 */
object Step14 extends hutil.App {

  val csv =
    """|1, 2, 3
       |4, , 6
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

    implicit def optionDecoder[A: CellDecoder]: CellDecoder[Option[A]] =
      cell =>
        Try {
          CellDecoder[A].decode(cell)
        }.toOption
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

  s"$dash10 parse csv to List[List[Option[Int]]] $dash10".magenta.println()
  parseCsv[Option[Int]](csv)
    .pipe { pprintln(_, width = 36, indent = 2) }
}
