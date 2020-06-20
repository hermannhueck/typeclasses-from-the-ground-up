package typeclasses

import scala.util.chaining._
import scala.util.Try
import hutil.stringformat._
import pprint._

/*
  Implement a heterogeneous triple row decoder RowDecoder[(A, B, C)].
  The members of the triple have different types.
  In our personCsv each row contains a mandatory Long (the id), a mandatory String
  (the name) and an Option[Int] (the age which might be known or unknown).
  The RowDecoder[(A, B, C)] requires 3 implicitly provided CellDecoders:
  a CellDecoder[A], a CellDecoder[B] and a CellDecoder[C] to decode the cells of a row
  into the different target types.
 */
object Step19 extends hutil.App {

  val csv =
    """|1, 2, 3
       |4, , 6
       |7, 8, 9
       |""".stripMargin

  val personsCsv = // id: Long, name: String, age: Option[Int]
    """|10000, Joe, 11
       |10001, John,
       |10002, Jack, 88
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

    implicit val longDecoder: CellDecoder[Long] =
      _.toLong

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

    implicit def heterogenousTripleRowDecoder[A, B, C](
        implicit
        decA: CellDecoder[A],
        decB: CellDecoder[B],
        decC: CellDecoder[C]
    ): RowDecoder[(A, B, C)] =
      row =>
        row
          .split(",")
          .toList
          .map(_.strip)
          .pipe { list =>
            // if last (optional) element is missing add empty String
            if (list.length >= 3) list else list :+ ""
          }
          .pipe { list =>
            require(list.length >= 3)
            (
              list(0) pipe decA.decodeCell,
              list(1) pipe decB.decodeCell,
              list(2) pipe decC.decodeCell
            )
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

  s"$dash10 parse personsCsv to List[(Long, String, Option[Int])] $dash10".magenta.println
  parseCsv[(Long, String, Option[Int])](personsCsv)
    .pipe { pprintln(_, width = 36, indent = 2) }
}
