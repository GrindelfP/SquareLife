package tech.onsibey.squarelife.detector.imageprocessor

import org.junit.jupiter.api.Test
import java.io.File

internal class ProcessorTest {

    @Test
    fun `GIVEN WHEN THEN`() {
        val pathToPhoto = "REPLACE_ME_WITH_PATH_TO_PHOTO"
        val imageBoard = Processor(pathToPhoto).processImageBoard()

        // Just writing the image board to a file for a visual validation
        val stringBuilder = StringBuilder()
        imageBoard.cells.forEach { row ->
            row.forEach { cell ->
                stringBuilder.append(if (cell.isPainted) "░" else  "█")
            }
            stringBuilder.append("\n")
        }

        File("testos.txt").writeText(stringBuilder.toString())
    }
}
