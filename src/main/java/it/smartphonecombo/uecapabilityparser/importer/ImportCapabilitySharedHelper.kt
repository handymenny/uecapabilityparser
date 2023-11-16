package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.typedList
import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyBCS
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.LinkDirection
import it.smartphonecombo.uecapabilityparser.model.Mimo
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
    bcsIntraEnDc: BCS
): Triple<BCS, BCS, BCS> {
    val intraBandEnDC = nrComponents.any { nr -> lteComponents.any { lte -> nr.band == lte.band } }
    val interBandLte =
        !intraBandEnDC || lteComponents.drop(1).any { it.band != lteComponents.firstOrNull()?.band }
    val interBandNr =
        !intraBandEnDC || nrComponents.drop(1).any { it.band != nrComponents.firstOrNull()?.band }

    return if (!intraBandEnDC) {
        // Don't set bcsIntraEnDc for ENDC Combos without any intraEnDc component
        Triple(bcsNr, bcsEutra, EmptyBCS)
    } else if (!interBandLte && !interBandNr) {
        // intraBandEnDc without additional interBand only has intraEnDc bcs
        // Set it to the max between bcsNr and bcsIntraEnDc to handle cases
        // where bcsIntraEnDc is missing
        Triple(EmptyBCS, EmptyBCS, maxOf(bcsIntraEnDc, bcsNr))
    } else {
        // interBand + intraBand, set all BCS available
        Triple(bcsNr, bcsEutra, bcsIntraEnDc)
    }
}

internal fun mergeComponentAndFeaturePerCC(
    component: IComponent,
    dlFeature: List<IFeaturePerCC>?,
    ulFeature: List<IFeaturePerCC>?,
    bandDetails: IBandDetails
): IComponent {
    if (component is ComponentLte) {
        val componentLte = component.copy()

        applyLteFeaturesPerCC(LinkDirection.DOWNLINK, componentLte, dlFeature, bandDetails.modDL)
        applyLteFeaturesPerCC(LinkDirection.UPLINK, componentLte, ulFeature, bandDetails.modUL)

        return componentLte
    } else {
        val componentNr = (component as ComponentNr).copy()
        val dlFeatureNr = dlFeature?.typedList<FeaturePerCCNr>()
        val ulFeatureNr = ulFeature?.typedList<FeaturePerCCNr>()

        applyNrFeaturesPerCC(LinkDirection.DOWNLINK, componentNr, dlFeatureNr, bandDetails.modDL)
        applyNrFeaturesPerCC(LinkDirection.UPLINK, componentNr, ulFeatureNr, bandDetails.modUL)

        return componentNr
    }
}

private fun applyNrFeaturesPerCC(
    direction: LinkDirection,
    component: ComponentNr,
    feature: List<FeaturePerCCNr>?,
    bandMod: Modulation?
) {
    if (feature.isNullOrEmpty()) {
        setSdlSul(direction, component)
        return
    }

    val mimo: Mimo
    val bw: Bandwidth
    var mod: Modulation

    // These are "single" features and shared between DL and UL
    // So we take the current value into account
    val scs = feature.maxOf(FeaturePerCCNr::scs)
    component.scs = maxOf(scs, component.scs)
    component.channelBW90mhz =
        component.channelBW90mhz || feature.any(FeaturePerCCNr::channelBW90mhz)

    if (feature.size > 1) {
        mimo = Mimo.from(feature.map { it.mimo.average().toInt() })
        bw = Bandwidth.from(feature.map(FeaturePerCCNr::bw))
        mod = Modulation.from(feature.map(IFeaturePerCC::qam))
    } else {
        val firstFeature = feature.first()
        mimo = firstFeature.mimo
        bw = firstFeature.bw.toBandwidth()
        mod = firstFeature.qam.toModulation()
    }

    /* mod in bandNrDetails takes precedence, because modulation in NR features means something else (see TS 38 306) */
    mod = bandMod ?: mod

    if (direction == LinkDirection.DOWNLINK) {
        component.mimoDL = mimo
        component.maxBandwidthDl = bw
        component.modDL = mod
    } else {
        component.mimoUL = mimo
        component.maxBandwidthUl = bw
        component.modUL = mod
    }
}

private fun applyLteFeaturesPerCC(
    direction: LinkDirection,
    component: ComponentLte,
    feature: List<IFeaturePerCC>?,
    bandMod: Modulation?
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
