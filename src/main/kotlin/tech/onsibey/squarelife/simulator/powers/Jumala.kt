package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.simulator.entities.Board
import tech.onsibey.squarelife.simulator.entities.Population

sealed interface Jumala {

    val witness: Witness
    val mover: Mover
    val updater: Updater
    val procreator: Procreator
    val death: Death
    val board: Board
    val population: Population

    /**
     * Function for starting the evolution process.
     */
    fun startEvolution(evolutionCycleNumber: Int) {
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
