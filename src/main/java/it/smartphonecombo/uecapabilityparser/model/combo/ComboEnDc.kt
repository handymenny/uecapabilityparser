package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.extension.populateCsvStringBuilders
import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyBCS
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.util.Output

data class ComboEnDc(
    override val masterComponents: List<ComponentLte>,
    override val secondaryComponents: List<ComponentNr>,
    override val featureSet: Int = 0,
    val bcsNr: BCS = EmptyBCS,
    val bcsEutra: BCS = EmptyBCS,
    val bcsIntraEnDc: BCS = EmptyBCS
) : ICombo {
    override val bcs: BCS
        get() = bcsNr
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

        repeat(lteDlCC - componentsLte.size) { Output.appendSeparator(separator, lteDl, lteMimoDl) }

        repeat(lteUlCC - ulLteCount) { Output.appendSeparator(separator, lteUl, lteUl, lteMimoUl) }

        return "$compact$lteDl$lteUl$nrBandBwScs$nrUlBwMod$lteMimoDl$nrMimoDl$lteMimoUl$nrMimoUl$bcs;$bcsEutra;$bcsIntraEnDc"
    }
}
