package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.simulator.entities.Board
import tech.onsibey.squarelife.simulator.entities.BoardSize
import tech.onsibey.squarelife.simulator.entities.Population

sealed class Jumala(
    board: Board,
    populationInitializer: (Board) -> Population,
    private val evolutionCycleNumber: Int
) {

    private val population: Population = populationInitializer(board)
    private val witness: Witness = Witness()
    private val updater: Updater = Updater(board, population)
    private val procreator: Procreator = Procreator(board, population, updater)
    private val death: Death = Death(population, updater)
    private val mover: Mover = Mover(population, updater)

    val evolutionResult: EvolutionResultReport = EvolutionResultReport(board.boardSize, performEvolution())


    /**
     * Function for starting the evolution process.
     */
    private fun performEvolution(): List<EvolutionCycle>  {
        updater.updateBoard(-1)
        repeat(evolutionCycleNumber) { evolutionCycle(it) }
        return witness.evolutionCycles()
    }

    /**
     * Function for evolution cycle.
     */
    private fun evolutionCycle(evolutionCycleNumber: Int) {
        val initialPopulationSnapshot = populationSnapshot(PopulationSnapshotType.INITIAL)

        // 1. command entities to move
        mover.move(evolutionCycleNumber)
        val afterMovementPopulationSnapshot = populationSnapshot(PopulationSnapshotType.AFTER_MOVEMENT)

        // 2. check results of the movement
        // 2.1 swallowing
        val swallowed = death.processSwallowing(evolutionCycleNumber)
        val afterSwallowingPopulationSnapshot = populationSnapshot(PopulationSnapshotType.AFTER_SWALLOWING)

        // 2.2 procreation
        val born = procreator.processProcreation(evolutionCycleNumber)
        val afterProcreationPopulationSnapshot = populationSnapshot(PopulationSnapshotType.AFTER_PROCREATION)

        witness.addEvolutionCycle(
            EvolutionCycle(
                number = evolutionCycleNumber, born = born,
                swallowed = swallowed, populationSnapshots = PopulationSnapshots(
                    initial = initialPopulationSnapshot,
                    afterMovement = afterMovementPopulationSnapshot,
                    afterProcreation = afterProcreationPopulationSnapshot,
                    afterSwallowing = afterSwallowingPopulationSnapshot
                )
            )
        )
    }

    private fun populationSnapshot(populationSnapshotType: PopulationSnapshotType) =
        PopulationSnapshot(snapshotType = populationSnapshotType, aliveEntities = population.aliveEntitiesPositions())
}

data class EvolutionResultReport(
    val boardSize: BoardSize,
    val evolutionCycles: List<EvolutionCycle>
)
