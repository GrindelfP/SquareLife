package tech.onsibey.squarelife.detector.imageprocessor

import ij.IJ
import ij.process.ImageProcessor
import tech.onsibey.squarelife.detector.imageprocessor.ImageJGridCellRecognition.recognizeDominantColours
import java.awt.Rectangle
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt

object Processor {
    const val COLOR_THRESHOLD = 128

    fun processImageBoard(pathToImage: String): ImageBoard = ImageBoard(cells(File(pathToImage)))


    private fun cells(file: File): List<List<Cell>> {
        val imagePlus = IJ.openImage(file.absolutePath)
        require(imagePlus != null) { "There is no image in provided path: ${file.absolutePath}" }

        // cropping the image to the size of the grid
        val isolatedGameBoardProcessor = imagePlus.processor.isolatedGameBoard()

        /*isolatedGameBoardProcessor.filter(MIN) // Trying to smooth the color of the image
        repeat(30) {
            isolatedGameBoardProcessor.smooth()
        }*/

        // Uncomment to save and take a look at the cropped picture
        ImageIO.write(isolatedGameBoardProcessor.bufferedImage, "jpg", File("cropped-preprocessed.jpg"))

        // split image into cells
        //val cells: List<List<ImageProcessor>> = divideImageByGrid(isolatedGameBoardProcessor)


        val cellsColors: List<List<Color>> = recognizeDominantColours(/*cells*/emptyList()) // get each sell's colour (black or white)

        return convertToCells(cellsColors) // interpret colors
    }

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
    val toleranceThreshold = 0.6 // 60% of the image should be of the same color
    val pixelsCount = this.width * this.height
    val colors = this.countColors()

    // we need to check if we've covered all the pixels for color percentage checking
    require(pixelsCount == colors.numberOfLightPixels + colors.numberOfDarkPixels)
    { "Not all pixels are checked by color" }
    
    fun PixelColorCounter.colorIsDominating(isDark: Boolean): Boolean = when {
        isDark -> numberOfDarkPixels.toDouble() / pixelsCount >= toleranceThreshold
        else -> numberOfLightPixels.toDouble() / pixelsCount >= toleranceThreshold
    }

    fun PixelColorCounter.neitherColorIsDominating(): Boolean =
        !this.colorIsDominating(isDark = true) && !this.colorIsDominating(isDark = false)

    return when {
        colors.colorIsDominating(isDark = false) -> Color.WHITE
        colors.colorIsDominating(isDark = true) -> Color.BLACK
        colors.neitherColorIsDominating() -> {
            val quarterImageProcessor = this.similarQuarterRectangle()
            quarterImageProcessor.dominantColour()
        }

        else -> {
            require(this.width == 1 || this.height == 1) { "Image is not proper" }
            if (this.getColor(0, 0) == Color.WHITE) Color.WHITE else Color.BLACK
        }
    }
}

fun ImageProcessor.countColors(): PixelColorCounter {
    var numberOfLightPixels = 0
    var numberOfDarkPixels = 0

    for (x in 0 until bufferedImage.width) {
        for (y in 0 until bufferedImage.height) {
            val pixelColor = this.getColor(x, y)
            if (pixelColor == Color.WHITE) numberOfLightPixels++
            else numberOfDarkPixels++
        }
    }

    return PixelColorCounter(numberOfLightPixels, numberOfDarkPixels)
}

private fun ImageProcessor.similarQuarterRectangle(): ImageProcessor {
    val iterationsCoordinateCoefficient = 0.25
    val iterationsSizeCoefficient = 0.5

    val newX = (this.width * iterationsCoordinateCoefficient).roundToInt()
    val newY = (this.height * iterationsCoordinateCoefficient).roundToInt()
    val newWidth = (this.width * iterationsSizeCoefficient).roundToInt()
    val newHeight = (this.height * iterationsSizeCoefficient).roundToInt()

    this.roi = Rectangle(newX, newY, newWidth, newHeight)

    return this.crop()
}

fun ImageProcessor.getColor(x: Int, y: Int): Color {
    val pixelColourRGB = this.getValue(x, y)
    return if (pixelColourRGB > Processor.COLOR_THRESHOLD) Color.WHITE else Color.BLACK
}

data class PixelColorCounter(val numberOfLightPixels: Int, val numberOfDarkPixels: Int)
