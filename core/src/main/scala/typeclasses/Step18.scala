package typeclasses

import scala.util.chaining._
import scala.util.Try
import hutil.stringformat._
import pprint._

/*
  Implement a homogeneous triple row decoder RowDecoder[(C, C, C)].
  All three members of the triple have the same type.
  In the previous example each row was decoded to a List of C.
  Here we decode each row into a triple of values of type C.
 */
object Step18 extends hutil.App {

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
      row =>
        row
          .split(",")
          .toList
          .map(_.strip)
          .map(cellDecoder.decodeCell)

    implicit def homogenousTripleRowDecoder[C](
        implicit
        listRowDecoder: RowDecoder[List[C]]
    ): RowDecoder[(C, C, C)] =
      row =>
        listRowDecoder.decodeRow(row) pipe { list =>
          require(list.length >= 3)
          (list(0), list(1), list(2))
        }
  }

  def parseCsv[A: RowDecoder](csv: String): List[A] =
    csv
      .split("\n")
      .toList
      .map(RowDecoder[A].decodeRow)

  s"$dash10 parse csv to List[(String, String, String)] $dash10".magenta.println
  parseCsv[(String, String, String)](csv)
    .pipe { pprintln(_, width = 32, indent = 2) }

  s"$dash10 parse csv to List[(Option[Int], Option[Int], Option[Int])] $dash10".magenta.println
  parseCsv[(Option[Int], Option[Int], Option[Int])](csv)
    .pipe { pprintln(_, width = 36, indent = 2) }
}
