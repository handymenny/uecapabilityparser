package it.smartphonecombo.uecapabilityparser.bean.nr

import it.smartphonecombo.uecapabilityparser.bean.IComponent

data class ComponentNr(
    override var band: Int,
    override var classDL: Char,
    override var classUL: Char,
    override var mimoDL: Int,
    override var mimoUL: Int,
    override var modDL: String?,
    override var modUL: String?
) : IComponent {
    var maxBandwidth = 0
    var channelBW90mhz = false
    var scs = 0
    var bandwidthsDL: MutableMap<Int, IntArray>? = null
    var bandwidthsUL: MutableMap<Int, IntArray>? = null
    var maxUplinkDutyCycle = 100
    var powerClass = 3
    var rateMatchingLTEcrs = false

    constructor(band: Int) : this(band, 'A', '0', 0, 0, "256qam", "64qam")
    constructor(band: Int, classDL: Char, classUL: Char) : this(band, classDL, classUL, 0, 0, "256qam", "64qam")

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
        if (classDL <= '0') {
            str += '*'
        } else {
            str += classDL
            if (mimoDL > 0) {
                str += mimoDL
            }
        }
        if (classUL > '0') {
            str += classUL
            if (mimoUL > 1) {
                str += mimoUL
            }
        }
        return str
    }

    override fun toStringExtended(): String {
        var str = "n$band"
        if (classDL <= '0') {
            str += '*'
        } else {
            str += classDL
            if (mimoDL > 0) {
                str += mimoDL
            }
            if ("256qam" != modDL) {
                str += "^$modDL"
            }
        }
        if (classUL > '0') {
            str += classUL
            if (mimoUL > 1) {
                str += mimoUL
            }
            if ("64qam" != modUL) {
                str += "^$modUL"
            }
        }
        str += "^$maxBandwidth-$scs"
        return str
    }

    val isSUL: Boolean
        get() = classUL <= '0'
    val isFR2: Boolean
        get() = band > 256
}