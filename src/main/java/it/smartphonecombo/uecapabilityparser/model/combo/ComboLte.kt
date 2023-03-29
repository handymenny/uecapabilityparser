package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.util.Utility

data class ComboLte(
    override val masterComponents: Array<ComponentLte>,
    override var bcs: IntArray = intArrayOf()
) : ICombo {
    override val secondaryComponents
        get() = emptyArray<IComponent>()

    override val featureSet: Int
        get() = 0

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComboLte) return false

        if (!masterComponents.contentEquals(other.masterComponents)) return false
        if (!bcs.contentEquals(other.bcs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = masterComponents.contentHashCode()
        result = 31 * result + bcs.contentHashCode()
        return result
    }
}
