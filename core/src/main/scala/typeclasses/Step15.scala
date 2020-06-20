package typeclasses

import scala.util.chaining._
import scala.util.Try
import hutil.stringformat._
import pprint._

/*
  Implement RowDecoder for List[Int]:
  (Our CSV again contains legal values in all cells).
  In this step we decode on the row level. Hence we provide a RowDecoder.
  The RowDecoder decodes all cells of a row (using a CellDecoder) where the
  target types of the cells do not differ. In subsequent examples
  we implement RowDecoders with different target types for each cell.
 */
object Step15 extends hutil.App {

  val csv =
    """|1, 2, 3
       |4, 5, 6
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

    implicit def intListRowDecoder(
        implicit
        cellDecoder: CellDecoder[Int]
    ): RowDecoder[List[Int]] =
      new RowDecoder[List[Int]] {
        def decodeRow(row: String): List[Int] =
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

  s"$dash10 parse csv to List[List[Int]] $dash10".magenta.println
  parseCsv[List[Int]](csv)
    .pipe { pprintln(_, width = 36, indent = 2) }
}
