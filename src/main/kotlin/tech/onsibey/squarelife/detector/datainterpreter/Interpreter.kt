package tech.onsibey.squarelife.detector.datainterpreter

import tech.onsibey.squarelife.detector.imageprocessor.ImageBoard
import tech.onsibey.squarelife.detector.imageprocessor.Processor.processImageBoard
import tech.onsibey.squarelife.simulator.EntitySize.KUVAHAKU_SIZE
import tech.onsibey.squarelife.simulator.EntitySize.KUVAT_SIZE
import tech.onsibey.squarelife.simulator.EntitySize.UUTISET_SIZE
import tech.onsibey.squarelife.simulator.entities.Entity
import tech.onsibey.squarelife.simulator.entities.Kuvahaku
import tech.onsibey.squarelife.simulator.entities.Kuvat
import tech.onsibey.squarelife.simulator.entities.Uutiset
import tech.onsibey.squarelife.simulator.world.Coordinate
import tech.onsibey.squarelife.simulator.world.Position

/**
 * Object which contains functionality for interpreting the received raw image
 * information as well as its wrapping into Mailman object to send to the SL Simulator.
 */
object Interpreter {
    /**
     * Function compiles the information about entities and their positioning
     * @return Mailman object, which contains a list of Entities
     */
    fun sendMailman(): Mailman = Mailman(interpretBoard(processImageBoard()))

    /**
     * Function interpreting the ImageBoard object into a list of entities.
     */
    private fun interpretBoard(imageBoard: ImageBoard): List<Entity> {
        val entities = mutableListOf<Entity>()
        val rows = imageBoard.cells
        for (j in rows.indices) {
            for (i in rows[j].indices) {
                if (rows[j][i].isPainted) { // all entity cases
                    if (rows[j][i + 1].isPainted) { // Kuvat and Uutiset cases
                        if (rows[j][i + 2].isPainted) { // Uutiset case
                            entities.add(initUutiset(topRightX = i, topRightY = j))
                            freeEntitySpace(topRightX = i,
                                topRightY = j, imageBoard = imageBoard, entitySize = UUTISET_SIZE)
                        }
                        entities.add(initKuvat(topRightX = i, topRightY = j))
                        freeEntitySpace(topRightX = i, topRightY = j, imageBoard = imageBoard, entitySize = KUVAT_SIZE)
                    }
                    entities.add(initKuvahaku(x = i, y = j))
                    freeEntitySpace(topRightX = i, topRightY = j, imageBoard = imageBoard, entitySize = KUVAHAKU_SIZE)
                }
            }
        }

        return entities
    }

    /**
     * Function frees the occupied by entity
     */
    private fun freeEntitySpace(topRightX: Int, topRightY: Int, imageBoard: ImageBoard, entitySize: Int) {
        var x = topRightX
        var y = topRightY
        repeat(entitySize) {
            repeat(entitySize) {
                imageBoard.cells[y++][x++].reverseState()
            }
        }
    }

    private fun initUutiset(topRightX: Int, topRightY: Int) = Uutiset(
        Position(
            setOf(
                Coordinate(x = topRightX, y = topRightY),
                Coordinate(x = topRightX + 1, y = topRightY),
                Coordinate(x = topRightX + 2, y = topRightY),
                Coordinate(x = topRightX, y = topRightY + 1),
                Coordinate(x = topRightX + 1, y = topRightY + 1),
                Coordinate(x = topRightX + 2, y = topRightY + 1),
                Coordinate(x = topRightX, y = topRightY + 2),
                Coordinate(x = topRightX + 1, y = topRightY + 2),
                Coordinate(x = topRightX + 2, y = topRightY + 2)
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

    private fun initKuvat(topRightX: Int, topRightY: Int) = Kuvat(
        Position(
            setOf(
                Coordinate(x = topRightX, y = topRightY),
                Coordinate(x = topRightX + 1, y = topRightY),
                Coordinate(x = topRightX, y = topRightY + 1),
                Coordinate(x = topRightX + 1, y = topRightY + 1)
            )
        )
    )
}

/**
 * Data class containing a list of Entities gotten from the image.
 */
data class Mailman(val entities: List<Entity>)
