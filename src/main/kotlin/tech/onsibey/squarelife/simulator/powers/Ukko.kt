package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.detector.datainterpreter.Mailman
import tech.onsibey.squarelife.simulator.entities.Population

/**
 * This class is named after the greatest divine in the Finno-Ugrian pantheon.
 * This class will run the simulation based on provided
 * @param mail Mailman instance containing the board and the list of entities
 * @param evolutionCycleLimit the integer limiting the number of cycles (90 by default)
 * Is the game processor and controls everything.
 * Contains the following properties:
 * - population: the game population based on provided in mail information
 * - updater: the game updater
 * - procreator: the procreation of the entities
 * - death: the death of the entities
 * - mover: the movement of the entities.
 */
class Ukko(mail: Mailman, private val evolutionCycleLimit: Int = 90) {
    private val population: Population = Population.generatePopulation(mail.entities, mail.board)
    private val updater: Updater = Updater(mail.board, population)
    private val procreator: Procreator = Procreator(mail.board, population, updater)
    private val death: Death = Death(population, updater)
    private val mover: Mover = Mover(population, updater)

    init {
        updater.updateBoard(-1)
        startEvolution()
    }

    /**
     * Function for starting the evolution process.
     */
    private fun startEvolution() {
        repeat(evolutionCycleLimit) {
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
