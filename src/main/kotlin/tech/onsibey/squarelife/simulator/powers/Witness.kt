package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.simulator.entities.Entity
import tech.onsibey.squarelife.simulator.entities.EntityPosition

class Witness {

    private val evolutionCycles = mutableListOf<EvolutionCycle>()

    fun addEvolutionCycle(evolutionCycle: EvolutionCycle) = evolutionCycles.add(evolutionCycle)

    fun evolutionCycles(): List<EvolutionCycle> = evolutionCycles.toList()
}

data class EvolutionCycle(
    val number: Int,
    val born: List<Entity>,
    val swallowed: List<Entity>,
    val populationSnapshots: PopulationSnapshots
)

data class PopulationSnapshots(
    val initial: PopulationSnapshot,
    val afterMovement: PopulationSnapshot,
    val afterProcreation: PopulationSnapshot,
    val afterSwallowing: PopulationSnapshot
)

data class PopulationSnapshot(
    val snapshotType: PopulationSnapshotType,
    val aliveEntities: List<EntityPosition>
)

enum class PopulationSnapshotType {
    INITIAL,
    AFTER_MOVEMENT,
    AFTER_PROCREATION,
    AFTER_SWALLOWING
}
