package tech.onsibey.squarelife.simulator.powers

import tech.onsibey.squarelife.simulator.entities.Board
import tech.onsibey.squarelife.simulator.entities.BoardSize
import tech.onsibey.squarelife.simulator.entities.Population
import tech.onsibey.squarelife.visualisation.NoopBoardVisualizer
import tech.onsibey.squarelife.visualisation.Visualizer

sealed class Jumala(
    private val board: Board,
    populationInitializer: (Board) -> Population,
    private val evolutionCycleNumber: Int
) {

    private val population: Population = populationInitializer(board)
    private val witness: Witness = Witness()
    private val procreator: Procreator = Procreator(board, population)
    private val death: Death = Death(population, board)
    private val mover: Mover = Mover(population, board)

    private val visualizer: Visualizer = NoopBoardVisualizer()
    //private val visualizer: Visualizer = ConsoleBoardVisualizer()

    val evolutionResult: EvolutionResultReport = EvolutionResultReport(board.boardSize, performEvolution())


    /**
     * Function for starting the evolution process.
     */
    private fun performEvolution(): List<EvolutionCycle>  {
        board.update(population.aliveEntitiesPositions())
        visualizer.visualize(
            EvolutionCycleReport(
                board.boardSize, EvolutionCycle(
                    number = -1, born = emptyList(),
                    swallowed = emptyList(), populationSnapshots = PopulationSnapshots(
                        initial = populationSnapshot(PopulationSnapshotType.INITIAL),
                        afterMovement = populationSnapshot(PopulationSnapshotType.AFTER_MOVEMENT),
                        afterProcreation = populationSnapshot(PopulationSnapshotType.AFTER_PROCREATION),
                        afterSwallowing = populationSnapshot(PopulationSnapshotType.AFTER_SWALLOWING)
                    )
                )
            )
        )
        repeat(evolutionCycleNumber) { evolutionCycle(it) }
        return witness.evolutionCycles()
    }

    /**
     * Function for evolution cycle.
     */
    private fun evolutionCycle(evolutionCycleNumber: Int) {
        val initialPopulationSnapshot = populationSnapshot(PopulationSnapshotType.INITIAL)

        // 1. command entities to move
        mover.move()
        val afterMovementPopulationSnapshot = populationSnapshot(PopulationSnapshotType.AFTER_MOVEMENT)

        // 2. check results of the movement
        // 2.1 swallowing
        val swallowed = death.processSwallowing()
        val afterSwallowingPopulationSnapshot = populationSnapshot(PopulationSnapshotType.AFTER_SWALLOWING)

        // 2.2 procreation
        val born = procreator.processProcreation()
        val afterProcreationPopulationSnapshot = populationSnapshot(PopulationSnapshotType.AFTER_PROCREATION)

        val evolutionCycle = EvolutionCycle(
            number = evolutionCycleNumber, born = born,
            swallowed = swallowed, populationSnapshots = PopulationSnapshots(
                initial = initialPopulationSnapshot,
                afterMovement = afterMovementPopulationSnapshot,
                afterProcreation = afterProcreationPopulationSnapshot,
                afterSwallowing = afterSwallowingPopulationSnapshot
            )
        )

        witness.addEvolutionCycle(evolutionCycle)

        visualizer.visualize(EvolutionCycleReport(boardSize = board.boardSize, evolutionCycle = evolutionCycle))
    }

    private fun populationSnapshot(populationSnapshotType: PopulationSnapshotType) =
        PopulationSnapshot(snapshotType = populationSnapshotType, aliveEntities = population.aliveEntitiesPositions())
}

data class EvolutionResultReport(
    val boardSize: BoardSize,
    val evolutionCycles: List<EvolutionCycle>
)

data class EvolutionCycleReport(
    val boardSize: BoardSize,
    val evolutionCycle: EvolutionCycle
)
