package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.extension.populateCsvStringBuilders
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent

data class ComboNrDc(
    override val masterComponents: Array<ComponentNr>,
    override val secondaryComponents: Array<ComponentNr>,
    override val featureSet: Int = 0,
    override val bcs: IntArray = IntArray(0)
) : ICombo {

    override fun toCompactStr(): String {
        val nr =
            componentsNr.joinToString(
                separator = "-",
                transform = IComponent::toCompactStr,
            )

        val nrDc =
            componentsNrDc.joinToString(
                separator = "-",
                transform = IComponent::toCompactStr,
            )

        return "${nr}_${nrDc}-$featureSet"
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
        val nrDcBandBwScs = StringBuilder()
        val nrDcUlBwMod = StringBuilder()
        val nrDcMimoDl = StringBuilder()
        val nrDcMimoUl = StringBuilder()

        componentsNr.populateCsvStringBuilders(
            nrBandBwScs,
            nrMimoDl,
            nrUlBwMod,
            nrMimoUl,
            nrDlCC,
            nrUlCC,
            separator
        )

        componentsNrDc.populateCsvStringBuilders(
            nrDcBandBwScs,
            nrDcMimoDl,
            nrDcUlBwMod,
            nrDcMimoUl,
            nrDcDlCC,
            nrDcUlCC,
            separator
        )

        return "$compact$nrBandBwScs$nrDcBandBwScs$nrUlBwMod$nrDcUlBwMod$nrMimoDl$nrDcMimoDl$nrMimoUl$nrDcMimoUl"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComboNrDc) return false

        if (!masterComponents.contentEquals(other.masterComponents)) return false
        if (!secondaryComponents.contentEquals(other.secondaryComponents)) return false
        if (featureSet != other.featureSet) return false

        return bcs.contentEquals(other.bcs)
    }

    override fun hashCode(): Int {
        var result = masterComponents.contentHashCode()
        result = 31 * result + secondaryComponents.contentHashCode()
        result = 31 * result + featureSet
        result = 31 * result + bcs.contentHashCode()
        return result
    }

    val componentsNr: Array<ComponentNr>
        get() = masterComponents

    val componentsNrDc: Array<ComponentNr>
        get() = secondaryComponents
}
