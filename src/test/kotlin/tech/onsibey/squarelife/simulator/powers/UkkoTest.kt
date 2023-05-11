package tech.onsibey.squarelife.simulator.powers

import org.junit.jupiter.api.Test
import tech.onsibey.squarelife.TestData
import tech.onsibey.squarelife.TestData.generateTestImageBoard
import tech.onsibey.squarelife.detector.datainterpreter.Interpreter
import tech.onsibey.squarelife.simulator.entities.Coordinate
import tech.onsibey.squarelife.visualisation.GifEvolutionCycleGenerator

class UkkoTest {
    private val testImageBoard = generateTestImageBoard(
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

    @Test
    fun `GIVEN imageBoard WHEN run Ukko evolution THEN gif of the arkestor is processed`() {
        val mail = Interpreter(testImageBoard).prepareMailman()

        val result = Ukko(mail = mail, evolutionCycleLimit = 45).evolutionResult
        GifEvolutionCycleGenerator(result).visualize()
    }
}
