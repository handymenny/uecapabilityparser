package it.smartphonecombo.uecapabilityparser.model.lte

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.Mimo
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.IComponent
import it.smartphonecombo.uecapabilityparser.model.Modulation

/** The Class LteBandAndBandwidth. */
data class ComponentLte(
    override var band: Band,
    override var classDL: BwClass,
    override var classUL: BwClass,
    override var mimoDL: Mimo,
    override var modDL: Modulation,
    override var modUL: Modulation,
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

    // Mimo UL is constant for LTE
    override var mimoUL = 1
        set(value) = Unit

    constructor(
        band: Band,
        classDL: BwClass,
        classUL: BwClass,
        mimoDL: Mimo
    ) : this(band, classDL, classUL, mimoDL, Modulation.QAM64, Modulation.QAM16)

    constructor(
        band: Band,
        classDL: BwClass,
        mimoDL: Mimo
    ) : this(band, classDL, BwClass.NONE, mimoDL)

    constructor(band: Band, classDL: BwClass, classUL: BwClass) : this(band, classDL, classUL, 0)

    constructor(band: Band) : this(band, BwClass.NONE, BwClass.NONE)

    constructor() : this(0)

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
