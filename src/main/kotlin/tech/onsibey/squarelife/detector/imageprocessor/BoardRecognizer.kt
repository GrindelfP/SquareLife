package tech.onsibey.squarelife.detector.imageprocessor

import ij.process.ImageProcessor
import kotlin.math.roundToInt

object BoardRecognizer {
    private const val MIN_LINE_LENGTH = 2

    fun getBoardParameters(imageProcessor: ImageProcessor, cellParameters: CellParameters): BoardParameters {
        val numberOfRows = ((imageProcessor.height).toDouble() / (cellParameters.height).toDouble()).roundToInt()
        val numberOfColumns = ((imageProcessor.width).toDouble() / (cellParameters.width).toDouble()).roundToInt()

        /*for (x in 0 until imageProcessor.width) {
            var periodCounter = 0
            for (y in 0 until imageProcessor.height) {
                if (imageProcessor.getColor(x, y) != Color.WHITE && periodCounter != 0) {
                    periodCounter = 0
                    numberOfRows++
                }
                if (imageProcessor.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }

        for (y in 0 until imageProcessor.height) {
            var periodCounter = 0
            for (x in 0 until imageProcessor.width) {
                if (imageProcessor.getColor(x, y) != Color.WHITE) {
                    periodCounter = 0
                    numberOfColumns++
                }
                if (imageProcessor.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }*/

        return BoardParameters(numberOfRows, numberOfColumns)
    }

    fun getCellParameters(imageProcessor: ImageProcessor): CellParameters {
        val imageWidth = imageProcessor.width
        val imageHeight = imageProcessor.height

        val listOfWidths = getHorizontalWhiteLines(imageWidth, imageHeight, imageProcessor)
        val listOfHeights = getVerticalWhiteLines(imageWidth, imageHeight, imageProcessor)

        require(listOfWidths.isNotEmpty() && listOfHeights.isNotEmpty()) { "No cells found!" }

        val averageWidth = SingleCriteriaParetoSet(listOfWidths, 10).averageInt()
        val averageHeights = SingleCriteriaParetoSet(listOfHeights, 10).averageInt()

        return CellParameters(averageWidth, averageHeights)
    }


    private fun getWhiteLines(
        direction: Direction,
        width: Int,
        height: Int,
        imageProcessor: ImageProcessor
    ): List<Int> {
        val whiteLines = mutableListOf<Int>()

        val widthLimit = when (direction) {
            Direction.HORIZONTAL -> width
            Direction.VERTICAL -> height
        }

        val heightLimit = when (direction) {
            Direction.HORIZONTAL -> height
            Direction.VERTICAL -> width
        }

        for (y in 0 until heightLimit) {
            var periodCounter = 0
            for (x in 0 until widthLimit) {

                val xToCheck = when (direction) {
                    Direction.HORIZONTAL -> x
                    Direction.VERTICAL -> y
                }
                val yToCheck = when (direction) {
                    Direction.HORIZONTAL -> y
                    Direction.VERTICAL -> x
                }

                if (imageProcessor.getColor(xToCheck, yToCheck) != Color.WHITE && periodCounter != 0) {
                    periodCounter = 0
                    whiteLines.add(periodCounter)
                } else if (xToCheck == widthLimit - 1) {
                    whiteLines.add(periodCounter + 1)
                    periodCounter = 0
                } else if (imageProcessor.getColor(xToCheck, yToCheck) == Color.WHITE) periodCounter++
            }
        }

        return whiteLines.filter { integer -> integer > 0 }
    }

    private fun getHorizontalWhiteLines(width: Int, height: Int, imageProcessor: ImageProcessor): List<Int> {
        val whiteLines = mutableListOf<Int>()

        for (y in 0 until height) {
            var periodCounter = 0
            for (x in 0 until width) {
                if (imageProcessor.getColor(x, y) != Color.WHITE && periodCounter != 0) {
                    periodCounter = 0
                    whiteLines.add(periodCounter)
                } else if (x == width - 1) {
                    whiteLines.add(periodCounter + 1)
                    periodCounter = 0
                } else if (imageProcessor.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }

        return whiteLines.filter { integer -> integer >= MIN_LINE_LENGTH }
    }

    private fun getVerticalWhiteLines(width: Int, height: Int, imageProcessor: ImageProcessor): List<Int> {
        val whiteLines = mutableListOf<Int>()

        for (x in 0 until width) {
            var periodCounter = 0
            for (y in 0 until height) {
                if (imageProcessor.getColor(x, y) != Color.WHITE && periodCounter != 0) {
                    periodCounter = 0
                    whiteLines.add(periodCounter)
                } else if (y == height - 1) {
                    whiteLines.add(periodCounter + 1)
                    periodCounter = 0
                } else if (imageProcessor.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }

        return whiteLines.filter { integer -> integer >= MIN_LINE_LENGTH }
    }
}

data class Coordinate(val x: Int, val y: Int)

enum class Direction {
    HORIZONTAL, VERTICAL
}

data class CellParameters(val width: Int, val height: Int)

data class BoardParameters(val numberOfRows: Int, val numberOfColumns: Int)
