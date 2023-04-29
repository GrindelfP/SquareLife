package tech.onsibey.squarelife.simulator.world

import tech.onsibey.squarelife.simulator.Color

/**
 * Class, representing position on the board in integer coordinates - x and y.
 */
data class Coordinate(val x: Int, val y: Int)

/**
 * Class representing position of the entity on the board.
 * It contains functionality to detect possible move coordinates,
 * functionality to move the entity on the board.
 * @param  coordinates set of coordinates of the entity.
 * @param board board on which the entity is placed.
 */
data class Position(val coordinates: Set<Coordinate>, private val board: Board) {

    /**
     * Function to detect possible move coordinates.
     */
    fun possibleMoveCoordinates(): List<Position> {
        val list = mutableListOf<Position>() // returned list of possible positions

        // possible move up
        val moveUpPosition = shiftToEmptyPosition(0, -1)
        if (moveUpPosition != null) list.add(moveUpPosition)

        // possible move down
        val moveDownPosition = shiftToEmptyPosition(0, +1)
        if (moveDownPosition != null) list.add(moveDownPosition)

        // possible move left
        val moveLeftPosition = shiftToEmptyPosition(-1, 0)
        if (moveLeftPosition != null) list.add(moveLeftPosition)

        // possible move right
        val moveRightPosition = shiftToEmptyPosition(+1, 0)
        if (moveRightPosition != null) list.add(moveRightPosition)

        // possible move up left
        val moveLeftUpPosition = shiftToEmptyPosition(-1, -1)
        if (moveLeftUpPosition != null) list.add(moveLeftUpPosition)

        // possible move up right
        val moveLeftDownPosition = shiftToEmptyPosition(-1, 1)
        if (moveLeftDownPosition != null) list.add(moveLeftDownPosition)

        // possible move down left
        val moveRightUpPosition = shiftToEmptyPosition(+1, -1)
        if (moveRightUpPosition != null) list.add(moveRightUpPosition)

        // possible move down right
        val moveRightDownPosition = shiftToEmptyPosition(+1, +1)
        if (moveRightDownPosition != null) list.add(moveRightDownPosition)

        return list // return list of possible positions
    }

    /**
     * Function to change the entity's position on the board.
     */
    fun shift(horizontal: Int, vertical: Int): Position? {
        val newCoordinates = shiftCoordinates(horizontal, vertical).toSet()
        return when {
            newCoordinates.size != coordinates.size -> null
            else -> Position(newCoordinates, board)
        }
    }

    /**
     * Function to change the entity's position on the board only on the empty positions.
     * Used in possibleMoveCoordinates() function to detect the empty coordinates.
     */
    private fun shiftToEmptyPosition(horizontal: Int, vertical: Int): Position? {
        val newCoordinates = shiftCoordinates(horizontal, vertical).filter {
            board.tileIsEmpty(it) || coordinates.contains(it)
        }.toSet() // filter out coordinates that are not empty or already occupied
        return when {
            newCoordinates.size != coordinates.size -> null
            else -> Position(newCoordinates, board)
        }
    }

    /**
     * Function, returning a list of shifted coordinates filtered by inclusion within the board.
     */
    private fun shiftCoordinates(horizontal: Int, vertical: Int): List<Coordinate> = coordinates.map { coordinate ->
        Coordinate(coordinate.x + horizontal, coordinate.y + vertical)
    }.filter { board.onBoard(it) }

    /**
     * Overrides the default toString() method. Returns a string representation of the position in coordinates.
     */
    override fun toString(): String {
        return "Position: $coordinates"
    }
}

/**
 * Class, representing entity position on the board, as well as its color.
 * @param position position of the entity.
 * @param color color of the entity.
 */
data class EntityPosition(val position: Position, val color: Color)
