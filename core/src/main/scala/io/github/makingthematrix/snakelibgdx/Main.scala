package io.github.makingthematrix.snakelibgdx

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import io.github.makingthematrix.snakelibgdx.Main.BOARD_SIZE

final class Main extends ApplicationAdapter:
  private lazy val board = Board(
    size = BOARD_SIZE,
    coins = List((3, 4), (6, 3), (0, 0), (7, 7), (0, 7), (7, 0)),
    snake = new Snake(List((3,2), (3, 3), (2, 3), (1, 3)), SnakeDir.Right)
  )

  private var lastUpdateTime: Float = 0f
  private val updateInterval: Float = 1f // 1 second
  private var gameRunning: Boolean = true

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

        // If 1 second has passed, update the board
        if lastUpdateTime >= updateInterval then
          board.update()
          lastUpdateTime = 0f

  override def dispose(): Unit =
    Draw.dispose()

  private def handleKeyPress(): Unit =
    import com.badlogic.gdx.Input.Keys.*
    if Gdx.input.isKeyPressed(LEFT) then board.updateSnakeDirection(SnakeDir.Left)
    else if Gdx.input.isKeyPressed(RIGHT) then board.updateSnakeDirection(SnakeDir.Right)
    else if Gdx.input.isKeyPressed(UP) then board.updateSnakeDirection(SnakeDir.Up)
    else if Gdx.input.isKeyPressed(DOWN) then board.updateSnakeDirection(SnakeDir.Down)

object Main:
  val BOARD_SIZE: Int = 8
