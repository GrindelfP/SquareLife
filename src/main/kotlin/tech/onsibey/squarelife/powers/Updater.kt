package tech.onsibey.squarelife.powers

import tech.onsibey.squarelife.space.Board
import tech.onsibey.squarelife.space.ConsoleBoardVisualizer
import tech.onsibey.squarelife.entities.Population
import tech.onsibey.squarelife.space.Visualizer

/**
 * Class for updating the board with the occurred changes in population. Contains
 * - visualizer - visualizer for the board property, also the following parameters:
 * @param board board to be updated
 * @param population updated population
 */
class Updater(private val board: Board, private val population: Population) {

    private val visualizer: Visualizer = ConsoleBoardVisualizer(board)

    /**
     * Function for updating the board.
     * Calls the update method of the board with the alive entities positions.
     * Then visualizes the updated board.
     */
    fun updateBoard(evolutionCycleNumber: Int, extraMessage: String? = null) {
        board.update(population.aliveEntitiesPositions())
        visualizer.visualize(evolutionCycleNumber, extraMessage)
    }
}
