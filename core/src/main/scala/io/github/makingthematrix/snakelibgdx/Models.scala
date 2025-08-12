package io.github.makingthematrix.snakelibgdx

enum SnakeDir (val x: Int, val y: Int):
  case Up extends SnakeDir(0, -1)
  case Right extends SnakeDir(1, 0)
  case Down extends SnakeDir(0, 1)
  case Left extends SnakeDir(-1, 0)

final class Snake(body: List[(x: Int, y: Int)], snakeDir: SnakeDir):

  def getBody: List[(x: Int, y: Int)] = body

  def changeDirection(newDir: SnakeDir): Snake =
    new Snake(body, newDir)

  def crawl: Snake =
    body match
      case Nil =>
        // Empty body - create new head at (0,0) moved by snakeDir with wrapping
        val newHead = wrapCoordinate(snakeDir.x, snakeDir.y)
        new Snake(List(newHead), snakeDir)
      case head :: Nil =>
        // Single element - new head is current head moved by snakeDir with wrapping
        val newHead = wrapCoordinate(head.x + snakeDir.x, head.y + snakeDir.y)
        new Snake(List(newHead), snakeDir)
      case head :: tail =>
        // Multiple elements - remove last, add new head with wrapping
        val newHead = wrapCoordinate(head.x + snakeDir.x, head.y + snakeDir.y)
        val newBody = newHead :: body.init // init removes the last element
        new Snake(newBody, snakeDir)

  private def wrapCoordinate(x: Int, y: Int): (Int, Int) =
    val wrappedX = if x == -1 then Main.BOARD_SIZE - 1 else if x == Main.BOARD_SIZE then 0 else x
    val wrappedY = if y == -1 then Main.BOARD_SIZE - 1 else if y == Main.BOARD_SIZE then 0 else y
    (wrappedX, wrappedY)

object Snake:
  def apply(): Snake = new Snake(Nil, SnakeDir.Right)

  def apply(body: List[(x: Int, y: Int)], snakeDir: SnakeDir = SnakeDir.Right): Option[Snake] =
    if isContinuous(body) then
      // For now, default direction is Right - this could be enhanced to detect direction from body
      Some(new Snake(body, snakeDir))
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

  def update(): Unit =
    _snake = _snake.crawl
    _snake.getBody.headOption.foreach { head =>
      if coins.contains(head) then
        coins = coins.filterNot(_ == head)
    }


object Board:
  def apply(size: Int): Board = new Board(size)
  def apply(size: Int, coins: List[(x: Int, y: Int)]): Board = new Board(size, coins)
  def apply(size: Int, coins: List[(x: Int, y: Int)], snake: Snake): Board = new Board(size, coins, snake)
  def apply(size: Int, snake: Snake): Board = new Board(size, Nil, snake)
