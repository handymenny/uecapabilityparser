@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.shannon.lte.ShannonComboLte
import it.smartphonecombo.uecapabilityparser.model.shannon.lte.ShannonComponentLte
import it.smartphonecombo.uecapabilityparser.model.shannon.lte.ShannonLteUECap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

object ImportShannonLteUeCap : ImportCapabilities {
    override fun parse(input: InputSource): Capabilities {
        val capabilities = Capabilities()
        val byteArray = input.readBytes()
        val lteUECap = ProtoBuf.decodeFromByteArray<ShannonLteUECap>(byteArray)

        capabilities.setMetadata("shannonUeCapVersion", lteUECap.version)

        val list = mutableListWithCapacity<ComboLte>(lteUECap.combos.size)
        for (combo in lteUECap.combos) {
            list.add(processShannonCombo(combo))
        }

        capabilities.lteCombos = list

        return capabilities
    }

    private fun processShannonCombo(shCombo: ShannonComboLte): ComboLte {
        val lteComponents = shCombo.components
        val lte = processShannonComponentsLte(lteComponents)
        val bcs = shCombo.bcs

        val combo = ComboLte(lte, bcs)
        return combo
    }

    private fun processShannonComponentsLte(
        shComponents: List<ShannonComponentLte>
    ): List<ComponentLte> {
        val components = shComponents.map { processShannonComponentLte(it) }

        return components.sortedDescending()
    }

    private fun processShannonComponentLte(shComponent: ShannonComponentLte): ComponentLte {
        return shComponent.toComponent()
    }
}
