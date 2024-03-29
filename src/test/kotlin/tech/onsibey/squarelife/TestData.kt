package tech.onsibey.squarelife

import tech.onsibey.squarelife.detector.imageprocessor.Cell
import tech.onsibey.squarelife.detector.imageprocessor.ImageBoard
import tech.onsibey.squarelife.simulator.entities.Coordinate

object TestData {

    fun generateTestImageBoard(
        testUutisetData: TestUutisetData,
        testKuvahakuData: List<TestKuvahakuData>,
        testKuvatData: List<TestKuvatData>,
        boardSize: Int
    ): ImageBoard {
        val imageBoardList = mutableListOf<MutableList<Cell>>()
        val occupiedCells = testUutisetData.coordinates() +
                testKuvahakuData.flatMap { it.coordinates() } + testKuvatData.flatMap { it.coordinates() }

        require(occupiedCells.all { it.x < boardSize && it.y < boardSize }) {
            "Test data contains coordinates that are out of bounds of the board"
        }

        for (i in 0 until boardSize) {
            val row = mutableListOf<Cell>()
            for (j in 0 until boardSize) row.add(Cell(occupiedCells.contains(Coordinate(j, i))))
            imageBoardList.add(row)
        }

        return ImageBoard(imageBoardList)
    }

    data class TestUutisetData(val topLeftCoordinate: Coordinate) {
        fun coordinates(): List<Coordinate> {
            return listOf(
                topLeftCoordinate,
                topLeftCoordinate.copy(x = topLeftCoordinate.x + 1),
                topLeftCoordinate.copy(x = topLeftCoordinate.x + 2),
                topLeftCoordinate.copy(y = topLeftCoordinate.y + 1),
                topLeftCoordinate.copy(x = topLeftCoordinate.x + 1, y = topLeftCoordinate.y + 1),
                topLeftCoordinate.copy(x = topLeftCoordinate.x + 2, y = topLeftCoordinate.y + 1),
                topLeftCoordinate.copy(y = topLeftCoordinate.y + 2),
                topLeftCoordinate.copy(x = topLeftCoordinate.x + 1, y = topLeftCoordinate.y + 2),
                topLeftCoordinate.copy(x = topLeftCoordinate.x + 2, y = topLeftCoordinate.y + 2),
            )
        }
    }

    data class TestKuvahakuData(val topLeftCoordinate: Coordinate) {
        fun coordinates(): List<Coordinate> = listOf(topLeftCoordinate)
    }

    data class TestKuvatData(val topLeftCoordinate: Coordinate) {
        fun coordinates(): List<Coordinate> {
            return listOf(
                topLeftCoordinate,
                topLeftCoordinate.copy(x = topLeftCoordinate.x + 1),
                topLeftCoordinate.copy(y = topLeftCoordinate.y + 1),
                topLeftCoordinate.copy(x = topLeftCoordinate.x + 1, y = topLeftCoordinate.y + 1),
            )
        }
    }
}
