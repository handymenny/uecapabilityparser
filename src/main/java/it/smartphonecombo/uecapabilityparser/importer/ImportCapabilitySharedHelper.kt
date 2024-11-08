package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.typedList
import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyBCS
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.LinkDirection
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.band.BandNrDetails
import it.smartphonecombo.uecapabilityparser.model.band.IBandDetails
import it.smartphonecombo.uecapabilityparser.model.bandwidth.Bandwidth
import it.smartphonecombo.uecapabilityparser.model.bandwidth.toBandwidth
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.model.feature.FeaturePerCCNr
import it.smartphonecombo.uecapabilityparser.model.feature.IFeaturePerCC
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation
import it.smartphonecombo.uecapabilityparser.model.modulation.toModulation

// Helper functions share between multiple importers
internal fun mergeAndSplitEnDcBCS(
    lteComponents: List<ComponentLte>,
    nrComponents: List<ComponentNr>,
    bcsNr: BCS,
    bcsEutra: BCS,
    bcsIntraEnDc: BCS,
): Triple<BCS, BCS, BCS> {
    val intraBandEnDC = nrComponents.any { nr -> lteComponents.any { lte -> nr.band == lte.band } }

    if (!intraBandEnDC) {
        // Don't set bcsIntraEnDc for ENDC Combos without any intraEnDc component
        return Triple(bcsNr, bcsEutra, EmptyBCS)
    }

    val interBandLte = lteComponents.drop(1).any { it.band != lteComponents[0].band }
    val interBandNr = nrComponents.drop(1).any { it.band != nrComponents[0].band }

    return if (interBandLte || interBandNr) {
        // interBand + intraBand, set all BCS available
        Triple(bcsNr, bcsEutra, bcsIntraEnDc)
    } else {
        /* intraBandEnDc without additional interBand only has intraEnDc bcs
         * Set it to the max between bcsNr and bcsIntraEnDc to handle cases
         * where bcsIntraEnDc is missing */
        Triple(EmptyBCS, EmptyBCS, maxOf(bcsIntraEnDc, bcsNr))
    }
}

internal fun mergeComponentAndFeaturePerCC(
    component: IComponent,
    dlFeature: List<IFeaturePerCC>?,
    ulFeature: List<IFeaturePerCC>?,
    bandDetails: IBandDetails?,
): IComponent {
    if (component is ComponentLte) {
        val componentLte = component.copy()

        applyLteFeaturesPerCC(LinkDirection.DOWNLINK, componentLte, dlFeature, bandDetails?.modDL)
        applyLteFeaturesPerCC(LinkDirection.UPLINK, componentLte, ulFeature, bandDetails?.modUL)

        return componentLte
    } else {
        val componentNr = (component as ComponentNr).copy()
        val dlFeatureNr = dlFeature?.typedList<FeaturePerCCNr>()
        val ulFeatureNr = ulFeature?.typedList<FeaturePerCCNr>()
        val bandNrDetails = bandDetails as? BandNrDetails

        applyNrFeaturesPerCC(LinkDirection.DOWNLINK, componentNr, dlFeatureNr, bandNrDetails)
        applyNrFeaturesPerCC(LinkDirection.UPLINK, componentNr, ulFeatureNr, bandNrDetails)

        return componentNr
    }
}

private fun clampNrBw(maxBwFeatures: Int, maxBwBands: Int): Int {
    // In channelBWs:
    //   - for fr1 an ue can't report 90MHz support
    //   - for fr2 an ue can't report >= 400MHz support
    // FR1 100MHz is a bit special:
    //   - when it's mandatory in rel15 it's added in BwsBitMap constructor
    //   - when it's optional in rel15 it should be reported in channelBWs-v1590
    //   - if neither of the above applies, it's assumed that 100MHz (and 90MHz) isn't supported
    // So we skip the clamp for bw = 90MHz and bw >= 400MHz
    return if (maxBwFeatures != 90 && maxBwFeatures < 400) {
        minOf(maxBwFeatures, maxBwBands)
    } else {
        maxBwFeatures
    }
}

private fun applyNrFeaturesPerCC(
    direction: LinkDirection,
    component: ComponentNr,
    feature: List<FeaturePerCCNr>?,
    bandDetails: BandNrDetails?,
) {
    if (feature.isNullOrEmpty()) {
        setSdlSul(direction, component)
        return
    }

    val mimo: Mimo
    val bw: Bandwidth
    val mod: Modulation

    // These are "single" features and shared between DL and UL
    // So we take the current value into account
    val scs = feature.maxOf(FeaturePerCCNr::scs)
    component.scs = maxOf(scs, component.scs)
    component.channelBW90mhz =
        component.channelBW90mhz || feature.any(FeaturePerCCNr::channelBW90mhz)

    val maxBwBand =
        bandDetails
            ?.bandwidths
            ?.find { it.scs == scs }
            ?.let {
                if (direction == LinkDirection.DOWNLINK) {
                    it.bwsDL
                } else {
                    it.bwsUL
                }
            }
            ?.maxOrNull() ?: Int.MAX_VALUE

    if (feature.size > 1) {
        mimo = Mimo.from(feature.map { it.mimo.average().toInt() })
        bw = Bandwidth.from(feature.map { clampNrBw(it.bw, maxBwBand) })
        mod = Modulation.from(feature.map(IFeaturePerCC::qam))
    } else {
        val firstFeature = feature.first()
        mimo = firstFeature.mimo
        bw = clampNrBw(firstFeature.bw, maxBwBand).toBandwidth()
        mod = firstFeature.qam.toModulation()
    }

    if (direction == LinkDirection.DOWNLINK) {
        component.mimoDL = mimo
        component.maxBandwidthDl = bw
        /* mod in bandNrDetails takes precedence, because modulation in NR features means something else (see TS 38 306) */
        component.modDL = bandDetails?.modDL ?: mod
    } else {
        component.mimoUL = mimo
        component.maxBandwidthUl = bw
        /* mod in bandNrDetails takes precedence, because modulation in NR features means something else (see TS 38 306) */
        component.modUL = bandDetails?.modUL ?: mod
    }
}

private fun applyLteFeaturesPerCC(
    direction: LinkDirection,
    component: ComponentLte,
    feature: List<IFeaturePerCC>?,
    bandMod: Modulation?,
) {
    if (feature.isNullOrEmpty()) {
        setSdlSul(direction, component)
        return
    }

    val mimo: Mimo
    var mod: Modulation

    if (feature.size > 1) {
        mimo = Mimo.from(feature.map { it.mimo.average().toInt() })
        mod = Modulation.from(feature.map(IFeaturePerCC::qam))
    } else {
        val firstFeature = feature.first()
        mimo = firstFeature.mimo
        mod = firstFeature.qam.toModulation()
    }

    // Set the max between features mod and band mod
    bandMod?.let { mod = maxOf(it, mod) }

    if (direction == LinkDirection.DOWNLINK) {
        component.mimoDL = mimo
        component.modDL = mod
    } else {
        component.mimoUL = mimo
        component.modUL = mod
    }
}

private fun setSdlSul(direction: LinkDirection, component: IComponent) {
    if (direction == LinkDirection.DOWNLINK) {
        // SDL
        component.classDL = BwClass.NONE
        component.mimoDL = EmptyMimo
    } else {
        // SUL
        component.classUL = BwClass.NONE
        component.mimoUL = EmptyMimo
    }
}
