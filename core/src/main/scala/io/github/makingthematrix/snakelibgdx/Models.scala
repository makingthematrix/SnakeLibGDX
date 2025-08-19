package io.github.makingthematrix.snakelibgdx

enum SnakeDir (val x: Int, val y: Int):
  case Up extends SnakeDir(0, -1)
  case Right extends SnakeDir(1, 0)
  case Down extends SnakeDir(0, 1)
  case Left extends SnakeDir(-1, 0)

  def opposite(other: SnakeDir): Boolean =
    (this, other) match
      case (Up, Down) | (Down, Up) | (Left, Right) | (Right, Left) => true
      case _ => false

final class Snake(val body: List[(x: Int, y: Int)], val snakeDir: SnakeDir, val hasCoin: Boolean = false):
  def setHasCoin(value: Boolean): Snake = new Snake(body, snakeDir, value)

  def hasSelfCollision: Boolean =
    body match
      case Nil => false // Empty snake cannot collide with itself
      case _ :: Nil => false // Single element snake cannot collide with itself
      case head :: tail => tail.contains(head) // Check if head position is in any tail segment

  def changeDirection(newDir: SnakeDir): Snake =
    new Snake(body, newDir, hasCoin) // Always change to new direction - validation handled at Board level

  def crawl(board: Board): Snake =
    body match
      case Nil =>
        // Empty body - create new head at (0,0) moved by snakeDir with wrapping
        val newHead = wrapCoordinate(snakeDir.x, snakeDir.y, board.size)
        new Snake(List(newHead), snakeDir, false) // hasCoin is always false after crawling
      case head :: Nil =>
        // Single element - new head is current head moved by snakeDir with wrapping
        val newHead = wrapCoordinate(head.x + snakeDir.x, head.y + snakeDir.y, board.size)
        val newBody = if hasCoin then
          // If hasCoin is true, grow the snake by keeping the old head
          List(newHead, head)
        else
          // Normal single element - just move the head
          List(newHead)
        new Snake(newBody, snakeDir, false) // hasCoin is always false after crawling
      case head :: tail =>
        // Multiple elements - add new head, optionally remove tail based on hasCoin
        val newHead = wrapCoordinate(head.x + snakeDir.x, head.y + snakeDir.y, board.size)
        val newBody = if hasCoin then
          // If hasCoin is true, don't remove tail (snake grows)
          newHead :: body
        else
          // Normal crawl - remove last element
          newHead :: body.init // init removes the last element
        new Snake(newBody, snakeDir, false) // hasCoin is always false after crawling

  private def wrapCoordinate(x: Int, y: Int, boardSize: Int): (Int, Int) =
    val wrappedX = if x == -1 then boardSize - 1 else if x == boardSize then 0 else x
    val wrappedY = if y == -1 then boardSize - 1 else if y == boardSize then 0 else y
    (wrappedX, wrappedY)

object Snake:
  def apply(): Snake = new Snake(Nil, SnakeDir.Right, false)

  def apply(body: List[(x: Int, y: Int)], snakeDir: SnakeDir = SnakeDir.Right): Option[Snake] =
    if isContinuous(body) then
      // For now, default direction is Right - this could be enhanced to detect direction from body
      Some(new Snake(body, snakeDir, false))
    else
      None

  private def isContinuous(body: List[(x: Int, y: Int)]): Boolean =
    body match
      case Nil => true // Empty list is considered continuous
      case _ :: Nil => true // Single element is continuous
      case _ =>
        body.zip(body.tail).forall { case ((x1, y1), (x2, y2)) =>
          math.abs(x2 - x1) + math.abs(y2 - y1) == 1
        }

final class Board(val size: Int, private var coins: List[(x: Int, y: Int)] = Nil, private var _snake: Snake = Snake()):
  def coinsPositions: List[(x: Int, y: Int)] = coins
  def snake: Snake = _snake
  def coinsNumber: Int = coins.length

  def emptyTiles: List[(x: Int, y: Int)] =
    val snakePositions = _snake.body.toSet
    val coinPositions = coins.toSet
    val occupiedPositions = snakePositions ++ coinPositions

    (for
      x <- 0 until size
      y <- 0 until size
      pos = (x, y)
      if !occupiedPositions.contains(pos)
    yield pos).toList

  def hasSnakeSelfCollision: Boolean = _snake.hasSelfCollision

  def changeSnakeDirection(newDir: SnakeDir): Boolean =
    // Get current direction from snake
    val currentDir = _snake.snakeDir

    // Check if the new direction is opposite to the current direction (backwards move)
    val isOpposite = (currentDir, newDir) match
      case (SnakeDir.Up, SnakeDir.Down) => true
      case (SnakeDir.Down, SnakeDir.Up) => true
      case (SnakeDir.Left, SnakeDir.Right) => true
      case (SnakeDir.Right, SnakeDir.Left) => true
      case _ => false

    if isOpposite then
      false // Cannot change to opposite direction
    else
      _snake = _snake.changeDirection(newDir)
      true // Valid direction change succeeded

  def updateSnake(newSnake: Snake): Unit =
    _snake = newSnake

  def snakeLength: Int = _snake.body.size

  def update(): Unit =
    _snake = _snake.crawl(this)
    if coins.contains(snake.body.head) then
      updateSnake(snake.setHasCoin(true))
      coins = coins.filterNot(_ == snake.body.head)
  
  private lazy val allPositions = for
    x <- 0 until size
    y <- 0 until size
  yield (x, y)

  def getEmptyTilePositions: List[(Int, Int)] =
    val occupiedPositions = _snake.body.toSet ++ coins.toSet
    allPositions.filterNot(occupiedPositions.contains).toList

  def addCoin(position: (Int, Int)): Unit =
    if !coins.contains(position) && !_snake.body.contains(position) then
      coins = position :: coins

  def updateSnakeDirection(newDir: SnakeDir): Boolean =
    if !newDir.opposite(_snake.snakeDir) then
      _snake = _snake.changeDirection(newDir)
      true
    else
      false

object Board:
  def apply(size: Int): Board = new Board(size)
  def apply(size: Int, coins: List[(x: Int, y: Int)]): Board = new Board(size, coins)
  def apply(size: Int, coins: List[(x: Int, y: Int)], snake: Snake): Board = new Board(size, coins, snake)
  def apply(size: Int, snake: Snake): Board = new Board(size, Nil, snake)
