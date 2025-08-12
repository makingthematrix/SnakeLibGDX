package io.github.makingthematrix.snakelibgdx

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.{Stage, Actor}
import com.badlogic.gdx.scenes.scene2d.ui.{Dialog, Skin, TextButton, Label}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.makingthematrix.snakelibgdx.Main.BOARD_SIZE
import scala.util.Random

final class Main extends ApplicationAdapter:
  private lazy val board = Board(
    size = BOARD_SIZE,
    coins = List((6, 3), (5, 2)),
    snake = new Snake(List((3,4), (2, 4), (1, 4)), SnakeDir.Right)
  )

  private var lastUpdateTime: Float = 0f
  private val updateInterval: Float = 1f // 1 second
  private var gameRunning: Boolean = true

  private val newCoinInterval: Float = 10f // 10 seconds
  private var lastCoinSpawnTime: Float = 0f

  private lazy val stage: Stage = new Stage(new ScreenViewport())
  private lazy val skin: Skin = new Skin(Gdx.files.internal("ui/uiskin.json"))
  private var showingDialog: Boolean = false

  override def create(): Unit =
    Draw.init()

    // Initialize UI components
    stage
    skin
    Gdx.input.setInputProcessor(stage)

  override def render(): Unit =
    if gameRunning && !showingDialog then
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
          // Check for self-collision after board update
          if board.hasSnakeSelfCollision then
            showScoreDialog()
          lastUpdateTime = 0f

        // If coin spawn interval has passed and we haven't reached MAX_COINS, spawn a new coin
        if lastCoinSpawnTime >= newCoinInterval && board.coinsNumber < Main.MAX_COINS then
          spawnNewCoin()
          lastCoinSpawnTime = 0f

    else if showingDialog then
      // Render the game board in background
      Draw.render(board)

    // Always update and draw the UI stage
    stage.act()
    stage.draw()

  override def dispose(): Unit =
    Draw.dispose()
    stage.dispose()
    skin.dispose()

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

  private def showScoreDialog(): Unit =
    showingDialog = true
    val score = board.snake.body.size
    val dialog = new Dialog("Game Over", skin)

    // Add score label to dialog
    val scoreLabel = new Label(s"Score: $score", skin)
    dialog.getContentTable.add(scoreLabel).pad(20f)

    // Add OK button
    val okButton = new TextButton("OK", skin)
    okButton.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        // Exit the application when OK is clicked
        Gdx.app.exit()
      }
    })
    dialog.getButtonTable.add(okButton).pad(10f)

    // Show the dialog
    dialog.show(stage)

  private def handleKeyPress(): Unit =
    import com.badlogic.gdx.Input.Keys.*
    if Gdx.input.isKeyPressed(LEFT) then board.updateSnakeDirection(SnakeDir.Left)
    else if Gdx.input.isKeyPressed(RIGHT) then board.updateSnakeDirection(SnakeDir.Right)
    else if Gdx.input.isKeyPressed(UP) then board.updateSnakeDirection(SnakeDir.Up)
    else if Gdx.input.isKeyPressed(DOWN) then board.updateSnakeDirection(SnakeDir.Down)

object Main:
  val BOARD_SIZE: Int = 8
  private val MAX_COINS: Int = 10
