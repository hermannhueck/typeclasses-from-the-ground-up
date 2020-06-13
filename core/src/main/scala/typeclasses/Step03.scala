package typeclasses

import scala.util.chaining._
import hutil.stringformat._
import pprint._

/*
  Introduce function 'decodeCell: String => Int'
 */
object Step03 extends hutil.App {

  val csv =
    """|1, 2, 3
       |4, 5, 6
       |7, 8, 9
       |""".stripMargin

  def parseCsv(csv: String)(decodeCell: String => Int): List[List[Int]] =
    csv
      .split("\n")
      .toList
      .map {
        _.split(",")
          .toList
          .map(_.strip)
          .map(decodeCell)
      }

  s"$dash10 parse csv to List[List[Int]] $dash10".magenta.println
  parseCsv(csv)(_.toInt)
    .tap { pprintln(_, width = 32, indent = 2) }
}
