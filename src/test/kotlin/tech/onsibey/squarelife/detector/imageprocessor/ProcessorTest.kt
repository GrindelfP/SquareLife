package tech.onsibey.squarelife.detector.imageprocessor

import ij.ImagePlus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

internal class ProcessorTest {

    @Test
    fun `GIVEN WHEN THEN`() {
        val pathToPhoto = "/Users/grindelf/Programming/Onsibey/SquareLife/photos/t1.jpeg"
        val imageBoard = Processor(pathToPhoto).processImageBoard()

        println(imageBoard)

        // Just writing the image board to a file for a visual validation
        val stringBuilder = StringBuilder()
        imageBoard.cells.forEach { row ->
            row.forEach { cell ->
                stringBuilder.append(if (cell.isPainted) "░" else "█")
            }
            stringBuilder.append("\n")
        }

        File("testos.txt").writeText(stringBuilder.toString())
    }


    @Test
    fun `GIVEN mostly black images WHEN dominant colours are estimated THEN black colours is returned`() {
        val paths = listOf(
            "/Users/grindelf/Programming/Onsibey/SquareLife/cells/3, 6.jpg",
            "/Users/grindelf/Programming/Onsibey/SquareLife/cells/3, 4.jpg",
            "/Users/grindelf/Programming/Onsibey/SquareLife/cells/3, 8.jpg",
            "/Users/grindelf/Programming/Onsibey/SquareLife/cells/4, 4.jpg"
        )
        val images = listOf(
            ImagePlus(paths[0]),
            ImagePlus(paths[1]),
            ImagePlus(paths[2]),
            ImagePlus(paths[3])
        )
        val dominantColours = images.map { image ->
            image.dominantColour()
        }

        dominantColours.forEach { colour ->
            assertThat(colour).isEqualTo(CellColor.BLACK)
        }
    }
}
