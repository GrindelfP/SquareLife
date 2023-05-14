package tech.onsibey.squarelife.detector.imageprocessor

import ij.IJ
import ij.process.ImageProcessor
import ij.process.ImageProcessor.MIN
import tech.onsibey.squarelife.detector.imageprocessor.ImageJGridCellRecognition.divideImageByGrid
import tech.onsibey.squarelife.detector.imageprocessor.ImageJGridCellRecognition.recognizeDominantColours
import java.awt.Rectangle
import java.io.File
import javax.imageio.ImageIO


object Processor {
    const val COLOR_THRESHOLD = 128

    fun processImageBoard(pathToImage: String): ImageBoard = ImageBoard(cells(File(pathToImage)))

    private fun convertToCells(analyzedGrid: List<List<Color>>): List<List<Cell>> { // TESTED
        val cells = mutableListOf<MutableList<Cell>>()
        analyzedGrid.forEach { row ->
            val rowCells = mutableListOf<Cell>()
            row.forEach { cell ->
                val interpretedCell = if (cell == Color.BLACK) Cell(true) else Cell(false)
                rowCells.add(interpretedCell)
            }
            cells.add(rowCells)
        }

        return cells
    }

    private fun cells(file: File): List<List<Cell>> {
        val imagePlus = IJ.openImage(file.absolutePath)
        require(imagePlus != null) { "There is no image in provided path: ${file.absolutePath}" }

        // cropping the image to the size of the grid
        val isolatedGameBoardProcessor = imagePlus.processor.isolatedGameBoard()

        isolatedGameBoardProcessor.filter(MIN) // Trying to smooth the color of the image
        repeat(30) {
            isolatedGameBoardProcessor.smooth()
        }

        // Uncomment to save and take a look at the cropped picture
        ImageIO.write(isolatedGameBoardProcessor.bufferedImage, "jpg", File("cropped-preprocessed.jpg"))

        // split image into cells
        val cells: List<List<ImageProcessor>> = divideImageByGrid(isolatedGameBoardProcessor)

        val cellsColors: List<List<Color>> = recognizeDominantColours(cells) // get each sell's colour (black or white)

        return convertToCells(cellsColors) // interpret colors
    }

    private fun ImageProcessor.isolatedGameBoard(): ImageProcessor {
        this.smooth() // Smooth the image to remove noise

        // Color COLOR_COLOR_THRESHOLD to convert the image to black and white
        val imageWidth = this.width
        val imageHeight = this.height

        // Crop the original image to the size of the grid image
        // 1. Find the boundaries of the grid image
        var top: Int = -100
        var bottom: Int = -100
        var left: Int = -100
        var right: Int = -100
        for (y in 0 until imageHeight) {
            for (x in 0 until imageWidth) {
                if (this.getValue(x, y) < COLOR_THRESHOLD) {
                    if (top == -100) top = y
                    if (top > y) top = y

                    if (bottom == -100) bottom = y
                    if (bottom < y) bottom = y

                    if (left == -100) left = x
                    if (left > x) left = x

                    if (right == -100) right = x
                    if (right < x) right = x
                }
            }
        }

        // 2. Find width and height of the grid image
        val gridWidth = right - left
        val gridHeight = bottom - top

        this.roi = Rectangle(left, top, gridWidth, gridHeight)

        return this.crop()
    }
}

enum class Color {
    BLACK,
    WHITE
}

data class ImageBoard(val cells: List<List<Cell>>)

data class Cell(var isPainted: Boolean) {
    fun reverseState() {
        this.isPainted = !isPainted
    }
}

fun ImageProcessor.dominantColour(): Color {
    var lightSide = 0
    var darkSide = 0

    for (x in 0 until bufferedImage.width) {
        for (y in 0 until bufferedImage.height) {
            val pixelColor = this.getColor(x, y)
            if (pixelColor == Color.WHITE) lightSide++
            else darkSide++
        }
    }

    return if (lightSide > darkSide) Color.WHITE else Color.BLACK
}

fun ImageProcessor.getColor(x: Int, y: Int): Color {
    val pixelColourRGB = this.getValue(x, y)
    return if (pixelColourRGB > Processor.COLOR_THRESHOLD) Color.WHITE else Color.BLACK
}
