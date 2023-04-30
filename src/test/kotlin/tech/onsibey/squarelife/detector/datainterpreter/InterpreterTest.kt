package tech.onsibey.squarelife.detector.datainterpreter

import org.junit.jupiter.api.Test
import tech.onsibey.squarelife.common.Coordinate
import tech.onsibey.squarelife.common.Position
import tech.onsibey.squarelife.detector.imageprocessor.Cell
import tech.onsibey.squarelife.detector.imageprocessor.ImageBoard
import tech.onsibey.squarelife.simulator.entities.Kuvahaku
import tech.onsibey.squarelife.simulator.entities.Kuvat

class InterpreterTest {

    private val testImageBoard = ImageBoard(
        listOf(
            listOf(
                Cell(false), Cell(false), Cell(false), Cell(false), Cell(false), Cell(true), Cell(false),
            ),
            listOf(
                Cell(false), Cell(true), Cell(false), Cell(false), Cell(false), Cell(false), Cell(false),
            ),
            listOf(
                Cell(false), Cell(false), Cell(false), Cell(true), Cell(true), Cell(false), Cell(false),
            ),
            listOf(
                Cell(false), Cell(false), Cell(false), Cell(true), Cell(true), Cell(false), Cell(false),
            ),
            listOf(
                Cell(true), Cell(true), Cell(false), Cell(false), Cell(false), Cell(true), Cell(false),
            ),
            listOf(
                Cell(true), Cell(true), Cell(false), Cell(false), Cell(false), Cell(false), Cell(false),
            ),
            listOf(
                Cell(false), Cell(false), Cell(false), Cell(true), Cell(false), Cell(true), Cell(false),
            )
        )
    )

    private val testEntityList = listOf(
        Kuvahaku(Position(setOf(Coordinate(5, 0)))),
        Kuvahaku(Position(setOf(Coordinate(1, 1)))),
        Kuvat(Position(setOf(Coordinate(3, 2), Coordinate(4, 2), Coordinate(3, 3), Coordinate(4, 3)))),
        Kuvat(Position(setOf(Coordinate(0, 4), Coordinate(1, 4), Coordinate(0, 5), Coordinate(1, 5)))),
        Kuvahaku(Position(setOf(Coordinate(5, 4)))),
        Kuvahaku(Position(setOf(Coordinate(3, 6)))),
        Kuvahaku(Position(setOf(Coordinate(5, 6)))),
    )

    private val testEntityNames = listOf(
        "Kuvahaku",
        "Kuvahaku",
        "Kuvat",
        "Kuvat",
        "Kuvahaku",
        "Kuvahaku",
        "Kuvahaku"
    )

    @Test
    fun `GIVEN correct test ImageBoard WHEN called sendMailman() THEN returns correct board size`() {
        val mailMan = Interpreter(testImageBoard).sendMailman()

        assert(mailMan.boardSize == 7)
    }

    @Test
    fun `GIVEN correct test ImageBoard WHEN called sendMailman() THEN returns correct number of entities`() {
        val mailMan = Interpreter(testImageBoard).sendMailman()

        assert(mailMan.entities.size == 7)
    }

    @Test
    fun `GIVEN correct test ImageBoard WHEN called sendMailman() THEN returns correct type of entities`() {
        val mailMan = Interpreter(testImageBoard).sendMailman()

        mailMan.entities.forEachIndexed { index, entity ->
            assert(entity::class.simpleName == testEntityNames[index])
        }
    }

    @Test
    fun `GIVEN correct test ImageBoard WHEN called sendMailman() THEN returns correct coordinates of entities`() {
        val mailMan = Interpreter(testImageBoard).sendMailman()

        mailMan.entities.forEachIndexed { index, entity ->
            assert(entity == testEntityList[index])
        }
    }
}
