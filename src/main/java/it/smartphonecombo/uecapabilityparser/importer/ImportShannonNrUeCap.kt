@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.feature.FeaturePerCCNr
import it.smartphonecombo.uecapabilityparser.model.feature.FeatureSet
import it.smartphonecombo.uecapabilityparser.model.shannon.ComboFeatures
import it.smartphonecombo.uecapabilityparser.model.shannon.ComboGroup
import it.smartphonecombo.uecapabilityparser.model.shannon.ShannonCombo
import it.smartphonecombo.uecapabilityparser.model.shannon.ShannonComponent
import it.smartphonecombo.uecapabilityparser.model.shannon.ShannonFeaturePerCCNr
import it.smartphonecombo.uecapabilityparser.model.shannon.ShannonFeatureSetEutra
import it.smartphonecombo.uecapabilityparser.model.shannon.ShannonNrUECap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

object ImportShannonNrUeCap : ImportCapabilities {
    override fun parse(input: ByteArray): Capabilities {
        val capabilities = Capabilities()
        val nrUeCap = ProtoBuf.decodeFromByteArray<ShannonNrUECap>(input)

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
                nrFeaturesPerCCUl
            )

        capabilities.enDcCombos = combos.filterIsInstance<ComboEnDc>()
        capabilities.nrCombos = combos.filterIsInstance<ComboNr>()
        capabilities.nrDcCombos = combos.filterIsInstance<ComboNrDc>()

        return capabilities
    }

    private fun processComboGroups(
        comboGroups: List<ComboGroup>,
        lteFeatureSetsDl: List<FeatureSet>,
        lteFeatureSetsUl: List<FeatureSet>,
        nrFeaturesPerCCDl: List<FeaturePerCCNr>,
        nrFeaturesPerCCUl: List<FeaturePerCCNr>
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
                        groupFeatures
                    )
                }

            list.addAll(groupCombos)
        }

        return list
    }

    private fun processShannonCombo(
        shCombo: ShannonCombo,
        lteFeatureSetsDl: List<FeatureSet>,
        lteFeatureSetsUl: List<FeatureSet>,
        nrFeaturesPerCCDl: List<FeaturePerCCNr>,
        nrFeaturesPerCCUl: List<FeaturePerCCNr>,
        groupFeatures: ComboFeatures
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
                    groupFeatures.bcsIntraEndc
                )
            combo = ComboEnDc(lte, nr, bcsNr, bcsEutra, bcsIntraEnDc)
        }
        return combo
    }

    private fun processShannonComponentsLte(
        shComponents: List<ShannonComponent>,
        featureSetsDl: List<FeatureSet>,
        featureSetsUl: List<FeatureSet>
    ): List<ComponentLte> {
        val components =
            shComponents.map { processShannonComponentLte(it, featureSetsDl, featureSetsUl) }

        return components.sortedDescending()
    }

    private fun processShannonComponentsNr(
        shComponents: List<ShannonComponent>,
        featuresPerCCDl: List<FeaturePerCCNr>,
        featuresPerCCUl: List<FeaturePerCCNr>
    ): List<ComponentNr> {
        val components =
            shComponents.map { processShannonComponentNr(it, featuresPerCCDl, featuresPerCCUl) }

        return components.sortedDescending()
    }

    private fun processShannonComponentLte(
        shComponent: ShannonComponent,
        featureSetsDl: List<FeatureSet>,
        featureSetsUl: List<FeatureSet>
    ): ComponentLte {
        val dlFeature = featureSetsDl.getOrNull(shComponent.dlFeatureIndex - 1)?.featureSetsPerCC
        val ulFeature = featureSetsUl.getOrNull(shComponent.ulFeatureIndex - 1)?.featureSetsPerCC
        val component =
            mergeComponentAndFeaturePerCC(shComponent.toComponent(), dlFeature, ulFeature, null)

        return component as ComponentLte
    }

    private fun processShannonComponentNr(
        shComponent: ShannonComponent,
        featuresPerCCDl: List<FeaturePerCCNr>,
        featuresPerCCUl: List<FeaturePerCCNr>
    ): ComponentNr {
        val dlFeature =
            shComponent.dlFeaturePerCCIds.mapNotNull { featuresPerCCDl.getOrNull(it - 1) }
        val ulFeature =
            shComponent.ulFeaturePerCCIds.mapNotNull { featuresPerCCUl.getOrNull(it - 1) }
        val component =
            mergeComponentAndFeaturePerCC(shComponent.toComponent(), dlFeature, ulFeature, null)

        return component as ComponentNr
    }
}
