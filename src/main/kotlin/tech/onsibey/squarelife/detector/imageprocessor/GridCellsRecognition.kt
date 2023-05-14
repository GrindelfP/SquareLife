package tech.onsibey.squarelife.detector.imageprocessor

import ij.process.ImageProcessor
import tech.onsibey.squarelife.detector.imageprocessor.BoardRecognizer.getBoardParameters
import tech.onsibey.squarelife.detector.imageprocessor.BoardRecognizer.getCellParameters
import java.awt.Rectangle
import java.io.File
import javax.imageio.ImageIO

interface GridCellsRecognition {
    fun divideImageByGrid(imageProcessor: ImageProcessor): List<List<ImageProcessor>>

    fun recognizeDominantColours(imageProcessors: List<List<ImageProcessor>>): List<List<Color>>
}

object ImageJGridCellRecognition : GridCellsRecognition {
    override fun divideImageByGrid(imageProcessor: ImageProcessor): List<List<ImageProcessor>> {
        val cells = mutableListOf<MutableList<ImageProcessor>>()

        /*val boardParameters = getBoardParameters(imageProcessor)
        val cellParameters = getCellParameters(imageProcessor)

        val numberOfRows = boardParameters.numberOfRows
        val numberOfColumns = boardParameters.numberOfColumns
        val cellHeight = cellParameters.height
        val cellWidth = cellParameters.width*/

        val numberOfRows = 10
        val numberOfColumns = 10
        val cellHeight = 82
        val cellWidth = 71


        for (y in 0 until numberOfColumns) {
            val row = mutableListOf<ImageProcessor>()
            for (x in 0 until numberOfRows) {
                imageProcessor.roi = Rectangle(x * cellWidth, y * cellHeight, cellWidth, cellHeight)
                row.add(imageProcessor.crop())
            }
            cells.add(row)
        }

        // writing of image parts, not needed on release!
        cells.forEachIndexed { x, row ->
            row.forEachIndexed { y, cell ->
                ImageIO.write(cell.bufferedImage, "jpg", File("cells/$x, $y.jpg"))
            }
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
