package tech.onsibey.squarelife.visualisation

import tech.onsibey.squarelife.simulator.entities.*
import tech.onsibey.squarelife.visualisation.Constants.GAP

/**
 * Board class which represents the game board.
 * Takes parameter boardSize - size of the creating board.
 * It contains companion object which represents the PADDING value,
 * which stands for padding of a board row.
 * Also, the class contains following properties:
 * - coordinates: list of coordinates of the board
 * - numberOfRowsWithPadding: number of rows with padding
 * - rowLengthWithPadding: length of the row with padding
 * - boardState: state of the board
 */
data class BoardView(val boardSize: BoardSize) {

    companion object {
        private const val PADDING = 2
    }

    private val coordinates: List<Coordinate> = initCoordinates()
    private val numberOfRowsWithPadding = boardSize.numberOfRows + PADDING
    private val rowLengthWithPadding = boardSize.rowLength + PADDING
    private val boardState = initBoard()

    /**
     * Function initializing the coordinates of the board.
     */
    private fun initCoordinates(): MutableList<Coordinate> {
        val coordinates = mutableListOf<Coordinate>()
        repeat(boardSize.numberOfRows) { y ->
            repeat(boardSize.rowLength) { x ->
                coordinates.add(Coordinate(x + 1, y + 1))
            }
        }

        return coordinates
    }

    /**
     * Function initializing the board with borders as a 2-dimensional list of String.
     */
    private fun initBoard(): MutableList<MutableList<String>> {
        val board = mutableListOf<MutableList<String>>()
        repeat(numberOfRowsWithPadding) { rowNumber ->
            board.add(mutableListOf())
            repeat(rowLengthWithPadding) { positionInRow ->
                when {
                    rowNumber == 0 -> {
                        horizontalBorder(BorderBlock.LEFT_UP, BorderBlock.RIGHT_UP, rowNumber, positionInRow, board)
                    }
                    rowNumber == numberOfRowsWithPadding - 1 -> {
                        horizontalBorder(BorderBlock.LEFT_DOWN, BorderBlock.RIGHT_DOWN, rowNumber, positionInRow, board)
                    }
                    positionInRow == 0 -> {
                        board[rowNumber].add(positionInRow, "${BorderBlock.VERTICAL.brick}$GAP")
                    }
                    positionInRow == rowLengthWithPadding - 1 -> {
                        board[rowNumber].add(positionInRow, BorderBlock.VERTICAL.brick)
                    }
                    else -> board[rowNumber].add(positionInRow, "$BoardTile")
                }
            }
        }

        return board
    }

    /**
     * Function updater of the board. Takes parameter entityPositions - list of entity positions.
     * Firstly it resets the board to default (all-grey) state.
     * Then it rewrites the entity positions on the board with the new coordinates.
     */
    fun update(entityPositions: List<EntityPosition>) {
        // reset the board
        coordinates.forEach { coordinate ->
            boardState[coordinate.y][coordinate.x] = Tile(Color.GREY).toString()
        }
        // We allow overlapping of entities - no need for this check, I leave it here just in case
        /*val positions = entityPositions.toSet()
        check(positions.size == entityPositions.size) { "Entities have overlapping positions!" }*/
        entityPositions.forEach { entityPosition ->
            entityPosition.position.coordinates.forEach { coordinate ->
                if (boardState[coordinate.y + 1][coordinate.x + 1].contains(Color.BLUE.value)) {
                    // tile is painted in magenta color if there is na overlapped entity
                    boardState[coordinate.y + 1][coordinate.x + 1] = Tile(Color.MAGENTA).toString()
                    // TODO: check why only Kuvahakus overlap with indication by magenta color
                } else {
                    // tile is painted in the color of this entity
                    boardState[coordinate.y + 1][coordinate.x + 1] = Tile(
                        when(entityPosition.type) {
                            Kuvahaku::class -> Color.BLUE
                            Uutiset::class -> Color.RED
                            Kuvat::class -> Color.GREEN
                            else -> TODO()
                        }
                    ).toString()
                }
            }
        }
    }

    /**
     * Function which overrides toString() function and returns the whole board as a string.
     */
    override fun toString(): String = with(StringBuffer()) {
        boardState.forEach { row ->
            row.forEach { element ->
                this.append(element)
            }
            append("\n")
        }
        toString()
    }

    /**
     * Function which add visual horizontal borders to the board.
     */
    private fun horizontalBorder(
        left: BorderBlock,
        right: BorderBlock,
        rowNumber: Int,
        positionInRow: Int,
        board: MutableList<MutableList<String>>
    ) {
        when (positionInRow) {
            0 -> {
                board[rowNumber].add(
                    positionInRow,
                    left.brick + BorderBlock.HORIZONTAL.brick
                )
            }
            rowLengthWithPadding - 1 -> {
                board[rowNumber].add(positionInRow, right.brick)
            }
            else -> {
                board[rowNumber].add(
                    positionInRow,
                    BorderBlock.HORIZONTAL.brick + BorderBlock.HORIZONTAL.brick + BorderBlock.HORIZONTAL.brick
                )
            }
        }
    }
}

/**
 * Enumeration of the blocks of the board borders (string characters):
 * - VERTICAL
 * - HORIZONTAL
 * - RIGHT_UP
 * - RIGHT_DOWN
 * - LEFT_UP
 * - LEFT_DOWN
 */
private enum class BorderBlock(val brick: String) {
    VERTICAL("║"),
    HORIZONTAL("═"),
    RIGHT_UP("╗"),
    RIGHT_DOWN("╝"),
    LEFT_UP("╔"),
    LEFT_DOWN("╚"),
}
