@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.modulation.ModulationOrder
import it.smartphonecombo.uecapabilityparser.model.shannon.nr.ShannonFeatureDlPerCCNr
import it.smartphonecombo.uecapabilityparser.model.shannon.nr.ShannonFeatureUlPerCCNr
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ShannonFeaturePerCCNrTest {

    @Test
    fun testSCS15() {
        val feature = ShannonFeatureDlPerCCNr(1, 0, 0, 0, false)

        assertEquals(15, feature.maxScs)
    }

    @Test
    fun testSCS30() {
        val feature = ShannonFeatureUlPerCCNr(2, 0, 0, 0, false, 0)

        assertEquals(30, feature.maxScs)
    }

    @Test
    fun testSCS60() {
        val feature = ShannonFeatureDlPerCCNr(3, 0, 0, 0, false)

        assertEquals(60, feature.maxScs)
    }

    @Test
    fun testSCS120() {
        val feature = ShannonFeatureUlPerCCNr(4, 0, 0, 0, false, 0)

        assertEquals(120, feature.maxScs)
    }

    @Test
    fun testSCSInvalid() {
        val feature = ShannonFeatureDlPerCCNr(1000, 0, 0, 0, false)

        assertEquals(1000, feature.maxScs)
    }

    @Test
    fun testModOrderNone() {
        val feature = ShannonFeatureDlPerCCNr(1, 0, 0, 0, false)

        assertEquals(ModulationOrder.NONE, feature.maxModOrder)
    }

    @Test
    fun testModOrderNone2() {
        val feature = ShannonFeatureUlPerCCNr(1, 0, 0, 100, false, 0)

        assertEquals(ModulationOrder.NONE, feature.maxModOrder)
    }

    @Test
    fun testModOrder64qam() {
        val feature = ShannonFeatureDlPerCCNr(1, 0, 0, 1, false)

        assertEquals(ModulationOrder.QAM64, feature.maxModOrder)
    }

    @Test
    fun testModOrder256qam() {
        val feature = ShannonFeatureUlPerCCNr(1, 0, 0, 2, false, 0)

        assertEquals(ModulationOrder.QAM256, feature.maxModOrder)
    }

    @Test
    fun testMaxMimo2Dl() {
        val feature = ShannonFeatureDlPerCCNr(1, 1, 0, 0, false)
        assertEquals(2, feature.maxMimo)
    }

    @Test
    fun testMaxMimo4Dl() {
        val feature = ShannonFeatureDlPerCCNr(1, 2, 0, 0, false)
        assertEquals(4, feature.maxMimo)
    }

    @Test
    fun testMaxMimo8Ul() {
        val feature = ShannonFeatureDlPerCCNr(1, 3, 0, 0, false)
        assertEquals(8, feature.maxMimo)
    }

    @Test
    fun testMaxMimoInvalidDl() {
        val feature = ShannonFeatureDlPerCCNr(1, 100, 0, 0, false)
        assertEquals(0, feature.maxMimo)
    }

    @Test
    fun testMaxMimo1Ul() {
        val feature = ShannonFeatureUlPerCCNr(1, 1, 0, 0, false, 1)
        assertEquals(1, feature.maxMimo)
    }

    @Test
    fun testMaxMimo2Ul() {
        val feature = ShannonFeatureUlPerCCNr(1, 2, 0, 0, false, 2)
        assertEquals(2, feature.maxMimo)
    }

    @Test
    fun testMaxMimo4Ul() {
        val feature = ShannonFeatureUlPerCCNr(1, 3, 0, 0, false, 2)
        assertEquals(4, feature.maxMimo)
    }

    @Test
    fun testMaxMimoInvalidUl() {
        val feature = ShannonFeatureUlPerCCNr(1, 1000, 0, 0, false, 100)
        assertEquals(0, feature.maxMimo)
    }
}
