package typeclasses

import scala.util.chaining._
import hutil.stringformat._
import pprint._

/*
  Provide summoner inside CellDecoder companion object
 */
object Step09 extends hutil.App {

  val csv =
    """|1, 2, 3
       |4, 5, 6
       |7, 8, 9
       |""".stripMargin

  // typeclass CellDecoder
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

    implicit val stringDecoder: CellDecoder[String] = new CellDecoder[String] {
      def decode(cell: String): String = cell
    }

    implicit val intDecoder: CellDecoder[Int] = new CellDecoder[Int] {
      def decode(cell: String): Int = cell.toInt
    }
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

  s"$dash10 parse csv to List[List[String]] $dash10".magenta.println
  parseCsv[String](csv)
    .pipe { pprintln(_, width = 32, indent = 2) }

  s"$dash10 parse csv to List[List[Int]] $dash10".magenta.println
  parseCsv[Int](csv)
    .pipe { pprintln(_, width = 32, indent = 2) }
}
