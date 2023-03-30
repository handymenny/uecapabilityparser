package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.extension.populateCsvStringBuilders
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent

data class ComboNrDc(
    override val masterComponents: List<ComponentNr>,
    override val secondaryComponents: List<ComponentNr>,
    override val featureSet: Int = 0,
    override val bcs: IntArray = IntArray(0)
) : ICombo {
    val componentsNr: List<ComponentNr>
        get() = masterComponents

    val componentsNrDc: List<ComponentNr>
        get() = secondaryComponents

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
