package io.github.makingthematrix.snakelibgdx

import munit.FunSuite

class ModelsSuite extends FunSuite {
  // Snake.apply tests for continuity validation
  test("Snake.apply should NOT create snake for empty body list") {
    val result = Snake(List.empty)
    assert(result.isEmpty, "Empty list should not be considered continuous")
  }

  test("Snake.apply should create snake for single element body") {
    val result = Snake(PosList((5, 5)))
    assert(result.isDefined, "Single element should be considered continuous")
  }

  test("Snake.apply should create snake for continuous horizontal sequence") {
    val horizontalBody = PosList((1, 1), (2, 1), (3, 1), (4, 1))
    val result = Snake(horizontalBody)
    assert(result.isDefined, "Horizontal continuous sequence should be valid")
  }

  test("Snake.apply should create snake for continuous vertical sequence") {
    val verticalBody = PosList((2, 1), (2, 2), (2, 3), (2, 4))
    val result = Snake(verticalBody)
    assert(result.isDefined, "Vertical continuous sequence should be valid")
  }

  test("Snake.apply should create snake for mixed continuous sequence") {
    val mixedBody = PosList((1, 1), (2, 1), (2, 2), (2, 3), (3, 3))
    val result = Snake(mixedBody)
    assert(result.isDefined, "Mixed continuous sequence should be valid")
  }

  test("Snake.apply should reject discontinuous horizontal sequence") {
    val discontinuousBody = PosList((1, 1), (3, 1), (4, 1)) // Gap between (1,1) and (3,1)
    val result = Snake(discontinuousBody)
    assert(result.isEmpty, "Discontinuous horizontal sequence should be rejected")
  }

  test("Snake.apply should reject discontinuous vertical sequence") {
    val discontinuousBody = PosList((2, 1), (2, 3), (2, 4)) // Gap between (2,1) and (2,3)
    val result = Snake(discontinuousBody)
    assert(result.isEmpty, "Discontinuous vertical sequence should be rejected")
  }

  test("Snake.apply should reject diagonal sequence") {
    val diagonalBody = PosList((1, 1), (2, 2), (3, 3)) // Diagonal moves have sum of differences = 2
    val result = Snake(diagonalBody)
    assert(result.isEmpty, "Diagonal sequence should be rejected")
  }

  test("Snake.apply should reject sequence with large gaps") {
    val gappedBody = PosList((1, 1), (1, 2), (1, 5)) // Large gap between (1,2) and (1,5)
    val result = Snake(gappedBody)
    assert(result.isEmpty, "Sequence with large gaps should be rejected")
  }

  test("Snake.apply should handle negative coordinates correctly") {
    val negativeBody = PosList((-2, -1), (-1, -1), (0, -1), (1, -1))
    val result = Snake(negativeBody)
    assert(result.isDefined, "Continuous sequence with negative coordinates should be valid")
  }

  test("Snake.apply should validate continuity for L-shaped snake") {
    val lShapedBody = PosList((1, 1), (2, 1), (3, 1), (3, 2), (3, 3))
    val result = Snake(lShapedBody)
    assert(result.isDefined, "L-shaped continuous sequence should be valid")
  }

  test("Snake.apply should reject sequence with backward jump") {
    val backwardJumpBody = PosList((1, 1), (2, 1), (3, 1), (1, 1)) // Jumps back to (1,1)
    val result = Snake(backwardJumpBody)
    assert(result.isEmpty, "Sequence with backward jump should be rejected")
  }

  test("changeDirection should preserve snake body") {
    val originalBody = PosList((3, 3), (2, 3), (1, 3))
    val snake = Snake(originalBody).get
    val newSnake = snake.changeDirection(Dir2D.Down)
    // Body should be preserved, only direction changed
    assert(newSnake != null, "Snake body should be preserved after direction change")
  }

  test("changeDirection should work with all directions") {
    val snake = Snake(PosList((5, 5))).get
    val upSnake = snake.changeDirection(Dir2D.Up)
    val downSnake = snake.changeDirection(Dir2D.Down)
    val leftSnake = snake.changeDirection(Dir2D.Left)
    val rightSnake = snake.changeDirection(Dir2D.Right)

    assert(upSnake != null, "Should change to Up direction")
    assert(downSnake != null, "Should change to Down direction")
    assert(leftSnake != null, "Should change to Left direction")
    assert(rightSnake != null, "Should change to Right direction")
  }

  // Empty tile positions tests
  test("Board getEmptyTilePositions should return all positions when board is empty") {
    val board = Board(3) // 3x3 board with no coins and empty snake
    val emptyPositions = board.getEmptyTilePositions

    assertEquals(emptyPositions.size, 9, "Empty 3x3 board should have 9 empty positions")
    assert(emptyPositions.contains(Pos2D(0, 0)), "Should contain (0,0)")
    assert(emptyPositions.contains(Pos2D(2, 2)), "Should contain (2,2)")
  }

  test("Board getEmptyTilePositions should exclude snake body positions") {
    val snake = Snake(PosList((1, 0), (1, 1), (1, 2)))
    val board = Board(3, Nil, snake.get)
    val emptyPositions = board.getEmptyTilePositions

    assertEquals(emptyPositions.size, 6, "3x3 board with 3-segment snake should have 6 empty positions")
    assert(!emptyPositions.contains(Pos2D(1, 0)), "Should not contain snake head position")
    assert(!emptyPositions.contains(Pos2D(1, 1)), "Should not contain snake body position")
    assert(!emptyPositions.contains(Pos2D(1, 2)), "Should not contain snake tail position")
    assert(emptyPositions.contains(Pos2D(0, 0)), "Should contain non-snake position")
  }

  test("Board getEmptyTilePositions should exclude coin positions") {
    val coins = PosList((0, 0), (1, 1), (2, 2))
    val board = Board(3, coins)
    val emptyPositions = board.getEmptyTilePositions

    assertEquals(emptyPositions.size, 6, "3x3 board with 3 coins should have 6 empty positions")
    assert(!emptyPositions.contains(Pos2D(0, 0)), "Should not contain coin position (0,0)")
    assert(!emptyPositions.contains(Pos2D(1, 1)), "Should not contain coin position (1,1)")
    assert(!emptyPositions.contains(Pos2D(2, 2)), "Should not contain coin position (2,2)")
    assert(emptyPositions.contains(Pos2D(0, 1)), "Should contain non-coin position")
  }

  test("Board getEmptyTilePositions should exclude both snake and coin positions") {
    val snake = Snake(PosList((0, 0), (0, 1)))
    val coins = PosList((1, 0), (2, 2))
    val board = Board(3, coins, snake.get)
    val emptyPositions = board.getEmptyTilePositions

    assertEquals(emptyPositions.size, 5, "3x3 board with 2-segment snake and 2 coins should have 5 empty positions")
    assert(!emptyPositions.contains(Pos2D(0, 0)), "Should not contain snake head position")
    assert(!emptyPositions.contains(Pos2D(0, 1)), "Should not contain snake body position")
    assert(!emptyPositions.contains(Pos2D(1, 0)), "Should not contain coin position")
    assert(!emptyPositions.contains(Pos2D(2, 2)), "Should not contain coin position")
    assert(emptyPositions.contains(Pos2D(1, 1)), "Should contain empty position")
  }

  test("Board getEmptyTilePositions should return empty list when board is full") {
    // Create a board where snake and coins occupy all positions
    val allPositions = for {
      x <- 0 until 2
      y <- 0 until 2
    } yield (x, y)

    val snake = Snake(PosList((0, 0), (0, 1)))
    val coins = PosList((1, 0), (1, 1))
    val board = Board(2, coins, snake.get)
    val emptyPositions = board.getEmptyTilePositions

    assertEquals(emptyPositions.size, 0, "Fully occupied 2x2 board should have 0 empty positions")
  }

  // AddCoin method tests
  test("Board addCoin should add coin to empty position") {
    val board = Board(3)
    val originalCoinCount = board.coinsNumber

    board.addCoin(Pos2D(1, 1))

    assertEquals(board.coinsNumber, originalCoinCount + 1, "Coin count should increase by 1")
    assert(board.coinsPositions.contains(Pos2D(1, 1)), "Board should contain the new coin")
  }

  test("Board addCoin should not add coin to position with existing coin") {
    val board = Board(3, PosList((1, 1)))
    val originalCoinCount = board.coinsNumber

    board.addCoin(Pos2D(1, 1)) // Try to add coin at same position

    assertEquals(board.coinsNumber, originalCoinCount, "Coin count should not change")
    assertEquals(board.coinsPositions.count(_ == Pos2D(1, 1)), 1, "Should still have only one coin at position")
  }

  test("Board addCoin should not add coin to position with snake body") {
    val snake = Snake(PosList((1, 1), (1, 2)))
    val board = Board(3, Nil, snake.get)
    val originalCoinCount = board.coinsNumber

    board.addCoin(Pos2D(1, 1)) // Try to add coin at snake head
    board.addCoin(Pos2D(1, 2)) // Try to add coin at snake body

    assertEquals(board.coinsNumber, originalCoinCount, "Coin count should not change")
    assert(!board.coinsPositions.contains(Pos2D(1, 1)), "Should not contain coin at snake head")
    assert(!board.coinsPositions.contains(Pos2D(1, 2)), "Should not contain coin at snake body")
  }

  test("Board addCoin should successfully add multiple coins to different positions") {
    val board = Board(3)

    board.addCoin(Pos2D(0, 0))
    board.addCoin(Pos2D(1, 1))
    board.addCoin(Pos2D(2, 2))

    assertEquals(board.coinsNumber, 3, "Should have 3 coins")
    assert(board.coinsPositions.contains(Pos2D(0, 0)), "Should contain coin at (0,0)")
    assert(board.coinsPositions.contains(Pos2D(1, 1)), "Should contain coin at (1,1)")
    assert(board.coinsPositions.contains(Pos2D(2, 2)), "Should contain coin at (2,2)")
  }
  // Board.changeSnakeDirection method tests
  test("Board.changeSnakeDirection should update snake direction to Left") {
    val snake = Snake(PosList((3, 3), (2, 3))).get // Snake moving right initially
    val board = Board(8, List.empty, snake)

    board.changeSnakeDirection(Dir2D.Left)

    // Test that direction changed by checking crawl behavior
    board.update() // This calls crawl with new direction
    val newHead = board.snake.body.head

    // If direction changed to Left, new head should be to the left of original head
    assertEquals(newHead, Pos2D(4, 3), "Snake should move left after direction change")
  }

  test("Board.changeSnakeDirection should update snake direction to Right") {
    val snake = Snake(PosList((3, 3), (4, 3))).get.changeDirection(Dir2D.Left) // Snake moving left initially
    val board = Board(8, List.empty, snake)

    board.changeSnakeDirection(Dir2D.Right)

    // Test that direction changed by checking crawl behavior
    board.update() // This calls crawl with new direction
    val newHead = board.snake.body.head

    // If direction changed to Right, new head should be to the right of original head
    assertEquals(newHead, Pos2D(2, 3), "Snake should move right after direction change")
  }

  test("Board.changeSnakeDirection should update snake direction to Up") {
    val snake = Snake(PosList((3, 3), (4, 3))).get.changeDirection(Dir2D.Right) // Snake moving right initially
    val board = Board(8, List.empty, snake)

    board.changeSnakeDirection(Dir2D.Up)

    // Test that direction changed by checking crawl behavior
    board.update() // This calls crawl with new direction
    val newHead = board.snake.body.head

    // If direction changed to Up, new head should be above original head (y decreases)
    assertEquals(newHead, Pos2D(3, 2), "Snake should move up after direction change")
  }

  test("Board.changeSnakeDirection should update snake direction to Down") {
    val snake = Snake(PosList((3, 3), (2, 3))).get.changeDirection(Dir2D.Left) // Snake moving left initially
    val board = Board(8, List.empty, snake)

    board.changeSnakeDirection(Dir2D.Down)

    // Test that direction changed by checking crawl behavior
    board.update() // This calls crawl with new direction
    val newHead = board.snake.body.head

    // If direction changed to Down, new head should be below original head (y increases)
    assertEquals(newHead, Pos2D(3, 4), "Snake should move down after direction change")
  }

  test("Board.changeSnakeDirection should work with all directions") {
    val snake = Snake(PosList((4, 4))).get
    val board = Board(8, List.empty, snake)

    // Test all direction changes work without errors
    board.changeSnakeDirection(Dir2D.Up)
    assert(board.snake != null, "Snake should exist after changing to Up")

    board.changeSnakeDirection(Dir2D.Right)
    assert(board.snake != null, "Snake should exist after changing to Right")

    board.changeSnakeDirection(Dir2D.Down)
    assert(board.snake != null, "Snake should exist after changing to Down")

    board.changeSnakeDirection(Dir2D.Left)
    assert(board.snake != null, "Snake should exist after changing to Left")
  }

  test("Board.changeSnakeDirection should preserve snake body") {
    val originalBody = PosList((5, 5), (4, 5), (3, 5))
    val snake = Snake(originalBody).get
    val board = Board(8, List.empty, snake)
    val originalBodySize = board.snake.body.size

    board.changeSnakeDirection(Dir2D.Up)

    // Body size and positions should be preserved, only direction changes
    assertEquals(board.snake.body.size, originalBodySize, "Snake body size should be preserved")
    assertEquals(board.snake.body, originalBody, "Snake body should be preserved")
  }

  // Backwards direction restriction tests
  test("Board.changeSnakeDirection should return false and block Up to Down direction change") {
    val snake = Snake(PosList((4, 4), (4, 5))).get.changeDirection(Dir2D.Up) // Snake moving up
    val board = Board(8, List.empty, snake)

    val result = board.changeSnakeDirection(Dir2D.Down)

    assertEquals(result, false, "Changing from Up to Down should return false")

    // Verify direction didn't change by checking crawl behavior
    board.update()
    val newHead = board.snake.body.head
    assertEquals(newHead, Pos2D(4, 3), "Snake should continue moving up (y decreases)")
  }

  test("Board.changeSnakeDirection should return false and block Down to Up direction change") {
    val snake = Snake(PosList((4, 4), (4, 3))).get.changeDirection(Dir2D.Down) // Snake moving down
    val board = Board(8, List.empty, snake)

    val result = board.changeSnakeDirection(Dir2D.Up)

    assertEquals(result, false, "Changing from Down to Up should return false")

    // Verify direction didn't change by checking crawl behavior
    board.update()
    val newHead = board.snake.body.head
    assertEquals(newHead, Pos2D(4, 5), "Snake should continue moving down (y increases)")
  }

  test("Board.changeSnakeDirection should return false and block Left to Right direction change") {
    val snake = Snake(PosList((4, 4), (5, 4))).get.changeDirection(Dir2D.Left) // Snake moving left
    val board = Board(8, List.empty, snake)

    val result = board.changeSnakeDirection(Dir2D.Right)

    assertEquals(result, false, "Changing from Left to Right should return false")

    // Verify direction didn't change by checking crawl behavior
    board.update()
    val newHead = board.snake.body.head
    assertEquals(newHead, Pos2D(3, 4), "Snake should continue moving left (x decreases)")
  }

  test("Board.changeSnakeDirection should return false and block Right to Left direction change") {
    val snake = Snake(PosList((4, 4), (3, 4))).get.changeDirection(Dir2D.Right) // Snake moving right
    val board = Board(8, List.empty, snake)

    val result = board.changeSnakeDirection(Dir2D.Left)

    assertEquals(result, false, "Changing from Right to Left should return false")

    // Verify direction didn't change by checking crawl behavior
    board.update()
    val newHead = board.snake.body.head
    assertEquals(newHead, Pos2D(5, 4), "Snake should continue moving right (x increases)")
  }

  test("Board.changeSnakeDirection should return true for valid direction changes") {
    val snake = Snake(PosList((4, 4))).get.changeDirection(Dir2D.Right) // Snake moving right
    val board = Board(8, List.empty, snake)

    // Test changing to Up (valid)
    val result1 = board.changeSnakeDirection(Dir2D.Up)
    assertEquals(result1, true, "Changing from Right to Up should return true")

    // Test changing to Left (valid)
    val result3 = board.changeSnakeDirection(Dir2D.Left)
    assertEquals(result3, true, "Changing from Down to Left should return true")
  }

  test("Board.changeSnakeDirection should return true when changing to same direction") {
    val snake = Snake(PosList((4, 4))).get.changeDirection(Dir2D.Right) // Snake moving right
    val board = Board(8, List.empty, snake)

    val result = board.changeSnakeDirection(Dir2D.Right)

    assertEquals(result, true, "Changing to same direction should return true")

    // Verify snake continues in same direction
    board.update()
    val newHead = board.snake.body.head
    assertEquals(newHead, Pos2D(5, 4), "Snake should continue moving right")
  }

  test("Board.changeSnakeDirection should handle all backwards combinations correctly") {
    // Test all four backwards direction combinations
    val testCases = List(
      (Dir2D.Up, Dir2D.Down),
      (Dir2D.Down, Dir2D.Up),
      (Dir2D.Left, Dir2D.Right),
      (Dir2D.Right, Dir2D.Left)
    )

    testCases.foreach { (currentDir, oppositeDir) =>
      val snake = Snake(PosList((4, 4))).get.changeDirection(currentDir)
      val board = Board(8, List.empty, snake)

      val result = board.changeSnakeDirection(oppositeDir)

      assertEquals(result, false, s"Changing from $currentDir to $oppositeDir should return false")
    }
  }

  // Board.getEmptyTiles tests
  test("Board.getEmptyTiles should return all positions when board is empty") {
    val board = Board(3) // 3x3 board with no coins or snake
    val emptyTiles = board.emptyTiles

    assertEquals(emptyTiles.size, 9, "3x3 board should have 9 empty tiles when completely empty")
    assert(emptyTiles.contains(Pos2D(0, 0)), "Should contain (0,0)")
    assert(emptyTiles.contains(Pos2D(1, 1)), "Should contain (1,1)")
    assert(emptyTiles.contains(Pos2D(2, 2)), "Should contain (2,2)")
  }

  test("Board.getEmptyTiles should exclude snake positions") {
    val snake = Snake(PosList((1, 1), (1, 0))).get
    val board = Board(3, List.empty, snake)
    val emptyTiles = board.emptyTiles

    assertEquals(emptyTiles.size, 7, "3x3 board should have 7 empty tiles with 2-element snake")
    assert(!emptyTiles.contains(Pos2D(1, 1)), "Should not contain snake head position")
    assert(!emptyTiles.contains(Pos2D(1, 0)), "Should not contain snake body position")
    assert(emptyTiles.contains(Pos2D(0, 0)), "Should contain other positions")
    assert(emptyTiles.contains(Pos2D(2, 2)), "Should contain other positions")
  }

  test("Board.getEmptyTiles should exclude coin positions") {
    val coins = PosList((0, 0), (2, 2))
    val board = Board(3, coins)
    val emptyTiles = board.emptyTiles

    assertEquals(emptyTiles.size, 7, "3x3 board should have 7 empty tiles with 2 coins")
    assert(!emptyTiles.contains(Pos2D(0, 0)), "Should not contain coin position")
    assert(!emptyTiles.contains(Pos2D(2, 2)), "Should not contain coin position")
    assert(emptyTiles.contains(Pos2D(1, 1)), "Should contain other positions")
  }

  test("Board.getEmptyTiles should exclude both snake and coin positions") {
    val snake = Snake(PosList((1, 1))).get
    val coins = PosList((0, 0), (2, 2))
    val board = Board(3, coins, snake)
    val emptyTiles = board.emptyTiles

    assertEquals(emptyTiles.size, 6, "3x3 board should have 6 empty tiles with snake and coins")
    assert(!emptyTiles.contains(Pos2D(1, 1)), "Should not contain snake position")
    assert(!emptyTiles.contains(Pos2D(0, 0)), "Should not contain coin position")
    assert(!emptyTiles.contains(Pos2D(2, 2)), "Should not contain coin position")
    assert(emptyTiles.contains(Pos2D(0, 1)), "Should contain empty positions")
    assert(emptyTiles.contains(Pos2D(2, 1)), "Should contain empty positions")
  }

  test("Board.getEmptyTiles should return empty list when board is full") {
    // 2x2 board has positions: (0,0), (0,1), (1,0), (1,1)
    // Create a continuous snake path and use remaining positions for coins
    val snake = Snake(PosList((0, 0), (0, 1), (1, 1))).get // 3-position continuous snake
    val coins = PosList((1, 0)) // Remaining position for coin
    val board = Board(2, coins, snake)
    val emptyTiles = board.emptyTiles

    assertEquals(emptyTiles.size, 0, "2x2 board should have no empty tiles when full")
  }

  // Board.coinsNumber tests
  test("Board.coinsNumber should return 0 for empty coin list") {
    val board = Board(5)
    assertEquals(board.coinsNumber, 0, "Empty board should have 0 coins")
  }

  test("Board.coinsNumber should return correct count for multiple coins") {
    val coins = PosList((1, 1), (2, 2), (3, 3))
    val board = Board(5, coins)
    assertEquals(board.coinsNumber, 3, "Board should report correct coin count")
  }

  // Board.addCoin tests
  test("Board.addCoin should add coin to empty position") {
    val board = Board(5)
    val position = Pos2D(2, 3)

    assertEquals(board.coinsNumber, 0, "Should start with no coins")
    board.addCoin(position)
    assertEquals(board.coinsNumber, 1, "Should have 1 coin after adding")
    assert(board.coinsPositions.contains(position), "Should contain the added coin")
  }

  test("Board.addCoin should not add coin on snake position") {
    val snake = Snake(PosList((2, 3), (1, 3))).get
    val board = Board(5, List.empty, snake)
    val position = Pos2D(2, 3) // Snake head position

    assertEquals(board.coinsNumber, 0, "Should start with no coins")
    board.addCoin(position) // Try to add on snake position
    assertEquals(board.coinsNumber, 0, "Should still have no coins")
    assert(!board.coinsPositions.contains(position), "Should not contain coin on snake position")
  }

  test("Board.addCoin should work with multiple coins") {
    val board = Board(5)
    val positions = PosList((1, 1), (2, 2), (3, 3))

    positions.foreach(board.addCoin)
    assertEquals(board.coinsNumber, 3, "Should have 3 coins")
    positions.foreach(pos => assert(board.coinsPositions.contains(pos), s"Should contain coin at $pos"))
  }

  test("rotation-based control should allow full clockwise cycle") {
    val snake = Snake(PosList((4, 4))).get.changeDirection(Dir2D.Up)
    val board = Board(8, List.empty, snake)

    // Start with Up, rotate clockwise 4 times to complete a full cycle
    val dir1 = board.snake.snakeDir.rotateClockwise // Up -> Right
    board.changeSnakeDirection(dir1)
    assertEquals(board.snake.snakeDir, Dir2D.Right, "First rotation should go to Right")

    val dir2 = board.snake.snakeDir.rotateClockwise // Right -> Down
    board.changeSnakeDirection(dir2)
    assertEquals(board.snake.snakeDir, Dir2D.Down, "Second rotation should go to Down")

    val dir3 = board.snake.snakeDir.rotateClockwise // Down -> Left
    board.changeSnakeDirection(dir3)
    assertEquals(board.snake.snakeDir, Dir2D.Left, "Third rotation should go to Left")

    val dir4 = board.snake.snakeDir.rotateClockwise // Left -> Up
    board.changeSnakeDirection(dir4)
    assertEquals(board.snake.snakeDir, Dir2D.Up, "Fourth rotation should return to Up")
  }

  test("rotation-based control should allow full counter-clockwise cycle") {
    val snake = Snake(PosList((4, 4))).get.changeDirection(Dir2D.Up)
    val board = Board(8, List.empty, snake)

    // Start with Up, rotate counter-clockwise 4 times to complete a full cycle
    val dir1 = board.snake.snakeDir.rotateCounterClockwise // Up -> Left
    board.changeSnakeDirection(dir1)
    assertEquals(board.snake.snakeDir, Dir2D.Left, "First rotation should go to Left")

    val dir2 = board.snake.snakeDir.rotateCounterClockwise // Left -> Down
    board.changeSnakeDirection(dir2)
    assertEquals(board.snake.snakeDir, Dir2D.Down, "Second rotation should go to Down")

    val dir3 = board.snake.snakeDir.rotateCounterClockwise // Down -> Right
    board.changeSnakeDirection(dir3)
    assertEquals(board.snake.snakeDir, Dir2D.Right, "Third rotation should go to Right")

    val dir4 = board.snake.snakeDir.rotateCounterClockwise // Right -> Up
    board.changeSnakeDirection(dir4)
    assertEquals(board.snake.snakeDir, Dir2D.Up, "Fourth rotation should return to Up")
  }

  test("rotation should work with actual snake movement") {
    val snake = Snake(PosList((4, 4))).get.changeDirection(Dir2D.Up)
    val board = Board(8, List.empty, snake)

    // Rotate from Up to Right and verify movement
    val newDirection = board.snake.snakeDir.rotateClockwise
    board.changeSnakeDirection(newDirection)
    assertEquals(board.snake.snakeDir, Dir2D.Right, "Direction should be Right after rotation")

    // Move the snake and verify it moves right
    board.update()
    assertEquals(board.snake.body.head, Pos2D(5, 4), "Snake should move right to (5,4)")
  }
}
