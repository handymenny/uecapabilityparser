package it.smartphonecombo.uecapabilityparser.model.component

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.ModulationOrder

sealed interface IComponent : Comparable<IComponent> {
    fun clone(): IComponent

    var band: Band
    var classDL: BwClass
    var classUL: BwClass
    var mimoDL: Mimo
    var mimoUL: Mimo
    var modDL: ModulationOrder
    var modUL: ModulationOrder

    fun toCompactStr(): String {
        val classDlStr = if (classDL != BwClass.NONE) classDL.toString() else "*"
        val mimoDlStr = if (mimoDL != EmptyMimo) mimoDL.toCompactStr() else ""
        val mimoUlStr = if (mimoUL.average() > 1) mimoUL.toCompactStr() else ""

        return "$band$classDlStr$mimoDlStr$classUL$mimoUlStr"
    }
}
