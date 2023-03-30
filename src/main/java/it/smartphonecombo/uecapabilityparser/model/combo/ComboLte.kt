package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.util.Utility
import it.smartphonecombo.uecapabilityparser.util.Utility.appendSeparator

data class ComboLte(
    override val masterComponents: List<ComponentLte>,
    override var bcs: IntArray = intArrayOf()
) : ICombo {
    override val secondaryComponents
        get() = emptyList<IComponent>()

    override val featureSet: Int
        get() = 0

    override fun toCompactStr(): String {
        val lte =
            masterComponents.joinToString(
                separator = "-",
                transform = IComponent::toCompactStr,
            )
        val bcs = Utility.arrayToQcomBcs(bcs)

        return "$lte-$bcs"
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
        val compact = this.toCompactStr() + separator
        val strBcs = if (bcs.isEmpty()) "all" else bcs.joinToString(", ")

        val strBand = StringBuilder()
        val strBw = StringBuilder()
        val strMimo = StringBuilder()
        val strUl = StringBuilder()
        val strDLmod = StringBuilder()
        val strULmod = StringBuilder()

        for (component in masterComponents) {
            strBand.append(component.band)
            strBw.append(component.classDL)
            strDLmod.append(component.modDL)

            val mimo = component.mimoDL
            if (mimo != 0) strMimo.append(mimo)

            val ulClass = component.classUL
            if (ulClass != BwClass.NONE) {
                strUl.append(ulClass)
                strULmod.append(component.modUL)
            }

            appendSeparator(separator, strBand, strBw, strMimo, strUl, strDLmod, strULmod)
        }

        repeat(lteDlCC - masterComponents.size) {
            appendSeparator(separator, strBand, strBw, strMimo, strUl, strDLmod, strULmod)
        }

        return "$compact$strBand$strBw$strMimo$strUl$strDLmod$strULmod$strBcs"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComboLte) return false

        if (masterComponents != other.masterComponents) return false
        if (!bcs.contentEquals(other.bcs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = masterComponents.hashCode()
        result = 31 * result + bcs.contentHashCode()
        return result
    }
}
