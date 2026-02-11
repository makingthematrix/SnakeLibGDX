package io.github.makingthematrix.snakelibgdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.utils.ScreenUtils

import java.nio.file.Paths

class DrawBoardIsometric (boardSize: Int,
                          squareSize: Double = 80.0,
                          graphicsSize: Size2D = Size2D(Gdx.graphics.getWidth, Gdx.graphics.getHeight)
                         ) {
  private val tileSize = Size2D(squareSize, squareSize / 2.0) // Height of isometric tile (half of width for 2:1 ratio)
  private lazy val shapeRenderer: ShapeRenderer = new ShapeRenderer()
  private lazy val batch = new SpriteBatch()
  private lazy val start: Size2D = {
    // Calculate the width and height of the isometric board
    val iso = tileSize * boardSize
    // Calculate the starting position to center the board
    val startX = (graphicsSize.width - iso.width) / 2.0 - iso.width / 16.0 // Adjust to center horizontally
    val startY = (graphicsSize.height - iso.height) / 2.0 // Adjust to center vertically
    Size2D(startX, startY)
  }

  def init(): Unit = {
    shapeRenderer
    batch
    start
  }

  def render(board: Board): Unit = {
    // Clear the screen
    ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)
    // Begin shape rendering in filled mode
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    drawBoard(board)
    drawSnake(board.snake)
    shapeRenderer.end()
    // Draw the snake
    batch.begin()
    drawCoins(board.coinsPositions)
    batch.end()
  }

  def dispose(): Unit = {
    shapeRenderer.dispose()
    batch.dispose()
  }

  // ------------------------------------------------------------------------------------------------------------------
  // Methods specific to the game of Snake
  private def drawSnake(snake: Snake): Unit =
    if (snake.body.nonEmpty) {
      shapeRenderer.setColor(DrawBoardIsometric.darkGreen)
      colorTile(snake.body.head)
      shapeRenderer.setColor(Color.GREEN)
      snake.body.tail.foreach(colorTile)
    }

  private def drawCoins(coinPositions: List[Pos2D]): Unit =
    for (coinPos <- coinPositions)
      drawTexture(DrawBoardIsometric.coinTexture, coinPos)
  // ------------------------------------------------------------------------------------------------------------------

  private def drawBoard(board: Board): Unit = {
    // First pass: Draw filled tiles (white)
    shapeRenderer.setColor(Color.WHITE)
    for {
      row <- 0 until board.size
      col <- 0 until board.size
    } {
      colorTile(Pos2D(col, row))
    }
    // End filled rendering and start line rendering for borders
    shapeRenderer.end()
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
    shapeRenderer.setColor(Color.BLACK)
    // Second pass: Draw black borders
    for {
      row <- 0 until board.size
      col <- 0 until board.size
    } {
      drawTile(Pos2D(col, row))
    }
    // End line rendering and restart filled rendering for next frame
    shapeRenderer.end()
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
  }

  private def calculateIso(pos: Pos2D): (x: Double, y: Double) = {
    val isoX = start.width + (pos.x - pos.y) * tileSize.width / 2.0 + boardSize * tileSize.width / 2.0
    val isoY = start.height + (pos.x + pos.y) * tileSize.height / 2.0
    (x = isoX, y = isoY)
  }

  private def colorTile(pos: Pos2D): Unit = {
    // Calculate isometric coordinates
    val (isoX, isoY) = calculateIso(pos)
    // Draw the isometric tile (diamond shape)
    val x1 = isoX.toFloat
    val y1 = (isoY + tileSize.height / 2.0).toFloat
    val x2 = (isoX + tileSize.width / 2.0).toFloat
    val x3 = (isoX + tileSize.width).toFloat
    shapeRenderer.triangle(
      x1, y1,
      x2, isoY.toFloat,
      x3, y1
    )
    shapeRenderer.triangle(
      x1, y1,
      x3, y1,
      x2, (isoY + tileSize.height).toFloat
    )
  }

  private def drawTile(pos: Pos2D): Unit = {
    // Calculate isometric coordinates
    val (isoX, isoY) = calculateIso(pos)
    // Draw the diamond border (4 lines forming the outline)
    val x1 = isoX.toFloat
    val y1 = (isoY + tileSize.height / 2.0).toFloat
    val x2 = (isoX + tileSize.width / 2.0).toFloat
    val y2 = isoY.toFloat
    val x3 = (isoX + tileSize.width).toFloat
    val y3 = (isoY + tileSize.height).toFloat
    // Top edge
    shapeRenderer.line(x1, y1, x2, y2)
    // Right edge
    shapeRenderer.line(x2, y2, x3, y1)
    // Bottom edge
    shapeRenderer.line(x3, y1, x2, y3)
    // Left edge
    shapeRenderer.line(x2, y3, x1, y1)
  }

  private def calculateTokenIso(pos: Pos2D): (x: Double, y: Double) = {
    val isoX = start.width + (pos.x - pos.y) * tileSize.width / 2.0 + boardSize * tileSize.width / 2.0
    val isoY = start.height + (pos.x + pos.y + 1.0) * tileSize.height / 2.0
    (x = isoX, y = isoY)
  }

  private def drawTexture(texture: Texture, pos: Pos2D): Unit = {
    // Calculate isometric coordinates for the pawn
    val (isoX, isoY) = calculateTokenIso(pos)
    // Adjust the pawn position to center it on the tile
    val tokenX = isoX + (1.5 * tileSize.width - squareSize) / 2.0
    val tokenY = isoY - squareSize / 4.0 // Adjust to position pawn on the tile
    val halfSquare = (squareSize / 2.0).toFloat

    // Wrap the Texture into Sprite and set its size to the size of the square
    val sprite = new Sprite(texture)
    sprite.setSize(halfSquare, halfSquare)
    sprite.setPosition(tokenX.toFloat, tokenY.toFloat)

    // Set the origin to the center of the sprite for proper rotation
    sprite.setOrigin(halfSquare, halfSquare)
    sprite.draw(batch)
  }
}

object DrawBoardIsometric {
  private val SQUARE_SIZE = 80f // Size of each square in pixels
  private val coinTexture = new Texture(Paths.get("scala.png").toString)
  private val darkGreen = new Color(0, 0.6f, 0, 1)
}