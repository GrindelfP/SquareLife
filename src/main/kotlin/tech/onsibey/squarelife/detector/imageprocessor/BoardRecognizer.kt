package tech.onsibey.squarelife.detector.imageprocessor

import ij.process.ImageProcessor

object BoardRecognizer {
    fun getBoardParameters(imageProcessor: ImageProcessor): BoardParameters {
        var numberOfRows = 0
        var numberOfColumns = 0

        for (x in 0 until imageProcessor.width) {
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
        }

        return BoardParameters(numberOfRows, numberOfColumns)
    }

    fun getCellParameters(imageProcessor: ImageProcessor): CellParameters {
        val imageWidth = imageProcessor.width
        val imageHeight = imageProcessor.height

        val setOfWidths = getWhiteLines(Direction.HORIZONTAL, imageWidth, imageHeight, imageProcessor)
        val setOfHeights = getWhiteLines(Direction.VERTICAL, imageWidth, imageHeight, imageProcessor)

        if (setOfWidths.isEmpty() || setOfHeights.isEmpty()) throw Exception("No cells found!")

        return when {
            setOfWidths.size == 1 && setOfHeights.size == 1 -> {
                CellParameters(setOfWidths.first(), setOfHeights.first())
            }
            else -> {
                val averageWidth = SingleCriteriaParetoSet(setOfWidths.toList()).averageInt()
                val averageHeights = SingleCriteriaParetoSet(setOfHeights.toList()).averageInt()
                CellParameters(averageWidth, averageHeights)
            }
        }
    }

    private fun getWhiteLines(direction: Direction, width: Int, height: Int, imageProcessor: ImageProcessor) : Set<Int> {
        val whiteLines = mutableSetOf<Int>()

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
                }
                else if (x == imageProcessor.height - 1) {
                    whiteLines.add(periodCounter + 1)
                    periodCounter = 0
                }
                else if (imageProcessor.getColor(xToCheck, yToCheck) == Color.WHITE) periodCounter++
            }
        }

        return whiteLines
    }
}

data class Coordinate(val x: Int, val y: Int)

enum class Direction {
    HORIZONTAL, VERTICAL
}

data class CellParameters(val width: Int, val height: Int)

data class BoardParameters(val numberOfRows: Int, val numberOfColumns: Int)
