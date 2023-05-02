package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.simulator.entities.Board
import tech.onsibey.squarelife.detector.datainterpreter.Mailman
import tech.onsibey.squarelife.simulator.entities.Population
import tech.onsibey.squarelife.simulator.entities.Population.Companion.toPopulation

/**
 * This class is named after the greatest divine in the Finno-Ugrian pantheon.
 * This class will run the simulation based on provided
 * @param mail Mailman instance containing the board and the list of entities
 * @param evolutionCycleLimit the integer limiting the number of cycles (90 by default)
 * Is the game processor and controls everything.
 * Contains the following properties:
 * - updater: the game updater
 * - procreator: the procreation of the entities
 * - death: the death of the entities
 * - mover: the movement of the entities.
 */
class Ukko(mail: Mailman, private val evolutionCycleLimit: Int) : Jumala {

    override val board = Board(mail.boardSize)
    override val population: Population = mail.entities.toPopulation(board)
    override val witness: Witness = Witness()
    override val updater: Updater = Updater(board, population)
    override val procreator: Procreator = Procreator(board, population, updater)
    override val death: Death = Death(population, updater)
    override val mover: Mover = Mover(population, updater)

    init {
        startEvolution(evolutionCycleLimit)
    }
}
