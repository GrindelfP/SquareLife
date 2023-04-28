package tech.onesingularitybeyond.squarelife.powers

import tech.onesingularitybeyond.squarelife.entities.Population

/**
 * This class is responsible for moving entities. It contains properties:
 * - population: the population to move
 * - updater: the board updater to update the board after moving
 */
class Mover(private val population: Population, private val updater: Updater) {

    /**
     * Function for moving entities. It moves all alive entities and updates the board.
     */
    fun move(evolutionCycleNumber: Int) {
        population.aliveEntities().forEach { entity ->
            entity.move()
        }
        updater.updateBoard(evolutionCycleNumber)
    }
}
