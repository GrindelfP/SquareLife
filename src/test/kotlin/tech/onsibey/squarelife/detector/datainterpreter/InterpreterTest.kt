package tech.onsibey.squarelife.detector.datainterpreter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.onsibey.squarelife.TestData
import tech.onsibey.squarelife.simulator.entities.Coordinate
import tech.onsibey.squarelife.simulator.entities.Position
import tech.onsibey.squarelife.simulator.entities.Kuvahaku
import tech.onsibey.squarelife.simulator.entities.Kuvat
import tech.onsibey.squarelife.simulator.entities.Board
import tech.onsibey.squarelife.simulator.entities.Uutiset
import kotlin.test.assertEquals

class InterpreterTest {

    private val testImageBoard = TestData.generateTestImageBoard(
        TestData.TestUutisetData(Coordinate(6, 4)),
        listOf(
            TestData.TestKuvahakuData(Coordinate(0, 0)),
            TestData.TestKuvahakuData(Coordinate(2, 0)),
            TestData.TestKuvahakuData(Coordinate(4, 3)),
            TestData.TestKuvahakuData(Coordinate(4, 3)),
            TestData.TestKuvahakuData(Coordinate(0, 5)),
            TestData.TestKuvahakuData(Coordinate(0, 6)),
            TestData.TestKuvahakuData(Coordinate(0, 7)),
            TestData.TestKuvahakuData(Coordinate(3, 8)),
            TestData.TestKuvahakuData(Coordinate(4, 8)),
            TestData.TestKuvahakuData(Coordinate(7, 8)),
            TestData.TestKuvahakuData(Coordinate(8, 8)),
            TestData.TestKuvahakuData(Coordinate(7, 9))
        ),
        listOf(
            TestData.TestKuvatData(Coordinate(8, 0)),
            TestData.TestKuvatData(Coordinate(1, 2))
        ),
        10
    )

    private val testEntityList = listOf(
        Kuvahaku(Position(setOf(Coordinate(0, 0)), Board(TEST_IMAGE_BOARD_SIZE))),
        Kuvahaku(Position(setOf(Coordinate(2, 0)), Board(TEST_IMAGE_BOARD_SIZE))),
        Kuvat(
            Position(setOf(
                Coordinate(8, 0), Coordinate(8, 1),
                Coordinate(9, 0), Coordinate(9, 1)
            ), Board(TEST_IMAGE_BOARD_SIZE)
            )
        ),
        Kuvat(
            Position(setOf(
                Coordinate(1, 2), Coordinate(2, 2),
                Coordinate(1, 3), Coordinate(2, 3)
            ), Board(TEST_IMAGE_BOARD_SIZE)
            )
        ),
        Kuvahaku(Position(setOf(Coordinate(4, 3)), Board(TEST_IMAGE_BOARD_SIZE))),
        Uutiset(
            Position(setOf(
                Coordinate(6, 4), Coordinate(7, 4), Coordinate(8, 4),
                Coordinate(6, 5), Coordinate(7, 5), Coordinate(8, 5),
                Coordinate(6, 6), Coordinate(7, 6), Coordinate(8, 6)
            ), Board(TEST_IMAGE_BOARD_SIZE)
            )
        ),
        Kuvahaku(Position(setOf(Coordinate(0, 5)), Board(TEST_IMAGE_BOARD_SIZE))),
        Kuvahaku(Position(setOf(Coordinate(0, 6)), Board(TEST_IMAGE_BOARD_SIZE))),
        Kuvahaku(Position(setOf(Coordinate(0, 7)), Board(TEST_IMAGE_BOARD_SIZE))),
        Kuvahaku(Position(setOf(Coordinate(3, 8)), Board(TEST_IMAGE_BOARD_SIZE))),
        Kuvahaku(Position(setOf(Coordinate(4, 8)), Board(TEST_IMAGE_BOARD_SIZE))),
        Kuvahaku(Position(setOf(Coordinate(7, 8)), Board(TEST_IMAGE_BOARD_SIZE))),
        Kuvahaku(Position(setOf(Coordinate(8, 8)), Board(TEST_IMAGE_BOARD_SIZE))),
        Kuvahaku(Position(setOf(Coordinate(7, 9)), Board(TEST_IMAGE_BOARD_SIZE)))
    )


    private val testEntityNames = listOf(
        "Kuvahaku",
        "Kuvahaku",
        "Kuvat",
        "Kuvat",
        "Kuvahaku",
        "Uutiset",
        "Kuvahaku",
        "Kuvahaku",
        "Kuvahaku",
        "Kuvahaku",
        "Kuvahaku",
        "Kuvahaku",
        "Kuvahaku",
        "Kuvahaku",
    )
    companion object {
        private const val TEST_IMAGE_BOARD_SIZE = 10
        private const val TEST_IMAGE_BOARD_POPULATION_COUNT = 14
    }


    @Test
    fun `GIVEN correct test ImageBoard WHEN called sendMailman() THEN returns correct board size`() {
        val mailMan = Interpreter(testImageBoard).prepareMailman()

        //assert(mailMan.boardSize == 6)
        /*assertEquals(expected = testImageBoardSize, actual = mailMan.boardSize, message = "Expected board size is $testImageBoardSize, " +
                "actual is ${mailMan.boardSize}")*/
        assertThat(TEST_IMAGE_BOARD_SIZE).isEqualTo(mailMan.boardSize.numberOfRows)
        // assertThatThrownBy {  }.isInstanceOf(IllegalStateException::class.java).hasMessageContaining("")
    }

    @Test
    fun `GIVEN valid ImageBoard WHEN called sendMailman() THEN returns correct number of entities`() {
        val mailMan = Interpreter(testImageBoard).prepareMailman()

        assertEquals(expected = TEST_IMAGE_BOARD_POPULATION_COUNT,
            actual = mailMan.entities.size,
            message = "The population should be $TEST_IMAGE_BOARD_POPULATION_COUNT, bu actually is $mailMan.entities.size")
    }

    @Test
    fun `GIVEN valid ImageBoard WHEN called sendMailman() THEN returns correct type of entities`() {
        val mailMan = Interpreter(testImageBoard).prepareMailman()

        mailMan.entities.forEachIndexed { index, entity ->
            assertThat(entity.entityType.simpleName).isEqualTo(testEntityNames[index])
        }
    }

    @Test
    fun `GIVEN valid ImageBoard WHEN called sendMailman() THEN returns correct coordinates of entities`() {
        val mailMan = Interpreter(testImageBoard).prepareMailman()

        mailMan.entities.forEachIndexed { index, entity ->
            assertThat(entity.entityType).isEqualTo(testEntityList[index]::class)
            assertThat(entity.coordinates).isEqualTo(testEntityList[index].position().coordinates)
        }
    }
}
