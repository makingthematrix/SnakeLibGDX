package io.github.makingthematrix.snakelibgdx

final case class Pos2D (x: Int, y: Int) {
  def +(dir: Dir2D): Pos2D = Pos2D(x + dir.x, y + dir.y)
  def wrap(size: Int): Pos2D = {
    val x1 = if (x < 0) size - 1 else if (x >= size) 0 else x
    val x2 = if (y < 0) size - 1 else if (y >= size) 0 else y
    Pos2D(x1, x2)
  }
}

object PosList {
  def apply(positions: (Int, Int)*): List[Pos2D] =
    positions.toList.map(Pos2D.apply)
}
