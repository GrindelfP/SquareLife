package tech.onsibey.squarelife.detector.imageprocessor

import ij.IJ
import ij.ImagePlus
import ij.process.ImageProcessor
import ij.process.ImageProcessor.MIN
import java.awt.Rectangle
import java.io.File
import javax.imageio.ImageIO


class Processor(private val pathToImage: String) : GridCellsRecognition {
    companion object {
        private const val COLOR_THRESHOLD = 128
    }

    fun processImageBoard(): ImageBoard = ImageBoard(analyzeGrid(File(pathToImage)))

    override fun divideImageByGrid(imagePlus: ImagePlus, gridSize: Int): List<List<ImagePlus>> {
        val imageProcessor = imagePlus.processor
        val imageWidth = imageProcessor.width
        val imageHeight = imageProcessor.height

        val cellWidth = imageWidth / gridSize
        val cellHeight = imageHeight / gridSize

        val cells = mutableListOf<MutableList<ImagePlus>>()

        for (y in 0 until gridSize) {
            val row = mutableListOf<ImagePlus>()
            for (x in 0 until gridSize) {
                val bufferImageProcessor = imageProcessor
                bufferImageProcessor.roi = Rectangle(x * cellWidth, y * cellHeight, cellWidth, cellHeight)
                row.add(ImagePlus("$x, $y", bufferImageProcessor.crop()))

            }
            cells.add(row)
        }

        cells.forEach { row ->
            row.forEach { cell ->
                cell.write()
            }
        }

        return cells
    }

    private fun ImagePlus.write() {
        ImageIO.write(this.processor.bufferedImage, "jpg", File("cells/${this.title}.jpg"))
    }

    override fun recognizeDominantColours(images: List<List<ImagePlus>>): List<List<CellColor>> {
        val cellsByColors = mutableListOf<MutableList<CellColor>>()
        images.forEach { rowOfImages ->
            val cellsRow = mutableListOf<CellColor>()
            rowOfImages.forEach { image ->
                cellsRow.add(image.dominantColour())
            }
            cellsByColors.add(cellsRow)
        }

        return cellsByColors
    }

    private fun ImagePlus.dominantColour(): CellColor {
        var lightSide = 0
        var darkSide = 0

        for (x in 0 until bufferedImage.width) {
            for (y in 0 until bufferedImage.height) {
                val color = bufferedImage.getRGB(x, y) // TODO: check the color return of the .getRGB() method
                if (color in 0 .. 128) darkSide++
                else lightSide++
            }
        }

        return if (lightSide > darkSide) CellColor.WHITE else CellColor.BlACK
    }

    private fun convertToCells(analyzedGrid: List<List<CellColor>>): List<List<Cell>> {
        val cells = mutableListOf<MutableList<Cell>>()
        analyzedGrid.forEach { row ->
            val rowCells = mutableListOf<Cell>()
            row.forEach { cell ->
                val interpretedCell = if (cell == CellColor.BlACK) Cell(true) else Cell(false)
                rowCells.add(interpretedCell)
            }
            cells.add(rowCells)
        }

        return cells
    }

    private fun analyzeGrid(file: File): List<List<Cell>> {
        val imagePlus = IJ.openImage(file.absolutePath)
        require(imagePlus != null) { "There is no image in provided path: $pathToImage" }

        val croppedImageProcessor = cropImage(imagePlus.processor) // cropping the image to the size of the grid

        croppedImageProcessor.filter(MIN) // Trying to smooth the color of the image
        repeat(30) {
            croppedImageProcessor.smooth()
        }

        // Uncomment to save and take a look at the cropped picture
        ImageIO.write(croppedImageProcessor.bufferedImage, "jpg", File("cropped-preprocessed.jpg"))

        // split image into cells
        val cells = divideImageByGrid(ImagePlus("croppedImage", croppedImageProcessor), 10)

        val cellsColors = recognizeDominantColours(cells) // get each sell's colour (black or white)

        return convertToCells(cellsColors) // interpret c
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

enum class CellColor {
    BlACK,
    WHITE
}

data class ImageBoard(val cells: List<List<Cell>>)

data class Cell(var isPainted: Boolean) {
    fun reverseState() {
        this.isPainted = !isPainted
    }
}
