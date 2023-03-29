package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Modulation
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr

data class ComboNrDc(
    override val masterComponents: Array<ComponentNr>,
    override val secondaryComponents: Array<ComponentNr>,
    override val featureSet: Int = 0,
    override val bcs: IntArray = IntArray(0)
) : ICombo {

    override fun toString(): String {
        val str = StringBuilder()
        for (x in componentsNr) {
            str.append(x.toCompactStr())
            str.append("-")
        }
        str.deleteCharAt(str.length - 1)
        str.append("_")
        for (x in componentsNrDc) {
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
        val nrband = IntArray(nrDlCC)
        val nrbandwidth = Array(nrDlCC, init = { BwClass.NONE })
        val nrmimo = IntArray(nrDlCC)
        val nrupload = Array(nrDlCC, init = { BwClass.NONE })
        val nrmimoUL = IntArray(nrDlCC)
        val nrmaxbandwidth = IntArray(nrDlCC)
        val nrscs = IntArray(nrDlCC)
        val nrModUL = Array(nrDlCC, init = { Modulation.NONE })
        val nrbandDc = IntArray(nrDcDlCC)
        val nrbandwidthDc = Array(nrDcDlCC, init = { BwClass.NONE })
        val nrmimoDc = IntArray(nrDcDlCC)
        val nruploadDc = Array(nrDcDlCC, init = { BwClass.NONE })
        val nrmimoULDc = IntArray(nrDcDlCC)
        val nrmaxbandwidthDc = IntArray(nrDcDlCC)
        val nrscsDc = IntArray(nrDcDlCC)
        val nrModULDc = Array(nrDcDlCC, init = { Modulation.NONE })
        var nrUL = ""
        var nrmimoULstring = ""
        var nrULDc = ""
        var nrmimoULstringDc = ""
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
        val nrbandsDc = componentsNrDc
        for (i in nrbandsDc.indices) {
            val nr = nrbandsDc[i]
            nrbandDc[i] = nr.band
            nrmimoDc[i] = nr.mimoDL
            nrbandwidthDc[i] = nr.classDL
            nrmaxbandwidthDc[i] = nr.maxBandwidth
            nruploadDc[i] = nr.classUL
            nrmimoULDc[i] = nr.mimoUL
            nrscsDc[i] = nr.scs
            nrModULDc[i] = nr.modUL
        }
        val str = StringBuilder(this.toString() + separator)

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
        for (i in 0 until nrDcDlCC) {
            val b = nrbandDc[i]
            val bw = nrbandwidthDc[i]
            if (b != 0 && bw != BwClass.NONE) {
                str.append(b)
                str.append(bw)
            }
            str.append(separator)
            val maxbw = nrmaxbandwidthDc[i]
            if (maxbw != 0) {
                str.append(maxbw)
            }
            str.append(separator)
            val scs = nrscsDc[i]
            if (scs != 0) {
                str.append(scs)
            }
            str.append(separator)
            val ul = nruploadDc[i]
            if (ul != BwClass.NONE && nrULDc.count { it == ';' } / 2 < nrDcUlCC) {
                nrULDc += "" + b + ul + separator + nrModULDc[i] + separator
                if (nrmimoULDc[i] != 0) {
                    nrmimoULstringDc += "" + nrmimoULDc[i]
                }
                nrmimoULstringDc + separator
            }
        }

        while (nrUL.count { it == ';' } / 2 < nrUlCC) {
            nrUL += ";;"
        }
        str.append(nrUL)

        while (nrULDc.count { it == ';' } / 2 < nrDcUlCC) {
            nrULDc += ";;"
        }
        str.append(nrULDc)

        for (c in nrmimo) {
            if (c != 0) {
                str.append(c)
            }
            str.append(separator)
        }

        for (c in nrmimoDc) {
            if (c != 0) {
                str.append(c)
            }
            str.append(separator)
        }

        while (nrmimoULstring.count { it == ';' } < nrUlCC) {
            nrmimoULstring += ";"
        }
        str.append(nrmimoULstring)

        while (nrmimoULstringDc.count { it == ';' } < nrDcUlCC) {
            nrmimoULstringDc += ";"
        }
        str.append(nrmimoULstringDc)
        return str.toString()
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
