package io.github.makingthematrix.snakelibgdx

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import io.github.makingthematrix.snakelibgdx.Main.BOARD_SIZE
import io.github.makingthematrix.snakelibgdx.Orientation.None

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
class Main extends ApplicationAdapter:
  given shapeRenderer: ShapeRenderer = new ShapeRenderer()
  private lazy val batch = new SpriteBatch()
  private lazy val board = Board(BOARD_SIZE)

  val token1 = Token(Tile.SnakeHead, None, (3, 4))
  val token2 = Token(Tile.SnakeHead, None, (6, 3))

  override def create(): Unit =
    shapeRenderer
    batch

  override def render(): Unit =
    // Clear the screen
    ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)
    // Begin shape rendering in filled mode
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    Draw.draw(board, shapeRenderer)
    shapeRenderer.end()
    // Draw the snake
    batch.begin()
    Draw.drawTokens(List(token1, token2), batch)
    batch.end()

  override def dispose(): Unit =
    shapeRenderer.dispose()
    batch.dispose()

object Main:
  val BOARD_SIZE: Int = 8
