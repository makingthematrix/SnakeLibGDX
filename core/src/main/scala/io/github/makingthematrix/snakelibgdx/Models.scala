package io.github.makingthematrix.snakelibgdx

import scala.collection.mutable.ArrayBuffer

enum Tile:
  case Empty
  case SnakeHead
  case SnakeBody
  case SnakeTail
  case Coin

enum Orientation:
  case Left, Right, Up, Down, None
  
final case class Token(tile: Tile, orientation: Orientation, pos: (x: Int, y: Int))

final class Snake(tokens: List[Token])

final class Board(val size: Int, private val arr: ArrayBuffer[(Tile, Orientation)])

object Board:
  def apply(size: Int): Board =
    new Board(size, ArrayBuffer.fill(size * size)(Tile.Empty, Orientation.None))