package tech.onsibey.squarelife.visualisation

import tech.onsibey.squarelife.simulator.entities.Board
import tech.onsibey.squarelife.simulator.entities.EntityPosition
import tech.onsibey.squarelife.simulator.powers.EvolutionCycle
import tech.onsibey.squarelife.simulator.powers.EvolutionResultReport
import tech.onsibey.squarelife.simulator.powers.PopulationSnapshot

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
    fun visualize(evolutionCycleNumber: Int, extraDescription: String? = null)
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
    override fun visualize(evolutionCycleNumber: Int, extraDescription: String?) {
        // general message is a string indicating the current evolution cycle (-1 - not started yet,
        // 0 - the first evolution cycle etc.)
        val generalMessage = when (evolutionCycleNumber) {
            -1 -> "Evolution haven't started yet!"
            else -> "Evolution cycle #${evolutionCycleNumber + 1}"
        }
        // message is the whole message to be printed,  including the general message, the board and the
        // extra description, if needed (it required if some entity died or was born during the evolution cycle)
        val message = "${generalMessage}${if (extraDescription != null) ", $extraDescription" else ""}"

        println(message)
        print(board.toString())
    }
}

class ConsoleEvolutionCycleVisualizer(private val evolutionResultReport: EvolutionResultReport) {

    fun visualize() {
       evolutionResultReport.evolutionCycles.forEach { evolutionCycle -> visualizeEvolutionCycle(evolutionCycle) }
    }

    private fun visualizeEvolutionCycle(cycle: EvolutionCycle) {
        if (cycle.number == 0) {
            println("Evolution hasn't started yet!")
            printBoard(cycle.populationSnapshots.initial)
        }

        println("Evolution cycle #${cycle.number + 1}")
        println("After movement:")
        printBoard(cycle.populationSnapshots.afterMovement)

        println("After swallowing:")
        printBoard(cycle.populationSnapshots.afterSwallowing)
        println("Following entities were swallowed: ${cycle.swallowed}")


        println("After procreation:")
        printBoard(cycle.populationSnapshots.afterProcreation)
        println("Following entities were born: ${cycle.born}")

    }

    private fun printBoard(populationSnapshot: PopulationSnapshot) {
        val board = Board(evolutionResultReport.boardSize)
        board.update(populationSnapshot.aliveEntities.map { EntityPosition(it.position, it.type) })
        print(board.toString())
    }
}
