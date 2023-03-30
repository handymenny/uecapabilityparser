package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.util.Utility

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
        var ulCount = 0
        var ulDcCount = 0

        for (component in componentsNr) {
            nrBandBwScs.append(component.band).append(component.classDL).append(separator)

            if (component.maxBandwidth != 0) {
                nrBandBwScs.append(component.maxBandwidth)
            }
            nrBandBwScs.append(separator)

            if (component.scs != 0) {
                nrBandBwScs.append(component.scs)
            }
            nrBandBwScs.append(separator)

            if (component.mimoDL != 0) {
                nrMimoDl.append(component.mimoDL)
            }
            nrMimoDl.append(separator)

            if (component.classUL != BwClass.NONE) {
                ulCount++
                nrUlBwMod
                    .append(component.band)
                    .append(component.classUL)
                    .append(separator)
                    .append(component.modUL)
                    .append(separator)

                if (component.mimoUL != 0) {
                    nrMimoUl.append(component.mimoUL)
                }
                nrMimoUl.append(separator)
            }
        }

        for (component in componentsNrDc) {
            nrDcBandBwScs.append(component.band).append(component.classDL).append(separator)

            if (component.maxBandwidth != 0) {
                nrDcBandBwScs.append(component.maxBandwidth)
            }
            nrDcBandBwScs.append(separator)

            if (component.scs != 0) {
                nrDcBandBwScs.append(component.scs)
            }
            nrDcBandBwScs.append(separator)

            if (component.mimoDL != 0) {
                nrDcMimoDl.append(component.mimoDL)
            }
            nrDcMimoDl.append(separator)

            if (component.classUL != BwClass.NONE) {
                ulDcCount++
                nrDcUlBwMod
                    .append(component.band)
                    .append(component.classUL)
                    .append(separator)
                    .append(component.modUL)
                    .append(separator)

                if (component.mimoUL != 0) {
                    nrDcMimoUl.append(component.mimoUL)
                }
                nrDcMimoUl.append(separator)
            }
        }

        repeat(nrDcDlCC - componentsNrDc.size) {
            Utility.appendSeparator(
                separator,
                nrDcBandBwScs,
                nrDcBandBwScs,
                nrDcBandBwScs,
                nrDcMimoDl
            )
        }

        repeat(nrDcUlCC - ulDcCount) {
            Utility.appendSeparator(separator, nrDcUlBwMod, nrDcUlBwMod, nrDcMimoUl)
        }

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
