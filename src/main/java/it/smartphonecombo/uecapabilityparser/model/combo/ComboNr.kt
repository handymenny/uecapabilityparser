package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Modulation
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent

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
        val nrband = IntArray(nrDlCC)
        val nrbandwidth = Array(nrDlCC, init = { BwClass.NONE })
        val nrmimo = IntArray(nrDlCC)
        val nrupload = Array(nrDlCC, init = { BwClass.NONE })
        val nrmimoUL = IntArray(nrDlCC)
        val nrmaxbandwidth = IntArray(nrDlCC)
        val nrscs = IntArray(nrDlCC)
        val nrModUL = Array(nrDlCC, init = { Modulation.NONE })
        var nrUL = ""
        var nrmimoULstring = ""
        val nrbands = componentsNr
        for (i in nrbands.indices) {
            val nr = nrbands[i]
            nrband[i] = nr.band
            nrmimo[i] = nr.mimoDL
            nrbandwidth[i] = nr.classDL
            nrmaxbandwidth[i] = nr.maxBandwidth
            nrupload[i] = nr.classUL
            nrmimoUL[i] = nr.mimoUL
            nrscs[i] = nr.scs
            nrModUL[i] = nr.modUL
        }
        val str = StringBuilder(this.toCompactStr() + separator)
        for (i in 0 until nrDlCC) {
            val b = nrband[i]
            val bw = nrbandwidth[i]
            if (b != 0 && bw != BwClass.NONE) {
                str.append(b)
                str.append(bw)
            }
            str.append(separator)
            val maxbw = nrmaxbandwidth[i]
            if (maxbw != 0) {
                str.append(maxbw)
            }
            str.append(separator)
            val scs = nrscs[i]
            if (scs != 0) {
                str.append(scs)
            }
            str.append(separator)
            val ul = nrupload[i]
            if (ul != BwClass.NONE && nrUL.count { it == ';' } / 2 < nrUlCC) {
                nrUL += "" + b + ul + separator + nrModUL[i] + separator
                if (nrmimoUL[i] != 0) {
                    nrmimoULstring += "" + nrmimoUL[i]
                }
                nrmimoULstring += separator
            }
        }

        while (nrUL.count { it == ';' } / 2 < nrUlCC) {
            nrUL += ";;"
        }
        str.append(nrUL)

        for (c in nrmimo) {
            if (c != 0) {
                str.append(c)
            }
            str.append(separator)
        }

        while (nrmimoULstring.count { it == ';' } < nrUlCC) {
            nrmimoULstring += ";"
        }
        str.append(nrmimoULstring)

        return str.toString()
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
