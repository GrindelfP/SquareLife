package tech.onsibey.squarelife.detector.imageprocessor

import kotlin.math.abs
import kotlin.math.roundToInt


interface ParetoSet {
    fun set(): List<Int>
    fun averageInt(): Int
}


data class SingleCriteriaParetoSet(private var container: List<Int>, val tolerance: Int = 1) : ParetoSet {
    init {
        // excluding the largest and smallest deviant elements of the set to create a Pareto's set
        while (this.maximalDecreasePossible() || this.minimalDecreasePossible()) {
            container = when {
                this.symmetricalDecreasePossible() -> this.symmetricalDecrease()
                this.maximalDecreasePossible() -> this.maximalDecrease()
                this.minimalDecreasePossible() -> this.minimalDecrease()
                else -> this.container
            }
        }
    }

    override fun averageInt(): Int = average().roundToInt()

    override fun set(): List<Int> = container

    private fun average(): Double = container.average()

    private fun maximalDecrease(): List<Int> = this.container.dropLast(1)

    private fun minimalDecrease(): List<Int> = this.container.drop(1)

    private fun symmetricalDecrease(): List<Int> = this.container.dropLast(1).drop(1)

    private fun maximalDecreasePossible(): Boolean =
        abs(this.average() - this.maximalDecrease().average()) >= tolerance

    private fun minimalDecreasePossible(): Boolean =
        abs(this.average() - this.minimalDecrease().average()) >= tolerance

    private fun symmetricalDecreasePossible(): Boolean = maximalDecreasePossible() && minimalDecreasePossible()
}