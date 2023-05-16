package tech.onsibey.squarelife.detector.imageprocessor

import ij.ImagePlus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.onsibey.squarelife.detector.imageprocessor.AverageCounter.getAverageValueByNormalDistribution
import tech.onsibey.squarelife.detector.imageprocessor.AverageCounter.getValueFrequency

class AverageCounterTest {
    @Test
    fun `GIVEN widths frequency maps for t1jpeg WHEN getAverageValueByNormalDistribution applied THEN average between 65 and 73 is returned`() {
        val imageProcessor = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/t1.jpeg").processor
        val listOfWidths = WhiteLinesAnalyticalCapture.getHorizontalWhiteLines(imageProcessor)
        val frequencyMapWidths = getValueFrequency(listOfWidths)
        val averageWidth = getAverageValueByNormalDistribution(frequencyMapWidths, imageProcessor.width)

        assertThat(averageWidth).isBetween(65, 73)
    }

    @Test
    fun `GIVEN heights frequency maps for t1jpeg WHEN getAverageValueByNormalDistribution applied THEN average between 71 and 83 is returned`() {
        val imageProcessor = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/t1.jpeg").processor
        val listOfHeights = WhiteLinesAnalyticalCapture.getVerticalWhiteLines(imageProcessor)
        val frequencyMapHeights = getValueFrequency(listOfHeights)
        val averageHeight = getAverageValueByNormalDistribution(frequencyMapHeights, imageProcessor.height)

        assertThat(averageHeight).isBetween(71, 83)
    }

    @Test
    fun `GIVEN widths frequency maps for t1ejpeg WHEN getAverageValueByNormalDistribution applied THEN average between 94 and 105 is returned`() {
        val imageProcessor = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/t1e.jpeg").processor
        val listOfWidths = WhiteLinesAnalyticalCapture.getHorizontalWhiteLines(imageProcessor)
        val frequencyMapWidths = getValueFrequency(listOfWidths)
        val averageWidth = getAverageValueByNormalDistribution(frequencyMapWidths, imageProcessor.width)

        assertThat(averageWidth).isBetween(94, 105)
    }

    @Test
    fun `GIVEN heights frequency maps for t1ejpeg WHEN getAverageValueByNormalDistribution applied THEN average between 78 and 87 is returned`() {
        val imageProcessor = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/t1e.jpeg").processor
        val listOfHeights = WhiteLinesAnalyticalCapture.getVerticalWhiteLines(imageProcessor)
        val frequencyMapHeights = getValueFrequency(listOfHeights)
        val averageHeight = getAverageValueByNormalDistribution(frequencyMapHeights, imageProcessor.height)

        assertThat(averageHeight).isBetween(78, 87)
    }

    @Test
    fun `GIVEN widths frequency maps for t2jpeg WHEN getAverageValueByNormalDistribution applied THEN average between 352 and 393 is returned`() {
        val imageProcessor = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/t3.jpeg").processor
        val listOfWidths = WhiteLinesAnalyticalCapture.getHorizontalWhiteLines(imageProcessor)
        val frequencyMapWidths = getValueFrequency(listOfWidths)
        val averageWidth = getAverageValueByNormalDistribution(frequencyMapWidths, imageProcessor.width)

        assertThat(averageWidth).isBetween(352, 393)
    }

    @Test
    fun `GIVEN frequency maps for t2jpeg WHEN getAverageValueByNormalDistribution applied THEN average between 343 and 383 is returned`() {
        val imageProcessor = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/t3.jpeg").processor
        val listOfHeights = WhiteLinesAnalyticalCapture.getVerticalWhiteLines(imageProcessor)
        val frequencyMapHeights = getValueFrequency(listOfHeights)
        val averageHeight = getAverageValueByNormalDistribution(frequencyMapHeights, imageProcessor.height)

        assertThat(averageHeight).isBetween(343, 383)
    }
}
