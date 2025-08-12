package io.github.makingthematrix.snakelibgdx

import scala.collection.mutable.ArrayBuffer

enum Tile:
  case Empty
  case SnakeHead
  case SnakeBody
  case SnakeTail
  case Coin

final class Board(val size: Int, private val arr: ArrayBuffer[Tile])

object Board:
  def apply(size: Int): Board =
    new Board(size, ArrayBuffer.fill(size * size)(Tile.Empty))