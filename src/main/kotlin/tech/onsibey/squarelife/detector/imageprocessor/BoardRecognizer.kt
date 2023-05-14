package tech.onsibey.squarelife.detector.imageprocessor

import ij.ImagePlus

object BoardRecognizer {
    fun getBoardParameters(imagePlus: ImagePlus, cellParameters: CellParameters): BoardParameters {
        var numberOfRows = 0
        var numberOfColumns = 0

        // here we initiate the list of dividers, which is dedicated to determine
        // the places of checking of the number of rows. As far as the smallest possible
        // board is 5x5 cells, the dividers will be placed in the expected middle of
        // the first, third and fifth cell. The horisontal dividers are initiated
        // with the same expectancies.
        val verticalDividers = mutableListOf(
            (cellParameters.width / 2),
            cellParameters.width / 2 + 2 * cellParameters.width,
            cellParameters.width / 2 + 4 * cellParameters.width
        )

        val horizontalDividers = mutableListOf(
            cellParameters.height / 2,
            cellParameters.height / 2 + 2 * cellParameters.height,
            cellParameters.height / 2 + 4 * cellParameters.height
        )

        for (x in verticalDividers) {
            var periodCounter = 0
            for (y in 0 until imagePlus.processor.height) {
                if (imagePlus.getColor(x, y) != Color.WHITE && periodCounter != 0) {
                    periodCounter = 0
                    numberOfRows++
                }
                if (imagePlus.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }

        for (y in horizontalDividers) {
            var periodCounter = 0
            for (x in 0 until imagePlus.processor.width) {
                if (imagePlus.getColor(x, y) != Color.WHITE) {
                    periodCounter = 0
                    numberOfColumns++
                }
                if (imagePlus.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }

        return BoardParameters(numberOfRows, numberOfColumns)
    }

    fun getCellParameters(imagePlus: ImagePlus): CellParameters {
        val setOfWidths = mutableSetOf<Int>()
        for (x in 0 until imagePlus.processor.width) {
            var periodCounter = 0
            for (y in 0 until imagePlus.processor.height) {
                if (imagePlus.getColor(x, y) != Color.WHITE) {
                    periodCounter = 0
                    setOfWidths.add(periodCounter)
                }
                if (imagePlus.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }

        val setOfHeights = mutableSetOf<Int>()
        for (y in 0 until imagePlus.processor.height) {
            var periodCounter = 0
            for (x in 0 until imagePlus.processor.width) {
                if (imagePlus.getColor(x, y) != Color.WHITE) {
                    periodCounter = 0
                    setOfHeights.add(periodCounter)
                }
                if (imagePlus.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }

        val averageWidth = SingleCriteriaParetoSet(setOfWidths.toList()).averageInt()
        val averageHeights = SingleCriteriaParetoSet(setOfHeights.toList()).averageInt()

        return CellParameters(averageWidth, averageHeights)
    }
}

data class CellParameters(val width: Int, val height: Int)

data class BoardParameters(val numberOfRows: Int, val numberOfColumns: Int)
