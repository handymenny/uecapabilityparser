package it.smartphonecombo.uecapabilityparser.model.component

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.Mimo
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Modulation

data class ComponentLte(
    override var band: Band = 0,
    override var classDL: BwClass = BwClass.NONE,
    override var classUL: BwClass = BwClass.NONE,
    override var mimoDL: Mimo = 0,
    override var mimoUL: Mimo = 0,
    override var modDL: Modulation = Modulation.NONE,
    override var modUL: Modulation = Modulation.NONE
) : IComponent {

    override fun compareTo(other: IComponent): Int {
        return compareValuesBy(
            this,
            other,
            { it.band },
            { it.classDL },
            { it.classUL },
            { it.mimoDL },
            { it.mimoUL }
        )
    }

    override fun clone() = copy()
}
