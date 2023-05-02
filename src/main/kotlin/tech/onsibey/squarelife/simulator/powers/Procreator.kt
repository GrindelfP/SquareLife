package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.simulator.entities.Board
import tech.onsibey.squarelife.simulator.entities.Coordinate
import tech.onsibey.squarelife.simulator.entities.Position
import tech.onsibey.squarelife.simulator.entities.Kuvahaku
import tech.onsibey.squarelife.simulator.entities.Kuvat
import tech.onsibey.squarelife.simulator.entities.Population
import tech.onsibey.squarelife.simulator.entities.Uutiset
import tech.onsibey.squarelife.simulator.entities.Mates
import tech.onsibey.squarelife.simulator.entities.Entity

/**
 * Class responsible for procreation of entities. It has the following properties passed as parameters:
 * @param board board on which entities are located
 * @param population current population of entities
 * @param updater updater of board instance
 */
class Procreator(private val board: Board, private val population: Population, private val updater: Updater) {
    /**
     * Function for processing the procreation. It is called every evolution cycle.
     * @param evolutionCycleNumber evolution cycle number
     */
    fun processProcreation(evolutionCycleNumber: Int): List<Entity> {
        val mates = buildMatesList() // list of mated entities
        val born = mutableListOf<Entity>() // list of born entities
        mates.forEach {
            born.addAll(it.procreate()) // make mates procreate
        }
        if (born.isNotEmpty()) { // check if someone was born
            updater.updateBoard(evolutionCycleNumber, "someone has been born!")
            println(population) // print updated population
        }
        return born
    }

    /**
     * Function for getting the list of mated entities.
     */
    private fun buildMatesList(): List<Mates> {
        val allMates = population.aliveEntities().flatMap { entity ->
            entity.mates() // get list of alive mated entities
        }

        val uniqueMates = mutableListOf<Mates>() // narrow the list of entities only to unique entities
        val matesIds = mutableListOf<String>() // list of ids of unique entities
        allMates.forEach {
            // Entities are monogamists, they don't mate with multiple entities at the same time
            if (!matesIds.contains(it.first.id) && !matesIds.contains(it.second.id)) {
                matesIds.add(it.first.id)
                matesIds.add(it.second.id)
                uniqueMates.add(it)
            } // complete list of unique entities, which are grouped only in pares (3 and more entities don't procreate)
        }

        return uniqueMates
    }

    /**
     * Extension function for class Entity, which checks if two entities mate with each other.
     */
    private fun Entity.mates(): List<Mates> {
        if (!alive) return emptyList() // if entity is dead, it doesn't mate

        val mates = population.aliveEntities().mapNotNull {
            if (this !is Uutiset && (this overlapsWithMate it || this neighboursWith it)) Mates(
                this,
                it
            ) else null
        // accept entity if it is not Uutiset and if it overlaps with mate or neighbours with mate
        }

        return mates
    }

    /**
     * Extension function for class Mate which lets mates to procreate.
     */
    private fun Mates.procreate(): List<Entity> {
        val born = mutableListOf<Entity>()
        val procreationSlots = procreationSlots().toMutableList() // list of positions where entities can procreate
        if (procreationSlots.size >= 2 + 1) {
            // picking new position for the first parent
            val firstNewPositionIndex = kotlin.random.Random.nextInt(0, procreationSlots.size)
            val firstNewPosition = procreationSlots.removeAt(firstNewPositionIndex)
            // picking new position for the second parent
            val secondNewPositionIndex = kotlin.random.Random.nextInt(0, procreationSlots.size)
            val secondNewPosition = procreationSlots.removeAt(secondNewPositionIndex)
            // picking new position for the child
            val childPositionIndex = kotlin.random.Random.nextInt(0, procreationSlots.size)
            val childPosition = procreationSlots.removeAt(childPositionIndex)

            // moving entities-parents to new positions
            first.position(firstNewPosition)
            second.position(secondNewPosition)

            when (first) { // adding new entity to population
                is Kuvat -> {
                    val child = Kuvat(childPosition)
                    population.addKuvat(child)
                    born.add(child)
                }
                is Kuvahaku -> {
                    val child = Kuvahaku(childPosition)
                    population.addKuvahaku(child)
                    born.add(child)
                }
                else -> throw IllegalStateException("This entity cannot procreate: ${first::class.simpleName}")
            }

            // We have to update board each time a new entity is born to prevent positioning of entities after
            // procreation on the same tiles. This can result in dramatic growth of the population when a lot of entities
            // occupy same tiles and procreation is not limited by available world
            board.update(population.aliveEntitiesPositions()) // update board with current alive entities positions
        }
        return born
    }

    /**
     * Function which returns a list of possible procreation slots for a happy couple of entities.
     */
    private fun Mates.procreationSlots(): List<Position> {
        val matesXCoordinatesSum =
            first.position().coordinates.sumOf { it.x } + second.position().coordinates.sumOf { it.x }
        val matesYCoordinatesSum =
            first.position().coordinates.sumOf { it.y } + first.position().coordinates.sumOf { it.y }
        val matesCoordinatesNumber = first.position().coordinates.size + second.position().coordinates.size
        val centerOfProcreation =
            Coordinate(matesXCoordinatesSum / matesCoordinatesNumber, matesYCoordinatesSum / matesCoordinatesNumber)

        // defining the area coordinates where entities can procreate
        var leftX = centerOfProcreation.x
        repeat(first.size * 2) {
            if (leftX - 1 != 0) leftX--
        }
        var rightX = centerOfProcreation.x + first.size
        repeat(first.size * 2) {
            if (rightX + 1 <= board.boardSize.rowLength) rightX++
        }
        var upY = centerOfProcreation.y
        repeat(first.size * 2) {
            if (upY - 1 != 0) upY--
        }
        var downY = centerOfProcreation.y + first.size
        repeat(first.size * 2) {
            if (downY + 1 <= board.boardSize.numberOfRows) downY++
        }

        // creating list of coordinates where entities can procreate
        val procreationCoordinates = mutableListOf<Coordinate>()
        (leftX..rightX).forEach { x ->
            (upY..downY).forEach { y ->
                procreationCoordinates.add(Coordinate(x, y))
            }
        }

        // creating list of positions where entities can procreate
        val procreationSlots = mutableListOf<Position>()
        procreationCoordinates.forEach { coordinate ->
            val coordinates = mutableSetOf<Coordinate>()
            repeat(first.size) { yDelta ->
                repeat(first.size) { xDelta ->
                    coordinates.add(Coordinate(coordinate.x + xDelta, coordinate.y + yDelta))
                }
            }
            procreationSlots.add(Position(coordinates, board))
        }

        // filtering out positions
        val validSlots =
            procreationSlots.filter {
                position -> position.coordinates.all { it.x in (leftX..rightX) && it.y in (upY..downY) }
            }

        return validSlots.filter { position ->
            position == first.position() ||
                    position == second.position() ||
                    position.coordinates.all { board.tileIsEmpty(it)
                    }
        }
    }

    /**
     * Extension-function for Entity type, checks if this Entity overlaps with its mate.
     */
    private infix fun Entity.overlapsWithMate(other: Entity) =
        id != other.id && this::class == other::class && commonCoordinates(other).size == size

    /**
     * Extension-function for Entity type, checks if it neighbours with other entity of the same kind.
     */
    private infix fun Entity.neighboursWith(second: Entity): Boolean =
        id != second.id && this::class == second::class && hasNeighbourCoordinates(second)

    /**
     * Extension-function for Entity type, checks if this entity has "contacting" coordinates with other entity
     * (i.e. this entity's position is different from other entity position only by size of this kind of entity).
     */
    private fun Entity.hasNeighbourCoordinates(second: Entity): Boolean =
        position().shift(0, -size) == second.position() || position().shift(0, size) == second.position()
                || position().shift(-size, 0) == second.position() || position().shift(size, 0) == second.position()
}
