package tech.onsibey.squarelife.visualisation

/**
 * Object-container which encapsulates constants:
 * - TILE: the symbol of a tile of a board
 * - GAP: the symbol of a gap between tiles of a board
 */
object Constants {
    const val TILE = "██"
    const val GAP = " "
}

/**
 * Enumeration of colors (takes a string value parameter):
 * - GREY: the color of a tile of a board
 * - GREEN: the color of a Kuvat entity
 * - BLUE: the color of a Kuvahaku entity
 * - MAGENTA: the color of an overlapping Kuvahaku entities
 * - RED: the color of an Uutiset entity
 * - NO: the absence of color
 */
enum class Color(val value: String) {
    GREY("\u001b[1;37m"),
    GREEN("\u001b[1;32m"),
    BLUE("\u001b[1;34m"),
    MAGENTA("\u001b[1;35m"),
    RED("\u001b[1;31m"),
    NO("\u001b[0m")
}

/**
 * Class which encapsulates a tile of a board end overrides toString() method for it.
 * @param color the color of a tile
 * It combines a TILE const symbol with a GAP const symbol and a color value.
 */
open class Tile(private val color: Color) {
    private val symbol = Constants.TILE + Constants.GAP

    /**
     * Function overrides toString() method. Returns this instance of Tile as a
     * string with needed color.
     */
    override fun toString(): String {
        return "${color.value}$symbol${Color.NO.value}"
    }
}

/**
 * Object which encapsulates a tile of a board.
 * Inherits Tile class, and its representation of a stringed tile.
 */
object BoardTile : Tile(Color.GREY)
