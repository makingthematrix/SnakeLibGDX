package io.github.makingthematrix.snakelibgdx

import com.badlogic.gdx.ApplicationAdapter
import io.github.makingthematrix.snakelibgdx.Main.BOARD_SIZE

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
final class Main extends ApplicationAdapter:
  private lazy val board = Board(
    size = BOARD_SIZE,
    coins = List((3, 4), (6, 3)),
    snake = new Snake(List((3,2), (3, 3), (2, 3), (1, 3)), SnakeDir.Right)
  )

  override def create(): Unit =
    Draw.init()

  override def render(): Unit =
    Draw.render(board)

  override def dispose(): Unit =
    Draw.dispose()

object Main:
  val BOARD_SIZE: Int = 8
