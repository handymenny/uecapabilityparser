package it.smartphonecombo.uecapabilityparser.model.lte

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.ICombo
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.util.Utility

/** The Class Combo. */
data class ComboLte(override var masterComponents: Array<IComponent>, var bcs: IntArray) : ICombo {
    override var secondaryComponents: Array<IComponent>
        get() = emptyArray()
        set(value) = Unit

    constructor(components: Array<IComponent>) : this(components, IntArray(0))

    constructor(components: Array<IComponent>, bcs: Int) : this(components) {
        setSingleBcs(bcs)
    }

    /**
     * Sets the bcs.
     *
     * @param bcs the bcs to set
     */
    fun setSingleBcs(bcs: Int) {
        this.bcs = intArrayOf(bcs)
    }

    /**
     * To string.
     *
     * @return the string
     * @see java.lang.Object.toString
     */
    override fun toString(): String {
        val str = StringBuilder()
        for (x in masterComponents) {
            str.append(x.toCompactStr())
            str.append("-")
        }
        str.append(Utility.arrayToQcomBcs(bcs))
        return str.toString()
    }

    override fun toCsv(
        separator: String,
        lteDlCC: Int,
        lteUlCC: Int,
        nrDlCC: Int,
        nrUlCC: Int,
        nrDcDlCC: Int,
        nrDcUlCC: Int
    ): String {
        val str = StringBuilder(this.toString() + separator)
        val strBw = StringBuilder()
        val strMimo = StringBuilder()
        val strUl = StringBuilder()
        val strDLmod = StringBuilder()
        val strULmod = StringBuilder()
        var i = 0
        while (i < masterComponents.size) {
            str.append(masterComponents[i].band)
            val x = masterComponents[i].mimoDL
            if (x != 0) {
                strMimo.append(x)
            }
            var y = masterComponents[i].classDL
            strBw.append(y)
            y = masterComponents[i].classUL
            if (y != BwClass.NONE) {
                strUl.append(y)
                strULmod.append(masterComponents[i].modUL)
            }
            str.append(separator)
            strMimo.append(separator)
            strBw.append(separator)
            strUl.append(separator)
            strDLmod.append(masterComponents[i].modDL).append(separator)
            strULmod.append(separator)
            i++
        }
        while (i < lteDlCC) {
            str.append(separator)
            strMimo.append(separator)
            strBw.append(separator)
            strUl.append(separator)
            strDLmod.append(separator)
            strULmod.append(separator)
            i++
        }
        str.append(strBw).append(strMimo).append(strUl).append(strDLmod).append(strULmod)
        var strBcs: String
        if (bcs.isNotEmpty()) {
            strBcs = bcs.contentToString().substring(1)
            strBcs = strBcs.substring(0, strBcs.length - 1)
        } else {
            strBcs = "all"
        }
        str.append(strBcs)
        return str.toString()
    }
}
