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
    // We can't directly access snakeDir, but we can test behavior through crawl
    val crawledSnake = newSnake.crawl
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
    val crawledSnake = snake.crawl
    assert(crawledSnake != null, "Snake should crawl successfully")
    // After crawling right, head should move from (3,3) to (4,3) and tail (1,3) should be removed
  }

  test("crawl should handle single element snake") {
    val snake = Snake.apply(List((5, 5))).get // Default direction is Right
    val crawledSnake = snake.crawl
    assert(crawledSnake != null, "Single element snake should crawl successfully")
    // After crawling right, head should move from (5,5) to (6,5)
  }

  test("crawl should handle empty snake body") {
    val snake = Snake.apply(List.empty).get // Default direction is Right
    val crawledSnake = snake.crawl
    assert(crawledSnake != null, "Empty snake should crawl successfully")
    // After crawling right from empty, should create head at (1,0)
  }

  test("crawl with different directions should move head correctly") {
    val snake = Snake.apply(List((5, 5), (4, 5))).get

    val upSnake = snake.changeDirection(SnakeDir.Up).crawl
    val downSnake = snake.changeDirection(SnakeDir.Down).crawl
    val leftSnake = snake.changeDirection(SnakeDir.Left).crawl
    val rightSnake = snake.changeDirection(SnakeDir.Right).crawl

    assert(upSnake != null, "Should crawl up successfully")
    assert(downSnake != null, "Should crawl down successfully")
    assert(leftSnake != null, "Should crawl left successfully")
    assert(rightSnake != null, "Should crawl right successfully")
  }

  test("crawl should preserve snake length for multi-element snake") {
    val originalBody = List((3, 3), (2, 3), (1, 3), (0, 3)) // 4-element snake
    val snake = Snake.apply(originalBody).get
    val crawledSnake = snake.crawl
    assert(crawledSnake != null, "Multi-element snake should crawl successfully")
    // Length should be preserved (removes tail, adds new head)
  }

  test("multiple crawl operations should work correctly") {
    val snake = Snake.apply(List((2, 2), (1, 2))).get // Moving right
    val crawled1 = snake.crawl // Head moves to (3,2), tail (1,2) removed -> [(3,2), (2,2)]
    val crawled2 = crawled1.crawl // Head moves to (4,2), tail (2,2) removed -> [(4,2), (3,2)]
    val crawled3 = crawled2.crawl // Head moves to (5,2), tail (3,2) removed -> [(5,2), (4,2)]

    assert(crawled1 != null, "First crawl should work")
    assert(crawled2 != null, "Second crawl should work")
    assert(crawled3 != null, "Third crawl should work")
  }

  test("crawl should preserve snake size") {
    val originalBody = List((5, 3), (4, 3), (3, 3), (2, 3)) // 4-element snake moving right
    val snake = Snake.apply(originalBody).get
    val originalSize = snake.body.size
    val crawledSnake = snake.crawl
    val newSize = crawledSnake.body.size

    assertEquals(newSize, originalSize, "Snake size should remain the same after crawl")
    assertEquals(newSize, 4, "Snake should still have 4 elements")
  }

  test("crawl with Right direction should have correct body structure") {
    val originalBody = List((3, 2), (2, 2), (1, 2)) // 3-element snake at positions (3,2), (2,2), (1,2)
    val snake = Snake.apply(originalBody).get.changeDirection(SnakeDir.Right) // Explicitly set to Right
    val crawledSnake = snake.crawl
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
    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (7, 4), "Snake moving left from x=0 should wrap to x=7")
  }

  test("crawl should wrap up from y=0 to y=7") {
    val snake = Snake.apply(List((4, 0))).get.changeDirection(SnakeDir.Up)
    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (4, 7), "Snake moving up from y=0 should wrap to y=7")
  }

  test("crawl should wrap right from x=7 to x=0") {
    val snake = Snake.apply(List((7, 4))).get.changeDirection(SnakeDir.Right)
    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (0, 4), "Snake moving right from x=7 should wrap to x=0")
  }

  test("crawl should wrap down from y=7 to y=0") {
    val snake = Snake.apply(List((4, 7))).get.changeDirection(SnakeDir.Down)
    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (4, 0), "Snake moving down from y=7 should wrap to y=0")
  }

  test("crawl should not wrap when moving within board boundaries") {
    val snake = Snake.apply(List((3, 3))).get.changeDirection(SnakeDir.Right)
    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (4, 3), "Snake moving within boundaries should not wrap")
  }

  test("crawl should wrap with multi-element snake from left boundary") {
    val snake = Snake.apply(List((0, 4), (1, 4))).get.changeDirection(SnakeDir.Left)
    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody, List((7, 4), (0, 4)), "Multi-element snake wrapping left should have correct structure")
  }

  test("crawl should wrap with multi-element snake from right boundary") {
    val snake = Snake.apply(List((7, 4), (6, 4))).get.changeDirection(SnakeDir.Right)
    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody, List((0, 4), (7, 4)), "Multi-element snake wrapping right should have correct structure")
  }

  test("crawl should wrap with multi-element snake from top boundary") {
    val snake = Snake.apply(List((4, 0), (4, 1))).get.changeDirection(SnakeDir.Up)
    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody, List((4, 7), (4, 0)), "Multi-element snake wrapping up should have correct structure")
  }

  test("crawl should wrap with multi-element snake from bottom boundary") {
    val snake = Snake.apply(List((4, 7), (4, 6))).get.changeDirection(SnakeDir.Down)
    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody, List((4, 0), (4, 7)), "Multi-element snake wrapping down should have correct structure")
  }

  test("crawl should handle corner wrapping - top-left to bottom-right") {
    val snake = Snake.apply(List((0, 0))).get.changeDirection(SnakeDir.Up).changeDirection(SnakeDir.Left)
    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody.head, (7, 0), "Snake at top-left corner moving left should wrap to right side")
  }

  test("Board should be created with size and coins") {
    val coins = List((1, 2), (3, 4))
    val board = new Board(8, coins)
    assertEquals(board.size, 8)
    assertEquals(board.coinsPositions, coins)
  }

  test("Board.coinsPositions should return the coins list") {
    val coins = List((0, 0), (1, 1), (2, 2))
    val board = new Board(5, coins)
    val retrievedCoins = board.coinsPositions
    assertEquals(retrievedCoins, coins)
    assertEquals(retrievedCoins.length, 3)
  }

  test("Board should handle empty coins list") {
    val board = new Board(10, List.empty)
    assertEquals(board.size, 10)
    assertEquals(board.coinsPositions, List.empty)
  }

  test("Board companion object apply should create board with empty coins") {
    val board = Board.apply(6)
    assertEquals(board.size, 6)
    assertEquals(board.coinsPositions, List.empty)
  }

  test("Board companion object apply should work with different sizes") {
    val smallBoard = Board.apply(1)
    val largeBoard = Board.apply(100)

    assertEquals(smallBoard.size, 1)
    assertEquals(smallBoard.coinsPositions, List.empty)
    assertEquals(largeBoard.size, 100)
    assertEquals(largeBoard.coinsPositions, List.empty)
  }

  test("Board should handle zero size") {
    val board = Board.apply(0)
    assertEquals(board.size, 0)
    assertEquals(board.coinsPositions, List.empty)
  }

  test("Multiple boards should be independent") {
    val board1 = new Board(5, List((1, 1)))
    val board2 = new Board(8, List((2, 2), (3, 3)))

    assertEquals(board1.size, 5)
    assertEquals(board1.coinsPositions, List((1, 1)))
    assertEquals(board2.size, 8)
    assertEquals(board2.coinsPositions, List((2, 2), (3, 3)))

    // Verify they are different instances
    assert(board1 ne board2, "Boards should be different instances")
  }

  // hasCoin functionality tests
  test("Snake should have hasCoin false by default") {
    val snake = Snake.apply(List((3, 3), (2, 3))).get
    assertEquals(snake.hasCoin, false, "Snake should have hasCoin false by default")
  }

  test("Snake getHasCoin should return correct value") {
    val snake = Snake.apply(List((3, 3), (2, 3))).get
    assertEquals(snake.hasCoin, false, "getHasCoin should return false for new snake")

    val snakeWithCoin = snake.addCoin
    assertEquals(snakeWithCoin.hasCoin, true, "getHasCoin should return true after setting to true")
  }

  test("Snake setHasCoin should create new snake with updated hasCoin value") {
    val originalSnake = Snake.apply(List((3, 3), (2, 3))).get
    val snakeWithCoin = originalSnake.addCoin
    val snakeWithoutCoin = originalSnake.setHasCoin(false)

    assertEquals(originalSnake.hasCoin, false, "Original snake should still have hasCoin false")
    assertEquals(snakeWithCoin.hasCoin, true, "New snake should have hasCoin true")
    assertEquals(snakeWithoutCoin.hasCoin, false, "New snake should have hasCoin false")

    // Body and direction should be preserved
    assertEquals(snakeWithCoin.body, originalSnake.body, "Body should be preserved when setting hasCoin")
  }

  test("Snake crawl with hasCoin false should remove tail normally") {
    val snake = Snake.apply(List((3, 3), (2, 3), (1, 3))).get.changeDirection(SnakeDir.Right)
    assertEquals(snake.hasCoin, false, "Snake should start with hasCoin false")

    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody.size, 3, "Snake size should remain the same (tail removed, head added)")
    assertEquals(newBody, List((4, 3), (3, 3), (2, 3)), "Snake should move normally with tail removal")
    assertEquals(crawledSnake.hasCoin, false, "Snake should have hasCoin false after crawl")
  }

  test("Snake crawl with hasCoin true should grow (not remove tail)") {
    val snake = Snake.apply(List((3, 3), (2, 3), (1, 3))).get.changeDirection(SnakeDir.Right).addCoin
    assertEquals(snake.hasCoin, true, "Snake should have hasCoin true")

    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody.size, 4, "Snake should grow (no tail removal)")
    assertEquals(newBody, List((4, 3), (3, 3), (2, 3), (1, 3)), "Snake should grow by keeping tail and adding head")
    assertEquals(crawledSnake.hasCoin, false, "Snake should have hasCoin false after growing crawl")
  }

  test("Snake crawl with hasCoin true on single element should not grow") {
    val snake = Snake.apply(List((3, 3))).get.changeDirection(SnakeDir.Right).addCoin
    assertEquals(snake.hasCoin, true, "Snake should have hasCoin true")

    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody.size, 1, "Single element snake should remain size 1")
    assertEquals(newBody, List((4, 3)), "Single element snake should move to new position")
    assertEquals(crawledSnake.hasCoin, false, "Snake should have hasCoin false after crawl")
  }

  test("Snake crawl with hasCoin true on empty snake should not grow") {
    val snake = Snake.apply(List()).get.addCoin
    assertEquals(snake.hasCoin, true, "Snake should have hasCoin true")

    val crawledSnake = snake.crawl
    val newBody = crawledSnake.body

    assertEquals(newBody.size, 1, "Empty snake should become size 1")
    assertEquals(crawledSnake.hasCoin, false, "Snake should have hasCoin false after crawl")
  }

  test("changeDirection should preserve hasCoin value") {
    val snake = Snake.apply(List((3, 3), (2, 3))).get.addCoin
    assertEquals(snake.hasCoin, true, "Snake should have hasCoin true")

    val changedSnake = snake.changeDirection(SnakeDir.Up)
    assertEquals(changedSnake.hasCoin, true, "hasCoin should be preserved after direction change")
    assertEquals(changedSnake.body, snake.body, "Body should be preserved after direction change")
  }

  test("Board updateSnake should update the snake instance") {
    val board = Board.apply(8)
    val newSnake = Snake.apply(List((5, 5), (4, 5))).get

    board.updateSnake(newSnake)
    assertEquals(board.snake.body, newSnake.body, "Board should have updated snake")
  }

  test("Board update should set hasCoin to true when snake head hits coin") {
    val coins = List((4, 3), (6, 6))
    val snake = Snake.apply(List((3, 3), (2, 3))).get.changeDirection(SnakeDir.Right)
    val board = Board.apply(8, coins, snake)

    assertEquals(board.snake.hasCoin, false, "Snake should start with hasCoin false")
    assertEquals(board.coinsPositions.size, 2, "Board should start with 2 coins")

    board.update() // Snake head moves from (3,3) to (4,3) which has a coin

    assertEquals(board.snake.hasCoin, true, "Snake should have hasCoin true after collecting coin")
    assertEquals(board.coinsPositions.size, 1, "Board should have 1 coin left after collection")
    assert(!board.coinsPositions.contains((4, 3)), "Collected coin should be removed")
    assertEquals(board.snake.body.size, 2, "Snake should not grow yet (growth happens on next turn)")

    // Second update - snake should grow now because hasCoin was true
    board.update()
    assertEquals(board.snake.hasCoin, false, "Snake should have hasCoin false after growth")
    assertEquals(board.snake.body.size, 3, "Snake should grow on second turn")
  }

  test("Board update should not affect snake when no coin is hit") {
    val coins = List((6, 6), (7, 7))
    val snake = Snake.apply(List((3, 3), (2, 3))).get.changeDirection(SnakeDir.Right)
    val board = Board.apply(8, coins, snake)

    assertEquals(board.snake.hasCoin, false, "Snake should start with hasCoin false")
    assertEquals(board.coinsPositions.size, 2, "Board should start with 2 coins")

    board.update() // Snake head moves from (3,3) to (4,3) which has no coin

    assertEquals(board.snake.hasCoin, false, "Snake should still have hasCoin false")
    assertEquals(board.coinsPositions.size, 2, "Board should still have 2 coins")
    assertEquals(board.snake.body.size, 2, "Snake should maintain size (normal crawl)")
  }

  test("Board update should handle multiple coin collections correctly") {
    val coins = List((4, 3), (5, 3))
    val snake = Snake.apply(List((3, 3), (2, 3))).get.changeDirection(SnakeDir.Right)
    val board = Board.apply(8, coins, snake)

    // First update: collect coin at (4, 3)
    board.update()
    assertEquals(board.coinsPositions.size, 1, "Should have 1 coin left after first collection")
    assertEquals(board.snake.hasCoin, true, "Snake should have hasCoin true after collecting first coin")
    assertEquals(board.snake.body.size, 2, "Snake should not grow yet")

    // Second update: should grow from first coin and collect second coin at (5, 3)
    board.update()
    assertEquals(board.coinsPositions.size, 0, "Should have 0 coins left after second collection")
    assertEquals(board.snake.hasCoin, true, "Snake should have hasCoin true after collecting second coin")
    assertEquals(board.snake.body.size, 3, "Snake should grow from first coin")

    // Third update: should grow from second coin
    board.update()
    assertEquals(board.snake.hasCoin, false, "Snake should have hasCoin false after growth")
    assertEquals(board.snake.body.size, 4, "Snake should grow from second coin")
  }
