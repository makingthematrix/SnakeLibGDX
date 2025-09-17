package io.github.makingthematrix.snakelibgdx



final class Snake(val body: List[Pos2D], val snakeDir: Dir2D, val hasCoin: Boolean = false):
  def setHasCoin(value: Boolean): Snake = new Snake(body, snakeDir, value)

  def hasSelfCollision: Boolean =
    body match {
      case Nil => false // Empty snake cannot collide with itself
      case _ :: Nil => false // Single element snake cannot collide with itself
      case head :: tail => tail.contains(head) // Check if head position is in any tail segment
    }

  def changeDirection(newDir: Dir2D): Snake =
    new Snake(body, newDir, hasCoin) // Always change to new direction - validation handled at Board level

  def crawl(board: Board): Snake =
    body match {
      case Nil =>
        // Empty body - create new head at (0,0) moved by snakeDir with wrapping
        val newHead = Pos2D(0, 0)
        new Snake(List(newHead), snakeDir, false) // hasCoin is always false after crawling
      case head :: Nil =>
        // Single element - new head is current head moved by snakeDir with wrapping
        val newHead = (head + snakeDir).wrap(board.size)
        // If hasCoin is true, grow the snake by keeping the old head, otherwise just move the head
        val newBody = if (hasCoin) List(newHead, head) else List(newHead)
        new Snake(newBody, snakeDir, false) // hasCoin is always false after crawling
      case head :: tail =>
        // Multiple elements - add new head, optionally remove tail based on hasCoin
        val newHead = (head + snakeDir).wrap(board.size)
        val newBody = if (hasCoin)
          // If hasCoin is true, don't remove tail (snake grows)
          newHead :: body
        else
          // Normal crawl - remove last element
          newHead :: body.init // init removes the last element
        new Snake(newBody, snakeDir, false) // hasCoin is always false after crawling
    }

object Snake:
  def apply(): Snake = new Snake(Nil, Dir2D.Right, false)

  def apply(body: List[Pos2D], snakeDir: Dir2D = Dir2D.Right): Option[Snake] =
    if (isContinuous(body))
      // For now, default direction is Right - this could be enhanced to detect direction from body
      Some(new Snake(body, snakeDir, false))
    else
      None

  private def isContinuous(body: List[Pos2D]): Boolean =
    body match {
      case Nil => true // Empty list is considered continuous
      case _ :: Nil => true // Single element is continuous
      case _ =>
        body.zip(body.tail).forall { case (Pos2D(x1, y1), Pos2D(x2, y2)) =>
          math.abs(x2 - x1) + math.abs(y2 - y1) == 1
        }
    }

final class Board(val size: Int, private var coins: List[Pos2D] = Nil, private var _snake: Snake = Snake()){
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

  def hasSnakeSelfCollision: Boolean = _snake.hasSelfCollision

  def changeSnakeDirection(newDir: Dir2D): Boolean = {
    // Get current direction from snake
    val currentDir = _snake.snakeDir

    // Check if the new direction is opposite to the current direction (backwards move)
    val isOpposite = (currentDir, newDir) match {
      case (Dir2D.Up, Dir2D.Down) => true
      case (Dir2D.Down, Dir2D.Up) => true
      case (Dir2D.Left, Dir2D.Right) => true
      case (Dir2D.Right, Dir2D.Left) => true
      case _ => false
    }

    if (isOpposite) false // Cannot change to opposite direction
    else {
      _snake = _snake.changeDirection(newDir)
      true // Valid direction change succeeded
    }
  }

  def updateSnake(newSnake: Snake): Unit =
    _snake = newSnake

  def snakeLength: Int = _snake.body.size

  def update(): Unit = {
    _snake = _snake.crawl(this)
    if (coins.contains(snake.body.head)) {
      _snake = _snake.setHasCoin(true)
      coins = coins.filterNot(_ == snake.body.head)
    }
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

  def updateSnakeDirection(newDir: Dir2D): Boolean =
    if (!newDir.opposite(_snake.snakeDir)) {
      _snake = _snake.changeDirection(newDir)
      true
    }
    else
      false
}

object Board:
  def apply(size: Int): Board = new Board(size)
  def apply(size: Int, coins: List[Pos2D]): Board = new Board(size, coins)
  def apply(size: Int, coins: List[Pos2D], snake: Snake): Board = new Board(size, coins, snake)
  def apply(size: Int, snake: Snake): Board = new Board(size, Nil, snake)
