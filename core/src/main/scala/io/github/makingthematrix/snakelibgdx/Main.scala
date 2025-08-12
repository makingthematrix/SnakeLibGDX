package io.github.makingthematrix.snakelibgdx

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.{ApplicationAdapter, Gdx}

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
class Main extends ApplicationAdapter:
  given shapeRenderer: ShapeRenderer = new ShapeRenderer()
  private lazy val batch = new SpriteBatch()
  given graphicsWH: (w: Int, h: Int) = (w = Gdx.graphics.getWidth, h = Gdx.graphics.getHeight)
  private lazy val board = Board(8)

  override def create(): Unit =
    shapeRenderer
    batch
    graphicsWH
  
  override def render(): Unit =
    // Clear the screen
    ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)
    // Begin shape rendering in filled mode
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    Draw.draw(board)
    shapeRenderer.end()
    // Draw the snake
    batch.begin()
    batch.end()

  override def dispose(): Unit =
    shapeRenderer.dispose()
    batch.dispose()
