package it.smartphonecombo.uecapabilityparser.bean.lte

import it.smartphonecombo.uecapabilityparser.bean.IComponent

/**
 * The Class LteBandAndBandwidth.
 */
data class ComponentLte(
    override var band: Int,
    override var classDL: Char,
    override var classUL: Char,
    override var mimoDL: Int,
    override var modDL: String?,
    override var modUL: String?,
) : Comparable<ComponentLte>, IComponent {

    override fun compareTo(iComponent: IComponent): Int {
        return IComponent.defaultComparator.compare(this, iComponent)
    }

    // Mimo UL is constant for LTE
    override var mimoUL = 1
        set(value) = Unit


    constructor(band: Int, classDL: Char, classUL: Char, mimoDL: Int) : this(
        band,
        classDL,
        classUL,
        mimoDL,
        "64qam",
        "16qam"
    )

    constructor(band: Int, classDL: Char, mimoDL: Int) : this(band, classDL, '0', mimoDL)

    constructor(band: Int, classDL: Char, classUL: Char) : this(band, classDL, classUL, 0)

    constructor(band: Int) : this(band, 'A', '0')

    constructor() : this(0)


    override fun compareTo(other: ComponentLte): Int {
        return IComponent.defaultComparator.compare(this, other)
    }

    /**
     * To string.
     *
     * @return the string
     * @see java.lang.Object.toString
     */
    override fun toString(): String {
        var str = band.toString() + ""
        str += classDL
        if (mimoDL > 0) {
            str += mimoDL
        }
        if (classUL > '0') {
            str += classUL
        }
        return str
    }

    override fun toStringExtended(): String {
        var str = band.toString() + ""
        str += classDL
        if ("64qam" != modDL) {
            str += "^$modDL"
        }
        if (mimoDL > 0) {
            str += mimoDL
        }
        if (classUL > '0') {
            str += classUL
            if ("16qam" != modUL) {
                str += "^$modUL"
            }
        }
        return str
    }

    companion object {
        fun lteComponentsToArrays(
            band: IntArray, bandwidth: CharArray, mimo: IntArray,
            upload: CharArray, modUL: Array<String?>, inputArray: Array<IComponent>
        ) {
            for (i in inputArray.indices) {
                band[i] = inputArray[i].band
                mimo[i] = inputArray[i].mimoDL
                bandwidth[i] = inputArray[i].classDL
                upload[i] = inputArray[i].classUL
                modUL[i] = inputArray[i].modUL
            }
        }
    }
}