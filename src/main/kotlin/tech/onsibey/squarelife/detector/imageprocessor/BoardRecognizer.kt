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
        val setOfWidths = mutableSetOf<Int>()
        for (x in 0 until imageProcessor.width) {
            var periodCounter = 0
            for (y in 0 until imageProcessor.height) {
                if (imageProcessor.getColor(x, y) != Color.WHITE) {
                    periodCounter = 0
                    setOfWidths.add(periodCounter)
                }
                if (imageProcessor.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }

        val setOfHeights = mutableSetOf<Int>()
        for (y in 0 until imageProcessor.height) {
            var periodCounter = 0
            for (x in 0 until imageProcessor.width) {
                if (imageProcessor.getColor(x, y) != Color.WHITE) {
                    periodCounter = 0
                    setOfHeights.add(periodCounter)
                }
                if (imageProcessor.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }

        val averageWidth = SingleCriteriaParetoSet(setOfWidths.toList()).averageInt()
        val averageHeights = SingleCriteriaParetoSet(setOfHeights.toList()).averageInt()

        return CellParameters(averageWidth, averageHeights)
    }
}

data class CellParameters(val width: Int, val height: Int)

data class BoardParameters(val numberOfRows: Int, val numberOfColumns: Int)
