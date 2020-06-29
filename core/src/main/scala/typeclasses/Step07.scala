package typeclasses

import scala.util.chaining._
import hutil.stringformat._
import pprint._

/*
  Move implicit CellDecoder instances inside CellDecoder companion object.
  In the previous example the implicit instances were defined in local scope.
  Defining the implicit instances in the companion object of the type class
  (or in the companion object of the target type) provides the in the implicit scope.
  Defined in implicit scope the instances are found by the compiler without extra
  import statement. Implicit scope is weaker than local scope. Hence these instances
  can be overridden be defining another instance of the same type in local scope.
 */
object Step07 extends hutil.App {

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

  def parseCsv[A](csv: String)(
      implicit
      decoder: CellDecoder[A]
  ): List[List[A]] =
    csv
      .split("\n")
      .toList
      .map {
        _.split(",")
          .toList
          .map(_.strip)
          .map(decoder.decode)
      }

  s"$dash10 parse csv to List[List[String]] $dash10".magenta.println()
  parseCsv[String](csv)
    .pipe { pprintln(_, width = 32, indent = 2) }

  s"$dash10 parse csv to List[List[Int]] $dash10".magenta.println()
  parseCsv[Int](csv)
    .pipe { pprintln(_, width = 32, indent = 2) }
}
