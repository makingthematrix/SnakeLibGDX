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

final class Snake(val body: List[(x: Int, y: Int)], val snakeDir: SnakeDir):
  def changeDirection(newDir: SnakeDir): Snake = new Snake(body, newDir)

  def crawl: Snake = body match
    case Nil =>
      this // Nothing to crawl, just return the same snake
    case head :: _ =>
      val newHead = (head.x + snakeDir.x, head.y + snakeDir.y)
      val newBody = newHead :: body.init // init removes the last element
      new Snake(newBody, snakeDir)

object Snake:
  def apply(): Snake = new Snake(Nil, SnakeDir.Right)
  def apply(body: List[(x: Int, y: Int)]): Snake = new Snake(body, SnakeDir.Right)

final class Board(val size: Int, private var coins: List[(x: Int, y: Int)] = Nil, private var _snake: Snake = Snake()):
  def coinsPositions: List[(x: Int, y: Int)] = coins
  def snake: Snake = _snake

  def coinsNumber: Int = coins.size
  def snakeLength: Int = _snake.body.size

  def updateSnake(newSnake: Snake): Unit =
    _snake = newSnake

  def update(): Unit =
    _snake = _snake.crawl

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
