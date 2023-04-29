package tech.onsibey.squarelife.detector.datainterpreter

import tech.onsibey.squarelife.detector.imageprocessor.ImageBoard
import tech.onsibey.squarelife.simulator.EntitySize.KUVAHAKU_SIZE
import tech.onsibey.squarelife.simulator.EntitySize.KUVAT_SIZE
import tech.onsibey.squarelife.simulator.EntitySize.UUTISET_SIZE
import tech.onsibey.squarelife.simulator.entities.Entity
import tech.onsibey.squarelife.simulator.entities.Kuvahaku
import tech.onsibey.squarelife.simulator.entities.Kuvat
import tech.onsibey.squarelife.simulator.entities.Uutiset
import tech.onsibey.squarelife.common.Coordinate
import tech.onsibey.squarelife.common.Position
import tech.onsibey.squarelife.detector.imageprocessor.Processor.processImageBoard

object Detector {
    fun getSimulationInformation() = Interpreter(processImageBoard()).sendMailman()
}

/**
 * Class which contains functionality for interpreting the received raw image
 * information as well as its wrapping into Mailman object to send to the SL Simulator.
 */
class Interpreter(private val imageBoard: ImageBoard) {
    /**
     * Function compiles the information about entities and their positioning
     * @return Mailman object, which contains a list of Entities
     */
    fun sendMailman() = Mailman(interpretBoard(), imageBoard.cells.size)

    /**
     * Function interpreting the ImageBoard object into a list of entities.
     */
    private fun interpretBoard(): List<Entity> {
        val entities = mutableListOf<Entity>()
        val rows = imageBoard.cells
        for (j in rows.indices) {
            for (i in rows[j].indices) {
                if (rows[j][i].isPainted) { // all entity cases
                    if (rows[j][i + 1].isPainted) { // Kuvat and Uutiset cases
                        if (rows[j][i + 2].isPainted) { // Uutiset case
                            entities.add(initUutiset(topLeftX = i, topLeftY = j))
                            freeEntitySpace(topLeftX = i, topLeftY = j, entitySize = UUTISET_SIZE)
                        }
                        entities.add(initKuvat(topLeftX = i, topLeftY = j))
                        freeEntitySpace(topLeftX = i, topLeftY = j, entitySize = KUVAT_SIZE)
                    }
                    entities.add(initKuvahaku(x = i, y = j))
                    freeEntitySpace(topLeftX = i, topLeftY = j, entitySize = KUVAHAKU_SIZE)
                }
            }
        }

        return entities
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
            )
        )
    )

    private fun initKuvahaku(x: Int, y: Int) = Kuvahaku(
        Position(
            setOf(
                Coordinate(x = x, y = y)
            )
        )
    )

    private fun initKuvat(topLeftX: Int, topLeftY: Int) = Kuvat(
        Position(
            setOf(
                Coordinate(x = topLeftX, y = topLeftY),
                Coordinate(x = topLeftX + 1, y = topLeftY),
                Coordinate(x = topLeftX, y = topLeftY + 1),
                Coordinate(x = topLeftX + 1, y = topLeftY + 1)
            )
        )
    )
}

/**
 * Data class containing a list of Entities gotten from the image.
 */
data class Mailman(val entities: List<Entity>, val boardSize: Int)
