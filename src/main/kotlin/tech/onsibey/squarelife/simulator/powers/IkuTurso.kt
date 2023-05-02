package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.simulator.entities.Population.Companion.generatePopulation
import tech.onsibey.squarelife.simulator.entities.Board
import tech.onsibey.squarelife.simulator.entities.BoardSize
import tech.onsibey.squarelife.common.DEFAULT_EVOLUTION_CYCLES_LIMIT
import tech.onsibey.squarelife.simulator.powers.IkuTursoConstants.STANDARD_BOARD_HORIZONTAL_SIDE_SIZE
import tech.onsibey.squarelife.simulator.powers.IkuTursoConstants.STANDARD_BOARD_VERTICAL_SIDE_SIZE
import tech.onsibey.squarelife.simulator.powers.IkuTursoConstants.STANDARD_NUMBER_OF_KUVAHAKU_IN_POPULATION
import tech.onsibey.squarelife.simulator.powers.IkuTursoConstants.STANDARD_NUMBER_OF_KUVAT_IN_POPULATION

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
object IkuTurso : Jumala(
    board = Board(BoardSize(STANDARD_BOARD_VERTICAL_SIDE_SIZE, STANDARD_BOARD_HORIZONTAL_SIDE_SIZE)),
    populationInitializer = { board ->
        generatePopulation(STANDARD_NUMBER_OF_KUVAHAKU_IN_POPULATION, STANDARD_NUMBER_OF_KUVAT_IN_POPULATION, board)
    },
    evolutionCycleNumber = DEFAULT_EVOLUTION_CYCLES_LIMIT
) {

    /**
     * Initializer of the game process.
     */
    init {
        startEvolution()
    }
}

private object IkuTursoConstants {
    const val STANDARD_BOARD_HORIZONTAL_SIDE_SIZE = 40
    const val STANDARD_BOARD_VERTICAL_SIDE_SIZE = 40
    const val STANDARD_NUMBER_OF_KUVAHAKU_IN_POPULATION = 25
    const val STANDARD_NUMBER_OF_KUVAT_IN_POPULATION = 20
}


