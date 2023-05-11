package tech.onsibey.squarelife.detector.imageprocessor

import ij.IJ
import ij.ImagePlus
import ij.measure.ResultsTable
import ij.process.ByteProcessor
import ij.process.ImageProcessor.MIN
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp
import java.io.File

class Processor(private val pathToPhoto: String) {
    // fun processImageBoard(): ImageBoard = ImageBoard(convertToCells(analyzeGrid(File(pathToPhoto))))
    fun processImageBoard(): ImageBoard = ImageBoard(convertToCells(analyzeGridAlternative(File(pathToPhoto))))

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

    // Function to analyze the grid image and return a 2D list of booleans
    private fun analyzeGrid(file: File): List<List<Boolean>> {
        // Load the image using the ImageJ library
        val image = IJ.openImage(file.absolutePath)
        // Convert the image to grayscale using the new extension function
        //IJ.convertToGray(image)
        val grayImage = convertToGray(image)
        // Threshold the image to separate the colored cells from the blank cells
        IJ.setAutoThreshold(grayImage, "Default dark")
        IJ.run(grayImage, "Threshold", "")
        // Identify the individual cells in the grayImage
        IJ.run(grayImage, "Analyze Particles...", "size=10-Infinity show=Nothing")
        // Get the coordinates and sizes of the identified particles
        val rt = ResultsTable.getResultsTable()
        val x = rt.getColumnAsDoubles(ResultsTable.X_CENTROID)
        val y = rt.getColumnAsDoubles(ResultsTable.Y_CENTROID)
        val width = rt.getColumnAsDoubles(rt.getColumnIndex("Width"))
        val height = rt.getColumnAsDoubles(rt.getColumnIndex("Height"))
        // Create a 2D list of booleans to represent the grid
        val grid = MutableList(grayImage.height) { MutableList(grayImage.width) { false } }
        // Loop through the identified cells and set the corresponding element in the 2D list
        for (i in x.indices) {
            // Calculate the x and y coordinates of the cell based on its centroid and size
            val x0 = (x[i] - width[i] / 2).toInt()
            val y0 = (y[i] - height[i] / 2).toInt()
            // Loop through the pixels in the cell and set the corresponding element in the 2D list to true
            for (j in x0 until x0 + width[i].toInt()) {
                for (k in y0 until y0 + height[i].toInt()) {
                    // Check that the pixel is within the bounds of the grayImage
                    if (j >= 0 && j < grayImage.width && k >= 0 && k < grayImage.height) {
                        // Set the corresponding element in the 2D list to true
                        grid[k][j] = true
                    }
                }
            }
        }

        // Return the resulting 2D list of booleans
        return grid
    }

    // Proposal on how to analyze the picture
    private fun analyzeGridAlternative(file: File): List<List<Boolean>> {
        val imagePlus = IJ.openImage(file.absolutePath)
        //val originalImageProcessor = imagePlus.processor
        val imageProcessor = imagePlus.processor
        imageProcessor.smooth() // Smooth the image to remove noise

        // Color threshold to convert the image to black and white
        val threshold = 128

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
                if (imageProcessor.getValue(x, y) < threshold) {
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
        val croppedImageProcessor = imageProcessor.crop()
        // Trying to smooth the color of the image
        croppedImageProcessor.filter(MIN)
        repeat(30) {
            croppedImageProcessor.smooth()
        }

        // Uncomment to save and take a look at the cropped picture
        // ImageIO.write(croppedImageProcessor.bufferedImage, "jpg", File("cropped-preprocessed.jpg"))

        // Convert the cropped image to a 2D list of booleans based on the color threshold
        val croppedImageWidth = croppedImageProcessor.width
        val croppedImageHeight = croppedImageProcessor.height

        val monochromeImageRepresentation = Array(croppedImageHeight) { Array(croppedImageWidth) { false } }

        for (y in 0 until croppedImageHeight) {
            for (x in 0 until croppedImageWidth) {
                val pixelColor = croppedImageProcessor.getValue(x, y)
                monochromeImageRepresentation[y][x] = pixelColor > threshold
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


    private fun convertToGray(image: ImagePlus): ImagePlus {
        // Convert the ImagePlus object to a BufferedImage
        val bufferedImage = image.bufferedImage
        // Create a new BufferedImage object with a single grayscale channel
        val grayBufferedImage = BufferedImage(bufferedImage.width, bufferedImage.height, BufferedImage.TYPE_BYTE_GRAY)
        // Convert the RGB image to grayscale using the ColorConvertOp class
        val colorConvertOp = ColorConvertOp(null)
        colorConvertOp.filter(bufferedImage, grayBufferedImage)
        // Convert the BufferedImage object back to an ImagePlus object with grayscale pixel values
        return ImagePlus("Grayscale", ByteProcessor(grayBufferedImage))
    }

}

data class ImageBoard(val cells: List<List<Cell>>)

data class Cell(var isPainted: Boolean) {
    fun reverseState() {
        this.isPainted = !isPainted
    }
}
