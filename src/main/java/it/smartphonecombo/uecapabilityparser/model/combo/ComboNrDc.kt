package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.extension.populateCsvStringBuilders
import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.EmptyBCS
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ComboNrDc(
    @SerialName("components-fr1") override val masterComponents: List<ComponentNr>,
    @SerialName("components-fr2") override val secondaryComponents: List<ComponentNr>,
    @Transient override val featureSet: Int = 0,
    override val bcs: BCS = EmptyBCS
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

        val bcsString = if (bcs == EmptyBCS) "" else "-${bcs.toCompactStr()}"
        return "${nr}_${nrDc}$bcsString"
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

        return "$compact$nrBandBwScs$nrDcBandBwScs$nrUlBwMod$nrDcUlBwMod$nrMimoDl$nrDcMimoDl$nrMimoUl$nrDcMimoUl$bcs"
    }
}
