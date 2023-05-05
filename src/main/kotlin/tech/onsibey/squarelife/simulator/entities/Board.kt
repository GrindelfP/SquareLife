package tech.onsibey.squarelife.simulator.entities

import tech.onsibey.squarelife.common.EntitySize.MIN_ENTITY_AREA_SIDE_SIZE

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
data class Board(val boardSize: BoardSize) {

    private val coordinates: List<Coordinate> = initCoordinates()
    private val boardState = initBoard()

    /**
     * Initializer of a board. Requires that:
     * - number of rows should be divisible by the minimum size of the entity area
     * - row length should be divisible by the minimum size of the entity area.
     *
     * If not so, throws an instance of IllegalArgumentException.
     */
    init {
        require(boardSize.numberOfRows % MIN_ENTITY_AREA_SIDE_SIZE == 0) {
            "Number of rows must be a multiple of the minimum size of the entity area of $MIN_ENTITY_AREA_SIDE_SIZE "
        }
        require(boardSize.rowLength % MIN_ENTITY_AREA_SIDE_SIZE == 0) {
            "Row length must be a multiple of the minimum size of the entity area of $MIN_ENTITY_AREA_SIDE_SIZE "
        }
    }

    /**
     * Function initializing the coordinates of the board.
     */
    private fun initCoordinates(): MutableList<Coordinate> {
        val coordinates = mutableListOf<Coordinate>()
        repeat(boardSize.numberOfRows) { y ->
            repeat(boardSize.rowLength) { x ->
                coordinates.add(Coordinate(x, y))
            }
        }

        return coordinates
    }

    /**
     * Constructor for creation of square boards.
     */
    constructor(size: Int) : this(BoardSize(size, size))

    /**
     * Function initializing the board with borders as a 2-dimensional list of String.
     */
    private fun initBoard(): MutableList<MutableList<Boolean>> {
        val board = mutableListOf<MutableList<Boolean>>()
        repeat(boardSize.numberOfRows) { rowNumber ->
            board.add(mutableListOf())
            repeat(boardSize.rowLength) { positionInRow -> board[rowNumber].add(positionInRow, false) }
        }

        return board
    }

    /**
     * Function which checks if the tile (of given coordinate) is empty (not occupied by any entity).
     */
    fun tileIsOccupied(coordinate: Coordinate) = boardState[coordinate.y][coordinate.x]

    /**
     * Function which checks if the given coordinate is within the edges of the bord.
     */
    fun onBoard(coordinate: Coordinate) =
        coordinate.x in (0 until boardSize.rowLength) && coordinate.y in (0 until boardSize.numberOfRows)

    /**
     * Function updater of the board. Takes parameter entityPositions - list of entity positions.
     * Firstly it resets the board to default (all-grey) state.
     * Then it rewrites the entity positions on the board with the new coordinates.
     */
    fun update(entityPositions: List<EntityPosition>) {
        // reset the board
        coordinates.forEach { coordinate ->
            boardState[coordinate.y][coordinate.x] = false
        }
        // We allow overlapping of entities - no need for this check, I leave it here just in case
        /*val positions = entityPositions.toSet()
        check(positions.size == entityPositions.size) { "Entities have overlapping positions!" }*/
        entityPositions.forEach { entityPosition ->
            entityPosition.position.coordinates.forEach { coordinate ->
                boardState[coordinate.y][coordinate.x] = true
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
}

/**
 * Data class representing the size of the board in number of rows and row length.
 */
data class BoardSize(
    val numberOfRows: Int, val rowLength: Int
)
