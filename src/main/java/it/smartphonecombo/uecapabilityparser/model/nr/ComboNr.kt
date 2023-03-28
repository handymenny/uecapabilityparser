package it.smartphonecombo.uecapabilityparser.model.nr

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.ICombo
import it.smartphonecombo.uecapabilityparser.model.IComponent
import it.smartphonecombo.uecapabilityparser.model.Modulation
import it.smartphonecombo.uecapabilityparser.model.lte.ComponentLte

/** The Class Combo. */
data class ComboNr(
    override var masterComponents: Array<IComponent>,
    override var secondaryComponents: Array<IComponent>,
    val featureSet: Int = 0
) : ICombo {
    constructor(
        master: Array<IComponent>,
        secondary: Array<IComponent>
    ) : this(master, secondary, 0)
    constructor(master: Array<IComponent>, featureSet: Int) : this(master, emptyArray(), featureSet)
    constructor(master: Array<IComponent>) : this(master, emptyArray())

    /**
     * To string.
     *
     * @return the string
     * @see Object.toString
     */
    override fun toString(): String {
        val str = StringBuilder()
        for (x in componentsLte) {
            str.append(x.toCompactStr())
            str.append("-")
        }
        if (str.length > 1) {
            str.deleteCharAt(str.length - 1)
            str.append("_")
        }
        for (x in componentsNr) {
            str.append(x.toCompactStr())
            str.append("-")
        }
        if (isNrDc && str.length > 1) {
            str.deleteCharAt(str.length - 1)
            str.append("_")
        }
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
        val nrbandDc = IntArray(nrDcDlCC)
        val nrbandwidthDc = Array(nrDcDlCC, init = { BwClass.NONE })
        val nrmimoDc = IntArray(nrDcDlCC)
        val nruploadDc = Array(nrDcDlCC, init = { BwClass.NONE })
        val nrmimoULDc = IntArray(nrDcDlCC)
        val nrmaxbandwidthDc = IntArray(nrDcDlCC)
        val nrscsDc = IntArray(nrDcDlCC)
        val nrModULDc = Array(nrDcDlCC, init = { Modulation.NONE })
        var lteUL = ""
        var nrUL = ""
        var nrmimoULstring = ""
        var nrULDc = ""
        var nrmimoULstringDc = ""
        val bands = componentsLte
        if (lteDlCC > 0)
            ComponentLte.lteComponentsToArrays(band, bandwidth, mimo, upload, lteModUL, bands)
        val nrbands = componentsNr
        for (i in nrbands.indices) {
            val nr = nrbands[i] as ComponentNr
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
            val nr = nrbandsDc[i] as ComponentNr
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

    val isEnDc: Boolean
        get() =
            (masterComponents.isNotEmpty() && (masterComponents[0] is ComponentLte)) &&
                (secondaryComponents.isNotEmpty() && (secondaryComponents[0] is ComponentNr))

    val isNrDc: Boolean
        get() =
            (masterComponents.isNotEmpty() && (masterComponents[0] is ComponentNr)) &&
                (secondaryComponents.isNotEmpty() && (secondaryComponents[0] is ComponentNr))

    val componentsNr: Array<IComponent>
        get() = if (isEnDc) secondaryComponents else masterComponents

    val componentsNrDc: Array<IComponent>
        get() = if (isNrDc) secondaryComponents else emptyArray()

    val componentsLte: Array<IComponent>
        get() = if (isEnDc) masterComponents else emptyArray()
}
