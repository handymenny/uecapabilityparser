package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.util.Utility

data class ComboNr(
    override val masterComponents: Array<ComponentNr>,
    override val featureSet: Int = 0,
    override val bcs: IntArray = IntArray(0)
) : ICombo {

    override val secondaryComponents
        get() = emptyArray<IComponent>()

    override fun toCompactStr(): String {
        val nr =
            componentsNr.joinToString(
                separator = "-",
                transform = IComponent::toCompactStr,
            )

        return "$nr-$featureSet"
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
        var ulCount = 0

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

        repeat(nrDlCC - componentsNr.size) {
            Utility.appendSeparator(separator, nrBandBwScs, nrBandBwScs, nrBandBwScs, nrMimoDl)
        }

        repeat(nrUlCC - ulCount) {
            Utility.appendSeparator(separator, nrUlBwMod, nrUlBwMod, nrMimoUl)
        }

        return "$compact$nrBandBwScs$nrUlBwMod$nrMimoDl$nrMimoUl"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComboNr) return false

        if (!masterComponents.contentEquals(other.masterComponents)) return false
        if (featureSet != other.featureSet) return false
        if (!bcs.contentEquals(other.bcs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = masterComponents.contentHashCode()
        result = 31 * result + featureSet
        result = 31 * result + bcs.contentHashCode()
        return result
    }

    val componentsNr: Array<ComponentNr>
        get() = masterComponents
}
