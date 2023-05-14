package tech.onsibey.squarelife.detector.imageprocessor

import kotlin.math.abs
import kotlin.math.roundToInt


interface ParetoSet {
    fun set(): List<Int>
    fun averageInt(): Int
}

data class SingleCriteriaParetoSet(private var container: List<Int>, val tolerance: Int = 10) : ParetoSet {
    init {
        trimContainerToParetoSet()
    }

    override fun averageInt(): Int = average().roundToInt()

    override fun set(): List<Int> = container

    /*private fun trimContainerToParetoSet() {
        // excluding the largest and smallest deviant elements of the set to create a Pareto's set
        while (this.maximalDecreasePossible() || this.minimalDecreasePossible()) {
            container = when {
                this.symmetricalDecreasePossible() -> this.symmetricalDecrease() // on small, it can decrease only twice
                this.maximalDecreasePossible() -> this.maximalDecrease()
                this.minimalDecreasePossible() -> this.minimalDecrease()
                else -> this.container
            }
        }
    }*/

    /*private fun trimContainerToParetoSet() {
        while (this.container[container.size - 1] - this.container[container.size - 2] > tolerance) {
            this.container = this.maximalDecrease()
            val a = this.container[container.size - 1] - this.container[container.size - 2]
        }

        while (this.container[1] - this.container[0] > tolerance) {
            this.container = this.minimalDecrease()
            val a = this.container[0] - this.container[1]
        }
    }*/

    private fun trimContainerToParetoSet() {
        val candidateSets = mutableListOf<List<Int>>()
        var startingIndex = 0
        for (i in 1 until this.container.size) {
            if (abs(this.container[i] - this.container[i - 1]) > tolerance) {
                candidateSets.add(this.container.subList(startingIndex, i))
                startingIndex = i
            }
        }

        if (candidateSets.size != 0) { // if candidateSets is empty, then there is no deviation in the set
            val maxSizeSet = candidateSets.maxBy { it.size }
            this.container = maxSizeSet
        }
    }

    private fun average(): Double = container.average()

    private fun maximalDecrease(): List<Int> = this.container.dropLast(1)

    private fun minimalDecrease(): List<Int> = this.container.drop(1)

    private fun symmetricalDecrease(): List<Int> = this.container.dropLast(1).drop(1)

    private fun maximalDecreasePossible(): Boolean {
        return abs(this.average() - this.maximalDecrease().average()) >= tolerance
    }

    private fun minimalDecreasePossible(): Boolean {
        return abs(this.average() - this.minimalDecrease().average()) >= tolerance
    }

    private fun symmetricalDecreasePossible(): Boolean = maximalDecreasePossible() && minimalDecreasePossible()
}