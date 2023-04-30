package tech.onsibey.squarelife.detector.datainterpreter

import tech.onsibey.squarelife.detector.imageprocessor.ImageBoard
import tech.onsibey.squarelife.common.EntitySize.KUVAHAKU_SIZE
import tech.onsibey.squarelife.common.EntitySize.KUVAT_SIZE
import tech.onsibey.squarelife.common.EntitySize.UUTISET_SIZE
import tech.onsibey.squarelife.simulator.entities.Entity
import tech.onsibey.squarelife.simulator.entities.Kuvahaku
import tech.onsibey.squarelife.simulator.entities.Kuvat
import tech.onsibey.squarelife.simulator.entities.Uutiset
import tech.onsibey.squarelife.common.Coordinate
import tech.onsibey.squarelife.common.Position
import tech.onsibey.squarelife.common.Board

/**
 * Class which contains functionality for interpreting the received raw image
 * information as well as its wrapping into Mailman object to send to the SL Simulator.
 */
class Interpreter(private val imageBoard: ImageBoard) {

    val board = Board(imageBoard.cells.size)

    /**
     * Function compiles the information about entities and their positioning
     * @return Mailman object, which contains a list of Entities
     */
    fun sendMailman() = Mailman(interpretBoard(), board)

    /**
     * Function interpreting the ImageBoard object into a list of entities.
     */
    private fun interpretBoard(): List<Entity> {
        val entities = mutableListOf<Entity>()
        for (j in imageBoard.cells.indices) {
            for (i in imageBoard.cells[j].indices) {
                if (kuvahakuChecked(x = i, y = j) /*&& imageBoard.cells.size != j + 10*/) {
                    if (kuvatChecked(x = i, y = j) /*&& imageBoard.cells.size != j + 2*/) {
                        if (uutisetChecked(x = i, y = j) /*&& imageBoard.cells.size != j + 3*/) {
                            entities.add(initUutiset(topLeftX = i + 1, topLeftY = j + 1))
                            freeEntitySpace(topLeftX = i, topLeftY = j, entitySize = UUTISET_SIZE)
                        } else {
                            entities.add(initKuvat(topLeftX = i + 1, topLeftY = j + 1))
                            freeEntitySpace(topLeftX = i, topLeftY = j, entitySize = KUVAT_SIZE)
                        }
                    } else {
                        entities.add(initKuvahaku(x = i + 1, y = j + 1))
                        freeEntitySpace(topLeftX = i, topLeftY = j, entitySize = KUVAHAKU_SIZE)
                    }
                }
            }
        }

        return entities
    }

    private fun uutisetChecked(x: Int, y: Int): Boolean {
        return imageBoard.cells.size != x + 2 && imageBoard.cells.size != y + 2 &&
                imageBoard.cells[y][x].isPainted &&
                imageBoard.cells[y + 1][x].isPainted &&
                imageBoard.cells[y + 2][x].isPainted &&
                imageBoard.cells[y][x + 1].isPainted &&
                imageBoard.cells[y + 1][x + 1].isPainted &&
                imageBoard.cells[y + 2][x + 1].isPainted &&
                imageBoard.cells[y][x + 2].isPainted &&
                imageBoard.cells[y + 1][x + 2].isPainted &&
                imageBoard.cells[y + 2][x + 2].isPainted
    }

    private fun kuvatChecked(x: Int, y: Int): Boolean {
        return imageBoard.cells.size != x + 1 && imageBoard.cells.size != y + 1 &&
                imageBoard.cells[y][x].isPainted &&
                imageBoard.cells[y + 1][x].isPainted &&
                imageBoard.cells[y][x + 1].isPainted &&
                imageBoard.cells[y + 1][x + 1].isPainted

    }

    private fun kuvahakuChecked(x: Int, y: Int): Boolean {
        return imageBoard.cells[y][x].isPainted
    }


    /**
     * Function frees the occupied by entity
     */
    private fun freeEntitySpace(topLeftX: Int, topLeftY: Int, entitySize: Int) {
        repeat(entitySize) { entityRow ->
            repeat(entitySize) { entityColumn ->
                imageBoard.cells[topLeftY + entityRow][topLeftX + entityColumn].reverseState()
            }
        }
    }

    private fun initUutiset(topLeftX: Int, topLeftY: Int) = Uutiset(
        Position(
            setOf(
                Coordinate(x = topLeftX, y = topLeftY),
                Coordinate(x = topLeftX + 1, y = topLeftY),
                Coordinate(x = topLeftX + 2, y = topLeftY),
                Coordinate(x = topLeftX, y = topLeftY + 1),
                Coordinate(x = topLeftX + 1, y = topLeftY + 1),
                Coordinate(x = topLeftX + 2, y = topLeftY + 1),
                Coordinate(x = topLeftX, y = topLeftY + 2),
                Coordinate(x = topLeftX + 1, y = topLeftY + 2),
                Coordinate(x = topLeftX + 2, y = topLeftY + 2)
            ),
            board
        )
    )

    private fun initKuvahaku(x: Int, y: Int) = Kuvahaku(
        Position(
            setOf(
                Coordinate(x = x, y = y)
            ),
            board
        )
    )

    private fun initKuvat(topLeftX: Int, topLeftY: Int) = Kuvat(
        Position(
            setOf(
                Coordinate(x = topLeftX, y = topLeftY),
                Coordinate(x = topLeftX + 1, y = topLeftY),
                Coordinate(x = topLeftX, y = topLeftY + 1),
                Coordinate(x = topLeftX + 1, y = topLeftY + 1)
            ),
            board
        )
    )
}

/**
 * Data class containing a list of Entities gotten from the image.
 */
data class Mailman(val entities: List<Entity>, val board: Board)
