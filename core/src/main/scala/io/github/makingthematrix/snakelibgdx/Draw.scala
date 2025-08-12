package io.github.makingthematrix.snakelibgdx

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

object Draw:
  private val SQUARE_SIZE = 80f // Size of each square in pixels
  private val TILE_WIDTH = SQUARE_SIZE // Width of an isometric tile
  private val TILE_HEIGHT = SQUARE_SIZE / 2 // Height of isometric tile (half of width for 2:1 ratio)

  private def startPos(boardSize: Int, graphicsWH: (w: Int, h: Int)): (x: Float, y: Float) =
    // Calculate the width and height of the isometric board
    val isoWidth = boardSize * TILE_WIDTH
    val isoHeight = boardSize * TILE_HEIGHT
    // Calculate the starting position to center the board
    val startX = (graphicsWH.w - isoWidth) / 2f - isoWidth / 16f // Adjust to center horizontally
    val startY = (graphicsWH.h - isoHeight) / 2f // Adjust to center vertically
    (startX, startY)

  def draw(board: Board)(using shapeRenderer: ShapeRenderer, graphicsWH: (w: Int, h: Int)): Unit =
    val start = startPos(board.size, graphicsWH)
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

/*  private def draw(token: Token): Unit =
    // Calculate isometric coordinates for the pawn
    val pawnIsoX = start.x + (token.pos.x - token.pos.y) * TILE_WIDTH / 2f + BOARD_SIZE * TILE_WIDTH / 2f
    val pawnIsoY = start.y + (token.pos.x + token.pos.y + 1f) * TILE_HEIGHT / 2f
    // Adjust the pawn position to center it on the tile
    val pawnX = pawnIsoX + (TILE_WIDTH - SQUARE_SIZE) / 2f
    val pawnY = pawnIsoY - SQUARE_SIZE / 4f // Adjust to position pawn on the tile
    batch.draw(token.texture, pawnX, pawnY, SQUARE_SIZE, SQUARE_SIZE)*/

/*      // Determine if the square should be black or white
      // If row + col is even, it's white; if odd, it's black
      shapeRenderer.setColor(if (row + col) % 2 == 0 then Color.WHITE
      else Color.BLACK)*/
