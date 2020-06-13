package typeclasses

import scala.util.chaining._
import scala.util.Try
import hutil.stringformat._
import pprint._

/*
  Now we can decode rows with missing values again implicitly using
  RowDecoder[List[C]] and CellDecoder[Option[C]] and CellDecoder[Int]
 */
object Step17 extends hutil.App {

  val csv =
    """|1, 2, 3
       |4, , 6
       |7, 8, 9
       |""".stripMargin

  // typeclass CellDecoder
  @FunctionalInterface
  trait CellDecoder[C] {
    def decodeCell(cell: String): C
  }

  object CellDecoder {

    // summoner
    def apply[C](
        implicit
        decoder: CellDecoder[C]
    ): CellDecoder[C] =
      decoder

    implicit val stringDecoder: CellDecoder[String] =
      identity

    implicit val intDecoder: CellDecoder[Int] =
      _.toInt

    implicit def optionDecoder[C: CellDecoder]: CellDecoder[Option[C]] =
      cell =>
        Try {
          CellDecoder[C].decodeCell(cell)
        }.toOption
  }

  // typeclass RowDecoder
  @FunctionalInterface
  trait RowDecoder[R] {
    def decodeRow(row: String): R
  }

  object RowDecoder {

    // summoner
    def apply[A](
        implicit
        decoder: RowDecoder[A]
    ): RowDecoder[A] =
      decoder

    implicit def listRowDecoder[C](
        implicit
        cellDecoder: CellDecoder[C]
    ): RowDecoder[List[C]] =
      new RowDecoder[List[C]] {
        def decodeRow(row: String): List[C] =
          row
            .split(",")
            .toList
            .map(_.strip)
            .map(cellDecoder.decodeCell)
      }
  }

  def parseCsv[A: RowDecoder](csv: String): List[A] =
    csv
      .split("\n")
      .toList
      .map(RowDecoder[A].decodeRow)

  s"$dash10 parse csv to List[List[String]] $dash10".magenta.println
  parseCsv[List[String]](csv)
    .pipe { pprintln(_, width = 32, indent = 2) }

  s"$dash10 parse csv to List[List[Option[Int]]] $dash10".magenta.println
  parseCsv[List[Option[Int]]](csv)
    .pipe { pprintln(_, width = 36, indent = 2) }
}
