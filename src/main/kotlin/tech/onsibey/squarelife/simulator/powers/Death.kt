package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.simulator.entities.*

/**
 * This class is responsible for the death of entities. It contains properties:
 * - population: the population of entities
 * - updater: the board updater.
 *
 * Also, the companion object contains some constants:
 * - UUTISET_TERMINAL_NUMBER_OF_OCCUPIED_TILES: the number of occupied tiles for the Uutiset entity
 * - KUVAT_TERMINAL_NUMBER_OF_OCCUPIED_TILES: the number of occupied tiles for the Kuvat entity
 */
class Death(private val population: Population, private val board: Board) {

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
    fun processSwallowing(): List<Entity> {
        val swallowed = mutableListOf<Entity>()
        population.aliveEntities().forEach { entity ->
            swallowed.addAll(processSwallowing(entity))
        }
        if (swallowed.isNotEmpty()) {
            /*board.updateBoard(
                evolutionCycleNumber,
                listOf("swallowed entities: ${swallowed.joinToString(", ")}}", population.toString())
            )*/
            board.update(population.aliveEntitiesPositions())
        }
        return swallowed
    }

    /**
     * Private function for the death of entities by swallowing by another entities.
     */
    private fun processSwallowing(entity: Entity): List<Entity> {
        // Guardian: we do not check swallowing if the entity is not alive (this value can be changed after we built the list of alive entities)
        if (!entity.alive) return emptyList()

        val overlappingAliens = population.aliveEntities().filter { entity overlapsWithAlien it }
        val commonCoordinates = entity.commonCoordinates(overlappingAliens)
        val swallowed = mutableListOf<Entity>()

        when (entity) {
            is Uutiset -> when {
                commonCoordinates.size >= UUTISET_TERMINAL_NUMBER_OF_OCCUPIED_TILES -> {
                    entity.alive = false
                    swallowed.add(entity)
                }
                else -> overlappingAliens.forEach { overlappingEntity ->
                    when {
                        overlappingEntity is Kuvahaku -> {
                            overlappingEntity.alive = false
                            swallowed.add(overlappingEntity)
                        }
                        // Uutiset overlaps with Kuvat, and they occupy 2 or more common coordinates
                        overlappingEntity is Kuvat && entity.commonCoordinates(overlappingEntity).size >= 2 -> {
                            overlappingEntity.alive = false
                            swallowed.add(overlappingEntity)
                        }
                    }
                }
            }
            is Kuvat -> when {
                commonCoordinates.size >= KUVAT_TERMINAL_NUMBER_OF_OCCUPIED_TILES -> {
                    entity.alive = false
                    swallowed.add(entity)
                }
                // if kuvat survived then kuvahakus (in fact the one overlapping kuvahaku) are swallowed.
                // But we should filter out Uutiset (if it survived)
                else -> overlappingAliens.filter { it !is Uutiset }.forEach {
                    it.alive = false
                    swallowed.add(it)
                }
            }
            is Kuvahaku -> {
            /*NOP: if Kuvahaku is still alive then there is nothing to check here: Kuvahaku is a real survivor!*/
            }
        }
        return swallowed
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
