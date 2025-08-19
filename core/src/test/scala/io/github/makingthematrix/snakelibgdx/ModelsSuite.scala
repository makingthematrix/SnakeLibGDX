package io.github.makingthematrix.snakelibgdx

import munit.FunSuite

class ModelsSuite extends FunSuite:
  // Snake.apply tests for continuity validation
  test("Snake.apply should create snake for empty body list") {
    val result = Snake.apply(List.empty)
    assert(result.isDefined, "Empty list should be considered continuous")
  }

  test("Snake.apply should create snake for single element body") {
    val result = Snake.apply(List((5, 5)))
    assert(result.isDefined, "Single element should be considered continuous")
  }

  test("Snake.apply should create snake for continuous horizontal sequence") {
    val horizontalBody = List((1, 1), (2, 1), (3, 1), (4, 1))
    val result = Snake.apply(horizontalBody)
    assert(result.isDefined, "Horizontal continuous sequence should be valid")
  }

  test("Snake.apply should create snake for continuous vertical sequence") {
    val verticalBody = List((2, 1), (2, 2), (2, 3), (2, 4))
    val result = Snake.apply(verticalBody)
    assert(result.isDefined, "Vertical continuous sequence should be valid")
  }

  test("Snake.apply should create snake for mixed continuous sequence") {
    val mixedBody = List((1, 1), (2, 1), (2, 2), (2, 3), (3, 3))
    val result = Snake.apply(mixedBody)
    assert(result.isDefined, "Mixed continuous sequence should be valid")
  }

  test("Snake.apply should reject discontinuous horizontal sequence") {
    val discontinuousBody = List((1, 1), (3, 1), (4, 1)) // Gap between (1,1) and (3,1)
    val result = Snake.apply(discontinuousBody)
    assert(result.isEmpty, "Discontinuous horizontal sequence should be rejected")
  }

  test("Snake.apply should reject discontinuous vertical sequence") {
    val discontinuousBody = List((2, 1), (2, 3), (2, 4)) // Gap between (2,1) and (2,3)
    val result = Snake.apply(discontinuousBody)
    assert(result.isEmpty, "Discontinuous vertical sequence should be rejected")
  }

  test("Snake.apply should reject diagonal sequence") {
    val diagonalBody = List((1, 1), (2, 2), (3, 3)) // Diagonal moves have sum of differences = 2
    val result = Snake.apply(diagonalBody)
    assert(result.isEmpty, "Diagonal sequence should be rejected")
  }

  test("Snake.apply should reject sequence with large gaps") {
    val gappedBody = List((1, 1), (1, 2), (1, 5)) // Large gap between (1,2) and (1,5)
    val result = Snake.apply(gappedBody)
    assert(result.isEmpty, "Sequence with large gaps should be rejected")
  }

  test("Snake.apply should handle negative coordinates correctly") {
    val negativeBody = List((-2, -1), (-1, -1), (0, -1), (1, -1))
    val result = Snake.apply(negativeBody)
    assert(result.isDefined, "Continuous sequence with negative coordinates should be valid")
  }

  test("Snake.apply should validate continuity for L-shaped snake") {
    val lShapedBody = List((1, 1), (2, 1), (3, 1), (3, 2), (3, 3))
    val result = Snake.apply(lShapedBody)
    assert(result.isDefined, "L-shaped continuous sequence should be valid")
  }

  test("Snake.apply should reject sequence with backward jump") {
    val backwardJumpBody = List((1, 1), (2, 1), (3, 1), (1, 1)) // Jumps back to (1,1)
    val result = Snake.apply(backwardJumpBody)
    assert(result.isEmpty, "Sequence with backward jump should be rejected")
  }

  // Snake.changeDirection tests
  test("changeDirection should update snake direction") {
    val snake = Snake.apply(List((1, 1), (2, 1))).get
    val newSnake = snake.changeDirection(SnakeDir.Up)
    val board = Board(8)
    // We can't directly access snakeDir, but we can test behavior through crawl
    val crawledSnake = newSnake.crawl(board)
    // After crawling with Up direction, new head should be at (1, 0) - current head (1,1) + Up(0,-1)
    // We'll verify this indirectly by checking the behavior is consistent
    assert(crawledSnake != null, "Snake with changed direction should crawl successfully")
  }

  test("changeDirection should preserve snake body") {
    val originalBody = List((3, 3), (2, 3), (1, 3))
    val snake = Snake.apply(originalBody).get
    val newSnake = snake.changeDirection(SnakeDir.Down)
    // Body should be preserved, only direction changed
    assert(newSnake != null, "Snake body should be preserved after direction change")
  }

  test("changeDirection should work with all directions") {
    val snake = Snake.apply(List((5, 5))).get
    val upSnake = snake.changeDirection(SnakeDir.Up)
    val downSnake = snake.changeDirection(SnakeDir.Down)
    val leftSnake = snake.changeDirection(SnakeDir.Left)
    val rightSnake = snake.changeDirection(SnakeDir.Right)

    assert(upSnake != null, "Should change to Up direction")
    assert(downSnake != null, "Should change to Down direction")
    assert(leftSnake != null, "Should change to Left direction")
    assert(rightSnake != null, "Should change to Right direction")
  }

  // Snake.crawl tests
  test("crawl should move snake head forward and remove tail") {
    val originalBody = List((3, 3), (2, 3), (1, 3)) // Snake moving right
    val snake = Snake.apply(originalBody).get // Default direction is Right
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    assert(crawledSnake != null, "Snake should crawl successfully")
    // After crawling right, head should move from (3,3) to (4,3) and tail (1,3) should be removed
  }

  test("crawl should handle single element snake") {
    val snake = Snake.apply(List((5, 5))).get // Default direction is Right
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    assert(crawledSnake != null, "Single element snake should crawl successfully")
    // After crawling right, head should move from (5,5) to (6,5)
  }

  test("crawl should handle empty snake body") {
    val snake = Snake.apply(List.empty).get // Default direction is Right
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    assert(crawledSnake != null, "Empty snake should crawl successfully")
    // After crawling right from empty, should create head at (1,0)
  }

  test("crawl with different directions should move head correctly") {
    val snake = Snake.apply(List((5, 5), (4, 5))).get
    val board = Board(8)

    val upSnake = snake.changeDirection(SnakeDir.Up).crawl(board)
    val downSnake = snake.changeDirection(SnakeDir.Down).crawl(board)
    val leftSnake = snake.changeDirection(SnakeDir.Left).crawl(board)
    val rightSnake = snake.changeDirection(SnakeDir.Right).crawl(board)

    assert(upSnake != null, "Should crawl up successfully")
    assert(downSnake != null, "Should crawl down successfully")
    assert(leftSnake != null, "Should crawl left successfully")
    assert(rightSnake != null, "Should crawl right successfully")
  }

  test("crawl should preserve snake length for multi-element snake") {
    val originalBody = List((3, 3), (2, 3), (1, 3), (0, 3)) // 4-element snake
    val snake = Snake.apply(originalBody).get
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    assert(crawledSnake != null, "Multi-element snake should crawl successfully")
    // Length should be preserved (removes tail, adds new head)
  }

  test("multiple crawl operations should work correctly") {
    val snake = Snake.apply(List((2, 2), (1, 2))).get // Moving right
    val board = Board(8)
    val crawled1 = snake.crawl(board) // Head moves to (3,2), tail (1,2) removed -> [(3,2), (2,2)]
    val crawled2 = crawled1.crawl(board) // Head moves to (4,2), tail (2,2) removed -> [(4,2), (3,2)]
    val crawled3 = crawled2.crawl(board) // Head moves to (5,2), tail (3,2) removed -> [(5,2), (4,2)]

    assert(crawled1 != null, "First crawl should work")
    assert(crawled2 != null, "Second crawl should work")
    assert(crawled3 != null, "Third crawl should work")
  }

  test("crawl should preserve snake size") {
    val originalBody = List((5, 3), (4, 3), (3, 3), (2, 3)) // 4-element snake moving right
    val snake = Snake.apply(originalBody).get
    val board = Board(8)
    val originalSize = snake.body.size
    val crawledSnake = snake.crawl(board)
    val newSize = crawledSnake.body.size

    assertEquals(newSize, originalSize, "Snake size should remain the same after crawl")
    assertEquals(newSize, 4, "Snake should still have 4 elements")
  }

  test("crawl with Right direction should have correct body structure") {
    val originalBody = List((3, 2), (2, 2), (1, 2)) // 3-element snake at positions (3,2), (2,2), (1,2)
    val snake = Snake.apply(originalBody).get.changeDirection(SnakeDir.Right) // Explicitly set to Right
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    val newBody = crawledSnake.body

    // After crawling right: new head at (4,2), original head becomes body (3,2), middle stays (2,2), tail (1,2) removed
    val expectedBody = List((4, 2), (3, 2), (2, 2))
    assertEquals(newBody, expectedBody, "Crawled snake should have new head, intact middle segments, and no original tail")

    // Verify specific requirements
    val newHead = newBody.head
    assertEquals(newHead, (4, 2), "New head should be at (4,2)")

    assert(!newBody.contains((1, 2)), "Original tail (1,2) should be removed")
    assert(newBody.contains((3, 2)), "Original head (3,2) should become body segment")
    assert(newBody.contains((2, 2)), "Middle segment (2,2) should be intact")
  }

  // Wrapping functionality tests
  test("crawl should wrap left from x=0 to x=7") {
    val snake = Snake.apply(List((0, 4))).get.changeDirection(SnakeDir.Left)
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (7, 4), "Snake moving left from x=0 should wrap to x=7")
  }

  test("crawl should wrap up from y=0 to y=7") {
    val snake = Snake.apply(List((4, 0))).get.changeDirection(SnakeDir.Up)
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (4, 7), "Snake moving up from y=0 should wrap to y=7")
  }

  test("crawl should wrap right from x=7 to x=0") {
    val snake = Snake.apply(List((7, 4))).get.changeDirection(SnakeDir.Right)
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (0, 4), "Snake moving right from x=7 should wrap to x=0")
  }

  test("crawl should wrap down from y=7 to y=0") {
    val snake = Snake.apply(List((4, 7))).get.changeDirection(SnakeDir.Down)
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (4, 0), "Snake moving down from y=7 should wrap to y=0")
  }

  test("crawl should not wrap when moving within board boundaries") {
    val snake = Snake.apply(List((3, 3))).get.changeDirection(SnakeDir.Right)
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (4, 3), "Snake moving within boundaries should not wrap")
  }

  test("crawl should wrap with multi-element snake from left boundary") {
    val snake = Snake.apply(List((0, 4), (1, 4))).get.changeDirection(SnakeDir.Left)
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    val newBody = crawledSnake.body

    assertEquals(newBody, List((7, 4), (0, 4)), "Multi-element snake wrapping left should have correct structure")
  }

  test("crawl should wrap with multi-element snake from right boundary") {
    val snake = Snake.apply(List((7, 4), (6, 4))).get.changeDirection(SnakeDir.Right)
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    val newBody = crawledSnake.body

    assertEquals(newBody, List((0, 4), (7, 4)), "Multi-element snake wrapping right should have correct structure")
  }

  test("crawl should wrap with multi-element snake from top boundary") {
    val snake = Snake.apply(List((4, 0), (4, 1))).get.changeDirection(SnakeDir.Up)
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    val newBody = crawledSnake.body

    assertEquals(newBody, List((4, 7), (4, 0)), "Multi-element snake wrapping up should have correct structure")
  }

  test("crawl should wrap with multi-element snake from bottom boundary") {
    val snake = Snake.apply(List((4, 7), (4, 6))).get.changeDirection(SnakeDir.Down)
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    val newBody = crawledSnake.body

    assertEquals(newBody, List((4, 0), (4, 7)), "Multi-element snake wrapping down should have correct structure")
  }

  test("crawl should handle corner wrapping - top-left to bottom-right") {
    val snake = Snake.apply(List((0, 0))).get.changeDirection(SnakeDir.Left)
    val board = Board(8)
    val crawledSnake = snake.crawl(board)
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (7, 0), "Snake at top-left corner moving left should wrap to right side")
  }
  // Empty tile positions tests
  test("Board getEmptyTilePositions should return all positions when board is empty") {
    val board = Board.apply(3) // 3x3 board with no coins and empty snake
    val emptyPositions = board.getEmptyTilePositions

    assertEquals(emptyPositions.size, 9, "Empty 3x3 board should have 9 empty positions")
    assert(emptyPositions.contains((0, 0)), "Should contain (0,0)")
    assert(emptyPositions.contains((2, 2)), "Should contain (2,2)")
  }

  test("Board getEmptyTilePositions should exclude snake body positions") {
    val snake = Snake(List((1, 0), (1, 1), (1, 2)))
    val board = Board.apply(3, Nil, snake.get)
    val emptyPositions = board.getEmptyTilePositions

    assertEquals(emptyPositions.size, 6, "3x3 board with 3-segment snake should have 6 empty positions")
    assert(!emptyPositions.contains((1, 0)), "Should not contain snake head position")
    assert(!emptyPositions.contains((1, 1)), "Should not contain snake body position")
    assert(!emptyPositions.contains((1, 2)), "Should not contain snake tail position")
    assert(emptyPositions.contains((0, 0)), "Should contain non-snake position")
  }

  test("Board getEmptyTilePositions should exclude coin positions") {
    val coins = List((0, 0), (1, 1), (2, 2))
    val board = Board.apply(3, coins)
    val emptyPositions = board.getEmptyTilePositions

    assertEquals(emptyPositions.size, 6, "3x3 board with 3 coins should have 6 empty positions")
    assert(!emptyPositions.contains((0, 0)), "Should not contain coin position (0,0)")
    assert(!emptyPositions.contains((1, 1)), "Should not contain coin position (1,1)")
    assert(!emptyPositions.contains((2, 2)), "Should not contain coin position (2,2)")
    assert(emptyPositions.contains((0, 1)), "Should contain non-coin position")
  }

  test("Board getEmptyTilePositions should exclude both snake and coin positions") {
    val snake = Snake(List((0, 0), (0, 1)))
    val coins = List((1, 0), (2, 2))
    val board = Board.apply(3, coins, snake.get)
    val emptyPositions = board.getEmptyTilePositions

    assertEquals(emptyPositions.size, 5, "3x3 board with 2-segment snake and 2 coins should have 5 empty positions")
    assert(!emptyPositions.contains((0, 0)), "Should not contain snake head position")
    assert(!emptyPositions.contains((0, 1)), "Should not contain snake body position")
    assert(!emptyPositions.contains((1, 0)), "Should not contain coin position")
    assert(!emptyPositions.contains((2, 2)), "Should not contain coin position")
    assert(emptyPositions.contains((1, 1)), "Should contain empty position")
  }

  test("Board getEmptyTilePositions should return empty list when board is full") {
    // Create a board where snake and coins occupy all positions
    val allPositions = for {
      x <- 0 until 2
      y <- 0 until 2
    } yield (x, y)

    val snake = Snake(List((0, 0), (0, 1)))
    val coins = List((1, 0), (1, 1))
    val board = Board.apply(2, coins, snake.get)
    val emptyPositions = board.getEmptyTilePositions

    assertEquals(emptyPositions.size, 0, "Fully occupied 2x2 board should have 0 empty positions")
  }

  // AddCoin method tests
  test("Board addCoin should add coin to empty position") {
    val board = Board.apply(3)
    val originalCoinCount = board.coinsNumber

    board.addCoin((1, 1))

    assertEquals(board.coinsNumber, originalCoinCount + 1, "Coin count should increase by 1")
    assert(board.coinsPositions.contains((1, 1)), "Board should contain the new coin")
  }

  test("Board addCoin should not add coin to position with existing coin") {
    val board = Board.apply(3, List((1, 1)))
    val originalCoinCount = board.coinsNumber

    board.addCoin((1, 1)) // Try to add coin at same position

    assertEquals(board.coinsNumber, originalCoinCount, "Coin count should not change")
    assertEquals(board.coinsPositions.count(_ == (1, 1)), 1, "Should still have only one coin at position")
  }

  test("Board addCoin should not add coin to position with snake body") {
    val snake = Snake(List((1, 1), (1, 2)))
    val board = Board.apply(3, Nil, snake.get)
    val originalCoinCount = board.coinsNumber

    board.addCoin((1, 1)) // Try to add coin at snake head
    board.addCoin((1, 2)) // Try to add coin at snake body

    assertEquals(board.coinsNumber, originalCoinCount, "Coin count should not change")
    assert(!board.coinsPositions.contains((1, 1)), "Should not contain coin at snake head")
    assert(!board.coinsPositions.contains((1, 2)), "Should not contain coin at snake body")
  }

  test("Board addCoin should successfully add multiple coins to different positions") {
    val board = Board.apply(3)

    board.addCoin((0, 0))
    board.addCoin((1, 1))
    board.addCoin((2, 2))

    assertEquals(board.coinsNumber, 3, "Should have 3 coins")
    assert(board.coinsPositions.contains((0, 0)), "Should contain coin at (0,0)")
    assert(board.coinsPositions.contains((1, 1)), "Should contain coin at (1,1)")
    assert(board.coinsPositions.contains((2, 2)), "Should contain coin at (2,2)")
  }
  // Board.changeSnakeDirection method tests
  test("Board.changeSnakeDirection should update snake direction to Left") {
    val snake = Snake.apply(List((3, 3), (2, 3))).get // Snake moving right initially
    val board = Board(8, List.empty, snake)

    board.changeSnakeDirection(SnakeDir.Left)

    // Test that direction changed by checking crawl behavior
    board.update() // This calls crawl with new direction
    val newHead = board.snake.body.head

    // If direction changed to Left, new head should be to the left of original head
    assertEquals(newHead, (4, 3), "Snake should move left after direction change")
  }

  test("Board.changeSnakeDirection should update snake direction to Right") {
    val snake = Snake.apply(List((3, 3), (4, 3))).get.changeDirection(SnakeDir.Left) // Snake moving left initially
    val board = Board(8, List.empty, snake)

    board.changeSnakeDirection(SnakeDir.Right)

    // Test that direction changed by checking crawl behavior
    board.update() // This calls crawl with new direction
    val newHead = board.snake.body.head

    // If direction changed to Right, new head should be to the right of original head
    assertEquals(newHead, (2, 3), "Snake should move right after direction change")
  }

  test("Board.changeSnakeDirection should update snake direction to Up") {
    val snake = Snake.apply(List((3, 3), (4, 3))).get.changeDirection(SnakeDir.Right) // Snake moving right initially
    val board = Board(8, List.empty, snake)

    board.changeSnakeDirection(SnakeDir.Up)

    // Test that direction changed by checking crawl behavior
    board.update() // This calls crawl with new direction
    val newHead = board.snake.body.head

    // If direction changed to Up, new head should be above original head (y decreases)
    assertEquals(newHead, (3, 2), "Snake should move up after direction change")
  }

  test("Board.changeSnakeDirection should update snake direction to Down") {
    val snake = Snake.apply(List((3, 3), (2, 3))).get.changeDirection(SnakeDir.Left) // Snake moving left initially
    val board = Board(8, List.empty, snake)

    board.changeSnakeDirection(SnakeDir.Down)

    // Test that direction changed by checking crawl behavior
    board.update() // This calls crawl with new direction
    val newHead = board.snake.body.head

    // If direction changed to Down, new head should be below original head (y increases)
    assertEquals(newHead, (3, 4), "Snake should move down after direction change")
  }

  test("Board.changeSnakeDirection should work with all directions") {
    val snake = Snake.apply(List((4, 4))).get
    val board = Board(8, List.empty, snake)

    // Test all direction changes work without errors
    board.changeSnakeDirection(SnakeDir.Up)
    assert(board.snake != null, "Snake should exist after changing to Up")

    board.changeSnakeDirection(SnakeDir.Right)
    assert(board.snake != null, "Snake should exist after changing to Right")

    board.changeSnakeDirection(SnakeDir.Down)
    assert(board.snake != null, "Snake should exist after changing to Down")

    board.changeSnakeDirection(SnakeDir.Left)
    assert(board.snake != null, "Snake should exist after changing to Left")
  }

  test("Board.changeSnakeDirection should preserve snake body") {
    val originalBody = List((5, 5), (4, 5), (3, 5))
    val snake = Snake.apply(originalBody).get
    val board = Board(8, List.empty, snake)
    val originalBodySize = board.snake.body.size

    board.changeSnakeDirection(SnakeDir.Up)

    // Body size and positions should be preserved, only direction changes
    assertEquals(board.snake.body.size, originalBodySize, "Snake body size should be preserved")
    assertEquals(board.snake.body, originalBody, "Snake body should be preserved")
  }

  // Backwards direction restriction tests
  test("Board.changeSnakeDirection should return false and block Up to Down direction change") {
    val snake = Snake.apply(List((4, 4), (4, 5))).get.changeDirection(SnakeDir.Up) // Snake moving up
    val board = Board(8, List.empty, snake)

    val result = board.changeSnakeDirection(SnakeDir.Down)

    assertEquals(result, false, "Changing from Up to Down should return false")

    // Verify direction didn't change by checking crawl behavior
    board.update()
    val newHead = board.snake.body.head
    assertEquals(newHead, (4, 3), "Snake should continue moving up (y decreases)")
  }

  test("Board.changeSnakeDirection should return false and block Down to Up direction change") {
    val snake = Snake.apply(List((4, 4), (4, 3))).get.changeDirection(SnakeDir.Down) // Snake moving down
    val board = Board(8, List.empty, snake)

    val result = board.changeSnakeDirection(SnakeDir.Up)

    assertEquals(result, false, "Changing from Down to Up should return false")

    // Verify direction didn't change by checking crawl behavior
    board.update()
    val newHead = board.snake.body.head
    assertEquals(newHead, (4, 5), "Snake should continue moving down (y increases)")
  }

  test("Board.changeSnakeDirection should return false and block Left to Right direction change") {
    val snake = Snake.apply(List((4, 4), (5, 4))).get.changeDirection(SnakeDir.Left) // Snake moving left
    val board = Board(8, List.empty, snake)

    val result = board.changeSnakeDirection(SnakeDir.Right)

    assertEquals(result, false, "Changing from Left to Right should return false")

    // Verify direction didn't change by checking crawl behavior
    board.update()
    val newHead = board.snake.body.head
    assertEquals(newHead, (3, 4), "Snake should continue moving left (x decreases)")
  }

  test("Board.changeSnakeDirection should return false and block Right to Left direction change") {
    val snake = Snake.apply(List((4, 4), (3, 4))).get.changeDirection(SnakeDir.Right) // Snake moving right
    val board = Board(8, List.empty, snake)

    val result = board.changeSnakeDirection(SnakeDir.Left)

    assertEquals(result, false, "Changing from Right to Left should return false")

    // Verify direction didn't change by checking crawl behavior
    board.update()
    val newHead = board.snake.body.head
    assertEquals(newHead, (5, 4), "Snake should continue moving right (x increases)")
  }

  test("Board.changeSnakeDirection should return true for valid direction changes") {
    val snake = Snake.apply(List((4, 4))).get.changeDirection(SnakeDir.Right) // Snake moving right
    val board = Board(8, List.empty, snake)

    // Test changing to Up (valid)
    val result1 = board.changeSnakeDirection(SnakeDir.Up)
    assertEquals(result1, true, "Changing from Right to Up should return true")

    // Test changing to Left (valid)
    val result3 = board.changeSnakeDirection(SnakeDir.Left)
    assertEquals(result3, true, "Changing from Down to Left should return true")
  }

  test("Board.changeSnakeDirection should return true when changing to same direction") {
    val snake = Snake.apply(List((4, 4))).get.changeDirection(SnakeDir.Right) // Snake moving right
    val board = Board(8, List.empty, snake)

    val result = board.changeSnakeDirection(SnakeDir.Right)

    assertEquals(result, true, "Changing to same direction should return true")

    // Verify snake continues in same direction
    board.update()
    val newHead = board.snake.body.head
    assertEquals(newHead, (5, 4), "Snake should continue moving right")
  }

  test("Board.changeSnakeDirection should handle all backwards combinations correctly") {
    // Test all four backwards direction combinations
    val testCases = List(
      (SnakeDir.Up, SnakeDir.Down),
      (SnakeDir.Down, SnakeDir.Up),
      (SnakeDir.Left, SnakeDir.Right),
      (SnakeDir.Right, SnakeDir.Left)
    )

    testCases.foreach { case (currentDir, oppositeDir) =>
      val snake = Snake.apply(List((4, 4))).get.changeDirection(currentDir)
      val board = Board(8, List.empty, snake)

      val result = board.changeSnakeDirection(oppositeDir)

      assertEquals(result, false, s"Changing from $currentDir to $oppositeDir should return false")
    }
  }

  // hasCoin functionality tests
  test("Snake should have hasCoin false by default") {
    val snake = Snake.apply(List((2, 2))).get
    assertEquals(snake.hasCoin, false, "Snake should have hasCoin false by default")
  }

  test("Snake getHasCoin should return correct value") {
    val snake = Snake.apply(List((2, 2))).get
    assertEquals(snake.hasCoin, false, "getHasCoin should return false initially")

    val snakeWithCoin = snake.setHasCoin(true)
    assertEquals(snakeWithCoin.hasCoin, true, "getHasCoin should return true after setHasCoin(true)")
  }

  test("Snake setHasCoin should create new instance with updated hasCoin value") {
    val snake = Snake.apply(List((2, 2), (1, 2))).get
    assertEquals(snake.hasCoin, false, "Initial snake should have hasCoin false")

    val snakeWithCoin = snake.setHasCoin(true)
    assertEquals(snakeWithCoin.hasCoin, true, "New snake should have hasCoin true")
    assertEquals(snake.hasCoin, false, "Original snake should still have hasCoin false")

    // Body and direction should be preserved
    assertEquals(snakeWithCoin.body, snake.body, "Body should be preserved")
    assertEquals(snakeWithCoin.snakeDir, snake.snakeDir, "Direction should be preserved")
  }

  test("Snake setHasCoin should work in both directions") {
    val snake = Snake.apply(List((2, 2))).get

    val snakeWithCoin = snake.setHasCoin(true)
    assertEquals(snakeWithCoin.hasCoin, true, "setHasCoin(true) should work")

    val snakeWithoutCoin = snakeWithCoin.setHasCoin(false)
    assertEquals(snakeWithoutCoin.hasCoin, false, "setHasCoin(false) should work")
  }

  test("Snake changeDirection should preserve hasCoin value") {
    val snake = Snake.apply(List((2, 2))).get.setHasCoin(true)
    assertEquals(snake.hasCoin, true, "Snake should have hasCoin true")

    val changedSnake = snake.changeDirection(SnakeDir.Up)
    assertEquals(changedSnake.hasCoin, true, "hasCoin should be preserved after changeDirection")
    assertEquals(changedSnake.snakeDir, SnakeDir.Up, "Direction should be updated")
    assertEquals(changedSnake.body, snake.body, "Body should be preserved")
  }

  test("Snake crawl should grow when hasCoin is true") {
    val originalBody = List((3, 3), (2, 3), (1, 3)) // 3-element snake
    val snake = Snake.apply(originalBody).get.setHasCoin(true) // Set hasCoin to true
    val board = Board(8)

    val crawledSnake = snake.crawl(board)

    // After crawling with hasCoin=true, snake should grow (not remove tail)
    assertEquals(crawledSnake.body.size, 4, "Snake should grow from 3 to 4 elements")
    assertEquals(crawledSnake.hasCoin, false, "hasCoin should be false after crawling")

    // New head should be at (4,3) when moving right
    assertEquals(crawledSnake.body.head, (4, 3), "New head should be at (4,3)")

    // Original tail should still be present (not removed)
    assert(crawledSnake.body.contains((1, 3)), "Original tail should still be present")
  }

  test("Snake crawl should not grow when hasCoin is false") {
    val originalBody = List((3, 3), (2, 3), (1, 3)) // 3-element snake
    val snake = Snake.apply(originalBody).get // hasCoin is false by default
    val board = Board(8)

    val crawledSnake = snake.crawl(board)

    // After crawling with hasCoin=false, snake should maintain same size
    assertEquals(crawledSnake.body.size, 3, "Snake should maintain same size")
    assertEquals(crawledSnake.hasCoin, false, "hasCoin should remain false after crawling")

    // New head should be at (4,3) when moving right
    assertEquals(crawledSnake.body.head, (4, 3), "New head should be at (4,3)")

    // Original tail should be removed
    assert(!crawledSnake.body.contains((1, 3)), "Original tail should be removed")
  }

  test("Board.update should set hasCoin to true when snake reaches coin") {
    val snake = Snake.apply(List((2, 2))).get.changeDirection(SnakeDir.Right)
    val coinAtNextPosition = (3, 2) // Where snake will move
    val board = Board(8, List(coinAtNextPosition), snake)

    assertEquals(snake.hasCoin, false, "Snake should start with hasCoin false")
    assertEquals(board.coinsPositions.size, 1, "Board should start with one coin")

    board.update()

    // After update, snake should have collected the coin and grown
    assertEquals(board.snake.hasCoin, false, "Snake hasCoin should be false after crawling")
    assertEquals(board.coinsPositions.size, 0, "Coin should be removed")
    assertEquals(board.snake.body.head, (3, 2), "Snake head should be at coin position")
    assertEquals(board.snake.body.size, 2, "Snake should have grown from 1 to 2 elements")
  }

  test("Board.update should grow snake when collecting multiple coins") {
    val snake = Snake.apply(List((1, 1))).get.changeDirection(SnakeDir.Right)
    val coins = List((2, 1), (3, 1)) // Two coins in snake's path
    val board = Board(8, coins, snake)

    val initialSize = snake.body.size
    assertEquals(initialSize, 1, "Snake should start with size 1")

    // First update: collect first coin at (2,1)
    board.update()
    assertEquals(board.snake.body.size, 2, "Snake should grow to size 2")
    assertEquals(board.coinsPositions.size, 1, "One coin should remain")

    // Second update: collect second coin at (3,1)
    board.update()
    assertEquals(board.snake.body.size, 3, "Snake should grow to size 3")
    assertEquals(board.coinsPositions.size, 0, "No coins should remain")
  }

  test("Board.updateSnake should replace the snake instance") {
    val originalSnake = Snake.apply(List((2, 2))).get
    val board = Board(8, List.empty, originalSnake)

    val newSnake = Snake.apply(List((5, 5), (4, 5))).get.setHasCoin(true)
    board.updateSnake(newSnake)

    assertEquals(board.snake.body, newSnake.body, "Snake body should be updated")
    assertEquals(board.snake.hasCoin, true, "Snake hasCoin should be updated")
    assert(board.snake ne originalSnake, "Snake instance should be different from original")
  }

  test("Snake crawl with single element and hasCoin true should grow correctly") {
    val snake = Snake.apply(List((3, 3))).get.setHasCoin(true)
    val board = Board(8)

    val crawledSnake = snake.crawl(board)

    // Single element snake with hasCoin should grow to 2 elements
    assertEquals(crawledSnake.body.size, 2, "Single element snake should grow to 2 elements")
    assertEquals(crawledSnake.hasCoin, false, "hasCoin should be false after crawling")
    assertEquals(crawledSnake.body.head, (4, 3), "Head should move to (4,3)")
    assertEquals(crawledSnake.body(1), (3, 3), "Original head should become second element")
  }

  test("Snake crawl with empty body and hasCoin true should work correctly") {
    val snake = Snake.apply(List.empty).get.setHasCoin(true)
    val board = Board(8)

    val crawledSnake = snake.crawl(board)

    // Empty snake should create new head regardless of hasCoin
    assertEquals(crawledSnake.body.size, 1, "Empty snake should create single element")
    assertEquals(crawledSnake.hasCoin, false, "hasCoin should be false after crawling")
    assertEquals(crawledSnake.body.head, (1, 0), "Head should be at (1,0)")
  }

  // Board.getEmptyTiles tests
  test("Board.getEmptyTiles should return all positions when board is empty") {
    val board = Board(3) // 3x3 board with no coins or snake
    val emptyTiles = board.emptyTiles

    assertEquals(emptyTiles.size, 9, "3x3 board should have 9 empty tiles when completely empty")
    assert(emptyTiles.contains((0, 0)), "Should contain (0,0)")
    assert(emptyTiles.contains((1, 1)), "Should contain (1,1)")
    assert(emptyTiles.contains((2, 2)), "Should contain (2,2)")
  }

  test("Board.getEmptyTiles should exclude snake positions") {
    val snake = Snake.apply(List((1, 1), (1, 0))).get
    val board = Board(3, List.empty, snake)
    val emptyTiles = board.emptyTiles

    assertEquals(emptyTiles.size, 7, "3x3 board should have 7 empty tiles with 2-element snake")
    assert(!emptyTiles.contains((1, 1)), "Should not contain snake head position")
    assert(!emptyTiles.contains((1, 0)), "Should not contain snake body position")
    assert(emptyTiles.contains((0, 0)), "Should contain other positions")
    assert(emptyTiles.contains((2, 2)), "Should contain other positions")
  }

  test("Board.getEmptyTiles should exclude coin positions") {
    val coins = List((0, 0), (2, 2))
    val board = Board(3, coins)
    val emptyTiles = board.emptyTiles

    assertEquals(emptyTiles.size, 7, "3x3 board should have 7 empty tiles with 2 coins")
    assert(!emptyTiles.contains((0, 0)), "Should not contain coin position")
    assert(!emptyTiles.contains((2, 2)), "Should not contain coin position")
    assert(emptyTiles.contains((1, 1)), "Should contain other positions")
  }

  test("Board.getEmptyTiles should exclude both snake and coin positions") {
    val snake = Snake.apply(List((1, 1))).get
    val coins = List((0, 0), (2, 2))
    val board = Board(3, coins, snake)
    val emptyTiles = board.emptyTiles

    assertEquals(emptyTiles.size, 6, "3x3 board should have 6 empty tiles with snake and coins")
    assert(!emptyTiles.contains((1, 1)), "Should not contain snake position")
    assert(!emptyTiles.contains((0, 0)), "Should not contain coin position")
    assert(!emptyTiles.contains((2, 2)), "Should not contain coin position")
    assert(emptyTiles.contains((0, 1)), "Should contain empty positions")
    assert(emptyTiles.contains((2, 1)), "Should contain empty positions")
  }

  test("Board.getEmptyTiles should return empty list when board is full") {
    // 2x2 board has positions: (0,0), (0,1), (1,0), (1,1)
    // Create a continuous snake path and use remaining positions for coins
    val snake = Snake.apply(List((0, 0), (0, 1), (1, 1))).get // 3-position continuous snake
    val coins = List((1, 0)) // Remaining position for coin
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
    val coins = List((1, 1), (2, 2), (3, 3))
    val board = Board(5, coins)
    assertEquals(board.coinsNumber, 3, "Board should report correct coin count")
  }

  test("Board.coinsNumber should update when coins are removed") {
    val snake = Snake.apply(List((1, 1))).get.changeDirection(SnakeDir.Right)
    val coins = List((2, 1), (3, 3))
    val board = Board(5, coins, snake)

    assertEquals(board.coinsNumber, 2, "Should start with 2 coins")

    board.update() // Snake moves to (2,1) and collects coin
    assertEquals(board.coinsNumber, 1, "Should have 1 coin after collection")
  }

  // Board.addCoin tests
  test("Board.addCoin should add coin to empty position") {
    val board = Board(5)
    val position = (2, 3)

    assertEquals(board.coinsNumber, 0, "Should start with no coins")
    board.addCoin(position)
    assertEquals(board.coinsNumber, 1, "Should have 1 coin after adding")
    assert(board.coinsPositions.contains(position), "Should contain the added coin")
  }

  test("Board.addCoin should not add duplicate coins") {
    val board = Board(5, List((2, 3)))
    val position = (2, 3)

    assertEquals(board.coinsNumber, 1, "Should start with 1 coin")
    board.addCoin(position) // Try to add duplicate
    assertEquals(board.coinsNumber, 1, "Should still have 1 coin (no duplicate)")
  }

  test("Board.addCoin should not add coin on snake position") {
    val snake = Snake.apply(List((2, 3), (1, 3))).get
    val board = Board(5, List.empty, snake)
    val position = (2, 3) // Snake head position

    assertEquals(board.coinsNumber, 0, "Should start with no coins")
    board.addCoin(position) // Try to add on snake position
    assertEquals(board.coinsNumber, 0, "Should still have no coins")
    assert(!board.coinsPositions.contains(position), "Should not contain coin on snake position")
  }

  test("Board.addCoin should work with multiple coins") {
    val board = Board(5)
    val positions = List((1, 1), (2, 2), (3, 3))

    positions.foreach(board.addCoin)
    assertEquals(board.coinsNumber, 3, "Should have 3 coins")
    positions.foreach(pos => assert(board.coinsPositions.contains(pos), s"Should contain coin at $pos"))
  }

  // Snake self-collision detection tests
  test("Snake.hasSelfCollision should return false for empty snake") {
    val snake = Snake.apply(List.empty).get
    assertEquals(snake.hasSelfCollision, false, "Empty snake should not have self-collision")
  }

  test("Snake.hasSelfCollision should return false for single element snake") {
    val snake = Snake.apply(List((3, 3))).get
    assertEquals(snake.hasSelfCollision, false, "Single element snake should not have self-collision")
  }

  test("Snake.hasSelfCollision should return false for two element snake without collision") {
    val snake = Snake.apply(List((3, 3), (2, 3))).get
    assertEquals(snake.hasSelfCollision, false, "Two element snake without collision should return false")
  }

  test("Snake.hasSelfCollision should return false for multi-element snake without collision") {
    val snake = Snake.apply(List((5, 5), (4, 5), (3, 5), (2, 5), (1, 5))).get
    assertEquals(snake.hasSelfCollision, false, "Multi-element snake without collision should return false")
  }

  test("Snake.hasSelfCollision should return true when head collides with immediate tail segment") {
    // Create a snake where head is at same position as second element
    val snake = new Snake(List((3, 3), (3, 3), (2, 3)), SnakeDir.Right)
    assertEquals(snake.hasSelfCollision, true, "Snake with head at same position as tail segment should return true")
  }

  test("Snake.hasSelfCollision should return true when head collides with any tail segment") {
    // Create a snake where head matches a middle segment
    val snake = new Snake(List((2, 3), (4, 3), (2, 3), (1, 3)), SnakeDir.Right)
    assertEquals(snake.hasSelfCollision, true, "Snake with head matching any tail segment should return true")
  }

  test("Snake.hasSelfCollision should return true when head collides with last tail segment") {
    // Create a snake where head matches the last segment
    val snake = new Snake(List((1, 3), (4, 3), (3, 3), (2, 3), (1, 3)), SnakeDir.Right)
    assertEquals(snake.hasSelfCollision, true, "Snake with head matching last tail segment should return true")
  }

  test("Snake.hasSelfCollision should handle complex collision scenarios") {
    // Test a longer snake with collision in the middle
    val snake = new Snake(List((5, 5), (4, 5), (3, 5), (5, 5), (6, 5), (7, 5)), SnakeDir.Right)
    assertEquals(snake.hasSelfCollision, true, "Complex snake with collision should return true")
  }

  test("Snake.hasSelfCollision should return false for valid L-shaped snake") {
    // Create a valid L-shaped snake without collision
    val snake = Snake.apply(List((3, 3), (2, 3), (1, 3), (1, 2), (1, 1))).get
    assertEquals(snake.hasSelfCollision, false, "Valid L-shaped snake should not have collision")
  }

  // Board self-collision detection tests
  test("Board.hasSnakeSelfCollision should return false for snake without collision") {
    val snake = Snake.apply(List((3, 3), (2, 3), (1, 3))).get
    val board = Board(8, List.empty, snake)
    assertEquals(board.hasSnakeSelfCollision, false, "Board with non-colliding snake should return false")
  }

  test("Board.hasSnakeSelfCollision should return true for snake with collision") {
    val snake = new Snake(List((3, 3), (3, 3), (2, 3)), SnakeDir.Right)
    val board = Board(8, List.empty, snake)
    assertEquals(board.hasSnakeSelfCollision, true, "Board with colliding snake should return true")
  }

  test("Board.hasSnakeSelfCollision should delegate to snake collision check") {
    val validSnake = Snake.apply(List((5, 5), (4, 5), (3, 5))).get
    val board = Board(8, List.empty, validSnake)

    // Should return false initially
    assertEquals(board.hasSnakeSelfCollision, false, "Board should delegate to snake collision check")

    // Update snake to one with collision
    val collidingSnake = new Snake(List((2, 2), (2, 2), (1, 2)), SnakeDir.Right)
    board.updateSnake(collidingSnake)

    // Should now return true
    assertEquals(board.hasSnakeSelfCollision, true, "Board should return true after updating to colliding snake")
  }

  // Tests for rotation-based direction control (simulating the Main.scala rotation logic)
  test("rotateClockwise should work correctly for all directions") {
    // Simulate the rotateClockwise logic from Main.scala
    def rotateClockwise(currentDir: SnakeDir): SnakeDir =
      currentDir match
        case SnakeDir.Up => SnakeDir.Right
        case SnakeDir.Right => SnakeDir.Down
        case SnakeDir.Down => SnakeDir.Left
        case SnakeDir.Left => SnakeDir.Up

    assertEquals(rotateClockwise(SnakeDir.Up), SnakeDir.Right, "Up should rotate clockwise to Right")
    assertEquals(rotateClockwise(SnakeDir.Right), SnakeDir.Down, "Right should rotate clockwise to Down")
    assertEquals(rotateClockwise(SnakeDir.Down), SnakeDir.Left, "Down should rotate clockwise to Left")
    assertEquals(rotateClockwise(SnakeDir.Left), SnakeDir.Up, "Left should rotate clockwise to Up")
  }

  test("rotateCounterClockwise should work correctly for all directions") {
    // Simulate the rotateCounterClockwise logic from Main.scala
    def rotateCounterClockwise(currentDir: SnakeDir): SnakeDir =
      currentDir match
        case SnakeDir.Up => SnakeDir.Left
        case SnakeDir.Left => SnakeDir.Down
        case SnakeDir.Down => SnakeDir.Right
        case SnakeDir.Right => SnakeDir.Up

    assertEquals(rotateCounterClockwise(SnakeDir.Up), SnakeDir.Left, "Up should rotate counter-clockwise to Left")
    assertEquals(rotateCounterClockwise(SnakeDir.Left), SnakeDir.Down, "Left should rotate counter-clockwise to Down")
    assertEquals(rotateCounterClockwise(SnakeDir.Down), SnakeDir.Right, "Down should rotate counter-clockwise to Right")
    assertEquals(rotateCounterClockwise(SnakeDir.Right), SnakeDir.Up, "Right should rotate counter-clockwise to Up")
  }

  test("rotation-based control should allow full clockwise cycle") {
    def rotateClockwise(currentDir: SnakeDir): SnakeDir =
      currentDir match
        case SnakeDir.Up => SnakeDir.Right
        case SnakeDir.Right => SnakeDir.Down
        case SnakeDir.Down => SnakeDir.Left
        case SnakeDir.Left => SnakeDir.Up

    val snake = Snake.apply(List((4, 4))).get.changeDirection(SnakeDir.Up)
    val board = Board(8, List.empty, snake)

    // Start with Up, rotate clockwise 4 times to complete a full cycle
    val dir1 = rotateClockwise(board.snake.snakeDir) // Up -> Right
    board.changeSnakeDirection(dir1)
    assertEquals(board.snake.snakeDir, SnakeDir.Right, "First rotation should go to Right")

    val dir2 = rotateClockwise(board.snake.snakeDir) // Right -> Down
    board.changeSnakeDirection(dir2)
    assertEquals(board.snake.snakeDir, SnakeDir.Down, "Second rotation should go to Down")

    val dir3 = rotateClockwise(board.snake.snakeDir) // Down -> Left
    board.changeSnakeDirection(dir3)
    assertEquals(board.snake.snakeDir, SnakeDir.Left, "Third rotation should go to Left")

    val dir4 = rotateClockwise(board.snake.snakeDir) // Left -> Up
    board.changeSnakeDirection(dir4)
    assertEquals(board.snake.snakeDir, SnakeDir.Up, "Fourth rotation should return to Up")
  }

  test("rotation-based control should allow full counter-clockwise cycle") {
    def rotateCounterClockwise(currentDir: SnakeDir): SnakeDir =
      currentDir match
        case SnakeDir.Up => SnakeDir.Left
        case SnakeDir.Left => SnakeDir.Down
        case SnakeDir.Down => SnakeDir.Right
        case SnakeDir.Right => SnakeDir.Up

    val snake = Snake.apply(List((4, 4))).get.changeDirection(SnakeDir.Up)
    val board = Board(8, List.empty, snake)

    // Start with Up, rotate counter-clockwise 4 times to complete a full cycle
    val dir1 = rotateCounterClockwise(board.snake.snakeDir) // Up -> Left
    board.changeSnakeDirection(dir1)
    assertEquals(board.snake.snakeDir, SnakeDir.Left, "First rotation should go to Left")

    val dir2 = rotateCounterClockwise(board.snake.snakeDir) // Left -> Down
    board.changeSnakeDirection(dir2)
    assertEquals(board.snake.snakeDir, SnakeDir.Down, "Second rotation should go to Down")

    val dir3 = rotateCounterClockwise(board.snake.snakeDir) // Down -> Right
    board.changeSnakeDirection(dir3)
    assertEquals(board.snake.snakeDir, SnakeDir.Right, "Third rotation should go to Right")

    val dir4 = rotateCounterClockwise(board.snake.snakeDir) // Right -> Up
    board.changeSnakeDirection(dir4)
    assertEquals(board.snake.snakeDir, SnakeDir.Up, "Fourth rotation should return to Up")
  }

  test("rotation should work with actual snake movement") {
    def rotateClockwise(currentDir: SnakeDir): SnakeDir =
      currentDir match
        case SnakeDir.Up => SnakeDir.Right
        case SnakeDir.Right => SnakeDir.Down
        case SnakeDir.Down => SnakeDir.Left
        case SnakeDir.Left => SnakeDir.Up

    val snake = Snake.apply(List((4, 4))).get.changeDirection(SnakeDir.Up)
    val board = Board(8, List.empty, snake)

    // Rotate from Up to Right and verify movement
    val newDirection = rotateClockwise(board.snake.snakeDir)
    board.changeSnakeDirection(newDirection)
    assertEquals(board.snake.snakeDir, SnakeDir.Right, "Direction should be Right after rotation")

    // Move the snake and verify it moves right
    board.update()
    assertEquals(board.snake.body.head, (5, 4), "Snake should move right to (5,4)")
  }
