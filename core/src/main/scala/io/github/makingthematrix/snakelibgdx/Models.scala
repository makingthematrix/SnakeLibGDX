package io.github.makingthematrix.snakelibgdx

final class Snake(val body: List[Pos2D], val snakeDir: Dir2D){
  def changeDirection(newDir: Dir2D): Snake =
    new Snake(body, newDir) // Always change to new direction - validation handled at Board level

  // Change #1: Write code to wrap the snake around the board
  def crawl: Snake = {
    val newHead = body.head + snakeDir
    val newBody = newHead :: body.init // init removes the last element
    new Snake(newBody, snakeDir)
  }

  // Change #2: Write code to detect that the snake ate a coin (Scala icon)

  // Change #3: Detect when the snake collides with itself
}

object Snake{
  def apply(): Snake = new Snake(List(Pos2D(3, 4)), Dir2D.Right)

  def apply(body: List[Pos2D], snakeDir: Dir2D = Dir2D.Right): Option[Snake] =
    if (isContinuous(body))
      // For now, default direction is Right - this could be enhanced to detect direction from body
      Some(new Snake(body, snakeDir))
    else
      None

  // Change #4: Suggest improvements to the isContinuous method
  private def isContinuous(body: List[Pos2D]): Boolean =
    body match {
      case Nil => false // Empty list is not considered continuous
      case _ :: Nil => true // Single element is continuous
      case _ =>
        body.zip(body.tail).forall { case (Pos2D(x1, y1), Pos2D(x2, y2)) =>
          math.abs(x2 - x1) + math.abs(y2 - y1) == 1
        }
    }
}

final class Board(val size: Int,
                  private var coins: List[Pos2D] = Nil,
                  private var _snake: Snake = Snake()) {
  def coinsPositions: List[Pos2D] = coins

  def snake: Snake = _snake

  def coinsNumber: Int = coins.length

  def emptyTiles: List[Pos2D] = {
    val snakePositions = _snake.body.toSet
    val coinPositions = coins.toSet
    val occupiedPositions = snakePositions ++ coinPositions

    (for {
      x <- 0 until size
      y <- 0 until size
      pos = Pos2D(x, y) if !occupiedPositions.contains(pos)
    } yield pos).toList
  }

  def changeSnakeDirection(newDir: Dir2D): Boolean =
    // Check if the new direction is opposite to the current direction (backwards move)
    if (_snake.snakeDir.opposite(newDir)) false // Cannot change to opposite direction
    else {
      _snake = _snake.changeDirection(newDir)
      true // Valid direction change succeeded
    }

  def update(): Unit = {
    _snake = _snake.crawl
  }

  private lazy val allPositions =
    for {
      x <- 0 until size
      y <- 0 until size
    } yield Pos2D(x, y)

  def getEmptyTilePositions: List[Pos2D] = {
    val occupiedPositions = _snake.body.toSet ++ coins.toSet
    allPositions.filterNot(occupiedPositions.contains).toList
  }

  def addCoin(position: Pos2D): Unit =
    if (!coins.contains(position) && !_snake.body.contains(position))
      coins = position :: coins
}

object Board{
  def apply(size: Int): Board = new Board(size)

  def apply(size: Int, coins: List[Pos2D]): Board = new Board(size, coins)

  def apply(size: Int, coins: List[Pos2D], snake: Snake): Board = new Board(size, coins, snake)

  def apply(size: Int, snake: Snake): Board = new Board(size, Nil, snake)
}
