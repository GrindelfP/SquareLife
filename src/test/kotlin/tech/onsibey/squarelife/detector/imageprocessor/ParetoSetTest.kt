package tech.onsibey.squarelife.detector.imageprocessor

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.onsibey.squarelife.detector.imageprocessor.ParetoTestData.expectedLargeParetoSet
import tech.onsibey.squarelife.detector.imageprocessor.ParetoTestData.expectedSmallParetoSet
import tech.onsibey.squarelife.detector.imageprocessor.ParetoTestData.largeInitialSet
import tech.onsibey.squarelife.detector.imageprocessor.ParetoTestData.smallInitialSet

class ParetoSetTest {
    @Test
    fun `GIVEN set of integers WHEN applying paretoSet() function to it THEN get pareto's set of the initial set`() {
        val actualParetoSet = SingleCriteriaParetoSet(smallInitialSet.toList(), 1)

        assertThat(actualParetoSet).isEqualTo(expectedSmallParetoSet)
        assertThat(actualParetoSet.averageInt()).isEqualTo(52)
    }

    @Test
    fun `GIVEN big set of integers WHEN applying paretoSet() function to it THEN get pareto's set of the initial set`() {
        val actualParetoSet = SingleCriteriaParetoSet(largeInitialSet.toList(), 5)

        assertThat(actualParetoSet).isEqualTo(expectedLargeParetoSet)
        assertThat(actualParetoSet.averageInt()).isEqualTo(503)
    }
}