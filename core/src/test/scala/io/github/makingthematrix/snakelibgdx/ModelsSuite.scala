package io.github.makingthematrix.snakelibgdx

import munit.FunSuite

class ModelsSuite extends FunSuite:

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
    val board = Board.apply(3, Nil, snake)
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
    val board = Board.apply(3, coins, snake)
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
    val board = Board.apply(2, coins, snake)
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
    val board = Board.apply(3, Nil, snake)
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
