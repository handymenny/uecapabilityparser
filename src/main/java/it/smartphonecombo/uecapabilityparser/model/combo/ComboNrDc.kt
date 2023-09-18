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
    @SerialName("componentsFr1") override val masterComponents: List<ComponentNr>,
    @SerialName("componentsFr2") override val secondaryComponents: List<ComponentNr>,
    override val bcs: BCS = EmptyBCS
) : ICombo {
    @Transient
    override var featureSet: Int = 0
        private set

    val componentsNr: List<ComponentNr>
        get() = masterComponents

    val componentsNrDc: List<ComponentNr>
        get() = secondaryComponents

    constructor(
        masterComponents: List<ComponentNr>,
        secondaryComponents: List<ComponentNr>,
        featureSet: Int,
        bcs: BCS
    ) : this(masterComponents, secondaryComponents, bcs) {
        this.featureSet = featureSet
    }

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
        val nrUlBwScsMod = StringBuilder()
        val nrMimoDl = StringBuilder()
        val nrMimoUl = StringBuilder()
        val nrDcBandBwScs = StringBuilder()
        val nrDcUlBwScsMod = StringBuilder()
        val nrDcMimoDl = StringBuilder()
        val nrDcMimoUl = StringBuilder()

        componentsNr.populateCsvStringBuilders(
            nrBandBwScs,
            nrMimoDl,
            nrUlBwScsMod,
            nrMimoUl,
            nrDlCC,
            nrUlCC,
            separator
        )

        componentsNrDc.populateCsvStringBuilders(
            nrDcBandBwScs,
            nrDcMimoDl,
            nrDcUlBwScsMod,
            nrDcMimoUl,
            nrDcDlCC,
            nrDcUlCC,
            separator
        )

        return "$compact$nrBandBwScs$nrDcBandBwScs$nrUlBwScsMod$nrDcUlBwScsMod$nrMimoDl$nrDcMimoDl$nrMimoUl$nrDcMimoUl$bcs"
    }
}
