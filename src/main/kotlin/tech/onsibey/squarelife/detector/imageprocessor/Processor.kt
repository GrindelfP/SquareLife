package tech.onsibey.squarelife.detector.imageprocessor

import ij.IJ
import ij.process.ImageProcessor
import ij.process.ImageProcessor.MIN
import java.awt.Rectangle
import java.io.File
import javax.imageio.ImageIO

class Processor(private val pathToPhoto: String) {
    companion object {
        private const val COLOR_THRESHOLD = 128
    }

    fun processImageBoard(): ImageBoard = ImageBoard(convertToCells(analyzeGrid(File(pathToPhoto))))

    private fun convertToCells(analyzedGrid: List<List<Boolean>>): List<List<Cell>> {
        val cells = mutableListOf<MutableList<Cell>>()
        analyzedGrid.forEach { row ->
            val rowCells = mutableListOf<Cell>()
            row.forEach { cell ->
                rowCells.add(Cell(cell))
            }
            cells.add(rowCells)
        }

        return cells
    }

    // Proposal on how to analyze the picture
    private fun analyzeGrid(file: File): List<List<Boolean>> {
        val imagePlus = IJ.openImage(file.absolutePath)
        val croppedImageProcessor = cropImage(imagePlus.processor) // cropping the image to the size of the grid

        croppedImageProcessor.filter(MIN) // Trying to smooth the color of the image
        repeat(30) {
            croppedImageProcessor.smooth()
        }

        // Uncomment to save and take a look at the cropped picture
        ImageIO.write(croppedImageProcessor.bufferedImage, "jpg", File("cropped-preprocessed.jpg"))



        // Convert the cropped image to a 2D list of booleans based on the color COLOR_COLOR_THRESHOLD
        val croppedImageWidth = croppedImageProcessor.width
        val croppedImageHeight = croppedImageProcessor.height

        val monochromeImageRepresentation = Array(croppedImageHeight) { Array(croppedImageWidth) { false } }

        for (y in 0 until croppedImageHeight) {
            for (x in 0 until croppedImageWidth) {
                val pixelColor = croppedImageProcessor.getValue(x, y)
                monochromeImageRepresentation[y][x] = pixelColor > COLOR_THRESHOLD
            }
        }

        // Algorithm or conversion of the cropped image to a 2D list of booleans
        // Find the size of the smallest cell
        // Use the size of the smallest cell to "split" the image into cells
        // Check center of each cell to see if it is black or white
        // Build a 2D list of booleans based on the color of the center of each cell

        // Now we just return the list of booleans based on each pixel of the cropped image [TEMPORARY, NEEDS TO BE REPLACED]
        return monochromeImageRepresentation.map { it.toList() }.toList()
    }

    private fun cropImage(imageProcessor: ImageProcessor): ImageProcessor {
        imageProcessor.smooth() // Smooth the image to remove noise

        // Color COLOR_COLOR_THRESHOLD to convert the image to black and white
        val imageWidth = imageProcessor.width
        val imageHeight = imageProcessor.height

        // Crop the original image to the size of the grid image
        // 1. Find the boundaries of the grid image
        var top: Int = -100
        var bottom: Int = -100
        var left: Int = -100
        var right: Int = -100
        for (y in 0 until imageHeight) {
            for (x in 0 until imageWidth) {
                if (imageProcessor.getValue(x, y) < COLOR_THRESHOLD) {
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

        imageProcessor.roi = Rectangle(left, top, gridWidth, gridHeight)

        return imageProcessor.crop()
    }
}

data class ImageBoard(val cells: List<List<Cell>>)

data class Cell(var isPainted: Boolean) {
    fun reverseState() {
        this.isPainted = !isPainted
    }
}
