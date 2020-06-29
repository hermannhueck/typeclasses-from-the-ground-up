package typeclasses

import scala.util.chaining._
import hutil.stringformat._
import pprint._

/*
  Introduce parametric polymorphism for parseCsv and function 'decodeCell: String => A':
  A cell value must not necessarily be parsed to Int. The target type of the
  cell conversion might be any type A. We parameterize parseCsv with a type parameter A.
 */
object Step04 extends hutil.App {

  val csv =
    """|1, 2, 3
       |4, 5, 6
       |7, 8, 9
       |""".stripMargin

  def parseCsv[A](csv: String)(decodeCell: String => A): List[List[A]] =
    csv
      .split("\n")
      .toList
      .map {
        _.split(",")
          .toList
          .map(_.strip)
          .map(decodeCell)
      }

  s"$dash10 parse csv to List[List[String]] $dash10".magenta.println()
  parseCsv(csv)(identity)
    .pipe { pprintln(_, width = 32, indent = 2) }

  s"$dash10 parse csv to List[List[Int]] $dash10".magenta.println()
  parseCsv(csv)(_.toInt)
    .pipe { pprintln(_, width = 32, indent = 2) }
}
