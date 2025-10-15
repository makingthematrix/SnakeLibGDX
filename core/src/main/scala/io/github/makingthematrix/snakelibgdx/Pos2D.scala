package io.github.makingthematrix.snakelibgdx

final case class Pos2D (x: Int, y: Int) {
  def +(dir: Dir2D): Pos2D = Pos2D(x + dir.x, y + dir.y)
}

object PosList {
  def apply(positions: (Int, Int)*): List[Pos2D] =
    positions.toList.map(Pos2D.apply)
}
