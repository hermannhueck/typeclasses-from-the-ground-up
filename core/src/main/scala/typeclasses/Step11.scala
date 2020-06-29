package typeclasses

import scala.util.chaining._
import scala.util.Try
import hutil.stringformat._
import pprint._

/*
  Delete a value in the 2nd column of the CSV.
  'parseCsv' still works with CellDecoder[String]. A cell contains an empty String in this case.
  'parseCsv' does not work with CellDecoder[Int]. An empty String cannot be converted to an Int.
  The program throws a NumberformatException when using the CellDecoder[Int].
 */
object Step11 extends hutil.App {

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

  s"$dash10 parse csv to List[List[Int]] --> throws NumberFormatException $dash10".magenta.println()
  Try {
    parseCsv[Int](csv)
      .pipe { pprintln(_, width = 32, indent = 2) }
  }.fold(ex => ex.toString, value => value.toString)
    .pipe(println)
}
