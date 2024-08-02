package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.extension.typedList
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.band.IBandDetails
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import it.smartphonecombo.uecapabilityparser.model.component.IComponent

internal fun Capabilities.optimize() {
    // bands
    lteBands = lteBands.map(IBandDetails::intern).typedList()
    nrBands = nrBands.map(IBandDetails::intern).typedList()

    // combos
    lteCombos = lteCombos.map(ICombo::optimizeAndIntern).typedList()
    nrCombos = nrCombos.map(ICombo::optimizeAndIntern).typedList()
    nrDcCombos = nrDcCombos.map(ICombo::optimizeAndIntern).typedList()
    enDcCombos = enDcCombos.map(ICombo::optimizeAndIntern).typedList()
}

private fun ICombo.optimizeAndIntern(): ICombo {
    val res = if (alreadyInterned()) this else optimized()
    return res.intern()
}

private fun ICombo.optimized(): ICombo {
    val masterComponentsOpt = masterComponents.map(IComponent::intern)
    val secondaryComponentsOpt = secondaryComponents.map(IComponent::intern)

    return when (this) {
        is ComboLte -> copy(masterComponents = masterComponentsOpt.typedList())
        is ComboNr -> copy(masterComponents = masterComponentsOpt.typedList())
        is ComboNrDc ->
            copy(
                masterComponents = masterComponentsOpt.typedList(),
                secondaryComponents = secondaryComponentsOpt.typedList(),
            )
        is ComboEnDc ->
            copy(
                masterComponents = masterComponentsOpt.typedList(),
                secondaryComponents = secondaryComponentsOpt.typedList(),
            )
    }
}
