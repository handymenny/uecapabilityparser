package it.smartphonecombo.uecapabilityparser.model.shannon

import it.smartphonecombo.uecapabilityparser.model.LinkDirection
import it.smartphonecombo.uecapabilityparser.model.SingleMimo
import it.smartphonecombo.uecapabilityparser.model.feature.FeaturePerCCLte
import it.smartphonecombo.uecapabilityparser.model.feature.FeatureSet

/**
 * On Shannon modems the Eutra FeatureSet (featureSetsEUTRA-r15, featureSetsDL-PerCC-r15) seems to
 * be hardcoded.
 *
 * Analyzing the ue capabilities of different shannon devices, going from Galaxy SM-G977B to Pixel
 * GP4BC, the Eutra FeatureSet is always the same or at the least a reduced version that shares the
 * same ids. That is, the proposed version has 10 ids, a reduced version has 6, but proposed id 1 =
 * reduced id 1 and so on.
 *
 * This object implements that hardcoded Eutra FeatureSet. Modulation is removed as it's controlled
 * by other bits.
 */
object ShannonFeatureSetEutra {
    private val downlinkPerCC =
        listOf(FeaturePerCCLte(mimo = SingleMimo(4)), FeaturePerCCLte(mimo = SingleMimo(2)))
    private val uplinkPerCC =
        listOf(
            FeaturePerCCLte(LinkDirection.UPLINK, mimo = SingleMimo(2)),
            FeaturePerCCLte(LinkDirection.UPLINK, mimo = SingleMimo(1)),
        )

    val downlink =
        listOf(
            FeatureSet(listOf(downlinkPerCC[0]), LinkDirection.DOWNLINK),
            FeatureSet(listOf(downlinkPerCC[1]), LinkDirection.DOWNLINK),
            FeatureSet(List(2) { downlinkPerCC[0] }, LinkDirection.DOWNLINK),
            FeatureSet(listOf(downlinkPerCC[1], downlinkPerCC[0]), LinkDirection.DOWNLINK),
            FeatureSet(listOf(downlinkPerCC[0], downlinkPerCC[1]), LinkDirection.DOWNLINK),
            FeatureSet(List(2) { downlinkPerCC[1] }, LinkDirection.DOWNLINK),
            FeatureSet(List(3) { downlinkPerCC[0] }, LinkDirection.DOWNLINK),
            FeatureSet(List(3) { downlinkPerCC[1] }, LinkDirection.DOWNLINK),
            FeatureSet(List(4) { downlinkPerCC[0] }, LinkDirection.DOWNLINK),
            FeatureSet(List(4) { downlinkPerCC[1] }, LinkDirection.DOWNLINK),
        )

    val uplink =
        listOf(
            FeatureSet(listOf(uplinkPerCC[0]), LinkDirection.UPLINK),
            FeatureSet(listOf(uplinkPerCC[1]), LinkDirection.UPLINK),
            FeatureSet(listOf(uplinkPerCC[0], uplinkPerCC[1]), LinkDirection.UPLINK),
            FeatureSet(List(2) { uplinkPerCC[1] }, LinkDirection.UPLINK),
        )
}
