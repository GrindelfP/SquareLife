package tech.onsibey.squarelife.visualisation

import tech.onsibey.squarelife.simulator.entities.*
import tech.onsibey.squarelife.simulator.powers.EvolutionResultReport
import tech.onsibey.squarelife.simulator.powers.PopulationSnapshot
import tech.onsibey.squarelife.visualisation.utils.GifSequenceWriter
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream
import javax.imageio.stream.ImageOutputStream


/**
 * An interface for visualizing the board.
 * Contains one method for visualization.
 */
interface Visualizer {
    /**
     * Visualizes the board.
     * @param evolutionCycleNumber the number of the current evolution cycle.
     * @param extraDescription an extra description of the visualization, if needed
     * (e.g. if some entity died or was born during the evolution cycle).
     */
    fun visualize(evolutionCycleNumber: Int, extraDescription: List<String>? = null)
}

/**
 * Abstract class for all kinds of visualisation of the board. Inherits from Visualizer interface.
 */
abstract class BoardVisualizer(private val board: Board) : Visualizer

/**
 * Visualizes the board in console.
 */
class ConsoleBoardVisualizer(private val board: Board) : BoardVisualizer(board) {

    /**
     * Overrides the visualize method from Visualizer interface.
     * It prints the message and the board.
     */
    override fun visualize(evolutionCycleNumber: Int, extraDescription: List<String>?) {
        // general message is a string indicating the current evolution cycle (-1 - not started yet,
        // 0 - the first evolution cycle etc.)
        val generalMessage = when (evolutionCycleNumber) {
            -1 -> "Evolution haven't started yet!"
            else -> "Evolution cycle #${evolutionCycleNumber + 1}"
        }
        // message is the whole message to be printed,  including the general message, the board and the
        // extra description, if needed (it required if some entity died or was born during the evolution cycle)
        val message =
            "${generalMessage}${if (extraDescription != null) ", \n${extraDescription.joinToString("\n")}" else ""}"

        println(message)
        print(board.toString())
    }
}

class NoopBoardVisualizer(board: Board) : BoardVisualizer(board) {
    override fun visualize(evolutionCycleNumber: Int, extraDescription: List<String>?) {
        // do nothing
    }
}

class GifEvolutionCycleVisualizer(private val evolutionResultReport: EvolutionResultReport) {

    companion object {
        private const val TILE_SIZE = 10
        private const val TILE_FILL_SIZE = 8
        private const val GIF_FILE_NAME_BASE = "evolution-simulation"
        private const val GIF_FILE_NAME_SUFFIX = ".gif"
        private const val GIF_FRAME_DELAY = 100

        private val GREY = Color(220, 220, 220)
        private val BLUE = Color(0, 191, 255)
        private val GREEN = Color(46, 139, 87)
        private val RED = Color(255, 69, 0)

        private val TEMP_FILE_DIR = File("temp").run {
            when {
                !this.exists() -> mkdir()
                else -> {
                    deleteRecursively()
                    mkdir()
                }
            }
            this
        }
    }

    private val height = evolutionResultReport.boardSize.numberOfRows * TILE_SIZE
    private val width = evolutionResultReport.boardSize.rowLength * TILE_SIZE

    fun visualize() {
        evolutionResultReport.evolutionCycles.forEach { evolutionCycle ->
            // there is extensive information about the evolution cycle in the report
            // (evolutionCycle, population size etc.) but we use only the snapshots for now.
            // This info can be added as a footer to the gif.
            if (evolutionCycle.number == 0) createImg(evolutionCycle.populationSnapshots.initial)
            createImg(evolutionCycle.populationSnapshots.afterMovement)
            createImg(evolutionCycle.populationSnapshots.afterSwallowing)
            createImg(evolutionCycle.populationSnapshots.afterProcreation)
        }

        generateGif()
    }

    private fun createImg(populationSnapshot: PopulationSnapshot) {
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.createGraphics()
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, width, height)

        graphics.drawEntities(populationSnapshot.aliveEntities)

        // To make sorting of files easier, we use ISO_INSTANT format for file names. Alternatively, we could use
        // the cycle number and the type of the snapshot, but then we have to find a way to sort them correctly.
        val file = File("${TEMP_FILE_DIR}/${timestamp()}.png")
        ImageIO.write(bufferedImage, "png", file)
    }

    private fun Graphics2D.drawEntities(entities: List<EntityPosition>) {
        (0 until evolutionResultReport.boardSize.numberOfRows).forEach { y ->
            (0 until evolutionResultReport.boardSize.rowLength).forEach { x ->
                // we are using a hack here: entities are drawn with a shift of 1 pixel to the right and to the bottom
                when (val entity = entities.find { it.position.contains(Coordinate(x + 1, y + 1)) }) {
                    null -> {
                        color = GREY
                        fillRect(x * TILE_SIZE + 1, y * TILE_SIZE + 1, TILE_FILL_SIZE, TILE_FILL_SIZE)
                    }

                    else -> {
                        color = when (entity.type) {
                            Kuvat::class -> GREEN
                            Kuvahaku::class -> BLUE
                            Uutiset::class -> RED
                            else -> TODO("Handle unexpected entity type. Even if we know that it is not possible.")
                        }
                        fillRect(x * TILE_SIZE + 1, y * TILE_SIZE + 1, TILE_FILL_SIZE, TILE_FILL_SIZE)
                    }
                }
            }
        }
    }

    private fun generateGif() {
        val files = TEMP_FILE_DIR.walk().filter {
            val fileExtension = it.name.substring(it.name.lastIndexOf(".") + 1)
            fileExtension.equals("jpg", ignoreCase = true) || fileExtension.equals("png", ignoreCase = true)
        }.sortedBy { it.name }
        val firstImage = files.first()

        val first = ImageIO.read(firstImage)
        val output: ImageOutputStream = FileImageOutputStream(
            File("$GIF_FILE_NAME_BASE-${timestamp()}$GIF_FILE_NAME_SUFFIX")
        )

        val writer = GifSequenceWriter(output, first.type, GIF_FRAME_DELAY, false)
        writer.writeToSequence(first)

        files.forEachIndexed { index, file ->
            if (index == 0) return@forEachIndexed
            val next = ImageIO.read(file)
            writer.writeToSequence(next)
        }

        writer.close()
        output.close()

        TEMP_FILE_DIR.deleteRecursively()
    }

    private fun timestamp() = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
}
