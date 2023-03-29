package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Modulation
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr

data class ComboEnDc(
    override val masterComponents: Array<ComponentLte>,
    override val secondaryComponents: Array<ComponentNr>,
    override val featureSet: Int = 0,
    override val bcs: IntArray = IntArray(0)
) : ICombo {

    override fun toString(): String {
        val str = StringBuilder()
        for (x in componentsLte) {
            str.append(x.toCompactStr())
            str.append("-")
        }
        str.deleteCharAt(str.length - 1)
        str.append("_")
        for (x in componentsNr) {
            str.append(x.toCompactStr())
            str.append("-")
        }
        str.append(featureSet)
        return str.toString()
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
        val band = IntArray(lteDlCC)
        val bandwidth = Array(lteDlCC, init = { BwClass.NONE })
        val mimo = IntArray(lteDlCC)
        val upload = Array(lteDlCC, init = { BwClass.NONE })
        val nrband = IntArray(nrDlCC)
        val nrbandwidth = Array(nrDlCC, init = { BwClass.NONE })
        val nrmimo = IntArray(nrDlCC)
        val nrupload = Array(nrDlCC, init = { BwClass.NONE })
        val nrmimoUL = IntArray(nrDlCC)
        val nrmaxbandwidth = IntArray(nrDlCC)
        val nrscs = IntArray(nrDlCC)
        val lteModUL = Array(lteDlCC, init = { Modulation.NONE })
        val nrModUL = Array(nrDlCC, init = { Modulation.NONE })
        var lteUL = ""
        var nrUL = ""
        var nrmimoULstring = ""
        val bands = componentsLte
        if (lteDlCC > 0)
            ComponentLte.lteComponentsToArrays(band, bandwidth, mimo, upload, lteModUL, bands)
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
        val str = StringBuilder(this.toString() + separator)
        for (i in 0 until lteDlCC) {
            val b = band[i]
            if (b != 0) {
                str.append(b)
            }
            val bw = bandwidth[i]
            str.append(bw)
            str.append(separator)
            val ul = upload[i]
            if (ul != BwClass.NONE && lteUL.count { it == ';' } / 2 < lteUlCC) {
                lteUL += "" + b + ul + separator + lteModUL[i] + separator
            }
        }

        while (lteUL.count { it == ';' } / 2 < lteUlCC) {
            lteUL += ";;"
        }
        if (lteUlCC > 0) {
            str.append(lteUL)
        }
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

        for (value in mimo) {
            if (value != 0) {
                str.append(value)
            }
            str.append(separator)
        }
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
        if (other !is ComboEnDc) return false

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
        get() = secondaryComponents

    val componentsLte: Array<ComponentLte>
        get() = masterComponents
}
