package typeclasses

import scala.util.chaining._
import hutil.stringformat._
import pprint._

/*
  Parsing a CSV String to a List[List[String]]:
  The cell type String is NOT converted to a different type.
 */
object Step01 extends hutil.App {

  val csv =
    """|1, 2, 3
       |4, 5, 6
       |7, 8, 9
       |""".stripMargin

  def parseCsv(csv: String): List[List[String]] =
    csv
      .split("\n")
      .toList
      .map {
        _.split(",")
          .toList
          .map(_.strip)
      }

  s"$dash10 parse csv to List[List[String]] $dash10".magenta.println()
  parseCsv(csv)
    .tap { pprintln(_, width = 32, indent = 2) }
}
