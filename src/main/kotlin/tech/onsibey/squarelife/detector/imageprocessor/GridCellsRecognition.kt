package tech.onsibey.squarelife.detector.imageprocessor

import ij.process.ImageProcessor
import tech.onsibey.squarelife.detector.imageprocessor.BoardRecognizer.getBoardParameters
import java.awt.Rectangle

interface GridCellsRecognition {
    fun divideImageByGrid(imageProcessor: ImageProcessor, cellParameters: CellParameters): List<List<ImageProcessor>>

    fun recognizeDominantColours(imageProcessors: List<List<ImageProcessor>>): List<List<Color>>
}

object ImageJGridCellRecognition : GridCellsRecognition {
    override fun divideImageByGrid(
        imageProcessor: ImageProcessor,
        cellParameters: CellParameters
    ): List<List<ImageProcessor>> {
        val cells = mutableListOf<MutableList<ImageProcessor>>()
        val boardParameters = getBoardParameters(imageProcessor, cellParameters)

        val numberOfRows = boardParameters.numberOfRows
        val numberOfColumns = boardParameters.numberOfColumns
        val cellHeight = cellParameters.height
        val cellWidth = cellParameters.width

        for (y in 0 until numberOfColumns) {
            val row = mutableListOf<ImageProcessor>()
            for (x in 0 until numberOfRows) {
                var requiredWidth = cellWidth
                if (x * cellWidth + requiredWidth > imageProcessor.width) {
                    requiredWidth = imageProcessor.width - x * cellWidth
                }
                var requiredHeight = cellHeight
                if (y * cellHeight + requiredHeight > imageProcessor.height) {
                    requiredHeight = imageProcessor.height - y * cellHeight
                }
                if (x * cellWidth > imageProcessor.width || y * cellHeight > imageProcessor.height) break

                imageProcessor.roi = Rectangle(x * cellWidth, y * cellHeight, requiredWidth, requiredHeight)
                row.add(imageProcessor.crop())
            }
            cells.add(row)
        }

        return cells
    }


    override fun recognizeDominantColours(imageProcessors: List<List<ImageProcessor>>): List<List<Color>> {
        val cellsByColors = mutableListOf<MutableList<Color>>()
        imageProcessors.forEach { rowOfImageProcessors ->
            val cellsRow = mutableListOf<Color>()
            rowOfImageProcessors.forEach { imageProcessor ->
                cellsRow.add(imageProcessor.dominantColour())
            }
            cellsByColors.add(cellsRow)
        }

        return cellsByColors
    }
}
