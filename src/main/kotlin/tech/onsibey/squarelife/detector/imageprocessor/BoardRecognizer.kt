package tech.onsibey.squarelife.detector.imageprocessor

import ij.process.ImageProcessor
import tech.onsibey.squarelife.detector.imageprocessor.LineCapture.Companion.MAX_LINE_RATIO
import tech.onsibey.squarelife.detector.imageprocessor.LineCapture.Companion.MIN_LINE_RATIO
import tech.onsibey.squarelife.detector.imageprocessor.Processor.COLOR_THRESHOLD
import java.io.File
import kotlin.math.roundToInt

interface Recognizer {
    fun getBoardParameters(imageProcessor: ImageProcessor, cellParameters: CellParameters): BoardParameters
    fun getCellParameters(imageProcessor: ImageProcessor): CellParameters
}

interface LineCapture {

    companion object {
        internal const val MIN_LINE_RATIO = 0.01
        internal const val MAX_LINE_RATIO = 0.5
    }

    fun getHorizontalWhiteLines(imageProcessor: ImageProcessor): List<Int>

    fun getVerticalWhiteLines(imageProcessor: ImageProcessor): List<Int>
}

object BoardRecognizer: Recognizer {
    override fun getBoardParameters(imageProcessor: ImageProcessor, cellParameters: CellParameters): BoardParameters {
        val numberOfRows = ((imageProcessor.height).toDouble() / (cellParameters.height).toDouble()).roundToInt()
        val numberOfColumns = ((imageProcessor.width).toDouble() / (cellParameters.width).toDouble()).roundToInt()

        return BoardParameters(numberOfRows, numberOfColumns)
    }

    override fun getCellParameters(imageProcessor: ImageProcessor): CellParameters {
        val listOfWidths = WhiteLinesAnalyticalCapture.getHorizontalWhiteLines(imageProcessor)
        val listOfHeights = WhiteLinesAnalyticalCapture.getVerticalWhiteLines(imageProcessor)

        val widths = listOfWidths.sorted().joinToString("\n", prefix = "")
        val heights = listOfHeights.sorted().joinToString("\n", prefix = "")

        File("widths.txt").writeText(widths)
        File("heights.txt").writeText(heights)

        require(listOfWidths.isNotEmpty() && listOfHeights.isNotEmpty()) { "No cells found!" }

        // 1. Find the most common width and height intervals
        // 2. Find the average width and height of these most common intervals
        // 3. Check if image.width / averageWidth and image.height / averageHeight remains in appropriate range e. g.
        // 723 % 71 = 13, 723 % 84 = 51 , so 71 is best
        // use this averages as cell parameters

        val averageWidth = SingleCriteriaParetoSet(listOfWidths, 10).averageInt()
        val averageHeights = SingleCriteriaParetoSet(listOfHeights, 10).averageInt()

        return CellParameters(averageWidth, averageHeights)
    }
}

object WhiteLinesParetoCapture: LineCapture {
    private const val MIN_LINE_LENGTH = 2

    override fun getHorizontalWhiteLines(imageProcessor: ImageProcessor): List<Int> {
        val whiteLines = mutableListOf<Int>()

        for (y in 0 until imageProcessor.height) {
            var periodCounter = 0
            for (x in 0 until imageProcessor.width) {
                if (imageProcessor.getColor(x, y) != Color.WHITE && periodCounter != 0) {
                    periodCounter = 0
                    whiteLines.add(periodCounter)
                } else if (x == imageProcessor.width - 1) {
                    whiteLines.add(periodCounter + 1)
                    periodCounter = 0
                } else if (imageProcessor.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }

        return whiteLines.filter { integer -> integer >= MIN_LINE_LENGTH }
    }

    override fun getVerticalWhiteLines(imageProcessor: ImageProcessor): List<Int> {
        val whiteLines = mutableListOf<Int>()

        for (x in 0 until imageProcessor.width) {
            var periodCounter = 0
            for (y in 0 until imageProcessor.height) {
                if (imageProcessor.getColor(x, y) != Color.WHITE && periodCounter != 0) {
                    periodCounter = 0
                    whiteLines.add(periodCounter)
                } else if (y == imageProcessor.height - 1) {
                    whiteLines.add(periodCounter + 1)
                    periodCounter = 0
                } else if (imageProcessor.getColor(x, y) == Color.WHITE) periodCounter++
            }
        }

        return whiteLines.filter { integer -> integer >= MIN_LINE_LENGTH }
    }
}

object WhiteLinesAnalyticalCapture: LineCapture {
    override fun getHorizontalWhiteLines(imageProcessor: ImageProcessor): List<Int> {
        val croppedImageWidth = imageProcessor.width
        val croppedImageHeight = imageProcessor.height

        val monochromeImageRepresentation = Array(croppedImageHeight) { Array(croppedImageWidth) { false } }

        for (y in 0 until croppedImageHeight) {
            for (x in 0 until croppedImageWidth) {
                val pixelColor = imageProcessor.getValue(x, y)
                monochromeImageRepresentation[y][x] = pixelColor > COLOR_THRESHOLD
            }
        }
        
        val lines = mutableListOf<Int>()
        monochromeImageRepresentation.forEach { row ->
            var counter = 0
            row.forEach { black ->
                when {
                    black && counter > 0 -> {
                        lines.add(counter)
                        counter = 0
                    }
                    else -> ++counter
                }
            }
        }
        return lines.filter { integer -> integer > imageProcessor.width * MIN_LINE_RATIO && integer < imageProcessor.width * MAX_LINE_RATIO }
    }

    override fun getVerticalWhiteLines(imageProcessor: ImageProcessor): List<Int> {
        val croppedImageWidth = imageProcessor.width
        val croppedImageHeight = imageProcessor.height

        val monochromeImageRepresentation = Array(croppedImageHeight) { Array(croppedImageWidth) { false } }

        for (y in 0 until croppedImageHeight) {
            for (x in 0 until croppedImageWidth) {
                val pixelColor = imageProcessor.getValue(x, y)
                monochromeImageRepresentation[y][x] = pixelColor > COLOR_THRESHOLD
            }
        }

        val lines = mutableListOf<Int>()
        val width = monochromeImageRepresentation.first().size
        for (column in 0 until width) {
            var counter = 0
            for (element in monochromeImageRepresentation) {
                val black = element[column]
                when {
                    black && counter > 0 -> {
                        lines.add(counter)
                        counter = 0
                    }
                    else -> ++counter
                }
            }
        }
        return lines.filter { integer -> integer > imageProcessor.height * MIN_LINE_RATIO && integer < imageProcessor.height * MAX_LINE_RATIO }
    }

}

data class Coordinate(val x: Int, val y: Int)

data class CellParameters(val width: Int, val height: Int)

data class BoardParameters(val numberOfRows: Int, val numberOfColumns: Int)
