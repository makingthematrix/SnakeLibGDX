package io.github.makingthematrix.snakelibgdx

enum Tile:
  case Empty
  case SnakeHead
  case SnakeBody
  case SnakeTail
  case Coin

final case class Token(tile: Tile, pos: (x: Int, y: Int))

final class Snake(tokens: List[Token])

final class Board(val size: Int, private var coins: List[(x: Int, y: Int)]):
  def coinsPositions: List[(x: Int, y: Int)] = coins

object Board:
  def apply(size: Int): Board = new Board(size, Nil)
