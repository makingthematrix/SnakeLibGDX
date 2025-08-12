package io.github.makingthematrix.snakelibgdx

import munit.FunSuite

class ModelsSuite extends FunSuite:

  test("Tile enum should have all expected cases") {
    val allTiles = Tile.values.toList
    assertEquals(allTiles.length, 5)
    assert(allTiles.contains(Tile.Empty))
    assert(allTiles.contains(Tile.SnakeHead))
    assert(allTiles.contains(Tile.SnakeBody))
    assert(allTiles.contains(Tile.SnakeTail))
    assert(allTiles.contains(Tile.Coin))
  }

  test("Tile enum cases should be distinct") {
    assert(Tile.Empty != Tile.SnakeHead)
    assert(Tile.SnakeHead != Tile.SnakeBody)
    assert(Tile.SnakeBody != Tile.SnakeTail)
    assert(Tile.SnakeTail != Tile.Coin)
    assert(Tile.Coin != Tile.Empty)
  }

  test("Token should be created with tile and position") {
    val position = (3, 4)
    val token = Token(Tile.SnakeHead, position)
    assertEquals(token.tile, Tile.SnakeHead)
    assertEquals(token.pos, position)
  }

  test("Token should handle different tile types") {
    val pos = (1, 2)
    val emptyToken = Token(Tile.Empty, pos)
    val coinToken = Token(Tile.Coin, pos)
    val headToken = Token(Tile.SnakeHead, pos)

    assertEquals(emptyToken.tile, Tile.Empty)
    assertEquals(coinToken.tile, Tile.Coin)
    assertEquals(headToken.tile, Tile.SnakeHead)
  }

  test("Token should handle different positions") {
    val tile = Tile.SnakeBody
    val token1 = Token(tile, (0, 0))
    val token2 = Token(tile, (5, 10))
    val token3 = Token(tile, (-1, -2))

    assertEquals(token1.pos, (0, 0))
    assertEquals(token2.pos, (5, 10))
    assertEquals(token3.pos, (-1, -2))
  }

  test("Snake should be created with a list of tokens") {
    val tokens = List(
      Token(Tile.SnakeHead, (3, 3)),
      Token(Tile.SnakeBody, (2, 3)),
      Token(Tile.SnakeTail, (1, 3))
    )
    val snake = Snake(tokens)
    // Snake constructor doesn't expose tokens, so we just test it can be created
    assert(snake != null, "Snake should be created successfully")
  }

  test("Snake should handle empty token list") {
    val emptySnake = Snake(List.empty)
    assert(emptySnake != null, "Snake with empty tokens should be created successfully")
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
