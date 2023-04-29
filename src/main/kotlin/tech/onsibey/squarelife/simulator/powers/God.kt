package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.detector.datainterpreter.Detector
import tech.onsibey.squarelife.simulator.entities.Population
import tech.onsibey.squarelife.simulator.entities.Population.Companion.generatePopulation
import tech.onsibey.squarelife.simulator.world.Board
import tech.onsibey.squarelife.simulator.world.BoardSize
import java.util.*

/**
 * The main object of the game. Is the game processor and controls everything.
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
object God {

    private val board: Board
    private val population: Population
    private val updater: Updater
    private val procreator: Procreator
    private val death: Death
    private val mover: Mover
    private val evolutionCyclesLimit: Int

    private const val STANDARD_BOARD_HORIZONTAL_SIDE_SIZE = 40
    private const val STANDARD_BOARD_VERTICAL_SIDE_SIZE = 40
    private const val STANDARD_NUMBER_OF_KUVAHAKU_IN_POPULATION = 25
    private const val STANDARD_NUMBER_OF_KUVAT_IN_POPULATION = 20
    private const val EVOLUTION_CYCLES_NUMBER = 90
    private const val PHOTO_PATH = "src/main/resources/"

    /**
     * Initializer of the game process.
     */
    init {
        if (initializationFromPhoto()) {
            evolutionCyclesLimit = getMaximumCyclesCount()
            println("Make sure that the photo is named with integer and placed in the following directory $PHOTO_PATH, " +
                    "and if there is many variants the desired one is named as the greatest number")
            val entitiesData = Detector.getSimulationInformation()
            board = Board(entitiesData.boardSize)
            population = generatePopulation(entitiesData.entities, board)
            updater = Updater(board, population)
            procreator = Procreator(board, population, updater)
            death = Death(population, updater)
            mover = Mover(population, updater)
            updater.updateBoard(-1)
            startEvolution()
        }
        else {
            evolutionCyclesLimit = EVOLUTION_CYCLES_NUMBER
            board = Board(BoardSize(STANDARD_BOARD_VERTICAL_SIDE_SIZE, STANDARD_BOARD_HORIZONTAL_SIDE_SIZE))
            population = generatePopulation(STANDARD_NUMBER_OF_KUVAHAKU_IN_POPULATION, STANDARD_NUMBER_OF_KUVAT_IN_POPULATION, board)
            updater = Updater(board, population)
            procreator = Procreator(board, population, updater)
            death = Death(population, updater)
            mover = Mover(population, updater)
            updater.updateBoard(-1)
            startEvolution()
        }
    }

    private fun getMaximumCyclesCount(): Int {
        TODO()
    }

    private fun initializationFromPhoto(): Boolean {
        println("Would you like to start the evolution from provided photo? Y/n")
        while (true) {
            val answer = readln()
            when {
                answer.uppercase(Locale.getDefault()) == "Y" -> return true
                answer.uppercase(Locale.getDefault()) == "N" -> return false
                else -> println("Enter Y or N!")
            }
        }
    }

    /**
     * Function for starting the evolution process.
     */
    private fun startEvolution() {
        repeat(evolutionCyclesLimit) {
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
