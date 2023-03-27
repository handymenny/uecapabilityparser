package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.Mimo

interface IComponent : Comparable<IComponent> {
    fun clone(): IComponent

    var band: Band
    var classDL: BwClass
    var classUL: BwClass
    var mimoDL: Mimo
    var mimoUL: Mimo
    var modDL: Modulation
    var modUL: Modulation
}
