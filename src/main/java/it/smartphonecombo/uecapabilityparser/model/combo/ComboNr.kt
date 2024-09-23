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
data class ComboNr(
    @SerialName("components") override val masterComponents: List<ComponentNr>,
    override val bcs: BCS = EmptyBCS
) : ICombo {
    @Transient
    override var featureSet: Int = 0
        private set

    constructor(
        masterComponents: List<ComponentNr>,
        featureSet: Int,
        bcs: BCS
    ) : this(masterComponents, bcs) {
        this.featureSet = featureSet
    }

    override val secondaryComponents
        get() = emptyList<IComponent>()

    val componentsNr: List<ComponentNr>
        get() = masterComponents

    override fun toCompactStr(): String {
        val nr =
            componentsNr.joinToString(
                separator = "-",
                transform = IComponent::toCompactStr,
            )

        val bcsString = if (bcs == EmptyBCS) "" else "-${bcs.toCompactStr()}"
        return "$nr$bcsString"
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

        componentsNr.populateCsvStringBuilders(
            nrBandBwScs,
            nrMimoDl,
            nrUlBwScsMod,
            nrMimoUl,
            nrDlCC,
            nrUlCC,
            separator
        )

        return "$compact$nrBandBwScs$nrUlBwScsMod$nrMimoDl$nrMimoUl$bcs"
    }

    fun copy(featureSet: Int): ComboNr {
        val copy = copy()
        copy.featureSet = featureSet
        return copy
    }
}
