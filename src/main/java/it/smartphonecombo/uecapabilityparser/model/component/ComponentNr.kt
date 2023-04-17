package it.smartphonecombo.uecapabilityparser.model.component

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.modulation.EmptyModulation
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation

data class ComponentNr(
    override var band: Band = 0,
    override var classDL: BwClass = BwClass.NONE,
    override var classUL: BwClass = BwClass.NONE,
    override var mimoDL: Mimo = EmptyMimo,
    override var mimoUL: Mimo = EmptyMimo,
    override var modDL: Modulation = EmptyModulation,
    override var modUL: Modulation = EmptyModulation
) : IComponent {
    var maxBandwidth = 0
    var channelBW90mhz = false
    var scs = 0

    override fun compareTo(other: IComponent): Int {
        return if (other is ComponentNr) {
            compareValuesBy(
                this,
                other,
                { it.band },
                { it.classDL },
                { it.classUL },
                { it.mimoDL },
                { it.mimoUL },
                { it.scs },
                { it.maxBandwidth }
            )
        } else {
            // Component Nr is higher than ComponentLTE
            1
        }
    }

    val isFR2: Boolean
        get() = band > 256

    override fun clone() = copy()
    override fun toCompactStr(): String = "n${super.toCompactStr()}"
}
