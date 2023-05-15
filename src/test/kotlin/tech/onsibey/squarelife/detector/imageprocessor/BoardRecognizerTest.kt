package tech.onsibey.squarelife.detector.imageprocessor

import ij.ImagePlus
import org.assertj.core.api.Assertions.assertThat
import tech.onsibey.squarelife.detector.imageprocessor.BoardRecognizer.getBoardParameters
import tech.onsibey.squarelife.detector.imageprocessor.BoardRecognizer.getCellParameters
import kotlin.test.Test

class BoardRecognizerTest {
    @Test
    fun `GIVEN empty photo 200x200 WHEN applying getCellParameters() THEN returns cell size 200x200`() {
        val imageWithWrapper = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/с0.jpeg")
        val image = imageWithWrapper.processor
        val cellParameters = getCellParameters(image)

        assertThat(cellParameters.height).isEqualTo(200)
        assertThat(cellParameters.width).isEqualTo(200)
    }

    @Test
    fun `GIVEN photo with 12 cells 200x200 WHEN applying getCellParameters() THEN returns cell size is between 67x50 and 50x40`() {
        val imageWithWrapper = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/с12_2.jpeg")
        val image = imageWithWrapper.processor
        val cellParameters = getCellParameters(image)

        println(cellParameters)

        assertThat(cellParameters.width).isBetween(50, 67)
        assertThat(cellParameters.height).isBetween(40, 50)
    }

    @Test
    fun `GIVEN photo with 4 cells 200x200 WHEN applying getCellParameters() THEN returns cell size is between 100x100 and 50x50`() {
        val imageWithWrapper = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/с4.jpeg")
        val image = imageWithWrapper.processor
        val cellParameters = getCellParameters(image)

        println(cellParameters)

        assertThat(cellParameters.width).isBetween(50, 100)
        assertThat(cellParameters.height).isBetween(50, 100)
    }

    @Test
    fun `GIVEN image of board with 100 cells WHEN applying getCellParameters() THEN returns cell size is between 65x75 and 72x83`() {
        val imageWithWrapper = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/photos/t1e.jpeg")
        val image = imageWithWrapper.processor
        val cellParameters = getCellParameters(image)

        println(cellParameters)

        assertThat(cellParameters.width).isBetween(65, 72)
        assertThat(cellParameters.height).isBetween(75, 83)
    }

    @Test
    fun `GIVEN cell size 70x80 WHEN applying getBoardParameters() THEN 10x10 board parameters returned`() {
        val cellParameters = CellParameters(70, 80)
        val imageProcessor = ImagePlus("/Users/grindelf/Programming/Onsibey/SquareLife/cropped-preprocessed.jpg").processor
        val boardParameters = getBoardParameters(imageProcessor, cellParameters)

        assertThat(boardParameters.numberOfRows).isEqualTo(10)
        assertThat(boardParameters.numberOfColumns).isEqualTo(10)
    }
}