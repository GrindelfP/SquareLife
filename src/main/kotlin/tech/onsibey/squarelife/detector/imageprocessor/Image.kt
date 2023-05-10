package tech.onsibey.squarelife.detector.imageprocessor

import ij.IJ
import ij.ImagePlus
import ij.measure.ResultsTable
import ij.process.ByteProcessor
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp
import java.io.File

class Processor(private val pathToPhoto: String) {
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
