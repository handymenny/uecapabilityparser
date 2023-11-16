package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.shannon.ShannonFeatureSetEutra
import korlibs.memory.isOdd
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ShannonFeatureSetEutraTest {

    private val featuresDl = ShannonFeatureSetEutra.downlink
    private val featuresUl = ShannonFeatureSetEutra.uplink

    @Test
    fun testMimoDl() {
        for (i in featuresDl.indices) {
            val maxMimo = featuresDl[i].featureSetsPerCC[0].mimo.average().toInt()
            if (i.isOdd) {
                assertEquals(2, maxMimo)
            } else {
                assertEquals(4, maxMimo)
            }
        }
    }

    @Test
    fun testMimoUl() {
        for (i in featuresUl.indices) {
            val maxMimo = featuresUl[i].featureSetsPerCC[0].mimo.average().toInt()
            if (i.isOdd) {
                assertEquals(1, maxMimo)
            } else {
                assertEquals(2, maxMimo)
            }
        }
    }
}
