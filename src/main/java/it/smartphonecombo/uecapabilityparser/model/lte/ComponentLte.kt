package it.smartphonecombo.uecapabilityparser.model.lte

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.IComponent
import it.smartphonecombo.uecapabilityparser.model.Modulation

/** The Class LteBandAndBandwidth. */
data class ComponentLte(
    override var band: Int,
    override var classDL: BwClass,
    override var classUL: BwClass,
    override var mimoDL: Int,
    override var modDL: Modulation,
    override var modUL: Modulation,
) : Comparable<ComponentLte>, IComponent {

    override fun compareTo(iComponent: IComponent): Int {
        return IComponent.defaultComparator.compare(this, iComponent)
    }

    // Mimo UL is constant for LTE
    override var mimoUL = 1
        set(value) = Unit

    constructor(
        band: Int,
        classDL: BwClass,
        classUL: BwClass,
        mimoDL: Int
    ) : this(band, classDL, classUL, mimoDL, Modulation.QAM64, Modulation.QAM16)

    constructor(
        band: Int,
        classDL: BwClass,
        mimoDL: Int
    ) : this(band, classDL, BwClass.NONE, mimoDL)

    constructor(band: Int, classDL: BwClass, classUL: BwClass) : this(band, classDL, classUL, 0)

    constructor(band: Int) : this(band, BwClass.NONE, BwClass.NONE)

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
        if (classDL == BwClass.NONE) {
            str += "*"
        } else {
            str += classDL
        }
        if (mimoDL > 0) {
            str += mimoDL
        }
        str += classUL
        return str
    }

    companion object {
        fun lteComponentsToArrays(
            band: IntArray,
            bandwidth: Array<BwClass>,
            mimo: IntArray,
            upload: Array<BwClass>,
            modUL: Array<Modulation>,
            inputArray: Array<IComponent>
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

    override fun clone() = copy()
}
