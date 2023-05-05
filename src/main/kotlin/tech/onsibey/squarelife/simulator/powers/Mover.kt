package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.simulator.entities.Board
import tech.onsibey.squarelife.simulator.entities.Population

/**
 * This class is responsible for moving entities. It contains properties:
 * - population: the population to move
 * - updater: the board updater to update the board after moving
 */
class Mover(private val population: Population, private val board: Board) {

    /**
     * Function for moving entities. It moves all alive entities and updates the board.
     */
    fun move() {
        population.aliveEntities().forEach { entity ->
            entity.move()
        }
        board.update(population.aliveEntitiesPositions())
    }
}
