package it.smartphonecombo.uecapabilityparser.model.component

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.Mimo
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Modulation

data class ComponentNr(
    override var band: Band = 0,
    override var classDL: BwClass = BwClass.NONE,
    override var classUL: BwClass = BwClass.NONE,
    override var mimoDL: Mimo = 0,
    override var mimoUL: Mimo = 0,
    override var modDL: Modulation = Modulation.NONE,
    override var modUL: Modulation = Modulation.NONE
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
