package it.smartphonecombo.uecapabilityparser.model.component

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.Mimo
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Modulation

sealed interface IComponent : Comparable<IComponent> {
    fun clone(): IComponent

    var band: Band
    var classDL: BwClass
    var classUL: BwClass
    var mimoDL: Mimo
    var mimoUL: Mimo
    var modDL: Modulation
    var modUL: Modulation

    fun toCompactStr(): String {
        val classDlStr = if (classDL != BwClass.NONE) classDL.toString() else "*"
        val mimoDlStr = if (mimoDL > 0) mimoDL.toString() else ""
        val mimoUlStr = if (mimoUL > 1) mimoUL.toString() else ""

        return "$band$classDlStr$mimoDlStr$classUL$mimoUlStr"
    }
}
