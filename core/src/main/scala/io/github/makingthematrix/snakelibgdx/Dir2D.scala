package io.github.makingthematrix.snakelibgdx

enum Dir2D (val x: Int, val y: Int) {
  case Right extends Dir2D(1, 0)
  case Left extends Dir2D(-1, 0)
  case Up extends Dir2D(0, -1)
  case Down extends Dir2D(0, 1)

  def opposite(dir: Dir2D): Boolean =
    dir match {
      case Right => this == Left
      case Left => this == Right
      case Up => this == Down
      case Down => this == Up
    }

  def rotateCounterClockwise: Dir2D =
    this match {
      case Right => Up
      case Left => Down
      case Up => Left
      case Down => Right
    }

  def rotateClockwise: Dir2D =
    this match {
      case Right => Down
      case Left => Up
      case Up => Right
      case Down => Left
    }
}

