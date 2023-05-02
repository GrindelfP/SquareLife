package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.simulator.entities.Board
import tech.onsibey.squarelife.simulator.entities.Population

sealed class Jumala(
    board: Board,
    populationInitializator: (Board) -> Population,
    private val evolutionCycleNumber: Int
) {

    private val population: Population = populationInitializator(board)
    val witness: Witness = Witness()
    private val updater: Updater = Updater(board, population)
    private val procreator: Procreator = Procreator(board, population, updater)
    private val death: Death = Death(population, updater)
    private val mover: Mover = Mover(population, updater)

    /**
     * Function for starting the evolution process.
     */
    fun startEvolution() {
        updater.updateBoard(-1)
        repeat(evolutionCycleNumber) {
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
