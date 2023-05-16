package tech.onsibey.squarelife.detector.imageprocessor

import ij.ImagePlus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.onsibey.squarelife.detector.imageprocessor.AverageCounter.getAverageValueByNormalDistribution
import tech.onsibey.squarelife.detector.imageprocessor.AverageCounter.getValueFrequency

class AverageCounterTest {
    @Test
    fun `GIVEN list of numbers WHEN value frequency is calculated THEN frequency map is returned`() {
        val imageProcessor = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/t2.jpg").processor
        val listOfWidths = WhiteLinesAnalyticalCapture.getHorizontalWhiteLines(imageProcessor)

        val frequencyMap = getValueFrequency(listOfWidths)

        frequencyMap.toList().forEach {
            println(it)
        }
    }

    @Test
    fun `GIVEN frequency maps WHEN getAverageValueByNormalDistribution applied THEN average between 65 and 73 and 71 and 83 is returned`() {
        val imageProcessor = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/t2.jpg").processor
        val listOfWidths = WhiteLinesAnalyticalCapture.getHorizontalWhiteLines(imageProcessor)
        val listOfHeights = WhiteLinesAnalyticalCapture.getVerticalWhiteLines(imageProcessor)
        val frequencyMapWidths = getValueFrequency(listOfWidths)
        val frequencyMapHeights = getValueFrequency(listOfHeights)

        frequencyMapHeights.forEach { println(it) }

        val averageWidth = getAverageValueByNormalDistribution(frequencyMapWidths, imageProcessor.width)
        val averageHeight = getAverageValueByNormalDistribution(frequencyMapHeights, imageProcessor.height)
        println(averageWidth)
        println(averageHeight)

        assertThat(averageWidth).isBetween(65, 73)
        assertThat(averageHeight).isBetween(71, 83)
    }
}