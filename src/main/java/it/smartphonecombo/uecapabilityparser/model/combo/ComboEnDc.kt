package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.extension.populateCsvStringBuilders
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyBCS
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ComboEnDc(
    @SerialName("componentsLte") override val masterComponents: List<ComponentLte>,
    @SerialName("componentsNr") override val secondaryComponents: List<ComponentNr>,
    @SerialName("bcsNr") val bcsNr: BCS = EmptyBCS,
    @SerialName("bcsEutra") val bcsEutra: BCS = EmptyBCS,
    @SerialName("bcsIntraEndc") val bcsIntraEnDc: BCS = EmptyBCS,
) : ICombo {
    @Transient
    override var featureSet: Int = 0
        private set

    override val bcs: BCS
        get() = bcsNr

    val componentsNr: List<ComponentNr>
        get() = secondaryComponents

    val componentsLte: List<ComponentLte>
        get() = masterComponents

    constructor(
        masterComponents: List<ComponentLte>,
        secondaryComponents: List<ComponentNr>,
        featureSet: Int,
        bcsNr: BCS,
        bcsEutra: BCS,
        bcsIntraEnDc: BCS,
    ) : this(masterComponents, secondaryComponents, bcsNr, bcsEutra, bcsIntraEnDc) {
        this.featureSet = featureSet
    }

    override fun toCompactStr(): String {
        val lte = componentsLte.joinToString(separator = "-", transform = IComponent::toCompactStr)
        val nr = componentsNr.joinToString(separator = "-", transform = IComponent::toCompactStr)

        var bcsString = ""
        if (bcs != EmptyBCS) {
            bcsString += "${bcs.toCompactStr()},"
        }
        if (bcsEutra != EmptyBCS) {
            bcsString += "${bcsEutra.toCompactStr()},"
        }
        if (bcsIntraEnDc != EmptyBCS) {
            bcsString += "${bcsIntraEnDc.toCompactStr()},"
        }
        if (bcsString.isNotEmpty()) {
            bcsString = "-[${bcsString.dropLast(1)}]"
        }

        return "${lte}_${nr}$bcsString"
    }

    override fun toCsv(
        separator: String,
        lteDlCC: Int,
        lteUlCC: Int,
        nrDlCC: Int,
        nrUlCC: Int,
        nrDcDlCC: Int,
        nrDcUlCC: Int,
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
            separator,
        )

        val lteDl = StringBuilder()
        val lteUl = StringBuilder()
        val lteMimoDl = StringBuilder()
        val lteMimoUl = StringBuilder()
        var ulLteCount = 0

        for (component in componentsLte) {
            lteDl.append(component.band).append(component.classDL).append(separator)
            if (component.mimoDL != EmptyMimo) lteMimoDl.append(component.mimoDL)
            lteMimoDl.append(separator)

            if (component.classUL != BwClass.NONE) {
                lteUl
                    .append(component.band)
                    .append(component.classUL)
                    .append(separator)
                    .append(component.modUL)
                    .append(separator)

                if (component.mimoUL != EmptyMimo) {
                    lteMimoUl.append(component.mimoUL)
                }
                lteMimoUl.append(separator)

                ulLteCount++
            }
        }

        repeat(lteDlCC - componentsLte.size) {
            IOUtils.appendSeparator(separator, lteDl, lteMimoDl)
        }

        repeat(lteUlCC - ulLteCount) { IOUtils.appendSeparator(separator, lteUl, lteUl, lteMimoUl) }

        return "$compact$lteDl$lteUl$nrBandBwScs$nrUlBwScsMod$lteMimoDl$nrMimoDl$lteMimoUl$nrMimoUl$bcs;$bcsEutra;$bcsIntraEnDc"
    }
}
