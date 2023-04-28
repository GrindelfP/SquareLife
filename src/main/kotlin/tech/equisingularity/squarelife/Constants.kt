package tech.equisingularity.squarelife

import tech.equisingularity.squarelife.Constants.GAP
import tech.equisingularity.squarelife.Constants.TILE

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
 * Object-container which encapsulates constants:
 * - KUVAHAKU_SIZE: the size of a Kuvahaku
 * - KUVAT_SIZE: the size of a Kuvat
 * - UUTISET_SIZE: the size of an Uutiset
 * - MIN_ENTITY_AREA_SIDE_SIZE: the minimum size of an entity area side
 */
object EntitySize {
    const val KUVAHAKU_SIZE = 1
    const val KUVAT_SIZE = 2
    const val UUTISET_SIZE = 3
    const val MIN_ENTITY_AREA_SIDE_SIZE = UUTISET_SIZE + 2
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
    private val symbol = TILE + GAP

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
