package it.smartphonecombo.uecapabilityparser.model.nr

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.Mimo
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.IComponent
import it.smartphonecombo.uecapabilityparser.model.Modulation
import it.smartphonecombo.uecapabilityparser.util.Utility.toBwString

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
    var bandwidthsDL: MutableMap<Int, IntArray>? = null
    var bandwidthsUL: MutableMap<Int, IntArray>? = null
    var maxUplinkDutyCycle = 100
    var powerClass = 3
    var rateMatchingLTEcrs = false

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
                { it.maxBandwidth },
                { it.powerClass }
            )
        } else {
            // Component Nr is higher than ComponentLTE
            1
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComponentNr) return false

        if (band != other.band) return false
        if (classDL != other.classDL) return false
        if (classUL != other.classUL) return false
        if (mimoDL != other.mimoDL) return false
        if (mimoUL != other.mimoUL) return false
        if (modDL != other.modDL) return false
        if (modUL != other.modUL) return false
        if (maxBandwidth != other.maxBandwidth) return false
        if (channelBW90mhz != other.channelBW90mhz) return false
        if (scs != other.scs) return false
        // TODO: customize equals
        if (toBwString() != other.toBwString()) return false
        if (maxUplinkDutyCycle != other.maxUplinkDutyCycle) return false
        if (powerClass != other.powerClass) return false
        if (rateMatchingLTEcrs != other.rateMatchingLTEcrs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = band
        result = 31 * result + classDL.hashCode()
        result = 31 * result + classUL.hashCode()
        result = 31 * result + mimoDL
        result = 31 * result + mimoUL
        result = 31 * result + (modDL?.hashCode() ?: 0)
        result = 31 * result + (modUL?.hashCode() ?: 0)
        result = 31 * result + maxBandwidth
        result = 31 * result + channelBW90mhz.hashCode()
        result = 31 * result + scs
        // TODO: customize hashcode
        result = 31 * result + toBwString().hashCode()
        result = 31 * result + maxUplinkDutyCycle
        result = 31 * result + powerClass
        result = 31 * result + rateMatchingLTEcrs.hashCode()
        return result
    }

    val isFR2: Boolean
        get() = band > 256

    override fun clone() = copy()
    override fun toCompactStr(): String = "n${super.toCompactStr()}"
}
