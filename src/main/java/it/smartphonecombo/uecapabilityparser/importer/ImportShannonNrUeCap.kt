@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.feature.FeaturePerCCNr
import it.smartphonecombo.uecapabilityparser.model.feature.FeatureSet
import it.smartphonecombo.uecapabilityparser.model.shannon.nr.ComboNrFeatures
import it.smartphonecombo.uecapabilityparser.model.shannon.nr.ComboNrGroup
import it.smartphonecombo.uecapabilityparser.model.shannon.nr.ShannonComboNr
import it.smartphonecombo.uecapabilityparser.model.shannon.nr.ShannonComponentNr
import it.smartphonecombo.uecapabilityparser.model.shannon.nr.ShannonFeaturePerCCNr
import it.smartphonecombo.uecapabilityparser.model.shannon.nr.ShannonFeatureSetEutra
import it.smartphonecombo.uecapabilityparser.model.shannon.nr.ShannonNrUECap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

object ImportShannonNrUeCap : ImportCapabilities {
    override fun parse(input: InputSource): Capabilities {
        val capabilities = Capabilities()
        val byteArray = input.readBytes()
        val nrUeCap = ProtoBuf.decodeFromByteArray<ShannonNrUECap>(byteArray)

        capabilities.setMetadata("shannonUeCapVersion", nrUeCap.version)
        if (nrUeCap.id != null) capabilities.setMetadata("shannonUeCapId", nrUeCap.id)

        val lteFeatureSetsDl = ShannonFeatureSetEutra.downlink
        val lteFeatureSetsUl = ShannonFeatureSetEutra.uplink

        val nrFeaturesPerCCDl =
            nrUeCap.dlFeaturePerCCList.map(ShannonFeaturePerCCNr::toFeaturePerCCNr)
        val nrFeaturesPerCCUl =
            nrUeCap.ulFeaturePerCCList.map(ShannonFeaturePerCCNr::toFeaturePerCCNr)

        val combos =
            processComboGroups(
                nrUeCap.comboGroups,
                lteFeatureSetsDl,
                lteFeatureSetsUl,
                nrFeaturesPerCCDl,
                nrFeaturesPerCCUl,
            )

        capabilities.enDcCombos = combos.filterIsInstance<ComboEnDc>()
        capabilities.nrCombos = combos.filterIsInstance<ComboNr>()
        capabilities.nrDcCombos = combos.filterIsInstance<ComboNrDc>()

        return capabilities
    }

    private fun processComboGroups(
        comboGroups: List<ComboNrGroup>,
        lteFeatureSetsDl: List<FeatureSet>,
        lteFeatureSetsUl: List<FeatureSet>,
        nrFeaturesPerCCDl: List<FeaturePerCCNr>,
        nrFeaturesPerCCUl: List<FeaturePerCCNr>,
    ): List<ICombo> {
        val list = mutableListWithCapacity<ICombo>(comboGroups.size * 2)

        for (group in comboGroups) {
            val groupFeatures = group.comboFeatures

            val groupCombos =
                group.combos.map { shannonCombo ->
                    processShannonCombo(
                        shannonCombo,
                        lteFeatureSetsDl,
                        lteFeatureSetsUl,
                        nrFeaturesPerCCDl,
                        nrFeaturesPerCCUl,
                        groupFeatures,
                    )
                }

            list.addAll(groupCombos)
        }

        return list
    }

    private fun processShannonCombo(
        shCombo: ShannonComboNr,
        lteFeatureSetsDl: List<FeatureSet>,
        lteFeatureSetsUl: List<FeatureSet>,
        nrFeaturesPerCCDl: List<FeaturePerCCNr>,
        nrFeaturesPerCCUl: List<FeaturePerCCNr>,
        groupFeatures: ComboNrFeatures,
    ): ICombo {
        val (nrComponents, lteComponents) = shCombo.components.partition { it.isNr }
        val lte = processShannonComponentsLte(lteComponents, lteFeatureSetsDl, lteFeatureSetsUl)
        val nr = processShannonComponentsNr(nrComponents, nrFeaturesPerCCDl, nrFeaturesPerCCUl)

        val combo: ICombo
        if (lte.isEmpty()) {
            // NR CA or NR-DC
            combo =
                if (nr.none { it.isFR2 } || nr.none { !it.isFR2 }) {
                    ComboNr(nr, groupFeatures.bcsNr)
                } else {
                    // Assume NR-DC if there are both FR1 and FR2
                    val (fr2, fr1) = nr.partition { it.isFR2 }
                    ComboNrDc(fr1, fr2, groupFeatures.bcsNr)
                }
        } else {
            // EN-DC
            val (bcsNr, bcsEutra, bcsIntraEnDc) =
                mergeAndSplitEnDcBCS(
                    lte,
                    nr,
                    groupFeatures.bcsNr,
                    groupFeatures.bcsEutra,
                    groupFeatures.bcsIntraEndc,
                )
            combo = ComboEnDc(lte, nr, bcsNr, bcsEutra, bcsIntraEnDc)
        }
        return combo
    }

    private fun processShannonComponentsLte(
        shComponents: List<ShannonComponentNr>,
        featureSetsDl: List<FeatureSet>,
        featureSetsUl: List<FeatureSet>,
    ): List<ComponentLte> {
        val components =
            shComponents.map { processShannonComponentLte(it, featureSetsDl, featureSetsUl) }

        return components.sortedDescending()
    }

    private fun processShannonComponentsNr(
        shComponents: List<ShannonComponentNr>,
        featuresPerCCDl: List<FeaturePerCCNr>,
        featuresPerCCUl: List<FeaturePerCCNr>,
    ): List<ComponentNr> {
        val components =
            shComponents.map { processShannonComponentNr(it, featuresPerCCDl, featuresPerCCUl) }

        return components.sortedDescending()
    }

    private fun processShannonComponentLte(
        shComponent: ShannonComponentNr,
        featureSetsDl: List<FeatureSet>,
        featureSetsUl: List<FeatureSet>,
    ): ComponentLte {
        val dlFeature =
            featureSetsDl.getOrNull((shComponent.dlFeatureIndex - 1).toInt())?.featureSetsPerCC
        val ulFeature =
            featureSetsUl.getOrNull((shComponent.ulFeatureIndex - 1).toInt())?.featureSetsPerCC
        val component =
            mergeComponentAndFeaturePerCC(shComponent.toComponent(), dlFeature, ulFeature, null)

        return component as ComponentLte
    }

    private fun processShannonComponentNr(
        shComponent: ShannonComponentNr,
        featuresPerCCDl: List<FeaturePerCCNr>,
        featuresPerCCUl: List<FeaturePerCCNr>,
    ): ComponentNr {
        val dlFeature =
            shComponent.dlFeaturePerCCIds.mapNotNull { featuresPerCCDl.getOrNull((it - 1).toInt()) }
        val ulFeature =
            shComponent.ulFeaturePerCCIds.mapNotNull { featuresPerCCUl.getOrNull((it - 1).toInt()) }
        val component =
            mergeComponentAndFeaturePerCC(shComponent.toComponent(), dlFeature, ulFeature, null)

        return component as ComponentNr
    }
}
