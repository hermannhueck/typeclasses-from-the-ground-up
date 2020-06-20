package typeclasses

import scala.util.chaining._
import hutil.stringformat._
import pprint._

/*
  Parsing a CSV String to a List[List[Int]]:
  The cell type String is converted to Int
  using String#toInt for the conversion of each cell.
 */
object Step02 extends hutil.App {

  val csv =
    """|1, 2, 3
       |4, 5, 6
       |7, 8, 9
       |""".stripMargin

  def parseCsv(csv: String): List[List[Int]] =
    csv
      .split("\n")
      .toList
      .map {
        _.split(",")
          .toList
          .map(_.strip)
          .map(_.toInt)
      }

  s"$dash10 parse csv to List[List[Int]] $dash10".magenta.println
  parseCsv(csv)
    .tap { pprintln(_, width = 32, indent = 2) }
}
