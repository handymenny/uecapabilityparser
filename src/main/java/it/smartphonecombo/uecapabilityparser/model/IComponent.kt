package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.Mimo

interface IComponent {
    fun compareTo(iComponent: IComponent): Int

    fun clone(): IComponent

    var band: Band
    var classDL: BwClass
    var classUL: BwClass
    var mimoDL: Mimo
    var mimoUL: Mimo
    var modDL: Modulation
    var modUL: Modulation

    companion object {
        val defaultComparator: Comparator<IComponent> by
            lazy(LazyThreadSafetyMode.PUBLICATION) {
                Comparator.comparing { obj: IComponent -> obj.band }
                    .thenComparing { obj: IComponent -> obj.classDL }
                    .thenComparing { obj: IComponent -> obj.classUL }
                    .thenComparing { obj: IComponent -> obj.mimoDL }
                    .thenComparing { obj: IComponent -> obj.mimoUL }
            }
    }
}
