package tech.onsibey.squarelife.powers

import tech.onsibey.squarelife.entities.Population
import tech.onsibey.squarelife.entities.Entity
import tech.onsibey.squarelife.entities.Kuvahaku
import tech.onsibey.squarelife.entities.Kuvat
import tech.onsibey.squarelife.entities.Uutiset

/**
 * This class is responsible for the death of entities. It contains properties:
 * - population: the population of entities
 * - updater: the board updater.
 *
 * Also, the companion object contains some constants:
 * - UUTISET_TERMINAL_NUMBER_OF_OCCUPIED_TILES: the number of occupied tiles for the Uutiset entity
 * - KUVAT_TERMINAL_NUMBER_OF_OCCUPIED_TILES: the number of occupied tiles for the Kuvat entity
 */
class Death(private val population: Population, private val updater: Updater) {

    /**
     * Companion object contains constants for the number of occupied tiles for the Uutiset and Kuvat entities.
     */
    companion object {
        private const val UUTISET_TERMINAL_NUMBER_OF_OCCUPIED_TILES = 5
        private const val KUVAT_TERMINAL_NUMBER_OF_OCCUPIED_TILES = 2
    }

    /**
     * Function for the death of entities by swallowing by another entities.
     * Encapsulates the private processSwallowing function.
     */
    fun processSwallowing(evolutionCycleNumber: Int) {
        val populationSizeProbe = population.size()
        population.aliveEntities().forEach { entity ->
            processSwallowing(entity)
        }
        if (population.size() != populationSizeProbe) {
            updater.updateBoard(evolutionCycleNumber, "someone didn't survive!")
            println(population)
        }
    }

    /**
     * Private function for the death of entities by swallowing by another entities.
     */
    private fun processSwallowing(entity: Entity) {
        // Guardian: we do not check swallowing if the entity is not alive (this value can be changed after we built the list of alive entities)
        if (!entity.alive) return

        val overlappingAliens = population.aliveEntities().filter { entity overlapsWithAlien it }
        val commonCoordinates = entity.commonCoordinates(overlappingAliens)

        when (entity) {
            is Uutiset -> when {
                commonCoordinates.size >= UUTISET_TERMINAL_NUMBER_OF_OCCUPIED_TILES -> entity.alive = false
                else -> overlappingAliens.forEach { overlappingEntity ->
                    when {
                        overlappingEntity is Kuvahaku -> overlappingEntity.alive = false
                        // Uutiset overlaps with Kuvat, and they occupy 2 or more common coordinates
                        overlappingEntity is Kuvat && entity.commonCoordinates(overlappingEntity).size >= 2 ->
                            overlappingEntity.alive = false
                    }
                }
            }
            is Kuvat -> when {
                commonCoordinates.size >= KUVAT_TERMINAL_NUMBER_OF_OCCUPIED_TILES -> entity.alive = false
                // if kuvat survived then kuvahakus (in fact the one overlapping kuvahaku) are swallowed.
                // But we should filter out Uutiset (if it survived)
                else -> overlappingAliens.filter { it !is Uutiset }.forEach { it.alive = false }
            }
            is Kuvahaku -> {
            /*NOP: if Kuvahaku is still alive then there is nothing to check here: Kuvahaku is a real survivor!*/
            }
        }
    }

    /**
     * Extension function for the Entity class.
     * Returns the common coordinates between the current entity and the given list of entities.
     */
    private fun Entity.commonCoordinates(others: List<Entity>) =
        position().coordinates.intersect(others.flatMap { it.position().coordinates }.toSet())

    /**
     * Extension infix function for the Entity class.
     * Returns true if the current entity overlaps with the given entity.
     */
    private infix fun Entity.overlapsWithAlien(other: Entity) =
        this::class != other::class && commonCoordinates(other).isNotEmpty()
}
