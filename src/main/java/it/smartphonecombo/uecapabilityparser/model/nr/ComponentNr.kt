package it.smartphonecombo.uecapabilityparser.model.nr

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.Mimo
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.IComponent
import it.smartphonecombo.uecapabilityparser.model.Modulation
import it.smartphonecombo.uecapabilityparser.util.Utility.toBwString

data class ComponentNr(
    override var band: Band,
    override var classDL: BwClass,
    override var classUL: BwClass,
    override var mimoDL: Mimo,
    override var mimoUL: Mimo,
    override var modDL: Modulation,
    override var modUL: Modulation
) : IComponent {
    var maxBandwidth = 0
    var channelBW90mhz = false
    var scs = 0
    var bandwidthsDL: MutableMap<Int, IntArray>? = null
    var bandwidthsUL: MutableMap<Int, IntArray>? = null
    var maxUplinkDutyCycle = 100
    var powerClass = 3
    var rateMatchingLTEcrs = false

    constructor(
        band: Band
    ) : this(band, BwClass.NONE, BwClass.NONE, 0, 0, Modulation.QAM256, Modulation.QAM64)
    constructor(
        band: Band,
        classDL: BwClass,
        classUL: BwClass
    ) : this(band, classDL, classUL, 0, 0, Modulation.QAM256, Modulation.QAM64)

    override fun compareTo(iComponent: IComponent): Int {
        TODO("Not yet implemented")
    }

    /**
     * To string.
     *
     * @return the string
     * @see java.lang.Object.toString
     */
    override fun toString(): String {
        var str = "n$band"
        if (classDL == BwClass.NONE) {
            str += '*'
        } else {
            str += classDL
            if (mimoDL > 0) {
                str += mimoDL
            }
        }
        str += classUL
        if (mimoUL > 1) {
            str += mimoUL
        }
        return str
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
}
