package io.github.makingthematrix.snakelibgdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.{Color, Texture}

import java.nio.file.Paths

object Draw:
  private val SQUARE_SIZE = 80f // Size of each square in pixels
  private val TILE_WIDTH = SQUARE_SIZE // Width of an isometric tile
  private val TILE_HEIGHT = SQUARE_SIZE / 2 // Height of isometric tile (half of width for 2:1 ratio)

  private lazy val graphicsWH: (w: Int, h: Int) = (w = Gdx.graphics.getWidth, h = Gdx.graphics.getHeight)

  lazy val textures = Map[Tile, Option[Texture]](
    Tile.Empty -> None,
    Tile.SnakeHead -> Some(new Texture(Paths.get("snakehead.png").toString)),
    Tile.SnakeBody -> Some(new Texture(Paths.get("pawn.png").toString)),
    Tile.SnakeTail -> Some(new Texture(Paths.get("pawn.png").toString)),
    Tile.Coin -> Some(new Texture(Paths.get("pawn.png").toString))
  )

  private lazy val start: (x: Float, y: Float) =
    // Calculate the width and height of the isometric board
    val isoWidth = Main.BOARD_SIZE * TILE_WIDTH
    val isoHeight = Main.BOARD_SIZE * TILE_HEIGHT
    // Calculate the starting position to center the board
    val startX = (graphicsWH.w - isoWidth) / 2f - isoWidth / 16f // Adjust to center horizontally
    val startY = (graphicsWH.h - isoHeight) / 2f // Adjust to center vertically
    (startX, startY)

  def draw(board: Board, shapeRenderer: ShapeRenderer): Unit =
    // First pass: Draw filled tiles (white)
    shapeRenderer.setColor(Color.WHITE)
    for
      row <- 0 until board.size
      col <- 0 until board.size
    do
      // Calculate isometric coordinates
      val isoX = start.x + (col - row) * TILE_WIDTH / 2f + board.size * TILE_WIDTH / 2f
      val isoY = start.y + (col + row) * TILE_HEIGHT / 2f
      // Draw the isometric tile (diamond shape)
      shapeRenderer.triangle(
        isoX, isoY + TILE_HEIGHT / 2f,
        isoX + TILE_WIDTH / 2f, isoY,
        isoX + TILE_WIDTH, isoY + TILE_HEIGHT / 2f
      )
      shapeRenderer.triangle(
        isoX, isoY + TILE_HEIGHT / 2f,
        isoX + TILE_WIDTH, isoY + TILE_HEIGHT / 2f,
        isoX + TILE_WIDTH / 2f, isoY + TILE_HEIGHT
      )

    // End filled rendering and start line rendering for borders
    shapeRenderer.end()
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
    shapeRenderer.setColor(Color.BLACK)

    // Second pass: Draw black borders
    for
      row <- 0 until board.size
      col <- 0 until board.size
    do
      // Calculate isometric coordinates
      val isoX = start.x + (col - row) * TILE_WIDTH / 2f + board.size * TILE_WIDTH / 2f
      val isoY = start.y + (col + row) * TILE_HEIGHT / 2f
      // Draw the diamond border (4 lines forming the outline)
      // Top edge
      shapeRenderer.line(isoX, isoY + TILE_HEIGHT / 2f, isoX + TILE_WIDTH / 2f, isoY)
      // Right edge
      shapeRenderer.line(isoX + TILE_WIDTH / 2f, isoY, isoX + TILE_WIDTH, isoY + TILE_HEIGHT / 2f)
      // Bottom edge
      shapeRenderer.line(isoX + TILE_WIDTH, isoY + TILE_HEIGHT / 2f, isoX + TILE_WIDTH / 2f, isoY + TILE_HEIGHT)
      // Left edge
      shapeRenderer.line(isoX + TILE_WIDTH / 2f, isoY + TILE_HEIGHT, isoX, isoY + TILE_HEIGHT / 2f)

    // End line rendering and restart filled rendering for next frame
    shapeRenderer.end()
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

  def drawTokens(tokens: List[Token], batch: SpriteBatch): Unit =
    for token <- tokens do
      textures(token.tile).foreach { texture =>
        // Calculate isometric coordinates for the pawn
        val tokenIsoX = start.x + (token.pos.x - token.pos.y) * TILE_WIDTH / 2f + Main.BOARD_SIZE * TILE_WIDTH / 2f
        val tokenIsoY = start.y + (token.pos.x + token.pos.y - 1f) * TILE_HEIGHT / 2f
        // Adjust the pawn position to center it on the tile
        val tokenX = tokenIsoX + (TILE_WIDTH - SQUARE_SIZE) / 2f
        val tokenY = tokenIsoY - SQUARE_SIZE / 4f // Adjust to position pawn on the tile
        batch.draw(texture, tokenX, tokenY, SQUARE_SIZE, SQUARE_SIZE)
      }

