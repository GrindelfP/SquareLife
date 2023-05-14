package tech.onsibey.squarelife.detector.imageprocessor

import ij.ImagePlus
import ij.process.ImageProcessor
import java.awt.Rectangle
import java.io.File
import javax.imageio.ImageIO

interface GridCellsRecognition {
    fun divideImageByGrid(
        imageProcessor: ImageProcessor,
        cellHeight: Int = 5,
        cellWidth: Int,
        numberOfRows: Int,
        numberOfColumns: Int
    ): List<List<ImagePlus>>

    fun recognizeDominantColours(images: List<List<ImagePlus>>): List<List<Color>>
}

object ImageJGridCellRecognition : GridCellsRecognition {
    override fun divideImageByGrid(
        imageProcessor: ImageProcessor,
        cellHeight: Int,
        cellWidth: Int,
        numberOfRows: Int,
        numberOfColumns: Int
    ): List<List<ImagePlus>> { // are these params needed (mb calculate board and cell params inside)?
        // maybe return ImageProcessor instead of ImagePlus?
        val cells = mutableListOf<MutableList<ImagePlus>>()

        for (y in 0 until numberOfColumns) {
            val row = mutableListOf<ImagePlus>()
            for (x in 0 until numberOfRows) {
                imageProcessor.roi = Rectangle(x * cellWidth, y * cellHeight, cellWidth, cellHeight)
                row.add(ImagePlus("$x, $y", imageProcessor.crop()))
            }
            cells.add(row)
        }

        // writing of image parts, not needed on release!
        cells.forEach { row ->
            row.forEach { cell ->
                ImageIO.write(cell.processor.bufferedImage, "jpg", File("cells/${cell.title}.jpg"))
            }
        }

        return cells
    }


    override fun recognizeDominantColours(images: List<List<ImagePlus>>): List<List<Color>> {
        val cellsByColors = mutableListOf<MutableList<Color>>()
        images.forEach { rowOfImages ->
            val cellsRow = mutableListOf<Color>()
            rowOfImages.forEach { image ->
                cellsRow.add(image.dominantColour())
            }
            cellsByColors.add(cellsRow)
        }

        return cellsByColors
    }
}
