package io.github.makingthematrix.snakelibgdx

import com.badlogic.gdx.{ApplicationAdapter, Gdx, InputProcessor}
import com.badlogic.gdx.scenes.scene2d.ui.{Dialog, Label, Skin, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.makingthematrix.snakelibgdx.Main.BOARD_SIZE

import scala.util.Random

final class Main extends ApplicationAdapter {
  private lazy val board = Board(
    size = BOARD_SIZE,
    coins = PosList((3, 4), (6, 3), (0, 0), (7, 7), (0, 7), (7, 0)),
    snake = new Snake(PosList((3, 2), (3, 3), (2, 3), (1, 3)), Dir2D.Right)
  )

  private var lastUpdateTime: Float = 0f
  private val updateInterval: Float = 0.5f
  private val newCoinInterval: Float = 5f
  private var lastCoinSpawnTime: Float = 0f

  // Game state management
  private enum GameState {
    case Playing, ShowingScorePopup, Ended
  }

  private var gameState: GameState = GameState.Playing

  // UI components
  private lazy val stage: Stage = new Stage(new ScreenViewport())
  private lazy val skin: Skin = new Skin(Gdx.files.internal("ui/uiskin.json"))
  private lazy val scoreDialog: Dialog =
    new Dialog("Game Over", skin, "dialog"){
      // Set size and padding for better visibility
      pad(20)
      setSize(300, 200)
    }

  // Custom InputProcessor to handle key input
  private class GameInputProcessor extends InputProcessor:

    private def rotateClockwise(currentDir: Dir2D): Dir2D =
      currentDir match {
        case Dir2D.Up => Dir2D.Right
        case Dir2D.Right => Dir2D.Down
        case Dir2D.Down => Dir2D.Left
        case Dir2D.Left => Dir2D.Up
      }

    private def rotateCounterClockwise(currentDir: Dir2D): Dir2D =
      currentDir match {
        case Dir2D.Up => Dir2D.Left
        case Dir2D.Left => Dir2D.Down
        case Dir2D.Down => Dir2D.Right
        case Dir2D.Right => Dir2D.Up
      }

    override def keyDown(keycode: Int): Boolean =
      // Delegate to stage first for UI handling
      stage.keyDown(keycode)

    override def keyUp(keycode: Int): Boolean =
      // Handle LEFT/RIGHT keys for snake rotation
      keycode match {
        case com.badlogic.gdx.Input.Keys.LEFT =>
          val newDirection = rotateClockwise(board.snake.snakeDir)
          board.changeSnakeDirection(newDirection)
          true
        case com.badlogic.gdx.Input.Keys.RIGHT =>
          // RIGHT key rotates clockwise
          val newDirection = rotateCounterClockwise(board.snake.snakeDir)
          board.changeSnakeDirection(newDirection)
          true
        case _ =>
          // Delegate other keys to stage
          stage.keyUp(keycode)
      }

    override def keyTyped(character: Char): Boolean = stage.keyTyped(character)

    override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean =
      stage.touchDown(screenX, screenY, pointer, button)

    override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean =
      stage.touchUp(screenX, screenY, pointer, button)

    override def touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean =
      stage.touchCancelled(screenX, screenY, pointer, button)

    override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean =
      stage.touchDragged(screenX, screenY, pointer)

    override def mouseMoved(screenX: Int, screenY: Int): Boolean =
      stage.mouseMoved(screenX, screenY)

    override def scrolled(amountX: Float, amountY: Float): Boolean =
      stage.scrolled(amountX, amountY)

  override def create(): Unit = {
    Draw.init()
    // Initialize UI components
    stage
    skin
    // Set our custom input processor
    Gdx.input.setInputProcessor(new GameInputProcessor())
  }

  override def render(): Unit =
    gameState match {
      case GameState.Playing =>
        // Always render the current board state
        Draw.render(board)

        // Update timing (input is now handled by InputProcessor)
        val currentTime = Gdx.graphics.getDeltaTime
        lastUpdateTime += currentTime
        // Update coin spawn timing
        lastCoinSpawnTime += currentTime
        // If interval has passed, update the board
        if (lastUpdateTime >= updateInterval) {
          board.update()
          lastUpdateTime = 0f
          // If coin spawn interval has passed and we haven't reached MAX_COINS, spawn a new coin
          if (lastCoinSpawnTime >= newCoinInterval && board.coinsNumber < Main.MAX_COINS) {
            spawnNewCoin()
            lastCoinSpawnTime = 0f
          }
        }

          if (board.hasSnakeSelfCollision) {
            val snakeLength = board.snake.body.size
            showScorePopup(snakeLength)
            gameState = GameState.ShowingScorePopup
          }

      case GameState.ShowingScorePopup =>
        // Continue rendering the board behind the popup
        Draw.render(board)
        // Render the UI stage with the popup
        stage.act()
        stage.draw()

      case GameState.Ended =>
        // Game over - could show final screen or just exit
        Gdx.app.exit()
    }

  private def showScorePopup(snakeLength: Int): Unit = {
    // Create the dialog
    scoreDialog
    // Add score label
    val scoreLabel = new Label(s"Score: $snakeLength", skin)
    scoreDialog.text(scoreLabel)
    // Add close button
    val closeButton = new TextButton("Close", skin)
    closeButton.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        gameState = GameState.Ended
      }
    })
    scoreDialog.button(closeButton)

    // Center the dialog and show it
    scoreDialog.show(stage)
  }

  override def dispose(): Unit = {
    Draw.dispose()
    if (stage != null) stage.dispose()
    if (skin != null) skin.dispose()
  }

  private def selectRandomPosition(positions: List[Pos2D]): Option[Pos2D] =
    if (positions.nonEmpty) Some(positions(Random.nextInt(positions.length)))
    else None

  private def spawnNewCoin(): Unit =
    selectRandomPosition(board.getEmptyTilePositions) match {
      case Some(position) => board.addCoin(position)
      case None => // No empty positions available, do nothing
    }
}

object Main {
  val BOARD_SIZE: Int = 8
  val MAX_COINS: Int = 10
}
