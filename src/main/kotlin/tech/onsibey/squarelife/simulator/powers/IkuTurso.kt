package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.simulator.entities.Population
import tech.onsibey.squarelife.simulator.entities.Population.Companion.generatePopulation
import tech.onsibey.squarelife.common.Board
import tech.onsibey.squarelife.common.BoardSize

/**
 * This class is named after son of the greatest divine in the Finno-Ugrian pantheon.
 * This class will run the simulation on its own - not receiving outer information.
 * Is the game processor and controls everything.
 * Contains the following properties:
 * - board: the game board
 * - population: the game population
 * - updater: the game updater
 * - procreator: the procreation of the entities
 * - death: the death of the entities
 * - mover: the movement of the entities.
 *
 * Also, it contains following constants:
 * - BOARD_HORIZONTAL_SIDE_SIZE: the size of the board in the horizontal direction
 * - BOARD_VERTICAL_SIDE_SIZE: the size of the board in the vertical direction
 * - NUMBER_OF_KUVAHAKU_IN_POPULATION: the number of Kuvahaku entities in the population
 * - NUMBER_OF_KUVAT_IN_POPULATION: the number of Kuvat entities in the population
 * - EVOLUTION_CYCLES_NUMBER: the top limit of evolution cycles
 */
object IkuTurso {

    private val board: Board
    private val population: Population
    private val updater: Updater
    private val procreator: Procreator
    private val death: Death
    private val mover: Mover

    private const val STANDARD_BOARD_HORIZONTAL_SIDE_SIZE = 40
    private const val STANDARD_BOARD_VERTICAL_SIDE_SIZE = 40
    private const val STANDARD_NUMBER_OF_KUVAHAKU_IN_POPULATION = 25
    private const val STANDARD_NUMBER_OF_KUVAT_IN_POPULATION = 20
    private const val EVOLUTION_CYCLES_NUMBER = 90

    /**
     * Initializer of the game process.
     */
    init {
        board = Board(BoardSize(STANDARD_BOARD_VERTICAL_SIDE_SIZE, STANDARD_BOARD_HORIZONTAL_SIDE_SIZE))
        population =
            generatePopulation(STANDARD_NUMBER_OF_KUVAHAKU_IN_POPULATION, STANDARD_NUMBER_OF_KUVAT_IN_POPULATION, board)
        updater = Updater(board, population)
        procreator = Procreator(board, population, updater)
        death = Death(population, updater)
        mover = Mover(population, updater)
        updater.updateBoard(-1)
        startEvolution()
    }

    /**
     * Function for starting the evolution process.
     */
    private fun startEvolution() {
        repeat(EVOLUTION_CYCLES_NUMBER) {
            evolutionCycle(it)
        }
    }

    /**
     * Function for evolution cycle.
     */
    private fun evolutionCycle(evolutionCycleNumber: Int) {
        // 1. command entities to move
        mover.move(evolutionCycleNumber)

        // 2. check results of the movement
        // 2.1 swallowing
        death.processSwallowing(evolutionCycleNumber)

        // 2.2 procreation
        procreator.processProcreation(evolutionCycleNumber)
    }
}
