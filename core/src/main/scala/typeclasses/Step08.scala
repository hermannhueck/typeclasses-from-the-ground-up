package typeclasses

import scala.util.chaining._
import hutil.stringformat._
import pprint._

/*
  Use equivalent context bound instead of implicit CellDecoder parameter:
  A context bound is just syntactic sugar for an implicit parameter.
  It is semantically completely equivalent to an implicit parameter.
  But using a context bound we don't habe a name for the implicit parameter.
  That is why we use implicitly to summon the implicit instance inside parseCsv.
 */
object Step08 extends hutil.App {

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
          .map(implicitly[CellDecoder[A]].decode)
      }

  s"$dash10 parse csv to List[List[String]] $dash10".magenta.println
  parseCsv[String](csv)
    .pipe { pprintln(_, width = 32, indent = 2) }

  s"$dash10 parse csv to List[List[Int]] $dash10".magenta.println
  parseCsv[Int](csv)
    .pipe { pprintln(_, width = 32, indent = 2) }
}
