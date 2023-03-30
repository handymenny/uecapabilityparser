package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.extension.populateCsvStringBuilders
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.util.Utility

data class ComboEnDc(
    override val masterComponents: List<ComponentLte>,
    override val secondaryComponents: List<ComponentNr>,
    override val featureSet: Int = 0,
    override val bcs: IntArray = IntArray(0)
) : ICombo {
    val componentsNr: List<ComponentNr>
        get() = secondaryComponents

    val componentsLte: List<ComponentLte>
        get() = masterComponents

    override fun toCompactStr(): String {
        val lte =
            componentsLte.joinToString(
                separator = "-",
                transform = IComponent::toCompactStr,
            )
        val nr =
            componentsNr.joinToString(
                separator = "-",
                transform = IComponent::toCompactStr,
            )

        return "${lte}_${nr}-$featureSet"
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

        val nrBandBwScs = StringBuilder()
        val nrUlBwMod = StringBuilder()
        val nrMimoDl = StringBuilder()
        val nrMimoUl = StringBuilder()

        componentsNr.populateCsvStringBuilders(
            nrBandBwScs,
            nrMimoDl,
            nrUlBwMod,
            nrMimoUl,
            nrDlCC,
            nrUlCC,
            separator
        )

        val lteDl = StringBuilder()
        val lteUl = StringBuilder()
        val lteMimoDl = StringBuilder()
        var ulLteCount = 0

        for (component in componentsLte) {
            lteDl.append(component.band).append(component.classDL).append(separator)
            if (component.mimoDL != 0) lteMimoDl.append(component.mimoDL)
            lteMimoDl.append(separator)

            if (component.classUL != BwClass.NONE) {
                lteUl
                    .append(component.band)
                    .append(component.classUL)
                    .append(separator)
                    .append(component.modUL)
                    .append(separator)
                ulLteCount++
            }
        }

        repeat(lteDlCC - componentsLte.size) {
            Utility.appendSeparator(separator, lteDl, lteMimoDl)
        }

        repeat(lteUlCC - ulLteCount) { Utility.appendSeparator(separator, lteUl, lteUl) }

        return "$compact$lteDl$lteUl$nrBandBwScs$nrUlBwMod$lteMimoDl$nrMimoDl$nrMimoUl"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComboEnDc) return false

        if (masterComponents != other.masterComponents) return false
        if (secondaryComponents != other.secondaryComponents) return false
        if (featureSet != other.featureSet) return false
        if (!bcs.contentEquals(other.bcs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = masterComponents.hashCode()
        result = 31 * result + secondaryComponents.hashCode()
        result = 31 * result + featureSet
        result = 31 * result + bcs.contentHashCode()
        return result
    }
}
