package tech.onsibey.squarelife.detector.imageprocessor

import ij.process.ImageProcessor
import tech.onsibey.squarelife.detector.imageprocessor.Average.Companion.INTERVALS_COUNT
import tech.onsibey.squarelife.detector.imageprocessor.Average.Companion.INTERVAL_RATIO
import tech.onsibey.squarelife.detector.imageprocessor.AverageCounter.getValueFrequency
import tech.onsibey.squarelife.detector.imageprocessor.AverageCounter.getValuesByNormalDistribution
import tech.onsibey.squarelife.detector.imageprocessor.LineCapture.Companion.MAX_LINE_RATIO
import tech.onsibey.squarelife.detector.imageprocessor.LineCapture.Companion.MIN_LINE_RATIO
import tech.onsibey.squarelife.detector.imageprocessor.Processor.COLOR_THRESHOLD
import kotlin.math.roundToInt

interface Recognizer {
    fun getBoardParameters(imageProcessor: ImageProcessor, cellParameters: CellParameters): BoardParameters
    fun getCellParameters(imageProcessor: ImageProcessor): List<CellParameters>
}

interface LineCapture {

    companion object {
        internal const val MIN_LINE_RATIO = 0.01
        internal const val MAX_LINE_RATIO = 0.5
    }

    fun getHorizontalWhiteLines(imageProcessor: ImageProcessor): List<Int>

    fun getVerticalWhiteLines(imageProcessor: ImageProcessor): List<Int>
}

interface Average {

    companion object {
        internal const val INTERVALS_COUNT = 40
        internal const val INTERVAL_RATIO = 1.0 / INTERVALS_COUNT
        internal const val SAMPLES_LIMIT = 3
    }

    fun getValueFrequency(dataList: List<Int>): Array<Interval>

    fun getValuesByNormalDistribution(edgesArray: Array<Interval>): List<Int>
}

object BoardRecognizer : Recognizer {
    override fun getBoardParameters(imageProcessor: ImageProcessor, cellParameters: CellParameters): BoardParameters {
        val numberOfRows = ((imageProcessor.height).toDouble() / (cellParameters.height).toDouble()).roundToInt()
        val numberOfColumns = ((imageProcessor.width).toDouble() / (cellParameters.width).toDouble()).roundToInt()

        return BoardParameters(numberOfRows, numberOfColumns)
    }

    override fun getCellParameters(imageProcessor: ImageProcessor): List<CellParameters> {
        val listOfWidths = WhiteLinesAnalyticalCapture.getHorizontalWhiteLines(imageProcessor)
        val listOfHeights = WhiteLinesAnalyticalCapture.getVerticalWhiteLines(imageProcessor)

        // uncomment for debug
        /*val widths = listOfWidths.sorted().joinToString("\n", prefix = "")
        val heights = listOfHeights.sorted().joinToString("\n", prefix = "")

        File("widths.txt").writeText(widths)
        File("heights.txt").writeText(heights)*/

        require(listOfWidths.isNotEmpty() && listOfHeights.isNotEmpty()) { "No cells found!" }

        val widthsFrequencyMap = getValuesByNormalDistribution(getValueFrequency(listOfWidths))
        val heightsFrequencyMap = getValuesByNormalDistribution(getValueFrequency(listOfHeights))

        return widthsFrequencyMap.zip(heightsFrequencyMap).map { (width, height) ->
            CellParameters(width, height)
        }
    }
}

object WhiteLinesParetoCapture : LineCapture {
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

object WhiteLinesAnalyticalCapture : LineCapture {
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

object AverageCounter : Average {

    override fun getValueFrequency(dataList: List<Int>): Array<Interval> {
        require(dataList.isNotEmpty()) { "No data to aggregate frequency!" }

        val frequencyMap = Array(INTERVALS_COUNT + 1) { Interval() }
        dataList.sorted()

        val intervalLength =
            ((dataList.max() - dataList.min()).toDouble() * INTERVAL_RATIO).roundToInt() // TODO: test whether roundToInt() or toInt()
        for (i in 0 until INTERVALS_COUNT) {
            val leftEdge = dataList.min() + (intervalLength * i)
            val rightEdge = dataList.min() + (intervalLength * (i + 1))
            val frequencySum = dataList.count { integer -> integer in leftEdge until rightEdge }

            val frequenciesByIntervalValue = mutableMapOf<Int, Int>()

            dataList.filter { integer -> integer in leftEdge until rightEdge }.forEach { integer ->
                when {
                    frequenciesByIntervalValue.containsKey(integer) -> frequenciesByIntervalValue[integer] =
                        frequenciesByIntervalValue[integer]!! + 1

                    else -> frequenciesByIntervalValue[integer] = 1
                }
            }

            val intervalElements = frequenciesByIntervalValue.map { (value, frequency) ->
                IntervalElement(value, frequency)
            }

            frequencyMap[i] = Interval(leftEdge, rightEdge, frequencySum, intervalElements)
        }

        return frequencyMap
    }

    override fun getValuesByNormalDistribution(edgesArray: Array<Interval>): List<Int> {
        require(edgesArray.isNotEmpty()) { "No data to aggregate frequency!" }

        val mostFrequent = edgesArray.maxBy { edgeSample -> edgeSample.frequencySum }
        val neighbours = Pair(
            edgesArray[edgesArray.indexOf(mostFrequent) - 1],
            edgesArray[edgesArray.indexOf(mostFrequent) + 1]
        ).toList().sortedBy { edgeSample -> edgeSample.frequencySum }

        return listOf(mostFrequent.average(), neighbours[0].average(), neighbours[1].average())
    }
}

data class Coordinate(val x: Int, val y: Int)

data class CellParameters(val width: Int, val height: Int)

data class BoardParameters(val numberOfRows: Int, val numberOfColumns: Int)

data class Interval(
    val leftEdge: Int,
    val rightEdge: Int,
    val frequencySum: Int,
    val intervalElements: List<IntervalElement>
) {
    constructor() : this(0, 0, 0, listOf(IntervalElement()))

    fun average(): Int {
        val sum = intervalElements.sumOf { value -> value.value * value.frequency }

        return (sum.toDouble() / this.frequencySum).roundToInt()
    }
}

data class IntervalElement(val value: Int, val frequency: Int) {
    constructor() : this(0, 0)
}
