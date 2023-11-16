package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyBCS
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.band.IBandDetails
import it.smartphonecombo.uecapabilityparser.model.bandwidth.Bandwidth
import it.smartphonecombo.uecapabilityparser.model.bandwidth.toBandwidth
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
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

internal fun mergeComponentLteAndFeature(
    component: ComponentLte,
    dlFeature: List<IFeaturePerCC>?,
    ulFeature: List<IFeaturePerCC>?,
    bandDetails: IBandDetails
): ComponentLte {
    val componentLte = component.copy()

    if (!dlFeature.isNullOrEmpty()) {
        if (dlFeature.size > 1) {
            val mixedMimo = dlFeature.map { it.mimo.average().toInt() }
            componentLte.mimoDL = Mimo.from(mixedMimo)
            val mixedModulation = dlFeature.map { it.qam }
            componentLte.modDL = maxOf(bandDetails.modDL, Modulation.from(mixedModulation))
        } else {
            val firstFeature = dlFeature.first()
            componentLte.mimoDL = firstFeature.mimo
            componentLte.modDL = maxOf(bandDetails.modDL, firstFeature.qam.toModulation())
        }
    } else {
        // only UL
        componentLte.classDL = BwClass.NONE
        componentLte.mimoDL = EmptyMimo
    }

    if (!ulFeature.isNullOrEmpty()) {
        if (ulFeature.size > 1) {
            val mixedMimo = ulFeature.map { it.mimo.average().toInt() }
            componentLte.mimoUL = Mimo.from(mixedMimo)
            val mixedModulation = ulFeature.map { it.qam }
            componentLte.modUL = maxOf(bandDetails.modUL, Modulation.from(mixedModulation))
        } else {
            val firstFeature = ulFeature.first()
            componentLte.mimoUL = firstFeature.mimo
            componentLte.modUL = maxOf(bandDetails.modUL, firstFeature.qam.toModulation())
        }
    } else {
        // only DL
        componentLte.classUL = BwClass.NONE
        componentLte.mimoUL = EmptyMimo
    }
    return componentLte
}

internal fun mergeComponentNrAndFeature(
    component: ComponentNr,
    dlFeature: List<FeaturePerCCNr>?,
    ulFeature: List<FeaturePerCCNr>?,
    nrBandDetails: IBandDetails
): ComponentNr {
    val componentNr = component.copy()
    var dlChannelBW90mhz = false
    var ulChannelBW90mhz = false

    if (!dlFeature.isNullOrEmpty()) {
        if (dlFeature.size > 1) {
            val mixedMimo = dlFeature.map { it.mimo.average().toInt() }
            componentNr.mimoDL = Mimo.from(mixedMimo)
            val mixedBandwidth = dlFeature.map(FeaturePerCCNr::bw)
            componentNr.maxBandwidthDl = Bandwidth.from(mixedBandwidth)
        } else {
            val firstFeature = dlFeature.first()
            componentNr.mimoDL = firstFeature.mimo
            componentNr.maxBandwidthDl = firstFeature.bw.toBandwidth()
        }
        dlChannelBW90mhz = dlFeature.any { it.channelBW90mhz }
        componentNr.scs = dlFeature.maxOf(FeaturePerCCNr::scs)
        // set mod dl from bandDetails, because modulation in NR features means something else
        // (see TS 38 306)
        componentNr.modDL = nrBandDetails.modDL
    } else {
        // only UL
        componentNr.classDL = BwClass.NONE
        componentNr.mimoDL = EmptyMimo
    }

    if (!ulFeature.isNullOrEmpty()) {
        if (ulFeature.size > 1) {
            val mixedMimo = ulFeature.map { it.mimo.average().toInt() }
            componentNr.mimoUL = Mimo.from(mixedMimo)
            val mixedBandwidth = ulFeature.map(FeaturePerCCNr::bw)
            componentNr.maxBandwidthUl = Bandwidth.from(mixedBandwidth)
        } else {
            val firstFeature = ulFeature.first()
            componentNr.mimoUL = firstFeature.mimo
            componentNr.maxBandwidthUl = firstFeature.bw.toBandwidth()
        }
        ulChannelBW90mhz = ulFeature.any { it.channelBW90mhz }
        componentNr.scs = maxOf(componentNr.scs, ulFeature.maxOf(FeaturePerCCNr::scs))

        // set mod ul from bandDetails, because modulation in NR features means something else
        // (see TS 38 306)
        componentNr.modUL = nrBandDetails.modUL
    } else {
        // only DL
        componentNr.classUL = BwClass.NONE
        componentNr.mimoUL = EmptyMimo
    }
    componentNr.channelBW90mhz = dlChannelBW90mhz || ulChannelBW90mhz
    return componentNr
}
