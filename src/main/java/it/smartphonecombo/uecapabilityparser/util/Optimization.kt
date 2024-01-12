package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.extension.trimToSize
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.band.IBandDetails
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent

internal fun Capabilities.optimize() {
    lteBands.forEach { it.optimize() }
    nrBands.forEach { it.optimize() }
    lteCombos.forEach { it.optimize() }
    nrCombos.forEach { it.optimize() }
    nrDcCombos.forEach { it.optimize() }
    enDcCombos.forEach { it.optimize() }
}

internal fun IBandDetails.optimize() {
    mimoDL = mimoDL.intern()
    mimoUL = mimoUL.intern()
    modDL = modDL.intern()
    modUL = modUL.intern()
}

internal fun ICombo.optimize() {
    masterComponents.trimToSize()
    masterComponents.forEach { it.optimize() }
    secondaryComponents.trimToSize()
    secondaryComponents.forEach { it.optimize() }
}

internal fun IComponent.optimize() {
    mimoDL = mimoDL.intern()
    mimoUL = mimoUL.intern()
    modDL = modDL.intern()
    modUL = modUL.intern()
    if (this is ComponentNr) {
        maxBandwidthDl = maxBandwidthDl.intern()
        maxBandwidthUl = maxBandwidthUl.intern()
    }
}
