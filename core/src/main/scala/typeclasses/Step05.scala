package typeclasses

import scala.util.chaining._
import hutil.stringformat._
import pprint._

/*
  Introduce typeclass 'CellDecoder'
 */
object Step05 extends hutil.App {

  val csv =
    """|1, 2, 3
       |4, 5, 6
       |7, 8, 9
       |""".stripMargin

  // typeclass CellDecoder
  trait CellDecoder[A] {
    def decode(cell: String): A
  }

  val stringDecoder: CellDecoder[String] = new CellDecoder[String] {
    def decode(cell: String): String = cell
  }

  val intDecoder: CellDecoder[Int] = new CellDecoder[Int] {
    def decode(cell: String): Int = cell.toInt
  }

  def parseCsv[A](csv: String)(decoder: CellDecoder[A]): List[List[A]] =
    csv
      .split("\n")
      .toList
      .map {
        _.split(",")
          .toList
          .map(_.strip)
          .map(decoder.decode)
      }

  s"$dash10 parse csv to List[List[String]] $dash10".magenta.println
  parseCsv(csv)(stringDecoder)
    .tap { pprintln(_, width = 32, indent = 2) }

  s"$dash10 parse csv to List[List[Int]] $dash10".magenta.println
  parseCsv(csv)(intDecoder)
    .tap { pprintln(_, width = 32, indent = 2) }
}
