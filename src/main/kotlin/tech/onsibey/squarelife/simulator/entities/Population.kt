package tech.onsibey.squarelife.simulator.entities

import tech.onsibey.squarelife.common.EntitySize
import kotlin.random.Random

/**
 * Class representing a population of entities. It contains information about all living entities.
 * It has properties:
 * - kuvahakuPopulation: list of all living Kuvahakus
 * - kuvatPopulation: list of all living Kuvats
 * - uutiset: the living Uutiset, also it contains following properties as parameters:
 * @param uutiset Uutiset entity
 * @param kuvahakus list of all Kuvahakus entity
 * @param kuvats list of all Kuvats entity
 */
data class Population(
    val uutiset: Uutiset,
    private val kuvahakus: List<Kuvahaku>,
    private val kuvats: List<Kuvat>,
) {

    private val kuvahakuPopulation: MutableList<Kuvahaku> = kuvahakus.toMutableList()
    private val kuvatPopulation: MutableList<Kuvat> = kuvats.toMutableList()

    /**
     * Constructor for Population. It initializes coordinates and populates them with entities.
     * It also requires that all entities have unique coordinates when simulation is started
     * (later they can overlap with each other).
     */
    init {
        val coordinates = mutableListOf<Coordinate>()
        coordinates.addAll(uutiset.position().coordinates)
        kuvahakus.forEach { coordinates.addAll(it.position().coordinates) }
        kuvats.forEach { coordinates.addAll(it.position().coordinates) }
        require(coordinates.size == coordinates.toSet().size) {
            "There are entities with overlapping positions."
        }
    }

    /**
     * Function returns a list of all alive entities.
     */
    fun aliveEntities() = mutableListOf<Entity>(uutiset).apply {
        addAll(kuvahakuPopulation)
        addAll(kuvatPopulation)
    }.filter { it.alive }

    /**
     * In the case of overlapping coordinated we want to show the smallest entities on the top
     * (e.g. if Kuvahaku is overlapping with Kuvat it will be shown on top of Kuvat - it means that
     * the Kuvat's tile representing the Kuvahaku on top of the Kuvat will be painted in Kuvahaku's color).
     */
    fun aliveEntitiesPositions(): List<EntityPosition> {
        val positions = mutableListOf<EntityPosition>()
        if (uutiset.alive) positions.add(EntityPosition(uutiset.position(), uutiset.color))
        kuvatPopulation.forEach { kuvat ->
            if (kuvat.alive) positions.add(EntityPosition(kuvat.position(), kuvat.color))
        }
        kuvahakuPopulation.forEach { kuvahaku ->
            if (kuvahaku.alive) positions.add(EntityPosition(kuvahaku.position(), kuvahaku.color))
        }

        return positions
    }

    /**
     * Function adds a new Kuvahaku to the population (when one is born).
     */
    fun addKuvahaku(kuvahaku: Kuvahaku) = kuvahakuPopulation.add(kuvahaku)

    /**
     * Function adds a new Kuvat to the population (when one is born).
     */
    fun addKuvat(kuvat: Kuvat) = kuvatPopulation.add(kuvat)

    /**
     * Function returns size of the population as integer (with no respect to entities' kinds).
     */
    fun size(): Int = aliveEntities().size

    /**
     * Function overrides toString method.
     * It returns a string with population size, and number of entities by their kind.
     */
    override fun toString(): String = buildString {
        append("Population size ${size()}, ")
        if (uutiset.alive) append("Uutiset is alive, ")
        append("number of Kuvahakus is ${kuvahakuPopulation.filter { it.alive }.size}, number of Kuvats is ${kuvatPopulation.filter { it.alive }.size}.")
    }

    /**
     * Companion object containing static functions for generating population and initializing entities.
     */
    companion object {

        fun List<EntityInfo>.toPopulation(board: Board): Population {
            var uutiset: Uutiset? = null
            val kuvahakus = mutableListOf<Kuvahaku>()
            val kuvats = mutableListOf<Kuvat>()
            this.forEach { entity ->
                when (entity.entityType) {
                    Uutiset::class -> uutiset = Uutiset(Position(entity.coordinates, board))
                    Kuvahaku::class -> kuvahakus.add(Kuvahaku(Position(entity.coordinates, board)))
                    Kuvat::class -> kuvats.add(Kuvat(Position(entity.coordinates, board)))
                }
            }

            val validatedUutiset = requireNotNull(uutiset) { "Uutiset is not found" }

            return Population(validatedUutiset, kuvahakus, kuvats)
        }


        /**
         * Function generates a population of entities.
         * It requires that the board is suitable for the population by height and width.
         * @param numberOfKuvahaku number of Kuvahakus in the population
         * @param numberOfKuvat number of Kuvats in the population
         *  (number of Uutiset is always 1)
         * @param board board to place entities on
         *
         */
        fun generatePopulation(numberOfKuvahaku: Int, numberOfKuvat: Int, board: Board): Population {
            // Check if board is suitable for the population
            val minSize = EntitySize.UUTISET_SIZE + EntitySize.KUVAHAKU_SIZE + EntitySize.KUVAT_SIZE + 2
            require(board.boardSize.numberOfRows >= minSize) { "Board height must be greater than $minSize" }
            require(board.boardSize.rowLength >= minSize) { "Board length must be greater than $minSize" }

            val numberOfEntities = 1 + numberOfKuvahaku + numberOfKuvat
            val numberOfFreeAreas =
                (board.boardSize.numberOfRows * board.boardSize.rowLength) /
                        (EntitySize.MIN_ENTITY_AREA_SIDE_SIZE * EntitySize.MIN_ENTITY_AREA_SIDE_SIZE)
            require(numberOfFreeAreas >= numberOfEntities) { "Not enough area to place all entities" }

            // generate positions for the entities in the population
            val areas = mutableListOf<Coordinate>()
            for (rowIndex in 1..board.boardSize.numberOfRows step EntitySize.MIN_ENTITY_AREA_SIDE_SIZE) {
                for (positionInRow in 1..board.boardSize.rowLength step EntitySize.MIN_ENTITY_AREA_SIDE_SIZE) {
                    areas.add(Coordinate(positionInRow, rowIndex))
                }
            }

            val uutiset = initUutiset(areas, board) // places Uutiset on the board
            val kuvahakus = initKuvahakus(numberOfKuvahaku, areas, board) // places Kuvahakus on the board
            val kuvats = initKuvats(numberOfKuvat, areas, board) // places Kuvats on the board

            return Population(uutiset, kuvahakus, kuvats) // returns new population
        }

        /**
         * Function initializes Kuvats by providing them with positions.
         * @param numberOfKuvat number of Kuvats to initialize
         * @param areas list of free areas on the board
         * @param board board to place entities on
         */
        private fun initKuvats(numberOfKuvat: Int, areas: MutableList<Coordinate>, board: Board): List<Kuvat> {
            val kuvats = mutableListOf<Kuvat>()
            repeat(numberOfKuvat) {
                val coordinate = areas[Random.nextInt(0, areas.size)] // randomly pick a free coordinates
                areas.remove(coordinate) // remove it from the list of free coordinates
                val shiftedCorner = Coordinate( // shift corner of the entity area
                    coordinate.x + Random.nextInt(0, 2),
                    coordinate.y + Random.nextInt(0, 2)
                )
                kuvats.add( // add entity to the population
                    Kuvat(
                        position = Position(
                            coordinates = setOf(
                                Coordinate(shiftedCorner.x + 1, shiftedCorner.y + 1),
                                Coordinate(shiftedCorner.x + 1, shiftedCorner.y + 2),
                                Coordinate(shiftedCorner.x + 2, shiftedCorner.y + 1),
                                Coordinate(shiftedCorner.x + 2, shiftedCorner.y + 2),
                            ),
                            board
                        )
                    )
                )
            }

            return kuvats
        }

        /**
         * Function initializes Kuvahakus by providing them with positions.
         * @param numberOfKuvahaku number of Kuvahakus to initialize
         * @param areas list of free areas on the board
         * @param board board to place entities on
         */
        private fun initKuvahakus(numberOfKuvahaku: Int, areas: MutableList<Coordinate>, board: Board): List<Kuvahaku> {
            val kuvahakus = mutableListOf<Kuvahaku>()
            repeat(numberOfKuvahaku) {
                val coordinate = areas[Random.nextInt(0, areas.size)] // randomly pick a free coordinates
                areas.remove(coordinate) // remove it from the list of free coordinates
                kuvahakus.add( // add entity to the population
                    Kuvahaku(
                        position = Position(
                            coordinates = setOf(
                                Coordinate(
                                    coordinate.x + Random.nextInt(1, EntitySize.MIN_ENTITY_AREA_SIDE_SIZE - 1),
                                    coordinate.y + Random.nextInt(1, EntitySize.MIN_ENTITY_AREA_SIDE_SIZE - 1)
                                )
                            ),
                            board
                        )
                    )
                )
            }

            return kuvahakus
        }

        /**
         * Function initializes Uutiset by providing them with positions. Number of Uutset is nor required because
         * there is only 1 Uutiset.
         * @param areas list of free areas on the board
         * @param board board to place entities on
         */
        private fun initUutiset(areas: MutableList<Coordinate>, board: Board): Uutiset {
            val uutisetIndex = Random.nextInt(0, areas.size)
            val uutisetUpperLeftCoordinate = areas[uutisetIndex] // randomly pick a free coordinate
            areas.remove(uutisetUpperLeftCoordinate) // remove it from the list of free coordinates
            val coordinates = setOf( // coordinates of the entity area
                Coordinate(uutisetUpperLeftCoordinate.x + 1, uutisetUpperLeftCoordinate.y + 1),
                Coordinate(uutisetUpperLeftCoordinate.x + 1, uutisetUpperLeftCoordinate.y + 2),
                Coordinate(uutisetUpperLeftCoordinate.x + 1, uutisetUpperLeftCoordinate.y + 3),
                Coordinate(uutisetUpperLeftCoordinate.x + 2, uutisetUpperLeftCoordinate.y + 1),
                Coordinate(uutisetUpperLeftCoordinate.x + 2, uutisetUpperLeftCoordinate.y + 2),
                Coordinate(uutisetUpperLeftCoordinate.x + 2, uutisetUpperLeftCoordinate.y + 3),
                Coordinate(uutisetUpperLeftCoordinate.x + 3, uutisetUpperLeftCoordinate.y + 1),
                Coordinate(uutisetUpperLeftCoordinate.x + 3, uutisetUpperLeftCoordinate.y + 2),
                Coordinate(uutisetUpperLeftCoordinate.x + 3, uutisetUpperLeftCoordinate.y + 3),
            )

            return Uutiset(position = Position(coordinates, board)) // return initialized Uutiset
        }
    }
}
