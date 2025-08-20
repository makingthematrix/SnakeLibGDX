package io.github.makingthematrix.snakelibgdx

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import io.github.makingthematrix.snakelibgdx.Main.BOARD_SIZE
import scala.util.Random

final class Main extends ApplicationAdapter:
  private lazy val board = Board(
    size = BOARD_SIZE,
    coins = List((6, 3), (5, 2)),
    snake = new Snake(List((3,4), (2, 4), (1, 4)), SnakeDir.Right)
  )

  private var lastUpdateTime: Float = 0f
  private val updateInterval: Float = 0.5f
  private var gameRunning: Boolean = true

  private val newCoinInterval: Float = 5f
  private var lastCoinSpawnTime: Float = 0f

  override def create(): Unit =
    Draw.init()

  override def render(): Unit =
    if gameRunning then
      // Always render the current board state
      Draw.render(board)

      // Check if any key is pressed
      if Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.ANY_KEY) then
        handleKeyPress()

      else
        // Update timing
        val currentTime = Gdx.graphics.getDeltaTime
        lastUpdateTime += currentTime
        lastCoinSpawnTime += currentTime

        // If 1 second has passed, update the board
        if lastUpdateTime >= updateInterval then
          board.update()
          lastUpdateTime = 0f

        // If coin spawn interval has passed and we haven't reached MAX_COINS, spawn a new coin
        if lastCoinSpawnTime >= newCoinInterval && board.coinsNumber < Main.MAX_COINS then
          spawnNewCoin()
          lastCoinSpawnTime = 0f

  override def dispose(): Unit =
    Draw.dispose()

  private def selectRandomPosition(positions: List[(Int, Int)]): Option[(Int, Int)] =
    if positions.nonEmpty then
      Some(positions(Random.nextInt(positions.length)))
    else
      None

  private def spawnNewCoin(): Unit =
    val emptyPositions = board.getEmptyTilePositions
    selectRandomPosition(emptyPositions) match
      case Some(position) => board.addCoin(position)
      case None => // No empty positions available, do nothing

  private def handleKeyPress(): Unit =
    import com.badlogic.gdx.Input.Keys.*
    if Gdx.input.isKeyPressed(LEFT) then board.updateSnakeDirection(SnakeDir.Left)
    else if Gdx.input.isKeyPressed(RIGHT) then board.updateSnakeDirection(SnakeDir.Right)
    else if Gdx.input.isKeyPressed(UP) then board.updateSnakeDirection(SnakeDir.Up)
    else if Gdx.input.isKeyPressed(DOWN) then board.updateSnakeDirection(SnakeDir.Down)

object Main:
  val BOARD_SIZE: Int = 8
  private val MAX_COINS: Int = 10
